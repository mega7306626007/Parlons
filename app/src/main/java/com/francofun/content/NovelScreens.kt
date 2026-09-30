package com.francofun.content

import com.francofun.*
import com.francofun.content.*
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

private val GENRES = NOVEL_GENRES
private val LEVELS = NOVEL_LEVELS

/* ═══════════ NOVEL READER ═══════════ */

@Composable
fun NovelTab(
    novels: List<Novel>,
    onNovelClick: (Novel) -> Unit,
    onWriteClick: () -> Unit,
    onBack: () -> Unit,
    lang: HelpLang = HelpLang.ENGLISH,
    loading: Boolean = false
) {
    var selectedGenre by remember { mutableStateOf("All") }
    var selectedLevel by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showMature by remember { mutableStateOf(false) }
    val allNovels = remember(novels) { novels }
    val shownNovels = remember(allNovels, selectedGenre, selectedLevel, searchQuery, showMature) {
        filteredNovels(allNovels, selectedGenre, selectedLevel, searchQuery, showMature)
    }

    AppBackground(tint = CobaltSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back to Enjoy"; role = Role.Button })
                    Text("📚 Romans & Histoires", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                Text(
                    lang.t(
                        "${allNovels.size} stories · ${allNovels.count { it.pageCount <= 200 }} short (30–200 p.) · " +
                            "${allNovels.count { it.pageCount >= 300 }} long (300–1000 p.)",
                        "Hadithi ${allNovels.size} · ${allNovels.count { it.pageCount <= 200 }} fupi (kurasa 30–200) · " +
                            "${allNovels.count { it.pageCount >= 300 }} ndefu (kurasa 300–1000)",
                        "Stories ${allNovels.size} · ${allNovels.count { it.pageCount <= 200 }} short (pages 30–200) · " +
                            "${allNovels.count { it.pageCount >= 300 }} long (pages 300–1000)"
                    ),
                    style = T.secondary, color = InkSoft
                )
            }
            item {
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                        SectionHeader(lang.t("🔍 Filter & Search", "🔍 Chuja & Tafuta", "🔍 Filter & Search"))
                        OutlinedSearchField(
                            lang.t("Search novels...", "Tafuta hadithi...", "Search stories..."),
                            searchQuery
                        ) { searchQuery = it }
                        Text(lang.t("Genre:", "Aina:", "Genre:"), style = T.label, color = Ink)
                        Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            GENRES.forEach { g ->
                                val sel = selectedGenre == g
                                Text(
                                    g, style = T.label,
                                    color = if (sel) White else InkSoft,
                                    modifier = Modifier
                                        .clip(Rad.pill)
                                        .background(if (sel) Cobalt else Surface)
                                        .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                                        .semantics { contentDescription = "Filter by genre $g"; role = Role.Button }
                                        .clickable { selectedGenre = g }
                                        .padding(horizontal = Sp.sm, vertical = Sp.xs)
                                )
                            }
                        }
                        Text(lang.t("Level:", "Kiwango:", "Level:"), style = T.label, color = Ink)
                        Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            LEVELS.forEach { l ->
                                val sel = selectedLevel == l
                                Text(
                                    l, style = T.label,
                                    color = if (sel) White else InkSoft,
                                    modifier = Modifier
                                        .clip(Rad.pill)
                                        .background(if (sel) Cobalt else Surface)
                                        .border(2.dp, if (sel) Cobalt else Border, Rad.pill)
                                        .semantics { contentDescription = "Filter by level $l"; role = Role.Button }
                                        .clickable { selectedLevel = l }
                                        .padding(horizontal = Sp.sm, vertical = Sp.xs)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                lang.t("🔞 Mature", "🔞 Za watu wazima", "🔞 Mature"),
                                style = T.label, color = Ink, modifier = Modifier.weight(1f)
                            )
                            Box(
                                Modifier.size(40.dp).clip(Rad.md).background(if (showMature) Cobalt else Border).clickable { showMature = !showMature },
                                contentAlignment = Alignment.Center
                            ) { Text(if (showMature) "✓" else "○", color = if (showMature) White else Ink, fontSize = 16.sp) }
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                    QuickNovelAction("📖", lang.t("All Novels", "Hadithi zote", "Stories zote") + " (${allNovels.size})", Cobalt, Modifier.weight(1f)) {
                        selectedGenre = "All"; selectedLevel = "All"; searchQuery = ""; showMature = false
                    }
                    QuickNovelAction("✏️", lang.t("Write Yours", "Andika yako", "Andika yako"), Violet, Modifier.weight(1f), onClick = onWriteClick)
                }
            }
            item {
                Text(
                    lang.t(
                        "${shownNovels.size} / ${allNovels.size} stories shown",
                        "${shownNovels.size} / ${allNovels.size} zimeonyeshwa",
                        "${shownNovels.size} / ${allNovels.size} zinaonyeshwa"
                    ),
                    style = T.caption, color = InkMuted,
                    modifier = Modifier.semantics { contentDescription = "${shownNovels.size} stories shown out of ${allNovels.size}" }
                )
            }
            items(shownNovels) { novel ->
                NovelCard(novel) { onNovelClick(novel) }
            }
            if (shownNovels.isEmpty()) {
                item {
                    if (loading) LoadingRow(
                        lang.t(
                            "Loading 1000 stories…",
                            "Inapakia hadithi 1000…",
                            "Inaload stories 1000…"
                        )
                    )
                    else Text(
                        lang.t(
                            "No stories match — loosen a filter 🔍",
                            "Hakuna hadithi — legeza kichuja 🔍",
                            "Hakuna story — loosen filter 🔍"
                        ),
                        style = T.secondary, color = InkSoft
                    )
                }
            }
        }
    }
}

private fun filteredNovels(novels: List<Novel>, genre: String, level: String, query: String, showMature: Boolean): List<Novel> {
    val q = query.trim().lowercase()
    return novels.filter {
        (genre == "All" || it.genre == genre) &&
        (level == "All" || it.level == level) &&
        (showMature || !it.mature) &&
        (q.isBlank() || it.title.lowercase().contains(q) || it.author.lowercase().contains(q) ||
            it.genre.lowercase().contains(q) || it.description.lowercase().contains(q))
    }
}

@Composable
private fun NovelCard(novel: Novel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick)
            .semantics { contentDescription = "${novel.title} by ${novel.author}, ${novel.genre}, level ${novel.level}, ${novel.pageCount} pages"; role = Role.Button }
    ) {
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
                Text("${novel.genre} · ${novel.level} · ${novel.pageCount}p", style = T.caption, color = InkSoft)
                Text("${novel.author}", style = T.caption, color = InkMuted)
            }
            Text("→", style = T.section, color = InkMuted)
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OutlinedSearchField(placeholder: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value, onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = Rad.xl,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Cobalt, unfocusedIndicatorColor = Border
        )
    )
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

/* ═══════════ ENJOY HUB ═══════════ */

@Composable
fun EnjoyTab(
    novelCount: Int,
    songCount: Int,
    poemCount: Int,
    onNovels: () -> Unit,
    onSongs: () -> Unit,
    onPoems: () -> Unit = {},
    onDownloads: () -> Unit = {},
    lang: HelpLang = HelpLang.ENGLISH,
    novelsLoading: Boolean = false
) {
    AppBackground(tint = VioletSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            item {
                Text("🎧 Enjoy", style = T.screenTitle, color = Ink,
                    modifier = Modifier.semantics { heading() })
                Text(
                    lang.t(
                        "Stories and music in French — tap a world to enter",
                        "Hadithi na muziki kwa Kifaransa — gusa dunia uingie",
                        "Stories na music kwa French — gusa world uingie"
                    ),
                    style = T.secondary, color = InkSoft
                )
            }
            item {
                Card(
                    modifier = Modifier.clickable(onClick = onNovels)
                        .semantics {
                            contentDescription = if (novelsLoading && novelCount == 0) "Open novels: loading stories"
                            else "Open novels: $novelCount stories"; role = Role.Button
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(56.dp).clip(Rad.md).background(EmeraldSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("📕", fontSize = 28.sp) }
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("Romans & Histoires", style = T.section, color = Ink)
                            if (novelsLoading && novelCount == 0) LoadingRow(
                                lang.t(
                                    "Loading stories…",
                                    "Inapakia hadithi…",
                                    "Inaload stories…"
                                )
                            )
                            else Text(
                                lang.t(
                                    "$novelCount stories · short and long · Hugo, Verne, Dumas…",
                                    "Hadithi $novelCount · fupi na ndefu · Hugo, Verne, Dumas…",
                                    "Stories $novelCount · short na long · Hugo, Verne, Dumas…"
                                ),
                                style = T.caption, color = InkSoft
                            )
                        }
                        Text("→", style = T.section, color = InkMuted)
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.clickable(onClick = onSongs)
                        .semantics { contentDescription = "Open songs: $songCount study songs"; role = Role.Button }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(56.dp).clip(Rad.md).background(GoldSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("🎵", fontSize = 28.sp) }
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("Chansons françaises", style = T.section, color = Ink)
                            Text(
                                lang.t(
                                    "$songCount song cards · Stromae, Piaf, Brel, Brassens… · listening included",
                                    "Nyimbo $songCount · Stromae, Piaf, Brel, Brassens… · kusikiliza kumejumuishwa",
                                    "Songs $songCount · Stromae, Piaf, Brel, Brassens… · listening iko ndani"
                                ),
                                style = T.caption, color = InkSoft
                            )
                        }
                        Text("→", style = T.section, color = InkMuted)
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.clickable(onClick = onPoems)
                        .semantics { contentDescription = "Open poems: $poemCount real poems"; role = Role.Button }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(56.dp).clip(Rad.md).background(VioletSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("📜", fontSize = 28.sp) }
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("Poèmes", style = T.section, color = Ink)
                            Text(
                                lang.t(
                                    "$poemCount real poems · full text · Rimbaud, Verlaine, Hugo…",
                                    "Mashairi $poemCount · maandishi yote · Rimbaud, Verlaine, Hugo…",
                                    "Poems $poemCount · full text · Rimbaud, Verlaine, Hugo…"
                                ),
                                style = T.caption, color = InkSoft
                            )
                        }
                        Text("→", style = T.section, color = InkMuted)
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.clickable(onClick = onDownloads)
                        .semantics { contentDescription = "Open download checklist for songs"; role = Role.Button }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(56.dp).clip(Rad.md).background(CobaltSoft),
                            contentAlignment = Alignment.Center
                        ) { Text("⬇️", fontSize = 28.sp) }
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("Liste de téléchargement", style = T.section, color = Ink)
                            Text(
                                lang.t(
                                    "which MP3s (or M4A, OGG…) to download and where to put them",
                                    "MP3 zipi (au M4A, OGG…) kupakua na kuziweka wapi",
                                    "which MP3s kupakua na kuziweka wapi"
                                ),
                                style = T.caption, color = InkSoft
                            )
                        }
                        Text("→", style = T.section, color = InkMuted)
                    }
                }
            }
            item {
                Text(
                    "Tip: every novel and song teaches vocabulary — tap 🔊 anywhere to hear it.",
                    style = T.caption, color = InkMuted
                )
            }
        }
    }
}

