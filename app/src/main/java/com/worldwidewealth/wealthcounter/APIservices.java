package com.worldwidewealth.wealthcounter;

import android.util.Log;

import com.worldwidewealth.wealthcounter.model.InAppModel;
import com.worldwidewealth.wealthcounter.model.PreResponseModel;
import com.worldwidewealth.wealthcounter.model.SignInModel;
import com.worldwidewealth.wealthcounter.model.TestModel;
import com.worldwidewealth.wealthcounter.until.Until;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by MyNet on 11/10/2559.
 */

public interface APIServices {

    @GET("apifcm/online.aspx")
    Call<ResponseBody> online();


    @GET("apifcm/pin.aspx")
    Call<ResponseBody> pin();



    @GET("apifcm/getluck.aspx")
    Call<ResponseBody> getluck();

    @POST("wealthservice/syncout.ashx")
    Call<ResponseBody> testpost(@Body TestModel test);

    @POST("service.ashx")
    Call<PreResponseModel> PRE(@Body InAppModel inAppModel);

    @POST("service.ashx")
    Call<ResponseBody> LOGIN(@Body SignInModel signInModel);


    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder();
                    Log.e("method", originalRequest.method());
                    if (originalRequest.method().equalsIgnoreCase("POST")){
                        Log.e("isPOST", "true");
                        builder = originalRequest.newBuilder()
                                .method(originalRequest.method(), Until.encode(originalRequest.body()));

                    }
                    return chain.proceed(builder.build());
                }
            }).build();

    public static final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://180.128.21.31/wealthservice/").client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

}

//http://180.128.21.31/apifcm/
//http://180.128.21.31/wealthservice/syncout.ashx
