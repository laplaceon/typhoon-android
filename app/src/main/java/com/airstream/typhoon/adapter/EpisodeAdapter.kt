package com.airstream.typhoon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.squareup.picasso.Picasso
import com.uvnode.typhoon.extensions.model.Episode

class EpisodeAdapter() : RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {

    private val episodes: MutableList<Episode> = mutableListOf()

    fun clear() {
        val size = itemCount
        episodes.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun reorderList() {
        episodes.reverse()
        notifyDataSetChanged()
    }

    fun addAll(episodes: List<Episode>?, ascending: Boolean) {
        if (episodes != null) {
            this.episodes.addAll(episodes)
            if (!ascending) {
                this.episodes.reverse()
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = episodes.size

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_episode)
        val number: TextView = itemView.findViewById(R.id.num_episode)
        val title: TextView = itemView.findViewById(R.id.title_episode)
        val background: LinearLayout = itemView.findViewById(R.id.episode_item_root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodes[position]

        if (!episode.image.isNullOrBlank()) {
            Picasso.get().load(episode.image).centerCrop().fit().into(holder.image)
            holder.image.visibility = View.VISIBLE
            holder.title.isSingleLine = false
        } else {
            holder.image.visibility = View.GONE
            holder.title.isSingleLine = true
        }

        if (episode.num.isNotEmpty()) {
            holder.number.visibility = View.VISIBLE
            holder.number.text = episode.num
        } else {
            holder.number.visibility = View.GONE
        }

        holder.title.text = episode.title
    }
}