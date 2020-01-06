package fr.frogdevelopment.pregnancycalendar.ui.chrono

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.frogdevelopment.pregnancycalendar.R

class ChronoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chrono_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, ChronoFragment())
                    .commitNow()
        }
    }
}