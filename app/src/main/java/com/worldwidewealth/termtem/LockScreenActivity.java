package com.worldwidewealth.termtem;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.worldwidewealth.termtem.database.AppDatabase;
import com.worldwidewealth.termtem.database.table.UserPin;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.util.TermTemSignIn;
import com.worldwidewealth.termtem.widgets.FingerprintController;


public class LockScreenActivity extends MyAppcompatActivity implements View.OnClickListener{

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private TextView mTextStatus;
    private AppCompatImageView mIconFingerprint;
    private TextView mTextScanStatus;
    private View mLayoutFingerprint;
    private Button mBtnChangUser;
    private String mFirstPass;
    private String mAction;
    private String mUsername;
    private String mPassword;
    private Bundle mBundle;
    private UserPin mUserPin, mUserHasFinger;
    private FingerprintController mFingerprintController;

    //Dialog setup fingerprint
    private Dialog mDialogFingerprint;
    private AppCompatImageView mIconFingerprintDialog;
    private TextView mTextScanStatusDialog;

    public static final String KEY_ACTION = "action";
    public static final String SETUP_PIN = "setuppin";
    public static final String LOCK_SCREEN = "lockscreen";
    public static final String USERPIN = "userpin";
//    public static final String USERNAME = "username";
//    public static final String PASSWORD = "password";
//    public static int LOCKSCREEN = 0x0111;

