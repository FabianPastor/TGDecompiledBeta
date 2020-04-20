package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.view.TextureView;
import android.view.ViewGroup;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.video.SurfaceNotValidException;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;

@SuppressLint({"NewApi"})
public class VideoPlayer implements Player.EventListener, SimpleExoPlayer.VideoListener, NotificationCenter.NotificationCenterDelegate {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer audioPlayer;
    /* access modifiers changed from: private */
    public boolean audioPlayerReady;
    private String audioType;
    private Uri audioUri;
    private boolean autoplay;
    private Uri currentUri;
    private VideoPlayerDelegate delegate;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState = 1;
    private boolean loop;
    private Handler mainHandler = new Handler();
    private DataSource.Factory mediaDataSourceFactory;
    private boolean mixedAudio;
    private boolean mixedPlayWhenReady;
    private SimpleExoPlayer player;
    private TextureView textureView;
    private MappingTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
    private boolean videoPlayerReady;
    private String videoType;
    private Uri videoUri;

    public interface VideoPlayerDelegate {
        void onError(Exception exc);

        void onRenderedFirstFrame();

        void onStateChanged(boolean z, int i);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    public /* synthetic */ void onIsPlayingChanged(boolean z) {
        Player.EventListener.CC.$default$onIsPlayingChanged(this, z);
    }

    public void onLoadingChanged(boolean z) {
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
        Player.EventListener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
    }

    public void onPositionDiscontinuity(int i) {
    }

    public void onSeekProcessed() {
    }

    public void onSurfaceSizeChanged(int i, int i2) {
    }

    public void onTimelineChanged(Timeline timeline, Object obj, int i) {
    }

    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
    }

    public VideoPlayer() {
        Context context = ApplicationLoader.applicationContext;
        DefaultBandwidthMeter defaultBandwidthMeter = BANDWIDTH_METER;
        this.mediaDataSourceFactory = new ExtendedDefaultDataSourceFactory(context, (TransferListener) defaultBandwidthMeter, (DataSource.Factory) new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", defaultBandwidthMeter));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.playerDidStartPlaying && ((VideoPlayer) objArr[0]) != this && isPlaying()) {
            pause();
        }
    }

    private void ensurePleyaerCreated() {
        DefaultLoadControl defaultLoadControl = new DefaultLoadControl(new DefaultAllocator(true, 65536), 15000, 50000, 100, 5000, -1, true);
        if (this.player == null) {
            SimpleExoPlayer newSimpleInstance = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, (TrackSelector) this.trackSelector, (LoadControl) defaultLoadControl, (DrmSessionManager<FrameworkMediaCrypto>) null, 2);
            this.player = newSimpleInstance;
            newSimpleInstance.addListener(this);
            this.player.setVideoListener(this);
            this.player.setVideoTextureView(this.textureView);
            this.player.setPlayWhenReady(this.autoplay);
        }
        if (this.mixedAudio && this.audioPlayer == null) {
            SimpleExoPlayer newSimpleInstance2 = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, (TrackSelector) this.trackSelector, (LoadControl) defaultLoadControl, (DrmSessionManager<FrameworkMediaCrypto>) null, 2);
            this.audioPlayer = newSimpleInstance2;
            newSimpleInstance2.addListener(new Player.EventListener() {
                public /* synthetic */ void onIsPlayingChanged(boolean z) {
                    Player.EventListener.CC.$default$onIsPlayingChanged(this, z);
                }

                public void onLoadingChanged(boolean z) {
                }

                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }

                public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
                    Player.EventListener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
                }

                public void onPlayerError(ExoPlaybackException exoPlaybackException) {
                }

                public void onPositionDiscontinuity(int i) {
                }

                public void onSeekProcessed() {
                }

                public void onTimelineChanged(Timeline timeline, Object obj, int i) {
                }

