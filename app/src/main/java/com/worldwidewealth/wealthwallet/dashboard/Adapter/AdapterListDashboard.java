package com.worldwidewealth.wealthwallet.dashboard.Adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by gozillatiamo on 10/4/16.
 */
public class AdapterListDashboard extends RecyclerView.Adapter<AdapterListDashboard.ViewHolder> {

    private Context mContext;
    private String[] mTextList;
    private TypedArray mIconList;
    private int[] mColorList;

    public AdapterListDashboard(Context context) {

        this.mContext = context;
        mTextList = mContext.getResources().getStringArray(R.array.str_list_dashboard);
        mIconList = mContext.getResources().obtainTypedArray(R.array.ic_list_dashboard);
        mColorList = mContext.getResources().getIntArray(R.array.color_list_dashboard);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.column_list_dashboard, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mIcon.setImageResource(mIconList.getResourceId(position, -1));
        holder.mIcon.setColorFilter(mColorList[position]);

        holder.mTitle.setText(mTextList[position]);
        holder.mTitle.setTextColor(mColorList[position]);

        holder.mCoins.setTextColor(mColorList[position]);
    }

    @Override
    public int getItemCount() {
        return mTextList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIcon;
        private TextView mTitle, mCoins;

        public ViewHolder(View itemView) {
            super(itemView);

            mIcon = (ImageView) itemView.findViewById(R.id.ic_list);
            mTitle = (TextView) itemView.findViewById(R.id.title_list);
            mCoins = (TextView) itemView.findViewById(R.id.text_coins);
        }
    }
}
