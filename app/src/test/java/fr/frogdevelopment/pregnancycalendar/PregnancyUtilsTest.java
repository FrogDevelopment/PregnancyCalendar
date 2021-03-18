package fr.frogdevelopment.pregnancycalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils;

import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_DAYS_TO_FECUNDATION;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MAX;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MIN;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Ignore("Fix the tests")
@RunWith(MockitoJUnitRunner.class)
public class PregnancyUtilsTest {

    private static final ZoneId UTC = ZoneId.of("UTC");

    @Mock
    private Context context;
    @Mock
    private Resources mMockResources;

    @Mock
    private SharedPreferences mMockSharedPreferences;

    @Before
    public void initMocks() {
        given(context.getResources())
                .willReturn(mMockResources);
    }

    @Test
    public void test_getAmenorrheaDate_use_default() {
        // given
        int nbDays = 14;
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_DAYS_TO_FECUNDATION, null))
                .willReturn(null);
        given(mMockResources.getString(R.integer.settings_days_to_fecundation_default))
                .willReturn(String.valueOf(nbDays));

        // when
        LocalDate amenorrheaDate = PregnancyUtils.getAmenorrheaDate(context, now);

        // then
        assertEquals(now.minusDays(nbDays), amenorrheaDate);
    }

    @Test
    public void test_getAmenorrheaDate() {
        // given
        String nbDays = "14";
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_DAYS_TO_FECUNDATION, null))
                .willReturn(nbDays);

        // when
        LocalDate amenorrheaDate = PregnancyUtils.getAmenorrheaDate(context, now);


        // then
        then(mMockResources).shouldHaveNoMoreInteractions();
        assertEquals(now.minusDays(Integer.parseInt(nbDays)), amenorrheaDate);
    }

    @Test
    public void test_getConceptionDate_use_default() {
        // given
        int nbDays = 14;
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_DAYS_TO_FECUNDATION, null))
                .willReturn(null);
        given(mMockResources
                .getInteger(R.integer.settings_days_to_fecundation_default))
                .willReturn(nbDays);

        // when
        LocalDate conceptionDate = PregnancyUtils.getConceptionDate(context, now);

        // then
        assertEquals(now.plusDays(nbDays), conceptionDate);
    }

    @Test
    public void test_getConceptionDate() {
        // given
        String nbDays = "14";
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_DAYS_TO_FECUNDATION, null))
                .willReturn(nbDays);

        // when
        LocalDate conceptionDate = PregnancyUtils.getConceptionDate(context, now);

        // then
        then(mMockResources).shouldHaveNoInteractions();
        assertEquals(now.plusDays(Integer.parseInt(nbDays)), conceptionDate);
    }

    @Test
    public void test_getBirthRangeStart_use_default() {
        // given
        int nbDays = 280;
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_GESTATION_MIN, null))
                .willReturn(null);
        given(mMockResources
                .getInteger(R.integer.settings_gestation_min_default))
                .willReturn(nbDays);

        // when
        LocalDate birthRangeStart = PregnancyUtils.getBirthRangeStart(context, now);

        // then
        assertEquals(now.plusDays(nbDays), birthRangeStart);
    }

    @Test
    public void test_getBirthRangeStart() {
        // given
        String nbDays = "280";
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_GESTATION_MIN, null))
                .willReturn(nbDays);

        // when
        LocalDate birthRangeStart = PregnancyUtils.getBirthRangeStart(context, now);

        // then
        then(mMockResources).shouldHaveNoInteractions();
        assertEquals(now.plusDays(Integer.parseInt(nbDays)), birthRangeStart);
    }

    @Test
    public void test_getBirthRangeEnd_use_default() {
        // given
        int nbDays = 290;
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_GESTATION_MAX, null))
        .willReturn(null);
        given(mMockResources
                .getInteger(R.integer.settings_gestation_max_default))
        .willReturn(nbDays);

        // when
        LocalDate birthRangeEnd = PregnancyUtils.getBirthRangeEnd(context, now);

        // then
        assertEquals(now.plusDays(nbDays), birthRangeEnd);
    }

    @Test
    public void test_getBirthRangeEnd() {
        // given
        String nbDays = "290";
        LocalDate now = LocalDate.now(UTC);

        given(mMockSharedPreferences
                .getString(KEY_GESTATION_MAX, null))
                .willReturn(nbDays);

        // when
        LocalDate birthRangeEnd = PregnancyUtils.getBirthRangeEnd(context, now);

        // then
        then(mMockResources).shouldHaveNoInteractions();
        assertEquals(now.plusDays(Integer.parseInt(nbDays)), birthRangeEnd);
    }

    @Test
    public void test_getCurrentMonth() {
        // given
        LocalDate conceptionDate = LocalDate.now(UTC).minusMonths(3);

        // when
        long currentMonth = PregnancyUtils.getCurrentMonth(conceptionDate);

        // then
        assertEquals(currentMonth, 4);
    }

    @Test
    public void test_getCurrentWeek() {
        // given
        LocalDate amenorrheaDate = LocalDate.now(UTC).minusWeeks(3);

        // when
        long currentWeek = PregnancyUtils.getCurrentWeek(amenorrheaDate);

        // then
        assertEquals(currentWeek, 4);
    }
}