package com.worldwidewealth.termtem.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Until;
import com.worldwidewealth.termtem.BuildConfig;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MainActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.SplashScreenWWW;
import com.worldwidewealth.termtem.dashboard.ActivityDashboard;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.PreRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SignInRequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 08-Mar-17.
 */

public class TermTemSignIn {

    private Context mContext;
    private APIServices services;
    private String mUsername, mPassword, mTXID;
    private TYPE mType;

    private static AlertDialog AlertWifi;

    public static final String TAG = TermTemSignIn.class.getSimpleName();

    public enum TYPE{
        NEWLOGIN(0),
        RELOGIN(1);

        private int type;

        TYPE(int i) {
            this.type = i;
        }

        public int getType() {
            return type;
        }
    }


    public TermTemSignIn(Context context, TYPE type) {
        if (AlertWifi != null){
            AlertWifi.dismiss();
        }
        this.mContext = context;
        this.services = APIServices.retrofit.create(APIServices.class);
        this.mType = type;
    }

    public void getTXIDfromServer(){

        GPSTracker gpsTracker = new GPSTracker(mContext);
        if (gpsTracker.canGetLocation()){
            double mLat = gpsTracker.getLatitude();
            double mLong = gpsTracker.getLongitude();

            PreRequestModel mPreRequestModel = new PreRequestModel("PRE", new PreRequestModel.Data(
                    Global.getInstance().getTOKEN(),
                    Global.getInstance().getDEVICEID(),
                    mLat,
                    mLong,
                    mContext.getString(R.string.platform)
            ));

            SendDataService(mPreRequestModel);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void SendDataService(PreRequestModel model){
        if (model != null) {
            Call<ResponseBody> call = services.PRE(model);
            APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Object responseValues = EncryptionData.getModel(mContext, call, response.body(), this);
                    if (responseValues == null) return;

                    if (responseValues instanceof ResponseModel){
                        ResponseModel responseModel = (ResponseModel)responseValues;

                        if (responseModel.getShow() == APIServices.SUCCESS) {
                            Log.e(TAG, "TXID from PRE: "+responseModel.getTXID());
                            mTXID = responseModel.getTXID();
                            switch (mType){
                                case NEWLOGIN:
                                    if (checkVersionApp(responseModel.getVersion())) {
                                        startLogin();
                                    }
                                    break;
                                case RELOGIN:
                                    String username = EncryptionData.DecryptData(Global.getInstance().getUSERNAME(),
                                            Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
                                    String password = EncryptionData.DecryptData(Global.getInstance().getPASSWORD(),
                                            Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());

                                    checkWifi(username, password, mTXID);
                                    break;
                            }
                        } else {
                            AlertDialog builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogWarning)
                                    .setCancelable(false)
                                    .setTitle(R.string.warning)
                                    .setMessage(responseModel.getDesc())
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((Activity)mContext).finish();
                                        }
                                    }).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(mContext, call, this);
                }
            });
        }

    }

    private void startLogin(){
        Global.getInstance().setTXID(mTXID);
        Intent intent = new Intent(mContext, MainActivity.class);
        ((Activity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();

    }

    private boolean checkVersionApp(String version){
        String currentVersion = (BuildConfig.VERSION_NAME).split("-")[0];
        if (!version.equals(currentVersion)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogWarning);
            builder.setTitle(mContext.getString(R.string.update_app_title));
            builder.setMessage(mContext.getString(R.string.update_message));
            builder.setCancelable(false);
            builder.setPositiveButton(mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

//                    String server = context.getString(R.string.server);

                    String packageName = mContext.getApplicationContext().getPackageName();
                    Log.e(TAG, "PackageName: "+packageName);
                    if (packageName.contains("demo")||packageName.contains("dev")) {
                        startLogin();
                        return;
                    }

                    try {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id" + BuildConfig.APPLICATION_ID)));
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity)mContext).finish();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(mContext.getResources()
                                    .getColor(android.R.color.holo_orange_dark));
                    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(mContext.getResources()
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

    public void checkWifi(String username, String password, String TXID){
        new DialogCounterAlert.DialogProgress(mContext);

        this.mUsername = username;
        this.mPassword = password;
        this.mTXID = TXID;
        Log.e(TAG, "Username: "+username);
        Log.e(TAG, "Password: "+password);
        Log.e(TAG, "TXID: "+TXID);

        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setMessage(R.string.alert_sure_connect_wifi)
                    .setCancelable(false)
                    .setPositiveButton(R.string.use_wifi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            serviceAcceptWIFI();
                        }
                    })
                    .setNegativeButton(R.string.close_wifi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DialogCounterAlert.DialogProgress.dismiss();
                            mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
            AlertWifi = builder.create();
            AlertWifi.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
                }
            });
            AlertWifi.show();

        } else {
            Login();
        }

    }

    private void serviceAcceptWIFI(){
        Call<ResponseBody> call = services.service(new RequestModel(APIServices.ACTIONACCPWIFI,
                new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(mContext, call, response.body(), this);
                if (responseValues != null) Login();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(mContext, call, this);
            }
        });
    }


    private void Login(){

        final String usernameEncode = EncryptionData.EncryptData(mUsername.replaceAll("-", ""), Global.getInstance().getDEVICEID()+mTXID);
        mPassword = EncryptionData.EncryptData(mPassword, Global.getInstance().getDEVICEID()+mTXID);

        Global.getInstance().setTXID(mTXID);

        Call<ResponseBody> call = services.LOGIN(new SignInRequestModel(new SignInRequestModel.Data(
                Global.getInstance().getDEVICEID(),
                mContext.getString(R.string.platform),
                usernameEncode,
                mPassword,
                Global.getInstance().getTXID())));

        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(mContext, call, response.body(), this);

                if (responseValues instanceof ResponseModel){
                    ResponseModel responseModel = (ResponseModel) responseValues;
                    String[] strRegisDevice = responseModel.getMsg().toString().split(":");
                    if (strRegisDevice != null) {
                        if (strRegisDevice.length == 4) {
                            registerDevice(strRegisDevice[1], strRegisDevice[2], strRegisDevice[3]);
                        } else {
                            Toast.makeText(mContext, responseModel.getMsg(),
                                    Toast.LENGTH_SHORT).show();
                            if (mType.equals(TYPE.RELOGIN)){
                                Global.getInstance().clearUserData(true);
                                Util.backToSignIn(((Activity)mContext));
                            }
                        }
                    }

                } else {
                    ContentValues values = new ContentValues();
                    values.put(Global.getKeyUSERNAME(), usernameEncode);
                    values.put(Global.getKeyPASSWORD(), mPassword);
                    values.put(Global.getKeyUSERDATA(), (String)responseValues);
                    Global.getInstance().setUserData(values);

                    switch (mType){
                        case NEWLOGIN:
                            Global.getInstance().setCacheUser(mUsername);
                            mUsername = null;
                            Intent intent = new Intent(mContext, ActivityDashboard.class);
                            ((Activity)mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                            mContext.startActivity(intent);
                            ((Activity)mContext).finish();

                            break;
                    }

                }

                DialogCounterAlert.DialogProgress.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(mContext, call, this);
            }
        });

    }

    private void registerDevice(String msg, final String agantId, final String userId){
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(msg)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DialogCounterAlert.DialogProgress(mContext);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Call<ResponseModel> call = services.registerDevice(
                                        new RequestModel(APIServices.ACTIONREGISTERDEVICE,
                                                new DataRequestModel(agantId, userId)));

                                APIHelper.enqueueWithRetry(call, new Callback<ResponseModel>() {
                                    @Override
                                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                        DialogCounterAlert.DialogProgress.dismiss();
                                        new DialogCounterAlert(mContext,
                                                mContext.getString(R.string.register),
                                                mContext.getString(R.string.register_device_done),
                                                null);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                                        t.printStackTrace();
                                        new ErrorNetworkThrowable(t).networkError(mContext, call, this);
                                    }
                                });

                            }
                        }, 1000);
                    }
                }).show();
    }


}
