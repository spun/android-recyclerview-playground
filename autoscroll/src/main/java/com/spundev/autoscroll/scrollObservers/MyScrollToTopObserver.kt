package com.spundev.autoscroll.scrollObservers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView.AdapterDataObserver that "locks" the scroll to the top if the user is already at
 * the start of the list and the RecyclerView receives new items to add to the start.
 */
open class MyScrollToTopObserver(
    private val recycler: RecyclerView,
    private val manager: LinearLayoutManager
) : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val firstVisiblePosition = manager.findFirstCompletelyVisibleItemPosition()
        // If the recycler view is initially being loaded or the
        // user is at the top of the list, scroll to the top
        // of the list to show the newly added item.
        val loading = firstVisiblePosition == -1
        if (!loading) {
            val atTop = firstVisiblePosition == 0
            if (atTop) {
                recycler.scrollToPosition(0)
            }
        }
    }
}
