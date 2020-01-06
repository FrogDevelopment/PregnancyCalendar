package fr.frogdevelopment.pregnancycalendar.ui.contraction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActivityNavigator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.data.Contraction;
import fr.frogdevelopment.pregnancycalendar.ui.chrono.ChronoActivity;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static fr.frogdevelopment.pregnancycalendar.utils.DateLabelUtils.millisecondsToLabel;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;

public class ContractionFragment extends Fragment {

    private ContractionViewModel mContractionViewModel;

    private TextView mAverageInterval;
    private TextView mAverageDuration;

    private ContractionAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContractionViewModel = new ViewModelProvider(this).get(ContractionViewModel.class);

        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.contraction_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        mAverageInterval = rootView.findViewById(R.id.average_interval);
        mAverageDuration = rootView.findViewById(R.id.average_duration);

        RecyclerView recyclerView = rootView.findViewById(R.id.chrono_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mAdapter = new ContractionAdapter(requireActivity());
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(requireContext(), this::remove));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mContractionViewModel.getAllContractions().observe(getViewLifecycleOwner(), contractions -> {
            mAdapter.setContractions(contractions);
            computeStats(contractions);
        });

        rootView.findViewById(R.id.chrono_button).setOnClickListener(view -> start());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.contraction, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.contraction_delete_all_title)
                    .setMessage(R.string.contraction_delete_all_confirmation)
                    .setPositiveButton(R.string.contraction_delete_all_positive_button, (dialog, which) -> mContractionViewModel.deleteAll())
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();

            return true;
        }

        return false;
    }

    private void computeStats(List<Contraction> contractions) {
        String averageInterval = null;
        String averageDuration = null;

        int itemCount = contractions.size();
        if (itemCount > 0) {
            List<Long> intervals = new ArrayList<>();
            List<Long> durations = new ArrayList<>();

            Contraction last = null;
            Contraction previous = null;
            Contraction contraction;
            for (int i = itemCount - 1; i >= 0; i--) {
                contraction = contractions.get(i);

                if (last == null) {
                    last = contraction;
                    previous = contraction;
                    durations.add(contraction.duration);
                    continue;
                }

                // compute stats on the last 2 hours only
                if (HOURS.between(contraction.dateTime, last.dateTime) < 2) {
                    intervals.add(MILLIS.between(contraction.dateTime.plus(contraction.duration, MILLIS), previous.dateTime));
                    durations.add(contraction.duration);
                } else {
                    // no need to loop any more
                    break;
                }

                previous = contraction;
            }

            averageInterval = millisecondsToLabel(requireContext(), intervals.stream().collect(Collectors.averagingLong(d -> d)).longValue());
            averageDuration = millisecondsToLabel(requireContext(), durations.stream().collect(Collectors.averagingLong(d -> d)).longValue());
        }

        mAverageInterval.setText(averageInterval);
        mAverageDuration.setText(averageDuration);
    }

    void remove(final RecyclerView.ViewHolder viewHolder) {
        final int adapterPosition = viewHolder.getAdapterPosition();

        final Contraction item = mAdapter.getAtPosition(adapterPosition);
        mContractionViewModel.delete(item);

        Snackbar.make(requireView(), R.string.contraction_delete_all_deleted, Snackbar.LENGTH_SHORT)
                .setAction(R.string.contraction_delete_all_undo, v -> {
                    item.id = null;
                    mContractionViewModel.insert(item);
                })
                .setActionTextColor(Color.RED)
                .show();
    }

    private void start() {
        Intent intent = new Intent(requireContext(), ChronoActivity.class);
        ActivityNavigator activityNavigator = new ActivityNavigator(requireContext());
        ActivityNavigator.Destination destination = activityNavigator
                .createDestination()
                .setIntent(intent);

        activityNavigator.navigate(destination, null, null, null);
    }

}
