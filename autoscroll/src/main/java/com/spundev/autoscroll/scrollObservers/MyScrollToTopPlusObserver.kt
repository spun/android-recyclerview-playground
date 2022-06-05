package com.spundev.autoscroll.scrollObservers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MyScrollToTopPlusObserver(
    private val recycler: RecyclerView,
    private val manager: LinearLayoutManager
) : MyScrollToTopObserver(recycler, manager) {
    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
        val firstVisiblePosition = manager.findFirstCompletelyVisibleItemPosition()
        // If the recycler view is initially being loaded or the
        // user is at the top of the list, scroll to the top
        // of the list to show the newly added item.
        val loading = firstVisiblePosition == -1
        if (!loading) {
            val atTop = firstVisiblePosition == 0
            // We don't want the list to be jumping all over the place, is more comprehensible if
            // the list stays where it is when an item moves.
            // The only special case is when the list is at the start and one item is moved to the top.
            // In that case we want to keep the list "locked" to the top.
            if (atTop) {
                recycler.scrollToPosition(0)
            } else {
                // scrollToPosition (from LinearLayoutManager) will scroll the minimum amount that is
                // necessary to make the target position visible
                recycler.scrollToPosition(fromPosition)
            }
        }
    }
}