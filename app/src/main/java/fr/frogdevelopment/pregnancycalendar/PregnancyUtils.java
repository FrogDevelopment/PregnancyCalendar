package fr.frogdevelopment.pregnancycalendar;

import android.support.annotation.NonNull;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

public class PregnancyUtils {

    // http://aly-abbara.com/utilitaires/calendrier/calendrier_de_grossesse.html
    // http://aly-abbara.com/utilitaires/calendrier/calculatrice_age_de_grossesse.html

    // http://www.guidegrossesse.com/grossesse/duree-d-une-grossesse.htm

    // fixme http://naitreetgrandir.com/fr/grossesse/trimestre1/fiche.aspx?doc=duree-grossesse

    private static final int GESTATION_LENGTH_MIN = 280;
    private static final int GESTATION_LENGTH_MAX = 290;
    private static final int DAYS_TO_FECONDATION = 14;

    static final int NB_DAYS_CONCEPTION_TO_BIRTH = GESTATION_LENGTH_MAX-DAYS_TO_FECONDATION;

    @NonNull
    public static LocalDate getAmenorrheaDate(LocalDate conceptionDate) {
        return conceptionDate.minusDays(DAYS_TO_FECONDATION);
    }

    @NonNull
    public static LocalDate getConceptionDate(LocalDate amenorrheaDate) {
        return amenorrheaDate.plusDays(DAYS_TO_FECONDATION);
    }

    @NonNull
    public static LocalDate getBirthRangeStart(LocalDate amenorrheaDate) {
        return amenorrheaDate.plusDays(GESTATION_LENGTH_MIN);
    }

    @NonNull
    public static LocalDate getBirthRangeEnd(LocalDate amenorrheaDate) {
        return amenorrheaDate.plusDays(GESTATION_LENGTH_MAX);
    }

    @NonNull
    public static long getCurrentMonth(LocalDate conceptionDate, LocalDate now) {
        return ChronoUnit.MONTHS.between(conceptionDate, now) + 1; // +1 car idem mais pour le mois
    }

    @NonNull
    public static long getCurrentWeek(LocalDate amenorrheaDate, LocalDate now) {
        return ChronoUnit.WEEKS.between(amenorrheaDate, now);
    }

    static LocalDate amenorrheaDate;
    static LocalDate conceptionDate;

    static final int AMENORRHEA = 0;
    static final int CONCEPTION = 1;

    public static void setDates(int year, int month, int day, int type) {
        if (type == AMENORRHEA) {
            amenorrheaDate = LocalDate.of(year, month, day);
            conceptionDate = getConceptionDate(amenorrheaDate);
        } else {
            conceptionDate = LocalDate.of(year, month, day);
            amenorrheaDate = getAmenorrheaDate(conceptionDate);
        }
    }

}
