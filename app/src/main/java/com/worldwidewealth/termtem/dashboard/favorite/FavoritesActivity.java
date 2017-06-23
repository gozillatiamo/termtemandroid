package com.worldwidewealth.termtem.dashboard.favorite;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.Global;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.ActivityAddCreditAgent;
import com.worldwidewealth.termtem.dashboard.addCreditAgent.adapter.AgentAdapter;
import com.worldwidewealth.termtem.dashboard.favorite.adapter.FavoritesAdapter;
import com.worldwidewealth.termtem.dashboard.report.ActivityReport;
import com.worldwidewealth.termtem.dashboard.topup.ActivityTopup;
import com.worldwidewealth.termtem.dashboard.topup.fragment.FragmentTopup;
import com.worldwidewealth.termtem.dialog.MyShowListener;
import com.worldwidewealth.termtem.model.ActionItemRequest;
import com.worldwidewealth.termtem.model.AgentResponse;
import com.worldwidewealth.termtem.model.DataRequestModel;
import com.worldwidewealth.termtem.model.LoadFavResponseModel;
import com.worldwidewealth.termtem.model.PGResponseModel;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.RecyclerItemClickListener;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.MenuButtonView;
import com.worldwidewealth.termtem.widgets.TermTemLoading;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {

    private APIServices services = APIServices.retrofit.create(APIServices.class);
    private TermTemLoading mLoading;
    private List<LoadFavResponseModel> mListFavModels;
    private RecyclerView mRecyclerFav;
    private FavoritesAdapter mAdapter;

    public static final String TAG = FavoritesActivity.class.getSimpleName();


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
        mRecyclerFav = (RecyclerView) findViewById(R.id.recycler_favorites);

    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_star);
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
                    mRecyclerFav.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this, LinearLayoutManager.VERTICAL, false));
                    mAdapter = new FavoritesAdapter(FavoritesActivity.this, mListFavModels);
                    mRecyclerFav.setAdapter(new ScaleInAnimationAdapter(mAdapter));
                    mRecyclerFav.addOnItemTouchListener(new RecyclerItemClickListener(FavoritesActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
//                            showDialogConfirm(position);
                            if (Global.getInstance().getLastTranId() != null) {
                                MenuButtonView.showDialogHasProcess(FavoritesActivity.this);
                                return;
                            }

                            startTopup(position);
                        }
                    }));

                    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                        @Override
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            removeFavorite(viewHolder.getAdapterPosition());
                        }
                    };

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                    itemTouchHelper.attachToRecyclerView(mRecyclerFav);
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

    private void removeFavorite(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogError)
                .setTitle(R.string.confirm_delete_title_fav)
                .setMessage(R.string.confirm_delete_msg_fav)
                .setCancelable(false)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<ResponseBody> call = services.service(new RequestModel(APIServices.ACTION_REMOVE_FAV,
                                ActionItemRequest.FAV(mAdapter.getItem(position).getFavid())));

                        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });

                        mAdapter.removeItem(position);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.notifyDataSetChanged();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new MyShowListener());

        alertDialog.show();
    }

    private void startTopup(int position){
        LoadFavResponseModel model = mListFavModels.get(position);

        Intent intent = null;

        switch (model.getService()){
            case ActivityReport.TOPUP_REPORT:
                LoadFavResponseModel.TopupListModel topupListModel = model.getTopuplist().get(0);

                intent = new Intent(FavoritesActivity.this, ActivityTopup.class);
                intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.MOBILE);
                intent.putExtra(ActivityTopup.KEY_PHONENO, topupListModel.getPhoneNo());
                intent.putExtra(ActivityTopup.KEY_CARRIER, topupListModel.getCarrierCode());
                intent.putExtra(ActivityTopup.KEY_AMT, topupListModel.getAmt());

                break;
            case ActivityReport.EPIN_REPORT:
                LoadFavResponseModel.EpinListModel epinListModel = model.getEpinlist().get(0);

                intent = new Intent(FavoritesActivity.this, ActivityTopup.class);
                intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.PIN);
                intent.putExtra(ActivityTopup.KEY_PHONENO, epinListModel.getPhoneNo());
                intent.putExtra(ActivityTopup.KEY_CARRIER, epinListModel.getCarrierCode());
                intent.putExtra(ActivityTopup.KEY_AMT, epinListModel.getAmt());

                break;
            case ActivityReport.VAS_REPORT:
                LoadFavResponseModel.VasListModel vasListModel = model.getVaslist().get(0);

                intent = new Intent(FavoritesActivity.this, ActivityTopup.class);
                intent.putExtra(FragmentTopup.keyTopup, FragmentTopup.VAS);
                intent.putExtra(ActivityTopup.KEY_PHONENO, vasListModel.getPhoneNo());
                intent.putExtra(ActivityTopup.KEY_CARRIER, vasListModel.getCarrierCode());
                intent.putExtra(ActivityTopup.KEY_AMT, vasListModel.getAmt());

                break;
            case ActivityReport.CASHIN_REPORT:
                LoadFavResponseModel.CashInListModel cashInListModel = model.getCashinlist().get(0);

                Bundle bundle = new Bundle();

                AgentResponse agentResponse = new AgentResponse(cashInListModel.getAgentId(),
                        cashInListModel.getAgentCode(),
                        cashInListModel.getAgentFirstName(),
                        cashInListModel.getAgentLastName(),
                        cashInListModel.getAgentPhone(), null);

                bundle.putParcelable(AgentAdapter.AGENTDATA, agentResponse);
                bundle.writeToParcel(Parcel.obtain(), 0);
                bundle.putDouble(ActivityTopup.KEY_AMT, cashInListModel.getAmt());

                intent = new Intent(this, ActivityAddCreditAgent.class);
                intent.putExtra("type", ActivityAddCreditAgent.SCAN);
                intent.putExtra("data", bundle);

                break;
            case ActivityReport.BILL_REPORT:
                break;
        }

        if (intent != null){
            overridePendingTransition(R.anim.slide_in_right, 0);

            startActivity(intent);
            finish();
        }

    }

/*
    private void showDialogConfirm(int position){
        LoadFavResponseModel model = mListFavModels.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this, R.style.MyAlertDialogWarning);

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);

        switch (model.getService()){
            case ActivityReport.TOPUP_REPORT:
                LoadFavResponseModel.TopupListModel topupListModel = model.getTopuplist().get(0);
                builder.setTitle(model.getName()+"\n"+format.format(topupListModel.getAmt())+" "+getString(R.string.currency));
                break;
            case ActivityReport.EPIN_REPORT:
                break;
            case ActivityReport.VAS_REPORT:
                break;
            case ActivityReport.CASHIN_REPORT:
                break;
            case ActivityReport.BILL_REPORT:
                break;
        }

    }
*/

}
