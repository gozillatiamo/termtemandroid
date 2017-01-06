package com.worldwidewealth.wealthwallet.dashboard.topup.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worldwidewealth.wealthwallet.dashboard.report.ActivityReport;
import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.FragmentTopupPreview;
import com.worldwidewealth.wealthwallet.Global;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.dialog.DialogNetworkError;
import com.worldwidewealth.wealthwallet.model.EslipRequestModel;
import com.worldwidewealth.wealthwallet.model.GetOTPRequestModel;
import com.worldwidewealth.wealthwallet.model.LoadButtonRequestModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.model.SubmitTopupRequestModel;
import com.worldwidewealth.wealthwallet.model.TopupPreviewRequestModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.TopupResponseModel;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.Until;

import java.io.IOException;
import java.text.NumberFormat;

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
    public Handler mHandler;
    private int mTimeout = 0;
    public Runnable mRunnableSubmit;

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

        Until.setupUI(rootView);
//        mHolder.mViewPage.setAdapter(new AdapterPageTopup(getChildFragmentManager()));
//        mHolder.mTab.setupWithViewPager(mHolder.mViewPage);
        initPageTopup();
        initData();
        initBtn();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setEnabledBtn(true);
    }

    public void setEnabledBtn(boolean enabled){
        mHolder.mBtnTopup.setEnabled(enabled);
        mHolder.mBtnNext.setEnabled(enabled);
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

        Log.e("initPageTopup", "true");
        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext());

        Call<ResponseBody> call = services.loadButton(new RequestModel(APIServices.ACTIONLOADBUTTON,
                    new LoadButtonRequestModel(mCarrier)
                ));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
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
                new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), call, this);
                setEnabledBtn(true);
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
                mHolder.mBtnNext.setEnabled(false);
                servicePreview();
            }
        });

        mHolder.mBtnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHolder.mBtnTopup.setEnabled(false);
                if(getFragmentManager().findFragmentById(R.id.container_topup_package) instanceof FragmentTopupPreview){
                    FragmentTopupPreview fragmentTopupPreview = (FragmentTopupPreview) getFragmentManager().findFragmentById(R.id.container_topup_package);
                    if (!fragmentTopupPreview.canTopup()) return;
                }

                serviceTopup();

            }
        });
    }

    private void servicePreview(){
        mPhone = mHolder.mEditPhone.getText().toString().replaceAll("-", "");

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

        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext());
        Call<ResponseBody> call = services.preview(new RequestModel(APIServices.ACTIONPREVIEW,
                new TopupPreviewRequestModel(mAmt, mCarrier)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DialogCounterAlert.DialogProgress.dismiss();
                ContentValues modelValues = EncryptionData.getModel(response.body());

                if (modelValues.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    ResponseModel responseModel = new Gson()
                            .fromJson(modelValues.getAsString(EncryptionData.STRMODEL), ResponseModel.class);

                    if (responseModel.getStatus() != APIServices.SUCCESS)
                        new ErrorNetworkThrowable(null).networkError(getContext(),
                                responseModel.getMsg(), call, this);

                } else {

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
                new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), call, this);
                setEnabledBtn(true);
            }
        });

    }

    public void setEnableEditPhone(boolean enable){
        if (enable){
            mHolder.mEditPhone.setBackgroundResource(android.R.drawable.editbox_background_normal);
            mHolder.mEditPhone.setInputType(InputType.TYPE_CLASS_PHONE);
            mHolder.mBtnNext.setVisibility(View.VISIBLE);
            mHolder.mBtnTopup.setVisibility(View.GONE);
        } else {
            mHolder.mEditPhone.setBackgroundResource(android.R.color.transparent);
            mHolder.mEditPhone.setInputType(InputType.TYPE_NULL);
            mHolder.mBtnNext.setVisibility(View.GONE);
            mHolder.mBtnTopup.setVisibility(View.VISIBLE);
        }
    }

    private void serviceTopup(){
        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext());
        Call<ResponseBody> call = services.getOTP(new RequestModel(APIServices.ACTIONGETOTP,
                new GetOTPRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
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
                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
                setEnabledBtn(true);
            }
        });

    }

    private void serviceSubmitToup(final String responseStr){

        String converted = Until.ConvertJsonEncode(responseStr);
        final String responDecode = Until.decode(converted);
        final TopupResponseModel model = new Gson().fromJson(responDecode, TopupResponseModel.class);
        mTimeout = 0;
        mRunnableSubmit = new Runnable() {
            @Override
            public void run() {
                mTimeout++;
                Log.e("Time", mTimeout+"");
                if (mTimeout == 10){
                    new DialogCounterAlert(getContext(), getString(R.string.error), getString(R.string.topup_time_out), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            serviceTopup();
                        }
                    });
                    mHandler.removeCallbacks(mRunnableSubmit);
                    return;
                }
                Log.e("OTP", Global.getOTP()+"");

                if (Global.getOTP() == null){
                    mHandler.removeCallbacks(mRunnableSubmit);
                    mHandler.postDelayed(mRunnableSubmit, postDelay);
                    return;
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
                APIHelper.enqueueWithRetry(callSubmit, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ContentValues responseValues = EncryptionData.getModel(response.body());
                        if (responseValues.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                            ResponseModel responseModel = new Gson().fromJson(responseValues.getAsString(EncryptionData.STRMODEL),
                                    ResponseModel.class);
                            if (responseModel.getStatus() == APIServices.SUCCESS) {
                                serviceEslip(model.getTranid());
                            } else {
                                if (responseModel.getStatus() != APIServices.SUCCESS)
                                    new ErrorNetworkThrowable(null).networkError(getContext(),
                                            responseModel.getMsg(), call, this);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), call, this);
                        setEnabledBtn(true);
                    }
                });

            }
        };

        mHandler.postDelayed(mRunnableSubmit, postDelay);

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
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ContentValues values = EncryptionData.getModel(response.body());
                if (values.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    ResponseModel responseModel = new Gson().fromJson(values.getAsString(EncryptionData.STRMODEL), ResponseModel.class);

                    if (responseModel.getStatus() != APIServices.SUCCESS)
                        new ErrorNetworkThrowable(null).networkError(getContext(),
                                responseModel.getMsg(), call, this);
                    else {
                        if (responseModel.getFf().equals("")) {

                            new DialogCounterAlert(FragmentTopupPackage.this.getContext(),
                                    null,
                                    getString(R.string.slip_not_found),
                                    null);

                        } else {

                            byte[] imageByte = Base64.decode(responseModel.getFf(), Base64.NO_WRAP);
                            AppCompatActivity activity = (AppCompatActivity) FragmentTopupPackage.this.getActivity();
                            activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container_topup, FragmentTopupSlip.newInstance(imageByte, transid)).commit();
                        }
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), call, this);
                setEnabledBtn(true);
            }
        });

//        DialogCounterAlert.DialogProgress.dismiss();
//        AppCompatActivity activity = (AppCompatActivity) FragmentTopupPackage.this.getActivity();
//        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        activity.getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container_topup, FragmentTopupSlip.newInstance()).commit();

    }

    public class ViewHolder{
        private Button mBtnNext, mBtnTopup;
        private TextView mTextPrice;
        private ImageView mLogoService;
        private EditText mEditPhone;
        private boolean mFormatting;
        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTopup = (Button) itemview.findViewById(R.id.btn_topup);
            mLogoService = (ImageView) itemview.findViewById(R.id.logo_service);
            mTextPrice = (TextView) itemview.findViewById(R.id.text_price);
            mEditPhone = (EditText) itemview.findViewById(R.id.edit_phone);
//            mEditPhone.setOnFocusChangeListener(Until.onFocusEditText());
            mEditPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!mFormatting){
                        mFormatting = true;
                        PhoneNumberUtils.formatNumber(s, PhoneNumberUtils.FORMAT_NANP);
                        mFormatting = false;
                    }
                }
            });

        }
    }

}
