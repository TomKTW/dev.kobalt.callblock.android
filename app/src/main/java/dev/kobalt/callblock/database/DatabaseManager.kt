package dev.kobalt.callblock.database

import android.content.Context
import androidx.room.Room

/** Manager for database. */
class DatabaseManager {

    /** Reference to context. */
    lateinit var context: Context

    /** Database object, initialized on first use. */
    val database by lazy {
        Room.databaseBuilder(context, Database::class.java, "db").build()
    }

}