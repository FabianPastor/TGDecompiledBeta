package org.telegram.messenger.exoplayer2.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaCodec;
import android.media.MediaCodec.OnFrameRenderedListener;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.BaseRenderer;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecInfo;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecRenderer;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener.EventDispatcher;

@TargetApi(16)
public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    private static final int MAX_PENDING_OUTPUT_STREAM_OFFSET_COUNT = 10;
    private static final int[] STANDARD_LONG_EDGE_VIDEO_PX = new int[]{1920, 1600, 1440, 1280, 960, 854, 640, 540, 480};
    private static final String TAG = "MediaCodecVideoRenderer";
    private final long allowedJoiningTimeMs;
    private int buffersInCodecCount;
    private CodecMaxValues codecMaxValues;
    private boolean codecNeedsSetOutputSurfaceWorkaround;
    private int consecutiveDroppedFrameCount;
    private final Context context;
    private int currentHeight;
    private float currentPixelWidthHeightRatio;
    private int currentUnappliedRotationDegrees;
    private int currentWidth;
    private final boolean deviceNeedsAutoFrcWorkaround;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrames;
    private Surface dummySurface;
    private final EventDispatcher eventDispatcher;
    private boolean forceRenderFrame;
    private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
    private long joiningDeadlineMs;
    private final int maxDroppedFramesToNotify;
    private long outputStreamOffsetUs;
    private int pendingOutputStreamOffsetCount;
    private final long[] pendingOutputStreamOffsetsUs;
    private float pendingPixelWidthHeightRatio;
    private int pendingRotationDegrees;
    private boolean renderedFirstFrame;
    private int reportedHeight;
    private float reportedPixelWidthHeightRatio;
    private int reportedUnappliedRotationDegrees;
    private int reportedWidth;
    private int scalingMode;
    private Format[] streamFormats;
    private Surface surface;
    private boolean tunneling;
    private int tunnelingAudioSessionId;
    OnFrameRenderedListenerV23 tunnelingOnFrameRenderedListener;

    protected static final class CodecMaxValues {
        public final int height;
        public final int inputSize;
        public final int width;

        public CodecMaxValues(int width, int height, int inputSize) {
            this.width = width;
            this.height = height;
            this.inputSize = inputSize;
        }
    }

    @TargetApi(23)
    private final class OnFrameRenderedListenerV23 implements OnFrameRenderedListener {
        private OnFrameRenderedListenerV23(MediaCodec codec) {
            codec.setOnFrameRenderedListener(this, new Handler());
        }

        public void onFrameRendered(MediaCodec codec, long presentationTimeUs, long nanoTime) {
            if (this == MediaCodecVideoRenderer.this.tunnelingOnFrameRenderedListener) {
                MediaCodecVideoRenderer.this.maybeNotifyRenderedFirstFrame();
            }
        }
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
        this(context, mediaCodecSelector, 0);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, null, null, -1);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFrameCountToNotify) {
        this(context, mediaCodecSelector, allowedJoiningTimeMs, null, false, eventHandler, eventListener, maxDroppedFrameCountToNotify);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
        super(2, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys);
        this.allowedJoiningTimeMs = allowedJoiningTimeMs;
        this.maxDroppedFramesToNotify = maxDroppedFramesToNotify;
        this.context = context.getApplicationContext();
        this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(context);
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.deviceNeedsAutoFrcWorkaround = deviceNeedsAutoFrcWorkaround();
        this.pendingOutputStreamOffsetsUs = new long[10];
        this.outputStreamOffsetUs = C0542C.TIME_UNSET;
        this.joiningDeadlineMs = C0542C.TIME_UNSET;
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        this.scalingMode = 1;
        clearReportedVideoSize();
    }

    protected int supportsFormat(MediaCodecSelector mediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, Format format) throws DecoderQueryException {
        String mimeType = format.sampleMimeType;
        int tunnelingSupport = 0;
        if (!MimeTypes.isVideo(mimeType)) {
            return 0;
        }
        boolean requiresSecureDecryption = false;
        DrmInitData drmInitData = format.drmInitData;
        if (drmInitData != null) {
            boolean requiresSecureDecryption2 = false;
            for (int i = 0; i < drmInitData.schemeDataCount; i++) {
                requiresSecureDecryption2 |= drmInitData.get(i).requiresSecureDecryption;
            }
            requiresSecureDecryption = requiresSecureDecryption2;
        }
        MediaCodecInfo decoderInfo = mediaCodecSelector.getDecoderInfo(mimeType, requiresSecureDecryption);
        int i2 = 2;
        boolean z = true;
        if (decoderInfo == null) {
            if (!requiresSecureDecryption || mediaCodecSelector.getDecoderInfo(mimeType, false) == null) {
                i2 = 1;
            }
            return i2;
        } else if (!BaseRenderer.supportsFormatDrm(drmSessionManager, drmInitData)) {
            return 2;
        } else {
            boolean decoderCapable = decoderInfo.isCodecSupported(format.codecs);
            if (decoderCapable && format.width > 0 && format.height > 0) {
                if (Util.SDK_INT >= 21) {
                    decoderCapable = decoderInfo.isVideoSizeAndRateSupportedV21(format.width, format.height, (double) format.frameRate);
                } else {
                    if (format.width * format.height > MediaCodecUtil.maxH264DecodableFrameSize()) {
                        z = false;
                    }
                    decoderCapable = z;
                    if (!decoderCapable) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("FalseCheck [legacyFrameSize, ");
                        stringBuilder.append(format.width);
                        stringBuilder.append("x");
                        stringBuilder.append(format.height);
                        stringBuilder.append("] [");
                        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
                        stringBuilder.append("]");
                        Log.d(str, stringBuilder.toString());
                    }
                }
            }
            int adaptiveSupport = decoderInfo.adaptive ? 16 : 8;
            if (decoderInfo.tunneling) {
                tunnelingSupport = 32;
            }
            return (adaptiveSupport | tunnelingSupport) | (decoderCapable ? 4 : 3);
        }
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        this.tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
        this.tunneling = this.tunnelingAudioSessionId != 0;
        this.eventDispatcher.enabled(this.decoderCounters);
        this.frameReleaseTimeHelper.enable();
    }

    protected void onStreamChanged(Format[] formats, long offsetUs) throws ExoPlaybackException {
        this.streamFormats = formats;
        if (this.outputStreamOffsetUs == C0542C.TIME_UNSET) {
            this.outputStreamOffsetUs = offsetUs;
        } else {
            if (this.pendingOutputStreamOffsetCount == this.pendingOutputStreamOffsetsUs.length) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Too many stream changes, so dropping offset: ");
                stringBuilder.append(this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1]);
                Log.w(str, stringBuilder.toString());
            } else {
                this.pendingOutputStreamOffsetCount++;
            }
            this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1] = offsetUs;
        }
        super.onStreamChanged(formats, offsetUs);
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        clearRenderedFirstFrame();
        this.consecutiveDroppedFrameCount = 0;
        if (this.pendingOutputStreamOffsetCount != 0) {
            this.outputStreamOffsetUs = this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1];
            this.pendingOutputStreamOffsetCount = 0;
        }
        if (joining) {
            setJoiningDeadlineMs();
        } else {
            this.joiningDeadlineMs = C0542C.TIME_UNSET;
        }
    }

    public boolean isReady() {
        if (super.isReady() && (this.renderedFirstFrame || ((this.dummySurface != null && this.surface == this.dummySurface) || getCodec() == null || this.tunneling))) {
            this.joiningDeadlineMs = C0542C.TIME_UNSET;
            return true;
        } else if (this.joiningDeadlineMs == C0542C.TIME_UNSET) {
            return false;
        } else {
            if (SystemClock.elapsedRealtime() < this.joiningDeadlineMs) {
                return true;
            }
            this.joiningDeadlineMs = C0542C.TIME_UNSET;
            return false;
        }
    }

    protected void onStarted() {
        super.onStarted();
        this.droppedFrames = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
    }

    protected void onStopped() {
        this.joiningDeadlineMs = C0542C.TIME_UNSET;
        maybeNotifyDroppedFrames();
        super.onStopped();
    }

    protected void onDisabled() {
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        this.outputStreamOffsetUs = C0542C.TIME_UNSET;
        this.pendingOutputStreamOffsetCount = 0;
        clearReportedVideoSize();
        clearRenderedFirstFrame();
        this.frameReleaseTimeHelper.disable();
        this.tunnelingOnFrameRenderedListener = null;
        this.tunneling = false;
        try {
            super.onDisabled();
        } finally {
            this.decoderCounters.ensureUpdated();
            this.eventDispatcher.disabled(this.decoderCounters);
        }
    }

    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        if (messageType == 1) {
            setSurface((Surface) message);
        } else if (messageType == 4) {
            this.scalingMode = ((Integer) message).intValue();
            MediaCodec codec = getCodec();
            if (codec != null) {
                setVideoScalingMode(codec, this.scalingMode);
            }
        } else {
            super.handleMessage(messageType, message);
        }
    }

    private void setSurface(Surface surface) throws ExoPlaybackException {
        if (surface == null) {
            if (this.dummySurface != null) {
                surface = this.dummySurface;
            } else {
                MediaCodecInfo codecInfo = getCodecInfo();
                if (codecInfo != null && shouldUseDummySurface(codecInfo)) {
                    this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
                    surface = this.dummySurface;
                }
            }
        }
        if (this.surface != surface) {
            this.surface = surface;
            int state = getState();
            if (state == 1 || state == 2) {
                MediaCodec codec = getCodec();
                if (Util.SDK_INT < 23 || codec == null || surface == null || this.codecNeedsSetOutputSurfaceWorkaround) {
                    releaseCodec();
                    maybeInitCodec();
                } else {
                    setOutputSurfaceV23(codec, surface);
                }
            }
            if (surface == null || surface == this.dummySurface) {
                clearReportedVideoSize();
                clearRenderedFirstFrame();
            } else {
                maybeRenotifyVideoSizeChanged();
                clearRenderedFirstFrame();
                if (state == 2) {
                    setJoiningDeadlineMs();
                }
            }
        } else if (surface != null && surface != this.dummySurface) {
            maybeRenotifyVideoSizeChanged();
            maybeRenotifyRenderedFirstFrame();
        }
    }

    protected boolean shouldInitCodec(MediaCodecInfo codecInfo) {
        if (this.surface == null) {
            if (!shouldUseDummySurface(codecInfo)) {
                return false;
            }
        }
        return true;
    }

    protected void configureCodec(MediaCodecInfo codecInfo, MediaCodec codec, Format format, MediaCrypto crypto) throws DecoderQueryException {
        this.codecMaxValues = getCodecMaxValues(codecInfo, format, this.streamFormats);
        MediaFormat mediaFormat = getMediaFormat(format, this.codecMaxValues, this.deviceNeedsAutoFrcWorkaround, this.tunnelingAudioSessionId);
        if (this.surface == null) {
            Assertions.checkState(shouldUseDummySurface(codecInfo));
            if (this.dummySurface == null) {
                this.dummySurface = DummySurface.newInstanceV17(this.context, codecInfo.secure);
            }
            this.surface = this.dummySurface;
        }
        codec.configure(mediaFormat, this.surface, crypto, 0);
        if (Util.SDK_INT >= 23 && this.tunneling) {
            this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
        }
    }

    protected void releaseCodec() {
        try {
            super.releaseCodec();
        } finally {
            this.buffersInCodecCount = 0;
            this.forceRenderFrame = false;
            if (this.dummySurface != null) {
                if (this.surface == this.dummySurface) {
                    this.surface = null;
                }
                this.dummySurface.release();
                this.dummySurface = null;
            }
        }
    }

    protected void flushCodec() throws ExoPlaybackException {
        super.flushCodec();
        this.buffersInCodecCount = 0;
        this.forceRenderFrame = false;
    }

    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
        this.codecNeedsSetOutputSurfaceWorkaround = codecNeedsSetOutputSurfaceWorkaround(name);
    }

    protected void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        super.onInputFormatChanged(newFormat);
        this.eventDispatcher.inputFormatChanged(newFormat);
        this.pendingPixelWidthHeightRatio = getPixelWidthHeightRatio(newFormat);
        this.pendingRotationDegrees = getRotationDegrees(newFormat);
    }

    protected void onQueueInputBuffer(DecoderInputBuffer buffer) {
        this.buffersInCodecCount++;
        if (Util.SDK_INT < 23 && this.tunneling) {
            maybeNotifyRenderedFirstFrame();
        }
    }

    protected void onOutputFormatChanged(MediaCodec codec, MediaFormat outputFormat) {
        int integer;
        boolean hasCrop = outputFormat.containsKey(KEY_CROP_RIGHT) && outputFormat.containsKey(KEY_CROP_LEFT) && outputFormat.containsKey(KEY_CROP_BOTTOM) && outputFormat.containsKey(KEY_CROP_TOP);
        if (hasCrop) {
            integer = (outputFormat.getInteger(KEY_CROP_RIGHT) - outputFormat.getInteger(KEY_CROP_LEFT)) + 1;
        } else {
            integer = outputFormat.getInteger("width");
        }
        this.currentWidth = integer;
        if (hasCrop) {
            integer = (outputFormat.getInteger(KEY_CROP_BOTTOM) - outputFormat.getInteger(KEY_CROP_TOP)) + 1;
        } else {
            integer = outputFormat.getInteger("height");
        }
        this.currentHeight = integer;
        this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
        if (Util.SDK_INT < 21) {
            this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
        } else if (this.pendingRotationDegrees == 90 || this.pendingRotationDegrees == 270) {
            int rotatedHeight = this.currentWidth;
            this.currentWidth = this.currentHeight;
            this.currentHeight = rotatedHeight;
            this.currentPixelWidthHeightRatio = 1.0f / this.currentPixelWidthHeightRatio;
        }
        setVideoScalingMode(codec, this.scalingMode);
    }

    protected boolean canReconfigureCodec(MediaCodec codec, boolean codecIsAdaptive, Format oldFormat, Format newFormat) {
        return areAdaptationCompatible(codecIsAdaptive, oldFormat, newFormat) && newFormat.width <= this.codecMaxValues.width && newFormat.height <= this.codecMaxValues.height && getMaxInputSize(newFormat) <= this.codecMaxValues.inputSize;
    }

    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs, boolean shouldSkip) throws ExoPlaybackException {
        MediaCodecVideoRenderer mediaCodecVideoRenderer = this;
        long j = elapsedRealtimeUs;
        MediaCodec mediaCodec = codec;
        int i = bufferIndex;
        long j2 = bufferPresentationTimeUs;
        while (mediaCodecVideoRenderer.pendingOutputStreamOffsetCount != 0 && j2 >= mediaCodecVideoRenderer.pendingOutputStreamOffsetsUs[0]) {
            mediaCodecVideoRenderer.outputStreamOffsetUs = mediaCodecVideoRenderer.pendingOutputStreamOffsetsUs[0];
            mediaCodecVideoRenderer.pendingOutputStreamOffsetCount--;
            System.arraycopy(mediaCodecVideoRenderer.pendingOutputStreamOffsetsUs, 1, mediaCodecVideoRenderer.pendingOutputStreamOffsetsUs, 0, mediaCodecVideoRenderer.pendingOutputStreamOffsetCount);
        }
        long presentationTimeUs = j2 - mediaCodecVideoRenderer.outputStreamOffsetUs;
        if (shouldSkip) {
            skipOutputBuffer(mediaCodec, i, presentationTimeUs);
            return true;
        }
        long earlyUs = j2 - positionUs;
        if (mediaCodecVideoRenderer.surface != mediaCodecVideoRenderer.dummySurface) {
            boolean z;
            if (!mediaCodecVideoRenderer.renderedFirstFrame) {
                j = presentationTimeUs;
                z = false;
                presentationTimeUs = 21;
            } else if (mediaCodecVideoRenderer.forceRenderFrame) {
                j = presentationTimeUs;
                z = false;
                presentationTimeUs = 21;
            } else if (getState() != 2) {
                return false;
            } else {
                long presentationTimeUs2;
                long earlyUs2 = earlyUs - ((SystemClock.elapsedRealtime() * 1000) - j);
                long systemTimeNs = System.nanoTime();
                long unadjustedFrameReleaseTimeNs = systemTimeNs + (earlyUs2 * 1000);
                long adjustedReleaseTimeNs = mediaCodecVideoRenderer.frameReleaseTimeHelper.adjustReleaseTime(j2, unadjustedFrameReleaseTimeNs);
                earlyUs = (adjustedReleaseTimeNs - systemTimeNs) / 1000;
                if (shouldDropBuffersToKeyframe(earlyUs, j)) {
                    j2 = earlyUs;
                    presentationTimeUs2 = presentationTimeUs;
                    if (maybeDropBuffersToKeyframe(mediaCodec, i, presentationTimeUs, positionUs)) {
                        mediaCodecVideoRenderer.forceRenderFrame = true;
                        return false;
                    }
                    z = false;
                } else {
                    j2 = earlyUs;
                    presentationTimeUs2 = presentationTimeUs;
                    long j3 = unadjustedFrameReleaseTimeNs;
                    z = false;
                }
                if (shouldDropOutputBuffer(j2, j)) {
                    dropOutputBuffer(mediaCodec, i, presentationTimeUs2);
                    return true;
                }
                unadjustedFrameReleaseTimeNs = presentationTimeUs2;
                if (Util.SDK_INT < 21) {
                    j = unadjustedFrameReleaseTimeNs;
                    if (j2 < 30000) {
                        if (j2 > 11000) {
                            try {
                                Thread.sleep((j2 - 10000) / 1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        renderOutputBuffer(mediaCodec, i, j);
                        return true;
                    }
                } else if (j2 < 50000) {
                    renderOutputBufferV21(mediaCodec, i, unadjustedFrameReleaseTimeNs, adjustedReleaseTimeNs);
                    return true;
                }
                return z;
            }
            mediaCodecVideoRenderer.forceRenderFrame = z;
            if (Util.SDK_INT >= presentationTimeUs) {
                j2 = earlyUs;
                renderOutputBufferV21(mediaCodec, i, j, System.nanoTime());
            } else {
                renderOutputBuffer(mediaCodec, i, j);
            }
            return true;
        } else if (!isBufferLate(earlyUs)) {
            return false;
        } else {
            mediaCodecVideoRenderer.forceRenderFrame = false;
            skipOutputBuffer(mediaCodec, i, presentationTimeUs);
            return true;
        }
    }

    protected void onProcessedOutputBuffer(long presentationTimeUs) {
        this.buffersInCodecCount--;
    }

    protected boolean shouldDropOutputBuffer(long earlyUs, long elapsedRealtimeUs) {
        return isBufferLate(earlyUs);
    }

    protected boolean shouldDropBuffersToKeyframe(long earlyUs, long elapsedRealtimeUs) {
        return isBufferVeryLate(earlyUs);
    }

    protected void skipOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("skipVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.skippedOutputBufferCount++;
    }

    protected void dropOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        TraceUtil.beginSection("dropVideoBuffer");
        codec.releaseOutputBuffer(index, false);
        TraceUtil.endSection();
        updateDroppedBufferCounters(1);
    }

    protected boolean maybeDropBuffersToKeyframe(MediaCodec codec, int index, long presentationTimeUs, long positionUs) throws ExoPlaybackException {
        int droppedSourceBufferCount = skipSource(positionUs);
        if (droppedSourceBufferCount == 0) {
            return false;
        }
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.droppedToKeyframeCount++;
        updateDroppedBufferCounters(this.buffersInCodecCount + droppedSourceBufferCount);
        flushCodec();
        return true;
    }

    protected void updateDroppedBufferCounters(int droppedBufferCount) {
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.droppedBufferCount += droppedBufferCount;
        this.droppedFrames += droppedBufferCount;
        this.consecutiveDroppedFrameCount += droppedBufferCount;
        this.decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedBufferCount);
        if (this.droppedFrames >= this.maxDroppedFramesToNotify) {
            maybeNotifyDroppedFrames();
        }
    }

    protected void renderOutputBuffer(MediaCodec codec, int index, long presentationTimeUs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, true);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    @TargetApi(21)
    protected void renderOutputBufferV21(MediaCodec codec, int index, long presentationTimeUs, long releaseTimeNs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(index, releaseTimeNs);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        maybeNotifyRenderedFirstFrame();
    }

    private boolean shouldUseDummySurface(MediaCodecInfo codecInfo) {
        return Util.SDK_INT >= 23 && !this.tunneling && !codecNeedsSetOutputSurfaceWorkaround(codecInfo.name) && (!codecInfo.secure || DummySurface.isSecureSupported(this.context));
    }

    private void setJoiningDeadlineMs() {
        this.joiningDeadlineMs = this.allowedJoiningTimeMs > 0 ? SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs : C0542C.TIME_UNSET;
    }

    private void clearRenderedFirstFrame() {
        this.renderedFirstFrame = false;
        if (Util.SDK_INT >= 23 && this.tunneling) {
            MediaCodec codec = getCodec();
            if (codec != null) {
                this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(codec);
            }
        }
    }

    void maybeNotifyRenderedFirstFrame() {
        if (!this.renderedFirstFrame) {
            this.renderedFirstFrame = true;
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void maybeRenotifyRenderedFirstFrame() {
        if (this.renderedFirstFrame) {
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void clearReportedVideoSize() {
        this.reportedWidth = -1;
        this.reportedHeight = -1;
        this.reportedPixelWidthHeightRatio = -1.0f;
        this.reportedUnappliedRotationDegrees = -1;
    }

    private void maybeNotifyVideoSizeChanged() {
        if (this.currentWidth != -1 || this.currentHeight != -1) {
            if (this.reportedWidth != this.currentWidth || this.reportedHeight != this.currentHeight || this.reportedUnappliedRotationDegrees != this.currentUnappliedRotationDegrees || this.reportedPixelWidthHeightRatio != this.currentPixelWidthHeightRatio) {
                this.eventDispatcher.videoSizeChanged(this.currentWidth, this.currentHeight, this.currentUnappliedRotationDegrees, this.currentPixelWidthHeightRatio);
                this.reportedWidth = this.currentWidth;
                this.reportedHeight = this.currentHeight;
                this.reportedUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
                this.reportedPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
            }
        }
    }

    private void maybeRenotifyVideoSizeChanged() {
        if (this.reportedWidth != -1 || this.reportedHeight != -1) {
            this.eventDispatcher.videoSizeChanged(this.reportedWidth, this.reportedHeight, this.reportedUnappliedRotationDegrees, this.reportedPixelWidthHeightRatio);
        }
    }

    private void maybeNotifyDroppedFrames() {
        if (this.droppedFrames > 0) {
            long now = SystemClock.elapsedRealtime();
            this.eventDispatcher.droppedFrames(this.droppedFrames, now - this.droppedFrameAccumulationStartTimeMs);
            this.droppedFrames = 0;
            this.droppedFrameAccumulationStartTimeMs = now;
        }
    }

    private static boolean isBufferLate(long earlyUs) {
        return earlyUs < -30000;
    }

    private static boolean isBufferVeryLate(long earlyUs) {
        return earlyUs < -500000;
    }

    @TargetApi(23)
    private static void setOutputSurfaceV23(MediaCodec codec, Surface surface) {
        codec.setOutputSurface(surface);
    }

    @TargetApi(21)
    private static void configureTunnelingV21(MediaFormat mediaFormat, int tunnelingAudioSessionId) {
        mediaFormat.setFeatureEnabled("tunneled-playback", true);
        mediaFormat.setInteger("audio-session-id", tunnelingAudioSessionId);
    }

    protected CodecMaxValues getCodecMaxValues(MediaCodecInfo codecInfo, Format format, Format[] streamFormats) throws DecoderQueryException {
        int maxWidth = format.width;
        int maxHeight = format.height;
        int maxInputSize = getMaxInputSize(format);
        if (streamFormats.length == 1) {
            return new CodecMaxValues(maxWidth, maxHeight, maxInputSize);
        }
        boolean haveUnknownDimensions = false;
        int maxInputSize2 = maxInputSize;
        maxInputSize = maxHeight;
        maxHeight = maxWidth;
        for (Format streamFormat : streamFormats) {
            if (areAdaptationCompatible(codecInfo.adaptive, format, streamFormat)) {
                int i;
                if (streamFormat.width != -1) {
                    if (streamFormat.height != -1) {
                        i = 0;
                        haveUnknownDimensions |= i;
                        maxHeight = Math.max(maxHeight, streamFormat.width);
                        maxInputSize = Math.max(maxInputSize, streamFormat.height);
                        maxInputSize2 = Math.max(maxInputSize2, getMaxInputSize(streamFormat));
                    }
                }
                i = 1;
                haveUnknownDimensions |= i;
                maxHeight = Math.max(maxHeight, streamFormat.width);
                maxInputSize = Math.max(maxInputSize, streamFormat.height);
                maxInputSize2 = Math.max(maxInputSize2, getMaxInputSize(streamFormat));
            }
        }
        if (haveUnknownDimensions) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Resolutions unknown. Codec max resolution: ");
            stringBuilder.append(maxHeight);
            stringBuilder.append("x");
            stringBuilder.append(maxInputSize);
            Log.w(str, stringBuilder.toString());
            Point codecMaxSize = getCodecMaxSize(codecInfo, format);
            if (codecMaxSize != null) {
                maxHeight = Math.max(maxHeight, codecMaxSize.x);
                maxInputSize = Math.max(maxInputSize, codecMaxSize.y);
                maxInputSize2 = Math.max(maxInputSize2, getMaxInputSize(format.sampleMimeType, maxHeight, maxInputSize));
                String str2 = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Codec max resolution adjusted to: ");
                stringBuilder2.append(maxHeight);
                stringBuilder2.append("x");
                stringBuilder2.append(maxInputSize);
                Log.w(str2, stringBuilder2.toString());
            }
        }
        return new CodecMaxValues(maxHeight, maxInputSize, maxInputSize2);
    }

    @SuppressLint({"InlinedApi"})
    protected MediaFormat getMediaFormat(Format format, CodecMaxValues codecMaxValues, boolean deviceNeedsAutoFrcWorkaround, int tunnelingAudioSessionId) {
        MediaFormat frameworkMediaFormat = getMediaFormatForPlayback(format);
        frameworkMediaFormat.setInteger("max-width", codecMaxValues.width);
        frameworkMediaFormat.setInteger("max-height", codecMaxValues.height);
        if (codecMaxValues.inputSize != -1) {
            frameworkMediaFormat.setInteger("max-input-size", codecMaxValues.inputSize);
        }
        if (deviceNeedsAutoFrcWorkaround) {
            frameworkMediaFormat.setInteger("auto-frc", 0);
        }
        if (tunnelingAudioSessionId != 0) {
            configureTunnelingV21(frameworkMediaFormat, tunnelingAudioSessionId);
        }
        return frameworkMediaFormat;
    }

    private static Point getCodecMaxSize(MediaCodecInfo codecInfo, Format format) throws DecoderQueryException {
        float f;
        MediaCodecInfo mediaCodecInfo = codecInfo;
        Format format2 = format;
        int i = 0;
        boolean isVerticalVideo = format2.height > format2.width;
        int formatLongEdgePx = isVerticalVideo ? format2.height : format2.width;
        int formatShortEdgePx = isVerticalVideo ? format2.width : format2.height;
        float aspectRatio = ((float) formatShortEdgePx) / ((float) formatLongEdgePx);
        int[] iArr = STANDARD_LONG_EDGE_VIDEO_PX;
        int length = iArr.length;
        while (i < length) {
            int longEdgePx = iArr[i];
            int shortEdgePx = (int) (((float) longEdgePx) * aspectRatio);
            if (longEdgePx <= formatLongEdgePx) {
                f = aspectRatio;
            } else if (shortEdgePx <= formatShortEdgePx) {
                r15 = formatShortEdgePx;
                f = aspectRatio;
            } else {
                if (Util.SDK_INT >= 21) {
                    Point alignedSize = mediaCodecInfo.alignVideoSizeV21(isVerticalVideo ? shortEdgePx : longEdgePx, isVerticalVideo ? longEdgePx : shortEdgePx);
                    r15 = formatShortEdgePx;
                    f = aspectRatio;
                    if (mediaCodecInfo.isVideoSizeAndRateSupportedV21(alignedSize.x, alignedSize.y, (double) format2.frameRate) != 0) {
                        return alignedSize;
                    }
                } else {
                    r15 = formatShortEdgePx;
                    f = aspectRatio;
                    int longEdgePx2 = Util.ceilDivide(longEdgePx, 16) * 16;
                    formatShortEdgePx = 16 * Util.ceilDivide(shortEdgePx, 16);
                    if (longEdgePx2 * formatShortEdgePx <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                        return new Point(isVerticalVideo ? formatShortEdgePx : longEdgePx2, isVerticalVideo ? longEdgePx2 : formatShortEdgePx);
                    }
                }
                i++;
                formatShortEdgePx = r15;
                aspectRatio = f;
            }
            return null;
        }
        f = aspectRatio;
        return null;
    }

    private static int getMaxInputSize(Format format) {
        if (format.maxInputSize == -1) {
            return getMaxInputSize(format.sampleMimeType, format.width, format.height);
        }
        int totalInitializationDataSize = 0;
        for (int i = 0; i < format.initializationData.size(); i++) {
            totalInitializationDataSize += ((byte[]) format.initializationData.get(i)).length;
        }
        return format.maxInputSize + totalInitializationDataSize;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getMaxInputSize(String sampleMimeType, int width, int height) {
        if (width != -1) {
            if (height != -1) {
                int i;
                int maxPixels;
                switch (sampleMimeType.hashCode()) {
                    case -1664118616:
                        if (sampleMimeType.equals(MimeTypes.VIDEO_H263)) {
                            i = 0;
                            break;
                        }
                    case -1662541442:
                        if (sampleMimeType.equals(MimeTypes.VIDEO_H265)) {
                            i = 4;
                            break;
                        }
                    case 1187890754:
                        if (sampleMimeType.equals(MimeTypes.VIDEO_MP4V)) {
                            i = 1;
                            break;
                        }
                    case 1331836730:
                        if (sampleMimeType.equals("video/avc")) {
                            i = 2;
                            break;
                        }
                    case 1599127256:
                        if (sampleMimeType.equals(MimeTypes.VIDEO_VP8)) {
                            i = 3;
                            break;
                        }
                    case 1599127257:
                        if (sampleMimeType.equals(MimeTypes.VIDEO_VP9)) {
                            i = 5;
                            break;
                        }
                    default:
                }
                i = -1;
                switch (i) {
                    case 0:
                    case 1:
                        maxPixels = width * height;
                        i = 2;
                        break;
                    case 2:
                        if (!"BRAVIA 4K 2015".equals(Util.MODEL)) {
                            maxPixels = 16 * ((Util.ceilDivide(width, 16) * Util.ceilDivide(height, 16)) * 16);
                            i = 2;
                            break;
                        }
                        return -1;
                    case 3:
                        maxPixels = width * height;
                        i = 2;
                        break;
                    case 4:
                    case 5:
                        maxPixels = width * height;
                        i = 4;
                        break;
                    default:
                        return -1;
                }
                return (maxPixels * 3) / (2 * i);
            }
        }
        return -1;
    }

    private static void setVideoScalingMode(MediaCodec codec, int scalingMode) {
        codec.setVideoScalingMode(scalingMode);
    }

    private static boolean deviceNeedsAutoFrcWorkaround() {
        return Util.SDK_INT <= 22 && "foster".equals(Util.DEVICE) && "NVIDIA".equals(Util.MANUFACTURER);
    }

    private static boolean codecNeedsSetOutputSurfaceWorkaround(String name) {
        if (!((("deb".equals(Util.DEVICE) || "flo".equals(Util.DEVICE)) && "OMX.qcom.video.decoder.avc".equals(name)) || (("tcl_eu".equals(Util.DEVICE) || "SVP-DTV15".equals(Util.DEVICE) || "BRAVIA_ATV2".equals(Util.DEVICE)) && "OMX.MTK.VIDEO.DECODER.AVC".equals(name)))) {
            if (!"OMX.k3.video.decoder.avc".equals(name) || !"ALE-L21".equals(Util.MODEL)) {
                return false;
            }
        }
        return true;
    }

    private static boolean areAdaptationCompatible(boolean codecIsAdaptive, Format first, Format second) {
        return first.sampleMimeType.equals(second.sampleMimeType) && getRotationDegrees(first) == getRotationDegrees(second) && (codecIsAdaptive || (first.width == second.width && first.height == second.height));
    }

    private static float getPixelWidthHeightRatio(Format format) {
        return format.pixelWidthHeightRatio == -1.0f ? 1.0f : format.pixelWidthHeightRatio;
    }

    private static int getRotationDegrees(Format format) {
        return format.rotationDegrees == -1 ? 0 : format.rotationDegrees;
    }
}
