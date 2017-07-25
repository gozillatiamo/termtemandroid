package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.dashboard.billpayment.BillPaymentActivity;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.AddFavRequestModel;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.LoginResponseModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.EslipRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.util.BadgeDrawable;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupSlip extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private TabLayout tabLayout;
    private String mPage;
    private byte[] mImageByte;
    private Bitmap mImageBitmap;
    private APIServices services = APIServices.retrofit.create(APIServices.class);
    private String mTransID;
    private String mFileName;
    private String mPhoneNo;
    private String mCarrier;
    private int mColorType;
    private Double mAmount;
    private String mTypeToup;
    private Drawable mIconCarrier;
    private Drawable mIconTypeTopup;
    private int mTypePage;
    private boolean mIsFav;
    private String mActionEslip = APIServices.ACTIONESLIP;


    public static final String TAG = FragmentTopupSlip.class.getSimpleName();

    public static final int PREVIEW = 0x0;
    public static final int ESLIP = 0x1;

    private static final String FAVORITE = "favorite";
    public static final String TRANID = "tranid";
    public static final String TYPEPAGE = "typepage";
    public static final String TYPETOPUP = "typetopup";



/*
    public static Fragment newInstance(byte[] imagebyte, String transid, boolean isFav){
        Bundle bundle = new Bundle();
        bundle.putByteArray(IMAGE, imagebyte);
        bundle.putString(TRANSID, transid);
        bundle.putBoolean(FAVORITE, isFav);
        FragmentTopupSlip fragment = new FragmentTopupSlip();
        fragment.setArguments(bundle);
        return fragment;
    }
*/

    public static Fragment newInstance(int type, String typetopup, String tranid, boolean isFav){
        Bundle bundle = new Bundle();
        bundle.putInt(TYPEPAGE, type);
        bundle.putString(TYPETOPUP, typetopup);
        bundle.putString(TRANID, tranid);
        bundle.putBoolean(FAVORITE, isFav);
        FragmentTopupSlip fragment = new FragmentTopupSlip();
        fragment.setArguments(bundle);
        return fragment;
    }




    public class ViewHolder{
        private Button mBtnBack, mBtnGame, mBtnSavePic;
        private TextView mTextPhone, mTextAmount, mTextSuccess;
        private ImageView mImageSlip, mIconService, mIconCarrier, mIconSuccess;
        private View mIncludeMyWallet, mLayoutInclude, mLayoutBorder;
        private ShineButton mBtnAddFavorite;
        public ViewHolder(View itemview){
            mBtnBack = (Button) itemview.findViewById(R.id.btn_back_to_dashboard);
            mBtnGame = (Button) itemview.findViewById(R.id.btn_play_game);
            mImageSlip = (ImageView) itemview.findViewById(R.id.image_slip);
            mBtnSavePic = (Button) itemview.findViewById(R.id.btn_save_pic);
            mIncludeMyWallet = (View) itemview.findViewById(R.id.include_my_wallet);
            mBtnAddFavorite = (ShineButton) itemview.findViewById(R.id.btn_add_favorite);
            mLayoutInclude = itemview.findViewById(R.id.include_layout_success);
            mTextPhone = (TextView) itemview.findViewById(R.id.text_phone_no);
            mTextAmount = (TextView) itemview.findViewById(R.id.text_amount_price);
            mIconService = (ImageView) itemview.findViewById(R.id.icon_service);
            mIconCarrier = (ImageView) itemview.findViewById(R.id.icon_carrier);
            mIconSuccess = (ImageView) itemview.findViewById(R.id.icon_success);
            mTextSuccess = (TextView) itemview.findViewById(R.id.text_success);
            mLayoutBorder = itemview.findViewById(R.id.layout_border);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        new DialogCounterAlert.DialogProgress(getContext()).show();
        mTypePage = getArguments().getInt(TYPEPAGE);
        mTypeToup = getArguments().getString(TYPETOPUP);
        mTransID = getArguments().getString(TRANID);
        mIsFav = getArguments().getBoolean(FAVORITE);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mPage = getArguments().getString("page");
/*
        mImageByte = getArguments().getByteArray(IMAGE);
        mTransID = getArguments().getString(TRANSID);
*/
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_slip, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        getBalance();
        setupDataType();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Global.getInstance().hasSubmit()){
            getActivity().finish();
            return;
        }

//        DialogCounterAlert.DialogProgress.show();
        NotificationManager mNM = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.cancel(MyApplication.NOTITOPUP);
        switch (mTypePage){
            case PREVIEW:
                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                initBtn();
                onBackPress();
                setupBtnFavorite();
                mHolder.mBtnAddFavorite.setChecked(mIsFav);
                mHolder.mBtnAddFavorite.setClickable(!mIsFav);

                setupPreviewSuccess();
                break;
            case ESLIP:
                mHolder.mLayoutInclude.setVisibility(View.GONE);
                mHolder.mBtnBack.setVisibility(View.GONE);
                serviceEslip();
                break;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupDataType(){

        switch (mTypeToup){
            case FragmentTopup.MOBILE:
                mActionEslip = APIServices.ACTIONESLIP;
                mIconTypeTopup = ContextCompat.getDrawable(getContext(), R.drawable.ic_topup);
                mColorType = ContextCompat.getColor(getContext(), R.color.color_topup);
                break;
            case FragmentTopup.PIN:
                mActionEslip = APIServices.ACTION_ESLIP_EPIN;
                mIconTypeTopup = ContextCompat.getDrawable(getContext(), R.drawable.ic_pin_code);
                mColorType = ContextCompat.getColor(getContext(), R.color.color_epin);
                break;
            case FragmentTopup.VAS:
                mActionEslip = APIServices.ACTION_ESLIP_VAS;
                mIconTypeTopup = ContextCompat.getDrawable(getContext(), R.drawable.ic_vas);
                mColorType = ContextCompat.getColor(getContext(), R.color.color_vas);

                break;
            case FragmentAddCreditChoice.AGENT_CASHIN:
                mActionEslip = APIServices.ACTION_ESLIP_AGENT_CASHIN;
                mIconTypeTopup = ContextCompat.getDrawable(getContext(), R.drawable.ic_agent_cashin);
                mColorType = ContextCompat.getColor(getContext(), R.color.color_agent_cashin);

                break;
            case BillPaymentActivity.BILLPAY:
                mActionEslip = APIServices.ACTIONESLIP;
                mIconTypeTopup = ContextCompat.getDrawable(getContext(), R.drawable.ic_bill);
                mColorType = ContextCompat.getColor(getContext(), R.color.color_bill_pay);

                break;
        }

    }

    private void setupSlip(){
        mTransID = getArguments().getString(TRANID);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        mFileName = mTransID + ".jpg";

        initEslip();

    }

    private void setupPreviewSuccess(){
        if (!Global.getInstance().hasSubmit()) return;
//        mPhoneNo = Global.getInstance().getLastSubmitPhoneNo();
        SubmitTopupRequestModel requestModel = (SubmitTopupRequestModel) Global.getInstance().getLastSubmit().getData();
        mAmount = Double.valueOf(requestModel.getAMT());
//        mCarrier = Global.getInstance().getLastSubmitCarrier();
        mPhoneNo = requestModel.getPHONENO();
        mCarrier = requestModel.getCARRIER();

        switch (mCarrier){
            case APIServices.AIS:
                switch (mTypeToup){
                    case FragmentTopup.MOBILE:
                        mIconCarrier = ContextCompat.getDrawable(getContext(), R.drawable.logo_ais);
                        break;
                    case FragmentTopup.PIN:
                        mIconCarrier = ContextCompat.getDrawable(getContext(), R.drawable.logo_ais_pin);
                        break;
                    case FragmentTopup.VAS:
                        mIconCarrier = ContextCompat.getDrawable(getContext(), R.drawable.ais_vas);
                        break;
                }

                break;
            case APIServices.TRUEMOVE:
                if (mTypeToup.equals(FragmentTopup.PIN))
                    mIconCarrier = ContextCompat.getDrawable(getContext(), R.drawable.logo_truemoney);
                else
                    mIconCarrier = ContextCompat.getDrawable(getContext(), R.drawable.logo_truemove);

                break;
            case APIServices.DTAC:
                mIconCarrier = ContextCompat.getDrawable(getContext(), R.drawable.logo_dtac);
                break;
        }

//        mTypeToup = MyApplication.getTypeToup(Global.getInstance().getLastSubmitAction());

        mHolder.mLayoutBorder.setBackgroundColor(mColorType);
        mHolder.mIconService.setImageDrawable(mIconTypeTopup);
        mHolder.mIconCarrier.setImageDrawable(mIconCarrier);
        EditText editText = new EditText(getContext());
        editText.setText(mPhoneNo);
        PhoneNumberUtils.formatNumber(editText.getText(), PhoneNumberUtils.FORMAT_NANP);
        mHolder.mTextPhone.setText(editText.getText().toString());
        NumberFormat format = NumberFormat.getInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        mHolder.mTextAmount.setText(format.format(mAmount));

        DialogCounterAlert.DialogProgress.dismiss();




/*
        mHolder.mIconSuccess.animate()
                .setStartDelay(1000)
                .setDuration(700)
                .alpha(1f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mHolder.mIconSuccess.setAlpha(0f);
                        mHolder.mIconSuccess.setVisibility(View.VISIBLE);
                    }
                }).start();
*/

        Animation loadanimation = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        loadanimation.setDuration(1000);
        loadanimation.setStartTime(2000);
        loadanimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                int width = mHolder.mTextSuccess.getMeasuredWidth();
                View view = getView().findViewById(R.id.layout_status_success);
                view.setTranslationX(((float) width)/2);
                mHolder.mIconSuccess.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                View view = getView().findViewById(R.id.layout_status_success);
                view.animate()
                        .translationX(0f)
                        .setDuration(700)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                mHolder.mTextSuccess.setAlpha(0f);
                                mHolder.mTextSuccess.setVisibility(View.VISIBLE);
                                mHolder.mTextSuccess.animate()
                                        .alpha(1f)
                                        .setDuration(1000)
                                        .start();
                            }
                        }).start();