/* ═══════════ PARCHMENT READER THEME ═══════════ */

/** Parchment goes candlelit at night instead of glaring papayawhip. */
val Papayawhip: Color
    get() = if (ParlonsDark.isDark) Color(0xFF241A0E) else Color(0xFFFFEFD5)
val ParchmentEdge: Color
    get() = if (ParlonsDark.isDark) Color(0xFFC89B5E) else Color(0xFF8A5A2A)
val ScrollInk: Color
    get() = if (ParlonsDark.isDark) Color(0xFFF2E4C4) else Color(0xFF3E2A14)

/** Old-paper texture: brown speckles + darkened edges, drawn under the text. */
@Composable
fun ParchmentTexture(modifier: Modifier = Modifier) {
    val speckles = remember {
        val rnd = kotlin.random.Random(42)
        List(140) {
            Triple(rnd.nextFloat(), rnd.nextFloat(), 0.04f + rnd.nextFloat() * 0.09f)
        }
    }
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        speckles.forEach { (fx, fy, alpha) ->
            drawCircle(
                ParchmentEdge.copy(alpha = alpha),
                radius = (1f + (fx * 3f)),
                center = Offset(fx * w, fy * h)
            )
        }
        // Burnt edges — top/bottom/left/right vignette bands.
        drawRect(ParchmentEdge.copy(alpha = 0.28f), topLeft = Offset.Zero, size = Size(w, h * 0.02f))
        drawRect(ParchmentEdge.copy(alpha = 0.28f), topLeft = Offset(0f, h * 0.98f), size = Size(w, h * 0.02f))
        drawRect(ParchmentEdge.copy(alpha = 0.20f), topLeft = Offset.Zero, size = Size(w * 0.025f, h))
        drawRect(ParchmentEdge.copy(alpha = 0.20f), topLeft = Offset(w * 0.975f, 0f), size = Size(w * 0.025f, h))
    }
}

/** Scroll-style drop cap: oversized serif first letter, readable body. */
private fun dropCapText(text: String): AnnotatedString {
    if (text.isEmpty()) return AnnotatedString("")
    return buildAnnotatedString {
        withStyle(SpanStyle(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ScrollInk)) {
            append(text.take(1))
        }
        withStyle(SpanStyle(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 14.sp, color = ScrollInk)) {
            append(text.drop(1))
        }
    }
}

/** Slim side scrollbar that mirrors a vertical ScrollState — drag-free position cue. */
@Composable
fun SideScrollbar(scroll: androidx.compose.foundation.ScrollState, color: Color) {
    BoxWithConstraints(Modifier.fillMaxHeight().width(8.dp)) {
        val viewportPx = constraints.maxHeight.toFloat()
        val totalPx = (viewportPx + scroll.maxValue).coerceAtLeast(1f)
        val thumbPx = (viewportPx * viewportPx / totalPx).coerceAtLeast(48f)
        val travel = (viewportPx - thumbPx).coerceAtLeast(0f)
        val frac = if (scroll.maxValue > 0) scroll.value / scroll.maxValue.toFloat() else 0f
        Box(Modifier.fillMaxSize().background(Border.copy(alpha = 0.5f), Rad.pill))
        Box(
            Modifier.offset { IntOffset(0, (frac * travel).toInt()) }
                .width(8.dp)
                .height(with(LocalDensity.current) { thumbPx.toDp() })
                .background(color, Rad.pill)
        )
    }
}

private fun fmtMs(ms: Int): String {
    val s = (ms / 1000).coerceAtLeast(0)
    return "${s / 60}:${(s % 60).toString().padStart(2, '0')}"
}

/** App's music folder: reachable over USB / file manager, no permission needed. */
private fun musicDir(context: Context): File =
    context.getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC)
        ?: File(context.filesDir, "songs")

/** Every container Android itself can decode — no converter needed. */
private val AUDIO_EXTS = listOf("mp3", "m4a", "aac", "ogg", "opus", "wav", "flac", "mid", "amr")

/** Display name of a picked document, for smart importing. */
private fun queryDisplayName(context: Context, uri: android.net.Uri): String? {
    return runCatching {
        context.contentResolver.query(
            uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null
        )?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
    }.getOrNull()
}

/** Strip dangerous chars, keep the real name + extension. */
private fun cleanFileName(name: String): String {
    val base = name.substringBeforeLast(".").ifBlank { "audio" }
    val ext = name.substringAfterLast(".", "mp3").lowercase().take(4).ifBlank { "mp3" }
    val safe = base.replace(Regex("[\\\\/:*?\"<>|]"), " ").replace(Regex("\\s+"), " ").trim().take(120)
    return "$safe.$ext"
}

