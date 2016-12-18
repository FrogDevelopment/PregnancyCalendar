package fr.frogdevelopment.pregnancycalendar;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import static fr.frogdevelopment.pregnancycalendar.PregnancyUtils.AMENORRHEA;
import static fr.frogdevelopment.pregnancycalendar.PregnancyUtils.amenorrheaDate;
import static fr.frogdevelopment.pregnancycalendar.PregnancyUtils.conceptionDate;
import static fr.frogdevelopment.pregnancycalendar.R.id.day;
import static fr.frogdevelopment.pregnancycalendar.R.id.month;
import static fr.frogdevelopment.pregnancycalendar.R.id.year;

public class SummaryFragment extends Fragment {

	public interface RefreshListener {
		void onRefresh();
	}

	private RefreshListener listener;

	private LocalDate mNow;
	private int       mDay;
	private int       mMonth;
	private int       mYear;
	private int       mTypeDate;

	private TextInputLayout   dayTextViewWrapper;
	private TextInputEditText dayTextView;
	private TextInputLayout   monthTextViewWrapper;
	private EditText          monthTextView;
	private TextInputLayout   yearTextViewWrapper;
	private TextInputEditText yearTextView;
	private TextView          birthRangeStart;
	private TextView          birthRangeEnd;
	private TextView          otherDateText;
	private TextView          otherDateValue;
	private TextView          currentWeek;
	private TextView          currentMonth;

	// https://developer.android.com/guide/topics/ui/controls/pickers.html#DatePicker ?

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_summary, container, false);

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

		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		mDay = sharedPref.getInt("day", mNow.getDayOfMonth());
		mMonth = sharedPref.getInt("month", mNow.getMonthValue());
		mYear = sharedPref.getInt("year", mNow.getYear());
		mTypeDate = sharedPref.getInt("typeDate", PregnancyUtils.CONCEPTION);

		dayTextView.setText(String.valueOf(mDay));
		monthTextView.setText(String.valueOf(mMonth));
		yearTextView.setText(String.valueOf(mYear));
		yearTextView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				SummaryFragment.this.refresh();
				return true;
			}

			return false;
		});

		RadioGroup toggle = (RadioGroup) rootView.findViewById(R.id.toggle);
		switch (mTypeDate) {
			case AMENORRHEA:
				toggle.check(R.id.amenorrhea);
				break;
			case PregnancyUtils.CONCEPTION:
				toggle.check(R.id.conception);
				break;
		}
		toggle.setOnCheckedChangeListener((radioGroup, i) -> {
			switch (i) {
				case R.id.amenorrhea:
					mTypeDate = AMENORRHEA;
					break;
				case R.id.conception:
					mTypeDate = PregnancyUtils.CONCEPTION;
					break;
			}

			SummaryFragment.this.refresh();
		});

		ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.date_picker_button);
		imageButton.setOnClickListener(view -> {
			DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (datePicker, year1, month1, dayOfMonth) -> {
				dayTextView.setText(String.valueOf(dayOfMonth));
				monthTextView.setText(String.valueOf(month1 + 1/*base 0*/));
				yearTextView.setText(String.valueOf(year1));

				SummaryFragment.this.refresh();

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

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

		if (mTypeDate == AMENORRHEA) {
			otherDateText.setText(getString(R.string.another_date_1));
			otherDateValue.setText(conceptionDate.format(dateTimeFormatter));
		} else {
			otherDateText.setText(getString(R.string.another_date_0));
			otherDateValue.setText(amenorrheaDate.format(dateTimeFormatter));
		}

		currentWeek.setText(String.valueOf(PregnancyUtils.getCurrentWeek(amenorrheaDate, mNow)));
		currentMonth.setText(String.valueOf(PregnancyUtils.getCurrentMonth(conceptionDate, mNow)));

		birthRangeStart.setText(PregnancyUtils.getBirthRangeStart(amenorrheaDate).format(dateTimeFormatter));
		birthRangeEnd.setText(PregnancyUtils.getBirthRangeEnd(amenorrheaDate).format(dateTimeFormatter));

		return true;
	}
}
