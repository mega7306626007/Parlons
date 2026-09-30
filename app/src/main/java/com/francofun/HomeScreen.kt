package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

/* ═══════════ HOME TAB — open layout, one primary action ═══════════ */

@Composable
fun HomeTab(
    store: Store,
    onLesson: (Lesson) -> Unit,
    onChat: () -> Unit,
    onLearn: () -> Unit,
    onPractice: () -> Unit,
    onWords: () -> Unit,
    onProfile: () -> Unit,
    onNovels: () -> Unit = {},
    onSongs: () -> Unit = {},
    onMistakes: () -> Unit = {},
    onChallenge: () -> Unit = {}
) {
    val lang = store.helpLang
    val dueN = allDueCount(store.srs)
    val (into, need) = xpIntoLevel(store.xp)
    AppBackground(tint = CobaltSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.lg)
        ) {
            item { HeaderRow(store, onProfile) }
            item { Greeting(lang) }
            item { ProgressCard(store, into, need) }
            item { UpNextCard(store, lang, onPractice, onLearn, onChat, onMistakes, onChallenge) }
            item { WordOfDayCard(store) }
            item { StreakCalendarCard(store) }
            item { PowerUpsCard(store) }
            item { LangChips(store) }
            item {
                HeroCard(color = Cobalt, onClick = onChat) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Mascot(MascotMood.EXCITED, size = 72.dp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("Chat with Simba 🦁", style = T.section, color = White)
                            Text(
                                "Offline conversations by text or voice — he reacts and suggests better phrasings.",
                                style = T.secondary, color = White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                    QuickAction("📚", "Learn path", Cobalt, Modifier.weight(1f)) { onLearn() }
                    QuickAction("💪", "Practice", Violet, Modifier.weight(1f)) { onPractice() }
                }
            }
            item {
                HeroCard(
                    color = Emerald, onClick = onNovels,
                    modifier = Modifier.semantics { contentDescription = "Open novels and stories"; role = Role.Button }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📕", fontSize = 36.sp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("1000 histoires & romans", style = T.section, color = White)
                            Text(
                                "500 courtes (30–200 p.) · 500 longues (300–1000 p.) · Hugo, Verne, Dumas…",
                                style = T.secondary, color = White.copy(alpha = 0.9f)
                            )
                        }
                        Text("→", style = T.section, color = White)
                    }
                }
            }
            item {
                HeroCard(
                    color = Coral, onClick = onSongs,
                    modifier = Modifier.semantics { contentDescription = "Open songs to listen"; role = Role.Button }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎵", fontSize = 36.sp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("600 chansons à écouter", style = T.section, color = White)
                            Text(
                                "Stromae, Piaf, Brel, Brassens… écoute sur YouTube / Spotify",
                                style = T.secondary, color = White.copy(alpha = 0.9f)
                            )
                        }
                        Text("→", style = T.section, color = White)
                    }
                }
            }
            if (dueN > 0) item { ReviewBanner(dueN, onPractice) }
            item { DailyQuestsCard(store) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                    QuickAction("📖", "Word bank", Turquoise, Modifier.weight(1f)) { onWords() }
                    QuickAction("👤", "Profile", Gold, Modifier.weight(1f)) { onProfile() }
                }
            }
            item { ContinueCard(store, lang, onLearn) }
        }
    }
}

@Composable
private fun HeaderRow(store: Store, onProfile: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Parlons", style = T.screenTitle, color = Cobalt, modifier = Modifier.weight(1f))
        BadgeCount("🔥", store.streak)
        if (store.streakFreezes > 0) BadgeCount("🧊", store.streakFreezes)
        if (store.xpBoostActive) BadgeCount("⚡", 2)
        BadgeCount("⭐", store.xp)
        BadgeCount("💎", store.gems)
        Text(
            "👤", fontSize = 20.sp,
            modifier = Modifier
                .clip(Rad.md)
                .clickable(onClick = onProfile)
                .padding(6.dp)
                .semantics { contentDescription = "Profile"; role = Role.Button }
        )
    }
}

