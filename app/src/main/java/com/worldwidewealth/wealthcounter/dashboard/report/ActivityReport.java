package com.worldwidewealth.wealthcounter.dashboard.report;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.report.adapter.ReportAdapter;
import com.worldwidewealth.wealthcounter.until.SimpleDividerItemDecoration;

public class ActivityReport extends AppCompatActivity {

    private ViewHolder mHolder;
    private ReportAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mHolder = new ViewHolder(this);

        initToolbar();
        initListReport();
    }

    private void initToolbar(){
        this.setSupportActionBar(mHolder.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListReport(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ReportAdapter(this);
        mHolder.mListReport.setLayoutManager(layoutManager);
        mHolder.mListReport.setAdapter(mAdapter);
        mHolder.mListReport.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private class ViewHolder{
        private RecyclerView mListReport;
        private Toolbar mToolbar;

        public ViewHolder(Activity itemView){
            mListReport = (RecyclerView) itemView.findViewById(R.id.list_report);
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
        }
    }
}
