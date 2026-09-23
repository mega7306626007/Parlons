package com.francofun

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate
import org.json.JSONObject

private const val MAX_HEARTS = 5
private const val START_GEMS = 20
/** Hearts regen: 1 heart per 30 min while below max — 0 hearts never soft-locks. */
private const val HEART_REGEN_MS = 30L * 60L * 1000L

class Store(ctx: Context) {
    private val sp = ctx.getSharedPreferences("parlons", Context.MODE_PRIVATE)
    private var lastDay: Long = sp.getLong("lastDay", -1L)

    var xp by mutableIntStateOf(sp.getInt("xp", 0))
        private set

    var gems by mutableIntStateOf(sp.getInt("gems", START_GEMS))
        private set

    var hearts by mutableIntStateOf(sp.getInt("hearts", MAX_HEARTS))
        private set

    val maxHearts get() = MAX_HEARTS
    var streak by mutableIntStateOf(
        if (lastDay >= LocalDate.now().toEpochDay() - 1) sp.getInt("streak", 0) else 0
    )
        private set
    var helpLang by mutableStateOf(
        runCatching { HelpLang.valueOf(sp.getString("lang", "ENGLISH") ?: "ENGLISH") }.getOrDefault(HelpLang.ENGLISH)
    )
        private set
    var autoSpeak by mutableStateOf(sp.getBoolean("auto", true))
        private set
    // v2 settings
    var darkMode by mutableStateOf(sp.getBoolean("dark", false))
        private set
    // §8: follow the system theme by default; manual override when the learner picks Light/Dark.
    var followSystemTheme by mutableStateOf(sp.getBoolean("darkAuto", true))
        private set
    var soundOn by mutableStateOf(sp.getBoolean("sound", true))
        private set
    // §8: in-app text size override (system font scale still applies underneath).
    var textScale by mutableStateOf(sp.getFloat("textScale", 1f))
        private set
    var onboarded by mutableStateOf(sp.getBoolean("onboarded", false))
        private set
    var dailyGoalXp by mutableIntStateOf(sp.getInt("goal", 50))
        private set
    var reminderHour by mutableIntStateOf(sp.getInt("remHour", 19))
        private set
    var reminderOn by mutableStateOf(sp.getBoolean("remOn", false))
        private set

    val stars = mutableStateMapOf<String, Int>().apply {
        allLessons().forEach { l ->
            val s = sp.getInt("stars_${l.id}", 0)
            if (s > 0) put(l.id, s)
        }
    }

    // counters
    var lessonsDone by mutableIntStateOf(sp.getInt("lessonsDone", 0)); private set
    var perfectCount by mutableIntStateOf(sp.getInt("perfect", 0)); private set
    var speakCorrectTotal by mutableIntStateOf(sp.getInt("speakOk", 0)); private set
    var chatMsgsTotal by mutableIntStateOf(sp.getInt("chatN", 0)); private set
    val badges = mutableStateMapOf<String, Boolean>().apply {
        BADGES.forEach { b -> if (sp.getBoolean("badge_${b.id}", false)) put(b.id, true) }
    }

    // daily progress keyed by epoch day -> stored for today + last 7 days
    private fun dayKey(prefix: String, day: Long = LocalDate.now().toEpochDay()) = "${prefix}_$day"
    var todayXp by mutableIntStateOf(sp.getInt(dayKey("dxp"), 0)); private set
    var todayLessons by mutableIntStateOf(sp.getInt(dayKey("dls"), 0)); private set
    var todaySpeak by mutableIntStateOf(sp.getInt(dayKey("dsp"), 0)); private set
    var todayChat by mutableIntStateOf(sp.getInt(dayKey("dch"), 0)); private set
    var todayReview by mutableIntStateOf(sp.getInt(dayKey("drv"), 0)); private set

    // SRS: key -> SrsItem
    val srs = mutableStateMapOf<String, SrsItem>().apply {
        sp.getString("srs", "")?.takeIf { it.isNotBlank() }?.let { raw ->
            runCatching {
                val o = JSONObject(raw)
                o.keys().forEach { k -> put(k, SrsItem.fromJson(o.getJSONObject(k))) }
            }
        }
    }
    private fun saveSrs() {
        val o = JSONObject()
        srs.forEach { (k, v) -> o.put(k, v.toJson()) }
        sp.edit().putString("srs", o.toString()).apply()
    }

