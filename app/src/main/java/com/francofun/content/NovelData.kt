package com.francofun.content

val GENRES = listOf(
    "Romance", "Mystery", "Sci-Fi", "Comedy", "Horror", "Adventure",
    "Drama", "Fantasy", "Historical", "Slice-of-Life", "Mature", "Funny",
    "Thriller", "Supernatural", "Coming-of-Age"
)

val LEVELS = listOf("A1", "A2", "B1", "B2")

private fun genTitle(genre: String, idx: Int): String {
    val prefix = when (genre.lowercase()) {
        "romance" -> listOf("L'Amour de", "Le Cœur de", "Les Yeux de", "Une Nuit avec", "Sous le Signe de")
        "mystery" -> listOf("Le Mystère de", "Le Secret de", "L'Affaire de", "L'Enigme de", "La Ombre de")
        "sci-fi" -> listOf("La Galaxie de", "Le Futur de", "L'Univers de", "Au-delà de", "La Planète de")
        "comedy" -> listOf("Le Démon de", "La Farce de", "Le Piège de", "Le Bouffon de", "La Blague de")
        "horror" -> listOf("La Malédiction de", "Le Fantôme de", "La Peur de", "Le Cauchemar de", "La Terreur de")
        "adventure" -> listOf("Le Voyage de", "L'Expédition de", "La Quête de", "Le Trésor de", "La Conquête de")
        "drama" -> listOf("La Vie de", "Le Destin de", "L'Histoire de", "Le Serment de", "La Promesse de")
        "fantasy" -> listOf("Le Royaume de", "La Magie de", "Le Sortilège de", "Le Dragon de", "L'Enchanteur de")
        "historical" -> listOf("Le Siècle de", "L'Époque de", "La Révolution de", "Le Royaume Ancien de", "Le Temps de")
        "slice-of-life" -> listOf("Un Jour de", "Le Matin de", "La Soirée de", "La Promenade de", "Le Café de")
        "mature" -> listOf("Les Ombres de", "L'Obsession de", "La Tentation de", "Le Désir de", "La Nuit de")
        "funny" -> listOf("Le Drôle de", "Le Rigolo de", "Le Fou de", "Le Nabot de", "Le Clown de")
        "thriller" -> listOf("Le Piège de", "L'Attaque de", "Le Danger de", "La Traque de", "Le Complot de")
        "supernatural" -> listOf("Le Monde d'en haut", "La Force invisible de", "Le Spirituel de", "L'Âme de", "Le Magique de")
        "coming-of-age" -> listOf("L'Apprentissage de", "La Croissance de", "L'Adolescence de", "La Jeunesse de", "La Révélation de")
        else -> listOf("Le", "La", "Les")
    }.getOrElse(idx % 5) { "Le" }
    val suffix = listOf("Aventures", "Histoire", "Voyage", "Secret", "Destin", "Passion", "Mystère", "Revanche", "Rêve", "Promesse", "Légende", "Quête", "Parallèle", "Oubli", "Éveil").getOrElse(idx % 15) { "Aventures" }
    return "$prefix $suffix"
}

private fun genDescription(genre: String, level: String): String {
    val descMap = mapOf(
        "Romance" to "Un histoire d'amour touchante et passionnée.",
        "Mystery" to "Un puzzle captivant à résoudre.",
        "Sci-Fi" to "Une aventure dans un futur lointain.",
        "Comedy" to "Une histoire hilarante qui vous fera rire.",
        "Horror" to "Une histoire terrifiante pour les courageux.",
        "Adventure" to "Un voyage épique plein de dangers.",
        "Drama" to "Une histoire émouvante et profonde.",
        "Fantasy" to "Un monde magique plein de merveilles.",
        "Historical" to "Un voyage dans le passé historique.",
        "Slice-of-Life" to "Un moment simple mais significatif.",
        "Mature" to "Une histoire pour adultes avec des thèmes complexes.",
        "Funny" to "Une histoire drôle et décalée.",
        "Thriller" to "Un suspense qui vous tiendra en haleine.",
        "Supernatural" to "Des événements impossibles qui defient la réalité.",
        "Coming-of-Age" to "Le parcours d'un jeune qui découvre la vie."
    )
    val base = descMap[genre] ?: "Une histoire captivante."
    return when (level) {
        "A1" -> "$base Écrit pour les débutants avec des phrases courtes."
        "A2" -> "$base Pour intermédiaires avec un vocabulaire riche."
        "B1" -> "$base Pour les avancés avec des structures complexes."
        "B2" -> "$base Pour les experts avec un style littéraire."
        else -> base
    }
}

