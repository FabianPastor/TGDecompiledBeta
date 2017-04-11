package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.view.TextureView;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.ExoPlayer.EventListener;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.SimpleExoPlayer;
import org.telegram.messenger.exoplayer2.SimpleExoPlayer.VideoListener;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.telegram.messenger.exoplayer2.source.LoopingMediaSource;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.MergingMediaSource;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.dash.DashMediaSource;
import org.telegram.messenger.exoplayer2.source.dash.DefaultDashChunkSource;
import org.telegram.messenger.exoplayer2.source.hls.HlsMediaSource;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.DefaultTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DefaultBandwidthMeter;
import org.telegram.messenger.exoplayer2.upstream.DefaultDataSourceFactory;
import org.telegram.messenger.exoplayer2.upstream.DefaultHttpDataSourceFactory;

@SuppressLint({"NewApi"})
public class VideoPlayer implements EventListener, VideoListener {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private boolean autoplay;
    private VideoPlayerDelegate delegate;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState = 1;
    private Handler mainHandler = new Handler();
    private Factory mediaDataSourceFactory = new DefaultDataSourceFactory(ApplicationLoader.applicationContext, BANDWIDTH_METER, new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", BANDWIDTH_METER));
    private SimpleExoPlayer player;
    private TextureView textureView;
    private MappingTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));

    public interface RendererBuilder {
        void buildRenderers(VideoPlayer videoPlayer);

        void cancel();
    }

    public interface VideoPlayerDelegate {
        void onError(Exception exception);

        void onRenderedFirstFrame();

        void onStateChanged(boolean z, int i);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    private void ensurePleyaerCreated() {
        if (this.player == null) {
            this.player = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, new DefaultLoadControl(), null, 0);
            this.player.addListener(this);
            this.player.setVideoListener(this);
            this.player.setVideoTextureView(this.textureView);
            this.player.setPlayWhenReady(this.autoplay);
        }
    }

    public void preparePlayerLoop(Uri videoUri, String videoType, Uri audioUri, String audioType) {
        ensurePleyaerCreated();
        MediaSource mediaSource1 = null;
        MediaSource mediaSource2 = null;
        for (int a = 0; a < 2; a++) {
            String type;
            Uri uri;
            MediaSource mediaSource;
            if (a == 0) {
                type = videoType;
                uri = videoUri;
            } else {
                type = audioType;
                uri = audioUri;
            }
            Object obj = -1;
            switch (type.hashCode()) {
                case 3680:
                    if (type.equals("ss")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 103407:
                    if (type.equals("hls")) {
                        obj = 1;
                        break;
                    }
                    break;
                case 3075986:
                    if (type.equals("dash")) {
                        obj = null;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    mediaSource = new DashMediaSource(uri, this.mediaDataSourceFactory, new DefaultDashChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
                    break;
                case 1:
                    mediaSource = new HlsMediaSource(uri, this.mediaDataSourceFactory, this.mainHandler, null);
                    break;
                case 2:
                    mediaSource = new SsMediaSource(uri, this.mediaDataSourceFactory, new DefaultSsChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
                    break;
                default:
                    mediaSource = new ExtractorMediaSource(uri, this.mediaDataSourceFactory, new DefaultExtractorsFactory(), this.mainHandler, null);
                    break;
            }
            MediaSource mediaSource3 = new LoopingMediaSource(mediaSource);
            if (a == 0) {
                mediaSource1 = mediaSource3;
            } else {
                mediaSource2 = mediaSource3;
            }
        }
        MergingMediaSource mergingMediaSource = new MergingMediaSource(mediaSource1, mediaSource2);
        this.player.prepare(mediaSource1, true, true);
    }

    public void preparePlayer(Uri uri, String type) {
        MediaSource mediaSource;
        ensurePleyaerCreated();
        boolean z = true;
        switch (type.hashCode()) {
            case 3680:
                if (type.equals("ss")) {
                    z = true;
                    break;
                }
                break;
            case 103407:
                if (type.equals("hls")) {
                    z = true;
                    break;
                }
                break;
            case 3075986:
                if (type.equals("dash")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                mediaSource = new DashMediaSource(uri, this.mediaDataSourceFactory, new DefaultDashChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
                break;
            case true:
                mediaSource = new HlsMediaSource(uri, this.mediaDataSourceFactory, this.mainHandler, null);
                break;
            case true:
                mediaSource = new SsMediaSource(uri, this.mediaDataSourceFactory, new DefaultSsChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
                break;
            default:
                mediaSource = new ExtractorMediaSource(uri, this.mediaDataSourceFactory, new DefaultExtractorsFactory(), this.mainHandler, null);
                break;
        }
        this.player.prepare(mediaSource, true, true);
    }

    public boolean isPlayerPrepared() {
        return this.player != null;
    }

    public void releasePlayer() {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
    }

    public void setTextureView(TextureView texture) {
        this.textureView = texture;
        if (this.player != null) {
            this.player.setVideoTextureView(this.textureView);
        }
    }

    public void play() {
        if (this.player != null) {
            this.player.setPlayWhenReady(true);
        }
    }

    public void pause() {
        if (this.player != null) {
            this.player.setPlayWhenReady(false);
        }
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.autoplay = playWhenReady;
        if (this.player != null) {
            this.player.setPlayWhenReady(playWhenReady);
        }
    }

    public long getDuration() {
        return this.player != null ? this.player.getDuration() : 0;
    }

    public long getCurrentPosition() {
        return this.player != null ? this.player.getCurrentPosition() : 0;
    }

    public void setMute(boolean value) {
        if (this.player != null) {
            if (value) {
                this.player.setVolume(0.0f);
            } else {
                this.player.setVolume(1.0f);
            }
        }
    }

    public void setVolume(float volume) {
        if (this.player != null) {
            this.player.setVolume(volume);
        }
    }

    public void seekTo(long positionMs) {
        if (this.player != null) {
            this.player.seekTo(positionMs);
        }
    }

    public void setDelegate(VideoPlayerDelegate videoPlayerDelegate) {
        this.delegate = videoPlayerDelegate;
    }

    public int getBufferedPercentage() {
        return this.player != null ? this.player.getBufferedPercentage() : 0;
    }

    public long getBufferedPosition() {
        return this.player != null ? this.player.getBufferedPosition() : 0;
    }

    public boolean isPlaying() {
        return this.player != null && this.player.getPlayWhenReady();
    }

    public boolean isBuffering() {
        return this.player != null && this.lastReportedPlaybackState == 2;
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

    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        this.delegate.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
    }

    public void onRenderedFirstFrame() {
        this.delegate.onRenderedFirstFrame();
    }

    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        return this.delegate.onSurfaceDestroyed(surfaceTexture);
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.delegate.onSurfaceTextureUpdated(surfaceTexture);
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
