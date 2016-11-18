package com.worldwidewealth.wealthcounter.dashboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.EncryptionData;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.adapter.AdapterDashboard;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment.FragmentSlipCreditLimit;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentSlip;
import com.worldwidewealth.wealthcounter.dashboard.topup.ActivityTopup;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.wealthcounter.dialog.DialogCounterAlert;
import com.worldwidewealth.wealthcounter.dialog.DialogNetworkError;
import com.worldwidewealth.wealthcounter.model.ChangePasswordRequestModel;
import com.worldwidewealth.wealthcounter.model.LogoutRequestModel;
import com.worldwidewealth.wealthcounter.model.RequestModel;
import com.worldwidewealth.wealthcounter.model.ResponseModel;
import com.worldwidewealth.wealthcounter.until.Until;

import okhttp3.ResponseBody;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        services = APIServices.retrofit.create(APIServices.class);
        mHolder = new ViewHolder(this);


        initToolbar();
        initClickMainMenu();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Call<ResponseBody> call = services.LOGOUT(new LogoutRequestModel());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Global.setAGENTID("");
                Global.setUSERID("");
                Global.setBALANCE(0.00);
                Global.setTXID("");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void initData(){
        Until.setBalanceWallet(mHolder.mTextBalanceInteger, mHolder.mTextBalanceDecimal);
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle("");
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

        mHolder.mBtnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialogChangePassword();
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
                                    new ChangePasswordRequestModel(EncryptionData.EncryptData(editNewPass.getText().toString(), null))
                            ));

                            call.enqueue(new Callback<ResponseModel>() {
                                @Override
                                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                                        if (response.body().getStatus() == APIServices.SUCCESS){
                                            alertDialog.dismiss();
                                            new DialogCounterAlert(ActivityDashboard.this,
                                                    response.body().getMsg(),
                                                    R.string.change_password_success);
                                        } else {
                                            new DialogNetworkError(ActivityDashboard.this);

                                        }
                                }

                                @Override
                                public void onFailure(Call<ResponseModel> call, Throwable t) {
                                    t.printStackTrace();
                                    new DialogNetworkError(ActivityDashboard.this);
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

        super.onBackPressed();
    }

    public class ViewHolder{

        private Toolbar mToolbar;
        private CardView mMenuTopup;
        private Button mBtnForgotPassword;
        private View mIncludeMyWallet;
        private TextView mTextBalanceInteger, mTextBalanceDecimal;
        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mMenuTopup = (CardView) view.findViewById(R.id.menu_topup);
            mBtnForgotPassword = (Button) view.findViewById(R.id.btn_forgot_password);
            mIncludeMyWallet = (View) view.findViewById(R.id.include_my_wallet);
            mTextBalanceDecimal = (TextView) mIncludeMyWallet.findViewById(R.id.txt_balance_decimal);
            mTextBalanceInteger = (TextView) mIncludeMyWallet.findViewById(R.id.txt_balance_integer);
        }
    }
}
