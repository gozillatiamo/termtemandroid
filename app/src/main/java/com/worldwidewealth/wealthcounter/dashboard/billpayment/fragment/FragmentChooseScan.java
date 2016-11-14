package com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dialog.DialogReference;

/**
 * Created by MyNet on 10/10/2559.
 */

public class FragmentChooseScan extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentChooseScan fragment = new FragmentChooseScan();
        return fragment;
    }

    public class ViewHolder{
        private View mBtnScan, mBtnEnterReference;
        public ViewHolder(View itemview){
            mBtnScan = (View) itemview.findViewById(R.id.btn_scan);
            mBtnEnterReference = (View) itemview.findViewById(R.id.btn_enter_reference);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_choose_scan, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mHolder.mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.dashboard_container, FragmentBillShow.newInstance())
                        .addToBackStack(null);

                transaction.commit();
*/
            }
        });

        mHolder.mBtnEnterReference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogReference dialogReference = new DialogReference(FragmentChooseScan.this.getContext());
                dialogReference.setOnSubmit(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
/*
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.dashboard_container, FragmentBillShow.newInstance())
                                .addToBackStack(null);
                        dialogReference.dismiss();
                        transaction.commit();
*/

                    }
                });
                dialogReference.show();

            }
        });

        return rootView;
    }

}
