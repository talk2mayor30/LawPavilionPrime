package git.lawpavilionprime;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.android.gms.gcm.GcmListenerService;

import git.lawpavilionprime.Constants;
import git.lawpavilionprime.R;
import git.lawpavilionprime.contentUpdate.ContentUpdate;


/**
 * Author: LANREWAJU
 * Date Created: Jul 14,2015
 * Time Created: 18:45
 * Project Name: LawPavilion
 */

//

//TODO: uncomment the code and extend: GcmListenerService

public class GCMIntentService extends GcmListenerService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GCMRIntentService";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        //super(CommonUtilities.SENDER_ID);
    }


    @Override
    public void onMessageReceived(String from, Bundle data) {
        //  Bundle extras = intent.getExtras();
        //    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        // String messageType = gcm.getMessageType(intent);

        if (!data.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */

            // This loop represents the service doing some work.
            for (int i = 0; i < 5; i++) {
                Log.d(TAG, "Working... " + (i + 1)
                        + "/5 @ " + SystemClock.elapsedRealtime());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

            }
            Log.d(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
            // Post notification of received message.
            //sendNotification(data.getString(Constants.GCM_EXPIRY));
            sendNotification("Check content");
            Log.d(TAG, "Received: " + data.toString());
            Log.d(TAG, "Received: " + data.getString(Constants.GCM_EXPIRY));
            Log.d(TAG, "Received: " + data.getString(Constants.GCM_HIGHLIGHTS));
            Log.d(TAG, "Received: " + data.getString(Constants.GCM_SECURITY));


        }

    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    /**
     * This method was used when GCMIntentService was extending IntentService
     *
     * @Override protected void onHandleIntent(Intent intent) {
     * Bundle extras = intent.getExtras();
     * GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
     * // The getMessageType() intent parameter must be the intent you received
     * // in your BroadcastReceiver.
     * String messageType = gcm.getMessageType(intent);
     * <p/>
     * if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
     * /*
     * Filter messages based on message type. Since it is likely that GCM
     * will be extended in the future with new message types, just ignore
     * any message types you're not interested in, or that you don't
     * recognize.
     */


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ContentUpdate.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("GCMDemo")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
