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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case SCAN:
                    LoadBillServiceResponse response = data.getExtras().getParcelable(MainBillPayFragment.KEY_BILL_DATA);
                    String result = data.getStringExtra(ScanBillActivity.KEY_SCAN_RESULT);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                            .replace(R.id.container_topup, FragmentTopupPackage.newInstance(response.getBILL_SERVICE_NAME(), BillPaymentActivity.BILLPAY, result, 0))
                            .commit();
                    finish();

                    break;
            }
        }
    }


    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void bindView(){

    }

}
