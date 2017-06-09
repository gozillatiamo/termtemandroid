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

        if (Global.getInstance().getLastTranId() != null){

            if (FragmentTopupPackage.callSubmit != null){
                FragmentTopupPackage.callSubmit.cancel();
                FragmentTopupPackage.callSubmit = null;
            }

            if (FragmentTopupPackage.callSubmit == null && !Global.getInstance().getSubmitStatus()){
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
                            if (responseValues == null){
//                                new ErrorNetworkThrowable(null).networkError(SplashScreenWWW.this, call, this);
                                getDataDevice();
                                return;
                            }

                            if (responseValues instanceof ResponseModel){
                                Global.getInstance().clearUserData(true);
                                getDataDevice();

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
            handler.postDelayed(runnable, 3000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
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

                new TermTemSignIn(SplashScreenWWW.this, TermTemSignIn.TYPE.NEWLOGIN, true).getTXIDfromServer();

/*
                GPSTracker gpsTracker = new GPSTracker(SplashScreenWWW.this);
                if (gpsTracker.canGetLocation()){
                    mLat = gpsTracker.getLatitude();
                    mLong = gpsTracker.getLongitude();

                    PreRequestModel mPreRequestModel = new PreRequestModel(mAction, new PreRequestModel.Data(
                            Global.getInstance().getTOKEN(),
                            Global.getInstance().getDEVICEID(),
                            mLat,
                            mLong,
                            getString(R.string.platform)
                    ));

                    SendDataService(mPreRequestModel);
                } else {
                    gpsTracker.showSettingsAlert();
                }

*/

            }
        };

        handler.postDelayed(runnable, 1000);
    }

/*
    protected  void SendDataService(PreRequestModel model){


        if (model != null) {
            Call<ResponseBody> call = services.PRE(model);
            APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Object responseValues = EncryptionData.getModel(SplashScreenWWW.this, call, response.body(), this);
                    if (responseValues == null) return;

                    if (responseValues instanceof ResponseModel){
                        ResponseModel responseModel = (ResponseModel)responseValues;

                        if (responseModel.getShow() == APIServices.SUCCESS) {

                            if (checkVersionApp(responseModel.getVersion(), responseModel.getTXID())) {
                                startLogin(responseModel.getTXID());
                            }
                        } else {
                            AlertDialog builder = new AlertDialog.Builder(SplashScreenWWW.this, R.style.MyAlertDialogWarning)
                                    .setCancelable(false)
                                    .setTitle(R.string.warning)
                                    .setMessage(responseModel.getDesc())
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(SplashScreenWWW.this, call, this);
                }
            });
        }

    }

    private void startLogin(String txid){
        Global.getInstance().setTXID(txid);
        Intent intent = new Intent(SplashScreenWWW.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

    private boolean checkVersionApp(String version, final String txid){

        String currentVersion = (BuildConfig.VERSION_NAME).split("-")[0];
        if (!version.equals(currentVersion)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogWarning);
            builder.setTitle(getString(R.string.update_app_title));
            builder.setMessage(getString(R.string.update_message));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    String server = getString(R.string.server);

                    if (!server.contains("180.128.21.81")) {
                        startLogin(txid);
                        return;
                    }

                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id" + BuildConfig.APPLICATION_ID)));
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(getResources()
                                    .getColor(android.R.color.holo_orange_dark));
                    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(getResources()
                                    .getColor(android.R.color.holo_red_dark));
                }
            });
            try {
                alertDialog.show();
            } catch (WindowManager.BadTokenException e){}

            return false;
        }
        return true;

    }
*/
}
