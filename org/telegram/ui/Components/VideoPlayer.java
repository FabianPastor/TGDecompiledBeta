package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Handler;
import android.view.TextureView;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.ExoPlayer.EventListener;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.SimpleExoPlayer;
import org.telegram.messenger.exoplayer2.SimpleExoPlayer.VideoListener;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.dash.DashMediaSource;
import org.telegram.messenger.exoplayer2.source.dash.DefaultDashChunkSource;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.DefaultTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelections;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DefaultBandwidthMeter;
import org.telegram.messenger.exoplayer2.upstream.DefaultDataSourceFactory;
import org.telegram.messenger.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.volley.DefaultRetryPolicy;

@SuppressLint({"NewApi"})
public class VideoPlayer implements EventListener, TrackSelector.EventListener<MappedTrackInfo>, VideoListener {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private VideoPlayerDelegate delegate;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private Handler mainHandler = new Handler();
    private Factory mediaDataSourceFactory = new DefaultDataSourceFactory(ApplicationLoader.applicationContext, BANDWIDTH_METER, new DefaultHttpDataSourceFactory(Util.getUserAgent(ApplicationLoader.applicationContext, "Telegram"), BANDWIDTH_METER));
    private SimpleExoPlayer player;
    private MappingTrackSelector trackSelector = new DefaultTrackSelector(this.mainHandler, new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER));

    public interface RendererBuilder {
        void buildRenderers(VideoPlayer videoPlayer);

        void cancel();
    }

    public interface VideoPlayerDelegate {
        void onError(Exception exception);

        void onRenderedFirstFrame();

        void onStateChanged(boolean z, int i);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    public VideoPlayer() {
        this.trackSelector.addListener(this);
        this.player = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, new DefaultLoadControl(), null, false);
        this.player.addListener(this);
        this.player.setVideoListener(this);
        this.lastReportedPlaybackState = 1;
    }

    public void preparePlayer(Uri uri) {
        MediaSource mediaSource;
        FileLog.e("tmessages", "open url " + uri);
        if (uri.getScheme().startsWith("http")) {
            mediaSource = new DashMediaSource(uri, this.mediaDataSourceFactory, new DefaultDashChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
        } else {
            mediaSource = new ExtractorMediaSource(uri, this.mediaDataSourceFactory, new DefaultExtractorsFactory(), this.mainHandler, null);
        }
        this.player.prepare(mediaSource, true, true);
    }

    public void releasePlayer() {
        if (this.player != null) {
            this.player.release();
            this.player = null;
            this.trackSelector = null;
        }
    }

    public void setTextureView(TextureView textureView) {
        this.player.setVideoTextureView(textureView);
    }

    public void play() {
        this.player.setPlayWhenReady(true);
    }

    public void pause() {
        this.player.setPlayWhenReady(false);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.player.setPlayWhenReady(playWhenReady);
    }

    public long getDuration() {
        return this.player.getDuration();
    }

    public long getCurrentPosition() {
        return this.player.getCurrentPosition();
    }

    public void setMute(boolean value) {
        if (value) {
            this.player.setVolume(0.0f);
        } else {
            this.player.setVolume(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
    }

    public void seekTo(long positionMs) {
        this.player.seekTo(positionMs);
    }

    public void setDelegate(VideoPlayerDelegate videoPlayerDelegate) {
        this.delegate = videoPlayerDelegate;
    }

    public int getBufferedPercentage() {
        return this.player.getBufferedPercentage();
    }

    public long getBufferedPosition() {
        return this.player.getBufferedPosition();
    }

    public boolean isPlaying() {
        return this.player.getPlayWhenReady();
    }

    public void onLoadingChanged(boolean isLoading) {
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
    }

    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    public void onPlayerError(ExoPlaybackException error) {
        this.delegate.onError(error);
    }

    public void onPositionDiscontinuity() {
    }

    public void onTrackSelectionsChanged(TrackSelections<? extends MappedTrackInfo> trackSelections) {
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        this.delegate.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
    }

    public void onRenderedFirstFrame() {
        this.delegate.onRenderedFirstFrame();
    }

    public void onVideoTracksDisabled() {
    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = this.player.getPlayWhenReady();
        int playbackState = this.player.getPlaybackState();
        if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
            this.delegate.onStateChanged(playWhenReady, playbackState);
            this.lastReportedPlayWhenReady = playWhenReady;
            this.lastReportedPlaybackState = playbackState;
        }
    }
}
