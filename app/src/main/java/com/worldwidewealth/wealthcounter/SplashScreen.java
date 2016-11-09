package com.worldwidewealth.wealthcounter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.worldwidewealth.wealthcounter.dialog.DialogNetworkError;
import com.worldwidewealth.wealthcounter.model.PreModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.until.GPSTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class SplashScreen extends AppCompatActivity{

    private static final String TAG = "FCM";
    protected String mAction;
    protected double mLat, mLong;
    private APIServices services;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        services = APIServices.retrofit.create(APIServices.class);
        initFCM();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataDevice();
            }
        }, 5000);

    }

    protected void getDataDevice(){
        mAction = "PRE";
        Global.setTOKEN(FirebaseInstanceId.getInstance().getToken());
        Global.setDEVICEID(Secure.getString(this.getContentResolver(), Secure.ANDROID_ID));

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


            PreModel mPreModel = new PreModel(mAction, new PreModel.Data(
                    Global.getTOKEN(),
                    Global.getDEVICEID(),
                    mLat,
                    mLong,
                    Configs.getPLATFORM()
            ));

            SendDataService(mPreModel);
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    protected  void SendDataService(PreModel model){

//        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//        startActivity(intent);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        finish();

        if (model != null) {
            Call<ResponseModel> call = services.PRE(model);
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    ResponseModel model = response.body();

                    Global.setTXID(model.getTXID());
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();

                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    t.printStackTrace();
                    new DialogNetworkError(SplashScreen.this);
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

}
