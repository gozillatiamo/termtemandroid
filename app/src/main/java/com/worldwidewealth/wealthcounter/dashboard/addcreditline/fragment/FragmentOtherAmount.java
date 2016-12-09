package com.worldwidewealth.wealthcounter.dashboard.addcreditline.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.worldwidewealth.wealthcounter.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentOtherAmount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOtherAmount extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewHolder mHolder;
    public FragmentOtherAmount() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentOtherAmount newInstance() {
        FragmentOtherAmount fragment = new FragmentOtherAmount();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_other_amount, container, false);
        mHolder = new ViewHolder(rootView);
        initEditOtherAmount();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onBackPress();
    }

    private void onBackPress(){
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    ((FragmentAddCreditChoice)getParentFragment()).clearData();

                return false;
            }
        });
    }
    private void initEditOtherAmount(){
        mHolder.mEditOtherAmount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    ((FragmentAddCreditChoice)getParentFragment()).clearData();
                return false;
            }
        });
        mHolder.mEditOtherAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment parentFragment = FragmentOtherAmount.this.getParentFragment();
                if (parentFragment instanceof FragmentAddCreditChoice){
                    ((FragmentAddCreditChoice)parentFragment).setPrice(Double.parseDouble(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class ViewHolder{
        private EditText mEditOtherAmount;
        public ViewHolder(View itemView) {
            mEditOtherAmount = (EditText) itemView.findViewById(R.id.edit_other_amount);
        }
    }


}
