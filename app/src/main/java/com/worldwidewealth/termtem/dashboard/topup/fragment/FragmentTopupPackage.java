package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;
import com.worldwidewealth.termtem.dashboard.billpayment.BillPaymentActivity;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.adapter.VasAdapter;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.model.LoadButtonResponseModel;
import com.worldwidewealth.termtem.model.PGResponseModel;
import com.worldwidewealth.termtem.model.PreviewBillRequest;
import com.worldwidewealth.termtem.model.TopupPreviewResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.FragmentTopupPreview;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
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
    private String mDueDate = "";
    private long mDueDateTimestamp = System.currentTimeMillis();
    private String mBarcode;
    private String mBillRef;
    private LoadBillServiceResponse mBillService;
    private TopupPreviewResponseModel mPreviewModel;
    private static Context mContext;

    private double mFavAmt = 0;

    private boolean mIsFAV = false;
    private double mChangeAmount;
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
    private FragmentTopupPreview mFragmentTopupPreview;

    private String mActionLoadButton = APIServices.ACTIONLOADBUTTON;
    private String mActionPreview = APIServices.ACTIONPREVIEW;
    private String mActionGetOTP = APIServices.ACTIONGETOTP;
    private String mActionSumitTopup = APIServices.ACTIONSUBMITTOPUP;

    private static final String CARRIER = "carrier";
    public static final String KEY_BARCODE = "barcode";
    public static final String KEY_BILLREF = "billref";
    private static final String KEY_BILL_SERVICE = "billcode";

    private TextWatcher mTextWatcher;
    private static final int postDelay = 1000;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            if (intent.getExtras() == null) return;

            if (!(intent.getExtras().containsKey("topup"))) return;

            stopTimer();

            if (callSubmit != null && callSubmit.isExecuted()){
                    callSubmit.cancel();
                }


                if (intent.getExtras().getBoolean("topup")){
                    if (Global.getInstance().getLastSubmit() != null){
                        if (MyApplication.LeavingOrEntering.currentActivity instanceof ActivityTopup) {
                            try {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container_topup, FragmentTopupSlip.newInstance(FragmentTopupSlip.PREVIEW, mTopup, transid, mIsFAV)).commit();
                            } catch (IllegalStateException e){
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Global.getInstance().setLastSubmit(null, false);

                    try {
                        String msg = getContext().getString(R.string.alert_topup_fail);
                        new DialogCounterAlert(getContext(), getContext().getString(R.string.error), msg, null);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }

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

    public static Fragment newInstanceBill(String topup, String barcode, String billRef,
                                           LoadBillServiceResponse billservice, String phone_no){
        FragmentTopupPackage fragment = new FragmentTopupPackage();
        Bundle bundle = new Bundle();
        bundle.putString(FragmentTopup.keyTopup, topup);
        bundle.putString(KEY_BARCODE, barcode);
        bundle.putString(KEY_BILLREF, billRef);
        bundle.putParcelable(KEY_BILL_SERVICE, billservice);
        bundle.putString(ActivityTopup.KEY_PHONENO, phone_no);

        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments() != null){
            mTopup = getArguments().getString(FragmentTopup.keyTopup);
            switch (mTopup){
                case BillPaymentActivity.BILLPAY:
                    mBarcode = getArguments().getString(KEY_BARCODE);
                    mBillRef = getArguments().getString(KEY_BILLREF);
                    mBillService = getArguments().getParcelable(KEY_BILL_SERVICE);
                    break;
                default:
                    mCarrier = getArguments().getString(CARRIER);
                    mPhone = getArguments().getString(ActivityTopup.KEY_PHONENO);
                    mFavAmt = getArguments().getDouble(ActivityTopup.KEY_AMT);

            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
        mHandler = new Handler();
        services = APIServices.retrofit.create(APIServices.class);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_package, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        setupService(mTopup);
        initBtn();
        initData();

        Util.setupUI(rootView);
        mHolder.mEditPhone.requestFocus();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

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
        if (mPreviewModel == null)
            getActivity().finish();
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
                break;
            case FragmentTopup.PIN:
                mActionLoadButton = APIServices.ACTION_LOAD_BUTTON_EPIN;
                mActionPreview = APIServices.ACTION_PREVIEW_EPIN;
                mActionGetOTP = APIServices.ACTION_GET_OTP_EPIN;
                mActionSumitTopup = APIServices.ACTION_SUBMIT_TOPUP_EPIN;
                break;
            case FragmentTopup.VAS:
                mActionLoadButton = APIServices.ACTION_GET_LISTPG;
                mActionPreview = APIServices.ACTION_PREVIEW_VAS;
                mActionGetOTP = APIServices.ACTION_GETOTP_VAS;
                mActionSumitTopup = APIServices.ACTION_SUBMIT_VAS;
                break;
            case BillPaymentActivity.BILLPAY:
                mActionLoadButton = APIServices.ACTIONLOADBUTTON;
                mActionPreview = APIServices.ACTION_PREVIEW_BILL;
                mActionGetOTP = APIServices.ACTION_GETOTP_BILL;
                mActionSumitTopup = APIServices.ACTION_SUBMIT_BILL;
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
        new Handler().post(new Runnable() {
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
        });
    }

    private void initData(){
        setAmt(mAmt, null);

        if (mPhone != null && !mTopup.equals(BillPaymentActivity.BILLPAY)){
            mHolder.mEditPhone.setText(mPhone);
            setEnableEditPhone(false);
        }

        if (mCarrier != null) {
            switch (mCarrier) {
                case APIServices.AIS:
                    switch (mTopup) {
                        case FragmentTopup.MOBILE:
                            mHolder.mLogoService.setImageResource(R.drawable.logo_ais);
                            break;
                        case FragmentTopup.PIN:
                            mHolder.mLogoService.setImageResource(R.drawable.logo_ais_pin);
                            break;
                        case FragmentTopup.VAS:
                            mHolder.mLogoService.setImageResource(R.drawable.ais_vas);
                            break;
                    }

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

        switch (mTopup){
            case FragmentTopup.MOBILE:
                mHolder.mTextHint.setText(R.string.topup);
                mBottomAction.setTitleAmount(getString(R.string.topup_title_amount));
                initPageTopup();

                break;
            case FragmentTopup.PIN:
                mHolder.mTextHint.setText(R.string.dashboard_pin);
                mBottomAction.setTitleAmount(getString(R.string.epin_title_amount));
                initPageTopup();


                break;
            case FragmentTopup.VAS:
                mHolder.mTextHint.setText(R.string.vas);
                mBottomAction.setTitleAmount(getString(R.string.epin_title_amount));
                initPageTopup();


                break;
            case BillPaymentActivity.BILLPAY:
                mHolder.mTextHint.setText(R.string.dashboard_bill_pay);
                mBottomAction.setTitleAmount(getString(R.string.epin_title_amount));
/*
                mHolder.mTextTitleAccount.setText(mBillService.getBILL_SERVICE_DESC());
                mHolder.mTextTitleCarrier.setText(mBillService.getBILL_SERVICE_NAME());
*/
                mHolder.mTextTitleCarrier.setVisibility(View.INVISIBLE);
                mHolder.mTextTitleAccount.setText(mBillService.getBILL_SERVICE_NAME());
                mHolder.mEditPhone.setVisibility(View.GONE);
                mHolder.mLayoutBillRef.setVisibility(View.VISIBLE);

/*
                switch (mBillService.getBILL_SERVICE_CODE()){
                    case BillPaymentActivity.MEA_CODE:
                        mHolder.mLayoutRef2.setVisibility(View.VISIBLE);
                        mHolder.mTextTitleRef1.setText(getString(R.string.mea_ref1));
                        mHolder.mTextTitleRef2.setText(getString(R.string.mea_ref2));
                        break;
                    case BillPaymentActivity.MWA_CODE:
                        mHolder.mTextTitleRef1.setText(getString(R.string.mwa_ref1));
                        break;
                    case BillPaymentActivity.PEA_CODE:
                        mHolder.mTextTitleRef1.setText(getString(R.string.pea_ref1));
                        break;
                }
*/

                Glide.with(this).load(getString(R.string.server)+mBillService.getLOGOURL())
                        .placeholder(new ColorDrawable(Color.parseColor("#FFFFFF")))
                        .thumbnail(0.6f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .into(mHolder.mLogoService);
                servicePreview();

                break;
        }

        if (mTopup != BillPaymentActivity.BILLPAY){
            mHolder.mEditPhone.setHint(getString(R.string.hint_phone_number));
        }
    }

    private void initBtn(){
        mBottomAction = new BottomAction(getContext(), mHolder.mIncludeBottomAction, BottomAction.NEXT, new BottomAction.OnActionClickListener() {
            @Override
            public boolean onActionClick() {
                mBottomAction.setEnable(false);
                servicePreview();
                return false;
            }
        });
    }

    private void servicePreview(){
        mChangeAmount = 0;
        mPhone = mHolder.mEditPhone.getText().toString().replaceAll("-", "");

        if (mTopup != BillPaymentActivity.BILLPAY && !checkData()) return;

        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();

        Call<ResponseBody> call;

        switch (mTopup){
            case BillPaymentActivity.BILLPAY:
                Log.e(TAG, "BeforePreview: "+mBarcode);
                call = services.billService(new RequestModel(mActionPreview,
                        new PreviewBillRequest(mBarcode != null ? mBarcode : mBillRef
                                , mBillService.getBILL_SERVICE_CODE(),
                                mBillService.getBILL_SERVICE_ID(), getArguments().getString(ActivityTopup.KEY_PHONENO))));
                break;
            default:
                call = services.preview(new RequestModel(mActionPreview,
                        new TopupPreviewRequestModel(mAmt, mCarrier)));
        }

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

                    try {
                        if (getActivity() != null && isAdded()) {

                            mPreviewModel = new Gson().fromJson(((String)modelValues), TopupPreviewResponseModel.class);

/*
                            if (mPreviewModel.getREF1() != null){
                                mHolder.mEditPhone.removeTextChangedListener(mTextWatcher);
                                InputFilter[] fArray = new InputFilter[1];
                                fArray[0] = new InputFilter.LengthFilter(50);
                                mHolder.mEditPhone.setFilters(fArray);
                                String textRef1 = getString(R.string.ref1_bill_title)+" "+mPreviewModel.getREF1();
                                mHolder.mEditPhone.setText(textRef1);
                                mHolder.mEditPhone.setGravity(Gravity.LEFT);
                                mHolder.mEditPhone.setTextSize(TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_SP,
                                        8,
                                        getResources().getDisplayMetrics()));
                            }
*/

                            if (mPreviewModel.getREF() != null){
                                List<TopupPreviewResponseModel.RefModel> refModels = mPreviewModel.getREF();
                                Collections.sort(refModels);

                                for (TopupPreviewResponseModel.RefModel model : refModels) {
                                    addBillRef(model);
                                }
                            }

                            FragmentTransaction fragmentManager = FragmentTopupPackage.this
                                    .getChildFragmentManager().beginTransaction();

                            fragmentManager.setCustomAnimations(R.anim.slide_in_right,
                                            R.anim.slide_out_left,
                                            R.anim.slide_in_left,
                                            R.anim.slide_out_right)
                                    .replace(R.id.container_topup_package, FragmentTopupPreview
                                            .newInstance(mTopup, mPreviewModel, mBottomAction.getPrice()));
                            if (mTopup != BillPaymentActivity.BILLPAY)
                                fragmentManager.addToBackStack(null);

                            fragmentManager.commit();


                            if (mPreviewModel.getNEEDDUEDATE() != null &&
                                    mPreviewModel.getNEEDDUEDATE().equals("Y")) initDueDateDialog();

                        }
                    } catch (IllegalStateException e){}

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

    private void addBillRef(TopupPreviewResponseModel.RefModel model){
        View itemRef = View.inflate(getContext(), R.layout.item_bill_ref, null);
        TextView textTitle = itemRef.findViewById(R.id.text_title_ref);
        TextView textRef = itemRef.findViewById(R.id.text_ref);
        String refName = model.getREF_NAME().replaceFirst(" ", "\n");
        textTitle.setText(refName+" :");
        textRef.setText(model.getREF_VALUE());
        mHolder.mLayoutBillRef.addView(itemRef);
    }

    private boolean checkData(){
        mPhone = mHolder.mEditPhone.getText().toString().replaceAll("-", "");

        if (mPhone.length() != 10 && mTopup != BillPaymentActivity.BILLPAY){
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
            mBottomAction.swichType(BottomAction.NEXT, new BottomAction.OnActionClickListener() {
                @Override
                public boolean onActionClick() {
                    mBottomAction.setEnable(false);
                    servicePreview();
                    return false;

                }
            });
            mHolder.mEditPhone.requestFocus();
            mHolder.mEditPhone.setCursorVisible(true);

            if (mHolder.mEditPhone.getTag() != null)
                mHolder.mEditPhone.setKeyListener((KeyListener) mHolder.mEditPhone.getTag());

        } else {
            mHolder.mEditPhone.setBackgroundResource(android.R.color.transparent);
            mHolder.mEditPhone.setInputType(InputType.TYPE_CLASS_TEXT);
            mHolder.mEditPhone.setCursorVisible(false);
            mHolder.mEditPhone.setTag(mHolder.mEditPhone.getKeyListener());
            mHolder.mEditPhone.setKeyListener(null);
/*
            mHolder.mBtnNext.setVisibility(View.GONE);
            mHolder.mLayoutBtnTopup.setVisibility(View.VISIBLE);
*/
            mBottomAction.swichType(BottomAction.SUBMIT, new BottomAction.OnActionClickListener() {
                @Override
                public boolean onActionClick() {
                    mBottomAction.setEnable(false);
                    if(getChildFragmentManager().findFragmentById(R.id.container_topup_package) instanceof FragmentTopupPreview){
                        mFragmentTopupPreview = (FragmentTopupPreview) getChildFragmentManager().findFragmentById(R.id.container_topup_package);

                        if (mFragmentTopupPreview.isLayoutEditIsShow() || (mPreviewModel.getAMOUNT() <= 0 && mChangeAmount <= 0)){
                            Toast.makeText(getContext(), R.string.comfirm_amount_please, Toast.LENGTH_LONG).show();
                            mBottomAction.setEnable(true);

                            return false;
                        }

                        if (!mFragmentTopupPreview.canTopup()) {
                            mBottomAction.setEnable(true);
                            return false;
                        }
                    }

                    if (mTopup.equals(BillPaymentActivity.BILLPAY)){
                        transid = mPreviewModel.getTNID();
                        serviceSubmitToup(null);
                    } else
                        serviceTopup();

                    return false;

                }
            });

        }

        if (mFavAmt > 0) {
            mHolder.mEditPhone.setBackgroundResource(android.R.color.transparent);
            mHolder.mEditPhone.setInputType(InputType.TYPE_CLASS_TEXT);
            mHolder.mEditPhone.setCursorVisible(false);

        }
    }

    private void serviceTopup(){
        new DialogCounterAlert.DialogProgress(FragmentTopupPackage.this.getContext()).show();
        Call<ResponseBody> call = MyApplication.getServiceSubmit(new RequestModel(mActionGetOTP,
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

    @AddTrace(name = "SUBMITSERVICE", enabled = true)
    private void serviceSubmitToup(final String responseStr){

        if (responseStr != null) {
            final TopupResponseModel model = new Gson().fromJson(responseStr, TopupResponseModel.class);
            this.transid = model.getTranid();
        }

        final RequestModel requestModel = getRequestSubmit();

        startTimeoutSubmit();

        SubmitTopupRequestModel submitTopupRequestModel  = (SubmitTopupRequestModel) requestModel.getData();
        if (submitTopupRequestModel.getAMT().equals("0")){
            ((SubmitTopupRequestModel)requestModel.getData()).setAMT(String.valueOf(mPreviewModel.getNET()));
        }

        Global.getInstance().setLastSubmit(requestModel, mIsFAV);
        if (mBillService != null){
            Global.getInstance().setLastBillService(mBillService);
        }
        Global.getInstance().setSubmitStatus(null);


        callSubmit = MyApplication.getServiceSubmit(requestModel);

/*
        if (mTopup.equals(BillPaymentActivity.BILLPAY)){
//            newInstanceSlip();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EncryptionData.getModel(mContext, callSubmit, null, null);
                }
            }, 10000);
            return;
        }
*/

        APIHelper.enqueueWithRetry(callSubmit, new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try{
                            getActivity().unregisterReceiver(myReceiver);
                            MyApplication.unregisterReceiver();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        stopTimer();

                        Object responseValues = EncryptionData.getModel(mContext, call, response.body(), this);

                        if (responseValues instanceof String){
                            String msg = (String)responseValues;
                            if (msg.contains(APIServices.MSG_WAIT)) return;
                        }

                        String title = MyApplication.getTitleTypeToup(mActionSumitTopup);

/*

                        if (mAlertTimeout != null)
                            mAlertTimeout.cancel();

                        String title;
                        if (mTopup.equals(FragmentTopup.MOBILE)) {

                            title = MyApplication.getContext().getString(R.string.title_topup);
                        } else {
                            title = MyApplication.getContext().getString(R.string.dashboard_pin);
                        }
*/

                        if (responseValues == null) {
                            mBottomAction.setEnable(true);
                            Global.getInstance().setLastSubmit(null, false);

                            return;
                        }

                        if (responseValues instanceof ResponseModel) {

                            MyApplication.uploadSuccess(MyApplication.NOTITOPUP, ((ResponseModel)responseValues).getAppdisplay());

                            if (MyApplication.LeavingOrEntering.currentActivity instanceof ActivityTopup) {
                                try {
                                    boolean hasError = EncryptionData.showErrorMSG(getActivity(), (ResponseModel) responseValues, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            newInstanceSlip();
                                        }
                                    });

                                    if (!hasError){
                                        newInstanceSlip();
                                    }

                                } catch (IllegalStateException e){
                                    e.printStackTrace();
                                }
                            }

//                            Global.getInstance().setProcessSubmit(null, null);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Exception submit topup: " + t.getMessage());
                        if (t.getMessage().equals("Canceled")) {
                            callSubmit = null;
                            return;
                        }

                        stopTimer();

                        String title = MyApplication.getTitleTypeToup(mActionSumitTopup);
/*
                        if (mTopup.equals(FragmentTopup.MOBILE)) {
                            title = MyApplication.getContext().getString(R.string.title_topup);
                        } else {
                            title = MyApplication.getContext().getString(R.string.dashboard_pin);
                        }
*/

                        MyApplication.uploadFail(MyApplication.NOTITOPUP, MyApplication.getContext().getString(R.string.network_disconnect));

                        if (MyApplication.LeavingOrEntering.currentActivity instanceof ActivityTopup) {
                            new ErrorNetworkThrowable(t).networkError(FragmentTopupPackage.this.getContext(),
                                    null, call, this, false, new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            startTimeoutSubmit();
                                        }
                                    });
                            mBottomAction.setEnable(true);
                        }

                    }
                });
    }

    private void newInstanceSlip(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_topup, FragmentTopupSlip.newInstance(FragmentTopupSlip.PREVIEW, mTopup, transid, mIsFAV)).commit();

    }

    private RequestModel getRequestSubmit(){

        SubmitTopupRequestModel submitTopupRequestModel = null;

        switch (mTopup){
            case FragmentTopup.MOBILE:
            case FragmentTopup.PIN:

                submitTopupRequestModel = SubmitTopupRequestModel.SubmitTopupRequestModel(
                        String.valueOf(getmAmt()),
                        mCarrier,
                        mPhone,
                        transid,
                        mButtonID
                );
                break;
            case FragmentTopup.VAS:
                submitTopupRequestModel = SubmitTopupRequestModel.SubmitVasRequestModel(
                        String.valueOf(getmAmt()),
                        mCarrier,
                        mPhone,
                        transid,
                        mPgName,
                        mButtonID
                );
                break;
            case BillPaymentActivity.BILLPAY:
                String useBarcode = (mBarcode != null) ? "Y":"N";
                mPhone = getArguments().getString(ActivityTopup.KEY_PHONENO);

                submitTopupRequestModel = SubmitTopupRequestModel.SubmitBillRequestModel(mPreviewModel.getTNID(),
                        mBillService.getBILL_SERVICE_ID(), mBillService.getBILL_SERVICE_CODE(),
                        useBarcode, mDueDate, mPhone, transid, mChangeAmount);

                break;
        }

        return new RequestModel(mActionSumitTopup, submitTopupRequestModel);

    }



    private void initDueDateDialog(){
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_duedate, null, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false).show();
        final Button btn_select_date = dialogView.findViewById(R.id.btn_select_duedate);
        btn_select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(mDueDateTimestamp);
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mCalendar.set(year, month, dayOfMonth);
                        String strDate = dayOfMonth+"-"+
                                mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("TH"))+"-"+
                                (year+543);

                        btn_select_date.setText(strDate);

                        mDueDateTimestamp = mCalendar.getTimeInMillis();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyy");

                        mDueDate = dateFormat.format(mCalendar.getTime());

                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));


                mDatePickerDialog.setCancelable(false);
                mDatePickerDialog.show();

            }
        });

        Button btn_confirm = dialogView.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        dialogView.findViewById(R.id.text_title).setVisibility(View.VISIBLE);
        dialogView.findViewById(R.id.layout_duedate).setVisibility(View.VISIBLE);
    }

    private void startTimeoutSubmit(){
        stopTimer();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                .setTitle(R.string.warning)
                .setMessage(R.string.error_msg_timeout)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.showNotifyUpload(MyApplication.NOTITOPUP);
                        getActivity().finish();
                    }
                });

        mAlertTimeout = builder.create();

        mAlertTimeout.setOnShowListener(new MyShowListener());
        int timeout_submit = getResources().getInteger(R.integer.timeout_submit);
        mTimerTimeout = new Timer();
        mTimerTimeout.schedule(new TimerTask() {
            @Override
            public void run() {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogCounterAlert.DialogProgress.dismiss();

                            mAlertTimeout.show();

                        }
                    });

                    }
        }, timeout_submit);

