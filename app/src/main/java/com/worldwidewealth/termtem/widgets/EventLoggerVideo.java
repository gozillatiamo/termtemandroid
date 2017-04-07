package com.worldwidewealth.termtem.widgets;

import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.io.IOException;

/**
 * Created by user on 07-Apr-17.
 */

final class EventLoggerVideo implements ExoPlayer.EventListener,
        AudioRendererEventListener, VideoRendererEventListener,
        AdaptiveMediaSourceEventListener, ExtractorMediaSource.EventListener,
        DefaultDrmSessionManager.EventListener, MetadataRenderer.Output{

    public static final String TAG = EventLoggerVideo.class.getSimpleName();
    //Implements ExoPlayer.EventListener
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.e(TAG, "onTimelineChanged");

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.e(TAG, "onTracksChanged");

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.e(TAG, "onLoadingChanged");

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.e(TAG, "onPlayerStateChanged");

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(TAG, "onPlayerError");

    }

    @Override
    public void onPositionDiscontinuity() {
        Log.e(TAG, "onPositionDiscontinuity");

    }


    //Implements AudioRendererEventListener

    @Override
    public void onAudioEnabled(DecoderCounters counters) {

    }

    @Override
    public void onAudioSessionId(int audioSessionId) {

    }

    @Override
    public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onAudioInputFormatChanged(Format format) {

    }

    @Override
    public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

    }

    @Override
    public void onAudioDisabled(DecoderCounters counters) {

    }


    //Implements VideoRendererEventListener
    @Override
    public void onVideoEnabled(DecoderCounters counters) {
        Log.e(TAG, "onVideoEnabled");

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
        Log.e(TAG, "onVideoDecoderInitialized");

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
        Log.e(TAG, "onVideoInputFormatChanged");

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
        Log.e(TAG, "onDroppedFrames");
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.e(TAG, "onVideoSizeChanged");
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {
        Log.e(TAG, "onRenderedFirstFrame");

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {
        Log.e(TAG, "onVideoDisabled");

    }


    //Implements AdaptiveMediaSourceEventListener
    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {
    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {

    }

    @Override
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {

    }


    //Implements ExtractorMediaSource.EventListener
    @Override
    public void onLoadError(IOException error) {
        Log.e(TAG, "onLoadError");

    }



    //Implements DefaultDrmSessionManager.EventListener
    @Override
    public void onDrmKeysLoaded() {

    }

    @Override
    public void onDrmSessionManagerError(Exception e) {

    }

    @Override
    public void onDrmKeysRestored() {

    }

    @Override
    public void onDrmKeysRemoved() {

    }


    //Implements MetadataRenderer.Output
    @Override
    public void onMetadata(Metadata metadata) {
        Log.e(TAG, "onMetadata");

    }
}
