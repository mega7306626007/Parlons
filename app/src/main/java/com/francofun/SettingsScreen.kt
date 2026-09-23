package com.francofun

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Fully offline app: no API key, no account, no server. Everything here is on-device. */
@Composable
fun SettingsScreen(store: Store, speechDebug: String = "", onBack: () -> Unit) {
    val ctx = LocalContext.current
    val lang = store.helpLang
    var confirmReset by remember { mutableStateOf(false) }
    var goal by remember { mutableIntStateOf(store.dailyGoalXp) }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) {
            store.setReminder(true, store.reminderHour)
            scheduleDailyReminder(ctx, store.reminderHour)
        } else Toast.makeText(ctx, "Notifications off — streak reminders disabled.", Toast.LENGTH_LONG).show()
    }

    // §7 local backup: pick a JSON file the learner saved by hand — no cloud.
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val ok = runCatching {
                val text = ctx.contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() } ?: ""
                importProgressJson(ctx, text)
            }.getOrDefault(false)
            Toast.makeText(
                ctx,
                if (ok) "Progress restored — restarting!" else "That's not a Parlons backup file.",
                Toast.LENGTH_LONG
            ).show()
            if (ok) (ctx as? Activity)?.recreate()
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 14.dp))
            Text(lang.t("Settings", "Mipangilio", "Settings"), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Blue)
        }
        Text(lang.t("I explain things in", "Nitaeleza kwa", "Nitaeleza kwa"), fontWeight = FontWeight.Bold)
        LangChips(store)
        Text(lang.t("Theme", "Mandhari", "Theme"), fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(lang.t("System", "Mfumo", "System"), store.followSystemTheme) { store.setFollowSystemTheme() }
            Chip(lang.t("Light", "Nuru", "Light"), !store.followSystemTheme && !store.darkMode) { store.setDark(false) }
            Chip(lang.t("Dark", "Giza", "Dark"), !store.followSystemTheme && store.darkMode) { store.setDark(true) }
        }
        Text(lang.t("Text size", "Ukubwa wa maandishi", "Text size"), fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(lang.t("Small", "Ndogo", "Small"), store.textScale == 0.85f) { store.updateTextScale(0.85f) }
            Chip(lang.t("Default", "Kawaida", "Default"), store.textScale == 1f) { store.updateTextScale(1f) }
            Chip(lang.t("Large", "Kubwa", "Large"), store.textScale == 1.15f) { store.updateTextScale(1.15f) }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Sound effects", fontWeight = FontWeight.Bold); Text("Dings + fanfare 🎉", fontSize = 13.sp, color = Color.Gray) }
            Switch(checked = store.soundOn, onCheckedChange = { store.setSound(it) })
        }
        Text("Daily goal: $goal XP", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(30, 50, 100).forEach { g -> Chip("$g XP", goal == g) { goal = g; store.setGoal(g) } }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(lang.t("Read Simba's replies aloud", "Soma majibu ya Simba kwa sauti", "Soma majibu ya Simba kwa sauti"), modifier = Modifier.weight(1f))
            Switch(checked = store.autoSpeak, onCheckedChange = { store.updateAutoSpeak(it) })
        }
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("❤️ ${lang.t("Hearts", "Mioyo", "Hearts")}: ${store.hearts} / ${store.maxHearts}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("💎 ${store.gems}", fontWeight = FontWeight.Bold)
            }
            Text(
                lang.t("Lose a heart on a wrong answer. Refill for 10 gems, earned by practising.",
                    "Unapoteza moyo ukikosea. Jaza tena kwa vito 10, unavyopata kwa kujizoeza.",
                    "Unapoteza heart ukikosea. Jaza tena na gems 10, unazopata kwa practice."),
                fontSize = 13.sp, color = Color(0xFF6B7280)
            )
            if (store.hearts < store.maxHearts) {
                val mins = ((store.heartRegenIn() + 59_999) / 60_000).coerceAtLeast(1)
                Text(
                    lang.t("+1 heart in ~$mins min — or refill now with gems.",
                        "+1 moyo baada ya dakika ~$mins — au jaza sasa na vito.",
                        "+1 heart in ~$mins min — ama jaza sahi na gems."),
                    fontSize = 13.sp, color = Color(0xFF1B7A2F)
                )
            }
            BigButton(
                lang.t("Refill hearts (10 💎)", "Jaza mioyo (vito 10)", "Jaza hearts (gems 10)"),
                onClick = {
                    if (store.hearts >= store.maxHearts) Toast.makeText(ctx, lang.t("Already full!", "Tayari zimejaa!", "Tayari zimejaa!"), Toast.LENGTH_SHORT).show()
                    else if (store.gems < 10) Toast.makeText(ctx, lang.t("Not enough gems yet", "Huna vito vya kutosha", "Huna gems za kutosha"), Toast.LENGTH_SHORT).show()
                    else store.refillHearts()
                },
                color = Pink
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Daily reminder (${store.reminderHour}:00)", fontWeight = FontWeight.Bold); Text(lang.t("Keep your streak alive", "Endelea na mfululizo", "Usiharibu streak msee"), fontSize = 13.sp, color = Color.Gray) }
            Switch(checked = store.reminderOn, onCheckedChange = { on ->
                if (on) {
                    if (Build.VERSION.SDK_INT >= 33 && !hasNotificationPermission(ctx)) {
                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        store.setReminder(true, store.reminderHour); scheduleDailyReminder(ctx, store.reminderHour)
                    }
                } else { store.setReminder(false, store.reminderHour); cancelReminder(ctx) }
            })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(7, 12, 19, 21).forEach { h -> Chip("$h:00", store.reminderHour == h) { store.setReminder(store.reminderOn, h); if (store.reminderOn) scheduleDailyReminder(ctx, h) } }
        }
        Text(
            lang.t(
                "Parlons runs entirely on your phone — no account, no API key, no internet connection required. Your progress is saved only on this device.",
                "Parlons inafanya kazi kwenye simu yako pekee — hauhitaji akaunti, funguo ya API, au intaneti. Maendeleo yako yanahifadhiwa kwenye simu hii tu.",
                "Parlons inafanya kazi kwa simu yako tu — hauhitaji akaunti, API key, ama net. Maendeleo yako yanahifadhiwa kwa simu hii tu."
            ),
            fontSize = 13.sp, color = Color(0xFF6B7280)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text("Developer: show speech engine", fontWeight = FontWeight.Bold); Text("Testing only (§9.4)", fontSize = 13.sp, color = Color.Gray) }
            Switch(checked = store.showEngine, onCheckedChange = { store.updateShowEngine(it) })
        }
        if (store.showEngine && speechDebug.isNotBlank()) {
            Text(speechDebug, fontSize = 13.sp, color = Color(0xFF1B7A2F))
        }
        Text(
            lang.t("Backup (this phone only)", "Hifadhi (simu hii tu)", "Backup (simu hii tu)"),
            fontWeight = FontWeight.Bold
        )
        Text(
            lang.t("Save your progress as a file, move it to a new phone yourself — no account, no cloud.",
                "Hifadhi maendeleo kama faili, uhamishe mwenyewe — hakuna akaunti, hakuna wingu.",
                "Save maendeleo kama file, uhamishe mwenyewe — hakuna account, hakuna cloud."),
            fontSize = 13.sp, color = Color(0xFF6B7280)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BigButton(lang.t("Export", "Hifadhi", "Export"), modifier = Modifier.weight(1f), onClick = {
                val path = writeBackupFile(ctx, exportProgressJson(ctx))
                Toast.makeText(
                    ctx,
                    if (path != null) lang.t("Saved: $path", "Imehifadhiwa: $path", "Imesave: $path")
                    else lang.t("Export failed", "Imeshindikana", "Imefail"),
                    Toast.LENGTH_LONG
                ).show()
            })
            BigButton(lang.t("Import", "Rejesha", "Import"), modifier = Modifier.weight(1f), onClick = { importLauncher.launch(arrayOf("*/*")) }, color = Blue)
        }
        if (!confirmReset) {
            BigButton(lang.t("Reset progress", "Futa maendeleo", "Futa maendeleo"), onClick = { confirmReset = true }, color = Red)
        } else {
            BigButton(lang.t("Tap again to confirm reset", "Gusa tena kuthibitisha", "Gusa tena kuthibitisha"), onClick = {
                store.resetProgress()
                confirmReset = false
            }, color = Red)
        }
    }
}
