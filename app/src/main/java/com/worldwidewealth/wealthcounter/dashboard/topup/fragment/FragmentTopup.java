package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.Configs;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthcounter.until.Until;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopup extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentTopup fragment = new FragmentTopup();
        return fragment;
    }

    public class ViewHolder{
        private CardView mBtnAis, mBtnTruemove, mBtnDtac;
        private View mIncludeMyWallet;
        private TextView mTextBalanceInteger, mTextBalanceDecimal;

        public ViewHolder(View itemview){
            mBtnAis = (CardView) itemview.findViewById(R.id.btn_ais);
            mBtnTruemove = (CardView) itemview.findViewById(R.id.btn_truemove);
            mBtnDtac = (CardView) itemview.findViewById(R.id.btn_dtac);
            mIncludeMyWallet = (View) itemview.findViewById(R.id.include_my_wallet);
            mTextBalanceDecimal = (TextView) mIncludeMyWallet.findViewById(R.id.txt_balance_decimal);
            mTextBalanceInteger = (TextView) mIncludeMyWallet.findViewById(R.id.txt_balance_integer);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        initBtnServices();
        initData();
        return rootView;
    }

    private void initBtnServices(){
        mHolder.mBtnAis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentService(APIServices.AIS);
            }
        });

        mHolder.mBtnTruemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentService(APIServices.TRUEMOVE);
            }
        });

        mHolder.mBtnDtac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentService(APIServices.DTAC);
            }
        });
    }

    private void initData(){
        Until.setBalanceWallet(mHolder.mTextBalanceInteger, mHolder.mTextBalanceDecimal);
    }

    private void startFragmentService(String service){

        this.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .replace(R.id.container_topup, FragmentTopupPackage.newInstance(service))
                .addToBackStack(null)
                .commit();
    }
}
