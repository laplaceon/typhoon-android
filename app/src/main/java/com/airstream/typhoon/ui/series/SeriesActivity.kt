package com.airstream.typhoon.ui.series

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airstream.typhoon.R
import com.airstream.typhoon.ui.series.episodes.EpisodesControlsFragment
import com.airstream.typhoon.ui.series.episodes.EpisodesFragment
import com.google.android.material.tabs.TabLayout
import com.uvnode.typhoon.extensions.model.Series

class SeriesActivity : AppCompatActivity() {

    private val seriesViewModel: SeriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        
        seriesViewModel.series = intent.extras?.getParcelable<Series>("series")
        seriesViewModel.sourceId = intent.extras?.getString("source")

        val pager: ViewPager = findViewById(R.id.pager)

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(pager)

        val seriesTitle: TextView = findViewById(R.id.series_title)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            seriesTitle.text = seriesViewModel.series?.title
            seriesTitle.visibility = View.VISIBLE

            // Remove episode controls
            val episodeControlsFragment = supportFragmentManager.findFragmentByTag(EpisodesControlsFragment.TAG + "_" + TAG)
            if (episodeControlsFragment != null) {
                supportFragmentManager.beginTransaction().remove(episodeControlsFragment).commitNow()
            }
        } else {
            supportActionBar!!.title = seriesViewModel.series?.title
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            seriesTitle.visibility = View.GONE
            tabs.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            // Add episode controls
            supportFragmentManager.beginTransaction().add(
                R.id.tab_fragment_container,
                EpisodesControlsFragment(),
                EpisodesControlsFragment.TAG + "_" + TAG
            ).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private inner class SeriesPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return EpisodesFragment()
        }

        override fun getCount(): Int {
            return 5
        }

    }

    companion object {
        private const val TAG = "SeriesActivity"
    }
}