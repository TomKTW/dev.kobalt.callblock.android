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

}