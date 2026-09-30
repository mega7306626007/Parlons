package com.francofun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Density
import com.francofun.content.EnjoyTab
import com.francofun.content.FRENCH_SONGS
import com.francofun.content.NovelReaderScreen
import com.francofun.content.POEMS
import com.francofun.content.PoemDetailScreen
import com.francofun.content.PoemTab
import com.francofun.content.WritePoemScreen
import com.francofun.content.WritingAssistantScreen
import com.francofun.content.getPoemById
import com.francofun.content.loadUserPoems
import com.francofun.content.saveUserNovel
import com.francofun.content.saveUserPoem
import com.francofun.content.NovelTab
import com.francofun.content.SongDetailScreen
import com.francofun.content.SongDownloadScreen
import com.francofun.content.SongTab
import com.francofun.content.buildNovelList
import com.francofun.content.getSongById

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = Store(applicationContext)
        setContent {
            val systemDark = isSystemInDarkTheme()
            val dark = if (store.followSystemTheme) systemDark else store.darkMode
            // Status + nav bar icons stay visible on night backgrounds.
            LaunchedEffect(dark) {
                (this@MainActivity).window?.let { w ->
                    androidx.core.view.WindowInsetsControllerCompat(w, w.decorView)
                        .isAppearanceLightStatusBars = !dark
                }
            }
            ParlonsTheme(dark = dark) { App(store) }
        }
    }
}

sealed interface Route {
    sealed interface Tab : Route
    object Home : Tab
    object Learn : Tab
    object Practice : Tab
    object Words : Tab
    object Profile : Tab
    object Enjoy : Tab

    object Novels : Route
    object Songs : Route
    object SongDownloads : Route
    object Poems : Route
    object WriteNovel : Route
    object WritePoem : Route
    object Drills : Route
    object Textbook : Route
    data class TextbookChapter(val chapterId: String) : Route
    data class StudyGuide(val unitId: String) : Route
    data class Play(val lesson: Lesson) : Route
    data class PoemDetail(val poemId: String) : Route
    data class NovelReader(val novelId: String) : Route
    data class SongDetail(val songId: String) : Route
    data class Chat(val chain: Boolean = false) : Route
    object Call : Route
    object Marathon : Route
    object Settings : Route
    object Stats : Route
    object Custom : Route
    object Review : Route
    object Speed : Route
    object WordBank : Route
    object Onboarding : Route
    object FreeTalk : Route
    object Mistakes : Route
    object Grammar : Route
    object Challenge : Route
}

private data class TabItem(val route: Route, val icon: String, val label: String)

private val TABS = listOf(
    TabItem(Route.Home, "🏠", "Home"),
    TabItem(Route.Learn, "📚", "Learn"),
    TabItem(Route.Practice, "💪", "Practice"),
    TabItem(Route.Words, "📖", "Words"),
    TabItem(Route.Profile, "👤", "Profile"),
    TabItem(Route.Enjoy, "🎧", "Enjoy")
)

