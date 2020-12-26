package com.airstream.typhoon.ui.series

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.airstream.typhoon.R
import com.squareup.picasso.Picasso

class SeriesDetailsFragment : Fragment() {

    private val seriesViewModel: SeriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_series_details, container, false)

        val poster: ImageView = root.findViewById(R.id.image_poster)
        val description: TextView = root.findViewById(R.id.text_description)
        val completedIndicator: TextView = root.findViewById(R.id.text_completed)

        seriesViewModel.series.observe(viewLifecycleOwner, Observer {
            it?.let {
                Picasso.get().load(it.image).into(poster)
                description.text = it.description

                if (it.isCompleted) {
                    completedIndicator.setText(R.string.series_completed)
                } else {
                    completedIndicator.setText(R.string.series_ongoing)
                }
            }
        })

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seriesViewModel.getSourceInfo()
    }
}