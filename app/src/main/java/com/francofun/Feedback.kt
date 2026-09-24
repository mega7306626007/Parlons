package com.francofun

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.random.Random

/** Sound effects (no assets needed) + haptics. Respects store.soundOn. */
object Sounds {
    private var tone: ToneGenerator? = null
    fun ok(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
            tone?.startTone(ToneGenerator.TONE_CDMA_PIP, 120)
        }
    }
    fun bad(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
            tone?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200)
        }
    }
    fun fanfare(enabled: Boolean) {
        if (!enabled) return
        runCatching {
            if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
            tone?.startTone(ToneGenerator.TONE_CDMA_ANSWER, 300)
        }
    }
}

fun buzz(ctx: Context) {
    runCatching {
        if (Build.VERSION.SDK_INT >= 31) {
            val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val v = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            @Suppress("DEPRECATION") v.vibrate(40)
        }
    }
}

// System "reduce motion" (§8): when the animator scale is off, decorative
// animation stays off too — functional transitions keep working.
fun animationsOff(ctx: Context): Boolean = runCatching {
    android.provider.Settings.Global.getFloat(
        ctx.contentResolver, android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1f
    ) == 0f
}.getOrDefault(false)

// Simple animated confetti overlay for perfect scores
@Composable
fun ConfettiOverlay(show: Boolean) {
    if (!show) return
    var tick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { while (true) { delay(120); tick++ } }
    val colors = listOf(Cobalt, Coral, Emerald, Gold, Violet, Turquoise, Pink)
    val parts = remember { List(60) { Random.nextFloat() to Random.nextFloat() } }
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            parts.forEachIndexed { i, (x, y) ->
                val yy = ((y + tick * 0.03f + x) % 1f) * size.height
                drawCircle(colors[i % colors.size], radius = 10f, center = androidx.compose.ui.geometry.Offset(x * size.width, yy))
            }
        }
    }
}
