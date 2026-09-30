package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Textbook engine: paragraphs, rule callouts, real TABLES, decision-step
// flows (diagrams as structured steps), worked examples, warnings.
// Chapters append to TEXTBOOK; the renderer handles every block type.

sealed interface TBBlock {
    data class Para(val text: String) : TBBlock
    data class Rule(val title: String, val body: String) : TBBlock
    data class Table(val caption: String, val headers: List<String>, val rows: List<List<String>>) : TBBlock
    data class Steps(val title: String, val steps: List<String>) : TBBlock
    data class Examples(val items: List<String>) : TBBlock
    data class Warning(val text: String) : TBBlock
    data class Tip(val text: String) : TBBlock
}

data class TBChapter(
    val id: String,
    val emoji: String,
    val title: String,
    val level: String,
    val intro: String,
    val blocks: List<TBBlock>,
    // Linked learn unit ("" = general). Powers the 📕 button on unit headers.
    val unitId: String = ""
)

fun textbookChapter(id: String): TBChapter? = TEXTBOOK.find { it.id == id }

fun textbookForUnit(unitId: String): TBChapter? = TEXTBOOK.firstOrNull { it.unitId == unitId }

@Composable
fun TBBlockView(b: TBBlock) {
    when (b) {
        is TBBlock.Para -> Text(b.text, style = T.body, color = Ink)
        is TBBlock.Rule -> Column(
            Modifier.fillMaxWidth().clip(Rad.md).background(CobaltSoft)
                .border(1.dp, Cobalt, Rad.md).padding(Sp.md),
            verticalArrangement = Arrangement.spacedBy(Sp.xs)
        ) {
            Text("📌 ${b.title}", style = T.bodySemi, color = Cobalt)
            Text(b.body, style = T.body, color = Ink)
        }
        is TBBlock.Table -> Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            Text(b.caption, style = T.label, color = Cobalt)
            Column(
                Modifier.fillMaxWidth().clip(Rad.md).border(1.dp, Border, Rad.md)
            ) {
                Row(Modifier.fillMaxWidth().background(Cobalt)) {
                    b.headers.forEach { h ->
                        Text(h, style = T.label, color = White,
                            modifier = Modifier.weight(1f).padding(Sp.xs))
                    }
                }
                b.rows.forEachIndexed { i, row ->
                    Row(
                        Modifier.fillMaxWidth()
                            .background(if (i % 2 == 0) Surface else CobaltSoft)
                    ) {
                        row.forEach { cell ->
                            Text(cell, style = T.caption, color = Ink,
                                modifier = Modifier.weight(1f).padding(Sp.xs))
                        }
                    }
                }
            }
        }
        is TBBlock.Steps -> Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            Text("🔀 ${b.title}", style = T.bodySemi, color = Violet)
            b.steps.forEachIndexed { i, s ->
                Row(verticalAlignment = Alignment.Top) {
                    Text("${i + 1}→ ", style = T.bodySemi, color = Violet)
                    Text(s, style = T.body, color = Ink, modifier = Modifier.weight(1f))
                }
                if (i < b.steps.size - 1) Text("⬇", style = T.caption, color = InkMuted)
            }
        }
        is TBBlock.Examples -> Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            b.items.forEach { ex ->
                Text("• $ex", style = T.body, color = Ink,
                    modifier = Modifier.fillMaxWidth().clip(Rad.md).background(Surface)
                        .border(1.dp, Border, Rad.md).padding(Sp.sm))
            }
        }
        is TBBlock.Warning -> Text("⚠️ ${b.text}", style = T.bodySemi, color = Coral,
            modifier = Modifier.fillMaxWidth().clip(Rad.md).background(CoralSoft).padding(Sp.sm))
        is TBBlock.Tip -> Text("💡 ${b.text}", style = T.body, color = Ink,
            modifier = Modifier.fillMaxWidth().clip(Rad.md).background(GoldSoft).padding(Sp.sm))
    }
}

@Composable
fun TextbookScreen(
    chapterId: String,
    onBack: () -> Unit,
    onDrills: () -> Unit = {},
    lang: HelpLang = HelpLang.ENGLISH
) {
    val ch = textbookChapter(chapterId)
    if (ch == null) {
        AppBackground {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                Text("No chapter yet.")
                BigButton("Back", onClick = onBack)
            }
        }
        return
    }
    AppBackground(tint = GoldSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm)
                            .semantics { contentDescription = "Back"; role = Role.Button })
                    Text("${ch.emoji} ${ch.title}", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                Text("${ch.level} · ${ch.blocks.size} sections", style = T.secondary, color = InkSoft)
                Text(ch.intro, style = T.body, color = Ink)
            }
            items(ch.blocks, key = { it.hashCode() }) { b -> TBBlockView(b) }
            item {
                BigButton(
                    lang.t("⚡ Drill this chapter", "⚡ Jizoeze sura", "⚡ Drill chapter"),
                    onClick = onDrills, color = Violet
                )
            }
        }
    }
}

@Composable
fun TextbookListScreen(onBack: () -> Unit, onOpen: (String) -> Unit, lang: HelpLang = HelpLang.ENGLISH) {
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
                    Text("📕 Manuel", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                Text(
                    lang.t(
                        "${TEXTBOOK.size} chapters · rules, tables, decision flows",
                        "Sura ${TEXTBOOK.size} · kanuni, majedwali",
                        "Chapters ${TEXTBOOK.size} · rules, tables"
                    ),
                    style = T.secondary, color = InkSoft
                )
            }
            items(TEXTBOOK, key = { it.id }) { ch ->
                Card(
                    modifier = Modifier.clickable(onClick = { onOpen(ch.id) })
                        .semantics { contentDescription = "Open chapter ${ch.title}"; role = Role.Button }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(ch.emoji, fontSize = 28.sp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text(ch.title, style = T.bodySemi, color = Ink)
                            Text("${ch.level} · ${ch.blocks.size} sections", style = T.caption, color = InkSoft)
                        }
                        Text("→", style = T.section, color = InkMuted)
                    }
                }
            }
        }
    }
}

