package news.TamilNewsPaper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import com.kobakei.ratethisapp.RateThisApp;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NationalIndiaNewspaperActivity extends AppCompatActivity {

    Context context = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences prefs = null;
    AdRequest adRequest = new AdRequest.Builder()
            .build();
    InterstitialAd mInterstitialAd;

    SharedPreferences dynamicLinksSharedPreference;

    DatabaseHandler db;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(this);


        dynamicLinksSharedPreference = getPreferences(MODE_PRIVATE);



        dynamic();



        //Admob
        //Admob Popup
        MobileAds.initialize(getApplicationContext(), context.getResources().getString(R.string.admob_app_id));
        AdRequest adRequest = new AdRequest.Builder().build();
//        AdView mAdView = (AdView) findViewById(R.id.adViewMain);
        //mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.admob_popup_id));
        // mInterstitialAd.loadAd(adRequest);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        logAnalytic(mFirebaseAnalytics, "1", "Hindi News Paper Home");


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





    }


    @Override
    protected void onResume() {
        super.onResume();
        dynamic();

    }

    public void openTopNewspaper(View v) {
        Intent intent = new Intent(context, news.TamilNewsPaper.TopNewspaperWebViewActivity.class);
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
        logAnalytic(mFirebaseAnalytics, url, "Hindi Newspaper " + url);

        storeFavourateLinks(newspaperName, url, isLite, actualTag);

    }

    private void openFavourate(String URL, String isLite) {
        String liteURL;
        String lite = "http://googleweblight.com/?lite_url=";
        if (isLite.equals("true")) {
            liteURL = lite + URL;
        } else {
            liteURL = URL;
        }
        Intent intent = new Intent(context, TopNewspaperWebViewActivity.class);
        intent.putExtra("url", liteURL);
        startActivity(intent);
        logAnalytic(mFirebaseAnalytics, URL, "Hindi Newspaper " + URL);

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
                Toast.makeText(NationalIndiaNewspaperActivity.this, "Thanks", Toast.LENGTH_SHORT).show();
                RateThisApp.showRateDialog(NationalIndiaNewspaperActivity.this);

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


    private void requestNewInterstitial() {


        mInterstitialAd.loadAd(adRequest);
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
            btn.getLayoutParams().width=150;
            btn.getLayoutParams().height=100;

            //btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            final String linkURL  = link.getLinkUrl();
            final String isLite  = link.getIsLite();

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    openFavourate(linkURL, isLite);
                }
            });

            ll.addView(btn, 0);

        }


        View best_up_hindi_news_linearlayout  = findViewById(R.id.best_punjabi_news_newspaper);

        if (favourateLinksSqlLiteTables.size() ==  0)
        {
            best_up_hindi_news_linearlayout.setVisibility(View.GONE);
        }
        else
        {
            best_up_hindi_news_linearlayout.setVisibility(View.VISIBLE);
        }


        db.deleteAllExceptTop3();
    }








}

