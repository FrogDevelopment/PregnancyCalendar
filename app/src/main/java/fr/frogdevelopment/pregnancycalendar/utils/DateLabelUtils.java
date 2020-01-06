package fr.frogdevelopment.pregnancycalendar.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import fr.frogdevelopment.pregnancycalendar.R;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class DateLabelUtils {

    public static final int ONE_MINUTE = 60/*seconds*/;
    public static final int ONE_HOUR = 60/*minutes*/;
    public static final int ONE_DAY = 24/*hours*/;

    public static String millisecondsToLabel(@NonNull Context context, long duration) {
        String label;
        long seconds = MILLISECONDS.toSeconds(duration);
        if (seconds < ONE_MINUTE) { // less than 1 minute
            label = context.getString(R.string.contraction_duration_seconds, seconds);
        } else { // more than 1 minute
            long minutes = MILLISECONDS.toMinutes(duration);
            if (minutes < ONE_HOUR) { // less than 1 hour
                long remainingSeconds = seconds - MINUTES.toSeconds(minutes);
                label = context.getString(R.string.contraction_duration_minutes, minutes, remainingSeconds);
            } else { // more than 1 hour
                long hours = MINUTES.toHours(minutes);
                if (hours < ONE_DAY) { // less than 1 day
                    long remainingMinutes = minutes - HOURS.toMinutes(hours);
                    label = context.getString(R.string.contraction_duration_hours, hours, remainingMinutes);
                } else { // more than 1 day
                    long days = HOURS.toDays(hours);
                    long remainingHours = MILLISECONDS.toHours(duration) - DAYS.toHours(days);
                    label = context.getString(R.string.contraction_duration_days, days, remainingHours);
                }
            }
        }

        return label;
    }
}
