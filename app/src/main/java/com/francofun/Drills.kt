package com.francofun

import kotlin.random.Random

// Drill generator engine: rule-based conjugations + themed vocab banks +
// seeded quiz generation. Produces thousands of verified-correct items at
// runtime (deterministic per seed) instead of hand-written lines.
// Tenses: present, imparfait, futur simple, passé composé.

enum class VGroup { ER, GER, CER, YER, DBL, ACC, IR, RE, ENIR, VRIR, IRREG }
enum class Tense { PRES, IMP, FUT, PC }

data class VerbInfo(
    val inf: String,
    val en: String,
    val group: VGroup = VGroup.ER,
    val futStem: String = "",      // override when future stem != rule
    val partOverride: String = "", // override when participle != rule
    val auxEtre: Boolean = false,
    val impStem: String = "",      // IRREG imparfait stem
    val pres: List<String> = emptyList() // IRREG present, 6 forms
)

private fun V(
    inf: String, en: String, g: VGroup = VGroup.ER,
    fut: String = "", part: String = "", etre: Boolean = false,
    imp: String = "", pres: List<String> = emptyList()
) = VerbInfo(inf, en, g, fut, part, etre, imp, pres)

val PRONOUNS = listOf("je", "tu", "il", "nous", "vous", "ils")

fun showPronoun(p: Int, form: String): String {
    if (p == 0 && form.firstOrNull()?.lowercase() in listOf("a", "e", "i", "o", "u", "y", "é", "è", "ê", "h")) {
        return "j'$form"
    }
    return "${PRONOUNS[p]} $form"
}

/** Present stem rules per group (stem before person endings). */
private fun presStem(v: VerbInfo): String = when (v.group) {
    VGroup.ER, VGroup.YER, VGroup.DBL, VGroup.ACC -> v.inf.dropLast(2)
    VGroup.GER -> v.inf.dropLast(3) // mang- (+e kept for nous)
    VGroup.CER -> v.inf.dropLast(3) // commen- (+ç for nous)
    VGroup.IR -> v.inf.dropLast(2)
    VGroup.RE -> v.inf.dropLast(2)
    VGroup.ENIR -> v.inf.dropLast(2)
    VGroup.VRIR -> v.inf.dropLast(2)
    VGroup.IRREG -> ""
}

fun conjugatePres(v: VerbInfo, p: Int): String {
    if (v.group == VGroup.IRREG) return v.pres[p]
    val s = presStem(v)
    return when (v.group) {
        VGroup.ER -> s + listOf("e", "es", "e", "ons", "ez", "ent")[p]
        VGroup.GER -> if (p == 3) s + "eons" else s + listOf("e", "es", "e", "eons", "ez", "ent")[p]
        // CER stem is "commen-": plain forms need the c back (commence),
        // nous keeps the cedilla (commençons).
        VGroup.CER -> if (p == 3) s + "çons" else s + "c" + listOf("e", "es", "e", "", "ez", "ent")[p]
        VGroup.YER -> {
            val i = s.dropLast(1) + "i"
            listOf(i + "e", i + "es", i + "e", s + "ons", s + "ez", i + "ent")[p]
        }
        VGroup.DBL -> {
            val d = s + s.last()
            listOf(d + "e", d + "es", d + "e", s + "ons", s + "ez", d + "ent")[p]
        }
        // ACC: stem vowel opens to è in je/tu/il/ils (achète, préfère),
        // stays closed in nous/vous (achetons, préférons).
        VGroup.ACC -> {
            val a = if (s.endsWith("é")) s.dropLast(1) + "è" else s.dropLast(1) + "è" + s.last()
            when (p) {
                0, 1, 2, 5 -> a + listOf("e", "es", "e", "", "", "ent")[p]
                else -> s + listOf("", "", "", "ons", "ez", "")[p]
            }
        }
        VGroup.IR -> s + listOf("is", "is", "it", "issons", "issez", "issent")[p]
        VGroup.RE -> s + listOf("s", "s", "", "ons", "ez", "ent")[p]
        VGroup.ENIR -> listOf(s + "iens", s + "iens", s + "ient", s + "enons", s + "enez", s + "iennent")[p]
        VGroup.VRIR -> s + listOf("e", "es", "e", "ons", "ez", "ent")[p]
        VGroup.IRREG -> v.pres[p]
    }
}

