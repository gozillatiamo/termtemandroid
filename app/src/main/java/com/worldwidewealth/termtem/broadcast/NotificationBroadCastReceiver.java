package com.worldwidewealth.termtem.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;

import static com.worldwidewealth.termtem.MyFirebaseMessagingService.BOX;
import static com.worldwidewealth.termtem.MyFirebaseMessagingService.INTENT_FILTER;
import static com.worldwidewealth.termtem.MyFirebaseMessagingService.TEXT;

/**
 * Created by gozillatiamo on 6/10/17.
 */

public class NotificationBroadCastReceiver extends WakefulBroadcastReceiver {

    public static final String TAG = NotificationBroadCastReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "I'm in!!!");

        Bundle dataBundle = intent.getExtras();

        Intent intentBroadcast = new Intent(INTENT_FILTER);
        if (dataBundle.getString(TEXT).equals("Transaction notification")){
            intentBroadcast.putExtra("topup", checkMsgTopup(dataBundle.getString(BOX)));
        }

        MyApplication.getContext().sendBroadcast(intentBroadcast);

        Log.d(TAG, dataBundle.getString(TEXT));
        Log.d(TAG, dataBundle.getString(BOX));
    }

    private boolean checkMsgTopup(String msg){

        if (msg.contains("incomplete")){
            Global.getInstance().setSubmitStatus("Fail");
            return false;
        }

        if (msg.contains("complete")){
            Global.getInstance().setSubmitStatus("Success");
            return true;
        }

        return false;
    }

}