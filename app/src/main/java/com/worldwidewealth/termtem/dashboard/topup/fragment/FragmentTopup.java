package com.worldwidewealth.termtem.dashboard.topup.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.util.Util;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopup extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private String mTopup;


    public static final String keyTopup = "topup";
    public static final String MOBILE = "mobile";
    public static final String PIN = "pin";


    public static Fragment newInstance(String topup){
        Bundle bundle = new Bundle();
        FragmentTopup fragment = new FragmentTopup();
        bundle.putString(keyTopup, topup);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{
        private CardView mBtnAis, mBtnTruemove, mBtnDtac;
        private View mIncludeMyWallet;
        private ImageView mImageTrue;

        public ViewHolder(View itemview){
            mBtnAis = (CardView) itemview.findViewById(R.id.btn_ais);
            mBtnTruemove = (CardView) itemview.findViewById(R.id.btn_truemove);
            mBtnDtac = (CardView) itemview.findViewById(R.id.btn_dtac);
            mIncludeMyWallet = (View) itemview.findViewById(R.id.include_my_wallet);
            mImageTrue = (ImageView) itemview.findViewById(R.id.img_true);

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

        mTopup = this.getArguments().getString(keyTopup);
        initBtnServices();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initBtnServices(){

        switch (mTopup){
            case PIN:
                mHolder.mImageTrue.setImageResource(R.drawable.logo_truemoney);
                mHolder.mBtnDtac.setVisibility(View.GONE);
                break;
        }


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
//        Util.setBalanceWallet(mHolder.mIncludeMyWallet);
        Util.updateMyBalanceWallet(getContext(), mHolder.mIncludeMyWallet);
    }

    private void startFragmentService(String service){

        this.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .replace(R.id.container_topup, FragmentTopupPackage.newInstance(service, mTopup))
                .addToBackStack(null)
                .commit();
    }

}
