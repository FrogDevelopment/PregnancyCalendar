package fr.frogdevelopment.pregnancycalendar.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.TimeZone;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.text.Html.fromHtml;
import static fr.frogdevelopment.pregnancycalendar.R.id.action_calendar;
import static fr.frogdevelopment.pregnancycalendar.R.id.birth_range_end;
import static fr.frogdevelopment.pregnancycalendar.R.id.birth_range_start;
import static fr.frogdevelopment.pregnancycalendar.R.id.current_month_value;
import static fr.frogdevelopment.pregnancycalendar.R.id.current_trimester_value;
import static fr.frogdevelopment.pregnancycalendar.R.id.current_week_value;
import static fr.frogdevelopment.pregnancycalendar.R.id.date_value;
import static fr.frogdevelopment.pregnancycalendar.R.id.other_date_text;
import static fr.frogdevelopment.pregnancycalendar.R.id.other_date_value;
import static fr.frogdevelopment.pregnancycalendar.R.id.toggle_amenorrhea;
import static fr.frogdevelopment.pregnancycalendar.R.id.toggle_button_group;
import static fr.frogdevelopment.pregnancycalendar.R.id.toggle_conception;
import static fr.frogdevelopment.pregnancycalendar.R.string.another_date_0;
import static fr.frogdevelopment.pregnancycalendar.R.string.another_date_1;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.AMENORRHEA;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.CONCEPTION;
import static java.time.ZoneOffset.UTC;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class HomeFragment extends Fragment {

    private static DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

    private int mTypeDate;

    private TextView dateTextView;
    private TextView birthRangeStart;
    private TextView birthRangeEnd;
    private TextView otherDateText;
    private TextView otherDateValue;
    private TextView currentWeek;
    private TextView currentMonth;
    private TextView currentTrimester;

    private long mSelectedDate;
    private String mDateValue;
    private LocalDate mMyDate;
    private SharedPreferences mSharedPref;
    private PregnancyUtils pregnancyUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        pregnancyUtils = new PregnancyUtils(getResources(), PreferenceManager.getDefaultSharedPreferences(requireActivity()));
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        dateTextView = rootView.findViewById(date_value);
        otherDateText = rootView.findViewById(other_date_text);
        otherDateValue = rootView.findViewById(other_date_value);
        currentWeek = rootView.findViewById(current_week_value);
        currentMonth = rootView.findViewById(current_month_value);
        currentTrimester = rootView.findViewById(current_trimester_value);
        birthRangeStart = rootView.findViewById(birth_range_start);
        birthRangeEnd = rootView.findViewById(birth_range_end);

        // todo retro-compatibility
        if (mSharedPref.contains("my_date")) {
            mDateValue = mSharedPref.getString("my_date", null);
            if (isNotBlank(mDateValue)) {
                mMyDate = LocalDate.parse(mDateValue, LONG_DATE_FORMATTER);
                setSelectedDate(mMyDate.atStartOfDay().toInstant(UTC).toEpochMilli());
            }
            mSharedPref.edit().remove("my_date").apply();
        } else {
            setSelectedDate(mSharedPref.getLong("selected_date", MaterialDatePicker.todayInUtcMilliseconds()));
        }

        if (mMyDate == null) {
            // fixme prompt to menu action
        }

        mTypeDate = mSharedPref.getInt("type_date", CONCEPTION);

        MaterialButtonToggleGroup group = rootView.findViewById(toggle_button_group);
        group.check(mTypeDate == AMENORRHEA ? toggle_amenorrhea : toggle_conception);
        group.addOnButtonCheckedListener((group1, checkedId, isChecked) -> {
            if (isChecked) {
                mTypeDate = checkedId == toggle_amenorrhea ? AMENORRHEA : CONCEPTION;
                refresh();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == action_calendar) {
            long today = MaterialDatePicker.todayInUtcMilliseconds();
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC));
            calendar.clear();
            calendar.setTimeInMillis(today);
            calendar.add(Calendar.MONTH, -9);
            long nineMonthsAgo = calendar.getTimeInMillis();

            long openSelection = mSelectedDate > 0 ? mSelectedDate : today;

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setStart(nineMonthsAgo);
            constraintsBuilder.setEnd(today);
            constraintsBuilder.setOpenAt(openSelection);

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(openSelection)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build();

            datePicker.addOnPositiveButtonClickListener(this::setSelectedDate);

            datePicker.showNow(getChildFragmentManager(), datePicker.toString());

            return true;
        }

        return false;
    }

    private void setSelectedDate(Long selection) {
        mSelectedDate = selection;
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault());
        mDateValue = LONG_DATE_FORMATTER.format(zonedDateTime);
        mMyDate = zonedDateTime.toLocalDate();
        refresh();
    }

    private void refresh() {
        if (mMyDate != null) {
            dateTextView.setText(getResources().getString(R.string.date_value, mDateValue));
            processDate();
            save();
        }
    }

    private void processDate() {
        LocalDate amenorrheaDate;
        LocalDate conceptionDate;
        if (mTypeDate == AMENORRHEA) {
            amenorrheaDate = mMyDate;
            conceptionDate = pregnancyUtils.getConceptionDate(amenorrheaDate);

            otherDateText.setText(getString(another_date_1));
            otherDateValue.setText(conceptionDate.format(LONG_DATE_FORMATTER));
        } else {
            conceptionDate = mMyDate;
            amenorrheaDate = pregnancyUtils.getAmenorrheaDate(conceptionDate);

            otherDateText.setText(getString(another_date_0));
            otherDateValue.setText(amenorrheaDate.format(LONG_DATE_FORMATTER));
        }

        int currentWeek = pregnancyUtils.getCurrentWeek(amenorrheaDate);
        this.currentWeek.setText(fromHtml(getResources().getQuantityString(R.plurals.week_n, currentWeek, currentWeek), FROM_HTML_MODE_LEGACY));

        int currentMonth = pregnancyUtils.getCurrentMonth(conceptionDate);
        this.currentMonth.setText(fromHtml(getResources().getQuantityString(R.plurals.month_n, currentMonth, currentMonth), FROM_HTML_MODE_LEGACY));

        int currentTrimester = pregnancyUtils.getCurrentTrimester(currentWeek);
        this.currentTrimester.setText(fromHtml(getResources().getQuantityString(R.plurals.trimester_n, currentTrimester, currentTrimester), FROM_HTML_MODE_LEGACY));

        birthRangeStart.setText(pregnancyUtils.getBirthRangeStart(amenorrheaDate).format(LONG_DATE_FORMATTER));
        birthRangeEnd.setText(pregnancyUtils.getBirthRangeEnd(amenorrheaDate).format(LONG_DATE_FORMATTER));
    }

    private void save() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putLong("selected_date", mSelectedDate);
        editor.putInt("type_date", mTypeDate);
        editor.apply();
    }
}
