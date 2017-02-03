package com.worldwidewealth.termtem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.PreRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.until.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.until.GPSTracker;
import com.worldwidewealth.termtem.until.Until;

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
    private int mRetryToken = 0;
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
                Global.setTXID(sharedPref.getString("TXID", ""));
                if (Global.getTXID().equals("")) logout = true;

                if (!logout) {

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
            handler.postDelayed(runnable, 3000);

/*
        if (!initFCM()) {
            handler.postDelayed(runnable, 3000);
        }
*/
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
                            getString(R.string.platform)
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
        Until.setTXIDSharedPreferences(txid, Global.getDEVICEID());
        Intent intent = new Intent(SplashScreenWWW.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

    private boolean checkVersionApp(String version, final String txid){

        if (!version.equals("0.1.0")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogWarning);
            alertDialog.setTitle(getString(R.string.update));
//                alertDialog.setMessage("You are using "+ExistingVersionName+" version\n"+MarketVersionName+" version is availble\nDo you which to download it?");
            alertDialog.setMessage(getString(R.string.update_message));
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id" + BuildConfig.APPLICATION_ID)));
                    }
                }
            });
            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startLogin(txid);
                }
            });
            alertDialog.show();
            return false;
        }
        return true;

    }

    private boolean initFCM(){
        if (getIntent().getExtras() != null) {

            String txt = getIntent().getExtras().getString("txt");
            String box = getIntent().getExtras().getString("box");
            getIntent().replaceExtras(new Bundle());
            if (txt != null && box != null) {
                Intent intent = new Intent(this, ActivityShowNotify.class);
                intent.putExtra(MyFirebaseMessagingService.TEXT, txt);
                intent.putExtra(MyFirebaseMessagingService.BOX, box);
                startActivity(intent);
                Log.e(TAG, "txt: "+ txt +"\nbox: "+box);
                return true;
            }
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
        }
    }
*/

}
