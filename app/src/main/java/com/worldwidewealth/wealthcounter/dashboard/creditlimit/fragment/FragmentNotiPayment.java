package com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

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

    public class ViewHolder{
        private Spinner mSpinnerCurrency, mSpinnerBankStart, mSpinnerBankEnd;
        private EditText mDate, mTime;
        public ViewHolder(View itemview){
            mSpinnerCurrency = (Spinner) itemview.findViewById(R.id.spinner_currency);
            mSpinnerBankEnd = (Spinner) itemview.findViewById(R.id.spinner_bank_end);
            mSpinnerBankStart = (Spinner) itemview.findViewById(R.id.spinner_bank_start);

            mDate = (EditText) itemview.findViewById(R.id.edit_date);
            mTime = (EditText) itemview.findViewById(R.id.edit_time);
        }
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
