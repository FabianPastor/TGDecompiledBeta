package org.telegram.messenger.exoplayer2;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import org.telegram.messenger.exoplayer2.ExoPlayer.ExoPlayerMessage;
import org.telegram.messenger.exoplayer2.Player.EventListener;
import org.telegram.messenger.exoplayer2.audio.AudioAttributes;
import org.telegram.messenger.exoplayer2.audio.AudioAttributes.Builder;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataRenderer.Output;
import org.telegram.messenger.exoplayer2.source.MediaSource;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.TextRenderer;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener;

@TargetApi(16)
public class SimpleExoPlayer implements ExoPlayer {
    private static final String TAG = "SimpleExoPlayer";
    private AudioAttributes audioAttributes;
    private AudioRendererEventListener audioDebugListener;
    private DecoderCounters audioDecoderCounters;
    private Format audioFormat;
    private final int audioRendererCount;
    private int audioSessionId;
    private float audioVolume;
    private final ComponentListener componentListener = new ComponentListener();
    private final CopyOnWriteArraySet<Output> metadataOutputs = new CopyOnWriteArraySet();
    private boolean needSetSurface = true;
    private boolean ownsSurface;
    private final ExoPlayer player;
    protected final Renderer[] renderers;
    private Surface surface;
    private SurfaceHolder surfaceHolder;
    private final CopyOnWriteArraySet<TextRenderer.Output> textOutputs = new CopyOnWriteArraySet();
    private TextureView textureView;
    private VideoRendererEventListener videoDebugListener;
    private DecoderCounters videoDecoderCounters;
    private Format videoFormat;
    private final CopyOnWriteArraySet<VideoListener> videoListeners = new CopyOnWriteArraySet();
    private final int videoRendererCount;
    private int videoScalingMode;

    public interface VideoListener {
        void onRenderedFirstFrame();

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    private final class ComponentListener implements Callback, SurfaceTextureListener, AudioRendererEventListener, Output, TextRenderer.Output, VideoRendererEventListener {
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
            Iterator it = SimpleExoPlayer.this.videoListeners.iterator();
            while (it.hasNext()) {
                ((VideoListener) it.next()).onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
            }
            if (SimpleExoPlayer.this.videoDebugListener != null) {
                SimpleExoPlayer.this.videoDebugListener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
            }
        }

        public void onRenderedFirstFrame(Surface surface) {
            if (SimpleExoPlayer.this.surface == surface) {
                Iterator it = SimpleExoPlayer.this.videoListeners.iterator();
                while (it.hasNext()) {
                    ((VideoListener) it.next()).onRenderedFirstFrame();
                }
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
            Iterator it = SimpleExoPlayer.this.textOutputs.iterator();
            while (it.hasNext()) {
                ((TextRenderer.Output) it.next()).onCues(cues);
            }
        }

        public void onMetadata(Metadata metadata) {
            Iterator it = SimpleExoPlayer.this.metadataOutputs.iterator();
            while (it.hasNext()) {
                ((Output) it.next()).onMetadata(metadata);
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
            Iterator it = SimpleExoPlayer.this.videoListeners.iterator();
            while (it.hasNext()) {
                if (((VideoListener) it.next()).onSurfaceDestroyed(surfaceTexture)) {
                    return false;
                }
            }
            SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
            SimpleExoPlayer.this.needSetSurface = true;
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            Iterator it = SimpleExoPlayer.this.videoListeners.iterator();
            while (it.hasNext()) {
                ((VideoListener) it.next()).onSurfaceTextureUpdated(surfaceTexture);
            }
        }
    }

    protected SimpleExoPlayer(RenderersFactory renderersFactory, TrackSelector trackSelector, LoadControl loadControl) {
        this.renderers = renderersFactory.createRenderers(new Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper()), this.componentListener, this.componentListener, this.componentListener, this.componentListener);
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
        this.audioAttributes = AudioAttributes.DEFAULT;
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
                messages[count] = new ExoPlayerMessage(renderer, 4, Integer.valueOf(videoScalingMode));
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

    public void clearVideoSurface(Surface surface) {
        if (surface != null && surface == this.surface) {
            setVideoSurface(null);
        }
    }

    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        removeSurfaceCallbacks();
        this.surfaceHolder = surfaceHolder;
        if (surfaceHolder == null) {
            setVideoSurfaceInternal(null, false);
            return;
        }
        surfaceHolder.addCallback(this.componentListener);
        Surface surface = surfaceHolder.getSurface();
        if (surface == null || !surface.isValid()) {
            surface = null;
        }
        setVideoSurfaceInternal(surface, false);
    }

    public void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        if (surfaceHolder != null && surfaceHolder == this.surfaceHolder) {
            setVideoSurfaceHolder(null);
        }
    }

    public void setVideoSurfaceView(SurfaceView surfaceView) {
        setVideoSurfaceHolder(surfaceView == null ? null : surfaceView.getHolder());
    }

