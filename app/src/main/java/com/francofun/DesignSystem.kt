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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    // Single transition; phase derived directly (never write state during composition).
    val transition = rememberInfiniteTransition(label = "wave")
    val anim by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "wavePhase"
    )
    val phase by transition.animateFloat(
        initialValue = 0f, targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "p"
    )
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

/** Polished inline loading state — spinner + label, screen-reader announced. */
@Composable
fun LoadingRow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Cobalt,
    textColor: Color = InkSoft
) {
    Row(
        modifier.semantics { contentDescription = text },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Sp.xs)
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = color
        )
        Text(text, style = T.caption, color = textColor)
    }
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
   MASCOT — "Simba" adult lion emblem
   Mature minimal head-badge: layered bronze mane, calm amber
   gaze with real blinking. No cartoon body, no blush.
   Strategic use: feedback, empty states, celebration, voice.
   ═══════════════════════════════════════════════ */

enum class MascotMood {
    HAPPY, EXCITED, THINKING, CONFUSED, ENCOURAGING,
    CELEBRATING, LISTENING, SPEAKING, SAD, SLEEPING
}

/**
 * Simba — a REAL lion photo bundled in the app (fully offline): slow cinematic
 * zoom + drift over the portrait, with a mood grade for every classic Simba
 * mood (warm gold when happy, dimmed when sad/sleeping, cool blue thinking,
 * teal listening/speaking). All 10 moods, any size, screen-reader label.
 */
@Composable
fun Mascot(
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 96.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "simbaLive")
    val zoom by transition.animateFloat(
        initialValue = 1f, targetValue = 1.14f,
        animationSpec = infiniteRepeatable(tween(4200), RepeatMode.Reverse),
        label = "zoom"
    )
    val driftX by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5200), RepeatMode.Reverse),
        label = "driftX"
    )
    val driftY by transition.animateFloat(
        initialValue = 1f, targetValue = -1f,
        animationSpec = infiniteRepeatable(tween(6000), RepeatMode.Reverse),
        label = "driftY"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.99f, targetValue = 1.01f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "breathe"
    )
    val grade = when (mood) {
        MascotMood.HAPPY, MascotMood.EXCITED, MascotMood.CELEBRATING, MascotMood.ENCOURAGING ->
            Color(0xFFFFB400).copy(alpha = 0.10f)
        MascotMood.SAD, MascotMood.SLEEPING ->
            Color(0xFF0B1020).copy(alpha = 0.45f)
        MascotMood.THINKING, MascotMood.CONFUSED ->
            Color(0xFF1E4FE0).copy(alpha = 0.12f)
        MascotMood.LISTENING, MascotMood.SPEAKING ->
            Color(0xFF0D9488).copy(alpha = 0.12f)
    }
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .border(3.dp, Color(0xFF6B3A05), CircleShape)
            .semantics { contentDescription = "Simba the lion, ${mood.name.lowercase()}"; role = Role.Image },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.simba_lion),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().graphicsLayer {
                scaleX = zoom * breathe
                scaleY = zoom * breathe
                translationX = driftX * 10f
                translationY = driftY * 10f
            },
            contentScale = ContentScale.Crop
        )
        Box(Modifier.fillMaxSize().background(grade))
    }
}

/**
 * Simba — anime lion in the spirit of The Lion King: sweeping layered mane,
 * large expressive amber eyes, strong muzzle. Fully drawn (works offline),
 * gently alive: breathing drift + real blinking. Offline fallback for [Mascot].
 */