    // weekly xp history (last 7 days, oldest->today)
    fun weeklyXp(): List<Int> {
        val today = LocalDate.now().toEpochDay()
        return (6 downTo 0).map { off -> sp.getInt(dayKey("dxp", today - off), 0) }
    }

    private fun rollDay() {
        val today = LocalDate.now().toEpochDay()
        if (sp.getLong("dailyDay", -1L) != today) {
            todayXp = 0; todayLessons = 0; todaySpeak = 0; todayChat = 0; todayReview = 0
            sp.edit().putLong("dailyDay", today)
                .putInt(dayKey("dxp"), 0).putInt(dayKey("dls"), 0).putInt(dayKey("dsp"), 0)
                .putInt(dayKey("dch"), 0).putInt(dayKey("drv"), 0).apply()
        }
    }

    init {
        rollDay()
        regenHearts()
        // load custom lessons
        runCatching {
            val raw = sp.getString("customLessons", "[]") ?: "[]"
            val arr = org.json.JSONArray(raw)
            customLessonsCache = (0 until arr.length()).map { lessonFromJson(arr.getJSONObject(it)) }
        }
    }

    fun touchStreak() {
        val today = LocalDate.now().toEpochDay()
        if (lastDay == today) return
        streak = if (lastDay == today - 1) streak + 1 else 1
        lastDay = today
        sp.edit().putInt("streak", streak).putLong("lastDay", lastDay).apply()
        checkBadges()
    }

    fun loseHeart(): Boolean {
        regenHearts()
        if (hearts <= 0) return false
        hearts--
        sp.edit().putInt("hearts", hearts).putLong("lastHeartTs", System.currentTimeMillis()).apply()
        return true
    }

    /** Ms until the next heart regenerates (0 when full). Shown in Settings. */
    fun heartRegenIn(now: Long = System.currentTimeMillis()): Long {
        if (hearts >= MAX_HEARTS) return 0L
        val last = sp.getLong("lastHeartTs", now)
        val elapsed = (now - last).coerceAtLeast(0L)
        return (HEART_REGEN_MS - (elapsed % HEART_REGEN_MS)).coerceAtLeast(0L)
    }

    /** Time-based refill so an empty heart meter recovers on its own. */
    fun regenHearts(now: Long = System.currentTimeMillis()) {
        if (hearts >= MAX_HEARTS) return
        val last = sp.getLong("lastHeartTs", now)
        val elapsed = (now - last).coerceAtLeast(0L)
        val gained = (elapsed / HEART_REGEN_MS).toInt().coerceAtLeast(0)
        if (gained > 0) {
            hearts = (hearts + gained).coerceAtMost(MAX_HEARTS)
            // Carry forward leftover time instead of resetting the clock.
            val remainder = elapsed % HEART_REGEN_MS
            sp.edit().putInt("hearts", hearts).putLong("lastHeartTs", now - remainder).apply()
        }
    }

    fun refillHearts(cost: Int = 10) {
        if (hearts >= MAX_HEARTS) return
        if (gems < cost) return
        gems -= cost
        hearts = MAX_HEARTS
        sp.edit().putInt("gems", gems).putInt("hearts", hearts).apply()
    }

