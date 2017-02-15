package com.worldwidewealth.termtem.dashboard.topup.fragment;


import android.os.Bundle;
import android.os.Handler;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.topup.adapter.AdapterPageTopup;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.until.Until;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAirtimeVAS extends Fragment {

    private View rootView;
    private ViewHolder mHolder;
    private String mData;
    private static int sTabColor = R.color.colorTabAirtime;
    private static final String DATA = "data";
    private AdapterPageTopup mAdapterPageTopup;

    private static final int AIRTIME = 0;
    private static final int VAS = 1;

    public static Fragment newInstance(String data) {
        Bundle bundle = new Bundle();
        FragmentAirtimeVAS fragmentAirtimeVAS = new FragmentAirtimeVAS();
        bundle.putString(DATA, data);
        fragmentAirtimeVAS.setArguments(bundle);
        return fragmentAirtimeVAS;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getParentFragment() instanceof FragmentTopupPackage){
            ((FragmentTopupPackage)getParentFragment()).setEnableEditPhone(true);
        }
        mData = this.getArguments().getString(DATA);
        if (!DialogCounterAlert.DialogProgress.isShow()){
            new DialogCounterAlert.DialogProgress(getContext());
        }

        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_airtime_vas, container, false);
            mHolder = new ViewHolder(rootView);
            Until.setupUI(rootView);
            rootView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) rootView.getTag();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initChoiceTopup();
                DialogCounterAlert.DialogProgress.dismiss();
            }
        },1000);
        return rootView;
    }

    private void initChoiceTopup(){
        mAdapterPageTopup = new AdapterPageTopup(this.getChildFragmentManager(), mData);
        mHolder.mPager.setAdapter(mAdapterPageTopup);
        mHolder.mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FragmentChoiceTopup fragmentChoiceTopup = (FragmentChoiceTopup)
                        Until.getFragmentFromViewpager(getChildFragmentManager(),
                                mHolder.mPager,
                                position);
                fragmentChoiceTopup.clearSelected();
                ((FragmentTopupPackage)getParentFragment()).setAmt(0, null);

                setTabViewColor(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mHolder.mTab.setupWithViewPager(mHolder.mPager);
/*
        mHolder.mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View viewTab = ((ViewGroup) mHolder.mTab.getChildAt(0)).getChildAt(tab.getPosition());
                viewTab.setBackgroundColor(getResources().getColor(android.R.color.white));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
*/

        for(int i=0; i < mHolder.mTab.getTabCount(); i++) {
            View tab = ((ViewGroup) mHolder.mTab.getChildAt(0)).getChildAt(i);
            tab.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//            tab.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tab.setBackgroundColor(getResources().getColor(android.R.color.white));
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            if (i == 0){
                p.setMargins(0, 0, 5, 0);
            } else {
                p.setMargins(5, 0, 0, 0);

            }

            tab.requestLayout();
        }

        setTabViewColor(0);

    }

    private void setTabViewColor(int position){

        switch (position){
            case AIRTIME:
                setsTabColor(R.color.colorTabAirtime);
                break;
            case VAS:
                setsTabColor(R.color.colorTabVAS);
                break;
        }

        mHolder.mTab.setSelectedTabIndicatorColor(getResources().getColor(getsTabColor()));
        View viewTab = ((ViewGroup) mHolder.mTab.getChildAt(0)).getChildAt(position);
        viewTab.setBackgroundColor(getResources().getColor(getsTabColor()));
        mHolder.mTab.setTabTextColors(getResources().getColor(android.R.color.tertiary_text_dark),
                getResources().getColor(android.R.color.white));


    }

    public static int getsTabColor() {
        return sTabColor;
    }

    public static void setsTabColor(int sTabColor) {
        FragmentAirtimeVAS.sTabColor = sTabColor;
    }

    private class ViewHolder{

        private TabLayout mTab;
        private ViewPager mPager;
        public ViewHolder(View itemView){
            mTab = (TabLayout) itemView.findViewById(R.id.topup_tab);
            mTab.setSelectedTabIndicatorColor(getResources().getColor(getsTabColor()));
            mTab.setTabTextColors(getResources().getColor(android.R.color.tertiary_text_dark),
                    getResources().getColor(getsTabColor()));
            mPager = (ViewPager) itemView.findViewById(R.id.topup_pager);
        }
    }

}
