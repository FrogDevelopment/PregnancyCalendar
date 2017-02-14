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

	final LinearLayout swipeLayout;
	final TextView undo;

	ContractionViewHolder(View view) {
		super(view);

		regularLayout = (LinearLayout) view.findViewById(R.id.regularLayout);

		date = (TextView) view.findViewById(R.id.row_contraction_date);
		time = (TextView) view.findViewById(R.id.row_contraction_time);
		duration = (TextView) view.findViewById(R.id.row_contraction_duration);
		last = (TextView) view.findViewById(R.id.row_contraction_last);

		swipeLayout = (LinearLayout) view.findViewById(R.id.swipeLayout);
		undo = (TextView) view.findViewById(R.id.undo);
	}
}
