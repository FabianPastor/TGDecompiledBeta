package org.telegram.messenger.exoplayer2.audio;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.PlaybackParameters;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.audio.AudioSink.ConfigurationException;
import org.telegram.messenger.exoplayer2.audio.AudioSink.InitializationException;
import org.telegram.messenger.exoplayer2.audio.AudioSink.Listener;
import org.telegram.messenger.exoplayer2.audio.AudioSink.WriteException;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.drm.DrmSession;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class SimpleDecoderAudioRenderer extends BaseRenderer implements MediaClock {
    private static final int REINITIALIZATION_STATE_NONE = 0;
    private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
    private boolean allowPositionDiscontinuity;
    private final AudioSink audioSink;
    private boolean audioTrackNeedsConfigure;
    private long currentPositionUs;
    private SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> decoder;
    private DecoderCounters decoderCounters;
    private boolean decoderReceivedBuffers;
    private int decoderReinitializationState;
    private DrmSession<ExoMediaCrypto> drmSession;
    private final DrmSessionManager<ExoMediaCrypto> drmSessionManager;
    private int encoderDelay;
    private int encoderPadding;
    private final EventDispatcher eventDispatcher;
    private final DecoderInputBuffer flagsOnlyBuffer;
    private final FormatHolder formatHolder;
    private DecoderInputBuffer inputBuffer;
    private Format inputFormat;
    private boolean inputStreamEnded;
    private SimpleOutputBuffer outputBuffer;
    private boolean outputStreamEnded;
    private DrmSession<ExoMediaCrypto> pendingDrmSession;
    private final boolean playClearSamplesWithoutKeys;
    private boolean waitingForKeys;

    @Retention(RetentionPolicy.SOURCE)
    private @interface ReinitializationState {
    }

    private final class AudioSinkListener implements Listener {
        private AudioSinkListener() {
        }

        public void onAudioSessionId(int i) {
            SimpleDecoderAudioRenderer.this.eventDispatcher.audioSessionId(i);
            SimpleDecoderAudioRenderer.this.onAudioSessionId(i);
        }

        public void onPositionDiscontinuity() {
            SimpleDecoderAudioRenderer.this.onAudioTrackPositionDiscontinuity();
            SimpleDecoderAudioRenderer.this.allowPositionDiscontinuity = true;
        }

        public void onUnderrun(int i, long j, long j2) {
            SimpleDecoderAudioRenderer.this.eventDispatcher.audioTrackUnderrun(i, j, j2);
            SimpleDecoderAudioRenderer.this.onAudioTrackUnderrun(i, j, j2);
        }
    }

    protected abstract SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> createDecoder(Format format, ExoMediaCrypto exoMediaCrypto) throws AudioDecoderException;

    public MediaClock getMediaClock() {
        return this;
    }

    protected void onAudioSessionId(int i) {
    }

    protected void onAudioTrackPositionDiscontinuity() {
    }

    protected void onAudioTrackUnderrun(int i, long j, long j2) {
    }

    protected abstract int supportsFormatInternal(DrmSessionManager<ExoMediaCrypto> drmSessionManager, Format format);

    public SimpleDecoderAudioRenderer() {
        this(null, null, new AudioProcessor[0]);
    }

    public SimpleDecoderAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioProcessor... audioProcessorArr) {
        this(handler, audioRendererEventListener, null, null, false, audioProcessorArr);
    }

    public SimpleDecoderAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioCapabilities audioCapabilities) {
        this(handler, audioRendererEventListener, audioCapabilities, null, false, new AudioProcessor[0]);
    }

    public SimpleDecoderAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, AudioCapabilities audioCapabilities, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean z, AudioProcessor... audioProcessorArr) {
        this(handler, audioRendererEventListener, drmSessionManager, z, new DefaultAudioSink(audioCapabilities, audioProcessorArr));
    }

    public SimpleDecoderAudioRenderer(Handler handler, AudioRendererEventListener audioRendererEventListener, DrmSessionManager<ExoMediaCrypto> drmSessionManager, boolean z, AudioSink audioSink) {
        super(1);
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = z;
        this.eventDispatcher = new EventDispatcher(handler, audioRendererEventListener);
        this.audioSink = audioSink;
        audioSink.setListener(new AudioSinkListener());
        this.formatHolder = new FormatHolder();
        this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
        this.decoderReinitializationState = null;
        this.audioTrackNeedsConfigure = true;
    }

    public final int supportsFormat(Format format) {
        format = supportsFormatInternal(this.drmSessionManager, format);
        if (format <= 2) {
            return format;
        }
        return format | ((Util.SDK_INT >= 21 ? 32 : 0) | 8);
    }

    protected final boolean supportsOutputEncoding(int i) {
        return this.audioSink.isEncodingSupported(i);
    }

    public void render(long j, long j2) throws ExoPlaybackException {
        if (this.outputStreamEnded != null) {
            try {
                this.audioSink.playToEndOfStream();
                return;
            } catch (long j3) {
                throw ExoPlaybackException.createForRenderer(j3, getIndex());
            }
        }
        if (this.inputFormat == null) {
            this.flagsOnlyBuffer.clear();
            j3 = readSource(this.formatHolder, this.flagsOnlyBuffer, true);
            if (j3 == -5) {
                onInputFormatChanged(this.formatHolder.format);
            } else if (j3 == -4) {
                Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
                this.inputStreamEnded = true;
                processEndOfStream();
                return;
            } else {
                return;
            }
        }
        maybeInitDecoder();
        if (this.decoder != null) {
            try {
                TraceUtil.beginSection("drainAndFeed");
                while (drainOutputBuffer() != null) {
                }
                while (feedInputBuffer() != null) {
                }
                TraceUtil.endSection();
                this.decoderCounters.ensureUpdated();
            } catch (long j32) {
                throw ExoPlaybackException.createForRenderer(j32, getIndex());
            }
        }
    }

    protected Format getOutputFormat() {
        return Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, this.inputFormat.channelCount, this.inputFormat.sampleRate, 2, null, null, 0, null);
    }

    private boolean drainOutputBuffer() throws ExoPlaybackException, AudioDecoderException, ConfigurationException, InitializationException, WriteException {
        if (this.outputBuffer == null) {
            this.outputBuffer = (SimpleOutputBuffer) this.decoder.dequeueOutputBuffer();
            if (this.outputBuffer == null) {
                return false;
            }
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
        }
        if (this.outputBuffer.isEndOfStream()) {
            if (this.decoderReinitializationState == 2) {
                releaseDecoder();
                maybeInitDecoder();
                this.audioTrackNeedsConfigure = true;
            } else {
                this.outputBuffer.release();
                this.outputBuffer = null;
                processEndOfStream();
            }
            return false;
        }
        if (this.audioTrackNeedsConfigure) {
            Format outputFormat = getOutputFormat();
            this.audioSink.configure(outputFormat.pcmEncoding, outputFormat.channelCount, outputFormat.sampleRate, 0, null, this.encoderDelay, this.encoderPadding);
            this.audioTrackNeedsConfigure = false;
        }
        if (!this.audioSink.handleBuffer(this.outputBuffer.data, this.outputBuffer.timeUs)) {
            return false;
        }
        decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.outputBuffer.release();
        this.outputBuffer = null;
        return true;
    }

    private boolean feedInputBuffer() throws AudioDecoderException, ExoPlaybackException {
        if (!(this.decoder == null || this.decoderReinitializationState == 2)) {
            if (!this.inputStreamEnded) {
                if (this.inputBuffer == null) {
                    this.inputBuffer = this.decoder.dequeueInputBuffer();
                    if (this.inputBuffer == null) {
                        return false;
                    }
                }
                if (this.decoderReinitializationState == 1) {
                    this.inputBuffer.setFlags(4);
                    this.decoder.queueInputBuffer(this.inputBuffer);
                    this.inputBuffer = null;
                    this.decoderReinitializationState = 2;
                    return false;
                }
                int i;
                if (this.waitingForKeys) {
                    i = -4;
                } else {
                    i = readSource(this.formatHolder, this.inputBuffer, false);
                }
                if (i == -3) {
                    return false;
                }
                if (i == -5) {
                    onInputFormatChanged(this.formatHolder.format);
                    return true;
                } else if (this.inputBuffer.isEndOfStream()) {
                    this.inputStreamEnded = true;
                    this.decoder.queueInputBuffer(this.inputBuffer);
                    this.inputBuffer = null;
                    return false;
                } else {
                    this.waitingForKeys = shouldWaitForKeys(this.inputBuffer.isEncrypted());
                    if (this.waitingForKeys) {
                        return false;
                    }
                    this.inputBuffer.flip();
                    this.decoder.queueInputBuffer(this.inputBuffer);
                    this.decoderReceivedBuffers = true;
                    DecoderCounters decoderCounters = this.decoderCounters;
                    decoderCounters.inputBufferCount++;
                    this.inputBuffer = null;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldWaitForKeys(boolean z) throws ExoPlaybackException {
        if (this.drmSession != null) {
            if (z || !this.playClearSamplesWithoutKeys) {
                z = this.drmSession.getState();
                boolean z2 = true;
                if (z) {
                    throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
                }
                if (z) {
                    z2 = false;
                }
                return z2;
            }
        }
        return false;
    }

    private void processEndOfStream() throws ExoPlaybackException {
        this.outputStreamEnded = true;
        try {
            this.audioSink.playToEndOfStream();
        } catch (Exception e) {
            throw ExoPlaybackException.createForRenderer(e, getIndex());
        }
    }

    private void flushDecoder() throws ExoPlaybackException {
        this.waitingForKeys = false;
        if (this.decoderReinitializationState != 0) {
            releaseDecoder();
            maybeInitDecoder();
            return;
        }
        this.inputBuffer = null;
        if (this.outputBuffer != null) {
            this.outputBuffer.release();
            this.outputBuffer = null;
        }
        this.decoder.flush();
        this.decoderReceivedBuffers = false;
    }

    public boolean isEnded() {
        return this.outputStreamEnded && this.audioSink.isEnded();
    }

    public boolean isReady() {
        if (!this.audioSink.hasPendingData()) {
            if (!(this.inputFormat == null || this.waitingForKeys)) {
                if (!isSourceReady()) {
                    if (this.outputBuffer != null) {
                    }
                }
            }
            return false;
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

    protected void onEnabled(boolean z) throws ExoPlaybackException {
        this.decoderCounters = new DecoderCounters();
        this.eventDispatcher.enabled(this.decoderCounters);
        z = getConfiguration().tunnelingAudioSessionId;
        if (z) {
            this.audioSink.enableTunnelingV21(z);
        } else {
            this.audioSink.disableTunneling();
        }
    }

    protected void onPositionReset(long j, boolean z) throws ExoPlaybackException {
        this.audioSink.reset();
        this.currentPositionUs = j;
        this.allowPositionDiscontinuity = 1;
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.decoder != null) {
            flushDecoder();
        }
    }

    protected void onStarted() {
        this.audioSink.play();
    }

    protected void onStopped() {
        this.audioSink.pause();
        updateCurrentPosition();
    }

    protected void onDisabled() {
        this.inputFormat = null;
        this.audioTrackNeedsConfigure = true;
        this.waitingForKeys = false;
        try {
            releaseDecoder();
            this.audioSink.release();
            try {
                if (this.drmSession != null) {
                    this.drmSessionManager.releaseSession(this.drmSession);
                }
                try {
                    if (!(this.pendingDrmSession == null || this.pendingDrmSession == this.drmSession)) {
                        this.drmSessionManager.releaseSession(this.pendingDrmSession);
                    }
                    this.drmSession = null;
                    this.pendingDrmSession = null;
                    this.decoderCounters.ensureUpdated();
                    this.eventDispatcher.disabled(this.decoderCounters);
                } catch (Throwable th) {
                    this.drmSession = null;
                    this.pendingDrmSession = null;
                    this.decoderCounters.ensureUpdated();
                    this.eventDispatcher.disabled(this.decoderCounters);
                }
            } catch (Throwable th2) {
                this.drmSession = null;
                this.pendingDrmSession = null;
                this.decoderCounters.ensureUpdated();
                this.eventDispatcher.disabled(this.decoderCounters);
            }
        } catch (Throwable th3) {
            this.drmSession = null;
            this.pendingDrmSession = null;
            this.decoderCounters.ensureUpdated();
            this.eventDispatcher.disabled(this.decoderCounters);
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

    private void maybeInitDecoder() throws ExoPlaybackException {
        if (this.decoder == null) {
            this.drmSession = this.pendingDrmSession;
            ExoMediaCrypto exoMediaCrypto = null;
            if (this.drmSession != null) {
                exoMediaCrypto = this.drmSession.getMediaCrypto();
                if (exoMediaCrypto == null) {
                    if (this.drmSession.getError() == null) {
                        return;
                    }
                }
            }
            try {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                TraceUtil.beginSection("createAudioDecoder");
                this.decoder = createDecoder(this.inputFormat, exoMediaCrypto);
                TraceUtil.endSection();
                long elapsedRealtime2 = SystemClock.elapsedRealtime();
                this.eventDispatcher.decoderInitialized(this.decoder.getName(), elapsedRealtime2, elapsedRealtime2 - elapsedRealtime);
                DecoderCounters decoderCounters = this.decoderCounters;
                decoderCounters.decoderInitCount++;
            } catch (Exception e) {
                throw ExoPlaybackException.createForRenderer(e, getIndex());
            }
        }
    }

    private void releaseDecoder() {
        if (this.decoder != null) {
            this.inputBuffer = null;
            this.outputBuffer = null;
            this.decoder.release();
            this.decoder = null;
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.decoderReleaseCount++;
            this.decoderReinitializationState = 0;
            this.decoderReceivedBuffers = false;
        }
    }

    private void onInputFormatChanged(Format format) throws ExoPlaybackException {
        Format format2 = this.inputFormat;
        this.inputFormat = format;
        if ((Util.areEqual(this.inputFormat.drmInitData, format2 == null ? null : format2.drmInitData) ^ 1) != 0) {
            if (this.inputFormat.drmInitData == null) {
                this.pendingDrmSession = null;
            } else if (this.drmSessionManager == null) {
                throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
            } else {
                this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.inputFormat.drmInitData);
                if (this.pendingDrmSession == this.drmSession) {
                    this.drmSessionManager.releaseSession(this.pendingDrmSession);
                }
            }
        }
        if (this.decoderReceivedBuffers) {
            this.decoderReinitializationState = 1;
        } else {
            releaseDecoder();
            maybeInitDecoder();
            this.audioTrackNeedsConfigure = true;
        }
        int i = 0;
        this.encoderDelay = format.encoderDelay == -1 ? 0 : format.encoderDelay;
        if (format.encoderPadding != -1) {
            i = format.encoderPadding;
        }
        this.encoderPadding = i;
        this.eventDispatcher.inputFormatChanged(format);
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
}
