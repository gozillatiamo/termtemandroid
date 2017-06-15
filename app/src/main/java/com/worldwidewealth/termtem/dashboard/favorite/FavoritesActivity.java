package com.worldwidewealth.termtem.dashboard.favorite;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.LoadFavResponseModel;
import com.worldwidewealth.termtem.model.PGResponseModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.TermTemLoading;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {

    private APIServices services = APIServices.retrofit.create(APIServices.class);
    private TermTemLoading mLoading;
    private List<LoadFavResponseModel> mListFavModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setup();
    }

    private void setup(){
        bindView();
        setupToolbar();
        setupRecyclear();
    }

    private void bindView(){
        mLoading = new TermTemLoading(this, (ViewGroup) findViewById(R.id.container));

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclear(){
        mLoading.show();
        Call<ResponseBody> call = services.service(new RequestModel(APIServices.ACTION_LOAD_FAV, new DataRequestModel()));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object responseValues = EncryptionData.getModel(FavoritesActivity.this, call, response.body(), this);

                if (responseValues instanceof String){
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    Type listType = new TypeToken<List<LoadFavResponseModel>>() {}.getType();
                    mListFavModels = gson.fromJson((String) responseValues, listType);

                }
                mLoading.hide();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(FavoritesActivity.this, call, this);
                mLoading.hide();
            }
        });

    }

}
