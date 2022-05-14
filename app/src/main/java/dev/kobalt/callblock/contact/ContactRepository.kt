package dev.kobalt.callblock.contact

import android.net.Uri
import android.provider.BaseColumns
import android.provider.ContactsContract
import dev.kobalt.callblock.main.MainApplication

class ContactRepository {

    /** Reference to main application. */
    var application: MainApplication? = null

    /** Returns true if given number exists in contact list. */
    fun isNumberInContacts(number: String): Boolean {
        // Query looks up for phone number in contact list.
        return application?.contentResolver?.query(
            Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)
            ),
            arrayOf(BaseColumns._ID), null, null, null
        )?.use {
            // The number exists in contacts if cursor has at least single entry.
            it.count > 0
        } == true
    }

}