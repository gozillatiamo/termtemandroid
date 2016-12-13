package com.worldwidewealth.wealthwallet.dashboard.creditlimit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by MyNet on 7/10/2559.
 */

public class FragmentSlipCreditLimit extends Fragment {

    private View rootView;
    private ViewHolder mHolder;

    public static Fragment newInstance(){
        FragmentSlipCreditLimit fragment = new FragmentSlipCreditLimit();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnBackToDashBoard;
        public ViewHolder(View itemView){
            mBtnBackToDashBoard = (Button) itemView.findViewById(R.id.btn_back_to_dashboard);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_slip_transfer, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        mHolder.mBtnBackToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSlipCreditLimit.this.getFragmentManager().popBackStack(1, 0);

            }
        });
        return rootView;
    }
}
