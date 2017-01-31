package com.worldwidewealth.termtem.dashboard.billpayment.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.termtem.R;

/**
 * Created by MyNet on 10/10/2559.
 */

public class FragmentBillSlip extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private TabLayout mTabMain;
    public static Fragment newInstance(){
        FragmentBillSlip fragment = new FragmentBillSlip();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnBackToDashboard;
        public ViewHolder(View itemview){
            mBtnBackToDashboard = (Button) itemview.findViewById(R.id.btn_back_to_dashboard);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_bill_slip, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

//        mTabMain = (TabLayout) getActivity().findViewById(R.id.tab_main);
//        mTabMain.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        mHolder.mBtnBackToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBillSlip.this.getFragmentManager().popBackStack(0, 0);
            }
        });

        return rootView;
    }


}