@Composable
private fun Greeting(lang: HelpLang) {
    Text(
        when (lang) {
            HelpLang.ENGLISH -> "Bonjour! Ready to speak French today?"
            HelpLang.SWAHILI -> "Bonjour! Uko tayari kuongea Kifaransa leo?"
            HelpLang.SHENG -> "Bonjour msee! Uko ready tuongee French leo?"
        },
        style = T.body, color = InkSoft
    )
}

@Composable
private fun ProgressCard(store: Store, into: Int, need: Int) {
    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProgressRing(store.todayXp.toFloat() / store.dailyGoalXp.coerceAtLeast(1).toFloat())
            Spacer(Modifier.padding(Sp.md))
            Column(Modifier.weight(1f)) {
                Text("Level ${levelForXp(store.xp)} · ${levelTitle(levelForXp(store.xp))}", style = T.bodySemi, color = Ink)
                LinearProgressIndicator(
                    progress = { into / need.toFloat() },
                    Modifier.fillMaxWidth().height(6.dp).clip(Rad.pill),
                    color = Cobalt
                )
                Text(
                    "Today ${store.todayXp}/${store.dailyGoalXp} XP · ${store.todayLessons} lessons",
                    style = T.caption, color = InkSoft
                )
            }
        }
    }
}

/**
 * "What should I practice next?" — single top priority from [LearnerSnapshot].
 * Names the real offender (due words, worst mistake with its miss count),
 * stays heart-aware (no hearts → free chat, never a lesson), and speaks
 * the learner's help language.
 */
@Composable
private fun UpNextCard(
    store: Store,
    lang: HelpLang,
    onPractice: () -> Unit,
    onLearn: () -> Unit,
    onChat: () -> Unit,
    onMistakes: () -> Unit,
    onChallenge: () -> Unit
) {
    val snap = store.snapshot()
    val emoji: String
    val title: String
    val sub: String
    val color: Color
    val action: () -> Unit
    val label: String
    when (snap.nextAction()) {
        NextAction.FIRST_STEPS -> {
            emoji = "👋"
            title = lang.t("Start your first lesson", "Anza somo lako la kwanza", "Anza lesson yako ya kwanza")
            sub = lang.t("Two minutes — Simba guides you", "Dakika mbili — Simba anaongoza", "Two minutes — Simba hushow")
            color = Violet
            action = onLearn
            label = "Start your first lesson"
        }
        NextAction.REVIEW_DUE -> {
            emoji = "🔁"
            title = lang.t(
                "Review ${snap.dueN} due word${if (snap.dueN == 1) "" else "s"}",
                "Rudia maneno ${snap.dueN} yaliyoiva",
                "Review maneno ${snap.dueN} ziko due"
            )
            sub = lang.t(
                "You're about to forget them — catch them now",
                "Unakaribia kuyasahau — yashike sasa",
                "Unazeepoteza — zisort sasa"
            )
            color = Gold
            action = onPractice
            label = "Review due words"
        }
        NextAction.FIX_MISTAKES -> {
            val short = snap.topMistake?.fr?.take(34) ?: ""
            val misses = snap.topMistake?.count ?: 2
            emoji = "📒"
            title = lang.t(
                "Still stuck on “$short”?",
                "Bado “$short” inakusumbua?",
                "Bado “$short” inakupea hard time?"
            )
            sub = lang.t(
                "Missed ${misses}× — let's drill it out",
                "Umekosea mara $misses — tuifanyie mazoezi",
                "Umekosea ${misses}× — tuidrill"
            )
            color = Coral
            action = onMistakes
            label = "Drill your most repeated mistake"
        }
        NextAction.CRUISING -> {
            emoji = "🚀"
            title = lang.t(
                "You're cruising — daily challenge?",
                "Unakimbia vizuri — changamoto ya leo?",
                "Unacruise — daily challenge?"
            )
            sub = lang.t(
                "95%+ lately — prove it under pressure",
                "95%+ hivi karibuni — jithibitishe",
                "95%+ lately — iprove"
            )
            color = Pink
            action = onChallenge
            label = "Take the daily challenge"
        }
        NextAction.DAILY_GOAL -> {
            if (snap.hearts <= 0) {
                emoji = "💬"
                title = lang.t("Out of hearts — chat is free", "Mioyo imeisha — chat ni bure", "Hearts zimeisha — chat ni free")
                sub = lang.t("Hearts refill soon — keep French moving", "Mioyo inajaa — endelea na Kifaransa", "Hearts zinarefill — songa na French")
                color = Turquoise
                action = onChat
                label = "Chat while hearts refill"
            } else {
                emoji = "🎯"
                title = lang.t(
                    "${snap.goalLeft} XP to your daily goal",
                    "XP ${snap.goalLeft} kufikia lengo la leo",
                    "${snap.goalLeft} XP kufika goal ya leo"
                )
                sub = lang.t("One lesson gets you closer", "Somo moja linakusogeza", "Lesson moja inakusogeza")
                color = Cobalt
                action = onLearn
                label = "Continue learning toward your daily goal"
            }
        }
        NextAction.KEEP_STREAK -> {
            emoji = "💬"
            title = lang.t("Keep the streak alive", "Linda mfululizo", "Maintain streak")
            sub = lang.t(
                if (snap.streak > 1) "${snap.streak}-day streak — a quick chat protects it" else "A quick chat with Simba counts",
                if (snap.streak > 1) "Mfululizo wa siku ${snap.streak} — chat fupi unaulinda" else "Chat fupi na Simba inatosha",
                if (snap.streak > 1) "Streak ya siku ${snap.streak} — quick chat inaiprotect" else "Quick chat na Simba inatosha"
            )
            color = Turquoise
            action = onChat
            label = "Start a conversation with Simba"
        }
    }
    HeroCard(
        color = color, onClick = action,
        modifier = Modifier.semantics { contentDescription = label; role = Role.Button }
    ) {
        // HeroCard is a Box: multiple children would stack on top of each
        // other, so everything goes in one Column.
        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎯 Up next", style = T.label, color = White.copy(alpha = 0.85f))
                Spacer(Modifier.weight(1f))
                Text(emoji, fontSize = 28.sp)
            }
            Text(title, style = T.section, color = White)
            Text(sub, style = T.secondary, color = White.copy(alpha = 0.9f))
        }
    }
}