/*
                animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
                animation.setDuration(1000);
                animation.setStartTime(3000);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mHolder.mTextSuccess.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mHolder.mTextSuccess.startAnimation(animation);
*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mHolder.mIconSuccess.startAnimation(loadanimation);



/*
        mHolder.mTextSuccess.animate()
                .setDuration(1000)
                .setStartDelay(700)
                .translationXBy(width)
                .translationX(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mHolder.mTextSuccess.setVisibility(View.VISIBLE);
                    }
                }).start();

*/
    }

    private void getBalance(){
        Call<ResponseBody> call = services.getbalance(new RequestModel(APIServices.ACTIONGETBALANCE, Global.getInstance().getLastSubmit().getData()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object values = EncryptionData.getModel(getContext(), call, response.body(), this);

                if (values instanceof String) {
                    LoginResponseModel loginResponseModel = new Gson().fromJson((String) values, LoginResponseModel.class);
                    Global.getInstance().setBALANCE(loginResponseModel.getBALANCE());
                    Global.getInstance().setMSGREAD(loginResponseModel.getMSGREAD());
                    Util.setBalanceWallet(mHolder.mIncludeMyWallet);

                }

                if (mImageBitmap != null || mTransID != null) {
                }


//                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

    }

    private void serviceEslip(){
//        if (!(MyApplication.LeavingOrEntering.currentActivity instanceof ActivityTopup)) return;

        Call<ResponseBody> call = services.eslip(new RequestModel(mActionEslip, new EslipRequestModel(mTransID, null)));

        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);

                if (responseValues == null) {
                    return;
                }

                if (responseValues instanceof ResponseModel){
                    mImageByte = Base64.decode(((ResponseModel)responseValues).getFf()
                            , Base64.NO_WRAP);

                    setupSlip();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), null, call, this, false);
            }
        });
    }


