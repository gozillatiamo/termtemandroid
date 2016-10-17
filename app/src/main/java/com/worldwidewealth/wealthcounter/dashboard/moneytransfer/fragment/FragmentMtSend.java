package com.worldwidewealth.wealthcounter.dashboard.moneytransfer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.worldwidewealth.wealthcounter.Configs;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentSlip;
import com.worldwidewealth.wealthcounter.until.Until;

/**
 * Created by MyNet on 17/10/2559.
 */

public class FragmentMtSend extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentMtSend fragment = new FragmentMtSend();
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
            rootView = inflater.inflate(R.layout.fragment_mt_send, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_mt));

        Until.initSpinnerCurrency(getContext(), mHolder.mSpinnerSend);
        Until.initSpinnerCurrency(getContext(), mHolder.mSpinnerRecipient);

        mHolder.mBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = FragmentMtSend.this.getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.dashboard_container, FragmentSlip.newInstance(Configs.Slip.MT_SEND))
                        .addToBackStack(null);

                transaction.commit();

            }
        });
        return rootView;
    }

}