@Composable
private fun MascotAnime(
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 96.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "simbaAnime")
    val bob by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1700), RepeatMode.Reverse),
        label = "bob"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.985f, targetValue = 1.015f,
        animationSpec = infiniteRepeatable(tween(2100), RepeatMode.Reverse),
        label = "breathe"
    )
    val blinkCycle by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3800), RepeatMode.Restart),
        label = "blink"
    )
    val blinking = blinkCycle < 0.045f && mood != MascotMood.SLEEPING

    Canvas(
        modifier
            .size(size)
            .graphicsLayer { translationY = bob; scaleX = breathe; scaleY = breathe }
            .semantics { contentDescription = "Simba the lion, ${mood.name.lowercase()}"; role = Role.Image }
    ) {
        val w = this.size.width
        val cx = w / 2f
        val cy = w / 2f
        val headR = w * 0.27f

        val maneDeep = Color(0xFF4A1E06)
        val mane = Color(0xFF7E3F0D)
        val maneHi = Color(0xFFB96A1B)
        val maneTip = Color(0xFFE8A83E)
        val face = Color(0xFFE7B86E)
        val muzzleC = Color(0xFFD29A52)
        val earIn = Color(0xFF8A4B12)
        val liner = Color(0xFF2A1708)
        val noseC = Color(0xFF2E1A0C)

        // 1. Flowing mane — 12 long swept blades, alternating lengths.
        for (i in 0 until 12) {
            val a0 = ((i * 30.0 + 12.0) * Math.PI / 180.0)
            val dirX = cos(a0).toFloat()
            val dirY = sin(a0).toFloat()
            val perpX = -dirY
            val perpY = dirX
            val rBase = headR * 0.95f
            val len = if (i % 2 == 0) headR * 1.72f else headR * 1.52f
            val halfW = headR * 0.17f
            val sweep = 0.30f
            val blade = Path().apply {
                moveTo(cx + dirX * rBase - perpX * halfW, cy + dirY * rBase - perpY * halfW)
                quadraticBezierTo(
                    cx + (dirX * 0.5f + perpX * sweep) * (rBase + len) * 0.62f,
                    cy + (dirY * 0.5f + perpY * sweep) * (rBase + len) * 0.62f,
                    cx + dirX * len + perpX * sweep * headR * 0.9f,
                    cy + dirY * len + perpY * sweep * headR * 0.9f
                )
                quadraticBezierTo(
                    cx + (dirX * 0.5f - perpX * sweep) * (rBase + len) * 0.62f,
                    cy + (dirY * 0.5f - perpY * sweep) * (rBase + len) * 0.62f,
                    cx + dirX * rBase + perpX * halfW,
                    cy + dirY * rBase + perpY * halfW
                )
                close()
            }
            drawPath(blade, if (i % 2 == 0) maneDeep else mane)
        }
        // 2. Mane body + golden inner ring.
        drawCircle(maneDeep, headR * 1.28f, Offset(cx, cy))
        drawCircle(mane, headR * 1.14f, Offset(cx, cy))
        drawCircle(maneHi, headR * 1.02f, Offset(cx, cy))
        // Mane highlight arcs (sunlit rim, Lion-King glow).
        drawArc(
            maneTip, startAngle = 200f, sweepAngle = 140f, useCenter = false,
            topLeft = Offset(cx - headR * 1.14f, cy - headR * 1.14f),
            size = Size(headR * 2.28f, headR * 2.28f),
            style = Stroke(headR * 0.10f, cap = StrokeCap.Round)
        )

        // 3. Rounded anime ears with inner detail.
        listOf(-1f, 1f).forEach { side ->
            val ex = cx + side * headR * 0.58f
            val ey = cy - headR * 0.66f
            drawCircle(mane, headR * 0.30f, Offset(ex, ey))
            drawCircle(earIn, headR * 0.17f, Offset(ex, ey + headR * 0.03f))
        }

        // 4. Face + cheek ruff strokes.
        drawCircle(face, headR, Offset(cx, cy))
        listOf(-1f, 1f).forEach { side ->
            for (k in 0..2) {
                drawLine(
                    mane, Offset(cx + side * headR * 0.86f, cy + headR * (0.18f + k * 0.12f)),
                    Offset(cx + side * headR * 1.04f, cy + headR * (0.24f + k * 0.12f)),
                    headR * 0.05f, StrokeCap.Round
                )
            }
        }

        // 5. Anime eyes — big almond whites, amber irises, tall pupils, twin highlights.
        val eyeY = cy - headR * 0.12f
        val eyeLx = cx - headR * 0.38f
        val eyeRx = cx + headR * 0.38f
        val eyeA = headR * 0.26f
        val eyeB = when (mood) {
            MascotMood.THINKING, MascotMood.CONFUSED -> headR * 0.10f
            MascotMood.SLEEPING -> 0f
            MascotMood.LISTENING, MascotMood.SPEAKING -> headR * 0.17f
            else -> headR * 0.15f
        }
        val gazeDx = when (mood) {
            MascotMood.CONFUSED -> -eyeA * 0.3f
            MascotMood.THINKING -> eyeA * 0.25f
            else -> 0f
        }
        fun upperLid(lx: Float) = Path().apply {
            moveTo(lx - eyeA * 1.08f, eyeY)
            quadraticBezierTo(lx, eyeY - eyeB * 2.4f, lx + eyeA * 1.08f, eyeY)
        }
        fun almond(lx: Float) = Path().apply {
            moveTo(lx - eyeA, eyeY)
            quadraticBezierTo(lx, eyeY - eyeB * 2f, lx + eyeA, eyeY)
            quadraticBezierTo(lx, eyeY + eyeB * 2f, lx - eyeA, eyeY)
            close()
        }
        if (mood == MascotMood.SLEEPING || blinking) {
            drawLine(liner, Offset(eyeLx - eyeA, eyeY), Offset(eyeLx + eyeA, eyeY), 3.5f, StrokeCap.Round)
            drawLine(liner, Offset(eyeRx - eyeA, eyeY), Offset(eyeRx + eyeA, eyeY), 3.5f, StrokeCap.Round)
        } else {
            listOf(eyeLx, eyeRx).forEach { lx ->
                drawPath(almond(lx), Color(0xFFFFFBF0))
                drawCircle(Color(0xFFC07F1B), eyeA * 0.60f, Offset(lx + gazeDx, eyeY))
                drawOval(
                    liner,
                    topLeft = Offset(lx + gazeDx - eyeA * 0.13f, eyeY - eyeB * 1.15f),
                    size = Size(eyeA * 0.26f, eyeB * 2.3f)
                )
                drawCircle(Color.White, eyeA * 0.20f, Offset(lx + gazeDx - eyeA * 0.18f, eyeY - eyeB * 0.6f))
                drawCircle(Color.White, eyeA * 0.09f, Offset(lx + gazeDx + eyeA * 0.16f, eyeY + eyeB * 0.5f))
                drawPath(upperLid(lx), liner, style = Stroke(headR * 0.045f, cap = StrokeCap.Round))
            }
        }

        // 6. Strong angled brows.
        val browC = liner.copy(alpha = 0.9f)
        drawLine(browC, Offset(eyeLx - eyeA * 1.1f, eyeY - eyeB * 3.4f), Offset(eyeLx + eyeA, eyeY - eyeB * 2.3f), headR * 0.07f, StrokeCap.Round)
        drawLine(browC, Offset(eyeRx - eyeA, eyeY - eyeB * 2.3f), Offset(eyeRx + eyeA * 1.1f, eyeY - eyeB * 3.4f), headR * 0.07f, StrokeCap.Round)

        // 7. Leather nose with shine + philtrum.
        val noseY = cy + headR * 0.20f
        drawPath(
            Path().apply {
                moveTo(cx - headR * 0.17f, noseY)
                quadraticBezierTo(cx, noseY - headR * 0.06f, cx + headR * 0.17f, noseY)
                quadraticBezierTo(cx + headR * 0.10f, noseY + headR * 0.16f, cx, noseY + headR * 0.17f)
                quadraticBezierTo(cx - headR * 0.10f, noseY + headR * 0.16f, cx - headR * 0.17f, noseY)
                close()
            },
            noseC
        )
        drawCircle(Color.White.copy(alpha = 0.35f), headR * 0.035f, Offset(cx - headR * 0.07f, noseY + headR * 0.02f))
        val mouthY = noseY + headR * 0.17f
        drawLine(liner, Offset(cx, mouthY - headR * 0.12f), Offset(cx, mouthY), 2.5f, StrokeCap.Round)

        // 8. Mouth per mood.
        val mp = Path()
        when (mood) {
            MascotMood.HAPPY, MascotMood.EXCITED, MascotMood.ENCOURAGING -> {
                mp.moveTo(cx - headR * 0.30f, mouthY)
                mp.quadraticBezierTo(cx + headR * 0.05f, mouthY + headR * 0.12f, cx + headR * 0.32f, mouthY - headR * 0.05f)
                drawPath(mp, liner, style = Stroke(3f, cap = StrokeCap.Round))
            }
            MascotMood.CELEBRATING -> {
                drawPath(
                    Path().apply {
                        moveTo(cx - headR * 0.26f, mouthY)
                        quadraticBezierTo(cx, mouthY + headR * 0.34f, cx + headR * 0.26f, mouthY)
                        close()
                    },
                    Color(0xFF5A2318)
                )
                drawOval(
                    Color(0xFFC25E4E),
                    topLeft = Offset(cx - headR * 0.13f, mouthY + headR * 0.08f),
                    size = Size(headR * 0.26f, headR * 0.16f)
                )
            }
            MascotMood.SAD, MascotMood.CONFUSED -> {
                mp.moveTo(cx - headR * 0.24f, mouthY + headR * 0.08f)
                mp.quadraticBezierTo(cx, mouthY - headR * 0.08f, cx + headR * 0.24f, mouthY + headR * 0.08f)
                drawPath(mp, liner, style = Stroke(2.5f, cap = StrokeCap.Round))
            }
            MascotMood.SPEAKING -> {
                drawOval(
                    Color(0xFF5A2318),
                    topLeft = Offset(cx - headR * 0.14f, mouthY - headR * 0.05f),
                    size = Size(headR * 0.28f, headR * 0.24f)
                )
            }
            else -> {
                drawLine(liner, Offset(cx - headR * 0.16f, mouthY), Offset(cx + headR * 0.16f, mouthY), 2.5f, StrokeCap.Round)
            }
        }

        // 9. Whisker pores — faint realism.
        val spotC = Color(0xFF8A5A2A).copy(alpha = 0.5f)
        listOf(-1f, 1f).forEach { side ->
            for (k in 0..2) {
                drawCircle(
                    spotC, headR * 0.022f,
                    Offset(cx + side * (headR * 0.34f + k * headR * 0.07f), mouthY + k * headR * 0.045f)
                )
            }
        }
    }
}

