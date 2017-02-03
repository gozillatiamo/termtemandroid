package com.worldwidewealth.termtem.services;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.PreRequestModel;
import com.worldwidewealth.termtem.model.RegisterRequestModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.ResponseModel;
import com.worldwidewealth.termtem.model.SignInRequestModel;
import com.worldwidewealth.termtem.until.Until;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
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
    public static final int SUCCESS = 0;

    public static final String ACTIONCHANGEPASSWORD = "CHANGEPASSWORD";
    public static final String ACTIONLOADBUTTON = "LOADBUTTON";
    public static final String ACTIONPREVIEW = "PREVIEW";
    public static final String ACTIONREGISTERDEVICE = "REGISTERDEVICE";
    public static final String ACTIONGETOTP = "GETOTP";
    public static final String ACTIONSUBMITTOPUP = "SUBMITTOPUP";
    public static final String ACTIONLOGOUT = "LOGOUT";
    public static final String ACTIONESLIP = "ESLIP";
    public static final String ACTIONSAVESLIP = "SAVESLIPE";
    public static final String ACTIONSALERPT = "SALERPT";
    public static final String ACTIONGETBALANCE = "GETBALANCE";
    public static final String ACTIONNOTIPAY = "NOTIPAY";
    public static final String ACTIONGENBARCODE = "GENBARCODE";
    public static final String ACTIONACCPWIFI = "ACCPWIFI";

    public static final String AIS = "12CALL";
    public static final String TRUEMOVE = "TMVH";
    public static final String DTAC = "HAPPY";
    public static final String TAG = APIServices.class.getSimpleName();


    @GET("apifcm/online.aspx")
    Call<ResponseBody> online();

    @GET("apifcm/pin.aspx")
    Call<ResponseBody> pin();



    @GET("apifcm/getluck.aspx")
    Call<ResponseBody> getluck();

    @POST("service.ashx")
    Call<ResponseBody> PRE(@Body PreRequestModel inAppModel);

    @POST("service.ashx")
    Call<ResponseBody> LOGIN(@Body SignInRequestModel signInRequestModel);

    @POST("service.ashx")
    Call<ResponseBody> logout(@Body RequestModel requestModel);

    @POST("service.ashx")
    Call<ResponseModel> SIGNUP(@Body RegisterRequestModel registerRequestModel);

    @POST("service.ashx")
    Call<ResponseModel> CHANGEPASSWORD(@Body RequestModel requestModel);

    @POST("topupservice.ashx")
    Call<ResponseBody> loadButton(@Body RequestModel requestModel);

    @POST("topupservice.ashx")
    Call<ResponseBody> preview(@Body RequestModel requestModel);

    @POST("service.ashx")
    Call<ResponseModel> registerDevice(@Body RequestModel requestModel);

    @POST("topupservice.ashx")
    Call<ResponseBody> getOTP(@Body RequestModel requestModel);

    @POST("topupservice.ashx")
    Call<ResponseBody> submitTopup(@Body RequestModel requestModel);

    @POST("topupservice.ashx")
    Call<ResponseBody> eslip(@Body RequestModel requestModel);

    @POST("topupservice.ashx")
    Call<ResponseBody> saveSlip(@Body RequestModel requestModel);

    @POST("topupservice.ashx")
    Call<ResponseBody> salerpt(@Body RequestModel requestModel);

    @POST("service.ashx")
    Call<ResponseBody> getbalance(@Body RequestModel requestModel);

    @POST("service.ashx")
    Call<ResponseBody> notipay(@Body RequestModel requestModel);

    @POST("service.ashx")
    Call<ResponseBody> service(@Body RequestModel requestModel);


    @POST("fundin.ashx")
    Call<ResponseBody> genBarcode(@Body RequestModel requestModel);


    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();

                    Request.Builder builder = originalRequest.newBuilder();
                    if (originalRequest.method().equalsIgnoreCase("POST")){

                        builder = originalRequest.newBuilder()
                                .method(originalRequest.method(), Until.encode(originalRequest.body()));
                    }
                    Response response = chain.proceed(builder.build());
                    Log.e(TAG, "isSuccessful: "+response.isSuccessful());
                    Log.e(TAG, "message: "+response.message());

                    return  response;
                }
            })
            .build();

    public static final Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(MyApplication.getContext().getString(R.string.server_dev)).client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();


}

//http://180.128.21.31/apifcm/
//http://180.128.21.31/wealthservice/syncout.ashx
