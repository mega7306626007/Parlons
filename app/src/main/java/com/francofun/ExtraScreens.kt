package com.francofun

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(store: Store, onDone: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Sp.xxl), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        when (step) {
            0 -> {
                Mascot(MascotMood.EXCITED, size = 120.dp)
                Text("Parlons!", style = T.display, color = Cobalt)
                Text("French for Kenyans — English, Kiswahili na Sheng.", style = T.body, color = InkSoft, modifier = Modifier.padding(vertical = Sp.sm), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                LangChips(store)
                Spacer(Modifier.padding(Sp.md))
                BigButton("Next →", onClick = { step = 1 })
            }
            1 -> {
                Mascot(MascotMood.THINKING, size = 100.dp)
                Text("Daily goal?", style = T.screenTitle, color = Ink)
                Spacer(Modifier.padding(Sp.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
                    listOf(30, 50, 100).forEach { g -> Chip("$g XP", store.dailyGoalXp == g) { store.setGoal(g) } }
                }
                Spacer(Modifier.padding(Sp.md))
                BigButton("Next →", onClick = { step = 2 })
            }
            2 -> {
                Mascot(MascotMood.HAPPY, size = 100.dp)
                Text("How Parlons works", style = T.screenTitle, color = Ink, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.padding(Sp.sm))
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm), horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    OnboardTip("📚", "Learn path", "6 units · 65 lessons, unlock as you go")
                    OnboardTip("🔁", "Smart review", "Spaced repetition keeps words fresh")
                    OnboardTip("🦁", "Chat with Simba", "Offline French conversation practice")
                    OnboardTip("⚡", "Power-ups", "Streak freezes, 2× XP, daily chests")
                }
                Spacer(Modifier.padding(Sp.md))
                BigButton("Next →", onClick = { step = 3 })
            }
            3 -> {
                Mascot(MascotMood.ENCOURAGING, size = 100.dp)
                Text("Reminders keep streaks alive.", style = T.section, color = Ink, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { store.setOnboarded(); onDone() }
                Spacer(Modifier.padding(Sp.sm))
                BigButton("Enable reminders", onClick = {
                    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else { store.setOnboarded(); onDone() }
                })
                Spacer(Modifier.padding(Sp.xs))
                Text("Skip", style = T.label, color = Blue, modifier = Modifier.clickable { store.setOnboarded(); onDone() }.padding(Sp.sm))
            }
        }
    }
}

@Composable
private fun OnboardTip(icon: String, title: String, sub: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.padding(Sp.sm))
        Column {
            Text(title, style = T.bodySemi, color = Ink)
            Text(sub, style = T.caption, color = InkSoft)
        }
    }
}

