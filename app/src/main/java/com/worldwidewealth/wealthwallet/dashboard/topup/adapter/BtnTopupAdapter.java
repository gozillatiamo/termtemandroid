package com.worldwidewealth.wealthwallet.dashboard.topup.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentAirtimeVAS;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.wealthwallet.model.LoadButtonResponseModel;

import java.util.List;

/**
 * Created by user on 04-Jan-17.
 */

public class BtnTopupAdapter extends RecyclerView.Adapter<BtnTopupAdapter.ViewHolder> {

    private Context mContext;
    private Fragment mFragment;
    private List<LoadButtonResponseModel> mDataList;
    private int previousSelectedPosition = -1;

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

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (getItem(position).getPRODUCT_TYPE().equals("VAS")){
            String[] item = getItem(position).getPRODUCT_ITEM().split("/");
            holder.mTextProductItem.setText(item[0]);
            holder.mTextCurency.setText(item[1]);
        }else holder.mTextProductItem.setText(getItem(position).getPRODUCT_ITEM());

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

    private void setClickChoiceTopup(ViewHolder holder, int position){

        double nowAmt = 0;
        String buttonId = null;
        if (position != -1) {
            if (previousSelectedPosition == position) return;
            if (mFragment != null) {
                if (mFragment.getParentFragment() instanceof FragmentAirtimeVAS) {
                    FragmentAirtimeVAS fragmentAirtimeVAS = (FragmentAirtimeVAS) mFragment.getParentFragment();
                    holder.mTextProductItem.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    holder.mTextCurency.setTextColor(mContext.getResources().getColor(android.R.color.white));
                    holder.mBtnChoice.setCardBackgroundColor(mContext.getResources().getColor(fragmentAirtimeVAS.getsTabColor()));
                }
            }
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

        private TextView mTextProductItem, mTextCurency;
        private CardView mBtnChoice;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextProductItem = (TextView) itemView.findViewById(R.id.txt_product_item);
            mTextCurency = (TextView) itemView.findViewById(R.id.txt_currency);
            mBtnChoice = (CardView) itemView.findViewById(R.id.btn_choice);
        }
    }
}
