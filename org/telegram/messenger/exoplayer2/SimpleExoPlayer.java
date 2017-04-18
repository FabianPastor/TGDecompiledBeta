package org.telegram.messenger.exoplayer2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.PlaybackParams;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.ExoPlayer.EventListener;
import org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage;
import org.telegram.messenger.exoplayer2.audio.AudioCapabilities;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.MediaCodecAudioRenderer;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataRenderer;
import org.telegram.messenger.exoplayer2.metadata.MetadataRenderer.Output;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.TextRenderer;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.video.MediaCodecVideoRenderer;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener;

@TargetApi(16)
public class SimpleExoPlayer implements ExoPlayer {
    public static final int EXTENSION_RENDERER_MODE_OFF = 0;
    public static final int EXTENSION_RENDERER_MODE_ON = 1;
    public static final int EXTENSION_RENDERER_MODE_PREFER = 2;
    protected static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
    private static final String TAG = "SimpleExoPlayer";
    private AudioRendererEventListener audioDebugListener;
    private DecoderCounters audioDecoderCounters;
    private Format audioFormat;
    private final int audioRendererCount;
    private int audioSessionId;
    private int audioStreamType;
    private float audioVolume;
    private final ComponentListener componentListener = new ComponentListener();
    private final Handler mainHandler = new Handler();
    private Output metadataOutput;
    private boolean needSetSurface = true;
    private boolean ownsSurface;
    private PlaybackParamsHolder playbackParamsHolder;
    private final ExoPlayer player;
    private final Renderer[] renderers;
    private Surface surface;
    private SurfaceHolder surfaceHolder;
    private TextRenderer.Output textOutput;
    private TextureView textureView;
    private VideoRendererEventListener videoDebugListener;
    private DecoderCounters videoDecoderCounters;
    private Format videoFormat;
    private VideoListener videoListener;
    private final int videoRendererCount;
    private int videoScalingMode;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ExtensionRendererMode {
    }

    @TargetApi(23)
    private static final class PlaybackParamsHolder {
        public final PlaybackParams params;

        public PlaybackParamsHolder(PlaybackParams params) {
            this.params = params;
        }
    }

