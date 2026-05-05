package com.codergang.chatdirecto.monetization

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback

class InterstitialAdManager(
    private val context: Context,
    private val adUnitId: String,
    private val showEveryN: Int = 3
) {
    private var interstitialAd: AdManagerInterstitialAd? = null
    private var isLoading = false
    private var chatCount = 0

    fun preload() {
        if (isLoading || interstitialAd != null) return
        isLoading = true
        AdManagerInterstitialAd.load(
            context,
            adUnitId,
            AdManagerAdRequest.Builder().build(),
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: AdManagerInterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    fun onChatOpened(activity: Activity, adsEnabled: Boolean) {
        if (!adsEnabled) return
        chatCount++
        if (chatCount % showEveryN != 0) return

        val ad = interstitialAd
        if (ad == null) {
            preload()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                preload()
            }

            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                interstitialAd = null
                preload()
            }
        }
        ad.show(activity)
    }
}
