package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.adapter.VasAdapter;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.LoadButtonResponseModel;
import com.worldwidewealth.termtem.model.PGResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.FragmentTopupPreview;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.EslipRequestModel;
import com.worldwidewealth.termtem.model.GetOTPRequestModel;
import com.worldwidewealth.termtem.model.LoadButtonRequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.model.TopupPreviewRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.TopupResponseModel;
import com.worldwidewealth.termtem.util.BottomAction;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.RecyclerItemClickListener;
import com.worldwidewealth.termtem.util.Util;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
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
    private String mTopup;
    private String mPhone;
    private double mFavAmt = 0;
    private boolean mIsFAV;
    private APIServices services;
    private double mAmt = 0.00;
    private String mButtonID = null;
    private String mPgName = null;
    public Handler mHandler;
    public Runnable mRunnableSubmit;
    public static final String TAG = FragmentTopupPackage.class.getSimpleName();
    private BottomAction mBottomAction;
    public static Call<ResponseBody> callSubmit = null;

    private byte[] imageByte = null;
    private String transid;
    private Timer mTimerTimeout = null;
    private AlertDialog mAlertTimeout;
    private VasAdapter mVasAdapter;

    private String mActionLoadButton = APIServices.ACTIONLOADBUTTON;
    private String mActionPreview = APIServices.ACTIONPREVIEW;
    private String mActionGetOTP = APIServices.ACTIONGETOTP;
    private String mActionSumitTopup = APIServices.ACTIONSUBMITTOPUP;
    private String mActionEslip = APIServices.ACTIONESLIP;

    private static final String CARRIER = "carrier";
    private static final int postDelay = 1000;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getExtras() == null) return;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!(intent.getExtras().containsKey("topup"))) return;

                    if (callSubmit != null && callSubmit.isExecuted()){
                        callSubmit.cancel();
                    }


                    if (intent.getExtras().getBoolean("topup")){
                        if (Global.getInstance().getLastSubmit() != null){
                            serviceEslip(Global.getInstance().getLastTranId());
                        }
                    } else {
                        Global.getInstance().setLastSubmit(null, false);

                        String msg = getContext().getString(R.string.alert_topup_fail);
                        new DialogCounterAlert(getContext(), getContext().getString(R.string.error), msg, null);

//                        getActivity().finish();
                    }

                }
            }, 3000);
        }
    };


    public static Fragment newInstance(String carrier, String topup, String phone_no, double amt){
        FragmentTopupPackage fragment = new FragmentTopupPackage();
        Bundle bundle = new Bundle();
        bundle.putString(CARRIER, carrier);
        bundle.putString(FragmentTopup.keyTopup, topup);
        bundle.putString(ActivityTopup.KEY_PHONENO, phone_no);
        bundle.putDouble(ActivityTopup.KEY_AMT, amt);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCarrier = getArguments().getString(CARRIER);
        mTopup = getArguments().getString(FragmentTopup.keyTopup);
        mPhone = getArguments().getString(ActivityTopup.KEY_PHONENO);
        mFavAmt = getArguments().getDouble(ActivityTopup.KEY_AMT);

        mHandler = new Handler();
        services = APIServices.retrofit.create(APIServices.class);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_package, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        setupService(mTopup);

        Util.setupUI(rootView);
//        mHolder.mViewPage.setAdapter(new AdapterPageTopup(getChildFragmentManager()));
//        mHolder.mTab.setupWithViewPager(mHolder.mViewPage);
        initPageTopup();
        initBtn();
        initData();
        mHolder.mEditPhone.requestFocus();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBottomAction != null)
            mBottomAction.setEnable(true);

    }

    @Override
    public void onPause() {
        super.onPause();
        Util.hideSoftKeyboard(mHolder.mEditPhone);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            getActivity().unregisterReceiver(myReceiver);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private void setupService(String type){

        switch (type){
            case FragmentTopup.MOBILE:
                mActionLoadButton = APIServices.ACTIONLOADBUTTON;
                mActionPreview = APIServices.ACTIONPREVIEW;
                mActionGetOTP = APIServices.ACTIONGETOTP;
                mActionSumitTopup = APIServices.ACTIONSUBMITTOPUP;
                mActionEslip = APIServices.ACTIONESLIP;
                break;
            case FragmentTopup.PIN:
                mActionLoadButton = APIServices.ACTION_LOAD_BUTTON_EPIN;
                mActionPreview = APIServices.ACTION_PREVIEW_EPIN;
                mActionGetOTP = APIServices.ACTION_GET_OTP_EPIN;
                mActionSumitTopup = APIServices.ACTION_SUBMIT_TOPUP_EPIN;
                mActionEslip = APIServices.ACTION_ESLIP_EPIN;
                break;
            case FragmentTopup.VAS:
                mActionLoadButton = APIServices.ACTION_GET_LISTPG;
                mActionPreview = APIServices.ACTION_PREVIEW_VAS;
                mActionGetOTP = APIServices.ACTION_GETOTP_VAS;
                mActionSumitTopup = APIServices.ACTION_SUBMIT_VAS;
                mActionEslip = APIServices.ACTION_ESLIP_VAS;
                break;
        }

    }

    public void setAmt(double price, String buttonid){
        this.mAmt = price;
        this.mButtonID = buttonid;
        mIsFAV = (mAmt == mFavAmt);

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        mHolder.mTextPrice.setText(format.format(mAmt));
    }

    public double getmAmt() {
        return mAmt;
    }

    private void initPageTopup(){

        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = services.loadButton(new RequestModel(mActionLoadButton,
                        new LoadButtonRequestModel(mCarrier)
                ));
                APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);

                        if (responseValues == null) return;

                        if (responseValues instanceof String){
                            if (mTopup.equals(FragmentTopup.VAS))
                                setupVAS((String) responseValues);
                            else
                                setupBtnToup((String) responseValues);
                        }

                        DialogCounterAlert.DialogProgress.dismiss();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), call, this);
