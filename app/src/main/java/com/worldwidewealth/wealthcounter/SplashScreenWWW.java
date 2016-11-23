package com.worldwidewealth.wealthcounter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.worldwidewealth.wealthcounter.dashboard.ActivityDashboard;
import com.worldwidewealth.wealthcounter.dialog.DialogNetworkError;
import com.worldwidewealth.wealthcounter.model.DataRequestModel;
import com.worldwidewealth.wealthcounter.model.PreRequestModel;
import com.worldwidewealth.wealthcounter.model.RequestModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthcounter.until.GPSTracker;
import com.worldwidewealth.wealthcounter.until.Until;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class SplashScreenWWW extends AppCompatActivity{

    private static final String TAG = "FCM";
    protected String mAction;
    protected double mLat, mLong;
    private APIServices services;
    private Runnable runnable;
    private Handler handler;

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
                SharedPreferences sharedPref = getSharedPreferences(Until.KEYPF, Context.MODE_PRIVATE);
                boolean logout = sharedPref.getBoolean("LOGOUT", true);
                if (!logout) {

                    Global.setTXID(sharedPref.getString("TXID", null));
                    Global.setAGENTID(sharedPref.getString("AGENTID", null));
                    Global.setUSERID(sharedPref.getString("USERID", null));
                    Global.setDEVICEID(sharedPref.getString("DEVICEID", null));

                    Call<ResponseBody> call = services.logout(new RequestModel(APIServices.ACTIONLOGOUT, new DataRequestModel()));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Global.setAGENTID("");
                            Global.setUSERID("");
                            Global.setBALANCE(0.00);
                            Global.setTXID("");
                            Global.setDEVICEID("");
                            Until.setSharedPreferences(SplashScreenWWW.this, true);
                            getDataDevice();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }else {
                    getDataDevice();
                }
            }
        };
        initFCM();

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    protected void getDataDevice(){
        mAction = "PRE";
        Global.setTOKEN(FirebaseInstanceId.getInstance().getToken());
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        Global.setDEVICEID(Secure.getString(this.getContentResolver(), Secure.ANDROID_ID));
        Global.setDEVICEID(mngr.getDeviceId());
        Log.e("DeviceId", Global.getDEVICEID());

        Log.e("SerialNumber", Build.SERIAL);
        Log.e("BuileID", Build.ID);
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()){
            mLat = gpsTracker.getLatitude();
            mLong = gpsTracker.getLongitude();
            Log.d("InAppData", "action:" + mAction + "\n" +
                    "token:" + Global.getTOKEN() + "\n" +
                    "device_id:" + Global.getDEVICEID() + "\n" +
                    "lat:" + mLat + "\n" +
                    "long:" + mLong + "\n" +
                    "platform:" + Configs.getPLATFORM());


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

    protected  void SendDataService(PreRequestModel model){

//        Intent intent = new Intent(SplashScreenWWW.this, MainActivity.class);
//        startActivity(intent);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        finish();

        if (model != null) {
            Call<ResponseModel> call = services.PRE(model);
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    ResponseModel model = response.body();

                    if (model.getStatus() == APIServices.SUCCESS) {
                        Global.setTXID(model.getTXID());
//                    EncryptionData.EncryptData("12345", "123456789");
                        Intent intent = new Intent(SplashScreenWWW.this, SplashScreenCounter.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        Toast.makeText(SplashScreenWWW.this, model.getMsg(), Toast.LENGTH_LONG).show();
                    }



                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    new ErrorNetworkThrowable(t).networkError(SplashScreenWWW.this);
                }
            });
        }

    }

    private void initFCM(){
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        Log.d(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
//        writeToFile(FirebaseInstanceId.getInstance().getToken());
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
