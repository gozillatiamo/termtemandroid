package com.worldwidewealth.termtem.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.vr.sdk.widgets.video.VrVideoView;
import com.worldwidewealth.termtem.MyApplication;
import com.worldwidewealth.termtem.R;
import com.worldwidewealth.termtem.model.AttachResponseModel;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by user on 07-Apr-17.
 */

public class WidgetTypeInbox extends FrameLayout{

    private RecyclerView mRecyclerImage;
    private View mLayoutVrView;
    private VideoView mVideoView;

    private int mWidgetType;
    private List<AttachResponseModel> mListImage;

    public enum WIDGET_TYPE{
        VIDEO(0),
        VIDEO_VR(1),
        IMAGE(2);

        private int widgetType;
        WIDGET_TYPE(int i) {
            this.widgetType = i;
        }

        public int getWidgetType() {
            return widgetType;
        }
    }

    public WidgetTypeInbox(@NonNull Context context) {
        super(context);
        setup(null);
    }

    public WidgetTypeInbox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);

    }

    public WidgetTypeInbox(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WidgetTypeInbox(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SaveState ss = new SaveState(parcelable);
        ss.mWidgetType = this.mWidgetType;
        ss.mListImage = this.mListImage;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if(!(state instanceof SaveState)){
            super.onRestoreInstanceState(state);
            return;
        }

        SaveState ss = (SaveState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mWidgetType = ss.mWidgetType;
        this.mListImage = ss.mListImage;
        setWidgetType(this.mWidgetType);
//        setImage();
    }

    private void setup(AttributeSet attrs){
        inflate(getContext(), R.layout.widget_type_inbox, this);
        bindView();
        setupStyleable(attrs);
        setupView();
    }

    private void bindView(){
        mRecyclerImage = (RecyclerView) findViewById(R.id.list_image);
        mLayoutVrView = findViewById(R.id.layout_360_view);
        mVideoView = (VideoView) findViewById(R.id.video_view);
    }

    private void setupStyleable(AttributeSet attrs){
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetTypeInbox);
            this.mWidgetType = typedArray.getInt(R.styleable.WidgetTypeInbox_ti_type, -1);
        }
    }

    private void setupView(){
        setWidgetType(this.mWidgetType);
    }

    public void setWidgetType(int type){
        this.mWidgetType = type;
//        mExoPlayerView.setVisibility(GONE);
        mVideoView.setVisibility(GONE);
        mLayoutVrView.setVisibility(GONE);
        mRecyclerImage.setVisibility(GONE);
        if (type == -1) return;
        switch (WIDGET_TYPE.values()[type]){
            case VIDEO:
                mVideoView.setVisibility(VISIBLE);
                mVideoView.play();
//                mExoPlayerView.setVisibility(VISIBLE);
                if (isInEditMode()) return;
//                setVideo(Uri.parse("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));
                break;
            case VIDEO_VR:
                mLayoutVrView.setVisibility(VISIBLE);

                break;
            case IMAGE:
                mRecyclerImage.setVisibility(VISIBLE);
                break;
        }
    }

    public void setImage(List<AttachResponseModel> listImage){
        this.mListImage = listImage;

        int spanCount;

        if (mListImage.size() > 3)
            spanCount = 3;
        else
            spanCount = mListImage.size();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        mRecyclerImage.setLayoutManager(gridLayoutManager);
        ImageInboxAdapter adapter = new ImageInboxAdapter();
        mRecyclerImage.setAdapter(new AlphaInAnimationAdapter(adapter));
        mRecyclerImage.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = getResources().getDimensionPixelSize(R.dimen.activity_small_space);
                outRect.left = size;
                outRect.right = size;
                outRect.top = size;
                outRect.bottom = size;
            }
        });
    }




    private static class SaveState extends BaseSavedState{

        int mWidgetType;
        List<AttachResponseModel> mListImage;
        public SaveState(Parcel source) {
            super(source);
            this.mWidgetType = source.readInt();
            this.mListImage = source.readArrayList(source.getClass().getClassLoader());
        }

        public SaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.mWidgetType);
            out.writeList(this.mListImage);
        }

        public static final Creator<SaveState> CREATOR = new Creator<SaveState>() {
            @Override
            public SaveState createFromParcel(Parcel parcel) {
                return new SaveState(parcel);
            }

            @Override
            public SaveState[] newArray(int i) {
                return new SaveState[i];
            }
        };
    }

    private class ImageInboxAdapter extends RecyclerView.Adapter<ImageInboxAdapter.ViewHolder>
        implements OnClickListener{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(new MyImageView(getContext()));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MyImageView image = (MyImageView) holder.itemView;
            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            int sizePadding = getResources().getDimensionPixelSize(R.dimen.activity_small_space);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                TypedArray a = getContext().obtainStyledAttributes(new int[] { android.R.attr.selectableItemBackground });
                int resource = a.getResourceId(0, 0);
                //first 0 is the index in the array, second is the   default value
                a.recycle();

                image.setBackgroundResource(resource);
            }

//            image.(sizePadding, sizePadding, sizePadding, sizePadding);
            if (getItemCount() > 1){
                Glide.with(getContext())
                        .load(mListImage.get(position).getURLFILE())
                        .override(300, 300)
                        .crossFade()
                        .centerCrop()
                        .placeholder(R.drawable.ic_picture)
                        .into(image);
            } else {
                Glide.with(getContext())
                        .load(mListImage.get(position).getURLFILE())
                        .override(300, 300)
                        .crossFade()
                        .placeholder(R.drawable.ic_picture)
                        .into(image);
            }
            image.setTag(position);
            image.setOnClickListener(this);

        }

        @Override
        public int getItemCount() {
            return mListImage.size();
        }

        @Override
        public void onClick(View view) {
            ZoomView.zoomImageFromThumb(getContext(),
                    WidgetTypeInbox.this.getRootView(),
                    view,
                    mListImage.get((Integer) view.getTag()).getURLFILE());
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
