package fr.frogdevelopment.pregnancycalendar.contraction;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.frogdevelopment.pregnancycalendar.R;


// essayer http://nemanjakovacevic.net/blog/english/2016/01/12/recyclerview-swipe-to-delete-no-3rd-party-lib-necessary/
class ContractionAdapter extends RecyclerView.Adapter<ContractionViewHolder> {

	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);

	private final Context mContext;
	private final LayoutInflater mInflater;

	private final Locale locale = Locale.getDefault();
	private final List<ContractionContract.Contraction> mRows = new ArrayList<>();
	private final List<ContractionContract.Contraction> mPendingRemovalRows = new ArrayList<>();

	private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
	private Handler handler = new Handler(); // hanlder for running delayed runnables
	private Map<ContractionContract.Contraction, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


	ContractionAdapter(Activity context) {
		mContext = context;
		mInflater = context.getLayoutInflater();
	}

	void add(ContractionContract.Contraction contraction) {
		mRows.add(contraction);
		notifyDataSetChanged();
	}

	void addAll(List<ContractionContract.Contraction> contractions) {
		mRows.addAll(contractions);
		notifyDataSetChanged();
	}

	void clear() {
		mRows.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mRows.size();
	}

	private ContractionContract.Contraction getItem(int position) {
		try {
			return mRows.get(getItemCount() - position - 1); // reverse order
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public ContractionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// Inflate the custom layout
		View contactView = mInflater.inflate(R.layout.row_contraction, parent, false);

		// Return a new holder instance
		return new ContractionViewHolder(contactView);
	}

	// Involves populating data into the item through holder
	@Override
	public void onBindViewHolder(ContractionViewHolder viewHolder, int position) {
		// Get the data model based on position
		final ContractionContract.Contraction current = getItem(position);

		if (current == null) {
			return;
		}

		if (mPendingRemovalRows.contains(current)) {
			/** {show swipe layout} and {hide regular layout} */
			viewHolder.regularLayout.setVisibility(View.GONE);
			viewHolder.swipeLayout.setVisibility(View.VISIBLE);

			viewHolder.undo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					undoOpt(current);
				}
			});
		} else {
			/** {show regular layout} and {hide swipe layout} */
			viewHolder.regularLayout.setVisibility(View.VISIBLE);
			viewHolder.swipeLayout.setVisibility(View.GONE);

			viewHolder.date.setText(current.dateTime.format(dateFormatter));
			viewHolder.time.setText(current.dateTime.format(timeFormatter));

			if (current.duration != null) {
				viewHolder.duration.setText(durationToLabel(current.duration));
			} else {
				viewHolder.duration.setText("--:--");
			}

			ContractionContract.Contraction previous = getItem(position + 1); // +1 as reverse order ...
			if (previous != null) {
				long durationSincePrevious = ChronoUnit.MILLIS.between(previous.dateTime.plus(previous.duration, ChronoUnit.MILLIS), current.dateTime);
				viewHolder.last.setText(durationToLabel(durationSincePrevious));
			} else {
				viewHolder.last.setText("--:--");
			}
		}
	}

	private void undoOpt(ContractionContract.Contraction data) {
		Runnable pendingRemovalRunnable = pendingRunnables.get(data);
		pendingRunnables.remove(data);
		if (pendingRemovalRunnable != null)
			handler.removeCallbacks(pendingRemovalRunnable);
		mPendingRemovalRows.remove(data);
		// this will rebind the row in "normal" state
		notifyItemChanged(mRows.indexOf(data));
	}

	private String durationToLabel(long duration) {
		String label;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		if (seconds < 60) { // less than 1 minute
			label = String.format(locale, "%02dsec", seconds);
		} else { // more than 1 minute
			long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
			if (minutes < 60) { // less than 1 hour
				label = String.format(locale, "%02dmin%02d", minutes, seconds - TimeUnit.MINUTES.toSeconds(minutes));
			} else { //more than 1 hour
				long hour = TimeUnit.MINUTES.toHours(minutes);
				if (hour < 24) { // less than 1 day
					label = String.format(locale, "%02dh%02d", hour, minutes - TimeUnit.HOURS.toMinutes(hour));
				} else { // more than 1 day
					long days = TimeUnit.HOURS.toDays(hour);
					label = String.format(locale, "%02dj%02dh", days, TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(days));
				}
			}
		}

		return label;
	}

	void pendingRemoval(RecyclerView.ViewHolder viewHolder) {
		int position = viewHolder.getAdapterPosition();

		final ContractionContract.Contraction data = mRows.get(position);

		if (!mPendingRemovalRows.contains(data)) {
			mPendingRemovalRows.add(data);
			// this will redraw row in "undo" state
			notifyItemChanged(position);
			// let's create, store and post a runnable to remove the data
			Runnable pendingRemovalRunnable = new Runnable() {
				@Override
				public void run() {
					remove(mRows.indexOf(data));
				}
			};
			handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
			pendingRunnables.put(data, pendingRemovalRunnable);
		}
	}

	private void remove(int position) {
		ContractionContract.Contraction data = mRows.get(position);
		if (mPendingRemovalRows.contains(data)) {
			mPendingRemovalRows.remove(data);
		}
		if (mRows.contains(data)) {
			mRows.remove(position);
			notifyItemRemoved(position);

			Uri uri = Uri.parse(ContractionContentProvider.URI_CONTRACTION + "/" + data.id);
			mContext.getContentResolver().delete(uri, null, null);
		}
	}

	boolean isPendingRemoval(RecyclerView.ViewHolder viewHolder) {
		int position = viewHolder.getAdapterPosition();

		ContractionContract.Contraction data = mRows.get(position);
		return mPendingRemovalRows.contains(data);
	}
}
