package com.worldwidewealth.termtem.dashboard.billpayment.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.LoadBillCategoryResponse;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.widgets.MenuButtonView;

import java.util.ArrayList;

/**
 * Created by gozillatiamo on 7/4/17.
 */

public class BillPayServiceAdapter extends RecyclerView.Adapter<BillPayServiceAdapter.ViewHolder> {

    private Fragment mFragment;
    private ArrayList<LoadBillServiceResponse> mListData;



    public BillPayServiceAdapter(Fragment fragment, ArrayList<LoadBillServiceResponse> listdata) {
        this.mFragment = fragment;
        mListData = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new MenuButtonView(mFragment.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LoadBillServiceResponse response = getItem(position);
        MenuButtonView menuButtonView = (MenuButtonView) holder.itemView;
        menuButtonView.setTitle(response.getBILL_SERVICE_NAME());
//        TypedArray typedArrayIcon = mContext.getResources().obtainTypedArray(R.array.list_icon_main_bill);
        Glide.with(mFragment).load(mFragment.getString(R.string.server)+response.getLOGOURL())
                .placeholder(new ColorDrawable(Color.parseColor("#FFFFFF")))
                .thumbnail(0.6f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .crossFade()
                .into(menuButtonView.getImageView());
/*
        menuButtonView.setIcon(typedArrayIcon.getResourceId(position, -1));
        typedArrayIcon.recycle();
*/
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public LoadBillServiceResponse getItem(int position){
        return mListData.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