@Composable
private fun ReviewBanner(dueN: Int, onPractice: () -> Unit) {
    HeroCard(color = Gold, onClick = onPractice) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔁", fontSize = 28.sp)
            Spacer(Modifier.padding(Sp.sm))
            Column(Modifier.weight(1f)) {
                Text("Review $dueN due words", style = T.bodySemi, color = Ink)
                Text("Spaced repetition keeps them fresh", style = T.caption, color = InkSoft)
            }
            Text("→", style = T.section, color = Ink)
        }
    }
}

/* ── Word of the day (research: daily habit hook) ── */

@Composable
private fun WordOfDayCard(store: Store) {
    // Deterministic pick from all core phrases — same word all day, rotates daily.
    val phrase = remember {
        val all = ALL_PHRASES
        val day = java.time.LocalDate.now().toEpochDay()
        all[(day % all.size).toInt().coerceAtLeast(0)]
    }
    val lang = store.helpLang
    Card {
        Column(verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌟 Word of the day", style = T.label, color = Gold, modifier = Modifier.weight(1f))
                Text(phrase.level, style = T.caption, color = InkMuted)
            }
            Text(phrase.fr, style = T.section, color = Cobalt)
            Text(phrase.meaning(lang), style = T.body, color = Ink)
            phrase.tip?.let {
                Text("💡 $it", style = T.caption, color = InkSoft)
            }
            if (store.srs.containsKey(phrase.key())) {
                val item = store.srs[phrase.key()]
                Text(
                    "In your deck · box ${item?.box ?: 1}",
                    style = T.caption,
                    color = Emerald
                )
            }
        }
    }
}

/* ── Streak calendar: last 7 days (research: glanceable consistency) ── */

