package com.airstream.typhoon.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.data.library.entities.Category
import com.google.android.material.checkbox.MaterialCheckBox

class LibraryCategoryAdapter(val ctx: Context, private val showChecks: Boolean) : RecyclerView.Adapter<LibraryCategoryAdapter.ViewHolder>() {

    private var libraryCategories: List<Category> = listOf()

    var onCheckChangedListener: RecyclerListener.OnCheckChangedListener? = null
    var onEditListener: OnEditCategoryListener? = null

    fun setList(libraryCategories: List<Category>) {
        this.libraryCategories = libraryCategories
        notifyDataSetChanged()
    }

    override fun getItemCount() = libraryCategories.size

    private fun getItem(position: Int) = libraryCategories[position]

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.category_name)
        val check: MaterialCheckBox = itemView.findViewById(R.id.category_checks)
        val editButton: Button = itemView.findViewById(R.id.category_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_library_category, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)

        holder.name.text = category.name

        if (showChecks) {
            holder.check.visibility = MaterialCheckBox.VISIBLE
            holder.editButton.visibility = Button.GONE

            holder.check.isChecked = category.hasSeries

            holder.check.setOnCheckedChangeListener { _, isChecked ->
                if (null != onCheckChangedListener) {
                    onCheckChangedListener!!.onCheckChanged(category.id, isChecked)
                }
            }
        } else {
            holder.check.visibility = MaterialCheckBox.GONE
            holder.editButton.visibility = Button.VISIBLE

            holder.editButton.setOnClickListener {
                onEditListener?.onEdit(category)
            }
        }
    }

    interface OnEditCategoryListener {
        fun onEdit(category: Category)
    }

}