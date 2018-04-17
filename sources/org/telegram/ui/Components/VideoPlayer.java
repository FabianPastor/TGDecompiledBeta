package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.view.TextureView;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.ExoPlayer.EventListener;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.Player;
import org.telegram.messenger.exoplayer2.SimpleExoPlayer;
import org.telegram.messenger.exoplayer2.SimpleExoPlayer.VideoListener;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.DefaultTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.MappingTrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DefaultBandwidthMeter;
import org.telegram.messenger.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;

@SuppressLint({"NewApi"})
public class VideoPlayer implements NotificationCenterDelegate, EventListener, VideoListener {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private SimpleExoPlayer audioPlayer;
    private boolean audioPlayerReady;
    private boolean autoplay;
    private VideoPlayerDelegate delegate;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState = 1;
    private Handler mainHandler = new Handler();
    private Factory mediaDataSourceFactory = new ExtendedDefaultDataSourceFactory(ApplicationLoader.applicationContext, BANDWIDTH_METER, new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", BANDWIDTH_METER));
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

    /* renamed from: org.telegram.ui.Components.VideoPlayer$1 */
    class C20961 implements Player.EventListener {
        C20961() {
        }

        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }

        public void onLoadingChanged(boolean isLoading) {
        }

        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        }

        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        public void onPositionDiscontinuity(int reason) {
        }

