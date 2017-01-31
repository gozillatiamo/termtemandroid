package com.worldwidewealth.termtem.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.worldwidewealth.termtem.R;

/**
 * Created by MyNet on 10/10/2559.
 */

public class DialogBillScan {

    private Context mContext;
    private Button mBtnClose;
    private ImageView mImageCode;
    private Dialog dialog;
    private String mType;

    public DialogBillScan(Context context,String type){
        this.mContext = context;
        this.mType = type;
        initDialog();
    }

    private void initDialog(){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bill_scan);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        mBtnClose = (Button) dialog.findViewById(R.id.btn_cancel);
        mBtnClose.setVisibility(View.VISIBLE);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mImageCode = (ImageView) dialog.findViewById(R.id.img_code);
        switch (mType){
            case "barcode":
                mImageCode.setImageResource(R.drawable.barcodebill);
                break;
            case "qrcode":
                mImageCode.setImageResource(R.drawable.qr_code);
                break;
        }
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
