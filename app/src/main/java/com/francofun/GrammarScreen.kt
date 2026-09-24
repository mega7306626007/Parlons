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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Grammar library (research: Babbel-style "why is it like this?").
 * Aggregates every grammar note + tip from the course into a searchable offline reference.
 */
data class GrammarEntry(
    val title: String,
    val body: String,
    val example: String,
    val level: String
)

fun buildGrammarLibrary(): List<GrammarEntry> {
    val fromPhrases = ALL_PHRASES.mapNotNull { p ->
        p.grammar?.let { g ->
            GrammarEntry(
                title = p.fr,
                body = g,
                example = "${p.fr} = ${p.en}",
                level = p.level
            )
        }
    }
    // Core curated entries so the library is never empty.
    val curated = listOf(
        GrammarEntry(
            "tu vs vous",
            "tu = informal (friends, kids). vous = formal (strangers, elders) or plural you. In Kenya, start with vous with new adults, switch to tu when they do.",
            "Tu es mon ami. / Vous êtes mon professeur.",
            "A1"
        ),
        GrammarEntry(
            "Articles le / la / les / un / une",
            "le/la/les = the (definite). un/une/des = a/an (indefinite). Masculine usually -e ending? Not always — learn the article with the noun.",
            "le matatu / la gare / les enfants",
            "A1"
        ),
        GrammarEntry(
            "Être vs avoir",
            "être = identity/state (je suis). avoir = possession/age (j'ai 20 ans, j'ai faim). Two of the most used verbs — drill them early.",
            "Je suis étudiant. J'ai faim.",
            "A1"
        ),
        GrammarEntry(
            "Passé composé (basics)",
            "Most verbs: avoir + past participle. être verbs (aller, venir, partir) agree: je suis allé(e). Regular -er → -é, -ir → -i, -re → -u.",
            "J'ai mangé. / Je suis allé au marché.",
            "A2"
        ),
        GrammarEntry(
            "Negation ne…pas",
            "Wrap the verb: ne before, pas after. Drop 'ne' in fast speech (ne → n'). With elision: je ne mange pas → je n'ai pas.",
            "Je ne comprends pas. / Je n'ai pas le temps.",
            "A1"
        ),
        GrammarEntry(
            "Question forms",
            "Informal: rising intonation (Tu viens ?). Standard: est-ce que (Est-ce que tu viens ?). Formal inversion (Viens-tu ?) — rare for learners.",
            "Où est-ce que tu habites ?",
            "A1"
        ),
        GrammarEntry(
            "Gender of nouns",
            "Usually -age/-ment/-oir masculine; -té/-ie/-elle feminine. When unsure, learn 'le/la' with the word. Swiss-army knife: la problem? No — le problème.",
            "le problème (m) / la chance (f)",
            "A1"
        ),
        GrammarEntry(
            "Futur proche",
            "aller + infinitive = about to / going to. Much easier than the true future tense for A2 learners.",
            "Je vais manger. / Nous allons partir.",
            "A2"
        )
    )
    return (curated + fromPhrases).distinctBy { it.title + it.body }.sortedBy { levelRank(it.level) }
}

@Composable
fun GrammarScreen(store: Store, onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("All") }
    val all = remember { buildGrammarLibrary() }
    val rows = remember(query, level) {
        all.filter { e ->
            (level == "All" || e.level == level) &&
                (query.isBlank() ||
                    e.title.contains(query, true) ||
                    e.body.contains(query, true) ||
                    e.example.contains(query, true))
        }
    }

    AppBackground(tint = CobaltSoft) {
        Column(Modifier.fillMaxSize().padding(Sp.xxl), verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "←", style = T.section, color = InkMuted,
                    modifier = Modifier
                        .clickable(onClick = onBack)
                        .padding(end = Sp.sm)
                        .semantics { contentDescription = "Back"; role = Role.Button }
                )
                Text("📐 Grammar", style = T.screenTitle, color = Cobalt, modifier = Modifier.weight(1f))
                Text("${rows.size}", style = T.stat, color = Cobalt)
            }
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search grammar, verbs, articles…") },
                singleLine = true, shape = Rad.xl
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
                listOf("All", "A1", "A2", "B1").forEach { lv -> Chip(lv, level == lv) { level = lv } }
            }
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Sp.xs)
            ) {
                items(rows, key = { it.title + it.body }) { e ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(Rad.xl)
                            .background(Surface)
                            .border(1.dp, Border, Rad.xl)
                            .padding(Sp.md),
                        verticalArrangement = Arrangement.spacedBy(Sp.xs)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(e.title, style = T.bodySemi, color = Ink, modifier = Modifier.weight(1f))
                            Text(e.level, style = T.caption, color = Cobalt)
                        }
                        Text(e.body, style = T.secondary, color = InkSoft)
                        Text("📝 ${e.example}", style = T.caption, color = Emerald)
                    }
                }
            }
        }
    }
}
