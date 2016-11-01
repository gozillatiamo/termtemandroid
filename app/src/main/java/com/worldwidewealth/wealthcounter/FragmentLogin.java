package com.worldwidewealth.wealthcounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.wealthcounter.dashboard.ActivityDashboard;
import com.worldwidewealth.wealthcounter.model.TestModel;

import org.json.JSONException;
import org.json.JSONObject;

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
    private API services;
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

        services = API.retrofit.create(API.class);
        mHolder.mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = FragmentLogin.this.getActivity();
                Intent intent = new Intent(activity, ActivityDashboard.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                activity.finish();
                TestModel test = new TestModel("1234", "got");

                    Call<ResponseBody> call = services.testpost(new TestModel("1234", "got"));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.e("respone", response.raw().toString());
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
                        .replace(R.id.main_container, FragmentRegister1.newInstance())
                        .addToBackStack(null);

                transaction.commit();

                String encrypt = EncryptionData.EncryptData("1234567890", "got");
                Log.e("encrypt", encrypt);
                String decrypt = EncryptionData.DecryptData("1234567890", encrypt);
                Log.e("decrypt", decrypt);
            }
        });

        return rootView;
    }

    public class ViewHolder{

        private Button mBtnRegister;
        private Button mBtnLogin;
        public ViewHolder(View view){

            mBtnRegister = (Button) view.findViewById(R.id.btn_register);
            mBtnLogin = (Button) view.findViewById(R.id.btn_login);
        }
    }
}
