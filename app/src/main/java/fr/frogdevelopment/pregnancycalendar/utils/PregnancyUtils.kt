package fr.frogdevelopment.pregnancycalendar.utils

import android.content.Context
import androidx.preference.PreferenceManager
import fr.frogdevelopment.pregnancycalendar.R.integer
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object PregnancyUtils {

    const val AMENORRHEA = 0
    const val CONCEPTION = 1
    const val KEY_DAYS_TO_FECUNDATION = "pref_key_days_to_fecundation"
    const val KEY_GESTATION_MIN = "pref_key_gestation_min"
    const val KEY_GESTATION_MAX = "pref_key_gestation_max"

    private fun getValue(context: Context, keyDaysToFecundation: String, id: Int): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(keyDaysToFecundation, getDefaultValue(context, id))!!.toInt()
    }

    private fun getDefaultValue(context: Context, id: Int): String {
        return context.resources.getString(id)
    }

    // ****************************************************************************************
    // http://aly-abbara.com/utilitaires/calendrier/calendrier_de_grossesse.html
    // http://aly-abbara.com/utilitaires/calendrier/calculatrice_age_de_grossesse.html
    // http://www.guidegrossesse.com/grossesse/duree-d-une-grossesse.htm
    // http://naitreetgrandir.com/fr/grossesse/trimestre1/fiche.aspx?doc=duree-grossesse

    @JvmStatic
    fun getAmenorrheaDate(context: Context, conceptionDate: LocalDate): LocalDate {
        return conceptionDate.minusDays(getValue(context, KEY_DAYS_TO_FECUNDATION, integer.default_days_to_fecundation).toLong())
    }

    @JvmStatic
    fun getConceptionDate(context: Context, amenorrheaDate: LocalDate): LocalDate {
        return amenorrheaDate.plusDays(getValue(context, KEY_DAYS_TO_FECUNDATION, integer.default_days_to_fecundation).toLong())
    }

    @JvmStatic
    fun getBirthRangeStart(context: Context, amenorrheaDate: LocalDate): LocalDate {
        return amenorrheaDate.plusDays(getValue(context, KEY_GESTATION_MIN, integer.default_gestation_min).toLong())
    }

    @JvmStatic
    fun getBirthRangeEnd(context: Context, amenorrheaDate: LocalDate): LocalDate {
        return amenorrheaDate.plusDays(getValue(context, KEY_GESTATION_MAX, integer.default_gestation_max).toLong())
    }

    @JvmStatic
    fun getCurrentMonth(conceptionDate: LocalDate?): Int {
        return (ChronoUnit.MONTHS.between(conceptionDate, LocalDate.now()) + 1).toInt()
        // +1 => current month (0 unit) is as 1
    }

    @JvmStatic
    fun getCurrentWeek(amenorrheaDate: LocalDate?): Int {
        return (ChronoUnit.WEEKS.between(amenorrheaDate, LocalDate.now()) + 1).toInt()
        // +1 => current week (0 unit) is as 1
    }

    @JvmStatic
    fun getCurrentTrimester(currentWeek: Int): Int {
        return when {
            currentWeek <= 14 -> {
                1
            }
            currentWeek <= 28 -> {
                2
            }
            else -> {
                3
            }
        }
    }
}