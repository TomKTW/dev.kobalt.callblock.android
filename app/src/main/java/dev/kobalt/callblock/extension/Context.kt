@file:Suppress("DEPRECATION")

package dev.kobalt.callblock.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.role.RoleManager
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
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import dev.kobalt.callblock.main.MainApplication
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*

/** Instance of main application. */
val Context.application get() = applicationContext as MainApplication

/** Instance of telecom manager. */
val Context.telecomManager get() = getSystemService<TelecomManager>()!!

/** Instance of telephony manager. */
val Context.telephonyManager get() = getSystemService<TelephonyManager>()!!

/** Instance of role manager. */
val Context.roleManager get() = getSystemService<RoleManager>()!!

/** Instance of input method manager. */
val Context.inputMethodManager get() = getSystemService<InputMethodManager>()!!

/** Displays a toast message. */
fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(applicationContext, message, length).show()
}

// Permission check is done before this method is run, but adding it in here makes it ugly to use.
@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("MissingPermission")
/** Terminate the call if it's active.*/
private fun TelecomManager.terminateCallIfActive() = if (isInCall) endCall() else false

/** Terminates currently active call and returns true if termination was successful. */
fun Context.terminateCall(): Boolean {
    return when {
        // On Android P+, require phone call answer permission before ending the call.
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> when {
            isPermissionGranted(Manifest.permission.ANSWER_PHONE_CALLS) -> telecomManager.terminateCallIfActive()
            else -> false
        }
        // On Android O-, terminate the call through reflection. This may fail due to API restrictions.
        else -> return runCatching {
            val telephonyManager = this.telephonyManager
            val telephonyClass = Class.forName(telephonyManager.javaClass.name)
            val methodGetITelephony = telephonyClass.getDeclaredMethod("getITelephony").also {
                it.isAccessible = true
            }
            val iTelephony = methodGetITelephony.invoke(telephonyManager)
            val iTelephonyClass = Class.forName(iTelephony.javaClass.name)
            iTelephonyClass.getDeclaredMethod("endCall").invoke(iTelephony)
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

/** Returns true if this application is treated as default dialer. */
fun Context.isDefaultDialer() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
        telecomManager.defaultDialerPackage == packageName
    }
    else -> {
        false
    }
}

fun Context.hasCallScreeningRole() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
        roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }
    else -> {
        false
    }
}

/** Returns true if it meets all conditions to screen calls. */
fun Context.isGrantedForCallScreening() = when {
    // Android Q+ Requires at least having call screening role or permissions.
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> areAllPermissionsGranted(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS
    ) || hasCallScreeningRole()
    // Android P+ Requires at least being a default dialer or permissions.
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> areAllPermissionsGranted(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS
    ) || isDefaultDialer()
    // Android O Requires being default dialer, permissions are not enough.
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> isDefaultDialer()
    // Android N+ has call screening that should be used when default dialer is enabled.
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> areAllPermissionsGranted(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.CALL_PHONE
    ) || isDefaultDialer()
    // Android M+ has runtime permissions that require following permissions to be granted.
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> areAllPermissionsGranted(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.CALL_PHONE
    )
    // Android L+ should check at least if application has these permissions.
    else -> areAllPermissionsGranted(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.CALL_PHONE
    )
}

/** Returns true if it meets all conditions to allow incoming calls from contacts only. */
fun Context.isGrantedToAllowContactCallsOnly() =
    isGrantedForCallScreening() && areAllPermissionsGranted(
        Manifest.permission.READ_CONTACTS
    )

/** Returns normalized phone number in E164 format. */
fun Context.normalizePhoneNumber(value: String): String? {
    return application.phoneNumberUtil.parse(value, Locale.getDefault().country)
        ?.let { application.phoneNumberUtil.format(it, PhoneNumberUtil.PhoneNumberFormat.E164) }
}

/** Returns phone number in international format. */
fun Context.internationalPhoneNumber(value: String): String? {
    return application.phoneNumberUtil.parse(value, Locale.getDefault().country)?.let {
        application.phoneNumberUtil.format(
            it,
            PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
        )
    }
}