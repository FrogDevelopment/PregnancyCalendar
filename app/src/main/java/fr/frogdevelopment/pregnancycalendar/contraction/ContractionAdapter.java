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
import fr.frogdevelopment.pregnancycalendar.contraction.ContractionContract.Contraction;

// http://nemanjakovacevic.net/blog/english/2016/01/12/recyclerview-swipe-to-delete-no-3rd-party-lib-necessary/
class ContractionAdapter extends RecyclerView.Adapter<ContractionViewHolder> {

	private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
	private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);

	private final Context mContext;
	private final LayoutInflater mInflater;

	private final Locale locale = Locale.getDefault();
	private final List<Contraction> mRows = new ArrayList<>();
	private final List<String> mPendingRemovalRows = new ArrayList<>();

	private final Handler mHandler = new Handler(); // hanlder for running delayed runnables
	private final Map<String, Runnable> mPendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

	ContractionAdapter(Activity context) {
		mContext = context;
		mInflater = context.getLayoutInflater();
	}

	void add(Contraction contraction) {
		mRows.add(contraction);
		notifyDataSetChanged();
	}

	void addAll(List<Contraction> contractions) {
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

	private Contraction getItem(int position) {
		try {
			return mRows.get(getItemCount() - position - 1); // reverse order
		} catch (IndexOutOfBoundsException e) {
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
		final Contraction item = getItem(position);

		if (item == null) {
			return;
		}

		if (mPendingRemovalRows.contains(item.id)) {
			/** {show undo layout} and {hide regular layout} */
			viewHolder.regularLayout.setVisibility(View.GONE);
			viewHolder.undoLayout.setVisibility(View.VISIBLE);

			viewHolder.undo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// user wants to undo the removal, let's cancel the pending task
					Runnable pendingRemovalRunnable = mPendingRunnables.get(item.id);
					mPendingRunnables.remove(item.id);
					if (pendingRemovalRunnable != null) {
						mHandler.removeCallbacks(pendingRemovalRunnable);
					}
					mPendingRemovalRows.remove(item.id);
					// this will rebind the row in "normal" state
//					notifyItemChanged(mRows.indexOf(item)); // fixme à vérifier
					notifyDataSetChanged();
				}
			});
		} else {
			/** {show regular layout} and {hide undo layout} */
			viewHolder.regularLayout.setVisibility(View.VISIBLE);
			viewHolder.undoLayout.setVisibility(View.GONE);

			viewHolder.date.setText(item.dateTime.format(dateFormatter));
			viewHolder.time.setText(item.dateTime.format(timeFormatter));

			if (item.duration != null) {
				viewHolder.duration.setText(durationToLabel(item.duration));
			} else {
				viewHolder.duration.setText("--:--");
			}

			Contraction previous = getItem(position + 1); // +1 as reverse order ...
			if (previous != null) {
				long durationSincePrevious = ChronoUnit.MILLIS.between(previous.dateTime.plus(previous.duration, ChronoUnit.MILLIS), item.dateTime);
				viewHolder.last.setText(durationToLabel(durationSincePrevious));
			} else {
				viewHolder.last.setText("--:--");
			}
		}
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

		final Contraction item = getItem(position);

		if (item != null && !mPendingRemovalRows.contains(item.id)) {
			mPendingRemovalRows.add(item.id);
			// this will redraw row in "undo" state
			notifyItemChanged(position);
			// let's create, store and post a runnable to remove the data
			Runnable pendingRemovalRunnable = new Runnable() {
				@Override
				public void run() {
					remove(item);
				}
			};
			mHandler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
			mPendingRunnables.put(item.id, pendingRemovalRunnable);
		}
	}

	private void remove(Contraction item) {
		if (mPendingRemovalRows.contains(item.id)) {
			mPendingRemovalRows.remove(item.id);
		}
		if (mRows.contains(item)) {
			mRows.remove(item);
//			notifyItemRemoved(mRows.indexOf(item)); // fixme à vérifier
			notifyDataSetChanged();

			Uri uri = Uri.parse(ContractionContentProvider.URI_CONTRACTION + "/" + item.id);
			mContext.getContentResolver().delete(uri, null, null);
		}
	}

	boolean isPendingRemoval(RecyclerView.ViewHolder viewHolder) {
		Contraction item = getItem(viewHolder.getAdapterPosition());
		return item != null && mPendingRemovalRows.contains(item.id);
	}
}
