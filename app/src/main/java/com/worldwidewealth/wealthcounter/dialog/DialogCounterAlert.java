package com.worldwidewealth.wealthcounter.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.worldwidewealth.wealthcounter.R;

/**
 * Created by MyNet on 15/11/2559.
 */

public class DialogCounterAlert {

    public DialogCounterAlert(Context context, String title, String msg){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(R.string.done, null)
                .show();

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
            return progressDialog.isShowing();
        }
    }
}
