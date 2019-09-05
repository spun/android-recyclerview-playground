package com.spundev.fixedheader.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spundev.fixedheader.R
import com.spundev.fixedheader.model.FixHeaderItemModel

class FixHeaderAdapter internal constructor(
    context: Context
) : ListAdapter<FixHeaderItemModel, FixHeaderAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FixHeaderItemModel>() {
            override fun areItemsTheSame(oldItem: FixHeaderItemModel, newItem: FixHeaderItemModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: FixHeaderItemModel,
                newItem: FixHeaderItemModel
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = inflater.inflate(R.layout.fix_recyclerview_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bindTo(getItem(position))


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.nameTextView)

        fun bindTo(item: FixHeaderItemModel) {
            name.text = item.name
        }
    }
}