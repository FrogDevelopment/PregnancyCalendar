package fr.frogdevelopment.pregnancycalendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
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

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SummaryFragment extends Fragment {

    public interface RefreshListener {
        void onRefresh();
    }

    private RefreshListener listener;

    private LocalDate mNow;
    private int mDay;
    private int mMonth;
    private int mYear;
    private int mTypeDate;

    private Unbinder unbinder;

    private TextInputLayout dayTextViewWrapper;
    private TextInputEditText dayTextView;
    private TextInputLayout monthTextViewWrapper;
    private EditText monthTextView;
    private TextInputLayout yearTextViewWrapper;
    private TextInputEditText yearTextView;
    private TextView birthRangeStart;
    private TextView birthRangeEnd;
    private TextView currentWeek;
    private TextView currentMonth;

    // https://developer.android.com/guide/topics/ui/controls/pickers.html#DatePicker ?

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        dayTextViewWrapper = ButterKnife.findById(rootView, R.id.dayWrapper);
        dayTextView = ButterKnife.findById(rootView, R.id.day);
        monthTextViewWrapper = ButterKnife.findById(rootView, R.id.monthWrapper);
        monthTextView = ButterKnife.findById(rootView, R.id.month);
        yearTextViewWrapper = ButterKnife.findById(rootView, R.id.yearWrapper);
        yearTextView = ButterKnife.findById(rootView, R.id.year);
        currentWeek = ButterKnife.findById(rootView, R.id.currentWeek);
        currentMonth = ButterKnife.findById(rootView, R.id.currentMonth);
        birthRangeStart = ButterKnife.findById(rootView, R.id.birth_range_start);
        birthRangeEnd = ButterKnife.findById(rootView, R.id.birth_range_end);

        yearTextView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                refresh();
                return true;
            }

            return false;
        });

        RadioGroup toggle = ButterKnife.findById(rootView, R.id.toggle);
        switch (mTypeDate) {
            case PregnancyUtils.AMENORRHEA:
                toggle.check(R.id.amenorrhea);
                break;
            case PregnancyUtils.CONCEPTION:
                toggle.check(R.id.conception);
                break;
        }
        toggle.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.amenorrhea:
                    mTypeDate = PregnancyUtils.AMENORRHEA;
                    break;
                case R.id.conception:
                    mTypeDate = PregnancyUtils.CONCEPTION;
                    break;
            }

            refresh();
        });

        ImageButton imageButton = ButterKnife.findById(rootView, R.id.date_picker_button);
        imageButton.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, month, dayOfMonth) -> {
                dayTextView.setText(String.valueOf(dayOfMonth));
                monthTextView.setText(String.valueOf(month + 1/*base 0*/));
                yearTextView.setText(String.valueOf(year));

                refresh();
            }, mYear, mMonth - 1/*base 0*/, mDay);

            datePickerDialog.getDatePicker().setMinDate(mNow.minusYears(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

        Button calculateButton = ButterKnife.findById(rootView, R.id.calculate);
        calculateButton.setOnClickListener(view -> refresh());

        mNow = LocalDate.now();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mDay = sharedPref.getInt("day", mNow.getDayOfMonth());
        mMonth = sharedPref.getInt("month", mNow.getMonthValue());
        mYear = sharedPref.getInt("year", mNow.getYear());
        mTypeDate = sharedPref.getInt("typeDate", PregnancyUtils.CONCEPTION);

        dayTextView.setText(String.valueOf(mDay));
        monthTextView.setText(String.valueOf(mMonth));
        yearTextView.setText(String.valueOf(mYear));

        calculate();

        return rootView;
    }

    private void refresh() {
        mDay = Integer.valueOf(dayTextView.getText().toString());
        mMonth = Integer.valueOf(monthTextView.getText().toString());
        mYear = Integer.valueOf(yearTextView.getText().toString());

        if (calculate()) {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("day", mDay);
            editor.putInt("month", mMonth);
            editor.putInt("year", mYear);
            editor.putInt("typeDate", mTypeDate);
            editor.apply();

            listener.onRefresh();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RefreshListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implements " + RefreshListener.class);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

        try {
            PregnancyUtils.setDates(mYear, mMonth, mDay, mTypeDate);
        } catch (DateTimeException e) {
            return false; // fixme
        }

        currentWeek.setText(Html.fromHtml(getString(R.string.current_week, PregnancyUtils.getCurrentWeek(PregnancyUtils.amenorrheaDate, mNow))));
        currentMonth.setText(Html.fromHtml(getString(R.string.current_month, PregnancyUtils.getCurrentMonth(PregnancyUtils.conceptionDate, mNow))));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        birthRangeStart.setText(PregnancyUtils.getBirthRangeStart(PregnancyUtils.amenorrheaDate).format(dateTimeFormatter));
        birthRangeEnd.setText(PregnancyUtils.getBirthRangeEnd(PregnancyUtils.amenorrheaDate).format(dateTimeFormatter));

        return true;
    }
}
