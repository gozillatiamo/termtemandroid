package com.worldwidewealth.wealthcounter.dashboard.addcreditline.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddCreditChoice extends Fragment {


    public FragmentAddCreditChoice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_credit_choice, container, false);
    }

}
