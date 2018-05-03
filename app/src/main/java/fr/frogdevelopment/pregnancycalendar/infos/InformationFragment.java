package fr.frogdevelopment.pregnancycalendar.infos;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils;

import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.AMENORRHEA;
import static fr.frogdevelopment.pregnancycalendar.utils.PregnancyUtils.CONCEPTION;

public class InformationFragment extends Fragment {

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
	private TextView          currentTrimester;

	// https://developer.android.com/guide/topics/ui/controls/pickers.html#DatePicker ?

	static final DateTimeFormatter ISO_DATE_FORMATTER  = DateTimeFormatter.BASIC_ISO_DATE;
	private      DateTimeFormatter LONG_DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

	private LocalDate         mMyDate;
	private SharedPreferences mSharedPref;
	private PregnancyUtils pregnancyUtils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_information, container, false);

		pregnancyUtils = new PregnancyUtils(getActivity().getResources(), PreferenceManager.getDefaultSharedPreferences(getActivity()));

		dayTextViewWrapper = rootView.findViewById(R.id.dayWrapper);
		dayTextView = rootView.findViewById(R.id.day);
		monthTextViewWrapper = rootView.findViewById(R.id.monthWrapper);
		monthTextView = rootView.findViewById(R.id.month);
		yearTextViewWrapper = rootView.findViewById(R.id.yearWrapper);
		yearTextView = rootView.findViewById(R.id.year);
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
				InformationFragment.this.refresh();
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

			InformationFragment.this.refresh();
		});

		ImageButton imageButton = rootView.findViewById(R.id.date_picker_button);
		imageButton.setOnClickListener(view -> {
			DatePickerDialog datePickerDialog = new DatePickerDialog(InformationFragment.this.getActivity(), (view1, year, month, dayOfMonth) -> {
				dayTextView.setText(String.valueOf(dayOfMonth));
				monthTextView.setText(String.valueOf(month + 1/*base 0*/));
				yearTextView.setText(String.valueOf(year));

				InformationFragment.this.refresh();

			}, mYear, mMonth - 1/*base 0*/, mDay);

			datePickerDialog.getDatePicker().setMinDate(mNow.minusYears(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
			datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

			datePickerDialog.show();
		});

		Button calculateButton = rootView.findViewById(R.id.calculate);
		calculateButton.setOnClickListener(view -> InformationFragment.this.refresh());

		calculate();

		setHasOptionsMenu(true);

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
