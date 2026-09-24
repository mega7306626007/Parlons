package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Voice call mode, fully offline: Simba speaks scripted turns via TTS, you reply on mic. */
@Composable
fun CallScreen(store: Store, speaker: Speaker, speechEnv: SpeechEnv, onBack: () -> Unit) {
    val msgs = remember { mutableStateListOf<ChatMsg>() }
    var turnIdx by remember { mutableIntStateOf(0) }
    var inCall by remember { mutableStateOf(false) }
    var thinking by remember { mutableStateOf(false) }
    var awaitingReply by remember { mutableStateOf(false) }
    var hint by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val lang = store.helpLang
    var scenario by remember { mutableStateOf(Scenario.FREE) }
    var reg by remember { mutableStateOf(Reg.DEBUTANT) }
    var simbaSpeaking by remember { mutableStateOf(false) }
    // Scenario + register chosen by the learner (was hard-coded FREE/DEBUTANT).
    val track = trackFor(scenario, reg)

    DisposableEffect(speaker) {
        speaker.onSpeakingChange = { simbaSpeaking = it }
        onDispose { speaker.onSpeakingChange = null; speaker.stop() }
    }

    // Changing scenario/register before a call resets the script.
    LaunchedEffect(scenario) { if (scriptFor(scenario).intermediate.isEmpty()) reg = Reg.DEBUTANT }

    DisposableEffect(Unit) { onDispose { speaker.stop() } }

    fun botSay(turn: Turn) {
        thinking = true
        awaitingReply = false
        scope.launch {
            delay(380)
            thinking = false
            msgs.add(ChatMsg(Who.TUTOR, turn.fr, turn.help(lang), "", turn.word ?: ""))
            store.touchStreak()
            if (store.autoSpeak) {
                speaker.speak(turn.fr, tag = "call-$turnIdx", onDone = { awaitingReply = inCall })
            } else {
                awaitingReply = inCall
            }
        }
    }

    fun handleReply(userText: String) {
        if (!inCall || thinking) return
        val text = userText.trim()
        if (text.isEmpty()) return
        val turn = track.getOrNull(turnIdx)
        var corr = ""
        if (turn != null && turn.expects.isNotEmpty() && bestMatch(text, turn.expects) < 0.55) {
            corr = turn.expects.first()
        }
        msgs.add(ChatMsg(Who.USER, text, correction = corr))
        store.addChatXp(2)
        awaitingReply = false
        turnIdx++
        val next = track.getOrNull(turnIdx)
        if (next != null) botSay(next)
        else {
            thinking = true
            scope.launch {
                delay(380)
                thinking = false
                msgs.add(ChatMsg(Who.TUTOR, "🎉", lang.t("Great call! That's the whole chat.", "Nzuri! Mazungumzo yameisha.", "Poa! Mazungumzo yameisha.")))
                if (Math.random() < 0.5) {
                    val (vfr, vhelp) = simbaVanne(lang)
                    msgs.add(ChatMsg(Who.TUTOR, vfr, vhelp))
                }
                inCall = false
            }
        }
    }

    val mic = rememberMic(onResult = { text -> handleReply(text) }, env = speechEnv)

    fun startCall() {
        msgs.clear()
        turnIdx = 0
        error = ""
        inCall = true
        botSay(track.first())
    }

    fun endCall() {
        inCall = false
        thinking = false
        awaitingReply = false
        speaker.stop()
        speaker.onDoneListener = null
    }

    AppBackground(tint = TurquoiseSoft) {
    Column(Modifier.fillMaxSize().padding(Sp.lg), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = { endCall(); onBack() }).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
            Text("📞 Simba call", style = T.screenTitle, color = Blue, modifier = Modifier.weight(1f))
            Text(if (hint) "💡 on" else "💡", style = T.label, color = Blue, modifier = Modifier.clickable { hint = !hint }.padding(Sp.xs))
        }
        Spacer(Modifier.size(Sp.md))
        if (!inCall) {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Sp.xs)
            ) {
                Scenario.entries.forEach { s -> Chip("${s.emoji} ${s.label}", scenario == s) { scenario = s; msgs.clear(); turnIdx = 0 } }
            }
            Spacer(Modifier.size(Sp.xs))
            if (scriptFor(scenario).intermediate.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(Sp.xs)
                ) {
                    Reg.entries.forEach { r -> Chip(r.label, reg == r) { reg = r; msgs.clear(); turnIdx = 0 } }
                }
                Spacer(Modifier.size(Sp.xs))
            }
        }
        Box(
            Modifier.size(140.dp).clip(CircleShape)
                .background(
                    when {
                        mic.listening -> Emerald
                        simbaSpeaking || thinking -> Gold
                        awaitingReply && inCall -> Cobalt
                        inCall -> CobaltDim
                        else -> Border
                    }
                )
                .semantics { contentDescription = "Tap to speak"; role = Role.Button }
                .clickable(enabled = inCall && !thinking && !simbaSpeaking) { error = ""; mic.press() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                when {
                    mic.listening -> "🎤"
                    simbaSpeaking || thinking -> "💭"
                    else -> "🦁"
                },
                fontSize = 64.sp
            )
        }
        if (mic.listening || simbaSpeaking) {
            Spacer(Modifier.size(Sp.xs))
            VoiceWaveform(active = true, color = if (simbaSpeaking) Gold else Turquoise, modifier = Modifier.fillMaxWidth().padding(horizontal = Sp.xl))
        }
        Spacer(Modifier.size(Sp.sm))
        Text(
            when {
                !inCall -> lang.t("Tap Start to talk hands-free", "Gusa Anza kuongea", "Gusa Start kuongea")
                mic.listening -> lang.t("Listening… speak French!", "Ninasikiliza… ongea Kifaransa!", "Naskiza… ongea French!")
                simbaSpeaking -> "🦁 Simba parle…"
                thinking -> "Simba réfléchit…"
                awaitingReply -> lang.t("Tap the mic to reply 🎤", "Gusa maiki kujibu 🎤", "Gusa mic kujibu 🎤")
                else -> lang.t("Simba speaks, then you reply", "Simba anaongea, kisha unajibu", "Simba anaongea, kisha unajibu")
            }, style = T.secondary, color = InkSoft, textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (error.isNotBlank()) Text(error, style = T.secondary, color = Red, modifier = Modifier.padding(Sp.xs))
        Spacer(Modifier.size(Sp.sm))
        if (!inCall) {
            BigButton("▶ ${lang.t("Start call", "Anza", "Anza")}", onClick = { startCall() })
        } else {
            BigButton("⏹ ${lang.t("End call", "Maliza", "Maliza")}", onClick = { endCall() }, color = Red)
        }
        Spacer(Modifier.size(Sp.sm))
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Sp.xs)) {
            items(msgs.toList()) { m ->
                Box(Modifier.fillMaxWidth().clip(Rad.xl).background(Surface).border(1.dp, Color(0xFFE3E7F2), Rad.xl).padding(Sp.sm)) {
                    Column {
                        Text(m.fr, style = T.bodySemi, color = Ink)
                        if (hint && m.help.isNotBlank()) Text(m.help, style = T.secondary, color = InkSoft)
                        if (m.correction.isNotBlank()) Text("✏️ ${m.correction}", style = T.caption, color = Gold)
                        if (m.word.isNotBlank()) Text("📚 ${m.word}", style = T.caption, color = Green)
                    }
                }
            }
        }
    }
    }
}