    public static final String TAG = LockScreenActivity.class.getSimpleName();

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            switch (mAction){
                case  LOCK_SCREEN:
                    if (pin.equals(mUserPin.getPinId())){
                        new TermTemSignIn(LockScreenActivity.this, TermTemSignIn.TYPE.NEWLOGIN,
                                new DialogCounterAlert.DialogProgress(LockScreenActivity.this).show())
                                .checkWifi(mUsername, mPassword);
                    } else {
                        mPinLockView.resetPinLockView();
                        mTextStatus.setText(R.string.status_pin_incorrect);
                    }

                    break;
                case SETUP_PIN:
                    if (mFirstPass == null){
                        mFirstPass = pin;
                        mPinLockView.resetPinLockView();
                        mTextStatus.setText(R.string.status_confirm_pin);
                    } else if (!mFirstPass.equals(pin)){
                        mPinLockView.resetPinLockView();
                        mTextStatus.setText(R.string.status_confirm_pin_incorrect);
                    } else {

                        if (checkPinCode(pin)) {
                            savePinDb(pin);
                            AlertDialog alertDialog = new AlertDialog.Builder(LockScreenActivity.this)
                                    .setTitle(R.string.success)
                                    .setMessage(R.string.status_complete_setup)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mFingerprintController = FingerprintController.getInstance(LockScreenActivity.this);

                                            if (mFingerprintController != null){
                                                setFingerprint();
                                            } else {
                                                LockScreenActivity.this.finish();
                                            }
                                        }
                                    }).show();
                        } else {
                            mTextStatus.setText(R.string.status_pin_duplicate);
                            mPinLockView.resetPinLockView();
                            mFirstPass = null;
                        }
                    }
                    break;

            }
            Log.d(TAG, "Pin complete: " + pin);
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        bindView();
        setupPinPad();
        mBundle = getIntent().getExtras();
        mAction = mBundle.getString(KEY_ACTION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAction();
    }

    private void bindView(){

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        mTextStatus = (TextView) findViewById(R.id.text_status);
        mBtnChangUser = (Button) findViewById(R.id.btn_other_user);
        mIconFingerprint = (AppCompatImageView) findViewById(R.id.icon_fingerprint);
        mTextScanStatus = (TextView) findViewById(R.id.text_status_fingerprint);
        mLayoutFingerprint = findViewById(R.id.layout_fingerprint);

        mBtnChangUser.setOnClickListener(this);

    }

    private void setupPinPad(){
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);

        mPinLockView.setPinLength(4);

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
    }

    private void checkAction(){
        switch (mAction){
            case LOCK_SCREEN:
                mUserPin = mBundle.getParcelable(USERPIN);
                mUsername = mUserPin.getUserid();
                mPassword = mUserPin.getPassword();
                mBtnChangUser.setVisibility(View.VISIBLE);
                checkHasFingerScan();
                break;
            case SETUP_PIN:
                mTextStatus.setText(R.string.status_set_pin);
                break;
        }
    }

    private boolean checkPinCode(String pin){
        UserPin userPin = AppDatabase.getAppDatabase(this).userPinDao().getuserPinByPinId(pin);
        return userPin == null;
    }

    private void savePinDb(String pin){
        mUsername = EncryptionData.DecryptData(Global.getInstance().getUSERNAME(),
                Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
        mPassword = EncryptionData.DecryptData(Global.getInstance().getPASSWORD(),
                Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());

        mUserPin = new UserPin();
        mUserPin.setUserid(mUsername);
        mUserPin.setPassword(mPassword);
        mUserPin.setPinId(pin);

        AppDatabase.getAppDatabase(this).userPinDao().insert(mUserPin);

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkHasFingerScan(){
        mUserHasFinger = AppDatabase.getAppDatabase(this).userPinDao().getUserUseFingerprint();
        if (mUserHasFinger != null){
            mLayoutFingerprint.setVisibility(View.VISIBLE);
            checkPermission();
            mFingerprintController = FingerprintController.getInstance(this);
            if (mFingerprintController != null){
                mFingerprintController.initController(new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        mIconFingerprint.setColorFilter(ContextCompat.getColor(LockScreenActivity.this, R.color.colorAccent));
                        mTextScanStatus.setText(R.string.status_fingerprint_success);
                        mTextScanStatus.setTextColor(ContextCompat.getColor(LockScreenActivity.this, R.color.colorAccent));
                        mUsername = mUserHasFinger.getUserid();
                        mPassword = mUserHasFinger.getPassword();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new TermTemSignIn(LockScreenActivity.this, TermTemSignIn.TYPE.NEWLOGIN,
                                        new DialogCounterAlert.DialogProgress(LockScreenActivity.this).show())
                                        .checkWifi(mUsername, mPassword);
                            }
                        }, 700);

                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        mIconFingerprint.setColorFilter(ContextCompat.getColor(LockScreenActivity.this, R.color.colorFail));
                        mTextScanStatus.setText(R.string.status_fingerprint_fail);
                        mTextScanStatus.setTextColor(ContextCompat.getColor(LockScreenActivity.this, R.color.colorFail));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIconFingerprint.setColorFilter(ContextCompat.getColor(LockScreenActivity.this, R.color.white));
                                mTextScanStatus.setText(R.string.status_fingerprint_normal);
                                mTextScanStatus.setTextColor(ContextCompat.getColor(LockScreenActivity.this, R.color.white));
                            }
                        }, 700);

                    }
                });
            }
        }
    }
    private void setFingerprint(){


        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.introduce)
                .setMessage(R.string.msg_introduce_fingerprint)
                .setPositiveButton(R.string.using, new DialogInterface.OnClickListener() {

                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserPin userPin = AppDatabase.getAppDatabase(LockScreenActivity.this).userPinDao().getUserUseFingerprint();
                        if (userPin != null) {
                            AlertDialog alertDialog = new AlertDialog.Builder(LockScreenActivity.this, R.style.MyAlertDialogError)
                                    .setTitle(R.string.error)
                                    .setMessage(R.string.error_has_fingerprint)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            LockScreenActivity.this.finish();
                                        }
                                    }).show();
                            return;
                        }

                        checkPermission();
                        showDialogSetupFingerprint();
                        mFingerprintController.initController(new FingerprintManager.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                mUserPin.setUseFingerprint(true);
                                AppDatabase.getAppDatabase(LockScreenActivity.this).userPinDao().updateUserPin(mUserPin);
                                mIconFingerprintDialog.setColorFilter(ContextCompat.getColor(LockScreenActivity.this, R.color.colorSuccess));
                                mTextScanStatusDialog.setText(R.string.status_fingerprint_success);
                                mTextScanStatusDialog.setTextColor(ContextCompat.getColor(LockScreenActivity.this, R.color.colorSuccess));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDialogFingerprint.dismiss();
                                        LockScreenActivity.this.finish();
                                    }
                                }, 700);
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                mIconFingerprintDialog.setColorFilter(ContextCompat.getColor(LockScreenActivity.this, R.color.colorFail));
                                mTextScanStatusDialog.setText(R.string.status_fingerprint_fail);
                                mTextScanStatusDialog.setTextColor(ContextCompat.getColor(LockScreenActivity.this, R.color.colorFail));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIconFingerprintDialog.setColorFilter(ContextCompat.getColor(LockScreenActivity.this, R.color.color_normal));
                                        mTextScanStatusDialog.setText(R.string.status_fingerprint_normal);
                                        mTextScanStatusDialog.setTextColor(ContextCompat.getColor(LockScreenActivity.this, R.color.color_normal));
                                    }
                                }, 700);

                            }
                        });
                    }
                })
                .setNegativeButton(R.string.ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LockScreenActivity.this.finish();
                    }
                }).show();

        alertDialog.setOnShowListener(new MyShowListener());

    }

    private void showDialogSetupFingerprint(){
        mDialogFingerprint = new Dialog(this);
        mDialogFingerprint.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogFingerprint.setContentView(R.layout.dialog_setup_fingerprint);
        mDialogFingerprint.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialogFingerprint.setCancelable(false);
        mDialogFingerprint.show();

        mIconFingerprintDialog = mDialogFingerprint.findViewById(R.id.icon_fingerprint);
        mTextScanStatusDialog = mDialogFingerprint.findViewById(R.id.text_status_fingerprint);
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.USE_FINGERPRINT)){
                 Toast.makeText(this, "shouldShowRequest", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, 0);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_other_user:

                Intent intent = new Intent(this, MainActivity.class);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent);
                finish();

                break;
        }
    }

//    private void showDialogEnterUser(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(R.layout.dialog_enter_username_pass);
//        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.setOnShowListener(new MyShowListener());
//        alertDialog.show();
//    }
}
