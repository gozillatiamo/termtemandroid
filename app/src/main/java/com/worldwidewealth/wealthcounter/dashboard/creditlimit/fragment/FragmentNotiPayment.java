package com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.R;

/**
 * Created by MyNet on 7/10/2559.
 */

public class FragmentNotiPayment extends Fragment {
    private View rootView;
    public static Fragment newInstance(){
        FragmentNotiPayment fragment = new FragmentNotiPayment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_noti_payment, container, false);
        }
        return rootView;
    }
}
