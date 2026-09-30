package com.francofun

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

/**
 * Contextual reminder copy: names the single most pressing item (fading
 * words → notebook → daily goal → generic nudge) in the learner's language.
 */
fun reminderText(lang: HelpLang, dueN: Int, mistakesN: Int, goalLeft: Int, streak: Int, atRisk: Boolean = false): Pair<String, String> {
    if (atRisk) return when (lang) {
        HelpLang.ENGLISH -> "Your $streak-day streak ends tonight! 🔥" to "Two minutes now saves it — quick review?"
        HelpLang.SWAHILI -> "Mfululizo wa siku $streak unaisha leo! 🔥" to "Dakika mbili sasa unauokoa — marudio ya haraka?"
        HelpLang.SHENG -> "Streak ya siku $streak inaisha leo! 🔥" to "Two minutes sai inaisave — quick review?"
    }
    if (dueN > 0) return when (lang) {
        HelpLang.ENGLISH -> "$dueN words are fading 🧠" to "Review them now — 3 minutes keeps them alive."
        HelpLang.SWAHILI -> "Maneno $dueN yanafifia 🧠" to "Yarudie sasa — dakika 3 yanabaki."
        HelpLang.SHENG -> "Maneno $dueN zinafade 🧠" to "Zirudie sai — 3 min zinabaki."
    }
    if (mistakesN > 0) return when (lang) {
        HelpLang.ENGLISH -> "Your notebook needs you 📒" to "$mistakesN recurring mistake${if (mistakesN == 1) "" else "s"} — drill the worst first."
        HelpLang.SWAHILI -> "Daftari lako linakuhitaji 📒" to "Makosa $mistakesN yanayojirudia — anza na mbaya zaidi."
        HelpLang.SHENG -> "Notebook inakuhitaji 📒" to "Makosa $mistakesN — drill mbaya zaidi."
    }
    if (goalLeft > 0 && streak > 0) return when (lang) {
        HelpLang.ENGLISH -> "Protect your $streak-day streak 🔥" to "$goalLeft XP to today's goal — one lesson does it."
        HelpLang.SWAHILI -> "Linda mfululizo wa siku $streak 🔥" to "XP $goalLeft kufikia lengo — somo moja linatosha."
        HelpLang.SHENG -> "Protect streak ya siku $streak 🔥" to "XP $goalLeft kufika goal — lesson moja inatosha."
    }
    return when (lang) {
        HelpLang.ENGLISH -> "Time for French! 🇫🇷" to "Keep your streak alive — 5 minutes today?"
        HelpLang.SWAHILI -> "Wakati wa Kifaransa! 🇫🇷" to "Endelea na mfululizo — dakika 5 leo?"
        HelpLang.SHENG -> "Time ya French msee! 🇫🇷" to "Usiharibu streak — ingia 5 min leo?"
    }
}

/** Raw-pref signals for [reminderText], read straight from SharedPreferences (no Store side effects). */
private fun reminderSignals(sp: android.content.SharedPreferences): ReminderSignals {
    val today = java.time.LocalDate.now().toEpochDay()
    var dueN = 0
    runCatching {
        val o = org.json.JSONObject(sp.getString("srs", "") ?: "")
        o.keys().forEach { k ->
            if (o.getJSONObject(k).optLong("d", Long.MAX_VALUE) <= today) dueN++
        }
    }
    var mistakesN = 0
    runCatching {
        val o = org.json.JSONObject(sp.getString("mistakes", "") ?: "")
        o.keys().forEach { _ -> mistakesN++ }
    }
    val goalLeft = (sp.getInt("goal", 50) - sp.getInt("dxp_$today", 0)).coerceAtLeast(0)
    val streak = sp.getInt("streak", 0)
    val atRisk = streak > 0 && sp.getLong("lastDay", -1L) == today - 1
    return ReminderSignals(dueN, mistakesN, goalLeft, streak, atRisk)
}

private data class ReminderSignals(val dueN: Int, val mistakesN: Int, val goalLeft: Int, val streak: Int, val atRisk: Boolean)

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent?) {
        // Alarms don't survive reboot — re-arm the daily reminder instead of firing.
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val sp = ctx.getSharedPreferences("parlons", Context.MODE_PRIVATE)
            if (sp.getBoolean("remOn", false)) scheduleDailyReminder(ctx, sp.getInt("remHour", 19))
            return
        }
        val sp = ctx.getSharedPreferences("parlons", Context.MODE_PRIVATE)
        val lang = runCatching { HelpLang.valueOf(sp.getString("lang", "ENGLISH")!!) }.getOrDefault(HelpLang.ENGLISH)
        val s = reminderSignals(sp)
        val (title, body) = reminderText(lang, s.dueN, s.mistakesN, s.goalLeft, s.streak, s.atRisk)
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(NotificationChannel("parlons-rem", "Reminders", NotificationManager.IMPORTANCE_DEFAULT))
        }
        val open = PendingIntent.getActivity(ctx, 0, Intent(ctx, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val n = NotificationCompat.Builder(ctx, "parlons-rem")
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body)
            .setContentIntent(open).setAutoCancel(true).build()
        nm.notify(1001, n)
    }
}

fun scheduleDailyReminder(ctx: Context, hour: Int) {
    val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pi = PendingIntent.getBroadcast(ctx, 42, Intent(ctx, ReminderReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val cal = java.util.Calendar.getInstance().apply { set(java.util.Calendar.HOUR_OF_DAY, hour); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0) }
    if (cal.timeInMillis < System.currentTimeMillis()) cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
}

fun cancelReminder(ctx: Context) {
    val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pi = PendingIntent.getBroadcast(ctx, 42, Intent(ctx, ReminderReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    am.cancel(pi)
}

fun hasNotificationPermission(ctx: Context): Boolean =
    if (Build.VERSION.SDK_INT >= 33) ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED else true
