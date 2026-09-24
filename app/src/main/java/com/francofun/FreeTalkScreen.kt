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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Free Talk (research: Speak Free Talk / open conversation).
 * Fully offline: Simba answers from the phrase bank by fuzzy-matching what you
 * said, acknowledges understanding, and always follows up with a new question.
 */
@Composable
fun FreeTalkScreen(store: Store, speaker: Speaker, speechEnv: SpeechEnv, onBack: () -> Unit) {
    val msgs = remember { mutableStateListOf<ChatMsg>() }
    var input by remember { mutableStateOf("") }
    var typing by remember { mutableStateOf(false) }
    var understood by remember { mutableStateOf<Double?>(null) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val lang = store.helpLang
    val pool = remember { ALL_PHRASES.filter { it.level in listOf("A1", "A2") }.shuffled() }
    var turn by remember { mutableStateOf(0) }

    fun botSay(fr: String, help: String = "", word: String = "") {
        typing = true
        scope.launch {
            delay(320)
            typing = false
            msgs.add(ChatMsg(Who.TUTOR, fr, help, word = word))
            if (store.autoSpeak) speaker.speak(fr)
        }
    }

    fun nextQuestion() {
        val p = pool[turn % pool.size]
        turn++
        // Prefer phrases that look like questions for follow-ups.
        val q = pool.drop(turn).firstOrNull { it.fr.contains("?") } ?: p
        botSay(
            fr = if (q.fr.contains("?")) q.fr else "Et toi ? ${q.fr}",
            help = q.meaning(lang),
            word = q.tip ?: ""
        )
    }

    LaunchedEffect(Unit) {
        store.touchStreak()
        botSay(
            fr = "Salut ! C'est le Free Talk — dis-moi tout ce que tu veux, en français. 🦁",
            help = lang.t(
                "Hi! This is Free Talk — say anything you like in French.",
                "Hambardhi! Hii ni Free Talk — sema unachotaka, Kifaransa.",
                "Sasa msee! Hii ni Free Talk — sema unachotaka, French."
            )
        )
    }

    fun send(raw: String) {
        val text = raw.trim()
        if (text.isEmpty() || typing) return
        msgs.add(ChatMsg(Who.USER, text))
        input = ""
        store.addChatXp(3)

        // Understand the learner: best fuzzy hit across the phrase bank.
        val best = ALL_PHRASES.maxByOrNull { similarity(text, it.fr) }
        val score = best?.let { similarity(text, it.fr) } ?: 0.0
        understood = score

        when {
            score >= 0.72 -> {
                val b = best!!
                val neighbour = pool.firstOrNull { it.key() != b.key() && it.level == b.level } ?: pool.random()
                botSay(
                    fr = listOf(
                        "Oui ! J'ai bien compris : « ${b.fr} ».",
                        "Parfait, j'ai compris « ${b.fr} » !",
                        "Compris ! « ${b.fr} », c'est exactement ça."
                    ).random(),
                    help = "${b.meaning(lang)} → ${neighbour.fr} = ${neighbour.meaning(lang)}",
                    word = neighbour.tip ?: neighbour.grammar ?: ""
                )
            }
            score >= 0.45 -> {
                val b = best!!
                botSay(
                    fr = "Je crois que tu voulais dire : « ${b.fr} » ?",
                    help = lang.t(
                        "I think you meant… Keep going!",
                        "Nadhani ulimaanisha… Endelea!",
                        "Nadhani ulikuwa unamaanisha… Endelea!"
                    ),
                    word = b.meaning(lang)
                )
            }
            else -> {
                // Open — encourage + always ask something back.
                botSay(
                    fr = listOf(
                        "Interesting ! Peux-tu essayer avec une phrase simple ?",
                        "D'accord ! Reformule un peu — je suis là pour t'aider.",
                        "Pas de souci — dis-le autrement, petit à petit on y arrive."
                    ).random(),
                    help = lang.t(
                        "No problem — try a simpler sentence, I'll follow.",
                        "Hakuna shida — jaribu sentensi rahisi, nitafuatilia.",
                        "Poa — jaribu tena tu, nitakusaidia."
                    )
                )
            }
        }
        // Always follow with a new prompt so the conversation keeps flowing.
        scope.launch {
            delay(900)
            nextQuestion()
        }
    }

    val mic = rememberMic(onResult = { text -> send(text) }, env = speechEnv)
    var simbaSpeaking by remember { mutableStateOf(false) }
    DisposableEffect(speaker) {
        speaker.onSpeakingChange = { simbaSpeaking = it }
        onDispose { speaker.onSpeakingChange = null }
    }
    LaunchedEffect(msgs.size, typing) {
        val n = msgs.size + if (typing) 1 else 0
        if (n > 0) listState.animateScrollToItem(n - 1)
    }

    AppBackground(tint = VioletSoft) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.padding(horizontal = Sp.md, vertical = Sp.sm), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "←", style = T.section, color = InkMuted,
                    modifier = Modifier
                        .clickable(onClick = onBack)
                        .padding(end = Sp.sm)
                        .semantics { contentDescription = "Back"; role = Role.Button }
                )
                Column(Modifier.weight(1f)) {
                    Text("🎤 Free Talk", style = T.screenTitle, color = Violet)
                    Text(
                        "Open conversation · no script · offline",
                        style = T.caption, color = InkSoft
                    )
                }
                understood?.let { s ->
                    val pct = (s * 100).toInt()
                    Text(
                        "🧠 $pct%",
                        style = T.label,
                        color = when {
                            s >= 0.72 -> Emerald
                            s >= 0.45 -> Gold
                            else -> Coral
                        }
                    )
                }
            }

            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(Sp.md),
                verticalArrangement = Arrangement.spacedBy(Sp.sm)
            ) {
                items(msgs) { m ->
                    FreeBubble(m, lang, onSpeak = { speaker.speak(m.fr) })
                }
                if (typing || simbaSpeaking) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🦁", fontSize = 20.sp, modifier = Modifier.padding(end = Sp.xs))
                            if (simbaSpeaking) {
                                VoiceWaveform(active = true, barCount = 12, color = Violet, modifier = Modifier.widthIn(max = 120.dp))
                                Text("  Simba speaks…", style = T.secondary, color = InkSoft)
                            } else {
                                Text("Simba réfléchit… 🤔", style = T.secondary, color = InkMuted)
                            }
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(Sp.xs).imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Parle librement en français…") },
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { send(input) }),
                    shape = Rad.pill
                )
                Spacer(Modifier.size(6.dp))
                Box(
                    Modifier.size(48.dp).clip(CircleShape).background(if (mic.listening) Coral else Violet)
                        .semantics { contentDescription = "Record voice"; role = Role.Button }
                        .clickable { mic.press() },
                    contentAlignment = Alignment.Center
                ) { Text("🎤", fontSize = 22.sp) }
                Spacer(Modifier.size(6.dp))
                Box(
                    Modifier.size(48.dp).clip(CircleShape).background(Emerald)
                        .semantics { contentDescription = "Send"; role = Role.Button }
                        .clickable { send(input) },
                    contentAlignment = Alignment.Center
                ) { Text("➤", fontSize = 20.sp, color = Color.White) }
            }
            Text(
                "+3 XP per message · understanding meter top-right",
                style = T.caption,
                modifier = Modifier.padding(horizontal = Sp.md, vertical = Sp.xxs)
            )
        }
    }
}

@Composable
private fun FreeBubble(m: ChatMsg, lang: HelpLang, onSpeak: () -> Unit) {
    when (m.who) {
        Who.USER -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                Modifier.widthIn(max = 300.dp)
                    .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                    .background(Violet).padding(12.dp)
            ) { Text(m.fr, color = Color.White, fontSize = 16.sp) }
        }
        Who.TUTOR -> Row(Modifier.fillMaxWidth()) {
            Text("🦁", fontSize = 26.sp, modifier = Modifier.padding(end = Sp.xs, top = Sp.xxs))
            Column(
                Modifier.widthIn(max = 310.dp)
                    .clip(Rad.lg)
                    .background(Surface).border(1.dp, Border, Rad.lg).padding(Sp.sm),
                verticalArrangement = Arrangement.spacedBy(Sp.xs)
            ) {
                Text(m.fr, style = T.bodySemi, color = Ink)
                Text("🔊 Listen", style = T.label, color = Violet, modifier = Modifier.clickable(onClick = onSpeak))
                if (m.help.isNotBlank()) Text(m.help, style = T.secondary, color = InkSoft)
                if (m.word.isNotBlank()) Text("📚 ${m.word}", style = T.caption, color = Emerald)
            }
        }
    }
}
