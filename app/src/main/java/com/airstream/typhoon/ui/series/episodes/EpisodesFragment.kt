package com.airstream.typhoon.ui.series.episodes

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.EpisodeAdapter
import com.airstream.typhoon.ui.player.PlayerActivity
import com.airstream.typhoon.ui.series.SeriesActivity
import com.airstream.typhoon.ui.series.SeriesViewModel
import com.airstream.typhoon.utils.ItemClickSupport

class EpisodesFragment : Fragment() {

    private val seriesViewModel: SeriesViewModel by activityViewModels()
    private val episodesAdapter = EpisodeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_episodes, container, false)

        val frameLayout: FrameLayout = root.findViewById(R.id.fragment_container)
        val gridView: RecyclerView = root.findViewById(R.id.gridview_episodes)
        gridView.layoutManager = GridLayoutManager(requireActivity(), resources.getInteger(R.integer.gridview_episodes_columns))
        gridView.adapter = episodesAdapter

        ItemClickSupport.addTo(gridView).setOnItemClickListener { _, position, v ->
            val episode = episodesAdapter.getItem(position)

            val intent = Intent(requireActivity(), PlayerActivity::class.java).apply {
                putExtra("episode", episode)
                putExtra("series", seriesViewModel.series.value)
            }
            requireActivity().startActivity(intent)
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            frameLayout.visibility = View.VISIBLE
            childFragmentManager.beginTransaction().add(
                R.id.fragment_container,
                EpisodesControlsFragment(),
                EpisodesControlsFragment.TAG
            ).commit()
        } else {
            frameLayout.visibility = View.GONE
            val episodesControlsFragment = childFragmentManager.findFragmentByTag(EpisodesControlsFragment.TAG)
            if (episodesControlsFragment != null) {
                childFragmentManager.beginTransaction().remove(episodesControlsFragment).commitNow()
            }
        }

        seriesViewModel.episodes.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onCreateView: $it")
            episodesAdapter.clear()
            episodesAdapter.addAll(it, seriesViewModel.sortOrder.value!!)
        })

        seriesViewModel.sortOrder.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onCreateView: $it")
            episodesAdapter.reorderList()
        })

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seriesViewModel.getListings()
    }
    
    companion object {
        private const val TAG = "EpisodesFragment"
    }
}