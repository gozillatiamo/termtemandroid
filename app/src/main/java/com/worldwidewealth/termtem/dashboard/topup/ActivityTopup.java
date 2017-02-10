package com.worldwidewealth.termtem.dashboard.topup;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityTopup extends MyAppcompatActivity {

    private FrameLayout mContainerTopup;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        initWidgets();
        initToolbar();
        initContainer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        Fragment fragment = getSupportFragmentManager()
//                .findFragmentById(R.id.container_topup)
//                .getChildFragmentManager()
//                .findFragmentById(R.id.container_topup_package);
//        if (fragment instanceof FragmentTopupPreview){
//            FragmentTopupPreview fragmentTopupPreview = (FragmentTopupPreview)fragment;
//            fragmentTopupPreview.fragmentPopBack(fragmentTopupPreview.getParentFragment());
//
//        } else {
//            super.onBackPressed();
//        }

        if (!getSupportFragmentManager()
                .findFragmentById(R.id.container_topup)
                .getChildFragmentManager()
                .popBackStackImmediate()){
            super.onBackPressed();
        }
    }

    private void initWidgets(){
        mContainerTopup = (FrameLayout) findViewById(R.id.container_topup);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_topup);

    }
    private void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void initContainer(){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_topup, FragmentTopup.newInstance());
        transaction.commit();
    }

}
