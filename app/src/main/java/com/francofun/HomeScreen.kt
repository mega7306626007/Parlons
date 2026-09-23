package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Parlons 🇫🇷", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Blue, modifier = Modifier.weight(1f))
                Text("🔥 ${store.streak}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(12.dp))
                Text("⭐ ${store.xp}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(12.dp))
                Text("💎 ${store.gems}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(12.dp))
                Text("❤️ ${store.hearts}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(12.dp))
                Text("📊", fontSize = 22.sp, modifier = Modifier.semantics { contentDescription = "Statistics"; role = Role.Button }.clickable(onClick = onStats).padding(end = 8.dp))
                Text("⚙️", fontSize = 22.sp, modifier = Modifier.semantics { contentDescription = "Settings"; role = Role.Button }.clickable(onClick = onSettings))
            }
        }
        item {
            Text(when (lang) {
                HelpLang.ENGLISH -> "Bonjour! Ready to speak French today?"
                HelpLang.SWAHILI -> "Bonjour! Uko tayari kuongea Kifaransa leo?"
                HelpLang.SHENG -> "Bonjour msee! Uko ready tuongee French leo?"
            }, fontSize = 16.sp, color = Color(0xFF444B5E))
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(12.dp)) {
                ProgressRing(store.todayXp.toFloat() / store.dailyGoalXp.coerceAtLeast(1).toFloat())
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Level ${levelForXp(store.xp)} • ${levelTitle(levelForXp(store.xp))}", fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(progress = { into / need.toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)))
                    Text("Today ${store.todayXp}/${store.dailyGoalXp} XP • ${store.todayLessons} lessons", fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
        item {
            Text("I'll explain things in:", fontSize = 13.sp, color = Color(0xFF6B7280))
            Spacer(Modifier.size(6.dp))
            LangChips(store)
        }
        if (dueN > 0) {
            item {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFFFF3CD)).clickable(onClick = onReview).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔁 Review $dueN due words", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Start →", color = Blue, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Brush.horizontalGradient(listOf(Blue, Color(0xFF5B7FE0)))).clickable(onClick = onChat).padding(20.dp)) {
                Text("Chat with Simba 🦁", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.size(4.dp))
                Text("Scripted conversations by text or voice — 100% offline, no account, nothing to set up. He reacts and offers better phrasings in ${lang.label}.", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                Spacer(Modifier.size(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.2f)).clickable(onClick = onCall).padding(horizontal = 14.dp, vertical = 8.dp)) { Text("📞 Voice call", color = Color.White, fontWeight = FontWeight.Bold) }
                    Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.2f)).clickable(onClick = onSpeed).padding(horizontal = 14.dp, vertical = 8.dp)) { Text("⚡ Speed", color = Color.White, fontWeight = FontWeight.Bold) }
                    Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.2f)).clickable(onClick = onCustom).padding(horizontal = 14.dp, vertical = 8.dp)) { Text("✨ Custom", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }
        item { DailyQuestsCard(store) }
        item {
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).clickable(onClick = onWordBank).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("📖 Word bank — ${store.wordsLearnedCount()} mastered", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Open →", color = Blue, fontWeight = FontWeight.Bold)
            }
        }
        UNITS.forEach { u ->
            val locked = !store.unitUnlocked(u.id)
            item { Text("${if (locked) "🔒" else u.emoji} ${u.fr}", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp)) }
            if (locked) {
                item { Text("Finish every lesson in Units 1–5 to unlock the capstone 🌉", fontSize = 14.sp, color = Color.Gray) }
            }
            val ls = u.lessonIds.mapNotNull { lessonById(it) }
            items(ls) { l -> LessonCard(l, lang, store.stars[l.id] ?: 0, enabled = !locked) { if (!locked) onLesson(l) } }
        }
        if (customLessonsCache.isNotEmpty()) {
            item { Text("✨ Custom", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            items(customLessonsCache) { l -> LessonCard(l, lang, store.stars[l.id] ?: 0) { onLesson(l) } }
        }
        if (store.unitUnlocked("u6")) {
            item {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF6A3FB5), Color(0xFF9B5DE5)))).padding(20.dp)) {
                    Text("🌉 Capstone: no training wheels", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.size(4.dp))
                    Text("Marathon review of your hardest words, or all twelve Simba scenarios back-to-back at Intermédiaire — native pace, open answers.",
                        color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    Spacer(Modifier.size(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.2f)).clickable(onClick = onMarathon).padding(horizontal = 14.dp, vertical = 8.dp)) { Text("🏃 Marathon", color = Color.White, fontWeight = FontWeight.Bold) }
                        Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.2f)).clickable(onClick = onChainChat).padding(horizontal = 14.dp, vertical = 8.dp)) { Text("🔗 12-scenario chain", color = Color.White, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
        item {
            var showBadges by remember { mutableStateOf(false) }
            Text(if (showBadges) "🏅 Badges (${store.badges.size}/${BADGES.size}) ▲" else "🏅 Badges (${store.badges.size}/${BADGES.size}) ▼", fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showBadges = !showBadges })
            if (showBadges) Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BADGES.forEach { b ->
                    val got = store.badges[b.id] == true
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (got) Color.White else Color(0xFFEEEEEE)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(b.emoji, fontSize = 22.sp, modifier = Modifier.padding(end = 10.dp))
                        Column(Modifier.weight(1f)) { Text(if (got) b.fr else "???", fontWeight = FontWeight.Bold); Text(b.desc, fontSize = 13.sp, color = Color.Gray) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyQuestsCard(store: Store) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("🎯 Daily quests", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        DAILY_QUESTS.forEach { q ->
            val p = questProgress(q.id, store.todayXp, store.todayLessons, store.todaySpeak, store.todayChat, store.todayReview).coerceAtMost(q.target)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${q.emoji} ", fontSize = 18.sp)
                Column(Modifier.weight(1f)) {
                    Text(when (store.helpLang) { HelpLang.SWAHILI -> q.sw; else -> q.en }, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    LinearProgressIndicator(progress = { p / q.target.toFloat() }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)))
                }
                Text(" $p/${q.target}", fontSize = 13.sp, color = Color.Gray)
                if (p >= q.target) Text(" ✅", fontSize = 16.sp)
            }
        }
        Text("Streak day: ${LocalDate.now()} • +${DAILY_QUESTS.first().xpReward} XP per quest", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun LessonCard(l: Lesson, lang: HelpLang, stars: Int, enabled: Boolean = true, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(if (enabled) Color.White else Color(0xFFEEEEEE)).clickable(enabled = enabled, onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFE8EEFF)), contentAlignment = Alignment.Center) { Text(l.emoji, fontSize = 26.sp) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(l.fr, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(if (lang == HelpLang.ENGLISH) l.en else l.sw, fontSize = 14.sp, color = Color(0xFF6B7280))
        }
        Text("★".repeat(stars) + "☆".repeat(3 - stars), color = Gold, fontSize = 20.sp)
    }
}
