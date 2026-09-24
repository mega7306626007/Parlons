package com.francofun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ═══════════════════════════════════════════════
   PARLONS DESIGN SYSTEM — original identity
   Fun · Friendly · Smart · Colorful · Polished
   NOT Duolingo. NOT corporate. NOT photo-heavy.
   ═══════════════════════════════════════════════ */

/* ── Primary: deep electric cobalt ── */
val Cobalt      = Color(0xFF1E4FE0)
val CobaltDeep  = Color(0xFF153CB8)
val CobaltSoft  = Color(0xFFE8EEFF)
val CobaltDim   = Color(0xFF5B7FE0)

/* ── Secondary: violet / indigo ── */
val Violet      = Color(0xFF7C3AED)
val VioletSoft  = Color(0xFFF3E8FF)

/* ── Accent: warm gold ── */
val Gold        = Color(0xFFFFB400)
val GoldSoft    = Color(0xFFFFF4D6)

/* ── Support colors ── */
val Coral       = Color(0xFFFF6B5B)
val CoralSoft   = Color(0xFFFFE5E0)
val Turquoise   = Color(0xFF0D9488)
val TurquoiseSoft = Color(0xFFD6F5F0)
val Pink        = Color(0xFFEC4899)
val PinkSoft    = Color(0xFFFCE7F3)
val Emerald     = Color(0xFF10B981)
val EmeraldSoft = Color(0xFFD1FAE5)

/* ── Neutrals ── */
val Ink         = Color(0xFF0F172A)
val InkSoft     = Color(0xFF475569)
val InkMuted    = Color(0xFF94A3B8)
val White       = Color(0xFFFFFFFF)
val Surface     = Color(0xFFFFFFFF)
val Cream       = Color(0xFFF8FAFF)
val Lavender    = Color(0xFFF5F3FF)
val WarmNeutral = Color(0xFFFFFBF5)
val Border      = Color(0xFFE2E8F0)
val BorderStrong = Color(0xFFCBD5E1)

/* ── Legacy aliases (keep existing call sites compiling) ── */
val Blue        = Cobalt
val BlueLight   = CobaltSoft
val BlueDim     = CobaltDim
val Red         = Coral
val RedLight    = CoralSoft
val Green       = Emerald
val GreenLight  = EmeraldSoft

/* ── Semantic aliases ── */
val Primary      = Cobalt
val Secondary    = Violet
val Tertiary     = Turquoise
val OnPrimary    = White
val OnSurface    = Ink
val OnSurfaceMuted = InkMuted
val SurfaceBg    = Cream
val Error        = Coral
val Success      = Emerald
val Warning      = Gold
val Info         = Cobalt

/* ── Functional color roles (§3) ── */
val ColorLearning   get() = Cobalt      // learning / navigation
val ColorReward     get() = Gold        // rewards / achievement
val ColorSpecial    get() = Violet      // special / AI features
val ColorEnergy     get() = Coral       // energy / challenges
val ColorVoice      get() = Turquoise   // voice / conversation
val ColorMistake    get() = Coral       // mistakes / warnings
val ColorCorrect    get() = Emerald     // correct answers

private val LightScheme = lightColorScheme(
    primary = Cobalt, secondary = Violet, tertiary = Turquoise,
    background = Cream, surface = Surface, onSurface = Ink,
    onSurfaceVariant = InkSoft, onPrimary = White,
    error = Coral, inverseSurface = Ink, inverseOnSurface = White
)
private val DarkScheme = darkColorScheme(
    primary = Color(0xFF8AA6FF), secondary = Color(0xFFC4B5FD), tertiary = Color(0xFF5EEAD4),
    background = Color(0xFF0B1020), surface = Color(0xFF151B2E), onSurface = Color(0xFFE8EAF6),
    onSurfaceVariant = Color(0xFFA8AEC8), onPrimary = Color(0xFF0B1020),
    error = Color(0xFFFF8A80), inverseSurface = Color(0xFFE8EAF6), inverseOnSurface = Color(0xFF0B1020)
)

