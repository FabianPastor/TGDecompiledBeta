package org.telegram.messenger.exoplayer2.video;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.decoder.DecoderCounters;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecInfo;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecRenderer;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.TraceUtil;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener.EventDispatcher;
import org.telegram.messenger.volley.DefaultRetryPolicy;

@TargetApi(16)
public class MediaCodecVideoRenderer extends MediaCodecRenderer {
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    private static final String TAG = "MediaCodecVideoRenderer";
    private final long allowedJoiningTimeMs;
    private CodecMaxValues codecMaxValues;
    private int consecutiveDroppedFrameCount;
    private int currentHeight;
    private float currentPixelWidthHeightRatio;
    private int currentUnappliedRotationDegrees;
    private int currentWidth;
    private final boolean deviceNeedsAutoFrcWorkaround;
    private long droppedFrameAccumulationStartTimeMs;
    private int droppedFrames;
    private final EventDispatcher eventDispatcher;
    private final VideoFrameReleaseTimeHelper frameReleaseTimeHelper;
    private long joiningDeadlineMs;
    private int lastReportedHeight;
    private float lastReportedPixelWidthHeightRatio;
    private int lastReportedUnappliedRotationDegrees;
    private int lastReportedWidth;
    private final int maxDroppedFramesToNotify;
    private float pendingPixelWidthHeightRatio;
    private int pendingRotationDegrees;
    private boolean renderedFirstFrame;
    private int scalingMode;
    private Format[] streamFormats;
    private Surface surface;

    private static final class CodecMaxValues {
        public final int height;
        public final int inputSize;
        public final int width;

        public CodecMaxValues(int width, int height, int inputSize) {
            this.width = width;
            this.height = height;
            this.inputSize = inputSize;
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
        this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(context);
        this.eventDispatcher = new EventDispatcher(eventHandler, eventListener);
        this.deviceNeedsAutoFrcWorkaround = deviceNeedsAutoFrcWorkaround();
        this.joiningDeadlineMs = C.TIME_UNSET;
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        this.scalingMode = 1;
        clearLastReportedVideoSize();
    }

    protected int supportsFormat(MediaCodecSelector mediaCodecSelector, Format format) throws DecoderQueryException {
        String mimeType = format.sampleMimeType;
        if (!MimeTypes.isVideo(mimeType)) {
            return 0;
        }
        boolean requiresSecureDecryption = false;
        DrmInitData drmInitData = format.drmInitData;
        if (drmInitData != null) {
            for (int i = 0; i < drmInitData.schemeDataCount; i++) {
                requiresSecureDecryption |= drmInitData.get(i).requiresSecureDecryption;
            }
        }
        MediaCodecInfo decoderInfo = mediaCodecSelector.getDecoderInfo(mimeType, requiresSecureDecryption);
        if (decoderInfo == null) {
            return 1;
        }
        boolean decoderCapable = decoderInfo.isCodecSupported(format.codecs);
        if (decoderCapable && format.width > 0 && format.height > 0) {
            if (Util.SDK_INT >= 21) {
                decoderCapable = format.frameRate > 0.0f ? decoderInfo.isVideoSizeAndRateSupportedV21(format.width, format.height, (double) format.frameRate) : decoderInfo.isVideoSizeSupportedV21(format.width, format.height);
            } else {
                if (format.width * format.height <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                    decoderCapable = true;
                } else {
                    decoderCapable = false;
                }
                if (!decoderCapable) {
                    Log.d(TAG, "FalseCheck [legacyFrameSize, " + format.width + "x" + format.height + "] [" + Util.DEVICE_DEBUG_INFO + "]");
                }
            }
        }
        return (decoderInfo.adaptive ? 8 : 4) | (decoderCapable ? 3 : 2);
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
        super.onEnabled(joining);
        this.eventDispatcher.enabled(this.decoderCounters);
        this.frameReleaseTimeHelper.enable();
    }

