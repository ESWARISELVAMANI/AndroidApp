package news.TamilNewsPaper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Context context = null;


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        context = this;


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            sendNotification(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

        //openTopNewspaper("http://dinamalar.com", "true");

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param notificationMap FCM message body received.
     */
    private void sendNotification(Map notificationMap) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", (String)notificationMap.get("url"));
        intent.putExtra("isLite", (String)notificationMap.get("isLite"));
        String appname = context.getResources().getString(R.string.app_name);
        //http://stackoverflow.com/questions/19403348/how-to-pass-variable-from-notificationmanager-to-an-activity-in-android
        //Request Added based on url


        //Image LargeIcon -> http://stackoverflow.com/questions/31979394/how-to-show-the-image-in-push-notification-gcm-android




        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        constructMsg(notificationMap, intent);


        /*NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notificatn)
                .setLargeIcon(bitmap)
                .setTicker(appname)
                .setContentTitle((String)notificationMap.get("title"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText((String)notificationMap.get("messageDetail"));
        bigText.setBigContentTitle((String)notificationMap.get("messageDetail"));
        bigText.setSummaryText((String)notificationMap.get("messageDetail"));
        notificationBuilder.setStyle(bigText);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle((String)notificationMap.get("title"))
                .setLargeIcon(bitmap)
                .setContentText((String)notificationMap.get("message"))
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.notificatn)
                .setStyle(new NotificationCompat.BigTextStyle().bigText((String)notificationMap.get("messageDetail")))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build()); */
    }

    private  void constructMsg(Map notificationMap, Intent intent){
        Bitmap remote_picture = null;
        String largeIconBitmapURL = "";
        String bigImageURL = "";
        Bitmap largeIconBitmap;
        long when = System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100) /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.notificatn);



        //Big Picture
       /* NotificationCompat.BigPictureStyle bigPictureNotification = new NotificationCompat.BigPictureStyle();
        bigPictureNotification.bigPicture(BitmapFactory.decodeResource(
                getResources(), R.drawable.ic_launcher));
        // Applying Big Picture Style to NotificationCompat.Builder Object
        mBuilder.setStyle(bigPictureNotification); */

        largeIconBitmapURL = (String)notificationMap.get("iconURL");
        if(largeIconBitmapURL != null && !largeIconBitmapURL.isEmpty()){
            try{
                largeIconBitmap  = BitmapFactory.decodeStream((InputStream) new URL(largeIconBitmapURL).getContent());
            }
            catch (Exception e){
                largeIconBitmap  = ((BitmapDrawable)drawable).getBitmap();
                e.printStackTrace();
            }

        }
        else{
            largeIconBitmap  = ((BitmapDrawable)drawable).getBitmap();
        }

        bigImageURL = (String)notificationMap.get("bigImageURL");
        NotificationCompat.BigPictureStyle bigPictureStyle = null;
        if(bigImageURL !=null && !bigImageURL.isEmpty()){
            bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setSummaryText((String)notificationMap.get("message"));

            try {
                remote_picture = BitmapFactory.decodeStream((InputStream) new URL(bigImageURL).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            bigPictureStyle.bigPicture(remote_picture);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notificatn)
                .setLargeIcon(largeIconBitmap)
                .setContentTitle(((String)notificationMap.get("title")))
                .setContentText((String)notificationMap.get("message"))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setStyle(bigPictureStyle)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(when);




        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(((String)notificationMap.get("title")));
        inboxStyle.addLine((String)notificationMap.get("message"));
        inboxStyle.addLine((String)notificationMap.get("messageDetail"));
        inboxStyle.addLine((String)notificationMap.get("messageDetailLine3"));
        mBuilder.setStyle(inboxStyle);

        if(bigPictureStyle != null){
            mBuilder.setStyle(bigPictureStyle);
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify((int)when, notification);
    }

    public void openTopNewspaper(String url, String isLite) {
        Intent intent = new Intent(context, MainActivity.class);
        invokeStartWebvieActivity(intent, url, isLite);
    }

    private void invokeStartWebvieActivity(Intent intent, String url, String isLite) {
        String lite = "http://googleweblight.com/?lite_url=";
        String openurl;
        if (isLite.equals("true")) {
            openurl = lite + url;
        } else {
            openurl = url;
        }
        intent.putExtra("url", openurl);
        startActivity(intent);
        logAnalytic(url, "Notification " + url);
        //finish();
    }

    public void logAnalytic(String id, String name){
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }



}
