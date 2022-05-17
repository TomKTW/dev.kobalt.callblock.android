package dev.kobalt.callblock.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.kobalt.callblock.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/** View model for home fragment. */
class HomeViewModel(application: Application) : BaseViewModel(application) {

    /** Updates page state. */
    val pageState = MutableSharedFlow<HomeFragment.Page>(1).apply {
        viewModelScope.launch { emit(HomeFragment.Page.Overview) }
    }

    /** Updates page state. */
    fun updatePageState(value: HomeFragment.Page) {
        viewModelScope.launch { pageState.emit(value) }
    }

    /** Flow for call list. */
    val callListFlow = app.databaseManager.database.callDao().getListFlow()

    /** Flow for allowed call count. */
    val countAllowedFlow = app.databaseManager.database.callDao().getCountAllowedFlow()

    /** Flow for warned call count. */
    val countWarnedFlow = app.databaseManager.database.callDao().getCountWarnedFlow()

    /** Flow for blocked call count. */
    val countBlockedFlow = app.databaseManager.database.callDao().getCountBlockedFlow()

    /** Flow for predefined rule count. */
    val countPredefinedFlow = app.databaseManager.database.ruleDao().getCountPredefinedFlow()

    /** Flow for user defined rule count. */
    val countUserDefinedFlow = app.databaseManager.database.ruleDao().getCountUserFlow()

    /** Flow for predefined rule option. */
    val predefinedRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.usePredefinedRules) }
    }

    /** Updates predefined rule option. */
    fun updatePredefinedRules(value: Boolean) {
        app.preferencesRepository.usePredefinedRules = value
        viewModelScope.launch { predefinedRulesFlow.emit(app.preferencesRepository.usePredefinedRules) }
    }

    /** Flow for contacts only rule option. */
    val contactRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.useContactRules) }
    }

    /** Updates contacts only rule option. */
    fun updateContactRules(value: Boolean) {
        app.preferencesRepository.useContactRules = value
        viewModelScope.launch { contactRulesFlow.emit(app.preferencesRepository.useContactRules) }
    }

    /** Flow for user defined rule option. */
    val userRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.useUserRules) }
    }

    /** Updates user defined rule option. */
    fun updateUserRules(value: Boolean) {
        app.preferencesRepository.useUserRules = value
        viewModelScope.launch { userRulesFlow.emit(app.preferencesRepository.useUserRules) }
    }

}