package com.appero.vehiclecomplaint.presentation.camera.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.appero.vehiclecomplaint.utilities.OnClickListenerAdapter

class ImageAdapter(
    private val imageList: List<String>,
    private val onClick: OnClickListenerAdapter<String>
): RecyclerView.Adapter<ItemImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemImageViewHolder {
        return ItemImageViewHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ItemImageViewHolder, position: Int) {
//        val item = getItem(position)
        val imagePath = imageList[position]
        Log.e("TAG", "onBindViewHolder: IM ADAPTER ${imagePath}", )
        (holder as ItemImageViewHolder).bind(imagePath)
        holder.itemView.setOnClickListener {
            onClick.onClick(imagePath)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String
            ): Boolean = oldItem == newItem

        }
    }
}