package com.worldwidewealth.termtem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.model.FileListNotifyResponseModel;
import com.worldwidewealth.termtem.widgets.InformationView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by MyNet on 11/10/2559.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Message";
    public static final String TEXT = "txt";
    public static final String BOX = "box";
    public static final String MSGID = "msgid";
    public static final String FILELIST = "filelist";
    public static final String TYPE = "_type";

    public static final String INTENT_FILTER = "INTENT_FILTER";

    private String urlImagePreview;
    private int mType = 0;

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

/*
            Intent intent = new Intent(INTENT_FILTER);
            if (remoteMessage.getData().get(TEXT).equals("Transaction notification")){
                intent.putExtra("topup", checkMsgTopup(remoteMessage.getData().get(BOX)));
            }
            sendBroadcast(intent);
*/

            if (remoteMessage.getData().containsKey(TYPE)){
                mType = Integer.parseInt(remoteMessage.getData().get(TYPE));
                bundle.putInt(TYPE, mType);
            }

            switch (InformationView.TYPE.getTypeAt(mType)){
                case IMAGE:
                    if (remoteMessage.getData().containsKey(FILELIST)){
                        ArrayList<FileListNotifyResponseModel> list = new Gson()
                                .fromJson(remoteMessage.getData().get(FILELIST),
                                        new TypeToken<ArrayList<FileListNotifyResponseModel>>() {
                                        }.getType());

                        bundle.putParcelableArrayList(FILELIST, list);
                        urlImagePreview  = list.get(0).getUrl();
                    }
                    break;
            }

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

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), iUniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_2);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(bundle.getString(TEXT))
                .setSmallIcon(R.drawable.ic_logo_termtem_small)
                .setContentText(bundle.getString(BOX))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

            switch (InformationView.TYPE.getTypeAt(mType)){
                case TEXT:
                    notificationBuilder.setLargeIcon(largeIcon);

                    break;
                case IMAGE:
                    Bitmap bitmapPreview = getBitmapfromUrl(urlImagePreview);
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmapPreview)
                            .bigLargeIcon(largeIcon)
                            .setSummaryText(bundle.getString(BOX)));
                    notificationBuilder.setLargeIcon(bitmapPreview);
                    break;
            }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m /* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
