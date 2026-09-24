package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

/**
 * Dedicated spaced-repetition review (research: Anki/Memdora/FSRS pattern).
 * Front → reveal → rate Again/Hard/Good/Easy with interval previews and
 * a live memory-strength (retention) meter. No hearts lost — review is safe.
 */
@Composable
fun ReviewScreen(store: Store, speaker: Speaker, onExit: () -> Unit) {
    val pool = allLessons().filter { store.unitUnlocked(it.unitId) }
    val phrases = remember(store.srs.size) {
        pool.flatMap { l -> duePhrases(l, store.srs, limit = 12, maxLevel = store.levelCeiling()) }
            .distinctBy { it.key() }
            .take(12)
    }

    if (phrases.isEmpty()) {
        ReviewEmpty(onExit)
        return
    }

    var idx by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    var totalXp by remember { mutableIntStateOf(0) }
    var againCount by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    if (finished) {
        ReviewDone(store, phrases.size, totalXp, againCount, onExit)
        return
    }

    val p = phrases[idx]
    val last = idx == phrases.size - 1
    val item = store.srs[p.key()] ?: SrsItem()
    val strength = retentionScore(item)
    val currentDueDays = (item.dueEpochDay - LocalDate.now().toEpochDay()).toInt()

    fun rate(key: String, rating: ReviewRating) {
        if (rating == ReviewRating.AGAIN) againCount++
        totalXp += store.recordReview(key, rating)
        if (last) finished = true
        else {
            idx++
            revealed = false
        }
    }

    Column(Modifier.fillMaxSize()) {
        // Header: exit + progress + strength
        Row(
            Modifier.padding(horizontal = Sp.md, vertical = Sp.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "✕",
                style = T.section,
                color = InkMuted,
                modifier = Modifier
                    .clickable(onClick = onExit)
                    .padding(end = Sp.sm)
                    .semantics { contentDescription = "Exit review"; role = Role.Button }
            )
            LinearProgressIndicator(
                progress = { (idx + if (revealed) 0.5f else 0f) / phrases.size.toFloat() },
                modifier = Modifier.weight(1f).height(10.dp).clip(Rad.pill),
                color = Emerald,
                trackColor = Border
            )
            Text(
                "  ${idx + 1}/${phrases.size}",
                style = T.label,
                color = InkSoft
            )
        }

        AppBackground(tint = EmeraldSoft, modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Sp.lg),
                verticalArrangement = Arrangement.spacedBy(Sp.md)
            ) {
                // Memory strength meter
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🧠 Memory strength", style = T.label, color = Ink, modifier = Modifier.weight(1f))
                            Text(
                                "${(strength * 100).toInt()}% · box ${item.box}",
                                style = T.caption,
                                color = InkSoft
                            )
                        }
                        LinearProgressIndicator(
                            progress = { strength },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(Rad.pill),
                            color = when {
                                strength >= 0.75f -> Emerald
                                strength >= 0.4f -> Gold
                                else -> Coral
                            },
                            trackColor = Border
                        )
                        Text(
                            if (currentDueDays <= 0) "Due now" else "Due in ${intervalLabel(currentDueDays)}",
                            style = T.caption,
                            color = InkMuted
                        )
                    }
                }

                // Flashcard
                key(p.key()) {
                    Card(elevation = 2.dp) {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Sp.sm)
                        ) {
                            Text("French", style = T.caption, color = InkMuted)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "🔊",
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { speaker.speak(p.fr) }
                                        .padding(Sp.xs)
                                        .semantics { contentDescription = "Play audio"; role = Role.Button }
                                )
                                Text(p.fr, style = T.display, color = Cobalt, textAlign = TextAlign.Center)
                            }

                            if (revealed) {
                                Divider()
                                Text(p.meaning(store.helpLang), style = T.section, color = Ink, textAlign = TextAlign.Center)
                                if (store.helpLang != HelpLang.ENGLISH) {
                                    Text(p.en, style = T.secondary, color = InkSoft, textAlign = TextAlign.Center)
                                }
                                p.tip?.let {
                                    Text("💡 $it", style = T.secondary, color = Violet, textAlign = TextAlign.Center)
                                }
                                p.grammar?.let {
                                    Text("📐 $it", style = T.caption, color = InkSoft, textAlign = TextAlign.Center)
                                }
                            } else {
                                Text(
                                    "Tap to reveal the meaning",
                                    style = T.secondary,
                                    color = InkMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .clip(Rad.lg)
                                        .clickable { revealed = true }
                                        .padding(Sp.md)
                                        .semantics { contentDescription = "Reveal answer"; role = Role.Button }
                                )
                            }
                        }
                    }
                }

                if (!revealed) {
                    BigButton("SHOW ANSWER", onClick = { revealed = true }, color = Emerald)
                }

                Spacer(Modifier.padding(Sp.xs))
            }
        }

        // Rating bar with interval previews
        Column(Modifier.fillMaxWidth().background(Surface).padding(Sp.md)) {
            if (!revealed) {
                Text(
                    "Rate how well you knew it — intervals adjust instantly",
                    style = T.caption,
                    color = InkMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("How did you do?", style = T.label, color = Ink, modifier = Modifier.padding(bottom = Sp.xs))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Sp.xs)
                ) {
                    ReviewRatingButton(
                        rating = ReviewRating.AGAIN,
                        days = previewIntervalDays(item, ReviewRating.AGAIN),
                        modifier = Modifier.weight(1f)
                    ) { rate(p.key(), ReviewRating.AGAIN) }
                    ReviewRatingButton(
                        rating = ReviewRating.HARD,
                        days = previewIntervalDays(item, ReviewRating.HARD),
                        modifier = Modifier.weight(1f)
                    ) { rate(p.key(), ReviewRating.HARD) }
                    ReviewRatingButton(
                        rating = ReviewRating.GOOD,
                        days = previewIntervalDays(item, ReviewRating.GOOD),
                        modifier = Modifier.weight(1f)
                    ) { rate(p.key(), ReviewRating.GOOD) }
                    ReviewRatingButton(
                        rating = ReviewRating.EASY,
                        days = previewIntervalDays(item, ReviewRating.EASY),
                        modifier = Modifier.weight(1f)
                    ) { rate(p.key(), ReviewRating.EASY) }
                }
            }
        }
    }
}