    /** Returns (xp gained, gems gained). Caller must ensure single call (LessonScreen uses keyed LaunchedEffect). */
    fun finishLesson(id: String, correct: Int, total: Int, speakOk: Int = 0, reviewed: Int = 0): Pair<Int, Int> {
        rollDay()
        touchStreak()
        val gained = correct * 10 + if (correct == total && total > 0) 20 else 0
        val gainedGems = correct + if (correct == total && total > 0) 5 else 0
        xp += gained
        gems += gainedGems
        todayXp += gained
        todayLessons += 1
        todaySpeak += speakOk
        todayReview += reviewed
        lessonsDone += 1
        if (correct == total) perfectCount += 1
        speakCorrectTotal += speakOk
        val ratio = if (total == 0) 0.0 else correct.toDouble() / total
        val s = when {
            ratio >= 0.9 -> 3
            ratio >= 0.7 -> 2
            else -> 1
        }
        if (s > (stars[id] ?: 0)) {
            stars[id] = s
            sp.edit().putInt("stars_$id", s).apply()
        }
        // quest bonus: completed quests grant extra once per day (simple: +reward when target first reached)
        var bonus = 0
        DAILY_QUESTS.forEach { q ->
            val p = questProgress(q.id, todayXp, todayLessons, todaySpeak, todayChat, todayReview)
            val flag = "qdone_${q.id}_${LocalDate.now().toEpochDay()}"
            if (p >= q.target && !sp.getBoolean(flag, false)) {
                bonus += q.xpReward
                sp.edit().putBoolean(flag, true).apply()
            }
        }
        if (bonus > 0) { xp += bonus; todayXp += bonus }
        sp.edit().putInt("xp", xp).putInt("gems", gems).putInt("lessonsDone", lessonsDone).putInt("perfect", perfectCount)
            .putInt("speakOk", speakCorrectTotal).putInt("chatN", chatMsgsTotal)
            .putInt(dayKey("dxp"), todayXp).putInt(dayKey("dls"), todayLessons)
            .putInt(dayKey("dsp"), todaySpeak).putInt(dayKey("drv"), todayReview).apply()
        checkBadges()
        return (gained + bonus) to gainedGems
    }

    /** Speed round scoring (§5.13): fun + daily-goal padding. No hearts, no SRS. Returns XP gained. */
    fun finishSpeed(correct: Int, total: Int, bestCombo: Int): Int {
        rollDay()
        touchStreak()
        val gained = correct * 2 + bestCombo
        xp += gained
        todayXp += gained
        sp.edit().putInt("xp", xp).putInt(dayKey("dxp"), todayXp).apply()
        checkBadges()
        return gained
    }

    fun addChatXp(n: Int = 2) {        rollDay()
        xp += n; todayXp += n; todayChat += 1; chatMsgsTotal += 1
        sp.edit().putInt("xp", xp).putInt(dayKey("dxp"), todayXp)
            .putInt(dayKey("dch"), todayChat).putInt("chatN", chatMsgsTotal).apply()
        checkBadges()
    }

    fun recordSrs(key: String, correct: Boolean, fuzzy: Double = 1.0) {        rollDay()
        val prev = srs[key] ?: SrsItem()
        srs[key] = scoreSrs(prev, qualityFor(correct, fuzzy))
        if (correct) { todayReview += 1; sp.edit().putInt(dayKey("drv"), todayReview).apply() }
        saveSrs()
    }

    /** Phrases answered correctly at least once (Leitner box 2+). */
    fun wordsLearnedCount(): Int = srs.count { it.value.box >= 2 }

    /** §6.4 level ceiling: the furthest unit with any starred lesson sets the
     * highest difficulty review may serve (u1–u2 → A1, u3–u4 → A2, u5–u6 → B1). */
    fun levelCeiling(): String {
        val order = listOf("u1", "u2", "u3", "u4", "u5", "u6")
        val caps = mapOf("u1" to "A1", "u2" to "A1", "u3" to "A2", "u4" to "A2", "u5" to "B1", "u6" to "B1")
        var idx = 0
        order.forEachIndexed { i, u ->
            if (UNITS.find { it.id == u }?.lessonIds?.any { (stars[it] ?: 0) > 0 } == true) idx = maxOf(idx, i)
        }
        return caps[order[idx]] ?: "A1"
    }

    /** U6 capstone (§4.1): locked until every U1–U5 lesson has at least 1 star. */
    fun unitUnlocked(unitId: String): Boolean {
        if (unitId != "u6") return true
        return UNITS.filter { it.id != "u6" }.flatMap { it.lessonIds }.all { (stars[it] ?: 0) >= 1 }
    }

    /** Units where every lesson has at least 1 star. */
    fun unitsCompleted(): Int = UNITS.count { u -> u.lessonIds.isNotEmpty() && u.lessonIds.all { (stars[it] ?: 0) >= 1 } }

    /** Records a finished Simba scenario; powers the Intermédiaire badge (§7). */
    fun recordSimba(scenario: String, reg: String) {
        sp.edit().putBoolean("simba_${scenario}_$reg", true).apply()
        checkBadges()
    }

