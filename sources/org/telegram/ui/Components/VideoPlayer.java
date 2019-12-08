package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.view.TextureView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.Player.EventListener.-CC;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer.VideoListener;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;

@SuppressLint({"NewApi"})
public class VideoPlayer implements EventListener, VideoListener, NotificationCenterDelegate {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private SimpleExoPlayer audioPlayer;
    private boolean audioPlayerReady;
    private boolean autoplay;
    private Uri currentUri;
    private VideoPlayerDelegate delegate;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState = 1;
    private Handler mainHandler = new Handler();
    private Factory mediaDataSourceFactory;
    private boolean mixedAudio;
    private boolean mixedPlayWhenReady;
    private SimpleExoPlayer player;
    private TextureView textureView;
    private MappingTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
    private boolean videoPlayerReady;

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

    public /* synthetic */ void onIsPlayingChanged(boolean z) {
        -CC.$default$onIsPlayingChanged(this, z);
    }

    public void onLoadingChanged(boolean z) {
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
        -CC.$default$onPlaybackSuppressionReasonChanged(this, i);
    }

    public void onPositionDiscontinuity(int i) {
    }

    public void onRepeatModeChanged(int i) {
    }

    public void onSeekProcessed() {
    }

    public void onShuffleModeEnabledChanged(boolean z) {
    }

    public void onSurfaceSizeChanged(int i, int i2) {
    }

