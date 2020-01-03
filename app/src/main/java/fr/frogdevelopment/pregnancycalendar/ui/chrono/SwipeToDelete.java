package fr.frogdevelopment.pregnancycalendar.ui.chrono;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import fr.frogdevelopment.pregnancycalendar.R;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

class SwipeToDelete extends ItemTouchHelper.SimpleCallback {

    @FunctionalInterface
    public interface DeleteListener {
        void onDelete(@NonNull RecyclerView.ViewHolder viewHolder);
    }

    private final DeleteListener mDeleteListener;

    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private final Drawable mBackground;
    private final Drawable mTrashIcon;
    private final int mIconMargin;

    SwipeToDelete(Context context, DeleteListener deleteListener) {
        super(ACTION_STATE_IDLE, LEFT | RIGHT);
        this.mDeleteListener = deleteListener;

        mBackground = new ColorDrawable(Color.RED);
        mTrashIcon = context.getResources().getDrawable(R.drawable.ic_baseline_delete_sweep_24, null);
        mIconMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // not important, we don't want drag & drop
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            return;
        }

        int backgroundCornerOffset = 20;
        int cornerRadius = 30;
        View itemView = viewHolder.itemView;

        int iconMargin = (itemView.getHeight() - mTrashIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + mTrashIcon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            // Draw background
            mBackground.setBounds(
                    itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + (int) dX + backgroundCornerOffset,
                    itemView.getBottom()
            );
            // Draw the delete icon
            mTrashIcon.setBounds(
                    itemView.getLeft() + iconMargin,
                    iconTop,
                    itemView.getLeft() + iconMargin + mTrashIcon.getIntrinsicWidth(),
                    iconBottom
            );
        } else if (dX < 0) { // Swiping to the left
            // Draw background
            mBackground.setBounds(
                    itemView.getRight() + (int) dX - backgroundCornerOffset,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom()
            );
            // Draw the delete icon
            mTrashIcon.setBounds(
                    itemView.getRight() - iconMargin - mTrashIcon.getIntrinsicWidth(),
                    iconTop,
                    itemView.getRight() - iconMargin,
                    iconBottom
            );
        } else { // view is unSwiped
            cornerRadius = 0;
            mBackground.setBounds(0, 0, 0, 0);
            mTrashIcon.setBounds(0, 0, 0, 0);
        }

        mBackground.draw(c);
        mTrashIcon.draw(c);

        if (viewHolder.itemView instanceof CardView) {
            CardView cardView = (CardView) viewHolder.itemView;
            cardView.setRadius(cornerRadius);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mDeleteListener.onDelete(viewHolder);
    }
}
