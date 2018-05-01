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
    class C21021 implements Player.EventListener {
        public void onLoadingChanged(boolean z) {
        }

        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
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

        C21021() {
        }

        public void onPlayerStateChanged(boolean z, int i) {
            if (!VideoPlayer.this.audioPlayerReady && i == true) {
                VideoPlayer.this.audioPlayerReady = 1;
                VideoPlayer.this.checkPlayersReady();
            }
        }
    }

    public void onLoadingChanged(boolean z) {
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
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

    public VideoPlayer() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.playerDidStartPlaying && ((VideoPlayer) objArr[0]) != this && isPlaying() != 0) {
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
            this.audioPlayer.addListener(new C21021());
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    public void preparePlayerLoop(android.net.Uri r16, java.lang.String r17, android.net.Uri r18, java.lang.String r19) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r7_7 org.telegram.messenger.exoplayer2.source.MediaSource) in PHI: PHI: (r7_11 org.telegram.messenger.exoplayer2.source.MediaSource) = (r7_7 org.telegram.messenger.exoplayer2.source.MediaSource), (r7_8 org.telegram.messenger.exoplayer2.source.MediaSource), (r7_9 org.telegram.messenger.exoplayer2.source.MediaSource), (r7_10 org.telegram.messenger.exoplayer2.source.MediaSource) binds: {(r7_7 org.telegram.messenger.exoplayer2.source.MediaSource)=B:23:0x0052, (r7_8 org.telegram.messenger.exoplayer2.source.MediaSource)=B:24:0x0063, (r7_9 org.telegram.messenger.exoplayer2.source.MediaSource)=B:25:0x0076, (r7_10 org.telegram.messenger.exoplayer2.source.MediaSource)=B:26:0x0080}
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
        r4 = r2;
        r5 = r3;
        r6 = r5;
    L_0x0010:
        r7 = 2;
        if (r4 >= r7) goto L_0x00a0;
    L_0x0013:
        if (r4 != 0) goto L_0x001a;
    L_0x0015:
        r10 = r16;
        r8 = r17;
        goto L_0x001e;
    L_0x001a:
        r10 = r18;
        r8 = r19;
    L_0x001e:
        r9 = -1;
        r11 = r8.hashCode();
        r12 = 3680; // 0xe60 float:5.157E-42 double:1.818E-320;
        if (r11 == r12) goto L_0x0046;
    L_0x0027:
        r7 = 103407; // 0x193ef float:1.44904E-40 double:5.109E-319;
        if (r11 == r7) goto L_0x003c;
    L_0x002c:
        r7 = 3075986; // 0x2eef92 float:4.310374E-39 double:1.519739E-317;
        if (r11 == r7) goto L_0x0032;
    L_0x0031:
        goto L_0x004f;
    L_0x0032:
        r7 = "dash";
        r7 = r8.equals(r7);
        if (r7 == 0) goto L_0x004f;
    L_0x003a:
        r9 = r2;
        goto L_0x004f;
    L_0x003c:
        r7 = "hls";
        r7 = r8.equals(r7);
        if (r7 == 0) goto L_0x004f;
    L_0x0044:
        r9 = r1;
        goto L_0x004f;
    L_0x0046:
        r11 = "ss";
        r8 = r8.equals(r11);
        if (r8 == 0) goto L_0x004f;
    L_0x004e:
        r9 = r7;
    L_0x004f:
        switch(r9) {
            case 0: goto L_0x0080;
            case 1: goto L_0x0076;
            case 2: goto L_0x0063;
            default: goto L_0x0052;
        };
    L_0x0052:
        r7 = new org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
        r11 = r0.mediaDataSourceFactory;
        r12 = new org.telegram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
        r12.<init>();
        r13 = r0.mainHandler;
        r14 = 0;
        r9 = r7;
        r9.<init>(r10, r11, r12, r13, r14);
        goto L_0x0092;
    L_0x0063:
        r7 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource;
        r11 = r0.mediaDataSourceFactory;
        r12 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory;
        r8 = r0.mediaDataSourceFactory;
        r12.<init>(r8);
        r13 = r0.mainHandler;
        r14 = 0;
        r9 = r7;
        r9.<init>(r10, r11, r12, r13, r14);
        goto L_0x0092;
    L_0x0076:
        r7 = new org.telegram.messenger.exoplayer2.source.hls.HlsMediaSource;
        r8 = r0.mediaDataSourceFactory;
        r9 = r0.mainHandler;
        r7.<init>(r10, r8, r9, r3);
        goto L_0x0092;
    L_0x0080:
        r7 = new org.telegram.messenger.exoplayer2.source.dash.DashMediaSource;
        r11 = r0.mediaDataSourceFactory;
        r12 = new org.telegram.messenger.exoplayer2.source.dash.DefaultDashChunkSource$Factory;
        r8 = r0.mediaDataSourceFactory;
        r12.<init>(r8);
        r13 = r0.mainHandler;
        r14 = 0;
        r9 = r7;
        r9.<init>(r10, r11, r12, r13, r14);
    L_0x0092:
        r8 = new org.telegram.messenger.exoplayer2.source.LoopingMediaSource;
        r8.<init>(r7);
        if (r4 != 0) goto L_0x009b;
    L_0x0099:
        r5 = r8;
        goto L_0x009c;
    L_0x009b:
        r6 = r8;
    L_0x009c:
        r4 = r4 + 1;
        goto L_0x0010;
    L_0x00a0:
        r2 = r0.player;
        r2.prepare(r5, r1, r1);
        r2 = r0.audioPlayer;
        r2.prepare(r6, r1, r1);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayerLoop(android.net.Uri, java.lang.String, android.net.Uri, java.lang.String):void");
    }

    public void preparePlayer(android.net.Uri r12, java.lang.String r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r0_3 org.telegram.messenger.exoplayer2.source.MediaSource) in PHI: PHI: (r0_7 org.telegram.messenger.exoplayer2.source.MediaSource) = (r0_3 org.telegram.messenger.exoplayer2.source.MediaSource), (r0_4 org.telegram.messenger.exoplayer2.source.MediaSource), (r0_5 org.telegram.messenger.exoplayer2.source.MediaSource), (r0_6 org.telegram.messenger.exoplayer2.source.MediaSource) binds: {(r0_3 org.telegram.messenger.exoplayer2.source.MediaSource)=B:24:0x0051, (r0_4 org.telegram.messenger.exoplayer2.source.MediaSource)=B:25:0x0063, (r0_5 org.telegram.messenger.exoplayer2.source.MediaSource)=B:26:0x0077, (r0_6 org.telegram.messenger.exoplayer2.source.MediaSource)=B:27:0x0082}
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
        r11 = this;
        r1 = 0;
        r11.videoPlayerReady = r1;
        r11.mixedAudio = r1;
        r2 = r12.getScheme();
        r3 = 1;
        if (r2 == 0) goto L_0x0016;
    L_0x000c:
        r4 = "file";
        r2 = r2.startsWith(r4);
        if (r2 != 0) goto L_0x0016;
    L_0x0014:
        r2 = r3;
        goto L_0x0017;
    L_0x0016:
        r2 = r1;
    L_0x0017:
        r11.isStreaming = r2;
        r11.ensurePleyaerCreated();
        r2 = -1;
        r4 = r13.hashCode();
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
        r0 = r13.equals(r4);
        if (r0 == 0) goto L_0x004d;
    L_0x0038:
        goto L_0x004e;
    L_0x0039:
        r1 = "hls";
        r0 = r13.equals(r1);
        if (r0 == 0) goto L_0x004d;
    L_0x0041:
        r1 = r3;
        goto L_0x004e;
    L_0x0043:
        r1 = "ss";
        r0 = r13.equals(r1);
        if (r0 == 0) goto L_0x004d;
    L_0x004b:
        r1 = 2;
        goto L_0x004e;
    L_0x004d:
        r1 = r2;
    L_0x004e:
        switch(r1) {
            case 0: goto L_0x0082;
            case 1: goto L_0x0077;
            case 2: goto L_0x0063;
            default: goto L_0x0051;
        };
    L_0x0051:
        r0 = new org.telegram.messenger.exoplayer2.source.ExtractorMediaSource;
        r7 = r11.mediaDataSourceFactory;
        r8 = new org.telegram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
        r8.<init>();
        r9 = r11.mainHandler;
        r10 = 0;
        r5 = r0;
        r6 = r12;
        r5.<init>(r6, r7, r8, r9, r10);
        goto L_0x0095;
    L_0x0063:
        r0 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource;
        r6 = r11.mediaDataSourceFactory;
        r7 = new org.telegram.messenger.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory;
        r1 = r11.mediaDataSourceFactory;
        r7.<init>(r1);
        r8 = r11.mainHandler;
        r9 = 0;
        r4 = r0;
        r5 = r12;
        r4.<init>(r5, r6, r7, r8, r9);
        goto L_0x0095;
    L_0x0077:
        r0 = new org.telegram.messenger.exoplayer2.source.hls.HlsMediaSource;
        r1 = r11.mediaDataSourceFactory;
        r2 = r11.mainHandler;
        r4 = 0;
        r0.<init>(r12, r1, r2, r4);
        goto L_0x0095;
    L_0x0082:
        r0 = new org.telegram.messenger.exoplayer2.source.dash.DashMediaSource;
        r7 = r11.mediaDataSourceFactory;
        r8 = new org.telegram.messenger.exoplayer2.source.dash.DefaultDashChunkSource$Factory;
        r1 = r11.mediaDataSourceFactory;
        r8.<init>(r1);
        r9 = r11.mainHandler;
        r10 = 0;
        r5 = r0;
        r6 = r12;
        r5.<init>(r6, r7, r8, r9, r10);
    L_0x0095:
        r1 = r11.player;
        r1.prepare(r0, r3, r3);
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

    public void setTextureView(TextureView textureView) {
        if (this.textureView != textureView) {
            this.textureView = textureView;
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

    public void setPlayWhenReady(boolean z) {
        this.mixedPlayWhenReady = z;
        if (z && this.mixedAudio && (!this.audioPlayerReady || !this.videoPlayerReady)) {
            if (this.player) {
                this.player.setPlayWhenReady(false);
            }
            if (this.audioPlayer) {
                this.audioPlayer.setPlayWhenReady(false);
            }
            return;
        }
        this.autoplay = z;
        if (this.player != null) {
            this.player.setPlayWhenReady(z);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setPlayWhenReady(z);
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

    public void setMute(boolean z) {
        float f = 1.0f;
        if (this.player != null) {
            this.player.setVolume(z ? 0.0f : 1.0f);
        }
        if (this.audioPlayer != null) {
            SimpleExoPlayer simpleExoPlayer = this.audioPlayer;
            if (z) {
                f = 0.0f;
            }
            simpleExoPlayer.setVolume(f);
        }
    }

    public void setVolume(float f) {
        if (this.player != null) {
            this.player.setVolume(f);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setVolume(f);
        }
    }

    public void seekTo(long j) {
        if (this.player != null) {
            this.player.seekTo(j);
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

    public void setStreamType(int i) {
        if (this.player != null) {
            this.player.setAudioStreamType(i);
        }
        if (this.audioPlayer != null) {
            this.audioPlayer.setAudioStreamType(i);
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
        boolean playWhenReady = this.player.getPlayWhenReady();
        int playbackState = this.player.getPlaybackState();
        if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
            this.delegate.onStateChanged(playWhenReady, playbackState);
            this.lastReportedPlayWhenReady = playWhenReady;
            this.lastReportedPlaybackState = playbackState;
        }
    }
}