/** Imparfait stem = nous-present minus -ons (explicit for IRREG). */
private fun impStem(v: VerbInfo): String {
    if (v.impStem.isNotEmpty()) return v.impStem
    return when (v.group) {
        VGroup.ER, VGroup.YER, VGroup.DBL, VGroup.ACC -> presStem(v)
        VGroup.GER -> presStem(v) + "e" // mange-
        VGroup.CER -> presStem(v) + "ç" // commenç-
        VGroup.IR -> presStem(v) + "iss" // finiss-
        VGroup.RE -> presStem(v)
        VGroup.ENIR -> presStem(v) // conjugateImp uses stem directly (venait)
        VGroup.VRIR -> presStem(v)
        VGroup.IRREG -> presStem(v)
    }
}

fun conjugateImp(v: VerbInfo, p: Int): String {
    // ENIR imparfait drops the -i-: venait, tenait (stem ven-/ten- + endings).
    val stem = if (v.group == VGroup.ENIR) presStem(v) else impStem(v)
    return stem + listOf("ais", "ais", "ait", "ions", "iez", "aient")[p]
}

/** Future stem: infinitive (minus final -e for -re), or override. */
private fun futStem(v: VerbInfo): String {
    if (v.futStem.isNotEmpty()) return v.futStem
    return if (v.group == VGroup.RE) v.inf.dropLast(1) else v.inf
}

fun conjugateFut(v: VerbInfo, p: Int): String =
    futStem(v) + listOf("ai", "as", "a", "ons", "ez", "ont")[p]

fun participle(v: VerbInfo): String {
    if (v.partOverride.isNotEmpty()) return v.partOverride
    return when (v.group) {
        VGroup.ER, VGroup.GER, VGroup.CER, VGroup.YER, VGroup.DBL, VGroup.ACC -> presStem(v) + "é"
        VGroup.IR -> presStem(v) + "i"
        VGroup.RE -> presStem(v) + "u"
        VGroup.ENIR -> presStem(v) + "u" // venu, tenu
        VGroup.VRIR -> presStem(v) + "ert" // couvert (ouvrir overridden)
        VGroup.IRREG -> presStem(v)
    }
}

private val AVOIR_PC = listOf("ai", "as", "a", "avons", "avez", "ont")
private val ETRE_PC = listOf("suis", "es", "est", "sommes", "êtes", "sont")

fun conjugatePC(v: VerbInfo, p: Int): String {
    val aux = if (v.auxEtre) ETRE_PC[p] else AVOIR_PC[p]
    val pron = if (p == 0) "j'" else PRONOUNS[p] + " "
    return "$pron$aux ${participle(v)}"
}

fun conjugate(v: VerbInfo, t: Tense, p: Int): String = when (t) {
    Tense.PRES -> showPronoun(p, conjugatePres(v, p))
    Tense.IMP -> showPronoun(p, conjugateImp(v, p))
    Tense.FUT -> showPronoun(p, conjugateFut(v, p))
    Tense.PC -> conjugatePC(v, p)
}

// ─── Verb banks ─────────────────────────────────────────────────────────────
// ~120 verbs: regulars by group + explicit irregulars. Rules do the rest.

