package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.drm.DrmSession;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public abstract class MediaCodecRenderer extends BaseRenderer {
    private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
    private static final int ADAPTATION_WORKAROUND_MODE_ALWAYS = 2;
    private static final int ADAPTATION_WORKAROUND_MODE_NEVER = 0;
    private static final int ADAPTATION_WORKAROUND_MODE_SAME_RESOLUTION = 1;
    private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
    private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000;
    private static final int RECONFIGURATION_STATE_NONE = 0;
    private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
    private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
    private static final int REINITIALIZATION_STATE_NONE = 0;
    private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
    private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
    private static final String TAG = "MediaCodecRenderer";
    private final DecoderInputBuffer buffer;
    private MediaCodec codec;
    private int codecAdaptationWorkaroundMode;
    private long codecHotswapDeadlineMs;
    private MediaCodecInfo codecInfo;
    private boolean codecNeedsAdaptationWorkaroundBuffer;
    private boolean codecNeedsDiscardToSpsWorkaround;
    private boolean codecNeedsEosFlushWorkaround;
    private boolean codecNeedsEosOutputExceptionWorkaround;
    private boolean codecNeedsEosPropagationWorkaround;
    private boolean codecNeedsFlushWorkaround;
    private boolean codecNeedsMonoChannelCountWorkaround;
    private boolean codecReceivedBuffers;
    private boolean codecReceivedEos;
    private int codecReconfigurationState;
    private boolean codecReconfigured;
    private int codecReinitializationState;
    private final List<Long> decodeOnlyPresentationTimestamps;
    protected DecoderCounters decoderCounters;
    private DrmSession<FrameworkMediaCrypto> drmSession;
    private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private final DecoderInputBuffer flagsOnlyBuffer;
    private Format format;
    private final FormatHolder formatHolder;
    private ByteBuffer[] inputBuffers;
    private int inputIndex;
    private boolean inputStreamEnded;
    private final MediaCodecSelector mediaCodecSelector;
    private ByteBuffer outputBuffer;
    private final BufferInfo outputBufferInfo;
    private ByteBuffer[] outputBuffers;
    private int outputIndex;
    private boolean outputStreamEnded;
    private DrmSession<FrameworkMediaCrypto> pendingDrmSession;
    private final boolean playClearSamplesWithoutKeys;
    private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
    private boolean shouldSkipOutputBuffer;
    private boolean waitingForFirstSyncFrame;
    private boolean waitingForKeys;

    @Retention(RetentionPolicy.SOURCE)
    private @interface AdaptationWorkaroundMode {
    }

    public static class DecoderInitializationException extends Exception {
        private static final int CUSTOM_ERROR_CODE_BASE = -50000;
        private static final int DECODER_QUERY_ERROR = -49998;
        private static final int NO_SUITABLE_DECODER_ERROR = -49999;
        public final String decoderName;
        public final String diagnosticInfo;
        public final String mimeType;
        public final boolean secureDecoderRequired;

        public DecoderInitializationException(Format format, Throwable th, boolean z, int i) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Decoder init failed: [");
            stringBuilder.append(i);
            stringBuilder.append("], ");
            stringBuilder.append(format);
            super(stringBuilder.toString(), th);
            this.mimeType = format.sampleMimeType;
            this.secureDecoderRequired = z;
            this.decoderName = null;
            this.diagnosticInfo = buildCustomDiagnosticInfo(i);
        }

        public DecoderInitializationException(Format format, Throwable th, boolean z, String str) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Decoder init failed: ");
            stringBuilder.append(str);
            stringBuilder.append(", ");
            stringBuilder.append(format);
            super(stringBuilder.toString(), th);
            this.mimeType = format.sampleMimeType;
            this.secureDecoderRequired = z;
            this.decoderName = str;
            this.diagnosticInfo = Util.SDK_INT >= true ? getDiagnosticInfoV21(th) : null;
        }

        @TargetApi(21)
        private static String getDiagnosticInfoV21(Throwable th) {
            return th instanceof CodecException ? ((CodecException) th).getDiagnosticInfo() : null;
        }

        private static String buildCustomDiagnosticInfo(int i) {
            String str = i < 0 ? "neg_" : TtmlNode.ANONYMOUS_REGION_ID;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("com.google.android.exoplayer.MediaCodecTrackRenderer_");
            stringBuilder.append(str);
            stringBuilder.append(Math.abs(i));
            return stringBuilder.toString();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ReconfigurationState {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface ReinitializationState {
    }

    protected boolean canReconfigureCodec(MediaCodec mediaCodec, boolean z, Format format, Format format2) {
        return false;
    }

    protected abstract void configureCodec(MediaCodecInfo mediaCodecInfo, MediaCodec mediaCodec, Format format, MediaCrypto mediaCrypto) throws DecoderQueryException;

    protected long getDequeueOutputBufferTimeoutUs() {
        return 0;
    }

    protected void onCodecInitialized(String str, long j, long j2) {
    }

    protected void onOutputFormatChanged(MediaCodec mediaCodec, MediaFormat mediaFormat) throws ExoPlaybackException {
    }

    protected void onProcessedOutputBuffer(long j) {
    }

    protected void onQueueInputBuffer(DecoderInputBuffer decoderInputBuffer) {
    }

    protected void onStarted() {
    }

    protected void onStopped() {
    }

    protected abstract boolean processOutputBuffer(long j, long j2, MediaCodec mediaCodec, ByteBuffer byteBuffer, int i, int i2, long j3, boolean z) throws ExoPlaybackException;

    protected void renderToEndOfStream() throws ExoPlaybackException {
    }

    protected boolean shouldInitCodec(MediaCodecInfo mediaCodecInfo) {
        return true;
    }

    protected abstract int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws DecoderQueryException;

    public final int supportsMixedMimeTypeAdaptation() {
        return 8;
    }

    public MediaCodecRenderer(int i, MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean z) {
        super(i);
        Assertions.checkState(Util.SDK_INT >= 16 ? 1 : 0);
        this.mediaCodecSelector = (MediaCodecSelector) Assertions.checkNotNull(mediaCodecSelector);
        this.drmSessionManager = drmSessionManager;
        this.playClearSamplesWithoutKeys = z;
        this.buffer = new DecoderInputBuffer(0);
        this.flagsOnlyBuffer = DecoderInputBuffer.newFlagsOnlyInstance();
        this.formatHolder = new FormatHolder();
        this.decodeOnlyPresentationTimestamps = new ArrayList();
        this.outputBufferInfo = new BufferInfo();
        this.codecReconfigurationState = 0;
        this.codecReinitializationState = 0;
    }

    public final int supportsFormat(Format format) throws ExoPlaybackException {
        try {
            return supportsFormat(this.mediaCodecSelector, this.drmSessionManager, format);
        } catch (Format format2) {
            throw ExoPlaybackException.createForRenderer(format2, getIndex());
        }
    }

    protected MediaCodecInfo getDecoderInfo(MediaCodecSelector mediaCodecSelector, Format format, boolean z) throws DecoderQueryException {
        return mediaCodecSelector.getDecoderInfo(format.sampleMimeType, z);
    }

    protected final void maybeInitCodec() throws ExoPlaybackException {
        if (this.codec == null) {
            if (this.format != null) {
                MediaCrypto wrappedMediaCrypto;
                boolean requiresSecureDecoderComponent;
                String str;
                StringBuilder stringBuilder;
                long elapsedRealtime;
                long elapsedRealtime2;
                DecoderCounters decoderCounters;
                this.drmSession = this.pendingDrmSession;
                String str2 = this.format.sampleMimeType;
                if (this.drmSession != null) {
                    FrameworkMediaCrypto frameworkMediaCrypto = (FrameworkMediaCrypto) this.drmSession.getMediaCrypto();
                    if (frameworkMediaCrypto != null) {
                        wrappedMediaCrypto = frameworkMediaCrypto.getWrappedMediaCrypto();
                        requiresSecureDecoderComponent = frameworkMediaCrypto.requiresSecureDecoderComponent(str2);
                        if (this.codecInfo == null) {
                            try {
                                this.codecInfo = getDecoderInfo(this.mediaCodecSelector, this.format, requiresSecureDecoderComponent);
                                if (this.codecInfo == null && requiresSecureDecoderComponent) {
                                    this.codecInfo = getDecoderInfo(this.mediaCodecSelector, this.format, false);
                                    if (this.codecInfo != null) {
                                        str = TAG;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("Drm session requires secure decoder for ");
                                        stringBuilder.append(str2);
                                        stringBuilder.append(", but no secure decoder available. Trying to proceed with ");
                                        stringBuilder.append(this.codecInfo.name);
                                        stringBuilder.append(".");
                                        Log.w(str, stringBuilder.toString());
                                    }
                                }
                            } catch (Throwable e) {
                                throwDecoderInitError(new DecoderInitializationException(this.format, e, requiresSecureDecoderComponent, -49998));
                            }
                            if (this.codecInfo == null) {
                                throwDecoderInitError(new DecoderInitializationException(this.format, null, requiresSecureDecoderComponent, -49999));
                            }
                        }
                        if (!shouldInitCodec(this.codecInfo)) {
                            str2 = this.codecInfo.name;
                            this.codecAdaptationWorkaroundMode = codecAdaptationWorkaroundMode(str2);
                            this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround(str2, this.format);
                            this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround(str2);
                            this.codecNeedsEosPropagationWorkaround = codecNeedsEosPropagationWorkaround(str2);
                            this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround(str2);
                            this.codecNeedsEosOutputExceptionWorkaround = codecNeedsEosOutputExceptionWorkaround(str2);
                            this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround(str2, this.format);
                            try {
                                elapsedRealtime = SystemClock.elapsedRealtime();
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("createCodec:");
                                stringBuilder.append(str2);
                                TraceUtil.beginSection(stringBuilder.toString());
                                this.codec = MediaCodec.createByCodecName(str2);
                                TraceUtil.endSection();
                                TraceUtil.beginSection("configureCodec");
                                configureCodec(this.codecInfo, this.codec, this.format, wrappedMediaCrypto);
                                TraceUtil.endSection();
                                TraceUtil.beginSection("startCodec");
                                this.codec.start();
                                TraceUtil.endSection();
                                elapsedRealtime2 = SystemClock.elapsedRealtime();
                                onCodecInitialized(str2, elapsedRealtime2, elapsedRealtime2 - elapsedRealtime);
                                getCodecBuffers();
                            } catch (Throwable e2) {
                                throwDecoderInitError(new DecoderInitializationException(this.format, e2, requiresSecureDecoderComponent, str2));
                            }
                            this.codecHotswapDeadlineMs = getState() != 2 ? SystemClock.elapsedRealtime() + MAX_CODEC_HOTSWAP_TIME_MS : C0542C.TIME_UNSET;
                            resetInputBuffer();
                            resetOutputBuffer();
                            this.waitingForFirstSyncFrame = true;
                            decoderCounters = this.decoderCounters;
                            decoderCounters.decoderInitCount++;
                        }
                    } else if (this.drmSession.getError() == null) {
                        return;
                    }
                }
                requiresSecureDecoderComponent = false;
                wrappedMediaCrypto = null;
                if (this.codecInfo == null) {
                    this.codecInfo = getDecoderInfo(this.mediaCodecSelector, this.format, requiresSecureDecoderComponent);
                    this.codecInfo = getDecoderInfo(this.mediaCodecSelector, this.format, false);
                    if (this.codecInfo != null) {
                        str = TAG;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Drm session requires secure decoder for ");
                        stringBuilder.append(str2);
                        stringBuilder.append(", but no secure decoder available. Trying to proceed with ");
                        stringBuilder.append(this.codecInfo.name);
                        stringBuilder.append(".");
                        Log.w(str, stringBuilder.toString());
                    }
                    if (this.codecInfo == null) {
                        throwDecoderInitError(new DecoderInitializationException(this.format, null, requiresSecureDecoderComponent, -49999));
                    }
                }
                if (!shouldInitCodec(this.codecInfo)) {
                    str2 = this.codecInfo.name;
                    this.codecAdaptationWorkaroundMode = codecAdaptationWorkaroundMode(str2);
                    this.codecNeedsDiscardToSpsWorkaround = codecNeedsDiscardToSpsWorkaround(str2, this.format);
                    this.codecNeedsFlushWorkaround = codecNeedsFlushWorkaround(str2);
                    this.codecNeedsEosPropagationWorkaround = codecNeedsEosPropagationWorkaround(str2);
                    this.codecNeedsEosFlushWorkaround = codecNeedsEosFlushWorkaround(str2);
                    this.codecNeedsEosOutputExceptionWorkaround = codecNeedsEosOutputExceptionWorkaround(str2);
                    this.codecNeedsMonoChannelCountWorkaround = codecNeedsMonoChannelCountWorkaround(str2, this.format);
                    elapsedRealtime = SystemClock.elapsedRealtime();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("createCodec:");
                    stringBuilder.append(str2);
                    TraceUtil.beginSection(stringBuilder.toString());
                    this.codec = MediaCodec.createByCodecName(str2);
                    TraceUtil.endSection();
                    TraceUtil.beginSection("configureCodec");
                    configureCodec(this.codecInfo, this.codec, this.format, wrappedMediaCrypto);
                    TraceUtil.endSection();
                    TraceUtil.beginSection("startCodec");
                    this.codec.start();
                    TraceUtil.endSection();
                    elapsedRealtime2 = SystemClock.elapsedRealtime();
                    onCodecInitialized(str2, elapsedRealtime2, elapsedRealtime2 - elapsedRealtime);
                    getCodecBuffers();
                    if (getState() != 2) {
                    }
                    this.codecHotswapDeadlineMs = getState() != 2 ? SystemClock.elapsedRealtime() + MAX_CODEC_HOTSWAP_TIME_MS : C0542C.TIME_UNSET;
                    resetInputBuffer();
                    resetOutputBuffer();
                    this.waitingForFirstSyncFrame = true;
                    decoderCounters = this.decoderCounters;
                    decoderCounters.decoderInitCount++;
                }
            }
        }
    }

    private void throwDecoderInitError(DecoderInitializationException decoderInitializationException) throws ExoPlaybackException {
        throw ExoPlaybackException.createForRenderer(decoderInitializationException, getIndex());
    }

    protected final MediaCodec getCodec() {
        return this.codec;
    }

    protected final MediaCodecInfo getCodecInfo() {
        return this.codecInfo;
    }

    protected final MediaFormat getMediaFormatForPlayback(Format format) {
        format = format.getFrameworkMediaFormatV16();
        if (Util.SDK_INT >= 23) {
            configureMediaFormatForPlaybackV23(format);
        }
        return format;
    }

    protected void onEnabled(boolean z) throws ExoPlaybackException {
        this.decoderCounters = new DecoderCounters();
    }

    protected void onPositionReset(long j, boolean z) throws ExoPlaybackException {
        this.inputStreamEnded = false;
        this.outputStreamEnded = false;
        if (this.codec != null) {
            flushCodec();
        }
    }

    protected void onDisabled() {
        this.format = null;
        try {
            releaseCodec();
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
                } catch (Throwable th) {
                    this.drmSession = null;
                    this.pendingDrmSession = null;
                }
            } catch (Throwable th2) {
                this.drmSession = null;
                this.pendingDrmSession = null;
            }
        } catch (Throwable th3) {
            this.drmSession = null;
            this.pendingDrmSession = null;
        }
    }

    protected void releaseCodec() {
        this.codecHotswapDeadlineMs = C0542C.TIME_UNSET;
        resetInputBuffer();
        resetOutputBuffer();
        this.waitingForKeys = false;
        this.shouldSkipOutputBuffer = false;
        this.decodeOnlyPresentationTimestamps.clear();
        resetCodecBuffers();
        this.codecInfo = null;
        this.codecReconfigured = false;
        this.codecReceivedBuffers = false;
        this.codecNeedsDiscardToSpsWorkaround = false;
        this.codecNeedsFlushWorkaround = false;
        this.codecAdaptationWorkaroundMode = 0;
        this.codecNeedsEosPropagationWorkaround = false;
        this.codecNeedsEosFlushWorkaround = false;
        this.codecNeedsMonoChannelCountWorkaround = false;
        this.codecNeedsAdaptationWorkaroundBuffer = false;
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        this.codecReceivedEos = false;
        this.codecReconfigurationState = 0;
        this.codecReinitializationState = 0;
        if (this.codec != null) {
            DecoderCounters decoderCounters = this.decoderCounters;
            decoderCounters.decoderReleaseCount++;
            try {
                this.codec.stop();
                try {
                    this.codec.release();
                    this.codec = null;
                    if (this.drmSession != null && this.pendingDrmSession != this.drmSession) {
                        try {
                            this.drmSessionManager.releaseSession(this.drmSession);
                        } finally {
                            this.drmSession = null;
                        }
                    }
                } catch (Throwable th) {
                    this.codec = null;
                    if (!(this.drmSession == null || this.pendingDrmSession == this.drmSession)) {
                        this.drmSessionManager.releaseSession(this.drmSession);
                    }
                } finally {
                    this.drmSession = null;
                }
            } catch (Throwable th2) {
                this.codec = null;
                if (!(this.drmSession == null || this.pendingDrmSession == this.drmSession)) {
                    try {
                        this.drmSessionManager.releaseSession(this.drmSession);
                    } finally {
                        this.drmSession = null;
                    }
                }
            } finally {
                this.drmSession = null;
            }
        }
    }

    public void render(long j, long j2) throws ExoPlaybackException {
        if (this.outputStreamEnded) {
            renderToEndOfStream();
            return;
        }
        if (this.format == null) {
            this.flagsOnlyBuffer.clear();
            int readSource = readSource(this.formatHolder, this.flagsOnlyBuffer, true);
            if (readSource == -5) {
                onInputFormatChanged(this.formatHolder.format);
            } else if (readSource == -4) {
                Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
                this.inputStreamEnded = true;
                processEndOfStream();
                return;
            } else {
                return;
            }
        }
        maybeInitCodec();
        if (this.codec != null) {
            TraceUtil.beginSection("drainAndFeed");
            while (drainOutputBuffer(j, j2)) {
            }
            while (feedInputBuffer() != null) {
            }
            TraceUtil.endSection();
        } else {
            j2 = this.decoderCounters;
            j2.skippedInputBufferCount += skipSource(j);
            this.flagsOnlyBuffer.clear();
            j = readSource(this.formatHolder, this.flagsOnlyBuffer, 0);
            if (j == -5) {
                onInputFormatChanged(this.formatHolder.format);
            } else if (j == -4) {
                Assertions.checkState(this.flagsOnlyBuffer.isEndOfStream());
                this.inputStreamEnded = true;
                processEndOfStream();
            }
        }
        this.decoderCounters.ensureUpdated();
    }

    protected void flushCodec() throws ExoPlaybackException {
        this.codecHotswapDeadlineMs = C0542C.TIME_UNSET;
        resetInputBuffer();
        resetOutputBuffer();
        this.waitingForFirstSyncFrame = true;
        this.waitingForKeys = false;
        this.shouldSkipOutputBuffer = false;
        this.decodeOnlyPresentationTimestamps.clear();
        this.codecNeedsAdaptationWorkaroundBuffer = false;
        this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
        if (!this.codecNeedsFlushWorkaround) {
            if (!this.codecNeedsEosFlushWorkaround || !this.codecReceivedEos) {
                if (this.codecReinitializationState != 0) {
                    releaseCodec();
                    maybeInitCodec();
                } else {
                    this.codec.flush();
                    this.codecReceivedBuffers = false;
                }
                if (this.codecReconfigured && this.format != null) {
                    this.codecReconfigurationState = 1;
                    return;
                }
            }
        }
        releaseCodec();
        maybeInitCodec();
        if (this.codecReconfigured) {
        }
    }

    private boolean feedInputBuffer() throws ExoPlaybackException {
        if (!(this.codec == null || this.codecReinitializationState == 2)) {
            if (!this.inputStreamEnded) {
                if (this.inputIndex < 0) {
                    this.inputIndex = this.codec.dequeueInputBuffer(0);
                    if (this.inputIndex < 0) {
                        return false;
                    }
                    this.buffer.data = getInputBuffer(this.inputIndex);
                    this.buffer.clear();
                }
                if (this.codecReinitializationState == 1) {
                    if (!this.codecNeedsEosPropagationWorkaround) {
                        this.codecReceivedEos = true;
                        this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0, 4);
                        resetInputBuffer();
                    }
                    this.codecReinitializationState = 2;
                    return false;
                } else if (this.codecNeedsAdaptationWorkaroundBuffer) {
                    this.codecNeedsAdaptationWorkaroundBuffer = false;
                    this.buffer.data.put(ADAPTATION_WORKAROUND_BUFFER);
                    this.codec.queueInputBuffer(this.inputIndex, 0, ADAPTATION_WORKAROUND_BUFFER.length, 0, 0);
                    resetInputBuffer();
                    this.codecReceivedBuffers = true;
                    return true;
                } else {
                    int i;
                    int i2;
                    if (this.waitingForKeys) {
                        i = -4;
                        i2 = 0;
                    } else {
                        if (this.codecReconfigurationState == 1) {
                            for (i = 0; i < this.format.initializationData.size(); i++) {
                                this.buffer.data.put((byte[]) this.format.initializationData.get(i));
                            }
                            this.codecReconfigurationState = 2;
                        }
                        i = this.buffer.data.position();
                        i2 = i;
                        i = readSource(this.formatHolder, this.buffer, false);
                    }
                    if (i == -3) {
                        return false;
                    }
                    if (i == -5) {
                        if (this.codecReconfigurationState == 2) {
                            this.buffer.clear();
                            this.codecReconfigurationState = 1;
                        }
                        onInputFormatChanged(this.formatHolder.format);
                        return true;
                    } else if (this.buffer.isEndOfStream()) {
                        if (this.codecReconfigurationState == 2) {
                            this.buffer.clear();
                            this.codecReconfigurationState = 1;
                        }
                        this.inputStreamEnded = true;
                        if (this.codecReceivedBuffers) {
                            try {
                                if (!this.codecNeedsEosPropagationWorkaround) {
                                    this.codecReceivedEos = true;
                                    this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0, 4);
                                    resetInputBuffer();
                                }
                                return false;
                            } catch (Exception e) {
                                throw ExoPlaybackException.createForRenderer(e, getIndex());
                            }
                        }
                        processEndOfStream();
                        return false;
                    } else if (!this.waitingForFirstSyncFrame || this.buffer.isKeyFrame()) {
                        this.waitingForFirstSyncFrame = false;
                        boolean isEncrypted = this.buffer.isEncrypted();
                        this.waitingForKeys = shouldWaitForKeys(isEncrypted);
                        if (this.waitingForKeys) {
                            return false;
                        }
                        if (this.codecNeedsDiscardToSpsWorkaround && !isEncrypted) {
                            NalUnitUtil.discardToSps(this.buffer.data);
                            if (this.buffer.data.position() == 0) {
                                return true;
                            }
                            this.codecNeedsDiscardToSpsWorkaround = false;
                        }
                        try {
                            long j = this.buffer.timeUs;
                            if (this.buffer.isDecodeOnly()) {
                                this.decodeOnlyPresentationTimestamps.add(Long.valueOf(j));
                            }
                            this.buffer.flip();
                            onQueueInputBuffer(this.buffer);
                            if (isEncrypted) {
                                this.codec.queueSecureInputBuffer(this.inputIndex, 0, getFrameworkCryptoInfo(this.buffer, i2), j, 0);
                            } else {
                                this.codec.queueInputBuffer(this.inputIndex, 0, this.buffer.data.limit(), j, 0);
                            }
                            resetInputBuffer();
                            this.codecReceivedBuffers = true;
                            this.codecReconfigurationState = 0;
                            DecoderCounters decoderCounters = this.decoderCounters;
                            decoderCounters.inputBufferCount++;
                            return true;
                        } catch (Exception e2) {
                            throw ExoPlaybackException.createForRenderer(e2, getIndex());
                        }
                    } else {
                        this.buffer.clear();
                        if (this.codecReconfigurationState == 2) {
                            this.codecReconfigurationState = 1;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void getCodecBuffers() {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = this.codec.getInputBuffers();
            this.outputBuffers = this.codec.getOutputBuffers();
        }
    }

    private void resetCodecBuffers() {
        if (Util.SDK_INT < 21) {
            this.inputBuffers = null;
            this.outputBuffers = null;
        }
    }

    private ByteBuffer getInputBuffer(int i) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getInputBuffer(i);
        }
        return this.inputBuffers[i];
    }

    private ByteBuffer getOutputBuffer(int i) {
        if (Util.SDK_INT >= 21) {
            return this.codec.getOutputBuffer(i);
        }
        return this.outputBuffers[i];
    }

    private boolean hasOutputBuffer() {
        return this.outputIndex >= 0;
    }

    private void resetInputBuffer() {
        this.inputIndex = -1;
        this.buffer.data = null;
    }

    private void resetOutputBuffer() {
        this.outputIndex = -1;
        this.outputBuffer = null;
    }

    private static CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer decoderInputBuffer, int i) {
        decoderInputBuffer = decoderInputBuffer.cryptoInfo.getFrameworkCryptoInfoV16();
        if (i == 0) {
            return decoderInputBuffer;
        }
        if (decoderInputBuffer.numBytesOfClearData == null) {
            decoderInputBuffer.numBytesOfClearData = new int[1];
        }
        int[] iArr = decoderInputBuffer.numBytesOfClearData;
        iArr[0] = iArr[0] + i;
        return decoderInputBuffer;
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

    protected void onInputFormatChanged(Format format) throws ExoPlaybackException {
        Format format2 = this.format;
        this.format = format;
        boolean z = true;
        if ((Util.areEqual(this.format.drmInitData, format2 == null ? null : format2.drmInitData) ^ 1) != null) {
            if (this.format.drmInitData == null) {
                this.pendingDrmSession = null;
            } else if (this.drmSessionManager == null) {
                throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
            } else {
                this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.format.drmInitData);
                if (this.pendingDrmSession == this.drmSession) {
                    this.drmSessionManager.releaseSession(this.pendingDrmSession);
                }
            }
        }
        if (this.pendingDrmSession == this.drmSession && this.codec != null && canReconfigureCodec(this.codec, this.codecInfo.adaptive, format2, this.format) != null) {
            this.codecReconfigured = true;
            this.codecReconfigurationState = 1;
            if (this.codecAdaptationWorkaroundMode != 2) {
                if (this.codecAdaptationWorkaroundMode != 1 || this.format.width != format2.width || this.format.height != format2.height) {
                    z = false;
                }
            }
            this.codecNeedsAdaptationWorkaroundBuffer = z;
        } else if (this.codecReceivedBuffers != null) {
            this.codecReinitializationState = 1;
        } else {
            releaseCodec();
            maybeInitCodec();
        }
    }

    public boolean isEnded() {
        return this.outputStreamEnded;
    }

    public boolean isReady() {
        return (this.format == null || this.waitingForKeys || (!isSourceReady() && !hasOutputBuffer() && (this.codecHotswapDeadlineMs == C0542C.TIME_UNSET || SystemClock.elapsedRealtime() >= this.codecHotswapDeadlineMs))) ? false : true;
    }

    private boolean drainOutputBuffer(long r16, long r18) throws org.telegram.messenger.exoplayer2.ExoPlaybackException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r15 = this;
        r12 = r15;
        r0 = r12.hasOutputBuffer();
        r13 = 1;
        r14 = 0;
        if (r0 != 0) goto L_0x009c;
    L_0x0009:
        r0 = r12.codecNeedsEosOutputExceptionWorkaround;
        if (r0 == 0) goto L_0x0029;
    L_0x000d:
        r0 = r12.codecReceivedEos;
        if (r0 == 0) goto L_0x0029;
    L_0x0011:
        r0 = r12.codec;	 Catch:{ IllegalStateException -> 0x001e }
        r1 = r12.outputBufferInfo;	 Catch:{ IllegalStateException -> 0x001e }
        r2 = r12.getDequeueOutputBufferTimeoutUs();	 Catch:{ IllegalStateException -> 0x001e }
        r0 = r0.dequeueOutputBuffer(r1, r2);	 Catch:{ IllegalStateException -> 0x001e }
        goto L_0x0035;
    L_0x001e:
        r12.processEndOfStream();
        r0 = r12.outputStreamEnded;
        if (r0 == 0) goto L_0x0028;
    L_0x0025:
        r12.releaseCodec();
    L_0x0028:
        return r14;
    L_0x0029:
        r0 = r12.codec;
        r1 = r12.outputBufferInfo;
        r2 = r12.getDequeueOutputBufferTimeoutUs();
        r0 = r0.dequeueOutputBuffer(r1, r2);
    L_0x0035:
        if (r0 < 0) goto L_0x007d;
    L_0x0037:
        r1 = r12.shouldSkipAdaptationWorkaroundOutputBuffer;
        if (r1 == 0) goto L_0x0043;
    L_0x003b:
        r12.shouldSkipAdaptationWorkaroundOutputBuffer = r14;
        r1 = r12.codec;
        r1.releaseOutputBuffer(r0, r14);
        return r13;
    L_0x0043:
        r1 = r12.outputBufferInfo;
        r1 = r1.flags;
        r1 = r1 & 4;
        if (r1 == 0) goto L_0x004f;
    L_0x004b:
        r12.processEndOfStream();
        return r14;
    L_0x004f:
        r12.outputIndex = r0;
        r0 = r12.getOutputBuffer(r0);
        r12.outputBuffer = r0;
        r0 = r12.outputBuffer;
        if (r0 == 0) goto L_0x0072;
    L_0x005b:
        r0 = r12.outputBuffer;
        r1 = r12.outputBufferInfo;
        r1 = r1.offset;
        r0.position(r1);
        r0 = r12.outputBuffer;
        r1 = r12.outputBufferInfo;
        r1 = r1.offset;
        r2 = r12.outputBufferInfo;
        r2 = r2.size;
        r1 = r1 + r2;
        r0.limit(r1);
    L_0x0072:
        r0 = r12.outputBufferInfo;
        r0 = r0.presentationTimeUs;
        r0 = r12.shouldSkipOutputBuffer(r0);
        r12.shouldSkipOutputBuffer = r0;
        goto L_0x009c;
    L_0x007d:
        r1 = -2;
        if (r0 != r1) goto L_0x0084;
    L_0x0080:
        r12.processOutputFormat();
        return r13;
    L_0x0084:
        r1 = -3;
        if (r0 != r1) goto L_0x008b;
    L_0x0087:
        r12.processOutputBuffersChanged();
        return r13;
    L_0x008b:
        r0 = r12.codecNeedsEosPropagationWorkaround;
        if (r0 == 0) goto L_0x009b;
    L_0x008f:
        r0 = r12.inputStreamEnded;
        if (r0 != 0) goto L_0x0098;
    L_0x0093:
        r0 = r12.codecReinitializationState;
        r1 = 2;
        if (r0 != r1) goto L_0x009b;
    L_0x0098:
        r12.processEndOfStream();
    L_0x009b:
        return r14;
    L_0x009c:
        r0 = r12.codecNeedsEosOutputExceptionWorkaround;
        if (r0 == 0) goto L_0x00c9;
    L_0x00a0:
        r0 = r12.codecReceivedEos;
        if (r0 == 0) goto L_0x00c9;
    L_0x00a4:
        r5 = r12.codec;	 Catch:{ IllegalStateException -> 0x00be }
        r6 = r12.outputBuffer;	 Catch:{ IllegalStateException -> 0x00be }
        r7 = r12.outputIndex;	 Catch:{ IllegalStateException -> 0x00be }
        r0 = r12.outputBufferInfo;	 Catch:{ IllegalStateException -> 0x00be }
        r8 = r0.flags;	 Catch:{ IllegalStateException -> 0x00be }
        r0 = r12.outputBufferInfo;	 Catch:{ IllegalStateException -> 0x00be }
        r9 = r0.presentationTimeUs;	 Catch:{ IllegalStateException -> 0x00be }
        r11 = r12.shouldSkipOutputBuffer;	 Catch:{ IllegalStateException -> 0x00be }
        r0 = r12;	 Catch:{ IllegalStateException -> 0x00be }
        r1 = r16;	 Catch:{ IllegalStateException -> 0x00be }
        r3 = r18;	 Catch:{ IllegalStateException -> 0x00be }
        r0 = r0.processOutputBuffer(r1, r3, r5, r6, r7, r8, r9, r11);	 Catch:{ IllegalStateException -> 0x00be }
        goto L_0x00e2;
    L_0x00be:
        r12.processEndOfStream();
        r0 = r12.outputStreamEnded;
        if (r0 == 0) goto L_0x00c8;
    L_0x00c5:
        r12.releaseCodec();
    L_0x00c8:
        return r14;
    L_0x00c9:
        r5 = r12.codec;
        r6 = r12.outputBuffer;
        r7 = r12.outputIndex;
        r0 = r12.outputBufferInfo;
        r8 = r0.flags;
        r0 = r12.outputBufferInfo;
        r9 = r0.presentationTimeUs;
        r11 = r12.shouldSkipOutputBuffer;
        r0 = r12;
        r1 = r16;
        r3 = r18;
        r0 = r0.processOutputBuffer(r1, r3, r5, r6, r7, r8, r9, r11);
    L_0x00e2:
        if (r0 == 0) goto L_0x00ef;
    L_0x00e4:
        r0 = r12.outputBufferInfo;
        r0 = r0.presentationTimeUs;
        r12.onProcessedOutputBuffer(r0);
        r12.resetOutputBuffer();
        return r13;
    L_0x00ef:
        return r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.mediacodec.MediaCodecRenderer.drainOutputBuffer(long, long):boolean");
    }

    private void processOutputFormat() throws ExoPlaybackException {
        MediaFormat outputFormat = this.codec.getOutputFormat();
        if (this.codecAdaptationWorkaroundMode != 0 && outputFormat.getInteger("width") == 32 && outputFormat.getInteger("height") == 32) {
            this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
            return;
        }
        if (this.codecNeedsMonoChannelCountWorkaround) {
            outputFormat.setInteger("channel-count", 1);
        }
        onOutputFormatChanged(this.codec, outputFormat);
    }

    private void processOutputBuffersChanged() {
        if (Util.SDK_INT < 21) {
            this.outputBuffers = this.codec.getOutputBuffers();
        }
    }

    private void processEndOfStream() throws ExoPlaybackException {
        if (this.codecReinitializationState == 2) {
            releaseCodec();
            maybeInitCodec();
            return;
        }
        this.outputStreamEnded = true;
        renderToEndOfStream();
    }

    private boolean shouldSkipOutputBuffer(long j) {
        int size = this.decodeOnlyPresentationTimestamps.size();
        for (int i = 0; i < size; i++) {
            if (((Long) this.decodeOnlyPresentationTimestamps.get(i)).longValue() == j) {
                this.decodeOnlyPresentationTimestamps.remove(i);
                return 1;
            }
        }
        return false;
    }

    @TargetApi(23)
    private static void configureMediaFormatForPlaybackV23(MediaFormat mediaFormat) {
        mediaFormat.setInteger("priority", 0);
    }

    private static boolean codecNeedsFlushWorkaround(String str) {
        if (Util.SDK_INT >= 18 && !(Util.SDK_INT == 18 && ("OMX.SEC.avc.dec".equals(str) || "OMX.SEC.avc.dec.secure".equals(str)))) {
            if (Util.SDK_INT == 19 && Util.MODEL.startsWith("SM-G800")) {
                if (!"OMX.Exynos.avc.dec".equals(str)) {
                    if ("OMX.Exynos.avc.dec.secure".equals(str) != null) {
                    }
                }
            }
            return null;
        }
        return true;
    }

    private int codecAdaptationWorkaroundMode(String str) {
        if (Util.SDK_INT <= 25 && "OMX.Exynos.avc.dec.secure".equals(str) && (Util.MODEL.startsWith("SM-T585") || Util.MODEL.startsWith("SM-A510") || Util.MODEL.startsWith("SM-A520") || Util.MODEL.startsWith("SM-J700"))) {
            return 2;
        }
        return (Util.SDK_INT >= 24 || ((!"OMX.Nvidia.h264.decode".equals(str) && "OMX.Nvidia.h264.decode.secure".equals(str) == null) || ("flounder".equals(Util.DEVICE) == null && "flounder_lte".equals(Util.DEVICE) == null && "grouper".equals(Util.DEVICE) == null && "tilapia".equals(Util.DEVICE) == null))) ? null : 1;
    }

    private static boolean codecNeedsDiscardToSpsWorkaround(String str, Format format) {
        return (Util.SDK_INT >= 21 || format.initializationData.isEmpty() == null || "OMX.MTK.VIDEO.DECODER.AVC".equals(str) == null) ? null : true;
    }

    private static boolean codecNeedsEosPropagationWorkaround(String str) {
        return (Util.SDK_INT > 17 || (!"OMX.rk.video_decoder.avc".equals(str) && "OMX.allwinner.video.decoder.avc".equals(str) == null)) ? null : true;
    }

    private static boolean codecNeedsEosFlushWorkaround(String str) {
        return ((Util.SDK_INT > 23 || !"OMX.google.vorbis.decoder".equals(str)) && (Util.SDK_INT > 19 || !"hb2000".equals(Util.DEVICE) || (!"OMX.amlogic.avc.decoder.awesome".equals(str) && "OMX.amlogic.avc.decoder.awesome.secure".equals(str) == null))) ? null : true;
    }

    private static boolean codecNeedsEosOutputExceptionWorkaround(String str) {
        return (Util.SDK_INT != 21 || "OMX.google.aac.decoder".equals(str) == null) ? null : true;
    }

    private static boolean codecNeedsMonoChannelCountWorkaround(String str, Format format) {
        if (Util.SDK_INT > 18 || format.channelCount != 1 || "OMX.MTK.AUDIO.DECODER.MP3".equals(str) == null) {
            return false;
        }
        return true;
    }
}
