package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.Configs;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.adapter.AdapterPageTopup;
import com.worldwidewealth.wealthcounter.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthcounter.dialog.DialogNetworkError;
import com.worldwidewealth.wealthcounter.model.GetOTPRequestModel;
import com.worldwidewealth.wealthcounter.model.LoadButtonRequestModel;
import com.worldwidewealth.wealthcounter.model.LoadButtonResponseModel;
import com.worldwidewealth.wealthcounter.model.LoginResponseModel;
import com.worldwidewealth.wealthcounter.model.PreviewRequestModel;
import com.worldwidewealth.wealthcounter.model.RequestModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthcounter.until.Until;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPackage extends  Fragment{
    private View rootView;
    private ViewHolder mHolder;
    private String mCarrier;
    private APIServices services;
    private double mAmt = 0.00;

    private static final String CARRIER = "carrier";
    public static Fragment newInstance(String carrier){
        FragmentTopupPackage fragment = new FragmentTopupPackage();
        Bundle bundle = new Bundle();
        bundle.putString(CARRIER, carrier);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCarrier = getArguments().getString(CARRIER);
        services = APIServices.retrofit.create(APIServices.class);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_package, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

//        mHolder.mViewPage.setAdapter(new AdapterPageTopup(getChildFragmentManager()));
//        mHolder.mTab.setupWithViewPager(mHolder.mViewPage);
        initPageTopup();
        initData();
        initBtn();
        return rootView;
    }

    public void setAmt(double price){
        this.mAmt = price;
        NumberFormat format = NumberFormat.getCurrencyInstance();
        mHolder.mTextPrice.setText(format.format(mAmt));
    }

    private void initPageTopup(){
        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext());

        Call<ResponseBody> call = services.loadButton(new RequestModel(APIServices.ACTIONLOADBUTTON,
                    new LoadButtonRequestModel(mCarrier)
                ));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String responseStr = null;
                try {
                    responseStr = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.container_topup_package, FragmentAirtimeVAS.newInstance(responseStr))
                        .commit();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new DialogNetworkError(FragmentTopupPackage.this.getContext());
            }
        });
    }

    private void initData(){
        setAmt(mAmt);
        switch (mCarrier){
            case APIServices.AIS:
                mHolder.mLogoService.setImageResource(R.drawable.logo_ais);
                break;
            case APIServices.TRUEMOVE:
                mHolder.mLogoService.setImageResource(R.drawable.logo_truemove);
                break;
            case APIServices.DTAC:
                mHolder.mLogoService.setImageResource(R.drawable.logo_dtac);
                break;
        }
    }

    private void initBtn(){
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = mHolder.mEditPhone.getText().toString();

                if (phoneNumber.length() != 10){
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setMessage(R.string.please_phone_topup_error)
                            .setPositiveButton(R.string.confirm, null)
                            .show();
                    return;
                }

                if (mAmt == 0){
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setMessage(R.string.please_choice_topup)
                            .setPositiveButton(R.string.confirm, null)
                            .show();
                    return;
                }

                Call<ResponseBody> call = services.preview(new RequestModel(APIServices.ACTIONPREVIEW,
                        new PreviewRequestModel(mAmt, mCarrier)));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String strRespose = null;
                        try {
                            strRespose = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mHolder.mBtnNext.setVisibility(View.GONE);
                        mHolder.mBtnTopup.setVisibility(View.VISIBLE);

                        FragmentTopupPackage.this.getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right)
                                .replace(R.id.container_topup_package, FragmentTopupPreview.newInstance(strRespose))
                                .addToBackStack(null)
                                .commit();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        new DialogNetworkError(FragmentTopupPackage.this.getContext());
                    }
                });
            }
        });

        mHolder.mBtnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext());
                Call<ResponseBody> call = services.getOTP(new RequestModel(APIServices.ACTIONGETOTP,
                        new GetOTPRequestModel()));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        String responseStr = null;
                        try {
                            responseStr = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String converted = Until.ConvertJsonEncode(responseStr);
                        String responDecode = Until.decode(converted);
                        Log.e("strResponse", converted);
                        Log.e("strDecode", responDecode);

                        Thread thread = new Thread() {
                            @Override
                            public void run() {

                                    while(Global.getOTP() == null) {
                                        try {
                                            sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e("NEWOTP", "null");
                                    }

                                    Log.e("NEWOTP", Global.getOTP());

                                    Global.setOTP(null);
                                    DialogCounterAlert.DialogProgress.dismiss();


                            }
                        };

                        thread.start();


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(getContext());
                    }
                });
/*
                Call<ResponseBody> call = null;

                switch (Global.getPage()){
                    case 0:
                        call = services.online();
                        break;
                    case 1:
                        call = services.pin();
                        break;
                }

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("response", response.raw().toString());
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Call<ResponseBody> callLuck = services.getluck();
                                callLuck.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Log.e("getLuck", response.raw().toString());
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                                .replace(R.id.container_topup, FragmentTopupSlip.newInstance())
                                                .addToBackStack(null)
                                                .commit();

                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });

                            }
                        }, 2000);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
*/


            }
        });

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    mHolder.mBtnNext.setVisibility(View.VISIBLE);
                    mHolder.mBtnTopup.setVisibility(View.GONE);
                }
                return false;
            }
        } );

    }

    public class ViewHolder{
        private ViewPager mViewPage;
        private TabLayout mTab;
        private Button mBtnNext, mBtnTopup;
        private TextView mTextPrice;
        private ImageView mLogoService;
        private EditText mEditPhone;
        public ViewHolder(View itemview){
//            mViewPage = (ViewPager) itemview.findViewById(R.id.viewpager);
//            mTab = (TabLayout) itemview.findViewById(R.id.tab_package);
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTopup = (Button) itemview.findViewById(R.id.btn_topup);
            mLogoService = (ImageView) itemview.findViewById(R.id.logo_service);
            mTextPrice = (TextView) itemview.findViewById(R.id.text_price);
            mEditPhone = (EditText) itemview.findViewById(R.id.edit_phone);

        }
    }

}
