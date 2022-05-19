package dev.kobalt.callblock.contact

import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.ContactsContract

class ContactRepository {

    /** Reference to context. */
    lateinit var context: Context

    /** Returns true if given number exists in contact list. */
    fun isNumberInContacts(number: String): Boolean {
        // Query looks up for phone number in contact list.
        return context.contentResolver?.query(
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