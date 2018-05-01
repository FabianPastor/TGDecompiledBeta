package org.telegram.messenger.exoplayer2.audio;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.audio.AudioSink.Listener;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
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
public class MediaCodecAudioRenderer extends MediaCodecRenderer implements MediaClock {
    private boolean allowPositionDiscontinuity;
    private final AudioSink audioSink;
    private int channelCount;
    private boolean codecNeedsDiscardChannelsWorkaround;
    private long currentPositionUs;
    private int encoderDelay;
    private int encoderPadding;
    private final EventDispatcher eventDispatcher;
    private boolean passthroughEnabled;
    private MediaFormat passthroughMediaFormat;
    private int pcmEncoding;

    private final class AudioSinkListener implements Listener {
        private AudioSinkListener() {
        }

        public void onAudioSessionId(int i) {
            MediaCodecAudioRenderer.this.eventDispatcher.audioSessionId(i);
            MediaCodecAudioRenderer.this.onAudioSessionId(i);
        }

        public void onPositionDiscontinuity() {
            MediaCodecAudioRenderer.this.onAudioTrackPositionDiscontinuity();
            MediaCodecAudioRenderer.this.allowPositionDiscontinuity = true;
        }

        public void onUnderrun(int i, long j, long j2) {
            MediaCodecAudioRenderer.this.eventDispatcher.audioTrackUnderrun(i, j, j2);
            MediaCodecAudioRenderer.this.onAudioTrackUnderrun(i, j, j2);
        }
    }

    public MediaClock getMediaClock() {
        return this;
    }

    protected void onAudioSessionId(int i) {
    }

    protected void onAudioTrackPositionDiscontinuity() {
    }

    protected void onAudioTrackUnderrun(int i, long j, long j2) {
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector) {
        this(mediaCodecSelector, null, true);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean z) {
        this(mediaCodecSelector, drmSessionManager, z, null, null);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, Handler handler, AudioRendererEventListener audioRendererEventListener) {
        this(mediaCodecSelector, null, true, handler, audioRendererEventListener);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean z, Handler handler, AudioRendererEventListener audioRendererEventListener) {
        this(mediaCodecSelector, drmSessionManager, z, handler, audioRendererEventListener, (AudioCapabilities) null, new AudioProcessor[0]);
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean z, Handler handler, AudioRendererEventListener audioRendererEventListener, AudioCapabilities audioCapabilities, AudioProcessor... audioProcessorArr) {
        this(mediaCodecSelector, drmSessionManager, z, handler, audioRendererEventListener, new DefaultAudioSink(audioCapabilities, audioProcessorArr));
    }

    public MediaCodecAudioRenderer(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean z, Handler handler, AudioRendererEventListener audioRendererEventListener, AudioSink audioSink) {
        super(1, mediaCodecSelector, drmSessionManager, z);
        this.eventDispatcher = new EventDispatcher(handler, audioRendererEventListener);
        this.audioSink = audioSink;
        audioSink.setListener(new AudioSinkListener());
    }

    protected int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws DecoderQueryException {
        String str = format.sampleMimeType;
        boolean z = false;
        if (!MimeTypes.isAudio(str)) {
            return 0;
        }
        int i = Util.SDK_INT >= 21 ? 32 : 0;
        drmSessionManager = BaseRenderer.supportsFormatDrm(drmSessionManager, format.drmInitData);
        int i2 = 4;
        if (drmSessionManager != null && allowPassthrough(str) && mediaCodecSelector.getPassthroughDecoderInfo() != null) {
            return (8 | i) | 4;
        }
        int i3 = 1;
        if ((MimeTypes.AUDIO_RAW.equals(str) && !this.audioSink.isEncodingSupported(format.pcmEncoding)) || !this.audioSink.isEncodingSupported(2)) {
            return 1;
        }
        boolean z2;
        DrmInitData drmInitData = format.drmInitData;
        if (drmInitData != null) {
            int i4 = 0;
            z2 = i4;
            while (i4 < drmInitData.schemeDataCount) {
                z2 |= drmInitData.get(i4).requiresSecureDecryption;
                i4++;
            }
        } else {
            z2 = false;
        }
        MediaCodecInfo decoderInfo = mediaCodecSelector.getDecoderInfo(str, z2);
        if (decoderInfo == null) {
            if (z2 && mediaCodecSelector.getDecoderInfo(str, false) != null) {
                i3 = 2;
            }
            return i3;
        } else if (drmSessionManager == null) {
            return 2;
        } else {
            if (Util.SDK_INT < 21 || ((format.sampleRate == -1 || decoderInfo.isAudioSampleRateSupportedV21(format.sampleRate) != null) && (format.channelCount == -1 || decoderInfo.isAudioChannelCountSupportedV21(format.channelCount) != null))) {
                z = true;
            }
            if (!z) {
                i2 = 3;
            }
            return (8 | i) | i2;
        }
    }

