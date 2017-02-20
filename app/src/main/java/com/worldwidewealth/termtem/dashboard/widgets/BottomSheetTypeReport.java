package com.worldwidewealth.termtem.dashboard.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;

import com.worldwidewealth.termtem.R;

/**
 * Created by user on 15-Feb-17.
 */

public class BottomSheetTypeReport extends BottomSheetDialog {

    public static final int TOPUP_REPORT_MENU = 0;
    public static final int CASHIN_AGENT_REPORT_MENU = 1;

    private AppCompatButton mMenuTopup, mMenuCashInAgent;


    public BottomSheetTypeReport(@NonNull Context context) {
        super(context);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.bottomsheet_type_report);
        initWidgets();
    }

    @Override
    public void show() {
        super.show();
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    private void initWidgets(){
        mMenuTopup = (AppCompatButton) findViewById(R.id.btn_topup_report);
        mMenuCashInAgent = (AppCompatButton) findViewById(R.id.btn_cashin_agent_report);
    }

    public void setOnClick(int type, View.OnClickListener listener){
        switch (type){
            case TOPUP_REPORT_MENU:
                mMenuTopup.setOnClickListener(listener);
                break;
            case CASHIN_AGENT_REPORT_MENU:
                mMenuCashInAgent.setOnClickListener(listener);
                break;
        }
    }
}
