package com.worldwidewealth.wealthcounter.dashboard.billpayment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.adapter.ListBillAdapter;

import java.util.ArrayList;
import java.util.List;

public class ActivityOneBill extends AppCompatActivity {

    private List<ContentValues> mListData;
    private String[] mTypeBill, mExpBill, mStatusBill, mPriceBill;
    private TypedArray mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_bill);
        initToolbar();
        mListData = new ArrayList<>();
        this.mTypeBill = getResources().getStringArray(R.array.list_type_bill);
        this.mExpBill = getResources().getStringArray(R.array.list_exp_bill);
        this.mStatusBill = getResources().getStringArray(R.array.list_status_bill);
        this.mPriceBill = getResources().getStringArray(R.array.list_price_bill);
        this.mIcon = getResources().obtainTypedArray(R.array.ic_bill);

        for (int i = 0; i < mTypeBill.length; i++){
            ContentValues values = new ContentValues();
            values.put("type", mTypeBill[i]);
            values.put("exp", mExpBill[i]);
            values.put("status", mStatusBill[i]);
            values.put("price", mPriceBill[i]);
            values.put("icon", mIcon.getResourceId(i, -1));
            mListData.add(values);
        }


        final ListView list_bill = (ListView) findViewById(R.id.list_bill);
        list_bill.setAdapter(new ListBillAdapter(this, R.layout.item_list_bill, mListData));
        list_bill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityOneBill.this, ActivityBillDetail.class);
                startActivity(intent);
            }
        });

        list_bill.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list_bill.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                final int checkedCount = list_bill.getCheckedItemCount();

                mode.setTitle(checkedCount + " Selected");

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_bill_select, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        list_bill.getChoiceMode();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("บิลทั้งหมด");
    }

}
