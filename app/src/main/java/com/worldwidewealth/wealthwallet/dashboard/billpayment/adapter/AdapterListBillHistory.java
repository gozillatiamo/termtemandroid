package com.worldwidewealth.wealthwallet.dashboard.billpayment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by MyNet on 28/10/2559.
 */

public class AdapterListBillHistory extends BaseAdapter{

    private Context mContext;
    private View rootview;
    public AdapterListBillHistory(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            rootview = LayoutInflater.from(mContext).inflate(R.layout.item_list_bill_history, null);
        }
        return rootview;
    }
}
