package com.worldwidewealth.termtem;

import android.os.Bundle;
import android.widget.TextView;

import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;
import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.github.omadahealth.typefaceview.TypefaceTextView;

/**
 * Created by gozillatiamo on 11/10/17.
 */

public class ActivityLockScreen extends AppLockActivity{

    private TypefaceTextView mPinCodeStepTextview, mPinCodeForgotTextview;
    @Override
    public void showForgotDialog() {

    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {

    }


    @Override
    public int getContentView() {
        return R.layout.activity_lock_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        setupView();
    }

    private void bindView(){
        mPinCodeStepTextview = findViewById(R.id.pin_code_step_textview);
        mPinCodeForgotTextview = findViewById(R.id.pin_code_forgot_textview);
    }

    private void setupView(){
        mPinCodeStepTextview.setText(R.string.enter_pin_code);
        mPinCodeForgotTextview.setText(R.string.forgot_password);

        LockManager<ActivityLockScreen> lockManager = LockManager.getInstance();
        lockManager.getAppLock().setLogoId(R.drawable.termtem_logo);
    }
}
