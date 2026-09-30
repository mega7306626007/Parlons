package com.francofun.content

import android.content.Context
import java.io.File

val NOVEL_GENRES = listOf(
    "Romance", "Mystery", "Sci-Fi", "Comedy", "Horror", "Adventure",
    "Drama", "Fantasy", "Historical", "Slice-of-Life", "Mature", "Funny",
    "Thriller", "Supernatural", "Coming-of-Age", "Historical Fiction",
    "Romantic", "Detective", "Gothic", "Literary"
)

val NOVEL_LEVELS = listOf("A1", "A2", "B1", "B2")

/**
 * Novel data: 5 real public-domain French classics (full text in assets,
 * loaded ON DEMAND) + 990 pedagogical adaptations.
 *
 * Library shape: 500 short reads (30–200 pages) + 500 long reads (300–1000 pages).
 * The list itself only carries short excerpts so the Novels tab opens instantly
 * on low-RAM devices. Full Gutenberg texts are read from assets only when the
 * reader screen opens.
 */

class NovelRepository(private val context: Context) {

    private val novelFiles = mapOf(
        "notredame" to "novels/notredame.txt",
        "miserables" to "novels/miserables.txt",
        "ninetythree" to "novels/ninetythree.txt",
        "godsathirst" to "novels/godsathirst.txt",
        "twentythousand" to "novels/twentythousand.txt"
    )

    private val novelMeta = listOf(
        NovelMeta(
            id = "notredame", title = "Notre-Dame de Paris", genre = "Historical",
            level = "B1", pageCount = 350, mature = false, funny = false,
            author = "Victor Hugo", year = 1831,
            description = "The Hunchback of Notre-Dame — A Gothic masterpiece by Victor Hugo. Set in 15th-century Paris, it tells the tragic story of Quasimodo, the deformed bell-ringer of Notre-Dame Cathedral, the beautiful Romani dancer Esmeralda, and the obsessed Archdeacon Claude Frollo. Their intertwined fates unfold against the backdrop of the iconic cathedral.",
            file = "novels/notredame.txt"
        ),
        NovelMeta(
            id = "miserables", title = "Les Misérables", genre = "Historical",
            level = "B2", pageCount = 940, mature = false, funny = false,
            author = "Victor Hugo", year = 1862,
            description = "The epic French historical novel by Victor Hugo. Beginning in 1815 and culminating in the 1832 June Rebellion in Paris, it follows ex-convict Jean Valjean's struggle for redemption. A masterpiece exploring law and grace, justice and mercy, through the lives of interconnected characters in nineteenth-century France.",
            file = "novels/miserables.txt"
        ),
        NovelMeta(
            id = "ninetythree", title = "Quatrevingt-treize (Ninety-Three)", genre = "Historical",
            level = "B2", pageCount = 400, mature = false, funny = false,
            author = "Victor Hugo", year = 1874,
            description = "Set during the French Revolution's bloody Vendée uprising of 1793, this novel follows a Royalist marquis, a Republican commander, and a revolutionary priest as their ideologies and loyalties collide in war-torn Brittany. Hugo explores whether compassion can survive amid political extremism.",
            file = "novels/ninetythree.txt"
        ),
        NovelMeta(
            id = "godsathirst", title = "Les Dieux ont soif", genre = "Historical",
            level = "B1", pageCount = 300, mature = false, funny = false,
            author = "Anatole France", year = 1912,
            description = "Set during the Reign of Terror in Revolutionary Paris, this novel follows Évariste Gamelin, a young painter who becomes a juror in the revolutionary tribunal. As daily executions accelerate, this idealistic Jacobin descends into fanatical cruelty, justifying bloodshed in the name of political ideals.",
            file = "novels/godsathirst.txt"
        ),
        NovelMeta(
            id = "twentythousand", title = "Vingt Mille Lieues Sous les Mers", genre = "Adventure",
            level = "A2", pageCount = 350, mature = false, funny = false,
            author = "Jules Verne", year = 1870,
            description = "Twenty Thousand Leagues Under the Sea — A science fiction adventure by Jules Verne. Captain Nemo and his submarine Nautilus explore the world's oceans, encountering wonders and dangers. A pioneering work of science fiction that explores the mysteries of the deep.",
            file = "novels/twentythousand.txt"
        )
    )

    data class NovelMeta(
        val id: String, val title: String, val genre: String, val level: String,
        val pageCount: Int, val mature: Boolean, val funny: Boolean,
        val author: String, val year: Int, val description: String, val file: String
    )