/** Lowercase, no accents, no punctuation — for matching real-world filenames. */
private fun normName(s: String): String =
    java.text.Normalizer.normalize(s.lowercase(), java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace(Regex("[^a-z0-9 ]"), " ")
        .replace(Regex("\\s+"), " ").trim()

/** One device track: raw names for display + normalized forms for matching.
 * [uri] is the MediaStore content URI — the scoped-storage-safe playback
 * handle (raw DATA paths fail on Android 10+). Null for app-folder files,
 * which always play by path. */
private data class DeviceTrack(
    val file: File,
    val rawTitle: String,
    val rawArtist: String,
    val normTitle: String,
    val normArtist: String,
    val normFile: String,
    // Filename segments ("01 - Artist - Title" → ["artist", "title"]),
    // precomputed once per scan so matching never re-splits.
    val segs: List<String>,
    val uri: android.net.Uri?
)

/**
 * ONE MediaStore pass for the whole library — never per-song. The checklist
 * calls this once and matches all 600 songs against the in-memory list.
 * Call off the main thread.
 */
private fun scanDeviceTracks(context: Context): List<DeviceTrack> {
    val out = mutableListOf<DeviceTrack>()
    val projection = arrayOf(
        android.provider.MediaStore.Audio.Media._ID,
        android.provider.MediaStore.Audio.Media.DATA,
        android.provider.MediaStore.Audio.Media.TITLE,
        android.provider.MediaStore.Audio.Media.ARTIST
    )
    val uri = android.net.Uri.parse("content://media/external/audio/media")
    val cursor = context.contentResolver.query(uri, projection, null, null, null) ?: return out
    cursor.use {
        if (!it.moveToFirst()) return out
        val idIdx = it.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
        val dataIdx = it.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
        val titleIdx = it.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
        val artistIdx = it.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
        val baseUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        do {
            val filePath = it.getString(dataIdx) ?: continue
            if (filePath.substringAfterLast('.', "").lowercase() !in AUDIO_EXTS) continue
            val rawTitle = it.getString(titleIdx) ?: continue
            val rawArtist = it.getString(artistIdx) ?: ""
            val file = java.io.File(filePath)
            if (!file.isFile) continue
            val trackUri = runCatching {
                android.content.ContentUris.withAppendedId(baseUri, it.getLong(idIdx))
            }.getOrNull()
            val nf = normName(file.nameWithoutExtension)
            out.add(
                DeviceTrack(
                    file = file,
                    rawTitle = rawTitle,
                    rawArtist = rawArtist,
                    normTitle = normName(rawTitle),
                    normArtist = normName(rawArtist),
                    normFile = nf,
                    segs = fileSegments(nf),
                    uri = trackUri
                )
            )
        } while (it.moveToNext())
    }
    return out
}

/** Split "01 - Artist - Title" style names into segments (track numbers stripped). */
private val FILE_SEP = Regex("\\s*[-–—_.|]+\\s*")
private val LEAD_NUM = Regex("^\\d{1,3}\\s*[-–—.]\\s*")

private fun fileSegments(normFile: String): List<String> =
    normFile.replace(LEAD_NUM, "").split(FILE_SEP).map { it.trim() }.filter { it.length > 2 }

/** Words that are almost never anything but French (post-normalization). */
private val FR_MARKERS = setOf(
    "les", "des", "une", "est", "avec", "sans", "dans", "pour", "amour",
    "toujours", "chanson", "coeur", "reve", "nuit", "vie", "merci", "voici",
    "voila", "quand", "comment", "pourquoi", "parce", "aussi", "tres",
    "tout", "meme", "etre", "francais", "france", "paris", "bonjour",
    "beaucoup", "jamais", "encore", "deja", "quelquun", "aujourdhui"
)

/** Common French words — only trusted together with an accent. */
private val FR_COMMON = setOf(
    "le", "la", "de", "du", "un", "et", "en", "que", "qui", "pas", "mon",
    "ma", "mes", "je", "tu", "il", "elle", "nous", "vous", "sur", "par",
    "au", "aux", "ce", "cette", "moi", "toi", "son", "ton", "non", "oui",
    "bon", "belle", "beau", "petit", "grand", "jour", "temps", "fleur",
    "soleil", "lune", "ciel", "mer", "chante", "danse", "histoire"
)

private val FR_ACCENT = Regex("[éèêëàâäîïôöùûüçœæ]")

/**
 * On-device French detector (no network, no models): ≥2 French-only
 * markers, or an accent plus a common French word. Heuristic — an odd
 * Spanish song with French words can slip in; the user just ignores it.
 */
private fun looksFrench(rawTitle: String, rawArtist: String, normFile: String): Boolean {
    // 1) Metadata path (existing, relaxed).
    val words = normName("$rawTitle $rawArtist").split(" ").filter { it.length > 2 }.toSet()
    if (words.count { it in FR_MARKERS } >= 1) return true
    if (FR_ACCENT.containsMatchIn("$rawTitle $rawArtist") && words.any { it in FR_COMMON }) return true
    if (FR_ACCENT.containsMatchIn("$rawTitle $rawArtist")) return true

    // 2) Filename path — many files have no tags but carry French names.
    val fnWords = normFile.split(" ").filter { it.length > 2 }.toSet()
    if (fnWords.count { it in FR_MARKERS } >= 1) return true
    if (FR_ACCENT.containsMatchIn(normFile)) return true

    return false
}

/**
 * Two-stage classifier: (1) regex filename parse — "Artist - Title" or
 * "Title - Artist" with full-phrase containment both ways; (2) scored fuzzy
 * fallback over metadata + filename where the best score wins
 * (metadata hits weigh double). Never first-row-wins.
 */
private fun matchTrack(song: Song, tracks: List<DeviceTrack>): DeviceTrack? {
    if (tracks.isEmpty()) return null
    val baseTitle = normName(song.title.substringBefore(" —"))
    val artist = normName(song.artist)
    val titleKeys = baseTitle.split(" ").filter { it.length > 2 }.toSet()
    if (titleKeys.isEmpty()) return null
    val artistKeys = artist.split(" ").filter { it.length > 2 }.toSet()
    // Stage 1: structured filename shapes ("Artist - Title" either order),
    // plus whole-filename containment anchored by an artist keyword —
    // the loose single-segment rule that caused false matches is gone.
    if (baseTitle.isNotEmpty() && artist.isNotEmpty()) {
        for (t in tracks) {
            val segs = t.segs
            if (segs.size >= 2) {
                val (a, b) = segs
                if ((b.contains(baseTitle) && a.contains(artist)) ||
                    (a.contains(baseTitle) && b.contains(artist))) return t
            }
            if (baseTitle.length >= 4 && t.normFile.contains(baseTitle) &&
                artistKeys.any { k -> t.normFile.contains(k) }) return t
        }
    }
    // Stage 2: scored fuzzy with two gates. Title gate: at least half the
    // title keywords (rounded up); short titles (≤2 keywords) must appear
    // as a full phrase — a lone "mer" must not claim "La Mer". Artist gate:
    // at least one artist keyword must hit somewhere. Best score wins.
    fun DeviceTrack.titleHits(): Int =
        titleKeys.count { k -> normTitle.contains(k) || normFile.contains(k) }
    fun DeviceTrack.artistHits(): Int =
        artistKeys.count { k ->
            normArtist.contains(k) || normFile.contains(k) ||
                rawArtist.lowercase().contains(k) || rawTitle.lowercase().contains(k)
        }
    fun DeviceTrack.score(): Int {
        var s = 0
        for (k in titleKeys) {
            if (normTitle.contains(k) || normFile.contains(k)) s += 2
            if (rawTitle.lowercase().contains(k) || rawArtist.lowercase().contains(k)) s += 1
        }
        for (k in artistKeys) {
            if (normArtist.contains(k) || normFile.contains(k)) s += 2
            if (rawTitle.lowercase().contains(k) || rawArtist.lowercase().contains(k)) s += 1
        }
        if (normFile.contains(baseTitle)) s += 3
        if (artist.isNotEmpty() && normFile.contains(artist)) s += 3
        return s
    }
    val needTitle = (titleKeys.size + 1) / 2
    // Exact filename ("formidable.mp3") bypasses the artist gate — the name
    // alone is conclusive. Everything else must show artist evidence.
    fun DeviceTrack.exactName(): Boolean = segs.any { it == baseTitle }
    return tracks.filter { t ->
        t.titleHits() >= needTitle &&
            (artistKeys.isEmpty() || t.artistHits() >= 1 || t.exactName()) &&
            (titleKeys.size > 2 || t.exactName() || t.normTitle.contains(baseTitle) || t.normFile.contains(baseTitle))
    }.maxByOrNull { it.score() }
}

/**
 * Pull recognized-but-outside files into the app's Music folder. Each UNIQUE
 * source is copied once as `<first-song-id>.<ext>` and shared by all its
 * editions. Returns (songId → app copy, failures). Runs on IO; [onOneCopied]
 * fires per distinct file copied (switch to Main inside if touching state).
 */
private suspend fun pullIntoApp(
    context: Context,
    external: List<SongFileStatus>,
    failedSrcs: MutableSet<String>,
    onOneCopied: suspend () -> Unit
): Pair<Map<String, File>, Int> = withContext(Dispatchers.IO) {
    val dir = musicDir(context).apply { mkdirs() }
    val shared = mutableMapOf<String, File>()
    val resolved = mutableMapOf<String, File>()
    var fails = 0
    external.filter { it.file != null && it.file.parent != dir.absolutePath }.forEach { st ->
        val src = st.file ?: return@forEach
        if (src.absolutePath in failedSrcs) return@forEach
        var dest = shared[src.absolutePath]
        if (dest == null) {
            dest = runCatching {
                val ext = src.extension.ifBlank { "mp3" }
                val d = File(dir, "${st.song.id}.$ext")
                src.inputStream().use { input ->
                    d.outputStream().use { input.copyTo(it) }
                }
                d
            }.getOrNull()
            if (dest == null) { fails++; failedSrcs.add(src.absolutePath) }
            else {
                shared[src.absolutePath] = dest
                onOneCopied()
            }
        }
        if (dest != null) resolved[st.song.id] = dest
    }
    resolved to fails
}

/** Tier 1 only: the app's private music folder (exact id, then fuzzy name). */
private fun findLocalAudio(context: Context, song: Song): File? {
    val dir = musicDir(context)
    if (!dir.isDirectory) return null
    AUDIO_EXTS.map { File(dir, "${song.id}.$it") }.firstOrNull { it.exists() }?.let { return it }
    val titleKeys = normName(song.title.substringBefore(" —")).split(" ").filter { it.length > 2 }
    if (titleKeys.isEmpty()) return null
    val artistKeys = normName(song.artist).split(" ").filter { it.length > 2 }
    val needArtist = (artistKeys.size + 1) / 2
    return dir.listFiles()?.firstOrNull { f ->
        f.isFile && f.extension.lowercase() in AUDIO_EXTS &&
            normName(f.nameWithoutExtension).let { n ->
                titleKeys.all { n.contains(it) } &&
                    (artistKeys.isEmpty() || artistKeys.count { n.contains(it) } >= needArtist)
            }
    }
}

/**
 * Smart audio lookup: exact `<id>.mp3` first, then fuzzy title + artist match
 * against the app's music folder, then one MediaStore pass over the device
 * library so songs already on the user's phone get recognized automatically.
 * Files like "Stromae - Formidable.mp3" just work.
 */
/** App-folder copy first (plays by path), else the matched device track
 * (plays by content URI — scoped-storage safe). Runs off-main-thread. */
private fun findSongAudioTrack(context: Context, song: Song): DeviceTrack? {
    val local = findLocalAudio(context, song)
    if (local != null) {
        val nf = normName(local.nameWithoutExtension)
        return DeviceTrack(
            file = local, rawTitle = song.title, rawArtist = song.artist,
            normTitle = normName(song.title), normArtist = normName(song.artist),
            normFile = nf, segs = fileSegments(nf), uri = null
        )
    }
    return matchTrack(song, scanDeviceTracks(context))
}

/** Small local-audio player (MP3/M4A/OGG/WAV). Load order: content URI
 * (scoped-storage safe, like Files by Google) → raw path → file descriptor.
 * Failures are SHOWN, never swallowed — a dead button is worse than an error. */
@Composable
private fun LocalAudioPlayer(
    label: String,
    file: File?,
    uri: android.net.Uri? = null,
    lang: HelpLang = HelpLang.ENGLISH
) {
    val context = LocalContext.current
    var playing by remember(label) { mutableStateOf(false) }
    var ready by remember(label) { mutableStateOf(false) }
    var pos by remember(label) { mutableStateOf(0) }
    var dur by remember(label) { mutableStateOf(1) }
    var error by remember(label) { mutableStateOf<String?>(null) }
    val player = remember(label) { MediaPlayer() }
    var fdHolder by remember(label) { mutableStateOf<java.io.FileInputStream?>(null) }
    fun closeFd() { fdHolder?.runCatching { close() }; fdHolder = null }
    DisposableEffect(label) {
        player.setOnCompletionListener { playing = false; pos = 0 }
        onDispose { runCatching { player.stop() }; player.release(); closeFd() }
    }
    LaunchedEffect(playing) {
        while (playing) {
            delay(500)
            pos = runCatching { player.currentPosition }.getOrDefault(pos)
            dur = runCatching { player.duration }.getOrDefault(dur).coerceAtLeast(1)
        }
    }
    fun loadTrack(): Boolean {
        error = null
        closeFd()
        if (uri != null && runCatching { player.setDataSource(context, uri) }.isSuccess) return true
        if (file != null) {
            if (runCatching { player.setDataSource(file.absolutePath) }.isSuccess) return true
            val fdOk = runCatching {
                fdHolder = java.io.FileInputStream(file)
                player.setDataSource(fdHolder!!.fd)
            }.isSuccess
            if (fdOk) return true
        }
        return false
    }
    fun playError(what: Int): String = lang.t(
        "Can't play this file (code $what). Re-import it from Téléchargements.",
        "Faili haichezi (kosa $what). Ilete tena kutoka Téléchargements.",
        "File haichezi (error $what). Iimport tena kutoka Téléchargements."
    )
    Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
        Text(
            "$label · ${fmtMs(pos)} / ${fmtMs(dur)}",
            style = T.label, color = Ink,
            modifier = Modifier.semantics {
                contentDescription = if (playing) "Playing $label" else "Paused $label"
            }
        )
        LinearProgressIndicator(
            progress = { (pos / dur.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(Rad.pill),
            color = Emerald
        )
        if (error != null) {
            Text(error!!, style = T.caption, color = Coral)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
            if (!playing) {
                BigButton("▶ Écouter", modifier = Modifier.weight(1f), onClick = {
                    runCatching {
                        if (!ready) {
                            player.reset()
                            player.setOnPreparedListener { ready = true; it.start(); playing = true }
                            player.setOnErrorListener { _, what, _ ->
                                error = playError(what)
                                playing = false
                                ready = false
                                true
                            }
                            if (loadTrack()) player.prepareAsync()
                            else error = playError(-1)
                        } else {
                            player.start()
                            playing = true
                        }
                    }.onFailure { error = playError(-2); ready = false }
                })
            } else {
                BigButton("⏸ Pause", color = Gold, modifier = Modifier.weight(1f), onClick = {
                    playing = false
                    runCatching { player.pause() }
                })
            }
            BigButton("⏹ Stop", color = InkSoft, modifier = Modifier.weight(1f), onClick = {
                playing = false
                runCatching { player.stop() }
                ready = false
                pos = 0
            })
            if (ready) {
                BigButton("⏪10", color = Turquoise, modifier = Modifier.weight(1f), onClick = {
                    runCatching {
                        val to = (player.currentPosition - 10_000).coerceAtLeast(0)
                        player.seekTo(to)
                        pos = to
                    }.onFailure { error = playError(-3) }
                })
                BigButton("10⏩", color = Turquoise, modifier = Modifier.weight(1f), onClick = {
                    runCatching {
                        val to = (player.currentPosition + 10_000).coerceAtMost(dur.coerceAtLeast(1))
                        player.seekTo(to)
                        pos = to
                    }.onFailure { error = playError(-3) }
                })
            }
        }
    }
}

/* ═══════════ NOVEL READER (detail) ═══════════ */

@Composable
fun NovelReaderScreen(
    novel: Novel,
    speaker: Speaker,
    store: Store,
    onBack: () -> Unit,
    lang: HelpLang = HelpLang.ENGLISH
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var fullText by remember(novel.id) { mutableStateOf<String?>(null) }
    var loadingFull by remember(novel.id) { mutableStateOf(false) }
    val repo = remember(context) { NovelRepository(context) }
    LaunchedEffect(novel.id) {
        loadingFull = true
        val t = withContext(Dispatchers.IO) { repo.getFullTextByNovelId(novel.id) }
        if (t.isNotBlank()) fullText = t
        loadingFull = false
    }
    val frToShow = fullText ?: novel.frText
    // Paginate into orderly pages (~1000 chars, never splitting mid-word).
    val pages = remember(frToShow) {
        val paras = frToShow.split(Regex("\\n\\s*\\n")).map { it.trim() }.filter { it.isNotEmpty() }
        val out = mutableListOf<String>()
        val cur = StringBuilder()
        fun flush() {
            if (cur.isNotBlank()) {
                out.add(cur.toString().trim())
                cur.clear()
            }
        }
        paras.forEach { p ->
            if (cur.length + p.length > 1000 && cur.isNotBlank()) flush()
            if (cur.isNotBlank()) cur.append("\n\n")
            cur.append(p)
        }
        flush()
        if (out.isEmpty()) listOf(frToShow) else out
    }
    var pageIdx by remember(novel.id, pages.size) {
        mutableStateOf(store.novelPage(novel.id).coerceIn(0, (pages.size - 1).coerceAtLeast(0)))
    }
    var showInfo by remember(novel.id) { mutableStateOf(false) }
    var showExtras by remember(novel.id) { mutableStateOf(false) }
    var readingAloud by remember(novel.id) { mutableStateOf(false) }
    // Leaving the reader always silences the voice — nothing drones on.
    DisposableEffect(novel.id) {
        onDispose { speaker.stop() }
    }
    LaunchedEffect(novel.id, pageIdx) { store.saveNovelPage(novel.id, pageIdx) }
    // Every page turn lands at the top — never mid-page.
    LaunchedEffect(pageIdx) { scrollState.scrollTo(0) }
    val page = pages[pageIdx.coerceIn(pages.indices)]
    Box(Modifier.fillMaxSize().background(Papayawhip)) {
        ParchmentTexture()
        Column(
            Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = Sp.lg, vertical = Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("←", style = T.section, color = ScrollInk, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                Text(
                    "📖 ${novel.title}",
                    style = T.screenTitle.copy(fontFamily = FontFamily.Serif),
                    color = ScrollInk, modifier = Modifier.weight(1f)
                )
            }
            Text(
                if (showInfo) lang.t("▲ Hide details", "▲ Ficha maelezo", "▲ Ficha details")
                else lang.t("ℹ️ Book details", "ℹ️ Maelezo ya kitabu", "ℹ️ Details za book"),
                style = T.label.copy(fontFamily = FontFamily.Serif), color = ParchmentEdge,
                modifier = Modifier.clickable { showInfo = !showInfo }.padding(vertical = Sp.xs)
                    .semantics { contentDescription = if (showInfo) "Hide book details" else "Show book details"; role = Role.Button }
            )
            if (showInfo) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${novel.genre} · ${novel.level} · ${novel.author} · ${novel.pageCount} pages",
                        style = T.caption.copy(fontFamily = FontFamily.Serif),
                        color = ParchmentEdge, modifier = Modifier.weight(1f)
                    )
                    if (novel.mature) Text("🔞", fontSize = 18.sp)
                    if (novel.funny) Text("😂", fontSize = 18.sp)
                }
                Text(
                    novel.description,
                    style = T.secondary.copy(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic),
                    color = ParchmentEdge
                )
            }
            if (loadingFull) LoadingRow("Chargement du texte intégral…", textColor = ParchmentEdge)
            if (pageIdx > 0) Text(
                "🔖 Reprise à la page ${pageIdx + 1}",
                style = T.caption.copy(fontFamily = FontFamily.Serif), color = Emerald
            )
            Spacer(Modifier.padding(Sp.xs))
            Divider()
            Text(
                "❦ ❧ ❦",
                style = T.section, color = ParchmentEdge, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.clip(Rad.pill).background(ParchmentEdge)
                            .padding(horizontal = Sp.md, vertical = Sp.xs)
                            .semantics { contentDescription = "Page ${pageIdx + 1} of ${pages.size}" }
                    ) {
                        Text(
                            "📄 Page ${pageIdx + 1} / ${pages.size}",
                            style = T.label.copy(fontFamily = FontFamily.Serif), color = Papayawhip
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        if (readingAloud) lang.t("⏹ Stop", "⏹ Acha", "⏹ Stop")
                        else lang.t("🔊 Listen", "🔊 Sikiliza", "🔊 Sikiza"),
                        style = T.label.copy(fontFamily = FontFamily.Serif), color = ScrollInk,
                        modifier = Modifier.clickable {
                            if (readingAloud) {
                                speaker.stop()
                                readingAloud = false
                            } else {
                                readingAloud = true
                                speaker.speak(
                                    page.take(600),
                                    onDone = { readingAloud = false },
                                    emotion = Speaker.VoiceEmotion.STORYTELLER
                                )
                            }
                        }.padding(Sp.xs)
                            .semantics {
                                contentDescription = if (readingAloud) "Stop reading aloud" else "Listen to this page"
                                role = Role.Button
                            }
                    )
                }
                LinearProgressIndicator(
                    progress = { (pageIdx + 1) / pages.size.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(Rad.pill),
                    color = Gold
                )
                Text(
                    "🇫🇷 Texte français",
                    style = T.section.copy(fontFamily = FontFamily.Serif),
                    color = ScrollInk
                )
                Text(
                    dropCapText(page),
                    style = T.secondary.copy(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, lineHeight = 22.sp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                    BigButton(
                        lang.t("← Previous", "← Iliyotangulia", "← Previous"),
                        modifier = Modifier.weight(1f),
                        enabled = pageIdx > 0, color = ParchmentEdge,
                        onClick = { pageIdx = (pageIdx - 1).coerceAtLeast(0) }
                    )
                    BigButton(
                        lang.t("Next →", "→ Ifuatayo", "Next →"),
                        modifier = Modifier.weight(1f), color = Emerald,
                        enabled = pageIdx < pages.size - 1,
                        onClick = { pageIdx = (pageIdx + 1).coerceAtMost(pages.size - 1) }
                    )
                }
            }
            Text(
                "❦ ❧ ❦",
                style = T.section, color = ParchmentEdge, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                if (showExtras) lang.t("▲ Hide translation & sharing", "▲ Ficha tafsiri", "▲ Ficha translation")
                else lang.t("🇬🇧 Translation & sharing", "🇬🇧 Tafsiri", "🇬🇧 Translation"),
                style = T.label.copy(fontFamily = FontFamily.Serif), color = ParchmentEdge,
                modifier = Modifier.clickable { showExtras = !showExtras }.padding(vertical = Sp.xs)
                    .semantics { contentDescription = if (showExtras) "Hide translation and sharing" else "Show translation and sharing"; role = Role.Button }
            )
            if (showExtras) {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    Text(
                        "🇬🇧 English translation",
                        style = T.section.copy(fontFamily = FontFamily.Serif),
                        color = ScrollInk
                    )
                    Text(
                        novel.enText,
                        style = T.body.copy(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, lineHeight = 25.sp),
                        color = ParchmentEdge
                    )
                }
                Spacer(Modifier.padding(Sp.sm))
                BigButton("🖨️ Print / Share", onClick = {
                    val shareText = "📖 ${novel.title}\n${novel.description}\n\n🇫🇷 ${novel.frText}\n🇬🇧 ${novel.enText}"
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(android.content.Intent.createChooser(intent, "Share ${novel.title}"))
                })
                if (novel.printable) {
                    BigButton("📄 Download Text", color = Turquoise, onClick = {
                        val file = File(context.filesDir, "${novel.id}.txt")
                        file.writeText("${novel.title}\n${novel.frText}\n\n${novel.enText}")
                    })
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

/* ═══════════ WRITING ASSISTANT ═══════════ */

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WritingAssistantScreen(
    onBack: () -> Unit,
    onSave: (title: String, genre: String, level: String, fr: String, en: String) -> Unit
) {
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
                    Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
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
                        colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Cobalt),
                        singleLine = false
                    )
                    SectionHeader("🇬🇧 English translation")
                    TextField(
                        value = englishText, onValueChange = { englishText = it },
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        placeholder = { Text("Write the English translation...") },
                        shape = Rad.xl,
                        colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Cobalt),
                        singleLine = false
                    )
                }
            }

            Card {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    SectionHeader("💡 AI Suggestions")
                    Text("Based on your genre ($genre) and level ($level):", style = T.caption, color = InkMuted)
                    Text(generateSuggestion(genre, level), style = T.body, color = Ink)
                    Spacer(Modifier.padding(Sp.xs))
                    BigButton("💡 Get Suggestion", onClick = {
                        suggestion = generateSuggestion(genre, level)
                    })
                    if (suggestion.isNotBlank()) {
                        Text(suggestion, style = T.bodySemi, color = Cobalt)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                BigButton("💾 Save Novel", modifier = Modifier.weight(1f), onClick = {
                    if (title.isNotBlank() && frenchText.isNotBlank()) {
                        onSave(title, genre, level, frenchText, englishText)
                        saved = true
                    }
                })
                BigButton("✨ Generate", color = Violet, modifier = Modifier.weight(1f), onClick = {
                    if (title.isBlank()) title = "Mon Roman ${System.currentTimeMillis().toString().takeLast(4)}"
                    if (frenchText.isBlank()) frenchText = generateFrenchExcerpt(genre)
                })
            }
            if (saved) Text("✅ Novel saved!", style = T.label, color = Emerald)
        }
    }
}

