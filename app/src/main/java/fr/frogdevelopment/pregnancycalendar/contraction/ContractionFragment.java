package fr.frogdevelopment.pregnancycalendar.contraction;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import fr.frogdevelopment.pregnancycalendar.R;
import fr.frogdevelopment.pregnancycalendar.contraction.ContractionContract.Contraction;
import fr.frogdevelopment.pregnancycalendar.utils.SwipeUtil;

public class ContractionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private ZoneId zoneId = ZoneId.systemDefault();

	private View mRootView;
	private Chronometer mChronometer;
	private Button mButton;

	private ContractionAdapter mAdapter;
	private Contraction currentContraction;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		mRootView = inflater.inflate(R.layout.fragment_contraction, container, false);

		mChronometer = (Chronometer) mRootView.findViewById(R.id.chronometer);
		mChronometer.setBase(SystemClock.elapsedRealtime());

		mButton = (Button) mRootView.findViewById(R.id.chrono_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startOrStop();
			}
		});

		RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.chrono_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(itemDecoration);

		mAdapter = new ContractionAdapter(getActivity());
		recyclerView.setAdapter(mAdapter);

		SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity()) {
			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
				mAdapter.pendingRemoval(viewHolder);
			}

			@Override
			public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				if (mAdapter.isPendingRemoval(viewHolder)) {
					return 0;
				}

				return super.getSwipeDirs(recyclerView, viewHolder);
			}
		};
		//set swipe label
		swipeHelper.setLeftSwipeLabel("Supprimer");
		//set swipe background-Color
		swipeHelper.setLeftColorCode(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

		ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
		mItemTouchHelper.attachToRecyclerView(recyclerView);

		getLoaderManager().initLoader(666, null, this);

		return mRootView;
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

		mRootView.setBackgroundResource(R.drawable.background_chrono_started);
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

		Uri insertUri = getActivity().getContentResolver().insert(ContractionContentProvider.URI_CONTRACTION, values);

		long newId = ContentUris.parseId(insertUri);
		currentContraction.id = String.valueOf(newId);

		mAdapter.notifyDataSetChanged();

		mRootView.setBackgroundResource(R.drawable.background_chrono_stoped);
		mChronometer.setBase(SystemClock.elapsedRealtime());
		currentContraction = null;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(getActivity(), ContractionContentProvider.URI_CONTRACTION, ContractionContract.COLUMNS, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.clear();

		if (cursor != null) {
			Contraction item;
			final List<Contraction> rows = new ArrayList<>();
			while (cursor.moveToNext()) {
				item = new Contraction();
				item.id = cursor.getString(ContractionContract.INDEX_ID);
				item.dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(ContractionContract.INDEX_DATETIME)), zoneId);
				item.duration = cursor.getLong(ContractionContract.INDEX_DURATION);

				// Add the definition to the list
				rows.add(item);
			}

			cursor.close();

			mAdapter.addAll(rows);
		}

		getLoaderManager().destroyLoader(loader.getId());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

}
