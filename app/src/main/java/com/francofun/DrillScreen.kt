package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/* ═══════════ DRILLS — endless generated practice ═══════════ */

@Composable
fun DrillScreen(store: Store, onBack: () -> Unit) {
    val lang = store.helpLang
    var kind by remember { mutableStateOf(DrillKind.VERB) }
    var seed by remember { mutableStateOf(System.currentTimeMillis()) }
    var items by remember(kind, seed) { mutableStateOf(genDrills(kind, 10, seed)) }
    var picked by remember(kind, seed) { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var checked by remember(kind, seed) { mutableStateOf(false) }
    val score = if (!checked) 0 else items.indices.count { picked[it] == items[it].options.indexOf(items[it].answer) }

    AppBackground(tint = GoldSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm)
                            .semantics { contentDescription = "Back"; role = Role.Button })
                    Text("⚡ Drills", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                Text(
                    lang.t(
                        "${drillScaleCount()}+ generated items · new set every round",
                        "Maswali ${drillScaleCount()}+ · seti mpya kila raundi",
                        "Items ${drillScaleCount()}+ · set mpya kila round"
                    ),
                    style = T.secondary, color = InkSoft
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
                    listOf(
                        DrillKind.VERB to lang.t("Verbs", "Vitenzi", "Verbs"),
                        DrillKind.VOCAB to lang.t("Words", "Maneno", "Words"),
                        DrillKind.GENDER to lang.t("le/la", "le/la", "le/la")
                    ).forEach { (k, label) ->
                        Text(
                            label, style = T.label,
                            color = if (kind == k) White else InkSoft,
                            modifier = Modifier
                                .clip(Rad.pill)
                                .background(if (kind == k) Cobalt else Surface)
                                .clickable { kind = k }
                                .padding(horizontal = Sp.md, vertical = Sp.xs)
                                .semantics { contentDescription = label; role = Role.Button }
                        )
                    }
                }
            }
            if (checked) {
                item {
                    Card {
                        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            Text(
                                lang.t(
                                    "Score: $score / ${items.size}",
                                    "Alama: $score / ${items.size}",
                                    "Score: $score / ${items.size}"
                                ),
                                style = T.section,
                                color = if (score >= 8) Emerald else if (score >= 5) Gold else Coral
                            )
                            BigButton(
                                lang.t("↺ New set", "↺ Seti mpya", "↺ Set mpya"),
                                onClick = {
                                    seed = System.currentTimeMillis()
                                    items = genDrills(kind, 10, seed)
                                    picked = emptyMap()
                                    checked = false
                                }
                            )
                        }
                    }
                }
            }
            itemsIndexed(items, key = { i, _ -> "$kind-$seed-$i" }) { i, d ->
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        Text("${i + 1}. ${d.prompt}", style = T.bodySemi, color = Ink)
                        d.options.forEach { opt ->
                            val sel = picked[i] == d.options.indexOf(opt)
                            val showRight = checked && opt == d.answer
                            val showWrong = checked && sel && opt != d.answer
                            Text(
                                opt, style = T.body,
                                color = when {
                                    showRight -> Emerald
                                    showWrong -> Coral
                                    sel -> White
                                    else -> Ink
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(Rad.md)
                                    .background(
                                        when {
                                            showRight -> EmeraldSoft
                                            showWrong -> CoralSoft
                                            sel -> Cobalt
                                            else -> Surface
                                        }
                                    )
                                    .border(
                                        1.dp, when {
                                            showRight -> Emerald
                                            showWrong -> Coral
                                            else -> Border
                                        }, Rad.md
                                    )
                                    .clickable(enabled = !checked) { picked = picked + (i to d.options.indexOf(opt)) }
                                    .padding(horizontal = Sp.sm, vertical = Sp.xs)
                                    .semantics {
                                        contentDescription = "Option: $opt" +
                                            if (showRight) ", correct" else if (showWrong) ", wrong" else ""
                                        role = Role.Button
                                    }
                            )
                        }
                        if (checked) Text("💡 ${d.explain}", style = T.caption, color = InkSoft)
                    }
                }
            }
            if (!checked) {
                item {
                    BigButton(
                        lang.t("✓ Check answers", "✓ Angalia", "✓ Check"),
                        onClick = { checked = true }
                    )
                }
            }
        }
    }
}
