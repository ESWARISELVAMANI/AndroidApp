package news.TamilNewsPaper;

/**
 * Created by chittanbm on 9/5/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdSettings;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import news.TamilNewsPaper.R;

public class TopNewspaperWebViewActivity extends Activity {


    private WebView webView;
    private ProgressDialog progress;
    static boolean isActivityRunning = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    Context context = null;
    int duration = Toast.LENGTH_SHORT;
    Toast toast;
    private com.facebook.ads.AdView fbADView;
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    AdApplication adApplication;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.top_newspaper_webview);
        Intent intent = getIntent();
        context = this;



        webView = (WebView) findViewById(R.id.punjabi_NewsPaper_Webview);
        WebSettings webSettings = webView.getSettings();

        //Improve top_newspaper_webview performance
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //improve webView performance
       /* webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true); */

        webView.setWebViewClient(new WebViewClient() {


            public void onPageStarted(WebView view,
                                      String url,
                                      Bitmap favicon) {
                /*editor.putInt("cntClick", 1 + myPrefs.getInt("cntClick",0));
                editor.apply(); */

                news.TamilNewsPaper.DataHolder.setData();

            }

            public void onPageFinished(WebView view, String url) {



            }
        });

        //webView.setWebChromeClient(new WebChromeClient()); //Added Chrome

        webView.getSettings().setLoadsImagesAutomatically(true);
        //webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
       // webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);;


        webSettings.setSaveFormData(true);
        final Activity activity = this;

         /*
        Progress Bar code
         */
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
               if (newProgress > 0 && isActivityRunning) {

                    showProgressDialog("Please Wait");
                }
                if (newProgress >= 95) {
                    hideProgressDialog();
                }

                /*if (newProgress >= 100) {
                    hideProgressDialog();
                }*/

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("TopNewsPaperWebview",  "Error errorCode" + errorCode + description);
            }

            public boolean shouldOverrideUrlLoading(WebView view,String url) {
                return false;
            }

        });




        String url = intent.getStringExtra("url");

        webView.loadUrl(Uri.parse(url).toString());


        adApplication = (AdApplication)getApplicationContext();

        /*if (!adApplication.mInterstitialAd.isLoaded()){
            adApplication.requestNewInterstitial();
        }*/

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        int duration = Toast.LENGTH_LONG;

            toast = Toast.makeText(context, "Fetching News", duration);


        //FB Ads
       // fbADView = new com.facebook.ads.AdView(this, context.getResources().getString(R.string.fb_banner_id), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        fbInterstitialAd = new  com.facebook.ads.InterstitialAd(this, context.getResources().getString(R.string.fb_popup_id_category));

        /*if(!url.contains("youtube")) {
            facebokBannerAds();
        }*/

        //AdSettings.addTestDevice("8189d85ff993a728794cddf341396627");

        loadFBInterstitialAd();


    }


    public void showProgressDialog(final String msg) {
        //toast.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
        //toast.show();



        findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);


    }

    public void hideProgressDialog() {
       /* if(toast != null ){
            toast.cancel();

        }*/
        findViewById(R.id.progressBar1).setVisibility(View.GONE);
    }



   /* @Override
    http://stackoverflow.com/questions/6077141/how-to-go-back-to-previous-page-if-back-button-is-pressed-in-webview
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                        return true;
                    } else {


                       Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                    }
            }

        }
        return super.onKeyDown(keyCode, event);
    } */

    @Override
    public void onStart() {
        super.onStart();
        isActivityRunning = true;

        showProgressDialog("Please Wait");

    }
    @Override
    public void onStop() {
        isActivityRunning = false;



        super.onStop();

        //   hideProgressDialog();
    }


    public void openTopNewspaper(View v) {
        Intent intent = new Intent(context, TopNewspaperWebViewActivity.class);
        invokeStartWebvieActivity(intent, v);
    }

    private void invokeStartWebvieActivity(Intent intent, View v) {
        String lite = "http://googleweblight.com/?lite_url=";
        String[] tags = v.getTag().toString().split(",");
        String url;
        if (tags[1].trim().equals("true")) {
            url = lite + tags[0];
        } else {
            url = tags[0];
        }
        intent.putExtra("url", url);
        startActivity(intent);
        logAnalytic(mFirebaseAnalytics, url, "Punjabi Newspaper "+url);
        //finish();
    }


    public void gotoHome(View v) {

        final Intent intent = new Intent(context, news.TamilNewsPaper.MainActivity.class);

        if (adApplication.mInterstitialAd.isLoaded()) {
            adApplication.mInterstitialAd.show();

            finish();

            adApplication.mInterstitialAd.setAdListener(new AdListener() {
                public void onAdClosed() {
                    startActivity(intent);

                    adApplication.requestNewInterstitial();
                }

            });
        }
        else if (fbInterstitialAd.isAdLoaded()) {
            fbInterstitialAd.show();
            finish();
            fbIntertialCallBackForHome(intent);

        }
        else{
            finish();
            startActivity(intent);

        }




    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (adApplication.mInterstitialAd.isLoaded()) {
                adApplication.mInterstitialAd.show();

                adApplication.mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdClosed() {
                        adApplication.requestNewInterstitial();
                    }

                });
            }
            else if (fbInterstitialAd.isAdLoaded()) {
                fbInterstitialAd.show();
            }

            finish();
            super.onBackPressed();
        }

    }

    public void logAnalytic(FirebaseAnalytics  mFirebaseAnalytics, String id, String name){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }





    public void facebokBannerAds() {
        // Instantiate an AdView view


        // Find the main layout of your activity
        LinearLayout layout = (LinearLayout) findViewById(R.id.kannada_news_paper);

        // Add the ad view to your activity layout
        layout.addView(fbADView);

        // Request to load an ad
        //AdSettings.addTestDevice("8abe89d13bcd1eb03f8452e08742daf3");
        fbADView.loadAd();

        fbADView.setAdListener(new com.facebook.ads.AdListener() {

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError error) {
                // Ad failed to load.
                // Add code to hide the ad's view
                System.out.println("TopNewspaperWebview Error while " + error + " Ad " + ad);
                Log.e("Facebook Error", "Error while "+error.getErrorMessage() +" Placement Id " + ad.getPlacementId());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad was loaded
                // Add code to show the ad's view
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Use this function to detect when an ad was clicked.
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        });
    }

    public void loadFBInterstitialAd() {

        fbInterstitialAd.setAdListener(new com.facebook.ads.InterstitialAdListener() {
            @Override
            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError error) {
                // Ad failed to load
                fbInterstitialAd.destroy();
                //Toast.makeText(TopNewspaperWebViewActivity.this, "Error: " + error.getErrorMessage(),Toast.LENGTH_LONG).show();
                Log.v("interstitialAd", error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(com.facebook.ads.Ad ad) {
                // Ad is loaded and ready to be displayed
                // You can now display the full screen ad using this code:

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Use this function as indication for a user's click on the ad.
                DataHolder.initZeroCNT();
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                DataHolder.initZeroCNT();
                fbInterstitialAd.loadAd();
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }


        });


        fbInterstitialAd.loadAd();


    }


    void fbIntertialCallBackForHome(final Intent intent){
        fbInterstitialAd.setAdListener(new com.facebook.ads.InterstitialAdListener() {
            @Override
            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError error) {
                // Ad failed to load
                fbInterstitialAd.destroy();
                //Toast.makeText(TopNewspaperWebViewActivity.this, "Error: " + error.getErrorMessage(),Toast.LENGTH_LONG).show();
                Log.v("interstitialAd", error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(com.facebook.ads.Ad ad) {
                // Ad is loaded and ready to be displayed
                // You can now display the full screen ad using this code:

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Use this function as indication for a user's click on the ad.
                DataHolder.initZeroCNT();

            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                DataHolder.initZeroCNT();
                startActivity(intent);

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }


        });
    }

    @Override
    protected void onDestroy() {
        if (fbADView != null) {
            fbADView.destroy();
        }
        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
            super.onDestroy();
        }


        super.onDestroy();
    }

    public static Bitmap viewToBitmap(View view, int width, int heaight) {
        Bitmap bitmap = Bitmap.createBitmap(width, heaight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

   /* public void shareWithScreenshoot(View v) {
        Toast.makeText(TopNewspaperWebViewActivity.this, "Hats Off! Be Proud of Spreading News", Toast.LENGTH_LONG).show();
        GridLayout webviewlayout = (GridLayout) findViewById(R.id.up_news_paper);
        Bitmap bitmap = viewToBitmap(webviewlayout, webviewlayout.getWidth(), webviewlayout.getHeight());
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "AppNews.jpg");
        if (Build.VERSION.SDK_INT < 23) {
            Log.v("permistioncheck : ", "Permission is granted");
            try {
                file.createNewFile();
                new FileOutputStream(file).write(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            shareIntent.putExtra("android.intent.extra.STREAM", Uri.parse("file:///sdcard/AppNews.jpg"));
            shareIntent.putExtra("android.intent.extra.TEXT", webView.getUrl()  + "\nDownload News App @ https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            Log.v("permistioncheck : ", "Permission is granted");
            try {
                file.createNewFile();
                new FileOutputStream(file).write(byteArrayOutputStream.toByteArray());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            shareIntent.putExtra("android.intent.extra.STREAM", Uri.parse("file:///sdcard/AppNews.jpg"));
            shareIntent.putExtra("android.intent.extra.TEXT", webView.getUrl()  + "\nDownload News App @ https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        }else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
    }*/

    public void shareNews(View v) {
        Toast.makeText(TopNewspaperWebViewActivity.this, "Hats Off! Be Proud of Spreading News", Toast.LENGTH_LONG).show();

        String urllocal = webView.getUrl();

        String message = webView.getTitle()  + "\nDownload News App @ https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share with your friends"));

    }
    public void shareWithScreenshoot(View v) {
        Toast.makeText(TopNewspaperWebViewActivity.this, "Hats Off! Be Proud of Spreading News", Toast.LENGTH_LONG).show();
        GridLayout webviewlayout = (GridLayout) findViewById(R.id.up_news_paper);
        Bitmap bitmap = viewToBitmap(webviewlayout, webviewlayout.getWidth(), webviewlayout.getHeight());
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("image/jpeg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "AppNews.jpg");
        if (Build.VERSION.SDK_INT < 23) {
            Log.v("permistioncheck : ", "Permission is granted");
            try {
                file.createNewFile();
                new FileOutputStream(file).write(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            shareIntent.putExtra("android.intent.extra.STREAM", Uri.parse("file:///sdcard/AppNews.jpg"));
            shareIntent.putExtra("android.intent.extra.TEXT", webView.getUrl()  + "\nDownload News App @ https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            Log.v("permistioncheck : ", "Permission is granted");
            try {
                file.createNewFile();
                new FileOutputStream(file).write(byteArrayOutputStream.toByteArray());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            shareIntent.putExtra("android.intent.extra.STREAM", Uri.parse("file:///sdcard/AppNews.jpg"));
            shareIntent.putExtra("android.intent.extra.TEXT", webView.getUrl()  + "\nDownload News App @ https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        }else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            }, 1);

        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(webView != null){
            webView.onPause();
            //webView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            //webView.resumeTimers();
        }
    }


}