package com.worldwidewealth.termtem.dashboard.billpayment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.Size;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.scan.ActivityScan;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.model.AgentResponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ScanBillActivity extends MyAppcompatActivity {

    private DecoratedBarcodeView mBarcodeView;
    private RadioGroup mReGroup;
    private static BarcodeFormat mBarcodeFormat = BarcodeFormat.CODABAR;
    private int mCurrentChecked = -1;

    public static final String TAG = ScanBillActivity.class.getSimpleName();
    public static final String KEY_SCAN_RESULT = "scanresult";

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Log.e(TAG, "ScanResult: "+result);
            Log.e(TAG, "ScanFormat: "+result.getBarcodeFormat().name());
            mBarcodeView.pause();
//            boolean isFormatCorrect;
            MyApplication.LeavingOrEntering.currentActivity = null;

            if (MyApplication.getTypeScreenLayout() != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
/*
                switch (mBarcodeFormat) {
                    case QR_CODE:
                        isFormatCorrect = mBarcodeFormat == result.getBarcodeFormat();
                        break;
                    default:
                        isFormatCorrect = result.getBarcodeFormat() != BarcodeFormat.QR_CODE;
                        break;
                }

                if (!isFormatCorrect) {
                    Toast.makeText(ScanBillActivity.this, getString(R.string.unavailable_format_code), Toast.LENGTH_SHORT).show();
                    mBarcodeView.resume();
                    return;
                }
*/

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }
//            Log.e(TAG, ""+getParent().toString());
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
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBarcodeFormat = BarcodeFormat.CODABAR;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bill);
        bindView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_checked", mCurrentChecked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentChecked = savedInstanceState.getInt("current_checked");
    }

    @Override
    public void onBackPressed() {
        if (MyApplication.getTypeScreenLayout() != Configuration.SCREENLAYOUT_SIZE_XLARGE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onBackPressed();
    }

    @Override
    public void onResume() {
        mBarcodeView.resume();
        super.onResume();

        if (mCurrentChecked == -1){
            mCurrentChecked = R.id.scan_bill_code;
        }

        Collection<BarcodeFormat> BARCODE_TYPES;

        if (MyApplication.getTypeScreenLayout() != Configuration.SCREENLAYOUT_SIZE_XLARGE) {

            switch (mCurrentChecked) {
                case R.id.scan_bill_code:
                    BARCODE_TYPES = Collections.unmodifiableCollection(Arrays.asList(BarcodeFormat.CODE_128));
                    mBarcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(BARCODE_TYPES, null, null));
                    break;
                case R.id.scan_bill_qr:
                    BARCODE_TYPES = Collections.unmodifiableCollection(Arrays.asList(BarcodeFormat.QR_CODE));
                    mBarcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(BARCODE_TYPES, null, null));
                    break;
            }

        } else {

            BARCODE_TYPES = Collections.unmodifiableCollection(Arrays.asList(BarcodeFormat.CODE_128, BarcodeFormat.QR_CODE));
            mBarcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(BARCODE_TYPES, null, null));

        }

        mBarcodeView.decodeContinuous(barcodeCallback);
        mBarcodeView.getBarcodeView().getCameraSettings().setAutoFocusEnabled(true);
        mBarcodeView.getBarcodeView().getCameraInstance().setTorch(true);
        mReGroup.check(mCurrentChecked);

        mReGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (mCurrentChecked == i) return;
                mCurrentChecked = i;
                MyApplication.LeavingOrEntering.currentActivity = null;

                switch (i) {
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

        mBarcodeView.getViewFinder().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mBarcodeView.getViewFinder().getMeasuredWidth(), height = mBarcodeView.getViewFinder().getMeasuredHeight();
                mBarcodeView.getBarcodeView().setFramingRectSize(new Size(width, height));
            }
        });

    }

    @Override
    public void onPause() {
        mBarcodeView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void bindView(){
        mBarcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_view);
        mReGroup = (RadioGroup) findViewById(R.id.group_scan);
    }

}