@Composable
private fun ReviewRatingButton(
    rating: ReviewRating,
    days: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (bg, fg) = when (rating) {
        ReviewRating.AGAIN -> CoralSoft to Coral
        ReviewRating.HARD -> GoldSoft to Color(0xFFB45309)
        ReviewRating.GOOD -> CobaltSoft to Cobalt
        ReviewRating.EASY -> EmeraldSoft to Emerald
    }
    Column(
        modifier
            .clip(Rad.md)
            .background(bg)
            .border(1.5.dp, fg, Rad.md)
            .clickable(onClick = onClick)
            .heightIn(min = 64.dp)
            .padding(vertical = Sp.sm, horizontal = Sp.xs)
            .semantics { contentDescription = "${rating.label}, ${intervalLabel(days)}"; role = Role.Button },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(rating.emoji, fontSize = 14.sp)
        Text(rating.label, style = T.label, color = fg, textAlign = TextAlign.Center)
        Text(intervalLabel(days), style = T.caption, color = fg.copy(alpha = 0.85f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun ReviewEmpty(onExit: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(Sp.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Mascot(MascotMood.HAPPY, size = 96.dp)
        Spacer(Modifier.padding(Sp.sm))
        Text("All caught up! 🎉", style = T.screenTitle, color = Emerald, textAlign = TextAlign.Center)
        Spacer(Modifier.padding(Sp.xs))
        Text(
            "Nothing is due for review right now. Come back later, or start a lesson to learn new phrases.",
            style = T.secondary,
            color = InkSoft,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.padding(Sp.xxl))
        BigButton("BACK", onClick = onExit, color = Emerald)
    }
}

@Composable
private fun ReviewDone(store: Store, count: Int, xp: Int, again: Int, onDone: () -> Unit) {
    val ctx = LocalContext.current
    if (!animationsOff(ctx)) ConfettiOverlay(true)
    Column(
        Modifier.fillMaxSize().padding(Sp.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Mascot(MascotMood.CELEBRATING, size = 96.dp)
        Spacer(Modifier.padding(Sp.sm))
        Text("Review complete!", style = T.screenTitle, color = Emerald, textAlign = TextAlign.Center)
        Spacer(Modifier.padding(Sp.xs))
        Text("$count phrases reviewed", style = T.section, color = Ink)
        if (again > 0) {
            Text(
                "$again need another look soon — they'll come back today",
                style = T.secondary,
                color = InkSoft,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.padding(Sp.sm))
        Text("+$xp XP ⭐", style = T.number, color = Gold)
        Text(
            "Streak 🔥 ${store.streak} · Words 📖 ${store.wordsLearnedCount()}",
            style = T.caption,
            color = InkSoft
        )
        Spacer(Modifier.padding(Sp.xxl))
        BigButton("CONTINUE", onClick = onDone, color = Emerald)
    }
}
