package fr.frogdevelopment.pregnancycalendar.ui.chrono;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.data.Contraction;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;

public class ContractionFragment extends Fragment {

    private ChronoViewModel chronoViewModel;

    private View mRootView;
    private Chronometer mChronometer;
    private MaterialButton mButton;

    private TextView mAverageInterval;
    private TextView mAverageDuration;

    private ContractionAdapter mAdapter;
    private Contraction currentContraction;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chronoViewModel = new ViewModelProvider(this).get(ChronoViewModel.class);

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.chrono_fragment, container, false);

        mChronometer = mRootView.findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());

        mButton = mRootView.findViewById(R.id.chrono_button);
        mButton.setOnClickListener(view -> startOrStop());

        mAverageInterval = mRootView.findViewById(R.id.average_interval);
        mAverageDuration = mRootView.findViewById(R.id.average_duration);

        mRecyclerView = mRootView.findViewById(R.id.chrono_list);
        mLayoutManager = new LinearLayoutManager(requireContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new ContractionAdapter(requireActivity());
        mRecyclerView.setAdapter(mAdapter);

        mItemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requireContext(), viewHolder -> mAdapter.remove(viewHolder)));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        chronoViewModel.getAllContractions().observe(getViewLifecycleOwner(), contractions -> mAdapter.setContractions(contractions));

        setHasOptionsMenu(true);

        return mRootView;
    }

    private void startOrStop() {
        if (currentContraction == null) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        currentContraction = new Contraction();
        currentContraction.dateTime = LocalDateTime.now();

        mButton.setText(R.string.stop);
        mButton.setIcon(getResources().getDrawable(R.drawable.ic_baseline_stop_24, null));

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        mAdapter.add(currentContraction);

//        mRootView.setBackgroundResource(R.drawable.background_chrono_started);

        mItemTouchHelper.attachToRecyclerView(null);
    }

    private void stop() {
        mChronometer.stop();

        mButton.setText(R.string.start);
        mButton.setIcon(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));

        currentContraction.duration = MILLIS.between(currentContraction.dateTime, LocalDateTime.now());

        chronoViewModel.insert(currentContraction);

