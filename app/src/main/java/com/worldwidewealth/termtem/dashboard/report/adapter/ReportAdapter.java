package com.worldwidewealth.termtem.dashboard.report.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dialog.PopupChoiceBank;
import com.worldwidewealth.termtem.model.ReportFundinResponse;
import com.worldwidewealth.termtem.model.SalerptResponseModel;
import com.worldwidewealth.termtem.model.TopupPreviewResponseModel;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by MyNet on 21/11/2559.
 */

public class ReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List mModelList;
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_BILL = 1;
    private static final int TYPE_FUNDIN = 2;
    private NumberFormat format;
    private static String mCurrentType;
    private static List<ContentValues> mListBankStart, mListBankEnd;

    private String[] mListNameBank;
    private String[] mListCodeBank;
    private TypedArray mLisIconBank;


    public ReportAdapter(Context mContext, List<SalerptResponseModel> modelList) {
        this.mContext = mContext;
        this.mModelList = modelList;

        format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

    }

    public void updateAll(List modelList){
        clear();

        this.mModelList = modelList;
        notifyDataSetChanged();
    }

    public void clear() {
        if (this.mModelList == null || this.mModelList.size() == 0) return;
        int size = this.mModelList.size();
        this.mModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void setCurrentType(String type){
        this.mCurrentType = type;
        if (mCurrentType.equals(ActivityReport.FUNDIN_REPORT)) bindListBank();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mCurrentType){
            case ActivityReport.BILL_REPORT:
                return TYPE_BILL;
            case ActivityReport.FUNDIN_REPORT:
                return TYPE_FUNDIN;
            default:
                return TYPE_DEFAULT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;

        switch (viewType){
            case TYPE_BILL:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_report_bill, parent, false);
                return new BillViewHolder(rootView);
            case TYPE_FUNDIN:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_report_fundin, parent, false);
                return new FundinViewHolder(rootView);
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
        } else if (holder instanceof BillViewHolder){
            BillViewHolder billViewHolder = (BillViewHolder) holder;
            setupBillViewHolder(billViewHolder, position);
        } else if (holder instanceof FundinViewHolder){
            FundinViewHolder fundinViewHolder = (FundinViewHolder) holder;
            setupFundinViewHolder(fundinViewHolder, position);
        }
    }

    private void setupFundinViewHolder(FundinViewHolder holder, int position){
        ReportFundinResponse response = (ReportFundinResponse) getItem(position);

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(response.getCREATED_DATE());

        holder.mTextAmount.setText(format.format(response.getCREDIT()));
        holder.mTextDate.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"
                +(calendar.get(Calendar.MONTH)+1)+"/"+(calendar.get(Calendar.YEAR)+543));
        holder.mTextTime.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));

        setTypePayment(holder, response);
    }

    private void setTypePayment(FundinViewHolder holder, ReportFundinResponse response){
        String type = response.getPAYMENT_TYPE();

        switch (type){
            case ReportFundinResponse.BANK:

                String[] bank = response.getBank().split(" - ");

                holder.mTextStartBank.setText(bank[0]);
                holder.mTextEndBank.setText(bank[1]);
                for (ContentValues values : mListBankStart){
                    if (values.getAsString(PopupChoiceBank.KEY_CODE).equals(bank[0])){
                        holder.mIconStartBank.setImageResource(values.getAsInteger(PopupChoiceBank.KEY_ICON));
                    }
                }

                for (ContentValues values : mListBankEnd){
                    if (values.getAsString(PopupChoiceBank.KEY_CODE).equals(bank[1])){
                        holder.mIconEndBank.setImageResource(values.getAsInteger(PopupChoiceBank.KEY_ICON));
                    }
                }

                holder.mLayoutStartBank.setVisibility(View.VISIBLE);
                holder.mLayoutEndBank.setVisibility(View.VISIBLE);

                holder.mTextTypePayment.setText(R.string.type_bank_transfer);
                break;
            case ReportFundinResponse.AGENT:
                holder.mTextTypePayment.setText(R.string.type_agent_cashin);
                break;
            case ReportFundinResponse.REFUND:
                holder.mTextTypePayment.setText(R.string.type_refund);
                break;
            case ReportFundinResponse.MPAY:
                holder.mTextTypePayment.setText(R.string.type_mpay);
                break;
            case ReportFundinResponse.BONUS:
                holder.mTextTypePayment.setText(R.string.type_bonus);
                break;
        }
    }

    private void bindListBank(){
        mListNameBank = mContext.getResources().getStringArray(R.array.list_name_bank_start);
        mListCodeBank = mContext.getResources().getStringArray(R.array.list_code_bank_start);
        mLisIconBank = mContext.getResources().obtainTypedArray(R.array.ic_list_bank_start);

        mListBankStart = new ArrayList<>();
        mListBankEnd = new ArrayList<>();

        for (int i = 0; i < mListNameBank.length; i++){
            ContentValues values = new ContentValues();
            values.put(PopupChoiceBank.KEY_NAME, mListNameBank[i]);
            values.put(PopupChoiceBank.KEY_ICON, mLisIconBank.getResourceId(i, -1));
            values.put(PopupChoiceBank.KEY_CODE, mListCodeBank[i]);
            mListBankStart.add(values);
        }

        mListNameBank = mContext.getResources().getStringArray(R.array.list_name_bank_end);
        mListCodeBank = mContext.getResources().getStringArray(R.array.list_code_bank_end);
        mLisIconBank = mContext.getResources().obtainTypedArray(R.array.ic_list_bank_end);

        for (int i = 0; i < mListNameBank.length; i++){
            ContentValues values = new ContentValues();
            values.put(PopupChoiceBank.KEY_NAME, mListNameBank[i]);
            values.put(PopupChoiceBank.KEY_ICON, mLisIconBank.getResourceId(i, -1));
            values.put(PopupChoiceBank.KEY_CODE, mListCodeBank[i]);
            mListBankEnd.add(values);
        }



    }


    private void setupDefaultViewHolder(DefaultViewHolder holder, int position){
        SalerptResponseModel model = (SalerptResponseModel) getItem(position);
        setPayCode(holder.mTextPayCode, position);
        setCheckTotal(holder.mTextCheckTotal, position);
        setDatePayment(holder.mTextDatePayment, position);
        setServicePrice(holder.mLayoutCommisstion, holder.mTextComAmount, model.getCOMM_AMT());
        setAmount(holder.mTextAmount, position);
        setPhoneNo(holder.mTextPhoneNum, position);

        switch (model.getTYPE()) {
            case ActivityReport.CASHIN_REPORT:
                holder.mTextTiltle.setText(mContext.getString(R.string.title_report_cashin));
                holder.mLayoutBiller.setVisibility(View.GONE);
                holder.mTextAgentName.setText(model.getAGENTNAME());
                holder.mLayoutAgentName.setVisibility(View.VISIBLE);
                break;
            default:
                holder.mTextBiller.setText(model.getBILLER());
                holder.mLayoutBiller.setVisibility(View.VISIBLE);
                holder.mLayoutAgentName.setVisibility(View.GONE);
                holder.mTextTiltle.setText(mContext.getString(R.string.topup) + " " + model.getTYPE());
        }

        if (model.getTYPE() != null) {
            if (model.getTYPE().equals(ActivityReport.VAS_REPORT)) {
                holder.mTextTitleAmonut.setText(R.string.title_text_price_vas);
            } else {
                holder.mTextTitleAmonut.setText(R.string.amount);
            }
        }

    }

    private void setupBillViewHolder(BillViewHolder holder, int position){
        SalerptResponseModel model = (SalerptResponseModel) getItem(position);

        setPayCode(holder.mTextPayCode, position);
        setCheckTotal(holder.mTextCheckTotal, position);
        setDatePayment(holder.mTextDatePayment, position);
        setServicePrice(holder.mLayoutCommisstion, holder.mTextComAmount, model.getCOMM_AMT());
        setAmount(holder.mTextAmount, position);
        setPhoneNo(holder.mTextPhoneNum, position);

        holder.mTextBillName.setText(model.getBILLNAME());
        if (model.getREF() != null) {
            List<TopupPreviewResponseModel.RefModel> refModels = model.getREF();
            Collections.sort(refModels);
            int positionItem = 2;
            holder.mLayoutItemBill.removeAllViews();
            for (TopupPreviewResponseModel.RefModel refModel : refModels) {
                setBillRef(holder, positionItem, refModel);
                positionItem++;
            }

        }
        setServicePrice(holder.mLayoutFee, holder.mTextFee, model.getCALFEE());

    }

    private void setBillRef(BillViewHolder holder, int position, TopupPreviewResponseModel.RefModel model){
        View itemRef = View.inflate(mContext, R.layout.item_report_bill_ref, null);
        TextView textTitle = itemRef.findViewById(R.id.text_title_ref);
        TextView textRef = itemRef.findViewById(R.id.text_ref);

        textTitle.setText(model.getREF_NAME()+" :");
        textRef.setText(model.getREF_VALUE());

        holder.mLayoutItemBill.addView(itemRef);

    }

    private void setPayCode(TextView textView, int position){
        SalerptResponseModel model = (SalerptResponseModel) getItem(position);

        textView.setText(model.getPAYCODE());
    }

    private void setCheckTotal(TextView textView, int position){
        SalerptResponseModel model = (SalerptResponseModel) getItem(position);

        textView.setText(format.format(model.getCHECKTOTAL()));
    }

    private void setDatePayment(TextView textView, int position){
        SalerptResponseModel model = (SalerptResponseModel) getItem(position);

        Format formatter;
        formatter = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("th", "TH"));
        String strDate = formatter.format(model.getPAYMENT_DATE());
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
        SalerptResponseModel model = (SalerptResponseModel) getItem(position);

        textView.setText(format.format(model.getAMOUNT()));
    }

    private void setPhoneNo(TextView textView, int position){
        SalerptResponseModel model = (SalerptResponseModel) getItem(position);

        textView.setText(model.getPHONENO());

    }

    @Override
    public int getItemCount() {
        if (mModelList != null) {
            return mModelList.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int position){
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
                , mTextComAmount, mTextDatePayment, mTextBillName, mTextFee;
        private View mLayoutCommisstion, mLayoutFee;
        private LinearLayout mLayoutItemBill;

        public BillViewHolder(View itemView) {
            super(itemView);
            mTextAmount = (TextView) itemView.findViewById(R.id.txt_amount);
            mTextComAmount = (TextView) itemView.findViewById(R.id.txt_commission_amount);
            mTextDatePayment = (TextView) itemView.findViewById(R.id.txt_payment_date);
            mTextCheckTotal = (TextView) itemView.findViewById(R.id.txt_check_total);
            mTextBillName = itemView.findViewById(R.id.txt_bill_name);
            mTextFee = itemView.findViewById(R.id.txt_fee);
            mTextPayCode = (TextView) itemView.findViewById(R.id.txt_pay_code);
            mTextPhoneNum = (TextView) itemView.findViewById(R.id.txt_phone_number);
            mLayoutCommisstion = (View) itemView.findViewById(R.id.layout_commission);
            mLayoutFee = (View) itemView.findViewById(R.id.layout_fee);
            mLayoutItemBill = itemView.findViewById(R.id.layout_item_bill);
        }
    }

    public class FundinViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextAmount, mTextDate, mTextTime, mTextStartBank, mTextEndBank, mTextTypePayment;
        private View mLayoutStartBank, mLayoutEndBank;
        private ImageView mIconStartBank, mIconEndBank;
        public FundinViewHolder(View itemView) {
            super(itemView);

            mTextAmount = itemView.findViewById(R.id.txt_amount_fundin);
            mTextDate = itemView.findViewById(R.id.txt_date_fundin);
            mTextTime = itemView.findViewById(R.id.txt_time_fundin);
            mTextStartBank = itemView.findViewById(R.id.txt_bank_start);
            mTextEndBank = itemView.findViewById(R.id.txt_bank_end);

            mLayoutStartBank = itemView.findViewById(R.id.layout_bank_start);
            mLayoutEndBank = itemView.findViewById(R.id.layout_bank_end);

            mIconStartBank = itemView.findViewById(R.id.icon_bank_start);
            mIconEndBank = itemView.findViewById(R.id.icon_bank_end);

            mTextTypePayment = itemView.findViewById(R.id.text_payment_type);
        }
    }

}
