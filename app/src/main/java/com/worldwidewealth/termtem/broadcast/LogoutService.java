package com.worldwidewealth.termtem.broadcast;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 7/13/17.
 */

public class LogoutService extends Service {
    private Call<ResponseBody> callLogout;
    private NotificationManager mNotifyManager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotifyManager =
                (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApplication.getContext().getApplicationContext());
        mBuilder.setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.title_logout))
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.termtem_logo))
                .setSmallIcon(android.R.drawable.ic_lock_power_off);
        mBuilder.setProgress(0, 0, true);
        startForeground(0x01, mBuilder.build());
//        mNotifyManager.notify(0x01, mBuilder.build());

        serviceLogout();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        if (callLogout != null && callLogout.isExecuted()){
            callLogout.cancel();
        }
        stopForeground(true);
        super.onDestroy();
    }

    private void serviceLogout(){
        if (Global.getInstance().getTXID() == null) return;

        APIServices services = APIServices.retrofit.create(APIServices.class);
        callLogout = services.logout(new RequestModel(APIServices.ACTIONLOGOUT,
                new DataRequestModel()));

        APIHelper.enqueueWithRetry(callLogout, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(null, call, response.body(), this);
                if (values instanceof ResponseModel){
                    ResponseModel responseModel = (ResponseModel) values;
                    if (responseModel.getMsg().equals(APIServices.MSG_SUCCESS)){
//                        Global.getInstance().clearUserData();
                    }

                }
                stopSelf();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                stopSelf();
            }
        });


    }

}
