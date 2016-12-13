package com.worldwidewealth.wealthwallet.dashboard.billpayment.fragment;

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
 * Created by MyNet on 10/10/2559.
 */

public class FragmentBillShow extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentBillShow fragment = new FragmentBillShow();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnNext;
        public ViewHolder(View itemview){
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_bill_show, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("ชำระบิล");
        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
//                        .replace(R.id.dashboard_container, FragmentBillPreview.newInstance())
//                        .addToBackStack(null);
//                transaction.commit();
            }
        });

        return rootView;
    }

}
