package com.worldwidewealth.termtem;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.DialogNetworkError;
import com.worldwidewealth.termtem.util.Util;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by MyNet on 17/11/2559.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Context mContext;
    private static NotificationManager mNotifyManager;
    private static NotificationCompat.Builder mBuilder;
    public static boolean clickable = true;
    private static final int NOTIUPLOAD = 1;
    private static boolean isUpload = false;
    private static Handler  mHandler = new Handler();
    private static Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Clear User Data");
            Global.getInstance().clearUserData();
        }
    };

    public static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(this);
        mContext = getApplicationContext();


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
            Log.e(TAG, "oldVersionCode: "+Global.getInstance().getVERSIONCODE());
            Log.e(TAG, "newVersionCode: "+versionCode);
            if (Global.getInstance().getVERSIONCODE() != versionCode){
                if (Global.getInstance().getTXID() != null){
                    Log.e(TAG, "TXID != null");
                    Util.logoutAPI(true);
                }

                Util.deleteCache(this);
                Global.getInstance().setVERSIONCODE(versionCode);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Context getContext(){
        return mContext;
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
        }

        LeavingOrEntering.currentActivity = null;

    }

    @Override
    public void onActivityPaused(Activity activity) {
        DialogCounterAlert.DialogProgress.dismiss();
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

    }

    public static class LeavingOrEntering
    {
        public static Activity currentActivity = null;

        public static void activityResumed( Activity activity )
        {
            String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

            if (strCurrentAtivity.equals(activity.getLocalClassName())){
                Log.e(TAG, "ActivityisEqual");
                if (Global.getInstance().getTXID() == null){
                    Log.e(TAG, "TXID == null");
                    Util.backToSignIn(activity);
                } /*else if (mRunable != null){
                    Log.e(TAG, "mRanble != null and remveCallbacks");
                    mHandler.removeCallbacks(mRunable);
                }*/

                else {
                    mHandler.removeCallbacks(mRunable);

                }

//                currentActivity = null;

            } /*else {
                Log.e(TAG, "ActivityNotEqual");
                currentActivity = activity;
            }*/

            currentActivity = activity;


        }

        public static void activityStopped( Activity activity ) {
            String strCurrentAtivity = (currentActivity == null) ? null:currentActivity.getLocalClassName();

            if (currentActivity == null) return;

            if ( strCurrentAtivity.equals(activity.getLocalClassName()) ) {
                // We were stopped and no-one else has been started.
                Log.e(TAG, "ActivityisEqual and Call Logout");
                Util.logoutAPI(false);
                mHandler.postDelayed(mRunable, 60000);
            }
        }
    }

    private boolean canUseLeaving(Activity activity){
        return !(activity instanceof SplashScreenWWW |
//                activity instanceof ActivityShowNotify |
                activity instanceof MainActivity |
                activity instanceof ActivityRegister);
    }

    public static void showNotifyUpload(){

        mNotifyManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(MyApplication.getContext().getApplicationContext());
        mBuilder.setContentTitle(getContext().getString(R.string.title_upload))
                .setContentText(getContext().getString(R.string.msg_upload))
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher_2))
                .setSmallIcon(android.R.drawable.stat_sys_upload);

        mBuilder.setProgress(0, 0, true);
        mNotifyManager.notify(NOTIUPLOAD, mBuilder.build());
        isUpload = true;
    }

    public static void uploadSuccess(){
        mBuilder.setContentTitle(getContext().getString(R.string.title_upload_success));
        mBuilder.setContentText(getContext().getString(R.string.msg_upload_success));
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_upload_done);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setOngoing(false);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mNotifyManager.notify(NOTIUPLOAD, mBuilder.build());
        isUpload = false;
    }

    public static void uploadFail(){
        mBuilder.setContentTitle(getContext().getString(R.string.title_upload_fail));
        mBuilder.setContentText(getContext().getString(R.string.msg_upload_fail));
        mBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
        mBuilder.setProgress(0, 0, false);
        mBuilder.setOngoing(false);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mNotifyManager.notify(NOTIUPLOAD, mBuilder.build());
        isUpload = false;
    }

    public static boolean isUpload(Context context){
        if (isUpload) {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setMessage(R.string.has_upload)
                    .setPositiveButton(R.string.confirm, null)
                    .setCancelable(false)
                    .show();
        }
        return isUpload;
    }
}
