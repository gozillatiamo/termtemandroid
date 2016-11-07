package com.worldwidewealth.wealthcounter.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.wealthcounter.Configs;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.moneytransfer.fragment.FragmentSlipRecieve;
import com.worldwidewealth.wealthcounter.dashboard.moneytransfer.fragment.FragmentSlipSend;

/**
 * Created by MyNet on 17/10/2559.
 */

public class FragmentSlip extends Fragment{
    private View rootView;
    private ViewHolder mHolder;
    private static final String PAGE = "page";
    private String mPage;
    public static Fragment newInstance(String page){
        FragmentSlip fragment = new FragmentSlip();
        Bundle bundle = new Bundle();
        bundle.putString(PAGE, page);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnBackToDashBoard;
        public ViewHolder(View itemview){
            mBtnBackToDashBoard = (Button) itemview.findViewById(R.id.btn_back_to_dashboard);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_slip, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mPage = getArguments().getString(PAGE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//        getActivity().findViewById(R.id.tab_main).setVisibility(View.GONE);

        mHolder.mBtnBackToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentSlip.this.getFragmentManager().popBackStack(0, 0);

            }
        });

        initFragmentSlip();
        return rootView;
    }

    private void initFragmentSlip(){
        Fragment fragmentslip = null;
        switch (mPage){

            case Configs.Slip.MT_RECEIVE:
                fragmentslip = FragmentSlipRecieve.newInstance();
                break;

            case Configs.Slip.MT_SEND:
                fragmentslip = FragmentSlipSend.newInstance();
                break;
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction()
                .add(R.id.slip_container, fragmentslip);
        transaction.commit();

    }

}
