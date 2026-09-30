package com.francofun.content

import com.francofun.*
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

/* ═══════════ POEM LIST ═══════════ */

@Composable
fun PoemTab(
    poems: List<Poem>,
    onPoemClick: (Poem) -> Unit,
    onBack: () -> Unit,
    lang: HelpLang = HelpLang.ENGLISH,
    onWrite: () -> Unit = {}
) {
    var selectedPoet by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val poets = remember(poems) { listOf("All") + poems.map { it.poet }.distinct().sorted() }
    val shown = remember(poems, selectedPoet, searchQuery) {
        val q = searchQuery.trim().lowercase()
        poems.filter { (selectedPoet == "All" || it.poet == selectedPoet) &&
            (q.isBlank() || it.title.lowercase().contains(q) || it.poet.lowercase().contains(q) ||
                it.topic.lowercase().contains(q)) }
    }

    AppBackground(tint = Lavender) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back to Enjoy"; role = Role.Button })
                    Text("📜 Poèmes", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                Text(
                    lang.t(
                        "${poems.size} poems · full text offline · Rimbaud, Verlaine, Hugo, Baudelaire… + yours",
                        "Mashairi ${poems.size} · maandishi yote · Rimbaud, Verlaine, Hugo, Baudelaire… + yako",
                        "Poems ${poems.size} · full text offline · Rimbaud, Verlaine, Hugo, Baudelaire… + zako"
                    ),
                    style = T.secondary, color = InkSoft
                )
            }
            item {
                BigButton(
                    lang.t("✏️ Write yours", "✏️ Andika lako", "✏️ Andika lako"),
                    onClick = onWrite, color = Violet
                )
            }
            item {
                OutlinedSearchField(
                    lang.t("Search title, poet, topic...", "Tafuta kichwa, mshairi...", "Search hapa..."),
                    searchQuery
                ) { searchQuery = it }
            }
            item {
                Text(lang.t("Poet:", "Mshairi:", "Poet:"), style = T.label, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    poets.forEach { p ->
                        val sel = selectedPoet == p
                        Text(
                            p.substringBefore(" (").take(24), style = T.label,
                            color = if (sel) White else InkSoft,
                            modifier = Modifier
                                .clip(Rad.pill)
                                .background(if (sel) Cobalt else Surface)
                                .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                                .clickable { selectedPoet = p }
                                .padding(horizontal = Sp.sm, vertical = Sp.xs)
                                .semantics { contentDescription = "Filter by poet: $p" }
                        )
                    }
                }
            }
            item {
                Text(
                    lang.t(
                        "${shown.size} / ${poems.size} poems shown",
                        "${shown.size} / ${poems.size} yameonyeshwa",
                        "${shown.size} / ${poems.size} zinaonyeshwa"
                    ),
                    style = T.caption, color = InkMuted
                )
            }
            if (shown.isEmpty()) {
                item {
                    Text(
                        lang.t(
                            "No poems match — try another poet or word 📜",
                            "Hakuna mashairi — jaribu mshairi au neno lingine 📜",
                            "Hakuna poem — jaribu poet ama word ingine 📜"
                        ),
                        style = T.secondary, color = InkSoft
                    )
                }
            }
            items(shown, key = { it.id }) { poem ->
                Card(
                    modifier = Modifier.clickable(onClick = { onPoemClick(poem) })
                        .semantics {
                            contentDescription = "${poem.title} by ${poem.poet}, ${poem.topic}, level ${poem.level}"
                            role = Role.Button
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(44.dp).clip(Rad.md).background(VioletSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("📜", fontSize = 22.sp) }
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text(poem.title, style = T.bodySemi, color = Ink)
                            Text("${poem.poet} · ${poem.topic} · ${poem.level}", style = T.caption, color = InkSoft)
                        }
                        Text("→", style = T.section, color = InkMuted)
                    }
                }
            }
        }
    }
}

/* ═══════════ POEM READER ═══════════ */

