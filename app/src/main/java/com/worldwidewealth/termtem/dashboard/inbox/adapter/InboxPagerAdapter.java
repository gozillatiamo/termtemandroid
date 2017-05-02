package com.worldwidewealth.termtem.dashboard.inbox.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.worldwidewealth.termtem.dashboard.inbox.fragment.InboxFragment;

/**
 * Created by user on 13-Mar-17.
 */

public class InboxPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 1;
    public static final int ALL = 0;
    public static final int TEXT = 1;
    public static final int IMAGE = 2;

    private String tabTitles[] = new String[] { "ALL", "TEXT", "IMAGE" };
    private Context mContext;

    public InboxPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return InboxFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
