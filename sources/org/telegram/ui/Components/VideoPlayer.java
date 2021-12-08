package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FourierTransform;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.secretmedia.ExtendedDefaultDataSourceFactory;

public class VideoPlayer implements Player.EventListener, SimpleExoPlayer.VideoListener, AnalyticsListener, NotificationCenter.NotificationCenterDelegate {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private SimpleExoPlayer audioPlayer;
    /* access modifiers changed from: private */
    public boolean audioPlayerReady;
    private String audioType;
    Handler audioUpdateHandler;
    private Uri audioUri;
    /* access modifiers changed from: private */
    public AudioVisualizerDelegate audioVisualizerDelegate;
    private boolean autoplay;
    private Uri currentUri;
    private VideoPlayerDelegate delegate;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private boolean looping;
    private boolean loopingMediaSource;
    private Handler mainHandler;
    private DataSource.Factory mediaDataSourceFactory;
    private boolean mixedAudio;
    /* access modifiers changed from: private */
    public boolean mixedPlayWhenReady;
    private SimpleExoPlayer player;
    private int repeatCount;
    private boolean shouldPauseOther;
    private Surface surface;
    private TextureView textureView;
    private MappingTrackSelector trackSelector;
    private boolean triedReinit;
    private boolean videoPlayerReady;
    private String videoType;
    private Uri videoUri;

    public interface AudioVisualizerDelegate {
        boolean needUpdate();

        void onVisualizerUpdate(boolean z, boolean z2, float[] fArr);
    }

    public /* synthetic */ void onAudioAttributesChanged(AnalyticsListener.EventTime eventTime, AudioAttributes audioAttributes) {
        AnalyticsListener.CC.$default$onAudioAttributesChanged(this, eventTime, audioAttributes);
    }

    public /* synthetic */ void onAudioSessionId(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onAudioSessionId(this, eventTime, i);
    }

