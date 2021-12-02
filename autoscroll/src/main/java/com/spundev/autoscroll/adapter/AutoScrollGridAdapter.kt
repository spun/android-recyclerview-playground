package com.spundev.autoscroll.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spundev.autoscroll.R
import com.spundev.autoscroll.databinding.AscRvGridItemBinding
import com.spundev.autoscroll.model.AutoScrollItem

class AutoScrollGridAdapter internal constructor(
    context: Context,
    val onClick: (item: AutoScrollItem) -> Unit,
) : ListAdapter<AutoScrollItem, AutoScrollGridAdapter.EpisodeViewHolder>(object :
    DiffUtil.ItemCallback<AutoScrollItem>() {
    override fun areItemsTheSame(oldItem: AutoScrollItem, newItem: AutoScrollItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: AutoScrollItem, newItem: AutoScrollItem) =
        oldItem == newItem
}) {
    init {
        setHasStableIds(true)
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding: AscRvGridItemBinding =
            AscRvGridItemBinding.inflate(inflater, parent, false)
        return EpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }


    val blueColor = ContextCompat.getColor(context, R.color.asc_blue_pastel)
    val redColor = ContextCompat.getColor(context, R.color.asc_red_pastel)
    val greenColor = ContextCompat.getColor(context, R.color.asc_green_pastel)

    inner class EpisodeViewHolder(
        private val binding: AscRvGridItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentItem: AutoScrollItem? = null

        init {
            binding.root.setOnClickListener { currentItem?.let(onClick) }
        }

        fun bindTo(item: AutoScrollItem?) {
            currentItem = item
            if (item != null) {
                binding.itemTextTextview.text = item.text
                if (item.isActive) {
                    binding.root.setBackgroundColor(greenColor)
                } else {
                    binding.root.setBackgroundColor(redColor)
                }
            } else {
                binding.itemTextTextview.text = ""
                binding.root.setBackgroundColor(blueColor)
            }
        }
    }
}