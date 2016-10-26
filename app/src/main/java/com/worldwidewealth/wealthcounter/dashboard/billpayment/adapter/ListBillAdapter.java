package com.worldwidewealth.wealthcounter.dashboard.billpayment.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gozillatiamo on 10/21/16.
 */

public class ListBillAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mTypeBill, mExpBill, mStatusBill, mPriceBill;
    private List<ContentValues> mListData;
    private View rootview;
    private ViewHolder mHolder;
    private static LayoutInflater inflater = null;

    public ListBillAdapter(Context context){
        this.mContext = context;
        inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTypeBill = mContext.getResources().getStringArray(R.array.list_type_bill);
        this.mExpBill = mContext.getResources().getStringArray(R.array.list_exp_bill);
        this.mStatusBill = mContext.getResources().getStringArray(R.array.list_status_bill);
        this.mPriceBill = mContext.getResources().getStringArray(R.array.list_price_bill);
        mListData = new ArrayList<>();

        for (int i = 0; i < mTypeBill.length; i++){
            ContentValues values = new ContentValues();
            values.put("type", mTypeBill[i]);
            values.put("exp", mExpBill[i]);
            values.put("status", mStatusBill[i]);
            values.put("price", mPriceBill[i]);
            mListData.add(values);
        }
    }
    @Override
    public int getCount() {
        return mTypeBill.length;
    }

    @Override
    public ContentValues getItem(int i) {
        return mListData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        rootview = view;
        if (rootview == null){
            rootview = inflater.inflate(R.layout.item_list_bill, null);
            mHolder = new ViewHolder(rootview);
            rootview.setTag(mHolder);
        }else  mHolder = (ViewHolder) rootview.getTag();

        initList(i);
        return rootview;
    }

    private void initList(int position){
        mHolder.mTextType.setText(getItem(position).getAsString("type"));
        mHolder.mTextExp.setText(getItem(position).getAsString("exp"));
        mHolder.mTextStatus.setText(getItem(position).getAsString("status"));
        switch (getItem(position).getAsString("status")){
            case mContext.getrgetString(R.string.wait_pay):
                break;

        }
        mHolder.mTextPrice.setText(getItem(position).getAsString("price"));
    }

    public class ViewHolder{
        private TextView mTextType, mTextExp, mTextStatus, mTextPrice;
        public ViewHolder(View itemview){
            mTextType = (TextView) itemview.findViewById(R.id.txt_type);
            mTextExp = (TextView) itemview.findViewById(R.id.txt_exp);
            mTextStatus = (TextView) itemview.findViewById(R.id.txt_status);
            mTextPrice = (TextView) itemview.findViewById(R.id.txt_price);
        }
    }
}
