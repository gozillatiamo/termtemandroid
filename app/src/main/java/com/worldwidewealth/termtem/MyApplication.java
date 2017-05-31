package com.worldwidewealth.termtem;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.squareup.otto.Bus;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.chat.ChatBotActivity;
import com.worldwidewealth.termtem.chat.PhotoViewActivity;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.DialogNetworkError;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.TermTemSignIn;
import com.worldwidewealth.termtem.util.Util;

import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by MyNet on 17/11/2559.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Context mContext;
    private static Bus mBus;
    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;
    public static boolean clickable = true;
    public static final int NOTIUPLOAD = 1;
    public static final int NOTITOPUP = 2;
    private static boolean isUpload = false;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_CHOOSE = 2;


    protected String userAgent;

    /*
    private static Handler  mHandler = new Handler();
    private static Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            Util.logoutAPI(null, true);
        }
    };
*/
    private static Thread mThread;

    public static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(this);
        mContext = getApplicationContext();
        userAgent = com.google.android.exoplayer2.util.Util.getUserAgent(this, getString(R.string.app_name));
        mBus = new Bus();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Mitr-Regular.ttf")
                .addCustomStyle(AppCompatTextView.class, android.R.attr.textViewStyle)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        try {
            PackageInfo packageInfo = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            if (Global.getInstance().getVERSIONCODE() != versionCode){
                if (Global.getInstance().getAGENTID() != null){
                    Util.logoutAPI(mContext, true);
                }

//                Util.deleteCache(this);
                Global.getInstance().clearAll();
                Global.getInstance().setVERSIONCODE(versionCode);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Context getContext(){
        return mContext;
    }

    public static Bus getBus() {
        return mBus;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (canUseLeaving(activity)){
            LeavingOrEntering.activityResumed(activity);
        } else {
            LeavingOrEntering.currentActivity = null;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
//        DialogCounterAlert.DialogProgress.dismiss();
        DialogNetworkError.dismiss();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (canUseLeaving(activity)){
            LeavingOrEntering.activityStopped(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        DialogCounterAlert.DialogProgress.dismiss();
    }

    public boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }



    public static class LeavingOrEntering
    {
        public static Activity currentActivity = null;
        public static int count;
        public static Timer T;

        public static void activityResumed( Activity activity )
        {
            String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

            try {
                if (mThread != null || mThread.isAlive()) {
//                    mHandler.removeCallbacks(mRunable);
                    mThread.interrupt();
                    mThread = null;

                    if (T != null) {
                        T.cancel();
                        T = null;
                    }
                }
            } catch (NullPointerException e){
                e.printStackTrace();
                mThread = null;
                T = null;
            }

            if (Global.getInstance().getUSERNAME() == null) {
                Util.backToSignIn(activity);
                return;
            }


            if (strCurrentAtivity == null) {
                currentActivity = activity;
                return;
            }
            if (strCurrentAtivity.equals(activity.getLocalClassName())) {

                if (Global.getInstance().getAGENTID() == null){
                    new TermTemSignIn(activity, TermTemSignIn.TYPE.RELOGIN,
                            new DialogCounterAlert.DialogProgress(activity).show()).getTXIDfromServer();
                } else {
                    Util.logoutAPI(activity, false);
                }
            }
            currentActivity = activity;


        }


        public static void activityStopped(final Activity activity ) {
            String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

            if (currentActivity == null) return;

            if ( strCurrentAtivity.equals(activity.getLocalClassName()) ) {
                // We were stopped and no-one else has been started.
//                Util.logoutAPI(false);
                if(mThread != null && mThread.isAlive()){
                    mThread.interrupt();
                }

                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int times = 0;
                        while (Global.getInstance().getAGENTID() == null && times < 3){
                            try {
                                Thread.sleep(1000);
                                times++;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                        if (Global.getInstance().getAGENTID() == null) return;
                        APIServices services = APIServices.retrofit.create(APIServices.class);
                        Call<ResponseBody> call = services.service(new RequestModel(APIServices.ACTIONLEAVE, new DataRequestModel()));
                        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Object values = EncryptionData.getModel(activity, call, response.body(), this);
                                if (values instanceof ResponseModel){
                                    ResponseModel model = (ResponseModel) values;
                                    count = model.getIdlelimit();
                                    countDownLogout(activity);
//                                    mHandler.postDelayed(mRunable, model.getIdlelimit()*1000);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                mHandler.postDelayed(mRunable, 60000);
                                count = 60;
                                countDownLogout(activity);

                            }
                        });
                    }
                });

                mThread.start();

            }
        }

        private static void countDownLogout(final Activity activity){
            T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.e(TAG, ""+count);
                            count--;
                            if (count == 0 && T != null){
                                Global.getInstance().clearUserName();
                                Util.logoutAPI(null, true);
                                T.cancel();
                                T = null;
                            }

                            if (count < 0 && T != null){
                                T.cancel();
                                T = null;
                                mThread.interrupt();
                                mThread = null;
                            }
                        }
                    });
                }
            }, 1000, 1000);

        }

    }

    private boolean canUseLeaving(Activity activity){
        return !(activity instanceof SplashScreenWWW |
//                activity instanceof ActivityShowNotify |
                activity instanceof MainActivity |
                activity instanceof ActivityRegister |
                activity instanceof ActivityShowNotify);
    }

    public static void showNotifyUpload(int id, String tag, String title, String message, int smallicon){

        mNotifyManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(MyApplication.getContext().getApplicationContext());
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.termtem_logo))
                .setSmallIcon(smallicon);

        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(tag, id, mBuilder.build());
        isUpload = true;
    }

    public static void uploadSuccess(int id, String tag, String title, String message, int smallicon, String type){
        if (mBuilder == null && (id != NOTIUPLOAD)) return;

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setSmallIcon(smallicon);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setOngoing(false);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (id != NOTIUPLOAD){
            Intent intent = new Intent(mContext, ActivityTopup.class);
            intent.putExtra(FragmentTopup.keyTopup, type);
            intent.putExtra("transid", tag);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

            mBuilder.setContentIntent(pendingIntent);
        }

        mNotifyManager.notify(tag, id, mBuilder.build());
        isUpload = false;
        mBuilder = null;
    }

    public static void uploadFail(int id, String tag, String title, String message, int smallicon, Call call, Callback callback){
        if (mBuilder == null && (id != NOTIUPLOAD)) return;

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setSmallIcon(smallicon);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setOngoing(false);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        if (call != null){
            Intent retryIntent = new Intent(mContext, retryButtonListener.class);
            retryIntent.putExtra("CALL", (Parcelable) call);
            retryIntent.putExtra("CALLBACK", (Parcelable) callback);
            PendingIntent pendingRetryIntent = PendingIntent.getBroadcast(mContext, 0,
                    retryIntent, 0);
            mBuilder.addAction(R.drawable.ic_refresh, mContext.getString(R.string.retry), pendingRetryIntent);
        }
        mNotifyManager.notify(tag, id, mBuilder.build());
        isUpload = false;
        mBuilder = null;

    }

    public static boolean isUpload(Context context, int message){
        if (isUpload) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage(R.string.has_upload)
                    .setPositiveButton(R.string.confirm, null)
                    .setCancelable(false)
                    .show();
        }
        return isUpload;
    }

    public static class retryButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am here");
            Call call = intent.getExtras().getParcelable("CALL");
            Callback callback = intent.getExtras().getParcelable("CALLBACK");
            APIHelper.enqueueWithRetry(call.clone(), callback);

        }
    }
}
