package com.worldwidewealth.wealthwallet.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.worldwidewealth.wealthwallet.R;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by MyNet on 4/11/2559.
 */

public class DialogNetworkError {

    private Context mContext;
    private AlertDialog.Builder builder;
    private static AlertDialog alertDialog;
    public DialogNetworkError(Context context, final Call call, final Callback callback){
        this.mContext = context;
        builder = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.network_error_title))
                .setMessage(mContext.getString(R.string.network_error_message))
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)mContext).finish();
                    }
                })
                .setPositiveButton(mContext.getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DialogCounterAlert.DialogProgress(mContext);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                call.clone().enqueue(callback);
                            }
                        }, 3000);
                    }
                });

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(mContext.getResources()
                                .getColor(android.R.color.holo_red_dark));
            }
        });
        alertDialog.show();

    }

    public static void dismiss(){
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }
}
