package fr.frogdevelopment.pregnancycalendar.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker.Builder
import com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds
import fr.frogdevelopment.pregnancycalendar.R
import fr.frogdevelopment.pregnancycalendar.R.string
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.getAmenorrheaDate
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.getBirthRangeEnd
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.getBirthRangeStart
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.getConceptionDate
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.getCurrentMonth
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.getCurrentTrimester
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.getCurrentWeek
import org.apache.commons.lang3.StringUtils
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var amenorrheaDateValue: TextView
    private lateinit var conceptionDateValue: TextView
    private lateinit var currentWeek: TextView
    private lateinit var currentMonth: TextView
    private lateinit var currentTrimester: TextView
    private lateinit var birthRangeStart: TextView
    private lateinit var birthRangeEnd: TextView

    private lateinit var mSharedPref: SharedPreferences
    private lateinit var typeDate: String

    private var mSelectedDate: Long = 0
    private var mDateValue: String? = null
    private var mMyDate: LocalDate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.home_fragment, container, false)
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        amenorrheaDateValue = rootView.findViewById(R.id.amenorrhea_date_value)
        conceptionDateValue = rootView.findViewById(R.id.conception_date_value)
        currentWeek = rootView.findViewById(R.id.current_week_value)
        currentMonth = rootView.findViewById(R.id.current_month_value)
        currentTrimester = rootView.findViewById(R.id.current_trimester_value)
        birthRangeStart = rootView.findViewById(R.id.birth_range_start)
        birthRangeEnd = rootView.findViewById(R.id.birth_range_end)

        // todo retro-compatibility, to be remove later
        if (mSharedPref.contains(OLD_TYPE_DATE)) {
            val oldTypeDate = mSharedPref.getInt(OLD_TYPE_DATE, 1)
            typeDate = getString(if (oldTypeDate == 0) string.settings_type_amenorrhea_value else string.settings_type_conception_value)

            mSharedPref.edit()
                    .remove(OLD_TYPE_DATE)
                    .putString(TYPE_DATE, typeDate)
                    .apply()
        } else {
            typeDate = mSharedPref.getString(TYPE_DATE, getString(string.settings_type_conception_value))!!
        }

        // todo retro-compatibility, to be remove later
        if (mSharedPref.contains(OLD_SELECTED_DATE)) {
            mDateValue = mSharedPref.getString(OLD_SELECTED_DATE, null)
            if (StringUtils.isNotBlank(mDateValue)) {
                setSelectedDate(LocalDate.parse(mDateValue, LONG_DATE_FORMATTER).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())
            }
            mSharedPref.edit()
                    .remove(OLD_SELECTED_DATE)
                    .putLong(SELECTED_DATE, mSelectedDate)
                    .apply()
        } else {
            if (mSharedPref.contains(SELECTED_DATE)) {
                setSelectedDate(mSharedPref.getLong(SELECTED_DATE, todayInUtcMilliseconds()))
            } else {
                showSelectDateDialog()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_calendar) {
            showSelectDateDialog()
            return true
        }
        return false
    }

    private fun showSelectDateDialog() {
        val today = todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC))
        calendar.clear()
        calendar.timeInMillis = today
        calendar.add(Calendar.MONTH, -9)
        val nineMonthsAgo = calendar.timeInMillis
        val openSelection = if (mSelectedDate > 0) mSelectedDate else today

        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setStart(nineMonthsAgo)
        constraintsBuilder.setEnd(today)
        constraintsBuilder.setOpenAt(openSelection)

        val datePicker = Builder.datePicker()
                .setTitleText(if (typeDate == getString(string.settings_type_amenorrhea_value)) string.home_calendar_title_amenorrhea else string.home_calendar_title_conception)
                .setSelection(openSelection)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
        datePicker.addOnPositiveButtonClickListener { selection: Long -> save(selection) }
        datePicker.showNow(childFragmentManager, datePicker.toString())
    }

    private fun save(selection: Long) {
        mSharedPref
                .edit()
                .putLong(SELECTED_DATE, selection)
                .apply()
        setSelectedDate(selection)
    }

    private fun setSelectedDate(selection: Long) {
        mSelectedDate = selection
        val zonedDateTime = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault())
        mDateValue = LONG_DATE_FORMATTER.format(zonedDateTime)
        mMyDate = zonedDateTime.toLocalDate()

        processDate()
    }

    private fun processDate() {
        val amenorrheaDate: LocalDate
        val conceptionDate: LocalDate

        if (typeDate == getString(string.settings_type_amenorrhea_value)) {
            amenorrheaDate = mMyDate!!
            conceptionDate = getConceptionDate(requireContext(), mMyDate!!)
        } else {
            amenorrheaDate = getAmenorrheaDate(requireContext(), mMyDate!!)
            conceptionDate = mMyDate!!
        }

        amenorrheaDateValue.text = LONG_DATE_FORMATTER.format(amenorrheaDate)
        conceptionDateValue.text = LONG_DATE_FORMATTER.format(conceptionDate)

        val currentWeek = getCurrentWeek(amenorrheaDate)
        this.currentWeek.text = Html.fromHtml(resources.getQuantityString(R.plurals.home_week_n, currentWeek, currentWeek), Html.FROM_HTML_MODE_LEGACY)
        val currentMonth = getCurrentMonth(conceptionDate)

        this.currentMonth.text = Html.fromHtml(resources.getQuantityString(R.plurals.home_month_n, currentMonth, currentMonth), Html.FROM_HTML_MODE_LEGACY)
        val currentTrimester = getCurrentTrimester(currentWeek)

        this.currentTrimester.text = Html.fromHtml(resources.getQuantityString(R.plurals.home_trimester_n, currentTrimester, currentTrimester), Html.FROM_HTML_MODE_LEGACY)

        birthRangeStart.text = getBirthRangeStart(requireContext(), amenorrheaDate).format(LONG_DATE_FORMATTER)
        birthRangeEnd.text = getBirthRangeEnd(requireContext(), amenorrheaDate).format(LONG_DATE_FORMATTER)
    }

    companion object {
        private val LONG_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        private const val SELECTED_DATE = "selected_date"
        private const val TYPE_DATE = "pref_key_type"

        // todo retro-compatibility, to be remove later
        private const val OLD_SELECTED_DATE = "my_date"
        private const val OLD_TYPE_DATE = "type_date"
    }
}