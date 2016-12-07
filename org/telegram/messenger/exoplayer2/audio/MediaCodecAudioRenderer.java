package org.telegram.messenger.exoplayer2.audio;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.PlaybackParams;
import android.os.Handler;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.audio.AudioTrack.InitializationException;
import org.telegram.messenger.exoplayer2.audio.AudioTrack.Listener;
import org.telegram.messenger.exoplayer2.audio.AudioTrack.WriteException;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecInfo;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecRenderer;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public class MediaCodecAudioRenderer extends MediaCodecRenderer implements MediaClock, Listener {
    private boolean allowPositionDiscontinuity;
    private int audioSessionId;
    private final AudioTrack audioTrack;
    private long currentPositionUs;
    private final EventDispatcher eventDispatcher;
    private boolean passthroughEnabled;
    private MediaFormat passthroughMediaFormat;
    private int pcmEncoding;

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector) {
        this(mediaCodecSelector, null, true);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys) {
        this(mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, null, null);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, Handler eventHandler, AudioRendererEventListener eventListener) {
        this(mediaCodecSelector, null, true, eventHandler, eventListener);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, AudioRendererEventListener eventListener) {
        this(mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, null);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, AudioRendererEventListener eventListener, AudioCapabilities audioCapabilities) {
        super(1, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys);
        this.audioSessionId = 0;
        this.audioTrack = new AudioTrack(audioCapabilities, this);
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
    }

    protected int supportsFormat(MediaCodecSelector mediaCodecSelector, Format format) throws DecoderQueryException {
        String mimeType = format.sampleMimeType;
        if (!MimeTypes.isAudio(mimeType)) {
            return 0;
        }
        if (allowPassthrough(mimeType) && mediaCodecSelector.getPassthroughDecoderInfo() != null) {
            return 7;
        }
        MediaCodecInfo decoderInfo = mediaCodecSelector.getDecoderInfo(mimeType, false);
        if (decoderInfo == null) {
            return 1;
        }
        boolean decoderCapable;
        if (Util.SDK_INT < 21 || ((format.sampleRate == -1 || decoderInfo.isAudioSampleRateSupportedV21(format.sampleRate)) && (format.channelCount == -1 || decoderInfo.isAudioChannelCountSupportedV21(format.channelCount)))) {
            decoderCapable = true;
        } else {
            decoderCapable = false;
        }
        return (decoderCapable ? 3 : 2) | 4;
    }

    protected MediaCodecInfo getDecoderInfo(MediaCodecSelector mediaCodecSelector, Format format, boolean requiresSecureDecoder) throws DecoderQueryException {
        if (allowPassthrough(format.sampleMimeType)) {
            MediaCodecInfo passthroughDecoderInfo = mediaCodecSelector.getPassthroughDecoderInfo();
            if (passthroughDecoderInfo != null) {
                this.passthroughEnabled = true;
                return passthroughDecoderInfo;
            }
        }
        this.passthroughEnabled = false;
        return super.getDecoderInfo(mediaCodecSelector, format, requiresSecureDecoder);
    }

    protected boolean allowPassthrough(String mimeType) {
        return this.audioTrack.isPassthroughSupported(mimeType);
    }

    protected void configureCodec(MediaCodec codec, Format format, MediaCrypto crypto) {
        if (this.passthroughEnabled) {
            this.passthroughMediaFormat = format.getFrameworkMediaFormatV16();
            this.passthroughMediaFormat.setString("mime", MimeTypes.AUDIO_RAW);
            codec.configure(this.passthroughMediaFormat, null, crypto, 0);
            this.passthroughMediaFormat.setString("mime", format.sampleMimeType);
            return;
        }
        codec.configure(format.getFrameworkMediaFormatV16(), null, crypto, 0);
        this.passthroughMediaFormat = null;
    }

    public MediaClock getMediaClock() {
        return this;
    }

    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
    }

    protected void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        super.onInputFormatChanged(newFormat);
        this.eventDispatcher.inputFormatChanged(newFormat);
        this.pcmEncoding = MimeTypes.AUDIO_RAW.equals(newFormat.sampleMimeType) ? newFormat.pcmEncoding : 2;
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) {
        boolean passthrough;
        MediaFormat format;
        if (this.passthroughMediaFormat != null) {
            passthrough = true;
        } else {
            passthrough = false;
        }
        String mimeType = passthrough ? this.passthroughMediaFormat.getString("mime") : MimeTypes.AUDIO_RAW;
        if (passthrough) {
            format = this.passthroughMediaFormat;
        } else {
            format = outputFormat;
        }
        this.audioTrack.configure(mimeType, format.getInteger("channel-count"), format.getInteger("sample-rate"), this.pcmEncoding, 0);
    }

    protected void onAudioSessionId(int audioSessionId) {
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        this.eventDispatcher.enabled(this.decoderCounters);
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        this.audioTrack.reset();
        this.currentPositionUs = positionUs;
        this.allowPositionDiscontinuity = true;
    }

    protected void onStarted() {
        super.onStarted();
        this.audioTrack.play();
    }

    protected void onStopped() {
        this.audioTrack.pause();
        super.onStopped();
    }

    protected void onDisabled() {
        this.audioSessionId = 0;
        try {
            this.audioTrack.release();
            try {
                super.onDisabled();
            } finally {
                this.decoderCounters.ensureUpdated();
                this.eventDispatcher.disabled(this.decoderCounters);
            }
        } catch (Throwable th) {
            super.onDisabled();
        } finally {
            this.decoderCounters.ensureUpdated();
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    public boolean isEnded() {
        return super.isEnded() && !this.audioTrack.hasPendingData();
    }

    public boolean isReady() {
        return this.audioTrack.hasPendingData() || super.isReady();
    }

    public long getPositionUs() {
        long newCurrentPositionUs = this.audioTrack.getCurrentPositionUs(isEnded());
        if (newCurrentPositionUs != Long.MIN_VALUE) {
            if (!this.allowPositionDiscontinuity) {
                newCurrentPositionUs = Math.max(this.currentPositionUs, newCurrentPositionUs);
            }
            this.currentPositionUs = newCurrentPositionUs;
            this.allowPositionDiscontinuity = false;
        }
        return this.currentPositionUs;
    }

    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs, boolean shouldSkip) throws ExoPlaybackException {
        if (this.passthroughEnabled && (bufferFlags & 2) != 0) {
            codec.releaseOutputBuffer(bufferIndex, false);
            return true;
        } else if (shouldSkip) {
            codec.releaseOutputBuffer(bufferIndex, false);
            r2 = this.decoderCounters;
            r2.skippedOutputBufferCount++;
            this.audioTrack.handleDiscontinuity();
            return true;
        } else {
            if (!this.audioTrack.isInitialized()) {
                try {
                    if (this.audioSessionId == 0) {
                        this.audioSessionId = this.audioTrack.initialize(0);
                        this.eventDispatcher.audioSessionId(this.audioSessionId);
                        onAudioSessionId(this.audioSessionId);
                    } else {
                        this.audioTrack.initialize(this.audioSessionId);
                    }
                    if (getState() == 2) {
                        this.audioTrack.play();
                    }
                } catch (InitializationException e) {
                    throw ExoPlaybackException.createForRenderer(e, getIndex());
                }
            }
            try {
                int handleBufferResult = this.audioTrack.handleBuffer(buffer, bufferPresentationTimeUs);
                if ((handleBufferResult & 1) != 0) {
                    handleAudioTrackDiscontinuity();
                    this.allowPositionDiscontinuity = true;
                }
                if ((handleBufferResult & 2) == 0) {
                    return false;
                }
                codec.releaseOutputBuffer(bufferIndex, false);
                r2 = this.decoderCounters;
                r2.renderedOutputBufferCount++;
                return true;
            } catch (WriteException e2) {
                throw ExoPlaybackException.createForRenderer(e2, getIndex());
            }
        }
    }

    protected void onOutputStreamEnded() {
        this.audioTrack.handleEndOfStream();
    }

    protected void handleAudioTrackDiscontinuity() {
    }

    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        switch (messageType) {
            case 2:
                this.audioTrack.setVolume(((Float) message).floatValue());
                return;
            case 3:
                this.audioTrack.setPlaybackParams((PlaybackParams) message);
                return;
            case 4:
                if (this.audioTrack.setStreamType(((Integer) message).intValue())) {
                    this.audioSessionId = 0;
                    return;
                }
                return;
            default:
                super.handleMessage(messageType, message);
                return;
        }
    }

    public void onUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
        this.eventDispatcher.audioTrackUnderrun(bufferSize, bufferSizeMs, elapsedSinceLastFeedMs);
    }
}