    public void onTimelineChanged(Timeline timeline, Object obj, int i) {
    }

    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
    }

    public VideoPlayer() {
        Context context = ApplicationLoader.applicationContext;
        TransferListener transferListener = BANDWIDTH_METER;
        this.mediaDataSourceFactory = new ExtendedDefaultDataSourceFactory(context, transferListener, new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", transferListener));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.playerDidStartPlaying && ((VideoPlayer) objArr[0]) != this && isPlaying()) {
            pause();
        }
    }

    private void ensurePleyaerCreated() {
        LoadControl defaultLoadControl = new DefaultLoadControl(new DefaultAllocator(true, 65536), 15000, 50000, 100, 5000, -1, true);
        if (this.player == null) {
            this.player = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, defaultLoadControl, null, 2);
            this.player.addListener(this);
            this.player.setVideoListener(this);
            this.player.setVideoTextureView(this.textureView);
            this.player.setPlayWhenReady(this.autoplay);
        }
        if (this.mixedAudio && this.audioPlayer == null) {
            this.audioPlayer = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, defaultLoadControl, null, 2);
            this.audioPlayer.addListener(new EventListener() {
                public /* synthetic */ void onIsPlayingChanged(boolean z) {
                    -CC.$default$onIsPlayingChanged(this, z);
                }

                public void onLoadingChanged(boolean z) {
                }

                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }

                public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
                    -CC.$default$onPlaybackSuppressionReasonChanged(this, i);
                }

                public void onPlayerError(ExoPlaybackException exoPlaybackException) {
                }

                public void onPositionDiscontinuity(int i) {
                }

                public void onRepeatModeChanged(int i) {
                }

                public void onSeekProcessed() {
                }

                public void onShuffleModeEnabledChanged(boolean z) {
                }

                public void onTimelineChanged(Timeline timeline, Object obj, int i) {
                }

                public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
                }

                public void onPlayerStateChanged(boolean z, int i) {
                    if (!VideoPlayer.this.audioPlayerReady && i == 3) {
                        VideoPlayer.this.audioPlayerReady = true;
                        VideoPlayer.this.checkPlayersReady();
                    }
                }
            });
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    public void preparePlayerLoop(Uri uri, String str, Uri uri2, String str2) {
        this.mixedAudio = true;
        this.audioPlayerReady = false;
        this.videoPlayerReady = false;
        ensurePleyaerCreated();
        MediaSource mediaSource = null;
        MediaSource mediaSource2 = mediaSource;
        for (int i = 0; i < 2; i++) {
            Uri uri3;
            String str3;
            MediaSource createMediaSource;
            if (i == 0) {
                uri3 = uri;
                str3 = str;
            } else {
                uri3 = uri2;
                str3 = str2;
            }
            Object obj = -1;
            int hashCode = str3.hashCode();
            if (hashCode != 3680) {
                if (hashCode != 103407) {
                    if (hashCode == 3075986 && str3.equals("dash")) {
                        obj = null;
                    }
                } else if (str3.equals("hls")) {
                    obj = 1;
                }
            } else if (str3.equals("ss")) {
                obj = 2;
            }
            Factory factory;
            MediaSource dashMediaSource;
            if (obj == null) {
                factory = this.mediaDataSourceFactory;
                dashMediaSource = new DashMediaSource(uri3, factory, new DefaultDashChunkSource.Factory(factory), this.mainHandler, null);
            } else if (obj == 1) {
                createMediaSource = new HlsMediaSource.Factory(this.mediaDataSourceFactory).createMediaSource(uri3);
            } else if (obj != 2) {
                dashMediaSource = new ExtractorMediaSource(uri3, this.mediaDataSourceFactory, new DefaultExtractorsFactory(), this.mainHandler, null);
            } else {
                factory = this.mediaDataSourceFactory;
                dashMediaSource = new SsMediaSource(uri3, factory, new DefaultSsChunkSource.Factory(factory), this.mainHandler, null);
            }
            LoopingMediaSource loopingMediaSource = new LoopingMediaSource(createMediaSource);
            if (i == 0) {
                mediaSource = loopingMediaSource;
            } else {
                mediaSource2 = loopingMediaSource;
            }
        }
        this.player.prepare(mediaSource, true, true);
        this.audioPlayer.prepare(mediaSource2, true, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0087  */
    /* JADX WARNING: Missing block: B:13:0x0039, code skipped:
            if (r10.equals("dash") == false) goto L_0x0050;
     */
    public void preparePlayer(android.net.Uri r9, java.lang.String r10) {
        /*
        r8 = this;
        r2 = 0;
        r8.videoPlayerReady = r2;
        r8.mixedAudio = r2;
        r8.currentUri = r9;
        r3 = r9.getScheme();
        r6 = 1;
        if (r3 == 0) goto L_0x0018;
    L_0x000e:
        r4 = "file";
        r3 = r3.startsWith(r4);
        if (r3 != 0) goto L_0x0018;
    L_0x0016:
        r3 = 1;
        goto L_0x0019;
    L_0x0018:
        r3 = 0;
    L_0x0019:
        r8.isStreaming = r3;
        r8.ensurePleyaerCreated();
        r3 = -1;
        r4 = r10.hashCode();
        r5 = 3680; // 0xe60 float:5.157E-42 double:1.818E-320;
        r7 = 2;
        if (r4 == r5) goto L_0x0046;
    L_0x0028:
        r5 = 103407; // 0x193ef float:1.44904E-40 double:5.109E-319;
        if (r4 == r5) goto L_0x003c;
    L_0x002d:
        r5 = 3075986; // 0x2eevar_ float:4.310374E-39 double:1.519739E-317;
        if (r4 == r5) goto L_0x0033;
    L_0x0032:
        goto L_0x0050;
    L_0x0033:
        r4 = "dash";
        r0 = r10.equals(r4);
        if (r0 == 0) goto L_0x0050;
    L_0x003b:
        goto L_0x0051;
    L_0x003c:
        r2 = "hls";
        r0 = r10.equals(r2);
        if (r0 == 0) goto L_0x0050;
    L_0x0044:
        r2 = 1;
        goto L_0x0051;
    L_0x0046:
        r2 = "ss";
        r0 = r10.equals(r2);
        if (r0 == 0) goto L_0x0050;
    L_0x004e:
        r2 = 2;
        goto L_0x0051;
    L_0x0050:
        r2 = -1;
    L_0x0051:
        if (r2 == 0) goto L_0x0087;
    L_0x0053:
        if (r2 == r6) goto L_0x007b;
    L_0x0055:
        if (r2 == r7) goto L_0x0069;
    L_0x0057:
        r7 = new com.google.android.exoplayer2.source.ExtractorMediaSource;
        r2 = r8.mediaDataSourceFactory;
        r3 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
        r3.<init>();
        r4 = r8.mainHandler;
        r5 = 0;
        r0 = r7;
        r1 = r9;
        r0.<init>(r1, r2, r3, r4, r5);
        goto L_0x0098;
    L_0x0069:
        r7 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
        r2 = r8.mediaDataSourceFactory;
        r3 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory;
        r3.<init>(r2);
        r4 = r8.mainHandler;
        r5 = 0;
        r0 = r7;
        r1 = r9;
        r0.<init>(r1, r2, r3, r4, r5);
        goto L_0x0098;
    L_0x007b:
        r0 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory;
        r2 = r8.mediaDataSourceFactory;
        r0.<init>(r2);
        r7 = r0.createMediaSource(r9);
        goto L_0x0098;
    L_0x0087:
        r7 = new com.google.android.exoplayer2.source.dash.DashMediaSource;
        r2 = r8.mediaDataSourceFactory;
        r3 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory;
        r3.<init>(r2);
        r4 = r8.mainHandler;
        r5 = 0;
        r0 = r7;
        r1 = r9;
        r0.<init>(r1, r2, r3, r4, r5);
    L_0x0098:
        r0 = r8.player;
        r0.prepare(r7, r6, r6);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayer(android.net.Uri, java.lang.String):void");
    }

    public boolean isPlayerPrepared() {
        return this.player != null;
    }

    public void releasePlayer(boolean z) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release(z);
            this.player = null;
        }
        simpleExoPlayer = this.audioPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release(z);
            this.audioPlayer = null;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void setTextureView(TextureView textureView) {
        if (this.textureView != textureView) {
            this.textureView = textureView;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setVideoTextureView(this.textureView);
            }
        }
    }

    public boolean getPlayWhenReady() {
        return this.player.getPlayWhenReady();
    }

    public int getPlaybackState() {
        return this.player.getPlaybackState();
    }

    public Uri getCurrentUri() {
        return this.currentUri;
    }

    public void play() {
        this.mixedPlayWhenReady = true;
        if (!this.mixedAudio || (this.audioPlayerReady && this.videoPlayerReady)) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(true);
            }
            simpleExoPlayer = this.audioPlayer;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(true);
            }
            return;
        }
        SimpleExoPlayer simpleExoPlayer2 = this.player;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(false);
        }
        simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(false);
        }
    }

    public void pause() {
        this.mixedPlayWhenReady = false;
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
        simpleExoPlayer = this.audioPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    public void setPlaybackSpeed(float f) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            float f2 = 1.0f;
            if (f > 1.0f) {
                f2 = 0.98f;
            }
            simpleExoPlayer.setPlaybackParameters(new PlaybackParameters(f, f2));
        }
    }

    public void setPlayWhenReady(boolean z) {
        this.mixedPlayWhenReady = z;
        if (z && this.mixedAudio && (!this.audioPlayerReady || !this.videoPlayerReady)) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
            }
            simpleExoPlayer = this.audioPlayer;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
            }
            return;
        }
        this.autoplay = z;
        SimpleExoPlayer simpleExoPlayer2 = this.player;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(z);
        }
        simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(z);
        }
    }

    public long getDuration() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        return simpleExoPlayer != null ? simpleExoPlayer.getDuration() : 0;
    }

    public long getCurrentPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        return simpleExoPlayer != null ? simpleExoPlayer.getCurrentPosition() : 0;
    }

    public boolean isMuted() {
        return this.player.getVolume() == 0.0f;
    }

    public void setMute(boolean z) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        float f = 0.0f;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(z ? 0.0f : 1.0f);
        }
        simpleExoPlayer = this.audioPlayer;
        if (simpleExoPlayer != null) {
            if (!z) {
                f = 1.0f;
            }
            simpleExoPlayer.setVolume(f);
        }
    }

    public void setVolume(float f) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(f);
        }
        simpleExoPlayer = this.audioPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(f);
        }
    }

    public void seekTo(long j) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(j);
        }
    }

    public void setDelegate(VideoPlayerDelegate videoPlayerDelegate) {
        this.delegate = videoPlayerDelegate;
    }

    public int getBufferedPercentage() {
        if (!this.isStreaming) {
            return 100;
        }
        SimpleExoPlayer simpleExoPlayer = this.player;
        return simpleExoPlayer != null ? simpleExoPlayer.getBufferedPercentage() : 0;
    }

    public long getBufferedPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return this.isStreaming ? simpleExoPlayer.getBufferedPosition() : simpleExoPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public boolean isStreaming() {
        return this.isStreaming;
    }

    public boolean isPlaying() {
        if (!(this.mixedAudio && this.mixedPlayWhenReady)) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer == null || !simpleExoPlayer.getPlayWhenReady()) {
                return false;
            }
        }
        return true;
    }

    public boolean isBuffering() {
        return this.player != null && this.lastReportedPlaybackState == 2;
    }

    public void setStreamType(int i) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setAudioStreamType(i);
        }
        simpleExoPlayer = this.audioPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setAudioStreamType(i);
        }
    }

    private void checkPlayersReady() {
        if (this.audioPlayerReady && this.videoPlayerReady && this.mixedPlayWhenReady) {
            play();
        }
    }

    public void onPlayerStateChanged(boolean z, int i) {
        maybeReportPlayerState();
        if (z && i == 3) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.playerDidStartPlaying, this);
        }
        if (!this.videoPlayerReady && i == 3) {
            this.videoPlayerReady = true;
            checkPlayersReady();
        }
    }

    public void onPlayerError(ExoPlaybackException exoPlaybackException) {
        this.delegate.onError(exoPlaybackException);
    }

    public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        this.delegate.onVideoSizeChanged(i, i2, i3, f);
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
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
            int playbackState = this.player.getPlaybackState();
            if (!(this.lastReportedPlayWhenReady == playWhenReady && this.lastReportedPlaybackState == playbackState)) {
                this.delegate.onStateChanged(playWhenReady, playbackState);
                this.lastReportedPlayWhenReady = playWhenReady;
                this.lastReportedPlaybackState = playbackState;
            }
        }
    }
}
