package dev.kobalt.callblock.extension

import dev.kobalt.callblock.main.MainApplication
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import java.util.*

/** Alias for phone number object. */
typealias PhoneNumber = Phonenumber.PhoneNumber

/** Returns phone number object parsed from string if it's a valid one. Otherwise, returns null. */
fun String.toPhoneNumber() = runCatching {
    PhoneNumberUtility.instance.parse(this, Locale.getDefault().country)
}.onFailure { it.printStackTrace() }.getOrNull()

/** Returns string value from phone number object using given format. Default used format is E164. */
fun PhoneNumber.toStringFormat(format: PhoneNumberUtil.PhoneNumberFormat = PhoneNumberUtil.PhoneNumberFormat.E164) =
    runCatching {
        PhoneNumberUtility.instance.format(this, format)
    }.onFailure { it.printStackTrace() }.getOrNull()

/** Singleton of phone number utilities, used to provide single instance of utilities to parse and format phone numbers in extensions. */
object PhoneNumberUtility {
    /**
     * Single instance of phone number utility for parsing and formatting phone numbers.
     * Global instance of application context is used to ensure that conversion process can be done without passing in context directly.
     */
    val instance by lazy { PhoneNumberUtil.createInstance(MainApplication.globalInstance)!! }
}