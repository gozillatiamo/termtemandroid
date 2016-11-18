package com.worldwidewealth.wealthcounter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by MyNet on 11/10/2559.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.e("notiData", remoteMessage.getData().toString());
        String txt = remoteMessage.getData().get("txt");
        String box = remoteMessage.getData().get("box");
        if (box.contains("*")){
            setOTP(box.split("\\*")[0]);
        }
        sendNotification(txt, box);
    }

    private void setOTP(String otp){
        Log.e("OTP", otp);
        String newEncryption = EncryptionData.EncryptData(
                EncryptionData.DecryptData(otp, null),
                EncryptionData.OTP
        );
        Global.setOTP(newEncryption);

    }

    private void sendNotification(String txt, String box) {
        Intent intent = null;

        if (box != null && box.contains("Get")){
           intent = new Intent(this, ActivityLuck.class);
        } else  intent = new Intent(this, ActivityPin.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_none_text)
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