//        90000

    }

    public void stopTimer(){

        if (mAlertTimeout != null) {
            mAlertTimeout.cancel();
        }

        if (mTimerTimeout != null) {
            mTimerTimeout.cancel();
            mTimerTimeout.purge();
            mTimerTimeout = null;
        }

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
                showDialogConfirmVAS(position);
            }
        }));

        for (PGResponseModel model : modelList){
                if (model.getAmount() == mFavAmt) {
                    int position = (model.getSortNo()-1);
                    setAmt(mVasAdapter.getItem(position).getAmount(), mVasAdapter.getItem(position).getPackageId());
                    mPgName = mVasAdapter.getItem(position).getServiceNameTh();
                    showDialogConfirmVAS(position);
                    break;
                }
            }
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

        try {
            if (getActivity() != null && isAdded()) {

                getChildFragmentManager().beginTransaction()
                        .replace(R.id.container_topup_package, FragmentChoiceTopup.newInstance(finalDataList, defaultPosition))
                        .commit();
            }
        } catch (IllegalStateException e){
//            getActivity().onBackPressed();
        }


    }

    private void showDialogConfirmVAS(final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                .setTitle(getString(R.string.title_phone_short)+" "+mHolder.mEditPhone.getText().toString())
                .setMessage(mVasAdapter.getAmount(position)+" "+getString(R.string.currency)+"\n"+
                        mVasAdapter.getItem(position).getServiceNameTh())
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkData()) return;
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
                });

        if (!mIsFAV){

            builder.setNegativeButton(R.string.edit_phone, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showDialogEditPhone(position);
                }
            });
        }

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new MyShowListener());
        alertDialog.show();

    }

    private boolean mFormatting;


    private void showDialogEditPhone(final int position){


        AlertDialog.Builder builderEditPhone = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                .setTitle(R.string.phone_number)
                .setView(R.layout.dialog_edit_text)
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
                final EditText editPhone = (EditText) dialogEditPhone.findViewById(R.id.edit_text);

                editPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
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

    public double getmChangeAmount() {
        return mChangeAmount;
    }

    public void setmChangeAmount(double mChangeAmount) {
        this.mChangeAmount = mChangeAmount;
        Log.e(TAG, ""+mChangeAmount);
    }

    public class ViewHolder{
//        private Button mBtnNext, mBtnTopup, mBtnCancel;
        private TextView mTextPrice, mTextHint, mTextTitleAccount, mTextTitleCarrier;
        private ImageView mLogoService;
        private EditText mEditPhone;
        private boolean mFormatting;
        private RecyclerView mRecyclerVAS;
//        private View mLayoutBtnTopup;
        private View mIncludeBottomAction;
        private LinearLayout mLayoutBillRef;
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
            mTextTitleAccount = (TextView) itemview.findViewById(R.id.title_account);
            mTextTitleCarrier = (TextView) itemview.findViewById(R.id.text_title_carrier);
            mEditPhone = (EditText) itemview.findViewById(R.id.edit_phone);
            mIncludeBottomAction = (View) itemview.findViewById(R.id.include_bottom_action);
            mRecyclerVAS = (RecyclerView) itemview.findViewById(R.id.recycler_vas);
//            mEditPhone.setOnFocusChangeListener(Util.onFocusEditText());
            mTextWatcher = new TextWatcher() {
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
            };

            mEditPhone.addTextChangedListener(mTextWatcher);

            mLayoutBillRef = itemview.findViewById(R.id.layout_bill_ref);

        }
    }

}