/*
    private void initHeader(){
        Call<ResponseBody> call = services.getbalance(new RequestModel(APIServices.ACTIONGETBALANCE, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ContentValues values = EncryptionData.getModel(response.body());
                if (values.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                    new DialogCounterAlert.DialogFromResponse(FragmentTopupSlip.this.getContext(), values.getAsString(EncryptionData.STRMODEL));
                } else {
                    LoginResponseModel loginResponseModel = new Gson().fromJson(values.getAsString(EncryptionData.STRMODEL), LoginResponseModel.class);
                    Global.setBALANCE(loginResponseModel.getBALANCE());
                    Util.setBalanceWallet(mHolder.mIncludeMyWallet);

                }

                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(FragmentTopupSlip.this.getContext(), call, this);
            }
        });

    }
*/

    private void onBackPress(){

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) return true;

                return false;
            }
        });

    }

    private void initEslip(){
        mImageBitmap = BitmapFactory.decodeByteArray(mImageByte, 0, mImageByte.length);
        mHolder.mImageSlip.setImageBitmap(mImageBitmap);
        saveImage();
    }

    private String getService(){

        switch (mTypeToup){
            case FragmentTopup.MOBILE:
                return ActivityReport.TOPUP_REPORT;
            case FragmentTopup.PIN:
                return ActivityReport.EPIN_REPORT;
            case FragmentTopup.VAS:
                return ActivityReport.VAS_REPORT;
            case FragmentAddCreditChoice.AGENT_CASHIN:
                return ActivityReport.CASHIN_REPORT;
        }

        return null;
    }

    private void setupBtnFavorite(){
        mHolder.mBtnAddFavorite.init(getActivity());
        mHolder.mBtnAddFavorite.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if (checked){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogWarning)
                            .setTitle(R.string.set_title_favorite)
                            .setView(R.layout.dialog_edit_text)
                            .setCancelable(false)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHolder.mBtnAddFavorite.setChecked(false);
                                }
                            })
                            .setPositiveButton(R.string.confirm, null);
                    final AlertDialog alertDialog = builder.create();


                    alertDialog.setOnShowListener(new MyShowListener(){
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            super.onShow(dialogInterface);
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText editFavoriteName = (EditText) alertDialog.findViewById(R.id.edit_text);

                                    String favName = editFavoriteName.getText().toString();
//                                    favName.replaceAll(" ", "");
                                    if (favName.trim().isEmpty()){
                                        Toast.makeText(getContext(), R.string.please_set_name, Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    mHolder.mBtnAddFavorite.setClickable(false);
                                    DataRequestModel requestModel = Global.getInstance().getLastSubmit().getData();
                                    AddFavRequestModel favRequestModel = new AddFavRequestModel(mTransID, favName, getService());
                                    favRequestModel.setAGENTID(requestModel.getAGENTID());
                                    favRequestModel.setUSERID(requestModel.getUSERID());
                                    favRequestModel.setTXID(requestModel.getTXID());

                                    Call<ResponseBody> call = services.service(new RequestModel(
                                            APIServices.ACTION_ADD_FAV, favRequestModel));

                                    APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            mIsFav = true;
                                            Global.getInstance().setSubmitIsFav(mIsFav);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });

                                    alertDialog.dismiss();
                                }
                            });
                        }
                    });

                    alertDialog.show();

                }
            }
        });
    }

    private void initBtn(){
        mHolder.mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTypePage == PREVIEW) {
                    Global.getInstance().setLastSubmit(null, false);
                }

                getActivity().finish();

