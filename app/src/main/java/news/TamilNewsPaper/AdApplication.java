package news.TamilNewsPaper;

import android.app.Application;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AdApplication extends Application {
    private int lastTabIndex;
    public InterstitialAd mInterstitialAd;
    AdView adView;

   // public InterstitialAd startupInterstitialAd;

    @Override
    public void onCreate() {
        super.onCreate();
        /*int id =  getResources().getIdentifier("c"+(getCurrentMonth()+1), "drawable", getPackageName());
        calendarImageSource = ImageSource.resource(id);*/

        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));
        //CreateAddForAllActivityOnce(getApplicationContext());

        setIntertitialAdUinit();
        requestNewInterstitial();


    }




    public int getLastTabIndex() {
        return this.lastTabIndex;
    }

    public void setLastTabIndex(int lastTabIndex) {
        this.lastTabIndex = lastTabIndex;
    }

    public void setIntertitialAdUinit() {
        this.mInterstitialAd = new InterstitialAd(this);
        this.mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_popup_id));
    }

    /*public void startupCreateWallAd() {
        this.startupInterstitialAd = new InterstitialAd(this);

        this.startupInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_popup_id));
        this.startupInterstitialAd.loadAd(new Builder().build());
    }*/

    public void requestNewInterstitial() {
        this.mInterstitialAd.loadAd(new Builder().build());
    }




    public boolean isAdLoaded() {
        if (this.mInterstitialAd.isLoaded()) {
            return true;
        }
        return false;
    }

    public void displayLoadedAd() {
        mInterstitialAd.show();
    }

    /*public void displayStartupLoadedAd() {
        this.startupInterstitialAd.show();
    }

    public  InterstitialAd getStartupInterstitialAd(){
        return this.startupInterstitialAd;
    }

    public boolean isstartupAdLoaded() {
        if (this.startupInterstitialAd.isLoaded()) {
            return true;
        }
        return false;
    }*/

    public void admobInterstialAdListener(InterstitialAd interstitialAd){
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("StartupAdmob", " "+errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });
    }


}
