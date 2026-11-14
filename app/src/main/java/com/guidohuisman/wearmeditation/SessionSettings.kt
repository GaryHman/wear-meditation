package com.guidohuisman.wearmeditation

import android.content.Context

/** The few settings the timer remembers between launches. */
class SessionSettings(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var sessionMinutes: Int
        get() = prefs.getInt(KEY_SESSION_MINUTES, DEFAULT_SESSION_MINUTES)
        set(value) = prefs.edit().putInt(KEY_SESSION_MINUTES, value).apply()

    /** 0 means "no interval bells". */
    var intervalMinutes: Int
        get() = prefs.getInt(KEY_INTERVAL_MINUTES, DEFAULT_INTERVAL_MINUTES)
        set(value) = prefs.edit().putInt(KEY_INTERVAL_MINUTES, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    private companion object {
        const val PREFS_NAME = "session_settings"
        const val KEY_SESSION_MINUTES = "session_minutes"
        const val KEY_INTERVAL_MINUTES = "interval_minutes"
        const val KEY_SOUND_ENABLED = "sound_enabled"
        const val DEFAULT_SESSION_MINUTES = 10
        const val DEFAULT_INTERVAL_MINUTES = 0
    }
}
