package com.worldwidewealth.termtem.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

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

/**
 * Created by user on 07-Apr-17.
 */

public class WidgetTypeInbox extends FrameLayout{

    private SimpleExoPlayerView mExoPlayerView;
    private SimpleExoPlayer player;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DefaultTrackSelector trackSelector;
    private EventLoggerVideo eventLogger;
    private DataSource.Factory mediaDataSourceFactory;
    private Handler mainHandler;


    private VrVideoView mVrVideoView;
    private SeekBar mSeekbar;
    private RecyclerView mRecyclerImage;
    private View mLayoutVrView;

    private int mWidgetType;

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
        setWidgetType(this.mWidgetType);
    }

    private void setup(AttributeSet attrs){
        inflate(getContext(), R.layout.widget_type_inbox, this);
        bindView();
        setupStyleable(attrs);
        setupView();
    }

    private void bindView(){
        mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer_view);
        mVrVideoView = (VrVideoView) findViewById(R.id.vr_view);
        mSeekbar = (SeekBar) findViewById(R.id.seek_vr);
        mRecyclerImage = (RecyclerView) findViewById(R.id.list_image);
        mLayoutVrView = findViewById(R.id.layout_360_view);
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

    private void setWidgetType(int type){
        this.mWidgetType = type;
        mExoPlayerView.setVisibility(GONE);
        mLayoutVrView.setVisibility(GONE);
        mRecyclerImage.setVisibility(GONE);

        switch (WIDGET_TYPE.values()[type]){
            case VIDEO:
                mExoPlayerView.setVisibility(VISIBLE);
                if (isInEditMode()) return;
                setVideo(Uri.parse("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));
                break;
            case VIDEO_VR:
                mLayoutVrView.setVisibility(VISIBLE);

                break;
            case IMAGE:
                mRecyclerImage.setVisibility(VISIBLE);
                break;
        }
    }

    private void setVideo(Uri uri){
        Activity activity = (Activity) getContext();
        @SimpleExoPlayer.ExtensionRendererMode int extensionRendererMode =
                ((MyApplication)activity.getApplication()).useExtensionRenderers()
                    ? SimpleExoPlayer.EXTENSION_RENDERER_MODE_ON:
                        SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF;

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        mediaDataSourceFactory = buildDataSourceFactory(true);
        mainHandler = new Handler();

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, new DefaultLoadControl());
//        eventLogger = new EventLoggerVideo();
/*
        player.addListener(eventLogger);
        player.setAudioDebugListener(eventLogger);
        player.setVideoDebugListener(eventLogger);
        player.setMetadataOutput(eventLogger);
*/
        mExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(true);

        MediaSource mediaSources = new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, null);

        player.prepare(mediaSources);

    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        Activity activity = (Activity) getContext();
        return ((MyApplication) activity.getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }


    private static class SaveState extends BaseSavedState{

        int mWidgetType;
        public SaveState(Parcel source) {
            super(source);
            this.mWidgetType = source.readInt();
        }

        public SaveState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.mWidgetType);
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

}
