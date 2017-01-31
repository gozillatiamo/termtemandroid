package com.worldwidewealth.termtem.services;

import android.os.Handler;
import android.util.Log;

import java.net.ConnectException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 27-Dec-16.
 */

public abstract class RetryableCallback<T> implements Callback<T> {
    private int totalRetries = 3;
    private static final String TAG = RetryableCallback.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public RetryableCallback(Call<T> call, int totalRetries){
        this.call = call;
        this.totalRetries = totalRetries;
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
        } else
            onFinalResponse(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        call.cancel();
        if (retryCount++ < totalRetries){
            Log.v(TAG, "Retrying API Call - (" + retryCount + "/" + totalRetries +")");
            retry();
        } else
            onFinalFailure(call, t);

    }

    public void onFinalResponse(Call<T> call, Response<T> response){

    }

    public void onFinalFailure(Call<T> call, Throwable t){

    }

    private void retry(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                call.clone().enqueue(RetryableCallback.this);
            }
        }, 3000);
    }
}
