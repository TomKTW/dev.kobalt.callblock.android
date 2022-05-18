package dev.kobalt.callblock.extension

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import dev.kobalt.callblock.main.MainApplication


/** Instance of main application. */
val Context.application get() = applicationContext as MainApplication

/** Instance of telecom manager. */
val Context.telecomManager get() = getSystemService<TelecomManager>()!!

/** Instance of telephony manager. */
val Context.telephonyManager get() = getSystemService<TelephonyManager>()!!

/** Instance of input method manager. */
val Context.inputMethodManager get() = getSystemService<InputMethodManager>()!!

/** Displays a toast message. */
fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, message, length).show()
}

/** Terminates currently active call. */
fun Context.terminateCall(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ANSWER_PHONE_CALLS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else {
            @Suppress("DEPRECATION")
            telecomManager.takeIf { it.isInCall }?.endCall() ?: false
        }
    } else {
        return runCatching {
            val telephonyManager = this.telephonyManager
            val classTelephonyManager = Class.forName(telephonyManager.javaClass.name)
            val methodGetITelephony =
                classTelephonyManager.getDeclaredMethod("getITelephony").also {
                    it.isAccessible = true
                }
            val telephony = methodGetITelephony.invoke(telephonyManager)
            val classTelephony = Class.forName(telephony.javaClass.name)
            classTelephony.getDeclaredMethod("endCall").invoke(telephony)
            true
        }.getOrElse {
            it.printStackTrace()
            false
        }
    }
}

/** Returns color value from resource ID. Used as a wrapper to avoid usage of given method. */
fun Context.getResourceColor(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

/** Returns drawable value from resource ID. Used as a wrapper to avoid usage of given method. */
fun Context.getResourceDrawable(@DrawableRes id: Int): Drawable {
    return AppCompatResources.getDrawable(this, id)!!
}

/** Wrapper extension for getting dimension value. Used as a wrapper to avoid usage of given method. */
fun Context.getDimensionValue(type: Int, value: Float): Float {
    return TypedValue.applyDimension(type, value, resources.displayMetrics)
}

/** Returns density independent unit value converted to pixels value. */
fun Context.dp(value: Int): Int {
    return getDimensionValue(TypedValue.COMPLEX_UNIT_DIP, value.toFloat()).toInt()
}

/** Returns scaled pixels unit value converted to pixels value. */
fun Context.sp(value: Int): Int {
    return getDimensionValue(TypedValue.COMPLEX_UNIT_SP, value.toFloat()).toInt()
}

/** Returns true if given permission is already granted. */
fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

/** Returns true if all given permissions are already granted. */
fun Context.areAllPermissionsGranted(vararg permissions: String): Boolean {
    return permissions.all { isPermissionGranted(it) }
}

/** Opens application info for given package name. */
fun Context.launchAppInfo(packageName: String = this.packageName) {
    startActivity(Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.parse("package:$packageName")
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    })
}

fun Context.isDefaultDialer() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    telecomManager.defaultDialerPackage == packageName
} else {
    false
}

fun Context.isGrantedForCallScreening() = areAllPermissionsGranted(
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.READ_CALL_LOG,
    Manifest.permission.CALL_PHONE
) && isDefaultDialer() && if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    isPermissionGranted(Manifest.permission.ANSWER_PHONE_CALLS)
} else true

fun Context.isGrantedToAllowContactCallsOnly() =
    isGrantedForCallScreening() && areAllPermissionsGranted(
        Manifest.permission.READ_CONTACTS
    )