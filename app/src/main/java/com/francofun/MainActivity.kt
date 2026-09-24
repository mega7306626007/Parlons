package com.francofun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
            // §8: follow the system theme by default; manual override from Settings.
            val systemDark = isSystemInDarkTheme()
            ParlonsTheme(dark = if (store.followSystemTheme) systemDark else store.darkMode) { App(store) }
        }
    }
}

sealed interface Route {
    object Home : Route
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

@Composable
fun App(store: Store) {
    val ctx = LocalContext.current
    val speaker = remember { Speaker(ctx) }
    // Session-pinned speech engines (§9.4): resolved once, never mid-conversation.
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
    // One-time offline-voice setup (§9.4). Skipped when the APK ships no models.
    var setupDone by remember {
        mutableStateOf(!ModelInstaller.bundledModelsPresent(ctx) || ModelInstaller.modelsReady(ctx))
    }
    if (!setupDone) {
        SetupScreen { setupDone = true }
        return
    }
    var route by remember { mutableStateOf<Route>(if (store.onboarded) Route.Home else Route.Onboarding) }
    BackHandler(enabled = route != Route.Home && route != Route.Onboarding) { route = Route.Home }

    // §8: in-app text size scales fonts only (dp layout untouched).
    val baseDensity = LocalDensity.current
    val scaled = Density(baseDensity.density, fontScale = baseDensity.fontScale * store.textScale)
    // §Phase 10: short route crossfade; honors system reduce-motion.
    val motionMs = if (animationsOff(ctx)) 0 else 220
    CompositionLocalProvider(LocalDensity provides scaled) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()) {
        Crossfade(targetState = route, animationSpec = tween(motionMs), label = "route") { r ->
        when (r) {
            Route.Onboarding -> OnboardingScreen(store) { route = Route.Home }
            Route.Home -> HomeScreen(
                store,
                onLesson = { route = Route.Play(it) },
                onChat = { route = Route.Chat() },
                onChainChat = { route = Route.Chat(chain = true) },
                onMarathon = { route = Route.Marathon },
                onCall = { route = Route.Call },
                onSettings = { route = Route.Settings },
                onStats = { route = Route.Stats },
                onReview = { route = Route.Review },
                onCustom = { route = Route.Custom },
                onSpeed = { route = Route.Speed },
                onWordBank = { route = Route.WordBank }
            )
            is Route.Play -> LessonScreen(store, speaker, speechEnv, r.lesson) { route = Route.Home }
            is Route.Chat -> ChatScreen(store, speaker, speechEnv, chain = r.chain, onBack = { route = Route.Home }, onSettings = { route = Route.Settings }, onCall = { route = Route.Call })
            Route.Marathon -> {
                val hard = marathonPhrases(allLessons(), store.srs, maxLevel = store.levelCeiling())
                val lesson = Lesson("marathon", "🏃", "Marathon", "Hardest words", "Maneno magumu", hard, "u6")
                LessonScreen(store, speaker, speechEnv, lesson) { route = Route.Home }
            }
            Route.Call -> CallScreen(store, speaker, speechEnv) { route = Route.Home }
            Route.Settings -> SettingsScreen(
                store,
                speechDebug = "Session engine: ${net.engineForSession} • " +
                    "Vosk model: ${if (vosk.isReady()) "ready" else "missing"} • " +
                    "Piper voice: ${if (piper.isReady()) "model present, engine pending espeak-ng" else "missing"}",
                onBack = { route = Route.Home }
            )
            Route.Stats -> StatsScreen(store) { route = Route.Home }
            Route.Custom -> CustomLessonDialog(store, onClose = { route = Route.Home }, onOpen = { route = Route.Play(it) })
            Route.Speed -> SpeedScreen(store, speaker) { route = Route.Home }
            Route.WordBank -> WordBankScreen(store, speaker) { route = Route.Home }
            Route.Review -> {
                // Locked U6 lessons never leak into review before the capstone unlocks.
                val pool = allLessons().filter { it.unitId != "u6" || store.unitUnlocked("u6") }
                val dueAll = pool.flatMap { l -> duePhrases(l, store.srs, limit = 3, maxLevel = store.levelCeiling()) }.distinctBy { it.fr }.take(12)
                val lesson = if (dueAll.isNotEmpty()) Lesson("review", "🔁", "Révision", "Review", "Marudio", dueAll, "u1")
                else pool.randomOrNull() ?: LESSONS.first()
                LessonScreen(store, speaker, speechEnv, lesson) { route = Route.Home }
            }
        }
        }
    }
    }
}

/** One-time "Setting up your offline French voice…" screen (§9.4). */
@Composable
private fun SetupScreen(onDone: () -> Unit) {
    val ctx = LocalContext.current
    var progress by remember { mutableStateOf(ModelInstaller.Progress(0, 1)) }
    LaunchedEffect(Unit) {
        ModelInstaller.install(ctx) { progress = it }
        onDone()
    }
    PhotoBg(R.drawable.bg_offline) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🇫🇷", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Setting up your offline French voice…",
            fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "One-time setup — after this, everything works with no internet.",
            fontSize = 14.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(24.dp))
        LinearProgressIndicator(
            progress = { progress.fraction },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(Rad.pill)
        )
    }
    }
}
