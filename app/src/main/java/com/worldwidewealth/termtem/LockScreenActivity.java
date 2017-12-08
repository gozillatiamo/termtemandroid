package com.worldwidewealth.termtem;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.worldwidewealth.termtem.database.AppDatabase;
import com.worldwidewealth.termtem.database.table.UserPin;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.util.TermTemSignIn;


public class LockScreenActivity extends MyAppcompatActivity implements View.OnClickListener{

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private TextView mTextStatus;
    private Button mBtnChangUser;
    private String mFirstPass;
    private String mAction;
    private String mUsername;
    private String mPassword;
    private Bundle mBundle;
    private UserPin mUserPin;

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
                                            LockScreenActivity.this.finish();
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

        UserPin userPin = new UserPin();
        userPin.setUserid(mUsername);
        userPin.setPassword(mPassword);
        userPin.setPinId(pin);

        AppDatabase.getAppDatabase(this).userPinDao().insert(userPin);

    }

    private void functionSetUp(){
/*
        if (!mBundle.containsKey(USERNAME)){
            showDialogEnterUser();
        } else {
            mUsername = mBundle.getString(USERNAME);
            mPassword = mBundle.getString(PASSWORD);
        }
*/
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
