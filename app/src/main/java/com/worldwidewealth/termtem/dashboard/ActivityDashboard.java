package com.worldwidewealth.termtem.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.dashboard.inbox.InboxActivity;
import com.worldwidewealth.termtem.dashboard.mPayStation.SelectChoiceMpayActivity;
import com.worldwidewealth.termtem.dashboard.maps.MapsActivity;
import com.worldwidewealth.termtem.dashboard.myqrcode.ActivityMyQrCode;
import com.worldwidewealth.termtem.dashboard.scan.ActivityScan;
import com.worldwidewealth.termtem.dialog.DialogHelp;
import com.worldwidewealth.termtem.model.UserMenuModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dashboard.reportmoneytransfer.ActivityReportMT;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.ChangePasswordRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.util.BadgeDrawable;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.MenuButtonView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityDashboard extends MyAppcompatActivity{

    private ViewHolder mHolder;
    private static final String TAG = ActivityDashboard.class.getSimpleName();
    private List<UserMenuModel> mUserMenuList;
    private LayerDrawable mIconNoti;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mHolder = new ViewHolder(this);
        mUserMenuList = Global.getInstance().getUserMenuList();

        initToolbar();
        initBtnMenu();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.updateMyBalanceWallet(this, mHolder.mIncludeMyWallet, mIconNoti);
        MenuButtonView.setsClickable(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHolder.mMenuSupport.dismiss();
        mHolder.mMenuSetUp.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (Global.getInstance().getTXID() != null) {
            Util.logoutAPI(true);
        }
    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (stackCount != 0) {
            Fragment currentFragment = getSupportFragmentManager().getFragments().get(stackCount - 1);
            if (currentFragment instanceof FragmentTopupSlip) return;
        }
        new DialogCounterAlert(ActivityDashboard.this, getString(R.string.title_leave_app),
                getString(R.string.msg_leave_app), getString(R.string.title_leave_app),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
/*
                        MyApplication.LeavingOrEntering.currentActivity = null;
                        Util.logoutAPI(true);
*/
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
        }
        return super.onOptionsItemSelected(item);
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
                        mHolder.mMenuScan.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case SETUP:
                        mHolder.mMenuSetUp.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case SUPPORT:
                        mHolder.mMenuSupport.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case TOPUP:
                        mHolder.mMenuTopup.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;
                    case AGENTCASHIN:
                        mHolder.mMenuAgentCashIn.setMenuVisibility(
                                MenuButtonView.VISIBILITY.valueOf(model.getSTATUS()).getVisibility());
                        break;

                }
            }
        }
    }
    public class ViewHolder{

        private Toolbar mToolbar;
        private MenuButtonView  mMenuCashIn, mMenuAgentCashIn, mMenuScan, mMenuTopup, mMenuSetUp,
                                mMenuSupport, mMenuNotiPay, mMenuHistory;
        private View mIncludeMyWallet;
        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mIncludeMyWallet = (View) view.findViewById(R.id.include_my_wallet);
            mMenuCashIn = (MenuButtonView) view.findViewById(R.id.mbv_cashin);
            mMenuScan = (MenuButtonView) view.findViewById(R.id.mbv_scan);
            mMenuAgentCashIn = (MenuButtonView) view.findViewById(R.id.mbv_agentcashin);
            mMenuTopup = (MenuButtonView) view.findViewById(R.id.mbv_topup);
            mMenuSetUp = (MenuButtonView) view.findViewById(R.id.mbv_setup);
            mMenuSupport = (MenuButtonView) view.findViewById(R.id.mbv_support);
            mMenuNotiPay = (MenuButtonView) view.findViewById(R.id.mbv_notipay);
            mMenuHistory = (MenuButtonView) view.findViewById(R.id.mbv_history);
        }
    }
}