    protected MediaCodecInfo getDecoderInfo(MediaCodecSelector mediaCodecSelector, Format format, boolean z) throws DecoderQueryException {
        if (allowPassthrough(format.sampleMimeType)) {
            MediaCodecInfo passthroughDecoderInfo = mediaCodecSelector.getPassthroughDecoderInfo();
            if (passthroughDecoderInfo != null) {
                this.passthroughEnabled = true;
                return passthroughDecoderInfo;
            }
        }
        this.passthroughEnabled = false;
        return super.getDecoderInfo(mediaCodecSelector, format, z);
    }

    protected boolean allowPassthrough(String str) {
        str = MimeTypes.getEncoding(str);
        return (str == null || this.audioSink.isEncodingSupported(str) == null) ? null : true;
    }

    protected void configureCodec(MediaCodecInfo mediaCodecInfo, MediaCodec mediaCodec, Format format, MediaCrypto mediaCrypto) {
        this.codecNeedsDiscardChannelsWorkaround = codecNeedsDiscardChannelsWorkaround(mediaCodecInfo.name);
        mediaCodecInfo = getMediaFormatForPlayback(format);
        if (this.passthroughEnabled) {
            this.passthroughMediaFormat = mediaCodecInfo;
            this.passthroughMediaFormat.setString("mime", MimeTypes.AUDIO_RAW);
            mediaCodec.configure(this.passthroughMediaFormat, null, mediaCrypto, 0);
            this.passthroughMediaFormat.setString("mime", format.sampleMimeType);
            return;
        }
        mediaCodec.configure(mediaCodecInfo, null, mediaCrypto, 0);
        this.passthroughMediaFormat = null;
    }

    protected void onCodecInitialized(String str, long j, long j2) {
        this.eventDispatcher.decoderInitialized(str, j, j2);
    }

    protected void onInputFormatChanged(Format format) throws ExoPlaybackException {
        super.onInputFormatChanged(format);
        this.eventDispatcher.inputFormatChanged(format);
        this.pcmEncoding = MimeTypes.AUDIO_RAW.equals(format.sampleMimeType) ? format.pcmEncoding : 2;
        this.channelCount = format.channelCount;
        int i = 0;
        this.encoderDelay = format.encoderDelay != -1 ? format.encoderDelay : 0;
        if (format.encoderPadding != -1) {
            i = format.encoderPadding;
        }
        this.encoderPadding = i;
    }

    protected void onOutputFormatChanged(MediaCodec mediaCodec, MediaFormat mediaFormat) throws ExoPlaybackException {
        if (this.passthroughMediaFormat != null) {
            mediaCodec = MimeTypes.getEncoding(this.passthroughMediaFormat.getString("mime"));
            mediaFormat = this.passthroughMediaFormat;
        } else {
            mediaCodec = this.pcmEncoding;
        }
        int i = mediaCodec;
        int integer = mediaFormat.getInteger("channel-count");
        int integer2 = mediaFormat.getInteger("sample-rate");
        if (this.codecNeedsDiscardChannelsWorkaround == null || integer != 6 || this.channelCount >= 6) {
            mediaCodec = null;
        } else {
            mediaCodec = new int[this.channelCount];
            for (mediaFormat = null; mediaFormat < this.channelCount; mediaFormat++) {
                mediaCodec[mediaFormat] = mediaFormat;
            }
        }
        try {
            this.audioSink.configure(i, integer, integer2, 0, mediaCodec, this.encoderDelay, this.encoderPadding);
        } catch (MediaCodec mediaCodec2) {
            throw ExoPlaybackException.createForRenderer(mediaCodec2, getIndex());
        }
    }

