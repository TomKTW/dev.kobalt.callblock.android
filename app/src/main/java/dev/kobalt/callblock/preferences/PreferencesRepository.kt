package dev.kobalt.callblock.preferences

import androidx.preference.PreferenceManager
import dev.kobalt.callblock.main.MainApplication

/** Repository for preferences. */
class PreferencesRepository {

    /** Reference to main application. */
    lateinit var application: MainApplication

    private val instance get() = application.let { PreferenceManager.getDefaultSharedPreferences(it) }!!

    /** State for applying predefined rules. */
    var usePredefinedRules: Boolean
        get() = instance.getBoolean("usePredefinedRules", true)
        set(value) {
            instance.edit()?.putBoolean("usePredefinedRules", value)?.apply()
        }

    /** State for applying contact only rules. */
    var useContactRules: Boolean
        get() = instance.getBoolean("useContactRules", false)
        set(value) {
            instance.edit()?.putBoolean("useContactRules", value)?.apply()
        }

    /** State for applying user defined rules. */
    var useUserRules: Boolean
        get() = instance.getBoolean("useUserRules", false)
        set(value) {
            instance.edit()?.putBoolean("useUserRules", value)?.apply()
        }

}