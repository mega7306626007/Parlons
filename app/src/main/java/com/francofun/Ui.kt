package com.francofun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* ─────────────────────────────────────────────
   COLOR SYSTEM — semantic palette (§3)
   ───────────────────────────────────────────── */
val Blue     = Color(0xFF2B59C3)
val BlueLight  = Color(0xFFE8EEFF)
val BlueDim    = Color(0xFF5B7FE0)
val Red      = Color(0xFFEF4135)
val RedLight   = Color(0xFFFFDAD6)
val Green    = Color(0xFF2E9E4F)
val GreenLight = Color(0xFFD7F5D3)
val Gold     = Color(0xFFFFB400)
val Cream    = Color(0xFFF5F7FF)
val Ink      = Color(0xFF1F2937)
val InkSoft  = Color(0xFF444B5E)
val InkMuted = Color(0xFF8A8D9A)
val Pink     = Color(0xFFE84393)
val White    = Color(0xFFFFFFFF)
val Surface  = Color(0xFFFFFFFF)

/* Semantic aliases */
val Primary     = Blue
val Secondary   = Red
val Tertiary    = Green
val OnPrimary   = White
val OnSurface   = Ink
val OnSurfaceMuted = InkMuted
val SurfaceBg   = Cream
val Error       = Red
val Success     = Green
val Warning     = Gold
val Info        = Blue

private val LightScheme = lightColorScheme(
    primary = Blue, secondary = Red, tertiary = Green,
    background = Cream, surface = Surface, onSurface = Ink,
    onSurfaceVariant = InkSoft, onPrimary = White,
    error = Red, inverseSurface = Ink, inverseOnSurface = White
)
private val DarkScheme = darkColorScheme(
    primary = Color(0xFF8AA6FF), secondary = Color(0xFFFF8A80), tertiary = Color(0xFF7BD88F),
    background = Color(0xFF12141C), surface = Color(0xFF1D2030), onSurface = Color(0xFFE8EAF2),
    onSurfaceVariant = Color(0xFFB0B5C8), onPrimary = Ink,
    error = Color(0xFFFF8A80), inverseSurface = Color(0xFFE8EAF2), inverseOnSurface = Ink
)

/* ─────────────────────────────────────────────
   TYPOGRAPHY — deliberate hierarchy (§3)
   ───────────────────────────────────────────── */
object T {
    val display   = androidx.compose.ui.text.TextStyle(fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
    val screenTitle = androidx.compose.ui.text.TextStyle(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
    val section   = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    val body      = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
    val bodySemi  = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    val secondary = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = InkSoft)
    val caption   = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = InkMuted)
    val label     = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    val number    = androidx.compose.ui.text.TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
    val vocab     = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
    val stat      = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
}

/* ─────────────────────────────────────────────
   SPACING SCALE — 4dp base (§3)
   ───────────────────────────────────────────── */
object Sp {
    val xxs = 4.dp; val xs = 8.dp; val sm = 12.dp; val md = 16.dp
    val lg = 20.dp; val xl = 24.dp; val xxl = 32.dp
}

/* ─────────────────────────────────────────────
   CORNER RADII — deliberate values (§3)
   ───────────────────────────────────────────── */
object R {
    val sm  = RoundedCornerShape(4.dp)
    val md  = RoundedCornerShape(8.dp)
    val lg  = RoundedCornerShape(12.dp)
    val xl  = RoundedCornerShape(16.dp)
    val xxl = RoundedCornerShape(24.dp)
    val full = RoundedCornerShape(50)
    val pill = RoundedCornerShape(999.dp)
}

/* ─────────────────────────────────────────────
   THEME
   ───────────────────────────────────────────── */
@Composable
fun ParlonsTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) DarkScheme else LightScheme, content = content)
}

/* ─────────────────────────────────────────────
   COMPONENTS — backward-compatible signatures
   ───────────────────────────────────────────── */

@Composable
fun BigButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, color: Color = Blue) {
    Button(
        onClick = onClick, enabled = enabled,
        shape = R.xl,
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = Color(0xFFD0D5E0)),
        modifier = modifier.fillMaxWidth().height(54.dp)
    ) { Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
}

@Composable
fun OutlinedButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Blue) {
    Button(
        onClick = onClick, shape = R.xl,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = color),
        modifier = modifier.fillMaxWidth().height(54.dp).border(1.5.dp, color, R.xl)
    ) { Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
}

@Composable
fun Chip(text: String, selected: Boolean, label: String = text, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(R.pill)
            .background(if (selected) Blue else Surface)
            .border(1.dp, if (selected) Blue else Color(0xFFD0D5E0), R.pill)
            .semantics(mergeDescendants = true) { contentDescription = label; role = Role.Button }
            .clickable(onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) White else Blue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun LangChips(store: Store) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        HelpLang.entries.forEach { l -> Chip(l.label, store.helpLang == l) { store.updateHelpLang(l) } }
    }
}

@Composable
fun ProgressRing(progress: Float, modifier: Modifier = Modifier, color: Color = Green) {
    Canvas(modifier.size(52.dp)) {
        drawArc(Color(0xFFE3E7F2), -90f, 360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
        drawArc(color, -90f, progress.coerceIn(0f, 1f) * 360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
    }
}
