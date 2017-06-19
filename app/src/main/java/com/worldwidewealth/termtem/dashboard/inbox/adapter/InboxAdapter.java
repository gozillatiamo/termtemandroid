package com.worldwidewealth.termtem.dashboard.inbox.adapter;

import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.fragment.InboxBottomSheetDialogFragment;
import com.worldwidewealth.termtem.dashboard.inbox.fragment.InboxFragment;
import com.worldwidewealth.termtem.model.ActionItemRequest;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.widgets.InformationView;
import com.worldwidewealth.termtem.widgets.OnLoadMoreListener;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.InboxResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 14-Feb-17.
 */

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        InformationView.InformationClickListener, InformationView.InformationLongClickListener {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 1;
    private int lastVisibleItem,totalItemCount;
    private int mPage;
    private List<InboxResponse> mListInbox;
    private InboxFragment mFragment;
    private OnItemLongClickListener longClickListener;
    private APIServices services = APIServices.retrofit.create(APIServices.class);
    public boolean maxInbox = false;
    public static final String TAG = InboxAdapter.class.getSimpleName();


    public interface OnItemLongClickListener{
        void onItemLongClick(InboxViewHolder holder, int position);
    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }


    public InboxAdapter(InboxFragment fragment, RecyclerView recyclerView, List<InboxResponse> listdata, int page) {
        this.mListInbox = listdata;
        this.mFragment = fragment;
        this.mPage = page;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem <= 0) return;

                if (!isLoading&&totalItemCount <= (lastVisibleItem+visibleThreshold)&&!isMaxInbox()){

                    isLoading = true;
                    if (mOnLoadMoreListener != null){
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        switch (viewType){
            case VIEW_TYPE_ITEM:
                rootView = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.item_inbox, parent, false);
                return new InboxViewHolder(rootView);
            case VIEW_TYPE_LOADING:
                rootView = LayoutInflater.from(mFragment.getContext()).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(rootView);
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof InboxViewHolder){
            holder.itemView.setTag(position);
            InboxViewHolder inboxViewHolder = (InboxViewHolder) holder;

            inboxViewHolder.mItemInbox.setTitle(getItem(position).getTitle());
            inboxViewHolder.mItemInbox.setDes(getItem(position).getMsg());
            inboxViewHolder.mItemInbox.setRead(getItem(position).isReaded());

            if (getItem(position).getThumbnail() != -1){
                inboxViewHolder.mItemInbox.setThumbnail(getItem(position).getThumbnail());
            }


//            inboxViewHolder.mItemInbox.setLengthVideo(getItem(position).getTimeLength());

            inboxViewHolder.mItemInbox.setDate(getItem(position).getCreate_Date());
            inboxViewHolder.mItemInbox.setType(getItem(position).get_type());
            switch (InformationView.TYPE.getTypeAt(getItem(position).get_type())){
                case IMAGE:
                    inboxViewHolder.mItemInbox.setImageThumbnail(getItem(position).getAttachlist());
                    break;

            }
            inboxViewHolder.mItemInbox.setInformationClickListener(this, (InboxViewHolder) holder, position);
            inboxViewHolder.mItemInbox.setInformationLongClickListener(this, (InboxViewHolder) holder);

            if (mFragment.isSelectable()){
                inboxViewHolder.mItemInbox.setEnableCheckDelete(true);
                inboxViewHolder.mItemInbox.setCheckDelete(mFragment.isItemChecked(position));
            } else {
                inboxViewHolder.mItemInbox.setEnableCheckDelete(false);
                inboxViewHolder.mItemInbox.setCheckDelete(false);
            }
        } else if (holder instanceof LoadingViewHolder){
            ((LoadingViewHolder) holder).mProgressbar.setIndeterminate(true);
        }
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        longClickListener = listener;
    }

    @Override
    public void onInformationLongViewClick(InboxViewHolder holder, final int position) {
        if(longClickListener != null){
            longClickListener.onItemLongClick(holder, position);
        }
    }


    @Override
    public void onInformationViewClick(InboxViewHolder holder, int position) {
        if (position == -1) return;
        if (!mFragment.isSelectable()) {
            BottomSheetDialogFragment bottomSheetDialogFragment = InboxBottomSheetDialogFragment.newInstance(getItem(position), position, mPage);
            bottomSheetDialogFragment.setTargetFragment(mFragment, 0);
            bottomSheetDialogFragment.show(mFragment.getFragmentManager(), bottomSheetDialogFragment.getTag());
        } else {
            holder.mItemInbox.checkToggle();
            mFragment.setItemChecked(position, holder.mItemInbox.isCheckDelete());
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

    public void readById(String id){
        if (mListInbox == null) return;
        for(InboxResponse inbox : mListInbox){
            if (inbox.getMsgid().equals(id) && !inbox.isReaded()){
                inbox.setReaded(true);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void removeByObject(InboxResponse object){
        if (mListInbox == null) return;
        for (int i = 0; i < mListInbox.size(); i++){
            if (mListInbox.get(i).getMsgid().equals(object.getMsgid())){
                mListInbox.remove(mListInbox.get(i));
                this.notifyItemRemoved(i);
                this.notifyItemRangeChanged(i, getItemCount());

                break;
            }
        }
    }


    public void clearAll(){
        mListInbox = null;
        notifyDataSetChanged();
    }

    public void addAll(List<InboxResponse> list_inbox){
        mListInbox = list_inbox;
        notifyDataSetChanged();
    }

    public void removeListSelected(List<Integer> items){

        for (int i = 0; i < items.size(); i++) {
            serviceRemove(items.get(i)-i);
/*
            mListInbox.remove(items.get(i)-i);
            this.notifyItemRemoved(items.get(i)-i);
            this.notifyItemRangeChanged(items.get(i)-i, getItemCount());
*/

        }
//            mListInbox.remove(item);
//        notifyDataSetChanged();
    }

    public void removeItem(int position){
        serviceRemove(position);
/*
        mListInbox.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
*/
    }

    private void serviceRemove(int position){

            Call<ResponseBody> call = services.service(
                    new RequestModel(APIServices.ACTIONREMOVEMSG,
                            ActionItemRequest.MSG(getItem(position).getMsgid())));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        MyApplication.getBus().post(getItem(position));

    }



    public void setMaxInbox(boolean maxInbox) {
        this.maxInbox = maxInbox;
    }


    public class InboxViewHolder extends RecyclerView.ViewHolder{
        public InformationView mItemInbox;
        public InboxViewHolder(View itemView) {
            super(itemView);

            mItemInbox = (InformationView) itemView.findViewById(R.id.item_inbox);

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
