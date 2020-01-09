package fr.frogdevelopment.pregnancycalendar

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class AbpApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}