fun generateNovels(count: Int = 500): List<Novel> {
    val novels = mutableListOf<Novel>()
    val genWeights = mapOf(
        "Romance" to 60, "Comedy" to 50, "Funny" to 45, "Slice-of-Life" to 45,
        "Drama" to 40, "Adventure" to 40, "Fantasy" to 35, "Mystery" to 35,
        "Historical" to 30, "Mature" to 25, "Sci-Fi" to 25, "Horror" to 20,
        "Thriller" to 25, "Coming-of-Age" to 30, "Supernatural" to 15
    )
    val levelWeights = mapOf("A1" to 35, "A2" to 30, "B1" to 25, "B2" to 10)
    val rng = java.util.Random(42)

    var generated = 0
    while (generated < count) {
        val genre = genWeights.entries.shuffled(rng).first().key
        val level = levelWeights.entries.shuffled(rng).first().key
        val idx = generated % 1000

        val isMature = genre == "Mature" || (genre in listOf("Horror", "Thriller") && rng.nextDouble() < 0.3)
        val isFunny = genre == "Funny" || genre == "Comedy"

        val title = genTitle(genre, idx) + " ${generated + 1}"
        val desc = genDescription(genre, level)
        val shortFr = listOf(
            "Il était une fois, dans un village français lointain, ${title.lowercase()}. Les personnages principaux étaient ${if (isMature) "des adultes complexes" else "de jeunes gens"}. L'histoire se déroulait ${if (level == "A1") "dans un endroit simple" else "dans un cadre complexe"}.",
            "La première scène montrait ${if (isFunny) "une situation absurde" else "un moment dramatique"}. Le protagoniste devait ${if (isMature) "prendre une décision difficile" else "faire face à un défi"}.",
            "Avec le temps, ${if (isFunny) "les quiproquos se multipliaient" else "les événements prenaient une tournure inattendue"}. La tension montait ${if (level == "A1") "progressivement" else "de manière intense"}.",
            "Finalement, ${if (isMature) "les vérités cachées émergeaient" else "la vérité était révélée"}. L'histoire se terminait ${if (isFunny) "de façon comique" else "de façon émouvante"}."
        ).joinToString(" ")

        val shortEn = listOf(
            "Once upon a time, in a faraway French village, ${title.lowercase()}. The main characters were ${if (isMature) "complex adults" else "young people"}. The story took place ${if (level == "A1") "in a simple place" else "in a complex setting"}.",
            "The first scene showed ${if (isFunny) "an absurd situation" else "a dramatic moment"}. The protagonist had to ${if (isMature) "make a difficult decision" else "face a challenge"}.",
            "As time went on, ${if (isFunny) "misunderstandings multiplied" else "events took an unexpected turn"}. The tension ${if (level == "A1") "built gradually" else "intensified"}.",
            "Finally, ${if (isMature) "hidden truths emerged" else "the truth was revealed"}. The story ended ${if (isFunny) "comically" else "movingly"}."
        ).joinToString(" ")

        novels.add(Novel(
            id = "novel-${generated + 1}",
            title = title,
            genre = genre,
            level = level,
            mature = isMature,
            funny = isFunny,
            printable = true,
            frText = shortFr,
            enText = shortEn,
            description = desc,
            author = "Adaptation ${if (rng.nextBoolean()) "Éditions Parlons" else "Collection Franz"}"
        ))
        generated++
    }
    return novels
}

val ALL_NOVELS: List<Novel> by lazy { generateNovels(500) }
