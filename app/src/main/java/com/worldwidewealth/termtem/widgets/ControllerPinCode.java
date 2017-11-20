package com.worldwidewealth.termtem.widgets;

import android.content.Context;
import android.content.SharedPreferences;

import com.worldwidewealth.termtem.MyApplication;

/**
 * Created by gozillatiamo on 11/20/17.
 */

public class ControllerPinCode {

    public static final String KEY_CONTROLLER = ControllerPinCode.class.getSimpleName();
    public static final String KEY_ENABLE = "enablecontroller";

    public static ControllerPinCode getInstance(){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEY_CONTROLLER, Context.MODE_PRIVATE);
        boolean enable = preferences.getBoolean(KEY_ENABLE, false);

        if (enable)
            return new ControllerPinCode();
        else
            return null;
    }

    public static void enable(boolean enable){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEY_CONTROLLER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(KEY_ENABLE, enable);
        editor.commit();
    }
}
