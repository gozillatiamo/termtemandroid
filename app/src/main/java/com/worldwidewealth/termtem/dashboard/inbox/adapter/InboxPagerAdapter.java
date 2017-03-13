package com.worldwidewealth.termtem.dashboard.inbox.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.worldwidewealth.termtem.dashboard.inbox.fragment.InboxFragment;

/**
 * Created by user on 13-Mar-17.
 */

public class InboxPagerAdapter extends FragmentPagerAdapter {

    public InboxPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return InboxFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 0;
    }
}
