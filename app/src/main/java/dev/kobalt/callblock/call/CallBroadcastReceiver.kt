package dev.kobalt.callblock.call

import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import dev.kobalt.callblock.base.BaseBroadcastReceiver
import dev.kobalt.callblock.extension.application
import dev.kobalt.callblock.extension.showToast
import dev.kobalt.callblock.extension.terminateCall

/** Broadcast receiver for incoming calls. */
class CallBroadcastReceiver : BaseBroadcastReceiver() {

    companion object {
        const val PHONE_STATE_INTENT_ACTION = "android.intent.action.PHONE_STATE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // Broadcast is received twice. Proceed with broadcast intent with incoming number.
        when (intent?.action) {
            PHONE_STATE_INTENT_ACTION -> @Suppress("DEPRECATION")
            intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)?.also { number ->
                // Proceed only if incoming call is ringing.
                if (intent.extras?.getString(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING) {
                    when {
                        // Proceed only if allowing contact calls.
                        context?.application?.preferencesRepository?.allowContactsOnly == true -> {
                            // Terminate if incoming call number does not exist in contact list.
                            if (!context.application.contactRepository.isNumberInContacts(number)) {
                                context.terminateCall()
                            }
                        }
                        // Proceed only if detecting suspicious calls.
                        context?.application?.preferencesRepository?.detectSuspicious == true -> {
                            // Warn if incoming call is suspicious or terminate if it's a scam.
                            when (context.application.callRepository.getState(number)) {
                                CallState.Suspicious -> context.showToast("Suspicious incoming call")
                                CallState.Scam -> context.terminateCall()
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

}