private fun generateSuggestion(genre: String, level: String): String {
    val suggestions = mapOf(
        "Romance" to "Try adding dialogue between characters. Use 'Tu' for informal or 'Vous' for formal address. Add emotional descriptions.",
        "Mystery" to "Add a clue in the next chapter. Use 'Qui...?' and 'Où...?' questions to create suspense.",
        "Comedy" to "Add a misunderstanding scene. Use wordplay or unexpected twists.",
        "Sci-Fi" to "Introduce a futuristic element. Describe technology using 'comme' comparisons.",
        "Horror" to "Build tension with short sentences. Use 'soudain' and 'aucun bruit'.",
        "Adventure" to "Add a journey scene. Use directional vocabulary ('à gauche', 'tout droit').",
        "Fantasy" to "Introduce a magical creature or spell. Use descriptive adjectives.",
        "Drama" to "Add an emotional climax. Use 'parce que' and 'bien que' for complex emotions.",
        "Slice-of-Life" to "Describe a mundane moment beautifully. Use sensory details.",
        "Mature" to "Explore complex themes like identity or loss. Use subjunctive mood.",
        "Funny" to "Add a character mistake or absurd situation. Use exaggerated descriptions.",
        "Thriller" to "Add a countdown or deadline. Use short, punchy sentences.",
        "Supernatural" to "Introduce an otherworldly element. Use mysterious descriptions.",
        "Coming-of-Age" to "Add a moment of self-discovery. Use 'je réalise que...'.",
        "Historical" to "Add a historical detail or setting description. Use past tense ('passé composé').",
        "Detective" to "Add clues and suspects. Use 'l'enquête', 'l'indice', 'le suspect'.",
        "Gothic" to "Create atmosphere with shadows and secrets. Use 'le château', 'la nuit', 'l'obscurité'.",
        "Literary" to "Explore language, memory, and the human condition. Use metaphors.",
        "Romantic" to "Celebrate love in all its complexity. Use passionate descriptions."
    )
    val suggestion = suggestions.getOrDefault(genre, "Try adding more dialogue and description to your novel.")
    return "$suggestion (Level: $level)"
}

