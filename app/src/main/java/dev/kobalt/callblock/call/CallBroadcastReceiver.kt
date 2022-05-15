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

/** Broadcast receiver for incoming calls. */
class CallBroadcastReceiver : BaseBroadcastReceiver() {

    companion object {
        const val PHONE_STATE_INTENT_ACTION = "android.intent.action.PHONE_STATE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Broadcast is received twice. Proceed with broadcast intent with incoming number.
        if (intent.action == PHONE_STATE_INTENT_ACTION) context.application.scope.launch(Dispatchers.IO) {
            @Suppress("DEPRECATION")
            // Convert number string to phone number and back to normalized string format for consistency purposes.
            intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)?.toPhoneNumber()
                ?.toStringFormat()?.let { number ->
                    // Get the action for given phone number to take specific action.
                    context.application.ruleRepository.getItemActionForPhoneNumber(number).let {
                        launch(Dispatchers.Main) {
                            when (it) {
                                RuleEntity.Action.Warn -> context.showToast(context.getString(R.string.call_broadcast_warning_message))
                                RuleEntity.Action.Block -> context.terminateCall()
                                RuleEntity.Action.Allow -> {}
                            }
                        }
                    }
                }
        }
    }

}