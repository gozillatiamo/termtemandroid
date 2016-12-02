package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.EncryptionData;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthcounter.dialog.DialogNetworkError;
import com.worldwidewealth.wealthcounter.model.EslipRequestModel;
import com.worldwidewealth.wealthcounter.model.GetOTPRequestModel;
import com.worldwidewealth.wealthcounter.model.LoadButtonRequestModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.model.SubmitTopupRequestModel;
import com.worldwidewealth.wealthcounter.model.TopupPreviewRequestModel;
import com.worldwidewealth.wealthcounter.model.RequestModel;
import com.worldwidewealth.wealthcounter.model.TopupPreviewResponseModel;
import com.worldwidewealth.wealthcounter.model.TopupResponseModel;
import com.worldwidewealth.wealthcounter.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthcounter.until.Until;

import java.io.IOException;
import java.text.NumberFormat;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPackage extends  Fragment{
    private View rootView;
    private ViewHolder mHolder;
    private String mCarrier;
    private String mPhone;
    private APIServices services;
    private double mAmt = 0.00;
    private String mButtonID = null;
    private Handler mHandler;

    private static final String CARRIER = "carrier";
    private static final int postDelay = 1000;

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
        mHandler = new Handler();
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


    public void setAmt(double price, String buttonid){
        this.mAmt = price;
        this.mButtonID = buttonid;
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        mHolder.mTextPrice.setText(format.format(mAmt));
    }

    public double getmAmt() {
        return mAmt;
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
        setAmt(mAmt, null);
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
                new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext());
                servicePreview();
            }
        });

        mHolder.mBtnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getFragmentManager().findFragmentById(R.id.container_topup_package) instanceof  FragmentTopupPreview){
                    FragmentTopupPreview fragmentTopupPreview = (FragmentTopupPreview) getFragmentManager().findFragmentById(R.id.container_topup_package);
                    if (!fragmentTopupPreview.canTopup()) return;
                }

                serviceTopup();

            }
        });
    }

    private void servicePreview(){
        mPhone = mHolder.mEditPhone.getText().toString();

        if (mPhone.length() != 10){
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
                new TopupPreviewRequestModel(mAmt, mCarrier)));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DialogCounterAlert.DialogProgress.dismiss();
                ContentValues modelValues = EncryptionData.getModel(response.body());

                if (modelValues.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    ResponseModel model = new Gson()
                            .fromJson(modelValues.getAsString(EncryptionData.STRMODEL), ResponseModel.class);
                    Toast.makeText(FragmentTopupPackage.this.getContext(), model.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    mHolder.mBtnNext.setVisibility(View.GONE);
                    mHolder.mBtnTopup.setVisibility(View.VISIBLE);

                    FragmentTopupPackage.this.getChildFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right)
                            .replace(R.id.container_topup_package, FragmentTopupPreview
                                    .newInstance(modelValues.getAsString(EncryptionData.STRMODEL)))
                            .addToBackStack(null)
                            .commit();
                    setEnableEditPhone(false);

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                new DialogNetworkError(FragmentTopupPackage.this.getContext());
            }
        });

    }

    public void setEnableEditPhone(boolean enable){
        if (enable){
            mHolder.mEditPhone.setBackgroundResource(android.R.drawable.editbox_background_normal);
            mHolder.mEditPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        } else {
            mHolder.mEditPhone.setBackgroundResource(android.R.color.transparent);
            mHolder.mEditPhone.setInputType(InputType.TYPE_NULL);
        }
    }

    private void serviceTopup(){
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

                serviceSubmitToup(responseStr);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext());
            }
        });

    }

    private void serviceSubmitToup(String responseStr){

        String converted = Until.ConvertJsonEncode(responseStr);
        final String responDecode = Until.decode(converted);
        final TopupResponseModel model = new Gson().fromJson(responDecode, TopupResponseModel.class);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                while(Global.getOTP() == null) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Call<ResponseBody> callSubmit = services.submitTopup(
                        new RequestModel(APIServices.ACTIONSUBMITTOPUP,
                                new SubmitTopupRequestModel(String.valueOf(getmAmt()),
                                        mCarrier,
                                        Global.getOTP(),
                                        mPhone,
                                        model.getTranid(),
                                        mButtonID)));
                Global.setOTP(null);
                callSubmit.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ContentValues responseValues = EncryptionData.getModel(response.body());
                        if (responseValues.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                            ResponseModel responseModel = new Gson().fromJson(responseValues.getAsString(EncryptionData.STRMODEL),
                                    ResponseModel.class);
                            if (responseModel.getStatus() == APIServices.SUCCESS) {
                                serviceEslip(model.getTranid());
                            } else {
                                new DialogCounterAlert(FragmentTopupPackage.this.getContext(), null, responseModel.getMsg());
                            }

                        } else {

                        }

                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext());
                    }
                });

            }
        }, postDelay);

/*
        Thread thread = new Thread() {
            @Override
            public void run() {

            }
        };

        thread.start();

*/

    }

    private void serviceEslip(final String transid){

        Call<ResponseBody> call = services.eslip(new RequestModel(APIServices.ACTIONESLIP, new EslipRequestModel(transid)));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ContentValues values = EncryptionData.getModel(response.body());
                if (values.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    ResponseModel responseModel = new Gson().fromJson(values.getAsString(EncryptionData.STRMODEL), ResponseModel.class);
//                    Toast.makeText(FragmentTopupPackage.this.getContext(), responseModel.getMsg(), Toast.LENGTH_LONG).show();
                    if (responseModel.getFf().equals("")) {

                        new DialogCounterAlert(FragmentTopupPackage.this.getContext(), null, getString(R.string.slip_not_found));

                    } else {

                        byte[] imageByte = Base64.decode(responseModel.getFf(), Base64.NO_WRAP);
                        AppCompatActivity activity = (AppCompatActivity) FragmentTopupPackage.this.getActivity();
                        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_topup, FragmentTopupSlip.newInstance(imageByte, transid)).commit();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext());
            }
        });

//        DialogCounterAlert.DialogProgress.dismiss();
//        AppCompatActivity activity = (AppCompatActivity) FragmentTopupPackage.this.getActivity();
//        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        activity.getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container_topup, FragmentTopupSlip.newInstance()).commit();

    }

    public void onBackPress(){
        mHolder.mBtnNext.setVisibility(View.VISIBLE);
        mHolder.mBtnTopup.setVisibility(View.GONE);
        setEnableEditPhone(true);
    }

    public class ViewHolder{
        private Button mBtnNext, mBtnTopup;
        private TextView mTextPrice;
        private ImageView mLogoService;
        private EditText mEditPhone;
        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTopup = (Button) itemview.findViewById(R.id.btn_topup);
            mLogoService = (ImageView) itemview.findViewById(R.id.logo_service);
            mTextPrice = (TextView) itemview.findViewById(R.id.text_price);
            mEditPhone = (EditText) itemview.findViewById(R.id.edit_phone);

        }
    }

}