@Composable
private fun MascotEmblem(
    mood: MascotMood = MascotMood.HAPPY,
    size: Dp = 96.dp,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "mascot")
    val bob by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "bob"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.98f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "breathe"
    )
    // Slow lifelike blink every ~3.6s (skipped while sleeping).
    val blinkCycle by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3600), RepeatMode.Restart),
        label = "blink"
    )
    val blinking = blinkCycle < 0.045f && mood != MascotMood.SLEEPING

    Canvas(
        modifier
            .size(size)
            .graphicsLayer { translationY = bob; scaleX = breathe; scaleY = breathe }
            .semantics { contentDescription = "Simba the lion"; role = Role.Image }
    ) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val headR = w * 0.30f
        val headCy = cy

        val maneDeep = Color(0xFF4E2703)
        val maneMid = Color(0xFF7E4608)
        val maneInner = Color(0xFFA86A12)
        val face = Color(0xFFD9A45B)
        val muzzleC = Color(0xFFC08A3E)

        // Mane — layered bronze rings, head-emblem style (no body, no tail).
        drawCircle(color = maneDeep, radius = headR * 1.52f, center = Offset(cx, headCy))
        drawCircle(color = maneMid, radius = headR * 1.30f, center = Offset(cx, headCy))
        // Mane texture: short angled tufts.
        for (i in 0 until 16) {
            val angle = (i * 22.5) * (Math.PI / 180.0)
            val r0 = headR * 1.30f
            val r1 = headR * 1.50f
            drawLine(
                maneDeep,
                Offset(cx + (cos(angle) * r0).toFloat(), headCy + (sin(angle) * r0).toFloat()),
                Offset(cx + (cos(angle + 0.10) * r1).toFloat(), headCy + (sin(angle + 0.10) * r1).toFloat()),
                headR * 0.09f, StrokeCap.Round
            )
        }
        drawCircle(color = maneInner, radius = headR * 1.08f, center = Offset(cx, headCy))

        // Ears — small angular triangles in mane tones.
        val earL = Path().apply {
            moveTo(cx - headR * 0.62f, headCy - headR * 0.55f)
            lineTo(cx - headR * 0.92f, headCy - headR * 1.02f)
            lineTo(cx - headR * 0.30f, headCy - headR * 0.82f)
            close()
        }
        val earR = Path().apply {
            moveTo(cx + headR * 0.62f, headCy - headR * 0.55f)
            lineTo(cx + headR * 0.92f, headCy - headR * 1.02f)
            lineTo(cx + headR * 0.30f, headCy - headR * 0.82f)
            close()
        }
        drawPath(earL, maneDeep)
        drawPath(earR, maneDeep)

        // Head base
        drawCircle(color = face, radius = headR, center = Offset(cx, headCy))

        // Muzzle — broad square jaw.
        drawCircle(color = muzzleC, radius = headR * 0.58f, center = Offset(cx, headCy + headR * 0.30f))
        // Jaw line — strong angular chin.
        val jaw = Path().apply {
            moveTo(cx - headR * 0.44f, headCy + headR * 0.62f)
            lineTo(cx - headR * 0.18f, headCy + headR * 0.74f)
            lineTo(cx + headR * 0.18f, headCy + headR * 0.74f)
            lineTo(cx + headR * 0.44f, headCy + headR * 0.62f)
        }
        drawPath(jaw, Color(0xFF3A2410), style = Stroke(headR * 0.045f, cap = StrokeCap.Round))

        // Eyes — calm almond feline gaze with vertical slit pupils.
        val eyeY = headCy - headR * 0.10f
        val eyeLx = cx - headR * 0.38f
        val eyeRx = cx + headR * 0.38f
        val eyeA = headR * 0.24f
        val eyeB = when (mood) {
            MascotMood.THINKING, MascotMood.CONFUSED -> headR * 0.07f
            MascotMood.SLEEPING -> 0f
            else -> headR * 0.11f
        }
        val gazeDx = when (mood) {
            MascotMood.CONFUSED -> -eyeA * 0.3f
            MascotMood.THINKING -> eyeA * 0.25f
            MascotMood.LISTENING, MascotMood.SPEAKING -> 0f
            else -> 0f
        }
        fun almond(lx: Float) = Path().apply {
            moveTo(lx - eyeA, eyeY)
            quadraticBezierTo(lx, eyeY - eyeB * 2f, lx + eyeA, eyeY)
            quadraticBezierTo(lx, eyeY + eyeB * 2f, lx - eyeA, eyeY)
            close()
        }
        val lidColor = Color(0xFF3A2410)
        if (mood == MascotMood.SLEEPING || blinking) {
            // Closed lids — calm lines.
            drawLine(lidColor, Offset(eyeLx - eyeA, eyeY), Offset(eyeLx + eyeA, eyeY), 3f, StrokeCap.Round)
            drawLine(lidColor, Offset(eyeRx - eyeA, eyeY), Offset(eyeRx + eyeA, eyeY), 3f, StrokeCap.Round)
        } else {
            listOf(eyeLx, eyeRx).forEach { lx ->
                drawPath(almond(lx), Color(0xFFF5EFE2))
                drawCircle(Color(0xFFE0A92E), eyeA * 0.52f, Offset(lx + gazeDx, eyeY))
                drawLine(
                    lidColor,
                    Offset(lx + gazeDx, eyeY - eyeB * 0.9f),
                    Offset(lx + gazeDx, eyeY + eyeB * 0.9f),
                    eyeA * 0.26f, StrokeCap.Round
                )
            }
        }

        // Brow ridge — heavy permanent masculine brow, angled down to center.
        val ridgeC = Color(0xFF3A2410).copy(alpha = 0.85f)
        drawLine(ridgeC, Offset(eyeLx - eyeA * 1.15f, eyeY - eyeB * 3.2f), Offset(eyeLx + eyeA * 1.05f, eyeY - eyeB * 2.2f), headR * 0.075f, StrokeCap.Round)
        drawLine(ridgeC, Offset(eyeRx - eyeA * 1.05f, eyeY - eyeB * 2.2f), Offset(eyeRx + eyeA * 1.15f, eyeY - eyeB * 3.2f), headR * 0.075f, StrokeCap.Round)

        // Brows — extra furrow when troubled.
        if (mood == MascotMood.CONFUSED || mood == MascotMood.SAD) {
            drawLine(lidColor, Offset(eyeLx - eyeA, eyeY - eyeB * 2.4f), Offset(eyeLx + eyeA, eyeY - eyeB * 2.9f), 2.5f, StrokeCap.Round)
            drawLine(lidColor, Offset(eyeRx - eyeA, eyeY - eyeB * 2.9f), Offset(eyeRx + eyeA, eyeY - eyeB * 2.4f), 2.5f, StrokeCap.Round)
        }

        // Nose — small dark triangle.
        val noseY = headCy + headR * 0.22f
        val noseW = headR * 0.16f
        val noseH = headR * 0.13f
        drawPath(
            Path().apply {
                moveTo(cx - noseW, noseY)
                lineTo(cx + noseW, noseY)
                lineTo(cx, noseY + noseH)
                close()
            },
            Color(0xFF3A2410)
        )

        // Mouth — confident minimal strokes.
        val mouthY = noseY + noseH + headR * 0.10f
        val mouthPath = Path()
        when (mood) {
            MascotMood.HAPPY, MascotMood.EXCITED, MascotMood.ENCOURAGING -> {
                mouthPath.moveTo(cx - headR * 0.30f, mouthY)
                mouthPath.quadraticBezierTo(cx + headR * 0.05f, mouthY + headR * 0.10f, cx + headR * 0.32f, mouthY - headR * 0.05f)
                drawPath(mouthPath, lidColor, style = Stroke(3f, cap = StrokeCap.Round))
            }
            MascotMood.CELEBRATING -> {
                val open = Path().apply {
                    moveTo(cx - headR * 0.22f, mouthY)
                    quadraticBezierTo(cx, mouthY + headR * 0.30f, cx + headR * 0.22f, mouthY)
                    close()
                }
                drawPath(open, Color(0xFF7A2E1D))
            }
            MascotMood.SAD, MascotMood.CONFUSED -> {
                mouthPath.moveTo(cx - headR * 0.24f, mouthY + headR * 0.08f)
                mouthPath.quadraticBezierTo(cx, mouthY - headR * 0.08f, cx + headR * 0.24f, mouthY + headR * 0.08f)
                drawPath(mouthPath, lidColor, style = Stroke(2.5f, cap = StrokeCap.Round))
            }
            MascotMood.SPEAKING -> {
                drawOval(
                    color = Color(0xFF5A2318),
                    topLeft = Offset(cx - headR * 0.14f, mouthY - headR * 0.05f),
                    size = Size(headR * 0.28f, headR * 0.24f)
                )
            }
            else -> {
                drawLine(lidColor, Offset(cx - headR * 0.16f, mouthY), Offset(cx + headR * 0.16f, mouthY), 2.5f, StrokeCap.Round)
            }
        }

        // Whisker spots — subtle realism, no blush.
        val spotC = Color(0xFF8A5A2A).copy(alpha = 0.55f)
        listOf(-1f, 1f).forEach { side ->
            for (k in 0..2) {
                drawCircle(
                    spotC, headR * 0.022f,
                    Offset(cx + side * (headR * 0.30f + k * headR * 0.07f), mouthY - headR * 0.02f + k * headR * 0.045f)
                )
            }
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
