package fr.frogdevelopment.pregnancycalendar.months;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.temporal.ChronoUnit;

import fr.frogdevelopment.pregnancycalendar.PregnancyUtils;
import fr.frogdevelopment.pregnancycalendar.R;

public class MonthsFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return new fr.frogdevelopment.pregnancycalendar.months.MonthsView(getActivity());
	}

//	private MonthsView mMonthsView;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		mMonthsView = new MonthsView(getActivity());
//		mMonthsView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
////		ScrollView scrollView = new ScrollView(getActivity());
////		scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
////		scrollView.addView(mMonthsView);
//
//		return mMonthsView;
//	}
//
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		if (!isVisibleToUser && mMonthsView != null) {
//			mMonthsView.mScaleFactor = 1.0f;
//			mMonthsView.invalidate();
//		}
//	}

	class MonthsView extends ScrollView {

		// defines paint and canvas
		private final Paint  drawPaint;
		private       Shader shader;

		public MonthsView(Context context) {
			super(context);
			this.drawPaint = new Paint();

			mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		}

		private float verticalLineWith = 100;

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			canvas.save();
			canvas.scale(mScaleFactor, mScaleFactor);


			// Set a pixels value to offset the line from canvas edge
			int offsetY = 75;
			int offsetX = (int) ((canvas.getWidth() / 4) / mScaleFactor);

			int start = offsetY;
			int end = canvas.getHeight() - offsetY;

			// draw vertical line
			drawPaint.setStrokeWidth(verticalLineWith);
			drawPaint.setColor(getResources().getColor(R.color.colorPrimary));
//			if (shader == null) {
//				shader = new LinearGradient(
//						offsetX,
//						start,
//						offsetX,
//						end,
//						getResources().getColor(android.R.color.holo_orange_light),
//						getResources().getColor(android.R.color.holo_green_dark),
//						Shader.TileMode.MIRROR /*or REPEAT*/);
//			}
//
//			drawPaint.setShader(shader);
			canvas.drawLine(offsetX, start, offsetX, end - 3 /* fixme find why this is need !!*/, drawPaint);
			drawPaint.setShader(null);

			// draw horizontal line (month divider)
			drawPaint.setStrokeWidth(5);
			drawPaint.setColor(Color.BLACK);
			drawPaint.setTextSize(50 / mScaleFactor);

			int length = end - start;
			int lengthMonth = length / 9;
			int positionMonth = offsetY;
			int positionText = positionMonth + lengthMonth / 2;

			LocalDate currentMonth = PregnancyUtils.conceptionDate;
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
			for (int i = 0; i <= 9; i++) {
				// line month
				canvas.drawLine(
						offsetX - verticalLineWith / 2, // startX
						positionMonth, // startY
						offsetX + verticalLineWith / 2, // stopX
						positionMonth, // stopY
						drawPaint // Paint
				);

				// label month
				canvas.drawText(
						currentMonth.format(dateTimeFormatter),
						offsetX + 120,
						positionMonth,
						drawPaint
				);

				currentMonth = currentMonth.plusMonths(1);

				if (i < 9) {
					// number month
					canvas.drawText(
							getContext().getString(R.string.month_month, i + 1),
							offsetX - 200,
							positionText,
							drawPaint
					);
				}

				positionMonth = positionMonth + lengthMonth;
				positionText = positionMonth + lengthMonth / 2 + 20;
			}

			addDate(canvas, offsetX, offsetY, length, getResources().getColor(R.color.colorPrimary), LocalDate.now());


			canvas.restore();
		}

		private void addDate(Canvas canvas, int offsetX, int offsetY, int length, int color, LocalDate date) {
			long duration = PregnancyUtils.NB_DAYS_CONCEPTION_TO_BIRTH;
			long now = ChronoUnit.DAYS.between(PregnancyUtils.conceptionDate, date);

			int y = (int) (now * length / duration) + offsetY;

			drawTriangle(canvas, offsetX, y, color);
		}

		private void drawTriangle(Canvas canvas, int x, int y, int color) {
			Path path = new Path();
			path.moveTo(x, y);
			path.lineTo(x + 50, y - 25);
			path.lineTo(x + 50, y + 25);
			path.close();

			drawPaint.setColor(color);
			drawPaint.setStyle(Paint.Style.FILL);
			canvas.drawPath(path, drawPaint);
		}

		// ********************************
		// ZOOM cf https://android-developers.googleblog.com/2010/06/making-sense-of-multitouch.html
		// ********************************

		private ScaleGestureDetector mScaleDetector;
		private float mScaleFactor = 1.0f;

		private final static float MIN_SCALE = 1.0f; // min and max for zoom level
		private final static float MAX_SCALE = 4.0f;

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			// Let the ScaleGestureDetector inspect all events.
			mScaleDetector.onTouchEvent(ev);
			return true;
		}

		private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				mScaleFactor *= detector.getScaleFactor();

				// Don't let the object get too small or too large.
				mScaleFactor = Math.max(MIN_SCALE, Math.min(mScaleFactor, MAX_SCALE));

				invalidate();
				return true;
			}
		}
	}
}
