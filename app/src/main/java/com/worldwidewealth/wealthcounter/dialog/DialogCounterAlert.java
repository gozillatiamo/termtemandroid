package com.worldwidewealth.wealthcounter.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.ActivityDashboard;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DialogCounterAlert {

    public DialogCounterAlert(Context context, String title, int msg){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.done, null)
                .show();

    }
}
