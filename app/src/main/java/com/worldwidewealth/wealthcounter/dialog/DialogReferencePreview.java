package com.worldwidewealth.wealthcounter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.worldwidewealth.wealthcounter.R;

/**
 * Created by MyNet on 17/10/2559.
 */

public class DialogReferencePreview {

    private Context mContext;
    private Button mBtnPayment;
    private Dialog dialog;

    public DialogReferencePreview(Context context){
        this.mContext = context;

        initDialog();
    }

    private void initDialog(){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_mt_receive);
        dialog.setCancelable(false);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        mBtnPayment = (Button) dialog.findViewById(R.id.btn_payment);

    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

    public void setOnSubmit(View.OnClickListener listener){
        mBtnPayment.setOnClickListener(listener);
    }

}
