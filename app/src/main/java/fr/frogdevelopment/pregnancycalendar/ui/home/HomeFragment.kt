package fr.frogdevelopment.pregnancycalendar.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import fr.frogdevelopment.pregnancycalendar.R
import fr.frogdevelopment.pregnancycalendar.R.string
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.AMENORRHEA
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.CONCEPTION
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

    private var mTypeDate = 0
    private var dateTextView: TextView? = null
    private var birthRangeStart: TextView? = null
    private var birthRangeEnd: TextView? = null
    private var otherDateText: TextView? = null
    private var otherDateValue: TextView? = null
    private var currentWeek: TextView? = null
    private var currentMonth: TextView? = null
    private var currentTrimester: TextView? = null
    private var mSelectedDate: Long = 0
    private var mDateValue: String? = null
    private var mMyDate: LocalDate? = null
    private var mSharedPref: SharedPreferences? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.home_fragment, container, false)
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        dateTextView = rootView.findViewById(R.id.date_value)
        otherDateText = rootView.findViewById(R.id.other_date_text)
        otherDateValue = rootView.findViewById(R.id.other_date_value)
        currentWeek = rootView.findViewById(R.id.current_week_value)
        currentMonth = rootView.findViewById(R.id.current_month_value)
        currentTrimester = rootView.findViewById(R.id.current_trimester_value)
        birthRangeStart = rootView.findViewById(R.id.birth_range_start)
        birthRangeEnd = rootView.findViewById(R.id.birth_range_end)

        // todo retro-compatibility, to be remove later
        if (mSharedPref!!.contains("my_date")) {
            mDateValue = mSharedPref!!.getString("my_date", null)
            if (StringUtils.isNotBlank(mDateValue)) {
                setSelectedDate(LocalDate.parse(mDateValue, LONG_DATE_FORMATTER).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())
            }
            mSharedPref!!.edit().remove("my_date").apply()
        } else {
            setSelectedDate(mSharedPref!!.getLong("selected_date", MaterialDatePicker.todayInUtcMilliseconds()))
        }

//        if (mMyDate == null) { // fixme prompt to menu action
//        }

        mTypeDate = mSharedPref!!.getInt("type_date", CONCEPTION)
        val group: MaterialButtonToggleGroup = rootView.findViewById(R.id.toggle_button_group)
        group.check(if (mTypeDate == AMENORRHEA) R.id.toggle_amenorrhea else R.id.toggle_conception)
        group.addOnButtonCheckedListener { _: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (isChecked) {
                mTypeDate = if (checkedId == R.id.toggle_amenorrhea) AMENORRHEA else CONCEPTION
                refresh()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_calendar) {
            val today = MaterialDatePicker.todayInUtcMilliseconds()
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

            val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(openSelection)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()
            datePicker.addOnPositiveButtonClickListener { selection: Long -> setSelectedDate(selection) }
            datePicker.showNow(childFragmentManager, datePicker.toString())
            return true
        }
        return false
    }

    private fun setSelectedDate(selection: Long) {
        mSelectedDate = selection
        val zonedDateTime = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault())
        mDateValue = LONG_DATE_FORMATTER.format(zonedDateTime)
        mMyDate = zonedDateTime.toLocalDate()
        refresh()
    }

    private fun refresh() {
        if (mMyDate != null) {
            dateTextView!!.text = resources.getString(string.date_value, mDateValue)
            processDate()
            save()
        }
    }

    private fun processDate() {
        val amenorrheaDate: LocalDate?
        val conceptionDate: LocalDate?
        if (mTypeDate == AMENORRHEA) {
            amenorrheaDate = mMyDate
            conceptionDate = getConceptionDate(requireContext(), amenorrheaDate!!)
            otherDateText!!.text = getString(string.another_date_1)
            otherDateValue!!.text = conceptionDate.format(LONG_DATE_FORMATTER)
        } else {
            conceptionDate = mMyDate
            amenorrheaDate = getAmenorrheaDate(requireContext(), conceptionDate!!)
            otherDateText!!.text = getString(string.another_date_0)
            otherDateValue!!.text = amenorrheaDate.format(LONG_DATE_FORMATTER)
        }

        val currentWeek = getCurrentWeek(amenorrheaDate)
        this.currentWeek!!.text = Html.fromHtml(resources.getQuantityString(R.plurals.week_n, currentWeek, currentWeek), Html.FROM_HTML_MODE_LEGACY)
        val currentMonth = getCurrentMonth(conceptionDate)

        this.currentMonth!!.text = Html.fromHtml(resources.getQuantityString(R.plurals.month_n, currentMonth, currentMonth), Html.FROM_HTML_MODE_LEGACY)
        val currentTrimester = getCurrentTrimester(currentWeek)

        this.currentTrimester!!.text = Html.fromHtml(resources.getQuantityString(R.plurals.trimester_n, currentTrimester, currentTrimester), Html.FROM_HTML_MODE_LEGACY)

        birthRangeStart!!.text = getBirthRangeStart(requireContext(), amenorrheaDate).format(LONG_DATE_FORMATTER)
        birthRangeEnd!!.text = getBirthRangeEnd(requireContext(), amenorrheaDate).format(LONG_DATE_FORMATTER)
    }

    private fun save() {
        val editor = mSharedPref!!.edit()
        editor.putLong("selected_date", mSelectedDate)
        editor.putInt("type_date", mTypeDate)
        editor.apply()
    }

    companion object {
        private val LONG_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    }
}