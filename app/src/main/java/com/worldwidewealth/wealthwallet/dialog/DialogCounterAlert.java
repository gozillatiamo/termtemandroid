package com.worldwidewealth.wealthwallet.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.model.ResponseModel;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DialogCounterAlert {

    private AlertDialog alertDialog;
    public DialogCounterAlert(final Context context,
                              String title,
                              String msg,
                              DialogInterface.OnClickListener listener){

        if (DialogProgress.isShow()){
            DialogProgress.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false);

        if (listener != null){
            builder.setPositiveButton(R.string.retry, listener);
            builder.setNegativeButton(R.string.cancel, null);

        } else {
            builder.setPositiveButton(R.string.done, listener);

        }
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources()
                            .getColor(android.R.color.holo_red_dark));
                }
            }
        });
        alertDialog.show();
    }

    public static class DialogFromResponse{
        public DialogFromResponse(Context context, String response){
            ResponseModel responseModel = new Gson().fromJson(response, ResponseModel.class);
            new AlertDialog.Builder(context)
                    .setMessage(responseModel.getMsg())
                    .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public static class DialogProgress{
        private static ProgressDialog progressDialog;

        public DialogProgress(Context context) {

            progressDialog = ProgressDialog.show(context,
                    null,
                    context.getString(R.string.msg_progress),
                    true,
                    false);

        }

        public static void dismiss(){
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        public static boolean isShow(){
            if (progressDialog == null) return false;
            return progressDialog.isShowing();
        }
    }
}
