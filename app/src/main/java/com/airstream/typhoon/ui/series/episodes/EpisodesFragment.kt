package com.airstream.typhoon.ui.series.episodes

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.ui.series.SeriesViewModel

class EpisodesFragment : Fragment() {

    private val seriesViewModel: SeriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_episodes, container, false)

        val frameLayout: FrameLayout = root.findViewById(R.id.fragment_container)
        val gridView: RecyclerView = root.findViewById(R.id.gridview_episodes)
        gridView.layoutManager = GridLayoutManager(requireActivity(), resources.getInteger(R.integer.gridview_episodes_columns))

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

        return root
    }
}