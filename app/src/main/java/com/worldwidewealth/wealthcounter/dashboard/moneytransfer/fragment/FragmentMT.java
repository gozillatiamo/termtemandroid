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

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillPreview;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillShow;

/**
 * Created by MyNet on 14/10/2559.
 */

public class FragmentMT extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentMT fragment = new FragmentMT();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnSend, mBtnReceive;
        public ViewHolder(View itemview){
            mBtnReceive = (Button) itemview.findViewById(R.id.btn_receive);
            mBtnSend = (Button) itemview.findViewById(R.id.btn_send);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_mt, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_mt));

//        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
//                        .replace(R.id.dashboard_container, FragmentBillPreview.newInstance())
//                        .addToBackStack(null);
//                transaction.commit();
//            }
//        });

        return rootView;
    }

}
