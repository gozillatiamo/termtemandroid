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
import com.worldwidewealth.termtem.util.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.CipherSuite;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.TlsVersion;
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
    public static final int SUCCESS = 0;

    String MSG_SUCCESS = "Success";
    String MSG_FAIL = "102";
    String MSG_WAIT = "100";

    public static final String ACTIONLOGIN = "LOGIN";
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
    public static final String ACTIONLEAVE = "LEAVE";
    public static final String ACTIONLOADINBOX = "LOADINBOX";
    public static final String ACTIONREADMSG = "READMSG";
    public static final String ACTIONREMOVEMSG = "REMOVEMSG";
    public static final String ACTIONGETAGENTLIST = "GETAGENTLIST";
    public static final String ACTION_PREVIEW_AGENT_CASHIN = "PREVIEWAGENTCASHIN";
    public static final String ACTION_GETOTP_AGENT_CASHIN = "GETOTPAGENTCASHIN";
    public static final String ACTION_SUBMIT_AGENT_CASHIN = "SUBMITAGENTCASHIN";
    public static final String ACTION_ESLIP_AGENT_CASHIN = "ESLIPAGENTCASHIN";
    public static final String ACTION_LOAD_BUTTON_EPIN = "LOADBUTTONEPIN";
    public static final String ACTION_PREVIEW_EPIN = "PREVIEWEPIN";
    public static final String ACTION_GET_OTP_EPIN = "GETOTPEPIN";
    public static final String ACTION_SUBMIT_TOPUP_EPIN = "SUBMITTOPUPEPIN";
    public static final String ACTION_ESLIP_EPIN = "ESLIPEPIN";
    public static final String ACTION_SERVICE_PRO = "SERVICEPRO";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_LINE_CHART = "SALERPTLINECHART";
    public static final String ACTION_PIE_CHART = "SALERPTPIECHART";
    public static final String ACTION_GET_LISTPG = "GETLISTPG";
    public static final String ACTION_PREVIEW_VAS = "PREVIEWVAS";
    public static final String ACTION_GETOTP_VAS = "GETOTPVAS";
    public static final String ACTION_SUBMIT_VAS = "SUBMITVAS";
    public static final String ACTION_ESLIP_VAS = "ESLIPVAS";
    public static final String ACTION_ADD_FAV = "ADDFAV";
    public static final String ACTION_REMOVE_FAV = "REMOVEFAV";
    public static final String ACTION_LOAD_FAV = "LOADFAV";







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

    @POST("topupservice.ashx")
    Call<ResponseBody> topupService(@Body RequestModel requestModel);

    @POST("fundin.ashx")
    Call<ResponseBody> genBarcode(@Body RequestModel requestModel);


    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(
                    TlsVersion.TLS_1_0,
                    TlsVersion.TLS_1_1,
                    TlsVersion.TLS_1_2)
            .supportsTlsExtensions(true)
            .cipherSuites(
                    CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
                    CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256,
                    CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384)
            .build();

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .connectionSpecs(Collections.singletonList(spec))
            .addInterceptor(interceptor)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request originalRequest = chain.request();

                    Request.Builder builder = originalRequest.newBuilder();
                    if (originalRequest.method().equalsIgnoreCase("POST")){


                        builder = originalRequest.newBuilder()
//                                .addHeader("connection", "close")
                                .method(originalRequest.method(), Util.encode(originalRequest.body()));
                    }

//                    Response response = client.newCall(builder.build()).execute();
                     return  chain.proceed(builder.build());
                }
            })
            .build();

    Gson gson = new GsonBuilder()
            .setLenient()
    .create();

     Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(MyApplication.getContext().getString(R.string.server)).client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();

}

//http://180.128.21.31/apifcm/
//http://180.128.21.31/wealthservice/syncout.ashx