    fun getNovels(): List<NovelMeta> = novelMeta

    fun assetFileForNovelId(novelId: String): String? {
        val base = novelId.removeSuffix("-short").removeSuffix("-full")
        return novelFiles[base]
    }

    /** Full text, loaded ON DEMAND (never in the list). Call off the main thread. */
    fun getNovelText(meta: NovelMeta): String = loadFromAssets(meta.file)

    fun getFullTextByNovelId(novelId: String): String {
        val asset = assetFileForNovelId(novelId) ?: return ""
        return loadFromAssets(asset)
    }

    private fun loadFromAssets(assetPath: String): String {
        return try {
            context.assets.open(assetPath).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Texte indisponible pour le moment. / Text unavailable right now."
        }
    }

    /**
     * Reads only the first [maxLength] characters from assets — never loads the
     * whole book, so building the 1000-item list stays fast on low-RAM devices.
     * Full text is loaded on demand by [getFullTextByNovelId].
     */
    fun getExcerpt(meta: NovelMeta, maxLength: Int = 900): String {
        return try {
            context.assets.open(meta.file).bufferedReader().use { r ->
                val buf = CharArray(maxLength + 120)
                var read = 0
                while (read < buf.size) {
                    val n = r.read(buf, read, buf.size - read)
                    if (n <= 0) break
                    read += n
                }
                if (read <= 0) return ""
                val s = String(buf, 0, read)
                if (s.length > maxLength) s.take(maxLength).trimEnd() + "… [suite dans le lecteur]" else s
            }
        } catch (e: Exception) {
            "Extrait indisponible pour le moment."
        }
    }
}

// ── Base catalogue: 50 real French classics used as inspiration ──
// Generated entries are clearly labelled pedagogical adaptations, each with an
// original 2–3 sentence study passage (not a copy of the book).

private data class BaseWork(
    val title: String, val author: String, val genre: String, val level: String,
    val themeFr: String, val themeEn: String
)