@Composable
private fun StreakCalendarCard(store: Store) {
    val days = store.last7DaysActivity()
    val today = java.time.LocalDate.now()
    Card {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥 ${store.streak}-day streak", style = T.bodySemi, color = Ink, modifier = Modifier.weight(1f))
                if (store.streakFreezes > 0) {
                    Text("🧊 ×${store.streakFreezes}", style = T.caption, color = Cobalt)
                }
            }
            if (store.longestStreak > 0) {
                val toRecord = store.longestStreak - store.streak
                Text(
                    when {
                        store.streak == 0 -> "🏆 Previous best: ${store.longestStreak} days — let's start the next one"
                        toRecord > 0 -> "🏆 $toRecord day${if (toRecord == 1) "" else "s"} to your best (${store.longestStreak})"
                        else -> "🏆 Personal best — right now!"
                    },
                    style = T.caption, color = InkSoft
                )
                if (store.streak > 0) {
                    val nextRound = ((store.streak / 10) + 1) * 10
                    val toRound = nextRound - store.streak
                    if (toRound in 1..5) {
                        Text(
                            "🎯 $toRound day${if (toRound == 1) "" else "s"} to a $nextRound-day streak!",
                            style = T.caption, color = Gold
                        )
                    }
                }
                val activeDays = store.last7DaysActivity().count { it }
                Text(
                    "📅 Active $activeDays of the last 7 days",
                    style = T.caption, color = InkMuted
                )
                if (store.streak > 0 && store.streakFreezes == 0) {
                    Text(
                        "🧊 A streak freeze saves your streak if you miss a day",
                        style = T.caption, color = InkMuted
                    )
                }
            }
            Spacer(Modifier.padding(Sp.xs))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEachIndexed { i, active ->
                    val date = today.minusDays((6 - i).toLong())
                    val letter = date.dayOfWeek.name.take(1).lowercase().replaceFirstChar { it.uppercase() }
                    val isToday = i == 6
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        active && isToday -> Gold
                                        active -> Emerald
                                        isToday -> CobaltSoft
                                        else -> Border
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (active) "🔥" else letter,
                                fontSize = if (active) 14.sp else 11.sp,
                                color = if (active) White else if (isToday) Cobalt else InkMuted
                            )
                        }
                        if (isToday) {
                            Text("now", style = T.caption, color = InkMuted)
                        }
                    }
                }
            }
        }
    }
}

/* ── Power-ups + time chests (research: streak freeze, early/night rewards) ── */

@Composable
private fun PowerUpsCard(store: Store) {
    var msg by remember { mutableStateOf<String?>(null) }

    Card {
        Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
            SectionHeader("⚡ Power-ups & chests")

            if (store.xpBoostActive) {
                val mins = (store.xpBoostMsLeft / 60_000L).toInt() + 1
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 18.sp)
                    Spacer(Modifier.padding(Sp.sm))
                    Text(
                        "2× XP active — ${mins}m left",
                        style = T.body, color = Violet, modifier = Modifier.weight(1f)
                    )
                }
            }

            // Early-bird chest
            ChestRow(
                icon = "🌅",
                title = "Early-bird chest",
                sub = "Claim before 10am · +25 XP · +5💎",
                claimed = store.chestClaimed("early"),
                available = store.chestAvailable("early")
            ) {
                val (xp, gems) = store.claimChest("early")
                msg = if (xp > 0) "🌅 Chest opened! +$xp XP +$gems💎" else "Not available right now (5–9am)"
            }

            // Night-owl chest
            ChestRow(
                icon = "🌙",
                title = "Night-owl chest",
                sub = "Claim 9pm–3am · +35 XP · +8💎",
                claimed = store.chestClaimed("night"),
                available = store.chestAvailable("night")
            ) {
                val (xp, gems) = store.claimChest("night")
                msg = if (xp > 0) "🌙 Chest opened! +$xp XP +$gems💎" else "Not available right now (9pm–3am)"
            }

            Divider()

            // Buy actions
            Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                BuyChip(
                    label = "🧊 Freeze · 15💎",
                    sub = "Protects 1 missed day",
                    enabled = store.gems >= 15,
                    modifier = Modifier.weight(1f)
                ) {
                    msg = if (store.buyStreakFreeze()) "🧊 Streak freeze bought!" else "Not enough gems"
                }
                BuyChip(
                    label = "⚡ 2× XP · 30💎",
                    sub = "15 minutes of double XP",
                    enabled = store.gems >= 30,
                    modifier = Modifier.weight(1f)
                ) {
                    msg = if (store.buyXpBoost()) "⚡ 2× XP for 15 minutes!" else "Not enough gems"
                }
            }

            msg?.let {
                Text(it, style = T.caption, color = InkSoft)
            }
        }
    }
}

