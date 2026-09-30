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
import androidx.compose.runtime.mutableStateMapOf
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

/* ═══════════ STUDY GUIDE — topic + reading notes + quiz per unit ═══════════ */

@Composable
fun StudyGuideScreen(
    store: Store,
    unitId: String,
    onBack: () -> Unit,
    lang: HelpLang = HelpLang.ENGLISH,
    onDrills: () -> Unit = {}
) {
    val guide = guideFor(unitId)
    if (guide == null) {
        AppBackground {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Text(lang.t("No guide for this unit yet.", "Hakuna mwongozo bado.", "Hakuna guide bado."))
                BigButton(lang.t("Back", "Rudi", "Rudi"), onClick = onBack)
            }
        }
        return
    }
    var tab by remember { mutableStateOf(0) }
    val picked = remember { mutableStateMapOf<Int, Int>() }
    var checked by remember { mutableStateOf(false) }
    val score = remember(picked, checked) {
        if (!checked) 0 else guide.quiz.indices.count { picked[it] == guide.quiz[it].answer }
    }

    AppBackground(tint = VioletSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm)
                            .semantics { contentDescription = "Back to path"; role = Role.Button })
                    Text("📖 ${guide.topicFr}", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                Text(guide.topicEn, style = T.secondary, color = InkSoft)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
                    listOf(
                        lang.t("Topic & notes", "Mada", "Topic"),
                        lang.t("Quiz (${guide.quiz.size})", "Jaribio", "Quiz")
                    ).forEachIndexed { i, label ->
                        Text(
                            label, style = T.label,
                            color = if (tab == i) White else InkSoft,
                            modifier = Modifier
                                .clip(Rad.pill)
                                .background(if (tab == i) Cobalt else Surface)
                                .clickable { tab = i }
                                .padding(horizontal = Sp.md, vertical = Sp.xs)
                                .semantics { contentDescription = label; role = Role.Button }
                        )
                    }
                }
            }
            if (tab == 0) {
                item {
                    Card {
                        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            SectionHeader(lang.t("🎯 By the end you can…", "🎯 Mwishoni utaweza…", "🎯 Mwishoni utaweza…"))
                            guide.objectives.forEach { o ->
                                Text("✓ $o", style = T.body, color = Ink)
                            }
                        }
                    }
                }
                itemsIndexed(guide.notes, key = { i, _ -> "n$i" }) { _, n ->
                    Card {
                        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            Text(n.title, style = T.bodySemi, color = Cobalt)
                            Text(n.body, style = T.body, color = Ink)
                        }
                    }
                }
            } else {
                if (checked) {
                    item {
                        Card {
                            Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                                Text(
                                    lang.t(
                                        "Score: $score / ${guide.quiz.size}",
                                        "Alama: $score / ${guide.quiz.size}",
                                        "Score: $score / ${guide.quiz.size}"
                                    ),
                                    style = T.section, color = if (score >= guide.quiz.size * 3 / 4) Emerald else Gold
                                )
                                Text(
                                    if (score == guide.quiz.size) lang.t("Perfect — umebobea! 🏆", "Kamili! 🏆", "Perfect! 🏆")
                                    else if (score >= guide.quiz.size * 3 / 4) lang.t("Strong — review the misses below.", "Vizuri — rudia uliokosea.", "Poa — review misses.")
                                    else lang.t("Read the notes tab, then retry.", "Soma notes, ujaribu tena.", "Soma notes, retry."),
                                    style = T.secondary, color = InkSoft
                                )
                                BigButton(
                                    lang.t("↺ Retry quiz", "↺ Jaribu tena", "↺ Retry"),
                                    onClick = { picked.clear(); checked = false }
                                )
                            }
                        }
                    }
                }
                itemsIndexed(guide.quiz, key = { i, _ -> "q$i" }) { i, q ->
                    Card {
                        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            Text("${i + 1}. ${q.q}", style = T.bodySemi, color = Ink)
                            q.options.forEachIndexed { j, opt ->
                                val sel = picked[i] == j
                                val showRight = checked && j == q.answer
                                val showWrong = checked && sel && j != q.answer
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
                                        .clickable(enabled = !checked) { picked[i] = j }
                                        .padding(horizontal = Sp.sm, vertical = Sp.xs)
                                        .semantics {
                                            contentDescription = "Option: $opt" +
                                                if (showRight) ", correct" else if (showWrong) ", wrong" else ""
                                            role = Role.Button
                                        }
                                )
                            }
                            if (checked) {
                                Text("💡 ${q.explain}", style = T.caption, color = InkSoft)
                            }
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
            item {
                BigButton(
                    lang.t("⚡ Drill this unit's skills", "⚡ Jizoeze ujuzi", "⚡ Drill skills"),
                    onClick = onDrills, color = Gold
                )
            }
        }
    }
}
}
