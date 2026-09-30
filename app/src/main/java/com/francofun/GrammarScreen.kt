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
    val level: String,
    // Explicit textbook chapter (1..6); 0 = auto-classify by keywords.
    val chapter: Int = 0,
    // Worked examples beyond the head example (shown up to 3 total).
    val examples: List<String> = emptyList()
)

fun buildGrammarLibrary(): List<GrammarEntry> {
    val fromPhrases = ALL_PHRASES.mapNotNull { p ->
        p.grammar?.let { g ->
            GrammarEntry(
                title = p.fr,
                body = g,
                example = "${p.fr} = ${p.en}",
                level = p.level,
                examples = listOfNotNull(
                    p.tip?.let { "💡 $it" },
                    p.sheng?.let { "Sheng: ${p.fr} → $it" }
                )
            )
        }
    }
    // Core curated entries so the library is never empty — explicit chapters
    // plus worked examples (textbook core; phrase notes attach automatically).
    val curated = listOf(
        GrammarEntry(
            "tu vs vous",
            "tu = informal (friends, kids, family). vous = formal (strangers, elders, officials) or ANY plural you. In Kenya, start with vous with new adults, switch to tu only when they do. Verbs change too: tu es / vous êtes, tu parles / vous parlez.",
            "Tu es mon ami. / Vous êtes mon professeur.",
            "A1", chapter = 5,
            examples = listOf(
                "Stranger: « Bonjour monsieur, vous allez bien ? »",
                "Friend: « Salut, tu viens au match ? »",
                "Group: « Vous êtes prêts ? » (plural, even friends)"
            )
        ),
        GrammarEntry(
            "Articles le / la / les / un / une",
            "le/la/les = the (definite, known things). un/une/des = a/an/some (first mention). Partitives du/de la/des = some (uncountables: du pain, de la patience). After negation: pas de — « Je n'ai pas de temps. »",
            "le matatu / la gare / les enfants",
            "A1", chapter = 1,
            examples = listOf(
                "First → known: « Un homme entre. L'homme parle. »",
                "Some: « Je veux du riz et de la viande. »",
                "Negated: « Il n'y a pas de bus. » (des → de)"
            )
        ),
        GrammarEntry(
            "Être vs avoir",
            "être = identity/state (je suis étudiant, elle est fatiguée). avoir = possession/age/body-states (j'ai 20 ans, j'ai faim, j'ai peur). Professions after être take NO article: « Il est professeur » (never *un professeur).",
            "Je suis étudiant. J'ai faim.",
            "A1", chapter = 3,
            examples = listOf(
                "Age: « J'ai 17 ans. » (never *je suis 17 ans)",
                "States: « J'ai raison. » / « Tu as tort. »",
                "Identity: « Ils sont kenyans. » (no article)"
            )
        ),
        GrammarEntry(
            "Passé composé (basics)",
            "avoir/être (present) + past participle. -er → -é, -ir → -i, -re → -u (+ irregulars: fait, pris, mis, dit, écrit, vu, bu). DR MRS VANDERTRAMP verbs take être and AGREE: je suis allé(e). Negation wraps the auxiliary: « Je n'ai pas mangé. »",
            "J'ai mangé. / Je suis allé au marché.",
            "A2", chapter = 4,
            examples = listOf(
                "avoir: « Nous avons fini à midi. »",
                "être + agreement: « Elle est venue tôt. »",
                "Preceding object: « les mangues que j'ai mangées »"
            )
        ),
        GrammarEntry(
            "Negation ne…pas (and friends)",
            "Wrap the verb: ne + verb + pas/jamais/rien/plus/personne/que. ne → n' before vowels. jamais/rien/personne REPLACE pas (never combine). Short replies drop ne: « Pas encore. » ne…que = only: « Je ne bois que de l'eau. »",
            "Je ne comprends pas. / Je n'ai pas le temps.",
            "A1", chapter = 6,
            examples = listOf(
                "Never: « Il ne vient jamais. » (no pas!)",
                "Nothing: « Je ne vois rien. »",
                "Only: « Je n'ai que dix shillings. »"
            )
        ),
        GrammarEntry(
            "Question forms (3 gears)",
            "1. Intonation (friends): « Tu viens ? » 2. est-ce que (everywhere): « Est-ce que tu viens ? » 3. Inversion (formal): « Viens-tu ? » (add -t-: « Va-t-il… ? »). Question words: qui, où (≠ ou!), quand, comment, pourquoi → parce que, combien (+ de), quel (agrees).",
            "Où est-ce que tu habites ?",
            "A1", chapter = 6,
            examples = listOf(
                "Neutral: « Quand part le bus ? »",
                "Formal: « Où habitez-vous ? »",
                "Which: « Quelle ligne ? Quels horaires ? »"
            )
        ),
        GrammarEntry(
            "Gender of nouns (prediction rules)",
            "Masculine: -age, -ment, -isme, -oir, -ème (Greek: le problème, le système). Feminine: -tion, -sion, -té, -ette, -elle, -ie. ~80% reliable — then memorize le/la WITH each noun. Traps: le problème (m), la main (f), l'examen (m), la leçon (f).",
            "le problème (m) / la chance (f)",
            "A1", chapter = 1,
            examples = listOf(
                "Masculine: « le voyage, le gouvernement, le courage »",
                "Feminine: « la nation, la liberté, la bicyclette »",
                "Traps: « le système, la fin, la nuit, le musée »"
            )
        ),
        GrammarEntry(
            "Futur proche (aller + infinitive)",
            "Present of aller + infinitive = about to / going to. Je vais manger, on va gagner, ils vont partir. Plans and intentions — easier than futur simple, accepted everywhere including exams.",
            "Je vais manger. / Nous allons partir.",
            "A2", chapter = 4,
            examples = listOf(
                "Tonight: « Ce soir, on va étudier. »",
                "Warning: « Attention, tu vas tomber ! »",
                "Question: « Vous allez voter ? »"
            )
        )
    )
    return (curated + fromPhrases).distinctBy { it.title + it.body }.sortedBy { levelRank(it.level) }
}

