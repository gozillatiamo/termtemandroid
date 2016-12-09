package com.worldwidewealth.wealthcounter.dashboard.topup;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.worldwidewealth.wealthcounter.FragmentTopupPreview;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentTopup;

public class ActivityTopup extends AppCompatActivity {

    private ViewHolder mHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        mHolder = new ViewHolder(this);

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
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.container_topup)
                .getChildFragmentManager()
                .findFragmentById(R.id.container_topup_package);
        if (fragment instanceof FragmentTopupPreview){
            FragmentTopupPreview fragmentTopupPreview = (FragmentTopupPreview)fragment;
            fragmentTopupPreview.fragmentPopBack(fragmentTopupPreview.getParentFragment());

        } else {
            super.onBackPressed();
        }
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
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

    public class ViewHolder{
        private FrameLayout mContainerTopup;
        private Toolbar mToolbar;

        public ViewHolder(Activity itemView){
            mContainerTopup = (FrameLayout) itemView.findViewById(R.id.container_topup);
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar_topup);
        }
    }
}