package com.francofun

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

/**
 * §38–46 photographic background system.
 *
 * Layer order: [REAL PHOTO] → [dark/brand scrim] → [content] → [interactive UI].
 * Falls back to the brand gradient if the drawable is missing so the app
 * never depends on a live network request for core UI rendering (§44).
 */
@Composable
fun PhotoBg(
    resId: Int?,
    modifier: Modifier = Modifier,
    /** Extra brand tint over the photo (§38). */
    tint: Color = Color.Transparent,
    tintAlpha: Float = 0f,
    /** Dark-mode treatment: stronger scrim, lower image brightness (§45). */
    dark: Boolean = isSystemInDarkTheme(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier.fillMaxSize()) {
        val hasPhoto = resId != null && resId != 0
        if (hasPhoto) {
            Image(
                painter = painterResource(resId!!),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = if (dark) 0.55f else 1f
            )
        } else {
            // Fallback: brand gradient, never a blank hole.
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BlueLight, Cream))))
        }
        // Scrim stack (§38)
        val base = when {
            dark -> Color(0xFF0B0D14).copy(alpha = if (hasPhoto) 0.62f else 0.35f)
            hasPhoto -> Color.Black.copy(alpha = 0.38f)
            else -> Color.Transparent
        }
        Box(Modifier.fillMaxSize().background(base))
        // Soft brand wash at the top for text hierarchy
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to (if (dark) Color.Black.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.22f)),
                    0.45f to Color.Transparent,
                    1f to (if (dark) Color.Black.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.12f))
                )
            )
        )
        if (tintAlpha > 0f && tint != Color.Transparent) {
            Box(Modifier.fillMaxSize().background(tint.copy(alpha = tintAlpha)))
        }
        content()
    }
}

/** Named photo slots matching §40 asset architecture (drawable resource ids resolved at call sites). */
object PhotoRes {
    var home: Int = 0
    var lesson: Int = 0
    var speed: Int = 0
    var srs: Int = 0
    var wordBank: Int = 0
    var conversation: Int = 0
    var voiceCall: Int = 0
    var progress: Int = 0
    var customLessons: Int = 0
    var offline: Int = 0
}

/**
 * §39: contextual lesson photography — stable mapping by lesson theme
 * (never random per-frame). Falls back to the default study photo.
 */
fun photoForLesson(lesson: Lesson): Int = when {
    lesson.id == "review" || lesson.id == "marathon" || lesson.id == "reviewa2" -> R.drawable.bg_srs
    lesson.id in setOf("food", "restaurant", "breakfast", "cooking") -> R.drawable.bg_food
    lesson.id in setOf("shop", "clothes", "home", "sunday") -> R.drawable.bg_custom_lessons
    lesson.id == "travel" || lesson.id == "airport" || lesson.id == "hotel" -> R.drawable.bg_travel
    lesson.id in setOf("city", "transport", "directions", "weekend") -> R.drawable.bg_city_ctx
    lesson.id in setOf(
        "friends", "meetup", "daystory", "debate", "slang", "roast", "sorry",
        "flirt", "interview", "wedding", "derby", "feel", "family", "opinions"
    ) -> R.drawable.bg_conversation
    lesson.id in setOf(
        "school", "verbs", "past", "numbers", "alphabet", "exams", "b1past", "b1future", "bridgeb1"
    ) -> R.drawable.bg_lesson
    lesson.id in setOf(
        "money", "bank", "housing", "doctor", "health", "police", "emergencies"
    ) -> R.drawable.bg_progress
    lesson.unitId == "u5" || lesson.unitId == "u6" -> R.drawable.bg_conversation
    else -> R.drawable.bg_lesson
}
