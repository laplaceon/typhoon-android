package com.airstream.typhoon.ui.series

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.airstream.typhoon.R
import com.airstream.typhoon.ui.series.episodes.EpisodesControlsFragment
import com.airstream.typhoon.ui.series.episodes.EpisodesFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.uvnode.typhoon.extensions.model.Series

class SeriesActivity : AppCompatActivity() {

    private val seriesViewModel: SeriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            seriesViewModel.sourceId = intent.extras?.getString("source")
            seriesViewModel.series.value = intent.extras?.getParcelable<Series>("series")
        }

        val viewPager: ViewPager2 = findViewById(R.id.pager)
        val tabs: TabLayout = findViewById(R.id.tabs)

        val pagerAdapter = SeriesPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        val titles = listOf(
            resources.getString(R.string.tab_episodes),
            resources.getString(R.string.tab_details)
        )

        TabLayoutMediator(tabs, viewPager) { tab, pos ->
            tab.text = titles[pos]
        }.attach()

        val seriesTitle: TextView = findViewById(R.id.series_title)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            seriesTitle.text = seriesViewModel.series.value?.title
            seriesTitle.visibility = View.VISIBLE

            // Remove episode controls
            val episodeControlsFragment = supportFragmentManager.findFragmentByTag(EpisodesControlsFragment.TAG + "_" + TAG)
            if (episodeControlsFragment != null) {
                supportFragmentManager.beginTransaction().remove(episodeControlsFragment).commitNow()
            }
        } else {
            supportActionBar!!.title = seriesViewModel.series.value?.title
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.series, menu)
        return super.onCreateOptionsMenu(menu)
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

    private inner class SeriesPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            val fragments = listOf(EpisodesFragment(), SeriesDetailsFragment())
            return fragments[position]
        }

    }

    companion object {
        private const val TAG = "SeriesActivity"
    }
}