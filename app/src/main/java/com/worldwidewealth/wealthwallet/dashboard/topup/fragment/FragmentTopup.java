package com.worldwidewealth.wealthwallet.dashboard.topup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthwallet.APIServices;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.until.Until;

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

        public ViewHolder(View itemview){
            mBtnAis = (CardView) itemview.findViewById(R.id.btn_ais);
            mBtnTruemove = (CardView) itemview.findViewById(R.id.btn_truemove);
            mBtnDtac = (CardView) itemview.findViewById(R.id.btn_dtac);
            mIncludeMyWallet = (View) itemview.findViewById(R.id.include_my_wallet);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("FragmentTopup", "true");
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        initBtnServices();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();

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
        Until.setBalanceWallet(mHolder.mIncludeMyWallet);
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
