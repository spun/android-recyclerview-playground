package com.spundev.multiselect.adapter

import android.content.Context
import android.os.Build
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spundev.commonresources.data.ItemModel
import com.spundev.multiselect.R

class GridAdapter internal constructor(
        context: Context,
        val onItemClick: (Int, View) -> Unit
) : ListAdapter<ItemModel, GridAdapter.UserViewHolder>(DIFF_CALLBACK) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    // Selection tracker
    private var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemModel>() {
            override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel) = oldItem === newItem
            override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = inflater.inflate(R.layout.ms_recyclerview_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindTo(getItem(position))

        if (tracker?.isSelected(position.toLong()) == true) {
            // Show scrim and checkbox if selected
            holder.markAsSelected()
        } else {
            // Reset color to white if not selected
            holder.markAsNotSelected()
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val image: ImageView = itemView.findViewById(R.id.imageView)
        private val selectionFeedback: FrameLayout = itemView.findViewById(R.id.selectionFeedback)

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition, image) }
        }

        fun bindTo(item: ItemModel) {
            image.setImageResource(item.image)
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
                object : ItemDetailsLookup.ItemDetails<Long>() {
                    override fun getPosition(): Int = adapterPosition

                    override fun getSelectionKey(): Long? = itemId
                }

        fun markAsSelected() {
            itemView.isActivated = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(selectionFeedback)
            }
            selectionFeedback.visibility = View.VISIBLE
        }

        fun markAsNotSelected() {
            itemView.isActivated = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(selectionFeedback)
            }
            selectionFeedback.visibility = View.INVISIBLE
        }
    }
}