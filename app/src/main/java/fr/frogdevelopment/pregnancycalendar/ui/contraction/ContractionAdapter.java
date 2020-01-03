package fr.frogdevelopment.pregnancycalendar.ui.contraction;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.data.Contraction;

import static fr.frogdevelopment.pregnancycalendar.utils.DateLabelUtils.millisecondsToLabel;
import static java.time.temporal.ChronoUnit.MILLIS;

class ContractionAdapter extends RecyclerView.Adapter<ContractionViewHolder> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);

    private final LayoutInflater mInflater;

    private final List<Contraction> mRows = new ArrayList<>();

    ContractionAdapter(@NonNull Activity activity) {
        mInflater = activity.getLayoutInflater();
    }

    void setContractions(List<Contraction> contractions) {
        mRows.clear();
        mRows.addAll(contractions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mRows.size();
    }

    Contraction getAtPosition(int position) {
        try {
            return mRows.get(getItemCount() - position - 1); // reverse order
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    Contraction getAtIndex(int index) {
        return mRows.get(index);
    }

    @NonNull
    @Override
    public ContractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = mInflater.inflate(R.layout.contraction_row, parent, false);
        return new ContractionViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContractionViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Contraction item = getAtPosition(position);

        if (item == null) {
            return;
        }

        if (viewHolder.itemView instanceof CardView) {
            CardView cardView = (CardView) viewHolder.itemView;
            cardView.setRadius(0);
        }

        viewHolder.date.setText(DATE_FORMATTER.format(item.dateTime));
        viewHolder.time.setText(TIME_FORMATTER.format(item.dateTime));

        if (item.duration != null) {
            viewHolder.duration.setText(millisecondsToLabel(item.duration));
        } else {
            viewHolder.duration.setText("--:--");
        }

        Contraction previous = getAtPosition(position + 1); // +1 as reverse order ...
        if (previous != null) {
            long durationSincePrevious = MILLIS.between(previous.dateTime.plus(previous.duration, MILLIS), item.dateTime);
            viewHolder.last.setText(millisecondsToLabel(durationSincePrevious));
        } else {
            viewHolder.last.setText("--:--");
        }
    }

}
