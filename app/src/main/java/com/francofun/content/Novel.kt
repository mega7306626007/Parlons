package com.francofun.content

data class Novel(
    val id: String,
    val title: String,
    val genre: String,
    val level: String,
    val pageCount: Int,
    val mature: Boolean = false,
    val funny: Boolean = false,
    val printable: Boolean = true,
    val frText: String,
    val enText: String,
    val description: String = "",
    val author: String = "Adaptation",
    val chapters: Int = 1
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
