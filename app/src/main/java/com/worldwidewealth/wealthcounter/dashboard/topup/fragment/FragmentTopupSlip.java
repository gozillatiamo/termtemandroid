package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;

import org.w3c.dom.Text;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupSlip extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private TabLayout tabLayout;
    private String mPage;

    public static Fragment newInstance(String page){
        Bundle bundle = new Bundle();
        bundle.putString("page", page);
        FragmentTopupSlip fragment = new FragmentTopupSlip();
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnBack;
        private TextView mTextTopup;
        public ViewHolder(View itemview){
            mBtnBack = (Button) itemview.findViewById(R.id.btn_back_to_dashboard);
            mTextTopup = (TextView) itemview.findViewById(R.id.txt_topup);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPage = getArguments().getString("page");

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_slip, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_main);
        tabLayout.setVisibility(View.GONE);

        switch (mPage){
            case "airtime":
                mHolder.mTextTopup.setText("10,000 KIP");
                mHolder.mTextTopup.setCompoundDrawables(null, null, getResources().getDrawable(R.drawable.lao_kip_64), null);
                break;
            case "vas":
                mHolder.mTextTopup.setText("3GB/10,000KIP/30day");
                mHolder.mTextTopup.setCompoundDrawables(null, null, null, null);

                break;

        }
        mHolder.mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTopupSlip.this.getFragmentManager().popBackStack(0, 0);
            }
        });
        return rootView;
    }

}
