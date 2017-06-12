package com.worldwidewealth.termtem.dashboard.report.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.report.fragment.GraphReportFragment;
import com.worldwidewealth.termtem.dashboard.report.fragment.TextReportFragment;

/**
 * Created by gozillatiamo on 6/5/17.
 */

public class PagerTypeReportAdapter extends FragmentPagerAdapter{

    public static final int TEXT = 0;
    public static final int GRAPH = 1;

    private String[] mTitle;
    private Context mContext;

    public PagerTypeReportAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        mTitle = context.getResources().getStringArray(R.array.list_tab_history_report);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case TEXT:
                return TextReportFragment.newInstance();
            case GRAPH:
                return GraphReportFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
//        return mTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }
}
