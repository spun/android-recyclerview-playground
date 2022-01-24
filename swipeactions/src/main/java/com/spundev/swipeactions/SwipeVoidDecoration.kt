package com.spundev.swipeactions

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Fill void left by the item swiped during animation
 * ItemDecoration idea from: https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete
 */
class SwipeVoidDecoration constructor(context: Context) : RecyclerView.ItemDecoration() {

    // FIXME: This works well when the swipe gesture removes the item from the list but, if the
    //  gesture is used to change some property that cause a reordering (ie: read/unread) the void
    //  filled by this decoration is not correct. To support the reordering we also need to add
    //  different color backgrounds and apply the correct one.

    private val background =
        ColorDrawable(ContextCompat.getColor(context, R.color.swa_swipe_orange))

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        // Is the animation running?
        if (parent.itemAnimator?.isRunning == true) {

            parent.layoutManager?.let { rvLayoutManager ->
                // We need to find the gap left from the item removed/moved. To do that, we go
                // over the items shown by the recyclerView and check if they are moving to fill
                // the gap.
                var itemAboveGap: View? = null
                var itemBelowGap: View? = null

                val childCount = rvLayoutManager.childCount
                for (i in 0 until childCount) {
                    val child = parent.layoutManager!!.getChildAt(i)
                    if (child?.translationY!! < 0) { // child is moving down
                        itemAboveGap = child
                    } else if (child.translationY > 0) { // child is moving up
                        itemBelowGap = child
                        // When we find the first child moving up, we can stop searching
                        break
                    }
                }

                // Calculate void position
                var top = 0
                var bottom = 0
                if (itemAboveGap != null && itemBelowGap != null) {
                    top = itemAboveGap.bottom + itemAboveGap.translationY.toInt()
                    bottom = itemBelowGap.top + itemBelowGap.translationY.toInt()
                } else if (itemAboveGap != null) {
                    top = itemAboveGap.bottom + itemAboveGap.translationY.toInt()
                    bottom = itemAboveGap.bottom
                } else if (itemBelowGap != null) {
                    top = itemBelowGap.top
                    bottom = itemBelowGap.top + itemBelowGap.translationY.toInt()
                }

                // Draw background color
                if (top != bottom) {
                    background.setBounds(0, top, parent.width, bottom)
                    background.draw(c)
                }
            }
        }
        super.onDraw(c, parent, state)
    }
}
