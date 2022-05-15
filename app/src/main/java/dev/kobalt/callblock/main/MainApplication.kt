package dev.kobalt.callblock.main

import dev.kobalt.callblock.base.BaseApplication
import dev.kobalt.callblock.contact.ContactRepository
import dev.kobalt.callblock.database.DatabaseManager
import dev.kobalt.callblock.preferences.PreferencesRepository
import dev.kobalt.callblock.rule.RuleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Main application as a base for getting resources. */
class MainApplication : BaseApplication() {

    companion object {
        /** Global instance of application object, used specifically for extensions where context isn't directly accessible. */
        lateinit var globalInstance: MainApplication
    }

    /** Repository for contacts. */
    lateinit var contactRepository: ContactRepository

    /** Repository for preferences. */
    lateinit var preferencesRepository: PreferencesRepository

    /** Repository for incoming call rules. */
    lateinit var ruleRepository: RuleRepository

    /** Manager for database. */
    lateinit var databaseManager: DatabaseManager

    /** Coroutine scope used for launching coroutines on non-UI thread. */
    val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        globalInstance = this
        databaseManager = DatabaseManager().also { it.application = this }
        contactRepository = ContactRepository().also { it.application = this }
        preferencesRepository = PreferencesRepository().also { it.application = this }
        ruleRepository = RuleRepository().also { it.application = this }
        scope.launch(Dispatchers.IO) { ruleRepository.updateDefaultItems() }
    }

}


