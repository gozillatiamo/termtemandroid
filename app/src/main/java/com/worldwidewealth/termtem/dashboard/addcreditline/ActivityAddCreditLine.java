package com.worldwidewealth.termtem.dashboard.addcreditline;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.addcreditline.fragment.FragmentScanCraditAgent;

public class ActivityAddCreditLine extends AppCompatActivity {

    private ViewHolder mHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_line);
        mHolder = new ViewHolder(this);
        initToolbar();
        initFragment();
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

        if (!getSupportFragmentManager()
                .findFragmentById(R.id.container_add_credit)
                .getChildFragmentManager()
                .popBackStackImmediate()){

            super.onBackPressed();
        }
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initFragment(){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_add_credit, new FragmentScanCraditAgent());
        transaction.commit();
    }

    private class ViewHolder{
        private Toolbar mToolbar;
        public ViewHolder(Activity itemView) {
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
        }
    }
}
