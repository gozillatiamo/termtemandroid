package com.worldwidewealth.termtem.dashboard.billpayment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.scan.ActivityScan;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.AgentResponse;

import java.util.List;


public class ScanBillActivity extends MyAppcompatActivity {

    private DecoratedBarcodeView mBarcodeView;
    private RadioGroup mReGroup;
    private BarcodeFormat mBarcodeFormat = BarcodeFormat.CODABAR;

    public static final String TAG = ScanBillActivity.class.getSimpleName();
    public static final String KEY_SCAN_RESULT = "scanresult";

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Log.e(TAG, "ScanResult: "+result);
            mBarcodeView.pause();
            boolean isFormatCorrect;

            switch (mBarcodeFormat){
                case QR_CODE:
                    isFormatCorrect = mBarcodeFormat == result.getBarcodeFormat();
                    break;
                default:
                    isFormatCorrect = result.getBarcodeFormat() != BarcodeFormat.QR_CODE;
                    break;
            }

            if (!isFormatCorrect){
                Toast.makeText(ScanBillActivity.this, getString(R.string.unavailable_format_code), Toast.LENGTH_SHORT).show();
                mBarcodeView.resume();
                return;
            }
            Log.e(TAG, ""+getParent().toString());
            Intent intent = getIntent();
            intent.putExtra(KEY_SCAN_RESULT, result.getText());

            setResult(Activity.RESULT_OK, intent);
            finish();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bill);

        bindView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mBarcodeView.decodeContinuous(barcodeCallback);

        mReGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.scan_bill_code:
                        mBarcodeFormat = BarcodeFormat.CODABAR;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case R.id.scan_bill_qr:
                        mBarcodeFormat = BarcodeFormat.QR_CODE;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                }
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



    private void bindView(){
        mBarcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_view);
        mReGroup = (RadioGroup) findViewById(R.id.group_scan);
    }

}
