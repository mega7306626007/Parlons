package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
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

/** §7 word bank: every phrase ever met, with mastery box, audio, and lesson source. All on-device. */
@Composable
fun WordBankScreen(store: Store, speaker: Speaker, onBack: () -> Unit) {
    val lang = store.helpLang
    var query by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("All") }
    val lessons = remember { allLessons() }
    val rows = remember(query, level, store.srs.size) {
        ALL_PHRASES
            .filter { (level == "All" || it.level == level) &&
                (query.isBlank() || it.fr.contains(query, true) || it.meaning(lang).contains(query, true)) }
            .map { p ->
                Triple(p, store.srs[p.key()]?.box ?: 0,
                    lessons.find { l -> l.phrases.any { it.fr == p.fr } }?.fr ?: "")
            }
            .sortedWith(compareBy({ it.second }, { it.first.fr }))
    }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 14.dp))
            Text("📖 Word bank (${rows.size})", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Blue)
        }
        OutlinedTextField(
            value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search French or meaning…") }, singleLine = true, shape = RoundedCornerShape(16.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "A1", "A2", "B1").forEach { lv -> Chip(lv, level == lv) { level = lv } }
        }
        Text("${store.wordsLearnedCount()} words at mastery box 2+ • tap 🔊 to hear", fontSize = 13.sp, color = Color.Gray)
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rows, key = { it.first.fr }) { (p, box, lesson) ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔊", fontSize = 22.sp, modifier = Modifier.clickable { speaker.speak(p.fr) }.padding(end = 10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(p.fr, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(p.meaning(lang), fontSize = 14.sp, color = Color(0xFF6B7280))
                        if (lesson.isNotBlank()) Text("📚 $lesson • ${p.level}", fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        if (box == 0) "○ new" else "●".repeat(box) + "○".repeat(5 - box),
                        fontSize = 12.sp, color = if (box == 0) Color.Gray else Gold
                    )
                }
            }
        }
    }
}
