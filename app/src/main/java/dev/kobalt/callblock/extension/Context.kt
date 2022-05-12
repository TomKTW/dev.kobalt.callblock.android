package dev.kobalt.callblock.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import dev.kobalt.callblock.main.MainApplication

/** Instance of main application. */
val Context.application get() = applicationContext as MainApplication

/** Instance of telecom manager. */
val Context.telecomManager get() = getSystemService<TelecomManager>()

/** Displays a toast message. */
fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}

/** Terminates currently active call. */
fun Context.terminateCall() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        telecomManager?.apply { @Suppress("DEPRECATION") if (isInCall) endCall() }
    }
}