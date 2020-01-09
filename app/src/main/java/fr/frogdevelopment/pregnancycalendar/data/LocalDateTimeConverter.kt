package fr.frogdevelopment.pregnancycalendar.data

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

object LocalDateTimeConverter {
    @JvmStatic
    @TypeConverter
    fun toDate(date: Long?): ZonedDateTime? {
        return if (date == null) {
            null
        } else {
            Instant.ofEpochSecond(date).atZone(ZoneId.systemDefault())
        }
    }

    @JvmStatic
    @TypeConverter
    fun toSqlDate(date: ZonedDateTime?): Long? {
        return date?.toEpochSecond()
    }
}