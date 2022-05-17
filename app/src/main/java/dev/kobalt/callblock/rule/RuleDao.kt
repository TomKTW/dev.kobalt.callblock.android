package dev.kobalt.callblock.rule

import androidx.room.Dao
import androidx.room.Query
import dev.kobalt.callblock.database.DatabaseDao
import kotlinx.coroutines.flow.Flow

@Dao
/** Data access object for call rules. */
abstract class RuleDao : DatabaseDao<RuleEntity>() {

    /** Returns a list of rules. */
    @Query("SELECT * FROM rule")
    abstract fun getList(): List<RuleEntity>?

    /** Returns a flow list of rules that are user-made, ordered by number. */
    @Query("SELECT * FROM rule WHERE from_user = 1 ORDER BY number")
    abstract fun getListUserOnlyFlow(): Flow<List<RuleEntity>>

    /** Returns a rule from given ID. */
    @Query("SELECT * FROM rule WHERE id IN (:id) LIMIT 1")
    abstract fun getItem(id: Long): RuleEntity?

    /** Returns a predefined rule from given phone number. */
    @Query("SELECT * FROM rule WHERE number IN (:number) AND from_user = 0 LIMIT 1")
    abstract fun getItemPredefinedOnlyByNumber(number: String): RuleEntity?

    /** Returns a user defined rule from given phone number. */
    @Query("SELECT * FROM rule WHERE number IN (:number) AND from_user = 1 LIMIT 1")
    abstract fun getItemUserOnlyByNumber(number: String): RuleEntity?

    /** Deletes a rule from given ID. */
    @Query("DELETE FROM rule WHERE id = :id")
    abstract fun deleteItemById(id: Long)

    /** Returns a flow count of predefined rules. */
    @Query("SELECT COUNT(id) FROM rule WHERE from_user = 0 ")
    abstract fun getCountPredefinedFlow(): Flow<Int>

    /** Returns a flow count of user defined rules. */
    @Query("SELECT COUNT(id) FROM rule WHERE from_user = 1")
    abstract fun getCountUserFlow(): Flow<Int>

}