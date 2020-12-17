package com.airstream.typhoon.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ExtensionInstallReceiver(private val listener: Listener) : BroadcastReceiver() {

    private val filter
        get() = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

    fun register(ctx: Context) = ctx.registerReceiver(this, filter)

    override fun onReceive(p0: Context?, p1: Intent?) {
        when (p1?.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                if (!isReplacing(p1)) {
                    listener.onExtensionInstalled()
                }
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                listener.onExtensionUpdated()
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                if (!isReplacing(p1)) {
                    val packageName = getPackageNameFromIntent(p1)
                    packageName?.let { listener.onExtensionUninstalled(it) }
                }

            }
        }
    }

    private fun isReplacing(intent: Intent) = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

    private fun getPackageNameFromIntent(intent: Intent?) = intent?.data?.encodedSchemeSpecificPart

    interface Listener {
        fun onExtensionInstalled()
        fun onExtensionUpdated()
        fun onExtensionUninstalled(packageName: String)
    }
}