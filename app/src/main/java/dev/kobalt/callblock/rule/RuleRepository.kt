package dev.kobalt.callblock.rule

import dev.kobalt.callblock.extension.toPhoneNumber
import dev.kobalt.callblock.main.MainApplication

/** Repository for call rules. */
class RuleRepository {

    /** Reference to main application. */
    lateinit var application: MainApplication

    /** Data access object for rules. */
    private val dao get() = application.databaseManager.database.ruleDao()

    /** List of pre-defined rules.*/
    private val defaultRules = listOf(
        RuleEntity(0, "4259501212".toPhoneNumber(), RuleEntity.Action.Warn, false),
        RuleEntity(1, "2539501212".toPhoneNumber(), RuleEntity.Action.Block, false)
    )

    /** Updates a list of predefined rules into database. */
    fun updateDefaultItems() {
        defaultRules.forEach { updateItem(it) }
    }

    /** Returns rule from database with given ID. */
    fun getItem(id: Long): RuleEntity? {
        return dao.getItem(id)
    }

    /** Returns user defined rule from database with given phone number. */
    fun getItemUserOnlyByNumber(number: String): RuleEntity? {
        return dao.getItemUserOnlyByNumber(number)
    }

    /**
     * Updates rule with new data if it contains ID that is in database, otherwise it will add new rule to database.
     * If there is no ID, it will add new rule into database with new ID.
     */
    fun updateItem(item: RuleEntity) {
        dao.upsert(item)
    }

    /** Deletes rule from database. */
    fun deleteItem(id: Long) {
        dao.deleteItemById(id)
    }

    /**
     * Returns rule action to take for given phone number.
     * To ensure that consistency between rules exist, the following priority has been defined:
     * - Check user rule for this number. This is the highest priority due to explicit definition of user defined rules.
     * - If no user rule exist or they are disabled, check predefined rules.
     * - If no predefined rule exist or they are disabled, check contact rules.
     * - Returned action is allow if the given number is in contact list or contact rules are disabled.
     */
    fun getItemActionForPhoneNumber(number: String): RuleEntity.Action {
        application.preferencesRepository.apply {
            /** Returns contact rule action. Returned action is allow if the given number is in contact list or contact rules are disabled.  */
            fun checkContactRule(number: String): RuleEntity.Action = when {
                useContactRules -> when {
                    application.contactRepository.isNumberInContacts(number) -> RuleEntity.Action.Allow
                    else -> RuleEntity.Action.Block
                }
                else -> RuleEntity.Action.Allow
            }

            /** Returns predefined rule action if exists or enabled. Otherwise, returns result from checking contact rules. */
            fun checkPredefinedRule(number: String): RuleEntity.Action = when {
                usePredefinedRules -> dao.getItemPredefinedOnlyByNumber(number)?.action
                    ?: checkContactRule(number)
                else -> checkContactRule(number)
            }

            /** Returns user rule action if exists or enabled. Otherwise, returns result from checking predefined rules. */
            fun checkUserRule(number: String): RuleEntity.Action = when {
                useUserRules -> dao.getItemUserOnlyByNumber(number)?.action
                    ?: checkPredefinedRule(number)
                else -> checkPredefinedRule(number)
            }
            return checkUserRule(number)
        }
    }

}