@Composable
fun StatsScreen(store: Store, onBack: () -> Unit) {
    val week = store.weeklyXp()
    val max = (week.maxOrNull() ?: 1).coerceAtLeast(1)
    // §46: dense statistics stay photo-free for maximum clarity.
    AppBackground(tint = GoldSoft) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Sp.xxl), verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
            Text("Stats 📊", style = T.screenTitle, color = Ink)
        }
        Row(Modifier.fillMaxWidth().clip(Rad.xl).background(Surface).border(1.dp, Color(0xFFE3E7F2), Rad.xl).padding(Sp.md), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatBox("🔥", "${store.streak}", "streak")
            StatBox("⭐", "${store.xp}", "XP")
            StatBox("🏅", "${store.badges.size}/${BADGES.size}", "badges")
            StatBox("📖", "${store.lessonsDone}", "lessons")
        }
        // §7 weekly recap + local-only league: this week vs your own 7-day daily average.
        val weekSum = week.sum()
        val dailyAvg = week.average()
        val pace: String = when {
            dailyAvg < 1 -> "Start earning XP to join your own league 🏁"
            store.todayXp >= dailyAvg * 1.2 -> "🔥 ${((store.todayXp / dailyAvg - 1) * 100).toInt()}% ahead of your usual pace!"
            store.todayXp >= dailyAvg -> "✅ Right on your usual pace."
            else -> "🐢 A little below your usual pace — one lesson fixes it."
        }
        Column(Modifier.fillMaxWidth().clip(Rad.xl).background(BlueLight).border(1.dp, Color(0xFFD6E0FF), Rad.xl).padding(Sp.md), verticalArrangement = Arrangement.spacedBy(Sp.xxs)) {
            Text("📰 Weekly recap", style = T.bodySemi, color = Ink)
            Text("This week: $weekSum XP • ${store.streak}-day streak • ${store.lessonsDone} lessons done • ${store.wordsLearnedCount()} words learned", style = T.secondary, color = InkSoft)
            Text(pace, style = T.label, color = Blue)
        }

        // Local league board (offline rivals paced from your average)
        LeagueCard(store, weekSum, week.average())

        Text("Last 7 days XP", style = T.bodySemi, color = Ink)
        Box(Modifier.fillMaxWidth().height(160.dp).clip(Rad.xl).background(Surface).border(1.dp, Color(0xFFE3E7F2), Rad.xl).padding(Sp.sm)) {
            Canvas(Modifier.fillMaxSize()) {
                val bw = size.width / 7
                week.forEachIndexed { i, v ->
                    val h = (v.toFloat() / max) * (size.height - 30)
                    drawRoundRect(Blue, topLeft = androidx.compose.ui.geometry.Offset(i * bw + 10, size.height - h - 18), size = androidx.compose.ui.geometry.Size(bw - 20, h), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                }
            }
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { Text(it, style = T.caption) }
            }
        }

        // 13-week activity heatmap (research: glanceable consistency history)
        ActivityHeatmap(store)

        Text("Trophies 🏆", style = T.bodySemi, color = Ink)
        BADGES.forEach { b ->
            val got = store.badges[b.id] == true
            Row(Modifier.fillMaxWidth().clip(Rad.lg).background(if (got) GoldSoft else Surface).border(1.dp, Border, Rad.lg).padding(Sp.sm), verticalAlignment = Alignment.CenterVertically) {
                Text(if (got) b.emoji else "🔒", fontSize = 24.sp, modifier = Modifier.padding(end = Sp.sm))
                Column { Text(b.fr, style = T.bodySemi, color = Ink); Text(b.desc, style = T.secondary, color = InkSoft) }
            }
        }
    }
    }
}

@Composable
private fun StatBox(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp)
        Text(value, style = T.stat, color = Ink)
        Text(label, style = T.caption)
    }
}

/* ── Local league (offline rivals) ── */

@Composable
private fun LeagueCard(store: Store, weekXp: Int, avg: Double) {
    val tier = LEAGUES.find { it.id == store.leagueTierId } ?: LEAGUES.first()
    val board = remember(weekXp, avg, store.leagueTierId) { buildLeagueBoard(weekXp, avg) }
    val yourRank = board.indexOfFirst { it.isYou } + 1

    Column(
        Modifier
            .fillMaxWidth()
            .clip(Rad.xl)
            .background(VioletSoft)
            .border(1.dp, Color(0xFFE9D5FF), Rad.xl)
            .padding(Sp.md),
        verticalArrangement = Arrangement.spacedBy(Sp.xs)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${tier.emoji} ${tier.name} League", style = T.bodySemi, color = Violet, modifier = Modifier.weight(1f))
            Text("#$yourRank this week", style = T.label, color = Ink)
        }
        Text(
            if (weekXp >= tier.promoteXp) "🟢 Promotion zone — keep it up!"
            else if (weekXp < tier.demoteXp && tier.demoteXp > 0) "🔴 Relegation zone — one lesson pulls you up"
            else "⚪ Safe · promote at ${tier.promoteXp} XP",
            style = T.caption, color = InkSoft
        )
        if (store.perfectWeeks > 0) {
            Text("✨ ${store.perfectWeeks} perfect week${if (store.perfectWeeks == 1) "" else "s"}", style = T.caption, color = Gold)
        }
        board.forEachIndexed { i, r ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Rad.sm)
                    .background(if (r.isYou) GoldSoft else Color.Transparent)
                    .padding(horizontal = Sp.xs, vertical = 2.dp)
            ) {
                Text("${i + 1}", style = T.caption, color = InkMuted, modifier = Modifier.padding(end = Sp.sm))
                Text(r.emoji, fontSize = 14.sp, modifier = Modifier.padding(end = Sp.xs))
                Text(
                    r.name,
                    style = if (r.isYou) T.label else T.secondary,
                    color = if (r.isYou) Ink else InkSoft,
                    modifier = Modifier.weight(1f)
                )
                Text("${r.weeklyXp} XP", style = T.caption, color = if (r.isYou) Violet else InkMuted)
            }
        }
    }
}

