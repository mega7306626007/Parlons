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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** §5.13 speed round: 60-second flashcard sprint over already-seen phrases. Fun + daily-goal padding. */
@Composable
fun SpeedScreen(store: Store, speaker: Speaker, onExit: () -> Unit) {
    val lang = store.helpLang
    // Whole course including custom lessons — but never locked capstone content.
    val deck = remember(store.stars.size) {
        allLessons().filter { it.unitId != "u6" || store.unitUnlocked("u6") }
            .flatMap { it.phrases }.ifEmpty { ALL_PHRASES }.shuffled()
    }
    var idx by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var bestCombo by remember { mutableIntStateOf(0) }
    var left by remember { mutableIntStateOf(60) }
    var over by remember { mutableStateOf(false) }
    var xpGained by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (left > 0 && !over) {
            delay(1000)
            left--
        }
        over = true
    }
    LaunchedEffect(over) {
        if (over && xpGained == 0 && (correct > 0 || idx > 0)) {
            xpGained = store.finishSpeed(correct, idx, bestCombo)
        }
    }

    if (over) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("⚡", fontSize = 80.sp)
            Spacer(Modifier.height(12.dp))
            Text("Speed round over!", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Blue, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("$correct correct • best combo x$bestCombo", fontSize = 20.sp)
            Text("+$xpGained XP ⭐", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gold)
            Spacer(Modifier.height(32.dp))
            BigButton("CONTINUE", onClick = onExit)
        }
        return
    }

    val ph = deck[idx % deck.size]
    val options = remember(idx) {
        val right = ph.meaning(lang)
        val wrong = deck.filter { it.fr != ph.fr }.map { it.meaning(lang) }
            .filter { it != right }.distinct().shuffled().take(3)
        (wrong + right).shuffled()
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✕", fontSize = 24.sp, modifier = Modifier.clickable(onClick = onExit).padding(end = 12.dp))
            Column(Modifier.weight(1f)) {
                Text("⏱ $left s", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = if (left <= 10) Red else Blue)
                LinearProgressIndicator(progress = { left / 60f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)), color = if (left <= 10) Red else Green)
            }
            if (combo >= 2) Text("  🔥x$combo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("  ⭐$correct", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("What does this mean? (fast!)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("🔊", fontSize = 30.sp, modifier = Modifier.clickable { speaker.speak(ph.fr) }.padding(end = 12.dp))
                Text(ph.fr, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Blue)
            }
            options.forEach { opt ->
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)
                        .border(2.dp, Color(0xFFD0D5E0), RoundedCornerShape(16.dp))
                        .clickable {
                            if (opt == ph.meaning(lang)) {
                                correct++; combo++
                                if (combo > bestCombo) bestCombo = combo
                                Sounds.ok(store.soundOn)
                            } else {
                                combo = 0
                                Sounds.bad(store.soundOn)
                            }
                            idx++
                        }
                        .padding(16.dp)
                ) { Text(opt, fontSize = 17.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
