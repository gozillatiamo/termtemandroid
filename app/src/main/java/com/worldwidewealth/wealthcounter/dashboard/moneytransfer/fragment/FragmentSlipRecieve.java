package com.worldwidewealth.wealthcounter.dashboard.moneytransfer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.until.Until;

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
            mSpinnerSend = (Spinner) itemview.findViewById(R.id.spinner_send);
            mSpinnerRecipient = (Spinner) itemview.findViewById(R.id.spinner_recipient);
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
