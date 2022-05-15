package dev.kobalt.callblock.rule.edit

import android.app.Application
import androidx.lifecycle.viewModelScope
import dev.kobalt.callblock.base.BaseViewModel
import dev.kobalt.callblock.extension.toPhoneNumber
import dev.kobalt.callblock.extension.toStringFormat
import dev.kobalt.callblock.rule.RuleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/** View model for rule editing fragment. */
class RuleEditViewModel(application: Application) : BaseViewModel(application) {

    /** Flow for rule ID, assigned on initial load. */
    val idFlow = MutableSharedFlow<Long?>(1).apply {
        viewModelScope.launch { emit(null) }
    }

    /** Flow for phone number value. */
    val numberFlow = MutableSharedFlow<String?>(1).apply {
        viewModelScope.launch { emit(null) }
    }

    /** Flow for action value. */
    val actionFlow = MutableSharedFlow<RuleEntity.Action?>(1).apply {
        viewModelScope.launch { emit(null) }
    }

    /** Flow for saving state. */
    val saveFlow = MutableSharedFlow<SaveState>(1).apply {
        viewModelScope.launch { emit(SaveState.Idle) }
    }

    /** Loads rule data from given ID and assigns it to flow objects.*/
    fun load(id: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            id?.let { app.ruleRepository.getItem(it) }?.let { item ->
                item.id?.let { updateId(it) }
                item.number?.toStringFormat().let { updateNumber(it) }
                item.action?.let { updateAction(it) }
            }
        }
    }

    /** Saves rule data from updated flows into database. */
    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            // Emit saving state.
            saveFlow.emit(SaveState.Saving)
            // Check if user defined rule for given phone number already exists to avoid duplicates.
            if (idFlow.replayCache.firstOrNull() == null) {
                numberFlow.replayCache.firstOrNull()?.toPhoneNumber()?.toStringFormat()?.let {
                    if (app.ruleRepository.getItemUserOnlyByNumber(it) != null) {
                        saveFlow.emit(SaveState.AlreadyExists)
                        saveFlow.emit(SaveState.Idle)
                        return@launch
                    }
                }
            }
            // Save changes to given rule.
            runCatching {
                app.ruleRepository.updateItem(
                    RuleEntity(
                        id = idFlow.replayCache.firstOrNull(),
                        number = numberFlow.replayCache.firstOrNull()?.toPhoneNumber(),
                        action = actionFlow.replayCache.firstOrNull(),
                        fromUser = true
                    )
                )
            }.onFailure {
                it.printStackTrace()
                saveFlow.emit(SaveState.Failure)
            }.onSuccess {
                saveFlow.emit(SaveState.Success)
            }
            // Send idle state to return back to normal state.
            saveFlow.emit(SaveState.Idle)
        }
    }

    /** Deletes rule from database. */
    fun delete() {
        viewModelScope.launch(Dispatchers.IO) {
            idFlow.replayCache.firstOrNull()?.let {
                app.ruleRepository.deleteItem(it)
            }
        }
    }

    /** Updates ID value in flow object. */
    fun updateId(value: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            idFlow.emit(value)
        }
    }

    /** Updates phone number value in flow object. */
    fun updateNumber(value: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            numberFlow.emit(value)
        }
    }

    /** Updates action value in flow object. */
    fun updateAction(value: RuleEntity.Action) {
        viewModelScope.launch(Dispatchers.IO) {
            actionFlow.emit(value)
        }
    }

    /** Enumeration of possible save states.*/
    enum class SaveState {
        Idle, Saving, Success, AlreadyExists, Failure
    }

}