        public void onSeekProcessed() {
        }

        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (!VideoPlayer.this.audioPlayerReady && playbackState == 3) {
                VideoPlayer.this.audioPlayerReady = true;
                VideoPlayer.this.checkPlayersReady();
            }
        }

        public void onRepeatModeChanged(int repeatMode) {
        }

        public void onPlayerError(ExoPlaybackException error) {
        }

        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }
    }

    public VideoPlayer() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.playerDidStartPlaying && args[0] != this && isPlaying()) {
            pause();
        }
    }

    private void ensurePleyaerCreated() {
        if (this.player == null) {
            this.player = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, new DefaultLoadControl(), null, 2);
            this.player.addListener(this);
            this.player.setVideoListener(this);
            this.player.setVideoTextureView(this.textureView);
            this.player.setPlayWhenReady(this.autoplay);
        }
        if (this.mixedAudio && this.audioPlayer == null) {
            this.audioPlayer = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, new DefaultLoadControl(), null, 2);
            this.audioPlayer.addListener(new C20961());
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    public void preparePlayerLoop(android.net.Uri r16, java.lang.String r17, android.net.Uri r18, java.lang.String r19) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r6_7 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource) in PHI: PHI: (r6_11 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource) = (r6_7 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource), (r6_8 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource), (r6_9 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource), (r6_10 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource) binds: {(r6_7 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:23:0x0053, (r6_8 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:24:0x0065, (r6_9 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:25:0x0079, (r6_10 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:26:0x0084}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r15 = this;
        r0 = r15;
        r1 = 1;
        r0.mixedAudio = r1;
        r2 = 0;
        r0.audioPlayerReady = r2;
        r0.videoPlayerReady = r2;
        r0.ensurePleyaerCreated();
        r3 = 0;
        r4 = 0;
        r5 = r4;
        r4 = r3;
        r3 = r2;
    L_0x0011:
        r6 = 2;
        if (r3 >= r6) goto L_0x00a8;
    L_0x0014:
        if (r3 != 0) goto L_0x001b;
    L_0x0016:
        r7 = r17;
        r8 = r16;
        goto L_0x001f;
    L_0x001b:
        r7 = r19;
        r8 = r18;
    L_0x001f:
        r9 = -1;
        r10 = r7.hashCode();
        r11 = 3680; // 0xe60 float:5.157E-42 double:1.818E-320;
        if (r10 == r11) goto L_0x0047;
    L_0x0028:
        r6 = 103407; // 0x193ef float:1.44904E-40 double:5.109E-319;
        if (r10 == r6) goto L_0x003d;
    L_0x002d:
        r6 = 3075986; // 0x2eef92 float:4.310374E-39 double:1.519739E-317;
        if (r10 == r6) goto L_0x0033;
    L_0x0032:
        goto L_0x0050;
    L_0x0033:
        r6 = "dash";
        r6 = r7.equals(r6);
        if (r6 == 0) goto L_0x0050;
    L_0x003b:
        r9 = r2;
        goto L_0x0050;
    L_0x003d:
        r6 = "hls";
        r6 = r7.equals(r6);
        if (r6 == 0) goto L_0x0050;
    L_0x0045:
        r9 = r1;
        goto L_0x0050;
    L_0x0047:
        r10 = "ss";
        r10 = r7.equals(r10);
        if (r10 == 0) goto L_0x0050;
    L_0x004f:
        r9 = r6;
    L_0x0050:
        switch(r9) {
            case 0: goto L_0x0084;
            case 1: goto L_0x0079;
            case 2: goto L_0x0065;
            default: goto L_0x0053;
        };
    L_0x0053:
        r6 = new org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
        r11 = r0.mediaDataSourceFactory;
        r12 = new org.telegram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
        r12.<init>();
        r13 = r0.mainHandler;
        r14 = 0;
        r9 = r6;
        r10 = r8;
        r9.<init>(r10, r11, r12, r13, r14);
        goto L_0x0098;
    L_0x0065:
        r6 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource;
        r11 = r0.mediaDataSourceFactory;
        r12 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory;
        r9 = r0.mediaDataSourceFactory;
        r12.<init>(r9);
        r13 = r0.mainHandler;
        r14 = 0;
        r9 = r6;
        r10 = r8;
        r9.<init>(r10, r11, r12, r13, r14);
        goto L_0x0098;
    L_0x0079:
        r6 = new org.telegram.messenger.exoplayer2.source.hls.HlsMediaSource;
        r9 = r0.mediaDataSourceFactory;
        r10 = r0.mainHandler;
        r11 = 0;
        r6.<init>(r8, r9, r10, r11);
        goto L_0x0098;
    L_0x0084:
        r6 = new org.telegram.messenger.exoplayer2.source.dash.DashMediaSource;
        r11 = r0.mediaDataSourceFactory;
        r12 = new org.telegram.messenger.exoplayer2.source.dash.DefaultDashChunkSource$Factory;
        r9 = r0.mediaDataSourceFactory;
        r12.<init>(r9);
        r13 = r0.mainHandler;
        r14 = 0;
        r9 = r6;
        r10 = r8;
        r9.<init>(r10, r11, r12, r13, r14);
        r9 = new org.telegram.messenger.exoplayer2.source.LoopingMediaSource;
        r9.<init>(r6);
        r6 = r9;
        if (r3 != 0) goto L_0x00a3;
    L_0x00a1:
        r4 = r6;
        goto L_0x00a4;
    L_0x00a3:
        r5 = r6;
    L_0x00a4:
        r3 = r3 + 1;
        goto L_0x0011;
    L_0x00a8:
        r2 = r0.player;
        r2.prepare(r4, r1, r1);
        r2 = r0.audioPlayer;
        r2.prepare(r5, r1, r1);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayerLoop(android.net.Uri, java.lang.String, android.net.Uri, java.lang.String):void");
    }

    public void preparePlayer(android.net.Uri r10, java.lang.String r11) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r0_9 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource) in PHI: PHI: (r0_13 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource) = (r0_9 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource), (r0_10 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource), (r0_11 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource), (r0_12 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource) binds: {(r0_9 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:24:0x0051, (r0_10 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:25:0x0063, (r0_11 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:26:0x0077, (r0_12 'mediaSource' org.telegram.messenger.exoplayer2.source.MediaSource)=B:27:0x0082}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = this;
        r0 = 0;
        r9.videoPlayerReady = r0;
        r9.mixedAudio = r0;
        r1 = r10.getScheme();
        r2 = 1;
        if (r1 == 0) goto L_0x0016;
    L_0x000c:
        r3 = "file";
        r3 = r1.startsWith(r3);
        if (r3 != 0) goto L_0x0016;
    L_0x0014:
        r3 = r2;
        goto L_0x0017;
    L_0x0016:
        r3 = r0;
    L_0x0017:
        r9.isStreaming = r3;
        r9.ensurePleyaerCreated();
        r3 = -1;
        r4 = r11.hashCode();
        r5 = 3680; // 0xe60 float:5.157E-42 double:1.818E-320;
        if (r4 == r5) goto L_0x0043;
    L_0x0025:
        r5 = 103407; // 0x193ef float:1.44904E-40 double:5.109E-319;
        if (r4 == r5) goto L_0x0039;
    L_0x002a:
        r5 = 3075986; // 0x2eef92 float:4.310374E-39 double:1.519739E-317;
        if (r4 == r5) goto L_0x0030;
    L_0x002f:
        goto L_0x004d;
    L_0x0030:
        r4 = "dash";
        r4 = r11.equals(r4);
        if (r4 == 0) goto L_0x004d;
    L_0x0038:
        goto L_0x004e;
    L_0x0039:
        r0 = "hls";
        r0 = r11.equals(r0);
        if (r0 == 0) goto L_0x004d;
    L_0x0041:
        r0 = r2;
        goto L_0x004e;
    L_0x0043:
        r0 = "ss";
        r0 = r11.equals(r0);
        if (r0 == 0) goto L_0x004d;
    L_0x004b:
        r0 = 2;
        goto L_0x004e;
    L_0x004d:
        r0 = r3;
    L_0x004e:
        switch(r0) {
            case 0: goto L_0x0082;
            case 1: goto L_0x0077;
            case 2: goto L_0x0063;
            default: goto L_0x0051;
        };
    L_0x0051:
        r0 = new org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
        r5 = r9.mediaDataSourceFactory;
        r6 = new org.telegram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
        r6.<init>();
        r7 = r9.mainHandler;
        r8 = 0;
        r3 = r0;
        r4 = r10;
        r3.<init>(r4, r5, r6, r7, r8);
        goto L_0x0096;
    L_0x0063:
        r0 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource;
        r5 = r9.mediaDataSourceFactory;
        r6 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory;
        r3 = r9.mediaDataSourceFactory;
        r6.<init>(r3);
        r7 = r9.mainHandler;
        r8 = 0;
        r3 = r0;
        r4 = r10;
        r3.<init>(r4, r5, r6, r7, r8);
        goto L_0x0096;
    L_0x0077:
        r0 = new org.telegram.messenger.exoplayer2.source.hls.HlsMediaSource;
        r3 = r9.mediaDataSourceFactory;
        r4 = r9.mainHandler;
        r5 = 0;
        r0.<init>(r10, r3, r4, r5);
        goto L_0x0096;
    L_0x0082:
        r0 = new org.telegram.messenger.exoplayer2.source.dash.DashMediaSource;
        r5 = r9.mediaDataSourceFactory;
        r6 = new org.telegram.messenger.exoplayer2.source.dash.DefaultDashChunkSource$Factory;
        r3 = r9.mediaDataSourceFactory;
        r6.<init>(r3);
        r7 = r9.mainHandler;
        r8 = 0;
        r3 = r0;
        r4 = r10;
        r3.<init>(r4, r5, r6, r7, r8);
        r3 = r9.player;
        r3.prepare(r0, r2, r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayer(android.net.Uri, java.lang.String):void");
    }

    public boolean isPlayerPrepared() {
        return this.player != null;
    }

    public void releasePlayer() {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.release();
            this.audioPlayer = null;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void setTextureView(TextureView texture) {
        if (this.textureView != texture) {
            this.textureView = texture;
            if (this.player != null) {
                this.player.setVideoTextureView(this.textureView);
            }
        }
    }

    public void play() {
        this.mixedPlayWhenReady = true;
        if (!this.mixedAudio || (this.audioPlayerReady && this.videoPlayerReady)) {
            if (this.player != null) {
                this.player.setPlayWhenReady(true);
            }
            if (this.audioPlayer != null) {
                this.audioPlayer.setPlayWhenReady(true);
            }
            return;
        }
        if (this.player != null) {
            this.player.setPlayWhenReady(false);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setPlayWhenReady(false);
        }
    }

    public void pause() {
        this.mixedPlayWhenReady = false;
        if (this.player != null) {
            this.player.setPlayWhenReady(false);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setPlayWhenReady(false);
        }
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.mixedPlayWhenReady = playWhenReady;
        if (playWhenReady && this.mixedAudio && (!this.audioPlayerReady || !this.videoPlayerReady)) {
            if (this.player != null) {
                this.player.setPlayWhenReady(false);
            }
            if (this.audioPlayer != null) {
                this.audioPlayer.setPlayWhenReady(false);
            }
            return;
        }
        this.autoplay = playWhenReady;
        if (this.player != null) {
            this.player.setPlayWhenReady(playWhenReady);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    public long getDuration() {
        return this.player != null ? this.player.getDuration() : 0;
    }

    public long getCurrentPosition() {
        return this.player != null ? this.player.getCurrentPosition() : 0;
    }

    public boolean isMuted() {
        return this.player.getVolume() == 0.0f;
    }

    public void setMute(boolean value) {
        float f = 1.0f;
        if (this.player != null) {
            this.player.setVolume(value ? 0.0f : 1.0f);
        }
        if (this.audioPlayer != null) {
            SimpleExoPlayer simpleExoPlayer = this.audioPlayer;
            if (value) {
                f = 0.0f;
            }
            simpleExoPlayer.setVolume(f);
        }
    }

    public void onRepeatModeChanged(int repeatMode) {
    }

    public void setVolume(float volume) {
        if (this.player != null) {
            this.player.setVolume(volume);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setVolume(volume);
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
        if (this.isStreaming) {
            return this.player != null ? this.player.getBufferedPercentage() : 0;
        } else {
            return 100;
        }
    }

    public long getBufferedPosition() {
        if (this.player != null) {
            return this.isStreaming ? this.player.getBufferedPosition() : this.player.getDuration();
        } else {
            return 0;
        }
    }

    public boolean isStreaming() {
        return this.isStreaming;
    }

    public boolean isPlaying() {
        return (this.mixedAudio && this.mixedPlayWhenReady) || (this.player != null && this.player.getPlayWhenReady());
    }

    public boolean isBuffering() {
        return this.player != null && this.lastReportedPlaybackState == 2;
    }

    public void setStreamType(int type) {
        if (this.player != null) {
            this.player.setAudioStreamType(type);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setAudioStreamType(type);
        }
    }

    private void checkPlayersReady() {
        if (this.audioPlayerReady && this.videoPlayerReady && this.mixedPlayWhenReady) {
            play();
        }
    }

    public void onLoadingChanged(boolean isLoading) {
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
        if (playWhenReady && playbackState == 3) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.playerDidStartPlaying, this);
        }
        if (!this.videoPlayerReady && playbackState == 3) {
            this.videoPlayerReady = true;
            checkPlayersReady();
        }
    }

    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    public void onPositionDiscontinuity(int reason) {
    }

    public void onSeekProcessed() {
    }

    public void onPlayerError(ExoPlaybackException error) {
        this.delegate.onError(error);
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

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
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
