package com.worldwidewealth.termtem.dashboard.billpayment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.billpayment.fragment.MainBillPayFragment;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.Util;


public class BillPaymentActivity extends MyAppcompatActivity {
    public static final String BILLPAY = "billpay";
    public static final int SCAN = 0x00;
    public static final int KEYIN = 0x01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payment);
        setupToolbar();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Util.setBalanceWallet(findViewById(R.id.include_my_wallet));

    }

    private void bindView(){

    }

}
