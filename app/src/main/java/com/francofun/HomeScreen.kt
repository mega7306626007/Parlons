package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francofun.R
import java.time.LocalDate

@Composable
fun HomeScreen(
    store: Store, onLesson: (Lesson) -> Unit, onChat: () -> Unit, onCall: () -> Unit,
    onSettings: () -> Unit, onStats: () -> Unit, onReview: () -> Unit, onCustom: () -> Unit,
    onSpeed: () -> Unit, onWordBank: () -> Unit, onMarathon: () -> Unit = {}, onChainChat: () -> Unit = {}
) {
    val lang = store.helpLang
    val dueN = allDueCount(store.srs)
    val (into, need) = xpIntoLevel(store.xp)
    PhotoBg(R.drawable.bg_home) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Sp.xxl),
        verticalArrangement = Arrangement.spacedBy(Sp.lg)
    ) {
        item { HeaderRow(store, onStats, onSettings) }
        item { Greeting(lang) }
        item { ProgressCard(store, into, need) }
        item { LangChips(store) }
        if (dueN > 0) item { ReviewCard(dueN, onReview) }
        item { HeroCta(lang, onChat, onCall, onSpeed, onCustom) }
        item { DailyQuestsCard(store) }
        item { WordBankRow(store, onWordBank) }
        if (customLessonsCache.isNotEmpty()) {
            item { CustomLessonsRow(store, onLesson) }
        }
        UNITS.forEach { u ->
            val locked = !store.unitUnlocked(u.id)
            item { UnitHeader(u, locked) }
            if (locked) item { LockMessage() }
            val ls = u.lessonIds.mapNotNull { lessonById(it) }
            items(ls, key = { it.id }) { l ->
                LessonCard(l, lang, store.stars[l.id] ?: 0, enabled = !locked) { onLesson(l) }
            }
        }
        if (store.unitUnlocked("u6")) {
            item { CapstoneCard(onMarathon, onChainChat) }
        }
        item { BadgesSection(store, onStats) }
    }
    }
}

@Composable
private fun HeaderRow(store: Store, onStats: () -> Unit, onSettings: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Parlons", style = T.screenTitle, color = Blue, modifier = Modifier.weight(1f))
        BadgeCount("🔥", store.streak)
        BadgeCount("⭐", store.xp)
        BadgeCount("💎", store.gems)
        BadgeCount("❤️", store.hearts)
        Text("📊", fontSize = 20.sp, modifier = Modifier.clickable(onClick = onStats).padding(start = 4.dp))
        Text("⚙️", fontSize = 20.sp, modifier = Modifier.clickable(onClick = onSettings).padding(start = 4.dp))
    }
}

@Composable
private fun Greeting(lang: HelpLang) {
    Text(
        when (lang) {
            HelpLang.ENGLISH -> "Bonjour! Ready to speak French today?"
            HelpLang.SWAHILI -> "Bonjour! Uko tayari kuongea Kifaransa leo?"
            HelpLang.SHENG -> "Bonjour msee! Uko ready tuongee French leo?"
        },
        style = T.secondary
    )
}

@Composable
private fun ProgressCard(store: Store, into: Int, need: Int) {
    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(store.todayXp.toFloat() / store.dailyGoalXp.coerceAtLeast(1).toFloat())
            Spacer(Modifier.padding(Sp.md))
            Column(Modifier.weight(1f)) {
                Text("Level ${levelForXp(store.xp)} · ${levelTitle(levelForXp(store.xp))}", style = T.bodySemi)
                LinearProgressIndicator(
                    progress = { into / need.toFloat() },
                    Modifier.fillMaxWidth().height(6.dp).clip(Rad.md),
                    color = Blue
                )
                Text("Today ${store.todayXp}/${store.dailyGoalXp} XP · ${store.todayLessons} lessons", style = T.caption)
            }
        }
    }
}

@Composable
private fun ReviewCard(dueN: Int, onReview: () -> Unit) {
    Card(elevation = 1.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔁 Review $dueN due words", style = T.bodySemi, modifier = Modifier.weight(1f))
            Text("Start →", color = Blue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onReview))
        }
    }
}

@Composable
private fun HeroCta(lang: HelpLang, onChat: () -> Unit, onCall: () -> Unit, onSpeed: () -> Unit, onCustom: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onChat),
        elevation = 2.dp
    ) {
        Column(Modifier.padding(Sp.lg)) {
            Text("Chat with Simba 🦁", style = T.screenTitle, color = Color.White)
            Spacer(Modifier.padding(Sp.sm))
            Text(
                "Scripted conversations by text or voice — 100% offline. He reacts and offers better phrasings.",
                style = T.secondary, color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.padding(Sp.md))
            Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm)) {
                TonalChip("📞", "Voice call", onCall)
                TonalChip("⚡", "Speed", onSpeed)
                TonalChip("✨", "Custom", onCustom)
            }
        }
    }
}

