package dev.kobalt.callblock.call

import androidx.room.Dao
import androidx.room.Query
import dev.kobalt.callblock.database.DatabaseDao
import kotlinx.coroutines.flow.Flow

@Dao
/** Data access object for logged calls. */
abstract class CallDao : DatabaseDao<CallEntity>() {

    /** Returns a flow list of calls ordered by timestamp. */
    @Query("SELECT * FROM call ORDER BY timestamp DESC LIMIT 100")
    abstract fun getListFlow(): Flow<List<CallEntity>>

    /** Returns a flow count of allowed calls. */
    @Query("SELECT COUNT(id) FROM call WHERE `action` = -1 ")
    abstract fun getCountAllowedFlow(): Flow<Int>

    /** Returns a flow count of warned calls. */
    @Query("SELECT COUNT(id) FROM call WHERE `action` = 0 ")
    abstract fun getCountWarnedFlow(): Flow<Int>

    /** Returns a flow count of blocked calls. */
    @Query("SELECT COUNT(id) FROM call WHERE `action` = 1 ")
    abstract fun getCountBlockedFlow(): Flow<Int>
}