/* ── 13-week activity heatmap ── */

@Composable
private fun ActivityHeatmap(store: Store) {
    val rows = remember(store.srs.size, store.lessonsDone) { store.activityHeatmap() }
    Text("Activity · last 13 weeks", style = T.bodySemi, color = Ink)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(Rad.xl)
            .background(Surface)
            .border(1.dp, Color(0xFFE3E7F2), Rad.xl)
            .padding(Sp.sm),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        rows.forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                week.forEach { active ->
                    Box(
                        Modifier
                            .size(14.dp)
                            .clip(Rad.sm)
                            .background(
                                when {
                                    active -> Emerald
                                    else -> Border
                                }
                            )
                    )
                }
            }
        }
        Text("🟩 active day · ⬜ rest", style = T.caption, color = InkMuted)
    }
}

@Composable
fun CustomLessonDialog(store: Store, onClose: () -> Unit, onOpen: (Lesson) -> Unit) {
    var title by remember { mutableStateOf("") }
    var fr by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }
    val phrases = remember { mutableStateListOf<Phrase>() }
    AppBackground(tint = VioletSoft) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Sp.lg), verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onClose).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
            Text("✨ Custom lesson", style = T.screenTitle, color = Blue)
        }
        Text("Build your own lesson offline — add at least 3 phrases, then save and practise.", style = T.secondary, color = InkSoft)
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Lesson title, e.g. Matatu") }, singleLine = true, shape = Rad.xl)
        OutlinedTextField(value = fr, onValueChange = { fr = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("French, e.g. Je prends le matatu") }, singleLine = true, shape = Rad.xl)
        OutlinedTextField(value = meaning, onValueChange = { meaning = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Meaning, e.g. I take the matatu") }, singleLine = true, shape = Rad.xl)
        if (err.isNotBlank()) Text(err, style = T.secondary, color = Red)
        BigButton("Add phrase (${phrases.size})", enabled = fr.isNotBlank() && meaning.isNotBlank(), onClick = {
            phrases.add(Phrase(fr.trim(), meaning.trim(), meaning.trim()))
            fr = ""; meaning = ""; err = ""
        })
        phrases.forEachIndexed { i, p ->
            Row(Modifier.fillMaxWidth().clip(Rad.lg).background(Surface).border(1.dp, Color(0xFFE3E7F2), Rad.lg).padding(Sp.sm), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text(p.fr, style = T.bodySemi, color = Ink); Text(p.en, style = T.secondary, color = InkSoft) }
                Text("✕", style = T.label, color = Red, modifier = Modifier.clickable { phrases.removeAt(i) }.padding(Sp.xs))
            }
        }
        BigButton("Save & practise", enabled = title.isNotBlank() && phrases.size >= 3, onClick = {
            val id = "custom-" + title.lowercase().filter { it.isLetterOrDigit() }.take(12) + "-" + (System.currentTimeMillis() % 10000)
            val lesson = Lesson(id, "✨", title.trim(), title.trim(), title.trim(), phrases.toList(), "u4")
            store.saveCustomLessons(customLessonsCache + lesson)
            onOpen(lesson)
        })
        if (customLessonsCache.isNotEmpty()) {
            Text("Your lessons:", style = T.bodySemi, color = Ink)
            customLessonsCache.forEach { l ->
                Row(Modifier.fillMaxWidth().clip(Rad.lg).background(Surface).border(1.dp, Color(0xFFE3E7F2), Rad.lg).padding(Sp.sm), verticalAlignment = Alignment.CenterVertically) {
                    Text("📚 ${l.fr} (${l.phrases.size})", style = T.body, color = Ink, modifier = Modifier.weight(1f))
                    Text("Delete", style = T.label, color = Red, modifier = Modifier.clickable {
                        store.saveCustomLessons(customLessonsCache - l)
                    }.padding(Sp.xs))
                }
            }
        }
    }
    }
}

