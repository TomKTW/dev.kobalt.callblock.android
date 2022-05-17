package dev.kobalt.callblock.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.kobalt.callblock.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/** View model for home fragment. */
class HomeViewModel(application: Application) : BaseViewModel(application) {

    /** Flow for predefined rule option. */
    val predefinedRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.usePredefinedRules) }
    }

    /** Flow for contacts only rule option. */
    val contactRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.useContactRules) }
    }

    /** Flow for user defined rule option. */
    val userRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.useUserRules) }
    }

    /** Flow for call list. */
    val callListFlow = app.databaseManager.database.callDao().getListFlow()

    /** Updates predefined rule option. */
    fun updatePredefinedRules(value: Boolean) {
        app.preferencesRepository.usePredefinedRules = value
        viewModelScope.launch { predefinedRulesFlow.emit(app.preferencesRepository.usePredefinedRules) }
    }

    /** Updates contacts only rule option. */
    fun updateContactRules(value: Boolean) {
        app.preferencesRepository.useContactRules = value
        viewModelScope.launch { contactRulesFlow.emit(app.preferencesRepository.useContactRules) }
    }

    /** Updates user defined rule option. */
    fun updateUserRules(value: Boolean) {
        app.preferencesRepository.useUserRules = value
        viewModelScope.launch { userRulesFlow.emit(app.preferencesRepository.useUserRules) }
    }

}