package dev.kobalt.callblock.call

import android.content.Context
import android.content.Intent
import android.os.Build
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

    @Suppress("DEPRECATION")
    /** Phone number of incoming call. Note: Broadcast may be received twice any only second one may have this value.*/
    private val Intent.incomingCallNumber
        get() = extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)

    /** Ring state of incoming call. It's true if incoming call is ringing. */
    private val Intent.isIncomingCallRinging
        get() = extras?.getString(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING

    override fun onReceive(context: Context, intent: Intent): Unit = context.run {
        when (intent.action) {
            "android.intent.action.PHONE_STATE" -> application.scope.launch(Dispatchers.IO) {
                // Proceed only if application is not default dialer on Android N+ (call screening service will take over).
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isDefaultDialer()) return@launch
                // Proceed only incoming call is ringing.
                if (intent.isIncomingCallRinging) {
                    // Normalize phone number value.
                    intent.incomingCallNumber?.let { normalizePhoneNumber(it) }?.let { number ->
                        /** Adds call log to database. */
                        fun log(action: CallEntity.Action?) = application.callRepository.insertItem(
                            CallEntity(null, number, action, LocalDateTime.now())
                        )
                        // Get, log and apply action for given phone number.
                        when (application.ruleRepository.getItemActionForPhoneNumber(number)) {
                            // No action taken.
                            RuleEntity.Action.Allow -> log(CallEntity.Action.Allow)
                            // Warn about call.
                            RuleEntity.Action.Warn -> {
                                withContext(Dispatchers.Main) {
                                    showToast(getString(R.string.call_broadcast_warning_message))
                                }
                                log(CallEntity.Action.Warn)
                            }
                            // Attempt to terminate call. In case of failure, log as undetermined action.
                            RuleEntity.Action.Block -> {
                                val success = withContext(Dispatchers.Main) {
                                    context.terminateCall()
                                }
                                log(if (success) CallEntity.Action.Block else null)
                            }
                        }
                    }
                }
            }
        }
    }
}
