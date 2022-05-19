package dev.kobalt.callblock.call

import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import androidx.annotation.RequiresApi
import dev.kobalt.callblock.R
import dev.kobalt.callblock.extension.application
import dev.kobalt.callblock.extension.showToast
import dev.kobalt.callblock.extension.toPhoneNumber
import dev.kobalt.callblock.extension.toStringFormat
import dev.kobalt.callblock.rule.RuleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/** Screening service used for managing incoming calls. This service is used when it's accessible through default dialer or role. */
@RequiresApi(Build.VERSION_CODES.N)
class CallScreeningService : CallScreeningService() {

    /** Phone number of incoming call. This is basically a scheme specific part of URI (number without "tel:" part). */
    private val Call.Details.incomingCallNumber
        get() = handle.schemeSpecificPart

    override fun onScreenCall(callDetails: Call.Details) {
        applicationContext.application.apply {
            scope.launch(Dispatchers.IO) {
                // Normalize phone number value.
                callDetails.incomingCallNumber?.toPhoneNumber()?.toStringFormat()?.let { number ->
                    /** Adds call log to database. */
                    fun log(action: CallEntity.Action?) = application.callRepository.insertItem(
                        CallEntity(null, number.toPhoneNumber(), action, LocalDateTime.now())
                    )
                    // Get, log and apply action for given phone number.
                    when (application.ruleRepository.getItemActionForPhoneNumber(number)) {
                        // No action taken.
                        RuleEntity.Action.Allow -> {
                            respondToCall(callDetails, CallResponse.Builder().build())
                            log(CallEntity.Action.Allow)
                        }
                        // Warn about call.
                        RuleEntity.Action.Warn -> {
                            respondToCall(callDetails, CallResponse.Builder().build())
                            withContext(Dispatchers.Main) {
                                showToast(getString(R.string.call_broadcast_warning_message))
                            }
                            log(CallEntity.Action.Warn)
                        }
                        // End the call and skip any notification about it if possible.
                        RuleEntity.Action.Block -> {
                            respondToCall(callDetails, CallResponse.Builder().apply {
                                setDisallowCall(true)
                                setRejectCall(true)
                                setSkipNotification(true)
                                setSkipCallLog(true)
                            }.build())
                            log(CallEntity.Action.Block)
                        }
                    }
                }
            }
        }
    }

}