package com.worldwidewealth.wealthwallet.dashboard.billpayment;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.billpayment.adapter.AdapterBillBox;
import com.worldwidewealth.wealthwallet.until.ToolbarActionModeCallback;

import java.util.ArrayList;
import java.util.List;

public class ActivityOneBill extends AppCompatActivity {

    private List<ContentValues> mListData;
    private String[] mTypeBill, mExpBill, mStatusBill, mPriceBill;
    private TypedArray mIcon;
    private AdapterBillBox mAdapter;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_bill);

        initDataValue();
        initToolbar();
        initListBill();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mActionMode != null)
            mActionMode.finish();
    }

    private void initDataValue(){
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

    }

    private void initListBill(){
        final ListView list_bill = (ListView) findViewById(R.id.list_bill);
        mAdapter = new AdapterBillBox(this, mListData);
        list_bill.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        list_bill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mActionMode != null){
                    onListItemSelect(position);
                } else {
                    Intent intent = new Intent(ActivityOneBill.this, ActivityBillDetail.class);
                    intent.putExtra("type", "detail");
                    startActivity(intent);
                }
            }
        });

        list_bill.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });


    }

    private void onListItemSelect(int position){
        mAdapter.toggleSelection(position);
        boolean hasCheckedItem = mAdapter.getSelectedCount() > 0;
        if (hasCheckedItem && mActionMode == null){
            mAdapter.toggleMode();
            mActionMode = startSupportActionMode(new ToolbarActionModeCallback(this, mAdapter));
        } else if (!hasCheckedItem && mActionMode != null){
            mActionMode.finish();

        }

        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(mAdapter.getSelectedCount()) + " Selected");
    }

    public void setNullToActionMode(){
        if (mActionMode != null)
            mActionMode = null;
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("บิลทั้งหมด");

    }

}
