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
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;

@SuppressLint({"NewApi"})
public class VideoPlayer implements Player.EventListener, SimpleExoPlayer.VideoListener, NotificationCenter.NotificationCenterDelegate {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private SimpleExoPlayer audioPlayer;
    /* access modifiers changed from: private */
    public boolean audioPlayerReady;
    private boolean autoplay;
    private Uri currentUri;
    private VideoPlayerDelegate delegate;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState = 1;
    private Handler mainHandler = new Handler();
    private DataSource.Factory mediaDataSourceFactory;
    private boolean mixedAudio;
    private boolean mixedPlayWhenReady;
    private SimpleExoPlayer player;
    private TextureView textureView;
    private MappingTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
    private boolean videoPlayerReady;

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

    /* JADX WARNING: type inference failed for: r6v1, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r8v8, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r6v5 */
    /* JADX WARNING: type inference failed for: r8v9, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r8v10, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayerLoop(android.net.Uri r15, java.lang.String r16, android.net.Uri r17, java.lang.String r18) {
        /*
            r14 = this;
            r0 = r14
            r1 = 1
            r0.mixedAudio = r1
            r2 = 0
            r0.audioPlayerReady = r2
            r0.videoPlayerReady = r2
            r14.ensurePleyaerCreated()
            r3 = 0
            r4 = r3
            r5 = 0
        L_0x000f:
            r6 = 2
            if (r5 >= r6) goto L_0x009f
            if (r5 != 0) goto L_0x0018
            r9 = r15
            r7 = r16
            goto L_0x001c
        L_0x0018:
            r9 = r17
            r7 = r18
        L_0x001c:
            r8 = -1
            int r10 = r7.hashCode()
            r11 = 3680(0xe60, float:5.157E-42)
            if (r10 == r11) goto L_0x0044
            r11 = 103407(0x193ef, float:1.44904E-40)
            if (r10 == r11) goto L_0x003a
            r11 = 3075986(0x2eevar_, float:4.310374E-39)
            if (r10 == r11) goto L_0x0030
            goto L_0x004d
        L_0x0030:
            java.lang.String r10 = "dash"
            boolean r7 = r7.equals(r10)
            if (r7 == 0) goto L_0x004d
            r8 = 0
            goto L_0x004d
        L_0x003a:
            java.lang.String r10 = "hls"
            boolean r7 = r7.equals(r10)
            if (r7 == 0) goto L_0x004d
            r8 = 1
            goto L_0x004d
        L_0x0044:
            java.lang.String r10 = "ss"
            boolean r7 = r7.equals(r10)
            if (r7 == 0) goto L_0x004d
            r8 = 2
        L_0x004d:
            if (r8 == 0) goto L_0x0081
            if (r8 == r1) goto L_0x0075
            if (r8 == r6) goto L_0x0064
            com.google.android.exoplayer2.source.ExtractorMediaSource r6 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r10 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r11 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r11.<init>()
            android.os.Handler r12 = r0.mainHandler
            r13 = 0
            r8 = r6
            r8.<init>(r9, r10, r11, r12, r13)
            goto L_0x0091
        L_0x0064:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r6 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r10 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r11 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            r11.<init>(r10)
            android.os.Handler r12 = r0.mainHandler
            r13 = 0
            r8 = r6
            r8.<init>(r9, r10, r11, r12, r13)
            goto L_0x0091
        L_0x0075:
            com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory r6 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r7 = r0.mediaDataSourceFactory
            r6.<init>((com.google.android.exoplayer2.upstream.DataSource.Factory) r7)
            com.google.android.exoplayer2.source.hls.HlsMediaSource r6 = r6.createMediaSource(r9)
            goto L_0x0091
        L_0x0081:
            com.google.android.exoplayer2.source.dash.DashMediaSource r6 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r10 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r11 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            r11.<init>(r10)
            android.os.Handler r12 = r0.mainHandler
            r13 = 0
            r8 = r6
            r8.<init>(r9, r10, r11, r12, r13)
        L_0x0091:
            com.google.android.exoplayer2.source.LoopingMediaSource r7 = new com.google.android.exoplayer2.source.LoopingMediaSource
            r7.<init>(r6)
            if (r5 != 0) goto L_0x009a
            r3 = r7
            goto L_0x009b
        L_0x009a:
            r4 = r7
        L_0x009b:
            int r5 = r5 + 1
            goto L_0x000f
        L_0x009f:
            com.google.android.exoplayer2.SimpleExoPlayer r2 = r0.player
            r2.prepare(r3, r1, r1)
            com.google.android.exoplayer2.SimpleExoPlayer r2 = r0.audioPlayer
            r2.prepare(r4, r1, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayerLoop(android.net.Uri, java.lang.String, android.net.Uri, java.lang.String):void");
    }

    /* JADX WARNING: type inference failed for: r7v1, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r0v8, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r7v4 */
    /* JADX WARNING: type inference failed for: r0v9, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r0v10, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0039, code lost:
        if (r10.equals("dash") == false) goto L_0x0050;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0087  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayer(android.net.Uri r9, java.lang.String r10) {
        /*
            r8 = this;
            r2 = 0
            r8.videoPlayerReady = r2
            r8.mixedAudio = r2
            r8.currentUri = r9
            java.lang.String r3 = r9.getScheme()
            r6 = 1
            if (r3 == 0) goto L_0x0018
            java.lang.String r4 = "file"
            boolean r3 = r3.startsWith(r4)
            if (r3 != 0) goto L_0x0018
            r3 = 1
            goto L_0x0019
        L_0x0018:
            r3 = 0
        L_0x0019:
            r8.isStreaming = r3
            r8.ensurePleyaerCreated()
            r3 = -1
            int r4 = r10.hashCode()
            r5 = 3680(0xe60, float:5.157E-42)
            r7 = 2
            if (r4 == r5) goto L_0x0046
            r5 = 103407(0x193ef, float:1.44904E-40)
            if (r4 == r5) goto L_0x003c
            r5 = 3075986(0x2eevar_, float:4.310374E-39)
            if (r4 == r5) goto L_0x0033
            goto L_0x0050
        L_0x0033:
            java.lang.String r4 = "dash"
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x0050
            goto L_0x0051
        L_0x003c:
            java.lang.String r2 = "hls"
            boolean r0 = r10.equals(r2)
            if (r0 == 0) goto L_0x0050
            r2 = 1
            goto L_0x0051
        L_0x0046:
            java.lang.String r2 = "ss"
            boolean r0 = r10.equals(r2)
            if (r0 == 0) goto L_0x0050
            r2 = 2
            goto L_0x0051
        L_0x0050:
            r2 = -1
        L_0x0051:
            if (r2 == 0) goto L_0x0087
            if (r2 == r6) goto L_0x007b
            if (r2 == r7) goto L_0x0069
            com.google.android.exoplayer2.source.ExtractorMediaSource r7 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r3 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r3.<init>()
            android.os.Handler r4 = r8.mainHandler
            r5 = 0
            r0 = r7
            r1 = r9
            r0.<init>(r1, r2, r3, r4, r5)
            goto L_0x0098
        L_0x0069:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r7 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r3 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            r3.<init>(r2)
            android.os.Handler r4 = r8.mainHandler
            r5 = 0
            r0 = r7
            r1 = r9
            r0.<init>(r1, r2, r3, r4, r5)
            goto L_0x0098
        L_0x007b:
            com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory r0 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            r0.<init>((com.google.android.exoplayer2.upstream.DataSource.Factory) r2)
            com.google.android.exoplayer2.source.hls.HlsMediaSource r7 = r0.createMediaSource(r9)
            goto L_0x0098
        L_0x0087:
            com.google.android.exoplayer2.source.dash.DashMediaSource r7 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r2 = r8.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r3 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            r3.<init>(r2)
            android.os.Handler r4 = r8.mainHandler
            r5 = 0
            r0 = r7
            r1 = r9
            r0.<init>(r1, r2, r3, r4, r5)
        L_0x0098:
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
            if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
                this.delegate.onStateChanged(playWhenReady, playbackState);
                this.lastReportedPlayWhenReady = playWhenReady;
                this.lastReportedPlaybackState = playbackState;
            }
        }
    }
}
