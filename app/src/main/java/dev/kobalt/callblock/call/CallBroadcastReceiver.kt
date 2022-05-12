package dev.kobalt.callblock.call

import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import dev.kobalt.callblock.base.BaseBroadcastReceiver

/** Broadcast receiver for incoming calls. */
class CallBroadcastReceiver : BaseBroadcastReceiver() {

    companion object {
        const val PHONE_STATE_INTENT_ACTION = "android.intent.action.PHONE_STATE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            PHONE_STATE_INTENT_ACTION -> {
                // Broadcast is received twice. Proceed with broadcast intent with incoming number.
                @Suppress("DEPRECATION")
                intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)?.also { number ->
                    when (intent.extras?.getString(TelephonyManager.EXTRA_STATE)) {
                        TelephonyManager.EXTRA_STATE_RINGING -> Log.d("Incoming", number)
                        TelephonyManager.EXTRA_STATE_IDLE -> Log.d("Hangup", number)
                        TelephonyManager.EXTRA_STATE_OFFHOOK -> Log.d("Active", number)
                    }
                }
            }
        }
    }

}