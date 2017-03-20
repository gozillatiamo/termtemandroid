package com.worldwidewealth.termtem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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
    public static final String TEXT = "txt";
    public static final String BOX = "box";
    public static final String MSGID = "msgid";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
/*
        String txt = remoteMessage.getData().get("txt");
        String box = remoteMessage.getData().get("box");
*/
//        String click_action = remoteMessage.getNotification().getClickAction();
        if (remoteMessage.getData() != null) {
            Bundle bundle = new Bundle();

            bundle.putString(TEXT, remoteMessage.getData().get(TEXT));
            bundle.putString(BOX, remoteMessage.getData().get(BOX));
            bundle.putString(MSGID, remoteMessage.getData().get(MSGID));

            sendNotification(bundle);

            Log.e(TAG, remoteMessage.getData().toString());
/*
            if (box.contains("*")) {
                Global.setOTP(box.split("\\*")[0]);
            } else {
                sendNotification(txt, box, click_action);
            }
*/
        }
    }


    private void sendNotification(Bundle bundle) {

        Intent intent = new Intent(this, ActivityShowNotify.class);
        intent.putExtras(bundle);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), iUniqueId, intent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.termtem_logo)
                .setContentTitle(bundle.getString(TEXT))
                .setContentText(bundle.getString(BOX))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
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
