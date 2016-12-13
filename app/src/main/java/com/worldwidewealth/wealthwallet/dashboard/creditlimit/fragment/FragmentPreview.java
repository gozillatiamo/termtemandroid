package com.worldwidewealth.wealthwallet.dashboard.creditlimit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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

public class FragmentPreview extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
    private TabLayout mMainTab;

    public static Fragment newInstance(){
        FragmentPreview fragment = new FragmentPreview();
        return fragment;
    }

    public class ViewHolder{
        private Button mBtnDone;
        public ViewHolder(View itemView){
            mBtnDone = (Button) itemView.findViewById(R.id.btn_done);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_preview_transfer, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Preview");

//        mMainTab = (TabLayout) getActivity().findViewById(R.id.tab_main);
//        mMainTab.setVisibility(View.GONE);
        mHolder.mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                FragmentTransaction transaction = FragmentPreview.this.getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.dialog_enter, R.anim.dialog_exit)
                        .replace(R.id.dashboard_container, FragmentSlipCreditLimit.newInstance())
                        .addToBackStack(null);

                transaction.commit();
*/

            }
        });
        return rootView;
    }
}