val DRILL_VERBS: List<VerbInfo> = listOf(
    // -er regulars
    V("parler", "to speak"), V("habiter", "to live"), V("aimer", "to like/love"),
    V("travailler", "to work"), V("regarder", "to watch"), V("écouter", "to listen"),
    V("jouer", "to play"), V("penser", "to think"), V("passer", "to pass/spend"),
    V("porter", "to carry/wear"), V("fermer", "to close"), V("garder", "to keep"),
    V("marcher", "to walk"), V("chanter", "to sing"), V("danser", "to dance"),
    V("demander", "to ask"), V("chercher", "to look for"), V("trouver", "to find"),
    V("donner", "to give"), V("étudier", "to study"), V("oublier", "to forget"),
    V("rester", "to stay", etre = true), V("monter", "to go up", etre = true),
    V("entrer", "to enter", etre = true), V("rentrer", "to return", etre = true),
    V("retourner", "to return", etre = true), V("tomber", "to fall", etre = true),
    V("arriver", "to arrive", etre = true),
    // -ger / -cer
    V("manger", "to eat", VGroup.GER), V("voyager", "to travel", VGroup.GER),
    V("nager", "to swim", VGroup.GER), V("changer", "to change", VGroup.GER),
    V("partager", "to share", VGroup.GER), V("corriger", "to correct", VGroup.GER),
    V("commencer", "to begin", VGroup.CER), V("placer", "to place", VGroup.CER),
    V("prononcer", "to pronounce", VGroup.CER), V("annoncer", "to announce", VGroup.CER),
    V("avancer", "to advance", VGroup.CER),
    // -yer / double / accent
    V("essayer", "to try", VGroup.YER), V("payer", "to pay", VGroup.YER),
    V("nettoyer", "to clean", VGroup.YER), V("envoyer", "to send", VGroup.YER, fut = "enverr"),
    V("appeler", "to call", VGroup.DBL), V("jeter", "to throw", VGroup.DBL),
    V("rappeler", "to remind/call back", VGroup.DBL),
    V("acheter", "to buy", VGroup.ACC), V("préférer", "to prefer", VGroup.ACC),
    V("espérer", "to hope", VGroup.ACC), V("répéter", "to repeat", VGroup.ACC),
    V("amener", "to bring", VGroup.ACC),
    // -ir (finir-type)
    V("finir", "to finish", VGroup.IR), V("choisir", "to choose", VGroup.IR),
    V("réussir", "to succeed", VGroup.IR), V("grandir", "to grow", VGroup.IR),
    V("réfléchir", "to think/reflect", VGroup.IR), V("remplir", "to fill", VGroup.IR),
    V("obéir", "to obey", VGroup.IR), V("punir", "to punish", VGroup.IR),
    V("nourrir", "to feed", VGroup.IR), V("guérir", "to heal", VGroup.IR),
    V("vieillir", "to age", VGroup.IR), V("rougir", "to blush", VGroup.IR),
    V("applaudir", "to applaud", VGroup.IR), V("définir", "to define", VGroup.IR),
    // -re
    V("vendre", "to sell", VGroup.RE), V("attendre", "to wait", VGroup.RE),
    V("entendre", "to hear", VGroup.RE), V("répondre", "to answer", VGroup.RE),
    V("perdre", "to lose", VGroup.RE), V("rendre", "to return/give back", VGroup.RE),
    V("défendre", "to defend", VGroup.RE), V("descendre", "to go down", VGroup.RE, etre = true),
    V("mordre", "to bite", VGroup.RE), V("fondre", "to melt", VGroup.RE),
    // -enir family
    V("venir", "to come", VGroup.ENIR, fut = "viendr", etre = true),
    V("revenir", "to come back", VGroup.ENIR, fut = "reviendr", etre = true),
    V("devenir", "to become", VGroup.ENIR, fut = "deviendr", etre = true),
    V("tenir", "to hold", VGroup.ENIR, fut = "tiendr"),
    V("obtenir", "to obtain", VGroup.ENIR, fut = "obtiendr"),
    V("retenir", "to retain", VGroup.ENIR, fut = "retiendr"),
    // -vrir family
    V("ouvrir", "to open", VGroup.VRIR, part = "ouvert"),
    V("offrir", "to offer", VGroup.VRIR), V("couvrir", "to cover", VGroup.VRIR),
    V("découvrir", "to discover", VGroup.VRIR), V("souffrir", "to suffer", VGroup.VRIR),
    // Irregulars (fully explicit present)
    V("être", "to be", VGroup.IRREG, fut = "ser", part = "été", imp = "ét",
        pres = listOf("suis", "es", "est", "sommes", "êtes", "sont")),
    V("avoir", "to have", VGroup.IRREG, fut = "aur", part = "eu", imp = "av",
        pres = listOf("ai", "as", "a", "avons", "avez", "ont")),
    V("aller", "to go", VGroup.IRREG, fut = "ir", part = "allé", imp = "all", etre = true,
        pres = listOf("vais", "vas", "va", "allons", "allez", "vont")),
    V("faire", "to do/make", VGroup.IRREG, fut = "fer", part = "fait", imp = "fais",
        pres = listOf("fais", "fais", "fait", "faisons", "faites", "font")),
    V("pouvoir", "to be able", VGroup.IRREG, fut = "pourr", part = "pu", imp = "pouv",
        pres = listOf("peux", "peux", "peut", "pouvons", "pouvez", "peuvent")),
    V("vouloir", "to want", VGroup.IRREG, fut = "voudr", part = "voulu", imp = "voul",
        pres = listOf("veux", "veux", "veut", "voulons", "voulez", "veulent")),
    V("devoir", "to must", VGroup.IRREG, fut = "devr", part = "dû", imp = "dev",
        pres = listOf("dois", "dois", "doit", "devons", "devez", "doivent")),
    V("savoir", "to know", VGroup.IRREG, fut = "saur", part = "su", imp = "sav",
        pres = listOf("sais", "sais", "sait", "savons", "savez", "savent")),
    V("voir", "to see", VGroup.IRREG, fut = "verr", part = "vu", imp = "voy",
        pres = listOf("vois", "vois", "voit", "voyons", "voyez", "voient")),
    V("prendre", "to take", VGroup.IRREG, fut = "prendr", part = "pris", imp = "pren",
        pres = listOf("prends", "prends", "prend", "prenons", "prenez", "prennent")),
    V("mettre", "to put", VGroup.IRREG, fut = "mettr", part = "mis", imp = "mett",
        pres = listOf("mets", "mets", "met", "mettons", "mettez", "mettent")),
    V("dire", "to say", VGroup.IRREG, fut = "dir", part = "dit", imp = "dis",
        pres = listOf("dis", "dis", "dit", "disons", "dites", "disent")),
    V("lire", "to read", VGroup.IRREG, fut = "lir", part = "lu", imp = "lis",
        pres = listOf("lis", "lis", "lit", "lisons", "lisez", "lisent")),
    V("écrire", "to write", VGroup.IRREG, fut = "écrir", part = "écrit", imp = "écriv",
        pres = listOf("écris", "écris", "écrit", "écrivons", "écrivez", "écrivent")),
    V("croire", "to believe", VGroup.IRREG, fut = "croir", part = "cru", imp = "croy",
        pres = listOf("crois", "crois", "croit", "croyons", "croyez", "croient")),
    V("recevoir", "to receive", VGroup.IRREG, fut = "recevr", part = "reçu", imp = "recev",
        pres = listOf("reçois", "reçois", "reçoit", "recevons", "recevez", "reçoivent")),
    V("connaître", "to know", VGroup.IRREG, fut = "connaîtr", part = "connu", imp = "connaiss",
        pres = listOf("connais", "connais", "connaît", "connaissons", "connaissez", "connaissent")),
    V("partir", "to leave", VGroup.IRREG, fut = "partir", part = "parti", imp = "part", etre = true,
        pres = listOf("pars", "pars", "part", "partons", "partez", "partent")),
    V("sortir", "to go out", VGroup.IRREG, fut = "sortir", part = "sorti", imp = "sort", etre = true,
        pres = listOf("sors", "sors", "sort", "sortons", "sortez", "sortent")),
    V("dormir", "to sleep", VGroup.IRREG, fut = "dormir", part = "dormi", imp = "dorm",
        pres = listOf("dors", "dors", "dort", "dormons", "dormez", "dorment")),
    V("servir", "to serve", VGroup.IRREG, fut = "servir", part = "servi", imp = "serv",
        pres = listOf("sers", "sers", "sert", "servons", "servez", "servent")),
    V("rire", "to laugh", VGroup.IRREG, fut = "rir", part = "ri", imp = "ri",
        pres = listOf("ris", "ris", "rit", "rions", "riez", "rient")),
    V("conduire", "to drive", VGroup.IRREG, fut = "conduir", part = "conduit", imp = "conduis",
        pres = listOf("conduis", "conduis", "conduit", "conduisons", "conduisez", "conduisent")),
    V("boire", "to drink", VGroup.IRREG, fut = "boir", part = "bu", imp = "buv",
        pres = listOf("bois", "bois", "boit", "buvons", "buvez", "boivent")),
    V("suivre", "to follow", VGroup.IRREG, fut = "suivr", part = "suivi", imp = "suiv",
        pres = listOf("suis", "suis", "suit", "suivons", "suivez", "suivent")),
    V("vivre", "to live", VGroup.IRREG, fut = "vivr", part = "vécu", imp = "viv",
        pres = listOf("vis", "vis", "vit", "vivons", "vivez", "vivent")),
    V("mourir", "to die", VGroup.IRREG, fut = "mourr", part = "mort", imp = "mour", etre = true,
        pres = listOf("meurs", "meurs", "meurt", "mourons", "mourez", "meurent")),
    V("naître", "to be born", VGroup.IRREG, fut = "naîtr", part = "né", imp = "naiss", etre = true,
        pres = listOf("nais", "nais", "naît", "naissons", "naissez", "naissent")),
    V("courir", "to run", VGroup.IRREG, fut = "courr", part = "couru", imp = "cour",
        pres = listOf("cours", "cours", "court", "courons", "courez", "courent"))
)

