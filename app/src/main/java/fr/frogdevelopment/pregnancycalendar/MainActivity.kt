package fr.frogdevelopment.pregnancycalendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.frogdevelopment.pregnancycalendar.R.id
import fr.frogdevelopment.pregnancycalendar.R.id.navigation_home

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        val navView = findViewById<BottomNavigationView>(id.nav_view)
        val appBarConfiguration = AppBarConfiguration.Builder(navigation_home, id.navigation_chrono, id.navigation_settings).build()
        val navController = Navigation.findNavController(this, id.nav_host_fragment)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
    }
}