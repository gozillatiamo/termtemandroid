package com.worldwidewealth.termtem.dashboard.scan;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.ActivityDashboard;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.adapter.AgentAdapter;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.dashboard.myqrcode.ActivityMyQrCode;
import com.worldwidewealth.termtem.model.AgentResponse;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityScan extends MyAppcompatActivity {

    private DecoratedBarcodeView mBarcodeView;
    private Toolbar mToolbar;
    private Button mBtnMyQR;
    public static final String TAG = ActivityScan.class.getSimpleName();


    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Log.e(TAG, "ScanResult: "+result);
            if (result.getText() != null) {
                String strResult = result.getText();
                AgentResponse agentResponse = null;

                try {
                    agentResponse = new Gson().fromJson(strResult, AgentResponse.class);
                } catch (JsonSyntaxException e){
                    e.printStackTrace();
                    Toast.makeText(ActivityScan.this, getString(R.string.unavailable_qrcode), Toast.LENGTH_SHORT).show();
                }

                if (agentResponse != null){
                    agentResponse.setAgentId(EncryptionData.DecryptData(agentResponse.getAgentId(), agentResponse.getTXID()));
                    addCreditAgent(agentResponse);
                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initWidgets();
        initToolbar();
        mBarcodeView.decodeContinuous(barcodeCallback);
        mBtnMyQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityScan.this, ActivityMyQrCode.class);
                overridePendingTransition(R.anim.slide_in_right, 0);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onResume() {
        mBarcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mBarcodeView.pause();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void initWidgets(){
        mBarcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBtnMyQR = (Button) mBarcodeView.findViewById(R.id.btn_my_qr);
    }

    private void addCreditAgent(AgentResponse agentResponse){
        Bundle bundle = new Bundle();
        bundle.putParcelable(AgentAdapter.AGENTDATA, agentResponse);
        bundle.writeToParcel(Parcel.obtain(), 0);

        Intent intent = new Intent(this, ActivityAddCreditAgent.class);
        intent.putExtra("type", ActivityAddCreditAgent.SCAN);
        intent.putExtra("data", bundle);
        overridePendingTransition(R.anim.slide_in_right, 0);
        startActivity(intent);
        finish();


    }
}
