package com.worldwidewealth.wealthcounter.dashboard.report.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;

import java.text.NumberFormat;

/**
 * Created by MyNet on 21/11/2559.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private Context mContext;
    private String[] mArrayTitle = {
            "TopUp Airtime",
            "TopUp VAS",
            "TopUp VAS"
    };
    private int[] mArrayPrice = {
            100,
            1000,
            300
    };
    private String tel = "เบอร์: 08111111111";
    private String date = "11/12/16";
    private int[] mArrayIcon = {
            R.drawable.logo_ais,
            R.drawable.logo_truemove,
            R.drawable.logo_dtac
    };

    public ReportAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mIcon.setImageResource(mArrayIcon[position]);
        holder.mTitle.setText(mArrayTitle[position]);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        holder.mPrice.setText(format.format(mArrayPrice[position]));
        holder.mSubTitle.setText(tel);
        holder.mDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return mArrayTitle.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIcon;
        private TextView mTitle, mSubTitle, mPrice, mDate;
        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.icon_item);
            mTitle = (TextView) itemView.findViewById(R.id.title_item);
            mSubTitle = (TextView) itemView.findViewById(R.id.subtitle_item);
            mPrice = (TextView) itemView.findViewById(R.id.price_item);
            mDate = (TextView) itemView.findViewById(R.id.date_item);
        }
    }
}
