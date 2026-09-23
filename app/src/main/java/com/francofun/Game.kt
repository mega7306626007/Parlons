package com.francofun

// ---- Leveling ----
fun levelForXp(xp: Int): Int = 1 + xp / 250
fun xpForLevel(level: Int): Int = (level - 1) * 250
fun xpIntoLevel(xp: Int): Pair<Int, Int> {
    val l = levelForXp(xp)
    val base = xpForLevel(l)
    return (xp - base) to 250
}
fun levelTitle(level: Int): String = when {
    level >= 20 -> "Légende 🏆"
    level >= 15 -> "Maître 🌟"
    level >= 10 -> "Expert 🎓"
    level >= 5 -> "Explorateur 🧭"
    level >= 3 -> "Voyageur ✈️"
    else -> "Débutant 🌱"
}

// ---- Badges ----
data class Badge(val id: String, val emoji: String, val fr: String, val en: String, val sw: String, val desc: String)

val BADGES: List<Badge> = listOf(
    Badge("first", "🎉", "Premier pas", "First steps", "Hatua ya kwanza", "Finish 1 lesson"),
    Badge("perfect1", "💯", "Sans faute", "First perfect lesson", "Somo kamilifu", "Finish a lesson with 100%"),
    Badge("streak3", "🔥", "Série 3", "3-day streak", "Mfululizo siku 3", "Learn 3 days in a row"),
    Badge("streak7", "🔥", "Série 7", "7-day streak", "Mfululizo siku 7", "Learn 7 days in a row"),
    Badge("streak30", "🏆", "Série 30", "30-day streak", "Mfululizo siku 30", "Learn 30 days in a row"),
    Badge("xp500", "⭐", "500 XP", "500 XP", "XP 500", "Earn 500 XP"),
    Badge("xp2000", "💫", "2000 XP", "2000 XP", "XP 2000", "Earn 2000 XP"),
    Badge("perfect5", "💯", "Perfectionniste", "Perfectionist", "Mkamilifu", "5 perfect lessons"),
    Badge("words100", "📖", "100 mots", "100 words learned", "Maneno 100", "Get 100 phrases to mastery box 2+"),
    Badge("unit1", "🏠", "Unité finie", "Finished a unit", "Umomaliza kitengo", "Earn a star in every lesson of one unit"),
    Badge("allunits", "🌍", "Tout le cours", "Finished all six units", "Umomaliza vyote", "Earn a star in every lesson of all 6 units"),
    Badge("speaker", "🎤", "Orateur", "Speaker", "Msemaji", "10 correct SPEAK answers"),
    Badge("speak50", "🎙️", "Grand orateur", "50 spoken sentences", "Sentensi 50", "50 correct SPEAK answers"),
    Badge("chatter", "🦁", "Ami de Simba", "Simba's friend", "Rafiki wa Simba", "Send 25 chat messages"),
    Badge("simbaInter", "🎓", "Intermédiaire", "Intermédiaire Simba", "Simba wa kati", "Finish an Intermédiaire Simba scenario"),
    Badge("scholar", "📚", "Érudit", "Scholar", "Mwanachuoni", "Finish all Unit 1 lessons with 2+ stars"),
    Badge("night", "🌙", "Oiseau de nuit", "Night owl", "Bundio", "Finish a lesson after 10pm"),
    Badge("earlybird", "🌅", "Lève-tôt", "Early bird", "Amkapo mapema", "Practise before 7am")
)

fun earnedBadges(
    xp: Int, streak: Int, lessonsDone: Int, perfectCount: Int,
    speakCorrect: Int, chatMsgs: Int, stars: Map<String, Int>, hour: Int,
    wordsLearned: Int = 0, unitsDone: Int = 0, totalUnits: Int = 6, simbaInter: Boolean = false
): Set<String> {
    val s = mutableSetOf<String>()
    if (lessonsDone >= 1) s += "first"
    if (perfectCount >= 1) s += "perfect1"
    if (streak >= 3) s += "streak3"
    if (streak >= 7) s += "streak7"
    if (streak >= 30) s += "streak30"
    if (xp >= 500) s += "xp500"
    if (xp >= 2000) s += "xp2000"
    if (perfectCount >= 5) s += "perfect5"
    if (wordsLearned >= 100) s += "words100"
    if (unitsDone >= 1) s += "unit1"
    if (unitsDone >= totalUnits && totalUnits > 0) s += "allunits"
    if (speakCorrect >= 10) s += "speaker"
    if (speakCorrect >= 50) s += "speak50"
    if (chatMsgs >= 25) s += "chatter"
    if (simbaInter) s += "simbaInter"
    val u1 = UNITS.find { it.id == "u1" }?.lessonIds ?: emptyList()
    if (u1.isNotEmpty() && u1.all { (stars[it] ?: 0) >= 2 }) s += "scholar"
    if (hour >= 22 || hour < 7) s += "night"
    if (hour in 5..6) s += "earlybird"
    return s
}

// ---- Daily quests ----
data class Quest(val id: String, val emoji: String, val en: String, val sw: String, val target: Int, val xpReward: Int)

val DAILY_QUESTS: List<Quest> = listOf(
    Quest("xp50", "⭐", "Earn 50 XP", "Pata XP 50", 50, 15),
    Quest("lessons2", "📖", "Finish 2 lessons", "Maliza masomo 2", 2, 20),
    Quest("speak5", "🎤", "5 correct speaking", "Ongea sahihi 5", 5, 15),
    Quest("chat5", "🦁", "Send 5 Simba messages", "Tumia Simba ujumbe 5", 5, 15),
    Quest("review10", "🔁", "Review 10 due words", "Rudia maneno 10", 10, 20)
)

fun questProgress(questId: String, todayXp: Int, todayLessons: Int, todaySpeak: Int, todayChat: Int, todayReview: Int): Int = when (questId) {
    "xp50" -> todayXp
    "lessons2" -> todayLessons
    "speak5" -> todaySpeak
    "chat5" -> todayChat
    "review10" -> todayReview
    else -> 0
}
