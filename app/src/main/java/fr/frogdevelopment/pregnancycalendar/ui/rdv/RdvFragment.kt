@file:Suppress("unused", "UNUSED_VARIABLE", "SpellCheckingInspection")

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

        println("***** RDV MEDICAUX")
        // 1er trimestre
        val consultationPrenatale1From = amenorrheaDate.plusWeeks(8)
        val consultationPrenatale1To = amenorrheaDate.plusWeeks(12)
        println("1ère consultation prénatale entre $consultationPrenatale1From (8 SA) et $consultationPrenatale1To (12 SA)")

        val echo1From = amenorrheaDate.plusWeeks(11)
        val echo1To = amenorrheaDate.plusWeeks(13)
        println("Échographie 1 (12 SA) entre $echo1From et $echo1To")

        // 2d trimestre
        val consultationPrenatale2 = calculateRdvMonthly(amenorrheaDate, 4)
        val consultationPrenatale3 = calculateRdvMonthly(amenorrheaDate, 5)

        val echo2From = amenorrheaDate.plusWeeks(21)
        val echo2To = amenorrheaDate.plusWeeks(23)
        println("Échographie 2 (22 SA) entre $echo2From et $echo2To")

        val consultationPrenatale4 = calculateRdvMonthly(amenorrheaDate, 6)

        // 3ème trimestre
        val consultationPrenatale5 = calculateRdvMonthly(amenorrheaDate, 7)

        val echo3From = amenorrheaDate.plusWeeks(31)
        val echo3To = amenorrheaDate.plusWeeks(33)
        println("Échographie 3 (32 SA) entre $echo3From et $echo3To")

        val consultationPrenatale6 = calculateRdvMonthly(amenorrheaDate, 8)
        val consultationPrenatale7 = calculateRdvMonthly(amenorrheaDate, 9)

        println("***** RDV ADMINISTRATIFS")
        val declarationGrossesse = amenorrheaDate.plusWeeks(15)
        println("Déclaration grossesse CAF/CPAM (15 SA) avant le $declarationGrossesse")

        val preparationAccouchementFrom = amenorrheaDate.plusWeeks(25)
        val preparationAccouchementTo = amenorrheaDate.plusWeeks(35)
        println("8 séances de préparation à l'accouchement entre le $preparationAccouchementFrom et $preparationAccouchementTo")

        val maternite = amenorrheaDate.plusWeeks(28)
        println("S'inscrire à la maternité avant le $maternite")

        val anesthesiste = amenorrheaDate.plusWeeks(33)
        println("Consultation chez l'anesthésiste avant le $anesthesiste")
    }

    private fun calculateRdvMonthly(amenorrheaDate: LocalDate, month: Long): LocalDate {
        val from = amenorrheaDate.plusMonths(month - 1).plusDays(1)
        println("Consultation ${month}ème mois à partir du $from")
        return from
    }

}