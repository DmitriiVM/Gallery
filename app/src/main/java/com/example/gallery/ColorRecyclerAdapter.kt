package com.example.gallery

import android.content.res.Configuration
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_item.view.*

class ColorRecyclerAdapter(
    private val listener: OnItemClickListener,
    private val longListener: OnItemLongClickListener
) : RecyclerView.Adapter<ColorRecyclerAdapter.ColorViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(uri: Uri)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(uri: Uri)
    }

    private var imageUriList = listOf<Uri>()

    fun setImages(imageUriList: List<Uri>) {
        this.imageUriList = imageUriList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder =
        ColorViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.image_item,
                parent,
                false
            ),
            listener,
            longListener
        )

    override fun getItemCount(): Int = imageUriList.size

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.onBind(imageUriList[position])
    }

    class ColorViewHolder(
        private val view: View,
        private val listener: OnItemClickListener,
        private val longListener: OnItemLongClickListener
    ) : RecyclerView.ViewHolder(view) {
        fun onBind(uri: Uri) {

            Glide.with(view).load(uri).into(view.imageView)

            view.setOnClickListener {
                listener.onItemClick(uri)
            }
            view.setOnLongClickListener {
                longListener.onItemLongClick(uri)
                return@setOnLongClickListener true
            }
        }
    }
}