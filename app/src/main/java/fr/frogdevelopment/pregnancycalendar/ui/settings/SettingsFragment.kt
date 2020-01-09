package fr.frogdevelopment.pregnancycalendar.ui.settings

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import fr.frogdevelopment.pregnancycalendar.BuildConfig.VERSION_NAME
import fr.frogdevelopment.pregnancycalendar.R
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_DAYS_TO_FECUNDATION
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MAX
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.KEY_GESTATION_MIN

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setAsNumberType(KEY_GESTATION_MIN)
        setAsNumberType(KEY_GESTATION_MAX)
        setAsNumberType(KEY_DAYS_TO_FECUNDATION)

        setSummary("settings_version", VERSION_NAME)
    }

    private fun setAsNumberType(key: String) {
        findPreference<EditTextPreference>(key)?.setOnBindEditTextListener { editText: EditText -> editText.inputType = InputType.TYPE_CLASS_NUMBER }
    }

    private fun setSummary(key: String, summary: String) {
        findPreference<Preference>(key)?.summary = summary
    }
}