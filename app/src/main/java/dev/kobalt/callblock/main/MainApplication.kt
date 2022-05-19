package dev.kobalt.callblock.main

import dev.kobalt.callblock.base.BaseApplication
import dev.kobalt.callblock.call.CallRepository
import dev.kobalt.callblock.contact.ContactRepository
import dev.kobalt.callblock.database.DatabaseManager
import dev.kobalt.callblock.preferences.PreferencesRepository
import dev.kobalt.callblock.rule.RuleRepository
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Main application as a base for getting resources. */
class MainApplication : BaseApplication() {

    /** Manager for database. */
    lateinit var databaseManager: DatabaseManager

    /** Repository for logged incoming calls. */
    lateinit var callRepository: CallRepository

    /** Repository for contacts. */
    lateinit var contactRepository: ContactRepository

    /** Repository for preferences. */
    lateinit var preferencesRepository: PreferencesRepository

    /** Repository for incoming call rules. */
    lateinit var ruleRepository: RuleRepository

    lateinit var phoneNumberUtil: PhoneNumberUtil

    /** Coroutine scope used for launching coroutines on non-UI thread. */
    val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        databaseManager = DatabaseManager().also { it.context = this }
        callRepository = CallRepository().also { it.dao = databaseManager.database.callDao() }
        contactRepository = ContactRepository().also { it.context = this }
        preferencesRepository = PreferencesRepository().also { it.context = this }
        ruleRepository = RuleRepository().also {
            it.context = this
            it.contactRepository = contactRepository
            it.preferencesRepository = preferencesRepository
            it.dao = databaseManager.database.ruleDao()
        }
        phoneNumberUtil = PhoneNumberUtil.createInstance(this)
        scope.launch(Dispatchers.IO) { ruleRepository.updateDefaultItems() }
    }

}


