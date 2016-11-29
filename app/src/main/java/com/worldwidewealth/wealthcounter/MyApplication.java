package com.worldwidewealth.wealthcounter;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
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
        Log.e("onActivityCreated", "true");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("onActivityStarted", "true");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.e("onActivityResumed", "true");
        if (canUseLeaving(activity)) LeavingOrEntering.activityResumed(activity);

    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("onActivityPaused", "true");

    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("onActivityStopped", "true");
        if (canUseLeaving(activity)) LeavingOrEntering.activityStopped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("onActivityDestroyed", "true");

    }

    static class LeavingOrEntering
    {
        private static Activity currentActivity = null;

        public static void activityResumed( Activity activity )
        {
                Log.e("currentActivity", "null");// Start the music!
            currentActivity = activity;
        }

        public static void activityStopped( Activity activity )
        {

            if (currentActivity == null) return;

            if ( currentActivity == activity )
            {
                // We were stopped and no-one else has been started.
                Log.e("currentActivity", "currentActivity == activity"); // Stop the music!
                Until.logoutAPI(activity);
                //Until.backToSignIn(activity);
                currentActivity = null;
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
