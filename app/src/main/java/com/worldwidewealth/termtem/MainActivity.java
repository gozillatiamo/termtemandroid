package com.worldwidewealth.termtem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.worldwidewealth.termtem.dashboard.ActivityDashboard;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.DialogHelp;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.LoginResponseModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SignInRequestModel;
import com.worldwidewealth.termtem.model.UserMenuModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.until.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.until.Until;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mHolder;
    private String TAG = "Main";
    private APIServices services;
    private String mPhone, mPassword;
    private SharedPreferences mShared;
    private Set<String> mSetHistoryUser;
    public static final String CACHEUSER = "cacheuser";
    public static final String USER = "user";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        mHolder = new ViewHolder(this);
        Until.setupUI(findViewById(R.id.layout_parent));
        services = APIServices.retrofit.create(APIServices.class);
        initEditText();
        initBtn();
//        initSpinner();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHolder.mBtnLogin.setEnabled(true);
        Global.setTXID(Until.getTXIDSharedPreferences());
        Global.setDEVICEID(Until.getDEVICEIDSharedPreferences());
    }

    private boolean mFormatting;
    private String lastChar = " ";
    private void initEditText(){
        mHolder.mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mFormatting){
                    mFormatting = true;
                    PhoneNumberUtils.formatNumber(s, PhoneNumberUtils.FORMAT_NANP);
                    mFormatting = false;
                }
            }
        });

        mHolder.mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO){
                    login();
                }
                return false;
            }
        });

        mShared = getSharedPreferences(CACHEUSER, Context.MODE_PRIVATE);
        mSetHistoryUser = new HashSet<>(mShared.getStringSet(USER, new HashSet<String>()));

        if (mSetHistoryUser.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                    mSetHistoryUser.toArray(new String[mSetHistoryUser.size()]));
            mHolder.mPhone.setAdapter(adapter);
            mHolder.mPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mHolder.mPhone.isPopupShowing())
                        mHolder.mPhone.showDropDown();
                }
            });
        }
    }

    private void initSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.phone_country, R.layout.text_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.mSpinnerPhoneCountry.setAdapter(adapter);
        mHolder.mSpinnerPhoneCountry.setSelection(0);

    }

    private void login(){
        mPhone = mHolder.mPhone.getText().toString().replaceAll("-", "");
        mPassword = mHolder.mPassword.getText().toString();
        if (mPhone.equals("") || mPassword.equals("")) {
            Toast.makeText(MainActivity.this, getString(R.string.please_enter_data), Toast.LENGTH_SHORT).show();
            return;
        }

        mHolder.mBtnLogin.setEnabled(false);


        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(R.string.alert_sure_connect_wifi)
                    .setPositiveButton(R.string.use_wifi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DialogCounterAlert.DialogProgress(MainActivity.this);
                            serviceLogin();
                        }
                    })
                    .setNegativeButton(R.string.close_wifi, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            DialogCounterAlert.DialogProgress.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mHolder.mBtnLogin.setEnabled(true);
                }
            });
            alertDialog.show();
