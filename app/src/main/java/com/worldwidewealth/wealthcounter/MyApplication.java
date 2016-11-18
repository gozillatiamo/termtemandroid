package com.worldwidewealth.wealthcounter;

import android.app.Application;
import android.content.Context;

/**
 * Created by MyNet on 17/11/2559.
 */

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }



    public static Context getContext(){
        return mContext;
    }
}
