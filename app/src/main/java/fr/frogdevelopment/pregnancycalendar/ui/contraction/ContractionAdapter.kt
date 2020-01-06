package fr.frogdevelopment.pregnancycalendar.ui.contraction

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import fr.frogdevelopment.pregnancycalendar.R
import fr.frogdevelopment.pregnancycalendar.data.Contraction
import fr.frogdevelopment.pregnancycalendar.utils.DateLabelUtils.millisecondsToLabel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*

internal class ContractionAdapter(activity: Activity) : RecyclerView.Adapter<ContractionViewHolder>() {

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        private val TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
    }

    private val mInflater: LayoutInflater = activity.layoutInflater
    private val mRows: MutableList<Contraction> = ArrayList()

    private var mSize = 0

    fun setContractions(contractions: List<Contraction>) {
        mRows.clear()
        mRows.addAll(contractions)
        mSize = mRows.size
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mSize
    }

    fun getAtPosition(position: Int): Contraction? {
        return try {
            mRows[mSize - position - 1] // reverse order
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContractionViewHolder {
        return ContractionViewHolder(mInflater.inflate(R.layout.contraction_row, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ContractionViewHolder, position: Int) {
        val item = getAtPosition(position) ?: return
        if (viewHolder.itemView is CardView) {
            viewHolder.itemView.radius = 0f
        }

        viewHolder.date.text = DATE_FORMATTER.format(item.dateTime)
        viewHolder.time.text = TIME_FORMATTER.format(item.dateTime)

        if (item.duration != null) {
            viewHolder.duration.text = millisecondsToLabel(mInflater.context, item.duration)
        } else {
            viewHolder.duration.text = "--:--"
        }

        val previous = getAtPosition(position + 1) // +1 as reverse order ...
        if (previous != null) {
            val durationSincePrevious = ChronoUnit.MILLIS.between(previous.dateTime.plus(previous.duration, ChronoUnit.MILLIS), item.dateTime)
            viewHolder.last.text = millisecondsToLabel(mInflater.context, durationSincePrevious)
        } else {
            viewHolder.last.text = "--:--"
        }
    }
}