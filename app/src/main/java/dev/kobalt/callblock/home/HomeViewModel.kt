package dev.kobalt.callblock.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.kobalt.callblock.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/** View model for home fragment. */
class HomeViewModel(application: Application) : BaseViewModel(application) {

    /** Flow for detecting suspicious calls state. */
    val detectSuspiciousFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.detectSuspicious) }
    }

    /** Flow for allowing calls from contacts only state. */
    val allowContactsOnlyFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.allowContactsOnly) }
    }

    /** Toggles state for suspicious call detection. */
    fun updateDetectSuspicious(value: Boolean) {
        app.preferencesRepository.detectSuspicious = value
        viewModelScope.launch { detectSuspiciousFlow.emit(app.preferencesRepository.detectSuspicious) }
        // Disable allowing contact only calls as incoming calls would be blocked either way if they are not in contacts.
        if (value && app.preferencesRepository.allowContactsOnly) updateAllowContactsOnly(false)
    }

    /** Toggles state for allowing contact calls only. */
    fun updateAllowContactsOnly(value: Boolean) {
        app.preferencesRepository.allowContactsOnly = value
        viewModelScope.launch { allowContactsOnlyFlow.emit(app.preferencesRepository.allowContactsOnly) }
        // Disable detecting suspicious calls when contact list can be treated as a list of allowed calls.
        if (value && app.preferencesRepository.detectSuspicious) updateDetectSuspicious(false)
    }

}