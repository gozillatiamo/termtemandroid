package com.worldwidewealth.wealthwallet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.wealthwallet.model.TopupPreviewResponseModel;

import java.text.NumberFormat;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPreview extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private APIServices service;
    private String mData;
    private TopupPreviewResponseModel mModel;
    private NumberFormat format = NumberFormat.getInstance();
    private static final String DATA = "data";
    public static Fragment newInstance(String data){
        FragmentTopupPreview fragment = new FragmentTopupPreview();
        Bundle bundle = new Bundle();
        bundle.putString(DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{

        private TextView mTextAmount, mTextCommissionRate, mTextCommissionAmout, mTextBalance;

        public ViewHolder(View itemview){
            mTextAmount = (TextView) itemview.findViewById(R.id.txt_amount);
            mTextCommissionRate = (TextView) itemview.findViewById(R.id.txt_commission_rate);
            mTextCommissionAmout = (TextView) itemview.findViewById(R.id.txt_commission_amount);
            mTextBalance = (TextView) itemview.findViewById(R.id.txt_balance);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        service = APIServices.retrofit.create(APIServices.class);
        mData = getArguments().getString(DATA);
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_preview, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        if (mData != null) {
            transferData();
            setData();
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        FragmentTopupPackage parentFragment = ((FragmentTopupPackage)getParentFragment());
        parentFragment.mHandler.removeCallbacks(parentFragment.mRunnableSubmit);
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentTopupPackage parentFragment = ((FragmentTopupPackage)getParentFragment());
        parentFragment.mHandler.removeCallbacks(parentFragment.mRunnableSubmit);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTopupPackage parentFragment = ((FragmentTopupPackage)getParentFragment());
        parentFragment.mHandler.removeCallbacks(parentFragment.mRunnableSubmit);

    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        initBackPress();
    }
*/

    private void transferData(){
        mModel = new Gson().fromJson(mData, TopupPreviewResponseModel.class);

    }

/*
    private void initBackPress(){
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

                    if (getParentFragment() instanceof FragmentTopupPackage){

                        fragmentPopBack(getParentFragment());

                        return true;
                    }
                }
                return false;
            }
        } );

    }
*/

/*
    public void fragmentPopBack(Fragment fragment){

        FragmentTopupPackage fragmentTopupPackage = (FragmentTopupPackage)fragment;
        fragmentTopupPackage.onBackPress();

        fragmentTopupPackage.getChildFragmentManager().popBackStack();

    }
*/

    private void setData(){
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        mHolder.mTextAmount.setText(format.format(mModel.getAMOUNT()));
        mHolder.mTextCommissionRate.setText(mModel.getCOMMISSION_RATE());
        mHolder.mTextCommissionAmout.setText(format.format(mModel.getCOMMISSION_AMOUNT()));
        mHolder.mTextBalance.setText(format.format(mModel.getBALANCE()));
    }

    public boolean canTopup(){
        if (mModel.getBALANCE() < 0) return false;

        return true;
    }


}
