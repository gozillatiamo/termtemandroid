package com.worldwidewealth.termtem.dashboard.reportmoneytransfer;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.reportmoneytransfer.fragment.FragmentReportMT;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityReportMT extends AppCompatActivity {

    private ViewHolder mHolder;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_mt);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_report_mt, FragmentReportMT.newInstance());
        fragmentTransaction.commit();

        mHolder = new ViewHolder(this);
        initToolbar();
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    private class ViewHolder{
        private Toolbar mToolbar;
        public ViewHolder(Activity itemView){

            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
        }
    }
}
