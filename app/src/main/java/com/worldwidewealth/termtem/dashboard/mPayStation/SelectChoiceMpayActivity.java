package com.worldwidewealth.termtem.dashboard.mPayStation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SelectChoiceMpayActivity extends MyAppcompatActivity {

    private Button mBtnMpayStation, mBtnMpayBank;
    private Toolbar mToolbar;

    public static final int MPAY_STATION = 2;
    public static final int MPAY_BANK = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_choice_mpay);
        initWidgets();
        initToolbar();
        initBtn();
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


    private void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    private void initWidgets(){
        mBtnMpayBank = (Button) findViewById(R.id.btn_mpay_bank);
        mBtnMpayStation = (Button) findViewById(R.id.btn_mpay_station);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_mpay);

    }

    private void initBtn(){
        mBtnMpayStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(MPAY_STATION);
            }
        });

        mBtnMpayBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(MPAY_BANK);

            }
        });
    }

    private void onclick(int type){
        Intent intent = new Intent(SelectChoiceMpayActivity.this, MPayStationActivity.class);
        intent.putExtra("type", type);
        overridePendingTransition(R.anim.slide_in_right, 0);
        startActivity(intent);

    }
}
