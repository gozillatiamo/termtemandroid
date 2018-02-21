package com.worldwidewealth.termtem.services;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.google.gson.Gson;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.util.Util;

import java.net.ConnectException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 27-Dec-16.
 */

public abstract class RetryableCallback<T> implements Callback<T> {
    private int totalRetries = 0;
    private static final String TAG = RetryableCallback.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;
    private Trace mTrace = null;

    public RetryableCallback(Call<T> call, int totalRetries){
        this.call = call;
        this.totalRetries = totalRetries;

        String strRequest = Util.convertToStringRequest(call.request().body());
        if (strRequest != null) {
            RequestModel requestModel = new Gson().fromJson(strRequest, RequestModel.class);
            mTrace = MyApplication.mPerformance.startTrace(requestModel.getAction());
            mTrace.incrementCounter("REQUEST");
        }

    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!APIHelper.isCallSuccess(response)){
            if (retryCount++ < totalRetries){
                Log.v(TAG, "Retrying API Call - (" + retryCount + "/" + totalRetries +")");
                call.cancel();
                retry();
            } else
                onFinalFailure(call, null);
        } else {
/*
            if (mTrace != null){
                mTrace.stop();
            }
*/

            onFinalResponse(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, ""+t.getMessage());
        call.cancel();
        if (retryCount++ < totalRetries){
            Log.v(TAG, "Retrying API Call - (" + retryCount + "/" + totalRetries +")");
            retry();
        } else {
            if (mTrace != null){
                mTrace.incrementCounter(t.getMessage());
//                mTrace.stop();

            }
            onFinalFailure(call, t);
        }

    }

    public void onFinalResponse(Call<T> call, Response<T> response){
    }

    public void onFinalFailure(Call<T> call, Throwable t){
    }

    private void retry(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTrace != null){
                    mTrace.incrementCounter("RETRY");
                }

                call.clone().enqueue(RetryableCallback.this);
            }
        }, 500);
    }
}
