package dev.kobalt.callblock.preferences

import androidx.preference.PreferenceManager
import dev.kobalt.callblock.main.MainApplication

/** Repository for preferences. */
class PreferencesRepository {

    /** Reference to main application. */
    var application: MainApplication? = null

    private val instance get() = application?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!

    /** Enabled state for detecting suspicious calls. */
    var isEnabled: Boolean
        get() = instance.getBoolean("isEnabled", false)
        set(value) {
            instance.edit()?.putBoolean("isEnabled", value)?.apply()
        }

}