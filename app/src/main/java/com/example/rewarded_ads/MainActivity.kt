package com.example.rewarded_ads

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rewarded_ads.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class MainActivity : AppCompatActivity(), OnUserEarnedRewardListener {

    private lateinit var binding: ActivityMainBinding
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {
            loadAd()
        }

    }

    override fun onUserEarnedReward(rewardItem: RewardItem) {
        Log.d(TAG, "User earned reward.")
    }

    private fun loadAd() {
        RewardedInterstitialAd.load(
            /* context = */ this,
            /* adUnitId = */ "ca-app-pub-3940256099942544/5354046379",
            /* adRequest = */ AdRequest.Builder().build(),
            /* loadCallback = */ object : RewardedInterstitialAdLoadCallback() {

                //If the AD is successfully loaded
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    super.onAdLoaded(ad)
                    Log.d("Waqas Loaded", "AD is loaded ")
                    rewardedInterstitialAd = ad

                    rewardedInterstitialAd?.fullScreenContentCallback = object :
                        FullScreenContentCallback() {

                        override fun onAdClicked() {
                            super.onAdClicked()
                            Log.d(TAG, "Ad was clicked.")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Ad dismissed fullscreen content.")
                            super.onAdDismissedFullScreenContent()
                            rewardedInterstitialAd = null
                            // Start the SecondActivity after the ad is dismissed
                            startSecondActivity()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.")
                            rewardedInterstitialAd = null
                            // Start the SecondActivity even if ad failed to show
                            startSecondActivity()
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(TAG, "Ad recorded an impression.")
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad showed fullscreen content.")
                        }
                    }
                }

                //If we are unable to load the AD
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("Error loading the Ad tag", adError.toString())
                    rewardedInterstitialAd = null
                }
            }
        )
    }



    fun onShowAdButtonClick(view: View) {
        showAd()
    }

    private fun showAd() {
        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd?.show(
                this@MainActivity,
                this@MainActivity
            )
        } else {
            Log.d("Ad not ready", "The ad has not been loaded yet.")
            loadAd() // Load the ad again if it's not ready
        }
    }

    private fun startSecondActivity() {
        showRewardToast()

        // Start the next activity
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
        // Finish the current activity to prevent going back to it when pressing the back button from the NextActivity
        finish()
    }
    private fun showRewardToast() {
        // Display a toast message to notify the user of the reward
        Toast.makeText(this, "Congratulations! You earned a reward!", Toast.LENGTH_SHORT).show()
    }
}
