package com.francofun

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import android.util.Log
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.File
import java.util.EnumSet

/**
 * Bundled offline speech engines (§9.1, §9.4). Both are real, free, and
 * redistributable; both read models installed by [ModelInstaller] from
 * `context.filesDir` — never from the network.
 *
 * - [VoskEngine]: French speech recognition (`vosk-model-small-fr-0.22`).
 * - [PiperTts]: French speech synthesis (`fr_FR-siwis-medium` + ONNX Runtime).
 */
object OfflineEngines {
    private const val TAG = "OfflineEngines"
    const val SAMPLE_RATE = 16000
}

/** Grapheme-to-phoneme front-end for Piper (§9.4). */
interface Phonemizer {
    /** Returns Piper phoneme ids for the text. */
    fun phonemize(text: String): LongArray
}

/**
 * Builder step: back this with an espeak-ng JNI module (Piper's standard
 * phonemizer). Until then [PiperTts.speak] reports `false` and callers keep
 * using system TTS — the offline TTS path activates automatically once a
 * phonemizer is bound, with no caller changes.
 */
object UnavailablePhonemizer : Phonemizer {
    override fun phonemize(text: String): LongArray =
        throw UnsupportedOperationException("espeak-ng JNI not bundled yet (see §9.3 note)")
}

/** Offline French STT via Vosk. Same result-callback shape as [Listener]. */
class VoskEngine(private val ctx: Context) {
    private var model: Model? = null
    private var service: SpeechService? = null
    private var timeout: Runnable? = null
    private val main = Handler(Looper.getMainLooper())

    fun isReady(): Boolean {
        val d = ModelInstaller.voskDir(ctx)
        if (!d.isDirectory) return false
        // A half-copied model dir is non-empty but unusable — validate shape:
        // vosk-model-small-fr contains conf/model.conf + am/final.mdl + graph.
        // Accept either the marker files or a credible size (>=5 files, >=1MB).
        var count = 0
        var bytes = 0L
        var hasConf = false
        var hasAm = false
        d.walkTopDown().forEach { f ->
            if (f.isFile) {
                count++
                bytes += f.length()
                val rel = f.relativeTo(d).invariantSeparatorsPath
                if (rel == "conf/model.conf" || rel.endsWith("model.conf")) hasConf = true
                if (rel == "am/final.mdl" || rel.endsWith("final.mdl")) hasAm = true
            }
        }
        if (hasConf && hasAm) return true
        return count >= 5 && bytes >= 1_000_000L
    }

    private fun ensureModel(): Boolean {
        if (model != null) return true
        if (!isReady()) return false
        return runCatching {
            model = Model(ModelInstaller.voskDir(ctx).absolutePath)
            true
        }.onFailure { Log.w("VoskEngine", "model load failed", it) }.getOrDefault(false)
    }

    fun listen(onText: (String) -> Unit, onError: (String) -> Unit, timeoutMs: Long = 12_000) {
        stop()
        if (!ensureModel()) {
            onError("Offline voice model missing.")
            return
        }
        try {
            val rec = Recognizer(model, OfflineEngines.SAMPLE_RATE.toFloat())
            val svc = SpeechService(rec, OfflineEngines.SAMPLE_RATE.toFloat())
            service = svc
            val to = Runnable {
                stop()
                onError("Didn't catch that. Try again!")
            }
            timeout = to
            main.postDelayed(to, timeoutMs)
            // SpeechService expects main-thread calls.
            main.post {
                svc.startListening(object : RecognitionListener {
                    override fun onPartialResult(hypothesis: String) = Unit
                    override fun onResult(hypothesis: String) = Unit
                    private fun finish(hypothesis: String) {
                        timeout?.let { main.removeCallbacks(it) }
                        val text = runCatching { JSONObject(hypothesis).optString("text", "") }.getOrDefault("")
                        stop()
                        if (text.isBlank()) onError("Didn't catch that. Try again!") else onText(text)
                    }
                    override fun onFinalResult(hypothesis: String) = finish(hypothesis)
                    override fun onError(e: Exception) {
                        timeout?.let { main.removeCallbacks(it) }
                        stop()
                        onError("Didn't catch that. Try again!")
                    }
                    override fun onTimeout() {
                        timeout?.let { main.removeCallbacks(it) }
                        stop()
                        onError("Didn't catch that. Try again!")
                    }
                })
            }
        } catch (e: Exception) {
            Log.w("VoskEngine", "listen failed", e)
            stop()
            onError("Didn't catch that. Try again!")
        }
    }

