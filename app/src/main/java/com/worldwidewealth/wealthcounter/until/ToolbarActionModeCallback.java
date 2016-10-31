package com.worldwidewealth.wealthcounter.until;


import android.content.Context;
import android.content.Intent;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.ActivityBillDetail;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.ActivityOneBill;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.adapter.AdapterBillBox;

/**
 * Created by MyNet on 27/10/2559.
 */

public class ToolbarActionModeCallback implements ActionMode.Callback {
    private AdapterBillBox mAdapter;
    private Context mContext;
    public ToolbarActionModeCallback(Context context, AdapterBillBox adapter){
        this.mAdapter = adapter;
        this.mContext = context;
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
        switch (item.getItemId()){
            case R.id.pay:
                Intent intent = new Intent(mContext, ActivityBillDetail.class);
                intent.putExtra("type", "onebill");
                mContext.startActivity(intent);

                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdapter.removeSelection();
        ((ActivityOneBill)mContext).setNullToActionMode();
    }
}
