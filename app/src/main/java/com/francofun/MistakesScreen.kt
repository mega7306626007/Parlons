package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

/**
 * Mistake notebook (research: error-driven follow-up).
 * Every miss from lessons lands here until you get it right again —
 * then it auto-clears via Store.recordSrs(correct).
 */
@Composable
fun MistakesScreen(
    store: Store,
    speaker: Speaker,
    onBack: () -> Unit,
    onPracticePhrase: (String) -> Unit
) {
    val lang = store.helpLang
    val entries = store.mistakes.entries.sortedByDescending { it.value.count }

    AppBackground(tint = CoralSoft) {
        Column(Modifier.fillMaxSize().padding(Sp.xxl), verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "←", style = T.section, color = InkMuted,
                    modifier = Modifier
                        .clickable(onClick = onBack)
                        .padding(end = Sp.sm)
                        .semantics { contentDescription = "Back"; role = Role.Button }
                )
                Text("📒 Mistake notebook", style = T.screenTitle, color = Coral, modifier = Modifier.weight(1f))
                Text("${entries.size}", style = T.stat, color = Coral)
            }
            Text(
                "Phrases you missed in lessons. Get one right in review or a lesson and it clears itself.",
                style = T.secondary, color = InkSoft
            )
            val worst = entries.firstOrNull()
            if (worst != null && worst.value.count >= 3) {
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        Text("🔥 Your #1 troublemaker", style = T.label, color = Coral)
                        Text(
                            "“${worst.value.fr}” — missed ${worst.value.count}×. The drill button below starts here.",
                            style = T.bodySemi, color = Ink
                        )
                    }
                }
            }

            if (entries.isEmpty()) {
                Spacer(Modifier.padding(Sp.xl))
                Mascot(MascotMood.HAPPY, size = 88.dp)
                Text(
                    "No mistakes logged — clean slate! 🎉",
                    style = T.bodySemi, color = Emerald,
                    modifier = Modifier.fillMaxWidth().padding(top = Sp.md),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else {
                // Phrase-bank lookup for WHY tips (one pass, reused by every row).
                val phraseByKey = remember { ALL_PHRASES.associateBy { it.key() } }
                // Severity groups: hardest patterns first.
                val stubborn = remember(entries) { entries.filter { it.value.count >= 5 } }
                val warming = remember(entries) { entries.filter { it.value.count in 3..4 } }
                val freshM = remember(entries) { entries.filter { it.value.count <= 2 } }
                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Sp.xs)
                ) {
                    if (stubborn.isNotEmpty()) {
                        item { Text("🧱 Stubborn (${stubborn.size}) — 5+ misses", style = T.label, color = Coral) }
                        items(stubborn, key = { it.key }) { (key, m) ->
                            MistakeRow(key, m, phraseByKey[key]?.tip, speaker, store)
                        }
                    }
                    if (warming.isNotEmpty()) {
                        item { Text("🌡 Warming up (${warming.size}) — 3–4 misses", style = T.label, color = Gold) }
                        items(warming, key = { it.key }) { (key, m) ->
                            MistakeRow(key, m, phraseByKey[key]?.tip, speaker, store)
                        }
                    }
                    if (freshM.isNotEmpty()) {
                        item { Text("🌱 Fresh (${freshM.size}) — 1–2 misses", style = T.label, color = InkMuted) }
                        items(freshM, key = { it.key }) { (key, m) ->
                            MistakeRow(key, m, phraseByKey[key]?.tip, speaker, store)
                        }
                    }
                }
                BigButton(
                    "Drill worst first (${entries.size} logged)",
                    onClick = { onPracticePhrase(entries.first().key) },
                    color = Coral
                )
            }
        }
    }
}

/** One mistake row: hear it, hear what you said, WHY tip, miss count, mark fixed. */
@Composable
private fun MistakeRow(
    key: String,
    m: MistakeRecord,
    tip: String?,
    speaker: Speaker,
    store: Store
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(Rad.xl)
            .background(Surface)
            .border(1.dp, Border, Rad.xl)
            .padding(Sp.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔊", fontSize = 22.sp, modifier = Modifier
            .clickable { speaker.speak(m.fr) }
            .padding(end = Sp.sm)
            .semantics { contentDescription = "Play ${m.fr}"; role = Role.Button }
        )
        Column(Modifier.weight(1f)) {
            Text(m.fr, style = T.bodySemi, color = Ink)
            Text(m.meaning, style = T.secondary, color = InkSoft)
            if (m.lastTried.isNotBlank()) {
                Text("You said: “${m.lastTried}”", style = T.caption, color = Coral)
                Text(
                    "🎧 Hear what you said",
                    style = T.caption, color = Cobalt,
                    modifier = Modifier
                        .clickable { speaker.speak(m.lastTried) }
                        .padding(vertical = 2.dp)
                        .semantics { contentDescription = "Hear what you said for ${m.fr}"; role = Role.Button }
                )
            }
            tip?.let { Text("💡 $it", style = T.caption, color = InkSoft) }
            Text(
                "missed ×${m.count}",
                style = T.caption,
                color = if (m.count >= 3) Coral else InkMuted
            )
        }
        Text(
            "✓ Fixed",
            style = T.label, color = Emerald,
            modifier = Modifier
                .clip(Rad.pill)
                .background(EmeraldSoft)
                .clickable { store.clearMistake(key) }
                .padding(horizontal = Sp.sm, vertical = Sp.xs)
                .semantics { contentDescription = "Mark ${m.fr} fixed"; role = Role.Button }
        )
    }
}
