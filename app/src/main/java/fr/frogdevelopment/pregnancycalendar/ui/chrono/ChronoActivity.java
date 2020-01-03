package fr.frogdevelopment.pregnancycalendar.ui.chrono;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.frogdevelopment.pregnancycalendar.R;

public class ChronoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chrono_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ChronoFragment.newInstance())
                    .commitNow();
        }
    }

}