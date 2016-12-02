package com.worldwidewealth.wealthcounter.dashboard.reportmoneytransfer.fragment;

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

public class FragmentReportMtPreview extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentReportMtPreview fragment = new FragmentReportMtPreview();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnDone;
        public ViewHolder(View itemview){
            mBtnDone = (Button)itemview.findViewById(R.id.btn_done);
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

        initBtn();
        return rootView;
    }

    private void initBtn(){
        mHolder.mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

}