//                    new DialogCounterAlert(MainActivity.this, null, getString(R.string.alert_connect_wifi), null);
//                    return;
        } else {
            new DialogCounterAlert.DialogProgress(MainActivity.this);
            serviceLogin();
        }

    }

    private void initBtn(){
        mHolder.mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mHolder.mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityRegister.class);
                startActivity(intent);
            }
        });

        mHolder.mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new DialogHelp(MainActivity.this).show();
            }
        });

    }

    private void serviceLogin(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = services.LOGIN(new SignInRequestModel(new SignInRequestModel.Data(
                        Global.getDEVICEID(),
                        getString(R.string.platform),
                        EncryptionData.EncryptData(mPhone.replace(" ", ""), Global.getDEVICEID()+Global.getTXID()),
                        EncryptionData.EncryptData(mPassword, Global.getDEVICEID()+Global.getTXID()),
                        Global.getTXID())));

                APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
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
                        }

                        if (responseModel != null){
//                            "Msg":"002:Please register your device first.:bb4e2a11-52c8-42fe-aa00-5df68125e61c:4d10edee-c7b8-4a36-abc1-b58fa5784398"

                            String[] strRegisDevice = responseModel.getMsg().toString().split(":");
                            if (strRegisDevice != null) {
                                if (strRegisDevice.length == 4) {
                                    registerDevice(strRegisDevice[1], strRegisDevice[2], strRegisDevice[3]);
                                } else {
                                    Toast.makeText(MainActivity.this, responseModel.getMsg(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            String responseStr = strResponse;
//                            String converted = Until.ConvertJsonEncode(responseStr);
                            String responDecode = Until.decode(responseStr);
                            Log.e(TAG, "ResposeLogIn: "+responDecode);

                            //String json = gson.toJson(responDecode);
                            LoginResponseModel loginResponseModel = gson.fromJson(responDecode, LoginResponseModel.class);
                            Global.setUSERID(loginResponseModel.getUSERID());
                            Global.setAGENTID(loginResponseModel.getAGENTID());
                            Global.setBALANCE(loginResponseModel.getBALANCE());
                            String decode = EncryptionData.DecryptData(Global.getUSERID(), Global.getTXID());
                            Log.e(TAG, "USERID: "+decode);
                            String decodeagent = EncryptionData.DecryptData(Global.getAGENTID(), Global.getTXID());
                            Log.e(TAG, "AGENTID: "+decodeagent);

                            if (!mSetHistoryUser.contains(mHolder.mPhone.getText().toString())){

                                mSetHistoryUser.add(mHolder.mPhone.getText().toString());
                                SharedPreferences.Editor editor = mShared.edit();
                                editor.putStringSet(USER, mSetHistoryUser);
                                editor.commit();
                            }

                            Intent intent = new Intent(MainActivity.this, ActivityDashboard.class);
                            intent.putExtra(UserMenuModel.KEY_MODEL, (ArrayList<UserMenuModel>)loginResponseModel.getUsermenu());
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                            finish();

                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        t.printStackTrace();
                        new ErrorNetworkThrowable(t).networkError(MainActivity.this, call, this);
                    }
                });
                mHolder.mBtnLogin.setEnabled(true);
            }
        }, 1000);



    }
    private void registerDevice(String msg, String agantId, String userId){
        //final DataRequestModel dataRequestModel = new DataRequestModel();
        Global.setAGENTID(agantId);
        Global.setUSERID(userId);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(msg)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DialogCounterAlert.DialogProgress(MainActivity.this);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Call<ResponseModel> call = services.registerDevice(new RequestModel(APIServices.ACTIONREGISTERDEVICE, new DataRequestModel()));
                                APIHelper.enqueueWithRetry(call, new Callback<ResponseModel>() {
                                    @Override
                                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                        DialogCounterAlert.DialogProgress.dismiss();
                                        new DialogCounterAlert(MainActivity.this,
                                                getString(R.string.register),
                                                getString(R.string.register_device_done),
                                                null);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                                        t.printStackTrace();
                                        new ErrorNetworkThrowable(t).networkError(MainActivity.this, call, this);
                                    }
                                });

                            }
                        }, 1000);
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class ViewHolder{

        private TextView mBtnRegister;
        private Button mBtnLogin;
        private EditText mPassword;
        private AutoCompleteTextView mPhone;
        private Spinner mSpinnerPhoneCountry;
        private TextView mHelp;

        public ViewHolder(AppCompatActivity view){

            mBtnRegister = (TextView) view.findViewById(R.id.btn_register);
            mBtnLogin = (Button) view.findViewById(R.id.btn_login);

            mPhone = (AutoCompleteTextView) view.findViewById(R.id.edit_phone);

            mPassword = (EditText) view.findViewById(R.id.edit_password);
            mHelp = (TextView) view.findViewById(R.id.help);
//            mSpinnerPhoneCountry = (Spinner) view.findViewById(R.id.spinner_phone_country);

        }
    }

}