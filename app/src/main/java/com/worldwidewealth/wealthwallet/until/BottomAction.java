package com.worldwidewealth.wealthwallet.until;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;

import java.text.NumberFormat;

/**
 * Created by user on 19-Jan-17.
 */

public class BottomAction {

    private ViewHolder mHolder;
    private  NumberFormat mFormat;
    public static final int NEXT = 0;
    public static final int SUBMIT = 1;

    public BottomAction(final Context context, View view, int type , View.OnClickListener listener){
        mHolder = new ViewHolder(view);
        mFormat = NumberFormat.getInstance();
        mFormat.setMaximumFractionDigits(2);
        mFormat.setMinimumFractionDigits(2);

        mHolder.mTextPrice.setText(mFormat.format(0));

        switch (type){
            case NEXT:
                mHolder.mBtnNext.setVisibility(View.VISIBLE);
                mHolder.mLayoutSubmit.setVisibility(View.GONE);
                mHolder.mBtnNext.setOnClickListener(listener);
                break;
            case SUBMIT:
                mHolder.mBtnNext.setVisibility(View.GONE);
                mHolder.mLayoutSubmit.setVisibility(View.VISIBLE);
                mHolder.mBtnSubmit.setOnClickListener(listener);
                mHolder.mBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity)context).finish();
                    }
                });
                break;
        }
    }

    public void updatePrice(double price){
        mHolder.mTextPrice.setText(mFormat.format(price));
    }

    public String getPrice(){
       return mHolder.mTextPrice.getText().toString();
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        private Button mBtnNext, mBtnSubmit, mBtnCancel;
        private TextView mTextPrice;
        private View mLayoutSubmit;
        public ViewHolder(View itemView) {
            super(itemView);
            mBtnNext = (Button) itemView.findViewById(R.id.btn_next);
            mBtnSubmit = (Button) itemView.findViewById(R.id.btn_submit);
            mBtnCancel = (Button) itemView.findViewById(R.id.btn_cancel);
            mTextPrice = (TextView) itemView.findViewById(R.id.text_price);
            mLayoutSubmit = (View) itemView.findViewById(R.id.layout_btn_submit);
        }
    }
}
