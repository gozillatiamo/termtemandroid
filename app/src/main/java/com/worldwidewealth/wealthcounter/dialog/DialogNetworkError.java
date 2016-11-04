package com.worldwidewealth.wealthcounter.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.worldwidewealth.wealthcounter.R;

/**
 * Created by MyNet on 4/11/2559.
 */

public class DialogNetworkError {

    private Context mContext;
    private AlertDialog.Builder alertDialog;
    public DialogNetworkError(Context context){
        this.mContext = context;
        alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.network_error_title))
                .setMessage(mContext.getString(R.string.network_error_message))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)mContext).recreate();
                    }
                });
        alertDialog.show();
    }
}
