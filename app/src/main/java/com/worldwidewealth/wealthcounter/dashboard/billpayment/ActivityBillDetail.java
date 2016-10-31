package com.worldwidewealth.wealthcounter.dashboard.billpayment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillDetail;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentOneBillPayment;
import com.worldwidewealth.wealthcounter.dialog.DialogBillAlert;
import com.worldwidewealth.wealthcounter.dialog.DialogBillScan;

public class ActivityBillDetail extends AppCompatActivity {
    private String mType;
    private View mBtnSettingAlert;
    private ImageButton mBtnBarcode, mBtnQrcode;
    private Button mBtnHistory;
    private DialogBillScan dialogBillScan = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        mType = getIntent().getExtras().getString("type");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        switch (mType){
            case "detail":
                fragment = FragmentBillDetail.newInstance();
                break;
            case "onebill":
                fragment = FragmentOneBillPayment.newInstance();
                break;
        }

        transaction.replace(R.id.container_bill, fragment);
        transaction.commit();

        initBtn();

    }

    private void initBtn(){
        mBtnSettingAlert = (View) findViewById(R.id.btn_setting_alert);
        mBtnSettingAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBillAlert dialog = new DialogBillAlert(ActivityBillDetail.this);
                dialog.show();
            }
        });

        mBtnBarcode = (ImageButton) findViewById(R.id.btn_barcode);
        mBtnQrcode = (ImageButton) findViewById(R.id.btn_qrcode);
        mBtnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBillScan = new DialogBillScan(ActivityBillDetail.this, "barcode");
                dialogBillScan.show();
            }
        });
        mBtnQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBillScan = new DialogBillScan(ActivityBillDetail.this, "qrcode");
                dialogBillScan.show();
            }
        });

        mBtnHistory = (Button) findViewById(R.id.btn_history);
        mBtnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityBillDetail.this, ActivityBillHistory.class);
                startActivity(intent);
            }
        });

    }
}
