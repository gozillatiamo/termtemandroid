package com.worldwidewealth.termtem.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;

/**
 * Created by user on 11-Apr-17.
 */

public class MyShowListener implements DialogInterface.OnShowListener{

    @Override
    public void onShow(DialogInterface dialogInterface) {
        AlertDialog alertDialog = (AlertDialog) dialogInterface;
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(((AlertDialog) dialogInterface).getContext().getResources()
                        .getColor(R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(((AlertDialog) dialogInterface).getContext().getResources()
                        .getColor(android.R.color.holo_red_dark));

        TextView msgTxt = (TextView) alertDialog.findViewById(android.R.id.message);
        msgTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, alertDialog.getContext().getResources().getDimension(R.dimen.my_text_size));

    }
}