//                        setEnabledBtn(true);
                    }
                });

            }
        }, 500);
    }

    private void initData(){
        setAmt(mAmt, null);
        if (mPhone != null){
            mPhone = getArguments().getString(ActivityTopup.KEY_PHONENO);
            mHolder.mEditPhone.setText(mPhone);
        }
        switch (mCarrier){
            case APIServices.AIS:
                if (mTopup.equals(FragmentTopup.PIN))
                    mHolder.mLogoService.setImageResource(R.drawable.logo_ais_pin);
                else
                    mHolder.mLogoService.setImageResource(R.drawable.logo_ais);

                break;
            case APIServices.TRUEMOVE:
                if (mTopup.equals(FragmentTopup.MOBILE))
                    mHolder.mLogoService.setImageResource(R.drawable.logo_truemove);
                else
                    mHolder.mLogoService.setImageResource(R.drawable.logo_truemoney);

                break;
            case APIServices.DTAC:
                mHolder.mLogoService.setImageResource(R.drawable.logo_dtac);
                break;
        }

        switch (mTopup){
            case FragmentTopup.MOBILE:
                mHolder.mTextHint.setText(R.string.topup);
                mBottomAction.setTitleAmount(getString(R.string.topup_title_amount));
                break;
            case FragmentTopup.PIN:
                mHolder.mTextHint.setText(R.string.dashboard_pin);
                mBottomAction.setTitleAmount(getString(R.string.epin_title_amount));

                break;
            case FragmentTopup.VAS:
                mHolder.mTextHint.setText(R.string.vas);
                mBottomAction.setTitleAmount(getString(R.string.epin_title_amount));

                break;
        }
    }

    private void initBtn(){
        mBottomAction = new BottomAction(getContext(), mHolder.mIncludeBottomAction, BottomAction.NEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomAction.setEnable(false);
                servicePreview();
            }
        });
    }

    private void servicePreview(){
        mPhone = mHolder.mEditPhone.getText().toString().replaceAll("-", "");

        if (!checkData()) return;

        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();

        Call<ResponseBody> call = services.preview(new RequestModel(mActionPreview,
                new TopupPreviewRequestModel(mAmt, mCarrier)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Object modelValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (modelValues == null) {
                    mBottomAction.setEnable(true);
                    return;
                }

                if (modelValues instanceof ResponseModel){
                    DialogCounterAlert.DialogProgress.dismiss();
                }else {
                    DialogCounterAlert.DialogProgress.dismiss();
                    FragmentTopupPackage.this.getChildFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right,
                                    R.anim.slide_out_left,
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right)
                            .replace(R.id.container_topup_package, FragmentTopupPreview
                                    .newInstance(((String)modelValues), mBottomAction.getPrice()))
                            .addToBackStack(null)
                            .commit();
/*
                    mBottomAction.swichType(BottomAction.SUBMIT, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomAction.setEnable(false);
                            if(getFragmentManager().findFragmentById(R.id.container_topup_package) instanceof FragmentTopupPreview){
                                FragmentTopupPreview fragmentTopupPreview = (FragmentTopupPreview) getFragmentManager().findFragmentById(R.id.container_topup_package);
                                if (!fragmentTopupPreview.canTopup()) {
                                    mBottomAction.setEnable(true);
                                    return;
                                }
                            }

                            serviceTopup();


                        }
                    });
*/
                    setEnableEditPhone(false);

                }

/*
                if (modelValues.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    ResponseModel responseModel = new Gson()
                            .fromJson(modelValues.getAsString(EncryptionData.STRMODEL), ResponseModel.class);

                    if (responseModel.getStatus() != APIServices.SUCCESS)
                        new ErrorNetworkThrowable(null).networkError(getContext(),
                                responseModel.getMsg(), call, this);
*/


/*
                } else {


                }

*/

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), call, this);
                mBottomAction.setEnable(true);
            }
        });

    }

    private boolean checkData(){
        mPhone = mHolder.mEditPhone.getText().toString().replaceAll("-", "");

        if (mPhone.length() != 10){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.please_phone_topup_error)
                    .setPositiveButton(R.string.confirm, null)
                    .show();
            mBottomAction.setEnable(true);

            return false;
        }

        if (mAmt == 0){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.please_choice_topup)
                    .setPositiveButton(R.string.confirm, null)
                    .show();
            mBottomAction.setEnable(true);

            return false;
        }

        if (Global.getInstance().getBALANCE() < mAmt){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.balance_not_enough)
                    .setPositiveButton(R.string.confirm, null)
                    .show();
            mBottomAction.setEnable(true);

            return false;
        }

        return true;

    }

    public void setEnableEditPhone(boolean enable){
        if (enable){
            mHolder.mEditPhone.setBackgroundResource(android.R.drawable.editbox_background_normal);
            mHolder.mEditPhone.setInputType(InputType.TYPE_CLASS_PHONE);
/*
            mHolder.mBtnNext.setVisibility(View.VISIBLE);
            mHolder.mLayoutBtnTopup.setVisibility(View.GONE);
*/
            mBottomAction.swichType(BottomAction.NEXT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomAction.setEnable(false);
                    servicePreview();

                }
            });
            mHolder.mEditPhone.requestFocus();
        } else {
            mHolder.mEditPhone.setBackgroundResource(android.R.color.transparent);
            mHolder.mEditPhone.setInputType(InputType.TYPE_NULL);
/*
            mHolder.mBtnNext.setVisibility(View.GONE);
            mHolder.mLayoutBtnTopup.setVisibility(View.VISIBLE);
*/
            mBottomAction.swichType(BottomAction.SUBMIT, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomAction.setEnable(false);
                    if(getFragmentManager().findFragmentById(R.id.container_topup_package) instanceof FragmentTopupPreview){
                        FragmentTopupPreview fragmentTopupPreview = (FragmentTopupPreview) getFragmentManager().findFragmentById(R.id.container_topup_package);
                        if (!fragmentTopupPreview.canTopup()) {
                            mBottomAction.setEnable(true);
                            return;
                        }
                    }

                    serviceTopup();


                }
            });

        }

        if (mFavAmt > 0) {
            mHolder.mEditPhone.setBackgroundResource(android.R.color.transparent);
            mHolder.mEditPhone.setInputType(InputType.TYPE_NULL);

        }
    }

    private void serviceTopup(){
        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();
        Call<ResponseBody> call = services.getOTP(new RequestModel(mActionGetOTP,
                new GetOTPRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                final Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (responseValues == null){
                    mBottomAction.setEnable(true);
                    return;
                }

                if (responseValues instanceof String){
                    serviceSubmitToup((String)responseValues);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
                mBottomAction.setEnable(true);
            }
        });

    }

    private void serviceSubmitToup(final String responseStr){

        final TopupResponseModel model = new Gson().fromJson(responseStr, TopupResponseModel.class);
        SubmitTopupRequestModel submitTopupRequestModel = null;

        switch (mTopup){
            case FragmentTopup.MOBILE:
            case FragmentTopup.PIN:
                submitTopupRequestModel = SubmitTopupRequestModel.SubmitTopupRequestModel(
                        String.valueOf(getmAmt()),
                        mCarrier,
                        mPhone,
                        model.getTranid(),
                        mButtonID
                );
                break;
            case FragmentTopup.VAS:
                submitTopupRequestModel = SubmitTopupRequestModel.SubmitVasRequestModel(
                        String.valueOf(getmAmt()),
                        mCarrier,
                        mPhone,
                        model.getTranid(),
                        mPgName,
                        mButtonID
                );

                break;
        }

        final RequestModel requestModel = new RequestModel(mActionSumitTopup, submitTopupRequestModel);
        callSubmit = services.submitTopup(requestModel);

        startTimeoutSubmit(model.getTranid(), requestModel);

        Global.getInstance().setLastSubmit(requestModel, mIsFAV);
        Global.getInstance().setSubmitStatus(null);

                APIHelper.enqueueWithRetry(callSubmit, new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try{
                            getActivity().unregisterReceiver(myReceiver);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        if (mTimerTimeout != null)
                            mTimerTimeout.cancel();

                        if (mAlertTimeout != null && mAlertTimeout.isShowing())
                            mAlertTimeout.cancel();

                        String title;
                        if (mTopup.equals(FragmentTopup.MOBILE)) {
                            title = MyApplication.getContext().getString(R.string.title_topup);
                        } else {
                            title = MyApplication.getContext().getString(R.string.dashboard_pin);
                        }

                        Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                        if (responseValues == null) {
                            mBottomAction.setEnable(true);
                            Global.getInstance().setLastSubmit(null, false);

                            MyApplication.uploadFail(MyApplication.NOTITOPUP,
                                    model.getTranid(),
                                    title + " " + mCarrier + " " + mHolder.mTextPrice.getText().toString() + " "
                                            + MyApplication.getContext().getString(R.string.currency),
                                    MyApplication.getContext().getString(R.string.phone_number) + " " + mPhone + " "
                                            + MyApplication.getContext().getString(R.string.msg_upload_fail),
                                    android.R.drawable.stat_sys_warning);

                            return;
                        }

                        if (responseValues instanceof ResponseModel) {

                            MyApplication.uploadSuccess(MyApplication.NOTITOPUP, model.getTranid(),
                                    title + " " + mCarrier + " " + mHolder.mTextPrice.getText().toString() +
                                            " " + MyApplication.getContext().getString(R.string.currency),
                                    MyApplication.getContext().getString(R.string.phone_number) + " " +
                                            mPhone + " " + MyApplication.getContext().getString(R.string.success),
                                    R.drawable.ic_check_circle_white);
                            serviceEslip(model.getTranid());

//                            Global.getInstance().setProcessSubmit(null, null);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Exception submit topup: " + t.getMessage());
                        if (t.getMessage().equals("Canceled")) return;

                        if (mTimerTimeout != null)
                            mTimerTimeout.cancel();

                        String title;
                        if (mTopup.equals(FragmentTopup.MOBILE)) {
                            title = MyApplication.getContext().getString(R.string.title_topup);
                        } else {
                            title = MyApplication.getContext().getString(R.string.dashboard_pin);
                        }


                        if (mAlertTimeout != null && mAlertTimeout.isShowing()) {
                            mAlertTimeout.cancel();
//                            Global.getInstance().setProcessSubmit(null, null);


                        }
                            MyApplication.uploadFail(MyApplication.NOTITOPUP, model.getTranid(),
                                    title + " " + mCarrier + " " + mHolder.mTextPrice.getText().toString() +
                                            " " + MyApplication.getContext().getString(R.string.currency),
                                    MyApplication.getContext().getString(R.string.phone_number) + " " + mPhone +
                                            " " + MyApplication.getContext().getString(R.string.msg_upload_fail),
                                    android.R.drawable.stat_sys_warning);


                        new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(),
                                null, call, this, false, new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                startTimeoutSubmit(model.getTranid(), requestModel);
                            }
                        });
                        mBottomAction.setEnable(true);

                    }
                });
    }

    private void startTimeoutSubmit(final String tranid, final RequestModel requestModel){

        if (mTimerTimeout != null){
            mTimerTimeout.cancel();
            mTimerTimeout = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                .setTitle(R.string.warning)
                .setMessage(R.string.error_msg_timeout)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.showNotifyUpload(MyApplication.NOTITOPUP,
                                tranid,
                                ((ActivityTopup)getActivity()).getTopupTitle()+" "
                                        +mCarrier+" "+mHolder.mTextPrice.getText().toString()+" "
                                        +getString(R.string.currency),
                                getString(R.string.phone_number)+" "+mPhone+" "
                                        +getString(R.string.processing),
                                android.R.drawable.stat_notify_sync);
                        getActivity().finish();
                    }
                });

        mAlertTimeout = builder.create();

        mAlertTimeout.setOnShowListener(new MyShowListener());

        mTimerTimeout = new Timer();
        mTimerTimeout.schedule(new TimerTask() {
            @Override
            public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogCounterAlert.DialogProgress.dismiss();

                            mAlertTimeout.show();

                        }
                    });

                    }
        }, 10000);