    fun simbaInterDone(): Boolean {
        val all = sp.all ?: return false
        return all.keys.any { it.startsWith("simba_") && it.endsWith("_INTERMEDIAIRE") && all[it] == true }
    }

    private fun checkBadges() {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val earned = earnedBadges(xp, streak, lessonsDone, perfectCount, speakCorrectTotal, chatMsgsTotal, stars, hour,
            wordsLearnedCount(), unitsCompleted(), UNITS.size, simbaInterDone())
        earned.forEach { id ->
            if (badges[id] != true) {
                badges[id] = true
                sp.edit().putBoolean("badge_$id", true).apply()
            }
        }
    }

    fun updateHelpLang(l: HelpLang) { helpLang = l; sp.edit().putString("lang", l.name).apply() }
    fun updateAutoSpeak(b: Boolean) { autoSpeak = b; sp.edit().putBoolean("auto", b).apply() }
    fun setDark(b: Boolean) { darkMode = b; followSystemTheme = false; sp.edit().putBoolean("dark", b).putBoolean("darkAuto", false).apply() }
    fun setFollowSystemTheme() { followSystemTheme = true; sp.edit().putBoolean("darkAuto", true).apply() }
    // §9.4 developer flag: surface the active speech engine (testing only, off by default).
    var showEngine by mutableStateOf(sp.getBoolean("dbgEngine", false))
        private set
    fun setShowEngine(b: Boolean) { showEngine = b; sp.edit().putBoolean("dbgEngine", b).apply() }
    fun setSound(b: Boolean) { soundOn = b; sp.edit().putBoolean("sound", b).apply() }
    fun setTextScale(v: Float) { textScale = v; sp.edit().putFloat("textScale", v).apply() }
    fun setOnboarded() { onboarded = true; sp.edit().putBoolean("onboarded", true).apply() }
    fun setGoal(v: Int) { dailyGoalXp = v; sp.edit().putInt("goal", v).apply() }
    fun setReminder(on: Boolean, hour: Int) { reminderOn = on; reminderHour = hour; sp.edit().putBoolean("remOn", on).putInt("remHour", hour).apply() }

    fun saveCustomLessons(list: List<Lesson>) {
        customLessonsCache = list
        val arr = org.json.JSONArray(list.map { lessonToJson(it) })
        sp.edit().putString("customLessons", arr.toString()).apply()
        list.forEach { if (!stars.containsKey(it.id)) stars[it.id] = 0 }
    }

    fun resetProgress() {
        xp = 0; gems = START_GEMS; hearts = MAX_HEARTS; streak = 0; lastDay = -1L; lessonsDone = 0; perfectCount = 0
        speakCorrectTotal = 0; chatMsgsTotal = 0
        todayXp = 0; todayLessons = 0; todaySpeak = 0; todayChat = 0; todayReview = 0
        stars.clear(); badges.clear(); srs.clear()
        val e = sp.edit()
        allLessons().forEach { e.remove("stars_${it.id}") }
        // Synthetic routes (never in allLessons) must not leak stars across resets.
        e.remove("stars_marathon").remove("stars_review")
        BADGES.forEach { e.remove("badge_${it.id}") }
        // Progress-only wipe: daily counters, quest flags, Simba records, heart clock.
        // Settings (lang/theme/goal/reminder/sound/onboarded) and custom lessons are kept.
        val keys = sp.all?.keys?.toList() ?: emptyList()
        keys.filter { k ->
            k.startsWith("qdone_") || k.startsWith("simba_") ||
                k.startsWith("dxp_") || k.startsWith("dls_") || k.startsWith("dsp_") ||
                k.startsWith("dch_") || k.startsWith("drv_")
        }.forEach { e.remove(it) }
        e.remove("lastHeartTs").remove("dailyDay")
        e.putInt("xp", 0).putInt("gems", START_GEMS).putInt("hearts", MAX_HEARTS).putInt("streak", 0).putLong("lastDay", -1L)
            .putInt("lessonsDone", 0).putInt("perfect", 0).putInt("speakOk", 0).putInt("chatN", 0)
            .putString("srs", "{}").apply()
    }
}
