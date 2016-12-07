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
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage;
import org.telegram.messenger.exoplayer2.audio.AudioCapabilities;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.MediaCodecAudioRenderer;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.telegram.messenger.exoplayer2.metadata.MetadataRenderer;
import org.telegram.messenger.exoplayer2.metadata.MetadataRenderer.Output;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.TextRenderer;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelections;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector.EventListener;
import org.telegram.messenger.exoplayer2.video.MediaCodecVideoRenderer;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener;
import org.telegram.messenger.volley.DefaultRetryPolicy;

@TargetApi(16)
public final class SimpleExoPlayer implements ExoPlayer {
    private static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
    private static final String TAG = "SimpleExoPlayer";
    private AudioRendererEventListener audioDebugListener;
    private DecoderCounters audioDecoderCounters;
    private Format audioFormat;
    private final int audioRendererCount;
    private int audioSessionId;
    private final ComponentListener componentListener = new ComponentListener();
    private Output<List<Id3Frame>> id3Output;
    private final Handler mainHandler = new Handler();
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
    private boolean videoTracksEnabled;
    private float volume;

    @TargetApi(23)
    private static final class PlaybackParamsHolder {
        public final PlaybackParams params;

        public PlaybackParamsHolder(PlaybackParams params) {
            this.params = params;
        }
    }

    public interface VideoListener {
        void onRenderedFirstFrame();

        void onVideoSizeChanged(int i, int i2, int i3, float f);

        void onVideoTracksDisabled();
    }

    private final class ComponentListener implements VideoRendererEventListener, AudioRendererEventListener, TextRenderer.Output, Output<List<Id3Frame>>, Callback, SurfaceTextureListener, EventListener<Object> {
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

        public void onMetadata(List<Id3Frame> id3Frames) {
            if (SimpleExoPlayer.this.id3Output != null) {
                SimpleExoPlayer.this.id3Output.onMetadata(id3Frames);
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
            SimpleExoPlayer.this.setVideoSurfaceInternal(new Surface(surfaceTexture), true);
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        public void onTrackSelectionsChanged(TrackSelections<?> trackSelections) {
            boolean videoTracksEnabled = false;
            int i = 0;
            while (i < SimpleExoPlayer.this.renderers.length) {
                if (SimpleExoPlayer.this.renderers[i].getTrackType() == 2 && trackSelections.get(i) != null) {
                    videoTracksEnabled = true;
                    break;
                }
                i++;
            }
            if (!(SimpleExoPlayer.this.videoListener == null || !SimpleExoPlayer.this.videoTracksEnabled || videoTracksEnabled)) {
                SimpleExoPlayer.this.videoListener.onVideoTracksDisabled();
            }
            SimpleExoPlayer.this.videoTracksEnabled = videoTracksEnabled;
        }
    }

    SimpleExoPlayer(Context context, TrackSelector<?> trackSelector, LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean preferExtensionDecoders, long allowedVideoJoiningTimeMs) {
        trackSelector.addListener(this.componentListener);
        ArrayList<Renderer> renderersList = new ArrayList();
        if (preferExtensionDecoders) {
            buildExtensionRenderers(renderersList, allowedVideoJoiningTimeMs);
            buildRenderers(context, drmSessionManager, renderersList, allowedVideoJoiningTimeMs);
        } else {
            buildRenderers(context, drmSessionManager, renderersList, allowedVideoJoiningTimeMs);
            buildExtensionRenderers(renderersList, allowedVideoJoiningTimeMs);
        }
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
        this.audioSessionId = 0;
        this.volume = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        this.player = new ExoPlayerImpl(this.renderers, trackSelector, loadControl);
    }

    public int getRendererCount() {
        return this.renderers.length;
    }

    public int getRendererType(int index) {
        return this.renderers[index].getTrackType();
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
        textureView.setSurfaceTextureListener(this.componentListener);
    }

    public void setVolume(float volume) {
        this.volume = volume;
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
                messages[count] = new ExoPlayerMessage(renderer, 2, Float.valueOf(volume));
            } else {
                count2 = count;
            }
            i++;
            count = count2;
        }
        this.player.sendMessages(messages);
    }

    public float getVolume() {
        return this.volume;
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

    public void setId3Output(Output<List<Id3Frame>> output) {
        this.id3Output = output;
    }

    public void addListener(ExoPlayer.EventListener listener) {
        this.player.addListener(listener);
    }

    public void removeListener(ExoPlayer.EventListener listener) {
        this.player.removeListener(listener);
    }

    public int getPlaybackState() {
        return this.player.getPlaybackState();
    }

    public void prepare(MediaSource mediaSource) {
        this.player.prepare(mediaSource);
    }

    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetTimeline) {
        this.player.prepare(mediaSource, resetPosition, resetTimeline);
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

    public Timeline getCurrentTimeline() {
        return this.player.getCurrentTimeline();
    }

    public Object getCurrentManifest() {
        return this.player.getCurrentManifest();
    }

    private void buildRenderers(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, ArrayList<Renderer> renderersList, long allowedVideoJoiningTimeMs) {
        ArrayList<Renderer> arrayList = renderersList;
        arrayList.add(new MediaCodecVideoRenderer(context, MediaCodecSelector.DEFAULT, 1, allowedVideoJoiningTimeMs, drmSessionManager, false, this.mainHandler, this.componentListener, 50));
        arrayList = renderersList;
        arrayList.add(new MediaCodecAudioRenderer(MediaCodecSelector.DEFAULT, drmSessionManager, true, this.mainHandler, this.componentListener, AudioCapabilities.getCapabilities(context), 3));
        renderersList.add(new TextRenderer(this.componentListener, this.mainHandler.getLooper()));
        renderersList.add(new MetadataRenderer(this.componentListener, this.mainHandler.getLooper(), new Id3Decoder()));
    }

    private void buildExtensionRenderers(ArrayList<Renderer> renderersList, long allowedVideoJoiningTimeMs) {
        try {
            renderersList.add((Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.vp9.LibvpxVideoRenderer").getConstructor(new Class[]{Boolean.TYPE, Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE}).newInstance(new Object[]{Boolean.valueOf(true), Long.valueOf(allowedVideoJoiningTimeMs), this.mainHandler, this.componentListener, Integer.valueOf(50)}));
            Log.i(TAG, "Loaded LibvpxVideoRenderer.");
        } catch (ClassNotFoundException e) {
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
        try {
            renderersList.add((Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.opus.LibopusAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class}).newInstance(new Object[]{this.mainHandler, this.componentListener}));
            Log.i(TAG, "Loaded LibopusAudioRenderer.");
        } catch (ClassNotFoundException e3) {
        } catch (Exception e22) {
            throw new RuntimeException(e22);
        }
        try {
            renderersList.add((Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.flac.LibflacAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class}).newInstance(new Object[]{this.mainHandler, this.componentListener}));
            Log.i(TAG, "Loaded LibflacAudioRenderer.");
        } catch (ClassNotFoundException e4) {
        } catch (Exception e222) {
            throw new RuntimeException(e222);
        }
        try {
            renderersList.add((Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class}).newInstance(new Object[]{this.mainHandler, this.componentListener}));
            Log.i(TAG, "Loaded FfmpegAudioRenderer.");
        } catch (ClassNotFoundException e5) {
        } catch (Exception e2222) {
            throw new RuntimeException(e2222);
        }
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