@Composable
fun App(store: Store) {
    val ctx = LocalContext.current
    val speaker = remember { Speaker(ctx) }
    val net = remember { NetConnectivity(ctx) }
    val vosk = remember { VoskEngine(ctx) }
    val piper = remember { PiperTts(ctx) }
    val speechEnv = remember(net) {
        SpeechEnv(vosk, net.engineForSession == SpeechEngine.OFFLINE)
    }
    DisposableEffect(speechEnv) {
        speaker.offlineTts = piper
        speaker.preferOffline = speechEnv.preferOffline
        onDispose { speaker.shutdown() }
    }
    var setupDone by remember {
        mutableStateOf(!ModelInstaller.bundledModelsPresent(ctx) || ModelInstaller.modelsReady(ctx))
    }
    if (!setupDone) {
        SetupScreen { setupDone = true }
        return
    }
    var route by remember { mutableStateOf<Route>(if (store.onboarded) Route.Home else Route.Onboarding) }
    var lastTab by remember { mutableStateOf<Route>(Route.Home) }
    // 1000-story library, built once per session off the main thread and shared by Enjoy + reader.
    var libraryNovels by remember { mutableStateOf<List<com.francofun.content.Novel>>(emptyList()) }
    var libraryReady by remember { mutableStateOf(false) }
    var userPoems by remember { mutableStateOf<List<com.francofun.content.Poem>>(emptyList()) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        libraryNovels = withContext(Dispatchers.IO) { buildNovelList(ctx) }
        libraryReady = true
        userPoems = withContext(Dispatchers.IO) { loadUserPoems(ctx) }
    }
    fun reloadLibrary() {
        scope.launch(Dispatchers.IO) {
            val novels = buildNovelList(ctx)
            val poems = loadUserPoems(ctx)
            withContext(Dispatchers.Main) {
                libraryNovels = novels
                userPoems = poems
            }
        }
    }
    fun go(r: Route) {
        if (r is Route.Tab) lastTab = r
        route = r
    }
    BackHandler(enabled = route != Route.Home && route != Route.Onboarding) {
        route = if (route is Route.Tab) Route.Home else lastTab
    }

    val baseDensity = LocalDensity.current
    val scaled = Density(baseDensity.density, fontScale = baseDensity.fontScale * store.textScale)
    val motionMs = if (animationsOff(ctx)) 0 else 220
    val onTab = route is Route.Tab
    CompositionLocalProvider(LocalDensity provides scaled) {
    Column(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            Crossfade(targetState = route, animationSpec = tween(motionMs), label = "route") { r ->
            when (r) {
                Route.Onboarding -> OnboardingScreen(store) { go(Route.Home) }
                Route.Home -> HomeTab(
                    store,
                    onLesson = { go(Route.Play(it)) },
                    onChat = { go(Route.Chat()) },
                    onLearn = { go(Route.Learn) },
                    onPractice = { go(Route.Practice) },
                    onWords = { go(Route.Words) },
                    onProfile = { go(Route.Profile) },
                    onNovels = { go(Route.Novels) },
                    onSongs = { go(Route.Songs) },
                    onMistakes = { go(Route.Mistakes) },
                    onChallenge = { go(Route.Challenge) }
                )
                Route.Learn -> LearnTab(store, onLesson = { go(Route.Play(it)) }, onGuide = { go(Route.StudyGuide(it)) },
                    onTextbook = { go(Route.TextbookChapter(it)) })
                is Route.StudyGuide -> StudyGuideScreen(store, r.unitId, onBack = { go(Route.Learn) }, lang = store.helpLang,
                    onDrills = { go(Route.Drills) })
                Route.Practice -> PracticeTab(
                    store,
                    onReview = { go(Route.Review) },
                    onChat = { go(Route.Chat()) },
                    onChainChat = { go(Route.Chat(chain = true)) },
                    onCall = { go(Route.Call) },
                    onSpeed = { go(Route.Speed) },
                    onCustom = { go(Route.Custom) },
                    onMarathon = { go(Route.Marathon) },
                    onFreeTalk = { go(Route.FreeTalk) },
                    onMistakes = { go(Route.Mistakes) },
                    onChallenge = { go(Route.Challenge) },
                    onGrammar = { go(Route.Grammar) },
                    onDrills = { go(Route.Drills) },
                    onTextbook = { go(Route.Textbook) }
                )
                Route.Drills -> DrillScreen(store, onBack = { go(Route.Practice) })
                Route.Textbook -> TextbookListScreen(onBack = { go(Route.Practice) },
                    onOpen = { go(Route.TextbookChapter(it)) }, lang = store.helpLang)
                is Route.TextbookChapter -> TextbookScreen(r.chapterId, onBack = { go(Route.Textbook) },
                    onDrills = { go(Route.Drills) }, lang = store.helpLang)
                Route.Words -> WordBankScreen(store, speaker) { go(Route.Home) }
                Route.Profile -> ProfileTab(
                    store,
                    onStats = { go(Route.Stats) },
                    onSettings = { go(Route.Settings) }
                )
                Route.Enjoy -> {
                    EnjoyTab(
                        novelCount = libraryNovels.size,
                        novelsLoading = !libraryReady,
                        songCount = FRENCH_SONGS.size,
                        poemCount = userPoems.size + POEMS.size,
                        onNovels = { go(Route.Novels) },
                        onSongs = { go(Route.Songs) },
                        onPoems = { go(Route.Poems) },
                        onDownloads = { go(Route.SongDownloads) },
                        lang = store.helpLang
                    )
                }
                Route.Poems -> PoemTab(
                    poems = userPoems + POEMS,
                    onPoemClick = { poem -> go(Route.PoemDetail(poem.id)) },
                    onBack = { go(Route.Enjoy) },
                    lang = store.helpLang,
                    onWrite = { go(Route.WritePoem) }
                )
                Route.WritePoem -> WritePoemScreen(
                    onBack = { go(Route.Poems) },
                    onSave = { title, topic, level, fr, en ->
                        scope.launch(Dispatchers.IO) {
                            saveUserPoem(ctx, title, topic, level, fr, en)
                            val poems = loadUserPoems(ctx)
                            withContext(Dispatchers.Main) {
                                userPoems = poems
                                go(Route.Poems)
                            }
                        }
                    },
                    lang = store.helpLang
                )
                Route.WriteNovel -> WritingAssistantScreen(
                    onBack = { go(Route.Novels) },
                    onSave = { title, genre, level, fr, en ->
                        scope.launch(Dispatchers.IO) {
                            saveUserNovel(ctx, title, genre, level, fr, en)
                            reloadLibrary()
                            withContext(Dispatchers.Main) { go(Route.Novels) }
                        }
                    }
                )
                is Route.PoemDetail -> {
                    val poem = getPoemById(ctx, r.poemId) ?: POEMS.firstOrNull()
                        ?: com.francofun.content.Poem(
                            id = "fallback", title = "Poème", poet = "Inconnu",
                            topic = "Nature", level = "A1", year = 1900,
                            source = "", frText = "Bonjour !", enText = "Hello!"
                        )
                    PoemDetailScreen(
                        poem = poem, speaker = speaker, onBack = { go(Route.Poems) }, lang = store.helpLang,
                        onPoemClick = { p -> go(Route.PoemDetail(p.id)) }
                    )
                }
                Route.SongDownloads -> SongDownloadScreen(
                    onBack = { go(Route.Enjoy) },
                    onOpenSong = { song -> go(Route.SongDetail(song.id)) },
                    lang = store.helpLang
                )
                Route.Novels -> {
                    NovelTab(
                        novels = libraryNovels,
                        onNovelClick = { novel -> go(Route.NovelReader(novel.id)) },
                        onWriteClick = { go(Route.WriteNovel) },
                        onBack = { go(Route.Enjoy) },
                        lang = store.helpLang,
                        loading = !libraryReady
                    )
                }
                Route.Songs -> SongTab(
                    songs = FRENCH_SONGS,
                    onSongClick = { song -> go(Route.SongDetail(song.id)) },
                    onBack = { go(Route.Enjoy) },
                    lang = store.helpLang,
                    onDownloads = { go(Route.SongDownloads) }
                )
                is Route.NovelReader -> {
                    val novel = libraryNovels.find { it.id == r.novelId } ?: libraryNovels.firstOrNull()
                        ?: com.francofun.content.Novel(
                            id = "fallback", title = "Roman", genre = "Literature",
                            level = "A1", pageCount = 30,
                            frText = "Bonjour !", enText = "Hello!"
                        )
                    NovelReaderScreen(novel = novel, speaker = speaker, store = store, onBack = { go(Route.Novels) }, lang = store.helpLang)
                }
                is Route.SongDetail -> {
                    val song = getSongById(r.songId) ?: FRENCH_SONGS.firstOrNull()
                        ?: com.francofun.content.Song(
                            id = "fallback", title = "Chanson", artist = "Inconnu",
                            genre = "Pop", year = 2000,
                            frLyrics = "Bonjour !", enTranslation = "Hello!"
                        )
                    SongDetailScreen(
                        song = song, speaker = speaker, onBack = { go(Route.Songs) }, lang = store.helpLang,
                        onPractice = { s ->
                            val fr = s.frLyrics.lines().firstOrNull { it.isNotBlank() && !it.startsWith("[") } ?: s.title
                            val en = s.enTranslation.lines().firstOrNull { it.isNotBlank() && !it.startsWith("[") } ?: s.artist
                            go(Route.Play(Lesson(
                                id = "song-${s.id}", emoji = "🎵", fr = s.title, en = s.artist, sw = s.genre,
                                phrases = listOf(Phrase(fr.take(200), en.take(200), s.title, s.artist)),
                                unitId = "u1"
                            )))
                        }
                    )
                }
                is Route.Play -> LessonScreen(store, speaker, speechEnv, r.lesson,
                    onExit = { go(Route.Learn) },
                    onGuide = { go(Route.StudyGuide(r.lesson.unitId)) })
                is Route.Chat -> ChatScreen(store, speaker, speechEnv, chain = r.chain, onBack = { go(Route.Practice) }, onSettings = { go(Route.Settings) }, onCall = { go(Route.Call) })
                Route.Marathon -> {
                    val hard = marathonPhrases(allLessons(), store.srs, maxLevel = store.levelCeiling())
                    val lesson = Lesson("marathon", "🏃", "Marathon", "Hardest words", "Maneno magumu", hard, "u6")
                    LessonScreen(store, speaker, speechEnv, lesson,
                        onExit = { go(Route.Practice) },
                        onGuide = { go(Route.StudyGuide(lesson.unitId)) })
                }
                Route.Call -> CallScreen(store, speaker, speechEnv) { go(Route.Practice) }
                Route.Settings -> SettingsScreen(
                    store,
                    speechDebug = "Session engine: ${net.engineForSession} • " +
                        "Vosk model: ${if (vosk.isReady()) "ready" else "missing"} • " +
                        "Piper voice: ${if (piper.isReady()) "model present, engine pending espeak-ng" else "missing"}",
                    onBack = { go(Route.Profile) }
                )
                Route.Stats -> StatsScreen(
                    store,
                    onBack = { go(Route.Profile) },
                    onAct = { a ->
                        when (a) {
                            NextAction.REVIEW_DUE -> go(Route.Review)
                            NextAction.FIX_MISTAKES -> go(Route.Mistakes)
                            NextAction.CRUISING -> go(Route.Challenge)
                            NextAction.FIRST_STEPS, NextAction.DAILY_GOAL -> go(Route.Learn)
                            NextAction.KEEP_STREAK -> go(Route.Chat())
                        }
                    }
                )
                Route.Custom -> CustomLessonDialog(store, onClose = { go(Route.Practice) }, onOpen = { go(Route.Play(it)) })
                Route.Speed -> SpeedScreen(store, speaker) { go(Route.Practice) }
                Route.WordBank -> WordBankScreen(store, speaker) { go(Route.Home) }
                Route.Review -> ReviewScreen(store, speaker) { go(Route.Practice) }
                Route.FreeTalk -> FreeTalkScreen(store, speaker, speechEnv) { go(Route.Practice) }
                Route.Mistakes -> MistakesScreen(store, speaker, onBack = { go(Route.Practice) }, onPracticePhrase = {
                    val lesson = Lesson(
                        "mistakes", "📒", "Mistakes", "From your notebook", "Makosa",
                        store.mistakes.keys.mapNotNull { k -> ALL_PHRASES.find { it.key() == k } },
                        "u1"
                    )
                    if (lesson.phrases.isNotEmpty()) go(Route.Play(lesson)) else go(Route.Practice)
                })
                Route.Grammar -> GrammarScreen(store, speaker, onBack = { go(Route.Practice) }, onDrills = { go(Route.Drills) })
                Route.Challenge -> {
                    // Daily mixed challenge: 8 questions across completed lessons.
                    val done = allLessons().filter { (store.stars[it.id] ?: 0) >= 1 }
                    val source = done.ifEmpty { allLessons().take(3) }
                    val mixed = Lesson(
                        "challenge", "🎯",
                        "Défi du jour", "Daily challenge", "Changamoto ya leo",
                        source.shuffled().flatMap { it.phrases.shuffled().take(2) }.distinctBy { it.key() }.take(8),
                        "u1"
                    )
                    LessonScreen(store, speaker, speechEnv, mixed,
                        onExit = { go(Route.Practice) },
                        onGuide = { go(Route.StudyGuide(mixed.unitId)) })
                }
            }
            }
        }
        if (onTab) BottomNav(current = route, onSelect = { go(it) })
    }
    }
}

