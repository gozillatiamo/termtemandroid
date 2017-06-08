package com.worldwidewealth.termtem.dashboard.topup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.annotations.Until;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.EslipRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.Util;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityTopup extends MyAppcompatActivity {

    private FrameLayout mContainerTopup;
    private Toolbar mToolbar;
    private String mTopup;
    private ImageView mMenuIcon;
    private TextView mTitle;
    private String mPreviousTransId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopup = this.getIntent().getExtras().getString(FragmentTopup.keyTopup);
        mPreviousTransId = getIntent().getExtras().getString("transid");
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
                mMenuIcon.setImageResource(R.drawable.ic_topup);
                break;
        }

        if (mPreviousTransId == null) initContainer();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreviousTransId != null){
            new DialogCounterAlert.DialogProgress(this).show();

            Util.getPreviousEslip(this, mPreviousTransId, mTopup, R.id.container_topup);
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

    }

    private void initContainer(){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_topup, FragmentTopup.newInstance(mTopup));
        transaction.commit();
    }

    public String getTopupTitle(){
        return mTitle.getText().toString();
    }

}
