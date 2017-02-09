package com.worldwidewealth.termtem.dashboard.mPayStation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.GenBarcodeRequestModel;
import com.worldwidewealth.termtem.model.MPayStationResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.until.BottomAction;
import com.worldwidewealth.termtem.until.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.until.Until;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MPayStationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerAmount;
    private View mIncludeBottomAction, mLayoutEditAmountOther;
    private BottomAction mBottomAction;
    private TextView mToolbarTitle;
    private EditText mEditAmountOther;
    private WebView mWebView;
    private String[] mListAmount;
    private APIServices services;
    private Toolbar mToolbar;
    private int mType;
    private static final String TAG = MPayStationActivity.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
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
        initListAmount();
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
        switch (mType){
            case SelectChoiceMpayActivity.MPAY_STATION:
                mToolbarTitle.setText(getString(R.string.title_mpay_station));
                break;
            case SelectChoiceMpayActivity.MPAY_BANK:
                mToolbarTitle.setText(getString(R.string.title_mpay_bank));
                break;
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void initWidgets(){
        mRecyclerAmount = (RecyclerView) findViewById(R.id.recycler_mpay_amount);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mIncludeBottomAction = (View) findViewById(R.id.include_bottom_action);
        mLayoutEditAmountOther = (View) findViewById(R.id.layout_edit_amount_other);
        mEditAmountOther = (EditText) findViewById(R.id.edit_amount_other);
        mWebView = (WebView) findViewById(R.id.webview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_mpay);

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
        mBottomAction = new BottomAction(this, mIncludeBottomAction, BottomAction.NEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String AMT = (mBottomAction.getPrice());
                if (Double.parseDouble(AMT) < 1 || Double.parseDouble(AMT) > 49000){
                    Toast.makeText(MPayStationActivity.this, getString(R.string.alert_amount_mpay_over), Toast.LENGTH_LONG).show();
                    return;
                }
                mBottomAction.setEnable(false);
                new DialogCounterAlert.DialogProgress(MPayStationActivity.this);
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
                                        new DialogCounterAlert.DialogProgress(MPayStationActivity.this);
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

    private void initListAmount(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerAmount.setLayoutManager(gridLayoutManager);
        mRecyclerAmount.setAdapter(new AmountBtnAdapter());

    }


    private class AmountBtnAdapter extends RecyclerView.Adapter<AmountBtnAdapter.ViewHolder>{

        public int previousSelectedPosition = -1;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(MPayStationActivity.this).inflate(R.layout.item_topup, parent, false);
            Until.setupUI(rootView);

            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTextProductItem.setText(getItem(position));

            holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
            holder.mBtnChoice.setCardBackgroundColor(getResources().getColor(android.R.color.white));

            if (position == previousSelectedPosition)
                setBackgroundSelect(holder, position);


            holder.mBtnChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickChoiceTopup(holder, position);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mListAmount.length;
        }

        public String getItem(int position){
            return mListAmount[position];
        }

        private void setBackgroundSelect(ViewHolder holder, int position){
            holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.white));
            holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.white));
            holder.mBtnChoice.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));


            if (previousSelectedPosition == position) return;

            clearSelected();
        }

        public void clearSelected(){

            if (previousSelectedPosition != -1){
                ViewHolder holder = (ViewHolder)
                        mRecyclerAmount.findViewHolderForAdapterPosition(previousSelectedPosition);
                if (holder == null) return;
                holder.mTextProductItem.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                holder.mTextCurency.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
                holder.mBtnChoice.setCardBackgroundColor(getResources().getColor(android.R.color.white));

                mLayoutEditAmountOther.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mLayoutEditAmountOther.setVisibility(View.INVISIBLE);
                                mRecyclerAmount.setFocusable(true);
                                mRecyclerAmount.requestFocus();
                            }
                        });

//            mAdapter.previousSelectedPosition = -1;
            }

        }

        private void setClickChoiceTopup(ViewHolder holder, int position){

            String nowAmt = "0";
            if (position != -1) {
                if (previousSelectedPosition == position) return;
                setBackgroundSelect(holder, position);
                if (position != getItemCount()-1) {
                    nowAmt = getItem(position);
                } else {
                    mEditAmountOther.setText("");
                    mLayoutEditAmountOther.setVisibility(View.VISIBLE);
                    mLayoutEditAmountOther.setAlpha(0.0f);
                    mLayoutEditAmountOther.animate()
                            .alpha(1.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mEditAmountOther.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(mEditAmountOther, InputMethodManager.SHOW_IMPLICIT);
                                }
                            });
                }
            }
            previousSelectedPosition = position;
            mBottomAction.updatePrice(Double.parseDouble(nowAmt));
//        clearSelected();


        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            private TextView mTextProductItem, mTextCurency;
            private CardView mBtnChoice;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextProductItem = (TextView) itemView.findViewById(R.id.txt_product_item);
                mTextCurency = (TextView) itemView.findViewById(R.id.txt_currency);
                mBtnChoice = (CardView) itemView.findViewById(R.id.btn_choice);
            }
        }

    }
}
