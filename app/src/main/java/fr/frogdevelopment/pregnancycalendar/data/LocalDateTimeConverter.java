package fr.frogdevelopment.pregnancycalendar.data;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTimeConverter {

    @TypeConverter
    public static ZonedDateTime toDate(Long date) {
        if (date == null) {
            return null;
        } else {
            return Instant.ofEpochSecond(date).atZone(ZoneId.systemDefault());
        }
    }

    @TypeConverter
    public static Long toSqlDate(ZonedDateTime date) {
        if (date == null) {
            return null;
        } else {
            return date.toEpochSecond();
        }
    }
}
