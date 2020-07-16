package com.airstream.typhoon.extension

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.airstream.typhoon.utils.Injector
import com.fasterxml.jackson.databind.ObjectMapper
import com.uvnode.typhoon.extensions.Extension
import com.uvnode.typhoon.extensions.api.LoadExtension
import dalvik.system.PathClassLoader
import net.swiftzer.semver.SemVer
import okhttp3.*
import java.io.File
import java.io.IOException

class ExtensionManager private constructor(private val ctx: Context) {

    private var installedExtensions: MutableList<ExtensionHolder> = mutableListOf()
    private var availableExtensions: MutableList<ExtensionHolder> = mutableListOf()
    private var packageMap: MutableMap<String, Int> = mutableMapOf()
    private var downloads: Map<String, File> = emptyMap()

    init {
        getAvailableExtensionsFromRepo()
        loadExtensions()
    }

    private fun loadExtensions() {
        val packageManager = ctx.packageManager
        val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_CONFIGURATIONS)

        installedPackages.filter {
            isExtensionPackage(it)
        }.forEach {
            val appInfo = packageManager.getApplicationInfo(it.packageName, PackageManager.GET_META_DATA)
            val icon = packageManager.getApplicationIcon(it.packageName)
            val extensionClass = it.packageName + appInfo.metaData.getString(EXTENSION_ENTRY)

            val pathClassLoader = PathClassLoader(appInfo.sourceDir, ctx.classLoader)
            val obj = Class.forName(extensionClass, false, pathClassLoader).newInstance()

            if (obj is LoadExtension) {
                val extension = obj.extension
                extension.packageName = it.packageName
                extension.version = it.versionName
                extension.versionCode = it.versionCode

                val extensionHolder = ExtensionHolder()
                extensionHolder.extension = extension
                extensionHolder.isInstalled = true
                extensionHolder.icon = icon

                if (isValidExtension(extensionHolder)) {
                    packageMap.put(extension.packageName, installedExtensions.size)
                    installedExtensions.add(extensionHolder)
                }
            }
        }
    }

    private fun isValidExtension(extensionHolder: ExtensionHolder) =
        SemVer.parse(extensionHolder.extension!!.apiVersion).compareTo(
            MINIMUM_SUPPORTED_EXTENSION_API
        ) >= 0

    private fun isExtensionPackage(pkgInfo: PackageInfo) = pkgInfo.reqFeatures.orEmpty().any { it.name == EXTENSION_FEATURE }

    private fun getAvailableExtensionsFromRepo() {
        val networkHelper = Injector.getNetworkHelper(ctx)

        val request = Request.Builder().url(MAIN_REPO + "repo.json").get().build()

        networkHelper.okClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val objectMapper = ObjectMapper()

                    val root = objectMapper.readTree(response.body?.bytes())
                    val repoId = root.get("id").asText()
                    val extensions = root.get("extensions")

                    for (extensionNode in extensions) {
                        val extension = Extension()
                        extension.name = extensionNode.get("name").asText()
                        val version = extensionNode.get("version").asText()
                        val versionCode = extensionNode.get("version_code").asInt()
                        val packageName = extensionNode.get("package").asText()
                        val url = extensionNode.get("url").asText()

                        extension.version = version
                        extension.packageName = packageName

                        val extensionHolder = ExtensionHolder()
                        extensionHolder.extension = extension
                        extensionHolder.repoId = repoId
                        extensionHolder.isInstalled = false
                        extensionHolder.url = url
                        extensionHolder.iconUrl = extensionNode.get("icon").asText()

                        if(packageMap.contains(packageName)) {
                            var installedExtensionHolder = installedExtensions.get(packageMap.get(packageName)!!)
                            if (versionCode > installedExtensionHolder.extension!!.versionCode) {
                                installedExtensionHolder.hasUpdate = true
                                installedExtensionHolder.url = url
                            }
                        }

                        availableExtensions.add(extensionHolder)
                    }
                }
            }

        })
    }

    fun getInstalledExtensions() = installedExtensions

    fun getInstallableExtensions() =
        availableExtensions.filter {
            it.isInstalled or it.hasUpdate
        }


    companion object {
        private const val EXTENSION_FEATURE = "typhoon.extension";
        private const val EXTENSION_ENTRY = "extension.class";
        private const val APK_MINE = "application/vnd.android.package-archive"
        private const val MAIN_REPO = "https://raw.githubusercontent.com/uvnode/typhoon-main-extensions/repo/"
        private val MINIMUM_SUPPORTED_EXTENSION_API = SemVer.parse("1.0.0");

        private var instance: ExtensionManager? = null;

        fun getInstance(ctx: Context) =
            instance
                ?: synchronized(this) {
                instance
                    ?: ExtensionManager(ctx)
                        .also { instance = it }
            }
    }

}