package com.worldwidewealth.wealthcounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.worldwidewealth.wealthcounter.dashboard.ActivityDashboard;
import com.worldwidewealth.wealthcounter.model.LoginSuccessModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.model.SignInModel;
import com.worldwidewealth.wealthcounter.until.Until;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class FragmentLogin extends Fragment{

    private View rootView;
    private ViewHolder mHolder;
    private APIServices services;
    private String mPhone, mPassword;

    public static Fragment newInstance(){
        FragmentLogin fragment = new FragmentLogin();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_login, null, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        services = APIServices.retrofit.create(APIServices.class);
        initEditText();
        initBtn();
        initSpinner();
        return rootView;
    }

    private void initEditText(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHolder.mPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("TH"));
        }
    }

    private void initSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.phone_country, R.layout.text_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHolder.mSpinnerPhoneCountry.setAdapter(adapter);
        mHolder.mSpinnerPhoneCountry.setSelection(0);

    }

    private void initBtn(){
        mHolder.mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPhone = mHolder.mPhone.getText().toString();
                mPassword = mHolder.mPassword.getText().toString();
                if (mPhone.equals("") || mPassword.equals("")) {
                    Toast.makeText(FragmentLogin.this.getContext(), getString(R.string.please_enter_data), Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("LoginData:", "action:LOGIN" + "\n"+
                        "DEVICEID:" + Global.getDEVICEID() + "\n" +
                        "PLATFORM:" + Configs.getPLATFORM() + "\n" +
                        "USERNAME:" + mPhone + "\n" +
                        "PASSWORD:" + mPassword + "\n" +
                        "TXIK:" + Global.getTXID());

                Call<Object> call = services.LOGIN(new SignInModel(new SignInModel.Data(
                        Global.getDEVICEID(),
                        Configs.getPLATFORM(),
                        EncryptionData.EncryptData(mPhone),
                        EncryptionData.EncryptData(mPassword),
                        Global.getTXID())));

                call.enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        Gson gson = new Gson();
                        if (!(response.body() instanceof String)){
                            Log.e("ResponseModel", "true");

                            JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                            ResponseModel responseModel = gson.fromJson(jsonObject, ResponseModel.class);
                            Toast.makeText(FragmentLogin.this.getContext(), responseModel.getMsg(),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Log.e("ResponseModel", "false");
                            String responseStr = (String)response.body();
                            String converted = Until.ConvertJsonEncode(responseStr);
                            String responDecode = Until.decode(converted);
                            Log.e("strResponse", converted);
                            Log.e("strDecode", responDecode);

                            //String json = gson.toJson(responDecode);
                            LoginSuccessModel loginSuccessModel = gson.fromJson(responDecode, LoginSuccessModel.class);
                            Global.setUSERID(loginSuccessModel.getUSERID());
                            Global.setAGENTID(loginSuccessModel.getAGENTID());
                        }

                        Activity activity = FragmentLogin.this.getActivity();
                        Intent intent = new Intent(activity, ActivityDashboard.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                        activity.finish();

                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        mHolder.mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = FragmentLogin.this.getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.main_container, FragmentRegister.newInstance())
                        .addToBackStack(null);

                transaction.commit();
            }
        });

    }

    public class ViewHolder{

        private TextView mBtnRegister;
        private AppCompatButton mBtnLogin;
        private AppCompatEditText mPhone, mPassword;
        private Spinner mSpinnerPhoneCountry;
        public ViewHolder(View view){

            mBtnRegister = (TextView) view.findViewById(R.id.btn_register);
            mBtnLogin = (AppCompatButton) view.findViewById(R.id.btn_login);

            mPhone = (AppCompatEditText) view.findViewById(R.id.edit_phone);

            mPassword = (AppCompatEditText) view.findViewById(R.id.edit_password);
            mSpinnerPhoneCountry = (Spinner) view.findViewById(R.id.spinner_phone_country);
        }
    }
}
