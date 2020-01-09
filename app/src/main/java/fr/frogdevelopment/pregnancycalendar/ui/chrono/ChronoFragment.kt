package fr.frogdevelopment.pregnancycalendar.ui.chrono

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import fr.frogdevelopment.pregnancycalendar.R
import fr.frogdevelopment.pregnancycalendar.data.Contraction
import fr.frogdevelopment.pregnancycalendar.ui.contraction.ContractionViewModel
import java.time.ZoneId
import java.time.ZonedDateTime

class ChronoFragment : Fragment() {

    private lateinit var mChronometer: Chronometer
    private lateinit var mContractionViewModel: ContractionViewModel
    private var mNow: ZonedDateTime? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContractionViewModel = ViewModelProvider(this).get(ContractionViewModel::class.java)
        return inflater.inflate(R.layout.chrono_fragment, container, false)
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        mNow = ZonedDateTime.now(ZoneId.systemDefault())
        mChronometer = rootView.findViewById(R.id.chronometer)
        mChronometer.start()
        rootView.findViewById<View>(R.id.chrono_button).setOnClickListener { stop() }
    }

    private fun stop() {
        mChronometer.stop()
        val contraction = Contraction()
        contraction.dateTime = mNow
        contraction.duration = SystemClock.elapsedRealtime() - mChronometer.base
        mContractionViewModel.insert(contraction)
        requireActivity().onBackPressed()
    }
}