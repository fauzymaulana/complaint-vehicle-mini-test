package com.appero.vehiclecomplaint.presentation.camera.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.appero.vehiclecomplaint.R
import com.appero.vehiclecomplaint.databinding.ItemLatestImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File

class ItemImageViewHolder(private val binding: ItemLatestImageBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(image: String) {
        val file = File(image)

        with(itemView) {
            Glide.with(context)
                .load(Uri.fromFile(file))
//                .load(Uri.parse(image))
//                .override(100, 100)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.img_not_found)
                .into(binding.imgLatestItem)
        }
    }

    companion object {
        fun create(view: ViewGroup): ItemImageViewHolder {
            val inflater = LayoutInflater.from(view.context)
            val binding = ItemLatestImageBinding.inflate(inflater)

            return ItemImageViewHolder(binding)
        }
    }
}