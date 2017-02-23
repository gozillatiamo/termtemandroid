package com.worldwidewealth.termtem.dashboard.addCreditAgent;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.adapter.AgentAdapter;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.fragment.FragmentAddCreditChoice;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.AgentResponse;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityAddCreditAgent extends MyAppcompatActivity {

    private ViewHolder mHolder;
    private APIServices services;
    private AgentAdapter mAdapter;
    private int mType;

    public static final int DEFAULT = 0;
    public static final int SCAN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_credit_to_agent);
        mHolder = new ViewHolder(this);
        mType = getIntent().getExtras().getInt("type");
        services = APIServices.retrofit.create(APIServices.class);
        initToolbar();

        switch (mType){
            case SCAN:
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_add_credit,
                                FragmentAddCreditChoice.newInstance(getIntent()
                                        .getBundleExtra("data")));
                transaction.commit();
                break;
            default:
                initData();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        try {
            if (!getSupportFragmentManager()
                    .findFragmentById(R.id.container_add_credit)
                    .getChildFragmentManager()
                    .popBackStackImmediate()) {

                super.onBackPressed();
            }
        } catch (NullPointerException e){
            super.onBackPressed();
        } catch (IllegalStateException e){}
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

    private void initData(){
        new DialogCounterAlert.DialogProgress(this);
        Call<ResponseBody> call = services.topupService(
                new RequestModel(APIServices.ACTIONGETAGENTLIST, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(ActivityAddCreditAgent.this,
                        call, response.body(), this);
                if (objectResponse instanceof String){
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    final List<AgentResponse> listagent = gson
                            .fromJson((String)objectResponse,
                                    new TypeToken<ArrayList<AgentResponse>>(){}.getType());
                    if (listagent.size() != -0){
                        initRecyclerView(listagent);
                    }
                }

                DialogCounterAlert.DialogProgress.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(ActivityAddCreditAgent.this, call, this);
            }
        });
    }

    private void initToolbar(){
        setSupportActionBar(mHolder.mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initRecyclerView(List<AgentResponse> list){
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mHolder.mRecyclerAgent.setLayoutManager(manager);
        mAdapter = new AgentAdapter(this, list);
        mHolder.mRecyclerAgent.setAdapter(mAdapter);
//        mHolder.mRecyclerAgent.setAdapter(new AgentAdapter());
    }


    private class ViewHolder{
        private Toolbar mToolbar;
        private RecyclerView mRecyclerAgent;
        public ViewHolder(Activity itemView) {
            mToolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
            mRecyclerAgent = (RecyclerView) itemView.findViewById(R.id.recycler_agent);
        }
    }


}
