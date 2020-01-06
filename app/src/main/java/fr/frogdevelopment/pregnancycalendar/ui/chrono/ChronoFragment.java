package fr.frogdevelopment.pregnancycalendar.ui.chrono;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.data.Contraction;
import fr.frogdevelopment.pregnancycalendar.ui.contraction.ContractionViewModel;

public class ChronoFragment extends Fragment {

    private Chronometer mChronometer;

    private ContractionViewModel mContractionViewModel;
    private ZonedDateTime mNow;

    public static ChronoFragment newInstance() {
        return new ChronoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContractionViewModel = new ViewModelProvider(this).get(ContractionViewModel.class);
        return inflater.inflate(R.layout.chrono_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        mNow = ZonedDateTime.now(ZoneId.systemDefault());

        mChronometer = rootView.findViewById(R.id.chronometer);
        mChronometer.start();

        rootView.findViewById(R.id.chrono_button).setOnClickListener(view -> stop());
    }

    private void stop() {
        mChronometer.stop();
        Contraction contraction = new Contraction();
        contraction.setDateTime(mNow);
        contraction.setDuration(SystemClock.elapsedRealtime() - mChronometer.getBase());

        mContractionViewModel.insert(contraction);

        requireActivity().onBackPressed();
    }
}