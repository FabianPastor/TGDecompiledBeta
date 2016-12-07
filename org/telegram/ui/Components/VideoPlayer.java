package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaCodec.CryptoException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.telegram.messenger.exoplayer.DummyTrackRenderer;
import org.telegram.messenger.exoplayer.ExoPlaybackException;
import org.telegram.messenger.exoplayer.ExoPlayer;
import org.telegram.messenger.exoplayer.ExoPlayer.Factory;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecSelector;
import org.telegram.messenger.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer.EventListener;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.TrackRenderer;
import org.telegram.messenger.exoplayer.audio.AudioCapabilities;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorSampleSource;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.upstream.DefaultAllocator;
import org.telegram.messenger.exoplayer.upstream.DefaultUriDataSource;
import org.telegram.messenger.exoplayer.util.PlayerControl;
import org.telegram.messenger.volley.DefaultRetryPolicy;

@SuppressLint({"NewApi"})
public class VideoPlayer implements org.telegram.messenger.exoplayer.ExoPlayer.Listener, EventListener {
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    public static final int RENDERER_COUNT = 2;
    public static final int STATE_BUFFERING = 3;
    public static final int STATE_ENDED = 5;
    public static final int STATE_IDLE = 1;
    public static final int STATE_PREPARING = 2;
    public static final int STATE_READY = 4;
    public static final int TRACK_DEFAULT = 0;
    public static final int TRACK_DISABLED = -1;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 0;
    private TrackRenderer audioRenderer;
    private boolean backgrounded;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState;
    private final CopyOnWriteArrayList<Listener> listeners;
    private final Handler mainHandler;
    private final ExoPlayer player = Factory.newInstance(2, 1000, Factory.DEFAULT_MIN_REBUFFER_MS);
    private final PlayerControl playerControl;
    private final RendererBuilder rendererBuilder;
    private int rendererBuildingState;
    private Surface surface;
    private TrackRenderer videoRenderer;
    private int videoTrackToRestore;

    public interface Listener {
        void onError(Exception exception);

        void onStateChanged(boolean z, int i);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    public interface RendererBuilder {
        void buildRenderers(VideoPlayer videoPlayer);

        void cancel();
    }

    public static class ExtractorRendererBuilder implements RendererBuilder {
        private static final int BUFFER_SEGMENT_COUNT = 256;
        private static final int BUFFER_SEGMENT_SIZE = 262144;
        private final Context context;
        private final Uri uri;
        private final String userAgent;

        public ExtractorRendererBuilder(Context context, String userAgent, Uri uri) {
            this.context = context;
            this.userAgent = userAgent;
            this.uri = uri;
        }

        public void buildRenderers(VideoPlayer player) {
            Allocator allocator = new DefaultAllocator(262144);
            Handler mainHandler = player.getMainHandler();
            TrackRenderer[] renderers = new TrackRenderer[2];
            ExtractorSampleSource sampleSource = new ExtractorSampleSource(this.uri, new DefaultUriDataSource(this.context, this.userAgent), allocator, 67108864, mainHandler, null, 0, new Extractor[0]);
            renderers[0] = new MediaCodecVideoTrackRenderer(this.context, sampleSource, MediaCodecSelector.DEFAULT, 1, HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS, mainHandler, player, 50) {
                protected void doSomeWork(long positionUs, long elapsedRealtimeUs, boolean sourceIsReady) throws ExoPlaybackException {
                    super.doSomeWork(positionUs, elapsedRealtimeUs, sourceIsReady);
                }
            };
            renderers[1] = new MediaCodecAudioTrackRenderer((SampleSource) sampleSource, MediaCodecSelector.DEFAULT, null, true, mainHandler, null, AudioCapabilities.getCapabilities(this.context), 3);
            player.onRenderers(renderers);
        }

        public void cancel() {
        }
    }

    public VideoPlayer(RendererBuilder rendererBuilder) {
        this.rendererBuilder = rendererBuilder;
        this.player.addListener(this);
        this.playerControl = new PlayerControl(this.player);
        this.mainHandler = new Handler();
        this.listeners = new CopyOnWriteArrayList();
        this.lastReportedPlaybackState = 1;
        this.rendererBuildingState = 1;
    }

    public PlayerControl getPlayerControl() {
        return this.playerControl;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
        pushSurface(false);
    }

    public Surface getSurface() {
        return this.surface;
    }