@Composable
private fun TonalChip(icon: String, text: String, onClick: () -> Unit) {
    Box(
        Modifier.clip(Rad.pill).background(Blue.copy(alpha = 0.15f))
            .clickable(onClick = onClick).padding(horizontal = Sp.md, vertical = Sp.sm),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(icon, fontSize = 16.sp)
            Text(text, color = Blue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun WordBankRow(store: Store, onWordBank: () -> Unit) {
    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📖 Word bank", style = T.bodySemi, modifier = Modifier.weight(1f))
            Text("${store.wordsLearnedCount()} mastered", style = T.caption)
            Text("Open →", color = Blue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onWordBank))
        }
    }
}

@Composable
private fun UnitHeader(u: StudyUnit, locked: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(if (locked) "🔒" else u.emoji, fontSize = 20.sp)
        Spacer(Modifier.padding(Sp.sm))
        Text(u.fr, style = T.section, color = if (locked) InkMuted else Ink)
    }
}

@Composable
private fun LockMessage() {
    Text("Finish every lesson in earlier units to unlock the capstone 🌉", style = T.caption)
}

@Composable
private fun LessonCard(l: Lesson, lang: HelpLang, stars: Int, enabled: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(enabled = enabled, onClick = onClick),
        elevation = if (enabled) 0.5.dp else 0.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(Sp.md)) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(BlueLight), contentAlignment = Alignment.Center) {
                Text(l.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.padding(Sp.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(l.fr, style = T.bodySemi)
                Text(if (lang == HelpLang.ENGLISH) l.en else l.sw, style = T.caption)
            }
            Text("★".repeat(stars) + "☆".repeat(3 - stars), color = Gold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun CustomLessonsRow(store: Store, onLesson: (Lesson) -> Unit) {
    SectionHeader("✨ Custom lessons")
    customLessonsCache.forEach { l ->
        Card {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(Sp.md).clickable(onClick = { onLesson(l) })) {
                Text("📚", fontSize = 20.sp)
                Spacer(Modifier.padding(Sp.sm))
                Column(modifier = Modifier.weight(1f)) {
                    Text(l.fr, style = T.bodySemi)
                    Text("${l.phrases.size} phrases", style = T.caption)
                }
            }
        }
    }
}

@Composable
private fun CapstoneCard(onMarathon: () -> Unit, onChainChat: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onMarathon),
        elevation = 2.dp
    ) {
        Column(Modifier.padding(Sp.lg)) {
            Text("🌉 Capstone: no training wheels", style = T.screenTitle, color = Color.White)
            Spacer(Modifier.padding(Sp.sm))
            Text("Marathon review of your hardest words, or all twelve Simba scenarios back-to-back.", style = T.secondary, color = Color.White.copy(alpha = 0.9f))
            Spacer(Modifier.padding(Sp.md))
            Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm)) {
                TonalChip("🏃", "Marathon", onMarathon)
                TonalChip("🔗", "12-scenario chain", onChainChat)
            }
        }
    }
}

@Composable
private fun DailyQuestsCard(store: Store) {
    Card {
        Column(Modifier.padding(Sp.md)) {
            SectionHeader("📜 Daily quests")
            DAILY_QUESTS.forEach { q ->
                val got = store.questClaimed(q.id)
                val p = questProgress(q.id, store.todayXp, store.todayLessons, store.todaySpeak, store.todayChat, store.todayReview)
                val done = p >= q.target
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(q.emoji, fontSize = 18.sp)
                    Spacer(Modifier.padding(Sp.sm))
                    Text(
                        if (store.helpLang == HelpLang.ENGLISH) q.en else q.sw,
                        style = T.body,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (got) "✓" else "${p.coerceAtMost(q.target)}/${q.target}",
                        style = T.caption,
                        color = if (done) Green else InkMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgesSection(store: Store, onStats: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(Sp.md).clickable { expanded = !expanded }) {
                Text("🏅 Badges (${store.badges.size}/${BADGES.size})", style = T.bodySemi, modifier = Modifier.weight(1f))
                Text(if (expanded) "▲" else "▼", color = InkMuted)
            }
            if (expanded) {
                Spacer(Modifier.padding(Sp.sm))
                BADGES.forEach { b ->
                    val got = store.badges[b.id] == true
                    Row(Modifier.fillMaxWidth().padding(horizontal = Sp.md, vertical = Sp.xs)) {
                        Text(if (got) b.emoji else "🔒", fontSize = 22.sp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text(if (got) b.fr else "???", style = T.bodySemi)
                            Text(b.desc, style = T.caption)
                        }
                    }
                }
                Spacer(Modifier.padding(Sp.sm))
            }
        }
    }
}
