package com.worldwidewealth.termtem.dashboard.inbox.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;
import com.worldwidewealth.termtem.EncryptionData;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.adapter.InboxAdapter;
import com.worldwidewealth.termtem.dashboard.inbox.adapter.InboxPagerAdapter;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.InboxRequest;
import com.worldwidewealth.termtem.model.InboxResponse;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIHelper;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.util.ErrorNetworkThrowable;
import com.worldwidewealth.termtem.util.Util;
import com.worldwidewealth.termtem.widgets.OnLoadMoreListener;
import com.worldwidewealth.termtem.widgets.TermTemLoading;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
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
    public static final String TEXT_SEARCH = "textsearch";
    public static final String DATE_FROM = "datefrom";
    public static final String DATE_TO = "dateto";
    public static final String INBOX_TYPE = "inboxtype";
    public static final String POSITION = "position";
    public static final String TAG = InboxFragment.class.getSimpleName();
    public static final int DELETE_INBOX = 0x0;
    public static final int READ_INBOX = 0x1;

    private RecyclerView mInboxRecycler;
    private SwipeRefreshLayout mRefresh;
    private InboxAdapter mInboxAdapter;
    private ScaleInAnimationAdapter animationAdapter;
    private TermTemLoading mLoading;

    private int mPageType;
    private OnActiveFragment mCallback;
    private APIServices services;
    private View rootView;
    private Call<ResponseBody> call;
    private ArrayList<InboxResponse> mListInbox;
    private int mPageList = 1;
    private long mDateFrom, mDateTo;
    private String mText = "";
    private ArrayList<InboxResponse> mListMockUp;
    private SparseBooleanArray mSelectedPositions;
    private boolean mIsSelectable = false;
    private int countSeclect = 0;



    // TODO: Rename and change types of parameters

/*
    private void onUpdateDataSearch(String text){

    }
*/

    @Subscribe
    public void readMessage(String id){
        if (mInboxAdapter == null) return;

        mInboxAdapter.readById(id);
    }

    @Subscribe
    public void removeMessage(InboxResponse object){
        if (mInboxAdapter == null) return;

        mInboxAdapter.removeByObject(object);
    }



    public interface OnActiveFragment{
        void onUpdateDataSearch(String text, long datefrom, long dateto);
        void onCallSelectMode(int count);
    }


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
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (OnActiveFragment) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
            + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        services = APIServices.retrofit.create(APIServices.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 1, 1);

        mDateFrom = Util.getTimestamp(calendar.getTimeInMillis(), 0, 0, 0);
        mDateTo = Util.getTimestamp(System.currentTimeMillis(), 23, 59, 59);
        if (getArguments() != null) {
            mPageType = getArguments().getInt(PAGE_TYPE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
            Util.setupUI(rootView.findViewById(R.id.layout_parent));
            mInboxRecycler = (RecyclerView) rootView.findViewById(R.id.inbox_recyclear);
            mRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
            mRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mListInbox == null) {
            if (mLoading == null){
                mLoading = new TermTemLoading(getContext(), (ViewGroup) getActivity().findViewById(R.id.layout_parent));
            }

            mLoading.show();

            loadDataInbox();
        } else {

            if(savedInstanceState != null) {
                mPageList = savedInstanceState.getInt(PAGE_LIST);
                mListInbox = savedInstanceState.getParcelableArrayList(LISTINBOX);
                mText = savedInstanceState.getString(TEXT_SEARCH);
                mDateFrom = savedInstanceState.getLong(DATE_FROM);
                mDateTo = savedInstanceState.getLong(DATE_TO);
                mPageType = savedInstanceState.getInt(INBOX_TYPE);
            }
            mCallback.onUpdateDataSearch(mText, mDateFrom, mDateTo);

            initListInbox();
//            mInboxAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveINstance");

        outState.putInt(PAGE_LIST, mPageList);
        outState.putParcelableArrayList(LISTINBOX, mListInbox);
        outState.putString(TEXT_SEARCH, mText);
        outState.putLong(DATE_FROM, mDateFrom);
        outState.putLong(DATE_TO, mDateTo);
        outState.putInt(INBOX_TYPE, mPageType);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getBus().register(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "DestroyView");
        MyApplication.getBus().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call.isExecuted()){
            call.cancel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case DELETE_INBOX:
                if (resultCode == Activity.RESULT_OK){
                    mInboxAdapter.removeItem(data.getExtras().getInt(POSITION));
                }
                break;
            case READ_INBOX:
                if (resultCode == Activity.RESULT_OK){
                    mInboxAdapter.getItem(data.getExtras().getInt(POSITION)).setReaded(true);
                    mInboxAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void initListInbox(){
        if (mInboxAdapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mInboxRecycler.setLayoutManager(layoutManager);
            mInboxAdapter = new InboxAdapter(this, mInboxRecycler, mListInbox, mPageType);
            animationAdapter = new ScaleInAnimationAdapter(mInboxAdapter);
            animationAdapter.setInterpolator(new OvershootInterpolator());
            animationAdapter.setDuration(700);
            mInboxRecycler.setAdapter(animationAdapter);
            mInboxAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!mInboxAdapter.isMaxInbox()) {
                        mPageList++;
                        loadDataInbox();
                    }
                }
            });

            mInboxAdapter.setOnItemLongClickListener(new InboxAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(InboxAdapter.InboxViewHolder holder, int position) {
                    Log.e(TAG, "InboxDate: "+holder.mItemInbox.getDate().toString());
                    Log.e(TAG, "position: "+holder.itemView.getTag());
                    if(!isSelectable()){
                        setSelectable(true);
                        setItemChecked(position, true);
                    }
                }
            });

            //SwipeRefresh
            mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mPageList = 1;
                    loadDataInbox();
                }
            });