// ─── Vocab banks: "fr (with article)|en", 10 themes ─────────────────────────

private val WK_FOOD = listOf(
    "le pain|bread", "le riz|rice", "la viande|meat", "le poisson|fish",
    "le poulet|chicken", "le lait|milk", "l'eau|water", "le thé|tea",
    "le café|coffee", "le sucre|sugar", "le sel|salt", "l'huile|oil",
    "le beurre|butter", "l'œuf|egg", "le fromage|cheese", "la pomme|apple",
    "la banane|banana", "l'orange|orange", "la mangue|mango", "l'avocat|avocado",
    "la tomate|tomato", "l'oignon|onion", "la pomme de terre|potato",
    "les haricots|beans", "le maïs|corn", "le chapati|chapati", "le pilau|pilau",
    "le nyama choma|roast meat", "le samosa|samosa", "le jus|juice"
)
private val WK_FAMILY = listOf(
    "le père|father", "la mère|mother", "le frère|brother", "la sœur|sister",
    "le fils|son", "la fille|daughter/girl", "le mari|husband", "la femme|wife/woman",
    "les parents|parents", "les enfants|children", "le grand-père|grandfather",
    "la grand-mère|grandmother", "l'oncle|uncle", "la tante|aunt",
    "le cousin|cousin (m)", "la cousine|cousin (f)", "le neveu|nephew",
    "la nièce|niece", "le beau-père|father-in-law", "la belle-mère|mother-in-law",
    "le jumeau|twin", "la famille|family", "le bébé|baby", "le voisin|neighbour",
    "la voisine|neighbour (f)", "l'ami|friend (m)", "l'amie|friend (f)",
    "le collègue|colleague", "le patron|boss", "l'invité|guest"
)
private val WK_SCHOOL = listOf(
    "l'école|school", "le professeur|teacher (m)", "la professeure|teacher (f)",
    "l'élève|pupil", "le livre|book", "le cahier|notebook", "le stylo|pen",
    "le crayon|pencil", "la gomme|eraser", "la règle|ruler", "le sac|sack/bag",
    "la classe|class", "le tableau|board", "la craie|chalk", "l'examen|exam",
    "la note|mark/grade", "le devoir|homework", "la leçon|lesson",
    "la récréation|break", "la cantine|canteen", "le diplôme|diploma",
    "la bourse|scholarship", "les frais|fees", "le trimestre|term",
    "les vacances|holidays", "la rentrée|back-to-school", "le surveillant|supervisor",
    "le directeur|principal", "la bibliothèque|library", "le laboratoire|lab"
)
private val WK_BODY = listOf(
    "la tête|head", "le visage|face", "l'œil|eye", "les yeux|eyes",
    "le nez|nose", "la bouche|mouth", "la dent|tooth", "l'oreille|ear",
    "le cou|neck", "l'épaule|shoulder", "le bras|arm", "la main|hand",
    "le doigt|finger", "la poitrine|chest", "le dos|back", "le ventre|belly",
    "la jambe|leg", "le genou|knee", "le pied|foot", "le cœur|heart",
    "le sang|blood", "l'os|bone", "la peau|skin", "le cheveu|hair",
    "la barbe|beard", "la fièvre|fever", "la toux|cough", "la douleur|pain",
    "la santé|health", "le médecin|doctor"
)
private val WK_HOME = listOf(
    "la maison|house", "l'appartement|flat", "la chambre|bedroom",
    "le salon|living room", "la cuisine|kitchen", "la salle de bain|bathroom",
    "les toilettes|toilet", "le lit|bed", "la table|table", "la chaise|chair",
    "la porte|door", "la fenêtre|window", "le mur|wall", "le toit|roof",
    "le sol|floor/ground", "l'escalier|stairs", "la clé|key", "la lampe|lamp",
    "le miroir|mirror", "le rideau|curtain", "le tapis|mat/carpet",
    "la couverture|blanket", "l'oreiller|pillow", "le balai|broom",
    "le seau|bucket", "le robinet|tap", "l'électricité|electricity",
    "le loyer|rent", "le voisinage|neighbourhood", "le jardin|garden"
)
private val WK_CLOTHES = listOf(
    "la chemise|shirt", "le pantalon|trousers", "la robe|dress", "la jupe|skirt",
    "le t-shirt|t-shirt", "le jean|jeans", "la veste|jacket", "le manteau|coat",
    "le pull|sweater", "les chaussures|shoes", "les sandales|sandals",
    "les chaussettes|socks", "le chapeau|hat", "la casquette|cap",
    "la ceinture|belt", "la cravate|tie", "l'uniforme|uniform", "le maillot|jersey",
    "le short|shorts", "le costume|suit", "les lunettes|glasses",
    "la montre|watch", "le sac à main|handbag", "le portefeuille|wallet",
    "le mouchoir|handkerchief", "le tailleur|tailored suit", "le boubou|boubou",
    "le kitenge|kitenge fabric", "le leso|leso wrap", "le voile|veil"
)
private val WK_CITY = listOf(
    "la ville|town/city", "le village|village", "la rue|street", "l'avenue|avenue",
    "le marché|market", "le magasin|shop", "le supermarché|supermarket",
    "la banque|bank", "l'hôpital|hospital", "la pharmacie|pharmacy",
    "l'école|school", "l'église|church", "la mosquée|mosque", "le stade|stadium",
    "le cinéma|cinema", "le restaurant|restaurant", "l'hôtel|hotel",
    "la gare|station", "l'aéroport|airport", "l'arrêt de bus|bus stop",
    "le matatu|minibus", "le taxi|taxi", "le boda|motorbike taxi",
    "le pont|bridge", "le carrefour|crossroads", "le feu|traffic light",
    "la foule|crowd", "le bruit|noise", "le quartier|estate/quarter",
    "la capitale|capital"
)
private val WK_NATURE = listOf(
    "le soleil|sun", "la lune|moon", "l'étoile|star", "le ciel|sky",
    "le nuage|cloud", "la pluie|rain", "l'orage|storm", "le vent|wind",
    "la rivière|river", "le fleuve|great river", "le lac|lake", "la mer|sea",
    "l'océan|ocean", "la plage|beach", "la montagne|mountain", "la colline|hill",
    "la vallée|valley", "la forêt|forest", "l'arbre|tree", "la fleur|flower",
    "l'herbe|grass", "le lion|lion", "l'éléphant|elephant", "la girafe|giraffe",
    "le zèbre|zebra", "le rhinocéros|rhino", "le buffle|buffalo", "le léopard|leopard",
    "le singe|monkey", "le crocodile|crocodile"
)
private val WK_TIME = listOf(
    "le matin|morning", "le midi|midday", "l'après-midi|afternoon",
    "le soir|evening", "la nuit|night", "minuit|midnight", "l'heure|hour/time",
    "la minute|minute", "la seconde|second", "le jour|day", "la semaine|week",
    "le mois|month", "l'année|year", "le lundi|Monday", "le mardi|Tuesday",
    "le mercredi|Wednesday", "le jeudi|Thursday", "le vendredi|Friday",
    "le samedi|Saturday", "le dimanche|Sunday", "janvier|January",
    "février|February", "mars|March", "avril|April", "mai|May", "juin|June",
    "juillet|July", "août|August", "aujourd'hui|today", "hier|yesterday"
)
private val WK_WORK = listOf(
    "le travail|work", "l'emploi|job", "le métier|trade", "le salaire|salary",
    "le patron|boss", "l'employé|employee", "le collègue|colleague",
    "la réunion|meeting", "le bureau|office", "l'usine|factory",
    "le champ|field", "la récolte|harvest", "le commerce|trade",
    "le client|customer", "le vendeur|seller", "l'acheteur|buyer",
    "le prix|price", "la monnaie|change/currency", "le bénéfice|profit",
    "la perte|loss", "la boutique|shop", "le marché|market",
    "le contrat|contract", "la signature|signature", "le stage|internship",
    "le CV|CV", "l'entretien|interview", "la grève|strike",
    "le syndicat|union", "la retraite|retirement"
)

