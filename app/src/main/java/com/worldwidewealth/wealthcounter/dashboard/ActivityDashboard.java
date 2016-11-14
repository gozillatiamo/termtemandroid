package com.worldwidewealth.wealthcounter.dashboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.worldwidewealth.wealthcounter.APIServices;
import com.worldwidewealth.wealthcounter.Global;
import com.worldwidewealth.wealthcounter.R;
import com.worldwidewealth.wealthcounter.dashboard.adapter.AdapterDashboard;
import com.worldwidewealth.wealthcounter.dashboard.billpayment.fragment.FragmentBillSlip;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentHome;
import com.worldwidewealth.wealthcounter.dashboard.creditlimit.fragment.FragmentSlipCreditLimit;
import com.worldwidewealth.wealthcounter.dashboard.fragment.FragmentSlip;
import com.worldwidewealth.wealthcounter.dashboard.topup.ActivityTopup;
import com.worldwidewealth.wealthcounter.dashboard.topup.fragment.FragmentTopupSlip;
import com.worldwidewealth.wealthcounter.model.LogoutModel;
import com.worldwidewealth.wealthcounter.until.SimpleDividerItemDecoration;
import com.worldwidewealth.wealthcounter.until.SpacesGridDecoration;
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
        initMainMenu();
//        initListDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Call<ResponseBody> call = services.LOGOUT(new LogoutModel());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Global.setAGENTID("");
                Global.setUSERID("");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("onNewIntent", "true");

    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setTitle("");
    }

    private void initMainMenu(){
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
                final AlertDialog alertDialog = new AlertDialog.Builder(ActivityDashboard.this)
                        .setView(R.layout.dialog_forgot_password)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();

            }
        });
    }

//    private void initListDashboard(){
//        mAdapter = new AdapterDashboard(this);
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 12);
//        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return mAdapter.getItemViewType(position);
//            }
//        });
//
//        mHolder.mListDashboard.setAdapter(mAdapter);
//        mHolder.mListDashboard.setLayoutManager(layoutManager);
//        mHolder.mListDashboard.addItemDecoration(new SpacesGridDecoration(6));
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }

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
//        private RecyclerView mListDashboard;

        public ViewHolder(Activity view){
            mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
            mMenuTopup = (CardView) view.findViewById(R.id.menu_topup);
            mBtnForgotPassword = (Button) view.findViewById(R.id.btn_forgot_password);
//            mListDashboard = (RecyclerView) view.findViewById(R.id.list_dashborad);
        }
    }
}
