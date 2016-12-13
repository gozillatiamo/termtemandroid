package com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.worldwidewealth.wealthwallet.R;

/**
 * Created by MyNet on 17/10/2559.
 */

public class FragmentSlipRecieve extends Fragment{
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentSlipRecieve fragment = new FragmentSlipRecieve();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnTransfer;
        private Spinner mSpinnerSend, mSpinnerRecipient;
        public ViewHolder(View itemview){
            mBtnTransfer = (Button) itemview.findViewById(R.id.btn_transfer);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_slip_receive, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();



        return rootView;
    }

}
