package com.worldwidewealth.wealthcounter.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentHome;
import com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment.FragmentSlipCreditLimit;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentSlip;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentTopupSlip;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityDashboard extends AppCompatActivity{

    private ViewHolder mHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mHolder = new ViewHolder(this);
        TabItem tabhome = (TabItem) findViewById(R.id.tab_home);

//        tabhome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityDashboard.this.getSupportFragmentManager().popBackStack(0, 0);
//            }
//        });
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_container, FragmentHome.newInstance())
                .addToBackStack(null);
        transaction.commit();

        initToolbar();
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();
        Fragment currentFragment = getSupportFragmentManager().getFragments().get(stackCount-1);
        if (currentFragment instanceof FragmentSlipCreditLimit ||
                currentFragment instanceof FragmentBillSlip ||
                currentFragment instanceof FragmentTopupSlip ||
                currentFragment instanceof FragmentSlip) return;
        if (stackCount == 1) finish();
        else  super.onBackPressed();
    }

    public class ViewHolder{

        private Toolbar mToolbar;
        private TabItem mItemHome;

        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mItemHome = (TabItem) view.findViewById(R.id.tab_home);
        }
    }
}
