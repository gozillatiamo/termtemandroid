package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.adapter.AdapterPageTopup;

/**
 * Created by MyNet on 11/10/2559.
 */

public class FragmentTopupPackage extends  Fragment{
    private View rootView;
    private ViewHolder mHolder;
    public static Fragment newInstance(){
        FragmentTopupPackage fragment = new FragmentTopupPackage();
        return fragment;
    }

    public class ViewHolder{
        private ViewPager mViewPage;
        private TabLayout mTab;
        private Button mBtnNext;
        public ViewHolder(View itemview){
            mViewPage = (ViewPager) itemview.findViewById(R.id.viewpager);
            mTab = (TabLayout) itemview.findViewById(R.id.tab_package);
            mBtnNext = (Button) itemview.findViewById(R.id.btn_next);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_topup_package, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else mHolder = (ViewHolder) rootView.getTag();

        mHolder.mViewPage.setAdapter(new AdapterPageTopup(getChildFragmentManager()));
        mHolder.mTab.setupWithViewPager(mHolder.mViewPage);


        mHolder.mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String page;

                if (mHolder.mViewPage.getCurrentItem() == 0) page = "airtime";
                else page = "vas";

                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.dashboard_container, FragmentTopupPreview.newInstance(page))
                        .addToBackStack(null);
                transaction.commit();

            }
        });

        return rootView;
    }

}
