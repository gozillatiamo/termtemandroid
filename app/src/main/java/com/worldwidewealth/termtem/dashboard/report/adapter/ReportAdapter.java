package com.worldwidewealth.termtem.dashboard.report.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.model.SalerptResponseModel;

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
        holder.mTextCheckTotal.setText(format.format(getItem(position).getCHECKTOTAL()));
        Format formatter;
        formatter = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("th", "TH"));
        String strDate = formatter.format(getItem(position).getPAYMENT_DATE());
        holder.mTextDatePayment.setText(strDate);
        if (getItem(position).getCOMM_AMT() == 0){
            holder.mLayoutCommisstion.setVisibility(View.GONE);
        } else {
            holder.mTextComAmount.setText(format.format(getItem(position).getCOMM_AMT()));
            holder.mLayoutCommisstion.setVisibility(View.VISIBLE);
        }
        switch (getItem(position).getTYPE()){
            case ActivityReport.CASHIN_REPORT:
                holder.mTextTiltle.setText(mContext.getString(R.string.title_report_cashin));
                holder.mLayoutBiller.setVisibility(View.GONE);
                holder.mTextAgentName.setText(getItem(position).getAGENTNAME());
                holder.mLayoutAgentName.setVisibility(View.VISIBLE);
                break;
            default:
                holder.mTextBiller.setText(getItem(position).getBILLER());
                holder.mLayoutBiller.setVisibility(View.VISIBLE);
                holder.mLayoutAgentName.setVisibility(View.GONE);
                holder.mTextTiltle.setText(mContext.getString(R.string.topup) + " " + getItem(position).getTYPE());
        }
        holder.mTextAmount.setText(format.format(getItem(position).getAMOUNT()));
        holder.mTextPhoneNum.setText(getItem(position).getPHONENO());
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
        private TextView mTextCheckTotal, mTextPayCode, mTextBiller, mTextPhoneNum, mTextAmount
                , mTextComAmount, mTextDatePayment, mTextTiltle, mTextAgentName;
        private View mLayoutCommisstion, mLayoutBiller, mLayoutAgentName;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextAmount = (TextView) itemView.findViewById(R.id.txt_amount);
            mTextBiller = (TextView) itemView.findViewById(R.id.txt_biller);
            mTextComAmount = (TextView) itemView.findViewById(R.id.txt_commission_amount);
            mTextDatePayment = (TextView) itemView.findViewById(R.id.txt_payment_date);
            mTextCheckTotal = (TextView) itemView.findViewById(R.id.txt_check_total);
            mTextPayCode = (TextView) itemView.findViewById(R.id.txt_pay_code);
            mTextPhoneNum = (TextView) itemView.findViewById(R.id.txt_phone_number);
            mTextTiltle = (TextView) itemView.findViewById(R.id.title_item);
            mTextAgentName = (TextView) itemView.findViewById(R.id.txt_agent_name);
            mLayoutCommisstion = (View) itemView.findViewById(R.id.layout_commission);
            mLayoutBiller = (View) itemView.findViewById(R.id.layout_biller);
            mLayoutAgentName = (View) itemView.findViewById(R.id.layout_agent_name);
        }
    }
}
