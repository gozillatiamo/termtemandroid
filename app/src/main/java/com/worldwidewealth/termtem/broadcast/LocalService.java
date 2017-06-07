package com.worldwidewealth.termtem.broadcast;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.util.Util;

/**
 * Created by gozillatiamo on 6/7/17.
 */

public class LocalService extends Service {
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
//    private int NOTIFICATION = R.string.local_service_started;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        if (Global.getInstance().getProcessSubmit() != null) {
            mNM.cancel(Global.getInstance().getProcessSubmit(), MyApplication.NOTITOPUP);
        }

        // Tell the user we stopped.
//        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {

        Log.e("LocalService", Global.getInstance().getProcessSubmit()+"");
        if (Global.getInstance().getProcessSubmit() != null) {
            mNM.cancel(Global.getInstance().getProcessSubmit(), MyApplication.NOTITOPUP);
            Global.getInstance().setProcessSubmit(null, null);
        }

        Util.logoutAPI(null, true);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
}