@Composable
private fun BottomNav(current: Route, onSelect: (Route) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Surface)
            .navigationBarsPadding()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TABS.forEach { tab ->
            val selected = current == tab.route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clip(Rad.md)
                    .background(if (selected) CobaltSoft else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onSelect(tab.route) }
                    .padding(vertical = 6.dp)
                    .semantics { contentDescription = tab.label; role = Role.Tab }
            ) {
                Text(tab.icon, fontSize = 18.sp)
                Text(
                    tab.label,
                    style = T.caption,
                    fontSize = 10.sp,
                    color = if (selected) Cobalt else InkMuted,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun SetupScreen(onDone: () -> Unit) {
    val ctx = LocalContext.current
    var progress by remember { mutableStateOf(ModelInstaller.Progress(0, 1)) }
    LaunchedEffect(Unit) {
        ModelInstaller.install(ctx) { progress = it }
        onDone()
    }
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Mascot(MascotMood.HAPPY, size = 120.dp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Setting up your offline French voice…",
            style = T.section, textAlign = TextAlign.Center, color = Ink
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "One-time setup — after this, everything works with no internet.",
            style = T.secondary, textAlign = TextAlign.Center, color = InkSoft
        )
        Spacer(Modifier.height(24.dp))
        LinearProgressIndicator(
            progress = { progress.fraction },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(Rad.pill),
            color = Cobalt
        )
    }
}
