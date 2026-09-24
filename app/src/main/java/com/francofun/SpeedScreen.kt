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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
        Column(Modifier.fillMaxSize().padding(Sp.xxl), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("⚡", fontSize = 80.sp)
            Spacer(Modifier.padding(Sp.sm))
            Text("Speed round over!", style = T.screenTitle, color = Blue, textAlign = TextAlign.Center)
            Spacer(Modifier.padding(Sp.xs))
            Text("$correct correct • best combo x$bestCombo", style = T.section, color = Ink)
            Text("+$xpGained XP ⭐", style = T.number, color = Gold)
            Spacer(Modifier.padding(Sp.xxl))
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
        Row(Modifier.padding(horizontal = Sp.md, vertical = Sp.sm), verticalAlignment = Alignment.CenterVertically) {
            Text("✕", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onExit).padding(end = Sp.sm).semantics { contentDescription = "Exit speed round"; role = Role.Button })
            Column(Modifier.weight(1f)) {
                Text("⏱ $left s", style = T.bodySemi, color = if (left <= 10) Red else Blue)
                LinearProgressIndicator(
                    progress = { left / 60f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(R.pill),
                    color = if (left <= 10) Red else Gold,
                    trackColor = Color(0xFFE3E7F2)
                )
            }
            if (combo >= 2) Text("  🔥x$combo", style = T.bodySemi, color = Gold)
            Text("  ⭐$correct", style = T.bodySemi, color = Blue)
        }
        Column(Modifier.weight(1f).fillMaxWidth().padding(horizontal = Sp.lg), verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
            Text("What does this mean? (fast!)", style = T.section, color = Ink)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("🔊", fontSize = 30.sp, modifier = Modifier.clickable { speaker.speak(ph.fr) }.padding(end = Sp.sm).semantics { contentDescription = "Play audio"; role = Role.Button })
                Text(ph.fr, style = T.display, color = Blue)
            }
            options.forEach { opt ->
                val isRight = opt == ph.meaning(lang)
                Box(
                    Modifier.fillMaxWidth().clip(R.xl).background(Surface)
                        .border(2.dp, Color(0xFFD0D5E0), R.xl)
                        .semantics(mergeDescendants = true) { contentDescription = opt; role = Role.Button }
                        .clickable {
                            if (isRight) {
                                correct++; combo++
                                if (combo > bestCombo) bestCombo = combo
                                Sounds.ok(store.soundOn)
                            } else {
                                combo = 0
                                Sounds.bad(store.soundOn)
                            }
                            idx++
                        }
                        .heightIn(min = 48.dp)
                        .padding(Sp.md)
                ) { Text(opt, style = T.bodySemi, color = Ink) }
            }
        }
    }
}
