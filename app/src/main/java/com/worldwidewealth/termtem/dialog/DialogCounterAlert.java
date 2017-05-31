package com.worldwidewealth.termtem.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.ResponseModel;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DialogCounterAlert {

    private static AlertDialog alertDialog;
    public DialogCounterAlert(final Context context,
                              String title,
                              String msg,
                              DialogInterface.OnClickListener listener){

        new DialogCounterAlert(context, title, msg, null, listener, null);

    }

    public DialogCounterAlert(final Context context,
                              String title,
                              String msg,
                              final String txtBtn,
                              DialogInterface.OnClickListener listener){

        new DialogCounterAlert(context, title, msg, txtBtn, listener, null);

    }


    public DialogCounterAlert(final Context context,
                              final String title,
                              String msg,
                              final String txtBtn,
                              DialogInterface.OnClickListener doneListener,
                              DialogInterface.OnClickListener cancelListener){

        if (DialogProgress.isShow()){
            DialogProgress.dismiss();
        }

        AlertDialog.Builder builder;

        if (title.equals(context.getString(R.string.error))){
            builder = new AlertDialog.Builder(context, R.style.MyAlertDialogError);
        } else {
            builder = new AlertDialog.Builder(context, R.style.MyAlertDialogWarning);
        }

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        if (txtBtn != null) {
            builder.setPositiveButton(txtBtn, doneListener);
            builder.setNegativeButton(R.string.cancel, cancelListener);

        } else if (doneListener != null){
            builder.setPositiveButton(R.string.retry, doneListener);
            builder.setNegativeButton(R.string.cancel, cancelListener);

        } else {
            builder.setPositiveButton(R.string.done, doneListener);

        }
        alertDialog = builder.create();
        alertDialog.setOnShowListener(new MyShowListener());
/*
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (txtBtn != null){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources()
                            .getColor(android.R.color.holo_red_dark));

                } else if (alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources()
                            .getColor(android.R.color.holo_red_dark));
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources()
                            .getColor(R.color.colorPrimary));
                }
            }
        });
*/

        try {
            alertDialog.show();
        }catch (Exception e){}

    }

    public void show(){
        alertDialog.show();
    }

    public void dismiss(){
        alertDialog.dismiss();
    }

    public boolean isShow(){
        return alertDialog.isShowing();
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
        private static Context mContext;

        public DialogProgress(Context context) {
            this.mContext = context;
        }

        public static boolean show(){
            boolean isShow = isShow();

            if (!isShow){
                try {
                    progressDialog = ProgressDialog.show(mContext,
                            null,
                            mContext.getString(R.string.msg_progress),
                            true,
                            false);
                } catch (WindowManager.BadTokenException e){
                    e.printStackTrace();
                    progressDialog = null;
                }
            }

            return isShow;
        }

        public static void dismiss(){
            try {
                if (progressDialog != null) {
                    progressDialog.cancel();
                    progressDialog = null;
                }
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }

        public static boolean isShow(){
            if (progressDialog == null) return false;
            return progressDialog.isShowing();
        }
    }
}
