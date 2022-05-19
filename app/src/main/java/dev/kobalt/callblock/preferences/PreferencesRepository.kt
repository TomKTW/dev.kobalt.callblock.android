package dev.kobalt.callblock.preferences

import android.content.Context
import androidx.preference.PreferenceManager

/** Repository for preferences. */
class PreferencesRepository {

    /** Reference to context. */
    lateinit var context: Context

    private val instance get() = context.let { PreferenceManager.getDefaultSharedPreferences(it) }!!

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