    protected void onStreamChanged(Format[] formats) throws ExoPlaybackException {
        this.streamFormats = formats;
        super.onStreamChanged(formats);
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
        super.onPositionReset(positionUs, joining);
        this.renderedFirstFrame = false;
        this.consecutiveDroppedFrameCount = 0;
        long elapsedRealtime = (!joining || this.allowedJoiningTimeMs <= 0) ? C.TIME_UNSET : SystemClock.elapsedRealtime() + this.allowedJoiningTimeMs;
        this.joiningDeadlineMs = elapsedRealtime;
    }

    public boolean isReady() {
        if ((this.renderedFirstFrame || super.shouldInitCodec()) && super.isReady()) {
            this.joiningDeadlineMs = C.TIME_UNSET;
            return true;
        } else if (this.joiningDeadlineMs == C.TIME_UNSET) {
            return false;
        } else {
            if (SystemClock.elapsedRealtime() < this.joiningDeadlineMs) {
                return true;
            }
            this.joiningDeadlineMs = C.TIME_UNSET;
            return false;
        }
    }

    protected void onStarted() {
        super.onStarted();
        this.droppedFrames = 0;
        this.droppedFrameAccumulationStartTimeMs = SystemClock.elapsedRealtime();
    }

    protected void onStopped() {
        this.joiningDeadlineMs = C.TIME_UNSET;
        maybeNotifyDroppedFrames();
        super.onStopped();
    }

    protected void onDisabled() {
        this.currentWidth = -1;
        this.currentHeight = -1;
        this.currentPixelWidthHeightRatio = -1.0f;
        this.pendingPixelWidthHeightRatio = -1.0f;
        clearLastReportedVideoSize();
        this.frameReleaseTimeHelper.disable();
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
        } else if (messageType == 5) {
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
        this.renderedFirstFrame = false;
        clearLastReportedVideoSize();
        if (this.surface != surface) {
            this.surface = surface;
            int state = getState();
            if (state == 1 || state == 2) {
                releaseCodec();
                maybeInitCodec();
            }
        }
    }

    protected boolean shouldInitCodec() {
        return super.shouldInitCodec() && this.surface != null && this.surface.isValid();
    }

    protected void configureCodec(MediaCodec codec, Format format, MediaCrypto crypto) {
        this.codecMaxValues = getCodecMaxValues(format, this.streamFormats);
        codec.configure(getMediaFormat(format, this.codecMaxValues, this.deviceNeedsAutoFrcWorkaround), this.surface, crypto, 0);
    }

    protected void onCodecInitialized(String name, long initializedTimestampMs, long initializationDurationMs) {
        this.eventDispatcher.decoderInitialized(name, initializedTimestampMs, initializationDurationMs);
    }

    protected void onInputFormatChanged(Format newFormat) throws ExoPlaybackException {
        super.onInputFormatChanged(newFormat);
        this.eventDispatcher.inputFormatChanged(newFormat);
        this.pendingPixelWidthHeightRatio = getPixelWidthHeightRatio(newFormat);
        this.pendingRotationDegrees = getRotationDegrees(newFormat);
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
            this.currentPixelWidthHeightRatio = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT / this.currentPixelWidthHeightRatio;
        }
        setVideoScalingMode(codec, this.scalingMode);
    }

    protected boolean canReconfigureCodec(MediaCodec codec, boolean codecIsAdaptive, Format oldFormat, Format newFormat) {
        return areAdaptationCompatible(oldFormat, newFormat) && newFormat.width <= this.codecMaxValues.width && newFormat.height <= this.codecMaxValues.height && newFormat.maxInputSize <= this.codecMaxValues.inputSize && (codecIsAdaptive || (oldFormat.width == newFormat.width && oldFormat.height == newFormat.height));
    }

