package dev.kobalt.callblock.call

import android.content.Context
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

    override fun onScreenCall(callDetails: Call.Details) {
        applicationContext.application.apply {
            scope.launch(Dispatchers.IO) {
                // Normalize number value.
                callDetails.handle.schemeSpecificPart?.toPhoneNumber()?.toStringFormat()
                    ?.let { number ->
                        fun logCall(context: Context, number: String, action: CallEntity.Action) {
                            context.application.callRepository.insertItem(
                                CallEntity(
                                    null,
                                    number.toPhoneNumber(),
                                    action,
                                    LocalDateTime.now()
                                )
                            )
                        }

                        suspend fun terminateCall() {
                            withContext(Dispatchers.Main) {
                                respondToCall(
                                    callDetails,
                                    CallResponse.Builder().setDisallowCall(true).setRejectCall(true)
                                        .setSkipNotification(true).setSkipCallLog(true).build()
                                )
                            }
                            logCall(applicationContext, number, CallEntity.Action.Block)
                        }

                        suspend fun warnCall() {
                            withContext(Dispatchers.Main) {
                                applicationContext.showToast(
                                    applicationContext.getString(
                                        R.string.call_broadcast_warning_message
                                    )
                                )
                                respondToCall(
                                    callDetails,
                                    CallResponse.Builder().build()
                                )
                            }
                            logCall(applicationContext, number, CallEntity.Action.Warn)
                        }

                        fun allowCall() {
                            respondToCall(
                                callDetails,
                                CallResponse.Builder().build()
                            )
                            logCall(applicationContext, number, CallEntity.Action.Allow)
                        }
                        when (ruleRepository.getItemActionForPhoneNumber(number)) {
                            RuleEntity.Action.Warn -> warnCall()
                            RuleEntity.Action.Block -> terminateCall()
                            RuleEntity.Action.Allow -> allowCall()
                        }
                    }
            }
        }
    }

}