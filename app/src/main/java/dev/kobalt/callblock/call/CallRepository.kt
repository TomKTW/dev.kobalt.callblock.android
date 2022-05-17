package dev.kobalt.callblock.call

import dev.kobalt.callblock.main.MainApplication


/** Repository for logged calls. */
class CallRepository {

    /** Reference to main application. */
    lateinit var application: MainApplication

    /** Data access object for calls. */
    private val dao get() = application.databaseManager.database.callDao()

    /** Adds new call to database. */
    fun insertItem(item: CallEntity) {
        dao.insert(item)
    }

}