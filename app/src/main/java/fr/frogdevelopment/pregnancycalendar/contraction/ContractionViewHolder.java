package fr.frogdevelopment.pregnancycalendar.contraction;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.frogdevelopment.pregnancycalendar.R;

class ContractionViewHolder extends RecyclerView.ViewHolder {

	final LinearLayout regularLayout;
	final TextView date;
	final TextView time;
	final TextView duration;
	final TextView last;

	final LinearLayout undoLayout;
	final TextView undo;

	ContractionViewHolder(View view) {
		super(view);

		regularLayout = (LinearLayout) view.findViewById(R.id.row_regular_layout);

		date = (TextView) view.findViewById(R.id.row_contraction_date);
		time = (TextView) view.findViewById(R.id.row_contraction_time);
		duration = (TextView) view.findViewById(R.id.row_contraction_duration);
		last = (TextView) view.findViewById(R.id.row_contraction_last);

		undoLayout = (LinearLayout) view.findViewById(R.id.row_undo_layout);
		undo = (TextView) view.findViewById(R.id.undo);
	}
}
