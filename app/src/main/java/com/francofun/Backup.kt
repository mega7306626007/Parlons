package com.francofun

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.time.LocalDate

/**
 * Local-only progress backup (§7 export/import). No cloud, no account:
 * a plain JSON dump of SharedPreferences the learner moves by hand
 * (Downloads folder → own file manager / messaging app / new phone).
 */

/** Serializes the whole local store (progress + prefs) to a JSON string. */
fun exportProgressJson(ctx: Context): String {
    val sp = ctx.getSharedPreferences("parlons", Context.MODE_PRIVATE)
    val o = org.json.JSONObject()
    sp.all?.forEach { (k, v) ->
        when (v) {
            is Set<*> -> o.put(k, org.json.JSONArray(v.map { it.toString() }))
            else -> o.put(k, v)
        }
    }
    return o.toString()
}

/**
 * Restores a backup produced by [exportProgressJson]. Returns false when the
 * file isn't a Parlons backup (wrong shape) — existing progress untouched.
 * Uses commit() so a following Activity.recreate() reads the fresh data.
 */
fun importProgressJson(ctx: Context, raw: String): Boolean {
    val o = runCatching { org.json.JSONObject(raw) }.getOrNull() ?: return false
    val keys = o.keys().asSequence().toList()
    if (keys.isEmpty()) return false
    val looksRight = keys.any {
        it in setOf("xp", "gems", "hearts", "streak", "srs", "customLessons", "lang", "onboarded") ||
            it.startsWith("stars_") || it.startsWith("badge_") || it.startsWith("dxp_") ||
            it.startsWith("simba_") || it.startsWith("qdone_")
    }
    if (!looksRight) return false
    val sp = ctx.getSharedPreferences("parlons", Context.MODE_PRIVATE)
    val e = sp.edit().clear()
    keys.forEach { k ->
        when (val v = o.get(k)) {
            is Int -> e.putInt(k, v)
            is Long -> e.putLong(k, v)
            is Boolean -> e.putBoolean(k, v)
            is Float -> e.putFloat(k, v)
            is Double -> e.putFloat(k, v.toFloat())
            is String -> e.putString(k, v)
            is org.json.JSONArray -> e.putStringSet(k, (0 until v.length()).map { v.getString(it) }.toSet())
            org.json.JSONObject.NULL -> e.remove(k)
            else -> e.putString(k, v.toString())
        }
    }
    return e.commit()
}

/** Writes [json] to Downloads (API 29+, no permission needed) or app files. Returns a user-facing path or null. */
fun writeBackupFile(ctx: Context, json: String): String? {
    val name = "parlons-backup-${LocalDate.now()}.json"
    return runCatching {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, name)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = ctx.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return null
            ctx.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
            "Downloads/$name"
        } else {
            val f = File(ctx.getExternalFilesDir(null), name)
            f.writeText(json)
            f.absolutePath
        }
    }.getOrNull()
}
