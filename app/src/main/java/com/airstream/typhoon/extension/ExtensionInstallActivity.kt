package com.airstream.typhoon.extension

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.airstream.typhoon.utils.Injector

class ExtensionInstallActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivityForResult(
            Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                setDataAndType(intent.data, intent.type)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
            }, INSTALL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INSTALL_REQUEST_CODE) {
            checkInstallationResult(resultCode)
        }
        finish()
    }

    private fun checkInstallationResult(resultCode: Int) =
        intent.getStringExtra("downloadId")?.let { Injector.getExtensionManager(this).deleteDownload(it) }

    companion object {
        private const val INSTALL_REQUEST_CODE = 300
    }
}