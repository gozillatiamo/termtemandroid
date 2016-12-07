package com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.ActivityOneBill;

/**
 * Created by MyNet on 10/10/2559.
 */

public class FragmentBillPayment extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentBillPayment fragment = new FragmentBillPayment();
        return fragment;
    }

    public class ViewHolder{
        private CardView mBtnScan, mBtnLao;
        public ViewHolder(View itemview){
            mBtnScan = (CardView) itemview.findViewById(R.id.btn_scan);
            mBtnLao = (CardView) itemview.findViewById(R.id.btn_lao_tel);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_bill_payment, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mHolder.mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        rootView.findViewById(R.id.btn_onebill).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FragmentBillPayment.this.getContext(), ActivityOneBill.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
