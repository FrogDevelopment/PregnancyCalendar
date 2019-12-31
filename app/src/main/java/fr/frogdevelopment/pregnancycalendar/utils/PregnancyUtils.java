package fr.frogdevelopment.pregnancycalendar.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static fr.frogdevelopment.pregnancycalendar.R.integer.default_days_to_fecundation;
import static fr.frogdevelopment.pregnancycalendar.R.integer.default_gestation_max;
import static fr.frogdevelopment.pregnancycalendar.R.integer.default_gestation_min;

public class PregnancyUtils {

    public static final int AMENORRHEA = 0;
    public static final int CONCEPTION = 1;

    public static final String KEY_DAYS_TO_FECUNDATION = "pref_key_days_to_fecundation";
    public static final String KEY_GESTATION_MIN = "pref_key_gestation_min";
    public static final String KEY_GESTATION_MAX = "pref_key_gestation_max";

    private static int getValue(Context context, String keyDaysToFecundation, int id) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(keyDaysToFecundation, getDefaultValue(context, id));
    }

    private static int getDefaultValue(Context context, int id) {
        return context.getResources().getInteger(id);
    }

    // ****************************************************************************************
    // http://aly-abbara.com/utilitaires/calendrier/calendrier_de_grossesse.html
    // http://aly-abbara.com/utilitaires/calendrier/calculatrice_age_de_grossesse.html
    // http://www.guidegrossesse.com/grossesse/duree-d-une-grossesse.htm
    // http://naitreetgrandir.com/fr/grossesse/trimestre1/fiche.aspx?doc=duree-grossesse

    @NonNull
    public static LocalDate getAmenorrheaDate(Context context, LocalDate conceptionDate) {
        return conceptionDate.minusDays(getValue(context, KEY_DAYS_TO_FECUNDATION, default_days_to_fecundation));
    }

    @NonNull
    public static LocalDate getConceptionDate(Context context, LocalDate amenorrheaDate) {
        return amenorrheaDate.plusDays(getValue(context, KEY_DAYS_TO_FECUNDATION, default_days_to_fecundation));
    }

    @NonNull
    public static LocalDate getBirthRangeStart(Context context, LocalDate amenorrheaDate) {
        return amenorrheaDate.plusDays(getValue(context, KEY_GESTATION_MIN, default_gestation_min));
    }

    @NonNull
    public static LocalDate getBirthRangeEnd(Context context, LocalDate amenorrheaDate) {
        return amenorrheaDate.plusDays(getValue(context, KEY_GESTATION_MAX, default_gestation_max));
    }

    public static int getCurrentMonth(LocalDate conceptionDate) {
        return (int) (ChronoUnit.MONTHS.between(conceptionDate, LocalDate.now()) + 1);
        // +1 => current month (0 unit) is as 1
    }

    public static int getCurrentWeek(LocalDate amenorrheaDate) {
        return (int) (ChronoUnit.WEEKS.between(amenorrheaDate, LocalDate.now()) + 1);
        // +1 => current week (0 unit) is as 1
    }

    public static int getCurrentTrimester(int currentWeek) {
        int currentTrimester;
        if (currentWeek <= 14) {
            currentTrimester = 1;
        } else if (currentWeek <= 28) {
            currentTrimester = 2;
        } else {
            currentTrimester = 3;
        }

        return currentTrimester;
    }
}
