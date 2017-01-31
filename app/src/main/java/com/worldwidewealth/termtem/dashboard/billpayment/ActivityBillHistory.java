package com.worldwidewealth.termtem.dashboard.billpayment;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.billpayment.adapter.AdapterListBillHistory;
import com.worldwidewealth.termtem.dialog.DialogBillDetail;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityBillHistory extends AppCompatActivity {

    private Spinner mSpinnerBill;
    private ListView mListHistory;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_history);
        initSpinner();
        initListHistory();
    }

    private void initSpinner(){
        mSpinnerBill = (Spinner) findViewById(R.id.spinner_bill_history);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bill_history_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBill.setAdapter(adapter);
        mSpinnerBill.setSelection(0);

    }

    private void initListHistory(){
        mListHistory = (ListView) findViewById(R.id.list_history);
        mListHistory.setAdapter(new AdapterListBillHistory(this));
        mListHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogBillDetail dialogBillDetail = new DialogBillDetail(ActivityBillHistory.this);
                dialogBillDetail.show();
            }
        });
    }
}
