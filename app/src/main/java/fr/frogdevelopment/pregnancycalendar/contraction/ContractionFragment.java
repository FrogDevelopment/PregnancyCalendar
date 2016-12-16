package fr.frogdevelopment.pregnancycalendar.contraction;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.contraction.ContractionContract.Contraction;

public class ContractionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Chronometer mChronometer;
    private Button mButton;

    private ContractionCursorAdapter mAdapter;
    private Contraction currentContraction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contraction, container, false);

        mChronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());

        mButton = (Button) rootView.findViewById(R.id.chrono_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrStop();
            }
        });

        ListView mChronoList = (ListView) rootView.findViewById(R.id.chrono_list);

        mAdapter = new ContractionCursorAdapter(getActivity());
        mChronoList.setAdapter(mAdapter);

        getLoaderManager().initLoader(666, null, this);

        return rootView;
    }

    private void startOrStop() {
        if (currentContraction == null) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        currentContraction = new Contraction();
        currentContraction.dateTime = LocalDateTime.now();

        mButton.setText(R.string.stop);
        mButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stop, 0, 0, 0);

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        mAdapter.add(currentContraction);
    }

    private void stop() {
        mChronometer.stop();
        LocalDateTime mStopDateTime = LocalDateTime.now();
        mButton.setText(R.string.start);
        mButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_start, 0, 0, 0);

        currentContraction.duration = ChronoUnit.MILLIS.between(currentContraction.dateTime, mStopDateTime);

        final ContentValues values = new ContentValues();
        values.put(ContractionContract.DATETIME, currentContraction.dateTime.atZone(zoneId).toEpochSecond());
        values.put(ContractionContract.DURATION, currentContraction.duration);

        currentContraction = null;
        mAdapter.notifyDataSetChanged();

        getActivity().getContentResolver().insert(ContractionContentProvider.URI_CONTRACTION, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ContractionContentProvider.URI_CONTRACTION, ContractionContract.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        getLoaderManager().destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
    private ZoneId zoneId = ZoneId.systemDefault();

    private class ViewHolder {

        private final TextView date;
        private final TextView time;
        private final TextView duration;
        private final TextView last;

        private ViewHolder(View view) {
            date = (TextView) view.findViewById(R.id.row_contraction_date);
            time = (TextView) view.findViewById(R.id.row_contraction_time);
            duration = (TextView) view.findViewById(R.id.row_contraction_duration);
            last = (TextView) view.findViewById(R.id.row_contraction_last);
        }
    }

    private class ContractionCursorAdapter extends SimpleCursorAdapter {

        private final LayoutInflater mInflater;
        private final List<Contraction> rows = new ArrayList<>();

        private ContractionCursorAdapter(Activity context) {
            super(context, 0, null, ContractionContract.COLUMNS, null, 0);

            mInflater = context.getLayoutInflater();
        }

//        // FIXME find sql request to try directly with cursor newView and bindView
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            View view = mInflater.inflate(R.layout.row_contraction, parent, false);
//            ViewHolder holder = new ViewHolder(view);
//            view.setTag(holder);
//
//            return view;
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            ViewHolder viewHolder = (ViewHolder) view.getTag();
//        }

        private void add(Contraction item) {
            rows.add(item);
            notifyDataSetChanged();
        }

        @Override
        public Cursor swapCursor(Cursor cursor) {
//            super.swapCursor(cursor);

            rows.clear();

            if (cursor == null) {
                notifyDataSetChanged();
                return null;
            }

            Contraction item;
            while (cursor.moveToNext()) {
                item = new Contraction();
                item.dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(ContractionContract.INDEX_DATETIME)), zoneId);
                item.duration = cursor.getLong(ContractionContract.INDEX_DURATION);

                // Add the definition to the list
                rows.add(item);
            }

            notifyDataSetChanged();

            return cursor;
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Contraction getItem(int position) {
            try {
                return rows.get(getCount() - position - 1); // reverse order
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_contraction, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Contraction current = getItem(position);
            if (current != null) {
                holder.date.setText(current.dateTime.format(dateFormatter));
                holder.time.setText(current.dateTime.format(timeFormatter));

                if (current.duration != null) {
                    holder.duration.setText(String.format(
                            Locale.getDefault()
                            , "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(current.duration),
                            TimeUnit.MILLISECONDS.toSeconds(current.duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(current.duration))
                    ));
                } else {
                    holder.duration.setText("--:--");
                }

                Contraction previous = getItem(position + 1); // +1 as reverse order ...
                if (previous != null) {
                    long durationSincePrevious = ChronoUnit.MILLIS.between(previous.dateTime.plus(previous.duration, ChronoUnit.MILLIS), current.dateTime);
                    holder.last.setText(String.format(
                            Locale.getDefault()
                            , "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(durationSincePrevious),
                            TimeUnit.MILLISECONDS.toSeconds(durationSincePrevious) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationSincePrevious))
                    ));
                } else {
                    holder.last.setText("--:--");
                }
            }

            return convertView;
        }


    }

}
