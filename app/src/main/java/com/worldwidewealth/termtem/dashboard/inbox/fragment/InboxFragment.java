package com.worldwidewealth.termtem.dashboard.inbox.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

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
import com.worldwidewealth.termtem.widgets.MultiSelector;
import com.worldwidewealth.termtem.widgets.OnLoadMoreListener;

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
    public static final String TAG = InboxFragment.class.getSimpleName();


    public static final int ALL = 0;
    public static final int TEXT = 1;
    public static final int VIDEO = 2;
    public static final int IMAGE = 3;

    private RecyclerView mInboxRecycler;
    private InboxAdapter mInboxAdapter;
    private ScaleInAnimationAdapter animationAdapter;

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

        mDateFrom = Util.getTimestamp(calendar.getTimeInMillis(), 0);
        mDateTo = Util.getTimestamp(System.currentTimeMillis(), 23);
        if (getArguments() != null) {
            mPageType = getArguments().getInt(PAGE_TYPE);
        }

/*
        mListMockUp = new ArrayList<>();

        for (int i = 0; i < 10; i++){
            InboxResponse response = new InboxResponse();
            response.setTitle("Title "+(i+1));
            response.setCreate_Date(new Date());
            response.setMsg("Description "+(i+1));
            response.setReaded(true);
            response.setThumbnail(R.drawable.thumbnail_video);
            if (mPageType == VIDEO){
                response.setTimeLength(i+":00");
                response.setUrl("https://video"+i+".co.th");
            }
            mListMockUp.add(response);
        }
*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
            Util.setupUI(rootView.findViewById(R.id.layout_parent));
            mInboxRecycler = (RecyclerView) rootView.findViewById(R.id.inbox_recyclear);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mListInbox == null) {
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
/*
            switch (mPageType){
                case ALL:
                    mListInbox.addAll(mListMockUp);
                    break;
                case VIDEO:
                case IMAGE:
                    mListInbox = mListMockUp;
                    break;
            }
*/
            mInboxAdapter = new InboxAdapter(this, mInboxRecycler, mListInbox);
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
                    Log.e(TAG, "position: "+position);
                    if(!isSelectable()){
                        setSelectable(true);
                        setItemChecked(position, true);
                    }
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

        mSelectedPositions.put(position, isChecked);
        mCallback.onCallSelectMode(countSeclect);

    }

    public boolean isItemChecked(int position) {
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
        mInboxAdapter.removeListSelected(mSelectedPositions);
        mSelectedPositions = new SparseBooleanArray();
//        mInboxAdapter.notifyItemRangeChanged(0, mInboxAdapter.getItemCount());
    }

    private void loadDataInbox(){
        if (mPageList == 1) {
//            new DialogCounterAlert.DialogProgress(getContext());
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
                                mInboxAdapter.notifyItemChanged(mInboxAdapter.getItemCount());
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

    public void search(String text, long datefrom, long dateto){
        this.mText = text;
        this.mDateFrom = datefrom;
        this.mDateTo = dateto;
        this.mPageList = 1;

        mCallback.onUpdateDataSearch(mText, mDateFrom, mDateTo);
        loadDataInbox();
    }

}
