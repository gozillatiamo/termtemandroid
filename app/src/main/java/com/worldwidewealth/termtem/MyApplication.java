package com.worldwidewealth.termtem;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.worldwidewealth.termtem.broadcast.LocalService;
import com.worldwidewealth.termtem.broadcast.NetworkStateMonitor;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.chat.ChatBotActivity;
import com.worldwidewealth.termtem.chat.PhotoViewActivity;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.DialogNetworkError;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.model.TopupResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.TermTemSignIn;
import com.worldwidewealth.termtem.util.Util;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by MyNet on 17/11/2559.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Context mContext;
    private static Bus mBus;
    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;
    private NetworkStateMonitor mNetworkStateMonitor;
    public static boolean clickable = true;
    public static final int NOTIUPLOAD = 1;
    public static final int NOTITOPUP = 2;
    public static boolean isUpload = false;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_CHOOSE = 2;
    private static Intent intentLocalService = null;


//    private static RequestModel mLastRequest;


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
//    private static Thread mThread;

    public static final String TAG = MyApplication.class.getSimpleName();

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SubmitTopupRequestModel submitModel = null;
            if (Global.getInstance().getLastTranId() != null){
                submitModel = (SubmitTopupRequestModel)Global.getInstance().getLastSubmit().getData();
            }

            if (submitModel == null) return;

            if (FragmentTopupPackage.callSubmit != null && FragmentTopupPackage.callSubmit.isExecuted()){
                FragmentTopupPackage.callSubmit.cancel();
            }

            if (!(intent.getExtras().containsKey("topup"))) return;

            String action = Global.getInstance().getLastSubmitAction();
            if (intent.getExtras().getBoolean("topup")){
                MyApplication.uploadSuccess(MyApplication.NOTITOPUP, submitModel.getTRANID(),
                        getTitleTypeToup(action) + " " + submitModel.getCARRIER() + " " + submitModel.getAMT() +
                                " " + MyApplication.getContext().getString(R.string.currency),
                        MyApplication.getContext().getString(R.string.phone_number) + " " +
                                submitModel.getPHONENO() + " " + MyApplication.getContext().getString(R.string.success),
                        R.drawable.ic_check_circle_white);

            } else {

                Global.getInstance().setLastSubmit(null);
                MyApplication.uploadFail(MyApplication.NOTITOPUP,
                        submitModel.getTRANID(),
                        getTitleTypeToup(action) + " " + submitModel.getCARRIER() + " " + submitModel.getAMT() + " "
                                + MyApplication.getContext().getString(R.string.currency),
                        MyApplication.getContext().getString(R.string.phone_number) + " " + submitModel.getPHONENO() + " "
                                + MyApplication.getContext().getString(R.string.msg_upload_fail),
                        android.R.drawable.stat_sys_warning);

            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(this);
        mContext = getApplicationContext();
        userAgent = com.google.android.exoplayer2.util.Util.getUserAgent(this, getString(R.string.app_name));
        mBus = new Bus();
        mNetworkStateMonitor = new NetworkStateMonitor(mContext);

        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));

