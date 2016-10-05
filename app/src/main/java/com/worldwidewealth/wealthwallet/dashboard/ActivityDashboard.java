package com.worldwidewealth.wealthwallet.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.Adapter.AdapterListDashboard;
import com.worldwidewealth.wealthwallet.dashboard.fragment.FragmentCreditLimit;
import com.worldwidewealth.wealthwallet.dashboard.fragment.FragmentHome;
import com.worldwidewealth.wealthwallet.until.SimpleDividerItemDecoration;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityDashboard extends AppCompatActivity {

    private ViewHolder mHolder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mHolder = new ViewHolder(this);

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
        mHolder.mBtnTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        Log.e("Title", "touch");
                        FragmentTransaction transaction = ActivityDashboard.this.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.dashboard_container, FragmentCreditLimit.newInstance())
                                .addToBackStack(null);

                        transaction.commit();

                        break;
                }
                return true;
            }
        });
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

        if (stackCount == 1) finish();
        else  super.onBackPressed();
    }

    public class ViewHolder{

        private Toolbar mToolbar;
        private ViewStub mTitleView;
        private View mBtnTitle;

        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mTitleView = (ViewStub) view.findViewById(R.id.title_container);
            mBtnTitle = (View) view.findViewById(R.id.btn_title);
            mTitleView.setVisibility(View.VISIBLE);
        }
    }
}
