package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Who { USER, TUTOR }

data class ChatMsg(
    val who: Who,
    val fr: String,
    val help: String = "",
    val correction: String = "",
    val word: String = "",
    val showHelp: Boolean = false
)

enum class Scenario(val emoji: String, val label: String, val brief: String) {
    FREE("💬", "Free chat", "Just chatting about you"),
    CAFE("☕", "Café", "Order food & drinks in Paris"),
    MARKET("🛍️", "Marché", "Bargain at a French market"),
    TRAVEL("✈️", "Voyage", "Ask for directions abroad"),
    MEET("🤝", "Rencontre", "Meet someone new"),
    JOB("💼", "Entretien", "Internship job interview"),
    BANK("🏦", "Banque", "Bank & mobile money in French"),
    DOCTOR("🩺", "Docteur", "Describe symptoms, understand advice"),
    RENT("🏠", "Logement", "Rent a room, negotiate price"),
    BARBER("💈", "Coiffeur", "Small talk + describe your haircut"),
    DERBY("⚽", "Débat foot", "Friendly football disagreement"),
    SORRY("🙏", "Pardon", "Apologize like you mean it")
}

/** Everything runs on-device from the scripted dialogue in Dialogue.kt — no key, no internet. */
@Composable
fun ChatScreen(store: Store, speaker: Speaker, speechEnv: SpeechEnv, chain: Boolean = false, onBack: () -> Unit, onSettings: () -> Unit, onCall: () -> Unit) {
    val msgs = remember { mutableStateListOf<ChatMsg>() }
    var scenario by remember { mutableStateOf(Scenario.FREE) }
    var reg by remember(chain) { mutableStateOf(if (chain) Reg.INTERMEDIAIRE else Reg.DEBUTANT) }
    var turnIdx by remember { mutableIntStateOf(0) }
    var typing by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val lang = store.helpLang

    fun speakBot(turn: Turn) {
        typing = true
        scope.launch {
            delay(380)
            typing = false
            msgs.add(ChatMsg(Who.TUTOR, turn.fr, turn.help(lang), "", turn.word ?: ""))
            store.touchStreak()
            if (store.autoSpeak) speaker.speak(turn.fr)
        }
    }

    fun restart() {
        msgs.clear()
        turnIdx = 0
        input = ""
        speakBot(trackFor(scenario, reg).first())
    }

    fun send(t: String) {
        val text = t.trim()
        if (text.isEmpty() || typing) return
        val track = trackFor(scenario, reg)
        val turn = track.getOrNull(turnIdx)
        var corr = ""
        if (turn != null && turn.expects.isNotEmpty()) {
            val match = bestMatch(text, turn.expects)
            if (match < 0.55) corr = turn.expects.first()
        }
        msgs.add(ChatMsg(Who.USER, text, correction = corr))
        input = ""
        store.addChatXp(2)
        // §6.2 branching: a matched branch replies once, then the script collapses back.
        val branch = turn?.branches?.firstOrNull { b -> bestMatch(text, b.match) >= 0.6 }
        turnIdx++
        if (branch != null) speakBot(branch.reply)
        else {
            val next = track.getOrNull(turnIdx)
            if (next != null) speakBot(next)
            else {
            typing = true
            scope.launch {
                delay(380)
                typing = false
                store.recordSimba(scenario.name, reg.name)
                // Chained capstone: roll straight into the next scenario.
                val next = if (chain) Scenario.entries.dropWhile { it != scenario }.drop(1).firstOrNull() else null
                if (next != null) {
                    scenario = next
                } else {
                    val bye = lang.t(
                        "That's the end of this scenario — great conversation! Pick another one above, or replay this one with ↻.",
                        "Hiyo ndiyo mwisho wa mazungumzo haya — umefanya vizuri! Chagua nyingine juu, au ↻ kucheza tena.",
                        "Hiyo ndio mwisho wa hii scenario — umeiweza msee! Chagua ingine juu, au ↻ ucheze tena."
                    )
                    msgs.add(ChatMsg(Who.TUTOR, "🎉", bye))
                    // Simba's personality: a gentle parting vanne, about half the time.
                    if (Math.random() < 0.5) {
                        val (vfr, vhelp) = simbaVanne(lang)
                        msgs.add(ChatMsg(Who.TUTOR, vfr, vhelp))
                    }
                }
            }
            }
        }
    }

    val mic = rememberMic(onResult = { text -> send(text) }, env = speechEnv)

    LaunchedEffect(scenario, reg) { restart() }
    // Intermediate register only where written (§6.1); fall back silently otherwise.
    LaunchedEffect(scenario) { if (scriptFor(scenario).intermediate.isEmpty()) reg = Reg.DEBUTANT }
    LaunchedEffect(msgs.size, typing) {
        val n = msgs.size + if (typing) 1 else 0
        if (n > 0) listState.animateScrollToItem(n - 1)
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = Sp.md, vertical = Sp.sm), verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.section, color = InkMuted, modifier = Modifier.clickable(onClick = onBack).padding(end = Sp.sm).semantics { contentDescription = "Back"; role = Role.Button })
            Text("Simba 🦁", style = T.screenTitle, color = Blue, modifier = Modifier.weight(1f))
            Text("📞", fontSize = 22.sp, modifier = Modifier.clickable(onClick = onCall).padding(end = Sp.sm).semantics { contentDescription = "Voice call"; role = Role.Button })
            Text("↻", style = T.section, color = Blue, modifier = Modifier.clickable { restart() }.semantics { contentDescription = "Restart scenario"; role = Role.Button })
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = Sp.md),
            horizontalArrangement = Arrangement.spacedBy(Sp.xs)
        ) {
            Scenario.entries.forEach { s -> Chip("${s.emoji} ${s.label}", scenario == s) { scenario = s } }
        }
        Spacer(Modifier.size(Sp.xs))
        if (scriptFor(scenario).intermediate.isNotEmpty()) {
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = Sp.md),
                horizontalArrangement = Arrangement.spacedBy(Sp.xs)
            ) {
                Reg.entries.forEach { r -> Chip(r.label, reg == r) { reg = r } }
            }
            Spacer(Modifier.size(Sp.xs))
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = Sp.md),
            horizontalArrangement = Arrangement.spacedBy(Sp.xs)
        ) {
            HelpLang.entries.forEach { l -> Chip("💡 ${l.label}", store.helpLang == l) { store.updateHelpLang(l) } }
        }
        if (chain) {
            Spacer(Modifier.size(Sp.xxs))
            Text(
                "🔗 Capstone chain ${Scenario.entries.indexOf(scenario) + 1}/12 — ${scenario.label} (${reg.label})",
                style = T.caption, color = Blue,
                modifier = Modifier.padding(horizontal = Sp.md)
            )
        }
        Spacer(Modifier.size(Sp.xxs))

        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(Sp.md),
            verticalArrangement = Arrangement.spacedBy(Sp.sm)
        ) {
            itemsIndexed(msgs) { i, m ->
                Bubble(
                    m, store.helpLang,
                    onSpeak = { speaker.speak(m.fr) },
                    onToggleHelp = { msgs[i] = m.copy(showHelp = !m.showHelp) }
                )
            }
            if (typing) {
                item { Text("Simba écrit… ✍️", style = T.secondary, color = InkMuted) }
            }
        }

        Row(Modifier.fillMaxWidth().padding(horizontal = Sp.sm), horizontalArrangement = Arrangement.Center) {
            Text("🔥 Chambrer Simba (roast me, gently)", style = T.label, color = Red, modifier = Modifier.clickable {
                val roast = ROASTS.random()
                msgs.add(ChatMsg(Who.TUTOR, roast.fr, roast.en))
                store.touchStreak()
                if (store.autoSpeak) speaker.speak(roast.fr)
            }.padding(Sp.xs))
        }

        Row(
            Modifier.fillMaxWidth().padding(Sp.xs).imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Écris en français…") },
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { send(input) }),
                shape = R.pill
            )
            Spacer(Modifier.size(6.dp))
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(if (mic.listening) Red else Blue)
                    .semantics { contentDescription = "Record voice message"; role = Role.Button }
                    .clickable { mic.press() },
                contentAlignment = Alignment.Center
            ) { Text("🎤", fontSize = 22.sp) }
            Spacer(Modifier.size(6.dp))
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(Green)
                    .semantics { contentDescription = "Send message"; role = Role.Button }
                    .clickable { send(input) },
                contentAlignment = Alignment.Center
            ) { Text("➤", fontSize = 20.sp, color = Color.White) }
        }
        Text("+2 XP per message • ${store.todayChat} today", style = T.caption, modifier = Modifier.padding(horizontal = Sp.md, vertical = Sp.xxs))
    }
}