    protected boolean processOutputBuffer(long positionUs, long elapsedRealtimeUs, MediaCodec codec, ByteBuffer buffer, int bufferIndex, int bufferFlags, long bufferPresentationTimeUs, boolean shouldSkip) {
        if (shouldSkip) {
            skipOutputBuffer(codec, bufferIndex);
            return true;
        } else if (!this.renderedFirstFrame) {
            if (Util.SDK_INT >= 21) {
                renderOutputBufferV21(codec, bufferIndex, System.nanoTime());
            } else {
                renderOutputBuffer(codec, bufferIndex);
            }
            return true;
        } else if (getState() != 2) {
            return false;
        } else {
            long earlyUs = (bufferPresentationTimeUs - positionUs) - ((SystemClock.elapsedRealtime() * 1000) - elapsedRealtimeUs);
            long systemTimeNs = System.nanoTime();
            long adjustedReleaseTimeNs = this.frameReleaseTimeHelper.adjustReleaseTime(bufferPresentationTimeUs, systemTimeNs + (1000 * earlyUs));
            earlyUs = (adjustedReleaseTimeNs - systemTimeNs) / 1000;
            if (earlyUs < -30000) {
                dropOutputBuffer(codec, bufferIndex);
                return true;
            }
            if (Util.SDK_INT >= 21) {
                if (earlyUs < 50000) {
                    renderOutputBufferV21(codec, bufferIndex, adjustedReleaseTimeNs);
                    return true;
                }
            } else if (earlyUs < 30000) {
                if (earlyUs > 11000) {
                    try {
                        Thread.sleep((earlyUs - 10000) / 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                renderOutputBuffer(codec, bufferIndex);
                return true;
            }
            return false;
        }
    }

    private void skipOutputBuffer(MediaCodec codec, int bufferIndex) {
        TraceUtil.beginSection("skipVideoBuffer");
        codec.releaseOutputBuffer(bufferIndex, false);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.skippedOutputBufferCount++;
    }

    private void dropOutputBuffer(MediaCodec codec, int bufferIndex) {
        TraceUtil.beginSection("dropVideoBuffer");
        codec.releaseOutputBuffer(bufferIndex, false);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.droppedOutputBufferCount++;
        this.droppedFrames++;
        this.consecutiveDroppedFrameCount++;
        this.decoderCounters.maxConsecutiveDroppedOutputBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedOutputBufferCount);
        if (this.droppedFrames == this.maxDroppedFramesToNotify) {
            maybeNotifyDroppedFrames();
        }
    }

    private void renderOutputBuffer(MediaCodec codec, int bufferIndex) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(bufferIndex, true);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        if (!this.renderedFirstFrame) {
            this.renderedFirstFrame = true;
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    @TargetApi(21)
    private void renderOutputBufferV21(MediaCodec codec, int bufferIndex, long releaseTimeNs) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        codec.releaseOutputBuffer(bufferIndex, releaseTimeNs);
        TraceUtil.endSection();
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = 0;
        if (!this.renderedFirstFrame) {
            this.renderedFirstFrame = true;
            this.eventDispatcher.renderedFirstFrame(this.surface);
        }
    }

    private void clearLastReportedVideoSize() {
        this.lastReportedWidth = -1;
        this.lastReportedHeight = -1;
        this.lastReportedPixelWidthHeightRatio = -1.0f;
        this.lastReportedUnappliedRotationDegrees = -1;
    }

    private void maybeNotifyVideoSizeChanged() {
        if (this.lastReportedWidth != this.currentWidth || this.lastReportedHeight != this.currentHeight || this.lastReportedUnappliedRotationDegrees != this.currentUnappliedRotationDegrees || this.lastReportedPixelWidthHeightRatio != this.currentPixelWidthHeightRatio) {
            this.eventDispatcher.videoSizeChanged(this.currentWidth, this.currentHeight, this.currentUnappliedRotationDegrees, this.currentPixelWidthHeightRatio);
            this.lastReportedWidth = this.currentWidth;
            this.lastReportedHeight = this.currentHeight;
            this.lastReportedUnappliedRotationDegrees = this.currentUnappliedRotationDegrees;
            this.lastReportedPixelWidthHeightRatio = this.currentPixelWidthHeightRatio;
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

    @SuppressLint({"InlinedApi"})
    private static MediaFormat getMediaFormat(Format format, CodecMaxValues codecMaxValues, boolean deviceNeedsAutoFrcWorkaround) {
        MediaFormat frameworkMediaFormat = format.getFrameworkMediaFormatV16();
        frameworkMediaFormat.setInteger("max-width", codecMaxValues.width);
        frameworkMediaFormat.setInteger("max-height", codecMaxValues.height);
        if (codecMaxValues.inputSize != -1) {
            frameworkMediaFormat.setInteger("max-input-size", codecMaxValues.inputSize);
        }
        if (deviceNeedsAutoFrcWorkaround) {
            frameworkMediaFormat.setInteger("auto-frc", 0);
        }
        return frameworkMediaFormat;
    }

    private static CodecMaxValues getCodecMaxValues(Format format, Format[] streamFormats) {
        int maxWidth = format.width;
        int maxHeight = format.height;
        int maxInputSize = getMaxInputSize(format);
        for (Format streamFormat : streamFormats) {
            if (areAdaptationCompatible(format, streamFormat)) {
                maxWidth = Math.max(maxWidth, streamFormat.width);
                maxHeight = Math.max(maxHeight, streamFormat.height);
                maxInputSize = Math.max(maxInputSize, getMaxInputSize(streamFormat));
            }
        }
        return new CodecMaxValues(maxWidth, maxHeight, maxInputSize);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getMaxInputSize(Format format) {
        if (format.maxInputSize != -1) {
            return format.maxInputSize;
        }
        if (format.width == -1 || format.height == -1) {
            return -1;
        }
        int i;
        int maxPixels;
        int minCompressionRatio;
        String str = format.sampleMimeType;
        switch (str.hashCode()) {
            case -1664118616:
                if (str.equals(MimeTypes.VIDEO_H263)) {
                    i = 0;
                    break;
                }
            case -1662541442:
                if (str.equals(MimeTypes.VIDEO_H265)) {
                    i = 4;
                    break;
                }
            case 1187890754:
                if (str.equals(MimeTypes.VIDEO_MP4V)) {
                    i = 1;
                    break;
                }
            case 1331836730:
                if (str.equals("video/avc")) {
                    i = 2;
                    break;
                }
            case 1599127256:
                if (str.equals(MimeTypes.VIDEO_VP8)) {
                    i = 3;
                    break;
                }
            case 1599127257:
                if (str.equals(MimeTypes.VIDEO_VP9)) {
                    i = 5;
                    break;
                }
            default:
                i = -1;
                break;
        }
        switch (i) {
            case 0:
            case 1:
                maxPixels = format.width * format.height;
                minCompressionRatio = 2;
                break;
            case 2:
                if (!"BRAVIA 4K 2015".equals(Util.MODEL)) {
                    maxPixels = ((((format.width + 15) / 16) * ((format.height + 15) / 16)) * 16) * 16;
                    minCompressionRatio = 2;
                    break;
                }
                return -1;
            case 3:
                maxPixels = format.width * format.height;
                minCompressionRatio = 2;
                break;
            case 4:
            case 5:
                maxPixels = format.width * format.height;
                minCompressionRatio = 4;
                break;
            default:
                return -1;
        }
        return (maxPixels * 3) / (minCompressionRatio * 2);
    }

    private static void setVideoScalingMode(MediaCodec codec, int scalingMode) {
        codec.setVideoScalingMode(scalingMode);
    }

    private static boolean deviceNeedsAutoFrcWorkaround() {
        return Util.SDK_INT <= 22 && "foster".equals(Util.DEVICE) && "NVIDIA".equals(Util.MANUFACTURER);
    }

    private static boolean areAdaptationCompatible(Format first, Format second) {
        return first.sampleMimeType.equals(second.sampleMimeType) && getRotationDegrees(first) == getRotationDegrees(second);
    }

    private static float getPixelWidthHeightRatio(Format format) {
        return format.pixelWidthHeightRatio == -1.0f ? DefaultRetryPolicy.DEFAULT_BACKOFF_MULT : format.pixelWidthHeightRatio;
    }

    private static int getRotationDegrees(Format format) {
        return format.rotationDegrees == -1 ? 0 : format.rotationDegrees;
    }
}