/*
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                    //Remove swiped item from list and notify the RecyclerView
                    Log.e(TAG, "position: "+viewHolder.itemView.getTag());
                    final int position = (int) viewHolder.itemView.getTag();
                    AlertDialog builderInner = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogError)
                            .setMessage(getContext().getString(R.string.confirm_delete_msg))
                            .setTitle(getContext().getString(R.string.confirm_delete_title))
                            .setCancelable(false)
                            .setPositiveButton(getContext().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.dismiss();
                                    mInboxAdapter.removeItem(position);
                                }
                            })
                            .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mInboxAdapter.notifyDataSetChanged();
                                }
                            }).create();

                    builderInner.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE)
                                    .setTextColor(getContext().getResources()
                                            .getColor(android.R.color.holo_red_dark));
                        }
                    });
                    builderInner.show();

                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(mInboxRecycler);
*/
        } else {
            mInboxAdapter.setLoaded();
            mInboxAdapter.setMaxInbox(false);
            Log.e(TAG, mListInbox.toString());
            mInboxAdapter.addAll(mListInbox);
        }
        DialogCounterAlert.DialogProgress.dismiss();
    }

    public void setItemChecked(int position, boolean isChecked) {
        if (isChecked) countSeclect++;
        else countSeclect--;
        Log.e(TAG, "At position: "+position+" isChecked: "+isChecked);
        mSelectedPositions.put(position, isChecked);
        mCallback.onCallSelectMode(countSeclect);

    }

    public boolean isItemChecked(int position) {
        if (mSelectedPositions == null) return false;
        return mSelectedPositions.get(position);
    }

    public void setSelectable(boolean selectable) {
        if (selectable){
            mSelectedPositions = new SparseBooleanArray();
            countSeclect = 0;
        }
        mIsSelectable = selectable;
        mInboxAdapter.notifyDataSetChanged();
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }

    public void deleteList(){
        List<Integer> items = new ArrayList<>(mSelectedPositions.size());
        for (int i = 0; i< mSelectedPositions.size(); i++){
            if (mSelectedPositions.valueAt(i))
                items.add(mSelectedPositions.keyAt(i));
            Log.e(TAG, "Position at: "+mSelectedPositions.keyAt(i)+" isChecked:"+mSelectedPositions.valueAt(i));
        }
        if (items.size() != 0){
            mInboxAdapter.removeListSelected(items);
            mSelectedPositions.clear();
            countSeclect = 0;
            mCallback.onCallSelectMode(countSeclect);
        }
//        mSelectedPositions = new SparseBooleanArray();
//        mInboxAdapter.notifyItemRangeChanged(0, mInboxAdapter.getItemCount());
    }

    private void loadDataInbox(){
        if (mPageList == 1) {
//            new DialogCounterAlert.DialogProgress(getContext());
            if (mInboxAdapter != null) mInboxAdapter.clearAll();

        }

        int typePage = -1;
        switch (mPageType){
            case InboxPagerAdapter.ALL:
                typePage = InboxRequest.TYPE_ALL;
                break;
            case InboxPagerAdapter.TEXT:
                typePage = InboxRequest.TYPE_TEXT;
                break;
            case InboxPagerAdapter.IMAGE:
                typePage = InboxRequest.TYPE_IMAGE;
                break;
            case InboxPagerAdapter.VIDEO:
                typePage = InboxRequest.TYPE_VIDEO;
                break;
        }


        call = services.service(
                new RequestModel(APIServices.ACTIONLOADINBOX,
                        new InboxRequest(mPageList, mDateFrom, mDateTo, mText, typePage)));
        APIHelper.enqueueWithRetry(call, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Object objectResponse = EncryptionData.getModel(
                        getContext(), call, response.body(), this);

                if (objectResponse instanceof String) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new Util.JsonDateDeserializer()).create();
                    final ArrayList<InboxResponse> listinbox = gson
                            .fromJson((String) objectResponse,
                                    new TypeToken<ArrayList<InboxResponse>>() {
                                    }.getType());

                    if (listinbox.size() == 0 && mInboxAdapter != null) {
                        mInboxAdapter.setLoaded();
                        mInboxAdapter.setMaxInbox(true);
                    } else {

                        if (mPageList == 1) {
                            mListInbox = listinbox;
                            initListInbox();
                        } else {
                            mListInbox.add(null);
                            mInboxAdapter.notifyItemInserted(mListInbox.size() - 1);
                            //Load more data for reyclerview
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Remove loading item
                                    mListInbox.remove(mListInbox.size() - 1);
                                    mInboxAdapter.notifyItemRemoved(mListInbox.size());
                                    //Load data
/*
                                int index = mListInbox.size();
                                int end = index + 20;
*/
                                    for (InboxResponse model : listinbox) {
                                        mListInbox.add(model);
                                    }
                                    mInboxAdapter.notifyItemChanged(mInboxAdapter.getItemCount());
                                    mInboxAdapter.setLoaded();
                                }
                            }, 1000);
                        }
                    }

                }

                if (mInboxAdapter == null || mInboxAdapter.getItemCount() == 0) {
                    getView().findViewById(R.id.msg_not_have).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.msg_not_have).setVisibility(View.GONE);
                }


                    mLoading.hide();
                if (mRefresh.isShown()){
                    mRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mLoading.hide();
                if (mRefresh.isShown()){
                    mRefresh.setRefreshing(false);
                }

                new ErrorNetworkThrowable(t).networkError(getContext(), call, this);
            }
        });
    }

    public void search(String text, long datefrom, long dateto){
        this.mText = text;
        this.mDateFrom = datefrom;
        this.mDateTo = dateto;
        this.mPageList = 1;

        mCallback.onUpdateDataSearch(mText, mDateFrom, mDateTo);
        if (mLoading == null){
            mLoading = new TermTemLoading(getContext(), (ViewGroup) getActivity().findViewById(R.id.layout_parent));
        }

        mLoading.show();

        loadDataInbox();
    }

}
