package com.worldwidewealth.wealthcounter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.worldwidewealth.wealthcounter.until.Until;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyNet on 17/11/2559.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
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
        Log.e("onActivityResumed", "true");
        if (canUseLeaving(activity)){
            if (LeavingOrEntering.currentActivity == null ||
                    activity != LeavingOrEntering.currentActivity) {
                LeavingOrEntering.activityResumed(activity);
            } else {
                LeavingOrEntering.activityStopped(activity);
            }
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("onActivityStopped", "true");
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


            currentActivity = activity;

        }

        public static void activityStopped( Activity activity )
        {

            if (currentActivity == null) return;

            if ( currentActivity == activity )
            {
                // We were stopped and no-one else has been started.
                Log.e("currentActivity", "currentActivity == activity"); // Stop the music!
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(Until.KEYPF, Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean("LOGOUT", true)){
                    Until.backToSignIn(activity);
                    currentActivity = null;
                } else {
                    Until.logoutAPI();
                }
                //Until.backToSignIn(activity);
            }
        }
    }

    private boolean canUseLeaving(Activity activity){
        return !(activity instanceof SplashScreenWWW |
                activity instanceof SplashScreenCounter |
                activity instanceof MainActivity |
                activity instanceof ActivityRegister);
    }
}