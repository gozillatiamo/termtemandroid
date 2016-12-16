package com.worldwidewealth.wealthwallet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by MyNet on 11/10/2559.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Message";
    public static final String TEXT = "text";
    public static final String BOX = "box";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.e("notiData", remoteMessage.getData().toString());
        String txt = remoteMessage.getData().get("txt");
        String box = remoteMessage.getData().get("box");

        if (box != null) {
            if (box.contains("*")) {
                Global.setOTP(box.split("\\*")[0]);
            } else {
                sendNotification(txt, box);
            }
        }
    }


    private void sendNotification(String txt, String box) {


        Intent intent = new Intent(this, ActivityShowNotify.class);
        intent.putExtra(TEXT, txt);
        intent.putExtra(BOX, box);
        Log.e("notiBox", box);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), iUniqueId, intent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_wc_without_text)
                .setContentTitle(txt)
                .setContentText(box)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m /* ID of notification */, notificationBuilder.build());
    }
}