/** Textbook chapter: numbered, leveled, grouping related rules like a manual. */
data class GrammarChapter(
    val n: Int,
    val title: String,
    val level: String,
    val entries: List<GrammarEntry>
)

private val CHAPTER_TITLES = mapOf(
    1 to "Le nom et l'article",
    2 to "Adjectifs et adverbes",
    3 to "Le verbe",
    4 to "Temps et modes",
    5 to "Pronoms et relatifs",
    6 to "La phrase et le style"
)

private fun chapterOf(e: GrammarEntry): Pair<Int, String> {
    if (e.chapter in 1..6) return e.chapter to (CHAPTER_TITLES[e.chapter] ?: "")
    val t = (e.title + " " + e.body).lowercase()
    fun has(vararg ks: String) = ks.any { k -> t.contains(k) }
    return when {
        has("subjonctif", "conditionnel", "plus-que-parfait", "futur antérieur",
            "passé composé", "imparfait", "futur", "auxiliaire", "participe") ->
            4 to "Temps et modes"
        has("pronom", "cod", "coi", "relatif", "possessif", "démonstratif",
            " qui ", " que ", " dont ", " y ", " en ", " lui ", " leur ") ->
            5 to "Pronoms et relatifs"
        has("question", "négation", "ne…pas", "ne ...pas", "préposition",
            "conjonction", "discours", "passif", "registre", "interjection",
            "préposition", "car ", "donc", "inversion") ->
            6 to "La phrase et le style"
        has("adjectif", "adverbe", "-ment", "comparatif", "superlatif",
            "meilleur", "mieux", "accord", "position", "couleur") ->
            2 to "Adjectifs et adverbes"
        has("verbe", "présent", "conjugaison", "être", "avoir", "aller", "faire",
            "prendre", "mettre", "-er", "-ir", "-re", "réfléchi", "impératif") ->
            3 to "Le verbe"
        has("article", "genre", "pluriel", "masculin", "féminin", "nom ",
            "le /", "la /", "un/une", "partitif", "du ", "de la") ->
            1 to "Le nom et l'article"
        else -> 1 to "Le nom et l'article"
    }
}