    fun stop() {
        timeout?.let { main.removeCallbacks(it) }
        timeout = null
        runCatching { service?.stop() }
        runCatching { service?.shutdown() }
        service = null
    }
}

/** Offline French TTS via Piper voice + ONNX Runtime (§9.1). */
class PiperTts(private val ctx: Context) {
    var phonemizer: Phonemizer? = null

    @Volatile private var env: OrtEnvironment? = null
    @Volatile private var session: OrtSession? = null
    @Volatile private var sampleRate = 22050
    private var track: AudioTrack? = null

    fun onnxFile(): File = File(ModelInstaller.piperDir(ctx), "voice.onnx")
    fun configFile(): File = File(ModelInstaller.piperDir(ctx), "voice.onnx.json")

    fun isReady(): Boolean {
        // Both files must exist AND be non-trivial — a 0-byte half-copy is "missing".
        val o = onnxFile()
        val c = configFile()
        return o.isFile && o.length() > 1024 && c.isFile && c.length() > 16
    }

    private fun ensureSession(): Boolean {
        if (session != null) return true
        if (!isReady()) return false
        return runCatching {
            sampleRate = runCatching {
                JSONObject(configFile().readText()).optInt("audio_sample_rate", 22050)
            }.getOrDefault(22050)
            val e = OrtEnvironment.getEnvironment()
            env = e
            session = e.createSession(onnxFile().absolutePath, OrtSession.SessionOptions())
            true
        }.onFailure { Log.w("PiperTts", "session load failed", it) }.getOrDefault(false)
    }

    /**
     * Synthesizes [text] to the speaker. Returns false when synthesis isn't
     * possible (no phonemizer / model missing) so callers fall back.
     */
    fun speak(text: String, slow: Boolean = false, onDone: (() -> Unit)? = null): Boolean {
        val ph = phonemizer ?: return false
        if (!ensureSession()) return false
        val ids: LongArray = runCatching { ph.phonemize(text) }.getOrElse { return false }
        if (ids.isEmpty()) return false
        return runCatching {
            stop()
            val e = env ?: return false
            val s = session ?: return false
            // Piper v1 inputs: phoneme ids, lengths, scales(noise, length, noise_w).
            val lengths = longArrayOf(ids.size.toLong())
            val scales = floatArrayOf(0.667f, if (slow) 1.25f else 1.0f, 0.8f)
            val wav: FloatArray = OnnxTensor.createTensor(e, java.nio.LongBuffer.wrap(ids), longArrayOf(1, ids.size.toLong())).use { tIds ->
                OnnxTensor.createTensor(e, java.nio.LongBuffer.wrap(lengths), longArrayOf(1)).use { tLen ->
                    OnnxTensor.createTensor(e, java.nio.FloatBuffer.wrap(scales), longArrayOf(1, 3)).use { tScales ->
                        s.run(mapOf("input" to tIds, "input_lengths" to tLen, "scales" to tScales)).use { out ->
                            @Suppress("UNCHECKED_CAST")
                            (((out[0].value as Array<*>)[0] as FloatArray)).copyOf()
                        }
                    }
                }
            }
            playPcm(wav, onDone)
            true
        }.onFailure { Log.w("PiperTts", "synth failed", it) }.getOrDefault(false)
    }

    private fun playPcm(wav: FloatArray, onDone: (() -> Unit)?) {
        val pcm = ShortArray(wav.size) { i -> (wav[i].coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort() }
        val t = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(pcm.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track = t
        t.write(pcm, 0, pcm.size)
        t.setNotificationMarkerPosition(pcm.size)
        t.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack) {
                track.stop(); track.release()
                onDone?.invoke()
            }
            override fun onPeriodicNotification(track: AudioTrack) = Unit
        })
        t.play()
    }

    fun stop() {
        runCatching { track?.stop() }
        runCatching { track?.release() }
        track = null
    }

    fun shutdown() {
        stop()
        runCatching { session?.close() }
        runCatching { env?.close() }
        session = null
        env = null
    }
}
