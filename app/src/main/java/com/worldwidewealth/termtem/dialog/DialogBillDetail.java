package com.worldwidewealth.termtem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.worldwidewealth.termtem.R;

/**
 * Created by MyNet on 10/10/2559.
 */

public class DialogBillDetail {

    private Context mContext;
    private Button mBtnClose;
    private Dialog dialog;

    public DialogBillDetail(Context context){
        this.mContext = context;

        initDialog();
    }

    private void initDialog(){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_bill);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        mBtnClose = (Button) dialog.findViewById(R.id.btn_close);
        mBtnClose.setVisibility(View.VISIBLE);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
