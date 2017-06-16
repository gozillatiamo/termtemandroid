package com.worldwidewealth.termtem.dashboard.favorite.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.model.LoadFavResponseModel;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by gozillatiamo on 6/16/17.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>{

    private Context mContext;
    private List<LoadFavResponseModel> mListFav;

    public FavoritesAdapter(Context context, List<LoadFavResponseModel> listfav) {
        this.mContext = context;
        this.mListFav = listfav;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LoadFavResponseModel model = mListFav.get(position);
        setIconService(model.getService(), holder.mIconFav);
        holder.mTextTitle.setText(model.getName());
        setTextDes(mListFav.get(position), holder.mTextDes, holder.mTextAmt);
        setTextDate(mListFav.get(position).getCreate_Date(), holder.mTextDate);
    }

    @Override
    public int getItemCount() {
        return mListFav.size();
    }

    private void setIconService(String typeService, ImageView icon){
        switch (typeService){
            case ActivityReport.TOPUP_REPORT:
                icon.setImageResource(R.drawable.ic_topup);
                break;
            case ActivityReport.EPIN_REPORT:
                icon.setImageResource(R.drawable.ic_pin_code);

                break;
            case ActivityReport.VAS_REPORT:
                icon.setImageResource(R.drawable.ic_topup);

                break;
            case ActivityReport.CASHIN_REPORT:
                icon.setImageResource(R.drawable.ic_agent_cashin);

                break;
            case ActivityReport.BILL_REPORT:
                icon.setImageResource(R.drawable.ic_topup);

                break;

        }
    }

    private void setTextDes(LoadFavResponseModel model, TextView textDes, TextView textAmt){
        String strDes = null;
        EditText editText = new EditText(mContext);
        PhoneNumberUtils.formatNumber(editText.getText(), PhoneNumberUtils.FORMAT_NANP);

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);


        switch (model.getService()){
            case ActivityReport.TOPUP_REPORT:
                LoadFavResponseModel.TopupListModel topupModel = model.getTopuplist().get(0);
                editText.setText(topupModel.getPhoneNo());
                strDes = model.getService()+" "+topupModel.getCarrierCode()+" "+
                        mContext.getString(R.string.title_phone_short)+" "+
                        editText.getText().toString();

                textAmt.setText(format.format(topupModel.getAmt()));
                textAmt.setTextColor(ContextCompat.getColor(mContext, R.color.color_topup));
                break;
            case ActivityReport.EPIN_REPORT:
                LoadFavResponseModel.EpinListModel epinModel = model.getEpinlist().get(0);
                editText.setText(epinModel.getPhoneNo());
                strDes = model.getService()+" "+epinModel.getCarrierCode()+" "+
                        mContext.getString(R.string.title_phone_short)+" "+
                        editText.getText().toString();

                textAmt.setText(format.format(epinModel.getAmt()));
                textAmt.setTextColor(ContextCompat.getColor(mContext, R.color.color_epin));

                break;
            case ActivityReport.VAS_REPORT:

                break;
            case ActivityReport.CASHIN_REPORT:
                LoadFavResponseModel.CashInListModel cashInModel = model.getCashinlist().get(0);
                strDes = model.getService()+" Agent: "+cashInModel.getAgentFirstName()+" "+
                        cashInModel.getAgentLastName();

                textAmt.setText(format.format(cashInModel.getAmt()));
                textAmt.setTextColor(ContextCompat.getColor(mContext, R.color.color_agent_cashin));

                break;
            case ActivityReport.BILL_REPORT:

                break;

        }

        textDes.setText(strDes);

    }

    private void setTextDate(Date date, TextView textDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String strDate = calendar.get(Calendar.DAY_OF_MONTH)+"-"+
                calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH"))+"-"+
                (calendar.get(Calendar.YEAR)+543)+" "+mContext.getString(R.string.time)+" "+
                calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

        textDate.setText(strDate);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIconFav;
        private TextView mTextDate, mTextTitle, mTextDes, mTextAmt;

        public ViewHolder(View itemView) {
            super(itemView);
            mIconFav = (ImageView) itemView.findViewById(R.id.icon_fav);
            mTextDate = (TextView) itemView.findViewById(R.id.text_fav_date);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_fav_title);
            mTextDes = (TextView) itemView.findViewById(R.id.text_fav_des);
            mTextAmt = (TextView) itemView.findViewById(R.id.text_fav_amt);
        }
    }
}
