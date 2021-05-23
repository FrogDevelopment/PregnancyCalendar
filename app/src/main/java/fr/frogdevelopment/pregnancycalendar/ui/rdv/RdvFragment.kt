@file:Suppress("unused", "UNUSED_VARIABLE")

package fr.frogdevelopment.pregnancycalendar.ui.rdv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import fr.frogdevelopment.pregnancycalendar.R
import org.threeten.bp.LocalDate

class RdvFragment : Fragment() {

    companion object {
        fun newInstance() = RdvFragment()
    }

    private lateinit var viewModel: RdvViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rdv_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RdvViewModel::class.java)
    }

    fun calculateRdv(amenorrheaDate: LocalDate) {
        println("Date Aménorrhée $amenorrheaDate")

        // 1er trimestre
        val consultationPrenatale1From = amenorrheaDate.plusWeeks(8).minusDays(1)
        val consultationPrenatale1To = amenorrheaDate.plusWeeks(12).minusDays(1)
        println("1ère consultation prénatale entre $consultationPrenatale1From (8 SA) et $consultationPrenatale1To(12 SA)")

        val echo1 = amenorrheaDate.plusWeeks(11).plusDays(1)
        println("Échographie 1 aux environ du $echo1 (12 SA)")

        // 2d trimestre
        val consultationPrenatale2 = calculateRdvMonthly(amenorrheaDate, 4)
        val consultationPrenatale3 = calculateRdvMonthly(amenorrheaDate, 5)

        val echo2 = amenorrheaDate.plusWeeks(21).plusDays(1)
        println("Échographie 2 aux environ du $echo2 (22 SA)")

        val consultationPrenatale4 = calculateRdvMonthly(amenorrheaDate, 6)

        // 3ème trimestre
        val consultationPrenatale5 = calculateRdvMonthly(amenorrheaDate, 7)

        val echo3 = amenorrheaDate.plusWeeks(31).plusDays(1)
        println("Échographie 3 aux environ du $echo3 (32 SA)")

        val consultationPrenatale6 = calculateRdvMonthly(amenorrheaDate, 8)
        val consultationPrenatale7 = calculateRdvMonthly(amenorrheaDate, 9)
    }

    private fun calculateRdvMonthly(amenorrheaDate: LocalDate, month: Long): LocalDate {
        val from = amenorrheaDate.plusMonths(month - 1)
        println("Consultation ${month}ème mois à partir de $from")
        return from
    }

}