/*
        Intent intent = new Intent(MyFirebaseMessagingService.INTENT_FILTER);
        sendBroadcast(intent);
*/


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
        if (!(activity instanceof ActivityShowNotify))
            stopService(activity);


        LeavingOrEntering.activityResumed(activity);

        startSlip();



    }

    public static void startSlip(){
        if (!canUseLeaving(LeavingOrEntering.currentActivity)) return;

        if (Global.getInstance().getLastTranId() != null &&
                !(LeavingOrEntering.currentActivity instanceof ActivityTopup)){

            if (Global.getInstance().getSubmitStatus()) {
                Intent intent = new Intent(LeavingOrEntering.currentActivity, ActivityTopup.class);
                intent.putExtra(FragmentTopup.keyTopup, getTypeToup(Global.getInstance().getLastSubmitAction()));
                LeavingOrEntering.currentActivity.startActivity(intent);
            }

        }

    }

    @Override
    public void onActivityPaused(Activity activity) {
//        DialogCounterAlert.DialogProgress.dismiss();
        DialogNetworkError.dismiss();
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        stopService(activity);


            LeavingOrEntering.activityStopped(activity);

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

    public static void stopService(Activity activity){
        if (MyApplication.intentLocalService != null){
            MyApplication.getContext().stopService(MyApplication.intentLocalService);
            intentLocalService = null;
/*

            Context context = LeavingOrEntering.currentActivity;

            if (context == null) context = getContext();


            if (MyApplication

*/

            if (!(canUseLeaving(activity))) return;

            if (Global.getInstance().getUSERNAME() == null) {
                Util.backToSignIn(activity);
                return;
            }



            if (Global.getInstance().getAGENTID() == null){
                new TermTemSignIn(MyApplication.getContext(), TermTemSignIn.TYPE.RELOGIN,
                        new DialogCounterAlert.DialogProgress(activity).show()).getTXIDfromServer();
            } else {
                Util.logoutAPI(activity, false);
            }

        }

    }

    public static void startService(){
//        if (Global.getInstance().getAGENTID() == null) return;
        if (MyApplication.intentLocalService != null) {
            MyApplication.getContext().stopService(MyApplication.intentLocalService);
            intentLocalService = null;
        }

        MyApplication.intentLocalService = new Intent(getContext(), LocalService.class);
        MyApplication.getContext().startService(MyApplication.intentLocalService);
    }


    public static class LeavingOrEntering {
        public static Activity currentActivity = null;
//        public static int count;
//        public static Timer T;

        public static void activityResumed( Activity activity ) {
            String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

/*
            try {
                if (mThread != null) {
//                    mHandler.removeCallbacks(mRunable);
                    if(mThread.isAlive()) {
                        mThread.interrupt();
                    }
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
*/




//            if (strCurrentAtivity == null) {
//                currentActivity = activity;
//                return;
//            }
//
//            if (strCurrentAtivity.equals(activity.getLocalClassName())) {
//
//            }
            currentActivity = activity;


        }


        public static void activityStopped(final Activity activity ) {
            String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

            if (currentActivity == null) return;

            if ( strCurrentAtivity.equals(activity.getLocalClassName()) ) {
                // We were stopped and no-one else has been started.
//                Util.logoutAPI(false);
/*
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
*/

//                stopService(activity);
                startService();


            }
        }

/*
        private static void countDownLogout(final Activity activity){
            Intent intent = new Intent(getContext(), LocalService.class);
            getContext().startService(intent);

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
*/

    }

    public static boolean canUseLeaving(Activity activity){
        return !(activity instanceof SplashScreenWWW |
                activity instanceof ActivityShowNotify |
                activity instanceof MainActivity |
                activity instanceof ActivityRegister |
                activity instanceof ChatBotActivity |
                activity instanceof PhotoViewActivity);
    }

    public static void showNotifyUpload(int id, String tag, String title, String message, int smallicon){

        if (id != NOTIUPLOAD){
            if (Global.getInstance().getLastSubmit() == null) return;
//            mLastRequest = Global.getInstance().getLastSubmit();
        }

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

    public static void uploadSuccess(int id, String tag, String title, String message, int smallicon){
        if (mBuilder == null && (id != NOTIUPLOAD)) return;


        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setSmallIcon(smallicon);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setOngoing(false);
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        isUpload = false;

        if (id != NOTIUPLOAD){
            /*try {
//                Global.getInstance().setProcessSubmit(null);
                mNotifyManager.cancel(Global.getInstance().getProcessSubmit(), id);
                Intent intent = new Intent(mContext, ActivityTopup.class);
                intent.putExtra(FragmentTopup.keyTopup, type);
                intent.putExtra("transid", tag);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                mBuilder = null;
                return;
            } catch (Exception e){
                e.printStackTrace();
            }*/

            if (Global.getInstance().getLastSubmit() == null) return;

            Intent intent = new Intent(mContext, ActivityTopup.class);
            intent.putExtra(FragmentTopup.keyTopup, getTypeToup(Global.getInstance().getLastSubmitAction()));
//            Global.getInstance().setProcessSubmit(null, null);
            intent.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            startSlip();

//            mLastRequest = null;

        }

        mNotifyManager.notify(tag, id, mBuilder.build());
        mBuilder = null;


    }

    public static void uploadFail(int id, String tag, String title, String message, int smallicon){
        if (mBuilder == null && (id != NOTIUPLOAD)) return;

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setSmallIcon(smallicon);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        RequestModel requestModel = Global.getInstance().getLastSubmit();
        if (requestModel != null){
            mBuilder.setOngoing(true);
            Intent retryIntent = new Intent(mContext, retryButtonListener.class);
            byte[] requestByte = Util.ParcelableUtil.toByteArray(requestModel);
//            Global.getInstance().setProcessSubmit(null, null);

            retryIntent.putExtra("REQUEST", requestByte);
            PendingIntent pendingRetryIntent = PendingIntent.getBroadcast(mContext, 0,
                    retryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(R.drawable.ic_refresh, mContext.getString(R.string.retry), pendingRetryIntent);
        } else {
            mBuilder.setOngoing(false);
            isUpload = false;

            if (id == NOTITOPUP) {
                if (Global.getInstance().getLastSubmit() == null) return;
                Global.getInstance().setLastSubmit(null);
//                mLastRequest = null;
//                mNotifyManager.cancel(tag, id);
/*
                mBuilder = null;
                return;
*/
            }
            mBuilder.setAutoCancel(true);
        }

        Log.e(TAG, "TAG: "+tag);
        Log.e(TAG, "ID: "+id);
        mNotifyManager.notify(tag, id, mBuilder.build());

        mBuilder = null;

    }

    public static boolean isUpload(Context context, int message){
        if (isUpload) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setPositiveButton(R.string.confirm, null)
                    .setCancelable(false)
                    .show();
        }
        return isUpload;
    }

    public static String getTypeToup(String action){
        switch (action){
            case APIServices.ACTIONSUBMITTOPUP:
                return FragmentTopup.MOBILE;
            case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                return FragmentTopup.PIN;
        }

        return null;

    }

    public static String getTitleTypeToup(String action){
        switch (action){
            case APIServices.ACTIONSUBMITTOPUP:
                return MyApplication.getContext().getString(R.string.title_topup);
            case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                return MyApplication.getContext().getString(R.string.dashboard_pin);
        }

        return null;

    }


    public static class retryButtonListener extends BroadcastReceiver {
        APIServices services = APIServices.retrofit.create(APIServices.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Here", "I am here");
/*
            Call call =  intent.getExtras().getParcelable("CALL");
            Callback callback = intent.getExtras().getParcelable("CALLBACK");
            APIHelper.enqueueWithRetry(call.clone(), callback);
*/


//            RequestModel requestModel = intent.getExtras().getby("REQUEST");
            final RequestModel requestModel = Util.ParcelableUtil.toParcelable(intent.getExtras().getByteArray("REQUEST"), RequestModel.CREATOR);

                    if (requestModel.getAction().contains("SUBMIT"))
                        topupService(requestModel);


        }

        private void topupService(final RequestModel requestModel){
            final Call<ResponseBody> call = services.topupService(requestModel);

            String title = null;
            String mTopup = null;
            SubmitTopupRequestModel submitModel = null;

/*
            switch (requestModel.getAction()){
                case APIServices.ACTIONSUBMITTOPUP:
                    title = MyApplication.getContext().getString(R.string.title_topup);
                    break;
                case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                    title = MyApplication.getContext().getString(R.string.dashboard_pin);
                    break;
            }
*/

            title = getTitleTypeToup(requestModel.getAction());
            mTopup = getTypeToup(requestModel.getAction());

            if (requestModel.getData() instanceof SubmitTopupRequestModel) {

                submitModel = (SubmitTopupRequestModel) requestModel.getData();

                MyApplication.showNotifyUpload(MyApplication.NOTITOPUP,
                        submitModel.getTRANID(),
                        title+" " +submitModel.getCARRIER()+" "+submitModel.getPHONENO()+" "
                                +getContext().getString(R.string.currency),
                        getContext().getString(R.string.phone_number)+" "+submitModel.getPHONENO()+" "
                                +getContext().getString(R.string.processing),
                        android.R.drawable.stat_notify_sync);

            }



            final String finalMTopup = mTopup;
            final String finalTitle = title;
            final SubmitTopupRequestModel finalSubmitModel = submitModel;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                            Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);

                            if (finalSubmitModel != null) {

                                if (responseValues == null) {

                                    MyApplication.uploadFail(MyApplication.NOTITOPUP,
                                            finalSubmitModel.getTRANID(),
                                            finalTitle + " " + finalSubmitModel.getCARRIER() + " " + finalSubmitModel.getAMT() + " "
                                                    + MyApplication.getContext().getString(R.string.currency),
                                            MyApplication.getContext().getString(R.string.phone_number) + " " + finalSubmitModel.getPHONENO() + " "
                                                    + MyApplication.getContext().getString(R.string.msg_upload_fail),
                                            android.R.drawable.stat_sys_warning);

                                    return;
                                }

                                if (responseValues instanceof ResponseModel) {

                                    MyApplication.uploadSuccess(MyApplication.NOTITOPUP, finalSubmitModel.getTRANID(),
                                            finalTitle + " " + finalSubmitModel.getCARRIER() + " " + finalSubmitModel.getAMT() +
                                                    " " + MyApplication.getContext().getString(R.string.currency),
                                            MyApplication.getContext().getString(R.string.phone_number) + " " +
                                                    finalSubmitModel.getPHONENO() + " " + MyApplication.getContext().getString(R.string.success),
                                            R.drawable.ic_check_circle_white);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, "Exception submit topup: " + t.getMessage());

                            if (finalSubmitModel != null) {

                                MyApplication.uploadFail(MyApplication.NOTITOPUP, finalSubmitModel.getTRANID(),
                                        finalTitle + " " + finalSubmitModel.getCARRIER() + " " + finalSubmitModel.getAMT() +
                                                " " + MyApplication.getContext().getString(R.string.currency),
                                        MyApplication.getContext().getString(R.string.phone_number) + " " + finalSubmitModel.getPHONENO() +
                                                " " + MyApplication.getContext().getString(R.string.msg_upload_fail),
                                        android.R.drawable.stat_sys_warning);
                            }

                        }
                    });
                }
            },5000);

        }

    }
}
