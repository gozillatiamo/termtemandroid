package com.worldwidewealth.wealthwallet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.worldwidewealth.wealthwallet.dashboard.report.ActivityReport;
import com.worldwidewealth.wealthwallet.dialog.DialogNetworkError;
import com.worldwidewealth.wealthwallet.model.DataRequestModel;
import com.worldwidewealth.wealthwallet.model.PreRequestModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.GPSTracker;
import com.worldwidewealth.wealthwallet.until.Until;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class SplashScreenWWW extends AppCompatActivity{

//    private static final String TAG = "FCM";
    protected String mAction;
    protected double mLat, mLong;
    private APIServices services;
    private Runnable runnable;
    private Handler handler;
    public static final String TAG = SplashScreenWWW.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen_www);
        services = APIServices.retrofit.create(APIServices.class);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = MyApplication.getContext().getSharedPreferences(Until.KEYPF, Context.MODE_PRIVATE);
                boolean logout = sharedPref.getBoolean("LOGOUT", true);
                if (!logout) {

                    Global.setTXID(sharedPref.getString("TXID", ""));
                    Global.setAGENTID(sharedPref.getString("AGENTID", ""));
                    Global.setUSERID(sharedPref.getString("USERID", ""));
                    Global.setDEVICEID(sharedPref.getString("DEVICEID", ""));

                    Call<ResponseBody> call = services.logout(new RequestModel(APIServices.ACTIONLOGOUT, new DataRequestModel()));
                    APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Object responseValues = EncryptionData.getModel(SplashScreenWWW.this, call, response.body(), this);
                            if (responseValues == null) return;

                            if (responseValues instanceof ResponseModel){
                                Global.setAGENTID("");
                                Global.setUSERID("");
                                Global.setBALANCE(0.00);
                                Global.setTXID("");
                                Global.setDEVICEID("");
                                Until.setLogoutSharedPreferences(MyApplication.getContext(), true);
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
        if (!initFCM()) {
            handler.postDelayed(runnable, 3000);
        }
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

    protected void getDataDevice(){
        mAction = "PRE";
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable(){

            @Override
            public void run() {
                Global.setTOKEN(FirebaseInstanceId.getInstance().getToken());
                if (Global.getTOKEN() == null || Global.getTOKEN().equals("")){
                    handler.removeCallbacks(this);
                    handler.postDelayed(this, 1000);
                }

                String deviceId = Until.getDEVICEIDSharedPreferences();

                if (deviceId == null) {
                    TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (mngr.getDeviceId() != null) {
                        deviceId = mngr.getDeviceId();
                    } else {
                        deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
                    }
                }

                Global.setDEVICEID(deviceId);

                GPSTracker gpsTracker = new GPSTracker(SplashScreenWWW.this);
                if (gpsTracker.canGetLocation()){
                    mLat = gpsTracker.getLatitude();
                    mLong = gpsTracker.getLongitude();

                    PreRequestModel mPreRequestModel = new PreRequestModel(mAction, new PreRequestModel.Data(
                            Global.getTOKEN(),
                            Global.getDEVICEID(),
                            mLat,
                            mLong,
                            Configs.getPLATFORM()
                    ));

                    SendDataService(mPreRequestModel);
                } else {
                    gpsTracker.showSettingsAlert();
                }

            }
        };

        handler.postDelayed(runnable, 1000);
    }

    protected  void SendDataService(PreRequestModel model){


        if (model != null) {
            Call<ResponseBody> call = services.PRE(model);
            APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Object responseValues = EncryptionData.getModel(SplashScreenWWW.this, call, response.body(), this);
                    if (responseValues == null) return;

                    if (responseValues instanceof ResponseModel){
                        Until.setTXIDSharedPreferences(((ResponseModel)responseValues).getTXID(), Global.getDEVICEID());
                        Intent intent = new Intent(SplashScreenWWW.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();

                    }
/*
                    if (values.getAsBoolean(EncryptionData.ASRESPONSEMODEL)){
                        ResponseModel model = new Gson().fromJson(values.getAsString(EncryptionData.STRMODEL), ResponseModel.class);

                        if (model.getStatus() == APIServices.SUCCESS) {
                        } else {
                            new ErrorNetworkThrowable(null).networkError(SplashScreenWWW.this,
                                    model.getMsg(), call, this);
                        }

                    }
*/

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(SplashScreenWWW.this, call, this);
                }
            });
        }

    }

    private boolean initFCM(){
        if (getIntent().getExtras() != null) {

            String txt = getIntent().getExtras().getString("txt");
            String box = getIntent().getExtras().getString("box");
//            Log.e(TAG, "txt: " +  txt + "\nbox: " + box);
            getIntent().replaceExtras(new Bundle());
            if (txt != null && box != null) {
                Intent intent = new Intent(this, ActivityShowNotify.class);
                intent.putExtra(MyFirebaseMessagingService.TEXT, txt);
                intent.putExtra(MyFirebaseMessagingService.BOX, box);
                startActivity(intent);
                return true;
            }
/*
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
*/
        }

//        writeToFile(FirebaseInstanceId.getInstance().getToken());

        return false;
    }

/*
    public void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "token.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
            Toast.makeText(this, "SAVE", Toast.LENGTH_SHORT).show();

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
*/

}
