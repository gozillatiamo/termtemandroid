package com.worldwidewealth.termtem.dashboard.topup.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.PGResponseModel;
import com.worldwidewealth.termtem.model.VasValuesModel;
import com.worldwidewealth.termtem.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gozillatiamo on 6/1/17.
 */

public class VasAdapter extends RecyclerView.Adapter<VasAdapter.ViewHolder>{

    private Context mContext;
    private List<VasValuesModel> mListValues;

    private String[] mListSpeed, mListVolume;
    private int[] mListPrice, mListLimitDay;

    private List<PGResponseModel> mListPG;
    public static final String TAG = VasAdapter.class.getSimpleName();
    public VasAdapter(Context context, List<PGResponseModel> listPG){
        mContext = context;
        this.mListPG = listPG;
        mListSpeed = context.getResources().getStringArray(R.array.list_vas_speed);
        mListVolume = context.getResources().getStringArray(R.array.list_vas_volume);
        mListPrice = context.getResources().getIntArray(R.array.list_vas_price);
        mListLimitDay = context.getResources().getIntArray(R.array.list_vas_limitday);
        mListValues = new ArrayList<>();

        while (mListValues.size() < mListSpeed.length){
            VasValuesModel model = new VasValuesModel();
            model.setPRICE(mListPrice[mListValues.size()]);
            model.setLIMITDAY(mListLimitDay[mListValues.size()]);
            model.setSPEED(mListSpeed[mListValues.size()]);
            model.setVOLUME(mListVolume[mListValues.size()]);
            mListValues.add(model);
            Log.e(TAG, ""+mListValues.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_vas_values, parent, false);
        Util.setupUI(view);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.mTextPrice.setText(getAmount(position)+" "+mContext.getString(R.string.currency));

        holder.mTextSpeed.setText(getStrDes(position)[0]+"4G");
        holder.mTextVolume.setText(getStrDes(position)[1]);

    }

    @Override
    public int getItemCount() {
        return mListPG.size();
    }

    public String getAmount(int position){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);

        return format.format(mListPG.get(position).getAmount());
    }

    public String[] getStrDes(int position){
        return  mListPG.get(position).getServiceNameTh().split("4G ");
    }

    public PGResponseModel getItem(int positon){
        return mListPG.get(positon);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextPrice, mTextSpeed, mTextVolume;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextPrice = (TextView) itemView.findViewById(R.id.text_price_values);
            mTextSpeed = (TextView) itemView.findViewById(R.id.text_speed_internet);
            mTextVolume = (TextView) itemView.findViewById(R.id.text_volume_internet);
        }
    }
}
