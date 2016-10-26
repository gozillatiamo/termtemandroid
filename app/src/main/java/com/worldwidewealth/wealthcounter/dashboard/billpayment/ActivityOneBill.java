package com.worldwidewealth.wealthcounter.dashboard.billpayment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.adapter.ListBillAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityOneBill extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_bill);

        ListView list_bill = (ListView) findViewById(R.id.list_bill);
        list_bill.setAdapter(new ListBillAdapter(this));
    }

}
