package com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.R;

/**
 * Created by MyNet on 26/10/2559.
 */

public class FragmentBillDetail extends Fragment {

    private View rootview;

    public static Fragment newInstance(){
        FragmentBillDetail fragment = new FragmentBillDetail();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootview == null){
            rootview = inflater.inflate(R.layout.fragment_bill, container, false);
        }
        return rootview;
    }
}
