package com.worldwidewealth.wealthcounter.dashboard.topup.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentChoiceTopup;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentListPackage;

/**
 * Created by MyNet on 11/10/2559.
 */

public class AdapterPageTopup extends FragmentPagerAdapter {

    private String[] title = new String[]{
        "Airtime",
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
        return new FragmentChoiceTopup();
    }
}
