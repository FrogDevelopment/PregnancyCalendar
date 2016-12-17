package fr.frogdevelopment.pregnancycalendar.contraction;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
        mButton.setOnClickListener(view -> startOrStop());

        ListView mChronoList = (ListView) rootView.findViewById(R.id.chrono_list);
        mChronoList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mChronoList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            final private Set<Integer> selectedRows = new HashSet<>();

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selectedRows.add(position);
                } else {
                    selectedRows.remove(position);
                }

                mode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                getActivity().getMenuInflater().inflate(R.menu.menu_contraction, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        onDelete(actionMode, selectedRows);
                        break;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                selectedRows.clear();
            }
        });

        mAdapter = new ContractionCursorAdapter(getActivity());
        mChronoList.setAdapter(mAdapter);

        getLoaderManager().initLoader(666, null, this);

        return rootView;
    }

    private void onDelete(final ActionMode actionMode, final Set<Integer> selectedRows) {
        final int nbSelectedRows = selectedRows.size();
        // Ask the user if they want to delete
        new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.ic_warning_black)
                .setTitle(R.string.delete_title)
                .setMessage(getResources().getQuantityString(R.plurals.delete_confirmation, nbSelectedRows, nbSelectedRows))
                .setPositiveButton(R.string.positive_button_continue, (dialog, which) -> {
                    if (nbSelectedRows == 1) {
                        final Contraction item = mAdapter.getItem(selectedRows.iterator().next());
                        Uri uri = Uri.parse(ContractionContentProvider.URI_CONTRACTION + "/" + item.id);
                        ContractionFragment.this.getActivity().getContentResolver().delete(uri, null, null);
                    } else {
                        StringBuilder inList = new StringBuilder(nbSelectedRows * 2);
                        final String[] selectionArgs = new String[nbSelectedRows];
                        int i = 0;
                        Contraction item;
                        for (Integer position : selectedRows) {
                            if (i > 0) {
                                inList.append(",");
                            }
                            inList.append("?");

                            item = mAdapter.getItem(position);
                            selectionArgs[i] = item.id;
                            i++;
                        }

                        final String selection = "_ID IN (" + inList.toString() + ")";
                        ContractionFragment.this.getActivity().getContentResolver().delete(ContractionContentProvider.URI_CONTRACTION, selection, selectionArgs);
                    }

                    Snackbar.make(getView(), R.string.delete_done, Snackbar.LENGTH_LONG).show();
                    actionMode.finish();

                    getLoaderManager().restartLoader(666, null, ContractionFragment.this);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
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
                item.id = cursor.getString(ContractionContract.INDEX_ID);
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
