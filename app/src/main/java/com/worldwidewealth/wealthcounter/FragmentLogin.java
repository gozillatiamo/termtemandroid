package com.worldwidewealth.wealthcounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.wealthcounter.dashboard.ActivityDashboard;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class FragmentLogin extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
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


        mHolder.mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = FragmentLogin.this.getActivity();
                Intent intent = new Intent(activity, ActivityDashboard.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                activity.finish();

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
