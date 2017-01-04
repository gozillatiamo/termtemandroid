package com.worldwidewealth.wealthwallet.dashboard.topup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.topup.adapter.BtnTopupAdapter;
import com.worldwidewealth.wealthwallet.model.LoadButtonResponseModel;
import com.worldwidewealth.wealthwallet.until.Until;

import java.util.List;

/**
 * Created by MyNet on 14/11/2559.
 */

public class FragmentChoiceTopup extends Fragment{

    private GridView mGrid;
    private int previousSelectedPosition = -1;
    private List<LoadButtonResponseModel> mDataList;
    private TextView textProductItem = null;
    private TextView textCurrency = null;
    private CardView mBtnChoice;
    private RecyclerView mRecyclerBtnTopup;


    public FragmentChoiceTopup(List datalist){
        this.mDataList = datalist;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mGrid = new GridView(getActivity());
        mRecyclerBtnTopup = new RecyclerView(getActivity());
        initGrid();
        Until.setupUI(mGrid);
        return mRecyclerBtnTopup;
    }

    @Override
    public void onResume() {
        super.onResume();
//        clearSelected();
        previousSelectedPosition = -1;
        Fragment fragment = getParentFragment().getParentFragment();

        if (fragment instanceof FragmentTopupPackage) {

            ((FragmentTopupPackage) fragment).setAmt(0, null);

        }

    }

    private void initGrid(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerBtnTopup.setLayoutManager(gridLayoutManager);
        BtnTopupAdapter adapter = new BtnTopupAdapter(FragmentChoiceTopup.this, mDataList);
        mRecyclerBtnTopup.setAdapter(adapter);
/*
        mGrid.setNumColumns(3);
        mGrid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public LoadButtonResponseModel getItem(int position) {
                return mDataList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_topup, parent, false);

                TextView textProductItem = (TextView) convertView.findViewById(R.id.txt_product_item);
                TextView textCurency = (TextView) convertView.findViewById(R.id.txt_currency);
                if (getItem(position).getPRODUCT_TYPE().equals("VAS")){
                    String[] item = getItem(position).getPRODUCT_ITEM().split("/");
                    textProductItem.setText(item[0]);
                    textCurency.setText(item[1]);
                }else textProductItem.setText(getItem(position).getPRODUCT_ITEM());
                return convertView;
            }
        });

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setClickChoiceTopup(parent, view, position);
            }
        });

*/
    }

    private void setClickChoiceTopup(AdapterView parent, View view, int position){

        double nowAmt = 0;
        String buttonId = null;
        if (position != -1) {
            if (previousSelectedPosition == position) return;
            textProductItem = (TextView) view.findViewById(R.id.txt_product_item);
            textCurrency = (TextView) view.findViewById(R.id.txt_currency);
            mBtnChoice = (CardView) view.findViewById(R.id.btn_choice);

            if (getParentFragment() instanceof FragmentAirtimeVAS) {
                FragmentAirtimeVAS fragmentAirtimeVAS = (FragmentAirtimeVAS)getParentFragment();
                textProductItem.setTextColor(getResources().getColor(android.R.color.white));
                textCurrency.setTextColor(getResources().getColor(android.R.color.white));
                mBtnChoice.setCardBackgroundColor(getResources().getColor(fragmentAirtimeVAS.getsTabColor()));
            }
            LoadButtonResponseModel buttonResponseModel = (LoadButtonResponseModel) parent.getItemAtPosition(position);
            nowAmt = buttonResponseModel.getPRODUCT_PRICE();
            buttonId = buttonResponseModel.getTXID();
        }

        Fragment fragment = getParentFragment().getParentFragment();

        if (fragment instanceof FragmentTopupPackage) {

            ((FragmentTopupPackage) fragment).setAmt(nowAmt, buttonId);
        }

        clearSelected();

        previousSelectedPosition = position;

    }

    public void clearSelected(){

        Log.e("previousSelected", previousSelectedPosition+"");
        if (previousSelectedPosition != -1){
            View previousSelectedView = mGrid.getChildAt(previousSelectedPosition);
            textProductItem = (TextView) previousSelectedView.findViewById(R.id.txt_product_item);
            textCurrency = (TextView) previousSelectedView.findViewById(R.id.txt_currency);
            mBtnChoice = (CardView) previousSelectedView.findViewById(R.id.btn_choice);


            textProductItem.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            textCurrency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            mBtnChoice.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            previousSelectedPosition = -1;
        }

    }

}
