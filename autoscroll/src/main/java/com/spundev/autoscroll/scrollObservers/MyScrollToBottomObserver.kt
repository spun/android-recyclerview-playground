package com.spundev.autoscroll.scrollObservers

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * RecyclerView.AdapterDataObserver that locks the scroll to the bottom if the user is already at
 * the end of the list and the RecyclerView receives new items to add to the end.
 * A chat is a good example for what we are looking to implement. If the user is at the bottom of
 * the list and receives a new message, we want the RecyclerView to scroll to show the new messages.
 * But, if the user is looking to old messages and is not at the bottom of the list, we don't want
 * to scroll when a new message is added to the list.
 * From https://github.com/firebase/codelab-friendlychat-android
 * Modified to keep the scroll lock if we add more than one item
 */
class MyScrollToBottomObserver(
    private val recycler: RecyclerView,
    private val manager: LinearLayoutManager,
    private val adapter: RecyclerView.Adapter<*>
) : RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        val count = adapter.itemCount
        val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added item.
        val loading = lastVisiblePosition == -1
        val atBottom =
            positionStart >= count - itemCount && lastVisiblePosition == positionStart - 1
        if (loading || atBottom) {
            val endPosition = positionStart + itemCount - 1
            recycler.scrollToPosition(endPosition)
        }
    }
}
