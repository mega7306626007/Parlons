package com.francofun

import org.json.JSONObject
import java.time.LocalDate

/**
 * Spaced repetition, Leitner-style (§6.4). Each phrase has a `box` (1–5)
 * and a `dueDate`:
 * - correct → up a box (box 1 → tomorrow, 2 → 3 days, 3 → a week,
 *   4 → two weeks, 5 → a month)
 * - wrong → back to box 1
 * "Quick review" pulls whatever is due first, then pads with lower-box
 * phrases so sessions are always full but prioritise what's at risk.
 * Persists in SharedPreferences alongside everything else — no server.
 */
data class SrsItem(
    val box: Int = 1,
    val dueEpochDay: Long = LocalDate.now().toEpochDay()
) {
    fun toJson(): JSONObject = JSONObject()
        .put("b", box).put("d", dueEpochDay)
    companion object {
        // Older SM-2 payloads ({e,i,r,l}) are ignored → phrase restarts at box 1.
        // Nothing crashes, nothing else is touched (§10 regression-safe).
        fun fromJson(o: JSONObject): SrsItem = SrsItem(
            o.optInt("b", 1).coerceIn(1, 5),
            o.optLong("d", LocalDate.now().toEpochDay())
        )
    }
}

fun daysForBox(box: Int): Int = when (box.coerceIn(1, 5)) {
    1 -> 1
    2 -> 3
    3 -> 7
    4 -> 14
    else -> 30
}

/** quality 0..5 (5=perfect, 3=hard-but-correct, 0=wrong). Returns updated item. */
fun scoreSrs(prev: SrsItem, quality: Int, today: Long = LocalDate.now().toEpochDay()): SrsItem {
    val q = quality.coerceIn(0, 5)
    val box = if (q >= 3) (prev.box + 1).coerceAtMost(5) else 1
    return SrsItem(box, today + daysForBox(box))
}

fun qualityFor(correct: Boolean, fuzzyScore: Double = 1.0): Int = when {
    !correct -> 0
    fuzzyScore >= 0.95 -> 5
    fuzzyScore >= 0.8 -> 4
    else -> 3
}

/** Self-rated review grades (FSRS-style UX: Again / Hard / Good / Easy). */
enum class ReviewRating(val label: String, val emoji: String) {
    AGAIN("Again", "🔴"),
    HARD("Hard", "🟠"),
    GOOD("Good", "🔵"),
    EASY("Easy", "🟢")
}

/**
 * Apply a self-rating to an SRS item.
 * Again → back to box 1, due today (re-queue). Hard → stay in box, half interval.
 * Good → up one box. Easy → up two boxes (capped at 5).
 */
fun applyReview(
    prev: SrsItem,
    rating: ReviewRating,
    today: Long = LocalDate.now().toEpochDay()
): SrsItem = when (rating) {
    ReviewRating.AGAIN -> SrsItem(1, today)
    ReviewRating.HARD -> {
        val b = prev.box.coerceIn(1, 5)
        SrsItem(b, today + maxOf(1, daysForBox(b) / 2))
    }
    ReviewRating.GOOD -> {
        val b = (prev.box + 1).coerceAtMost(5)
        SrsItem(b, today + daysForBox(b))
    }
    ReviewRating.EASY -> {
        val b = (prev.box + 2).coerceAtMost(5)
        SrsItem(b, today + daysForBox(b))
    }
}

/** Days until due after applying [rating] — shown on the review buttons. */
fun previewIntervalDays(
    prev: SrsItem,
    rating: ReviewRating,
    today: Long = LocalDate.now().toEpochDay()
): Int = (applyReview(prev, rating, today).dueEpochDay - today).toInt().coerceAtLeast(0)

/** Human label for an interval: 0 = "today", 1 = "1 day", n = "n days". */
fun intervalLabel(days: Int): String = when (days) {
    0 -> "today"
    1 -> "1 day"
    else -> "$days days"
}

/** Memory strength 0..1 from Leitner box — powers the retention meter. */
fun retentionScore(item: SrsItem?): Float =
    if (item == null) 0f else (item.box.coerceIn(1, 5) - 1) / 4f

/** CEFR rank for the §6.4 difficulty gate. Unknown tags sort as hardest. */
fun levelRank(level: String): Int = when (level.uppercase()) {
    "A1" -> 0
    "A2" -> 1
    "B1" -> 2
    else -> 3
}

/** Due phrases first (oldest-due first), then padded with lowest-box phrases up to [limit]. */
fun duePhrases(
    lesson: Lesson,
    srs: Map<String, SrsItem>,
    today: Long = LocalDate.now().toEpochDay(),
    limit: Int = 12,
    // §4.2: a brand-new learner's very first review draws core phrases only.
    // §6.4: never serve phrases above the learner's level ceiling.
    maxLevel: String = "B1"
): List<Phrase> {
    val base = if (srs.isEmpty()) {
        val core = lesson.phrases.filter { it.core }
        if (core.isNotEmpty()) core else lesson.phrases
    } else lesson.phrases
    val gated = base.filter { levelRank(it.level) <= levelRank(maxLevel) }
    val pool = if (gated.isNotEmpty()) gated else base
    val due = pool
        .filter { (srs[it.key()]?.dueEpochDay ?: today) <= today }
        .sortedBy { srs[it.key()]?.dueEpochDay ?: 0L }
    if (due.size >= limit) return due.take(limit)
    val rest = pool
        .filter { p -> due.none { it.fr == p.fr } }
        .sortedBy { srs[it.key()]?.box ?: 1 }
    return (due + rest).take(limit)
}

fun allDueCount(srs: Map<String, SrsItem>, today: Long = LocalDate.now().toEpochDay()): Int =
    srs.count { it.value.dueEpochDay <= today }

/**
 * §4.1 weekly "marathon": the hardest phrases course-wide — lowest Leitner
 * box first, oldest-due first — gated by the learner's level ceiling so a
 * B1 monster never ambushes an A1 learner.
 */
fun marathonPhrases(
    lessons: List<Lesson>,
    srs: Map<String, SrsItem>,
    today: Long = LocalDate.now().toEpochDay(),
    limit: Int = 20,
    maxLevel: String = "B1"
): List<Phrase> {
    val pool = lessons.flatMap { it.phrases }
        .distinctBy { it.key() }
        .filter { levelRank(it.level) <= levelRank(maxLevel) }
        .filter { (srs[it.key()]?.dueEpochDay ?: today) <= today + 7 }
    return pool.sortedWith(
        compareBy({ srs[it.key()]?.box ?: 1 }, { srs[it.key()]?.dueEpochDay ?: 0L })
    ).take(limit)
}
