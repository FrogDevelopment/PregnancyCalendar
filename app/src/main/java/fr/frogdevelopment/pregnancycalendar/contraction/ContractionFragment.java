package fr.frogdevelopment.pregnancycalendar.contraction;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class ContractionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private ZoneId zoneId = ZoneId.systemDefault();

	private View mRootView;
	private Chronometer mChronometer;
	private Button mButton;

	private ContractionAdapter mAdapter;
	private Contraction currentContraction;
	private ItemTouchHelper mItemTouchHelper;
	private RecyclerView mRecyclerView;

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

		mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.chrono_list);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		mRecyclerView.addItemDecoration(itemDecoration);

		mAdapter = new ContractionAdapter(getActivity());
		mRecyclerView.setAdapter(mAdapter);

		mItemTouchHelper = new ItemTouchHelper(new SwipeToDelete(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT, getActivity()));
		mItemTouchHelper.attachToRecyclerView(mRecyclerView);

		getLoaderManager().initLoader(666, null, this);

		setHasOptionsMenu(true);

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

		mItemTouchHelper.attachToRecyclerView(null);
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

		mItemTouchHelper.attachToRecyclerView(mRecyclerView);
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


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.menu_contraction, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_delete:
				// Ask the user if they want to delete
				new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.ic_warning_black)
						.setTitle(R.string.delete_title)
						.setMessage(R.string.delete_confirmation)
						.setPositiveButton(R.string.delete_positive_button_continue, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								getActivity().getContentResolver().delete(ContractionContentProvider.URI_CONTRACTION, null, null);

								Snackbar.make(mRootView, R.string.delete_done, Snackbar.LENGTH_LONG).show();
								mAdapter.clear();
							}
						})
						.setNegativeButton(android.R.string.no, null)
						.show();

				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}


	private class SwipeToDelete extends ItemTouchHelper.SimpleCallback {

		// we want to cache these and not allocate anything repeatedly in the onChildDraw method
		private Drawable background;
		private Drawable xMark;

		private int xMarkMargin;

		private boolean initiated;
		private Context context;

		SwipeToDelete(int dragDirs, int swipeDirs, Context context) {
			super(dragDirs, swipeDirs);
			this.context = context;
		}

		private void init() {
			background = new ColorDrawable(Color.RED);
			xMark = ContextCompat.getDrawable(context, R.drawable.ic_delete);
			xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
			xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
			initiated = true;
		}

		@Override
		public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
			// not important, we don't want drag & drop
			return false;
		}

		@Override
		public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
			View itemView = viewHolder.itemView;

			// not sure why, but this method get's called for viewholder that are already swiped away
			if (viewHolder.getAdapterPosition() == -1) {
				// not interested in those
				return;
			}

			if (!initiated) {
				init();
			}

			// draw red background
			background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
			background.draw(c);

			if (dX <= -70) {
				// draw x mark
				int itemHeight = itemView.getBottom() - itemView.getTop();
				int intrinsicWidth = xMark.getIntrinsicWidth();
				int intrinsicHeight = xMark.getIntrinsicWidth();

				int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
				int xMarkRight = itemView.getRight() - xMarkMargin;
				int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
				int xMarkBottom = xMarkTop + intrinsicHeight;
				xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

				xMark.draw(c);

			}

			super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
		}

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
	}

}
