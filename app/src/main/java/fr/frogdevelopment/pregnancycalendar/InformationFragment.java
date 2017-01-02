package fr.frogdevelopment.pregnancycalendar;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.threeten.bp.DateTimeException;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.ChronoUnit;

import static fr.frogdevelopment.pregnancycalendar.R.id.day;
import static fr.frogdevelopment.pregnancycalendar.R.id.month;
import static fr.frogdevelopment.pregnancycalendar.R.id.year;

public class InformationFragment extends Fragment {

    private LocalDate mNow;
    private int mDay;
    private int mMonth;
    private int mYear;
    private int mTypeDate;

    private TextInputLayout dayTextViewWrapper;
    private TextInputEditText dayTextView;
    private TextInputLayout monthTextViewWrapper;
    private EditText monthTextView;
    private TextInputLayout yearTextViewWrapper;
    private TextInputEditText yearTextView;
    private TextView birthRangeStart;
    private TextView birthRangeEnd;
    private TextView otherDateText;
    private TextView otherDateValue;
    private TextView currentWeek;
    private TextView currentMonth;

    // https://developer.android.com/guide/topics/ui/controls/pickers.html#DatePicker ?

    static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

    private LocalDate mMyDate;
    private SharedPreferences mSharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_information, container, false);

        dayTextViewWrapper = (TextInputLayout) rootView.findViewById(R.id.dayWrapper);
        dayTextView = (TextInputEditText) rootView.findViewById(day);
        monthTextViewWrapper = (TextInputLayout) rootView.findViewById(R.id.monthWrapper);
        monthTextView = (EditText) rootView.findViewById(month);
        yearTextViewWrapper = (TextInputLayout) rootView.findViewById(R.id.yearWrapper);
        yearTextView = (TextInputEditText) rootView.findViewById(year);
        otherDateText = (TextView) rootView.findViewById(R.id.other_date_text);
        otherDateValue = (TextView) rootView.findViewById(R.id.other_date_value);
        currentWeek = (TextView) rootView.findViewById(R.id.current_week_value);
        currentMonth = (TextView) rootView.findViewById(R.id.current_month_value);
        birthRangeStart = (TextView) rootView.findViewById(R.id.birth_range_start);
        birthRangeEnd = (TextView) rootView.findViewById(R.id.birth_range_end);

        mNow = LocalDate.now();

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String date = mSharedPref.getString("my_date", null);

        if (date != null) {
            mMyDate = LocalDate.parse(date, ISO_DATE_FORMATTER);
        } else {
            mMyDate = mNow;
        }

        mDay = mMyDate.getDayOfMonth();
        mMonth = mMyDate.getMonthValue();
        mYear = mMyDate.getYear();
        mTypeDate = mSharedPref.getInt("type_date", CONCEPTION);

        dayTextView.setText(String.valueOf(mDay));
        monthTextView.setText(String.valueOf(mMonth));
        yearTextView.setText(String.valueOf(mYear));
        yearTextView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                refresh();
                return true;
            }

            return false;
        });

        RadioGroup toggle = (RadioGroup) rootView.findViewById(R.id.toggle);
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

        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.date_picker_button);
        imageButton.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (datePicker, year1, month1, dayOfMonth) -> {
                dayTextView.setText(String.valueOf(dayOfMonth));
                monthTextView.setText(String.valueOf(month1 + 1/*base 0*/));
                yearTextView.setText(String.valueOf(year1));

                InformationFragment.this.refresh();

            }, mYear, mMonth - 1/*base 0*/, mDay);

            datePickerDialog.getDatePicker().setMinDate(mNow.minusYears(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

        Button calculateButton = (Button) rootView.findViewById(R.id.calculate);
        calculateButton.setOnClickListener(view -> refresh());

        calculate();

        return rootView;
    }

    private void refresh() {
        mDay = Integer.valueOf(dayTextView.getText().toString());
        mMonth = Integer.valueOf(monthTextView.getText().toString());
        mYear = Integer.valueOf(yearTextView.getText().toString());

        if (calculate()) {
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString("my_date", mMyDate.format(ISO_DATE_FORMATTER));
            editor.putInt("type_date", mTypeDate);
            editor.apply();
        }
    }

    private boolean calculate() {
        // check correct inputs
        try {
            dayTextViewWrapper.setError(null);
            ChronoField.DAY_OF_MONTH.checkValidValue((long) mDay);
        } catch (DateTimeException e) {
            dayTextViewWrapper.setError(getString(R.string.date_error_day));
            return false;
        }

        try {
            monthTextViewWrapper.setError(null);
            ChronoField.MONTH_OF_YEAR.checkValidValue((long) mMonth);
        } catch (DateTimeException e) {
            monthTextViewWrapper.setError(getString(R.string.date_error_month));
            return false;
        }

        try {
            yearTextViewWrapper.setError(null);
            ChronoField.YEAR.checkValidValue((long) mYear);
        } catch (DateTimeException e) {
            yearTextViewWrapper.setError(getString(R.string.date_error_year));
            return false;
        }

        mMyDate = LocalDate.of(mYear, mMonth, mDay);

        LocalDate amenorrheaDate;
        LocalDate conceptionDate;
        if (mTypeDate == AMENORRHEA) {
            amenorrheaDate = mMyDate;
            conceptionDate = getConceptionDate(amenorrheaDate);

            otherDateText.setText(getString(R.string.another_date_1));
            otherDateValue.setText(conceptionDate.format(LONG_DATE_FORMATTER));
        } else {
            conceptionDate = mMyDate;
            amenorrheaDate = getAmenorrheaDate(conceptionDate);

            otherDateText.setText(getString(R.string.another_date_0));
            otherDateValue.setText(amenorrheaDate.format(LONG_DATE_FORMATTER));
        }

        currentWeek.setText(String.valueOf(getCurrentWeek(amenorrheaDate)));
        currentMonth.setText(String.valueOf(getCurrentMonth(conceptionDate)));

        birthRangeStart.setText(getBirthRangeStart(amenorrheaDate).format(LONG_DATE_FORMATTER));
        birthRangeEnd.setText(getBirthRangeEnd(amenorrheaDate).format(LONG_DATE_FORMATTER));

        return true;
    }

    // ****************************************************************************************
    // http://aly-abbara.com/utilitaires/calendrier/calendrier_de_grossesse.html
    // http://aly-abbara.com/utilitaires/calendrier/calculatrice_age_de_grossesse.html

    // http://www.guidegrossesse.com/grossesse/duree-d-une-grossesse.htm

    // http://naitreetgrandir.com/fr/grossesse/trimestre1/fiche.aspx?doc=duree-grossesse

    static final int AMENORRHEA = 0;
    static final int CONCEPTION = 1;

    @NonNull
    private LocalDate getAmenorrheaDate(LocalDate conceptionDate) {
        int daysToFecondation;
        String value = mSharedPref.getString("pref_key_days_to_fecondation", null);
        if (value != null) {
            daysToFecondation = Integer.parseInt(value);
        } else {
            daysToFecondation = getResources().getInteger(R.integer.default_days_to_fecondation);
        }
        return conceptionDate.minusDays(daysToFecondation);
    }

    @NonNull
    private LocalDate getConceptionDate(LocalDate amenorrheaDate) {
        int daysToFecondation;
        String value = mSharedPref.getString("pref_key_days_to_fecondation", null);
        if (value != null) {
            daysToFecondation = Integer.parseInt(value);
        } else {
            daysToFecondation = getResources().getInteger(R.integer.default_days_to_fecondation);
        }
        return amenorrheaDate.plusDays(daysToFecondation);
    }

    @NonNull
    private LocalDate getBirthRangeStart(LocalDate amenorrheaDate) {
        int gestationMin;
        String value = mSharedPref.getString("pref_key_gestation_min", null);
        if (value != null) {
            gestationMin = Integer.parseInt(value);
        } else {
            gestationMin = getResources().getInteger(R.integer.default_gestation_min);
        }
        return amenorrheaDate.plusDays(gestationMin);
    }

    @NonNull
    private LocalDate getBirthRangeEnd(LocalDate amenorrheaDate) {
        int gestationMax;
        String value = mSharedPref.getString("pref_key_gestation_max", null);
        if (value != null) {
            gestationMax = Integer.parseInt(value);
        } else {
            gestationMax = getResources().getInteger(R.integer.default_gestation_max);
        }
        return amenorrheaDate.plusDays(gestationMax);
    }

    private long getCurrentMonth(LocalDate conceptionDate) {
        return ChronoUnit.MONTHS.between(conceptionDate, mNow) + 1; // fixme pour le +1 ?
    }

    private long getCurrentWeek(LocalDate amenorrheaDate) {
        return ChronoUnit.WEEKS.between(amenorrheaDate, mNow);
    }

}