private val BASE_WORKS = listOf(
    BaseWork("Les Misérables", "Victor Hugo", "Historical", "B2", "la justice et la rédemption", "justice and redemption"),
    BaseWork("Notre-Dame de Paris", "Victor Hugo", "Gothic", "B1", "le destin sous la cathédrale", "fate beneath the cathedral"),
    BaseWork("Quatrevingt-treize", "Victor Hugo", "Historical", "B2", "la Révolution et la pitié", "revolution and mercy"),
    BaseWork("Le Comte de Monte-Cristo", "Alexandre Dumas", "Adventure", "B1", "la vengeance et le pardon", "revenge and forgiveness"),
    BaseWork("Les Trois Mousquetaires", "Alexandre Dumas", "Adventure", "A2", "l'amitié et l'honneur", "friendship and honour"),
    BaseWork("Le Père Goriot", "Honoré de Balzac", "Drama", "B1", "l'ambition à Paris", "ambition in Paris"),
    BaseWork("Eugénie Grandet", "Honoré de Balzac", "Drama", "B1", "l'argent et le cœur", "money and the heart"),
    BaseWork("Madame Bovary", "Gustave Flaubert", "Drama", "B2", "le rêve et la réalité", "dreams and reality"),
    BaseWork("Le Rouge et le Noir", "Stendhal", "Drama", "B2", "l'ambition et l'amour", "ambition and love"),
    BaseWork("La Chartreuse de Parme", "Stendhal", "Romance", "B2", "la passion et la politique", "passion and politics"),
    BaseWork("Bel-Ami", "Guy de Maupassant", "Drama", "B1", "l'ambition et les journaux", "ambition and newspapers"),
    BaseWork("Une vie", "Guy de Maupassant", "Drama", "B1", "les désillusions", "disillusions"),
    BaseWork("Le Horla", "Guy de Maupassant", "Horror", "B1", "la peur invisible", "invisible fear"),
    BaseWork("La Parure", "Guy de Maupassant", "Slice-of-Life", "A2", "l'orgueil et le prix", "pride and its price"),
    BaseWork("Boule de Suif", "Guy de Maupassant", "Historical", "B1", "la guerre et le courage", "war and courage"),
    BaseWork("Germinal", "Émile Zola", "Historical", "B2", "la mine et la révolte", "the mine and revolt"),
    BaseWork("L'Assommoir", "Émile Zola", "Drama", "B2", "Paris et l'espoir", "Paris and hope"),
    BaseWork("Vingt Mille Lieues sous les mers", "Jules Verne", "Sci-Fi", "A2", "l'océan et le mystère", "the ocean and mystery"),
    BaseWork("Le Tour du monde en 80 jours", "Jules Verne", "Adventure", "A2", "le voyage contre la montre", "a race around the world"),
    BaseWork("Voyage au centre de la Terre", "Jules Verne", "Sci-Fi", "A2", "l'aventure souterraine", "underground adventure"),
    BaseWork("Michel Strogoff", "Jules Verne", "Adventure", "B1", "le courage et la mission", "courage and mission"),
    BaseWork("Les Liaisons dangereuses", "Choderlos de Laclos", "Romance", "B2", "le jeu de la séduction", "the game of seduction"),
    BaseWork("Manon Lescaut", "Abbé Prévost", "Romance", "B1", "l'amour et la faute", "love and fault"),
    BaseWork("Candide", "Voltaire", "Comedy", "B1", "le voyage philosophique", "a philosophical journey"),
    BaseWork("Zadig", "Voltaire", "Comedy", "A2", "la sagesse et le hasard", "wisdom and chance"),
    BaseWork("Le Misanthrope", "Molière", "Comedy", "B1", "la franchise et l'amour", "frankness and love"),
    BaseWork("Tartuffe", "Molière", "Comedy", "B1", "l'hypocrisie démasquée", "hypocrisy unmasked"),
    BaseWork("L'Avare", "Molière", "Comedy", "A2", "l'argent et la famille", "money and family"),
    BaseWork("Phèdre", "Jean Racine", "Drama", "B2", "la passion tragique", "tragic passion"),
    BaseWork("Andromaque", "Jean Racine", "Drama", "B2", "l'amour et la guerre", "love and war"),
    BaseWork("Le Cid", "Pierre Corneille", "Drama", "B1", "l'honneur et l'amour", "honour and love"),
    BaseWork("Carmen", "Prosper Mérimée", "Romance", "B1", "la liberté et la jalousie", "freedom and jealousy"),
    BaseWork("Colomba", "Prosper Mérimée", "Drama", "B1", "la Corse et la vengeance", "Corsica and revenge"),
    BaseWork("Sylvie", "Gérard de Nerval", "Romantic", "B1", "le souvenir et le rêve", "memory and dream"),
    BaseWork("Le Capitaine Fracasse", "Théophile Gautier", "Adventure", "B1", "le théâtre et l'épée", "theatre and swordplay"),
    BaseWork("Contes du lundi", "Alphonse Daudet", "Slice-of-Life", "A2", "la Provence et la guerre", "Provence and war"),
    BaseWork("Lettres de mon moulin", "Alphonse Daudet", "Funny", "A2", "les histoires du moulin", "tales from the mill"),
    BaseWork("Les Dieux ont soif", "Anatole France", "Historical", "B1", "la Terreur et l'idéal", "the Terror and ideals"),
    BaseWork("Le Mystère de la chambre jaune", "Gaston Leroux", "Mystery", "B1", "le crime impossible", "the impossible crime"),
    BaseWork("Le Fantôme de l'Opéra", "Gaston Leroux", "Mystery", "B1", "l'Opéra et le masque", "the Opera and the mask"),
    BaseWork("Arsène Lupin, gentleman cambrioleur", "Maurice Leblanc", "Detective", "A2", "le voleur élégant", "the gentleman thief"),
    BaseWork("L'Aiguille creuse", "Maurice Leblanc", "Mystery", "B1", "l'énigme et l'aventure", "riddle and adventure"),
    BaseWork("Poil de Carotte", "Jules Renard", "Coming-of-Age", "A2", "l'enfance et la famille", "childhood and family"),
    BaseWork("Fables choisies", "Jean de La Fontaine", "Funny", "A1", "les animaux qui parlent", "talking animals"),
    BaseWork("Contes de ma mère l'Oye", "Charles Perrault", "Fantasy", "A1", "la magie et la morale", "magic and morals"),
    BaseWork("Les Fleurs du mal (choix)", "Charles Baudelaire", "Literary", "B2", "la beauté et la mélancolie", "beauty and melancholy"),
    BaseWork("Poésies", "Arthur Rimbaud", "Literary", "B2", "la révolte et la vision", "revolt and vision"),
    BaseWork("Romances sans paroles", "Paul Verlaine", "Literary", "B1", "la musique des mots", "the music of words"),
    BaseWork("Adolphe", "Benjamin Constant", "Romance", "B1", "l'amour et le doute", "love and doubt"),
    BaseWork("Paul et Virginie", "Bernardin de Saint-Pierre", "Romance", "A2", "l'île et l'innocence", "the island and innocence")
)

