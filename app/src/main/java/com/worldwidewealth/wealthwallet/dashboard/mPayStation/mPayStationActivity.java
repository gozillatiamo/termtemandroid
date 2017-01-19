package com.worldwidewealth.wealthwallet.dashboard.mPayStation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.SplashScreenWWW;
import com.worldwidewealth.wealthwallet.dashboard.topup.adapter.BtnTopupAdapter;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentAirtimeVAS;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentChoiceTopup;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.model.GenBarcodeRequestModel;
import com.worldwidewealth.wealthwallet.model.LoadButtonResponseModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.until.BottomAction;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.Until;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class mPayStationActivity extends AppCompatActivity {

    private RecyclerView mRecyclerAmount;
    private View mIncludeBottomAction, mLayoutEditAmountOther;
    private BottomAction mBottomAction;
    private EditText mEditAmountOther;
    private WebView mWebView;
    private String[] mListAmount;
    private APIServices services;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_pay_station);
        services = APIServices.retrofit.create(APIServices.class);
        mListAmount = new String[]{
                "50",
                "100",
                "150",
                "200",
                "250",
                "300",
                "500",
                "1000",
                getString(R.string.other)
        };
        initWidgets();
        initListAmount();
    }

    private void initWidgets(){
        mRecyclerAmount = (RecyclerView) findViewById(R.id.recycler_mpay_amount);
        mIncludeBottomAction = (View) findViewById(R.id.include_bottom_action);
        mLayoutEditAmountOther = (View) findViewById(R.id.layout_edit_amount_other);
        mEditAmountOther = (EditText) findViewById(R.id.edit_amount_other);
        mWebView = (WebView) findViewById(R.id.webview);
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
                if (!s.toString().equals("")) {
                   amt = Double.parseDouble(s.toString());
                }
                mBottomAction.updatePrice(amt);
            }
        });
        mBottomAction = new BottomAction(this, mIncludeBottomAction, BottomAction.NEXT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String AMT = (mBottomAction.getPrice());
                if (Double.parseDouble(AMT) == 0) return;

                Call<ResponseBody> call = services.genBarcode(
                        new RequestModel(APIServices.ACTIONGENBARCODE,
                                new GenBarcodeRequestModel(AMT.replaceAll("\\.", ""))));
                APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Object responseValues = EncryptionData.getModel(mPayStationActivity.this, call, response.body(), this);

                        if (responseValues == null) return;

                        if (responseValues instanceof ResponseModel){
                        } else {
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        new ErrorNetworkThrowable(t).networkError(mPayStationActivity.this, call, this);
                    }
                });
/*
                mWebView.setVisibility(View.VISIBLE);
                mWebView.getSettings().setJavaScriptEnabled(true);


                mWebView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        // Activities and WebViews measure progress with different scales.
                        // The progress meter will automatically disappear when we reach 100%
                        mPayStationActivity.this.setProgress(progress * 1000);
                    }
                });
                mWebView.setWebViewClient(new WebViewClient() {
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(mPayStationActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                    }
                });

                mWebView.loadUrl("http://www.youtube.com/");
*/
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
            View rootView = LayoutInflater.from(mPayStationActivity.this).inflate(R.layout.item_topup, parent, false);
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
                                }
                            });
                }
            }
            previousSelectedPosition = position;
            mBottomAction.updatePrice(Double.parseDouble(nowAmt));
//        clearSelected();


        }


        public class ViewHolder extends RecyclerView.ViewHolder{

            public TextView mTextProductItem, mTextCurency;
            public CardView mBtnChoice;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextProductItem = (TextView) itemView.findViewById(R.id.txt_product_item);
                mTextCurency = (TextView) itemView.findViewById(R.id.txt_currency);
                mBtnChoice = (CardView) itemView.findViewById(R.id.btn_choice);
            }
        }

    }
}
