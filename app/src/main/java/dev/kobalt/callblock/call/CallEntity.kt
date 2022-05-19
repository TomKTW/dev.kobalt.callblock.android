package dev.kobalt.callblock.call

import androidx.room.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@TypeConverters(CallEntity.DataConverter::class)
@Entity(tableName = "call")
/** Entity for incoming call that contains phone number, taken action and timestamp when call has occurred. */
data class CallEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long?,
    @ColumnInfo(name = "number") val number: String?,
    @ColumnInfo(name = "action") val action: Action?,
    @ColumnInfo(name = "timestamp") val timestamp: LocalDateTime?
) {

    /** Given action enumerations from the rule. */
    enum class Action(val value: Int) {
        Allow(-1), Warn(0), Block(1);
    }

    /** Database type converter for actions, phone numbers and timestamps. */
    class DataConverter {

        @TypeConverter
        fun fromAction(value: Action?): Int? = value?.value

        @TypeConverter
        fun toAction(value: Int?): Action? = Action.values().find { it.value == value }

        @TypeConverter
        fun fromTimestamp(value: Long?): LocalDateTime? =
            value?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC) }

        @TypeConverter
        fun toTimestamp(value: LocalDateTime?): Long? =
            value?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }

}


