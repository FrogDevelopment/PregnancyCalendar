package fr.frogdevelopment.pregnancycalendar.ui.contraction

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import fr.frogdevelopment.pregnancycalendar.R

internal class SwipeToDelete(context: Context, private val mDeleteListener: DeleteListener) : SimpleCallback(ACTION_STATE_IDLE, LEFT or RIGHT) {

    @FunctionalInterface
    interface DeleteListener {
        fun onDelete(viewHolder: RecyclerView.ViewHolder)
    }

    private val mBackground: Drawable
    private val mTrashIcon: Drawable

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean { // not important, we don't want drag & drop
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        if (actionState != ACTION_STATE_SWIPE) {
            return
        }

        val backgroundCornerOffset = 20
        var cornerRadius = 30
        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - mTrashIcon.intrinsicHeight) / 2
        val iconTop = itemView.top + iconMargin
        val iconBottom = iconTop + mTrashIcon.intrinsicHeight

        when {
            // Swiping to the right
            dX > 0 -> {
                // Draw background
                mBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt() + backgroundCornerOffset,
                        itemView.bottom
                )
                // Draw the delete icon
                mTrashIcon.setBounds(
                        itemView.left + iconMargin,
                        iconTop,
                        itemView.left + iconMargin + mTrashIcon.intrinsicWidth,
                        iconBottom
                )
            }

            // Swiping to the left
            dX < 0 -> {
                // Draw background
                mBackground.setBounds(
                        itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                )
                // Draw the delete icon
                mTrashIcon.setBounds(
                        itemView.right - iconMargin - mTrashIcon.intrinsicWidth,
                        iconTop,
                        itemView.right - iconMargin,
                        iconBottom
                )
            }

            // view is unSwiped
            else -> {
                cornerRadius = 0
                mBackground.setBounds(0, 0, 0, 0)
                mTrashIcon.setBounds(0, 0, 0, 0)
            }
        }

        mBackground.draw(c)
        mTrashIcon.draw(c)

        if (viewHolder.itemView is CardView) {
            val cardView = viewHolder.itemView as CardView
            cardView.radius = cornerRadius.toFloat()
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mDeleteListener.onDelete(viewHolder)
    }

    init {
        mBackground = ColorDrawable(Color.RED)
        mTrashIcon = context.resources.getDrawable(R.drawable.ic_baseline_delete_sweep_24, null)
    }
}