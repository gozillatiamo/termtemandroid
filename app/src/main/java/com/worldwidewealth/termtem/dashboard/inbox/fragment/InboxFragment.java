package com.worldwidewealth.termtem.dashboard.inbox.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.InboxActivity;
import com.worldwidewealth.termtem.dashboard.inbox.adapter.InboxAdapter;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.InboxRepuest;
import com.worldwidewealth.termtem.model.InboxResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InboxFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PAGE_TYPE = "pagetype";
    public static final String PAGE_LIST = "pagelist";
    public static final String LISTINBOX = "listinbox";
    public static final String TAG = InboxFragment.class.getSimpleName();

    public static final int ALL = 0;
    public static final int TEXT = 1;
    public static final int IMAGE = 2;
    public static final int VIDEO = 3;

    private RecyclerView mInboxRecycler;
    private InboxAdapter mInboxAdapter;

    private int mPageType;
    private APIServices services;
    private View rootView;
    private Call<ResponseBody> call;
    private ArrayList<InboxResponse> mListInbox;
    private int mPageList = 1;
    private long mDateFrom, mDateTo;
    private String mText = "";

    // TODO: Rename and change types of parameters


    public InboxFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InboxFragment newInstance(int type) {
        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        services = APIServices.retrofit.create(APIServices.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 1, 1);

        mDateFrom = Util.getTimestamp(calendar.getTimeInMillis(), 0);
        mDateTo = Util.getTimestamp(System.currentTimeMillis(), 23);

        if (getArguments() != null) {
            mPageType = getArguments().getInt(PAGE_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
            mInboxRecycler = (RecyclerView) rootView.findViewById(R.id.inbox_recyclear);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null ) {
            loadDataInbox();
        } else {
            mPageList = savedInstanceState.getInt(PAGE_LIST);
            mListInbox = savedInstanceState.getParcelableArrayList(LISTINBOX);
//            mInboxAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveINstance");

        outState.putInt(PAGE_LIST, mPageList);
        outState.putParcelableArrayList(LISTINBOX, mListInbox);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "DestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call.isExecuted()){
            call.cancel();
        }
    }

    private void initListInbox(){
        if (mInboxAdapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mInboxRecycler.setLayoutManager(layoutManager);
            mInboxAdapter = new InboxAdapter(getContext(), mInboxRecycler, mListInbox);
            mInboxRecycler.setAdapter(mInboxAdapter);
            mInboxAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!mInboxAdapter.isMaxInbox()) {
                        mPageList++;
                        loadDataInbox();
                    }
                }
            });
        } else {
            mInboxAdapter.setLoaded();
            mInboxAdapter.setMaxInbox(false);

            mInboxAdapter.notifyDataSetChanged();
        }
        DialogCounterAlert.DialogProgress.dismiss();
    }

    private void loadDataInbox(){
        if (mPageList == 1) {
            new DialogCounterAlert.DialogProgress(getContext());
            if (mInboxAdapter != null) mInboxAdapter.clearAll();
        }

        call = services.service(
                new RequestModel(APIServices.ACTIONLOADINBOX,
                        new InboxRepuest(mPageList, mDateFrom, mDateTo, mText)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(
                        getContext(), call, response.body(), this);

                if (objectResponse instanceof String){
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    final ArrayList<InboxResponse> listinbox = gson
                            .fromJson((String)objectResponse,
                                    new TypeToken<ArrayList<InboxResponse>>(){}.getType());

                    if (listinbox.size() == 0 && mInboxAdapter != null ){
                        mInboxAdapter.setLoaded();
                        mInboxAdapter.setMaxInbox(true);
                        return;
                    }

                    if (mPageList == 1) {
                        mListInbox = listinbox;
                        initListInbox();
                    } else {
                        mListInbox.add(null);
                        mInboxAdapter.notifyItemInserted(mListInbox.size() - 1);
                        //Load more data for reyclerview
                        new Handler().postDelayed(new Runnable() {
                            @Override public void run() {
                                //Remove loading item
                                mListInbox.remove(mListInbox.size() - 1);
                                mInboxAdapter.notifyItemRemoved(mListInbox.size());
                                //Load data
/*
                                int index = mListInbox.size();
                                int end = index + 20;
*/
                                for (InboxResponse model:listinbox){
                                    mListInbox.add(model);
                                }
                                mInboxAdapter.notifyDataSetChanged();
                                mInboxAdapter.setLoaded();
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
            }
        });
    }

}
