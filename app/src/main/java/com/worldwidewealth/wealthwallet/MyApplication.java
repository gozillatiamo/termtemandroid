package com.worldwidewealth.wealthwallet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.dialog.DialogNetworkError;
import com.worldwidewealth.wealthwallet.until.Until;
import io.fabric.sdk.android.Fabric;

/**
 * Created by MyNet on 17/11/2559.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        registerActivityLifecycleCallbacks(this);
        mContext = getApplicationContext();
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
/*
            if (LeavingOrEntering.currentActivity == null ||
                    activity != LeavingOrEntering.currentActivity) {
                LeavingOrEntering.activityResumed(activity);
            } else {
                LeavingOrEntering.activityStopped(activity);
            }
*/
        }

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

            if (currentActivity == activity || Global.getAGENTID() == null){
                Until.backToSignIn(activity);
                currentActivity = null;
            } else {
                currentActivity = activity;
            }


        }

        public static void activityStopped( Activity activity )
        {

            if (currentActivity == null) return;

            if ( currentActivity == activity )
            {
                // We were stopped and no-one else has been started.
                Until.logoutAPI();
/*
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Until.KEYPF, Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean("LOGOUT", true)){
                    Until.backToSignIn(activity);
                    currentActivity = null;
                } else {
                    Until.logoutAPI();
                }
*/
                //Until.backToSignIn(activity);
            }
        }
    }

    private boolean canUseLeaving(Activity activity){
        return !(activity instanceof SplashScreenWWW |
                activity instanceof ActivityShowNotify |
                activity instanceof MainActivity |
                activity instanceof ActivityRegister);
    }
}