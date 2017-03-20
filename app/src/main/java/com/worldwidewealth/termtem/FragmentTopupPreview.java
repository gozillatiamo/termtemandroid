package com.worldwidewealth.termtem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.TopupPreviewResponseModel;
import com.worldwidewealth.termtem.services.APIServices;

import java.text.NumberFormat;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPreview extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private APIServices service;
    private String mData;
    private String mSelectAmout;
    private TopupPreviewResponseModel mModel;
    private NumberFormat format = NumberFormat.getInstance();
    private static final String DATA = "data";
    private static final String SELECT_AMOUNT = "selectamount";

    public static Fragment newInstance(String data, String selectAmount){
        FragmentTopupPreview fragment = new FragmentTopupPreview();
        Bundle bundle = new Bundle();
        bundle.putString(DATA, data);
        bundle.putString(SELECT_AMOUNT, selectAmount);
        fragment.setArguments(bundle);
        return fragment;
    }



    public class ViewHolder{

        private TextView mTextAmount, mTextCommissionRate, mTextCommissionAmout, mTextBalance,
        mTextMarkup, mTextTotal, mTextSelectAmout;
        private View mLayoutCommission, mLayoutMarkup;
        public ViewHolder(View itemview){
            mTextAmount = (TextView) itemview.findViewById(R.id.txt_amount);
            mTextCommissionRate = (TextView) itemview.findViewById(R.id.txt_commission_rate);
            mTextCommissionAmout = (TextView) itemview.findViewById(R.id.txt_commission_amount);
            mTextMarkup = (TextView) itemview.findViewById(R.id.txt_markup);
            mTextBalance = (TextView) itemview.findViewById(R.id.txt_balance);
            mTextTotal = (TextView) itemview.findViewById(R.id.txt_total);
            mLayoutCommission = (View) itemview.findViewById(R.id.layout_commission);
            mLayoutMarkup = (View) itemview.findViewById(R.id.layout_markup);
            mTextSelectAmout = (TextView) itemview.findViewById(R.id.txt_select_amount);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        service = APIServices.retrofit.create(APIServices.class);
        mData = getArguments().getString(DATA);
        mSelectAmout = getArguments().getString(SELECT_AMOUNT);
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
        if (getParentFragment() instanceof FragmentTopupPackage) {
            FragmentTopupPackage parentFragment = ((FragmentTopupPackage) getParentFragment());
            parentFragment.mHandler.removeCallbacks(parentFragment.mRunnableSubmit);
        }
//        parentFragment.setEnabledBtn(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getParentFragment() instanceof FragmentTopupPackage) {

            FragmentTopupPackage parentFragment = ((FragmentTopupPackage) getParentFragment());
            parentFragment.mHandler.removeCallbacks(parentFragment.mRunnableSubmit);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getParentFragment() instanceof FragmentTopupPackage) {

            FragmentTopupPackage parentFragment = ((FragmentTopupPackage) getParentFragment());
            parentFragment.mHandler.removeCallbacks(parentFragment.mRunnableSubmit);
        }
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
        mHolder.mTextSelectAmout.setText(format.format(mSelectAmout));
        if (mModel.getCOMMISSION_AMOUNT() != 0) {
            mHolder.mTextCommissionRate.setText(mModel.getCOMMISSION_RATE());
            mHolder.mTextCommissionAmout.setText(format.format(mModel.getCOMMISSION_AMOUNT()));
//            mHolder.mLayoutCommission.setVisibility(View.VISIBLE);
        } else {
            mHolder.mLayoutCommission.setVisibility(View.GONE);
        }

        if (mModel.getMARKUP() == 0)
            mHolder.mLayoutMarkup.setVisibility(View.GONE);
        else
            mHolder.mTextMarkup.setText(format.format(mModel.getMARKUP()));

        mHolder.mTextBalance.setText(format.format(mModel.getBALANCE()));
        mHolder.mTextTotal.setText(format.format(mModel.getTOTAL()));
    }

    public boolean canTopup(){
        if (mModel.getBALANCE() < 0) return false;

        return true;
    }


}