    protected void onEnabled(boolean z) throws ExoPlaybackException {
        super.onEnabled(z);
        this.eventDispatcher.enabled(this.decoderCounters);
        z = getConfiguration().tunnelingAudioSessionId;
        if (z) {
            this.audioSink.enableTunnelingV21(z);
        } else {
            this.audioSink.disableTunneling();
        }
    }

    protected void onPositionReset(long j, boolean z) throws ExoPlaybackException {
        super.onPositionReset(j, z);
        this.audioSink.reset();
        this.currentPositionUs = j;
        this.allowPositionDiscontinuity = 1;
    }

    protected void onStarted() {
        super.onStarted();
        this.audioSink.play();
    }

    protected void onStopped() {
        this.audioSink.pause();
        updateCurrentPosition();
        super.onStopped();
    }

    protected void onDisabled() {
        try {
            this.audioSink.release();
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
        return super.isEnded() && this.audioSink.isEnded();
    }

    public boolean isReady() {
        if (!this.audioSink.hasPendingData()) {
            if (!super.isReady()) {
                return false;
            }
        }
        return true;
    }

    public long getPositionUs() {
        if (getState() == 2) {
            updateCurrentPosition();
        }
        return this.currentPositionUs;
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        return this.audioSink.setPlaybackParameters(playbackParameters);
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.audioSink.getPlaybackParameters();
    }

    protected boolean processOutputBuffer(long j, long j2, MediaCodec mediaCodec, ByteBuffer byteBuffer, int i, int i2, long j3, boolean z) throws ExoPlaybackException {
        if (this.passthroughEnabled != null && (i2 & 2) != null) {
            mediaCodec.releaseOutputBuffer(i, false);
            return true;
        } else if (z) {
            mediaCodec.releaseOutputBuffer(i, false);
            j = this.decoderCounters;
            j.skippedOutputBufferCount++;
            this.audioSink.handleDiscontinuity();
            return true;
        } else {
            try {
                if (this.audioSink.handleBuffer(byteBuffer, j3) == null) {
                    return false;
                }
                mediaCodec.releaseOutputBuffer(i, false);
                j = this.decoderCounters;
                j.renderedOutputBufferCount++;
                return true;
            } catch (long j4) {
                throw ExoPlaybackException.createForRenderer(j4, getIndex());
            }
        }
    }

    protected void renderToEndOfStream() throws ExoPlaybackException {
        try {
            this.audioSink.playToEndOfStream();
        } catch (Exception e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    public void handleMessage(int i, Object obj) throws ExoPlaybackException {
        switch (i) {
            case 2:
                this.audioSink.setVolume(((Float) obj).floatValue());
                return;
            case 3:
                this.audioSink.setAudioAttributes((AudioAttributes) obj);
                return;
            default:
                super.handleMessage(i, obj);
                return;
        }
    }

    private void updateCurrentPosition() {
        long currentPositionUs = this.audioSink.getCurrentPositionUs(isEnded());
        if (currentPositionUs != Long.MIN_VALUE) {
            if (!this.allowPositionDiscontinuity) {
                currentPositionUs = Math.max(this.currentPositionUs, currentPositionUs);
            }
            this.currentPositionUs = currentPositionUs;
            this.allowPositionDiscontinuity = false;
        }
    }

    private static boolean codecNeedsDiscardChannelsWorkaround(String str) {
        return (Util.SDK_INT >= 24 || "OMX.SEC.aac.dec".equals(str) == null || "samsung".equals(Util.MANUFACTURER) == null || (Util.DEVICE.startsWith("zeroflte") == null && Util.DEVICE.startsWith("herolte") == null && Util.DEVICE.startsWith("heroqlte") == null)) ? null : true;
    }
}