// ─── Chapter content ────────────────────────────────────────────────────────
// General grammar chapters first, then one linked chapter per learn unit.

val TEXTBOOK: List<TBChapter> = listOf(
    TBChapter(
        id = "tb-u1", emoji = "🌱", title = "Survie : saluer et se présenter", level = "A1",
        intro = "Unit 1 in textbook form: the greeting system, the être/avoir split, and the question gears — with tables you can revise in five minutes.",
        unitId = "u1",
        blocks = listOf(
            TBBlock.Para("Two layers make every greeting: the TIME word (Bonjour → Bonsoir → Bonne nuit) and the TITLE (madame/monsieur for strangers, nothing for friends). Time decides the word, company decides the title. Get both right and every door in France opens."),
            TBBlock.Table(
                "Saluer selon l'heure et la personne", listOf("Moment", "Inconnu", "Ami"),
                listOf(
                    listOf("Matin–18h", "Bonjour madame", "Salut !"),
                    listOf("Après 18h", "Bonsoir monsieur", "Coucou !"),
                    listOf("Départ (jour)", "Au revoir", "À plus !"),
                    listOf("Départ (soir)", "Bonne soirée", "À demain !"),
                    listOf("Quelqu'un dort", "Bonne nuit", "Bonne nuit !")
                )
            ),
            TBBlock.Table(
                "être ou avoir ?", listOf("Verbe", "Usage", "Exemples"),
                listOf(
                    listOf("être", "identité, état", "je suis élève, elle est fatiguée"),
                    listOf("avoir", "âge, besoins du corps", "j'ai 17 ans, j'ai faim"),
                    listOf("avoir", "peur, raison, besoin", "j'ai peur, tu as raison")
                )
            ),
            TBBlock.Steps(
                "Poser une question (3 vitesses)",
                listOf(
                    "Amis, oral → INTONATION : « Tu viens ? » (voix qui monte).",
                    "Partout, examen → EST-CE QUE : « Est-ce que tu viens ? ».",
                    "Écrit formel → INVERSION : « Viens-tu ? » (+ -t- : « Va-t-il… ? »)."
                )
            ),
            TBBlock.Rule("Se présenter en 30 secondes", "« Je m'appelle Baraka, j'ai 17 ans, j'habite à Nakuru, je suis en quatrième année. Enchanté ! » Nom (s'appeler), âge (avoir !), ville (habiter à), classe. L'ordre ne change jamais."),
            TBBlock.Examples(
                listOf(
                    "« Bonjour madame, comment allez-vous ? »",
                    "« Salut ! Ça va ? — Ça va bien, et toi ? »",
                    "« Je m'appelle Wanjiku : W-A-N-J-I-K-U. »"
                )
            ),
            TBBlock.Warning("*Je suis 17 ans, *je suis faim : l'erreur n°1 des anglophones. Âge et besoins du corps = AVOIR, toujours."),
            TBBlock.Tip("Salue chaque commerçant en entrant : « Bonjour ! » fort et clair. Au Kenya le sourire suffit ; en France le mot est obligatoire.")
        )
    ),
    TBChapter(
        id = "tb-u2", emoji = "🏠", title = "Quotidien : temps, marché, météo", level = "A1–A2",
        intro = "Unit 2 textbook: the Swahili↔French clock conversion, the 6-move bargaining script, and weather talk — tabulated for revision.",
        unitId = "u2",
        blocks = listOf(
            TBBlock.Para("French time counts from midnight; Swahili time counts from 6am. Every hour must be converted before it is spoken. The rule fits in one line — French = Swahili + 6 in the morning — but it must become a reflex, because examiners set traps exactly here."),
            TBBlock.Table(
                "Convertir l'heure", listOf("Swahili", "Français (matin)", "Français (24h)"),
                listOf(
                    listOf("saa moja (7h)", "sept heures", "7h00"),
                    listOf("saa mbili (8h)", "huit heures", "8h00"),
                    listOf("saa sita (12h)", "midi", "12h00"),
                    listOf("saa saba (13h)", "une heure de l'après-midi", "13h00"),
                    listOf("saa kumi na mbili (18h)", "six heures du soir", "18h00")
                )
            ),
            TBBlock.Steps(
                "Marchander en 6 coups",
                listOf(
                    "Saluer + demander : « Bonjour ! C'est combien ? »",
                    "Réagir : grimace + « Ooh… c'est trop cher ! »",
                    "Proposer ~60% : « Je vous propose deux cents. »",
                    "Hésiter : deux pas, « Bon, je vais réfléchir… »",
                    "Retrouver le milieu : « On se retrouve au milieu ? »",
                    "Conclure : « Marché conclu ! Merci ! »"
                )
            ),
            TBBlock.Table(
                "Météo de base", listOf("Expression", "Sens", "Suite naturelle"),
                listOf(
                    listOf("Il fait beau/chaud/froid", "nice/hot/cold", "Et demain ?"),
                    listOf("Il pleut / Il neige", "raining/snowing", "Prenez un parapluie !"),
                    listOf("Il y a du vent", "windy", "Fermez les fenêtres !")
                )
            ),
            TBBlock.Rule("Sentiments : deux listes, zéro mélange", "AVOIR : faim, soif, chaud, froid, peur, raison, besoin. ÊTRE : fatigué, content, triste, malade, prêt. Besoins du corps = avoir ; état = être."),
            TBBlock.Examples(
                listOf(
                    "« Saa mbili asubuhi → huit heures. »",
                    "« Faites-moi un petit prix, s'il vous plaît ! »",
                    "« En juillet il fait frais à Limuru. »"
                )
            ),
            TBBlock.Warning("*Il est chaud pour la météo ! Weather = IL FAIT + adjectif, toujours. « Il est chaud » parle d'une personne."),
            TBBlock.Tip("Chaque matin, dis l'heure des deux horloges à voix haute : « saa mbili — huit heures ». Deux semaines = réflexe à vie.")
        )
    ),
    TBChapter(
        id = "tb-u3", emoji = "🌆", title = "Comme un local : passé et opinions", level = "A2",
        intro = "Unit 3 textbook: passé composé in one rule, the opinion skeleton, and the on-pronoun — the three machines of everyday conversation.",
        unitId = "u3",
        blocks = listOf(
            TBBlock.Para("Eighty percent of the past tense is one sentence: movement verbs take être, everything else takes avoir. Movement = DR MRS VANDERTRAMP (aller, venir, partir, sortir…). Learn that sentence and the passé composé is nearly solved; the rest is spelling."),
            TBBlock.Table(
                "Raconter au passé", listOf("Rôle", "Temps", "Exemple"),
                listOf(
                    listOf("Action finie", "passé composé", "Je suis allé au marché."),
                    listOf("Fond / habitude", "imparfait (dès U5)", "Il faisait beau."),
                    listOf("Négation", "n' + auxiliaire + pas", "Je n'ai pas mangé."),
                    listOf("Question", "est-ce que + passé", "Est-ce que tu as vu ?")
                )
            ),
            TBBlock.Rule("Squelette d'opinion", "À mon avis… + parce que… + par exemple (nom propre + détail)… + certes…, mais…. Opinion + raison + exemple + concession = note maximale. « À mon avis, le foot unit le Kenya parce qu'en 2023 tout Nairobi portait le maillot. Certes, c'est cher, mais la joie est gratuite. »"),
            TBBlock.Steps(
                "Raconter sa journée",
                listOf(
                    "Ancre du matin : « Ce matin, je me suis levé à six heures. »",
                    "Chaîne 3 événements au passé composé : cours, marché, match.",
                    "Termine par un sentiment : « C'était une bonne journée. »"
                )
            ),
            TBBlock.Table(
                "on = nous (oral)", listOf("Écrit", "Parlé", "Exemple"),
                listOf(
                    listOf("nous allons", "on va", "On va au match ?"),
                    listOf("nous mangeons", "on mange", "On mange à midi."),
                    listOf("nous verrons", "on verra", "On verra demain.")
                )
            ),
            TBBlock.Examples(
                listOf(
                    "« Hier, je suis allé au stade, j'ai vu le derby. »",
                    "« Je suis d'accord parce que les prix ont baissé. »",
                    "« Docteur, j'ai mal à la tête depuis trois jours. »"
                )
            ),
            TBBlock.Warning("*J'ai allé : aller prend ÊTRE. *On avons : on prend les formes de IL (on a, on est, on va)."),
            TBBlock.Tip("Chaque soir, raconte ta journée en 5 phrases au passé composé. Le passé devient un réflexe, pas une règle.")
        )
    ),
    TBChapter(
        id = "tb-u4", emoji = "🧭", title = "Se débrouiller : ville et urgences", level = "A2–B1",
        intro = "Unit 4 textbook: direction scripts, money tables, and emergency lines — the French that solves real problems.",
        unitId = "u4",
        blocks = listOf(
            TBBlock.Para("Getting things done in French runs on three scripts: asking the way (imperatives + landmarks), handling money (account verbs + mobile money), and emergencies (short correct sentences). Memorize the scripts, not isolated words."),
            TBBlock.Table(
                "Demander et indiquer", listOf("Toi", "L'autre", "Exemple"),
                listOf(
                    listOf("Pour aller à… ?", "Continuez tout droit.", "Pour aller à la gare ?"),
                    listOf("Je cherche…", "C'est à côté de…", "Je cherche l'hôpital."),
                    listOf("C'est loin ?", "À cinq minutes à pied.", "C'est loin d'ici ?")
                )
            ),
            TBBlock.Table(
                "L'argent", listOf("Français", "Sens", "Phrase"),
                listOf(
                    listOf("le retrait / dépôt", "withdrawal/deposit", "Je veux faire un retrait."),
                    listOf("le virement", "transfer", "Virez sur ce compte."),
                    listOf("le solde / les frais", "balance/fees", "Quel est mon solde ?"),
                    listOf("par carte / en espèces", "by card / in cash", "Je paie par carte.")
                )
            ),
            TBBlock.Steps(
                "Urgence : 4 phrases qui sauvent",
                listOf(
                    "Alerte : « Au secours ! Appelez une ambulance ! »",
                    "Perte : « J'ai perdu mon passeport. » / « On m'a volé mon sac ! »",
                    "Détails : « C'était hier vers vingt heures, près du marché. »",
                    "Demande : « Que dois-je faire ? Où est l'ambassade ? »"
                )
            ),
            TBBlock.Rule("Check-list logement", "« C'est combien le loyer ? Combien de pièces ? Les charges sont comprises ? C'est meublé ? Il y a l'eau chaude ? » Sept questions, un bedsitter sans surprise. « C'est un peu cher… vous pouvez baisser ? » pour négocier."),
            TBBlock.Examples(
                listOf(
                    "« Tournez à gauche après le pont, c'est en face de la banque. »",
                    "« Envoyez de l'argent par téléphone, confirmez avec le PIN. »",
                    "« J'ai perdu mon portefeuille hier soir. »"
                )
            ),
            TBBlock.Warning("Perdre (objets) ≠ se perdre (soi-même). « J'ai perdu mes clés » mais « Je me suis perdu ». Deux verbes, deux situations."),
            TBBlock.Tip("Joue les trois scènes (perdu, banque, logement) à voix haute une fois par semaine. Le jour J, ce sera une rediffusion.")
        )
    ),
    TBChapter(
        id = "tb-u5", emoji = "💬", title = "Créer des liens : récits et débats", level = "B1",
        intro = "Unit 5 textbook: the two-past storytelling engine, the concede-advance debate formula, and the three-beat apology.",
        unitId = "u5",
        blocks = listOf(
            TBBlock.Para("Stories need two pasts the way football needs two teams. Imparfait paints the background (il faisait beau, j'avais peur); passé composé fires the events (je suis sorti, tout a changé). One without the other is either a painting with no action or action with no scene."),
            TBBlock.Table(
                "Deux passés", listOf("Signal", "Temps", "Exemple"),
                listOf(
                    listOf("d'habitude, chaque jour", "imparfait", "On jouait le samedi."),
                    listOf("soudain, tout à coup", "passé composé", "Soudain, il a sifflé."),
                    listOf("pendant que", "imparfait + imparfait", "Je mangeais, il parlait."),
                    listOf("quand + événement", "imparfait PUIS passé", "Je marchais quand il a plu.")
                )
            ),
            TBBlock.Steps(
                "Débattre sans se fâcher",
                listOf(
                    "Concède : « C'est vrai que… / Je comprends, mais… »",
                    "Avance : « À mon avis… parce que… »",
                    "Prouve : « Par exemple, samedi… » (nom propre + détail)",
                    "Invite : « Et toi, tu dis quoi ? »"
                )
            ),
            TBBlock.Rule("S'excuser en 3 temps", "Regret (« Je suis vraiment désolé ») + responsabilité (« C'était ma faute / J'aurais dû prévenir ») + réparation (« Je me rattraperai samedi »). Sans réparation, ce sont des excuses vides."),
            TBBlock.Examples(
                listOf(
                    "« Je marchais quand l'arbitre est arrivé. Soudain, il a sifflé ! »",
                    "« Tu n'as pas tort, mais AFC joue mieux parce que… »",
                    "« Pardon pour le retard : j'aurais dû partir plus tôt. Je paie le thé ! »"
                )
            ),
            TBBlock.Warning("Le registre se choisit : verlan et gros mots avec les amis, jamais avec l'examinateur. « Se marrer » en dissertation = pénalité directe."),
            TBBlock.Tip("Débat d'entraînement : Gor vs AFC, 2 minutes par camp, formule imposée. La passion avec une structure devient du B1.")
        )
    ),
    TBChapter(
        id = "tb-u6", emoji = "🌉", title = "Fluidité : vitesse et autonomie", level = "B1",
        intro = "Unit 6 textbook: why production replaces recognition, the 5-minute shadowing protocol, and the B1 independence checklist.",
        unitId = "u6",
        blocks = listOf(
            TBBlock.Para("Recognition feels like knowledge; production IS knowledge. Recall builds triple-strength memory traces versus multiple choice. U6 removes the training wheels — scores dip for a week, then overtake permanently. Trust the dip; it is the method working."),
            TBBlock.Steps(
                "Shadowing : protocole 5 minutes",
                listOf(
                    "Choisis 30 secondes d'audio (Simba ou chanson).",
                    "Passe 1 : marmonne le rythme, pas les mots.",
                    "Passes 2–3 : répète une seconde derrière, mélodie copiée.",
                    "Chaque semaine : nouveau clip, vitesse 1.1×."
                )
            ),
            TBBlock.Table(
                "Check-list B1 (auto-test mensuel)", listOf("Capacité", "Preuve", "Unité"),
                listOf(
                    listOf("Voyager seul", "billet, hôtel, direction", "U4"),
                    listOf("Raconter", "2 passés en ordre", "U5"),
                    listOf("Opiner", "avis + raison + exemple", "U3/U5"),
                    listOf("Suivre du natif clair", "scénario Intermédiaire", "U6"),
                    listOf("Écrire", "paragraphes liés", "U8")
                )
            ),
            TBBlock.Rule("Lire ses boîtes Leitner", "Boîtes 1–2 : fragiles, contact hebdomadaire — le marathon les sert en premier. Boîtes 4–5 : acquises, entretien mensuel. Aime ta liste boîte-1 : c'est la seule qui compte."),
            TBBlock.Examples(
                listOf(
                    "« J'mange pas » (rue) = « Je ne mange pas » (examen).",
                    "« Catch nouns + verbs » : les mots porteurs portent 70% du sens.",
                    "« On y va ? » shadowé 10× > relu 10×."
                )
            ),
            TBBlock.Warning("Relire donne l'illusion d'apprendre ; rappeler construit. Cache-toujours : page blanche ou ça ne compte pas."),
            TBBlock.Tip("Test mensuel : un scénario + un récit + une opinion, sans aide. Réussi = progrès réel, pas compteur de leçons.")
        )
    ),
    TBChapter(
        id = "tb-u8", emoji = "🎓", title = "Lycée : méthode d'examen", level = "A2–B1",
        intro = "Unit 8 textbook: the 6-habit paper method, both letter architectures, and the B+ composition recipe — the marking scheme made visible.",
        unitId = "u8",
        blocks = listOf(
            TBBlock.Para("Method beats vocabulary. Read questions first, justify from the text, never leave blanks, sweep agreements at the end. These four habits outweigh fifty extra words — they are literally what the marking scheme pays for."),
            TBBlock.Table(
                "Budget 2h", listOf("Épreuve", "Minutes", "Règle d'or"),
                listOf(
                    listOf("Compréhension", "35", "Questions d'abord, fence du paragraphe."),
                    listOf("Grammaire", "25", "Relever = copier ; trouver = reformuler."),
                    listOf("Rédaction", "50", "10 plan + 35 écrit + 5 relecture."),
                    listOf("Relecture", "10", "Verbes→sujets, adjectifs→noms.")
                )
            ),
            TBBlock.Steps(
                "Lettre formelle (6 boîtes)",
                listOf(
                    "En-tête : « Nairobi, le 3 mars 2026 » (lieu + date).",
                    "Titre : « Monsieur le Directeur, ».",
                    "Ouverture : « Je me permets de vous écrire au sujet de… »",
                    "Corps : situation → demande → justification.",
                    "Clôture : « Dans l'attente… veuillez agréer… salutations distinguées. »",
                    "Signature : nom + classe + téléphone."
                )
            ),
            TBBlock.Rule("Recette rédaction B+", "Intro (accroche + sujet + plan annoncé) + 3 paragraphes (connecteur + affirmation + parce que + exemple concret + ) + conclusion (bilan + avis + ouverture au subjonctif). Un subjonctif + un nom propre + des connecteurs variés = la grille visible."),
            TBBlock.Examples(
                listOf(
                    "« Vrai : « les mangues sont mûres » (l.4). » (verdict + preuve citée)",
                    "« D'une part… d'autre part… en revanche… en conclusion… »",
                    "« Si j'ai bien compris, vous demandez si… » (reformulation orale)"
                )
            ),
            TBBlock.Warning("Répondre avec ses connaissances au lieu du texte = zéro même si c'est vrai. L'examinateur note le TEXTE, pas le monde."),
            TBBlock.Tip("Chaque réponse doit pointer une ligne. Pas de ligne → pas de réponse. Compte tes preuves avant de rendre.")
        )
    ),
    TBChapter(
        id = "tb-u9", emoji = "🗣️", title = "Prononciation : le système sonore", level = "A1–A2",
        intro = "Unit 9 textbook: the 4 nasals, the throat R, the 3 liaison laws, and the rhythm-melody system — with drill ladders.",
        unitId = "u9",
        blocks = listOf(
            TBBlock.Para("French pronunciation is a system, not a talent. Four nasals, one throat-R, three liaison laws, final stress. Each has a drill ladder below — climb one rung daily and the oral exam becomes a performance, not a gamble."),
            TBBlock.Table(
                "Les 4 nasales", listOf("Son", "Graphies", "Exemples", "Piège"),
                listOf(
                    listOf("[ɑ̃]", "an, en", "vent, enfant", "devant p/b/m : oral ! (comme)"),
                    listOf("[ɔ̃]", "on", "bon, maison", "bon/bain !"),
                    listOf("[ɛ̃]", "in, ain, ein", "vin, pain, plein", "vin/vent !"),
                    listOf("[œ̃]", "un", "brun, lundi", "→ [ɛ̃] moderne")
                )
            ),
            TBBlock.Steps(
                "Le R en 4 marches",
                listOf(
                    "Gargarisme à l'eau : sens la vibration du fond-gorge.",
                    "À sec : « ra-re-ri-ro-ru » guttural.",
                    "Mots : rue, rouge, rare, professeur.",
                    "Grappes : trois, frère, prendre, propre (une syllabe, pas de voyelle ajoutée)."
                )
            ),
            TBBlock.Table(
                "3 lois des liaisons", listOf("Loi", "Cas", "Exemples"),
                listOf(
                    listOf("OBLIGATOIRE", "article/adjectif + nom, pronom + verbe", "les‿amis, nous‿avons"),
                    listOf("INTERDITE", "après et, h aspiré", "et // ami, les // héros"),
                    listOf("OPTIONNELLE", "style soigné", "pas‿encore, temps‿en temps")
                )
            ),
            TBBlock.Rule("Rythme et mélodie", "Accent = DERNIÈRE syllabe du groupe. Temps égal par syllabe (syllabé, pas accentué). Montée [↗] = question/suite, chute [↘] = fin. Fredonne d'abord la mélodie, ajoute les mots ensuite."),
            TBBlock.Examples(
                listOf(
                    "« Un bon vin blanc » ([œ̃]-[ɔ̃]-[ɛ̃]-[ɑ̃] en 5 mots !)",
                    "« Comment‿allez-vous ? » ([t] + [z] : showcase total)",
                    "« le-pe-tit-chat-est-mort » (6 coups égaux, applaudis)"
                )
            ),
            TBBlock.Warning("Rouler le R à l'espagnole = étranger instantané. Et : et est un MUR — pause, respire, continue, jamais de liaison."),
            TBBlock.Tip("Main sur le nez : vibre sur nasales, rien sur « comme ». Miroir + main = laboratoire de poche.")
        )
    ),
    TBChapter(
        id = "tb-u10", emoji = "🌍", title = "Afrique francophone : le français qui paie", level = "A2–B1",
        intro = "Unit 10 textbook: the neighbours, the money, and the templates — French as a professional tool, tabulated.",
        unitId = "u10",
        blocks = listOf(
            TBBlock.Para("French is next door, not overseas. DRC and Rwanda border Kenya; Dakar and Abidjan hire in French. Swahili on the street plus French in the office is a complete professional profile — trade, NGOs, mining, tourism."),
            TBBlock.Table(
                "Les voisins", listOf("Pays", "Villes", "Langues + monnaie", "Atout"),
                listOf(
                    listOf("RDC", "Kinshasa, Goma", "français + swahili", "commerce, mines, rumba"),
                    listOf("Rwanda", "Kigali", "anglais + français", "café, tourisme, EAC"),
                    listOf("Sénégal", "Dakar", "français + wolof", "UCAD, Teranga"),
                    listOf("Côte d'Ivoire", "Abidjan", "français + nouchi", "cacao, stages")
                )
            ),
            TBBlock.Steps(
                "Candidature en 5 pièces",
                listOf(
                    "Objet : « Candidature — [poste] ».",
                    "Ligne CV (en haut, gras) : « Trilingue anglais–swahili–français (B2). »",
                    "Lettre : Je me permets… + Ci-joint… + Cordialement + téléphone.",
                    "Entretien 90s : formation (passé) → expérience (présent) → projet (futur).",
                    "Suivi : « Je vous remercie de votre attention… »"
                )
            ),
            TBBlock.Rule("DELF B2 = ticket d'entrée", "Universités françaises : 50/100, aucune épreuve sous 5. Bourse : postuler avant mars (relevés + recommandations + projet d'études). Campus France : entretien EN FRANÇAIS — l'oral paie directement."),
            TBBlock.Examples(
                listOf(
                    "« Ci-joint mon CV et ma lettre. Cordialement, Baraka 0712… »",
                    "« Le franc CFA (XOF), fixe à l'euro : le taux, convertir. »",
                    "« Murakaza neza ! » (= Bienvenue — un mot local ouvre les portes)"
                )
            ),
            TBBlock.Warning("« Bises » à un recruteur = faute de registre. Échelle : Bises (amis) → Cordialement (pro) → distinguées (formel)."),
            TBBlock.Tip("5 documents mémorisés une fois (CV, mail, lettre, pitch 90s, suivi) = réutilisables à vie. Gabarits, pas talent.")
        )
    ),
    TBChapter(
        id = "tb-u12", emoji = "💬", title = "Idiomes : parler par blocs", level = "B1",
        intro = "Unit 12 textbook: natives speak in chunks. The starter arsenal, the beast zoo, and proverb closers — sorted by register.",
        unitId = "u12",
        blocks = listOf(
            TBBlock.Para("One idiom per conversation doubles perceived fluency with zero new grammar. Learn them as unbreakable blocks — never swap words inside, never translate your Sheng literally (*tirer ma jambe → dis « me faire marcher »)."),
            TBBlock.Table(
                "Arsenal de départ", listOf("Expression", "Sens", "Usage"),
                listOf(
                    listOf("C'est du gâteau.", "facile", "victoires, examens"),
                    listOf("Couper la poire en deux.", "compromis", "marché, réunions"),
                    listOf("La cerise sur le gâteau.", "bonus", "bonnes nouvelles"),
                    listOf("Se serrer les coudes.", "solidarité", "chama, équipe"),
                    listOf("Mettre son grain de sel.", "avis (annoncé)", "débats")
                )
            ),
            TBBlock.Table(
                "Le zoo", listOf("Bête", "Trait humain", "Exemple"),
                listOf(
                    listOf("paon / mule", "fier / têtu", "fier comme un paon"),
                    listOf("renard / agneau", "rusé / doux", "rusé comme un renard"),
                    listOf("lapin / chat", "lapin posé / gorge", "poser un lapin"),
                    listOf("canard / cafard", "froid / blues", "un froid de canard")
                )
            ),
            TBBlock.Steps(
                "Clore une dissertation",
                listOf(
                    "Bilan : « En somme… » (résume sans répéter).",
                    "Avis : « À mon avis… » (ta voix).",
                    "Sagesse : proverbe du sujet (pluie/difficulté, petit à petit/effort, habit/jugement).",
                    "Bonus bilingue : jumeau kenyan (« …— kama Haraka haraka… »)."
                )
            ),
            TBBlock.Rule("Registres des images", "Partout : cerise, beurre, fin des haricots, bonne pâte. Amis : casser les pieds, se marrer, kif-kif, bouffer. Reconnaître seulement : merde, putain, con (films compris, classe jamais)."),
            TBBlock.Examples(
                listOf(
                    "« Le réveil a sonné, j'ai pris mon courage à deux mains : contrôle du gâteau ! »",
                    "« Tu veux le beurre et l'argent du beurre ! » (arme de débat)",
                    "« Après la pluie, le beau temps — baada ya dhiki faraja. »"
                )
            ),
            TBBlock.Warning("Un idiome par paragraphe/conversation. Plus = frime ; au milieu du devoir = bruit. Les proverbes CLOSENT, jamais au milieu."),
            TBBlock.Tip("Raconte hier avec 3 idiomes imposés. Le récit devient un jeu — et le jeu devient du B1.")
        )
    ),
    TBChapter(
        id = "tb-u13", emoji = "🏦", title = "Banque quotidienne : la vie en phrases", level = "A1–A2",
        intro = "Unit 13 textbook: the phrase bank is revision fuel. How to mine 150 everyday sentences for pronunciation, agreement and speed.",
        unitId = "u13",
        blocks = listOf(
            TBBlock.Para("The bank's 150 sentences cover market, transport, home, school, church, football, phone, health, cooking and neighbours. They are deliberately situational (two clauses, real scenes) so each one rehearses grammar inside life, not inside tables."),
            TBBlock.Steps(
                "Méthode banque (15 min/jour)",
                listOf(
                    "Choisis UN thème (ex. marché). Lis 15 phrases à voix haute.",
                    "Cache la colonne aide : traduis vers le français de mémoire.",
                    "Marque les 3 qui résistent → cahier + Leitner.",
                    "Le lendemain : les 3 d'hier + 15 nouvelles."
                )
            ),
            TBBlock.Table(
                "Que chasser par thème", listOf("Thème", "Cible gram.", "Cible sons"),
                listOf(
                    listOf("Marché", "nombres, partitifs", "nasales (vent, temps)"),
                    listOf("Transport", "impératifs, heures", "R (rue, droite)"),
                    listOf("Maison", "avoir/être, ordres", "u/ou (dessus/dessous)"),
                    listOf("École", "passé composé", "liaisons (les‿enfants)"),
                    listOf("Santé", "depuis/pendant", "é/è (fièvre, mère)")
                )
            ),
            TBBlock.Rule("Vitesse progressive", "Semaine 1 : lis lentement, juste. Semaine 2 : même phrases, tempo normal. Semaine 3 : shadowing (une seconde derrière l'audio). Les mêmes 150 phrases, trois vitesses, trois cerveaux."),
            TBBlock.Examples(
                listOf(
                    "« Les tomates ont augmenté cette semaine. » → passé + marché.",
                    "« J'ai raté la correspondance de huit heures. » → passé + transport.",
                    "« Le toit fuit quand il pleut. » → présent d'habitude + maison."
                )
            ),
            TBBlock.Warning("Ne traverse pas la banque en diagonale : 150 phrases survolées = 0 acquises. Un thème creusé > dix thèmes effleurés."),
            TBBlock.Tip("Transforme chaque phrase en question pour un ami : « Les tomates ont augmenté ? — Oui, trop ! » Révision déguisée en conversation.")
        )
    ),
    TBChapter(
        id = "tb-u11", emoji = "🎓", title = "Maîtrise B2 : compacité et nuances", level = "B2",
        intro = "Unit 11 textbook: reported-speech machine, compression ladder, regret system, and register wardrobes — the B2 difference, tabulated.",
        unitId = "u11",
        blocks = listOf(
            TBBlock.Para("B2 is B1 compressed and shaded. Same meanings, half the words (gérondif, participles, infinitive pasts), plus stance (subjunctive moods, registers, reported distance). This chapter is the compression manual."),
            TBBlock.Table(
                "Discours indirect (verbe introducteur au passé)", listOf("Direct", "Rapporté", "Exemple"),
                listOf(
                    listOf("présent", "imparfait", "« J'ai faim » → qu'il avait faim"),
                    listOf("passé composé", "plus-que-parfait", "« J'ai mangé » → qu'il avait mangé"),
                    listOf("futur", "conditionnel", "« Je viendrai » → qu'elle viendrait"),
                    listOf("impératif", "de + infinitif", "« Tais-toi ! » → de se taire"),
                    listOf("question oui/non", "si", "« Tu viens ? » → si je venais")
                )
            ),
            TBBlock.Steps(
                "Compresser (échelle)",
                listOf(
                    "Repère quand/parce que/qui dans ton brouillon.",
                    "Même sujet + simultané → GÉRONDIF : « En mangeant, il parle. »",
                    "qui + verbe → PARTICIPE : « un élève ayant fini… »",
                    "après/quand + passé → INFINITIF : « Après avoir mangé… »"
                )
            ),
            TBBlock.Table(
                "Regrets", listOf("Structure", "Sens", "Exemple"),
                listOf(
                    listOf("si + PQP → cond. passé", "irréel passé", "Si j'avais su, je serais venu."),
                    listOf("aurait dû/pu/fallu", "hindsight", "J'aurais dû réviser."),
                    listOf("futur antérieur", "supposition passée", "Il aura oublié, c'est sûr.")
                )
            ),
            TBBlock.Rule("Armoire des registres", "SOUTENU : veuillez, prière de, inversion, ne…pas gardé. STANDARD : est-ce que, Cordialement, avis + parce que. FAMILIER (amis !) : tu, verlan, ne tombé, bosser/bouffer. Habille chaque phrase pour son occasion."),
            TBBlock.Examples(
                listOf(
                    "« Elle a dit qu'elle viendrait. » (futur → conditionnel)",
                    "« C'est en forgeant qu'on devient forgeron. »",
                    "« Veuillez patienter » / « Un moment ! » / « Attends ! » (3 registres)"
                )
            ),
            TBBlock.Warning("*Si j'aurais su : la faute B2 la plus entourée du Kenya. SI aime la famille IMPARFAIT (imparfait, plus-que-parfait) ; le CONDITIONNEL vit dans la RÉSULTANTE, jamais après si."),
            TBBlock.Tip("Réécris un paragraphe B1 en version B2 : compresse 3 subordonnées, ajoute un subjonctif et un registre marqué. Compare : moitié moins de mots, double d'effet.")
        )
    ),
    TBChapter(
        id = "tb-articles", emoji = "📰", title = "Articles et genre", level = "A1",
        intro = "Every French noun has a gender, and the article proves it. This chapter gives you prediction tables that get ~80% right, then the memorization habit for the rest.",
        blocks = listOf(
            TBBlock.Para("French has no neutral « the ». Each noun is masculine or feminine, and you must learn its article WITH it: not « table » but « la table ». The article is part of the word's identity — dictionaries list it, teachers test it, ears expect it."),
            TBBlock.Table(
                "Les articles", listOf("", "Masculin", "Féminin", "Pluriel"),
                listOf(
                    listOf("Défini (the)", "le (l')", "la (l')", "les"),
                    listOf("Indéfini (a)", "un", "une", "des"),
                    listOf("Partitif (some)", "du (de l')", "de la (de l')", "des"),
                    listOf("Après négation", "de (d')", "de (d')", "de (d')")
                )
            ),
            TBBlock.Rule(
                "La règle de la première mention",
                "First mention uses un/une/des; every later mention uses le/la/les. « Un homme entre. L'homme parle. Des enfants jouent. Les enfants rient. » Examiners check this switch in compositions."
            ),
            TBBlock.Table(
                "Prédire le genre (~80%)", listOf("Terminaison", "Genre", "Exemples"),
                listOf(
                    listOf("-age, -ment, -isme", "m", "voyage, gouvernement, courage"),
                    listOf("-oir, -phone, -scope", "m", "devoir, téléphone, microscope"),
                    listOf("-tion, -sion", "f", "nation, télévision, décision"),
                    listOf("-té, -ette, -elle", "f", "liberté, bicyclette, poubelle"),
                    listOf("-ie, -ure, -ence", "f", "vie, voiture, patience"),
                    listOf("-ème (grec)", "m ⚠️", "problème, système, programme")
                )
            ),
            TBBlock.Warning("Pièges classiques : le problème, le système, le musée (m) · la main, la fin, la nuit, la dent (f). L'apostrophe cache le genre — l'examen (m), l'école (f) : memorize it."),
            TBBlock.Steps(
                "Quel article ? (arbre de décision)",
                listOf(
                    "Négation avec pas/jamais/plus ? → DE (« Je n'ai pas de temps »).",
                    "Chose incomptable (pain, patience) ? → DU / DE LA.",
                    "Première mention ? → UN / UNE / DES.",
                    "Déjà mentionné ou unique ? → LE / LA / LES.",
                    "Devant voyelle ? → L' (« l'école, l'examen » — mais retiens le genre !)."
                )
            ),
            TBBlock.Examples(
                listOf(
                    "« Un garçon mange. Le garçon rit. » (première → connue)",
                    "« Je veux du riz et de la viande. » (partitifs)",
                    "« Il n'y a pas de bus aujourd'hui. » (négation → de)"
                )
            ),
            TBBlock.Tip("Apprends chaque nom avec son article à voix haute : « LA table, LE pain ». Trois semaines de ce réflexe valent trois ans de devinettes.")
        )
    ),
    TBChapter(
        id = "tb-passe", emoji = "⏪", title = "Passé composé", level = "A2",
        intro = "The past tense is one formula plus one list. Master the auxiliary choice and the two agreement cases and 80% of KCSE past-tense marks are yours.",
        blocks = listOf(
            TBBlock.Para("Formula: AUXILIAIRE (avoir ou être au présent) + PARTICIPE PASSÉ. « J'ai mangé. Je suis allé. Elle est venue. » Regular participles: -er → -é (parlé), -ir → -i (fini), -re → -u (vendu). Everything else is choosing the auxiliary and spelling irregulars."),
            TBBlock.Table(
                "Participles irréguliers (top 16)", listOf("Verbe", "Participe", "Auxiliaire"),
                listOf(
                    listOf("être", "été", "avoir"),
                    listOf("avoir", "eu", "avoir"),
                    listOf("faire", "fait", "avoir"),
                    listOf("prendre", "pris", "avoir"),
                    listOf("mettre", "mis", "avoir"),
                    listOf("dire", "dit", "avoir"),
                    listOf("écrire", "écrit", "avoir"),
                    listOf("lire", "lu", "avoir"),
                    listOf("voir", "vu", "avoir"),
                    listOf("boire", "bu", "avoir"),
                    listOf("aller", "allé", "être ✦"),
                    listOf("venir", "venu", "être ✦"),
                    listOf("partir", "parti", "être ✦"),
                    listOf("sortir", "sorti", "être ✦"),
                    listOf("naître", "né", "être ✦"),
                    listOf("mourir", "mort", "être ✦")
                )
            ),
            TBBlock.Rule(
                "DR MRS VANDERTRAMP — les verbes en être",
                "Devenir, Revenir, Mourir, Retourner, Sortir, Venir, Arriver, Naître, Descendre, Entrer, Rester, Tomber, Rentrer, Aller, Monter, Partir. Mouvement ou changement d'état → être. TOUT le reste → avoir."
            ),
            TBBlock.Steps(
                "Accorde-t-on ? (arbre de décision)",
                listOf(
                    "Auxiliaire être ? → OUI, accorde avec le SUJET (« Elle est allée »).",
                    "Auxiliaire avoir + objet AVANT le verbe ? → OUI, accorde avec l'objet (« les mangues que j'ai mangées »).",
                    "Auxiliaire avoir, objet après ou absent ? → NON (« J'ai mangé les mangues »).",
                    "Verbe réfléchi ? → être + accord sujet (sauf objet après : « Elle s'est lavé les mains »)."
                )
            ),
            TBBlock.Table(
                "Négation et place", listOf("Cas", "Exemple"),
                listOf(
                    listOf("Standard", "Je n'ai pas mangé."),
                    listOf("Jamais/rien/plus", "Je n'ai jamais vu. / Je n'ai rien dit."),
                    listOf("Avec être", "Elle n'est pas venue."),
                    listOf("Ordre des mots", "ne + AUXILIAIRE + pas + participe")
                )
            ),
            TBBlock.Examples(
                listOf(
                    "« Hier, je suis allé au marché, j'ai acheté des mangues. »",
                    "« Les mangues que j'ai achetées étaient mûres. » (objet avant → -es)",
                    "« Nous nous sommes levés tôt. » (réfléchi + accord)"
                )
            ),
            TBBlock.Warning("Les fautes qui tuent : *j'ai allé (aller = être !) · *elle est mangé (manger = avoir !) · *ils ont allés (avoir + sujet → jamais d'accord). Demande-toi TOUJOURS l'auxiliaire d'abord."),
            TBBlock.Tip("Raconte ta journée d'hier chaque soir en 5 phrases au passé composé. Deux semaines = réflexe installé.")
        )
    ),
    TBChapter(
        id = "tb-pronoms", emoji = "🔗", title = "Pronoms objets", level = "B1",
        intro = "French pronouns jump BEFORE the verb — the opposite of English. One fixed order, one flip in orders, and the tiny words y/en that carry whole suitcases of meaning.",
        blocks = listOf(
            TBBlock.Para("English: « I see him ». French: « Je LE vois » — the pronoun leaps before the verb. Every pronoun except in affirmative orders lives pre-verb. Drill the leap until it feels wrong to say « *Je vois le » for « him » (garde « Je vois Pierre » pour les noms !)."),
            TBBlock.Table(
                "L'ordre fixe (ne change JAMAIS)", listOf("Position", "Pronoms", "Exemple"),
                listOf(
                    listOf("1", "me te se nous vous", "Il me voit."),
                    listOf("2", "le la les", "Je les aime."),
                    listOf("3", "lui leur", "Je lui parle."),
                    listOf("4", "y", "J'y vais. (= à Paris)"),
                    listOf("5", "en", "J'en veux. (= du gâteau)")
                )
            ),
            TBBlock.Rule(
                "COD ou COI ?",
                "Le verbe décide : voir/aimer/manger + QUELQU'UN (direct) → le/la/les. parler/donner/écrire + À quelqu'un → lui/leur. Apprends chaque verbe AVEC sa préposition : parler DE, penser À, avoir besoin DE."
            ),
            TBBlock.Steps(
                "Place le pronom",
                listOf(
                    "Phrase normale ? → TOUT avant le verbe : « Tu me le donnes ? »",
                    "Ordre affirmatif ? → TOUT après, avec traits d'union : « Donne-le-moi ! »",
                    "Ordre négatif ? → comme normal : « Ne me le donne pas ! »",
                    "Deux pronoms ? → respecte l'ordre 1-2-3-4-5 : « Je le lui ai dit. »"
                )
            ),
            TBBlock.Table(
                "y et en", listOf("Mot", "Remplace", "Exemples"),
                listOf(
                    listOf("y", "à + lieu", "J'y vais. / J'y pense."),
                    listOf("y", "à + chose", "Je m'y habitue."),
                    listOf("en", "de + chose", "J'en parle. / J'en ai peur."),
                    listOf("en", "quantité / partitif", "J'en veux deux. / Il y en a.")
                )
            ),
            TBBlock.Examples(
                listOf(
                    "« Tu vois Marie ? — Oui, je la vois. »",
                    "« Parle à ton père ! — Je lui parle demain. »",
                    "« Tu veux du gâteau ? — Oui, j'en veux un peu. »"
                )
            ),
            TBBlock.Warning(" me/te + le/la → m'/t' ? NON : « Tu me le donnes » garde me entier (seul je/tu/il + voyelle s'élident : j'ai, t'aime, s'habille). Et : lui = him OR her — le contexte décide, pas le mot."),
            TBBlock.Tip("Réécris 5 phrases de ton cahier en remplaçant chaque nom par son pronom. Le saut pré-verbal devient un réflexe en une semaine.")
        )
    )
)