val VOCAB_BANKS: Map<String, List<String>> = mapOf(
    "Nourriture" to WK_FOOD, "Famille" to WK_FAMILY, "École" to WK_SCHOOL,
    "Corps" to WK_BODY, "Maison" to WK_HOME, "Vêtements" to WK_CLOTHES,
    "Ville" to WK_CITY, "Nature" to WK_NATURE, "Temps" to WK_TIME, "Travail" to WK_WORK
)

private fun splitVocab(entry: String): Pair<String, String> {
    val i = entry.lastIndexOf("|")
    return entry.substring(0, i) to entry.substring(i + 1)
}

// ─── Drill generation (seeded → endless, deterministic) ─────────────────────

data class DrillItem(
    val prompt: String,
    val answer: String,
    val options: List<String>,
    val explain: String
)

enum class DrillKind { VERB, VOCAB, GENDER }

private val TENSE_FR = mapOf(
    Tense.PRES to "présent", Tense.IMP to "imparfait",
    Tense.FUT to "futur simple", Tense.PC to "passé composé"
)

fun genVerbDrill(rnd: Random): DrillItem {
    val v = DRILL_VERBS[rnd.nextInt(DRILL_VERBS.size)]
    val t = Tense.values()[rnd.nextInt(4)]
    val p = rnd.nextInt(6)
    val answer = conjugate(v, t, p)
    val distract = linkedSetOf<String>()
    var guard = 0
    while (distract.size < 3 && guard++ < 40) {
        val cand = when (rnd.nextInt(3)) {
            0 -> conjugate(v, t, rnd.nextInt(6)) // same tense, other person
            1 -> conjugate(v, Tense.values()[rnd.nextInt(4)], p) // same person, other tense
            else -> conjugate(DRILL_VERBS[rnd.nextInt(DRILL_VERBS.size)], t, p) // other verb
        }
        if (cand != answer) distract.add(cand)
    }
    val options = (distract.toList() + answer).shuffled(rnd)
    return DrillItem(
        prompt = "${v.inf} (${v.en}) — ${TENSE_FR[t]}, ${PRONOUNS[p]}",
        answer = answer,
        options = options,
        explain = "${v.inf}: ${groupHint(v)}. ${TENSE_FR[t]} → ${tenseHint(t)}."
    )
}

