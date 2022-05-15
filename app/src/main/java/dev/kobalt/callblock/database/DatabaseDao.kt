package dev.kobalt.callblock.database

import androidx.room.*

/** Data access object with basic methods (insert, update, delete and upsert). */
@Dao
abstract class DatabaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(obj: List<T>?): List<Long>

    @Update
    abstract fun update(obj: T)

    @Update
    abstract fun update(obj: List<T>?)

    @Delete
    abstract fun delete(obj: T)

    @Transaction
    open fun upsert(obj: T) {
        val id = insert(obj)
        if (id == -1L) update(obj)
    }

    @Transaction
    open fun upsert(objList: List<T>) {
        val insertResult = insert(objList)
        val updateList: MutableList<T> = ArrayList()
        insertResult.indices.forEach { if (insertResult[it] == (-1).toLong()) updateList.add(objList[it]) }
        if (updateList.isNotEmpty()) update(updateList)
    }

}