    public void clearVideoSurfaceView(SurfaceView surfaceView) {
        clearVideoSurfaceHolder(surfaceView == null ? null : surfaceView.getHolder());
    }

    public void setVideoTextureView(TextureView textureView) {
        Surface surface = null;
        if (this.textureView != textureView) {
            removeSurfaceCallbacks();
            this.textureView = textureView;
            this.needSetSurface = true;
            if (textureView == null) {
                setVideoSurfaceInternal(null, true);
                return;
            }
            SurfaceTexture surfaceTexture;
            if (textureView.getSurfaceTextureListener() != null) {
                Log.w(TAG, "Replacing existing SurfaceTextureListener.");
            }
            textureView.setSurfaceTextureListener(this.componentListener);
            if (textureView.isAvailable()) {
                surfaceTexture = textureView.getSurfaceTexture();
            } else {
                surfaceTexture = null;
            }
            if (surfaceTexture != null) {
                surface = new Surface(surfaceTexture);
            }
            setVideoSurfaceInternal(surface, true);
        }
    }

    public void clearVideoTextureView(TextureView textureView) {
        if (textureView != null && textureView == this.textureView) {
            setVideoTextureView(null);
        }
    }

    @Deprecated
    public void setAudioStreamType(int streamType) {
        int usage = Util.getAudioUsageForStreamType(streamType);
        setAudioAttributes(new Builder().setUsage(usage).setContentType(Util.getAudioContentTypeForStreamType(streamType)).build());
    }

    @Deprecated
    public int getAudioStreamType() {
        return Util.getStreamTypeForAudioUsage(this.audioAttributes.usage);
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
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
                messages[count] = new ExoPlayerMessage(renderer, 3, audioAttributes);
            } else {
                count2 = count;
            }
            i++;
            count = count2;
        }
        this.player.sendMessages(messages);
    }

    public AudioAttributes getAudioAttributes() {
        return this.audioAttributes;
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
    @Deprecated
    public void setPlaybackParams(PlaybackParams params) {
        PlaybackParameters playbackParameters;
        if (params != null) {
            params.allowDefaults();
            playbackParameters = new PlaybackParameters(params.getSpeed(), params.getPitch());
        } else {
            playbackParameters = null;
        }
        setPlaybackParameters(playbackParameters);
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

    public void addVideoListener(VideoListener listener) {
        this.videoListeners.add(listener);
    }

    public void removeVideoListener(VideoListener listener) {
        this.videoListeners.remove(listener);
    }

    @Deprecated
    public void setVideoListener(VideoListener listener) {
        this.videoListeners.clear();
        if (listener != null) {
            addVideoListener(listener);
        }
    }

    @Deprecated
    public void clearVideoListener(VideoListener listener) {
        removeVideoListener(listener);
    }

    public void addTextOutput(TextRenderer.Output listener) {
        this.textOutputs.add(listener);
    }

    public void removeTextOutput(TextRenderer.Output listener) {
        this.textOutputs.remove(listener);
    }

    @Deprecated
    public void setTextOutput(TextRenderer.Output output) {
        this.textOutputs.clear();
        if (output != null) {
            addTextOutput(output);
        }
    }

    @Deprecated
    public void clearTextOutput(TextRenderer.Output output) {
        removeTextOutput(output);
    }

    public void addMetadataOutput(Output listener) {
        this.metadataOutputs.add(listener);
    }

    public void removeMetadataOutput(Output listener) {
        this.metadataOutputs.remove(listener);
    }

    @Deprecated
    public void setMetadataOutput(Output output) {
        this.metadataOutputs.clear();
        if (output != null) {
            addMetadataOutput(output);
        }
    }

    @Deprecated
    public void clearMetadataOutput(Output output) {
        removeMetadataOutput(output);
    }

    public void setVideoDebugListener(VideoRendererEventListener listener) {
        this.videoDebugListener = listener;
    }

    public void setAudioDebugListener(AudioRendererEventListener listener) {
        this.audioDebugListener = listener;
    }

    public Looper getPlaybackLooper() {
        return this.player.getPlaybackLooper();
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

    public int getRepeatMode() {
        return this.player.getRepeatMode();
    }

    public void setRepeatMode(int repeatMode) {
        this.player.setRepeatMode(repeatMode);
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

    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        this.player.setPlaybackParameters(playbackParameters);
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.player.getPlaybackParameters();
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

    public boolean isPlayingAd() {
        return this.player.isPlayingAd();
    }

    public int getCurrentAdGroupIndex() {
        return this.player.getCurrentAdGroupIndex();
    }

    public int getCurrentAdIndexInAdGroup() {
        return this.player.getCurrentAdIndexInAdGroup();
    }

    public long getContentPosition() {
        return this.player.getContentPosition();
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
            this.player.blockingSendMessages(messages);
            if (this.ownsSurface) {
                this.surface.release();
            }
        }
        this.surface = surface;
        this.ownsSurface = ownsSurface;
    }
}
