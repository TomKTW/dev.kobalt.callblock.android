package dev.kobalt.callblock.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.kobalt.callblock.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/** View model for home fragment. */
class HomeViewModel(application: Application) : BaseViewModel(application) {

    val predefinedRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.usePredefinedRules) }
    }

    val contactRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.useContactRules) }
    }

    val userRulesFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.useUserRules) }
    }

    fun updatePredefinedRules(value: Boolean) {
        app.preferencesRepository.usePredefinedRules = value
        viewModelScope.launch { predefinedRulesFlow.emit(app.preferencesRepository.usePredefinedRules) }
    }

    fun updateContactRules(value: Boolean) {
        app.preferencesRepository.useContactRules = value
        viewModelScope.launch { contactRulesFlow.emit(app.preferencesRepository.useContactRules) }
    }

    fun updateUserRules(value: Boolean) {
        app.preferencesRepository.useUserRules = value
        viewModelScope.launch { userRulesFlow.emit(app.preferencesRepository.useUserRules) }
    }

}