@Composable
private fun ChestRow(
    icon: String,
    title: String,
    sub: String,
    claimed: Boolean,
    available: Boolean,
    onClaim: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(40.dp).clip(Rad.md).background(GoldSoft),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 20.sp) }
        Spacer(Modifier.padding(Sp.sm))
        Column(Modifier.weight(1f)) {
            Text(title, style = T.body, color = Ink)
            Text(sub, style = T.caption, color = InkSoft)
        }
        when {
            claimed -> Text("✓", style = T.label, color = Emerald)
            available -> Box(
                Modifier
                    .clip(Rad.pill)
                    .background(Gold)
                    .clickable(onClick = onClaim)
                    .padding(horizontal = Sp.sm, vertical = Sp.xs)
                    .semantics { contentDescription = "Claim $title"; role = Role.Button }
            ) { Text("Claim", style = T.label, color = Ink) }
            else -> Text("Later", style = T.caption, color = InkMuted)
        }
    }
}

@Composable
private fun BuyChip(label: String, sub: String, enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(Rad.md)
            .background(if (enabled) CobaltSoft else Surface)
            .border(1.dp, if (enabled) Cobalt.copy(alpha = 0.4f) else Border, Rad.md)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(Sp.sm)
            .semantics { contentDescription = label; role = Role.Button },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = T.label, color = if (enabled) Cobalt else InkMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(sub, style = T.caption, color = InkSoft, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun QuickAction(icon: String, label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(Rad.xl)
            .background(Surface)
            .border(1.dp, Border, Rad.xl)
            .clickable(onClick = onClick)
            .padding(Sp.md)
            .semantics { contentDescription = label; role = Role.Button },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(44.dp).clip(Rad.md).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 22.sp) }
        Spacer(Modifier.padding(Sp.xs))
        Text(label, style = T.label, color = Ink, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
private fun ContinueCard(store: Store, lang: HelpLang, onLearn: () -> Unit) {
    val next = allLessons()
        .filter { store.stars[it.id] ?: 0 < 3 && store.unitUnlocked(it.unitId) }
        .minByOrNull { UNITS.indexOfFirst { u -> u.id == it.unitId } * 100 + LESSONS.indexOfFirst { l -> l.id == it.id } }
    if (next != null) {
        HeroCard(color = Violet, onClick = { onLearn() }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(next.emoji, fontSize = 36.sp)
                Spacer(Modifier.padding(Sp.sm))
                Column(Modifier.weight(1f)) {
                    Text("Continue: ${next.fr}", style = T.bodySemi, color = White)
                    Text(
                        if (lang == HelpLang.ENGLISH) next.en else next.sw,
                        style = T.caption, color = White.copy(alpha = 0.85f)
                    )
                }
                Text("→", style = T.section, color = White)
            }
        }
    }
}

/* ═══════════ LEARN TAB — unit path ═══════════ */

@Composable
fun LearnTab(
    store: Store,
    onLesson: (Lesson) -> Unit,
    onGuide: (String) -> Unit = {},
    onTextbook: (String) -> Unit = {}
) {
    val lang = store.helpLang
    AppBackground {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            item {
                Text("Learning path", style = T.screenTitle, color = Ink)
                Text("13 units · 128 lessons · finish earlier units to unlock", style = T.caption, color = InkSoft)
                Spacer(Modifier.padding(Sp.xs))
            }
            UNITS.forEach { u ->
                val locked = !store.unitUnlocked(u.id)
                val ls = u.lessonIds.mapNotNull { lessonById(it) }
                val doneN = ls.count { (store.stars[it.id] ?: 0) > 0 }
                val masteredN = ls.count { (store.stars[it.id] ?: 0) >= 3 }
                item {
                    UnitHeader(u, locked, doneN, ls.size, masteredN,
                        onGuide = { onGuide(u.id) },
                        onTextbook = textbookForUnit(u.id)?.let { ch -> { onTextbook(ch.id) } })
                }
                if (locked) item { LockMessage() }
                items(ls, key = { it.id }) { l ->
                    LessonCard(l, lang, store.stars[l.id] ?: 0, enabled = !locked) { onLesson(l) }
                }
            }
            if (customLessonsCache.isNotEmpty()) {
                item { SectionHeader("✨ Custom lessons") }
                items(customLessonsCache, key = { it.id }) { l ->
                    CustomLessonRow(l) { onLesson(l) }
                }
            }
            if (store.unitUnlocked("u6")) {
                item { SectionHeader("🌉 Capstone") }
                item { CapstoneHint() }
            }
        }
    }
}

