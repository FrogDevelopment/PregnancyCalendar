package fr.frogdevelopment.pregnancycalendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        Handler().postDelayed({
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)

            finish()
        }, SPLASH_TIME_OUT)
    }

    companion object {
        private const val SPLASH_TIME_OUT: Long = 500
    }
}