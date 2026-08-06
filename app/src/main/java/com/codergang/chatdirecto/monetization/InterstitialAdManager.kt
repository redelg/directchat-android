package com.codergang.chatdirecto.monetization

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(
    private val context: Context,
    private val adUnitId: String,
    private val showEveryN: Int = 3
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var chatCount = 0

    fun preload() {
        if (isLoading || interstitialAd != null) return
        isLoading = true
        InterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
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
