package com.worldwidewealth.termtem.broadcast;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.SplashScreenWWW;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.TermTemSignIn;
import com.worldwidewealth.termtem.util.Util;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 6/7/17.
 */

public class LocalService extends Service {
    private NotificationManager mNM;
    private static Timer T;
    public static String TAG = LocalService.class.getSimpleName();
    private int count;
    private int countRetry = 0;
    private APIServices services = APIServices.retrofit.create(APIServices.class);


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
        if (Global.getInstance().getAGENTID() != null
                && !(MyApplication.LeavingOrEntering.currentActivity instanceof SplashScreenWWW)) {
            serviceLeave(getApplicationContext(), startId);
        }
        return START_NOT_STICKY;
    }

    private void serviceLeave(final Context context, final int startId){
        countRetry = 0;
        Call<ResponseBody> call = services.service(new RequestModel(APIServices.ACTIONLEAVE, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(context, call, response.body(), this);
                if (values instanceof ResponseModel){
                    ResponseModel model = (ResponseModel) values;
                    count = model.getIdlelimit();
                    countDownLogout(context, startId);
//                                    mHandler.postDelayed(mRunable, model.getIdlelimit()*1000);
                } else if (countRetry < 3){
                    countRetry++;
                    retryService(call, this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (countRetry < 3){
                    countRetry++;
                    retryService(call, this);
                }

                t.printStackTrace();
            }
        });

    }

    private void retryService(final Call call, final Callback callback){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String strRequest = Util.convertToStringRequest(call.request().body());
                if (strRequest != null) {
                    RequestModel requestModel = new Gson().fromJson(strRequest, RequestModel.class);
                    requestModel.setData(new DataRequestModel());
                    Call<ResponseBody> newCall = services.service(requestModel);
                    APIHelper.enqueueWithRetry(newCall, callback);
                }

            }
        }, 3000);

    }

    private void countDownLogout(final Context context, final int startId){
        if (Global.getInstance().getUSERNAME() == null) return;

        T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                        Log.e(TAG, ""+count);
                        count--;
                        if (count == 0){
                            Global.getInstance().clearUserName();
                            serviceLogout(startId);
                            if (T != null) {
                                T.cancel();
                                T.purge();
                            }
                            T = null;
                            this.cancel();
                        }

                        if ((count < 0 && T != null) ||
                                (!MyApplication.canUseLeaving(MyApplication.LeavingOrEntering.currentActivity))){
                            if (T != null) {
                                T.cancel();
                                T.purge();
                            }
                            T = null;
                            this.cancel();
                        }

                    }


        }, 1000, 1000);

    }

    private void serviceLogout(final int statId){
        if (Global.getInstance().getTXID() == null) return;
        countRetry = 0;

        APIServices services = APIServices.retrofit.create(APIServices.class);
        Call<ResponseBody> call = services.logout(new RequestModel(APIServices.ACTIONLOGOUT,
                new DataRequestModel()));

        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(null, call, response.body(), this);
                if (values == null){
                    if (countRetry < 3){
                        countRetry++;
                        retryService(call, this);
                    }
                } else {
                    Global.getInstance().clearUserData();
                    if (MyApplication.LeavingOrEntering.currentActivity != null){
                        MyApplication.LeavingOrEntering.currentActivity.finish();
                    }

                    MyApplication.LeavingOrEntering.currentActivity = null;
                    stopSelf(statId);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (countRetry < 3){
                    countRetry++;
                    retryService(call, this);
                }                    }
        });


    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        Log.e("LocalService", "onDestroy");

        if (T != null) {
            T.cancel();
            T.purge();
            T = null;
        }

/*
        if (Global.getInstance().getProcessSubmit() != null) {
            mNM.cancel(Global.getInstance().getProcessSubmit(), MyApplication.NOTITOPUP);
        }
*/

        // Tell the user we stopped.
//        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {

        Log.e("LocalService", "onTaskRemoved");
        MyApplication.LeavingOrEntering.currentActivity = null;
        mNM.cancelAll();

/*
        if (Global.getInstance().getProcessSubmit() != null) {
            mNM.cancel(Global.getInstance().getProcessSubmit(), MyApplication.NOTITOPUP);
            Global.getInstance().setProcessSubmit(null, null);
        }
*/

//        Util.logoutAPI(null, true);

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