package fr.frogdevelopment.pregnancycalendar.ui.contraction

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.frogdevelopment.pregnancycalendar.R

internal class ContractionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val date: TextView = view.findViewById(R.id.row_contraction_date)
    val time: TextView = view.findViewById(R.id.row_contraction_time)
    val duration: TextView = view.findViewById(R.id.row_contraction_duration)
    val last: TextView = view.findViewById(R.id.row_contraction_last)
}