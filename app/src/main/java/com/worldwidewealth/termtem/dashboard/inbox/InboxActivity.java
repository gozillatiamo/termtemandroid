package com.worldwidewealth.termtem.dashboard.inbox;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.ActivityShowNotify;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MainActivity;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.adapter.InboxAdapter;
import com.worldwidewealth.termtem.dashboard.widgets.OnLoadMoreListener;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.DialogNetworkError;
import com.worldwidewealth.termtem.dialog.TermTemDialog;
import com.worldwidewealth.termtem.model.InboxRepuest;
import com.worldwidewealth.termtem.model.InboxResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.model.SalerptResponseModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.until.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.until.Until;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InboxActivity extends MyAppcompatActivity {

    private RecyclerView mInboxRecycler;
    private Toolbar mToolbar;
    private TermTemDialog.SearchDateRangeDialog mSearchDateRange;
    private APIServices services;
    private List<InboxResponse> mListInbox;
    private long mDateFrom = Until.getTimestamp(System.currentTimeMillis(), 0),
            mDateTo = Until.getTimestamp(System.currentTimeMillis(), 23);
    private String mText = "";
    private InboxAdapter mAdapter;
    private int mPage = 1;
    private SearchView searchView = null;
    public static final String TAG = InboxActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        services = APIServices.retrofit.create(APIServices.class);
        Until.setupUI(findViewById(R.id.layout_parent));
        initWidgets();
        initToolbar();
        loadDataInbox();

        mSearchDateRange = new TermTemDialog.SearchDateRangeDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mSearchDateRange.canSearch()) return;

                ContentValues values = mSearchDateRange.getDateFromTo();
                mDateFrom = values.getAsLong(TermTemDialog.SearchDateRangeDialog.DATEFROM);
                mDateTo = values.getAsLong(TermTemDialog.SearchDateRangeDialog.DATETO);
                mPage = 1;
                loadDataInbox();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.inbox_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) InboxActivity.this.getSystemService(Context.SEARCH_SERVICE);


        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(InboxActivity.this.getComponentName()));
            View searchEdit = searchView.findViewById(android.support.v7.appcompat.R.id.search_edit_frame);
            searchEdit.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.editbox_background_normal));

            EditText txt = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            txt.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
            txt.setTextColor(getResources().getColor(android.R.color.black));
            ImageView close = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            close.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.e(TAG, "OnSearch");
                    mText = query;
                    mPage = 1;
                    loadDataInbox();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mText = newText;
                    if (newText.equals("")){
                        mPage = 1;
                        loadDataInbox();
                    }
                    return false;
                }
            });

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_date_range:
                mSearchDateRange.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDataInbox(){
        if (mPage == 1) {
            new DialogCounterAlert.DialogProgress(this);
            if (mAdapter != null) mAdapter.clearAll();
        }
        Call<ResponseBody> call = services.service(
                new RequestModel(APIServices.ACTIONLOADINBOX,
                        new InboxRepuest(mPage, mDateFrom, mDateTo, mText)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(
                        InboxActivity.this, call, response.body(), this);

                if (objectResponse instanceof String){
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Until.JsonDateDeserializer()).create();
                    final List<InboxResponse> listinbox = gson
                            .fromJson((String)objectResponse,
                                    new TypeToken<ArrayList<InboxResponse>>(){}.getType());

                    if (listinbox.size() == 0 && mAdapter != null ){
                        mAdapter.setLoaded();
                        mAdapter.setMaxInbox(true);
                        return;
                    }

                    if (mPage == 1) {
                        mListInbox = listinbox;
                        initListInbox();
                    } else {
                        mListInbox.add(null);
                        mAdapter.notifyItemInserted(mListInbox.size() - 1);
                        //Load more data for reyclerview
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                Log.e("haint", "Load More "+mPage);
                                //Remove loading item
                                mListInbox.remove(mListInbox.size() - 1);
                                mAdapter.notifyItemRemoved(mListInbox.size());
                                //Load data
/*
                                int index = mListInbox.size();
                                int end = index + 20;
*/
                                for (InboxResponse model:listinbox){
                                    mListInbox.add(model);
                                }
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setLoaded();
                            }
                        }, 5000);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(InboxActivity.this, call, this);
            }
        });
    }

    private void initWidgets(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mInboxRecycler = (RecyclerView) findViewById(R.id.inbox_recyclear);
    }

    private void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.in_box));
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListInbox(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mInboxRecycler.setLayoutManager(layoutManager);
        mAdapter = new InboxAdapter(this, mInboxRecycler, mListInbox);
        mInboxRecycler.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override public void onLoadMore() {
                if (!mAdapter.isMaxInbox()) {
                    mPage++;
                    loadDataInbox();
                }
            }
        });
        DialogCounterAlert.DialogProgress.dismiss();
    }


}
