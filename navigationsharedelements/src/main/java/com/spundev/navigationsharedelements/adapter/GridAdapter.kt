package com.spundev.navigationsharedelements.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spundev.commonresources.data.ItemModel
import com.spundev.navigationsharedelements.R

class GridAdapter internal constructor(
    context: Context,
    val onItemClick: (Int, View) -> Unit
) : ListAdapter<ItemModel, GridAdapter.UserViewHolder>(DIFF_CALLBACK) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemModel>() {
            override fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel) = oldItem === newItem
            override fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = inflater.inflate(R.layout.nse_recyclerview_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val image: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener { onItemClick(adapterPosition, image) }
        }

        fun bindTo(item: ItemModel) {
            image.setImageResource(item.image)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image.transitionName = item.transitionName
            }
        }
    }
}