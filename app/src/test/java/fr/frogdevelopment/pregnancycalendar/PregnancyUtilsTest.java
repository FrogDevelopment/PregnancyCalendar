package fr.frogdevelopment.pregnancycalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.threeten.bp.LocalDate;

import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils;

import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_DAYS_TO_FECUNDATION;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MAX;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MIN;
import static org.mockito.BDDMockito.given;

@RunWith(JUnit4.class)
public class PregnancyUtilsTest {

	@Mock
	private Context context;
	@Mock
	private Resources mMockResources;

	@Mock
	private SharedPreferences mMockSharedPreferences;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		given(context.getResources())
				.willReturn(mMockResources);
	}

	@Test
	public void test_getAmenorrheaDate_use_default() {
        int nbDays = 14;
        LocalDate now = LocalDate.now();

        Mockito.doReturn(null)
                .when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);
        Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.settings_days_to_fecundation_default);

        LocalDate amenorrheaDate = PregnancyUtils.getAmenorrheaDate(context, now);

        Assert.assertEquals(now.minusDays(nbDays), amenorrheaDate);
    }

	@Test
	public void test_getAmenorrheaDate() {
		String nbDays = "14";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);

		LocalDate amenorrheaDate = PregnancyUtils.getAmenorrheaDate(context, now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.minusDays(Integer.parseInt(nbDays)), amenorrheaDate);
	}

	@Test
	public void test_getConceptionDate_use_default() {
        int nbDays = 14;
        LocalDate now = LocalDate.now();

        Mockito.doReturn(null).when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);
        Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.settings_days_to_fecundation_default);

        LocalDate conceptionDate = PregnancyUtils.getConceptionDate(context, now);

        Assert.assertEquals(now.plusDays(nbDays), conceptionDate);
    }

	@Test
	public void test_getConceptionDate() {
		String nbDays = "14";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);

		LocalDate conceptionDate = PregnancyUtils.getConceptionDate(context, now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.plusDays(Integer.parseInt(nbDays)), conceptionDate);
	}

	@Test
	public void test_getBirthRangeStart_use_default() {
        int nbDays = 280;
        LocalDate now = LocalDate.now();

        Mockito.doReturn(null).when(mMockSharedPreferences).getString(KEY_GESTATION_MIN, null);
        Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.settings_gestation_min_default);

        LocalDate birthRangeStart = PregnancyUtils.getBirthRangeStart(context, now);

        Assert.assertEquals(now.plusDays(nbDays), birthRangeStart);
    }

	@Test
	public void test_getBirthRangeStart() {
		String nbDays = "280";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_GESTATION_MIN, null);

		LocalDate birthRangeStart = PregnancyUtils.getBirthRangeStart(context, now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.plusDays(Integer.parseInt(nbDays)), birthRangeStart);
	}

	@Test
	public void test_getBirthRangeEnd_use_default() {
        int nbDays = 290;
        LocalDate now = LocalDate.now();

        Mockito.doReturn(null).when(mMockSharedPreferences).getString(KEY_GESTATION_MAX, null);
        Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.settings_gestation_max_default);

        LocalDate birthRangeEnd = PregnancyUtils.getBirthRangeEnd(context, now);

        Assert.assertEquals(now.plusDays(nbDays), birthRangeEnd);
    }

	@Test
	public void test_getBirthRangeEnd() {
		String nbDays = "290";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_GESTATION_MAX, null);

		LocalDate birthRangeEnd = PregnancyUtils.getBirthRangeEnd(context, now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.plusDays(Integer.parseInt(nbDays)), birthRangeEnd);
	}

	@Test
	public void test_getCurrentMonth() {
		long currentMonth = PregnancyUtils.getCurrentMonth(LocalDate.now().minusMonths(3));

		Assert.assertEquals(currentMonth, 4);
	}

	@Test
	public void test_getCurrentWeek() {
		long currentWeek = PregnancyUtils.getCurrentWeek(LocalDate.now().minusWeeks(3));

		Assert.assertEquals(currentWeek, 4);
	}
}