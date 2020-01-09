package fr.frogdevelopment.pregnancycalendar.utils

import android.content.Context
import fr.frogdevelopment.pregnancycalendar.R
import java.util.concurrent.TimeUnit

object DateLabelUtils {

    private const val ONE_MINUTE = 60 /*seconds*/
    private const val ONE_HOUR = 60 /*minutes*/
    private const val ONE_DAY = 24 /*hours*/

    fun millisecondsToLabel(context: Context, duration: Long?): String? {
        if (duration == null) {
            return null
        }
        val label: String
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
        label = if (seconds < ONE_MINUTE) { // less than 1 minute
            context.getString(R.string.contraction_duration_seconds, seconds)
        } else { // more than 1 minute
            val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
            if (minutes < ONE_HOUR) { // less than 1 hour
                val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
                context.getString(R.string.contraction_duration_minutes, minutes, remainingSeconds)
            } else { // more than 1 hour
                val hours = TimeUnit.MINUTES.toHours(minutes)
                if (hours < ONE_DAY) { // less than 1 day
                    val remainingMinutes = minutes - TimeUnit.HOURS.toMinutes(hours)
                    context.getString(R.string.contraction_duration_hours, hours, remainingMinutes)
                } else { // more than 1 day
                    val days = TimeUnit.HOURS.toDays(hours)
                    val remainingHours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(days)
                    context.getString(R.string.contraction_duration_days, days, remainingHours)
                }
            }
        }

        return label
    }
}