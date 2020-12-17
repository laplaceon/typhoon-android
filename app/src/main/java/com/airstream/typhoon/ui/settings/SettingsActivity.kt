package com.airstream.typhoon.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceFragmentCompat
import com.airstream.typhoon.BuildConfig
import com.airstream.typhoon.R
import com.webianks.easy_feedback.EasyFeedback

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.action_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, PrefsFragment())
            .commit()
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

    class PrefsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            val versionPreference: Preference? = preferenceScreen.findPreference("version")
            versionPreference?.summary = BuildConfig.VERSION_NAME

            val openSourcePreference: Preference? = preferenceScreen.findPreference("open_source")
            openSourcePreference?.setOnPreferenceClickListener {
                val intent =Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("http://typhoonapp.io/typhoon-android-open-source-licenses")
                }

                startActivity(intent)

                true
            }

            val feedbackPreference: Preference? = preferenceScreen.findPreference("feedback")
            feedbackPreference?.setOnPreferenceClickListener {
                EasyFeedback.Builder(requireActivity())
                    .withEmail("typhoonapp@protonmail.com")
                    .withSystemInfo()
                    .build()
                    .start()

                true
            }
        }

    }
}