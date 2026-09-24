package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francofun.R

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
    PhotoBg(R.drawable.bg_word_bank) {
    Column(Modifier.fillMaxSize().padding(Sp.lg), verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
            Text("📖 Word bank (${rows.size})", style = T.screenTitle, color = Blue)
        }
        OutlinedTextField(
            value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search French or meaning…") }, singleLine = true, shape = Rad.xl
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
            listOf("All", "A1", "A2", "B1").forEach { lv -> Chip(lv, level == lv) { level = lv } }
        }
        Text("${store.wordsLearnedCount()} words at mastery box 2+ • tap 🔊 to hear", style = T.caption)
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            items(rows, key = { it.first.fr }) { (p, box, lesson) ->
                Row(
                    Modifier.fillMaxWidth().clip(Rad.xl).background(Surface).border(1.dp, Color(0xFFE3E7F2), Rad.xl).padding(Sp.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔊", fontSize = 22.sp, modifier = Modifier.clickable { speaker.speak(p.fr) }.padding(end = Sp.sm).semantics { contentDescription = "Play ${p.fr}"; role = Role.Button })
                    Column(Modifier.weight(1f)) {
                        Text(p.fr, style = T.bodySemi, color = Ink)
                        Text(p.meaning(lang), style = T.secondary, color = InkSoft)
                        if (lesson.isNotBlank()) Text("📚 $lesson • ${p.level}", style = T.caption)
                    }
                    Text(
                        if (box == 0) "○ new" else "●".repeat(box) + "○".repeat(5 - box),
                        style = T.caption, color = if (box == 0) InkMuted else Gold
                    )
                }
            }
        }
    }
    }
}
