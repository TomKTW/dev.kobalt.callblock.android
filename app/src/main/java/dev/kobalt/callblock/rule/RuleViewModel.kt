package dev.kobalt.callblock.rule

import android.app.Application
import dev.kobalt.callblock.base.BaseViewModel

/** View model for rule fragment. */
class RuleViewModel(application: Application) : BaseViewModel(application) {

    /** Flow for rule list. */
    val listFlow = app.databaseManager.database.ruleDao().getListUserOnlyFlow()

}