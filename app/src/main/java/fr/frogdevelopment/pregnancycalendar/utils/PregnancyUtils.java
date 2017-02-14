package fr.frogdevelopment.pregnancycalendar.utils;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import fr.frogdevelopment.pregnancycalendar.R;

public class PregnancyUtils {

	public static final int AMENORRHEA = 0;
	public static final int CONCEPTION = 1;

	public static final String KEY_DAYS_TO_FECUNDATION = "pref_key_days_to_fecundation";
	public static final String KEY_GESTATION_MIN       = "pref_key_gestation_min";
	public static final String KEY_GESTATION_MAX       = "pref_key_gestation_max";

	private final Resources         mResources;
	private final SharedPreferences mSharedPref;

	public PregnancyUtils(Resources resources, SharedPreferences sharedPref) {
		this.mResources = resources;
		this.mSharedPref = sharedPref;
	}

	// ****************************************************************************************
	// http://aly-abbara.com/utilitaires/calendrier/calendrier_de_grossesse.html
	// http://aly-abbara.com/utilitaires/calendrier/calculatrice_age_de_grossesse.html

	// http://www.guidegrossesse.com/grossesse/duree-d-une-grossesse.htm

	// http://naitreetgrandir.com/fr/grossesse/trimestre1/fiche.aspx?doc=duree-grossesse

	@NonNull
	public LocalDate getAmenorrheaDate(LocalDate conceptionDate) {
		int daysToFecundation;
		String value = mSharedPref.getString(KEY_DAYS_TO_FECUNDATION, null);
		if (value != null) {
			daysToFecundation = Integer.parseInt(value);
		} else {
			daysToFecundation = mResources.getInteger(R.integer.default_days_to_fecundation);
		}
		return conceptionDate.minusDays(daysToFecundation);
	}

	@NonNull
	public LocalDate getConceptionDate(LocalDate amenorrheaDate) {
		int daysToFecundation;
		String value = mSharedPref.getString(KEY_DAYS_TO_FECUNDATION, null);
		if (value != null) {
			daysToFecundation = Integer.parseInt(value);
		} else {
			daysToFecundation = mResources.getInteger(R.integer.default_days_to_fecundation);
		}
		return amenorrheaDate.plusDays(daysToFecundation);
	}

	@NonNull
	public LocalDate getBirthRangeStart(LocalDate amenorrheaDate) {
		int gestationMin;
		String value = mSharedPref.getString(KEY_GESTATION_MIN, null);
		if (value != null) {
			gestationMin = Integer.parseInt(value);
		} else {
			gestationMin = mResources.getInteger(R.integer.default_gestation_min);
		}
		return amenorrheaDate.plusDays(gestationMin);
	}

	@NonNull
	public LocalDate getBirthRangeEnd(LocalDate amenorrheaDate) {
		int gestationMax;
		String value = mSharedPref.getString(KEY_GESTATION_MAX, null);
		if (value != null) {
			gestationMax = Integer.parseInt(value);
		} else {
			gestationMax = mResources.getInteger(R.integer.default_gestation_max);
		}
		return amenorrheaDate.plusDays(gestationMax);
	}

	public int getCurrentMonth(LocalDate conceptionDate) {
		return (int) (ChronoUnit.MONTHS.between(conceptionDate, LocalDate.now()) + 1);
		// +1 => current month (0 unit) is as 1
	}

	public int getCurrentWeek(LocalDate amenorrheaDate) {
		return (int) (ChronoUnit.WEEKS.between(amenorrheaDate, LocalDate.now()) + 1);
		// +1 => current week (0 unit) is as 1
	}

	public int getCurrentTrimester(int currentWeek) {
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