/** Group a filtered entry list into numbered textbook chapters. */
fun buildGrammarChapters(entries: List<GrammarEntry>): List<GrammarChapter> {
    val byChapter = entries.groupBy { chapterOf(it) }
    return byChapter.entries.map { (key, list) ->
        val (n, title) = key
        val level = list.minByOrNull { levelRank(it.level) }?.level ?: "A1"
        GrammarChapter(n, title, level, list.sortedBy { levelRank(it.level) })
    }.sortedBy { it.n }
}

@Composable
fun GrammarScreen(store: Store, speaker: Speaker, onBack: () -> Unit, onDrills: () -> Unit = {}) {
    val lang = store.helpLang
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
    val chapters = remember(rows) { buildGrammarChapters(rows) }

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
                listOf("All", "A1", "A2", "B1", "B2").forEach { lv -> Chip(lv, level == lv) { level = lv } }
            }
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Sp.xs)
            ) {
                chapters.forEach { ch ->
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            SectionHeader("📖 Chapitre ${ch.n} · ${ch.title}")
                            Text(
                                lang.t(
                                    "${ch.entries.size} règles · niveau ${ch.level}",
                                    "Kanuni ${ch.entries.size} · kiwango ${ch.level}",
                                    "Rules ${ch.entries.size} · level ${ch.level}"
                                ),
                                style = T.caption, color = InkMuted
                            )
                        }
                    }
                    items(ch.entries, key = { it.title + it.body }) { e ->
                        val struggling = remember(e.title) {
                            store.mistakes.keys.any { it == e.title.trim().lowercase() }
                        }
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
                                Text("§ ", style = T.bodySemi, color = Cobalt)
                                Text(e.title, style = T.bodySemi, color = Ink, modifier = Modifier.weight(1f))
                                if (struggling) {
                                    Text(
                                        "🔥",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(end = Sp.xs)
                                            .semantics { contentDescription = "You struggle with this" }
                                    )
                                }
                                Text(
                                    "🔊", fontSize = 18.sp,
                                    modifier = Modifier
                                        .clickable { speaker.speak(e.title) }
                                        .padding(end = Sp.xs)
                                        .semantics { contentDescription = "Hear ${e.title}"; role = Role.Button }
                                )
                                Text(e.level, style = T.caption, color = Cobalt)
                            }
                            Text(e.body, style = T.secondary, color = InkSoft)
                            Text("📝 ${e.example}", style = T.caption, color = Emerald)
                            (listOf(e.example).filter { it.isNotBlank() } + e.examples)
                                .distinct().take(4).drop(1).forEach { ex ->
                                    Text("• $ex", style = T.caption, color = InkSoft)
                                }
                            Text(
                                lang.t("⚡ S'entraîner", "⚡ Jizoeze", "⚡ Practice"),
                                style = T.label, color = Cobalt,
                                modifier = Modifier
                                    .clip(Rad.pill)
                                    .background(CobaltSoft)
                                    .clickable(onClick = onDrills)
                                    .padding(horizontal = Sp.sm, vertical = Sp.xs)
                                    .semantics { contentDescription = "Drill this rule"; role = Role.Button }
                            )
                        }
                    }
                }
                if (chapters.isEmpty()) {
                    item {
                        Text(
                            lang.t(
                                "No rules match — try another word 🔍",
                                "Hakuna kanuni — jaribu neno lingine 🔍",
                                "Hakuna rule — jaribu word ingine 🔍"
                            ),
                            style = T.secondary, color = InkSoft
                        )
                    }
                }
            }
        }
    }
}
