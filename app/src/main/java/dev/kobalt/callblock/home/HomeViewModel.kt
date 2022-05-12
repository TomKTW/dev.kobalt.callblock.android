package dev.kobalt.callblock.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.kobalt.callblock.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/** View model for home fragment. */
class HomeViewModel(application: Application) : BaseViewModel(application) {

    /** Flow for detecting suspicious calls state. */
    val stateFlow = MutableSharedFlow<Boolean>(1).apply {
        viewModelScope.launch { emit(app.preferencesRepository.isEnabled) }
    }

    /** Toggles state for suspicious call detection. */
    fun toggleState() {
        app.preferencesRepository.isEnabled = !app.preferencesRepository.isEnabled
        viewModelScope.launch { stateFlow.emit(app.preferencesRepository.isEnabled) }
    }

}