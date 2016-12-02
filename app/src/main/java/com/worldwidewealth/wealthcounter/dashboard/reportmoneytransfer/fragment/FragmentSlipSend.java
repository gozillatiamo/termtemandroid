package com.worldwidewealth.wealthcounter.dashboard.reportmoneytransfer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.R;

public class FragmentSlipSend extends Fragment {

    public static FragmentSlipSend newInstance() {
        FragmentSlipSend fragment = new FragmentSlipSend();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slip_send, container, false);
    }
}
