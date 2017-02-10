package com.worldwidewealth.termtem.until;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;

import java.text.NumberFormat;

/**
 * Created by user on 19-Jan-17.
 */

public class BottomAction {

    private ViewHolder mHolder;
    private  NumberFormat mFormat;
    private Context mContext;
    private int mCurrentType = 0;
    public static final int NEXT = 0;
    public static final int SUBMIT = 1;

    public BottomAction(final Context context, View view, int type , View.OnClickListener listener){
        mHolder = new ViewHolder(view);
        this.mContext = context;
        mFormat = NumberFormat.getInstance();
        mFormat.setMaximumFractionDigits(2);
        mFormat.setMinimumFractionDigits(2);

        mHolder.mTextPrice.setText(mFormat.format(0));

        swichType(type, listener);
    }

    public void swichType(int type, View.OnClickListener listener){
        mCurrentType = type;
        switch (mCurrentType){
            case NEXT:
                mHolder.mBtnNext.setVisibility(View.VISIBLE);
                mHolder.mLayoutSubmit.setVisibility(View.GONE);
                mHolder.mLayoutAmountPreview.setVisibility(View.VISIBLE);
                mHolder.mBtnNext.setOnClickListener(listener);
                break;
            case SUBMIT:
                mHolder.mBtnNext.setVisibility(View.GONE);
                mHolder.mLayoutAmountPreview.setVisibility(View.GONE);
                mHolder.mLayoutSubmit.setVisibility(View.VISIBLE);
                mHolder.mBtnSubmit.setOnClickListener(listener);
                mHolder.mBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity)mContext).finish();
                    }
                });
                break;
        }

        setEnable(true);
    }

    public void toggleType(){
        switch (getType()){
            case NEXT:
                mHolder.mBtnNext.setVisibility(View.GONE);
                mHolder.mLayoutSubmit.setVisibility(View.VISIBLE);
                mHolder.mLayoutAmountPreview.setVisibility(View.GONE);
                break;
            case SUBMIT:
                mHolder.mBtnNext.setVisibility(View.VISIBLE);
                mHolder.mLayoutSubmit.setVisibility(View.GONE);
                mHolder.mLayoutAmountPreview.setVisibility(View.VISIBLE);
                break;
        }
    }

    public int getType(){
        return mCurrentType;
    }

    public void updatePrice(double price){
        mHolder.mTextPrice.setText(mFormat.format(price));
    }

    public String getPrice(){
       return mHolder.mTextPrice.getText().toString().replaceAll(",","");
    }

    public void setEnable(boolean enable){
        mHolder.mBtnNext.setEnabled(enable);
        mHolder.mBtnSubmit.setEnabled(enable);
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        private Button mBtnNext, mBtnSubmit, mBtnCancel;
        private TextView mTextPrice;
        private View mLayoutSubmit, mLayoutAmountPreview;
        public ViewHolder(View itemView) {
            super(itemView);
            mBtnNext = (Button) itemView.findViewById(R.id.btn_next);
            mBtnSubmit = (Button) itemView.findViewById(R.id.btn_submit);
            mBtnCancel = (Button) itemView.findViewById(R.id.btn_cancel);
            mTextPrice = (TextView) itemView.findViewById(R.id.text_price);
            mLayoutSubmit = (View) itemView.findViewById(R.id.layout_btn_submit);
            mLayoutAmountPreview = (View) itemView.findViewById(R.id.layout_amout_preview);
        }
    }
}