/* ── Typography — no baked-in colors (dark-mode safe) ── */
object T {
    val display    = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 38.sp)
    val screenTitle = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp)
    val section    = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
    val body       = TextStyle(fontSize = 16.sp, lineHeight = 22.sp)
    val bodySemi   = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp)
    val secondary  = TextStyle(fontSize = 14.sp, lineHeight = 20.sp) // color set at call site
    val caption    = TextStyle(fontSize = 12.sp, lineHeight = 16.sp) // color set at call site
    val label      = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
    val number     = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp)
    val vocab      = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
    val stat       = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 22.sp)
    val button     = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
}

/* ── Spacing — 4dp base ── */
object Sp {
    val xxs = 4.dp; val xs = 8.dp; val sm = 12.dp; val md = 16.dp
    val lg = 20.dp; val xl = 24.dp; val xxl = 32.dp
}

/* ── Corner radii — friendlier, rounder ── */
object Rad {
    val sm   = RoundedCornerShape(8.dp)
    val md   = RoundedCornerShape(12.dp)
    val lg   = RoundedCornerShape(16.dp)
    val xl   = RoundedCornerShape(20.dp)
    val xxl  = RoundedCornerShape(28.dp)
    val full = RoundedCornerShape(50)
    val pill = RoundedCornerShape(999.dp)
}

@Composable
fun ParlonsTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) DarkScheme else LightScheme, content = content)
}

/* ═══════════════════════════════════════════════
   COMPONENTS — tactile, friendly, game-like
   ═══════════════════════════════════════════════ */

/** Primary CTA — large, high-contrast, tactile press feedback. */
@Composable
fun BigButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Cobalt
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val lift = if (pressed) 0.dp else 3.dp
    val shadowColor = if (color == Cobalt) CobaltDeep else color.copy(alpha = 0.45f)
    Box(
        modifier
            .fillMaxWidth()
            .shadow(lift, if (pressed) RoundedCornerShape(20.dp) else Rad.xl, spotColor = shadowColor)
            .clip(Rad.xl)
            .background(if (!enabled) Color(0xFFE2E8F0) else color)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .height(54.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = T.button,
            color = if (!enabled) InkMuted else White
        )
    }
}

/** Secondary button — lighter visual weight. */
@Composable
fun OutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Cobalt) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(Rad.xl)
            .background(color.copy(alpha = 0.10f))
            .border(2.dp, color.copy(alpha = 0.35f), Rad.xl)
            .clickable(onClick = onClick)
            .height(54.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = T.button, color = color)
    }
}

/** Pill chip — selectable filter / option. */
@Composable
fun Chip(text: String, selected: Boolean, label: String = text, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(Rad.pill)
            .background(if (selected) Cobalt else Surface)
            .border(2.dp, if (selected) Cobalt else Border, Rad.pill)
            .semantics(mergeDescendants = true) { contentDescription = label; role = Role.Button }
            .clickable(onClick = onClick)
            .heightIn(min = 44.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) White else InkSoft,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun LangChips(store: Store) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        HelpLang.entries.forEach { l -> Chip(l.label, store.helpLang == l) { store.updateHelpLang(l) } }
    }
}

/** Progress ring — gold for rewards, cobalt for learning. */
@Composable
fun ProgressRing(progress: Float, modifier: Modifier = Modifier, color: Color = Gold) {
    Canvas(modifier.size(52.dp)) {
        drawArc(Border, -90f, 360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
        drawArc(color, -90f, progress.coerceIn(0f, 1f) * 360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
    }
}

/** Themed screen background — clean color blocks, never photo wallpapers. */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    /** Optional soft tint wash for screen personality. */
    tint: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    Box(modifier.fillMaxWidth().background(Cream)) {
        if (tint != Color.Transparent) {
            Box(Modifier.matchParentSize().background(tint.copy(alpha = 0.35f)))
        }
        content()
    }
}
