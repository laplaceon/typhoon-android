package com.airstream.typhoon.extension

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airstream.typhoon.utils.Injector
import com.fasterxml.jackson.databind.ObjectMapper
import com.uvnode.typhoon.extensions.Extension
import com.uvnode.typhoon.extensions.api.LoadExtension
import dalvik.system.PathClassLoader
import net.swiftzer.semver.SemVer
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExtensionManager private constructor(private val ctx: Context) {

    private val _installedExtensions: MutableLiveData<List<ExtensionHolder>> by lazy {
        MutableLiveData<List<ExtensionHolder>>()
    }

    private val installedExtensions: LiveData<List<ExtensionHolder>> = _installedExtensions
    private val availableExtensions: MutableList<ExtensionHolder> = mutableListOf()
    private val packageMap: MutableMap<String, Int> = mutableMapOf()
    private val downloads: MutableMap<String, File> = mutableMapOf()
    private val extensionInstallReceiver = ExtensionInstallReceiver(InstallListener())

    private val networkHelper = Injector.getNetworkHelper(ctx)

    init {
        getAvailableExtensionsFromRepo()
        loadExtensions()
        extensionInstallReceiver.register(ctx)
    }

    private fun loadExtensions() {
        val packageManager = ctx.packageManager

        val installedPackages = packageManager.getInstalledPackages(PACKAGE_FLAGS)

        val installedExtensions = mutableListOf<ExtensionHolder>()

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
                    packageMap[extension.packageName] = installedExtensions.size
                    installedExtensions.add(extensionHolder)
                }
            }
        }

        this._installedExtensions.value = installedExtensions
    }

    private fun isValidExtension(extensionHolder: ExtensionHolder) = SemVer.parse(extensionHolder.extension!!.apiVersion) >= MINIMUM_SUPPORTED_EXTENSION_API

    private fun isExtensionPackage(pkgInfo: PackageInfo) = pkgInfo.reqFeatures.orEmpty().any { it.name == EXTENSION_FEATURE }

    private fun getAvailableExtensionsFromRepo() {
        val request = Request.Builder().url(MAIN_REPO + "repo.json").get().build()

        networkHelper.okClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
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
                            val installedExtensionHolder = installedExtensions.value!![packageMap[packageName]!!]
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

    fun downloadAndInstall(apkUrl: String, packageName: String) {
        if (apkUrl.isNotBlank()) {
            val request = Request.Builder().url(apkUrl).get().build()

            val fileName = request.url.pathSegments.last()

            networkHelper.okClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val downloadPath = ctx.filesDir.absolutePath
                        val apkFile = File("$downloadPath/$fileName")
                        apkFile.createNewFile()

                        val fileOutputStream = FileOutputStream(apkFile)
                        fileOutputStream.write(response.body?.bytes())
                        fileOutputStream.close()
                        install(apkFile, packageName)
                    }
                }

            })
        }
    }

    fun install(apkFile: File, packageName: String) {
        downloads[packageName] = apkFile.absoluteFile

        val apkUri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", apkFile)
        val intent = Intent(ctx, ExtensionInstallActivity::class.java).apply {
            setDataAndType(apkUri, APK_MINE)
            putExtra("downloadId", packageName)
        }
        ctx.startActivity(intent)
    }

    fun uninstall(packageName: String) {
        val packageInstaller = ctx.packageManager.packageInstaller

        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.parse("package:$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        ctx.startActivity(intent)
    }

    fun deleteDownload(packageName: String) {
        downloads[packageName]?.delete()
        downloads.remove(packageName)
    }

    inner class InstallListener : ExtensionInstallReceiver.Listener {
        override fun onExtensionInstalled() {
            loadExtensions()
        }

        override fun onExtensionUpdated() {
            loadExtensions()
        }

        override fun onExtensionUninstalled(packageName: String) {
            packageMap.remove(packageName)
            loadExtensions()
        }

    }

    companion object {
        private const val EXTENSION_FEATURE = "typhoon.extension";
        private const val EXTENSION_ENTRY = "extension.class";
        private const val APK_MINE = "application/vnd.android.package-archive"
        private const val MAIN_REPO = "https://raw.githubusercontent.com/uvnode/typhoon-main-extensions/repo/"
        private val MINIMUM_SUPPORTED_EXTENSION_API = SemVer.parse("1.0.0");

        private const val PACKAGE_FLAGS = PackageManager.GET_CONFIGURATIONS or PackageManager.GET_SIGNATURES

        private const val TAG = "ExtensionManager"

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