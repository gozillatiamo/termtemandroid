package com.worldwidewealth.wealthcounter.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.adapter.AdapterDashboard;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentHome;
import com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment.FragmentSlipCreditLimit;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentSlip;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.wealthcounter.until.SimpleDividerItemDecoration;
import com.worldwidewealth.wealthcounter.until.SpacesGridDecoration;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityDashboard extends AppCompatActivity{

    private ViewHolder mHolder;
    private AdapterDashboard mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mHolder = new ViewHolder(this);

//        tabhome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityDashboard.this.getSupportFragmentManager().popBackStack(0, 0);
//            }
//        });
//        FragmentTransaction transaction = getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.dashboard_container, FragmentHome.newInstance())
//                .addToBackStack(null);
//        transaction.commit();

        initToolbar();
        initListDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().show();
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle("");
    }

    private void initListDashboard(){
        mAdapter = new AdapterDashboard(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 12);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.getItemViewType(position);
            }
        });

        mHolder.mListDashboard.setAdapter(mAdapter);
        mHolder.mListDashboard.setLayoutManager(layoutManager);
        mHolder.mListDashboard.addItemDecoration(new SpacesGridDecoration(6));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (stackCount != 0) {
            Fragment currentFragment = getSupportFragmentManager().getFragments().get(stackCount - 1);
            if (currentFragment instanceof FragmentSlipCreditLimit ||
                    currentFragment instanceof FragmentBillSlip ||
                    currentFragment instanceof FragmentTopupSlip ||
                    currentFragment instanceof FragmentSlip) return;
        }

        super.onBackPressed();
    }

    public class ViewHolder{

        private Toolbar mToolbar;
        private RecyclerView mListDashboard;

        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mListDashboard = (RecyclerView) view.findViewById(R.id.list_dashborad);
        }
    }
}
