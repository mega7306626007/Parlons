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
                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Sp.xs)
                ) {
                    items(entries, key = { it.key }) { (key, m) ->
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
                                }
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
                }
                BigButton(
                    "Practice these (${entries.size})",
                    onClick = { onPracticePhrase(entries.first().key) },
                    color = Coral
                )
            }
        }
    }
}
