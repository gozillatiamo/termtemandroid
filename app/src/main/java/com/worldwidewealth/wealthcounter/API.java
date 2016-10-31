package com.worldwidewealth.wealthcounter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by MyNet on 11/10/2559.
 */

public interface API{

    @GET("online.aspx")
    Call<ResponseBody> online();


    @GET("pin.aspx")
    Call<ResponseBody> pin();



    @GET("getluck.aspx")
    Call<ResponseBody> getluck();

    @FormUrlEncoded
    @POST("SyncOut.ashx")
    Call<ResponseBody> testpost(@Field("id")String id, @Field("name")String name);

public static final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://180.128.21.31/apifcm/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
}
//http://180.128.21.31/apifcm/
//http://192.168.200.70:52029/SyncOut.ashx