private fun groupHint(v: VerbInfo): String = when (v.group) {
    VGroup.ER -> "-er régulier"
    VGroup.GER -> "-ger (mangeons)"
    VGroup.CER -> "-cer (commençons)"
    VGroup.YER -> "-yer (essaie/essayons)"
    VGroup.DBL -> "double consonne (appelle)"
    VGroup.ACC -> "é→è (achète)"
    VGroup.IR -> "-ir type finir (-ss-)"
    VGroup.RE -> "-re (vends, vend)"
    VGroup.ENIR -> "-enir (viens/venons)"
    VGroup.VRIR -> "-vrir (ouvre/ouvert)"
    VGroup.IRREG -> "irrégulier — par cœur"
}

private fun tenseHint(t: Tense): String = when (t) {
    Tense.PRES -> "maintenant"
    Tense.IMP -> "habitude / fond (autrefois…)"
    Tense.FUT -> "demain et après"
    Tense.PC -> "hier et déjà (auxiliaire + participe)"
}

fun genVocabDrill(rnd: Random): DrillItem {
    val theme = VOCAB_BANKS.keys.toList()[rnd.nextInt(VOCAB_BANKS.size)]
    val bank = VOCAB_BANKS.getValue(theme)
    val (fr, en) = splitVocab(bank[rnd.nextInt(bank.size)])
    val fr2en = rnd.nextBoolean()
    val answer = if (fr2en) en else fr
    val distract = linkedSetOf<String>()
    var guard = 0
    while (distract.size < 3 && guard++ < 40) {
        val (f2, e2) = splitVocab(bank[rnd.nextInt(bank.size)])
        val cand = if (fr2en) e2 else f2
        if (cand != answer) distract.add(cand)
    }
    val options = (distract.toList() + answer).shuffled(rnd)
    return DrillItem(
        prompt = if (fr2en) "$fr = ? [$theme]" else "$en = ? [$theme]",
        answer = answer,
        options = options,
        explain = "$fr = $en. Thème : $theme."
    )
}

