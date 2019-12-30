package fr.frogdevelopment.pregnancycalendar.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.TimeZone;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils;

import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.AMENORRHEA;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.CONCEPTION;
import static java.time.format.DateTimeFormatter.BASIC_ISO_DATE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class HomeFragment extends Fragment {

    private LocalDate mNow;
    private int mTypeDate;

    private TextInputLayout dateTextViewWrapper;
    private TextInputEditText dateTextView;

    private TextView birthRangeStart;
    private TextView birthRangeEnd;
    private TextView otherDateText;
    private TextView otherDateValue;
    private TextView currentWeek;
    private TextView currentMonth;
    private TextView currentTrimester;

    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

    private LocalDate mMyDate;
    private SharedPreferences mSharedPref;
    private PregnancyUtils pregnancyUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        pregnancyUtils = new PregnancyUtils(getResources(), PreferenceManager.getDefaultSharedPreferences(requireActivity()));

        dateTextViewWrapper = rootView.findViewById(R.id.dateWrapper);
        dateTextView = rootView.findViewById(R.id.date);
        otherDateText = rootView.findViewById(R.id.other_date_text);
        otherDateValue = rootView.findViewById(R.id.other_date_value);
        currentWeek = rootView.findViewById(R.id.current_week_value);
        currentMonth = rootView.findViewById(R.id.current_month_value);
        currentTrimester = rootView.findViewById(R.id.current_trimester_value);
        birthRangeStart = rootView.findViewById(R.id.birth_range_start);
        birthRangeEnd = rootView.findViewById(R.id.birth_range_end);

        mNow = LocalDate.now();

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String date = mSharedPref.getString("my_date", null);

        if (isNotBlank(date)) {
            mMyDate = LocalDate.parse(date, getDateTimeFormatter(date));
            dateTextView.setText(date);
        } else {
            mMyDate = mNow;
        }

        mTypeDate = mSharedPref.getInt("type_date", CONCEPTION);

        dateTextView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                refresh();
                return true;
            }

            return false;
        });

        RadioGroup toggle = rootView.findViewById(R.id.toggle);
        switch (mTypeDate) {
            case AMENORRHEA:
                toggle.check(R.id.amenorrhea);
                break;
            case CONCEPTION:
                toggle.check(R.id.conception);
                break;
        }
        toggle.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.amenorrhea:
                    mTypeDate = AMENORRHEA;
                    break;
                case R.id.conception:
                    mTypeDate = CONCEPTION;
                    break;
            }

            refresh();
        });

        long today = MaterialDatePicker.todayInUtcMilliseconds();
        Calendar calendar = getClearedUtc();

        calendar.setTimeInMillis(today);
        calendar.add(Calendar.MONTH, -9);
        long nineMonthsAgo = calendar.getTimeInMillis();

        calendar.setTimeInMillis(today);
        calendar.add(Calendar.MONTH, 9);
        long nineMonthsLater = calendar.getTimeInMillis();

        MaterialButton imageButton = rootView.findViewById(R.id.date_picker_button);
        imageButton.setOnClickListener(view -> {

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setStart(nineMonthsAgo);
            constraintsBuilder.setEnd(nineMonthsLater);
            constraintsBuilder.setOpenAt(today);

            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(today)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                mMyDate = Instant.ofEpochMilli(selection)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dateTextView.setText(mMyDate.format(DATE_FORMATTER));
                refresh();
            });

            datePicker.showNow(getParentFragmentManager(), datePicker.toString());
        });

        Button calculateButton = rootView.findViewById(R.id.calculate);
        calculateButton.setOnClickListener(view -> refresh());

        checkDateIsValid();

        return rootView;
    }

    private static Calendar getClearedUtc() {
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.clear();
        return utc;
    }

    private static DateTimeFormatter getDateTimeFormatter(String value) {
        if (value.contains("/")) {
            return DATE_FORMATTER;
        } else {
            return BASIC_ISO_DATE;
        }
    }

    private void refresh() {
        if (checkDateIsValid()) {
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString("my_date", mMyDate.format(DATE_FORMATTER));
            editor.putInt("type_date", mTypeDate);
            editor.apply();
        }
    }

    private boolean checkDateIsValid() {
        String value = dateTextView.getText().toString();
        if (isBlank(value)) {
            return false;
        }

        try {
            dateTextViewWrapper.setError(null);
            mMyDate = LocalDate.parse(value, getDateTimeFormatter(value));
        } catch (DateTimeException e) {
            dateTextViewWrapper.setError(getString(R.string.date_error));
            return false;
        }

        LocalDate amenorrheaDate;
        LocalDate conceptionDate;
        if (mTypeDate == AMENORRHEA) {
            amenorrheaDate = mMyDate;
            conceptionDate = pregnancyUtils.getConceptionDate(amenorrheaDate);

            otherDateText.setText(getString(R.string.another_date_1));
            otherDateValue.setText(conceptionDate.format(LONG_DATE_FORMATTER));
        } else {
            conceptionDate = mMyDate;
            amenorrheaDate = pregnancyUtils.getAmenorrheaDate(conceptionDate);

            otherDateText.setText(getString(R.string.another_date_0));
            otherDateValue.setText(amenorrheaDate.format(LONG_DATE_FORMATTER));
        }

        int currentWeek = pregnancyUtils.getCurrentWeek(amenorrheaDate);
        this.currentWeek.setText(Html.fromHtml(getResources().getQuantityString(R.plurals.week_n, currentWeek, currentWeek)));

        int currentMonth = pregnancyUtils.getCurrentMonth(conceptionDate);
        this.currentMonth.setText(Html.fromHtml(getResources().getQuantityString(R.plurals.month_n, currentMonth, currentMonth)));

        int currentTrimester = pregnancyUtils.getCurrentTrimester(currentWeek);
        this.currentTrimester.setText(Html.fromHtml(getResources().getQuantityString(R.plurals.trimester_n, currentTrimester, currentTrimester)));

        birthRangeStart.setText(pregnancyUtils.getBirthRangeStart(amenorrheaDate).format(LONG_DATE_FORMATTER));
        birthRangeEnd.setText(pregnancyUtils.getBirthRangeEnd(amenorrheaDate).format(LONG_DATE_FORMATTER));

        return true;
    }
}
