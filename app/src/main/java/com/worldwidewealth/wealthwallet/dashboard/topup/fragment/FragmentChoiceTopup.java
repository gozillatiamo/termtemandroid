package com.worldwidewealth.wealthwallet.dashboard.topup.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.worldwidewealth.wealthwallet.dashboard.topup.adapter.BtnTopupAdapter;
import com.worldwidewealth.wealthwallet.model.LoadButtonResponseModel;
import com.worldwidewealth.wealthwallet.until.Until;

import java.util.List;

/**
 * Created by MyNet on 14/11/2559.
 */

public class FragmentChoiceTopup extends Fragment{

    private static List<LoadButtonResponseModel> mDataList;
    private RecyclerView mRecyclerBtnTopup;
    private BtnTopupAdapter mAdapter;

    public static FragmentChoiceTopup newInstance(List datalis) {

        Bundle args = new Bundle();
        FragmentChoiceTopup fragment = new FragmentChoiceTopup(datalis);
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

        mRecyclerBtnTopup = new RecyclerView(getActivity());
        initGrid();
        Until.setupUI(mRecyclerBtnTopup);
        return mRecyclerBtnTopup;
    }

    @Override
    public void onResume() {
        super.onResume();
        clearSelected();
        mAdapter.previousSelectedPosition = -1;
        Fragment fragment = getParentFragment().getParentFragment();

        if (fragment instanceof FragmentTopupPackage) {

            ((FragmentTopupPackage) fragment).setAmt(0, null);

        }

    }

    private void initGrid(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerBtnTopup.setLayoutManager(gridLayoutManager);
        mAdapter = new BtnTopupAdapter(FragmentChoiceTopup.this, mDataList);
        mRecyclerBtnTopup.setAdapter(mAdapter);
    }


    public void clearSelected(){

        if (mAdapter.previousSelectedPosition != -1){
            BtnTopupAdapter.ViewHolder holder = (BtnTopupAdapter.ViewHolder)
                    mRecyclerBtnTopup.findViewHolderForAdapterPosition(mAdapter.previousSelectedPosition);
            if (holder == null) return;
            holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mBtnChoice.setCardBackgroundColor(getResources().getColor(android.R.color.white));
//            mAdapter.previousSelectedPosition = -1;
        }

    }

}
