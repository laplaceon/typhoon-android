package com.airstream.typhoon.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.extension.ExtensionHolder
import com.airstream.typhoon.ui.extensions.ExtensionInfoActivity
import com.airstream.typhoon.utils.Injector
import com.squareup.picasso.Picasso


class ExtensionAdapter(private val ctx: Context) : RecyclerView.Adapter<ExtensionAdapter.ViewHolder>() {

    private val extensions: MutableList<ExtensionHolder> = mutableListOf()

    fun clear() {
        val size = itemCount
        extensions.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun addAll(extensions: List<ExtensionHolder>) {
        val size = itemCount
        this.extensions.addAll(extensions)
        notifyItemRangeInserted(size, extensions.size)
    }

    private fun getItem(position: Int) = extensions[position]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
        val version: TextView = itemView.findViewById(R.id.version)
        val installButton: ImageButton = itemView.findViewById(R.id.button_install)
        val manageButton: ImageButton = itemView.findViewById(R.id.button_manage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_extension, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount() = extensions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val extension = getItem(position)

        holder.name.text = extension.extension?.name
        holder.version.text = extension.extension?.version

        if (extension.isInstalled) {
            holder.manageButton.visibility = View.VISIBLE
            holder.icon.setImageDrawable(extension.icon)

            if (extension.hasUpdate) {
                holder.installButton.visibility = View.VISIBLE
            } else {
                holder.installButton.visibility = View.GONE
            }
        } else {
            holder.manageButton.visibility = View.GONE
            holder.installButton.visibility = View.VISIBLE

            if (!extension.iconUrl.isNullOrBlank()) {
                Picasso.get().load(extension.iconUrl).centerCrop().fit().into(holder.icon)
            }
        }

        holder.installButton.setOnClickListener {
            Injector.getExtensionManager(ctx).downloadAndInstall(extension.url!!, extension.extension!!.packageName);
        }

        holder.manageButton.setOnClickListener {
            val intent = Intent(ctx, ExtensionInfoActivity::class.java)
            intent.putExtra("extension", extension.extension!!.packageName)
            ctx.startActivity(intent)
        }
    }
}