//        mRootView.setBackgroundResource(R.drawable.background_chrono_stoped);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        currentContraction = null;

        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void computeStats() {
        // reset label
        mAverageInterval.setText(null);
        mAverageDuration.setText(null);

        // compute stats on the last 2 hours
        if (!mAdapter.mRows.isEmpty()) {
            List<Long> intervals = new ArrayList<>();
            List<Long> durations = new ArrayList<>();

            Contraction last = null;
            Contraction previous = null;
            Contraction contraction;
            for (int i = mAdapter.mRows.size() - 1; i >= 0; i--) {
                contraction = mAdapter.mRows.get(i);

                if (last == null) {
                    last = contraction;
                    previous = contraction;
                    durations.add(contraction.duration);
                    continue;
                }

                long durationSinceLast = HOURS.between(contraction.dateTime, last.dateTime);

                if (durationSinceLast < 2) {
                    intervals.add(MILLIS.between(contraction.dateTime.plus(contraction.duration, MILLIS), previous.dateTime));
                    durations.add(contraction.duration);
                } else {
                    // no need to loop any more
                    break;
                }

                previous = contraction;
            }

            if (!intervals.isEmpty()) {
                // interval moyen entre 2 contractions
                //Long averageInterval = intervals.stream().collect(Collectors.averagingLong(d->d));
                long totalInterval = 0L;
                for (long interval : intervals) {
                    totalInterval += interval;
                }

                long averageInterval = totalInterval / intervals.size();
                String labelInterval = mAdapter.millisecondsToLabel(averageInterval);
                mAverageInterval.setText(labelInterval);

                // duréee moyenne de la contractions
                // Long averageDuration = durations.stream().collect(Collectors.averagingLong(d->d));
                long totalDuration = 0L;
                for (long duration : durations) {
                    totalDuration += duration;
                }

                long averageDuration = totalDuration / durations.size();
                String labelDuration = mAdapter.millisecondsToLabel(averageDuration);
                mAverageDuration.setText(labelDuration);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_delete) {// Ask the user if they want to delete
//            new AlertDialog.Builder(requireContext())
//                    .setTitle(R.string.delete_title)
//                    .setMessage(R.string.delete_confirmation)
//                    .setPositiveButton(R.string.delete_positive_button_continue, (dialog, which) -> Snackbar.make(mRootView, R.string.delete_deleted, Snackbar.LENGTH_LONG)
//                            .setAction(R.string.undo, v -> {
//                                // reload data
////                                getLoaderManager().restartLoader(666, null, ContractionFragment.this);
//                            })
//                            .setActionTextColor(Color.RED)
//                            .addCallback(new Snackbar.Callback() {
//
//                                @Override
//                                public void onShown(Snackbar sb) {
//                                    // clear view
//                                    mAdapter.clear();
//                                }
//
//                                @Override
//                                public void onDismissed(Snackbar transientBottomBar, int event) {
//                                    if (event == DISMISS_EVENT_TIMEOUT) {
//                                        // clear data base if Undo action not clicked
//                                        getActivity().getContentResolver().delete(ContractionContentProvider.URI_CONTRACTION, null, null);
//                                    }
//                                }
//                            })
//                            .show())
//                    .setNegativeButton(no, null)
//                    .show();
//
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
////        if (isVisibleToUser) {
//            // reload data when showing view
////            getLoaderManager().restartLoader(666, null, ContractionFragment.this);
////        }
//    }

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);

    private class ContractionAdapter extends RecyclerView.Adapter<ContractionViewHolder> {

        private final LayoutInflater mInflater;

        private final Locale locale = Locale.getDefault();
        private final List<Contraction> mRows = new ArrayList<>();

        ContractionAdapter(@NonNull Activity activity) {
            mInflater = activity.getLayoutInflater();

            registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    computeStats();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    computeStats();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    computeStats();
                }
            });
        }

        void add(Contraction contraction) {
            mRows.add(contraction);
            notifyItemInserted(0);
            mLayoutManager.scrollToPosition(0);
        }

        void setContractions(List<Contraction> contractions) {
            mRows.clear();
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

        @NonNull
        @Override
        public ContractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the custom layout
            View contactView = mInflater.inflate(R.layout.contraction_row, parent, false);

            // Return a new holder instance
            return new ContractionViewHolder(contactView);
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(@NonNull final ContractionViewHolder viewHolder, int position) {
            // Get the data model based on position
            final Contraction item = getItem(position);

            if (item == null) {
                return;
            }

            if (viewHolder.itemView instanceof CardView) {
                CardView cardView = (CardView) viewHolder.itemView;
                cardView.setRadius(0);
            }

            viewHolder.date.setText(item.dateTime.format(dateFormatter));
            viewHolder.time.setText(item.dateTime.format(timeFormatter));

            if (item.duration != null) {
                viewHolder.duration.setText(millisecondsToLabel(item.duration));
            } else {
                viewHolder.duration.setText("--:--");
            }

            Contraction previous = getItem(position + 1); // +1 as reverse order ...
            if (previous != null) {
                long durationSincePrevious = MILLIS.between(previous.dateTime.plus(previous.duration, MILLIS), item.dateTime);
                viewHolder.last.setText(millisecondsToLabel(durationSincePrevious));
            } else {
                viewHolder.last.setText("--:--");
            }
        }

        private String millisecondsToLabel(long duration) {
            String label;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            if (seconds < 60) { // less than 1 minute
                label = String.format(locale, "%02dsec", seconds);
            } else { // more than 1 minute
                long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                if (minutes < 60) { // less than 1 hour
                    label = String.format(locale, "%02dmin%02d", minutes, seconds - TimeUnit.MINUTES.toSeconds(minutes));
                } else { // more than 1 hour
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

        void remove(final RecyclerView.ViewHolder viewHolder) {
            final int adapterPosition = viewHolder.getAdapterPosition();

            final Contraction item = getItem(adapterPosition);

            if (item != null) {

                final int indexOfItem = mRows.indexOf(item);

                Snackbar.make(mRootView, R.string.delete_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, v -> {
                            // re-insert item
                            mRows.add(indexOfItem, item);
                            notifyItemInserted(adapterPosition);
                        })
                        .setActionTextColor(Color.RED)
                        .addCallback(new Snackbar.Callback() {

                            @Override
                            public void onShown(Snackbar sb) {
                                // remove item from view
                                mRows.remove(item);
                                notifyItemRemoved(adapterPosition);
                            }

                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event == DISMISS_EVENT_TIMEOUT) {
                                    // remove from data base if Undo action not clicked
                                    chronoViewModel.delete(item);
                                }
                            }
                        })
                        .show();
            }
        }
    }

    static class ContractionViewHolder extends RecyclerView.ViewHolder {

        final TextView date;
        final TextView time;
        final TextView duration;
        final TextView last;

        ContractionViewHolder(View view) {
            super(view);

            date = view.findViewById(R.id.row_contraction_date);
            time = view.findViewById(R.id.row_contraction_time);
            duration = view.findViewById(R.id.row_contraction_duration);
            last = view.findViewById(R.id.row_contraction_last);
        }
    }

}
