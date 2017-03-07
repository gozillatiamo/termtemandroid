package com.worldwidewealth.termtem.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by user on 03-Mar-17.
 */

public class UninstallIntentReceiver extends BroadcastReceiver {
    public static final String TAG = UninstallIntentReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Uninstall package");
    }
}