@Composable
fun PoemDetailScreen(
    poem: Poem,
    speaker: Speaker,
    onBack: () -> Unit,
    lang: HelpLang = HelpLang.ENGLISH,
    onPoemClick: (Poem) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var readingAloud by remember(poem.id) { mutableStateOf(false) }
    androidx.compose.runtime.DisposableEffect(poem.id) {
        onDispose { speaker.stop() }
    }
    // Parchment page, serif italic verse — same reader soul as the novels.
    Box(Modifier.fillMaxSize().background(Papayawhip)) {
        ParchmentTexture()
        Column(
            Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = Sp.lg, vertical = Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("←", style = T.section, color = ScrollInk, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                Text(
                    "📜 ${poem.title}",
                    style = T.screenTitle.copy(fontFamily = FontFamily.Serif),
                    color = ScrollInk, modifier = Modifier.weight(1f)
                )
            }
            Text(
                "${poem.poet} · ${poem.topic} · ${poem.level} · ${poem.year}",
                style = T.caption.copy(fontFamily = FontFamily.Serif),
                color = ParchmentEdge
            )
            Text(
                poem.source,
                style = T.caption.copy(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic),
                color = ParchmentEdge
            )
            Text(
                if (readingAloud) lang.t("⏹ Stop", "⏹ Acha", "⏹ Stop")
                else lang.t("🔊 Listen to the poem", "🔊 Sikiliza shairi", "🔊 Sikiza poem"),
                style = T.label.copy(fontFamily = FontFamily.Serif), color = ScrollInk,
                modifier = Modifier.clickable {
                    if (readingAloud) {
                        speaker.stop()
                        readingAloud = false
                    } else {
                        readingAloud = true
                        speaker.speak(
                            poem.frText.take(1200),
                            onDone = { readingAloud = false },
                            emotion = Speaker.VoiceEmotion.DRAMATIC
                        )
                    }
                }.padding(vertical = Sp.xs)
                    .semantics {
                        contentDescription = if (readingAloud) "Stop reading aloud" else "Listen to the poem"
                        role = Role.Button
                    }
            )
            Text("❦ ❧ ❦", style = T.section, color = ParchmentEdge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            poem.frText.split(Regex("\\n\\s*\\n")).map { it.trim() }.filter { it.isNotEmpty() }.forEach { stanza ->
                Text(
                    stanza,
                    style = T.secondary.copy(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, lineHeight = 22.sp)
                )
            }
            Text("❦ ❧ ❦", style = T.section, color = ParchmentEdge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(
                if (lang == HelpLang.ENGLISH) "📖 About this poem" else "📖 Kuhusu shairi",
                style = T.section.copy(fontFamily = FontFamily.Serif), color = ScrollInk
            )
            Text(
                poem.enText,
                style = T.body.copy(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, lineHeight = 25.sp),
                color = ParchmentEdge
            )
            Spacer(Modifier.padding(Sp.sm))
            BigButton(lang.t("🖨️ Share poem", "🖨️ Shiriki", "🖨️ Share"), onClick = {
                val shareText = "📜 ${poem.title} — ${poem.poet}\n\n${poem.frText}\n\n${poem.enText}"
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(android.content.Intent.createChooser(intent, "Share ${poem.title}"))
            })
            val related = remember(poem.id) {
                allPoems(context).filter { it.id != poem.id &&
                    (it.poet == poem.poet || it.topic == poem.topic || it.level == poem.level)
                }.sortedWith(
                    compareByDescending<Poem> { it.poet == poem.poet }
                        .thenByDescending { it.topic == poem.topic }
                ).take(3)
            }
            if (related.isNotEmpty()) {
                SectionHeader(lang.t("📚 Read next", "📚 Soma pia", "📚 Soma next"))
                related.forEach { r ->
                    Card(
                        modifier = Modifier.clickable(onClick = { onPoemClick(r) })
                            .semantics {
                                contentDescription = "Read ${r.title} by ${r.poet}"
                                role = Role.Button
                            }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📜", fontSize = 20.sp)
                            Spacer(Modifier.padding(Sp.sm))
                            Column(Modifier.weight(1f)) {
                                Text(r.title, style = T.bodySemi, color = Ink)
                                Text("${r.poet} · ${r.topic} · ${r.level}", style = T.caption, color = InkSoft)
                            }
                            Text("→", style = T.section, color = InkMuted)
                        }
                    }
                }
            }
        }
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = 4.dp, top = 96.dp, bottom = 96.dp)
        ) {
            SideScrollbar(scrollState, ParchmentEdge)
        }
    }
}

/* ═══════════ POEM WRITER ═══════════ */

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WritePoemScreen(
    onBack: () -> Unit,
    onSave: (title: String, topic: String, level: String, fr: String, en: String) -> Unit,
    lang: HelpLang = HelpLang.ENGLISH
) {
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("A2") }
    var frText by remember { mutableStateOf("") }
    var enText by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    AppBackground(tint = Lavender) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("←", style = T.section, color = InkMuted,
                    modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm)
                        .semantics { contentDescription = "Back"; role = Role.Button })
                Text(lang.t("✏️ Write a Poem", "✏️ Andika Shairi", "✏️ Andika Poem"), style = T.screenTitle, color = Ink)
            }
            Card {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    OutlinedSearchField(
                        lang.t("Poem title...", "Kichwa...", "Title..."), title
                    ) { title = it }
                    OutlinedSearchField(
                        lang.t("Topic (Amour, Nature...)", "Mada (Mapenzi...)", "Topic..."), topic
                    ) { topic = it }
                    Text(lang.t("Level:", "Kiwango:", "Level:"), style = T.label, color = Ink)
                    Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        listOf("A1", "A2", "B1", "B2").forEach { l ->
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
                                    .semantics { contentDescription = "Level $l"; role = Role.Button }
                            )
                        }
                    }
                }
            }
            Card {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    SectionHeader(lang.t("🇫🇷 French verses", "🇫🇷 Beti za Kifaransa", "🇫🇷 French lines"))
                    TextField(
                        value = frText, onValueChange = { frText = it; saved = false },
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        placeholder = { Text("Écris ton poème ici...") },
                        shape = Rad.xl,
                        colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Cobalt),
                        singleLine = false
                    )
                    SectionHeader(lang.t("🇬🇧 English meaning", "🇬🇧 Maana", "🇬🇧 Meaning"))
                    TextField(
                        value = enText, onValueChange = { enText = it; saved = false },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        placeholder = { Text("What does it mean...") },
                        shape = Rad.xl,
                        colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Cobalt),
                        singleLine = false
                    )
                }
            }
            BigButton(
                lang.t("💾 Save Poem", "💾 Hifadhi Shairi", "💾 Save Poem"),
                onClick = {
                    if (frText.isNotBlank()) {
                        onSave(title, topic, level, frText, enText)
                        saved = true
                    }
                }
            )
            if (saved) Text(
                lang.t("✅ Poem saved — find it at the top of your poems!", "✅ Shairi limehifadhiwa!", "✅ Poem imesave!"),
                style = T.label, color = Emerald
            )
        }
    }
}