    public void blockingClearSurface() {
        this.surface = null;
        pushSurface(true);
    }

    public int getTrackCount(int type) {
        return this.player.getTrackCount(type);
    }

    public MediaFormat getTrackFormat(int type, int index) {
        return this.player.getTrackFormat(type, index);
    }

    public int getSelectedTrack(int type) {
        return this.player.getSelectedTrack(type);
    }

    public void setSelectedTrack(int type, int index) {
        this.player.setSelectedTrack(type, index);
    }

    public boolean getBackgrounded() {
        return this.backgrounded;
    }

    public void setBackgrounded(boolean backgrounded) {
        if (this.backgrounded != backgrounded) {
            this.backgrounded = backgrounded;
            if (backgrounded) {
                this.videoTrackToRestore = getSelectedTrack(0);
                setSelectedTrack(0, -1);
                blockingClearSurface();
                return;
            }
            setSelectedTrack(0, this.videoTrackToRestore);
        }
    }

    public void prepare() {
        if (this.rendererBuildingState == 3) {
            this.player.stop();
        }
        this.rendererBuilder.cancel();
        this.videoRenderer = null;
        this.audioRenderer = null;
        this.rendererBuildingState = 2;
        maybeReportPlayerState();
        this.rendererBuilder.buildRenderers(this);
    }

    void onRenderers(TrackRenderer[] renderers) {
        for (int i = 0; i < 2; i++) {
            if (renderers[i] == null) {
                renderers[i] = new DummyTrackRenderer();
            }
        }
        this.videoRenderer = renderers[0];
        this.audioRenderer = renderers[1];
        pushSurface(false);
        this.player.prepare(renderers);
        this.rendererBuildingState = 3;
    }

    void onRenderersError(Exception e) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(e);
        }
        this.rendererBuildingState = 1;
        maybeReportPlayerState();
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.player.setPlayWhenReady(playWhenReady);
    }

    public void seekTo(long positionMs) {
        this.player.seekTo(positionMs);
    }

    public void release() {
        this.rendererBuilder.cancel();
        this.rendererBuildingState = 1;
        this.surface = null;
        this.player.release();
    }

    public int getPlaybackState() {
        if (this.rendererBuildingState == 2) {
            return 2;
        }
        int playerState = this.player.getPlaybackState();
        if (this.rendererBuildingState == 3 && playerState == 1) {
            return 2;
        }
        return playerState;
    }

    public long getCurrentPosition() {
        return this.player.getCurrentPosition();
    }

    public long getDuration() {
        return this.player.getDuration();
    }

    public int getBufferedPercentage() {
        return this.player.getBufferedPercentage();
    }

    public boolean getPlayWhenReady() {
        return this.player.getPlayWhenReady();
    }

    Looper getPlaybackLooper() {
        return this.player.getPlaybackLooper();
    }

    Handler getMainHandler() {
        return this.mainHandler;
    }

    public void onPlayerStateChanged(boolean playWhenReady, int state) {
        maybeReportPlayerState();
    }

    public void onPlayerError(ExoPlaybackException exception) {
        this.rendererBuildingState = 1;
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(exception);
        }
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
        }
    }

    public void onDroppedFrames(int count, long elapsed) {
    }

    public void onDecoderInitializationError(DecoderInitializationException e) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(e);
        }
    }

    public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
    }

    public void onPlayWhenReadyCommitted() {
    }

    public void onDrawnToSurface(Surface surface) {
    }

    public void onCryptoError(CryptoException e) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onError(e);
        }
    }

    public void setMute(boolean value) {
        if (this.audioRenderer != null) {
            if (value) {
                this.player.sendMessage(this.audioRenderer, 1, Float.valueOf(0.0f));
            } else {
                this.player.sendMessage(this.audioRenderer, 1, Float.valueOf(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        }
    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = this.player.getPlayWhenReady();
        int playbackState = getPlaybackState();
        if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Listener) it.next()).onStateChanged(playWhenReady, playbackState);
            }
            this.lastReportedPlayWhenReady = playWhenReady;
            this.lastReportedPlaybackState = playbackState;
        }
    }

    private void pushSurface(boolean blockForSurfacePush) {
        if (this.videoRenderer != null) {
            if (blockForSurfacePush) {
                this.player.blockingSendMessage(this.videoRenderer, 1, this.surface);
            } else {
                this.player.sendMessage(this.videoRenderer, 1, this.surface);
            }
        }
    }
}
