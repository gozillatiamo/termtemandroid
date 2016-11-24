package com.worldwidewealth.wealthcounter.dashboard.report.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.model.SalerptResponseModel;

import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by MyNet on 21/11/2559.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private Context mContext;
    private List<SalerptResponseModel> mModelList;

    public ReportAdapter(Context mContext, List<SalerptResponseModel> modelList) {
        this.mContext = mContext;
        this.mModelList = modelList;
    }

    public void updateAll(List<SalerptResponseModel> modelList){
        this.mModelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_report, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        holder.mTextPayCode.setText(getItem(position).getPAYCODE());
        holder.mTextGrantTotal.setText(format.format(getItem(position).getGRANTTOTAL()));
        Format formatter;
        formatter = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("th", "TH"));
        String strDate = formatter.format(getItem(position).getPAYMENT_DATE());
        holder.mTextDatePayment.setText(strDate);
        holder.mTextComRate.setText(mContext.getString(R.string.commission) + "  " +
                getItem(position).getCOMM_RATE() + "% :");
        holder.mTextComAmount.setText(format.format(getItem(position).getCOMM_AMT()));
        holder.mTextAmount.setText(format.format(getItem(position).getAMOUNT()));
    }

    @Override
    public int getItemCount() {
        if (mModelList != null) {
            return mModelList.size();
        } else {
            return 0;
        }
    }

    public SalerptResponseModel getItem(int position){
        return mModelList.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextGrantTotal, mTextPayCode, mTextCarrier, mTextPhoneNum, mTextAmount
                , mTextComRate, mTextComAmount, mTextDatePayment;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextAmount = (TextView) itemView.findViewById(R.id.txt_amount);
            mTextCarrier = (TextView) itemView.findViewById(R.id.txt_carrier);
            mTextComAmount = (TextView) itemView.findViewById(R.id.txt_commission_amount);
            mTextComRate = (TextView) itemView.findViewById(R.id.txt_commission_rate);
            mTextDatePayment = (TextView) itemView.findViewById(R.id.txt_payment_date);
            mTextGrantTotal = (TextView) itemView.findViewById(R.id.txt_grant_total);
            mTextPayCode = (TextView) itemView.findViewById(R.id.txt_pay_code);
            mTextPhoneNum = (TextView) itemView.findViewById(R.id.txt_phone_number);
        }
    }
}
