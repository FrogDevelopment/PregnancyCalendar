package fr.frogdevelopment.pregnancycalendar.ui.settings;

import android.os.Bundle;
import android.text.InputType;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import fr.frogdevelopment.pregnancycalendar.R;

import static fr.frogdevelopment.pregnancycalendar.BuildConfig.VERSION_CODE;
import static fr.frogdevelopment.pregnancycalendar.BuildConfig.VERSION_NAME;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_DAYS_TO_FECUNDATION;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MAX;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MIN;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        setAsNumberType(KEY_GESTATION_MIN);
        setAsNumberType(KEY_GESTATION_MAX);
        setAsNumberType(KEY_DAYS_TO_FECUNDATION);

        Preference version = findPreference("settings_version");
        if (version != null) {
            version.setSummary(VERSION_NAME);
        }
        Preference build = findPreference("settings_build");
        if (build != null) {
            build.setSummary(String.valueOf(VERSION_CODE));
        }
    }

    private void setAsNumberType(String key) {
        EditTextPreference numberPreference = findPreference(key);
        if (numberPreference != null) {
            numberPreference.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        }
    }
}