    public /* synthetic */ void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        AnalyticsListener.CC.$default$onAudioUnderrun(this, eventTime, i, j, j2);
    }

    public /* synthetic */ void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
        AnalyticsListener.CC.$default$onBandwidthEstimate(this, eventTime, i, j, j2);
    }

    public /* synthetic */ void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderDisabled(this, eventTime, i, decoderCounters);
    }

    public /* synthetic */ void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
        AnalyticsListener.CC.$default$onDecoderEnabled(this, eventTime, i, decoderCounters);
    }

    public /* synthetic */ void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int i, String str, long j) {
        AnalyticsListener.CC.$default$onDecoderInitialized(this, eventTime, i, str, j);
    }

    public /* synthetic */ void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int i, Format format) {
        AnalyticsListener.CC.$default$onDecoderInputFormatChanged(this, eventTime, i, format);
    }

    public /* synthetic */ void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onDownstreamFormatChanged(this, eventTime, mediaLoadData);
    }

    public /* synthetic */ void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysLoaded(this, eventTime);
    }

    public /* synthetic */ void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRemoved(this, eventTime);
    }

    public /* synthetic */ void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmKeysRestored(this, eventTime);
    }

    public /* synthetic */ void onDrmSessionAcquired(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionAcquired(this, eventTime);
    }

    public /* synthetic */ void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception exc) {
        AnalyticsListener.CC.$default$onDrmSessionManagerError(this, eventTime, exc);
    }

    public /* synthetic */ void onDrmSessionReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onDrmSessionReleased(this, eventTime);
    }

    public /* synthetic */ void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j) {
        AnalyticsListener.CC.$default$onDroppedVideoFrames(this, eventTime, i, j);
    }

    public /* synthetic */ void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onIsPlayingChanged(this, eventTime, z);
    }

    public /* synthetic */ void onIsPlayingChanged(boolean z) {
        Player.EventListener.CC.$default$onIsPlayingChanged(this, z);
    }

    public /* synthetic */ void onLoadCanceled(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCanceled(this, eventTime, loadEventInfo, mediaLoadData);
    }

    public /* synthetic */ void onLoadCompleted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadCompleted(this, eventTime, loadEventInfo, mediaLoadData);
    }

    public /* synthetic */ void onLoadError(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z) {
        AnalyticsListener.CC.$default$onLoadError(this, eventTime, loadEventInfo, mediaLoadData, iOException, z);
    }

    public /* synthetic */ void onLoadStarted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onLoadStarted(this, eventTime, loadEventInfo, mediaLoadData);
    }

    public /* synthetic */ void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onLoadingChanged(this, eventTime, z);
    }

    public /* synthetic */ void onMediaPeriodCreated(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onMediaPeriodCreated(this, eventTime);
    }

    public /* synthetic */ void onMediaPeriodReleased(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onMediaPeriodReleased(this, eventTime);
    }

    public /* synthetic */ void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
        AnalyticsListener.CC.$default$onMetadata(this, eventTime, metadata);
    }

    public /* synthetic */ void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
        AnalyticsListener.CC.$default$onPlaybackParametersChanged(this, eventTime, playbackParameters);
    }

    public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
        Player.EventListener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
    }

    public /* synthetic */ void onPlaybackSuppressionReasonChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onPlaybackSuppressionReasonChanged(this, eventTime, i);
    }

    public /* synthetic */ void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException exoPlaybackException) {
        AnalyticsListener.CC.$default$onPlayerError(this, eventTime, exoPlaybackException);
    }

    public /* synthetic */ void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
        AnalyticsListener.CC.$default$onPlayerStateChanged(this, eventTime, z, i);
    }

    public /* synthetic */ void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onPositionDiscontinuity(this, eventTime, i);
    }

    public /* synthetic */ void onReadingStarted(AnalyticsListener.EventTime eventTime) {
        AnalyticsListener.CC.$default$onReadingStarted(this, eventTime);
    }

    public /* synthetic */ void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onRepeatModeChanged(this, eventTime, i);
    }

    public /* synthetic */ void onShuffleModeChanged(AnalyticsListener.EventTime eventTime, boolean z) {
        AnalyticsListener.CC.$default$onShuffleModeChanged(this, eventTime, z);
    }

    public /* synthetic */ void onSurfaceSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2) {
        AnalyticsListener.CC.$default$onSurfaceSizeChanged(this, eventTime, i, i2);
    }

    public /* synthetic */ void onTimelineChanged(Timeline timeline, int i) {
        Player.EventListener.CC.$default$onTimelineChanged(this, timeline, i);
    }

    public /* synthetic */ void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
        AnalyticsListener.CC.$default$onTimelineChanged(this, eventTime, i);
    }

    public /* synthetic */ void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        AnalyticsListener.CC.$default$onTracksChanged(this, eventTime, trackGroupArray, trackSelectionArray);
    }

    public /* synthetic */ void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
        AnalyticsListener.CC.$default$onUpstreamDiscarded(this, eventTime, mediaLoadData);
    }

    public /* synthetic */ void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2, int i3, float f) {
        AnalyticsListener.CC.$default$onVideoSizeChanged(this, eventTime, i, i2, i3, f);
    }

    public /* synthetic */ void onVolumeChanged(AnalyticsListener.EventTime eventTime, float f) {
        AnalyticsListener.CC.$default$onVolumeChanged(this, eventTime, f);
    }

    public interface VideoPlayerDelegate {
        void onError(VideoPlayer videoPlayer, Exception exc);

        void onRenderedFirstFrame();

        void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime);

        void onSeekFinished(AnalyticsListener.EventTime eventTime);

        void onSeekStarted(AnalyticsListener.EventTime eventTime);

        void onStateChanged(boolean z, int i);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);

        /* renamed from: org.telegram.ui.Components.VideoPlayer$VideoPlayerDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$onRenderedFirstFrame(VideoPlayerDelegate _this, AnalyticsListener.EventTime eventTime) {
            }

            public static void $default$onSeekStarted(VideoPlayerDelegate _this, AnalyticsListener.EventTime eventTime) {
            }

            public static void $default$onSeekFinished(VideoPlayerDelegate _this, AnalyticsListener.EventTime eventTime) {
            }
        }
    }

    public VideoPlayer() {
        this(true);
    }

    public VideoPlayer(boolean pauseOther) {
        this.audioUpdateHandler = new Handler(Looper.getMainLooper());
        Context context = ApplicationLoader.applicationContext;
        DefaultBandwidthMeter defaultBandwidthMeter = BANDWIDTH_METER;
        this.mediaDataSourceFactory = new ExtendedDefaultDataSourceFactory(context, (TransferListener) defaultBandwidthMeter, (DataSource.Factory) new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", defaultBandwidthMeter));
        this.mainHandler = new Handler();
        this.trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(defaultBandwidthMeter));
        this.lastReportedPlaybackState = 1;
        this.shouldPauseOther = pauseOther;
        if (pauseOther) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.playerDidStartPlaying && ((VideoPlayer) args[0]) != this && isPlaying()) {
            pause();
        }
    }

    private void ensurePleyaerCreated() {
        DefaultRenderersFactory factory;
        DefaultLoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, 65536), 15000, 50000, 100, 5000, -1, true);
        if (this.player == null) {
            if (this.audioVisualizerDelegate != null) {
                factory = new AudioVisualizerRenderersFactory(ApplicationLoader.applicationContext);
            } else {
                factory = new DefaultRenderersFactory(ApplicationLoader.applicationContext);
            }
            factory.setExtensionRendererMode(2);
            SimpleExoPlayer newSimpleInstance = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, (RenderersFactory) factory, (TrackSelector) this.trackSelector, (LoadControl) loadControl, (DrmSessionManager<FrameworkMediaCrypto>) null);
            this.player = newSimpleInstance;
            newSimpleInstance.addAnalyticsListener(this);
            this.player.addListener(this);
            this.player.setVideoListener(this);
            TextureView textureView2 = this.textureView;
            if (textureView2 != null) {
                this.player.setVideoTextureView(textureView2);
            } else {
                Surface surface2 = this.surface;
                if (surface2 != null) {
                    this.player.setVideoSurface(surface2);
                }
            }
            this.player.setPlayWhenReady(this.autoplay);
            this.player.setRepeatMode(this.looping ? 2 : 0);
        }
        if (this.mixedAudio && this.audioPlayer == null) {
            SimpleExoPlayer newSimpleInstance2 = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, (TrackSelector) this.trackSelector, (LoadControl) loadControl, (DrmSessionManager<FrameworkMediaCrypto>) null, 2);
            this.audioPlayer = newSimpleInstance2;
            newSimpleInstance2.addListener(new Player.EventListener() {
                public /* synthetic */ void onIsPlayingChanged(boolean z) {
                    Player.EventListener.CC.$default$onIsPlayingChanged(this, z);
                }

                public /* synthetic */ void onPlaybackSuppressionReasonChanged(int i) {
                    Player.EventListener.CC.$default$onPlaybackSuppressionReasonChanged(this, i);
                }

                public /* synthetic */ void onTimelineChanged(Timeline timeline, int i) {
                    Player.EventListener.CC.$default$onTimelineChanged(this, timeline, i);
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
                        boolean unused = VideoPlayer.this.audioPlayerReady = true;
                        VideoPlayer.this.checkPlayersReady();
                    }
                }

                public void onRepeatModeChanged(int repeatMode) {
                }

                public void onPlayerError(ExoPlaybackException error) {
                }

                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }
            });
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    /* JADX WARNING: type inference failed for: r10v2, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r12v11, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r10v16 */
    /* JADX WARNING: type inference failed for: r12v12, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r12v13, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0055, code lost:
        if (r11.equals("ss") != false) goto L_0x0059;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayerLoop(android.net.Uri r20, java.lang.String r21, android.net.Uri r22, java.lang.String r23) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r0.videoUri = r1
            r2 = r22
            r0.audioUri = r2
            r3 = r21
            r0.videoType = r3
            r4 = r23
            r0.audioType = r4
            r5 = 1
            r0.loopingMediaSource = r5
            r0.mixedAudio = r5
            r6 = 0
            r0.audioPlayerReady = r6
            r0.videoPlayerReady = r6
            r19.ensurePleyaerCreated()
            r7 = 0
            r8 = 0
            r9 = 0
        L_0x0022:
            r10 = 2
            if (r9 >= r10) goto L_0x00c3
            if (r9 != 0) goto L_0x002d
            r11 = r21
            r12 = r20
            r15 = r12
            goto L_0x0032
        L_0x002d:
            r11 = r23
            r12 = r22
            r15 = r12
        L_0x0032:
            r12 = -1
            int r13 = r11.hashCode()
            switch(r13) {
                case 3680: goto L_0x004f;
                case 103407: goto L_0x0045;
                case 3075986: goto L_0x003b;
                default: goto L_0x003a;
            }
        L_0x003a:
            goto L_0x0058
        L_0x003b:
            java.lang.String r10 = "dash"
            boolean r10 = r11.equals(r10)
            if (r10 == 0) goto L_0x003a
            r10 = 0
            goto L_0x0059
        L_0x0045:
            java.lang.String r10 = "hls"
            boolean r10 = r11.equals(r10)
            if (r10 == 0) goto L_0x003a
            r10 = 1
            goto L_0x0059
        L_0x004f:
            java.lang.String r13 = "ss"
            boolean r13 = r11.equals(r13)
            if (r13 == 0) goto L_0x003a
            goto L_0x0059
        L_0x0058:
            r10 = -1
        L_0x0059:
            switch(r10) {
                case 0: goto L_0x009b;
                case 1: goto L_0x008e;
                case 2: goto L_0x0072;
                default: goto L_0x005c;
            }
        L_0x005c:
            r6 = r15
            com.google.android.exoplayer2.source.ExtractorMediaSource r10 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r14 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r15 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r15.<init>()
            android.os.Handler r13 = r0.mainHandler
            r17 = 0
            r12 = r10
            r16 = r13
            r13 = r6
            r12.<init>(r13, r14, r15, r16, r17)
            goto L_0x00b3
        L_0x0072:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r10 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r14 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r13 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r12 = r0.mediaDataSourceFactory
            r13.<init>(r12)
            android.os.Handler r12 = r0.mainHandler
            r17 = 0
            r16 = r12
            r12 = r10
            r18 = r13
            r13 = r15
            r6 = r15
            r15 = r18
            r12.<init>((android.net.Uri) r13, (com.google.android.exoplayer2.upstream.DataSource.Factory) r14, (com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory) r15, (android.os.Handler) r16, (com.google.android.exoplayer2.source.MediaSourceEventListener) r17)
            goto L_0x00b3
        L_0x008e:
            r6 = r15
            com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory r10 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r12 = r0.mediaDataSourceFactory
            r10.<init>((com.google.android.exoplayer2.upstream.DataSource.Factory) r12)
            com.google.android.exoplayer2.source.hls.HlsMediaSource r10 = r10.createMediaSource((android.net.Uri) r6)
            goto L_0x00b3
        L_0x009b:
            r6 = r15
            com.google.android.exoplayer2.source.dash.DashMediaSource r10 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r14 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r15 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r12 = r0.mediaDataSourceFactory
            r15.<init>(r12)
            android.os.Handler r13 = r0.mainHandler
            r17 = 0
            r12 = r10
            r16 = r13
            r13 = r6
            r12.<init>((android.net.Uri) r13, (com.google.android.exoplayer2.upstream.DataSource.Factory) r14, (com.google.android.exoplayer2.source.dash.DashChunkSource.Factory) r15, (android.os.Handler) r16, (com.google.android.exoplayer2.source.MediaSourceEventListener) r17)
        L_0x00b3:
            com.google.android.exoplayer2.source.LoopingMediaSource r12 = new com.google.android.exoplayer2.source.LoopingMediaSource
            r12.<init>(r10)
            r10 = r12
            if (r9 != 0) goto L_0x00bd
            r7 = r10
            goto L_0x00be
        L_0x00bd:
            r8 = r10
        L_0x00be:
            int r9 = r9 + 1
            r6 = 0
            goto L_0x0022
        L_0x00c3:
            com.google.android.exoplayer2.SimpleExoPlayer r6 = r0.player
            r6.prepare(r7, r5, r5)
            com.google.android.exoplayer2.SimpleExoPlayer r6 = r0.audioPlayer
            r6.prepare(r8, r5, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayerLoop(android.net.Uri, java.lang.String, android.net.Uri, java.lang.String):void");
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r3v13, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r0v16 */
    /* JADX WARNING: type inference failed for: r3v14, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r3v15, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0038, code lost:
        if (r11.equals("dash") != false) goto L_0x0050;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayer(android.net.Uri r10, java.lang.String r11) {
        /*
            r9 = this;
            r9.videoUri = r10
            r9.videoType = r11
            r0 = 0
            r9.audioUri = r0
            r9.audioType = r0
            r0 = 0
            r9.loopingMediaSource = r0
            r9.videoPlayerReady = r0
            r9.mixedAudio = r0
            r9.currentUri = r10
            java.lang.String r1 = r10.getScheme()
            r2 = 1
            if (r1 == 0) goto L_0x0023
            java.lang.String r3 = "file"
            boolean r3 = r1.startsWith(r3)
            if (r3 != 0) goto L_0x0023
            r3 = 1
            goto L_0x0024
        L_0x0023:
            r3 = 0
        L_0x0024:
            r9.isStreaming = r3
            r9.ensurePleyaerCreated()
            r3 = -1
            int r4 = r11.hashCode()
            switch(r4) {
                case 3680: goto L_0x0045;
                case 103407: goto L_0x003b;
                case 3075986: goto L_0x0032;
                default: goto L_0x0031;
            }
        L_0x0031:
            goto L_0x004f
        L_0x0032:
            java.lang.String r4 = "dash"
            boolean r4 = r11.equals(r4)
            if (r4 == 0) goto L_0x0031
            goto L_0x0050
        L_0x003b:
            java.lang.String r0 = "hls"
            boolean r0 = r11.equals(r0)
            if (r0 == 0) goto L_0x0031
            r0 = 1
            goto L_0x0050
        L_0x0045:
            java.lang.String r0 = "ss"
            boolean r0 = r11.equals(r0)
            if (r0 == 0) goto L_0x0031
            r0 = 2
            goto L_0x0050
        L_0x004f:
            r0 = -1
        L_0x0050:
            switch(r0) {
                case 0: goto L_0x0085;
                case 1: goto L_0x0079;
                case 2: goto L_0x0065;
                default: goto L_0x0053;
            }
        L_0x0053:
            com.google.android.exoplayer2.source.ExtractorMediaSource r0 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r9.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r6 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r6.<init>()
            android.os.Handler r7 = r9.mainHandler
            r8 = 0
            r3 = r0
            r4 = r10
            r3.<init>(r4, r5, r6, r7, r8)
            goto L_0x0099
        L_0x0065:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r0 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r9.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r6 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r3 = r9.mediaDataSourceFactory
            r6.<init>(r3)
            android.os.Handler r7 = r9.mainHandler
            r8 = 0
            r3 = r0
            r4 = r10
            r3.<init>((android.net.Uri) r4, (com.google.android.exoplayer2.upstream.DataSource.Factory) r5, (com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory) r6, (android.os.Handler) r7, (com.google.android.exoplayer2.source.MediaSourceEventListener) r8)
            goto L_0x0099
        L_0x0079:
            com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory r0 = new com.google.android.exoplayer2.source.hls.HlsMediaSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r3 = r9.mediaDataSourceFactory
            r0.<init>((com.google.android.exoplayer2.upstream.DataSource.Factory) r3)
            com.google.android.exoplayer2.source.hls.HlsMediaSource r0 = r0.createMediaSource((android.net.Uri) r10)
            goto L_0x0099
        L_0x0085:
            com.google.android.exoplayer2.source.dash.DashMediaSource r0 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r9.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r6 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            com.google.android.exoplayer2.upstream.DataSource$Factory r3 = r9.mediaDataSourceFactory
            r6.<init>(r3)
            android.os.Handler r7 = r9.mainHandler
            r8 = 0
            r3 = r0
            r4 = r10
            r3.<init>((android.net.Uri) r4, (com.google.android.exoplayer2.upstream.DataSource.Factory) r5, (com.google.android.exoplayer2.source.dash.DashChunkSource.Factory) r6, (android.os.Handler) r7, (com.google.android.exoplayer2.source.MediaSourceEventListener) r8)
        L_0x0099:
            com.google.android.exoplayer2.SimpleExoPlayer r3 = r9.player
            r3.prepare(r0, r2, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.VideoPlayer.preparePlayer(android.net.Uri, java.lang.String):void");
    }

    public boolean isPlayerPrepared() {
        return this.player != null;
    }

    public void releasePlayer(boolean async) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release(async);
            this.player = null;
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.release(async);
            this.audioPlayer = null;
        }
        if (this.shouldPauseOther) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.playerDidStartPlaying);
        }
    }

    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onSeekStarted(eventTime);
        }
    }

    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onSeekFinished(eventTime);
        }
    }

    public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Surface surface2) {
        VideoPlayerDelegate videoPlayerDelegate = this.delegate;
        if (videoPlayerDelegate != null) {
            videoPlayerDelegate.onRenderedFirstFrame(eventTime);
        }
    }

    public void setTextureView(TextureView texture) {
        if (this.textureView != texture) {
            this.textureView = texture;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setVideoTextureView(texture);
            }
        }
    }

    public void setSurface(Surface s) {
        if (this.surface != s) {
            this.surface = s;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setVideoSurface(s);
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
        if (this.audioVisualizerDelegate != null) {
            this.audioUpdateHandler.removeCallbacksAndMessages((Object) null);
            this.audioVisualizerDelegate.onVisualizerUpdate(false, true, (float[]) null);
        }
    }

    public void setPlaybackSpeed(float speed) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            float f = 1.0f;
            if (speed > 1.0f) {
                f = 0.98f;
            }
            simpleExoPlayer.setPlaybackParameters(new PlaybackParameters(speed, f));
        }
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.mixedPlayWhenReady = playWhenReady;
        if (!playWhenReady || !this.mixedAudio || (this.audioPlayerReady && this.videoPlayerReady)) {
            this.autoplay = playWhenReady;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(playWhenReady);
            }
            SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
            if (simpleExoPlayer2 != null) {
                simpleExoPlayer2.setPlayWhenReady(playWhenReady);
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
        SimpleExoPlayer simpleExoPlayer = this.player;
        return simpleExoPlayer != null && simpleExoPlayer.getVolume() == 0.0f;
    }

    public void setMute(boolean value) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        float f = 0.0f;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(value ? 0.0f : 1.0f);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            if (!value) {
                f = 1.0f;
            }
            simpleExoPlayer2.setVolume(f);
        }
    }

    public void onRepeatModeChanged(int repeatMode) {
    }

    public void onSurfaceSizeChanged(int width, int height) {
    }

    public void setVolume(float volume) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(volume);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setVolume(volume);
        }
    }

    public void seekTo(long positionMs) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(positionMs);
        }
    }

    public void setDelegate(VideoPlayerDelegate videoPlayerDelegate) {
        this.delegate = videoPlayerDelegate;
    }

    public void setAudioVisualizerDelegate(AudioVisualizerDelegate audioVisualizerDelegate2) {
        this.audioVisualizerDelegate = audioVisualizerDelegate2;
    }

    public int getBufferedPercentage() {
        if (!this.isStreaming) {
            return 100;
        }
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getBufferedPercentage();
        }
        return 0;
    }

    public long getBufferedPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return this.isStreaming ? simpleExoPlayer.getBufferedPosition() : simpleExoPlayer.getDuration();
        }
        return 0;
    }

    public boolean isStreaming() {
        return this.isStreaming;
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

    public boolean isBuffering() {
        return this.player != null && this.lastReportedPlaybackState == 2;
    }

    public void setStreamType(int type) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setAudioStreamType(type);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setAudioStreamType(type);
        }
    }

    public void setLooping(boolean looping2) {
        if (this.looping != looping2) {
            this.looping = looping2;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setRepeatMode(looping2 ? 2 : 0);
            }
        }
    }

    public boolean isLooping() {
        return this.looping;
    }

    /* access modifiers changed from: private */
    public void checkPlayersReady() {
        if (this.audioPlayerReady && this.videoPlayerReady && this.mixedPlayWhenReady) {
            play();
        }
    }

    public void onLoadingChanged(boolean isLoading) {
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
        if (playWhenReady && playbackState == 3 && !isMuted() && this.shouldPauseOther) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.playerDidStartPlaying, this);
        }
        if (!this.videoPlayerReady && playbackState == 3) {
            this.videoPlayerReady = true;
            checkPlayersReady();
        }
        if (playbackState != 3) {
            this.audioUpdateHandler.removeCallbacksAndMessages((Object) null);
            AudioVisualizerDelegate audioVisualizerDelegate2 = this.audioVisualizerDelegate;
            if (audioVisualizerDelegate2 != null) {
                audioVisualizerDelegate2.onVisualizerUpdate(false, true, (float[]) null);
            }
        }
    }

    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    public void onPositionDiscontinuity(int reason) {
        if (reason == 0) {
            this.repeatCount++;
        }
    }

    public void onSeekProcessed() {
    }

    public void onPlayerError(ExoPlaybackException error) {
        Throwable cause = error.getCause();
        TextureView textureView2 = this.textureView;
        if (textureView2 == null || ((this.triedReinit || !(cause instanceof MediaCodecRenderer.DecoderInitializationException)) && !(cause instanceof SurfaceNotValidException))) {
            this.delegate.onError(this, error);
            return;
        }
        this.triedReinit = true;
        if (this.player != null) {
            ViewGroup parent = (ViewGroup) textureView2.getParent();
            if (parent != null) {
                int i = parent.indexOfChild(this.textureView);
                parent.removeView(this.textureView);
                parent.addView(this.textureView, i);
            }
            this.player.clearVideoTextureView(this.textureView);
            this.player.setVideoTextureView(this.textureView);
            if (this.loopingMediaSource) {
                preparePlayerLoop(this.videoUri, this.videoType, this.audioUri, this.audioType);
            } else {
                preparePlayer(this.videoUri, this.videoType);
            }
            play();
        }
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

    public int getRepeatCount() {
        return this.repeatCount;
    }

    private class AudioVisualizerRenderersFactory extends DefaultRenderersFactory {
        public AudioVisualizerRenderersFactory(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void buildAudioRenderers(Context context, int extensionRendererMode, MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, boolean enableDecoderFallback, AudioProcessor[] audioProcessors, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {
            super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, enableDecoderFallback, new AudioProcessor[]{new TeeAudioProcessor(new VisualizerBufferSink())}, eventHandler, eventListener, out);
        }
    }

    private class VisualizerBufferSink implements TeeAudioProcessor.AudioBufferSink {
        private final int BUFFER_SIZE = 1024;
        private final int MAX_BUFFER_SIZE = 8192;
        ByteBuffer byteBuffer;
        FourierTransform.FFT fft = new FourierTransform.FFT(1024, 48000.0f);
        long lastUpdateTime;
        int position = 0;
        float[] real = new float[1024];

        public VisualizerBufferSink() {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(8192);
            this.byteBuffer = allocateDirect;
            allocateDirect.position(0);
        }

        public void flush(int sampleRateHz, int channelCount, int encoding) {
        }

        public void handleBuffer(ByteBuffer buffer) {
            ByteBuffer byteBuffer2 = buffer;
            if (VideoPlayer.this.audioVisualizerDelegate != null) {
                if (byteBuffer2 == AudioProcessor.EMPTY_BUFFER || !VideoPlayer.this.mixedPlayWhenReady) {
                    VideoPlayer.this.audioUpdateHandler.postDelayed(new VideoPlayer$VisualizerBufferSink$$ExternalSyntheticLambda0(this), 80);
                } else if (VideoPlayer.this.audioVisualizerDelegate.needUpdate()) {
                    int len = buffer.limit();
                    if (len > 8192) {
                        VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages((Object) null);
                        VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, (float[]) null);
                        return;
                    }
                    this.byteBuffer.put(byteBuffer2);
                    int i = this.position + len;
                    this.position = i;
                    if (i >= 1024) {
                        this.byteBuffer.position(0);
                        for (int i2 = 0; i2 < 1024; i2++) {
                            this.real[i2] = ((float) this.byteBuffer.getShort()) / 32768.0f;
                        }
                        this.byteBuffer.rewind();
                        this.position = 0;
                        this.fft.forward(this.real);
                        float sum = 0.0f;
                        for (int i3 = 0; i3 < 1024; i3++) {
                            float r = this.fft.getSpectrumReal()[i3];
                            float img = this.fft.getSpectrumImaginary()[i3];
                            float peak = ((float) Math.sqrt((double) ((r * r) + (img * img)))) / 30.0f;
                            if (peak > 1.0f) {
                                peak = 1.0f;
                            } else if (peak < 0.0f) {
                                peak = 0.0f;
                            }
                            sum += peak * peak;
                        }
                        float amplitude = (float) Math.sqrt((double) (sum / ((float) 1024)));
                        float[] partsAmplitude = new float[7];
                        partsAmplitude[6] = amplitude;
                        if (amplitude < 0.4f) {
                            for (int k = 0; k < 7; k++) {
                                partsAmplitude[k] = 0.0f;
                            }
                        } else {
                            int part = 1024 / 6;
                            for (int k2 = 0; k2 < 6; k2++) {
                                int start = part * k2;
                                float r2 = this.fft.getSpectrumReal()[start];
                                float img2 = this.fft.getSpectrumImaginary()[start];
                                partsAmplitude[k2] = (float) (Math.sqrt((double) ((r2 * r2) + (img2 * img2))) / 30.0d);
                                if (partsAmplitude[k2] > 1.0f) {
                                    partsAmplitude[k2] = 1.0f;
                                } else if (partsAmplitude[k2] < 0.0f) {
                                    partsAmplitude[k2] = 0.0f;
                                }
                            }
                        }
                        if (System.currentTimeMillis() - this.lastUpdateTime >= ((long) 64)) {
                            this.lastUpdateTime = System.currentTimeMillis();
                            VideoPlayer.this.audioUpdateHandler.postDelayed(new VideoPlayer$VisualizerBufferSink$$ExternalSyntheticLambda1(this, partsAmplitude), 130);
                        }
                    }
                }
            }
        }

        /* renamed from: lambda$handleBuffer$0$org-telegram-ui-Components-VideoPlayer$VisualizerBufferSink  reason: not valid java name */
        public /* synthetic */ void m2716xfa955ae7() {
            VideoPlayer.this.audioUpdateHandler.removeCallbacksAndMessages((Object) null);
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(false, true, (float[]) null);
        }

        /* renamed from: lambda$handleBuffer$1$org-telegram-ui-Components-VideoPlayer$VisualizerBufferSink  reason: not valid java name */
        public /* synthetic */ void m2717xb50afb68(float[] partsAmplitude) {
            VideoPlayer.this.audioVisualizerDelegate.onVisualizerUpdate(true, true, partsAmplitude);
        }
    }
}
