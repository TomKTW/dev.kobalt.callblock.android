package dev.kobalt.callblock.call

/** Repository for logged calls. */
class CallRepository {

    /** Data access object for calls. */
    lateinit var dao: CallDao

    /** Adds new call to database. */
    fun insertItem(item: CallEntity) {
        dao.insert(item)
    }

}