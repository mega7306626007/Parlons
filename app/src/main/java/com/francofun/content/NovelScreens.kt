package com.francofun.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

/* ═══════════ NOVEL READER ═══════════ */

@Composable
fun NovelTab(
    novels: List<Novel>,
    onNovelClick: (Novel) -> Unit,
    onWriteClick: () -> Unit,
    onBack: () -> Unit
) {
    var selectedGenre by remember { mutableStateOf("All") }
    var selectedLevel by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showMature by remember { mutableStateOf(false) }
    val genres = listOf("All") + GENRES
    val levels = listOf("All") + LEVELS

    AppBackground(tint = CobaltSoft) {
        LazyColumn(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                    Text("📚 Novels", style = T.screenTitle, color = Ink)
                }
                Text("500+ novels in French · printable · with English translations", style = T.secondary, color = InkSoft)
            }
            item {
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                        SectionHeader("🔍 Filter & Search")
                        OutlinedSearchField("Search novels...", searchQuery) { searchQuery = it }
                        Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                            Chip("Genre", selectedGenre != "All", "Genre") {
                                selectedGenre = if (selectedGenre == "All") "All" else "All"
                            }
                            Chip("Level", selectedLevel != "All", "Level") {
                                selectedLevel = if (selectedLevel == "All") "All" else "All"
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                            GenreChip(selectedGenre) { selectedGenre = it }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                            LevelChip(selectedLevel) { selectedLevel = it }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔞 Mature", style = T.label, color = Ink, modifier = Modifier.weight(1f))
                            Box(
                                Modifier.size(40.dp).clip(Rad.md).background(if (showMature) Cobalt else Border).clickable { showMature = !showMature },
                                contentAlignment = Alignment.Center
                            ) { Text(if (showMature) "✓" else "○", color = if (showMature) White else Ink, fontSize = 16.sp) }
                        }
                    }
                }
            }
            item {
                Text("${filteredNovels(novels, selectedGenre, selectedLevel, searchQuery, showMature).size} novels found", style = T.caption, color = InkMuted)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                    QuickNovelAction("📖", "All Novels", Cobalt, Modifier.weight(1f)) {
                        selectedGenre = "All"; selectedLevel = "All"; searchQuery = ""; showMature = false
                    }
                    QuickNovelAction("✏️", "Write Yours", Violet, Modifier.weight(1f), onClick = onWriteClick)
                }
            }
            items(filteredNovels(novels, selectedGenre, selectedLevel, searchQuery, showMature)) { novel ->
                NovelCard(novel) { onNovelClick(novel) }
            }
        }
    }
}

private fun filteredNovels(novels: List<Novel>, genre: String, level: String, query: String, showMature: Boolean): List<Novel> {
    return novels.filter {
        (genre == "All" || it.genre == genre) &&
        (level == "All" || it.level == level) &&
        (showMature || !it.mature) &&
        (query.isBlank() || it.title.lowercase().contains(query.lowercase()) || it.genre.lowercase().contains(query.lowercase()))
    }
}

@Composable
private fun NovelCard(novel: Novel, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(Rad.md).background(
                    when {
                        novel.mature -> CoralSoft
                        novel.funny -> GoldSoft
                        else -> CobaltSoft
                    }
                ), contentAlignment = Alignment.Center
            ) {
                Text(
                    when {
                        novel.mature -> "🔞"
                        novel.funny -> "😂"
                        else -> "📖"
                    }, fontSize = 22.sp
                )
            }
            Spacer(Modifier.padding(Sp.sm))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(novel.title, style = T.bodySemi, color = Ink)
                    if (novel.mature) Text(" 🔞", fontSize = 12.sp)
                }
                Text("${novel.genre} · ${novel.level}", style = T.caption, color = InkSoft)
                Text("${novel.author}", style = T.caption, color = InkMuted)
            }
            Text("→", style = T.section, color = InkMuted)
        }
    }
}

