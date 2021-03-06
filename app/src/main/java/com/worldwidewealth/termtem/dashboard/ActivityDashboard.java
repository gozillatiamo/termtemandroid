package com.worldwidewealth.termtem.dashboard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.LockScreenActivity;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;
import com.worldwidewealth.termtem.dashboard.favorite.FavoritesActivity;
import com.worldwidewealth.termtem.dashboard.inbox.InboxActivity;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.database.AppDatabase;
import com.worldwidewealth.termtem.database.table.UserPin;
import com.worldwidewealth.termtem.model.UserMenuModel;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.util.BadgeDrawable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.ControllerPinCode;
import com.worldwidewealth.termtem.widgets.MenuButtonView;

import java.util.List;
import java.util.Locale;

import static com.worldwidewealth.termtem.MyApplication.getTypeToup;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityDashboard extends MyAppcompatActivity implements View.OnClickListener{

    private ViewHolder mHolder;
    private static final String TAG = ActivityDashboard.class.getSimpleName();
    private List<UserMenuModel> mUserMenuList;
    private LayerDrawable mIconNoti;
    private boolean mIsFromPOS;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBalance();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
        mHolder = new ViewHolder(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mIsFromPOS = bundle.getBoolean("frompos");
        }
        updateBalance();
        initToolbar();
        showDialogAdvice();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Util.setBalanceWallet(mHolder.mIncludeMyWallet);

        mUserMenuList = Global.getInstance().getUserMenuList();
        initBtnMenu();

        MenuButtonView.setsClickable(true);



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
/*
        if (Global.getInstance().getTXID() != null) {
            Util.logoutAPI(this, true);
        }
*/
    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (stackCount != 0) {
            super.onBackPressed();
            return;
           /* Fragment currentFragment = getSupportFragmentManager().getFragments().get(stackCount - 1);
            if (currentFragment instanceof FragmentTopupSlip) return;*/
        }

        new DialogCounterAlert(ActivityDashboard.this, getString(R.string.title_leave_app),
                getString(R.string.msg_leave_app), getString(R.string.title_leave_app),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        MyApplication.LeavingOrEntering.currentActivity = null;
                        Util.logoutAPI(ActivityDashboard.this, true);
                        finish();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_meun, menu);
        MenuItem itemCart = menu.findItem(R.id.action_in_box);
        mIconNoti = (LayerDrawable) itemCart.getIcon();
        BadgeDrawable.setBadgeCount(this, mIconNoti, Global.getInstance().getMSGREAD());

        if (!mIsFromPOS){
            itemCart = menu.findItem(R.id.action_to_pos);
            itemCart.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_in_box:
                Intent intent = new Intent(ActivityDashboard.this, InboxActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent);

                break;
            case R.id.action_to_pos:
                finish();
/*
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.www.pos");
                startActivity(LaunchIntent);
*/
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogAdvice(){
        final ControllerPinCode controllerPinCode = ControllerPinCode.getInstance();

        if (controllerPinCode != null && !controllerPinCode.getIgnorDialog() && !mIsFromPOS){
            AppDatabase appDatabase = AppDatabase.getAppDatabase(this);
            String username = EncryptionData.DecryptData(Global.getInstance().getUSERNAME(),
                    Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
            UserPin userPin = null;
            List<UserPin> list = appDatabase.userPinDao().selectAll();
            for (UserPin userpin: list) {
                if (userpin.getUserid().equals(username)) {
                    userPin = userpin;
                    break;
                }
            }

            if (userPin == null){
                controllerPinCode.showDialogAdvice(this);

                return;
            }
        }

    }

    public void updateBalance(){
        if(Global.getInstance().getAGENTID() != null) {
            Util.updateMyBalanceWallet(this, mHolder.mIncludeMyWallet, mIconNoti);
        }

    }

    private void initToolbar(){
        mHolder.mToolbar.setNavigationIcon(R.drawable.ic_power_settings_new);
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle("");
        mHolder.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogCounterAlert(ActivityDashboard.this, getString(R.string.title_logout),
                        getString(R.string.msg_logout),getString(R.string.title_logout),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Util.logoutAPI(true);
                        Util.backToSignIn(ActivityDashboard.this);

                    }
                });
            }
        });


    }

    private void initBtnMenu(){
        String userDecoded = EncryptionData.DecryptData(Global.getInstance().getUSERNAME(),
                Global.getInstance().getDEVICEID()+Global.getInstance().getTXID());
        String username = null;

        if (userDecoded == null){
            Util.backToSignIn(ActivityDashboard.this);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            username = PhoneNumberUtils.formatNumber(userDecoded, Locale.JAPAN.getCountry());
        } else {
            EditText editText = new EditText(this);
            editText.setText(userDecoded);
            PhoneNumberUtils.formatNumber(editText.getText(), PhoneNumberUtils.FORMAT_NANP);
            username = editText.getText().toString();
        }


        mHolder.mTextUserName.setText(username);
        if (mUserMenuList == null) return;
        for (UserMenuModel model : mUserMenuList){
            MenuButtonView.TYPE type = MenuButtonView.TYPE.asTYPE(model.getBUTTON());
            if (type != null) {

                switch (type) {
                    case CASHIN:
                        mHolder.mMenuCashIn.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case HISTORY:
                        mHolder.mMenuHistory.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case NOTIPAY:
                        mHolder.mMenuNotiPay.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case SCAN:
                    case SETUP:
                    case SUPPORT:
                        if (model.getSTATUS().equals(MenuButtonView.VISIBILITY.SHOW.name()))
                            mHolder.mMenuOther.setMenuVisibility(MenuButtonView.VISIBILITY.SHOW.getVisibility());
                        break;
                    case TOPUP:
                        mHolder.mMenuTopup.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case AGENTCASHIN:
                        mHolder.mMenuAgentCashIn.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case EPIN:
                        mHolder.mMenuTopupPIN.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case VAS:
                        mHolder.mMenuVAS.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case BILL:
                        mHolder.mMenuBillPay.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;



                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_favorites:
                Intent intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                break;
        }
    }

    public class ViewHolder{

        private Toolbar mToolbar;
        private MenuButtonView  mMenuCashIn, mMenuAgentCashIn, mMenuTopup,
                                mMenuNotiPay, mMenuHistory, mMenuTopupPIN, mMenuOther, mMenuVAS, mMenuBillPay;
        private TextView mTextUserName;
        private View mIncludeMyWallet;
        private AppCompatButton mBtnFavorites;
        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mIncludeMyWallet = (View) view.findViewById(R.id.include_my_wallet);
            mMenuCashIn = (MenuButtonView) view.findViewById(R.id.mbv_cashin);
//            mMenuScan = (MenuButtonView) view.findViewById(R.id.mbv_scan);
            mMenuAgentCashIn = (MenuButtonView) view.findViewById(R.id.mbv_agentcashin);
            mMenuTopup = (MenuButtonView) view.findViewById(R.id.mbv_topup);
//            mMenuSetUp = (MenuButtonView) view.findViewById(R.id.mbv_setup);
//            mMenuSupport = (MenuButtonView) view.findViewById(R.id.mbv_support);
            mMenuNotiPay = (MenuButtonView) view.findViewById(R.id.mbv_notipay);
            mMenuHistory = (MenuButtonView) view.findViewById(R.id.mbv_history);
            mMenuTopupPIN = (MenuButtonView) view.findViewById(R.id.mbv_topup_pin);
            mTextUserName = (TextView) view.findViewById(R.id.text_username);
            mMenuOther = (MenuButtonView) view.findViewById(R.id.mbv_other);
            mMenuVAS = (MenuButtonView) view.findViewById(R.id.mbv_vas);
            mMenuBillPay = (MenuButtonView) view.findViewById(R.id.mbv_bill_pay);
            mBtnFavorites = (AppCompatButton) view.findViewById(R.id.btn_favorites);

            mBtnFavorites.setOnClickListener(ActivityDashboard.this);
        }
    }
}