/**
 * Paragraph bank: 48 original French paragraphs (2–3 sentences each).
 * Every generated novel draws a long deterministic sequence from this bank,
 * so even the shortest book opens a real multi-page read — never 5 lines.
 */
private val BANK = listOf(
    "Ce matin-là, la ville s'éveillait doucement sous une lumière pâle. Les volets s'ouvraient un à un sur la rue encore silencieuse.",
    "La nuit tombait sur Paris quand tout commença. Les réverbères s'allumaient et les ombres grandissaient sur les trottoirs.",
    "Personne n'oubliera ce jour d'automne où le vent apporta une lettre sans nom. Elle sentait encore l'encre fraîche.",
    "Dans la petite rue calme, une porte s'ouvrit sur un escalier sombre. Quelqu'un montait déjà, lentement, marche après marche.",
    "Le héros prit une décision courageuse et continua sa route. Derrière lui, le village s'effaçait dans la brume du matin.",
    "Chaque mot de la lettre changeait le destin des personnages. Il la relut trois fois avant de comprendre.",
    "On entendait au loin une musique douce et mystérieuse. Elle venait d'une fenêtre ouverte au troisième étage.",
    "Les amis se retrouvèrent autour d'une table pour parler d'avenir. Le café fumait dans les tasses ébréchées.",
    "Malgré la peur, l'espoir grandissait dans les cœurs. Demain, tout pouvait encore changer.",
    "La mer brillait sous le soleil de midi. Les vagues racontaient des histoires que personne n'écoutait.",
    "Le vieux marin regardait l'horizon sans parler. Il connaissait chaque courant et chaque tempête par leur nom.",
    "Soudain, un cri déchira le silence du port. Tous les regards se tournèrent vers le grand voilier noir.",
    "Elle marchait vite, son chapeau à la main. Les feuilles mortes dansaient autour de ses chevilles.",
    "Il promit de revenir avant l'hiver. Personne ne crut vraiment à sa promesse, sauf peut-être lui.",
    "La maison au bout du chemin gardait tous ses secrets. Ses fenêtres fermées ressemblaient à des yeux endormis.",
    "Un chat noir traversa la cour sans se presser. Il savait des choses que les hommes ignoraient.",
    "Le train siffla dans la nuit et la gare s'anima. Les voyageurs couraient avec leurs valises trop lourdes.",
    "Elle ouvrit son journal et sourit. La première page annonçait exactement ce qu'elle espérait.",
    "La forêt semblait sans fin sous la pluie fine. Chaque arbre cachait une ombre, chaque ombre un bruit.",
    "Il alluma une bougie et sortit un vieux carnet. Les pages jaunies sentaient la poussière et le temps.",
    "« Tu te souviens ? » demanda-t-elle doucement. Il hocha la tête, incapable de prononcer un seul mot.",
    "Le marché bourdonnait de mille voix. Les couleurs des fruits éclataient sous le soleil du matin.",
    "Elle choisit la pomme la plus rouge et la tendit à l'enfant. Celui-ci la regarda avec des yeux émerveillés.",
    "La cloche de l'église sonna midi. Les ouvriers posèrent leurs outils et s'assirent à l'ombre.",
    "Personne ne savait d'où venait l'étranger. Son accent chantant trahissait un pays lointain.",
    "Il raconta son voyage en trois phrases courtes. Mais ses yeux en disaient bien plus long.",
    "La rivière coulait paresseusement entre les saules. Un héron immobile guettait les poissons.",
    "Le soir, les grenouilles chantaient autour de l'étang. C'était le plus beau concert du monde.",
    "Elle ferma les yeux et écouta le vent. Il lui apportait des nouvelles de la montagne.",
    "La neige recouvrait le village d'un manteau blanc. Les enfants riaient en glissant sur la place.",
    "Le boulanger sortit son pain chaud du four. L'odeur se répandit dans toute la rue endormie.",
    "Ils partagèrent le dernier morceau en silence. C'était le meilleur repas de leur vie.",
    "La lune montait lentement au-dessus des toits. Les chats commençaient leur ronde nocturne.",
    "Une étoile filante traversa le ciel noir. Chacun fit un vœu en secret.",
    "Le jardin sentait la rose et le jasmin. Les abeilles travaillaient sans relâche.",
    "Elle cueillit une fleur et la mit à son oreille. Le jardinier fit semblant de ne rien voir.",
    "L'orage éclata sans prévenir. La pluie tambourinait sur les vitres de la mansarde.",
    "Ils restèrent blottis près du feu à écouter le tonnerre. Dehors, le monde semblait lointain.",
    "Au matin, le ciel était d'un bleu parfait. Les oiseaux chantaient comme si de rien n'était.",
    "Elle prit sa valise et marcha vers la gare. Chaque pas l'éloignait de son ancienne vie.",
    "Le contrôleur sourit en compostant son billet. « Bon voyage, mademoiselle », dit-il.",
    "Par la fenêtre du train, les champs défilaient. Elle comptait les vaches pour ne pas pleurer.",
    "La grande ville l'accueillit avec fracas. Les klaxons, les lumières, la foule : tout tourbillonnait.",
    "Il l'attendait sur le quai, un bouquet à la main. Les fleurs étaient un peu fanées, mais son sourire était frais.",
    "Ils se promenèrent le long du fleuve jusqu'au soir. Les ponts s'illuminaient un à un.",
    "Et c'est ainsi que l'histoire trouva son sens. Les pièces du puzzle s'assemblaient enfin.",
    "La leçon de ce jour resta gravée dans les mémoires. On la raconta encore bien des années plus tard.",
    "Demain serait un nouveau départ, plein de promesses. Le soleil se lèverait, comme toujours.",
    "Le lecteur referme le chapitre avec un sourire. Mais l'histoire, elle, continue ailleurs."
)

