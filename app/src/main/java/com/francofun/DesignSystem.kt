package com.francofun

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/* ═══════════════════════════════════════════════
   DESIGN SYSTEM — extended components
   Clean, open layouts. Minimal cards.
   One coherent visual language.
   ═══════════════════════════════════════════════ */

/** Section header — open typography, not a card. */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, action: @Composable (() -> Unit)? = null) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = Sp.xs, vertical = Sp.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = T.section, color = Ink, modifier = Modifier.weight(1f))
        action?.invoke()
    }
}

/** Card surface — white, rounded, subtle border. Used sparingly for meaningful groups. */
@Composable
fun Card(modifier: Modifier = Modifier, elevation: Dp = 1.dp, content: @Composable () -> Unit) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(Rad.xl)
            .background(Surface)
            .border(1.dp, Border, Rad.xl)
            .padding(Sp.md)
    ) { content() }
}

/** Hero card — bold color block for primary CTAs (not a photo). */
@Composable
fun HeroCard(
    color: Color = Cobalt,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val base = modifier
        .fillMaxWidth()
        .clip(Rad.xl)
        .background(
            Brush.linearGradient(
                listOf(color, color.copy(alpha = 0.82f))
            )
        )
    val clickable = if (onClick != null) base.clickable(onClick = onClick) else base
    Box(clickable.padding(Sp.lg)) { content() }
}

/** Badge count in header. */
@Composable
fun BadgeCount(icon: String, count: Int, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(4.dp))
        Text("$count", style = T.label, color = Ink)
    }
}

/** Voice waveform — reacts while active (real listening indicator). */
@Composable
fun VoiceWaveform(active: Boolean, barCount: Int = 24, modifier: Modifier = Modifier, color: Color = Turquoise) {
    var phase by remember { mutableStateOf(0f) }
    val transition = rememberInfiniteTransition(label = "wave")
    val anim by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "wavePhase"
    )
    if (active) {
        // continuous phase advance
        val t = rememberInfiniteTransition(label = "wave2")
        val p by t.animateFloat(
            initialValue = 0f, targetValue = 6.28f,
            animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
            label = "p"
        )
        phase = p
    }
    Row(
        modifier.fillMaxWidth().height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { i ->
            val h = if (active) {
                val base = sin(phase + i * 0.55f) * 0.5f + 0.5f
                (6f + base * 22f * (0.5f + anim * 0.5f))
            } else 4f
            Box(
                Modifier.width(3.dp).height(h.dp).clip(Rad.sm)
                    .background(if (active) color else Border)
            )
        }
    }
}

/** Divider line. */
@Composable
fun Divider(modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Border).then(modifier))
}

