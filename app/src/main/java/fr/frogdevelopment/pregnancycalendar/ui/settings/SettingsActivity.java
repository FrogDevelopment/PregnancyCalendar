package fr.frogdevelopment.pregnancycalendar.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.appcompat.app.AppCompatActivity;

import fr.frogdevelopment.pregnancycalendar.R;

import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_DAYS_TO_FECUNDATION;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MAX;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MIN;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//		ActionBar actionBar = getSupportActionBar();
//		if (actionBar != null) {
//			actionBar.setDisplayShowHomeEnabled(true);
//		}
//
//		getFragmentManager()
//				.beginTransaction()
//				.replace(android.R.id.content, new SettingsFragment())
//				.commit();
    }

    static public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            EditTextPreference preferenceMin = (EditTextPreference) findPreference(KEY_GESTATION_MIN);
            preferenceMin.setSummary(preferenceMin.getText());

            EditTextPreference preferenceMax = (EditTextPreference) findPreference(KEY_GESTATION_MAX);
            preferenceMax.setSummary(preferenceMax.getText());

            EditTextPreference preferenceDays = (EditTextPreference) findPreference(KEY_DAYS_TO_FECUNDATION);
            preferenceDays.setSummary(preferenceDays.getText());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // update info view
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference) {
                preference.setSummary(((EditTextPreference) preference).getText());
            }
        }
    }
}
