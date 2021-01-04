package com.airstream.typhoon.analytics

import android.app.Activity
import android.util.Log
import com.adcolony.sdk.*
import com.airstream.typhoon.BuildConfig
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance


class AdsManager {
    var adPlayed = false

    var adColonyAd: AdColonyInterstitial? = null

    init {
        val adColonyInterstitialListener: AdColonyInterstitialListener =
            object : AdColonyInterstitialListener() {
                override fun onRequestFilled(adColonyInterstitial: AdColonyInterstitial) {
                    adColonyAd = adColonyInterstitial
                    Log.d(TAG, "Filled by Adcolony")
                }

                override fun onRequestNotFilled(zone: AdColonyZone) {
                    super.onRequestNotFilled(zone)
                    Log.d(TAG, "Not filled by Adcolony")
                }

                override fun onOpened(ad: AdColonyInterstitial) {
                    super.onOpened(ad)
                    adPlayed = true
                }
            }

        if (adColonyAd == null || adColonyAd?.isExpired == true) {
            AdColony.requestInterstitial(ADCOLONY_ZONE_ID, adColonyInterstitialListener)
        }

    }

    fun tryPlayAd(ctx: Activity, onFinishedCallback: OnFinishedCallback) {
        if (!adPlayed && adColonyAd != null) {
            adColonyAd!!.show()
            onFinishedCallback.onFinish()
        }
    }

    interface OnFinishedCallback {
        fun onFinish()
    }

    companion object {
        private const val TAG = "AdsManager"

        private const val showAds = true

        private const val ADCOLONY_APP_ID = "appb610ab14ec7c4c79b1f919"
        private const val ADCOLONY_ZONE_ID = "vze92996c3b40d40dba3a6e5"

        private const val FLURRY_KEY = "FTBGV9T6H7GCNY2S53QZ"

        fun configure(ctx: Activity) {
            val GDPR_ALLOWED = "1"

            val appOptions = AdColonyAppOptions()
                .setAppVersion(BuildConfig.VERSION_NAME)
                .setPrivacyFrameworkRequired(AdColonyAppOptions.GDPR, true)
                .setPrivacyConsentString(AdColonyAppOptions.GDPR, GDPR_ALLOWED)

            val configureResult = AdColony.configure(
                ctx.application,
                appOptions,
                ADCOLONY_APP_ID,
                ADCOLONY_ZONE_ID
            )

            Log.d(TAG, "configure: $configureResult")

            FlurryAgent.Builder()
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(ctx, FLURRY_KEY)
        }
    }
}