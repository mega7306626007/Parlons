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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = Store(applicationContext)
        setContent {
            val systemDark = isSystemInDarkTheme()
            ParlonsTheme(dark = if (store.followSystemTheme) systemDark else store.darkMode) { App(store) }
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

    data class Play(val lesson: Lesson) : Route
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
}

private data class TabItem(val route: Route, val icon: String, val label: String)

private val TABS = listOf(
    TabItem(Route.Home, "🏠", "Home"),
    TabItem(Route.Learn, "📚", "Learn"),
    TabItem(Route.Practice, "💪", "Practice"),
    TabItem(Route.Words, "📖", "Words"),
    TabItem(Route.Profile, "👤", "Profile")
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
                    onProfile = { go(Route.Profile) }
                )
                Route.Learn -> LearnTab(store) { go(Route.Play(it)) }
                Route.Practice -> PracticeTab(
                    store,
                    onReview = { go(Route.Review) },
                    onChat = { go(Route.Chat()) },
                    onChainChat = { go(Route.Chat(chain = true)) },
                    onCall = { go(Route.Call) },
                    onSpeed = { go(Route.Speed) },
                    onCustom = { go(Route.Custom) },
                    onMarathon = { go(Route.Marathon) }
                )
                Route.Words -> WordBankScreen(store, speaker) { go(Route.Home) }
                Route.Profile -> ProfileTab(
                    store,
                    onStats = { go(Route.Stats) },
                    onSettings = { go(Route.Settings) }
                )
                is Route.Play -> LessonScreen(store, speaker, speechEnv, r.lesson) { go(Route.Learn) }
                is Route.Chat -> ChatScreen(store, speaker, speechEnv, chain = r.chain, onBack = { go(Route.Practice) }, onSettings = { go(Route.Settings) }, onCall = { go(Route.Call) })
                Route.Marathon -> {
                    val hard = marathonPhrases(allLessons(), store.srs, maxLevel = store.levelCeiling())
                    val lesson = Lesson("marathon", "🏃", "Marathon", "Hardest words", "Maneno magumu", hard, "u6")
                    LessonScreen(store, speaker, speechEnv, lesson) { go(Route.Practice) }
                }
                Route.Call -> CallScreen(store, speaker, speechEnv) { go(Route.Practice) }
                Route.Settings -> SettingsScreen(
                    store,
                    speechDebug = "Session engine: ${net.engineForSession} • " +
                        "Vosk model: ${if (vosk.isReady()) "ready" else "missing"} • " +
                        "Piper voice: ${if (piper.isReady()) "model present, engine pending espeak-ng" else "missing"}",
                    onBack = { go(Route.Profile) }
                )
                Route.Stats -> StatsScreen(store) { go(Route.Profile) }
                Route.Custom -> CustomLessonDialog(store, onClose = { go(Route.Practice) }, onOpen = { go(Route.Play(it)) })
                Route.Speed -> SpeedScreen(store, speaker) { go(Route.Practice) }
                Route.WordBank -> WordBankScreen(store, speaker) { go(Route.Home) }
                Route.Review -> ReviewScreen(store, speaker) { go(Route.Practice) }
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
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TABS.forEach { tab ->
            val selected = current == tab.route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(Rad.md)
                    .background(if (selected) CobaltSoft else androidx.compose.ui.graphics.Color.Transparent)
                    .clickable { onSelect(tab.route) }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .semantics { contentDescription = tab.label; role = Role.Tab }
            ) {
                Text(tab.icon, fontSize = 20.sp)
                Text(
                    tab.label,
                    style = T.caption,
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
