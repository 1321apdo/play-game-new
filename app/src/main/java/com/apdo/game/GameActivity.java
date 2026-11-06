package com.apdo.game;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.LoadAdError;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private AdView mAdView;
    private RewardedAd rewardedAd;
    private int mySecretNumber;
    private int userScore = 0;
    
    private final String rewardedUnitId = "ca-app-pub-3940256099942544/5224354917"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // توليد رقم عشوائي للعبة الأصلية
        generateNewSecretNumber();

        // تهيئة إعلانات جوجل
        MobileAds.initialize(this, initializationStatus -> {
            loadBannerAd();
            loadRewardedAd();
        });

        EditText inputNumber = findViewById(R.id.inputNumber);
        Button btnCheck = findViewById(R.id.btnCheck);
        Button btnWatchAd = findViewById(R.id.btn_watch_ad);

        // منطق التخمين داخل اللعبة
        btnCheck.setOnClickListener(v -> {
            String val = inputNumber.getText().toString();
            if(!val.isEmpty()) {
                int userGuess = Integer.parseInt(val);
                if(userGuess == mySecretNumber) {
                    userScore += 10;
                    Toast.makeText(this, "تخمين صحيح! رصيدك الآن: " + userScore, Toast.LENGTH_LONG).show();
                    generateNewSecretNumber();
                    inputNumber.setText("");
                } else {
                    Toast.makeText(this, "حاول مجدداً! رقمك غير صحيح.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // تشغيل الإعلان بمكافأة لمضاعفة الأرباح
        btnWatchAd.setOnClickListener(v -> showRewardVideo());
    }

    private void generateNewSecretNumber() {
        mySecretNumber = new Random().nextInt(10) + 1;
    }

    private void loadBannerAd() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, rewardedUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardedAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
            }
        });
    }

    public void showRewardVideo() {
        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem -> {
                userScore += 50;
                Toast.makeText(GameActivity.this, "تمت مشاهدة الإعلان! رصيدك الإجمالي مكافأة: " + userScore, Toast.LENGTH_LONG).show();
                loadRewardedAd();
            });
        } else {
            Toast.makeText(this, "الفيديو يتم تحميله حالياً، انتظر ثانية.", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
        }
    }
}
