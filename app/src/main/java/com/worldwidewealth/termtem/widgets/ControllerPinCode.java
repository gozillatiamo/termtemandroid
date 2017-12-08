package com.worldwidewealth.termtem.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.worldwidewealth.termtem.LockScreenActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;

/**
 * Created by gozillatiamo on 11/20/17.
 */

public class ControllerPinCode {

    public static final String KEY_CONTROLLER = ControllerPinCode.class.getSimpleName();
    public static final String KEY_ENABLE = "enablecontroller";
    public static final String KEY_IGNOR = "ignor";
    private static Dialog mDialogAdvice;
//    private static Context mContext;

    public static ControllerPinCode getInstance(){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEY_CONTROLLER, Context.MODE_PRIVATE);
        boolean enable = preferences.getBoolean(KEY_ENABLE, false);
//        mContext = context;
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

    public void showDialogAdvice(final Context context){
            mDialogAdvice = new Dialog(context);
            mDialogAdvice.setContentView(R.layout.dialog_setting_pin_code);
            mDialogAdvice.setCancelable(false);
            mDialogAdvice.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final DialogAdviceViewHolder holder = new DialogAdviceViewHolder(mDialogAdvice);

            mDialogAdvice.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    holder.mCheckIgnor.setChecked(getIgnorDialog());
                }
            });

            holder.mCheckIgnor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    setIgnorDialog(b);
                }
            });

            holder.mBtnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialogAdvice.cancel();
                    Intent intent = new Intent(context, LockScreenActivity.class);
                    intent.putExtra(LockScreenActivity.KEY_ACTION, LockScreenActivity.SETUP_PIN);
                    context.startActivity(intent);

                }
            });

            holder.mBtnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialogAdvice.cancel();
                }
            });

/*
            holder.mBtnLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialogAdvice.cancel();
                }
            });

            holder.mBtnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
*/

        mDialogAdvice.show();
    }

    public void dismissDialogAdvice(){
        if (mDialogAdvice != null)
            mDialogAdvice.dismiss();
    }

    public void setIgnorDialog(boolean ignorDialog){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEY_CONTROLLER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(KEY_IGNOR, ignorDialog);
        editor.commit();

    }

    public boolean getIgnorDialog(){
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(KEY_CONTROLLER, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_IGNOR, false);
    }

    class DialogAdviceViewHolder{
        public CheckBox mCheckIgnor;
        public Button mBtnLater, mBtnSetting;
        public DialogAdviceViewHolder(Dialog itemView){
            mCheckIgnor = itemView.findViewById(R.id.checkbox_ignor);
            mBtnLater = itemView.findViewById(R.id.btn_later);
            mBtnSetting = itemView.findViewById(R.id.btn_setting);
        }
    }
}
