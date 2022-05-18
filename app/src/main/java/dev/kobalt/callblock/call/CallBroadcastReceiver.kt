package dev.kobalt.callblock.call

import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import dev.kobalt.callblock.R
import dev.kobalt.callblock.base.BaseBroadcastReceiver
import dev.kobalt.callblock.extension.*
import dev.kobalt.callblock.rule.RuleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


/** Broadcast receiver for incoming calls. */
class CallBroadcastReceiver : BaseBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // If application is treated as default dialer, skip checking and let call screening service process the call request.
        if (context.isDefaultDialer()) {
            // Note that broadcast may be received twice. Second broadcast may contain phone number that will be normalized.
            if (intent.action == "android.intent.action.PHONE_STATE") context.application.scope.launch(
                Dispatchers.IO
            ) {
                // Proceed only if incoming call is ringing.
                if (intent.extras?.getString(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING) {
                    @Suppress("DEPRECATION")
                    // Convert number string to phone number and back to normalized string format for consistency purposes.
                    intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                        ?.toPhoneNumber()
                        ?.toStringFormat()?.let { number ->
                            // Get the action for given phone number to take specific action.
                            context.application.ruleRepository.getItemActionForPhoneNumber(number)
                                .let { action ->
                                    when (action) {
                                        RuleEntity.Action.Warn -> {
                                            val success = withContext(Dispatchers.Main) {
                                                context.showToast(
                                                    context.getString(
                                                        R.string.call_broadcast_warning_message
                                                    )
                                                )
                                                true
                                            }
                                            logCall(
                                                context,
                                                number,
                                                if (success) CallEntity.Action.Warn else CallEntity.Action.Allow
                                            )
                                        }
                                        RuleEntity.Action.Block -> {
                                            val success = withContext(Dispatchers.Main) {
                                                context.terminateCall()
                                            }
                                            logCall(
                                                context, number, if (success) {
                                                    CallEntity.Action.Block
                                                } else {
                                                    CallEntity.Action.Allow
                                                }
                                            )
                                        }
                                        RuleEntity.Action.Allow ->
                                            logCall(context, number, CallEntity.Action.Allow)
                                    }
                                }
                        }
                }

            }
        }
    }

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

}

