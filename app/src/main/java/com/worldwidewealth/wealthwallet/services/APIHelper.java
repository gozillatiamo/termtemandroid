package com.worldwidewealth.wealthwallet.services;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.worldwidewealth.wealthwallet.EncryptionData;
import com.worldwidewealth.wealthwallet.model.ResponseModel;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 26-Dec-16.
 */

public class APIHelper {

    public static final int DEFAULT_RETIES = 3;
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
        enqueueWithRetry(call, DEFAULT_RETIES, callback);
    }

    public static boolean isCallSuccess(Response response){

        int code = response.code();
        return (code >= 200 && code < 400);
    }
}
