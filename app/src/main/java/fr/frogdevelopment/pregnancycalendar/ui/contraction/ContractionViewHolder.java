package fr.frogdevelopment.pregnancycalendar.ui.contraction;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import fr.frogdevelopment.pregnancycalendar.R;

class ContractionViewHolder extends RecyclerView.ViewHolder {

    final TextView date;
    final TextView time;
    final TextView duration;
    final TextView last;

    ContractionViewHolder(View view) {
        super(view);

        date = view.findViewById(R.id.row_contraction_date);
        time = view.findViewById(R.id.row_contraction_time);
        duration = view.findViewById(R.id.row_contraction_duration);
        last = view.findViewById(R.id.row_contraction_last);
    }
}
