package com.apdo.game

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import java.util.Random

class GameActivity : AppCompatActivity() {

    private var mAdView: AdView? = null
    private var rewardedAd: RewardedAd? = null
    private var mySecretNumber = 0
    private var userScore = 0
    
    private val rewardedUnitId = "ca-app-pub-3940256099942544/5224354917"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // توليد رقم عشوائي لبدء اللعبة الأصلية
        generateNewSecretNumber()

        // تهيئة نظام إعلانات جوجل AdMob
        MobileAds.initialize(this) {
            loadBannerAd()
            loadRewardedAd()
        }

        val inputNumber = findViewById<EditText>(R.id.inputNumber)
        val btnCheck = findViewById<Button>(R.id.btnCheck)
        val btnWatchAd = findViewById<Button>(R.id.btn_watch_ad)

        // منطق تخمين الأرقام بلغة كوتلين
        btnCheck.setOnClickListener {
            val valStr = inputNumber.text.toString()
            if (valStr.isNotEmpty()) {
                val userGuess = valStr.toInt()
                if (userGuess == mySecretNumber) {
                    userScore += 10
                    Toast.makeText(this, "تخمين صحيح! رصيدك الآن: $userScore", Toast.LENGTH_LONG).show()
                    generateNewSecretNumber()
                    inputNumber.setText("")
                } else {
                    Toast.makeText(this, "حاول مجدداً! رقمك غير صحيح.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // زر مشاهدة الإعلان بمكافأة لمضاعفة الأرباح
        btnWatchAd.setOnClickListener {
            showRewardVideo()
        }
    }

    private fun generateNewSecretNumber() {
        mySecretNumber = Random().nextInt(10) + 1
    }

    private fun loadBannerAd() {
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView?.loadAd(adRequest)
    }

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, rewardedUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }
        })
    }

    private fun showRewardVideo() {
        rewardedAd?.let { ad ->
            ad.show(this) {
                userScore += 50
                Toast.makeText(this, "تمت مشاهدة الإعلان! رصيدك الإجمالي مكافأة: $userScore", Toast.LENGTH_LONG).show()
                loadRewardedAd()
            }
        } ?: run {
            Toast.makeText(this, "الفيديو يتم تحميله حالياً، انتظر ثوانٍ.", Toast.LENGTH_SHORT).show()
            loadRewardedAd()
        }
    }
}
