package com.francofun

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(store: Store, onDone: () -> Unit) {
    var step by remember { mutableStateOf(0) }
    var topic by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        when (step) {
            0 -> {
                Text("🇫🇷", fontSize = 72.sp)
                Text("Parlons!", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = Blue)
                Text("French for Kenyans — English, Kiswahili na Sheng.", fontSize = 16.sp, modifier = Modifier.padding(vertical = 12.dp))
                LangChips(store)
                Spacer(Modifier.height(16.dp))
                BigButton("Next →", onClick = { step = 1 })
            }
            1 -> {
                Text("🎯", fontSize = 64.sp)
                Text("Daily goal?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30, 50, 100).forEach { g -> Chip("$g XP", store.dailyGoalXp == g) { store.setGoal(g) } }
                }
                Spacer(Modifier.height(16.dp))
                BigButton("Next →", onClick = { step = 2 })
            }
            2 -> {
                Text("🔔", fontSize = 64.sp)
                Text("Reminders keep streaks alive.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { store.setOnboarded(); onDone() }
                Spacer(Modifier.height(12.dp))
                BigButton("Enable reminders", onClick = {
                    if (Build.VERSION.SDK_INT >= 33) launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else { store.setOnboarded(); onDone() }
                })
                Spacer(Modifier.height(8.dp))
                Text("Skip", color = Blue, modifier = Modifier.clickable { store.setOnboarded(); onDone() }.padding(12.dp))
                // hidden topic field kept for parity with custom-lesson onboarding variant
                topic = topic
            }
        }
    }
}

@Composable
fun StatsScreen(store: Store, onBack: () -> Unit) {
    val week = store.weeklyXp()
    val max = (week.maxOrNull() ?: 1).coerceAtLeast(1)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 14.dp))
            Text("Stats 📊", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Blue)
        }
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(14.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
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
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFE8EEFF)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("📰 Weekly recap", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text("This week: $weekSum XP • ${store.streak}-day streak • ${store.lessonsDone} lessons done • ${store.wordsLearnedCount()} words learned", fontSize = 14.sp)
            Text(pace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Blue)
        }
        Text("Last 7 days XP", fontWeight = FontWeight.Bold)
        Box(Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).padding(12.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val bw = size.width / 7
                week.forEachIndexed { i, v ->
                    val h = (v.toFloat() / max) * (size.height - 30)
                    drawRoundRect(Color(0xFF2B59C3), topLeft = androidx.compose.ui.geometry.Offset(i * bw + 10, size.height - h - 18), size = androidx.compose.ui.geometry.Size(bw - 20, h), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                }
            }
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { Text(it, fontSize = 11.sp, color = Color.Gray) }
            }
        }
        Text("Trophies 🏆", fontWeight = FontWeight.Bold)
        BADGES.forEach { b ->
            val got = store.badges[b.id] == true
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (got) Color(0xFFFFF8E1) else Color.White).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (got) b.emoji else "🔒", fontSize = 24.sp, modifier = Modifier.padding(end = 10.dp))
                Column { Text(b.fr, fontWeight = FontWeight.Bold); Text(b.desc, fontSize = 13.sp, color = Color.Gray) }
            }
        }
    }
}

@Composable
private fun StatBox(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp); Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp); Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun CustomLessonDialog(store: Store, onClose: () -> Unit, onOpen: (Lesson) -> Unit) {
    var title by remember { mutableStateOf("") }
    var fr by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var err by remember { mutableStateOf("") }
    val phrases = remember { mutableStateListOf<Phrase>() }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onClose).padding(end = 14.dp))
            Text("✨ Custom lesson", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Blue)
        }
        Text("Build your own lesson offline — add at least 3 phrases, then save and practise.")
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Lesson title, e.g. Matatu") }, singleLine = true)
        OutlinedTextField(value = fr, onValueChange = { fr = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("French, e.g. Je prends le matatu") }, singleLine = true)
        OutlinedTextField(value = meaning, onValueChange = { meaning = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Meaning, e.g. I take the matatu") }, singleLine = true)
        if (err.isNotBlank()) Text(err, color = Color(0xFFB3261E))
        BigButton("Add phrase (${phrases.size})", enabled = fr.isNotBlank() && meaning.isNotBlank(), onClick = {
            phrases.add(Phrase(fr.trim(), meaning.trim(), meaning.trim()))
            fr = ""; meaning = ""; err = ""
        })
        phrases.forEachIndexed { i, p ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text(p.fr, fontWeight = FontWeight.Bold); Text(p.en, fontSize = 13.sp, color = Color.Gray) }
                Text("✕", color = Red, modifier = Modifier.clickable { phrases.removeAt(i) }.padding(8.dp))
            }
        }
        BigButton("Save & practise", enabled = title.isNotBlank() && phrases.size >= 3, onClick = {
            val id = "custom-" + title.lowercase().filter { it.isLetterOrDigit() }.take(12) + "-" + (System.currentTimeMillis() % 10000)
            val lesson = Lesson(id, "✨", title.trim(), title.trim(), title.trim(), phrases.toList(), "u4")
            store.saveCustomLessons(customLessonsCache + lesson)
            onOpen(lesson)
        })
        if (customLessonsCache.isNotEmpty()) {
            Text("Your lessons:", fontWeight = FontWeight.Bold)
            customLessonsCache.forEach { l ->
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📚 ${l.fr} (${l.phrases.size})", modifier = Modifier.weight(1f))
                    Text("Delete", color = Red, modifier = Modifier.clickable {
                        store.saveCustomLessons(customLessonsCache - l)
                    }.padding(8.dp))
                }
            }
        }
    }
}
