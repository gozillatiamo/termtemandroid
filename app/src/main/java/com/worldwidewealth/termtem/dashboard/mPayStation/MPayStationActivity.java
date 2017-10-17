package com.worldwidewealth.termtem.dashboard.mPayStation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.widgets.SelectAmountAndOtherFragment;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.GenBarcodeRequestModel;
import com.worldwidewealth.termtem.model.MPayStationResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.BottomAction;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MPayStationActivity extends MyAppcompatActivity {

    private View mIncludeBottomAction;
    private BottomAction mBottomAction;
    private TextView mToolbarTitle;
    private WebView mWebView;
    private String[] mListAmount;
    private APIServices services;
    private Toolbar mToolbar;
    private int mType;
    private static final String TAG = MPayStationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_m_pay_station);
        services = APIServices.retrofit.create(APIServices.class);
        mType = getIntent().getExtras().getInt("type");
        mListAmount = new String[]{
                "100",
                "150",
                "200",
                "300",
                "400",
                "500",
                "600",
                "700",
                "800",
                "900",
                "1000",
                getString(R.string.other)
        };




        initWidgets();
        initToolbar();
        initGrid();
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

    private void initGrid(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_select_amount, SelectAmountAndOtherFragment.newInstance(mBottomAction, mListAmount, 0))
                .commit();

    }

    private void initToolbar(){
        switch (mType){
            case SelectChoiceMpayActivity.MPAY_STATION:
                mToolbarTitle.setText(getString(R.string.mpay_station));
                break;
            case SelectChoiceMpayActivity.MPAY_BANK:
                mToolbarTitle.setText(getString(R.string.mpay_bank));
                break;
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void initWidgets(){
//        mRecyclerAmount = (RecyclerView) findViewById(R.id.recycler_mpay_amount);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mIncludeBottomAction = (View) findViewById(R.id.include_bottom_action);
//        mLayoutEditAmountOther = (View) findViewById(R.id.layout_edit_amount_other);
//        mEditAmountOther = (EditText) findViewById(R.id.edit_amount_other);
        mWebView = (WebView) findViewById(R.id.webview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_mpay);

/*
        mEditAmountOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double amt = 0;
                if (!s.toString().equals("") && !s.toString().equals(".")) {
                   amt = Double.parseDouble(s.toString());
                }
                mBottomAction.updatePrice(amt);
            }
        });
*/
        mBottomAction = new BottomAction(this, mIncludeBottomAction, BottomAction.NEXT, new BottomAction.OnActionClickListener() {
            @Override
            public void onActionClick() {
                String AMT = (mBottomAction.getPrice());
                if (Double.parseDouble(AMT) < 1 || Double.parseDouble(AMT) > 49000){
                    Toast.makeText(MPayStationActivity.this, getString(R.string.alert_amount_mpay_over), Toast.LENGTH_LONG).show();
                    return;
                }
                mBottomAction.setEnable(false);
                new DialogCounterAlert.DialogProgress(MPayStationActivity.this).show();
                Call<ResponseBody> call = services.genBarcode(
                        new RequestModel(APIServices.ACTIONGENBARCODE,
                                new GenBarcodeRequestModel(AMT.replaceAll("\\.", ""), mType)));
                APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Object responseValues = EncryptionData.getModel(MPayStationActivity.this, call, response.body(), this);

                        if (responseValues == null){
                            return;
                        }

                        if (responseValues instanceof String){
                            DialogCounterAlert.DialogProgress.dismiss();
                            MPayStationResponse mPayStationResponse = new Gson().fromJson((String)responseValues, MPayStationResponse.class);

                            /*if (APIServices.retrofit.baseUrl().toString().equals(getString(R.string.server_dev))) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(mPayStationResponse.getURL()));
                                startActivity(i);
                            } else {*/
                            Log.e(TAG, "mPay Url: "+mPayStationResponse.getURL());

                            if (mPayStationResponse.getURL() != null && !mPayStationResponse.getURL().equals("")){
                                mWebView.setVisibility(View.VISIBLE);
                                mWebView.getSettings().setJavaScriptEnabled(true);


                                mWebView.setWebChromeClient(new WebChromeClient() {
                                    public void onProgressChanged(WebView view, int progress) {
                                        // Activities and WebViews measure progress with different scales.
                                        // The progress meter will automatically disappear when we reach 100%
                                        MPayStationActivity.this.setProgress(progress * 1000);
                                    }
                                });

                                mWebView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                        super.onPageStarted(view, url, favicon);
                                        new DialogCounterAlert.DialogProgress(MPayStationActivity.this).show();
                                    }

                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        super.onPageFinished(view, url);
                                        DialogCounterAlert.DialogProgress.dismiss();
                                    }

                                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                        DialogCounterAlert.DialogProgress.dismiss();
                                    }



                                });

                                mWebView.loadUrl(mPayStationResponse.getURL());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(MPayStationActivity.this, call, this);
                        mBottomAction.setEnable(true);
                    }
                });
            }
        });
    }
}
