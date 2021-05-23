package fr.frogdevelopment.pregnancycalendar.ui.rdv

import junit.framework.TestCase
import org.junit.Test
import org.threeten.bp.LocalDate

class RdvFragmentTest : TestCase() {

    @Test
    fun test() {
        // given
        val fragment = RdvFragment()
        val amenorrheaDate = LocalDate.parse("2021-03-22")

        // when
        fragment.calculateRdv(amenorrheaDate)

    }
}