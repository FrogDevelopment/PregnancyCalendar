package fr.frogdevelopment.pregnancycalendar.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateLabelUtils {

    // fixme more generic for sec / min / h / j to be multi-languages
    public static String millisecondsToLabel(long duration) {
        String label;
        Locale locale = Locale.getDefault();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);

        if (seconds < 60) { // less than 1 minute
            label = String.format(locale, "%02dsec", seconds);
        } else { // more than 1 minute
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            if (minutes < 60) { // less than 1 hour
                label = String.format(locale, "%02dmin%02d", minutes, seconds - TimeUnit.MINUTES.toSeconds(minutes));
            } else { // more than 1 hour
                long hour = TimeUnit.MINUTES.toHours(minutes);
                if (hour < 24) { // less than 1 day
                    label = String.format(locale, "%02dh%02d", hour, minutes - TimeUnit.HOURS.toMinutes(hour));
                } else { // more than 1 day
                    long days = TimeUnit.HOURS.toDays(hour);
                    label = String.format(locale, "%02dj%02dh", days, TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(days));
                }
            }
        }

        return label;
    }
}
