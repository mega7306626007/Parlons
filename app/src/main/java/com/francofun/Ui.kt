package com.francofun

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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

val Blue = Color(0xFF2B59C3)
val Red = Color(0xFFEF4135)
val Green = Color(0xFF2E9E4F)
val Gold = Color(0xFFFFB400)
val Cream = Color(0xFFF5F7FF)
val Ink = Color(0xFF1F2937)
val Pink = Color(0xFFE84393)

private val LightScheme = lightColorScheme(
    primary = Blue, secondary = Red, tertiary = Green,
    background = Cream, surface = Color.White, onSurface = Ink
)
private val DarkScheme = darkColorScheme(
    primary = Color(0xFF8AA6FF), secondary = Color(0xFFFF8A80), tertiary = Color(0xFF7BD88F),
    background = Color(0xFF12141C), surface = Color(0xFF1D2030), onSurface = Color(0xFFE8EAF2)
)

@Composable
fun ParlonsTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) DarkScheme else LightScheme, content = content)
}

@Composable
fun BigButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, color: Color = Blue) {
    Button(
        onClick = onClick, enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, disabledContainerColor = Color(0xFFD0D5E0)),
        modifier = modifier.fillMaxWidth().height(54.dp)
    ) { Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
}

@Composable
fun Chip(text: String, selected: Boolean, label: String = text, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        Modifier
            .clip(shape)
            .background(if (selected) Blue else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) Blue else Color(0xFFD0D5E0), shape)
            .semantics(mergeDescendants = true) { contentDescription = label; role = Role.Button }
            .clickable(onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) Color.White else Blue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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