@Composable
private fun Bubble(m: ChatMsg, lang: HelpLang, onSpeak: () -> Unit, onToggleHelp: () -> Unit) {
    when (m.who) {
        Who.USER -> Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(
                    Modifier.widthIn(max = 300.dp)
                        .clip(RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp))
                        .background(Blue).padding(12.dp)
                ) { Text(m.fr, color = Color.White, fontSize = 16.sp) }
            }
            if (m.correction.isNotBlank()) {
                Box(
                    Modifier.widthIn(max = 300.dp).clip(R.md)
                        .background(Color(0xFFFFF3CD)).padding(Sp.xs)
                ) {
                    Text(
                        "💡 ${lang.t("You could also say", "Unaweza pia sema", "Unaweza pia sema")}: “${m.correction}”",
                        style = T.caption, color = Ink
                    )
                }
            }
        }
        Who.TUTOR -> Row(Modifier.fillMaxWidth()) {
            Text("🦁", fontSize = 26.sp, modifier = Modifier.padding(end = Sp.xs, top = Sp.xxs))
            Column(
                Modifier.widthIn(max = 300.dp)
                    .clip(R.lg)
                    .background(Surface).border(1.dp, Color(0xFFE3E7F2), R.lg).padding(Sp.sm),
                verticalArrangement = Arrangement.spacedBy(Sp.xs)
            ) {
                Text(m.fr, style = T.bodySemi, color = Ink)
                Row(horizontalArrangement = Arrangement.spacedBy(Sp.md)) {
                    Text("🔊 Listen", style = T.label, color = Blue, modifier = Modifier.clickable(onClick = onSpeak))
                    if (m.help.isNotBlank()) {
                        Text(
                            "💡 ${lang.t("Help", "Msaada", "Msaada")}",
                            style = T.label, color = Blue,
                            modifier = Modifier.clickable(onClick = onToggleHelp)
                        )
                    }
                }
                if (m.showHelp) Text(m.help, style = T.secondary, color = InkSoft)
                if (m.word.isNotBlank()) Text("📚 ${m.word}", style = T.caption, color = Green)
            }
        }
    }
}
