package fr.frogdevelopment.pregnancycalendar.ui.chrono;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.data.Contraction;

import static fr.frogdevelopment.pregnancycalendar.utils.DateLabelUtils.millisecondsToLabel;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;

public class ContractionFragment extends Fragment {

    private ChronoViewModel mChronoViewModel;

    private Chronometer mChronometer;
    private MaterialButton mButton;

    private TextView mAverageInterval;
    private TextView mAverageDuration;

    private ContractionAdapter mAdapter;
    private Contraction mCurrentContraction;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mChronoViewModel = new ViewModelProvider(this).get(ChronoViewModel.class);

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.chrono_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        mChronometer = rootView.findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());

        mButton = rootView.findViewById(R.id.chrono_button);
        mButton.setOnClickListener(view -> startOrStop());

        mAverageInterval = rootView.findViewById(R.id.average_interval);
        mAverageDuration = rootView.findViewById(R.id.average_duration);

        mRecyclerView = rootView.findViewById(R.id.chrono_list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        mLayoutManager = new LinearLayoutManager(requireContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ContractionAdapter(requireActivity());
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
        mRecyclerView.setAdapter(mAdapter);

        mItemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requireContext(), this::remove));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mChronoViewModel.getAllContractions().observe(getViewLifecycleOwner(), contractions -> mAdapter.setContractions(contractions));
    }

    private void startOrStop() {
        if (mCurrentContraction == null) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        mCurrentContraction = new Contraction();
        mCurrentContraction.dateTime = LocalDateTime.now();

        mButton.setIcon(getResources().getDrawable(R.drawable.ic_baseline_stop_24, null));

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        mAdapter.add(mCurrentContraction);
        mLayoutManager.scrollToPosition(0);

        mItemTouchHelper.attachToRecyclerView(null);
    }

    private void stop() {
        mChronometer.stop();

        mButton.setIcon(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24, null));

        mCurrentContraction.duration = MILLIS.between(mCurrentContraction.dateTime, LocalDateTime.now());

        mChronoViewModel.insert(mCurrentContraction);

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mCurrentContraction = null;

        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void computeStats() {
        // reset label
        mAverageInterval.setText(null);
        mAverageDuration.setText(null);

        // compute stats on the last 2 hours
        if (mAdapter.getItemCount() > 0) {
            List<Long> intervals = new ArrayList<>();
            List<Long> durations = new ArrayList<>();

            Contraction last = null;
            Contraction previous = null;
            Contraction contraction;
            for (int i = mAdapter.getItemCount() - 1; i >= 0; i--) {
                contraction = mAdapter.get(i);

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
                long averageInterval = intervals.stream().collect(Collectors.averagingLong(d -> d)).longValue();
                mAverageInterval.setText(millisecondsToLabel(averageInterval));

                long averageDuration = durations.stream().collect(Collectors.averagingLong(d -> d)).longValue();
                mAverageDuration.setText(millisecondsToLabel(averageDuration));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.contraction, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete_title)
                    .setMessage(R.string.delete_confirmation)
                    .setPositiveButton(R.string.delete_positive_button, (dialog, which) -> mChronoViewModel.deleteAll())
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();

            return true;
        }

        return false;
    }

    void remove(final RecyclerView.ViewHolder viewHolder) {
        final int adapterPosition = viewHolder.getAdapterPosition();

        final Contraction item = mAdapter.getItem(adapterPosition);

        if (item != null) {
            final int previousIndex = mAdapter.indexOf(item);
            Snackbar.make(requireView(), R.string.delete_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> mAdapter.reInsert(adapterPosition, previousIndex, item))
                    .setActionTextColor(Color.RED)
                    .addCallback(new Snackbar.Callback() {

                        @Override
                        public void onShown(Snackbar sb) {
                            mAdapter.remove(adapterPosition, item);
                        }

                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                // remove from data base if Undo action not clicked
                                mChronoViewModel.delete(item);
                            }
                        }
                    })
                    .show();
        }
    }

}