private fun generateFrenchExcerpt(genre: String): String {
    val excerpt = when (genre.lowercase()) {
        "romance" -> "La première fois que je l'ai vue, le monde s'est arrêté. Ses yeux brillaient comme deux étoiles perdues dans la nuit parisienne."
        "mystery" -> "La lettre arriva sans expéditeur. Son contenu était clair, mais les implications étaient troublantes."
        "comedy" -> "Le chat était assis sur le clavier. Il tapait des mots au hasard. Quand j'ai regardé l'écran, le message dit : 'Je t'aime'."
        "adventure" -> "Le bateau voguait sur l'océan infini. L'horizon était une ligne brisée entre le ciel et la mer."
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
    onBack: () -> Unit,
    lang: HelpLang = HelpLang.ENGLISH,
    onDownloads: () -> Unit = {}
) {
    val context = LocalContext.current
    // Only songs physically in the app's Music folder are shown here —
    // the full catalog lives in Téléchargements. Checked off-main-thread.
    var imported by remember { mutableStateOf<List<Song>?>(null) }
    LaunchedEffect(songs) {
        imported = withContext(Dispatchers.IO) {
            songs.filter { findLocalAudio(context, it) != null }
        }
    }
    val library = imported ?: emptyList()
    var selectedArtist by remember { mutableStateOf("All") }
    var selectedGenre by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val artists = remember(library) { listOf("All") + library.map { it.artist }.distinct() }
    val genres = remember(library) { listOf("All") + library.map { it.genre }.distinct().sorted() }
    val shown = remember(library, selectedArtist, selectedGenre, searchQuery) {
        // Accent-insensitive: "zaz" finds Zaz, "fete" finds "fête".
        val q = normName(searchQuery.trim())
        library.filter { (selectedArtist == "All" || it.artist == selectedArtist) &&
            (selectedGenre == "All" || it.genre == selectedGenre) &&
            (q.isBlank() || normName(it.title).contains(q) || normName(it.artist).contains(q) ||
                normName(it.genre).contains(q)) }
    }

    AppBackground(tint = GoldSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back to Enjoy"; role = Role.Button })
                    Text("🎵 Chansons françaises", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                Text(
                    lang.t(
                        "Only your imported songs — playable offline, anytime",
                        "Nyimbo zako ulizoimport tu — zinachezwa offline",
                        "Songs zako umeimport tu — zinaplay offline"
                    ),
                    style = T.secondary, color = InkSoft
                )
            }
            item {
                OutlinedSearchField(
                    lang.t("Search songs...", "Tafuta nyimbo...", "Search songs..."),
                    searchQuery
                ) { searchQuery = it }
            }
            item {
                Text(lang.t("Artiste :", "Msanii :", "Artist:"), style = T.label, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs), modifier = Modifier.horizontalScroll(rememberScrollState())) {
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
                                .semantics { contentDescription = "Filter by artist: $a" }
                        )
                    }
                }
            }
            item {
                Text(lang.t("Genre :", "Aina :", "Genre:"), style = T.label, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.xs), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    genres.forEach { g ->
                        val sel = selectedGenre == g
                        Text(
                            g, style = T.label,
                            color = if (sel) White else InkSoft,
                            modifier = Modifier
                                .clip(Rad.pill)
                                .background(if (sel) Violet else Surface)
                                .border(2.dp, if (sel) Violet else Border, Rad.pill)
                                .clickable { selectedGenre = g }
                                .padding(horizontal = Sp.sm, vertical = Sp.xs)
                                .semantics { contentDescription = "Filter by genre: $g" }
                        )
                    }
                }
            }
            if (imported == null) {
                item {
                    LoadingRow(lang.t("Finding your songs…", "Natafuta nyimbo zako…", "Natafuta songs zako…"))
                }
            } else {
                item {
                    Text(
                        lang.t(
                            "${shown.size} / ${library.size} imported songs",
                            "${shown.size} / ${library.size} zilizoimportiwa",
                            "${shown.size} / ${library.size} zimeimportiwa"
                        ),
                        style = T.caption, color = InkMuted,
                        modifier = Modifier.semantics { contentDescription = "${shown.size} songs shown out of ${library.size}" }
                    )
                }
            }
            items(shown) { song ->
                SongCard(song) { onSongClick(song) }
            }
            if (imported != null && library.isEmpty()) {
                item {
                    Card {
                        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            SectionHeader(lang.t("🎵 No songs yet", "🎵 Hakuna nyimbo", "🎵 Hakuna songs"))
                            Text(
                                lang.t(
                                    "Scan your phone and import French songs in Téléchargements — they'll appear here, ready offline.",
                                    "Changanua simu na ulete nyimbo za Kifaransa kwa Téléchargements — zitaonekana hapa.",
                                    "Scan phone ulete songs za French kwa Téléchargements — zitaonekana hapa."
                                ),
                                style = T.caption, color = InkSoft
                            )
                            BigButton(
                                lang.t("⬇ Open Téléchargements", "⬇ Fungua Téléchargements", "⬇ Fungua Téléchargements"),
                                onClick = onDownloads
                            )
                        }
                    }
                }
            } else if (shown.isEmpty() && imported != null) {
                item {
                    Text(
                        lang.t(
                            "No songs match — try another artist or word 🎵",
                            "Hakuna nyimbo — jaribu msanii au neno lingine 🎵",
                            "Hakuna song — jaribu artist ama word ingine 🎵"
                        ),
                        style = T.secondary, color = InkSoft
                    )
                }
            }
        }
    }
}

