package com.worldwidewealth.termtem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.PreRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.GPSTracker;
import com.worldwidewealth.termtem.util.TermTemSignIn;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.worldwidewealth.termtem.MyApplication.getContext;
import static com.worldwidewealth.termtem.MyApplication.getTitleTypeToup;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class SplashScreenWWW extends MyAppcompatActivity{

    protected String mAction;
    protected double mLat, mLong;
    private APIServices services;
    private Runnable runnable;
    private Handler handler;
    private int mRetryToken = 0;
    public static final String TAG = SplashScreenWWW.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen_www);
        services = APIServices.retrofit.create(APIServices.class);

        String deviceId = Global.getInstance().getDEVICEID();

        if (deviceId == null) {
            TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (mngr.getDeviceId() != null) {
                deviceId = mngr.getDeviceId();
            } else {
                deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
            }
        }

        Global.getInstance().setDEVICEID(deviceId);


        if (Global.getInstance().getLastTranId() != null){

            if ((FragmentTopupPackage.callSubmit == null || FragmentTopupPackage.callSubmit.isCanceled())
                    && !Global.getInstance().getSubmitStatus()){
                showLastSubmit();
            }
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                if (Global.getInstance().getAGENTID() != null) {

                    Call<ResponseBody> call = services.logout(new RequestModel(APIServices.ACTIONLOGOUT, new DataRequestModel()));
                    APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Object responseValues = EncryptionData.getModel(SplashScreenWWW.this, call, response.body(), this);

                            if (responseValues instanceof ResponseModel &&
                                    ((ResponseModel)responseValues).getMsg().equals(APIServices.MSG_SUCCESS)){
                                Global.getInstance().clearUserName();
//                                Global.getInstance().clearUserData();
                                getDataDevice();
                            } else {
                                if (Global.getInstance().getTXID() != null) {
                                    APIHelper.enqueueWithRetry(call.clone(), this);
                                } else {
                                    getDataDevice();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            new ErrorNetworkThrowable(t).networkError(SplashScreenWWW.this, call, this);
                        }
                    });
                } else {
                    getDataDevice();
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        Locale locale = new Locale("TH");
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
            handler.post(runnable);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        GPSTracker.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    private void showLastSubmit(){

        final SubmitTopupRequestModel submitModel = (SubmitTopupRequestModel) Global.getInstance().getLastSubmit().getData();

        final String tranId = Global.getInstance().getLastTranId();
        final String action = Global.getInstance().getLastSubmitAction();
        final String title = getTitleTypeToup(action);

        Call<ResponseBody> callSubmit = services.topupService(new RequestModel(action, submitModel));

        MyApplication.showNotifyUpload(MyApplication.NOTITOPUP,
                tranId,
                title+" " +submitModel.getCARRIER()+" "+submitModel.getAMT()+" "
                        +getContext().getString(R.string.currency),
                getContext().getString(R.string.phone_number)+" "+submitModel.getPHONENO()+" "
                        +getContext().getString(R.string.processing),
                android.R.drawable.stat_notify_sync);

        APIHelper.enqueueWithRetry(callSubmit, new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Object responseValues = EncryptionData.getModel(getContext(), call, response.body(), this);
                if (responseValues == null) {

                    MyApplication.uploadFail(MyApplication.NOTITOPUP,
                            tranId,
                            title + " " + submitModel.getCARRIER() + " " + submitModel.getAMT() + " "
                                    + MyApplication.getContext().getString(R.string.currency),
                            MyApplication.getContext().getString(R.string.phone_number) + " " + submitModel.getPHONENO() + " "
                                    + MyApplication.getContext().getString(R.string.msg_upload_fail),
                            android.R.drawable.stat_sys_warning);

                    return;
                }

                if (responseValues instanceof ResponseModel) {

                    MyApplication.uploadSuccess(MyApplication.NOTITOPUP, tranId,
                            title + " " + submitModel.getCARRIER() + " " + submitModel.getAMT() +
                                    " " + MyApplication.getContext().getString(R.string.currency),
                            MyApplication.getContext().getString(R.string.phone_number) + " " +
                                    submitModel.getPHONENO() + " " + MyApplication.getContext().getString(R.string.success),
                            R.drawable.ic_check_circle_white);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t.getMessage().equals("Canceled")) return;

                MyApplication.uploadFail(MyApplication.NOTITOPUP, tranId,
                        title + " " + submitModel.getCARRIER() + " " + submitModel.getAMT() +
                                " " + MyApplication.getContext().getString(R.string.currency),
                        MyApplication.getContext().getString(R.string.phone_number) + " " + submitModel.getPHONENO() +
                                " " + MyApplication.getContext().getString(R.string.msg_upload_fail),
                        android.R.drawable.stat_sys_warning);

            }
        });

    }

    protected void getDataDevice(){
        mAction = "PRE";

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable(){

            @Override
            public void run() {
                Global.getInstance().setTOKEN(FirebaseInstanceId.getInstance().getToken());

                if (Global.getInstance().getTOKEN() == null){
                    handler.removeCallbacks(this);
                    mRetryToken++;
                    if (mRetryToken == 10){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenWWW.this)
                                .setMessage(R.string.network_error_message)
                                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SplashScreenWWW.this.recreate();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SplashScreenWWW.this.finish();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE)
                                        .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            }
                        });
                    }else {
                        handler.postDelayed(this, 1000);
                    }
                    return;
                }

                new TermTemSignIn(SplashScreenWWW.this, TermTemSignIn.TYPE.NEWLOGIN, true).getTXIDfromServer();


            }
        };

        handler.postDelayed(runnable, 1000);
    }

}
