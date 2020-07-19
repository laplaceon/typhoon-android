package com.airstream.typhoon.ui.series.episodes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.airstream.typhoon.R
import com.airstream.typhoon.ui.series.SeriesViewModel

class EpisodesControlsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_episode_controls, container, false)

        Log.d(TAG, "onCreateView: ")
        
        return root
    }

    companion object {
        const val TAG = "EpisodeControlsFragment"
    }
}