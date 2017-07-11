package com.worldwidewealth.termtem.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.termtem.BuildConfig;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MainActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.SplashScreenWWW;
import com.worldwidewealth.termtem.dashboard.ActivityDashboard;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.PreRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SignInRequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;

import java.util.Arrays;
import java.util.List;

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
    private String mUsername, mPassword;
    private static String mTXID;
    private static boolean isExecuting= false;
    private TYPE mType;
    private boolean isAlreadyShowProgress;

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


    public TermTemSignIn(Context context, TYPE type, boolean isAlreadyShowProgress) {
        if (AlertWifi != null && AlertWifi.isShowing()){
            AlertWifi.dismiss();
        }
        this.mContext = context;
        if (context instanceof SplashScreenWWW){
            isExecuting = false;
        }
        this.services = APIServices.retrofit.create(APIServices.class);
        this.mType = type;
        this.isAlreadyShowProgress = isAlreadyShowProgress;
    }

    public void getTXIDfromServer(){
        if (isExecuting) return;

        isExecuting = true;

        GPSTracker gpsTracker = new GPSTracker(mContext);
        if (gpsTracker.canGetLocation()){
            double mLat = gpsTracker.getLatitude();
            double mLong = gpsTracker.getLongitude();

            final PreRequestModel mPreRequestModel = new PreRequestModel("PRE", new PreRequestModel.Data(
                    Global.getInstance().getTOKEN(),
                    Global.getInstance().getDEVICEID(),
                    mLat,
                    mLong,
                    mContext.getString(R.string.platform)
            ));

            if (Global.getInstance().getAGENTID() != null){
                Call<ResponseBody> call = services.logout(new RequestModel(APIServices.ACTIONLOGOUT,
                        new DataRequestModel()));
                APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Object responseValues = EncryptionData.getModel(mContext, call, response.body(), this);

                        if (responseValues instanceof ResponseModel &&
                                ((ResponseModel)responseValues).getMsg().equals(APIServices.MSG_SUCCESS)){
                            Global.getInstance().clearUserData();
                            SendDataService(mPreRequestModel);
                        } else {
                            if (Global.getInstance().getTXID() != null) {
                                APIHelper.enqueueWithRetry(call.clone(), this);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(mContext, call, this);
                        isExecuting = false;
                    }
                });
            } else {
                SendDataService(mPreRequestModel);
            }

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private boolean checkAdvoidDevice(){
        List<String> listDevice = Arrays.asList(mContext.getResources().getStringArray(R.array.list_advoid_device_id));
        return listDevice.contains(Global.getInstance().getDEVICEID());
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

                        if (responseModel.getShow() == APIServices.SUCCESS || checkAdvoidDevice()) {
                            Log.e(TAG, "TXID from PRE: "+responseModel.getTXID());
                            mTXID = responseModel.getTXID();

                            switch (mType){
                                case NEWLOGIN:
                                    if (checkVersionApp(responseModel.getVersion())) {
                                        startLogin();
                                    }
                                    break;
                                case RELOGIN:
                                    Log.e(TAG, "Username before encode: "+Global.getInstance().getUSERNAME());
                                    Log.e(TAG, "Password before encode: "+Global.getInstance().getPASSWORD());
                                    Log.e(TAG, "TXID before encode: "+Global.getInstance().getTXID());
                                    Log.e(TAG, "DeviceId before encode: "+Global.getInstance().getDEVICEID());
                                    String username = EncryptionData.DecryptData(Global.getInstance().getUSERNAME(),
                                            Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
                                    String password = EncryptionData.DecryptData(Global.getInstance().getPASSWORD(),
                                            Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
                                    Global.getInstance().setTXID(mTXID);
                                    checkWifi(username, password);
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
                                    }).create();

                            builder.setOnShowListener(new MyShowListener());
                            builder.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(mContext, call, this);
                    isExecuting = false;

                }
            });
        }

    }

    private void startLogin(){
        Global.getInstance().setTXID(mTXID);
        Intent intent = new Intent(mContext, MainActivity.class);
        ((AppCompatActivity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mContext.startActivity(intent);
        ((AppCompatActivity)mContext).finish();
    }

    private boolean checkVersionApp(String version){
        if (checkAdvoidDevice()) return true;

        String strCurrentVersion = (BuildConfig.VERSION_NAME).split("-")[0];
        String removeDotVersion = version.replaceAll("\\.", "");
        String removeDotCurrentVersion = strCurrentVersion.replaceAll("\\.", "");
        Log.e(TAG, "serverVersion: "+removeDotVersion);
        Log.e(TAG, "currentVersion: "+removeDotCurrentVersion);


        int serverVersion = Integer.parseInt(removeDotVersion);
        int currentVersion = Integer.parseInt(removeDotCurrentVersion);
        Log.e(TAG, "serverVersion: "+serverVersion);
        Log.e(TAG, "currentVersion: "+currentVersion);

        if (currentVersion < serverVersion) {
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
            alertDialog.setOnShowListener(new MyShowListener());
            try {
                alertDialog.show();
            } catch (Exception e){}

            return false;
        }
        return true;

    }

    public void checkWifi(String username, String password){
//        new DialogCounterAlert.DialogProgress(mContext);

        this.mUsername = username;
        this.mPassword = password;
        Log.e(TAG, "Username: "+mUsername);
        Log.e(TAG, "Password: "+mPassword);

        if (mUsername == null || mUsername.isEmpty() || mPassword.isEmpty()){
            DialogCounterAlert.DialogProgress.dismiss();
            Util.backToSignIn((Activity) mContext);
        }

        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            switch (mType){
                case NEWLOGIN:
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogWarning)
                            .setTitle(R.string.warning)
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
                    AlertWifi.setOnShowListener(new MyShowListener());
                    AlertWifi.show();
                    TextView messageText = (TextView) AlertWifi.findViewById(android.R.id.message);
                    messageText.setGravity(Gravity.CENTER);
                    break;
                case RELOGIN:
                    serviceAcceptWIFI();
                    break;
            }

        } else {
            Login();
        }

    }

    private void serviceAcceptWIFI(){
        Log.e(TAG, "TXID before acceptwifi: "+Global.getInstance().getTXID());
        Call<ResponseBody> call = services.service(new RequestModel(APIServices.ACTIONACCPWIFI,
                new DataRequestModel(null, null)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(mContext, call, response.body(), this);
                if (responseValues != null) Login();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(mContext, call, this);
                isExecuting = false;

            }
        });
    }


    private void Login(){

        final String usernameEncode = EncryptionData.EncryptData(mUsername.replaceAll("-", ""), Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
        mPassword = EncryptionData.EncryptData(mPassword, Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());

//        Global.getInstance().setTXID(mTXID);

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
//                                Global.getInstance().clearUserData();
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

                    if (!isAlreadyShowProgress){
                        DialogCounterAlert.DialogProgress.dismiss();
                    }
                    switch (mType){
                        case NEWLOGIN:
                            Global.getInstance().setCacheUser(mUsername);
                            mUsername = null;
                            Intent intent = null;

                            if(BuildConfig.FLAVOR.equals("demoairtime")) {
                                try {
                                    intent = new Intent(mContext, Class.forName("com.worldwidewealth.termtem.demoairtime.dashboard.ActivityDashboard"));
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                intent = new Intent(mContext, ActivityDashboard.class);
                            }
                            mContext.startActivity(intent);
                            ((AppCompatActivity)mContext).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                            ((AppCompatActivity)mContext).finish();
                            break;
                    }

                }

                isExecuting = false;
//                DialogCounterAlert.DialogProgress.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(mContext, call, this);
                isExecuting = false;
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
                        new DialogCounterAlert.DialogProgress(mContext).show();
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
/*
                                        new DialogCounterAlert(mContext,
                                                mContext.getString(R.string.register),
                                                mContext.getString(R.string.register_device_done),
                                                null);
*/

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogWarning)
                                                .setTitle(mContext.getString(R.string.register))
                                                .setMessage(mContext.getString(R.string.register_device_done))
                                                .setPositiveButton(R.string.confirm, null);

                                        AlertDialog alertDialogDone = builder.create();
                                        alertDialogDone.setOnShowListener(new MyShowListener());
                                        alertDialogDone.show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                                        t.printStackTrace();
                                        new ErrorNetworkThrowable(t).networkError(mContext, call, this);
                                        isExecuting = false;

                                    }
                                });

                            }
                        }, 1000);
                    }
                }).create();

        alertDialog.setOnShowListener(new MyShowListener());
        alertDialog.show();
    }


}
