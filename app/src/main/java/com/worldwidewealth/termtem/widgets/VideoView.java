package com.worldwidewealth.termtem.widgets;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.google.android.exoplayer2.DefaultLoadControl;
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
 * Created by user on 05-May-17.
 */

public class VideoView extends FrameLayout{

    private SimpleExoPlayerView mExoPlayerView;
    private SimpleExoPlayer player;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DefaultTrackSelector trackSelector;
    private EventLoggerVideo eventLogger;
    private DataSource.Factory mediaDataSourceFactory;
    private Handler mainHandler;


    private VrVideoView mVrVideoView;
    private SeekBar mSeekbar;


    public VideoView(@NonNull Context context) {
        super(context);
        setup(null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(attrs);

    }

    private void setup(AttributeSet attrs){
        inflate(getContext(), R.layout.widget_video_view, this);
        bindView();
        setStyleable(attrs);
        setupView();
    }

    private void bindView(){
        mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer_view);
//        mVrVideoView = (VrVideoView) findViewById(R.id.vr_view);

    }

    private void setupView(){
        setVideo(Uri.parse("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));
    }

    private void setStyleable(AttributeSet attrs){

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
//        player.setPlayWhenReady(true);

        MediaSource mediaSources = new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, null);

        player.prepare(mediaSources);

    }

    public void play(){
        player.setPlayWhenReady(true);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        Activity activity = (Activity) getContext();
        return ((MyApplication) activity.getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

}
