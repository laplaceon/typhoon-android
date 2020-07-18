package com.airstream.typhoon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.squareup.picasso.Picasso
import com.uvnode.typhoon.extensions.model.Series

class SeriesAdapter : RecyclerView.Adapter<SeriesAdapter.ViewHolder>() {

    private val series: MutableList<Series> = mutableListOf()

    val onBottomReachedListener: RecyclerListener.OnBottomReachedListener? = null

    fun clear() {
        val size = itemCount
        series.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun add(series: Series) {
        this.series.add(series)
        notifyItemInserted(itemCount)
    }

    fun addAll(series: List<Series>) {
        val size = itemCount
        this.series.addAll(series)
        notifyItemRangeInserted(size, series.size)
    }

    fun getItem(position: Int) = series[position]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_card)
        val title: TextView = itemView.findViewById(R.id.title_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_series, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount() = series.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = series[position]

        if (!s.image.isNullOrBlank()) {
            Picasso.get().load(s.image).centerCrop().fit()
                .placeholder(R.drawable.source_image_placeholder).into(holder.image)
        }
        holder.title.text = s.title

        if (null != onBottomReachedListener && position == itemCount - 1) {
            onBottomReachedListener.onBottomReached(position)
        }
    }
}