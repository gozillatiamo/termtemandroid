package com.worldwidewealth.wealthcounter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Excluder;
import com.worldwidewealth.wealthcounter.dashboard.ActivityDashboard;
import com.worldwidewealth.wealthcounter.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthcounter.dialog.DialogNetworkError;
import com.worldwidewealth.wealthcounter.model.DataRequestModel;
import com.worldwidewealth.wealthcounter.model.LoginResponseModel;
import com.worldwidewealth.wealthcounter.model.RequestModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.model.SignInRequestModel;
import com.worldwidewealth.wealthcounter.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthcounter.until.Until;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mHolder;
    private String TAG = "Main";
    private APIServices services;
    private String mPhone, mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHolder = new ViewHolder(this);

        services = APIServices.retrofit.create(APIServices.class);
        initEditText();
        initBtn();
//        initSpinner();

    }


    private void initEditText(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHolder.mPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("TH"));
        }
    }

    private void initSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.phone_country, R.layout.text_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.mSpinnerPhoneCountry.setAdapter(adapter);
        mHolder.mSpinnerPhoneCountry.setSelection(0);

    }

    private void initBtn(){
        mHolder.mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                Intent intent = new Intent(MainActivity.this, ActivityDashboard.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                finish();
*/

                mPhone = mHolder.mPhone.getText().toString();
                mPassword = mHolder.mPassword.getText().toString();
                if (mPhone.equals("") || mPassword.equals("")) {
                    Toast.makeText(MainActivity.this, getString(R.string.please_enter_data), Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("LoginData:", "action:LOGIN" + "\n"+
                        "DEVICEID:" + Global.getDEVICEID() + "\n" +
                        "PLATFORM:" + Configs.getPLATFORM() + "\n" +
                        "USERNAME:" + mPhone.replace(" ", "") + "\n" +
                        "PASSWORD:" + mPassword + "\n" +
                        "TXIK:" + Global.getTXID());
                new DialogCounterAlert.DialogProgress(MainActivity.this);

                Call<ResponseBody> call = services.LOGIN(new SignInRequestModel(new SignInRequestModel.Data(
                        Global.getDEVICEID(),
                        Configs.getPLATFORM(),
                        EncryptionData.EncryptData(mPhone.replace(" ", ""), Global.getDEVICEID()+Global.getTXID()),
                        EncryptionData.EncryptData(mPassword, Global.getDEVICEID()+Global.getTXID()),
                        Global.getTXID())));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Gson gson = new Gson();
                        DialogCounterAlert.DialogProgress.dismiss();
                        String strResponse = null;
                        ResponseModel responseModel = null;
                        try {
                           strResponse = response.body().string();
                            JsonParser jsonParser = new JsonParser();

                            JsonObject jsonObject = jsonParser.parse(strResponse).getAsJsonObject();
                            responseModel = gson.fromJson(jsonObject, ResponseModel.class);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (responseModel != null){
                            Log.e("ResponseModel", "true");
//                            "Msg":"002:Please register your device first.:bb4e2a11-52c8-42fe-aa00-5df68125e61c:4d10edee-c7b8-4a36-abc1-b58fa5784398"

                            String[] strRegisDevice = responseModel.getMsg().split(":");
                            if (strRegisDevice.length > 1){
                                registerDevice(strRegisDevice[1], strRegisDevice[2], strRegisDevice[3]);
                            } else{
                                Toast.makeText(MainActivity.this, responseModel.getMsg(),
                                        Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Log.e("ResponseModel", "false");
                            String responseStr = strResponse;
                            String converted = Until.ConvertJsonEncode(responseStr);
                            String responDecode = Until.decode(converted);
                            Log.e("strResponse", converted);
                            Log.e("strDecode", responDecode);

                            //String json = gson.toJson(responDecode);
                            LoginResponseModel loginResponseModel = gson.fromJson(responDecode, LoginResponseModel.class);
                            Global.setUSERID(loginResponseModel.getUSERID());
                            Global.setAGENTID(loginResponseModel.getAGENTID());
                            Log.e("AgenidDecrytp",  EncryptionData.DecryptData(Global.getAGENTID(), Global.getTXID()));
                            Global.setBALANCE(loginResponseModel.getBALANCE());

                            Intent intent = new Intent(MainActivity.this, ActivityDashboard.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                            finish();

                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
//                        new ErrorNetworkThrowable(t).networkError(MainActivity.this);
                    }
                });
            }
        });

        mHolder.mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityRegister.class);
                startActivity(intent);
            }
        });

    }

    private void registerDevice(String msg, String agantId, String userId){
        final DataRequestModel dataRequestModel = new DataRequestModel();
        dataRequestModel.setAGENTID(agantId);
        dataRequestModel.setUSERID(userId);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(msg)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<ResponseModel> call = services.registerDevice(new RequestModel(APIServices.ACTIONREGISTERDEVICE, dataRequestModel));
                        call.enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                new DialogCounterAlert(MainActivity.this, getString(R.string.register), R.string.register_done);
                            }

                            @Override
                            public void onFailure(Call<ResponseModel> call, Throwable t) {
                                t.printStackTrace();
                                new DialogNetworkError(MainActivity.this);
                            }
                        });
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class ViewHolder{

        private TextView mBtnRegister;
        private AppCompatButton mBtnLogin;
        private AppCompatEditText mPhone, mPassword;
        private Spinner mSpinnerPhoneCountry;

        public ViewHolder(AppCompatActivity view){

            mBtnRegister = (TextView) view.findViewById(R.id.btn_register);
            mBtnLogin = (AppCompatButton) view.findViewById(R.id.btn_login);

            mPhone = (AppCompatEditText) view.findViewById(R.id.edit_phone);

            mPassword = (AppCompatEditText) view.findViewById(R.id.edit_password);
            mSpinnerPhoneCountry = (Spinner) view.findViewById(R.id.spinner_phone_country);

        }
    }

}