@Composable
private fun OutlinedSearchField(placeholder: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = Rad.xl,
        colors = TextFieldDefaults.textFieldColors(
            focusedBorderColor = Cobalt, unfocusedBorderColor = Border
        )
    )
}

@Composable
private fun GenreChip(selected: String, onClick: (String) -> Unit) {
    val genres = listOf("All") + GENRES
    val isSelected = selected == "All" || selected == "All"
    Row(
        Modifier.clip(Rad.pill).background(if (selected == "All" || selected == "All") Cobalt else Surface)
            .border(2.dp, if (selected == "All") Cobalt else Border, Rad.pill)
            .semantics(mergeDescendants = true) { contentDescription = selected; role = Role.Button }
            .clickable(onClick = { onClick("All") })
            .padding(horizontal = Sp.sm, vertical = Sp.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("All", color = if (selected == "All") White else InkSoft, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
    GENRES.forEach { g ->
        val sel = selected == g
        Row(
            Modifier.clip(Rad.pill).background(if (sel) Cobalt else Surface)
                .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                .semantics(mergeDescendants = true) { contentDescription = g; role = Role.Button }
                .clickable(onClick = { onClick(g) })
                .padding(horizontal = Sp.sm, vertical = Sp.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(g, color = if (sel) White else InkSoft, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LevelChip(selected: String, onClick: (String) -> Unit) {
    val levels = listOf("All") + LEVELS
    Row(
        Modifier.clip(Rad.pill).background(if (selected == "All") Cobalt else Surface)
            .border(2.dp, if (selected == "All") Cobalt else Border, Rad.pill)
            .semantics(mergeDescendants = true) { contentDescription = selected; role = Role.Button }
            .clickable(onClick = { onClick("All") })
            .padding(horizontal = Sp.sm, vertical = Sp.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("All", color = if (selected == "All") White else InkSoft, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
    LEVELS.forEach { l ->
        val sel = selected == l
        Row(
            Modifier.clip(Rad.pill).background(if (sel) Cobalt else Surface)
                .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                .semantics(mergeDescendants = true) { contentDescription = l; role = Role.Button }
                .clickable(onClick = { onClick(l) })
                .padding(horizontal = Sp.sm, vertical = Sp.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(l, color = if (sel) White else InkSoft, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun QuickNovelAction(icon: String, label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier.clip(Rad.xl).background(Surface).border(1.dp, Border, Rad.xl)
            .clickable(onClick = onClick).padding(Sp.md).semantics { contentDescription = label; role = Role.Button },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(44.dp).clip(Rad.md).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 22.sp)
        }
        Spacer(Modifier.padding(Sp.xs))
        Text(label, style = T.label, color = Ink, textAlign = TextAlign.Center)
    }
}

/* ═══════════ NOVEL READER (detail) ═══════════ */

@Composable
fun NovelReaderScreen(novel: Novel, onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    AppBackground(tint = Lavender) {
        Column(
            Modifier.fillMaxSize().verticalScroll(scrollState).padding(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                Text("📖 ${novel.title}", style = T.screenTitle, color = Ink, modifier = Modifier.weight(1f))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${novel.genre} · ${novel.level} · ${novel.author}", style = T.caption, color = InkMuted, modifier = Modifier.weight(1f))
                if (novel.mature) Text("🔞", fontSize = 18.sp)
                if (novel.funny) Text("😂", fontSize = 18.sp)
            }
            Text(novel.description, style = T.secondary, color = InkSoft)
            Spacer(Modifier.padding(Sp.xs))
            Divider()
            Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                SectionHeader("🇫🇷 Texte français")
                Text(novel.frText, style = T.body, color = Ink, modifier = Modifier.padding(Sp.sm))
            }
            Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                SectionHeader("🇬🇧 English translation")
                Text(novel.enText, style = T.body, color = InkSoft, modifier = Modifier.padding(Sp.sm))
            }
            Spacer(Modifier.padding(Sp.sm))
            BigButton("🖨️ Print / Share", onClick = {
                val shareText = "📖 ${novel.title}\n${novel.description}\n\n🇫🇷 ${novel.frText}\n🇬🇧 ${novel.enText}"
                val intent = android.content.Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(Intent.createChooser(intent, "Share ${novel.title}"))
            })
            if (novel.printable) {
                BigButton("📄 Download PDF", color = Turquoise, onClick = {
                    // Create a simple text file for printing
                    val file = File(context.filesDir, "${novel.id}.txt")
                    file.writeText("${novel.title}\n${novel.frText}\n\n${novel.enText}")
                })
            }
        }
    }
}

/* ═══════════ WRITING ASSISTANT ═══════════ */

@Composable
fun WritingAssistantScreen(onBack: () -> Unit, onSave: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("Drama") }
    var level by remember { mutableStateOf("A2") }
    var frenchText by remember { mutableStateOf("") }
    var englishText by remember { mutableStateOf("") }
    var suggestion by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AppBackground(tint = VioletSoft) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                Text("✏️ Write a Novel", style = T.screenTitle, color = Cobalt)
            }
            Text("Create your own novel with AI-powered suggestions. Fully offline.", style = T.secondary, color = InkSoft)

            Card {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    SectionHeader("📝 Write")
                    OutlinedSearchField("Novel title...", title, { title = it })
                    Text("Genre:", style = T.label, color = Ink)
                    Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs), modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                        GENRES.forEach { g ->
                            val sel = genre == g
                            Text(
                                g, style = T.label,
                                color = if (sel) White else InkSoft,
                                modifier = Modifier
                                    .clip(Rad.pill)
                                    .background(if (sel) Cobalt else Surface)
                                    .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                                    .clickable { genre = g }
                                    .padding(horizontal = Sp.sm, vertical = Sp.xs)
                            )
                        }
                    }
                    Text("Level:", style = T.label, color = Ink)
                    Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        LEVELS.forEach { l ->
                            val sel = level == l
                            Text(
                                l, style = T.label,
                                color = if (sel) White else InkSoft,
                                modifier = Modifier
                                    .clip(Rad.pill)
                                    .background(if (sel) Cobalt else Surface)
                                    .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                                    .clickable { level = l }
                                    .padding(horizontal = Sp.sm, vertical = Sp.xs)
                            )
                        }
                    }
                }
            }

            Card {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    SectionHeader("🇫🇷 French text (write your novel)")
                    TextField(
                        value = frenchText, onValueChange = { frenchText = it },
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        placeholder = { Text("Écris ton roman ici...") },
                        shape = Rad.xl,
                        colors = TextFieldDefaults.textFieldColors(focusedBorderColor = Cobalt),
                        singleLine = false
                    )
                    SectionHeader("🇬🇧 English translation")
                    TextField(
                        value = englishText, onValueChange = { englishText = it },
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        placeholder = { Text("Write the English translation...") },
                        shape = Rad.xl,
                        colors = TextFieldDefaults.textFieldColors(focusedBorderColor = Cobalt),
                        singleLine = false
                    )
                }
            }

            Card {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    SectionHeader("💡 AI Suggestions")
                    Text("Based on your genre ($genre) and level ($level):", style = T.caption, color = InkMuted)
                    Text(generateSuggestion(genre, level, frenchText), style = T.body, color = Ink)
                    Spacer(Modifier.padding(Sp.xs))
                    BigButton("💡 Get Suggestion", onClick = {
                        suggestion = generateSuggestion(genre, level, frenchText)
                    })
                    if (suggestion.isNotBlank()) {
                        Text(suggestion, style = T.bodySemi, color = Cobalt)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                BigButton("💾 Save Novel", modifier = Modifier.weight(1f), onClick = {
                    if (title.isNotBlank() && frenchText.isNotBlank()) {
                        onSave(title, frenchText, englishText)
                        saved = true
                    }
                })
                BigButton("✨ Generate", color = Violet, modifier = Modifier.weight(1f), onClick = {
                    if (title.isBlank()) title = "Mon Roman ${System.currentTimeMillis().toString().takeLast(4)}"
                    if (frenchText.isBlank()) frenchText = generateFrenchExcerpt(genre, level)
                })
            }
            if (saved) Text("✅ Novel saved!", style = T.label, color = Emerald)
        }
    }
}

private fun generateSuggestion(genre: String, level: String, text: String): String {
    val suggestions = mapOf(
        "Romance" to "Try adding dialogue between characters. Use 'Tu' for informal or 'Vous' for formal address. At $level, consider adding emotional descriptions.",
        "Mystery" to "Add a clue in the next chapter. Use 'Qui...?' and 'Où...?' questions to create suspense. At $level, introduce red herrings.",
        "Comedy" to "Add a misunderstanding scene. Use wordplay or unexpected twists. At $level, try a running gag.",
        "Sci-Fi" to "Introduce a futuristic element. Describe technology using 'comme' comparisons. At $level, add philosophical themes.",
        "Horror" to "Build tension with short sentences. Use 'soudain' and 'aucun bruit'. At $level, add unreliable narration.",
        "Adventure" to "Add a journey scene. Use directional vocabulary ('à gauche', 'tout droit'). At $level, include a moral lesson.",
        "Fantasy" to "Introduce a magical creature or spell. Use descriptive adjectives. At $level, create a prophecy.",
        "Drama" to "Add an emotional climax. Use 'parce que' and 'bien que' for complex emotions. At $level, add a twist ending.",
        "Slice-of-Life" to "Describe a mundane moment beautifully. Use sensory details. At $level, add inner monologue.",
        "Mature" to "Explore complex themes like identity or loss. Use subjunctive mood. At $level, add ambiguous endings.",
        "Funny" to "Add a character mistake or absurd situation. Use exaggerated descriptions. At $level, create verbal irony.",
        "Thriller" to "Add a countdown or deadline. Use short, punchy sentences. At $level, add a plot twist.",
        "Supernatural" to "Introduce an otherworldly element. Use mysterious descriptions. At $level, blur reality and fantasy.",
        "Coming-of-Age" to "Add a moment of self-discovery. Use 'je réalise que...'. At $level, explore identity conflicts.",
        "Historical" to "Add a historical detail or setting description. Use past tense ('passé composé'). At $level, add real events."
    ).getOrDefault(genre, "Try adding more dialogue and description to your novel.")
    return suggestions
}

private fun generateFrenchExcerpt(genre: String, level: String): String {
    val excerpt = when (genre.lowercase()) {
        "romance" -> "La première fois que je l'ai vue, le monde s'est arrêté. Ses yeux brillaient comme deux étoiles perdues dans la nuit parisienne."
        "mystery" -> "La lettre était posée sur la table. Personne ne savait qui l'avait écrite. Le parfum sur le papier était familier."
        "comedy" -> "Le chat était assis sur le clavier. Il tapait des mots au hasard. Quand j'ai regardé l'écran, le message dit : 'Je t'aime'."
        "adventure" -> "Le bateau voguait sur l'océan infini. L'horizon était une ligne brisée entre le ciel et la mer. Personne ne savait ce qui nous attendait."
        "fantasy" -> "La porte lumineuse s'ouvrit sur un monde inconnu. Des créations d'argent et d'or nous attendaient au-delà du voile."
        else -> "Le soleil se couchait sur la ville. Les rues se vidaient doucement. Un silence paisible envahit les trottoirs déserts."
    }
    return excerpt
}

/* ═══════════ SONG PLAYER ═══════════ */

@Composable
fun SongTab(
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onBack: () -> Unit
) {
    var selectedArtist by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val artists = listOf("All") + songs.map { it.artist }.distinct()
    val scrollState = rememberScrollState()

    AppBackground(tint = GoldSoft) {
        LazyColumn(
            Modifier.fillMaxSize().verticalScroll(scrollState),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                    Text("🎵 French Songs", style = T.screenTitle, color = Ink)
                }
                Text("Offline French songs · Stromae, Indila, and more", style = T.secondary, color = InkSoft)
            }
            item {
                OutlinedSearchField("Search songs...", searchQuery) { searchQuery = it }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs), modifier = Modifier.verticalScroll(rememberScrollState())) {
                    artists.forEach { a ->
                        val sel = selectedArtist == a
                        Text(
                            a, style = T.label,
                            color = if (sel) White else InkSoft,
                            modifier = Modifier
                                .clip(Rad.pill)
                                .background(if (sel) Cobalt else Surface)
                                .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                                .clickable { selectedArtist = a }
                                .padding(horizontal = Sp.sm, vertical = Sp.xs)
                        )
                    }
                }
            }
            item {
                Text("${songs.filter { selectedArtist == "All" || it.artist == selectedArtist }.filter { searchQuery.isBlank() || it.title.lowercase().contains(searchQuery.lowercase()) || it.artist.lowercase().contains(searchQuery.lowercase()) }.size} songs found", style = T.caption, color = InkMuted)
            }
            items(songs.filter { selectedArtist == "All" || it.artist == selectedArtist }.filter { searchQuery.isBlank() || it.title.lowercase().contains(searchQuery.lowercase()) || it.artist.lowercase().contains(searchQuery.lowercase()) }) { song ->
                SongCard(song) { onSongClick(song) }
            }
        }
    }
}

