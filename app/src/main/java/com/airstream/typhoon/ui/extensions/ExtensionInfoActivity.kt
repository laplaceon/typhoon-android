package com.airstream.typhoon.ui.extensions

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import com.airstream.typhoon.R
import com.airstream.typhoon.extension.ExtensionManager
import com.airstream.typhoon.utils.Injector
import com.uvnode.typhoon.extensions.source.Configurable
import com.uvnode.typhoon.extensions.source.MetaSource
import eu.kanade.tachiyomi.data.preference.SharedPreferencesDataStore


class ExtensionInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_extension_info)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Extension Preferences"


        val extensionManager = Injector.getExtensionManager(this)

        val iconView: ImageView = findViewById(R.id.image_icon)
        val nameTextView = findViewById<TextView>(R.id.text_name)
        val versionTextView = findViewById<TextView>(R.id.text_version)

        val extensionId = intent.extras!!.getString("extension")

        val extension = extensionManager.getExtensionWithPackage(extensionId)!!

        iconView.setImageDrawable(extension.icon)
        nameTextView.setText(extension.extension!!.name)
        versionTextView.setText(extension.extension!!.version)

        val uninstallButton: Button = findViewById(R.id.button_uninstall)
        uninstallButton.setOnClickListener {
            extensionManager.uninstall(extension.extension!!.packageName)
            finish()
        }

//        val preferenceFragment = ExtensionPreferencesFragment()
//
//        supportFragmentManager.beginTransaction().add(R.id.extension_prefs_view, preferenceFragment)
//            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    /*class ExtensionPreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
            val screen: PreferenceScreen = preferenceManager.createPreferenceScreen(context)
            for (i in 0 until extension.getExtension().getSources().size()) {
                val source: MetaSource = extension.getExtension().getSources().get(i)
                if (source is Configurable) {
                    try {
                        addPreferencesForSource(
                            screen,
                            source,
                            extension.getExtension().getPackageName()
                        )
                    } catch (e: AbstractMethodError) {
                        e.printStackTrace()
                    }
                }
            }
            preferenceScreen = screen
        }

        private fun addPreferencesForSource(
            screen: PreferenceScreen,
            source: MetaSource,
            extensionPackage: String
        ) {
            val context: Context = screen.context
            val key: String = ExtensionManager.getPreferenceKey(extensionPackage, source.source.id)
            val dataStore: PreferenceDataStore =
                SharedPreferencesDataStore(context.getSharedPreferences(key, MODE_PRIVATE))
            val newScreen: PreferenceScreen = preferenceManager.createPreferenceScreen(context)
            val preferenceCategory = PreferenceCategory(context)
            preferenceCategory.setTitle(source.source.name)
            newScreen.addPreference(preferenceCategory)
            (source as Configurable).addPreferences(newScreen)
            while (newScreen.preferenceCount != 0) {
                val preference: Preference = newScreen.getPreference(0)
                preference.setPreferenceDataStore(dataStore)
                preference.order = Int.MAX_VALUE
                newScreen.removePreference(preference)
                screen.addPreference(preference)
            }
        }
    }*/

}