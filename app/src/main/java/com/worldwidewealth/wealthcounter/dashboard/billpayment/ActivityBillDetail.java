package com.worldwidewealth.wealthcounter.dashboard.billpayment;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillDetail;

public class ActivityBillDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_bill, FragmentBillDetail.newInstance());

        transaction.commit();
    }
}
