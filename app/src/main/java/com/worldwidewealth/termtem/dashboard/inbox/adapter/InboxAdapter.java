package com.worldwidewealth.termtem.dashboard.inbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.worldwidewealth.termtem.ActivityShowNotify;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.InboxActivity;
import com.worldwidewealth.termtem.dashboard.widgets.OnLoadMoreListener;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.InboxResponse;

import java.util.List;

/**
 * Created by user on 14-Feb-17.
 */

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem,totalItemCount;
    private List<InboxResponse> mListInbox;
    private Context mContext;
    public boolean maxInbox = false;



    public InboxAdapter(Context context, RecyclerView recyclerView, List<InboxResponse> listdata) {
        this.mListInbox = listdata;
        this.mContext = context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading&&totalItemCount <= (lastVisibleItem+visibleThreshold)&&!isMaxInbox()){
                    if (mOnLoadMoreListener != null){
                        mOnLoadMoreListener.onLoadMore();
                    }

                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        switch (viewType){
            case VIEW_TYPE_ITEM:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.item_inbox, parent, false);
                return new InboxViewHolder(rootView);
            case VIEW_TYPE_LOADING:
                rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(rootView);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof InboxViewHolder){

            if (!getItem(position).isReaded()){
                ((CardView)holder.itemView).setCardBackgroundColor(mContext.getResources().getColor(android.R.color.holo_orange_light));
            } else {
                ((CardView)holder.itemView).setCardBackgroundColor(mContext.getResources().getColor(android.R.color.white));
            }

            ((InboxViewHolder) holder).mTitleNotify.setText(getItem(position).getTitle());
            ((InboxViewHolder) holder).mDesNotify.setText(getItem(position).getMsg());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItem(position).setReaded(true);
                    notifyDataSetChanged();
                    Intent intent = new Intent(mContext, ActivityShowNotify.class);
                    intent.putExtra(MyFirebaseMessagingService.TEXT, getItem(position).getTitle());
                    intent.putExtra(MyFirebaseMessagingService.BOX, getItem(position).getMsg());
                    intent.putExtra(MyFirebaseMessagingService.MSGID, getItem(position).getMsgid());
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_in_up, 0);
                    mContext.startActivity(intent);

                }
            });


        } else if (holder instanceof LoadingViewHolder){
            ((LoadingViewHolder) holder).mProgressbar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mListInbox == null ? 0 : mListInbox.size();
    }

    public InboxResponse getItem(int position){
        return mListInbox.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mListInbox.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }

    public void setLoaded(){
        isLoading = false;
        DialogCounterAlert.DialogProgress.dismiss();
    }

    public boolean isMaxInbox() {
        return maxInbox;
    }

    public void clearAll(){
        mListInbox = null;
        notifyDataSetChanged();
    }

    public void setMaxInbox(boolean maxInbox) {
        this.maxInbox = maxInbox;
    }

    public class InboxViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIconNotify;
        private TextView mTitleNotify, mDesNotify;
        public InboxViewHolder(View itemView) {
            super(itemView);
            mIconNotify = (ImageView) itemView.findViewById(R.id.icon_notify);
            mTitleNotify = (TextView) itemView.findViewById(R.id.title_notify);
            mDesNotify = (TextView) itemView.findViewById(R.id.des_notify);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar mProgressbar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgressbar = (ProgressBar) itemView.findViewById(R.id.progress_loading);
        }
    }
}