@Composable
private fun SongCard(song: Song, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(Rad.md).background(GoldSoft), contentAlignment = Alignment.Center
            ) { Text("🎶", fontSize = 24.sp) }
            Spacer(Modifier.padding(Sp.sm))
            Column(Modifier.weight(1f)) {
                Text(song.title, style = T.bodySemi, color = Ink)
                Text("${song.artist} · ${song.genre} · ${song.year}", style = T.caption, color = InkSoft)
            }
            Text("▶", style = T.section, color = InkMuted)
        }
    }
}

@Composable
fun SongDetailScreen(song: Song, onBack: () -> Unit) {
    val scrollState = rememberScrollState()
    AppBackground(tint = Lavender) {
        Column(
            Modifier.fillMaxSize().verticalScroll(scrollState).padding(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                Text("🎵 ${song.title}", style = T.screenTitle, color = Ink, modifier = Modifier.weight(1f))
            }
            Text("${song.artist} · ${song.genre} · ${song.year}", style = T.caption, color = InkMuted)
            Spacer(Modifier.padding(Sp.xs))
            Divider()
            Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                SectionHeader("🇫🇷 Paroles originales")
                Text(song.frLyrics, style = T.body, color = Ink, modifier = Modifier.padding(Sp.sm))
            }
            Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                SectionHeader("🇬🇧 English translation")
                Text(song.enTranslation, style = T.body, color = InkSoft, modifier = Modifier.padding(Sp.sm))
            }
            Spacer(Modifier.padding(Sp.sm))
            BigButton("🖨️ Print Lyrics", onClick = {
                val context = LocalContext.current
                val shareText = "🎵 ${song.title} - ${song.artist}\n${song.year}\n\n🇫🇷 ${song.frLyrics}\n\n🇬🇧 ${song.enTranslation}"
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(android.content.Intent.createChooser(intent, "Share ${song.title}"))
            })
            BigButton("💾 Download Lyrics", color = Turquoise, onClick = {
                val file = java.io.File(LocalContext.current.filesDir, "${song.id}.txt")
                file.writeText("${song.title}\n${song.frLyrics}\n\n${song.enTranslation}")
            })
        }
    }
}

fun parseLyrics(text: String): List<String> {
    return text.lines().filter { it.isNotBlank() && !it.startsWith("[") && !it.startsWith("(") }
}