@Composable
private fun UnitHeader(
    u: StudyUnit,
    locked: Boolean,
    doneN: Int,
    totalN: Int,
    masteredN: Int,
    onGuide: () -> Unit = {},
    onTextbook: (() -> Unit)? = null
) {
    Column(modifier = Modifier.padding(top = Sp.sm)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (locked) "🔒" else u.emoji, fontSize = 20.sp)
            Spacer(Modifier.padding(Sp.sm))
            Text(u.fr, style = T.section, color = if (locked) InkMuted else Ink)
            Spacer(Modifier.padding(Sp.xs))
            Text(u.en, style = T.caption, color = InkSoft, modifier = Modifier.weight(1f))
            if (guideFor(u.id) != null) {
                Text(
                    "📖 Guide", style = T.label, color = Cobalt,
                    modifier = Modifier
                        .clip(Rad.pill)
                        .background(CobaltSoft)
                        .clickable(onClick = onGuide)
                        .padding(horizontal = Sp.sm, vertical = Sp.xs)
                        .semantics { contentDescription = "Open study guide for ${u.fr}"; role = Role.Button }
                )
            }
            if (onTextbook != null) {
                Text(
                    "📕", style = T.label, color = Violet,
                    modifier = Modifier
                        .clip(Rad.pill)
                        .background(VioletSoft)
                        .clickable(onClick = onTextbook)
                        .padding(horizontal = Sp.sm, vertical = Sp.xs)
                        .semantics { contentDescription = "Open textbook chapter for ${u.fr}"; role = Role.Button }
                )
            }
        }
        if (!locked && totalN > 0) {
            LinearProgressIndicator(
                progress = { (doneN.toFloat() / totalN.toFloat()).coerceIn(0f, 1f) },
                Modifier.fillMaxWidth().height(6.dp).clip(Rad.pill).padding(top = Sp.xxs),
                color = Cobalt
            )
            Text(
                "$doneN/$totalN started · $masteredN mastered ★",
                style = T.caption, color = InkMuted
            )
        }
    }
}

@Composable
private fun LockMessage() {
    Text(
        "Finish every lesson in earlier units to unlock the capstone 🌉",
        style = T.caption, color = InkMuted,
        modifier = Modifier.padding(bottom = Sp.xs)
    )
}

@Composable
private fun LessonCard(l: Lesson, lang: HelpLang, stars: Int, enabled: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = l.fr; role = Role.Button },
        elevation = if (enabled) 1.dp else 0.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(Rad.md).background(if (enabled) CobaltSoft else Border),
                contentAlignment = Alignment.Center
            ) { Text(l.emoji, fontSize = 22.sp) }
            Spacer(Modifier.padding(Sp.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(l.fr, style = T.bodySemi, color = if (enabled) Ink else InkMuted)
                Text(
                    if (lang == HelpLang.ENGLISH) l.en else l.sw,
                    style = T.caption, color = InkSoft
                )
            }
            Text(
                "★".repeat(stars) + "☆".repeat(3 - stars),
                color = Gold, fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CustomLessonRow(l: Lesson, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("📚", fontSize = 20.sp)
            Spacer(Modifier.padding(Sp.sm))
            Column(Modifier.weight(1f)) {
                Text(l.fr, style = T.bodySemi, color = Ink)
                Text("${l.phrases.size} phrases", style = T.caption, color = InkSoft)
            }
        }
    }
}

@Composable
private fun CapstoneHint() {
    Text(
        "Marathon review of your hardest words, or all twelve Simba scenarios back-to-back.",
        style = T.secondary, color = InkSoft
    )
}

/* ═══════════ PRACTICE TAB ═══════════ */

