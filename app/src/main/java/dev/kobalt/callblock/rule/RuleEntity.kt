package dev.kobalt.callblock.rule

import androidx.room.*

@TypeConverters(RuleEntity.DataConverter::class)
@Entity(tableName = "rule")
/**
 * Rule entity for incoming call. Apply given action if the phone number matches.
 * User-made rules have "fromUser" value as true to separate them from predefined rules.
 */
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long?,
    @ColumnInfo(name = "number") val number: String?,
    @ColumnInfo(name = "action") val action: Action?,
    @ColumnInfo(name = "from_user") val fromUser: Boolean?
) {

    /**
     * Action for given rule. Depending on matching phone number and given action,
     * the call can be allowed normally, warned with message or blocked by terminating the call.
     */
    enum class Action(val value: Int) {
        Allow(-1), Warn(0), Block(1);
    }

    /** Database type converter for actions and phone numbers. */
    class DataConverter {

        @TypeConverter
        fun fromAction(value: Action?): Int? = value?.value

        @TypeConverter
        fun toAction(value: Int?): Action? = Action.values().find { it.value == value }

    }

}


