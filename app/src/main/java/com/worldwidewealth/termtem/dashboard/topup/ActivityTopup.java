package com.worldwidewealth.termtem.dashboard.topup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.billpayment.BillPaymentActivity;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupPackage;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.LoadBillServiceResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.SubmitTopupRequestModel;

public class ActivityTopup extends MyAppcompatActivity {

    private FrameLayout mContainerTopup;
    private Toolbar mToolbar;
    private String mTopup;
    private ImageView mMenuIcon;
    private TextView mTitle;
    private String mPhoneNo;
    private String mCarrier;
    private double mLastAmt = 0;
    private boolean hasSubmit = false;

    public static final String KEY_PHONENO = "phoneno";
    public static final String KEY_CARRIER = "carrier";
    public static final String KEY_AMT = "amt";
//    public static final String KEY_BARCODE = "barcode";
    public static final String KEY_BILLSERVICE = "billservice";

    public static final String TAG = ActivityTopup.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        mTopup = bundle.getString(FragmentTopup.keyTopup);

        if (bundle.containsKey(KEY_PHONENO)){
            mPhoneNo = bundle.getString(KEY_PHONENO);
            mCarrier = bundle.getString(KEY_CARRIER);
        }

        if (bundle.containsKey(KEY_AMT)) mLastAmt = bundle.getDouble(KEY_AMT);

        setContentView(R.layout.activity_topup);
        initWidgets();
        initToolbar();
        switch (mTopup){
            case FragmentTopup.MOBILE:
                mMenuIcon.setImageResource(R.drawable.ic_topup);
                break;
            case FragmentTopup.PIN:
                mTitle.setText(getString(R.string.dashboard_pin));
                mMenuIcon.setImageResource(R.drawable.ic_pin_code);
                break;
            case FragmentTopup.VAS:
                mTitle.setText(getString(R.string.vas));
                mMenuIcon.setImageResource(R.drawable.ic_vas);
                break;
            case BillPaymentActivity.BILLPAY:
                mTitle.setText(getString(R.string.dashboard_bill_pay));
                mMenuIcon.setImageResource(R.drawable.ic_bill);
                break;

        }

        hasSubmit = Global.getInstance().hasSubmit();

        if (!hasSubmit){
            if (mPhoneNo != null)
                startWithOldPhoneNO();
            else
                initContainer();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(getSupportFragmentManager().findFragmentById(R.id.container_topup) instanceof FragmentTopupSlip)) {

        if (Global.getInstance().hasSubmit() && Global.getInstance().getSubmitStatus()){
            new DialogCounterAlert.DialogProgress(this).show();

            RequestModel requestModel = Global.getInstance().getLastSubmit();
            SubmitTopupRequestModel submitTopupRequestModel = (SubmitTopupRequestModel) requestModel.getData();
            String errorMsg = getIntent().getExtras().containsKey(FragmentTopupSlip.ERRORMSG) ?
                    getIntent().getExtras().getString(FragmentTopupSlip.ERRORMSG) : null;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_topup, FragmentTopupSlip.newInstance(FragmentTopupSlip.PREVIEW,
                            MyApplication.getTypeToup(requestModel.getAction()),
                            submitTopupRequestModel.getTRANID(),
                            Global.getInstance().getSubmitIsFav(), errorMsg)).commit();
            }

//            Util.getPreviousEslip(this, mTopup, R.id.container_topup);
        }

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

    @Override
    public void onBackPressed() {
        try {
            if (!getSupportFragmentManager()
                    .findFragmentById(R.id.container_topup)
                    .getChildFragmentManager()
                    .popBackStackImmediate()) {
                super.onBackPressed();
            } else if (mTopup.equals(FragmentTopup.VAS)) {
                final View view = getSupportFragmentManager()
                        .findFragmentById(R.id.container_topup)
                        .getView().findViewById(R.id.recycler_vas);

                view.animate()
                        .alpha(1.0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                view.setAlpha(0.0f);
                                view.setVisibility(View.VISIBLE);
                            }
                        });
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            finish();
        }
    }

    private void initWidgets(){
        mContainerTopup = (FrameLayout) findViewById(R.id.container_topup);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_topup);
        mMenuIcon = (ImageView) findViewById(R.id.logo_menu);
        mTitle = (TextView) findViewById(R.id.title);
/*
        mMenuIcon.bringToFront();
        mToolbar.invalidate();
*/
    }
    private void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (hasSubmit){
            new DialogCounterAlert.DialogProgress(this).show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        }


    }

    private void initContainer(){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_topup, FragmentTopup.newInstance(mTopup));
        transaction.commit();
    }

    private void startWithOldPhoneNO(){
        Bundle bundle = getIntent().getExtras();
        switch (mTopup){
            case BillPaymentActivity.BILLPAY:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                        .replace(R.id.container_topup, FragmentTopupPackage.newInstanceBill(
                                BillPaymentActivity.BILLPAY,
                                bundle.containsKey(FragmentTopupPackage.KEY_BARCODE) ? bundle.getString(FragmentTopupPackage.KEY_BARCODE) : null,
                                bundle.containsKey(FragmentTopupPackage.KEY_BILLREF) ? bundle.getString(FragmentTopupPackage.KEY_BILLREF) : null,
                                (LoadBillServiceResponse) getIntent().getExtras().getParcelable(KEY_BILLSERVICE),
                                mPhoneNo))
                        .commit();

                break;
            default:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                        .replace(R.id.container_topup, FragmentTopupPackage.newInstance(mCarrier, mTopup, mPhoneNo, mLastAmt))
                        .commit();

                break;
        }

    }

    public String getTopupTitle(){
        return mTitle.getText().toString();
    }

}
