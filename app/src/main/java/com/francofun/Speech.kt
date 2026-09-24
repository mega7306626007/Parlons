package com.francofun

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.util.Locale
import java.util.UUID

/** French text-to-speech: Android system voice first, bundled Piper voice as silent fallback (§9.4). */
class Speaker(private val ctx: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(ctx.applicationContext, this)
    private var ready = false
    private var warned = false
    private var pending: String? = null
    private var systemFailed = false

    /** Bound by the app when offline models are installed; silent fallback path. */
    var offlineTts: PiperTts? = null

    /** Pinned per session from [NetConnectivity.engineForSession]: offline sessions prefer Piper. */
    var preferOffline: Boolean = false

    /** U6 capstone (§4.1): native-paced audio — normal rate instead of learner-slowed. */
    var nativeRate: Boolean = false

    init {
        // Short init timeout (§9.4): if the system engine doesn't come up, route to Piper.
        Handler(Looper.getMainLooper()).postDelayed({ if (!ready) systemFailed = true }, 1500)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val r = tts?.setLanguage(Locale.FRANCE)
            ready = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
            if (!ready) {
                systemFailed = true
            } else {
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) {}
                    override fun onDone(id: String?) { onDoneListener?.invoke(id ?: "") }
                    override fun onError(id: String?) {}
                })
                pending?.let { speak(it); pending = null }
            }
        } else {
            systemFailed = true
            pending?.let { speak(it); pending = null }
        }
    }

    var onDoneListener: ((String) -> Unit)? = null

    /** UI signal: true while TTS is actively speaking (chat/call/lesson voice states). */
    var isSpeaking: Boolean = false
        private set
    var onSpeakingChange: ((Boolean) -> Unit)? = null

    private fun setSpeaking(v: Boolean) {
        if (isSpeaking != v) {
            isSpeaking = v
            onSpeakingChange?.invoke(v)
        }
    }

    fun speak(text: String, slow: Boolean = false, onDone: (() -> Unit)? = null, tag: String = UUID.randomUUID().toString()) {
        // Offline-first sessions (or a failed system engine) prefer bundled Piper (§9.4).
        setSpeaking(true)
        if (preferOffline || systemFailed) {
            if (offlineTts?.speak(text, slow) { setSpeaking(false); onDone?.invoke() } == true) return
            // Piper unavailable: use the system voice if there is one, else stay silent
            // (no install nag in an offline session).
            if (!ready) { setSpeaking(false); return }
        }
        if (!ready) {
            pending = text
            setSpeaking(false)
            if (!warned) {
                warned = true
                Toast.makeText(ctx, "French voice not ready. Install French in Google Text-to-speech settings.", Toast.LENGTH_LONG).show()
                runCatching { ctx.startActivity(Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            }
            onDone?.let { onDoneListener = { setSpeaking(false); onDone() } }
            return
        }
        if (onDone != null || true) {
            val userDone = onDone
            onDoneListener = { id ->
                if (id.isEmpty() || id == tag) {
                    setSpeaking(false)
                    userDone?.invoke()
                }
            }
        }
        tts?.setSpeechRate(if (slow) 0.6f else if (nativeRate) 1.0f else 0.9f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, tag)
    }

    fun stop() {
        tts?.stop(); offlineTts?.stop()
        setSpeaking(false)
    }

    fun shutdown() {
        tts?.stop(); tts?.shutdown(); tts = null; offlineTts?.shutdown(); offlineTts = null
        setSpeaking(false)
    }
}

/** French speech recognition: Android system first, bundled Vosk as silent fallback (§9.4). */
class Listener(private val ctx: Context) {
    private var rec: SpeechRecognizer? = null
    private var active = false

    /** Bound by the app when offline models are installed; silent fallback path. */
    var offline: VoskEngine? = null

    /** Pinned per session from [NetConnectivity.engineForSession]: offline sessions go straight to Vosk. */
    var preferOffline: Boolean = false
    var allowOfflineFallback: Boolean = true

    private fun offlineReady(): Boolean = allowOfflineFallback && offline?.isReady() == true

    private fun startOffline(onResult: (String) -> Unit, onError: (String) -> Unit, onState: (Boolean) -> Unit) {
        stopInternal()
        active = true
        onState(true)
        offline?.listen(
            onText = { active = false; onState(false); onResult(it) },
            onError = { active = false; onState(false); onError(it) }
        )
    }

    fun start(onResult: (String) -> Unit, onError: (String) -> Unit, onState: (Boolean) -> Unit) {
        if (active) return
        if (preferOffline && offlineReady()) {
            startOffline(onResult, onError, onState)
            return
        }
        if (!SpeechRecognizer.isRecognitionAvailable(ctx)) {
            if (offlineReady()) {
                startOffline(onResult, onError, onState)
            } else {
                onError("Speech recognition isn't available on this phone.")
            }
            return
        }
        stopInternal()
        val r = SpeechRecognizer.createSpeechRecognizer(ctx.applicationContext)
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { active = true; onState(true) }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onError(error: Int) {
                // Network/server failures fail over to bundled Vosk silently (§9.4).
                if ((error == SpeechRecognizer.ERROR_NETWORK || error == SpeechRecognizer.ERROR_SERVER) && offlineReady()) {
                    destroy()
                    startOffline(onResult, onError, onState)
                    return
                }
                active = false; onState(false)
                destroy()
                onError(if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)
                    "Didn't catch that. Try again!" else "Mic error ($error). Check internet + mic.")
            }
            override fun onResults(results: Bundle?) {
                active = false; onState(false)
                val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                destroy()
                if (text.isNullOrBlank()) onError("Didn't catch that. Try again!") else onResult(text)
            }
        })
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        rec = r
        r.startListening(i)
    }

    private fun destroy() { rec?.destroy(); rec = null }
    private fun stopInternal() { runCatching { rec?.cancel(); rec?.destroy() }; rec = null; offline?.stop(); active = false }

    fun stop() { stopInternal() }
    fun isActive(): Boolean = active
}

class Mic(val listening: Boolean, val press: () -> Unit)

/** Shared offline speech handles, resolved once per session in [App]. */
data class SpeechEnv(val vosk: VoskEngine, val preferOffline: Boolean)

@Composable
fun rememberMic(
    onResult: (String) -> Unit,
    onErrorToast: Boolean = true,
    env: SpeechEnv? = null
): Mic {
    val ctx = LocalContext.current
    val listener = remember {
        Listener(ctx).apply {
            offline = env?.vosk
            preferOffline = env?.preferOffline == true
        }
    }
    var listening by remember { mutableStateOf(false) }
    val current by rememberUpdatedState(onResult)
    DisposableEffect(Unit) { onDispose { listener.stop() } }

    fun begin() {
        listener.start(
            onResult = { current(it) },
            onError = { if (onErrorToast) Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show() },
            onState = { listening = it }
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) begin() else Toast.makeText(ctx, "Mic permission is needed to practise speaking.", Toast.LENGTH_LONG).show()
    }
    return Mic(listening) {
        if (listening || listener.isActive()) {
            listener.stop(); listening = false
        } else if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            begin()
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}
