package dev.kobalt.callblock.database

import androidx.room.Room
import dev.kobalt.callblock.main.MainApplication

/** Manager for database. */
class DatabaseManager {

    /** Reference to main application. */
    var application: MainApplication? = null

    /** Database object, initialized on first use. */
    val database by lazy {
        Room.databaseBuilder(application!!, Database::class.java, "db").build()
    }

}