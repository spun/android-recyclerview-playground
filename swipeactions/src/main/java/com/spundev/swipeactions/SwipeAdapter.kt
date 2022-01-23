package com.spundev.swipeactions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spundev.swipeactions.databinding.SwaRvSwipeActionsItemBinding

class SwipeAdapter internal constructor(
    context: Context,
) : ListAdapter<SwipeItem, SwipeAdapter.MyViewHolder>(object : DiffUtil.ItemCallback<SwipeItem>() {
    override fun areItemsTheSame(oldItem: SwipeItem, newItem: SwipeItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: SwipeItem, newItem: SwipeItem) = oldItem == newItem
}), SwipeableAdapter<SwipeItem> {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: SwaRvSwipeActionsItemBinding =
            SwaRvSwipeActionsItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    val blueColor = ContextCompat.getColor(context, R.color.swa_swipe_blue)
    val redColor = ContextCompat.getColor(context, R.color.swa_swipe_red)
    val greenColor = ContextCompat.getColor(context, R.color.swa_swipe_green)

    inner class MyViewHolder(
        private val binding: SwaRvSwipeActionsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentItem: SwipeItem? = null

        fun bindTo(item: SwipeItem?) {
            currentItem = item
            if (item != null) {
                binding.itemTextTextview.text = item.title
                if (item.isActive) {
                    binding.itemIsActiveImageView.setColorFilter(greenColor)
                } else {
                    binding.itemIsActiveImageView.setColorFilter(redColor)
                }
            } else {
                binding.itemTextTextview.text = ""
                binding.itemIsActiveImageView.setColorFilter(blueColor)
            }
        }
    }

    override fun getSwipedItem(position: Int): SwipeItem? = getItem(position)
    override fun getSwipedItemId(position: Int): Int = getItem(position)?.id ?: -1
}
