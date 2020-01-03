package fr.frogdevelopment.pregnancycalendar.data;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;

import static java.time.ZoneId.systemDefault;

public class LocalDateTimeConverter {

    @TypeConverter
    public static LocalDateTime toDate(Long date) {
        if (date == null) {
            return null;
        } else {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(date), systemDefault());
        }
    }

    @TypeConverter
    public static Long toSqlDate(LocalDateTime date) {
        if (date == null) {
            return null;
        } else {
            return date.atZone(systemDefault()).toEpochSecond();
        }
    }
}