@Composable
private fun SongCard(song: Song, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick)
            .semantics { contentDescription = "${song.title} by ${song.artist}, ${song.genre} ${song.year}"; role = Role.Button }
    ) {
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

/** Awaits one TTS utterance; cancelling stops the voice. */
private suspend fun Speaker.speakAndWait(
    text: String,
    emotion: Speaker.VoiceEmotion = Speaker.VoiceEmotion.NEUTRAL
) = suspendCancellableCoroutine { cont ->
    speak(text, onDone = { if (cont.isActive) cont.resume(Unit) }, emotion = emotion)
    cont.invokeOnCancellation { stop() }
}

/**
 * Only the singable French reaches the voice: instruction lines ([…]) are
 * skipped and « Extrait : … » wrappers are unwrapped to the bare hook.
 */
private fun speakable(line: String): String {
    val t = line.trim()
    if (t.isEmpty() || t.startsWith("[")) return ""
    val quoted = Regex("«(.+)»").find(t)?.groupValues?.getOrNull(1)?.trim()
    return quoted?.takeIf { it.isNotEmpty() } ?: t
}

@Composable
fun SongDetailScreen(
    song: Song,
    speaker: Speaker,
    onBack: () -> Unit,
    onPractice: (Song) -> Unit = {},
    lang: HelpLang = HelpLang.ENGLISH
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val lines = remember(song.id) {
        song.frLyrics.lines().map { it.trim() }.filter { it.isNotEmpty() }
    }
    var playing by remember(song.id) { mutableStateOf(false) }
    var lineIdx by remember(song.id) { mutableStateOf(0) }
    DisposableEffect(song.id) {
        onDispose { speaker.stop() }
    }
    LaunchedEffect(playing, song.id) {
        if (!playing) return@LaunchedEffect
        var i = lineIdx
        while (i < lines.size) {
            lineIdx = i
            val say = speakable(lines[i])
            if (say.isNotEmpty()) {
                speaker.speakAndWait(say, Speaker.VoiceEmotion.SINGING)
                if (!playing) break
                // Breath between lines — the voice flows instead of machine-gunning.
                delay(220)
            }
            i++
        }
        playing = false
    }
    AppBackground(tint = Lavender) {
        Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(scrollState).padding(Sp.xxl).padding(end = Sp.md),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
                Text("🎵 ${song.title}", style = T.screenTitle, color = Ink, modifier = Modifier.weight(1f))
            }
            Text("${song.artist} · ${song.genre} · ${song.year}", style = T.caption, color = InkMuted)
            Spacer(Modifier.padding(Sp.xs))
            Divider()
            SectionHeader(lang.t("▶ Listen right here", "▶ Sikiliza hapa", "▶ Sikiza hapa"))
            Card {
                Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                    Text(
                        if (playing) lang.t(
                            "▶ Playing… line ${lineIdx + 1} / ${lines.size}",
                            "▶ Inacheza… mstari ${lineIdx + 1} / ${lines.size}",
                            "▶ Inacheza… line ${lineIdx + 1} / ${lines.size}"
                        ) else lang.t("En pause", "Imesimama", "Ime-pause"),
                        style = T.label, color = Cobalt,
                        modifier = Modifier.semantics { contentDescription = if (playing) "Playing line ${lineIdx + 1} of ${lines.size}" else "Paused" }
                    )
                    LinearProgressIndicator(
                        progress = { if (lines.isEmpty()) 0f else (lineIdx + 1) / lines.size.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(Rad.pill),
                        color = Coral
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                        if (!playing) {
                            BigButton(lang.t("▶ Listen", "▶ Sikiliza", "▶ Sikiza"), modifier = Modifier.weight(1f), onClick = {
                                if (lineIdx >= lines.size - 1 && lineIdx > 0) lineIdx = 0
                                playing = true
                            })
                        } else {
                            BigButton(lang.t("⏸ Pause", "⏸ Sitisha", "⏸ Pause"), color = Gold, modifier = Modifier.weight(1f), onClick = {
                                playing = false
                                speaker.stop()
                            })
                        }
                        BigButton(lang.t("↺ Restart", "↺ Anza upya", "↺ Restart"), color = Turquoise, modifier = Modifier.weight(1f), onClick = {
                            speaker.stop()
                            lineIdx = 0
                            playing = true
                        })
                    }
                }
            }
            SectionHeader("🎧 Fichier audio")
            // Audio lookup (incl. MediaStore scan) runs off the main thread.
            var audioFile by remember(song.id) { mutableStateOf<File?>(null) }
            var audioUri by remember(song.id) { mutableStateOf<android.net.Uri?>(null) }
            var audioScanned by remember(song.id) { mutableStateOf(false) }
            LaunchedEffect(song.id) {
                val track = withContext(Dispatchers.IO) { findSongAudioTrack(context, song) }
                audioFile = track?.file
                audioUri = track?.uri
                audioScanned = true
            }
            val file = audioFile
            if (file != null) {
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        Text(
                            "Trouvé : ${file.name}",
                            style = T.caption, color = Emerald,
                            modifier = Modifier.semantics { contentDescription = "Audio file found: ${file.name}" }
                        )
                        LocalAudioPlayer(label = song.title, file = file, uri = audioUri, lang = lang)
                    }
                }
            } else {
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        Text(if (audioScanned) "Pas encore de MP3 pour cette chanson." else "Vérification des fichiers audio…", style = T.bodySemi, color = Ink)
                        Text(
                            "Téléchargez-la dans n'importe quel format (MP3, M4A, OGG, WAV, FLAC, OPUS…), nommez-la comme ceci (ou « Artiste - Titre.mp3 ») :",
                            style = T.caption, color = InkSoft
                        )
                        Text(
                            "${song.id}.mp3",
                            style = T.bodySemi, color = Cobalt,
                            modifier = Modifier.semantics { contentDescription = "Needed file name: ${song.id} dot mp3" }
                        )
                        Text(
                            "Placez le fichier dans le dossier Music de l'app, puis revenez ici.",
                            style = T.caption, color = InkSoft
                        )
                    }
                }
            }
            BigButton(lang.t("🎤 Practice this song", "🎤 Jizoeze na wimbo huu", "🎤 Practice na song hii"), color = Violet, onClick = {
                onPractice(song)
            })
            Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                SectionHeader("🇫🇷 Fiche d'étude")
                Text(
                    "💡 Astuce : touchez une ligne pour l'entendre seule",
                    style = T.caption, color = InkMuted
                )
                lines.forEachIndexed { i, line ->
                    val current = playing && i == lineIdx
                    Text(
                        line, style = T.body,
                        color = Ink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Rad.md)
                            .background(if (current) GoldSoft else androidx.compose.ui.graphics.Color.Transparent)
                            .border(1.dp, if (current) Gold else Border, Rad.md)
                            .clickable {
                                speaker.speak(
                                    speakable(line).ifBlank { line },
                                    emotion = Speaker.VoiceEmotion.SINGING
                                )
                            }
                            .padding(Sp.sm)
                            .semantics { contentDescription = "Line ${i + 1}: $line. Tap to hear it."; role = Role.Button }
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                SectionHeader("🇬🇧 English translation")
                Text(song.enTranslation, style = T.body, color = InkSoft, modifier = Modifier.padding(Sp.sm))
            }
            Spacer(Modifier.padding(Sp.sm))
            BigButton("🖨️ Print Lyrics", onClick = {
                val shareText = "🎵 ${song.title} - ${song.artist}\n${song.year}\n\n🇫🇷 ${song.frLyrics}\n\n🇬🇧 ${song.enTranslation}"
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(android.content.Intent.createChooser(intent, "Share ${song.title}"))
            })
            BigButton("💾 Download Lyrics", color = Turquoise, onClick = {
                val file = File(context.filesDir, "${song.id}.txt")
                file.writeText("${song.title}\n${song.frLyrics}\n\n${song.enTranslation}")
            })
        }
        Box(
            Modifier.align(Alignment.CenterEnd).padding(end = 4.dp, top = 96.dp, bottom = 96.dp)
        ) {
            SideScrollbar(scrollState, Cobalt)
        }
        }
    }
}

