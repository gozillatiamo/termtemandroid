package com.worldwidewealth.wealthcounter.dashboard.billpayment.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gozillatiamo on 10/21/16.
 */

public class AdapterBillBox extends BaseAdapter {

    private Context mContext;
    private List<ContentValues> mListData;
    private View rootview;
    private ViewHolder mHolder;
    private LayoutInflater inflater;
    private SparseBooleanArray mSelectItemIds;
    private boolean mActionMode = false;

    public AdapterBillBox(Context context , List<ContentValues> list){
        this.mContext = context;
        this.mListData = list;
        inflater = LayoutInflater.from(context);
        mSelectItemIds = new SparseBooleanArray();
    }
    @Override
    public int getCount() {
        return mListData.size();
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
        initCheckMode(i);
        return rootview;
    }

    private void initList(int position){
        mHolder.mTextType.setText(getItem(position).getAsString("type"));
        mHolder.mTextExp.setText(getItem(position).getAsString("exp"));
        mHolder.mTextStatus.setText(getItem(position).getAsString("status"));
        mHolder.mIcBill.setImageResource(getItem(position).getAsInteger("icon"));

        String status = getItem(position).getAsString("status");
        if (status.equals(mContext.getString(R.string.wait_pay)))
            mHolder.mTextStatus.setBackgroundResource(R.drawable.background_status_warning);
        else if (status.equals(mContext.getString(R.string.paid)))
            mHolder.mTextStatus.setBackgroundResource(R.drawable.background_status_success);
        else if (status.equals(mContext.getString(R.string.owe)))
            mHolder.mTextStatus.setBackgroundResource(R.drawable.background_status_danger);


        mHolder.mTextPrice.setText(getItem(position).getAsString("price"));
    }

    private void initCheckMode(int position){
        if (mActionMode)
            mHolder.mCheckbox.setVisibility(View.VISIBLE);
        else
            mHolder.mCheckbox.setVisibility(View.GONE);

        mHolder.mCheckbox.setChecked(mSelectItemIds.get(position));
    }

    public void toggleMode(){
        this.mActionMode = !mActionMode;
    }

    public void toggleSelection(int position){
        selectView(position, !mSelectItemIds.get(position));
    }

    public void removeSelection(){
        mSelectItemIds = new SparseBooleanArray();
        toggleMode();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value){
        if (value)
            mSelectItemIds.put(position, value);
        else
            mSelectItemIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount(){
        return mSelectItemIds.size();
    }

    public SparseBooleanArray getSelectedIds(){
        return mSelectItemIds;
    }


    public class ViewHolder{
        private TextView mTextType, mTextExp, mTextStatus, mTextPrice;
        private ImageView mIcBill;
        private CheckBox mCheckbox;
        public ViewHolder(View itemview){
            mTextType = (TextView) itemview.findViewById(R.id.txt_type);
            mTextExp = (TextView) itemview.findViewById(R.id.txt_exp);
            mTextStatus = (TextView) itemview.findViewById(R.id.txt_status);
            mTextPrice = (TextView) itemview.findViewById(R.id.txt_price);
            mIcBill = (ImageView) itemview.findViewById(R.id.ic_bill);
            mCheckbox = (CheckBox) itemview.findViewById(R.id.checkbox);
        }
    }
}
