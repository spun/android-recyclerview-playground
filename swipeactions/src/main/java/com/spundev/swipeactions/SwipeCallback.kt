package com.spundev.swipeactions

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class SwipeCallback<T>(
    private val context: Context,
    private val adapter: SwipeableAdapter<T>,
    private val swipeRightColor: ColorDrawable,
    private val swipeLeftColor: ColorDrawable,
    private val swipeRightIcon: Drawable? = null,
    private val swipeLeftIcon: Drawable? = null,
    private val onSwipeListener: (item: T?, direction: Int) -> Unit = { _, _ -> }
) : ItemTouchHelper.SimpleCallback(
    0 /* Disable drag to move */,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT /* Enable LEFT AND RIGHT swipe */
) {
    // Rect where we are going to draw the background and the icons
    private val rectBounds = Rect()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Note: If we are using a ConcatAdapter, we need to decide if the best way of getting the
        // position is by using getBindingAdapterPosition or getAbsoluteAdapterPosition
        //  [ [1 2 3] [1 2 3] ] -> getBindingAdapterPosition
        //  [ [1 2 3] [4 5 6] ] -> getAbsoluteAdapterPosition
        val position = viewHolder.bindingAdapterPosition
        val swipedItem = adapter.getSwipedItem(position)
        onSwipeListener(swipedItem, direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView

        if (dX > 0) { // Swiping from left to right
            // Calculate the Rect left by the swipe gesture
            rectBounds.set(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
            // Limit the background and icon that we are going to draw to the rect we calculated
            c.save()
            c.clipRect(rectBounds)
            // Add a fade in effect to our background and icon.
            // The fadeIn will be completed when the swipe gesture reaches 1/4
            val progress = (dX / (itemView.width / 4)).coerceAtMost(1f)
            val alpha = (progress * 255).toInt()
            // Draw background
            swipeRightColor.alpha = alpha
            swipeRightColor.bounds = rectBounds
            swipeRightColor.draw(c)
            // Draw icon
            if (swipeRightIcon != null) {
                val iconMargin = (itemView.height - swipeRightIcon.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + swipeRightIcon.intrinsicHeight
                // We want out icon to have an easeOut enter animation relative to the swipe progress
                val easeOutProgress = 1 - (1 - progress) * (1 - progress)
                val easeOutMargin = (iconMargin * easeOutProgress).toInt()
                val iconLeft = itemView.left + easeOutMargin
                val iconRight = itemView.left + easeOutMargin + swipeRightIcon.intrinsicWidth
                swipeRightIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                swipeRightIcon.alpha = alpha
                swipeRightIcon.draw(c)
            }
            // Restore canvas
            c.restore()
        } else if (dX < 0) { // Swiping from right to left
            // Calculate the Rect left by the swipe gesture
            rectBounds.set(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            // Limit the background and icon that we are going to draw to the rect we calculated
            c.save()
            c.clipRect(rectBounds)
            // Add a fade in effect to our background and icon.
            // The fadeIn will be completed when the swipe gesture reaches 1/4
            val progress = (dX / (-itemView.width / 4)).coerceAtMost(1f)
            val alpha = (progress * 255).toInt()
            // Draw background
            swipeLeftColor.alpha = alpha
            swipeLeftColor.bounds = rectBounds
            swipeLeftColor.draw(c)
            // Draw icon
            if (swipeLeftIcon != null) {
                val iconMargin = (itemView.height - swipeLeftIcon.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconBottom = iconTop + swipeLeftIcon.intrinsicHeight
                // We want out icon to have an easeOut enter animation relative to the swipe progress
                val easeOutProgress = 1 - (1 - progress) * (1 - progress)
                val easeOutMargin = (iconMargin * easeOutProgress).toInt()
                val iconLeft = itemView.right - easeOutMargin - swipeLeftIcon.intrinsicWidth
                val iconRight = itemView.right - easeOutMargin
                swipeLeftIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                swipeLeftIcon.alpha = alpha
                swipeLeftIcon.draw(c)
            }
            // Restore canvas
            c.restore()
        }
    }
}

interface SwipeableAdapter<T> {
    fun getSwipedItem(position: Int): T?
    fun getSwipedItemId(position: Int): Int
}