@Composable
fun PracticeTab(
    store: Store,
    onReview: () -> Unit,
    onChat: () -> Unit,
    onChainChat: () -> Unit,
    onCall: () -> Unit,
    onSpeed: () -> Unit,
    onCustom: () -> Unit,
    onMarathon: () -> Unit,
    onFreeTalk: () -> Unit = {},
    onMistakes: () -> Unit = {},
    onChallenge: () -> Unit = {},
    onGrammar: () -> Unit = {},
    onDrills: () -> Unit = {},
    onTextbook: () -> Unit = {}
) {
    val dueN = allDueCount(store.srs)
    val mistakeN = store.mistakes.size
    val grammarN = remember { buildGrammarLibrary().size }
    var showMore by remember { mutableStateOf(store.lessonsDone >= 10) }
    AppBackground(tint = VioletSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            item {
                Text("Practice", style = T.screenTitle, color = Ink)
                Text("Keep skills sharp — review, speak, challenge yourself", style = T.caption, color = InkSoft)
            }
            item {
                HeroCard(color = Gold, onClick = onChallenge) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎯", fontSize = 32.sp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("Daily challenge", style = T.bodySemi, color = Ink)
                            Text("8 mixed questions from what you've learned", style = T.caption, color = InkSoft)
                        }
                    }
                }
            }
            item {
                HeroCard(color = if (dueN > 0) Gold else Cobalt, onClick = onReview) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔁", fontSize = 32.sp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (dueN > 0) "$dueN words due for review" else "Review when words come due",
                                style = T.bodySemi, color = if (dueN > 0) Ink else White
                            )
                            Text(
                                "Spaced repetition (SRS)",
                                style = T.caption,
                                color = if (dueN > 0) InkSoft else White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                    PracticeTile("🎤", "Free Talk", Violet, Modifier.weight(1f), onFreeTalk)
                    PracticeTile(
                        "📒",
                        if (mistakeN > 0) "Mistakes ($mistakeN)" else "Mistakes",
                        Coral, Modifier.weight(1f), onMistakes
                    )
                }
            }
            if (mistakeN > 0) {
                item {
                    Text(
                        "📒 $mistakeN phrase${if (mistakeN == 1) "" else "s"} in your mistake notebook",
                        style = T.caption, color = Coral
                    )
                }
            }
            item {
                HeroCard(color = Cobalt, onClick = onChat) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Mascot(MascotMood.LISTENING, size = 56.dp)
                        Spacer(Modifier.padding(Sp.sm))
                        Column(Modifier.weight(1f)) {
                            Text("Chat with Simba", style = T.bodySemi, color = White)
                            Text("Scripted offline conversation", style = T.caption, color = White.copy(alpha = 0.85f))
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                    PracticeTile("📞", "Voice call", Turquoise, Modifier.weight(1f), onCall)
                    PracticeTile(
                        "⚡",
                        if (store.bestSpeedCombo > 0) "Speed (×${store.bestSpeedCombo} best)" else "Speed round",
                        Coral, Modifier.weight(1f), onSpeed
                    )
                }
            }
            item {
                OutlinedButton(
                    if (showMore) "Fewer tools ▴" else "More practice tools ▾",
                    onClick = { showMore = !showMore },
                    color = Violet
                )
            }
            if (showMore) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(Sp.sm), modifier = Modifier.fillMaxWidth()) {
                        PracticeTile("✨", "Custom lesson", Violet, Modifier.weight(1f), onCustom)
                        PracticeTile("🏃", "Marathon", Emerald, Modifier.weight(1f), onMarathon)
                    }
                }
                item {
                    PracticeTile("📐", "Grammar ($grammarN)", Cobalt, Modifier.fillMaxWidth(), onGrammar)
                }
                item {
                    PracticeTile("⚡", "Drills (${drillScaleCount()}+)", Gold, Modifier.fillMaxWidth(), onDrills)
                }
                item {
                    PracticeTile("📕", "Textbook (${TEXTBOOK.size} chapters)", Violet, Modifier.fillMaxWidth(), onTextbook)
                }
                item {
                    PracticeTile("🔗", "12-scenario chain", Gold, Modifier.fillMaxWidth(), onChainChat)
                }
                if (store.unitUnlocked("u6")) {
                    item {
                        PracticeTile("🌉", "Capstone challenges", Ink, Modifier.fillMaxWidth()) { onMarathon() }
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeTile(icon: String, label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier
            .clip(Rad.xl)
            .background(Surface)
            .border(1.dp, Border, Rad.xl)
            .clickable(onClick = onClick)
            .padding(Sp.md)
            .semantics { contentDescription = label; role = Role.Button },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(Rad.md).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 20.sp) }
        Spacer(Modifier.padding(Sp.sm))
        Text(label, style = T.bodySemi, color = Ink, modifier = Modifier.weight(1f))
        Text("→", color = InkMuted)
    }
}

