package news.TamilNewsPaper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import news.TamilNewsPaper.MainActivity;
import news.TamilNewsPaper.R;


import com.facebook.ads.Ad;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 4600;
    InterstitialAd mInterstitialAd;
    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);



        final AdApplication adApplication = (AdApplication)getApplicationContext();

        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (adApplication.isAdLoaded()) {

                    adApplication.displayLoadedAd();

                    SplashActivity.this.finish();

                    adApplication.mInterstitialAd.setAdListener(new AdListener() {
                        public void onAdClosed() {
                            startActivity(intent);
                            //adApplication.createWallAd();
                            adApplication.requestNewInterstitial();

                        }
                    });
                }
                else{
                    startActivity(intent);
                    finish();

                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }






   @Override
    protected void onDestroy() {


        super.onDestroy();
    }

}