                public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
                }

                public void onPlayerStateChanged(boolean z, int i) {
                    if (!VideoPlayer.this.audioPlayerReady && i == 3) {
                        boolean unused = VideoPlayer.this.audioPlayerReady = true;
                        VideoPlayer.this.checkPlayersReady();
                    }
                }
            });
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    /* JADX WARNING: type inference failed for: r10v1, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r12v8, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r10v5 */
    /* JADX WARNING: type inference failed for: r12v9, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r12v10, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayerLoop(android.net.Uri r19, java.lang.String r20, android.net.Uri r21, java.lang.String r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r0.videoUri = r1
            r2 = r21
            r0.audioUri = r2
            r3 = r20
            r0.videoType = r3
            r4 = r22
            r0.audioType = r4
            r5 = 1
            r0.loop = r5
            r0.mixedAudio = r5
            r6 = 0
            r0.audioPlayerReady = r6
            r0.videoPlayerReady = r6
            r18.ensurePleyaerCreated()
            r7 = 0
            r8 = r7
            r9 = 0
        L_0x0022:
            r10 = 2
            if (r9 >= r10) goto L_0x00b8
            if (r9 != 0) goto L_0x002a
            r13 = r1
            r11 = r3
            goto L_0x002c
        L_0x002a:
            r13 = r2
            r11 = r4
        L_0x002c:
            r12 = -1
            int r14 = r11.hashCode()
            r15 = 3680(0xe60, float:5.157E-42)
            if (r14 == r15) goto L_0x0054
            r15 = 103407(0x193ef, float:1.44904E-40)
            if (r14 == r15) goto L_0x004a
            r15 = 3075986(0x2eevar_, float:4.310374E-39)
            if (r14 == r15) goto L_0x0040
            goto L_0x005d
        L_0x0040:
            java.lang.String r14 = "dash"
            boolean r11 = r11.equals(r14)
            if (r11 == 0) goto L_0x005d
            r12 = 0
            goto L_0x005d
        L_0x004a:
            java.lang.String r14 = "hls"
            boolean r11 = r11.equals(r14)
            if (r11 == 0) goto L_0x005d
            r12 = 1
            goto L_0x005d
        L_0x0054:
            java.lang.String r14 = "ss"
            boolean r11 = r11.equals(r14)
            if (r11 == 0) goto L_0x005d
            r12 = 2
        L_0x005d:
            if (r12 == 0) goto L_0x0097
            if (r12 == r5) goto L_0x008b
            if (r12 == r10) goto L_0x0077
            com.google.android.exoplayer2.source.ExtractorMediaSource r10 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r14 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r15 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r15.<init>()
            android.os.Handler r11 = r0.mainHandler
            r17 = 0
            r12 = r10
            r16 = r11
            r12.<init>(r13, r14, r15, r16, r17)
            goto L_0x00aa
        L_0x0077:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r10 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r14 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r15 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            r15.<init>(r14)
            android.os.Handler r11 = r0.mainHandler
            r17 = 0
            r12 = r10
            r16 = r11
            r12.<init>(r13, r14, r15, r16, r17)
            goto L_0x00aa
        L_0x008b:
            com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory r10 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r11 = r0.mediaDataSourceFactory
            r10.<init>((com.google.android.exoplayer2.upstream.DataSource.Factory) r11)
            com.google.android.exoplayer2.source.hls.HlsMediaSource r10 = r10.createMediaSource(r13)
            goto L_0x00aa
        L_0x0097:
            com.google.android.exoplayer2.source.dash.DashMediaSource r10 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r14 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r15 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            r15.<init>(r14)
            android.os.Handler r11 = r0.mainHandler
            r17 = 0
            r12 = r10
            r16 = r11
            r12.<init>(r13, r14, r15, r16, r17)
        L_0x00aa:
            com.google.android.exoplayer2.source.LoopingMediaSource r11 = new com.google.android.exoplayer2.source.LoopingMediaSource
            r11.<init>(r10)
            if (r9 != 0) goto L_0x00b3
            r7 = r11
            goto L_0x00b4
        L_0x00b3:
            r8 = r11
        L_0x00b4:
            int r9 = r9 + 1
            goto L_0x0022
        L_0x00b8:
            com.google.android.exoplayer2.SimpleExoPlayer r1 = r0.player
            r1.prepare(r7, r5, r5)
            com.google.android.exoplayer2.SimpleExoPlayer r1 = r0.audioPlayer
            r1.prepare(r8, r5, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayerLoop(android.net.Uri, java.lang.String, android.net.Uri, java.lang.String):void");
    }

    /* JADX WARNING: type inference failed for: r7v1, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r0v8, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r7v4 */
    /* JADX WARNING: type inference failed for: r0v9, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r0v10, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0044, code lost:
        if (r10.equals("dash") == false) goto L_0x005b;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0092  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayer(android.net.Uri r9, java.lang.String r10) {
        /*
            r8 = this;
            r8.videoUri = r9
            r8.videoType = r10
            r2 = 0
            r8.audioUri = r2
            r8.audioType = r2
            r2 = 0
            r8.loop = r2
            r8.videoPlayerReady = r2
            r8.mixedAudio = r2
            r8.currentUri = r9
            java.lang.String r3 = r9.getScheme()
            r6 = 1
            if (r3 == 0) goto L_0x0023
            java.lang.String r4 = "file"
            boolean r3 = r3.startsWith(r4)
            if (r3 != 0) goto L_0x0023
            r3 = 1
            goto L_0x0024
        L_0x0023:
            r3 = 0
        L_0x0024:
            r8.isStreaming = r3
            r8.ensurePleyaerCreated()
            r3 = -1
            int r4 = r10.hashCode()
            r5 = 3680(0xe60, float:5.157E-42)
            r7 = 2
            if (r4 == r5) goto L_0x0051
            r5 = 103407(0x193ef, float:1.44904E-40)
            if (r4 == r5) goto L_0x0047
            r5 = 3075986(0x2eevar_, float:4.310374E-39)
            if (r4 == r5) goto L_0x003e
            goto L_0x005b
        L_0x003e:
            java.lang.String r4 = "dash"
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x005b
            goto L_0x005c
        L_0x0047:
            java.lang.String r2 = "hls"
            boolean r0 = r10.equals(r2)
            if (r0 == 0) goto L_0x005b
            r2 = 1
            goto L_0x005c
        L_0x0051:
            java.lang.String r2 = "ss"
            boolean r0 = r10.equals(r2)
            if (r0 == 0) goto L_0x005b
            r2 = 2
            goto L_0x005c
        L_0x005b:
            r2 = -1
        L_0x005c:
            if (r2 == 0) goto L_0x0092
            if (r2 == r6) goto L_0x0086
            if (r2 == r7) goto L_0x0074
            com.google.android.exoplayer2.source.ExtractorMediaSource r7 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r3 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r3.<init>()
            android.os.Handler r4 = r8.mainHandler
            r5 = 0
            r0 = r7
            r1 = r9
            r0.<init>(r1, r2, r3, r4, r5)
            goto L_0x00a3
        L_0x0074:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r7 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r3 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            r3.<init>(r2)
            android.os.Handler r4 = r8.mainHandler
            r5 = 0
            r0 = r7
            r1 = r9
            r0.<init>(r1, r2, r3, r4, r5)
            goto L_0x00a3
        L_0x0086:
            com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory r0 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            r0.<init>((com.google.android.exoplayer2.upstream.DataSource.Factory) r2)
            com.google.android.exoplayer2.source.hls.HlsMediaSource r7 = r0.createMediaSource(r9)
            goto L_0x00a3
        L_0x0092:
            com.google.android.exoplayer2.source.dash.DashMediaSource r7 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r3 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            r3.<init>(r2)
            android.os.Handler r4 = r8.mainHandler
            r5 = 0
            r0 = r7
            r1 = r9
            r0.<init>(r1, r2, r3, r4, r5)
        L_0x00a3:
            com.google.android.exoplayer2.SimpleExoPlayer r0 = r8.player
            r0.prepare(r7, r6, r6)
            return
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
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.release(z);
            this.audioPlayer = null;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void setTextureView(TextureView textureView2) {
        if (this.textureView != textureView2) {
            this.textureView = textureView2;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setVideoTextureView(textureView2);
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
            SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
            if (simpleExoPlayer2 != null) {
                simpleExoPlayer2.setPlayWhenReady(true);
                return;
            }
            return;
        }
        SimpleExoPlayer simpleExoPlayer3 = this.player;
        if (simpleExoPlayer3 != null) {
            simpleExoPlayer3.setPlayWhenReady(false);
        }
        SimpleExoPlayer simpleExoPlayer4 = this.audioPlayer;
        if (simpleExoPlayer4 != null) {
            simpleExoPlayer4.setPlayWhenReady(false);
        }
    }

    public void pause() {
        this.mixedPlayWhenReady = false;
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(false);
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
        if (!z || !this.mixedAudio || (this.audioPlayerReady && this.videoPlayerReady)) {
            this.autoplay = z;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(z);
            }
            SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
            if (simpleExoPlayer2 != null) {
                simpleExoPlayer2.setPlayWhenReady(z);
                return;
            }
            return;
        }
        SimpleExoPlayer simpleExoPlayer3 = this.player;
        if (simpleExoPlayer3 != null) {
            simpleExoPlayer3.setPlayWhenReady(false);
        }
        SimpleExoPlayer simpleExoPlayer4 = this.audioPlayer;
        if (simpleExoPlayer4 != null) {
            simpleExoPlayer4.setPlayWhenReady(false);
        }
    }

    public long getDuration() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getDuration();
        }
        return 0;
    }

    public long getCurrentPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getCurrentPosition();
        }
        return 0;
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
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            if (!z) {
                f = 1.0f;
            }
            simpleExoPlayer2.setVolume(f);
        }
    }

    public void setVolume(float f) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(f);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setVolume(f);
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

    public long getBufferedPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return this.isStreaming ? simpleExoPlayer.getBufferedPosition() : simpleExoPlayer.getDuration();
        }
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r1.player;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isPlaying() {
        /*
            r1 = this;
            boolean r0 = r1.mixedAudio
            if (r0 == 0) goto L_0x0008
            boolean r0 = r1.mixedPlayWhenReady
            if (r0 != 0) goto L_0x0012
        L_0x0008:
            com.google.android.exoplayer2.SimpleExoPlayer r0 = r1.player
            if (r0 == 0) goto L_0x0014
            boolean r0 = r0.getPlayWhenReady()
            if (r0 == 0) goto L_0x0014
        L_0x0012:
            r0 = 1
            goto L_0x0015
        L_0x0014:
            r0 = 0
        L_0x0015:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.isPlaying():boolean");
    }

    public void setStreamType(int i) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setAudioStreamType(i);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setAudioStreamType(i);
        }
    }

    /* access modifiers changed from: private */
    public void checkPlayersReady() {
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
        Throwable cause = exoPlaybackException.getCause();
        TextureView textureView2 = this.textureView;
        if (textureView2 == null || !(cause instanceof SurfaceNotValidException)) {
            this.delegate.onError(exoPlaybackException);
        } else if (this.player != null) {
            ViewGroup viewGroup = (ViewGroup) textureView2.getParent();
            if (viewGroup != null) {
                int indexOfChild = viewGroup.indexOfChild(this.textureView);
                viewGroup.removeView(this.textureView);
                viewGroup.addView(this.textureView, indexOfChild);
            }
            this.player.clearVideoTextureView(this.textureView);
            this.player.setVideoTextureView(this.textureView);
            if (this.loop) {
                preparePlayerLoop(this.videoUri, this.videoType, this.audioUri, this.audioType);
            } else {
                preparePlayer(this.videoUri, this.videoType);
            }
            play();
        }
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
            if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
                this.delegate.onStateChanged(playWhenReady, playbackState);
                this.lastReportedPlayWhenReady = playWhenReady;
                this.lastReportedPlaybackState = playbackState;
            }
        }
    }
}
