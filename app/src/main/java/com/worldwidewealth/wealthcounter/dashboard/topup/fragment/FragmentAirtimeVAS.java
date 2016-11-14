package com.worldwidewealth.wealthcounter.dashboard.topup.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.adapter.AdapterPageTopup;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAirtimeVAS extends Fragment {

    private View rootView;
    private ViewHolder mHolder;

    public static Fragment newInstance() {
        FragmentAirtimeVAS fragmentAirtimeVAS = new FragmentAirtimeVAS();
        return fragmentAirtimeVAS;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment__airtime_vas, container, false);
            mHolder = new ViewHolder(rootView);
            rootView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) rootView.getTag();
        }

        initChoiceTopup();
        return rootView;
    }

    private void initChoiceTopup(){
        mHolder.mPager.setAdapter(new AdapterPageTopup(this.getChildFragmentManager()));
        mHolder.mTab.setupWithViewPager(mHolder.mPager);

        for(int i=0; i < mHolder.mTab.getTabCount(); i++) {
            View tab = ((ViewGroup) mHolder.mTab.getChildAt(0)).getChildAt(i);
            tab.setBackgroundColor(getResources().getColor(android.R.color.white));
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            if (i == 0){
                p.setMargins(0, 0, 5, 0);
            } else {
                p.setMargins(5, 0, 0, 0);

            }

            tab.requestLayout();
        }
    }

    private class ViewHolder{

        private TabLayout mTab;
        private ViewPager mPager;
        public ViewHolder(View itemView){
            mTab = (TabLayout) itemView.findViewById(R.id.topup_tab);
            mPager = (ViewPager) itemView.findViewById(R.id.topup_pager);
        }
    }

}