//        90000

    }

    private void serviceEslip(final String transid){
        if (mAlertTimeout != null && mAlertTimeout.isShowing()) {
            mAlertTimeout.cancel();
        }

        if (mTimerTimeout != null){
            mTimerTimeout.cancel();
            mTimerTimeout = null;
        }

        DialogCounterAlert.DialogProgress.show();
        Call<ResponseBody> call = services.eslip(new RequestModel(mActionEslip, new EslipRequestModel(transid, null)));

        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);

                if (responseValues == null) {
                    mBottomAction.setEnable(true);
                    return;
                }

                if (responseValues instanceof ResponseModel){
                    FragmentTopupPackage.this.transid = transid;
                    imageByte = Base64.decode(((ResponseModel)responseValues).getFf()
                            , Base64.NO_WRAP);
                    AppCompatActivity activity = (AppCompatActivity) FragmentTopupPackage.this.getActivity();
//                    activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    try {
                        if (activity == null) return;
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_topup, FragmentTopupSlip.newInstance(imageByte, transid, mIsFAV)).commit();
                    } catch (IllegalStateException e){e.printStackTrace();}

                }


/*
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

                        }
                    }

                }
*/

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), null, call, this, false);
                mBottomAction.setEnable(true);
            }
        });

//        DialogCounterAlert.DialogProgress.dismiss();
//        AppCompatActivity activity = (AppCompatActivity) FragmentTopupPackage.this.getActivity();
//        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        activity.getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container_topup, FragmentTopupSlip.newInstance()).commit();

    }

    private void setupVAS(String response){
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
        Type listType = new TypeToken<List<PGResponseModel>>() {}.getType();
        List<PGResponseModel> modelList = gson.fromJson(response, listType);

        mHolder.mRecyclerVAS.setVisibility(View.VISIBLE);
        mVasAdapter = new VasAdapter(getContext(), modelList);
        mHolder.mRecyclerVAS.setLayoutManager(new LinearLayoutManager(getContext()));
        mHolder.mRecyclerVAS.setAdapter(new ScaleInAnimationAdapter(mVasAdapter));
        mHolder.mRecyclerVAS.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                setAmt(mVasAdapter.getItem(position).getAmount(), mVasAdapter.getItem(position).getPackageId());
                mPgName = mVasAdapter.getItem(position).getServiceNameTh();
                if (!checkData()) return;
                showDialogConfirmVAS(position);
            }
        }));
    }

    private void setupBtnToup(String response){
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
        Type listType = new TypeToken<List<LoadButtonResponseModel>>() {}.getType();
        List<LoadButtonResponseModel> modelList = gson.fromJson(response, listType);

        List<LoadButtonResponseModel> finalDataList = new ArrayList<>();
        int defaultPosition = -1;
        for (LoadButtonResponseModel model : modelList){
            if (model.getPRODUCT_TYPE().equals("AIRTIME") || model.getPRODUCT_TYPE().equals("E-PIN")){
                finalDataList.add(model.getSORT_NO()-1, model);

                if (model.getPRODUCT_PRICE() == mFavAmt) defaultPosition = model.getSORT_NO()-1;
            }
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.container_topup_package, FragmentChoiceTopup.newInstance(finalDataList, defaultPosition))
                .commit();


    }

    private void showDialogConfirmVAS(final int position){
        final AlertDialog confirmDialog = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                .setTitle(getString(R.string.title_phone_short)+" "+mHolder.mEditPhone.getText().toString())
                .setMessage(mVasAdapter.getAmount(position)+" "+getString(R.string.currency)+"\n"+
                        mVasAdapter.getItem(position).getServiceNameTh())
                .setNegativeButton(R.string.edit_phone, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialogEditPhone(position);
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBottomAction.setEnable(false);
                        mHolder.mRecyclerVAS.animate()
                                .alpha(0.0f)
                                .setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mHolder.mRecyclerVAS.setVisibility(View.GONE);
                                    }
                                });
                        servicePreview();
                    }
                }).show();

        confirmDialog.setOnShowListener(new MyShowListener());

    }

    private boolean mFormatting;


    private void showDialogEditPhone(final int position){

        final EditText editPhone = new EditText(getContext());
        editPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        editPhone.setPadding(20, 20, 20, 20);
        editPhone.setGravity(Gravity.CENTER);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(12);
        editPhone.setFilters(FilterArray);

        editPhone.addTextChangedListener(new TextWatcher() {
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

        AlertDialog.Builder builderEditPhone = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                .setTitle(R.string.phone_number)
                .setView(editPhone)
                .setPositiveButton(R.string.confirm, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialogConfirmVAS(position);
                    }
                });

        final AlertDialog dialogEditPhone = builderEditPhone.create();


        dialogEditPhone.setOnShowListener(new MyShowListener(){
            @Override
            public void onShow(DialogInterface dialogInterface) {
                super.onShow(dialogInterface);
                Button btnConfirm = dialogEditPhone.getButton(AlertDialog.BUTTON_POSITIVE);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPhone = editPhone.getText().toString().replaceAll("-", "");

                        if (mPhone.length() != 10){
                            Toast.makeText(getContext(), R.string.please_phone_topup_error, Toast.LENGTH_LONG).show();
                            return;
                        }

                        mHolder.mEditPhone.setText(editPhone.getText());
                        dialogEditPhone.cancel();
                        showDialogConfirmVAS(position);
                    }
                });

            }
        });

        dialogEditPhone.show();

    }

    public class ViewHolder{
//        private Button mBtnNext, mBtnTopup, mBtnCancel;
        private TextView mTextPrice, mTextHint;
        private ImageView mLogoService;
        private EditText mEditPhone;
        private boolean mFormatting;
        private RecyclerView mRecyclerVAS;
//        private View mLayoutBtnTopup;
        private View mIncludeBottomAction;
        public ViewHolder(View itemview){
/*
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
            mBtnTopup = (Button) itemview.findViewById(R.id.btn_topup);
            mBtnCancel = (Button) itemview.findViewById(R.id.btn_cancel);
            mLayoutBtnTopup = (View) itemview.findViewById(R.id.layout_btn_topup);
*/
            mLogoService = (ImageView) itemview.findViewById(R.id.logo_service);
            mTextPrice = (TextView) itemview.findViewById(R.id.text_price);
            mTextHint = (TextView) itemview.findViewById(R.id.text_hint);
            mEditPhone = (EditText) itemview.findViewById(R.id.edit_phone);
            mIncludeBottomAction = (View) itemview.findViewById(R.id.include_bottom_action);
            mRecyclerVAS = (RecyclerView) itemview.findViewById(R.id.recycler_vas);
//            mEditPhone.setOnFocusChangeListener(Util.onFocusEditText());
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
