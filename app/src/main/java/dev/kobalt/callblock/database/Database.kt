package dev.kobalt.callblock.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.kobalt.callblock.call.CallDao
import dev.kobalt.callblock.call.CallEntity
import dev.kobalt.callblock.rule.RuleDao
import dev.kobalt.callblock.rule.RuleEntity

/** Database used to persist objects. */
@Database(
    entities = [RuleEntity::class, CallEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    /** Data access object for rule entities. */
    abstract fun ruleDao(): RuleDao

    /** Data access object for call entities. */
    abstract fun callDao(): CallDao

}