package com.francofun

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * First-launch setup for the bundled offline speech assets (§9.3–9.4).
 * Copies `assets/vosk-model-fr` and `assets/piper-fr` into [context.filesDir]
 * on a background coroutine — both engines need real files on disk, not
 * compressed APK assets, to load.
 *
 * Resumable: each file is skipped when the destination already exists with
 * the same byte size, so killing the app mid-extraction (§10
 * interrupted-install pass) simply resumes on the next launch instead of
 * leaving a half-copied, unusable model.
 *
 * Reports determinate progress (bytes copied) for the one-time
 * "Setting up your offline French voice…" screen.
 */
object ModelInstaller {
    const val VOSK_ASSET_DIR = "vosk-model-fr"
    const val PIPER_ASSET_DIR = "piper-fr"

    fun voskDir(ctx: Context): File = File(ctx.filesDir, VOSK_ASSET_DIR)
    fun piperDir(ctx: Context): File = File(ctx.filesDir, PIPER_ASSET_DIR)

    /** True only when both model dirs exist and are non-empty. */
    fun modelsReady(ctx: Context): Boolean =
        voskDir(ctx).isReadyDir() && piperDir(ctx).isReadyDir()

    /**
     * True when the APK actually bundles the model assets (§9.3). The setup
     * screen is skipped entirely when false — the app then runs on system
     * voices, and the offline path activates on builds that ship models.
     */
    fun bundledModelsPresent(ctx: Context): Boolean {
        val vosk = runCatching { ctx.assets.list(VOSK_ASSET_DIR) }.getOrNull() ?: emptyArray()
        val piper = runCatching { ctx.assets.list(PIPER_ASSET_DIR) }.getOrNull() ?: emptyArray()
        return vosk.isNotEmpty() && piper.isNotEmpty()
    }

    private fun File.isReadyDir(): Boolean =
        isDirectory && (listFiles()?.isNotEmpty() == true)

    data class Progress(val bytesCopied: Long, val bytesTotal: Long) {
        val fraction: Float get() = if (bytesTotal <= 0) 1f else (bytesCopied.toFloat() / bytesTotal).coerceIn(0f, 1f)
    }

    suspend fun install(ctx: Context, onProgress: (Progress) -> Unit = {}): Unit = withContext(Dispatchers.IO) {
        val voskTotal = assetBytes(ctx, VOSK_ASSET_DIR)
        val total = voskTotal + assetBytes(ctx, PIPER_ASSET_DIR)
        // Phase progress is intra-dir bytes this run; skipped (already-present)
        // files count as done, so snap to the phase total when each dir finishes.
        copyDir(ctx, VOSK_ASSET_DIR, voskDir(ctx)) { onProgress(Progress(it, total)) }
        onProgress(Progress(voskTotal, total))
        copyDir(ctx, PIPER_ASSET_DIR, piperDir(ctx)) { onProgress(Progress(voskTotal + it, total)) }
        onProgress(Progress(total, total))
    }

    private fun assetBytes(ctx: Context, assetDir: String): Long {
        var sum = 0L
        fun walk(path: String) {
            val list = runCatching { ctx.assets.list(path) }.getOrNull() ?: emptyArray()
            if (list.isEmpty()) {
                sum += runCatching { ctx.assets.openFd(path).length }.getOrDefault(0L)
            } else list.forEach { walk(if (path.isEmpty()) it else "$path/$it") }
        }
        walk(assetDir)
        return sum
    }

    /** Returns bytes copied for this dir. Skips files already present at full size (resume). */
    private fun copyDir(ctx: Context, assetDir: String, dest: File, onDirProgress: (Long) -> Unit): Long {
        var copied = 0L
        fun walk(path: String, out: File) {
            val list = runCatching { ctx.assets.list(path) }.getOrNull() ?: emptyArray()
            if (list.isEmpty()) {
                val expected = runCatching { ctx.assets.openFd(path).length }.getOrDefault(-1L)
                if (expected >= 0 && out.exists() && out.length() == expected) return // resume: already done
                out.parentFile?.mkdirs()
                ctx.assets.open(path).use { input ->
                    out.outputStream().use { output -> input.copyTo(output) }
                }
                copied += if (expected >= 0) expected else out.length()
                onDirProgress(copied)
            } else {
                out.mkdirs()
                list.forEach { walk(if (path.isEmpty()) it else "$path/$it", File(out, it)) }
            }
        }
        walk(assetDir, dest)
        return copied
    }
}
