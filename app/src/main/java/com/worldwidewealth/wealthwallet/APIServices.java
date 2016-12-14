package com.worldwidewealth.wealthwallet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.worldwidewealth.wealthwallet.model.PreRequestModel;
import com.worldwidewealth.wealthwallet.model.RegisterRequestModel;
import com.worldwidewealth.wealthwallet.model.RequestModel;
import com.worldwidewealth.wealthwallet.model.ResponseModel;
import com.worldwidewealth.wealthwallet.model.SignInRequestModel;
import com.worldwidewealth.wealthwallet.until.Until;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    public static final String AIS = "12CALL";
    public static final String TRUEMOVE = "TMVH";
    public static final String DTAC = "HAPPY";

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

    @Multipart
    @POST("/")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("data") RequestModel requestModel);

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
                    if (originalRequest.method().equalsIgnoreCase("POST")){
                        builder = originalRequest.newBuilder()
                                .method(originalRequest.method(), Until.encode(originalRequest.body()));

                    }

                    return  chain.proceed(builder.build());
                }
            }).build();

    public static final Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://180.128.21.31/wealthservice/").client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();

}

//http://180.128.21.31/apifcm/
//http://180.128.21.31/wealthservice/syncout.ashx
