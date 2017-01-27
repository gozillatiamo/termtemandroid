package com.worldwidewealth.wealthwallet.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.SplashScreenWWW;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by MyNet on 4/11/2559.
 */

public class DialogNetworkError {

    private Context mContext;
    private AlertDialog.Builder builder;
    private static AlertDialog alertDialog;
    public DialogNetworkError(final Context context, String msg, final Call call, final Callback callback){
        this.mContext = context;
        if (mContext == null) return;
        if (msg == null) msg = mContext.getString(R.string.network_error_message);
        builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialog)
                .setTitle(mContext.getString(R.string.error))
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context instanceof SplashScreenWWW)
                            ((Activity)mContext).finish();
                        else
                            dialog.dismiss();
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

        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(mContext.getResources()
                                .getColor(android.R.color.holo_red_dark));
                ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(mContext.getResources()
                                .getColor(R.color.colorPrimary));
                TextView msgTxt = (TextView) ((AlertDialog)dialog).findViewById(android.R.id.message);
                msgTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.small_text_size));
/*
                TextView msgTxt = (TextView) ((AlertDialog)dialog).findViewById(android.R.id.message);
                TextView titleTxt = (TextView) ((AlertDialog)dialog).findViewById(android.R.id.title);
                msgTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.my_text_size);
                titleTxt.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
*/
            }
        });

        try {
            alertDialog.show();
        } catch (WindowManager.BadTokenException e){}

    }

    public static void dismiss(){
        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }
}
