package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthcounter.game.ActivityGame;

import org.w3c.dom.Text;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupSlip extends Fragment {
    private View rootView;
    private ViewHolder mHolder;
    private TabLayout tabLayout;
    private String mPage;

    public static Fragment newInstance(){
        Bundle bundle = new Bundle();
//        bundle.putString("page", page);
        FragmentTopupSlip fragment = new FragmentTopupSlip();
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnBack, mBtnGame;
        private TextView mTextTopup;
        public ViewHolder(View itemview){
            mBtnBack = (Button) itemview.findViewById(R.id.btn_back_to_dashboard);
            mBtnGame = (Button) itemview.findViewById(R.id.btn_play_game);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mPage = getArguments().getString("page");

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_slip, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

//        tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_main);
//        tabLayout.setVisibility(View.GONE);
        initBtn();
        return rootView;
    }

    private void initBtn(){
        mHolder.mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
//                FragmentManager fragmentManager = FragmentTopupSlip.this.getActivity().getSupportFragmentManager();
//                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                ((AppCompatActivity)FragmentTopupSlip.this.getActivity()).getSupportActionBar().show();
//                FragmentTopupSlip.this.getFragmentManager().popBackStack();
            }
        });

        mHolder.mBtnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentTopupSlip.this.getContext(), ActivityGame.class);
                startActivity(intent);
                getActivity().finish();
//                FragmentManager fragmentManager = FragmentTopupSlip.this.getActivity().getSupportFragmentManager();
//                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                ((AppCompatActivity)FragmentTopupSlip.this.getActivity()).getSupportActionBar().show();
            }
        });

    }

}
