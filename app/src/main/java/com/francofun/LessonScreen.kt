package com.francofun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
private fun praise(lang: HelpLang): String = when (lang) {
    HelpLang.ENGLISH -> listOf("Great job!", "Perfect!", "Nailed it!", "Excellent!", "Trop fort!", "Even my grandma is clapping!",
        "Clean! Your accent is almost less dry than pilau without spices 😄", "Magnifique! The matatu brakes worked today 🏆")
    HelpLang.SWAHILI -> listOf("Hongera!", "Vizuri sana!", "Safi kabisa!", "Umepatia!", "Umetisha!",
        "Safi! Hata matatu bila breki ingesimama kukupongeza 😄")
    HelpLang.SHENG -> listOf("Uko kali!", "Poa sana msee!", "Umeiweza!", "Wewe ni mkali!", "Umetisha msee!",
        "Uko moto! French yako inaleta nyama choma vibes 🔥")
}.random()

private fun wrongMsg(lang: HelpLang): String = when (lang) {
    HelpLang.ENGLISH -> listOf("Not quite. Simba chuckles gently…", "Almost! The matatu left without that answer.", "Nope — but champions miss too!",
        "Hmm! Even my cousin conjugates faster… but he can't order nyama choma like you 😌", "Ouch! Your French runs like a giraffe in flip-flops — funny, but it arrives 😄").random()
    HelpLang.SWAHILI -> listOf("Pole! Jaribu tena, utaweza.", "Pole! Hata mabingwa hukosea — matatu bila breki! 😄").random()
    HelpLang.SHENG -> listOf("Pole msee, next time utaiweza!", "Pole msee! Kifaransa yako ni kama pilau bila viungo — tunaiweka spices 😄").random()
}

