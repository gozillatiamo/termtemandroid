package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.adapter.BtnTopupAdapter;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.LoadButtonResponseModel;
import com.worldwidewealth.termtem.util.Util;

import java.util.List;

/**
 * Created by MyNet on 14/11/2559.
 */

public class FragmentChoiceTopup extends Fragment{

    private static List<LoadButtonResponseModel> mDataList;
    private RecyclerView mRecyclerBtnTopup;
    private BtnTopupAdapter mAdapter;
//    private int mDefaultPostionAmt = -1;

    public static final String TAG = FragmentChoiceTopup.class.getSimpleName();

    public static final String KEY_DEFAULT_AMT = "defaultamt";

    public static FragmentChoiceTopup newInstance(List datalis, int defaultPostionAmt) {

        Bundle args = new Bundle();
        FragmentChoiceTopup fragment = new FragmentChoiceTopup(datalis);
        args.putInt(KEY_DEFAULT_AMT, defaultPostionAmt);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentChoiceTopup() {
    }

    @SuppressLint("ValidFragment")
    public FragmentChoiceTopup(List datalist){
        this.mDataList = datalist;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getParentFragment() instanceof FragmentTopupPackage){
            ((FragmentTopupPackage)getParentFragment()).setEnableEditPhone(true);
        }

        mRecyclerBtnTopup = new RecyclerView(getActivity());
        mRecyclerBtnTopup.setScrollbarFadingEnabled(true);
        initGrid();
        Util.setupUI(mRecyclerBtnTopup);
        return mRecyclerBtnTopup;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments().getInt(KEY_DEFAULT_AMT) == -1) {
            mAdapter.previousSelectedPosition = -1;
            clearSelected();

            Fragment fragment = getParentFragment();

            if (fragment instanceof FragmentTopupPackage) {

                ((FragmentTopupPackage) fragment).setAmt(0, null);

            }
        }

    }

    private void initGrid(){
        if (mDataList == null) return;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerBtnTopup.setLayoutManager(gridLayoutManager);
        mAdapter = new BtnTopupAdapter(FragmentChoiceTopup.this, mDataList);
        mRecyclerBtnTopup.setAdapter(mAdapter);
        mRecyclerBtnTopup.setItemViewCacheSize(mDataList.size());
        if (getArguments().getInt(KEY_DEFAULT_AMT) > -1) {
            mAdapter.setClickChoiceTopup(getArguments().getInt(KEY_DEFAULT_AMT));
/*
            mAdapter.notifyDataSetChanged();
            Log.e(TAG, "postion: "+mAdapter.previousSelectedPosition);
            Log.e(TAG, "Amt: "+ ((FragmentTopupPackage) getParentFragment()).getmAmt());
*/
        }
    }


    public void clearSelected(){

        if (mAdapter.previousSelectedPosition != -1){
            mAdapter.notifyDataSetChanged();
/*
            BtnTopupAdapter.ViewHolder holder = (BtnTopupAdapter.ViewHolder)
                    mRecyclerBtnTopup.findViewHolderForAdapterPosition(mAdapter.previousSelectedPosition);
            if (holder == null) return;
*/
/*
            holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mBtnChoice.setCardBackgroundColor(Color.parseColor("#f5f5f5"));
*/
//            mAdapter.previousSelectedPosition = -1;
        }

    }

}
