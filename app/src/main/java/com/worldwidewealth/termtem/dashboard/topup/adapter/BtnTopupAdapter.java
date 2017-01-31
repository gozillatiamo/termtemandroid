package com.worldwidewealth.termtem.dashboard.topup.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentAirtimeVAS;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentChoiceTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.LoadButtonResponseModel;
import com.worldwidewealth.termtem.until.Until;

import java.util.List;

/**
 * Created by user on 04-Jan-17.
 */

public class BtnTopupAdapter extends RecyclerView.Adapter<BtnTopupAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private List<LoadButtonResponseModel> mDataList;
    public int previousSelectedPosition = -1;
    public String TAG = BtnTopupAdapter.class.getSimpleName();
    private SparseBooleanArray selectedItems;

    public BtnTopupAdapter(Context mContext, List<LoadButtonResponseModel> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    public BtnTopupAdapter(Fragment mFragment, List<LoadButtonResponseModel> mDataList) {
        this.mFragment = mFragment;
        this.mContext = mFragment.getContext();
        this.mDataList = mDataList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_topup, parent, false);
        Until.setupUI(rootView);

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (getItem(position).getPRODUCT_TYPE().equals("VAS")){
            String[] item = getItem(position).getPRODUCT_ITEM().split("/");
            holder.mTextProductItem.setText(item[0]);
            holder.mTextCurency.setText(item[1]);

        }else holder.mTextProductItem.setText(getItem(position).getPRODUCT_ITEM());

        holder.mTextProductItem.setTextColor(mContext.getResources().getColor(android.R.color.tertiary_text_dark));
        holder.mTextCurency.setTextColor(mContext.getResources().getColor(android.R.color.tertiary_text_dark));
        holder.mBtnChoice.setCardBackgroundColor(mContext.getResources().getColor(android.R.color.white));

        if (position == previousSelectedPosition)
            setBackgroundSelect(holder, position);


        holder.mBtnChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickChoiceTopup(holder, position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public LoadButtonResponseModel getItem(int position){
        return mDataList.get(position);
    }

    private void setBackgroundSelect(ViewHolder holder, int position){
        if (mFragment != null) {

            if (mFragment.getParentFragment() instanceof FragmentAirtimeVAS) {
                FragmentAirtimeVAS fragmentAirtimeVAS = (FragmentAirtimeVAS) mFragment.getParentFragment();
                holder.mTextProductItem.setTextColor(mContext.getResources().getColor(android.R.color.white));
                holder.mTextCurency.setTextColor(mContext.getResources().getColor(android.R.color.white));
                holder.mBtnChoice.setCardBackgroundColor(mContext.getResources().getColor(fragmentAirtimeVAS.getsTabColor()));
            }


            if (previousSelectedPosition == position) return;

            if (mFragment instanceof FragmentChoiceTopup){
                ((FragmentChoiceTopup)mFragment).clearSelected();
            }
        }
    }

    private void setClickChoiceTopup(ViewHolder holder, int position){

        double nowAmt = 0;
        String buttonId = null;
        if (position != -1) {
            if (previousSelectedPosition == position) return;
            setBackgroundSelect(holder, position);
            LoadButtonResponseModel buttonResponseModel = getItem(position);
            nowAmt = buttonResponseModel.getPRODUCT_PRICE();
            buttonId = buttonResponseModel.getTXID();
        }

        if (mFragment != null) {
            Fragment fragment = mFragment.getParentFragment().getParentFragment();

            if (fragment instanceof FragmentTopupPackage) {

                ((FragmentTopupPackage) fragment).setAmt(nowAmt, buttonId);
            }
        }
//        clearSelected();

        previousSelectedPosition = position;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextProductItem, mTextCurency;
        public CardView mBtnChoice;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextProductItem = (TextView) itemView.findViewById(R.id.txt_product_item);
            mTextCurency = (TextView) itemView.findViewById(R.id.txt_currency);
            mBtnChoice = (CardView) itemView.findViewById(R.id.btn_choice);
        }
    }
}
