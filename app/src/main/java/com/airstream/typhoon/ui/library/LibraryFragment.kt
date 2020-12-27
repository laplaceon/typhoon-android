package com.airstream.typhoon.ui.library

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airstream.typhoon.R
import com.airstream.typhoon.data.library.entities.Category
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_library, container, false)

        val toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        val navSpinner: Spinner = requireActivity().findViewById(R.id.nav_spinner)

        toolbar?.setDisplayShowTitleEnabled(true)
        navSpinner.visibility = View.GONE

        setHasOptionsMenu(true)

        val chipGroup: ChipGroup = root.findViewById(R.id.library_category_chips)

        libraryViewModel.categories.observe(viewLifecycleOwner, {
            chipGroup.removeAllViews()

            for (i in it.indices) {
                val chip = Chip(requireActivity())
                chip.id = ViewCompat.generateViewId()
                chip.text = it[i].name
                chip.tag = i
                chip.isCheckable = true

                chipGroup.addView(chip)
            }

            if (chipGroup.childCount > 0) {
                (chipGroup.getChildAt(libraryViewModel.selectedCategory.value!!) as Chip).isChecked = true
            }
        })

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val chip: Chip = chipGroup.findViewById(checkedId)
            libraryViewModel.setCategory(chip.tag as Int)
        }

        libraryViewModel.selectedCategory.observe(viewLifecycleOwner, {
            libraryViewModel.categories.value?.let { l ->
                val category = l[it]
                CoroutineScope(Dispatchers.Default).launch {
                    val series = libraryViewModel.seriesDao.getAll(category.id)
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "onCreateView: $series")
                    }
                }
            }
        })

        val textView: TextView = root.findViewById(R.id.text_library)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.library, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_managelibrary -> {
                CoroutineScope(Dispatchers.Default).launch {
                    libraryViewModel.categoryDao.insertAll(Category("September"))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "LibraryFragment"
    }
}