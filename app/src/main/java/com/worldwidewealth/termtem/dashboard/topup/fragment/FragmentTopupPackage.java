package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
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
import com.worldwidewealth.termtem.util.Util;

import java.text.NumberFormat;

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
    private APIServices services;
    private double mAmt = 0.00;
    private String mButtonID = null;
    public Handler mHandler;
    public Runnable mRunnableSubmit;
    public static final String TAG = FragmentTopupPackage.class.getSimpleName();
    private BottomAction mBottomAction;
    private Call<ResponseBody> call;
    private Callback<ResponseBody> callback;
    private byte[] imageByte = null;
    private String transid;

    private String mActionLoadButton = APIServices.ACTIONLOADBUTTON;
    private String mActionPreview = APIServices.ACTIONPREVIEW;
    private String mActionGetOTP = APIServices.ACTIONGETOTP;
    private String mActionSumitTopup = APIServices.ACTIONSUBMITTOPUP;
    private String mActionEslip = APIServices.ACTIONESLIP;

    private static final String CARRIER = "carrier";
    private static final int postDelay = 1000;

    public static Fragment newInstance(String carrier, String topup){
        FragmentTopupPackage fragment = new FragmentTopupPackage();
        Bundle bundle = new Bundle();
        bundle.putString(CARRIER, carrier);
        bundle.putString(FragmentTopup.keyTopup, topup);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCarrier = getArguments().getString(CARRIER);
        mTopup = getArguments().getString(FragmentTopup.keyTopup);
        if(mTopup.equals(FragmentTopup.PIN)){
            mActionLoadButton = APIServices.ACTION_LOAD_BUTTON_EPIN;
            mActionPreview = APIServices.ACTION_PREVIEW_EPIN;
            mActionGetOTP = APIServices.ACTION_GET_TOPUP_EPIN;
            mActionSumitTopup = APIServices.ACTION_SUBMIT_TOPUP_EPIN;
            mActionEslip = APIServices.ACTION_ESLIP_EPIN;
            getView().findViewById(R.id.text_hint_pin).setVisibility(View.VISIBLE);
        }
        mHandler = new Handler();
        services = APIServices.retrofit.create(APIServices.class);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_package, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        Util.setupUI(rootView);
//        mHolder.mViewPage.setAdapter(new AdapterPageTopup(getChildFragmentManager()));
//        mHolder.mTab.setupWithViewPager(mHolder.mViewPage);
        initPageTopup();
        initData();
        initBtn();
        mHolder.mEditPhone.requestFocus();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBottomAction != null)
            mBottomAction.setEnable(true);

        if (Global.getInstance().getAGENTID() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (imageByte != null) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_topup, FragmentTopupSlip.newInstance(imageByte, transid)).commit();
                    }
                }
            }, 2000);
        }

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

        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                call = services.loadButton(new RequestModel(mActionLoadButton,
                        new LoadButtonRequestModel(mCarrier)
                ));
                APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);

                        if (responseValues == null) return;

                        if (responseValues instanceof ResponseModel){
                            DialogCounterAlert.DialogProgress.dismiss();
                        } else {
                            getChildFragmentManager().beginTransaction()
                                    .replace(R.id.container_topup_package, FragmentAirtimeVAS.newInstance((String)responseValues, mTopup))
                                    .commit();
                        }
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
        switch (mCarrier){
            case APIServices.AIS:
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

        if (mPhone.length() != 10){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.please_phone_topup_error)
                    .setPositiveButton(R.string.confirm, null)
                    .show();
            mBottomAction.setEnable(true);

            return;
        }

        if (mAmt == 0){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.please_choice_topup)
                    .setPositiveButton(R.string.confirm, null)
                    .show();
            mBottomAction.setEnable(true);

            return;
        }

        if (Global.getInstance().getBALANCE() < mAmt){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.balance_not_enough)
                    .setPositiveButton(R.string.confirm, null)
                    .show();
            mBottomAction.setEnable(true);

            return;
        }


        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();

        call = services.preview(new RequestModel(mActionPreview,
                new TopupPreviewRequestModel(mAmt, mCarrier)));
        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
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
    }

    private void serviceTopup(){
        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();
        call = services.getOTP(new RequestModel(mActionGetOTP,
                new GetOTPRequestModel()));
        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
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
        call = services.submitTopup(
                new RequestModel(mActionSumitTopup,
                        new SubmitTopupRequestModel(String.valueOf(getmAmt()),
                                mCarrier,
                                mPhone,
                                model.getTranid(),
                                mButtonID,
                                null)));
        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (responseValues == null) {
                    mBottomAction.setEnable(true);
                    return;
                }

                if (responseValues instanceof ResponseModel){
                    serviceEslip(model.getTranid());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(), null, call, this, false);
                mBottomAction.setEnable(true);
            }
        });
    }

    private void serviceEslip(final String transid){

        call = services.eslip(new RequestModel(mActionEslip, new EslipRequestModel(transid, null)));

        APIHelper.enqueueWithRetry(call, callback = new Callback<ResponseBody>() {
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
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_topup, FragmentTopupSlip.newInstance(imageByte, transid)).commit();
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

    public class ViewHolder{
//        private Button mBtnNext, mBtnTopup, mBtnCancel;
        private TextView mTextPrice;
        private ImageView mLogoService;
        private EditText mEditPhone;
        private boolean mFormatting;
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
            mEditPhone = (EditText) itemview.findViewById(R.id.edit_phone);
            mIncludeBottomAction = (View) itemview.findViewById(R.id.include_bottom_action);
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