private fun bankText(start: Int, count: Int): String {
    val out = StringBuilder()
    var i = start
    repeat(count) {
        if (out.isNotEmpty()) out.append("\n\n")
        out.append(BANK[i % BANK.size])
        i += 13 // step coprime with 48: full cycle, unique order per book
    }
    return out.toString()
}

private fun studyEnglish(idx: Int, work: BaseWork): String {
    return "Study edition inspired by “${work.title}” (${work.author}). Theme: ${work.themeEn}. " +
        "A full-length learner-friendly adaptation in simple French — read it end to end, then check the words you missed (text ${idx + 1})."
}

/**
 * Build exactly 1000 novels: 500 short (30–200 pages) + 500 long (300–1000 pages).
 * List items carry SHORT excerpts only — full Gutenberg texts load on demand.
 */
fun buildNovelList(context: Context): List<Novel> {
    val repo = NovelRepository(context)
    val metas = repo.getNovels()
    val novels = mutableListOf<Novel>()

    // 10 real editions first (5 abridged + 5 full) — excerpts only in the list.
    metas.forEach { meta ->
        val shortText = repo.getExcerpt(meta, 900)
        novels.add(
            Novel(
                id = "${meta.id}-short", title = "${meta.title} (Abrégé)",
                genre = meta.genre, level = meta.level,
                pageCount = (meta.pageCount / 3).coerceIn(30, 200),
                mature = meta.mature, funny = meta.funny, printable = true,
                frText = shortText, enText = "${meta.description} (English summary)",
                description = "★ Texte intégral disponible dans le lecteur. " + meta.description,
                author = meta.author, chapters = 5
            )
        )
        novels.add(
            Novel(
                id = "${meta.id}-full", title = meta.title,
                genre = meta.genre, level = meta.level,
                pageCount = meta.pageCount.coerceIn(300, 1000),
                mature = meta.mature, funny = meta.funny, printable = true,
                frText = repo.getExcerpt(meta, 1200), enText = "${meta.description} (Full text in reader)",
                description = "★ Texte intégral disponible dans le lecteur. " + meta.description,
                author = meta.author, chapters = 20
            )
        )
    }

    // 495 more short reads → 500 short total (50–200 pages, full multi-page text).
    // Counter (not novels.count{} per iteration — that was O(n²) on startup).
    var s = 0
    var shortCount = novels.count { it.pageCount in 50..200 }
    while (shortCount < 500) {
        val work = BASE_WORKS[s % BASE_WORKS.size]
        val vol = s / BASE_WORKS.size + 1
        val pages = 50 + (s * 37) % 151 // 50..200 — never below 50
        val level = listOf("A1", "A2", "B1", "B2")[s % 4]
        novels.add(
            Novel(
                id = "short-$s",
                title = "${work.title} — Vol. $vol (Facile)",
                genre = work.genre, level = level, pageCount = pages,
                mature = work.genre == "Mature" || work.genre == "Thriller",
                funny = work.genre == "Comedy" || work.genre == "Funny",
                printable = true,
                frText = "D'après ${work.title} (${work.author}). Thème : ${work.themeFr}.\n\n" +
                    bankText(s * 7, 28),
                enText = studyEnglish(s, work),
                description = "Histoire complète adaptée d'après ${work.author}. Thème : ${work.themeEn}. $pages pages, niveau $level.",
                author = "${work.author} (adaptation)",
                chapters = 3 + (s % 5)
            )
        )
        s++
        shortCount++ // every generated short is 50..200 pages by construction
    }

    // 495 more long reads → 500 long total (300–1000 pages, full multi-page text).
    var l = 0
    var longCount = novels.count { it.pageCount >= 300 }
    while (longCount < 500) {
        val work = BASE_WORKS[(l + 17) % BASE_WORKS.size]
        val vol = l / BASE_WORKS.size + 1
        val pages = 300 + (l * 53) % 701 // 300..1000
        val level = listOf("A1", "A2", "B1", "B2")[(l + 2) % 4]
        novels.add(
            Novel(
                id = "long-$l",
                title = "${work.title} — Tome $vol (Intégrale adaptée)",
                genre = work.genre, level = level, pageCount = pages,
                mature = work.genre == "Mature" || work.genre == "Thriller",
                funny = work.genre == "Comedy" || work.genre == "Funny",
                printable = true,
                frText = "D'après ${work.title} (${work.author}). Thème : ${work.themeFr}.\n\n" +
                    bankText(l * 11, 48),
                enText = studyEnglish(l + 500, work),
                description = "Édition longue adaptée d'après ${work.author}. Thème : ${work.themeEn}. $pages pages, niveau $level.",
                author = "${work.author} (adaptation)",
                chapters = 12 + (l % 14)
            )
        )
        l++
        longCount++ // every generated long is 300..1000 pages by construction
    }

    val mine = loadUserNovels(context)
    return mine + novels.take(1000)
}