/* ═══════════ PROFILE TAB ═══════════ */

@Composable
fun ProfileTab(store: Store, onStats: () -> Unit, onSettings: () -> Unit) {
    val (into, need) = xpIntoLevel(store.xp)
    AppBackground(tint = GoldSoft) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Sp.xxl),
            verticalArrangement = Arrangement.spacedBy(Sp.md)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Mascot(MascotMood.HAPPY, size = 88.dp)
                    Spacer(Modifier.padding(Sp.sm))
                    Column {
                        Text("Your profile", style = T.screenTitle, color = Ink)
                        Text(
                            "Level ${levelForXp(store.xp)} · ${levelTitle(levelForXp(store.xp))}",
                            style = T.secondary, color = InkSoft
                        )
                    }
                }
            }
            item {
                Card {
                    Column(verticalArrangement = Arrangement.spacedBy(Sp.sm)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ProfileStat("🔥", "${store.streak}", "streak")
                            ProfileStat("🧊", "${store.streakFreezes}", "freezes")
                            ProfileStat("⭐", "${store.xp}", "XP")
                            ProfileStat("💎", "${store.gems}", "gems")
                            ProfileStat("❤️", "${store.hearts}", "hearts")
                        }
                        Divider()
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ProfileStat("🏅", "${store.badges.size}/${BADGES.size}", "badges")
                            ProfileStat("📖", "${store.lessonsDone}", "lessons")
                            ProfileStat("🗣", "${store.wordsLearnedCount()}", "words")
                            ProfileStat("📜", "${store.srs.size}", "in SRS")
                        }
                    }
                }
            }
            item {
                Card {
                    Column {
                        Text("Level progress", style = T.bodySemi, color = Ink)
                        Spacer(Modifier.padding(Sp.xs))
                        LinearProgressIndicator(
                            progress = { into / need.toFloat() },
                            Modifier.fillMaxWidth().height(8.dp).clip(Rad.pill),
                            color = Cobalt
                        )
                        Text(
                            "$into / $need XP to next level · goal ${store.dailyGoalXp} XP/day",
                            style = T.caption, color = InkSoft,
                            modifier = Modifier.padding(top = Sp.xs)
                        )
                    }
                }
            }
            item {
                PracticeTile("📊", "Detailed stats", Cobalt, Modifier.fillMaxWidth(), onStats)
            }
            item {
                PracticeTile("⚙️", "Settings", InkSoft, Modifier.fillMaxWidth(), onSettings)
            }
            item {
                Text(
                    "Parlons works 100% offline · no account · no API keys",
                    style = T.caption, color = InkMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = Sp.sm)
                )
            }
        }
    }
}

@Composable
private fun ProfileStat(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 20.sp)
        Text(value, style = T.stat, color = Ink)
        Text(label, style = T.caption, color = InkSoft)
    }
}

/* ═══════════ shared bits used by Learn ═══════════ */

@Composable
private fun DailyQuestsCard(store: Store) {
    Card {
        Column {
            SectionHeader("📜 Daily quests")
            DAILY_QUESTS.forEach { q ->
                val got = store.questClaimed(q.id)
                val p = questProgress(q.id, store.todayXp, store.todayLessons, store.todaySpeak, store.todayChat, store.todayReview)
                val done = p >= q.target
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = Sp.md, vertical = Sp.xxs)) {
                    Text(q.emoji, fontSize = 18.sp)
                    Spacer(Modifier.padding(Sp.sm))
                    Text(
                        if (store.helpLang == HelpLang.ENGLISH) q.en else q.sw,
                        style = T.body, color = Ink,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (got) "✓" else "${p.coerceAtMost(q.target)}/${q.target}",
                        style = T.caption,
                        color = if (done) Emerald else InkMuted
                    )
                }
            }
        }
    }
}