fun parseLyrics(text: String): List<String> {
    return text.lines().filter { it.isNotBlank() && !it.startsWith("[") && !it.startsWith("(") }
}

/* ═══════════ DOWNLOAD CHECKLIST ═══════════ */

private data class SongFileStatus(val song: Song, val file: File?, val uri: android.net.Uri? = null)

/** One non-catalog French track: expandable mini player + pull-into-app. */
@Composable
private fun FrenchTrackRow(
    track: DeviceTrack,
    onImport: () -> Unit,
    lang: HelpLang = HelpLang.ENGLISH
) {
    var open by remember(track.file.absolutePath) { mutableStateOf(false) }
    Card {
        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (open) "▼" else "▶",
                    style = T.section, color = Cobalt,
                    modifier = Modifier
                        .clickable { open = !open }
                        .padding(end = Sp.sm)
                        .semantics {
                            contentDescription = if (open) "Close player for ${track.rawTitle}" else "Play ${track.rawTitle} by ${track.rawArtist}"
                            role = Role.Button
                        }
                )
                Column(Modifier.weight(1f)) {
                    Text(track.rawTitle, style = T.bodySemi, color = Ink)
                    Text(track.rawArtist, style = T.caption, color = InkSoft)
                }
                Text(
                    "＋ Import",
                    style = T.label, color = Cobalt,
                    modifier = Modifier
                        .clip(Rad.pill)
                        .background(CobaltSoft)
                        .clickable(onClick = onImport)
                        .padding(horizontal = Sp.sm, vertical = Sp.xs)
                        .semantics { contentDescription = "Import ${track.rawTitle} into the app"; role = Role.Button }
                )
            }
            if (open) {
                LocalAudioPlayer(
                    label = "${track.rawTitle} — ${track.rawArtist}",
                    file = track.file,
                    uri = track.uri,
                    lang = lang
                )
            }
        }
    }
}

/**
 * The shopping list: every study song, what to name the file, what's already
 * in the app's Music folder (matched smartly — any format, any naming).
 */
