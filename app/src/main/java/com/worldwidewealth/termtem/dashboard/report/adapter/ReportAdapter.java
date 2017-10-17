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

public class ReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SalerptResponseModel> mModelList;
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_BILL = 1;
    private NumberFormat format;

    public ReportAdapter(Context mContext, List<SalerptResponseModel> modelList) {
        this.mContext = mContext;
        this.mModelList = modelList;

        format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

    }

    public void updateAll(List<SalerptResponseModel> modelList){
        this.mModelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getTYPE() == null)
            return TYPE_BILL;

        return TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;

        switch (viewType){
            case TYPE_BILL:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_report_bill, parent, false);
                return new BillViewHolder(rootView);
            default:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_report, parent, false);
                return new DefaultViewHolder(rootView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DefaultViewHolder){
            DefaultViewHolder defaultViewHolder = (DefaultViewHolder) holder;
            setupDefaultViewHolder(defaultViewHolder, position);
        } else {
            BillViewHolder billViewHolder = (BillViewHolder) holder;
            setupBillViewHolder(billViewHolder, position);
        }
    }

    private void setupDefaultViewHolder(DefaultViewHolder holder, int position){
        setPayCode(holder.mTextPayCode, position);
        setCheckTotal(holder.mTextCheckTotal, position);
        setDatePayment(holder.mTextDatePayment, position);
        setServicePrice(holder.mLayoutCommisstion, holder.mTextComAmount, getItem(position).getCOMM_AMT());
        setAmount(holder.mTextAmount, position);
        setPhoneNo(holder.mTextPhoneNum, position);

        switch (getItem(position).getTYPE()) {
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

        if (getItem(position).getTYPE() != null) {
            if (getItem(position).getTYPE().equals(ActivityReport.VAS_REPORT)) {
                holder.mTextTitleAmonut.setText(R.string.title_text_price_vas);
            } else {
                holder.mTextTitleAmonut.setText(R.string.amount);
            }
        }

    }

    private void setupBillViewHolder(BillViewHolder holder, int position){
        setPayCode(holder.mTextPayCode, position);
        setCheckTotal(holder.mTextCheckTotal, position);
        setDatePayment(holder.mTextDatePayment, position);
        setServicePrice(holder.mLayoutCommisstion, holder.mTextComAmount, getItem(position).getCOMM_AMT());
        setAmount(holder.mTextAmount, position);
        setPhoneNo(holder.mTextPhoneNum, position);

        holder.mTextBillName.setText(getItem(position).getBILLNAME());
        holder.mTextRef1.setText(getItem(position).getREF1());
        setServicePrice(holder.mLayoutFee, holder.mTextFee, getItem(position).getCALFEE());

    }

    private void setPayCode(TextView textView, int position){
        textView.setText(getItem(position).getPAYCODE());
    }

    private void setCheckTotal(TextView textView, int position){
        textView.setText(format.format(getItem(position).getCHECKTOTAL()));
    }

    private void setDatePayment(TextView textView, int position){
        Format formatter;
        formatter = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("th", "TH"));
        String strDate = formatter.format(getItem(position).getPAYMENT_DATE());
        textView.setText(strDate);
    }

    private void setServicePrice(View layout, TextView textView, double value){
        if (value == 0){
            layout.setVisibility(View.GONE);
        } else {
            textView.setText(format.format(value));
            layout.setVisibility(View.VISIBLE);
        }

    }

    private void setAmount(TextView textView, int position){
        textView.setText(format.format(getItem(position).getAMOUNT()));
    }

    private void setPhoneNo(TextView textView, int position){
        textView.setText(getItem(position).getPHONENO());

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

    public class DefaultViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextCheckTotal, mTextPayCode, mTextBiller, mTextPhoneNum, mTextAmount
                , mTextComAmount, mTextDatePayment, mTextTiltle, mTextAgentName, mTextTitleAmonut;
        private View mLayoutCommisstion, mLayoutBiller, mLayoutAgentName;
        public DefaultViewHolder(View itemView) {
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
            mTextTitleAmonut = (TextView) itemView.findViewById(R.id.title_amount_report);
        }
    }

    public class BillViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextCheckTotal, mTextPayCode, mTextPhoneNum, mTextAmount
                , mTextComAmount, mTextDatePayment, mTextBillName, mTextRef1, mTextFee;
        private View mLayoutCommisstion, mLayoutFee;

        public BillViewHolder(View itemView) {
            super(itemView);
            mTextAmount = (TextView) itemView.findViewById(R.id.txt_amount);
            mTextComAmount = (TextView) itemView.findViewById(R.id.txt_commission_amount);
            mTextDatePayment = (TextView) itemView.findViewById(R.id.txt_payment_date);
            mTextCheckTotal = (TextView) itemView.findViewById(R.id.txt_check_total);
            mTextBillName = itemView.findViewById(R.id.txt_bill_name);
            mTextRef1 = itemView.findViewById(R.id.txt_bill_ref_1);
            mTextFee = itemView.findViewById(R.id.txt_fee);
            mTextPayCode = (TextView) itemView.findViewById(R.id.txt_pay_code);
            mTextPhoneNum = (TextView) itemView.findViewById(R.id.txt_phone_number);
            mLayoutCommisstion = (View) itemView.findViewById(R.id.layout_commission);
            mLayoutFee = (View) itemView.findViewById(R.id.layout_fee);
        }
    }

}