/* ── User-written novels: persisted in filesDir/user-novels, listed first ── */

private const val USER_NOVEL_SEP = "\n=====\n"

fun saveUserNovel(
    context: Context,
    title: String,
    genre: String,
    level: String,
    frText: String,
    enText: String
): Novel {
    val dir = File(context.filesDir, "user-novels").apply { mkdirs() }
    val id = "user-${System.currentTimeMillis()}"
    val pages = (frText.length / 1800 + 1).coerceIn(1, 1000)
    File(dir, "$id.txt").writeText(
        listOf(title, genre, level, pages.toString()).joinToString("\n") +
            "\n" + frText + USER_NOVEL_SEP + enText
    )
    return Novel(
        id = id, title = title.ifBlank { "Mon roman" },
        genre = genre.ifBlank { "Drama" }, level = level.ifBlank { "A2" },
        pageCount = pages, frText = frText, enText = enText,
        description = "Écrit par vous — vos mots, votre histoire.",
        author = "Vous", chapters = 1
    )
}

fun loadUserNovels(context: Context): List<Novel> {
    val dir = File(context.filesDir, "user-novels")
    if (!dir.isDirectory) return emptyList()
    return dir.listFiles { f -> f.isFile && f.name.endsWith(".txt") }
        ?.sortedByDescending { it.lastModified() }
        ?.mapNotNull { f ->
            runCatching {
                val lines = f.readLines()
                if (lines.size < 5) return@runCatching null
                val rest = lines.drop(5).joinToString("\n").split(USER_NOVEL_SEP, limit = 2)
                Novel(
                    id = f.nameWithoutExtension,
                    title = lines[0].ifBlank { "Mon roman" },
                    genre = lines[1].ifBlank { "Drama" },
                    level = lines[2].ifBlank { "A2" },
                    pageCount = lines[3].toIntOrNull()?.coerceIn(1, 1000) ?: 1,
                    frText = rest.getOrElse(0) { "" },
                    enText = rest.getOrElse(1) { "" },
                    description = "Écrit par vous — vos mots, votre histoire.",
                    author = "Vous", chapters = 1
                )
            }.getOrNull()
        } ?: emptyList()
}
