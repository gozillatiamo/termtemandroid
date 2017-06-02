package com.worldwidewealth.termtem.services;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 26-Dec-16.
 */

public class APIHelper {

    public static int DEFAULT_RETIES;
    private static RequestModel requestModel = null;

    public static final String TAG = APIHelper.class.getSimpleName();

    public static <T> void enqueueWithRetry(Call<T> call, final int retryCount, final Callback<T> callback){
        call.enqueue(new RetryableCallback<T>(call, retryCount) {
            @Override
            public void onFinalResponse(Call<T> call, Response<T> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFinalFailure(Call<T> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public static <T> void enqueueWithRetry(Call<T> call, final Callback<T> callback){
        DEFAULT_RETIES = 3;
        String strRequest = Util.convertToStringRequest(call.request().body());
        if (strRequest != null){
            requestModel = new Gson().fromJson(strRequest, RequestModel.class);
            switch (requestModel.getAction()){
                case APIServices.ACTIONSUBMITTOPUP:
                case APIServices.ACTION_SUBMIT_TOPUP_EPIN:
                case APIServices.ACTION_SUBMIT_AGENT_CASHIN:
                case APIServices.ACTIONNOTIPAY:
                case APIServices.ACTION_REGISTER:
                    DEFAULT_RETIES = 0;
                    break;
            }
        }

        Log.e(TAG, requestModel.getAction());

        enqueueWithRetry(call, DEFAULT_RETIES, callback);
    }

    public static boolean isCallSuccess(Response response){
        int code = response.code();
        return (code >= 200 && code < 400);
    }
}