@Composable
fun LessonScreen(store: Store, speaker: Speaker, speechEnv: SpeechEnv, lesson: Lesson, onExit: () -> Unit) {
    // SRS due first (§6.4: gated by the learner's level ceiling)
    val due = remember(lesson) { duePhrases(lesson, store.srs, maxLevel = store.levelCeiling()) }
    val questions = remember(lesson, store.helpLang) { buildQuestions(lesson, store.helpLang, 13, due) }
    var finishedCorrect by remember { mutableStateOf<Int?>(null) }
    var speakOk by remember { mutableIntStateOf(0) }
    var reward by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var outOfHearts by remember { mutableStateOf(false) }
    // U6 capstone: native-paced audio (§4.1); restored on exit.
    DisposableEffect(lesson.unitId) {
        speaker.nativeRate = lesson.unitId == "u6"
        onDispose { speaker.nativeRate = false }
    }
    val done = finishedCorrect
    when {
        outOfHearts -> OutOfHearts(store.helpLang, onExit)
        done == null -> Quiz(store, speaker, speechEnv, questions, onExit,
            onFinish = { correct, speak -> finishedCorrect = correct; speakOk = speak },
            onOutOfHearts = { outOfHearts = true }
        )
        else -> {
            // single-shot award (keyed by lesson id to fix double-XP on rotation)
            LaunchedEffect(lesson.id) { reward = store.finishLesson(lesson.id, done, questions.size, speakOk, due.size) }
            val r = reward
            if (r == null) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else Finished(store, done, questions.size, r.first, r.second, onExit)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Quiz(store: Store, speaker: Speaker, speechEnv: SpeechEnv, questions: List<Question>, onExit: () -> Unit, onFinish: (Int, Int) -> Unit, onOutOfHearts: () -> Unit) {
    val lang = store.helpLang
    val ctx = LocalContext.current
    var idx by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    var speakOk by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    val q = questions[idx]
    val last = idx == questions.size - 1

    var chosen by remember(idx) { mutableStateOf<String?>(null) }
    val used = remember(idx) { mutableStateListOf<Int>() }
    var heard by remember(idx) { mutableStateOf("") }
    var typed by remember(idx) { mutableStateOf("") }
    var matchSel by remember(idx) { mutableStateOf<String?>(null) }
    var matchDone by remember(idx) { mutableStateOf(setOf<String>()) }
    var matchErrors by remember(idx) { mutableIntStateOf(0) }
    var result by remember(idx) { mutableStateOf<Boolean?>(null) }
    var feedback by remember(idx) { mutableStateOf("") }

    fun grade(ok: Boolean, fuzzy: Double = 1.0) {
        if (result != null) return
        result = ok
        if (ok) {
            correct++; combo++
            feedback = praise(lang) + if (combo >= 3) "  🔥 x$combo" else ""
            Sounds.ok(store.soundOn)
            buzz(ctx) // §8: short haptic tick on correct
            speaker.speak(q.phrase.fr)
        } else {
            combo = 0
            feedback = wrongMsg(lang)
            Sounds.bad(store.soundOn); buzz(ctx)
            if (!store.loseHeart()) { onOutOfHearts(); return }
        }
        // SRS scoring — except drills with ad-hoc keys ("je suis", "tu/tout"),
        // which would corrupt per-word mastery if recorded.
        if (q.type != QType.CONJUGATE && q.type != QType.MINIMAL_PAIR) store.recordSrs(q.phrase.key(), ok, fuzzy)
        if (q.type == QType.SPEAK && ok) speakOk++
    }

    fun next() { if (last) onFinish(correct, speakOk) else idx++ }

    val mic = rememberMic(onResult = { text ->
        heard = text
        val sim = similarity(text, q.phrase.fr)
        grade(sim >= 0.72, sim)
    }, env = speechEnv)

    val detail = buildString {
        if (result == false) append("Correct answer:  ")
        when (q.type) {
            QType.CONJUGATE -> append("${q.pronoun} ${q.answer}  (${q.verb})")
            QType.CLOZE -> append("${q.phrase.fr}  =  ${q.phrase.meaning(lang)}")
            QType.MATCH -> append(q.matchPairs.joinToString(" • ") { "${it.first} = ${it.second}" })
            QType.MINIMAL_PAIR -> append("You heard:  ${q.answer}")
            QType.REPLAY -> append(q.replayLines.joinToString("  /  "))
            QType.STORY -> append("${q.hint}:  ${q.answer}")
            else -> append("${q.phrase.fr}  =  ${q.phrase.meaning(lang)}")
        }
        if (lang != HelpLang.ENGLISH && q.type != QType.CONJUGATE) append("\n(${q.phrase.en})")
        q.phrase.tip?.let { append("\n💡 $it") }
        if (q.hint.isNotBlank() && q.type == QType.CONJUGATE) append("\n💡 ${q.hint}")
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✕", fontSize = 24.sp, modifier = Modifier.clickable(onClick = onExit).padding(end = 12.dp))
            LinearProgressIndicator(
                progress = { (idx + if (result != null) 1 else 0) / questions.size.toFloat() },
                modifier = Modifier.weight(1f).height(10.dp).clip(RoundedCornerShape(50)),
                color = Green
            )
            if (combo >= 2) Text("  🔥$combo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("  ❤️${store.hearts}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        key(idx) {
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                when (q.type) {
                    QType.FR_TO_MEANING -> {
                        Prompt("What does this mean?")
                        BigPhrase(q.phrase.fr) { speaker.speak(q.phrase.fr) }
                        Options(q, chosen, result) { chosen = it }
                    }
                    QType.LISTEN -> {
                        Prompt("Listen, then pick the meaning")
                        LaunchedEffect(idx) { if (store.autoSpeak) speaker.speak(q.phrase.fr) }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            RoundButton("🔊", Blue) { speaker.speak(q.phrase.fr) }
                            Spacer(Modifier.size(16.dp))
                            RoundButton("🐢", Color(0xFF7A8AB8)) { speaker.speak(q.phrase.fr, slow = true) }
                        }
                        Options(q, chosen, result) { chosen = it }
                    }
                    QType.MEANING_TO_FR -> {
                        Prompt("How do you say this in French?")
                        Text(q.phrase.meaning(lang), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Options(q, chosen, result) { chosen = it; speaker.speak(it) }
                    }
                    QType.ORDER -> {
                        Prompt("Build the sentence in French")
                        Text(q.phrase.meaning(lang), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        FlowRow(Modifier.fillMaxWidth().heightIn(min = 72.dp).clip(RoundedCornerShape(14.dp)).background(Color.White).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            used.toList().forEach { wi -> WordChip(q.words[wi], false) { if (result == null) used.removeAll { it == wi } } }
                        }
                        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            q.words.forEachIndexed { i, w ->
                                val taken = i in used
                                WordChip(w, taken) { if (result == null && !taken) used.add(i) }
                            }
                        }
                    }
                    QType.SPEAK -> {
                        Prompt("Say it out loud!")
                        BigPhrase(q.phrase.fr) { speaker.speak(q.phrase.fr) }
                        Text(q.phrase.meaning(lang), fontSize = 16.sp, color = Color(0xFF6B7280))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Box(Modifier.size(88.dp).clip(CircleShape).background(if (mic.listening) Red else Blue).semantics { contentDescription = "Record your voice"; role = Role.Button }.clickable(enabled = result == null) { mic.press() }, contentAlignment = Alignment.Center) { Text("🎤", fontSize = 38.sp) }
                        }
                        Text(when { mic.listening -> "Listening…"; heard.isNotEmpty() -> "I heard: $heard"; else -> "Tap the mic and speak" }, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF444B5E))
                        if (result == null) TextButton(onClick = { combo = 0; next() }, modifier = Modifier.fillMaxWidth()) { Text("Can't speak right now, skip") }
                    }
                    QType.CLOZE -> {
                        Prompt("Fill in the blank")
                        Text(q.clozeDisplay, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        Text(q.phrase.meaning(lang), fontSize = 16.sp, color = Color(0xFF6B7280))
                        Options(Question(q.type, q.phrase, q.options, q.answer), chosen, result) { chosen = it }
                    }
                    QType.MATCH -> {
                        Prompt("Tap a French tile, then its meaning (${matchDone.size}/${q.matchPairs.size})")
                        // left: FR tiles, right: meaning tiles
                        // Stable per question: shuffling on every recomposition
                        // re-orders tiles after each tap, so remember it.
                        val frs = remember(idx) { q.matchPairs.map { it.first } }
                        val means = remember(idx) { q.matchPairs.map { it.second }.shuffled() }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            frs.filter { f -> matchDone.none { it.startsWith(f) } }.forEach { f ->
                                WordChip(f, matchSel == f) { if (result == null) matchSel = f }
                            }
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            means.forEach { m ->
                                val already = matchDone.any { it.endsWith("=$m") }
                                if (!already) WordChip(m, false) {
                                    if (result == null && matchSel != null) {
                                        val f = matchSel!!
                                        val okPair = q.matchPairs.any { it.first == f && it.second == m }
                                        if (okPair) {
                                            matchDone = matchDone + "$f=$m"
                                            matchSel = null
                                            Sounds.ok(store.soundOn)
                                            if (matchDone.size == q.matchPairs.size) grade(true)
                                        } else { matchErrors++; matchSel = null; Sounds.bad(store.soundOn) }
                                    }
                                }
                            }
                        }
                        if (matchErrors > 0) Text("Mistakes: $matchErrors — keep going!", color = Color(0xFFB3261E))
                    }
                    QType.TYPE -> {
                        Prompt("Type it in French")
                        Text(q.phrase.meaning(lang), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        BigPhrase(q.phrase.fr.takeIf { result != null } ?: "••• 🤫 •••") { speaker.speak(q.phrase.fr) }
                        OutlinedTextField(value = typed, onValueChange = { typed = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Écris en français…") }, singleLine = true, enabled = result == null)
                        AccentRow { typed += it }
                    }
                    QType.CONJUGATE -> {
                        Prompt("Conjugate: ${q.verb} — ${q.pronoun} ___")
                        Text(q.hint, fontSize = 16.sp, color = Color(0xFF6B7280))
                        OutlinedTextField(value = typed, onValueChange = { typed = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("e.g. suis") }, singleLine = true, enabled = result == null)
                    }
                    QType.DICTATION -> {
                        Prompt("Listen and type what you hear")
                        LaunchedEffect(idx) { if (store.autoSpeak) speaker.speak(q.answer) }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            RoundButton("🔊", Blue) { speaker.speak(q.answer) }
                            Spacer(Modifier.size(16.dp))
                            RoundButton("🐢", Color(0xFF7A8AB8)) { speaker.speak(q.answer, slow = true) }
                        }
                        OutlinedTextField(value = typed, onValueChange = { typed = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Écris ce que tu entends…") }, singleLine = true, enabled = result == null)
                        AccentRow { typed += it }
                    }
                    QType.MINIMAL_PAIR -> {
                        Prompt("Which one did you hear?")
                        Text(q.hint, fontSize = 16.sp, color = Color(0xFF6B7280))
                        LaunchedEffect(idx) { if (store.autoSpeak) speaker.speak(q.answer) }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            RoundButton("🔊", Blue) { speaker.speak(q.answer) }
                            Spacer(Modifier.size(16.dp))
                            RoundButton("🐢", Color(0xFF7A8AB8)) { speaker.speak(q.answer, slow = true) }
                        }
                        Options(q, chosen, result) { chosen = it }
                    }
                    QType.REPLAY -> {
                        Prompt("Listen, then rebuild the exchange in order")
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            RoundButton("🔊", Blue) { q.replayLines.forEach { speaker.speak(it) } }
                        }
                        FlowRow(Modifier.fillMaxWidth().heightIn(min = 72.dp).clip(RoundedCornerShape(14.dp)).background(Color.White).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            used.toList().forEach { wi -> WordChip(q.words[wi], false) { if (result == null) used.removeAll { it == wi } } }
                        }
                        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            q.words.forEachIndexed { i, w ->
                                val taken = i in used
                                WordChip(w, taken) { if (result == null && !taken) used.add(i) }
                            }
                        }
                    }
                    QType.STORY -> {
                        Prompt("📖 ${q.hint}")
                        Text(q.storyText, fontSize = 16.sp, color = Color(0xFF1F2937))
                        Text(q.storyQuestion, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Options(q, chosen, result) { chosen = it }
                    }
                }
            }
        }

        val bg = when (result) { true -> Color(0xFFD7F5D3); false -> Color(0xFFFFDAD6); null -> Color.Transparent }
        Column(Modifier.fillMaxWidth().background(bg).padding(16.dp)) {
            if (result != null) {
                Text(feedback, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = if (result == true) Color(0xFF1B7A2F) else Color(0xFFB3261E))
                Text(detail, fontSize = 14.sp, color = Color(0xFF333333), modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
                BigButton(if (last) "FINISH" else "CONTINUE", onClick = { next() }, color = if (result == true) Green else Red)
            } else if (q.type != QType.SPEAK && q.type != QType.MATCH) {
                val can = when (q.type) {
                    QType.ORDER -> used.isNotEmpty()
                    QType.REPLAY -> used.size == q.words.size && q.words.isNotEmpty()
                    QType.TYPE, QType.CONJUGATE, QType.DICTATION -> typed.isNotBlank()
                    else -> chosen != null
                }
                BigButton("CHECK", enabled = can, onClick = {
                    val ok = when (q.type) {
                        QType.ORDER -> gradeOrder(used.map { q.words[it] }, q.answer)
                        QType.REPLAY -> used.map { q.words[it] }.joinToString("\n") == q.answer
                        QType.TYPE -> gradeTyped(typed, q.answer)
                        QType.CONJUGATE -> gradeTyped(typed, q.answer)
                        QType.DICTATION -> gradeTyped(typed, q.answer)
                        QType.MINIMAL_PAIR -> chosen == q.answer
                        else -> chosen == q.answer
                    }
                    val fuzzy = when (q.type) { QType.TYPE, QType.CONJUGATE, QType.DICTATION -> similarity(typed, q.answer); else -> 1.0 }
                    grade(ok, fuzzy)
                })
            }
        }
    }
}

private enum class OptState { IDLE, SELECTED, CORRECT, WRONG }

@Composable
private fun Options(q: Question, chosen: String?, result: Boolean?, onPick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        q.options.forEach { opt ->
            // Minimal pairs differ only by accent/sound — require the exact word, never fuzzy.
            val isRight = if (q.type == QType.MINIMAL_PAIR) opt == q.answer
                else (opt == q.answer || norm(opt) == norm(q.answer))
            val st = when {
                result != null && isRight -> OptState.CORRECT
                result != null && opt == chosen -> OptState.WRONG
                opt == chosen -> OptState.SELECTED
                else -> OptState.IDLE
            }
            val (bg, border) = when (st) {
                OptState.IDLE -> Color.White to Color(0xFFD0D5E0)
                OptState.SELECTED -> Color(0xFFE3EBFF) to Blue
                OptState.CORRECT -> Color(0xFFD7F5D3) to Green
                OptState.WRONG -> Color(0xFFFFDAD6) to Red
            }
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(bg).border(2.dp, border, RoundedCornerShape(16.dp)).semantics(mergeDescendants = true) { contentDescription = opt; role = Role.RadioButton }.clickable(enabled = result == null) { onPick(opt) }.padding(16.dp)) { Text(opt, fontSize = 17.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun Prompt(text: String) { Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937), modifier = Modifier.padding(top = 8.dp)) }

@Composable
private fun BigPhrase(text: String, onSpeak: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("🔊", fontSize = 30.sp, modifier = Modifier.clickable(onClick = onSpeak).padding(end = 12.dp))
        Text(text, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Blue)
    }
}

@Composable
private fun RoundButton(label: String, color: Color, onClick: () -> Unit) {
    Box(Modifier.size(84.dp).clip(CircleShape).background(color).semantics { contentDescription = label; role = Role.Button }.clickable(onClick = onClick), contentAlignment = Alignment.Center) { Text(label, fontSize = 34.sp) }
}

@Composable
private fun WordChip(text: String, faded: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(Modifier.alpha(if (faded) 0.25f else 1f).clip(shape).background(Color.White).border(2.dp, Color(0xFFD0D5E0), shape).semantics(mergeDescendants = true) { contentDescription = text; role = Role.Button }.clickable(onClick = onClick).heightIn(min = 48.dp).padding(horizontal = 14.dp, vertical = 10.dp)) { Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccentRow(onAccent: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("é", "è", "ê", "à", "ç", "î", "ô", "û", "’").forEach { a ->
            Box(Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFFE8EEFF)).clickable { onAccent(a) }.padding(horizontal = 12.dp, vertical = 8.dp)) { Text(a, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Blue) }
        }
    }
}

@Composable
private fun OutOfHearts(lang: HelpLang, onExit: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("💔", fontSize = 80.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            lang.t("Out of hearts!", "Umeisha mioyo!", "Umeisha hearts!"),
            fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Red, textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            lang.t("Hearts refill from Settings using gems, or just come back and try again.",
                "Mioyo inajazwa tena kutoka Mipangilio kwa kutumia vito, au rudi tena baadaye.",
                "Hearts zinajazwa tena kutoka Settings na gems, au rudi tena baadaye."),
            fontSize = 15.sp, textAlign = TextAlign.Center, color = Color(0xFF6B7280)
        )
        Spacer(Modifier.height(32.dp))
        BigButton(lang.t("Back to path", "Rudi", "Rudi"), onClick = onExit, color = Red)
    }
}

@Composable
private fun Finished(store: Store, correct: Int, total: Int, xp: Int, gems: Int, onDone: () -> Unit) {
    val perfect = correct == total
    val ctx = LocalContext.current
    if (perfect) { Sounds.fanfare(store.soundOn); if (!animationsOff(ctx)) ConfettiOverlay(true) }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(if (perfect) "🏆" else "🎉", fontSize = 80.sp)
        Spacer(Modifier.height(12.dp))
        Text(when (store.helpLang) { HelpLang.ENGLISH -> "Lesson complete!"; HelpLang.SWAHILI -> "Hongera! Umemaliza somo!"; HelpLang.SHENG -> "Umemaliza msee! Uko kali!" }, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Blue, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("$correct / $total correct", fontSize = 20.sp)
        if (!perfect) {
            // Simba's personality: every imperfect lesson ends with a gentle vanne + love.
            val (vfr, vhelp) = remember { simbaVanne(store.helpLang) }
            Spacer(Modifier.height(4.dp))
            Text(vfr, fontSize = 15.sp, textAlign = TextAlign.Center)
            if (vhelp.isNotBlank()) Text(vhelp, fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
        Text("+$xp XP ⭐   +$gems 💎", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gold)
        val (into, need) = xpIntoLevel(store.xp)
        Text("Level ${levelForXp(store.xp)} ${levelTitle(levelForXp(store.xp))} • $into/$need XP", fontSize = 14.sp, color = Color.Gray)
        Spacer(Modifier.height(32.dp))
        BigButton("CONTINUE", onClick = onDone)
    }
}
