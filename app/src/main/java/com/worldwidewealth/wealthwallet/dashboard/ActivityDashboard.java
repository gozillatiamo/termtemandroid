package com.worldwidewealth.wealthwallet.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.wealthwallet.dialog.DialogHelp;
import com.worldwidewealth.wealthwallet.services.APIHelper;
import com.worldwidewealth.wealthwallet.services.APIServices;
import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.Global;
import com.worldwidewealth.wealthwallet.MyApplication;
import com.worldwidewealth.wealthwallet.R;
import com.worldwidewealth.wealthwallet.dashboard.adapter.AdapterDashboard;
import com.worldwidewealth.wealthwallet.dashboard.addcreditline.ActivityAddCreditLine;
import com.worldwidewealth.wealthwallet.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthwallet.dashboard.creditlimit.fragment.FragmentSlipCreditLimit;
import com.worldwidewealth.wealthwallet.dashboard.fragment.FragmentSlip;
import com.worldwidewealth.wealthwallet.dashboard.report.ActivityReport;
import com.worldwidewealth.wealthwallet.dashboard.reportmoneytransfer.ActivityReportMT;
import com.worldwidewealth.wealthwallet.dashboard.topup.ActivityTopup;
import com.worldwidewealth.wealthwallet.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.wealthwallet.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthwallet.dialog.DialogNetworkError;
import com.worldwidewealth.wealthwallet.model.ChangePasswordRequestModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.until.ErrorNetworkThrowable;
import com.worldwidewealth.wealthwallet.until.Until;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gozillatiamo on 10/3/16.
 */
public class ActivityDashboard extends AppCompatActivity{

    private ViewHolder mHolder;
    private AdapterDashboard mAdapter;
    private APIServices services;
    private long userInteractionTime = 0;
    private static final String TAG = ActivityDashboard.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        services = APIServices.retrofit.create(APIServices.class);
        mHolder = new ViewHolder(this);


        initToolbar();
        initClickMainMenu();

        if (Global.getAGENTID() != null) {
            Until.setLogoutSharedPreferences(MyApplication.getContext(), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Until.setBalanceWallet(mHolder.mIncludeMyWallet);
        Until.updateMyBalanceWallet(this, mHolder.mIncludeMyWallet);
    }



    private void initToolbar(){
        mHolder.mToolbar.setNavigationIcon(R.drawable.ic_power_settings_new);
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle("");
        mHolder.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Logout click: true");
                new DialogCounterAlert(ActivityDashboard.this, getString(R.string.title_logout),
                        getString(R.string.msg_logout),getString(R.string.title_logout),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Until.logoutAPI();
                        Until.backToSignIn(ActivityDashboard.this);
                    }
                });
            }
        });

    }

    private void initClickMainMenu(){
        mHolder.mMenuTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDashboard.this, ActivityTopup.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, 0);
            }
        });

        mHolder.mMenuReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDashboard.this, ActivityReport.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mHolder.mMenuReMT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDashboard.this, ActivityReportMT.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

/*
        mHolder.mBtnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
*/

/*
        mHolder.mMenuMyQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDashboard.this, ActivityMyQrCode.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
*/

        mHolder.mMenuAddCreditLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDashboard.this, ActivityAddCreditLine.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, 0);
            }
        });

        mHolder.mMenuHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelp dialog = new DialogHelp(ActivityDashboard.this);
                dialog.show();
            }
        });

        mHolder.mMenuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ActivityDashboard.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_setting);
                Button btnChangePassword = (Button)dialog.findViewById(R.id.btn_forgot_password);
                btnChangePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initDialogChangePassword();
                    }
                });
                dialog.show();

            }
        });

    }

    private void initDialogChangePassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDashboard.this);

        LayoutInflater inflater = ActivityDashboard.this.getLayoutInflater();
        View dialogView  = inflater.inflate(R.layout.dialog_change_password, null);

        final EditText editNewPass = (EditText) dialogView.findViewById(R.id.edit_new_password);
        final EditText editNewPassAgain = (EditText) dialogView.findViewById(R.id.edit_new_password_again);

        builder.setView(dialogView);
        builder.setTitle(R.string.change_password);
        builder.setPositiveButton(R.string.confirm, null);
        builder.setNegativeButton(R.string.cancel, null);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button confirm = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (editNewPass.getText().length() < 8 || editNewPassAgain.length() < 8 ){
                            Toast.makeText(ActivityDashboard.this, R.string.please_enter_data, Toast.LENGTH_LONG).show();
                        } else if (!editNewPass.getText().toString()
                                .equals(editNewPassAgain.getText().toString())){
                            Toast.makeText(ActivityDashboard.this, R.string.password_not_same, Toast.LENGTH_LONG).show();
                        } else {
                            Call<ResponseModel> call = services.CHANGEPASSWORD(new RequestModel(APIServices.ACTIONCHANGEPASSWORD,
                                    new ChangePasswordRequestModel(EncryptionData.EncryptData(editNewPass.getText().toString(), Global.getDEVICEID()+Global.getTXID()))
                            ));

                            APIHelper.enqueueWithRetry(call, new Callback<ResponseModel>() {
                                @Override
                                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                        if (response.body().getStatus() == APIServices.SUCCESS){
                                            alertDialog.dismiss();
                                            new DialogCounterAlert(ActivityDashboard.this,
                                                    response.body().getMsg(),
                                                    getString(R.string.change_password_success),
                                                    null);
                                        }/* else {
                                            new DialogNetworkError(ActivityDashboard.this);

                                        }*/
                                }

                                @Override
                                public void onFailure(Call<ResponseModel> call, Throwable t) {
                                    new ErrorNetworkThrowable(t).networkError(ActivityDashboard.this, call, this);
                                }
                            });
                        }

                    }
                });
            }
        });

        alertDialog.show();

    }


    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();

        if (stackCount != 0) {
            Fragment currentFragment = getSupportFragmentManager().getFragments().get(stackCount - 1);
            if (currentFragment instanceof FragmentSlipCreditLimit ||
                    currentFragment instanceof FragmentBillSlip ||
                    currentFragment instanceof FragmentTopupSlip ||
                    currentFragment instanceof FragmentSlip) return;
        }
        new DialogCounterAlert(ActivityDashboard.this, getString(R.string.title_leave_app),
                getString(R.string.msg_leave_app), getString(R.string.title_leave_app),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

    }

    public class ViewHolder{

        private Toolbar mToolbar;
        private CardView mMenuTopup, mMenuReport, mMenuMyQR, mMenuReMT, mMenuAddCreditLine
                , mMenuHelp, mMenuSetting;
//        private TextView mBtnForgotPassword;
        private View mIncludeMyWallet;
        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mMenuTopup = (CardView) view.findViewById(R.id.menu_topup);
            mMenuReport = (CardView) view.findViewById(R.id.menu_report);
//            mBtnForgotPassword = (TextView) view.findViewById(R.id.btn_forgot_password);
            mIncludeMyWallet = (View) view.findViewById(R.id.include_my_wallet);
            mMenuMyQR = (CardView) view.findViewById(R.id.menu_my_qr);
            mMenuReMT = (CardView) view.findViewById(R.id.menu_report_mtf);
            mMenuHelp = (CardView) view.findViewById(R.id.menu_help);
            mMenuSetting = (CardView) view.findViewById(R.id.menu_setting);
            mMenuAddCreditLine = (CardView) view.findViewById(R.id.menu_add_credit_line);
        }
    }
}