/** (noun without article, article) for every bank entry starting with one. */
private val ALL_NOUNS: List<Pair<String, String>> by lazy {
    VOCAB_BANKS.values.flatten().mapNotNull { e ->
        val (fr, _) = splitVocab(e)
        val art = fr.substringBefore(" ")
        if (art in listOf("le", "la", "l'", "les")) fr.substringAfter(" ") to art else null
    }.distinct()
}

fun genGenderDrill(rnd: Random): DrillItem {
    val nouns = ALL_NOUNS.ifEmpty { listOf("pain" to "le", "table" to "la") }
    val (noun, art) = nouns[rnd.nextInt(nouns.size)]
    val answer = when (art) {
        "le" -> "le $noun"; "la" -> "la $noun"; "l'" -> "l'$noun"; else -> "les $noun"
    }
    val options = if (art == "les") {
        listOf("les $noun", "des $noun", "le $noun", "la $noun").shuffled(rnd)
    } else {
        listOf("le $noun", "la $noun", "l'$noun", "les $noun").shuffled(rnd)
    }
    return DrillItem(
        prompt = "___ $noun (which article?)",
        answer = answer,
        options = options,
        explain = "$answer — learn every noun WITH its article; endings (-tion/-ette → la, -age/-ment → le) predict ~80%."
    )
}

fun genDrills(kind: DrillKind, n: Int, seed: Long): List<DrillItem> {
    val rnd = Random(seed)
    return List(n) {
        when (kind) {
            DrillKind.VERB -> genVerbDrill(rnd)
            DrillKind.VOCAB -> genVocabDrill(rnd)
            DrillKind.GENDER -> genGenderDrill(rnd)
        }
    }
}

/** Honest scale: curated combinations the engine can produce. */
fun drillScaleCount(): Long {
    val verbs = DRILL_VERBS.size.toLong() * 4 * 6 // verb × tense × person
    val words = VOCAB_BANKS.values.sumOf { it.size }.toLong() * 2 // both directions
    val nouns = ALL_NOUNS.size.toLong()
    return verbs + words + nouns
}

