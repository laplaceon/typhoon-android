package com.airstream.typhoon.ui.series.episodes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.airstream.typhoon.R
import com.airstream.typhoon.ui.series.SeriesViewModel
import kotlinx.android.synthetic.main.app_bar_main.*
import org.w3c.dom.Text

class EpisodesControlsFragment : Fragment() {

    private val seriesViewModel: SeriesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_episode_controls, container, false)

        val listingsSpinner: Spinner = root.findViewById(R.id.spinner_listings)
        val listingIndicatorText: TextView = root.findViewById(R.id.textview_listing_indicator)
        val sortToggleButton: ImageButton = root.findViewById(R.id.togglebutton_episodes_ordering)

        listingsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        listingsSpinner.visibility = View.GONE
        listingIndicatorText.visibility = View.VISIBLE
        listingIndicatorText.text = resources.getString(R.string.listings_none)

        sortToggleButton.setOnClickListener {
            seriesViewModel.toggleOrder()
        }

        seriesViewModel.episodes.observe(viewLifecycleOwner, Observer {
            if (it.size > 1) {
                listingsSpinner.visibility = View.VISIBLE
                listingIndicatorText.visibility = View.GONE

                val listingsAdapter = ArrayAdapter<CharSequence>(requireActivity(), android.R.layout.simple_spinner_item)
                listingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                listingsSpinner.adapter = listingsAdapter
            } else {
                listingsSpinner.visibility = View.GONE
                listingIndicatorText.visibility = View.VISIBLE
                listingIndicatorText.text = resources.getString(R.string.listings_none)
            }
        })

        seriesViewModel.sortOrder.observe(viewLifecycleOwner, Observer {
            if (it) {
                sortToggleButton.setImageResource(R.drawable.ic_sort_ascending)
            } else {
                sortToggleButton.setImageResource(R.drawable.ic_sort_descending)
            }
        })

        return root
    }

    companion object {
        const val TAG = "EpisodeControlsFragment"
    }
}