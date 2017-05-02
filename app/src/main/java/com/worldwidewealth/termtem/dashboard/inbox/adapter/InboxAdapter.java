package com.worldwidewealth.termtem.dashboard.inbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.worldwidewealth.termtem.ActivityShowNotify;
import com.worldwidewealth.termtem.MyFirebaseMessagingService;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.dashboard.inbox.fragment.InboxBottomSheetDialogFragment;
import com.worldwidewealth.termtem.dashboard.inbox.fragment.InboxFragment;
import com.worldwidewealth.termtem.model.ReadMsgRequest;
import com.worldwidewealth.termtem.model.RequestModel;
import com.worldwidewealth.termtem.services.APIServices;
import com.worldwidewealth.termtem.widgets.InformationView;
import com.worldwidewealth.termtem.widgets.OnLoadMoreListener;
import com.worldwidewealth.termtem.dialog.DialogCounterAlert;
import com.worldwidewealth.termtem.model.InboxResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
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


            switch (mPage){
                case InboxPagerAdapter.ALL:
                    inboxViewHolder.mItemInbox.setType(InformationView.TYPE.TEXT.getType());
                    break;
                case InboxPagerAdapter.TEXT:
                    inboxViewHolder.mItemInbox.setType(InformationView.TYPE.TEXT.getType());
                    break;
                case InboxPagerAdapter.IMAGE:
                    int countImage = position % 5;
                    if (countImage == 0){
                        List<String> mListImage = new ArrayList<>();
                        mListImage.add("http://placehold.it/120x120&text=image1");
                        mListImage.add("http://placehold.it/120x120&text=image2");
                        mListImage.add("http://placehold.it/120x120&text=image3");
                        mListImage.add("http://placehold.it/120x120&text=image4");
                        mListImage.add("http://placehold.it/120x120&text=image4");
                        inboxViewHolder.mItemInbox.setType(InformationView.TYPE.IMAGE.getType());
                        inboxViewHolder.mItemInbox.setImageThumbnail(mListImage);

                    } else if (countImage == 1){
                        List<String> mListImage = new ArrayList<>();
                        mListImage.add("http://placehold.it/120x120&text=image1");
                        mListImage.add("http://placehold.it/120x120&text=image2");
                        mListImage.add("http://placehold.it/120x120&text=image3");
                        mListImage.add("http://placehold.it/120x120&text=image4");
                        inboxViewHolder.mItemInbox.setType(InformationView.TYPE.IMAGE.getType());
                        inboxViewHolder.mItemInbox.setImageThumbnail(mListImage);

                    } else if (countImage == 2){
                        List<String> mListImage = new ArrayList<>();
                        mListImage.add("http://placehold.it/120x120&text=image1");
                        mListImage.add("http://placehold.it/120x120&text=image2");
                        mListImage.add("http://placehold.it/120x120&text=image3");
                        inboxViewHolder.mItemInbox.setType(InformationView.TYPE.IMAGE.getType());
                        inboxViewHolder.mItemInbox.setImageThumbnail(mListImage);

                    } else if (countImage == 3){
                        List<String> mListImage = new ArrayList<>();
                        mListImage.add("http://placehold.it/120x120&text=image1");
                        mListImage.add("http://placehold.it/120x120&text=image2");
                        inboxViewHolder.mItemInbox.setType(InformationView.TYPE.IMAGE.getType());
                        inboxViewHolder.mItemInbox.setImageThumbnail(mListImage);

                    } else if (countImage == 4){
                        List<String> mListImage = new ArrayList<>();
                        mListImage.add("http://placehold.it/120x120&text=image1");
                        inboxViewHolder.mItemInbox.setType(InformationView.TYPE.IMAGE.getType());
                        inboxViewHolder.mItemInbox.setImageThumbnail(mListImage);

                    }

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
            mListInbox.remove(items.get(i)-i);
            this.notifyItemRemoved(items.get(i)-i);
            this.notifyItemRangeChanged(items.get(i)-i, getItemCount());

        }
//            mListInbox.remove(item);
//        notifyDataSetChanged();
    }

    public void removeItem(int position){
        serviceRemove(position);
        mListInbox.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, getItemCount());
    }

    private void serviceRemove(int position){
            Call<ResponseBody> call = services.service(
                    new RequestModel(APIServices.ACTIONREMOVEMSG,
                            new ReadMsgRequest(getItem(position).getMsgid())));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
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
