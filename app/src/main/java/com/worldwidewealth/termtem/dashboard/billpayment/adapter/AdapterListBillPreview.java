package com.worldwidewealth.termtem.dashboard.billpayment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.worldwidewealth.termtem.R;

/**
 * Created by MyNet on 28/10/2559.
 */

public class AdapterListBillPreview extends BaseAdapter{

    private Context mContext;
    private View rootview;
    public AdapterListBillPreview(Context context){
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 3;
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
            rootview = LayoutInflater.from(mContext).inflate(R.layout.item_list_onebill, null);
        }
        return rootview;
    }
}
