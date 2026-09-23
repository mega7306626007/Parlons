package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

/* ─────────────────────────────────────────────
   DESIGN SYSTEM EXTENSIONS — new components
   All preserve backward compatibility with
   existing call sites in HomeScreen, LessonScreen, etc.
   ───────────────────────────────────────────── */

/* ── Stat card component ── */
@Composable
fun StatCard(icon: String, value: String, label: String, modifier: Modifier = Modifier) {
    Box(Modifier.clip(R.xl).background(Surface).padding(Sp.md)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 24.sp)
            Text(value, style = T.number)
            Text(label, style = T.caption)
        }
    }
}

/* ── Section header ── */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier, action: @Composable (() -> Unit)? = null) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = Sp.md, vertical = Sp.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = T.section, modifier = Modifier.weight(1f))
        action?.invoke()
    }
}

/* ── Divider ── */
@Composable
fun Divider(modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE3E7F2)).then(modifier))
}

/* ── Gradient strip ── */
@Composable
fun GradientStrip(colors: List<Color> = listOf(Blue, BlueDim), modifier: Modifier = Modifier) {
    Box(Modifier.fillMaxWidth().height(3.dp).background(Brush.horizontalGradient(colors)))
}

/* ── Card surface ── */
@Composable
fun Card(modifier: Modifier = Modifier, elevation: Dp = 1.dp, content: @Composable () -> Unit) {
    Box(Modifier.clip(R.xl).background(Surface).padding(Sp.md)) { content() }
}

/* ── Empty state ── */
@Composable
fun EmptyState(icon: String, message: String) {
    Column(Modifier.fillMaxSize().padding(Sp.xxl), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(icon, fontSize = 56.sp)
        Spacer(Modifier.padding(Sp.md))
        Text(message, style = T.body, color = InkMuted, textAlign = TextAlign.Center)
    }
}

/* ── Loading placeholder ── */
@Composable
fun LoadingPlaceholder(modifier: Modifier = Modifier, height: Dp = 48.dp) {
    Box(Modifier.fillMaxWidth().height(height).clip(R.md).background(Color(0xFFE8ECF4)).then(modifier))
}

/* ── Badge count ── */
@Composable
fun BadgeCount(icon: String, count: Int, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Text(icon, fontSize = 20.sp)
        Spacer(Modifier.padding(Sp.xs))
        Text("$count", style = T.bodySemi)
    }
}

/* ── Voice waveform — visual listening indicator ── */
@Composable
fun VoiceWaveform(active: Boolean, barCount: Int = 20, modifier: Modifier = Modifier, color: Color = Blue) {
    var heights: List<Float> by remember { mutableStateOf(List(barCount) { 4f }) }
    LaunchedEffect(active) {
        if (!active) return@LaunchedEffect
        while (active) { heights = List(barCount) { Random.nextFloat() * 20f + 4f }; delay(80L) }
    }
    Row(modifier.fillMaxWidth().height(32.dp), horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
        heights.forEach { h ->
            Box(Modifier.width(3.dp).height((if (active) h else 4f).dp).clip(R.sm).background(if (active) color else Color(0xFFD0D5E0)))
        }
    }
}

/* ── Pulse dot ── */
@Composable
fun PulseDot(active: Boolean, color: Color = Blue, size: Dp = 8.dp) {
    val expanded = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) { expanded.value = true; delay(400L); expanded.value = false; delay(400L) }
    }
    Box(Modifier.size(if (active && expanded.value) size + 4.dp else size).clip(CircleShape).background(color))
}

/* ── Fade-in content ── */
@Composable
fun FadeInContent(visible: Boolean = true, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var alpha by remember { mutableStateOf(if (visible) 0f else 1f) }
    LaunchedEffect(visible) { alpha = if (visible) 1f else 0f }
    Box(Modifier.alpha(alpha).then(modifier)) { content() }
}

/* ── Icon chip ── */
@Composable
fun IconChip(icon: String, text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(R.pill)
            .background(if (selected) Blue else Surface)
            .border(1.dp, if (selected) Blue else Color(0xFFD0D5E0), R.pill)
            .semantics { contentDescription = text; role = Role.Button }
            .clickable(onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(icon, fontSize = 16.sp)
            Text(text, color = if (selected) White else Blue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

/* ── Accent card with left bar ── */
@Composable
fun AccentCard(accentColor: Color = Blue, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier.fillMaxWidth().clip(R.xl).background(Surface)) {
        Box(Modifier.fillMaxHeight().width(4.dp).background(accentColor))
        Box(Modifier.padding(Sp.md).padding(start = Sp.md)) { content() }
    }
}
