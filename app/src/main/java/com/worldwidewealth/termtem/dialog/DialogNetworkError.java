package com.worldwidewealth.termtem.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.SplashScreenWWW;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.Util;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by MyNet on 4/11/2559.
 */

public class DialogNetworkError {

    private Context mContext;
    private AlertDialog.Builder builder;
    private static AlertDialog alertDialog;
    private String positiveBtn;
    private int styleAlert;
    private RequestModel requestModel = null;
    private String strRequest;
    private String title;

    public DialogNetworkError(final Context context, String msg, final Call call, final Callback callback , boolean canCancel){
        new DialogNetworkError(context, msg, call, callback, canCancel, null);
    }

    public DialogNetworkError(final Context context, String msg, final Call call, final Callback callback , boolean canCancel, DialogInterface.OnDismissListener dismissListener){
        this.mContext = context;
        if (mContext == null) return;
        strRequest = Util.convertToStringRequest(call.request().body());

        if (strRequest != null){
            requestModel = new Gson().fromJson(strRequest, RequestModel.class);
        }

        if (msg == null)
            msg = /*requestModel.getAction()+"\n"+*/mContext.getString(R.string.network_error_message);

        switch (requestModel.getAction()){
            case APIServices.ACTIONESLIP:
                msg = /*requestModel.getAction()+"\n"+*/context.getString(R.string.message_get_slip_again);
                positiveBtn = context.getString(R.string.confirm);
                styleAlert = R.style.MyAlertDialogWarning;
                title = mContext.getString(R.string.warning);
                break;
            default:
                positiveBtn = context.getString(R.string.try_again);
                styleAlert = R.style.MyAlertDialogError;
                title = mContext.getString(R.string.error);

        }

        builder = new AlertDialog.Builder(mContext, styleAlert)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DialogCounterAlert.DialogProgress(mContext).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                APIHelper.enqueueWithRetry(call.clone(), callback);
                            }
                        }, 5000);
                    }
                });

        if (canCancel){
            builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (context instanceof SplashScreenWWW)
                        ((Activity)mContext).finish();
                    else
                        dialog.dismiss();
                }
            });
        }

        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new MyShowListener());
        try {
            alertDialog.show();
            TextView msgTxt = (TextView) alertDialog.findViewById(android.R.id.message);
            msgTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.small_text_size));

        } catch (Exception e){}

    }

/*
    public DialogNetworkError(final Context context, String msg, final Call call, final Callback callback) {
        new DialogNetworkError(context, msg, call, callback, true);
    }
*/

    public static void dismiss(){
        if (alertDialog != null){
            try {
                alertDialog.dismiss();
            } catch (RuntimeException e){}
        }
    }
}
