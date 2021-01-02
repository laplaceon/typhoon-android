package com.airstream.typhoon.extension

import android.graphics.drawable.Drawable
import com.uvnode.typhoon.extensions.Extension

data class ExtensionHolder(
    var extension: Extension? = null,
    var repoId: String? = null,
    var url: String? = null,
    var icon: Drawable? = null,
    var iconUrl: String? = null,
    var isInstalled: Boolean = false,
    var hasUpdate: Boolean = false
)