@Composable
fun SongDownloadScreen(
    onBack: () -> Unit,
    onOpenSong: (Song) -> Unit,
    lang: HelpLang = HelpLang.ENGLISH
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var missingOnly by remember { mutableStateOf(false) }
    var statuses by remember { mutableStateOf<List<SongFileStatus>>(emptyList()) }
    var scanning by remember { mutableStateOf(true) }
    var importsDone by remember { mutableStateOf(0) }
    var importing by remember { mutableStateOf(false) }
    var importTotal by remember { mutableStateOf(0) }
    var importedN by remember { mutableStateOf(0) }
    var importFails by remember { mutableStateOf(0) }
    var deviceN by remember { mutableStateOf(0) }
    var deviceTracks by remember { mutableStateOf<List<DeviceTrack>>(emptyList()) }
    val scope = rememberCoroutineScope()
    // Sources that failed to copy — skipped on later rescans so one bad
    // file never spins the import again. Plain set, one coroutine at a time.
    val failedSrcs = remember { mutableSetOf<String>() }
    // Storage permission: Android 13+ needs READ_MEDIA_AUDIO, older needs
    // READ_EXTERNAL_STORAGE. Without it the MediaStore query silently
    // returns nothing — this gate is why scans came back empty.
    val audioPerm = if (android.os.Build.VERSION.SDK_INT >= 33)
        android.Manifest.permission.READ_MEDIA_AUDIO
    else android.Manifest.permission.READ_EXTERNAL_STORAGE
    fun hasAudioPerm() = context.checkSelfPermission(audioPerm) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
    var hasPerm by remember { mutableStateOf(hasAudioPerm()) }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPerm = granted
        if (granted) importsDone++
    }
    // Ask once on entry; the rationale card below stays until granted.
    LaunchedEffect(Unit) {
        if (!hasAudioPerm()) permLauncher.launch(audioPerm)
    }
    LaunchedEffect(importsDone, hasPerm) {
        if (!hasPerm) {
            scanning = false
            statuses = emptyList()
            deviceN = 0
            deviceTracks = emptyList()
            return@LaunchedEffect
        }
        scanning = true
        // Pass 1: one device scan, match all 600 songs in memory.
        val tracks = withContext(Dispatchers.IO) { scanDeviceTracks(context) }
        deviceN = tracks.size
        deviceTracks = tracks
        val first = withContext(Dispatchers.IO) {
            FRENCH_SONGS.map { s ->
                val local = findLocalAudio(context, s)
                if (local != null) SongFileStatus(s, local)
                else {
                    val t = matchTrack(s, tracks)
                    SongFileStatus(s, t?.file, t?.uri)
                }
            }
        }
        statuses = first
        // Pass 2: auto-import via the shared routine (same as Import All).
        val musicDirPath = musicDir(context).absolutePath
        val external = first.filter { it.file != null && it.file.parent != musicDirPath }
        if (external.isNotEmpty()) {
            importing = true
            importTotal = external.size
            importedN = 0
            importFails = 0
            val (resolved, fails) = pullIntoApp(context, external, failedSrcs) {
                withContext(Dispatchers.Main) { importedN++ }
            }
            if (resolved.isNotEmpty()) {
                statuses = first.map { st -> resolved[st.song.id]?.let { st.copy(file = it) } ?: st }
            }
            importFails = fails
            importing = false
        } else {
            importedN = 0
            importFails = 0
        }
        scanning = false
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (!uris.isNullOrEmpty()) {
            runCatching {
                val dir = musicDir(context).apply { mkdirs() }
                uris.forEach { uri ->
                    val raw = queryDisplayName(context, uri) ?: "audio-${System.currentTimeMillis()}.mp3"
                    val clean = cleanFileName(raw)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        File(dir, clean).outputStream().use { input.copyTo(it) }
                    }
                }
            }
            importsDone++
        }
    }
    val done = statuses.count { it.file != null }
    val shown = remember(statuses, query, missingOnly) {
        val q = normName(query.trim())
        statuses.filter { (if (missingOnly) it.file == null else true) &&
            (q.isBlank() || normName(it.song.title).contains(q) || normName(it.song.artist).contains(q)) }
    }
    // French on this phone that is NOT in the study catalog: device tracks
    // the recognizer didn't claim, passing the on-device French detector.
    val frenchOnly = remember(statuses, deviceTracks, scanning, query) {
        if (scanning || deviceTracks.isEmpty()) emptyList()
        else {
            val claimed = statuses.mapNotNull { it.file?.absolutePath }.toSet()
            val qn = normName(query.trim())
            deviceTracks.filter { t ->
                t.file.absolutePath !in claimed && looksFrench(t.rawTitle, t.rawArtist, t.normFile) &&
                    (qn.isBlank() || normName("${t.rawTitle} ${t.rawArtist}").contains(qn))
            }
        }
    }
    AppBackground(tint = GoldSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            if (!hasPerm && !scanning) {
                item {
                    Card {
                        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                            SectionHeader(lang.t("🔒 Music access needed", "🔒 Ruhusa ya muziki", "🔒 Permission ya music"))
                            Text(
                                lang.t(
                                    "Parlons needs access to your music files to find French songs. Nothing leaves your phone.",
                                    "Parlons inahitaji kuona nyimbo zako kupata za Kifaransa. Hakuna kinachotoka kwa simu.",
                                    "Parlons inahitaji kuona songs zako kupata za French. Hakuna kitu inatoka kwa phone."
                                ),
                                style = T.caption, color = InkSoft
                            )
                            BigButton(
                                lang.t("Allow music access", "Ruhusu", "Allow"),
                                onClick = { permLauncher.launch(audioPerm) }
                            )
                        }
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("←", style = T.section, color = InkMuted,
                        modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back to Enjoy"; role = Role.Button })
                    Text("⬇ Chansons à télécharger", style = T.screenTitle, color = Ink,
                        modifier = Modifier.semantics { heading() })
                }
                if (scanning) LoadingRow(lang.t("Checking files…", "Inaangalia faili…", "Inacheck files…"))
                else Text(
                    lang.t(
                        "$done / ${FRENCH_SONGS.size} already in the app",
                        "$done / ${FRENCH_SONGS.size} zimo kwenye app",
                        "$done / ${FRENCH_SONGS.size} ziko kwa app"
                    ),
                    style = T.secondary, color = InkSoft
                )
                val importable = remember(statuses) {
                    val dirPath = musicDir(context).absolutePath
                    statuses.filter { it.file != null && it.file.parent != dirPath }
                }
                if (!scanning && !importing && importable.isNotEmpty()) {
                    BigButton(
                        lang.t(
                            "⬇ Import all ${importable.size} recognized",
                            "⬇ Leta zote ${importable.size} zilizotambuliwa",
                            "⬇ Import zote ${importable.size} zimejulikana"
                        ),
                        onClick = {
                            importing = true
                            importTotal = importable.size
                            importedN = 0
                            importFails = 0
                            scope.launch {
                                val (resolved, fails) = pullIntoApp(context, importable, failedSrcs) {
                                    withContext(Dispatchers.Main) { importedN++ }
                                }
                                if (resolved.isNotEmpty()) {
                                    statuses = statuses.map { st ->
                                        resolved[st.song.id]?.let { st.copy(file = it) } ?: st
                                    }
                                }
                                importFails = fails
                                importing = false
                            }
                        }
                    )
                }
                if (importing) LoadingRow(
                    lang.t(
                        "Moving songs in… $importedN/$importTotal",
                        "Inahamisha nyimbo… $importedN/$importTotal",
                        "Inamove songs… $importedN/$importTotal"
                    )
                )
                if (!scanning && !importing && importedN > 0) {
                    Text(
                        lang.t(
                            "✅ $importedN files moved into the app — play from any song card",
                            "✅ Faili $importedN zimehamishwa — cheza kutoka song yoyote",
                            "✅ Files $importedN zimeingia — play kutoka song yoyote"
                        ),
                        style = T.caption, color = Emerald
                    )
                }
                if (!scanning && !importing && importFails > 0) {
                    Text(
                        lang.t(
                            "⚠️ $importFails couldn't be moved (storage or permission)",
                            "⚠️ $importFails hazikuhamishika (hifadhi au ruhusa)",
                            "⚠️ $importFails hazikuingia (storage ama permission)"
                        ),
                        style = T.caption, color = Coral
                    )
                }
                val appMb = remember(statuses) {
                    (musicDir(context).listFiles()?.sumOf { it.length() } ?: 0L) / 1048576f
                }
                if (!scanning && appMb >= 0.1f) {
                    val mb = "%.1f".format(appMb)
                    Text(
                        lang.t(
                            "📦 $mb MB of songs in the app",
                            "📦 MB $mb za nyimbo",
                            "📦 $mb MB za songs"
                        ),
                        style = T.caption, color = InkMuted
                    )
                }
                if (!scanning && deviceN == 0 && done == 0) {
                    Text(
                        lang.t(
                            "No music found on this phone — use Import below ⬇",
                            "Hakuna muziki — tumia Import hapa chini ⬇",
                            "Hakuna music — tumia Import hapa chini ⬇"
                        ),
                        style = T.caption, color = Cobalt
                    )
                }
            }
            if (frenchOnly.isNotEmpty()) {
                item {
                    SectionHeader(
                        lang.t(
                            "🇫🇷 French on this phone (${frenchOnly.size})",
                            "🇫🇷 Kifaransa kwa simu (${frenchOnly.size})",
                            "🇫🇷 French kwa phone (${frenchOnly.size})"
                        )
                    )
                    Text(
                        lang.t(
                            "Not in the study list — play below, or pull a song in.",
                            "Sio kwa orodha — cheza hapa, au ivute.",
                            "Sio kwa study list — play hapa, ama iimport."
                        ),
                        style = T.caption, color = InkSoft
                    )
                }
                items(frenchOnly, key = { it.file.absolutePath }) { t ->
                    FrenchTrackRow(
                        track = t,
                        lang = lang,
                        onImport = {
                            scope.launch(Dispatchers.IO) {
                                runCatching {
                                    val dir = musicDir(context).apply { mkdirs() }
                                    val ext = t.file.extension.ifBlank { "mp3" }
                                    val clean = cleanFileName("${t.rawArtist} - ${t.rawTitle}.$ext")
                                    t.file.inputStream().use { input ->
                                        File(dir, clean).outputStream().use { input.copyTo(it) }
                                    }
                                }
                                withContext(Dispatchers.Main) { importsDone++ }
                            }
                        }
                    )
                }
            }
            item {
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
                        SectionHeader(lang.t("📂 Where to put them", "📂 Kuziweka wapi", "📂 Kuziweka wapi"))
                        Text(
                            lang.t(
                                "1. Keep the song anywhere on your phone (MP3, M4A, OGG, WAV, FLAC… any folder).",
                                "1. Weka wimbo popote kwa simu (MP3, M4A, OGG, WAV, FLAC… folda yoyote).",
                                "1. Weka song popote kwa phone (MP3, M4A, OGG, WAV, FLAC… folder yoyote)."
                            ),
                            style = T.caption, color = InkSoft
                        )
                        Text(
                            lang.t(
                                "2. Open this screen — recognized songs move into the app by themselves.",
                                "2. Fungua ukurasa huu — nyimbo zinazotambuliwa zinahamia zenyewe.",
                                "2. Fungua hii screen — songs zinajitambua zinaingia zenyewe."
                            ),
                            style = T.caption, color = InkSoft
                        )
                        Text(
                            lang.t(
                                "3. Play them from any song card. No naming, no USB, no tapping each song.",
                                "3. Zicheze kutoka song yoyote. Hakuna kujina, USB, ama kugusa kila moja.",
                                "3. Play kutoka song yoyote. No naming, no USB, no tap kila song."
                            ),
                            style = T.caption, color = InkSoft
                        )
                        Text(
                            lang.t(
                                "Names like “Artist - Title.mp3” match instantly; odd names still match by tags.",
                                "Majina kama “Msanii - Kichwa.mp3” yanashikana papo; majina mengine kwa tags.",
                                "Majina kama “Artist - Title.mp3” zinashika papo; zingine na tags."
                            ),
                            style = T.caption, color = Emerald
                        )
                        BigButton(lang.t("📥 Import my MP3s", "📥 Leta MP3 zangu", "📥 Import MP3 zangu"), onClick = {
                            picker.launch(arrayOf("audio/*"))
                        })
                    }
                }
            }
            item {
                OutlinedSearchField("Search title or artist...", query) { query = it }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        lang.t("Missing only", "Zilizokosekana tu", "Missing only"),
                        style = T.label, color = Ink, modifier = Modifier.weight(1f)
                    )
                    Box(
                        Modifier.size(40.dp).clip(Rad.md)
                            .background(if (missingOnly) Cobalt else Border)
                            .clickable { missingOnly = !missingOnly }
                            .semantics { contentDescription = "Show missing only"; role = Role.Button },
                        contentAlignment = Alignment.Center
                    ) { Text(if (missingOnly) "✓" else "○", color = if (missingOnly) White else Ink, fontSize = 16.sp) }
                }
            }
            item {
                Text(
                    "${shown.size} chansons listées",
                    style = T.caption, color = InkMuted
                )
            }
            if (!scanning && statuses.isNotEmpty() && statuses.all { it.file != null }) {
                item {
                    Card {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Mascot(MascotMood.CELEBRATING, size = 64.dp)
                            Spacer(Modifier.padding(Sp.sm))
                            Column(Modifier.weight(1f)) {
                                Text("🎉 Collection complete!", style = T.bodySemi, color = Ink)
                                Text(
                                    "Every song is ready — enjoy the music.",
                                    style = T.caption, color = InkSoft
                                )
                            }
                        }
                    }
                }
            }
            items(shown, key = { it.song.id }) { (song, file) ->
                Card(
                    modifier = Modifier.clickable(onClick = { onOpenSong(song) })
                        .semantics {
                            contentDescription = "${song.title} by ${song.artist}, " +
                                if (file != null) "downloaded as ${file.name}" else "not downloaded yet"
                            role = Role.Button
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(44.dp).clip(Rad.md)
                                .background(if (file != null) EmeraldSoft else GoldSoft),
                            contentAlignment = Alignment.Center
                        ) { Text(if (file != null) "✅" else "⬇️", fontSize = 22.sp) }
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text(song.title.substringBefore(" —"), style = T.bodySemi, color = Ink)
                            Text("${song.artist} · ${song.genre}", style = T.caption, color = InkSoft)
                            Text(
                                file?.name ?: "${song.artist} - ${song.title.substringBefore(" —")}.mp3",
                                style = T.caption, color = if (file != null) Emerald else InkMuted
                            )
                        }
                        Text("→", style = T.section, color = InkMuted)
                    }
                }
            }
        }
    }
}
