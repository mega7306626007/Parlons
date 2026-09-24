package com.francofun.content

data class Novel(
    val id: String,
    val title: String,
    val genre: String,
    val level: String,
    val mature: Boolean = false,
    val funny: Boolean = false,
    val printable: Boolean = true,
    val frText: String,
    val enText: String,
    val description: String = "",
    val author: String = "Adaptation"
)

data class NovelChapter(
    val title: String,
    val frText: String,
    val enText: String
)

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val year: Int,
    val frLyrics: String,
    val enTranslation: String,
    val offlineAvailable: Boolean = true
)