    public interface VideoListener {
        void onRenderedFirstFrame();

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    private final class ComponentListener implements VideoRendererEventListener, AudioRendererEventListener, TextRenderer.Output, Output, Callback, SurfaceTextureListener {
        private ComponentListener() {
        }

        public void onVideoEnabled(DecoderCounters counters) {
            SimpleExoPlayer.this.videoDecoderCounters = counters;
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onVideoEnabled(counters);
            }
        }

        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onVideoDecoderInitialized(decoderName, initializedTimestampMs, initializationDurationMs);
            }
        }

        public void onVideoInputFormatChanged(Format format) {
            SimpleExoPlayer.this.videoFormat = format;
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onVideoInputFormatChanged(format);
            }
        }

        public void onDroppedFrames(int count, long elapsed) {
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onDroppedFrames(count, elapsed);
            }
        }

        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            if (SimpleExoPlayer.this.videoListener != null) {
                SimpleExoPlayer.this.videoListener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
            }
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
            }
        }

        public void onRenderedFirstFrame(Surface surface) {
            if (SimpleExoPlayer.this.videoListener != null && SimpleExoPlayer.this.surface == surface) {
                SimpleExoPlayer.this.videoListener.onRenderedFirstFrame();
            }
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onRenderedFirstFrame(surface);
            }
        }

        public void onVideoDisabled(DecoderCounters counters) {
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onVideoDisabled(counters);
            }
            SimpleExoPlayer.this.videoFormat = null;
            SimpleExoPlayer.this.videoDecoderCounters = null;
        }

        public void onAudioEnabled(DecoderCounters counters) {
            SimpleExoPlayer.this.audioDecoderCounters = counters;
            if (SimpleExoPlayer.this.audioDebugListener != null) {
                SimpleExoPlayer.this.audioDebugListener.onAudioEnabled(counters);
            }
        }

        public void onAudioSessionId(int sessionId) {
            SimpleExoPlayer.this.audioSessionId = sessionId;
            if (SimpleExoPlayer.this.audioDebugListener != null) {
                SimpleExoPlayer.this.audioDebugListener.onAudioSessionId(sessionId);
            }
        }

        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
            if (SimpleExoPlayer.this.audioDebugListener != null) {
                SimpleExoPlayer.this.audioDebugListener.onAudioDecoderInitialized(decoderName, initializedTimestampMs, initializationDurationMs);
            }
        }

        public void onAudioInputFormatChanged(Format format) {
            SimpleExoPlayer.this.audioFormat = format;
            if (SimpleExoPlayer.this.audioDebugListener != null) {
                SimpleExoPlayer.this.audioDebugListener.onAudioInputFormatChanged(format);
            }
        }

        public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
            if (SimpleExoPlayer.this.audioDebugListener != null) {
                SimpleExoPlayer.this.audioDebugListener.onAudioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
            }
        }

        public void onAudioDisabled(DecoderCounters counters) {
            if (SimpleExoPlayer.this.audioDebugListener != null) {
                SimpleExoPlayer.this.audioDebugListener.onAudioDisabled(counters);
            }
            SimpleExoPlayer.this.audioFormat = null;
            SimpleExoPlayer.this.audioDecoderCounters = null;
            SimpleExoPlayer.this.audioSessionId = 0;
        }

        public void onCues(List<Cue> cues) {
            if (SimpleExoPlayer.this.textOutput != null) {
                SimpleExoPlayer.this.textOutput.onCues(cues);
            }
        }

        public void onMetadata(Metadata metadata) {
            if (SimpleExoPlayer.this.metadataOutput != null) {
                SimpleExoPlayer.this.metadataOutput.onMetadata(metadata);
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            SimpleExoPlayer.this.setVideoSurfaceInternal(holder.getSurface(), false);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            SimpleExoPlayer.this.setVideoSurfaceInternal(null, false);
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (SimpleExoPlayer.this.needSetSurface) {
                SimpleExoPlayer.this.setVideoSurfaceInternal(new Surface(surfaceTexture), true);
                SimpleExoPlayer.this.needSetSurface = false;
            }
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (SimpleExoPlayer.this.videoListener.onSurfaceDestroyed(surfaceTexture)) {
                return false;
            }
            SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
            SimpleExoPlayer.this.needSetSurface = true;
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            SimpleExoPlayer.this.videoListener.onSurfaceTextureUpdated(surfaceTexture);
        }
    }

    protected SimpleExoPlayer(Context context, TrackSelector trackSelector, LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, long allowedVideoJoiningTimeMs) {
        ArrayList<Renderer> renderersList = new ArrayList();
        buildRenderers(context, this.mainHandler, drmSessionManager, extensionRendererMode, allowedVideoJoiningTimeMs, renderersList);
        this.renderers = (Renderer[]) renderersList.toArray(new Renderer[renderersList.size()]);
        int videoRendererCount = 0;
        int audioRendererCount = 0;
        for (Renderer renderer : this.renderers) {
            switch (renderer.getTrackType()) {
                case 1:
                    audioRendererCount++;
                    break;
                case 2:
                    videoRendererCount++;
                    break;
                default:
                    break;
            }
        }
        this.videoRendererCount = videoRendererCount;
        this.audioRendererCount = audioRendererCount;
        this.audioVolume = 1.0f;
        this.audioSessionId = 0;
        this.audioStreamType = 3;
        this.videoScalingMode = 1;
        this.player = new ExoPlayerImpl(this.renderers, trackSelector, loadControl);
    }

    public void setVideoScalingMode(int videoScalingMode) {
        this.videoScalingMode = videoScalingMode;
        ExoPlayerMessage[] messages = new ExoPlayerMessage[this.videoRendererCount];
        Renderer[] rendererArr = this.renderers;
        int length = rendererArr.length;
        int i = 0;
        int count = 0;
        while (i < length) {
            int count2;
            Renderer renderer = rendererArr[i];
            if (renderer.getTrackType() == 2) {
                count2 = count + 1;
                messages[count] = new ExoPlayerMessage(renderer, 5, Integer.valueOf(videoScalingMode));
            } else {
                count2 = count;
            }
            i++;
            count = count2;
        }
        this.player.sendMessages(messages);
    }

    public int getVideoScalingMode() {
        return this.videoScalingMode;
    }

    public void clearVideoSurface() {
        setVideoSurface(null);
    }

    public void setVideoSurface(Surface surface) {
        removeSurfaceCallbacks();
        setVideoSurfaceInternal(surface, false);
    }

    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        removeSurfaceCallbacks();
        this.surfaceHolder = surfaceHolder;
        if (surfaceHolder == null) {
            setVideoSurfaceInternal(null, false);
            return;
        }
        setVideoSurfaceInternal(surfaceHolder.getSurface(), false);
        surfaceHolder.addCallback(this.componentListener);
    }

    public void setVideoSurfaceView(SurfaceView surfaceView) {
        setVideoSurfaceHolder(surfaceView.getHolder());
    }

    public void setVideoTextureView(TextureView textureView) {
        Surface surface = null;
        removeSurfaceCallbacks();
        this.textureView = textureView;
        if (textureView == null) {
            setVideoSurfaceInternal(null, true);
            return;
        }
        if (textureView.getSurfaceTextureListener() != null) {
            Log.w(TAG, "Replacing existing SurfaceTextureListener.");
        }
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        if (surfaceTexture != null) {
            surface = new Surface(surfaceTexture);
        }
        setVideoSurfaceInternal(surface, true);
        if (surfaceTexture != null) {
            this.needSetSurface = false;
        }
        textureView.setSurfaceTextureListener(this.componentListener);
    }

    public void setAudioStreamType(int audioStreamType) {
        this.audioStreamType = audioStreamType;
        ExoPlayerMessage[] messages = new ExoPlayerMessage[this.audioRendererCount];
        Renderer[] rendererArr = this.renderers;
        int length = rendererArr.length;
        int i = 0;
        int count = 0;
        while (i < length) {
            int count2;
            Renderer renderer = rendererArr[i];
            if (renderer.getTrackType() == 1) {
                count2 = count + 1;
                messages[count] = new ExoPlayerMessage(renderer, 4, Integer.valueOf(audioStreamType));
            } else {
                count2 = count;
            }
            i++;
            count = count2;
        }
        this.player.sendMessages(messages);
    }

    public int getAudioStreamType() {
        return this.audioStreamType;
    }

    public void setVolume(float audioVolume) {
        this.audioVolume = audioVolume;
        ExoPlayerMessage[] messages = new ExoPlayerMessage[this.audioRendererCount];
        Renderer[] rendererArr = this.renderers;
        int length = rendererArr.length;
        int i = 0;
        int count = 0;
        while (i < length) {
            int count2;
            Renderer renderer = rendererArr[i];
            if (renderer.getTrackType() == 1) {
                count2 = count + 1;
                messages[count] = new ExoPlayerMessage(renderer, 2, Float.valueOf(audioVolume));
            } else {
                count2 = count;
            }
            i++;
            count = count2;
        }
        this.player.sendMessages(messages);
    }

    public float getVolume() {
        return this.audioVolume;
    }

    @TargetApi(23)
    public void setPlaybackParams(PlaybackParams params) {
        if (params != null) {
            params.allowDefaults();
            this.playbackParamsHolder = new PlaybackParamsHolder(params);
        } else {
            this.playbackParamsHolder = null;
        }
        ExoPlayerMessage[] messages = new ExoPlayerMessage[this.audioRendererCount];
        Renderer[] rendererArr = this.renderers;
        int length = rendererArr.length;
        int i = 0;
        int count = 0;
        while (i < length) {
            int count2;
            Renderer renderer = rendererArr[i];
            if (renderer.getTrackType() == 1) {
                count2 = count + 1;
                messages[count] = new ExoPlayerMessage(renderer, 3, params);
            } else {
                count2 = count;
            }
            i++;
            count = count2;
        }
        this.player.sendMessages(messages);
    }

    @TargetApi(23)
    public PlaybackParams getPlaybackParams() {
        return this.playbackParamsHolder == null ? null : this.playbackParamsHolder.params;
    }

    public Format getVideoFormat() {
        return this.videoFormat;
    }

    public Format getAudioFormat() {
        return this.audioFormat;
    }

    public int getAudioSessionId() {
        return this.audioSessionId;
    }

    public DecoderCounters getVideoDecoderCounters() {
        return this.videoDecoderCounters;
    }

    public DecoderCounters getAudioDecoderCounters() {
        return this.audioDecoderCounters;
    }

    public void setVideoListener(VideoListener listener) {
        this.videoListener = listener;
    }

    public void setVideoDebugListener(VideoRendererEventListener listener) {
        this.videoDebugListener = listener;
    }

    public void setAudioDebugListener(AudioRendererEventListener listener) {
        this.audioDebugListener = listener;
    }

    public void setTextOutput(TextRenderer.Output output) {
        this.textOutput = output;
    }

    public void setMetadataOutput(Output output) {
        this.metadataOutput = output;
    }

    public void addListener(EventListener listener) {
        this.player.addListener(listener);
    }

    public void removeListener(EventListener listener) {
        this.player.removeListener(listener);
    }

    public int getPlaybackState() {
        return this.player.getPlaybackState();
    }

    public void prepare(MediaSource mediaSource) {
        this.player.prepare(mediaSource);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        this.player.prepare(mediaSource, resetPosition, resetState);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.player.setPlayWhenReady(playWhenReady);
    }

    public boolean getPlayWhenReady() {
        return this.player.getPlayWhenReady();
    }

    public boolean isLoading() {
        return this.player.isLoading();
    }

    public void seekToDefaultPosition() {
        this.player.seekToDefaultPosition();
    }

    public void seekToDefaultPosition(int windowIndex) {
        this.player.seekToDefaultPosition(windowIndex);
    }

    public void seekTo(long positionMs) {
        this.player.seekTo(positionMs);
    }

    public void seekTo(int windowIndex, long positionMs) {
        this.player.seekTo(windowIndex, positionMs);
    }

    public void stop() {
        this.player.stop();
    }

    public void release() {
        this.player.release();
        removeSurfaceCallbacks();
        if (this.surface != null) {
            if (this.ownsSurface) {
                this.surface.release();
            }
            this.surface = null;
        }
    }

    public void sendMessages(ExoPlayerMessage... messages) {
        this.player.sendMessages(messages);
    }

    public void blockingSendMessages(ExoPlayerMessage... messages) {
        this.player.blockingSendMessages(messages);
    }

    public int getRendererCount() {
        return this.player.getRendererCount();
    }

    public int getRendererType(int index) {
        return this.player.getRendererType(index);
    }

    public TrackGroupArray getCurrentTrackGroups() {
        return this.player.getCurrentTrackGroups();
    }

    public TrackSelectionArray getCurrentTrackSelections() {
        return this.player.getCurrentTrackSelections();
    }

    public Timeline getCurrentTimeline() {
        return this.player.getCurrentTimeline();
    }

    public Object getCurrentManifest() {
        return this.player.getCurrentManifest();
    }

    public int getCurrentPeriodIndex() {
        return this.player.getCurrentPeriodIndex();
    }

    public int getCurrentWindowIndex() {
        return this.player.getCurrentWindowIndex();
    }

    public long getDuration() {
        return this.player.getDuration();
    }

    public long getCurrentPosition() {
        return this.player.getCurrentPosition();
    }

    public long getBufferedPosition() {
        return this.player.getBufferedPosition();
    }

    public int getBufferedPercentage() {
        return this.player.getBufferedPercentage();
    }

    public boolean isCurrentWindowDynamic() {
        return this.player.isCurrentWindowDynamic();
    }

    public boolean isCurrentWindowSeekable() {
        return this.player.isCurrentWindowSeekable();
    }

    private void buildRenderers(Context context, Handler mainHandler, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        buildVideoRenderers(context, mainHandler, drmSessionManager, extensionRendererMode, this.componentListener, allowedVideoJoiningTimeMs, out);
        buildAudioRenderers(context, mainHandler, drmSessionManager, extensionRendererMode, this.componentListener, buildAudioProcessors(), out);
        buildTextRenderers(context, mainHandler, extensionRendererMode, this.componentListener, out);
        buildMetadataRenderers(context, mainHandler, extensionRendererMode, this.componentListener, out);
        buildMiscellaneousRenderers(context, mainHandler, extensionRendererMode, out);
    }

    protected void buildVideoRenderers(Context context, Handler mainHandler, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, VideoRendererEventListener eventListener, long allowedVideoJoiningTimeMs, ArrayList<Renderer> out) {
        int extensionRendererIndex;
        Throwable e;
        ArrayList<Renderer> arrayList = out;
        arrayList.add(new MediaCodecVideoRenderer(context, MediaCodecSelector.DEFAULT, allowedVideoJoiningTimeMs, drmSessionManager, false, mainHandler, eventListener, 50));
        if (extensionRendererMode != 0) {
            int extensionRendererIndex2 = out.size();
            if (extensionRendererMode == 2) {
                extensionRendererIndex = extensionRendererIndex2 - 1;
            } else {
                extensionRendererIndex = extensionRendererIndex2;
            }
            try {
                extensionRendererIndex2 = extensionRendererIndex + 1;
                try {
                    out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.vp9.LibvpxVideoRenderer").getConstructor(new Class[]{Boolean.TYPE, Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE}).newInstance(new Object[]{Boolean.valueOf(true), Long.valueOf(allowedVideoJoiningTimeMs), mainHandler, this.componentListener, Integer.valueOf(50)}));
                    Log.i(TAG, "Loaded LibvpxVideoRenderer.");
                } catch (ClassNotFoundException e2) {
                } catch (Exception e3) {
                    e = e3;
                    throw new RuntimeException(e);
                }
            } catch (ClassNotFoundException e4) {
                extensionRendererIndex2 = extensionRendererIndex;
            } catch (Exception e5) {
                e = e5;
                extensionRendererIndex2 = extensionRendererIndex;
                throw new RuntimeException(e);
            }
        }
    }

    protected void buildAudioRenderers(Context context, Handler mainHandler, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, AudioRendererEventListener eventListener, AudioProcessor[] audioProcessors, ArrayList<Renderer> out) {
        Exception e;
        ArrayList<Renderer> arrayList = out;
        arrayList.add(new MediaCodecAudioRenderer(MediaCodecSelector.DEFAULT, drmSessionManager, true, mainHandler, eventListener, AudioCapabilities.getCapabilities(context), audioProcessors));
        if (extensionRendererMode != 0) {
            int extensionRendererIndex;
            int extensionRendererIndex2 = out.size();
            if (extensionRendererMode == 2) {
                extensionRendererIndex = extensionRendererIndex2 - 1;
            } else {
                extensionRendererIndex = extensionRendererIndex2;
            }
            try {
                extensionRendererIndex2 = extensionRendererIndex + 1;
                try {
                    out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.opus.LibopusAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                    Log.i(TAG, "Loaded LibopusAudioRenderer.");
                    extensionRendererIndex = extensionRendererIndex2;
                } catch (ClassNotFoundException e2) {
                    extensionRendererIndex = extensionRendererIndex2;
                    extensionRendererIndex2 = extensionRendererIndex + 1;
                    try {
                        out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.flac.LibflacAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                        Log.i(TAG, "Loaded LibflacAudioRenderer.");
                        extensionRendererIndex = extensionRendererIndex2;
                    } catch (ClassNotFoundException e3) {
                        extensionRendererIndex = extensionRendererIndex2;
                        extensionRendererIndex2 = extensionRendererIndex + 1;
                        out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                        Log.i(TAG, "Loaded FfmpegAudioRenderer.");
                    } catch (Exception e4) {
                        e = e4;
                        throw new RuntimeException(e);
                    }
                    extensionRendererIndex2 = extensionRendererIndex + 1;
                    try {
                        out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                        Log.i(TAG, "Loaded FfmpegAudioRenderer.");
                    } catch (ClassNotFoundException e5) {
                        return;
                    } catch (Exception e6) {
                        e = e6;
                        throw new RuntimeException(e);
                    }
                } catch (Exception e7) {
                    e = e7;
                    throw new RuntimeException(e);
                }
            } catch (ClassNotFoundException e8) {
                extensionRendererIndex2 = extensionRendererIndex;
                extensionRendererIndex = extensionRendererIndex2;
                extensionRendererIndex2 = extensionRendererIndex + 1;
                out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.flac.LibflacAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                Log.i(TAG, "Loaded LibflacAudioRenderer.");
                extensionRendererIndex = extensionRendererIndex2;
                extensionRendererIndex2 = extensionRendererIndex + 1;
                out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                Log.i(TAG, "Loaded FfmpegAudioRenderer.");
            } catch (Exception e9) {
                e = e9;
                extensionRendererIndex2 = extensionRendererIndex;
                throw new RuntimeException(e);
            }
            try {
                extensionRendererIndex2 = extensionRendererIndex + 1;
                out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.flac.LibflacAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                Log.i(TAG, "Loaded LibflacAudioRenderer.");
                extensionRendererIndex = extensionRendererIndex2;
            } catch (ClassNotFoundException e10) {
                extensionRendererIndex2 = extensionRendererIndex;
                extensionRendererIndex = extensionRendererIndex2;
                extensionRendererIndex2 = extensionRendererIndex + 1;
                out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                Log.i(TAG, "Loaded FfmpegAudioRenderer.");
            } catch (Exception e11) {
                e = e11;
                extensionRendererIndex2 = extensionRendererIndex;
                throw new RuntimeException(e);
            }
            try {
                extensionRendererIndex2 = extensionRendererIndex + 1;
                out.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{mainHandler, this.componentListener, audioProcessors}));
                Log.i(TAG, "Loaded FfmpegAudioRenderer.");
            } catch (ClassNotFoundException e12) {
                extensionRendererIndex2 = extensionRendererIndex;
            } catch (Exception e13) {
                e = e13;
                extensionRendererIndex2 = extensionRendererIndex;
                throw new RuntimeException(e);
            }
        }
    }

    protected void buildTextRenderers(Context context, Handler mainHandler, int extensionRendererMode, TextRenderer.Output output, ArrayList<Renderer> out) {
        out.add(new TextRenderer(output, mainHandler.getLooper()));
    }

    protected void buildMetadataRenderers(Context context, Handler mainHandler, int extensionRendererMode, Output output, ArrayList<Renderer> out) {
        out.add(new MetadataRenderer(output, mainHandler.getLooper()));
    }

    protected void buildMiscellaneousRenderers(Context context, Handler mainHandler, int extensionRendererMode, ArrayList<Renderer> arrayList) {
    }

    protected AudioProcessor[] buildAudioProcessors() {
        return new AudioProcessor[0];
    }

    private void removeSurfaceCallbacks() {
        if (this.textureView != null) {
            if (this.textureView.getSurfaceTextureListener() != this.componentListener) {
                Log.w(TAG, "SurfaceTextureListener already unset or replaced.");
            } else {
                this.textureView.setSurfaceTextureListener(null);
            }
            this.textureView = null;
        }
        if (this.surfaceHolder != null) {
            this.surfaceHolder.removeCallback(this.componentListener);
            this.surfaceHolder = null;
        }
    }

    private void setVideoSurfaceInternal(Surface surface, boolean ownsSurface) {
        ExoPlayerMessage[] messages = new ExoPlayerMessage[this.videoRendererCount];
        Renderer[] rendererArr = this.renderers;
        int length = rendererArr.length;
        int i = 0;
        int count = 0;
        while (i < length) {
            int count2;
            Renderer renderer = rendererArr[i];
            if (renderer.getTrackType() == 2) {
                count2 = count + 1;
                messages[count] = new ExoPlayerMessage(renderer, 1, surface);
            } else {
                count2 = count;
            }
            i++;
            count = count2;
        }
        if (this.surface == null || this.surface == surface) {
            this.player.sendMessages(messages);
        } else {
            if (this.ownsSurface) {
                this.surface.release();
            }
            this.player.blockingSendMessages(messages);
        }
        this.surface = surface;
        this.ownsSurface = ownsSurface;
    }
}
