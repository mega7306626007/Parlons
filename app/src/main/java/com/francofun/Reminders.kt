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

fun reminderText(lang: HelpLang): Pair<String, String> = when (lang) {
    HelpLang.ENGLISH -> "Time for French! 🇫🇷" to "Keep your streak alive — 5 minutes today?"
    HelpLang.SWAHILI -> "Wakati wa Kifaransa! 🇫🇷" to "Endelea na mfululizo — dakika 5 leo?"
    HelpLang.SHENG -> "Time ya French msee! 🇫🇷" to "Usiharibu streak — ingia 5 min leo?"
}

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
        val (title, body) = reminderText(lang)
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
