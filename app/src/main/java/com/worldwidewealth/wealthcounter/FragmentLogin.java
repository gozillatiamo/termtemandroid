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
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.wealthcounter.dashboard.ActivityDashboard;
import com.worldwidewealth.wealthcounter.model.SignInModel;

import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class FragmentLogin extends Fragment {

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
        return rootView;
    }

    private void initEditText(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHolder.mPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("TH"));
        }
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

                Log.e("LoginData:", "Phone:" + mPhone + "\n"+
                        "Password:" + mPassword);

                Call<ResponseBody> call = services.LOGIN(new SignInModel(new SignInModel.Data(
                        Global.getDEVICEID(),
                        Configs.getPLATFORM(),
                        mPhone,
                        mPassword,
                        Global.getTXID())));

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Activity activity = FragmentLogin.this.getActivity();
                        Intent intent = new Intent(activity, ActivityDashboard.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                        activity.finish();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
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
        public ViewHolder(View view){

            mBtnRegister = (TextView) view.findViewById(R.id.btn_register);
            mBtnLogin = (AppCompatButton) view.findViewById(R.id.btn_login);

            mPhone = (AppCompatEditText) view.findViewById(R.id.edit_phone);

            mPassword = (AppCompatEditText) view.findViewById(R.id.edit_password);
        }
    }
}
