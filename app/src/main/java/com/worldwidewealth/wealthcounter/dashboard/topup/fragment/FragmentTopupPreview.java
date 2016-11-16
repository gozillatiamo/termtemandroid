package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.model.PreviewResponseModel;
import com.worldwidewealth.wealthcounter.until.Until;

import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPreview extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private APIServices service;
    private String mData;
    private PreviewResponseModel mModel;
    private NumberFormat format = NumberFormat.getInstance();
    private static final String DATA = "data";
    public static Fragment newInstance(String data){
        FragmentTopupPreview fragment = new FragmentTopupPreview();
        Bundle bundle = new Bundle();
        bundle.putString(DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{

        private TextView mTextAmount, mTextCommissionRate, mTextCommissionAmout, mTextBalance;

        public ViewHolder(View itemview){
            mTextAmount = (TextView) itemview.findViewById(R.id.txt_amount);
            mTextCommissionRate = (TextView) itemview.findViewById(R.id.txt_commission_rate);
            mTextCommissionAmout = (TextView) itemview.findViewById(R.id.txt_commission_amount);
            mTextBalance = (TextView) itemview.findViewById(R.id.txt_balance);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        service = APIServices.retrofit.create(APIServices.class);
        mData = getArguments().getString(DATA);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_preview, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        transferData();
        setData();
        return rootView;
    }

    private void transferData(){
        String converted = Until.ConvertJsonEncode(mData);
        String responDecode = Until.decode(converted);
        Log.e("strDecodePreview", responDecode);

        mModel = new Gson().fromJson(responDecode, PreviewResponseModel.class);

    }

    private void setData(){
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        mHolder.mTextAmount.setText(format.format(mModel.getAMOUNT()));
        mHolder.mTextCommissionRate.setText(mModel.getCOMMISSION_RATE());
        mHolder.mTextCommissionAmout.setText(format.format(mModel.getCOMMISSION_AMOUNT()));
        mHolder.mTextBalance.setText(format.format(Global.getBALANCE() + mModel.getBALANCE()));
    }


}
