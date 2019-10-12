package news.TamilNewsPaper ;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdSettings;
//import com.google.android.ads.mediationtestsuite.MediationTestSuite;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import com.kobakei.ratethisapp.RateThisApp;

import org.chromium.customtabsclient.shared.CustomTabsHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener{

    Context context = null;
    FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences prefs = null;
    AdRequest adRequest = new AdRequest.Builder()
            .build();
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    SharedPreferences dynamicLinksSharedPreference;
    private com.facebook.ads.AdView fbBigBannerAdView;
    DatabaseHandler db;
    boolean isAppOnLoadAdDisplayed = false;
    AdView mAdView;

    AdApplication adApplication;
    CustomTabsIntent.Builder intentBuilder;
    Intent exitIntent;
    CustomTabsSession session;
    private String mPackageNameToBind = null;

    int requestCode100 = 100;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adApplication = (AdApplication)getApplicationContext();

        db = new DatabaseHandler(this);

        setContentView(R.layout.tab1);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled (false);
        context = this;

        dynamicLinksSharedPreference = getPreferences(MODE_PRIVATE);

        fbInterstitialAd = new  com.facebook.ads.InterstitialAd(this, context.getResources().getString(R.string.fb_popup_id));
        loadFBInterstitialAd();

        dynamic();

        //Tabs
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Three tab to display in actionbar
        ab.addTab(ab.newTab().setText("News").setTabListener(this));
        ab.addTab(ab.newTab().setText("Jathagam").setTabListener(this));


        //Admob
        //Admob Popup
        //MobileAds.initialize(getApplicationContext(), context.getResources().getString(R.string.admob_app_id));
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = (AdView) findViewById(R.id.adViewMain);

        //mAdView.loadAd(adRequest);


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logAnalytic(mFirebaseAnalytics, "1", "Punjabi News Paper Home");


        //Rating
        // Custom criteria: 3 days and 5 launches
        RateThisApp.Config config = new RateThisApp.Config(15, 25);
        // Custom title ,message and buttons names
        config.setTitle(R.string.rating_title);
        config.setMessage(R.string.rating_message);
        config.setCancelButtonText(R.string.rating_later);
        config.setNoButtonText(R.string.rating_no);
        config.setYesButtonText(R.string.rating_yes);
        RateThisApp.init(config);



        exitIntent = new Intent(getApplicationContext(), ExitActivity.class);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setColor(1);

        } else {
            builder.setSmallIcon(R.drawable.notificatn);

        }




        intentBuilder = new CustomTabsIntent.Builder(session);
        intentBuilder.setToolbarColor(1);
        intentBuilder.setShowTitle(true);
        intentBuilder.enableUrlBarHiding();
        mPackageNameToBind = CustomTabsHelper.getPackageNameToUse(this);

        //Notification Code
        //Notification Code
        FirebaseMessaging.getInstance().subscribeToTopic(context.getResources().getString(R.string.notication_topic));
        if(getIntent().getExtras() != null){
            //do your stuff
            String url = getIntent().getStringExtra("url");
            String isLite = getIntent().getStringExtra("isLite");

            if(isLite == null){
                isLite = "false";
            }

            Intent intent = new Intent(context, TopNewspaperWebViewActivity.class);
            if(url != null && !url.isEmpty()){
                invokeStartWebvieActivity(intent, url, isLite);
            }

        }else{
            //do that you normally do
        }


       // MediationTestSuite.launch(MainActivity.this, getResources().getString(R.string.admob_app_id));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == requestCode100) {
                displayAd();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void displayAd()
    {

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

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(adApplication != null && !adApplication.isAdLoaded()) {
            adApplication.requestNewInterstitial();
        }
        else if(adApplication == null){
            adApplication = (AdApplication)getApplicationContext();
            adApplication.requestNewInterstitial();
        }
        dynamic();
    }

    public void openTopNewspaper(View v) {
        Intent intent = new Intent(context, news.TamilNewsPaper .TopNewspaperWebViewActivity.class);
        invokeStartWebvieActivity(intent, v);
    }

    public void openNational(View v) {
        Intent intent = new Intent(context, NationalIndiaNewspaperActivity.class);
        invokeStartWebvieActivity(intent, v);
    }



    private void invokeStartWebvieActivity(Intent intent, View v) {
        String newspaperName;
        String isLite;
        String lite = "http://googleweblight.com/?lite_url=";
        String actualTag = v.getTag().toString();
        String[] tags = actualTag.split(",");
        String url;
        if (tags[1].trim().equals("true")) {
            url = lite + tags[0];
        } else {
            url = tags[0];
        }

        isLite = tags[1];

        if(tags[2] != null){
            newspaperName = tags[2].trim();
        }
        else {
            newspaperName = "default_small_favourate";
        }

        intent.putExtra("url", url);
        startActivity(intent);
        logAnalytic(mFirebaseAnalytics, url, "Malayalam Magazine " + url);

        storeFavourateLinks(newspaperName, url, isLite, actualTag);

    }

    private void openFavourate(String URL, String isLite) {
        String liteURL;
/*        String lite = "http://googleweblight.com/?lite_url=";
        if (isLite.equals("true")) {
            liteURL = lite + URL;
        } else {
            liteURL = URL;
        }*/
        Intent intent = new Intent(context, TopNewspaperWebViewActivity.class);
        intent.putExtra("url", URL);
        startActivity(intent);
        logAnalytic(mFirebaseAnalytics, URL, "Kannada Newspaper " + URL);

    }

    private void storeFavourateLinks(String name, String url, String isLite, String actualTag){

        Log.d("Insert: ", "Inserting ..");
        db.addContact(new FavourateLinksSqlLiteTable(name, url, isLite, actualTag));


    }

    @Override
    public void onStart() {
        super.onStart();


        // Monitor launch times and interval from installation
        RateThisApp.onStart(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);
        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                Toast.makeText(MainActivity.this, "Thanks", Toast.LENGTH_SHORT).show();
                RateThisApp.showRateDialog(MainActivity.this);

            }

            @Override
            public void onNoClicked() {
                // Toast.makeText(MainActivity.this, "No event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                //Toast.makeText(MainActivity.this, "Cancel event", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void logAnalytic(FirebaseAnalytics  mFirebaseAnalytics, String id, String name){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private void invokeStartWebvieActivity(Intent intent, String url, String isLite) {
        String lite = "http://googleweblight.com/?lite_url=";
        ;
        String openurl;
        if (isLite.equals("true")) {
            openurl = lite + url;
        } else {
            openurl = url;
        }
        intent.putExtra("url", openurl);
        startActivity(intent);

        //finish();
    }





    private void dynamic(){
        GridLayout ll = (GridLayout)findViewById(R.id.best_punjabi_news_newspaper);

        ll.removeAllViews();

        List<FavourateLinksSqlLiteTable> favourateLinksSqlLiteTables = db.getTop3FavourateLinks();
        for (final FavourateLinksSqlLiteTable link : favourateLinksSqlLiteTables) {
            String log = "Id: "+link.getId()+" ,Name: " + link.getLinkName() + " ,URL: " +
                    link.getLinkUrl();
            // Writing Contacts to log
            Log.d("Name: ", log);
            Button btn = new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            btn.setTag(link.getActualTag());

            String imgDrawable = "@drawable/"+link.getLinkName();
            btn.setBackgroundResource(getResources().getIdentifier(imgDrawable, null, getPackageName()));
            btn.getLayoutParams().width=122;
            btn.getLayoutParams().height=100;

            //btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            final String linkURL  = link.getLinkUrl();
            final String isLite  = link.getIsLite();

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    openUrlinChromeTabDirectOnlyLink(linkURL);
                }
            });

            ll.addView(btn, 0);

        }


        View best_punjabi_news_linearlayout  = findViewById(R.id.best_punjabi_news_linearlayout);

        if (favourateLinksSqlLiteTables.size() ==  0)
        {
            best_punjabi_news_linearlayout.setVisibility(View.GONE);
        }
        else
        {
            best_punjabi_news_linearlayout.setVisibility(View.VISIBLE);
        }


        db.deleteAllExceptTop3();
    }


    public void rateus(View v){
        Toast.makeText(MainActivity.this, "Huge Thanks, Please rate 5 Star if you like our APP. Thanks",Toast.LENGTH_LONG).show();
        // RateThisApp.showRateDialog(this);


        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        //Called when a tab is selected
        int nTabSelected = tab.getPosition();
        switch (nTabSelected) {
            case 0:
                setContentView(R.layout.tab1);
                dynamic();
                mAdView = (AdView) findViewById(R.id.adViewMain);
                mAdView.loadAd(adRequest);
                //displayBigFBBanner();
                break;
            case 1:
                setContentView(R.layout.tab2);
                dynamic();
                mAdView = (AdView) findViewById(R.id.adViewMain);
                mAdView.loadAd(adRequest);
                //displayBigFBBanner();
                break;


        }
        admobBannerAdListener();
    }

    public void rateus(MenuItem item) {
        Toast.makeText( MainActivity.this, "Huge Thanks, Please rate 5 Star if you like our APP. Thanks", Toast.LENGTH_LONG ).show();
        // RateThisApp.showRateDialog(this);


        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=" + appPackageName ) ) );
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "https://play.google.com/store/apps/details?id=" + appPackageName ) ) );
        }
    }
    public void rateusDirect(){
        Toast.makeText( MainActivity.this, "Huge Thanks, Please rate 5 Star if you like our APP. Thanks", Toast.LENGTH_LONG ).show();
        // RateThisApp.showRateDialog(this);


        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=" + appPackageName ) ) );
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "https://play.google.com/store/apps/details?id=" + appPackageName ) ) );
        }
    }

    public void admobBannerAdListener(){
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d("MainActivityERR", "Admob Banner Ad not loaded"+ errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    public void displayBigFBBanner(){
        /*fbBigBannerAdView = new com.facebook.ads.AdView(this, context.getResources().getString(R.string.fb_big_banner_id), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.big_banner_container);
        adContainer.addView(fbBigBannerAdView);
        fbBigBannerAdView.loadAd();
        fbBigBannerAdLister();*/
    }

    public void fbBigBannerAdLister(){
        fbBigBannerAdView.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                // Ad error callback
                //Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(), Toast.LENGTH_LONG).show();


                Log.e("Facebook Error: ", adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // Called when a tab unselected.
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        // Called when a tab is selected again.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    public void shareNews() {

        String message = "Android App to read all Newspapers download @  https://play.google.com/store/apps/details?id="+ getApplicationContext().getPackageName();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share with your friends"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rating:
                // User chose the "Settings" item, show the app settings UI...
                RateThisApp.showRateDialog(this);
                return true;
            case R.id.share:
                String message = "Android App for Astrology download @ https://play.google.com/store/apps/details?id=news.TamilNewsPaper ";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(message));
                startActivity(Intent.createChooser(share, "Share with your friends"));
                return true;
            case R.id.share2:
                shareNews();
                return true;
            case R.id.policy:
                openUrlinPlainWebview("http://tntamilnews.com/privacy_policy");
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);


        }
    }



    public void openUrlinChromeTabDirectOnlyLink(String url){
        if(mPackageNameToBind != null) {

            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.intent.setData(Uri.parse(url));
            startActivityForResult(customTabsIntent.intent, requestCode100);
        }
        else{
            openUrlinPlainWebview(url);
        }


    }



    public void openUrlinChromeTabDirect(View v){
        String[] tags = v.getTag().toString().split(",");
        String actualTag = v.getTag().toString();
        String url = tags[0];
        String newspaperName;

        if(mPackageNameToBind != null) {

            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.intent.setData(Uri.parse(url));
            startActivityForResult(customTabsIntent.intent, requestCode100);
        }
        else{
            openTopNewspaper(v);
        }

        if(tags[2] != null){
            newspaperName = tags[2].trim();
        }
        else {
            newspaperName = "default_small_favourate";
        }

        logAnalytic(mFirebaseAnalytics, url, "News " + url);

        storeFavourateLinks(newspaperName, url, "false", actualTag);


    }



    private void openUrlinPlainWebview(String URL) {

        Intent intent = new Intent(context, TopNewspaperWebViewActivity.class);
        intent.putExtra("url", URL);
        startActivity(intent);
        logAnalytic(mFirebaseAnalytics, URL, context.getResources().getString(R.string.app_name) + URL);

    }


    public void loadFBInterstitialAd() {


        fbInterstitialAd.setAdListener(new com.facebook.ads.InterstitialAdListener() {
            @Override
            public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError error) {
                // Ad failed to load
                fbInterstitialAd.destroy();
                //Toast.makeText(TopNewspaperWebViewActivity.this, "Error: " + error.getErrorMessage(),Toast.LENGTH_LONG).show();
                Log.v("Facebook Error ", error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(com.facebook.ads.Ad ad) {
                // Ad is loaded and ready to be displayed
                // You can now display the full screen ad using this code:
               /* if(!isAppOnLoadAdDisplayed) {
                    fbInterstitialAd.show();
                }
                isAppOnLoadAdDisplayed  = true;*/
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

        //AdSettings.addTestDevice("dfc7f9f3-05db-4933-8d56-24d634ed5842");
        fbInterstitialAd.loadAd();


    }

    @Override
    public void onBackPressed()
    {
        if(adApplication.isAdLoaded()) {
            adApplication.mInterstitialAd.show();
        }
        else{
            MainActivity.this.finish();
        }

        adApplication.mInterstitialAd.setAdListener(new AdListener() {

            public void onAdClosed() {
                startActivity(exitIntent);
                Toast.makeText(MainActivity.this, "Thanks for reading. You are Leaving now.", Toast.LENGTH_SHORT).show();;
                finish();
            }

        });


    }

}