/** Empty state with optional mascot. */
@Composable
fun EmptyState(icon: String, message: String) {
    Column(
        Modifier.fillMaxSize().padding(Sp.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(icon, fontSize = 56.sp)
        Spacer(Modifier.padding(Sp.md))
        Text(message, style = T.body, color = InkMuted, textAlign = TextAlign.Center)
    }
}

/* ═══════════════════════════════════════════════
   MASCOT — "Simba" original Compose-drawn character
   Friendly geometric lion. Not Duo. Not stock art.
   Strategic use: feedback, empty states, celebration, voice.
   ═══════════════════════════════════════════════ */

enum class MascotMood {
    HAPPY, EXCITED, THINKING, CONFUSED, ENCOURAGING,
    CELEBRATING, LISTENING, SPEAKING, SAD, SLEEPING
}

@Composable
fun Mascot(
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 96.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mascot")
    val bob by transition.animateFloat(
        initialValue = -2f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "bob"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "breathe"
    )

    Canvas(
        modifier
            .size(size)
            .graphicsLayer { translationY = bob; scaleX = breathe; scaleY = breathe }
    ) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f + h * 0.04f
        val bodyR = w * 0.32f
        val headR = w * 0.26f
        val headCy = cy - h * 0.16f

        // Tail (behind body)
        if (mood != MascotMood.SLEEPING && mood != MascotMood.SAD) {
            val tailPath = Path()
            val tailStartX = cx + bodyR * 0.8f
            val tailStartY = cy + h * 0.08f
            tailPath.moveTo(tailStartX, tailStartY)
            tailPath.quadraticBezierTo(
                tailStartX + w * 0.22f, tailStartY - h * 0.10f,
                tailStartX + w * 0.16f, tailStartY - h * 0.26f
            )
            drawPath(tailPath, Gold, style = Stroke(w * 0.05f, cap = StrokeCap.Round))
            drawCircle(Gold, w * 0.055f, Offset(tailStartX + w * 0.16f, tailStartY - h * 0.26f))
        }

        // Mane — warm gold petals
        for (i in 0 until 12) {
            val angle = (i * 30.0) * (Math.PI / 180.0)
            val mx = cx + (cos(angle) * headR * 1.05f).toFloat()
            val my = headCy + (sin(angle) * headR * 1.05f).toFloat()
            drawCircle(color = Gold.copy(alpha = 0.95f), radius = headR * 0.40f, center = Offset(mx, my))
        }
        drawCircle(color = Gold, radius = headR * 1.18f, center = Offset(cx, headCy))

        // Body
        drawCircle(color = Cobalt, radius = bodyR, center = Offset(cx, cy + h * 0.14f))
        drawCircle(color = CobaltSoft, radius = bodyR * 0.55f, center = Offset(cx, cy + h * 0.18f))

        // Head base
        drawCircle(color = Color(0xFFFFE0B2), radius = headR, center = Offset(cx, headCy))

        // Ears
        drawCircle(color = Color(0xFFFFE0B2), radius = headR * 0.34f, center = Offset(cx - headR * 0.78f, headCy - headR * 0.72f))
        drawCircle(color = Color(0xFFFFE0B2), radius = headR * 0.34f, center = Offset(cx + headR * 0.78f, headCy - headR * 0.72f))
        drawCircle(color = Pink, radius = headR * 0.16f, center = Offset(cx - headR * 0.78f, headCy - headR * 0.72f))
        drawCircle(color = Pink, radius = headR * 0.16f, center = Offset(cx + headR * 0.78f, headCy - headR * 0.72f))

        // Muzzle
        drawCircle(color = Color(0xFFFFF3E0), radius = headR * 0.70f, center = Offset(cx, headCy + headR * 0.18f))

        // Eyes
        val eyeY = headCy - headR * 0.06f
        val eyeLx = cx - headR * 0.34f
        val eyeRx = cx + headR * 0.34f
        val eyeR = headR * 0.16f
        val pupilR = headR * 0.08f

        when (mood) {
            MascotMood.SLEEPING -> {
                drawArc(
                    color = Ink, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(eyeLx - eyeR, eyeY - eyeR * 0.2f),
                    size = Size(eyeR * 2, eyeR * 1.3f),
                    style = Stroke(3f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Ink, startAngle = 200f, sweepAngle = 140f, useCenter = false,
                    topLeft = Offset(eyeRx - eyeR, eyeY - eyeR * 0.2f),
                    size = Size(eyeR * 2, eyeR * 1.3f),
                    style = Stroke(3f, cap = StrokeCap.Round)
                )
            }
            MascotMood.THINKING, MascotMood.CONFUSED -> {
                drawCircle(color = White, radius = eyeR, center = Offset(eyeLx, eyeY))
                drawCircle(color = White, radius = eyeR, center = Offset(eyeRx, eyeY))
                val look = if (mood == MascotMood.CONFUSED) -eyeR * 0.35f else eyeR * 0.2f
                drawCircle(color = Ink, radius = pupilR, center = Offset(eyeLx + look, eyeY - eyeR * 0.3f))
                drawCircle(color = Ink, radius = pupilR, center = Offset(eyeRx + look, eyeY - eyeR * 0.3f))
            }
            else -> {
                drawCircle(color = White, radius = eyeR, center = Offset(eyeLx, eyeY))
                drawCircle(color = White, radius = eyeR, center = Offset(eyeRx, eyeY))
                drawCircle(color = Ink, radius = pupilR, center = Offset(eyeLx, eyeY))
                drawCircle(color = Ink, radius = pupilR, center = Offset(eyeRx, eyeY))
                drawCircle(color = White, radius = pupilR * 0.35f, center = Offset(eyeLx + pupilR * 0.3f, eyeY - pupilR * 0.3f))
                drawCircle(color = White, radius = pupilR * 0.35f, center = Offset(eyeRx + pupilR * 0.3f, eyeY - pupilR * 0.3f))
            }
        }

        // Eyebrows
        if (mood == MascotMood.CONFUSED || mood == MascotMood.SAD) {
            drawLine(Ink, Offset(eyeLx - eyeR, eyeY - eyeR * 1.7f), Offset(eyeLx + eyeR, eyeY - eyeR * 2.0f), 3f, StrokeCap.Round)
            drawLine(Ink, Offset(eyeRx - eyeR, eyeY - eyeR * 2.0f), Offset(eyeRx + eyeR, eyeY - eyeR * 1.7f), 3f, StrokeCap.Round)
        }

        // Nose
        val noseY = headCy + headR * 0.24f
        drawCircle(color = Color(0xFF8D6E63), radius = headR * 0.10f, center = Offset(cx, noseY))

        // Mouth
        val mouthY = noseY + headR * 0.24f
        val mouthPath = Path()
        when (mood) {
            MascotMood.HAPPY, MascotMood.EXCITED, MascotMood.CELEBRATING, MascotMood.ENCOURAGING -> {
                mouthPath.moveTo(cx - headR * 0.36f, mouthY)
                mouthPath.quadraticBezierTo(cx, mouthY + headR * 0.40f, cx + headR * 0.36f, mouthY)
                drawPath(mouthPath, Ink, style = Stroke(3.5f, cap = StrokeCap.Round))
                if (mood == MascotMood.EXCITED || mood == MascotMood.CELEBRATING) {
                    val open = Path()
                    open.moveTo(cx - headR * 0.30f, mouthY)
                    open.quadraticBezierTo(cx, mouthY + headR * 0.52f, cx + headR * 0.30f, mouthY)
                    open.close()
                    drawPath(open, Color(0xFFE57373))
                }
            }
            MascotMood.SAD, MascotMood.CONFUSED -> {
                mouthPath.moveTo(cx - headR * 0.28f, mouthY + headR * 0.14f)
                mouthPath.quadraticBezierTo(cx, mouthY - headR * 0.16f, cx + headR * 0.28f, mouthY + headR * 0.14f)
                drawPath(mouthPath, Ink, style = Stroke(3f, cap = StrokeCap.Round))
            }
            MascotMood.SPEAKING -> {
                drawOval(
                    color = Color(0xFFE57373),
                    topLeft = Offset(cx - headR * 0.18f, mouthY - headR * 0.06f),
                    size = Size(headR * 0.36f, headR * 0.32f)
                )
            }
            else -> {
                drawLine(Ink, Offset(cx - headR * 0.18f, mouthY), Offset(cx + headR * 0.18f, mouthY), 3f, StrokeCap.Round)
            }
        }

        // Blush
        if (mood == MascotMood.HAPPY || mood == MascotMood.EXCITED || mood == MascotMood.CELEBRATING) {
            drawCircle(Pink.copy(alpha = 0.35f), headR * 0.14f, Offset(cx - headR * 0.55f, noseY + headR * 0.05f))
            drawCircle(Pink.copy(alpha = 0.35f), headR * 0.14f, Offset(cx + headR * 0.55f, noseY + headR * 0.05f))
        }
    }
}

/* ── Small decorative shapes for playful backgrounds ── */

/** Soft floating blobs — decorative, not photo wallpapers. */
@Composable
fun PlayfulBlobs(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(CobaltSoft.copy(alpha = 0.55f), radius = w * 0.35f, center = Offset(w * 0.85f, h * 0.12f))
        drawCircle(VioletSoft.copy(alpha = 0.50f), radius = w * 0.28f, center = Offset(w * 0.08f, h * 0.78f))
        drawCircle(GoldSoft.copy(alpha = 0.55f), radius = w * 0.22f, center = Offset(w * 0.90f, h * 0.88f))
        drawCircle(TurquoiseSoft.copy(alpha = 0.40f), radius = w * 0.18f, center = Offset(w * 0.15f, h * 0.18f))
    }
}