/*
                if (mTypeToup.equals(FragmentTopup.MOBILE) && mCarrier.equals(APIServices.AIS)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.interesting_vas))
                            .setPositiveButton(getString(R.string.interested), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), ActivityTopup.class);
                                    intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.VAS);
                                    intent.putExtra(ActivityTopup.KEY_CARRIER, mCarrier);
                                    intent.putExtra(ActivityTopup.KEY_PHONENO, mPhoneNo);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            })
                            .setNegativeButton(getString(R.string.ignore), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new MyShowListener());
                    alertDialog.show();
                } else {
                    getActivity().finish();
                }
*/
            }
        });


        mHolder.mBtnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void saveImage(){
/*
        NotificationManager nMgr = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
*/
        if (mFileName == null) return;


        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        File myDirOld = new File(root + "/WealthCounterSlip");
        File myDir = new File(root + "/TermTemSlip");
        if(myDirOld.exists()){
            Log.e(TAG, "isHasOldDir");
            myDirOld.renameTo(myDir);
        }
        myDir.mkdirs();

        File file = new File(myDir, mFileName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            MediaScannerConnection.scanFile(FragmentTopupSlip.this.getContext(), new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);

            DialogCounterAlert.DialogProgress.dismiss();

            Toast.makeText(getContext(), getString(R.string.save_eslip_success), Toast.LENGTH_LONG).show();


/*
            DataRequestModel dataRequestModel = Global.getInstance().getLastSubmit().getData();
            SubmitTopupRequestModel submitTopupRequestModel = (SubmitTopupRequestModel) dataRequestModel;
            EslipRequestModel eslipRequestModel = new EslipRequestModel(Global.getInstance().getLastTranId(), submitTopupRequestModel.getPHONENO());
            eslipRequestModel.setUSERID(dataRequestModel.getUSERID());
            eslipRequestModel.setTXID(dataRequestModel.getTXID());
            eslipRequestModel.setDEVICEID(dataRequestModel.getDEVICEID());
            eslipRequestModel.setAGENTID(dataRequestModel.getAGENTID());

            Call<ResponseBody> call = services.saveSlip(
                    new RequestModel(APIServices.ACTIONSAVESLIP,
                            eslipRequestModel));

            APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    DialogCounterAlert.DialogProgress.dismiss();
                    mFileName = null;
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(FragmentTopupSlip.this.getContext(), call, this);
                }

            });
*/

        } catch (Exception e) {
            e.printStackTrace();
            new DialogCounterAlert(getContext(), getString(R.string.error), getString(R.string.save_eslip_fail), null);
        }


    }
}
