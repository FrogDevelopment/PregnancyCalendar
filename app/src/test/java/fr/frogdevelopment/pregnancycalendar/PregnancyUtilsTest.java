package fr.frogdevelopment.pregnancycalendar;

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

import java.time.LocalDate;

import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils;

import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_DAYS_TO_FECUNDATION;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MAX;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MIN;

@RunWith(JUnit4.class)
public class PregnancyUtilsTest {

	private PregnancyUtils pregnancyUtils;

	@Mock
	private Resources mMockResources;

	@Mock
	private SharedPreferences mMockSharedPreferences;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		pregnancyUtils = new PregnancyUtils(mMockResources, mMockSharedPreferences);
	}

	@Test
	public void test_getAmenorrheaDate_use_default() {
		int nbDays = 14;
		LocalDate now = LocalDate.now();

		Mockito.doReturn(null).when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);
		Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.default_days_to_fecundation);

		LocalDate amenorrheaDate = pregnancyUtils.getAmenorrheaDate(now);

		Assert.assertEquals(now.minusDays(nbDays), amenorrheaDate);
	}

	@Test
	public void test_getAmenorrheaDate() {
		String nbDays = "14";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);

		LocalDate amenorrheaDate = pregnancyUtils.getAmenorrheaDate(now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.minusDays(Integer.parseInt(nbDays)), amenorrheaDate);
	}

	@Test
	public void test_getConceptionDate_use_default() {
		int nbDays = 14;
		LocalDate now = LocalDate.now();

		Mockito.doReturn(null).when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);
		Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.default_days_to_fecundation);

		LocalDate conceptionDate = pregnancyUtils.getConceptionDate(now);

		Assert.assertEquals(now.plusDays(nbDays), conceptionDate);
	}

	@Test
	public void test_getConceptionDate() {
		String nbDays = "14";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_DAYS_TO_FECUNDATION, null);

		LocalDate conceptionDate = pregnancyUtils.getConceptionDate(now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.plusDays(Integer.parseInt(nbDays)), conceptionDate);
	}

	@Test
	public void test_getBirthRangeStart_use_default() {
		int nbDays = 280;
		LocalDate now = LocalDate.now();

		Mockito.doReturn(null).when(mMockSharedPreferences).getString(KEY_GESTATION_MIN, null);
		Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.default_gestation_min);

		LocalDate birthRangeStart = pregnancyUtils.getBirthRangeStart(now);

		Assert.assertEquals(now.plusDays(nbDays), birthRangeStart);
	}

	@Test
	public void test_getBirthRangeStart() {
		String nbDays = "280";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_GESTATION_MIN, null);

		LocalDate birthRangeStart = pregnancyUtils.getBirthRangeStart(now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.plusDays(Integer.parseInt(nbDays)), birthRangeStart);
	}

	@Test
	public void test_getBirthRangeEnd_use_default() {
		int nbDays = 290;
		LocalDate now = LocalDate.now();

		Mockito.doReturn(null).when(mMockSharedPreferences).getString(KEY_GESTATION_MAX, null);
		Mockito.doReturn(nbDays).when(mMockResources).getInteger(R.integer.default_gestation_max);

		LocalDate birthRangeEnd = pregnancyUtils.getBirthRangeEnd(now);

		Assert.assertEquals(now.plusDays(nbDays), birthRangeEnd);
	}

	@Test
	public void test_getBirthRangeEnd() {
		String nbDays = "290";
		LocalDate now = LocalDate.now();

		Mockito.doReturn(nbDays).when(mMockSharedPreferences).getString(KEY_GESTATION_MAX, null);

		LocalDate birthRangeEnd = pregnancyUtils.getBirthRangeEnd(now);

		Mockito.verifyZeroInteractions(mMockResources);

		Assert.assertEquals(now.plusDays(Integer.parseInt(nbDays)), birthRangeEnd);
	}

	@Test
	public void test_getCurrentMonth() {
		long currentMonth = pregnancyUtils.getCurrentMonth(LocalDate.now().minusMonths(3));

		Assert.assertEquals(currentMonth, 4);
	}

	@Test
	public void test_getCurrentWeek() {
		long currentWeek = pregnancyUtils.getCurrentWeek(LocalDate.now().minusWeeks(3));

		Assert.assertEquals(currentWeek, 4);
	}
}