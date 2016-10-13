package com.worldwidewealth.wealthcounter.dashboard.topup.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentListPackage;

/**
 * Created by MyNet on 11/10/2559.
 */

public class AdapterPageTopup extends FragmentPagerAdapter {

    private String[] title = new String[]{
        "AIRTIME",
            "VAS"
    };

    public AdapterPageTopup(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentListPackage.newInstance(R.layout.fragment_topup_listairtime_package);
            case 1:
                return FragmentListPackage.newInstance(R.layout.fragment_topup_listvas_package);
        }

        return null;
    }
}
