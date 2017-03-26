package com.worldwidewealth.termtem.dashboard.inbox;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.adapter.InboxAdapter;
import com.worldwidewealth.termtem.dashboard.inbox.adapter.InboxPagerAdapter;
import com.worldwidewealth.termtem.dashboard.inbox.fragment.InboxFragment;
import com.worldwidewealth.termtem.widgets.EnableViewPager;
import com.worldwidewealth.termtem.widgets.OnLoadMoreListener;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.dialog.TermTemDialog;
import com.worldwidewealth.termtem.model.InboxRepuest;
import com.worldwidewealth.termtem.model.InboxResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxActivity extends MyAppcompatActivity implements InboxFragment.OnActiveFragment{

    private RecyclerView mInboxRecycler;
    private Toolbar mToolbar;
    private TermTemDialog.SearchDateRangeDialog mSearchDateRange;
    private APIServices services;
    private List<InboxResponse> mListInbox;
    private long mDateFrom, mDateTo;
    private String mText = "";
    private InboxAdapter mAdapter;
    private int mPage = 1;
    private SearchView searchView = null;
    private TabLayout mInboxTabLayout;
    private EnableViewPager mInboxViewPager;
    private int statusBarColor;
    private static InboxFragment mCurrentInbox;
    private ActionMode mActionMode;
    public static final String TAG = InboxActivity.class.getSimpleName();

    private ActionMode.Callback mDeleteMode = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_bill_select, menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //hold current color of status bar
                statusBarColor = getWindow().getStatusBarColor();
                //set your gray color
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark));
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_delete:
                    mCurrentInbox = (InboxFragment) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.pager_inbox + ":"
                                    + mInboxViewPager.getCurrentItem());
                    mCurrentInbox.deleteList();
//                    mode.finish();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //return to "old" color of status bar
                getWindow().setStatusBarColor(statusBarColor);
            }
            mInboxViewPager.setPagingEnabled(true);
            mInboxTabLayout.setVisibility(View.VISIBLE);
            mCurrentInbox = (InboxFragment) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.pager_inbox + ":"
                            + mInboxViewPager.getCurrentItem());
            mCurrentInbox.setSelectable(false);
        }
    };


    @Override
    public void onUpdateDataSearch(String text, long datefrom, long dateto) {
        //searchView.setQuery(text, false);
        mText = text;
        mSearchDateRange.setDateFrom(datefrom);
        mSearchDateRange.setDateTo(dateto);
    }

    @Override
    public void onCallSelectMode(int count) {

        if(mInboxTabLayout.getVisibility() == View.VISIBLE){
            mActionMode = this.startSupportActionMode(mDeleteMode);
            this.getSupportActionBar().startActionMode(mDeleteMode);
            mInboxViewPager.setPagingEnabled(false);
            mInboxTabLayout.setVisibility(View.GONE);

        }
        mActionMode.setTitle(count+" รายการที่เลือก");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        services = APIServices.retrofit.create(APIServices.class);
        Util.setupUI(findViewById(R.id.layout_parent));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 1, 1);
        mDateFrom = Util.getTimestamp(calendar.getTimeInMillis(), 0);
        mDateTo = Util.getTimestamp(System.currentTimeMillis(), 23);
        bindView();
        initToolbar();
        initViewPager();
//        loadDataInbox();

        mSearchDateRange = new TermTemDialog.SearchDateRangeDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mSearchDateRange.canSearch()) return;

                ContentValues values = mSearchDateRange.getDateFromTo();
                mDateFrom = values.getAsLong(TermTemDialog.SearchDateRangeDialog.DATEFROM);
                mDateTo = values.getAsLong(TermTemDialog.SearchDateRangeDialog.DATETO);
                mPage = 1;
                searchInbox(mText, mDateFrom, mDateTo);
//                loadDataInbox();
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

            final EditText txt = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
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
                    searchInbox(mText, mDateFrom, mDateTo);

//                    loadDataInbox();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (mText.equals(newText)) return true;
                    mText = newText;
                    if (newText.equals("")){
                        mPage = 1;
                        searchInbox(mText, mDateFrom, mDateTo);

//                        loadDataInbox();
                    }
                    return false;
                }
            });

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e(TAG, "onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
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

    private void initViewPager(){
        mInboxViewPager.setAdapter(new InboxPagerAdapter(getSupportFragmentManager(), this));
        mInboxTabLayout.setupWithViewPager(mInboxViewPager);

        mInboxViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentInbox = (InboxFragment) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.pager_inbox + ":"
                                + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void searchInbox(String text, long datefrom, long dateto){
        mCurrentInbox = (InboxFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.pager_inbox + ":"
                        + mInboxViewPager.getCurrentItem());

        if (mCurrentInbox != null){
            mCurrentInbox.search(text, datefrom, dateto);
        }
    }


    private void bindView(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mInboxTabLayout = (TabLayout) findViewById(R.id.tablayout_inbox);
        mInboxViewPager = (EnableViewPager) findViewById(R.id.pager_inbox);
    }

    private void initToolbar(){
        mToolbar.hideOverflowMenu();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.in_box));
    }


}
