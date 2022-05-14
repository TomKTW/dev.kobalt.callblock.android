package dev.kobalt.callblock.preferences

import androidx.preference.PreferenceManager
import dev.kobalt.callblock.main.MainApplication

/** Repository for preferences. */
class PreferencesRepository {

    /** Reference to main application. */
    var application: MainApplication? = null

    private val instance get() = application?.let { PreferenceManager.getDefaultSharedPreferences(it) }!!

    /** Enabled state for detecting suspicious calls. */
    var detectSuspicious: Boolean
        get() = instance.getBoolean("detectSuspicious", false)
        set(value) {
            instance.edit()?.putBoolean("detectSuspicious", value)?.apply()
        }

    /** State for allowing only calls from contacts. */
    var allowContactsOnly: Boolean
        get() = instance.getBoolean("allowContactsOnly", false)
        set(value) {
            instance.edit()?.putBoolean("allowContactsOnly", value)?.apply()
        }

}