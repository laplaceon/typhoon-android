package com.airstream.typhoon.ui.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.SeriesAdapter
import com.airstream.typhoon.data.library.entities.Category
import com.airstream.typhoon.ui.series.SeriesActivity
import com.airstream.typhoon.utils.ItemClickSupport
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.uvnode.typhoon.extensions.model.Series
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModels()
    private var seriesAdapter: SeriesAdapter = SeriesAdapter()

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

        val gridView: RecyclerView = root.findViewById(R.id.gridview_library)
        gridView.layoutManager = GridLayoutManager(requireActivity(), resources.getInteger(R.integer.gridview_series_columns))
        gridView.setHasFixedSize(true)
        gridView.adapter = seriesAdapter

        ItemClickSupport.addTo(gridView).setOnItemClickListener { _, position, _ ->
            val series = seriesAdapter.getItem(position)

            val intent = Intent(requireActivity(), SeriesActivity::class.java).apply {
                putExtra("series", series)
                putExtra("source", series.source)
            }
            requireActivity().startActivity(intent)
        }

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
                        val mappedSeries = series.map {
                            val s = Series()
                            s.source = it.sourceId
                            s.id = it.seriesId
                            s.image = it.image
                            s.title = it.title
                            s.uri = it.uri

                            s
                        }

                        Log.d(TAG, "onCreateView: $mappedSeries")

                        seriesAdapter.clear()
                        seriesAdapter.addAll(mappedSeries)

                        gridView.adapter = seriesAdapter
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
                val libraryManagerFragment = LibraryManagerFragment()

                val bundle = Bundle()
                bundle.putInt("mode", 0)

                libraryManagerFragment.arguments = bundle
                libraryManagerFragment.show(childFragmentManager, "Manage Library")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "LibraryFragment"
    }
}