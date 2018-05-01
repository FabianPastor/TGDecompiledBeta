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

        public CodecMaxValues(int i, int i2, int i3) {
            this.width = i;
            this.height = i2;
            this.inputSize = i3;
        }
    }

    @TargetApi(23)
    private final class OnFrameRenderedListenerV23 implements OnFrameRenderedListener {
        private OnFrameRenderedListenerV23(MediaCodec mediaCodec) {
            mediaCodec.setOnFrameRenderedListener(this, new Handler());
        }

        public void onFrameRendered(MediaCodec mediaCodec, long j, long j2) {
            if (this == MediaCodecVideoRenderer.this.tunnelingOnFrameRenderedListener) {
                MediaCodecVideoRenderer.this.maybeNotifyRenderedFirstFrame();
            }
        }
    }

    private static boolean isBufferLate(long j) {
        return j < -30000;
    }

    private static boolean isBufferVeryLate(long j) {
        return j < -500000;
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
        this(context, mediaCodecSelector, 0);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long j) {
        this(context, mediaCodecSelector, j, null, null, -1);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long j, Handler handler, VideoRendererEventListener videoRendererEventListener, int i) {
        this(context, mediaCodecSelector, j, null, false, handler, videoRendererEventListener, i);
    }

    public MediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long j, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean z, Handler handler, VideoRendererEventListener videoRendererEventListener, int i) {
        super(2, mediaCodecSelector, drmSessionManager, z);
        this.allowedJoiningTimeMs = j;
        this.maxDroppedFramesToNotify = i;
        this.context = context.getApplicationContext();
        this.frameReleaseTimeHelper = new VideoFrameReleaseTimeHelper(context);
        this.eventDispatcher = new EventDispatcher(handler, videoRendererEventListener);
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
        String str = format.sampleMimeType;
        int i = 0;
        if (!MimeTypes.isVideo(str)) {
            return 0;
        }
        boolean z;
        DrmInitData drmInitData = format.drmInitData;
        if (drmInitData != null) {
            int i2 = 0;
            z = i2;
            while (i2 < drmInitData.schemeDataCount) {
                z |= drmInitData.get(i2).requiresSecureDecryption;
                i2++;
            }
        } else {
            z = false;
        }
        MediaCodecInfo decoderInfo = mediaCodecSelector.getDecoderInfo(str, z);
        int i3 = 2;
        if (decoderInfo == null) {
            if (!z || mediaCodecSelector.getDecoderInfo(str, false) == null) {
                i3 = 1;
            }
            return i3;
        } else if (BaseRenderer.supportsFormatDrm(drmSessionManager, drmInitData) == null) {
            return 2;
        } else {
            mediaCodecSelector = decoderInfo.isCodecSupported(format.codecs);
            if (mediaCodecSelector != null && format.width > null && format.height > null) {
                if (Util.SDK_INT >= 21) {
                    mediaCodecSelector = decoderInfo.isVideoSizeAndRateSupportedV21(format.width, format.height, (double) format.frameRate);
                } else {
                    mediaCodecSelector = format.width * format.height <= MediaCodecUtil.maxH264DecodableFrameSize() ? 1 : null;
                    if (mediaCodecSelector == null) {
                        drmSessionManager = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("FalseCheck [legacyFrameSize, ");
                        stringBuilder.append(format.width);
                        stringBuilder.append("x");
                        stringBuilder.append(format.height);
                        stringBuilder.append("] [");
                        stringBuilder.append(Util.DEVICE_DEBUG_INFO);
                        stringBuilder.append("]");
                        Log.d(drmSessionManager, stringBuilder.toString());
                    }
                }
            }
            drmSessionManager = decoderInfo.adaptive != null ? 16 : 8;
            if (decoderInfo.tunneling != null) {
                i = 32;
            }
            return (mediaCodecSelector != null ? 4 : 3) | (drmSessionManager | i);
        }
    }

    protected void onEnabled(boolean z) throws ExoPlaybackException {
        super.onEnabled(z);
        this.tunnelingAudioSessionId = getConfiguration().tunnelingAudioSessionId;
        this.tunneling = this.tunnelingAudioSessionId;
        this.eventDispatcher.enabled(this.decoderCounters);
        this.frameReleaseTimeHelper.enable();
    }

    protected void onStreamChanged(Format[] formatArr, long j) throws ExoPlaybackException {
        this.streamFormats = formatArr;
        if (this.outputStreamOffsetUs == C0542C.TIME_UNSET) {
            this.outputStreamOffsetUs = j;
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
            this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1] = j;
        }
        super.onStreamChanged(formatArr, j);
    }

    protected void onPositionReset(long j, boolean z) throws ExoPlaybackException {
        super.onPositionReset(j, z);
        clearRenderedFirstFrame();
        this.consecutiveDroppedFrameCount = 0;
        if (this.pendingOutputStreamOffsetCount != 0) {
            this.outputStreamOffsetUs = this.pendingOutputStreamOffsetsUs[this.pendingOutputStreamOffsetCount - 1];
            this.pendingOutputStreamOffsetCount = 0;
        }
        if (z) {
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

    public void handleMessage(int i, Object obj) throws ExoPlaybackException {
        if (i == 1) {
            setSurface((Surface) obj);
        } else if (i == 4) {
            this.scalingMode = ((Integer) obj).intValue();
            i = getCodec();
            if (i != 0) {
                setVideoScalingMode(i, this.scalingMode);
            }
        } else {
            super.handleMessage(i, obj);
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
                return;
            }
            maybeRenotifyVideoSizeChanged();
            clearRenderedFirstFrame();
            if (state == 2) {
                setJoiningDeadlineMs();
            }
        } else if (surface != null && surface != this.dummySurface) {
            maybeRenotifyVideoSizeChanged();
            maybeRenotifyRenderedFirstFrame();
        }
    }

    protected boolean shouldInitCodec(MediaCodecInfo mediaCodecInfo) {
        if (this.surface == null) {
            if (shouldUseDummySurface(mediaCodecInfo) == null) {
                return null;
            }
        }
        return true;
    }

    protected void configureCodec(MediaCodecInfo mediaCodecInfo, MediaCodec mediaCodec, Format format, MediaCrypto mediaCrypto) throws DecoderQueryException {
        this.codecMaxValues = getCodecMaxValues(mediaCodecInfo, format, this.streamFormats);
        format = getMediaFormat(format, this.codecMaxValues, this.deviceNeedsAutoFrcWorkaround, this.tunnelingAudioSessionId);
        if (this.surface == null) {
            Assertions.checkState(shouldUseDummySurface(mediaCodecInfo));
            if (this.dummySurface == null) {
                this.dummySurface = DummySurface.newInstanceV17(this.context, mediaCodecInfo.secure);
            }
            this.surface = this.dummySurface;
        }
        mediaCodec.configure(format, this.surface, mediaCrypto, 0);
        if (Util.SDK_INT >= 23 && this.tunneling != null) {
            this.tunnelingOnFrameRenderedListener = new OnFrameRenderedListenerV23(mediaCodec);
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

    protected void onCodecInitialized(String str, long j, long j2) {
        this.eventDispatcher.decoderInitialized(str, j, j2);
        this.codecNeedsSetOutputSurfaceWorkaround = codecNeedsSetOutputSurfaceWorkaround(str);
    }

    protected void onInputFormatChanged(Format format) throws ExoPlaybackException {
        super.onInputFormatChanged(format);
        this.eventDispatcher.inputFormatChanged(format);
        this.pendingPixelWidthHeightRatio = getPixelWidthHeightRatio(format);
        this.pendingRotationDegrees = getRotationDegrees(format);
    }

    protected void onQueueInputBuffer(DecoderInputBuffer decoderInputBuffer) {
        this.buffersInCodecCount++;
        if (Util.SDK_INT < 23 && this.tunneling != null) {
            maybeNotifyRenderedFirstFrame();
        }
    }

    protected void onOutputFormatChanged(MediaCodec mediaCodec, MediaFormat mediaFormat) {
        int integer;
        int i = (mediaFormat.containsKey(KEY_CROP_RIGHT) && mediaFormat.containsKey(KEY_CROP_LEFT) && mediaFormat.containsKey(KEY_CROP_BOTTOM) && mediaFormat.containsKey(KEY_CROP_TOP)) ? 1 : 0;
        if (i != 0) {
            integer = (mediaFormat.getInteger(KEY_CROP_RIGHT) - mediaFormat.getInteger(KEY_CROP_LEFT)) + 1;
        } else {
            integer = mediaFormat.getInteger("width");
        }
        this.currentWidth = integer;
        if (i != 0) {
            i = (mediaFormat.getInteger(KEY_CROP_BOTTOM) - mediaFormat.getInteger(KEY_CROP_TOP)) + 1;
        } else {
            i = mediaFormat.getInteger("height");
        }
        this.currentHeight = i;
        this.currentPixelWidthHeightRatio = this.pendingPixelWidthHeightRatio;
        if (Util.SDK_INT < 21) {
            this.currentUnappliedRotationDegrees = this.pendingRotationDegrees;
        } else if (this.pendingRotationDegrees == 90 || this.pendingRotationDegrees == 270) {
            mediaFormat = this.currentWidth;
            this.currentWidth = this.currentHeight;
            this.currentHeight = mediaFormat;
            this.currentPixelWidthHeightRatio = NUM / this.currentPixelWidthHeightRatio;
        }
        setVideoScalingMode(mediaCodec, this.scalingMode);
    }

    protected boolean canReconfigureCodec(MediaCodec mediaCodec, boolean z, Format format, Format format2) {
        return (areAdaptationCompatible(z, format, format2) == null || format2.width > this.codecMaxValues.width || format2.height > this.codecMaxValues.height || getMaxInputSize(format2) > this.codecMaxValues.inputSize) ? null : true;
    }

    protected boolean processOutputBuffer(long r25, long r27, android.media.MediaCodec r29, java.nio.ByteBuffer r30, int r31, int r32, long r33, boolean r35) throws org.telegram.messenger.exoplayer2.ExoPlaybackException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r24 = this;
        r7 = r24;
        r8 = r27;
        r10 = r29;
        r11 = r31;
        r0 = r33;
    L_0x000a:
        r2 = r7.pendingOutputStreamOffsetCount;
        r12 = 1;
        r13 = 0;
        if (r2 == 0) goto L_0x002d;
    L_0x0010:
        r2 = r7.pendingOutputStreamOffsetsUs;
        r3 = r2[r13];
        r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r2 < 0) goto L_0x002d;
    L_0x0018:
        r2 = r7.pendingOutputStreamOffsetsUs;
        r3 = r2[r13];
        r7.outputStreamOffsetUs = r3;
        r2 = r7.pendingOutputStreamOffsetCount;
        r2 = r2 - r12;
        r7.pendingOutputStreamOffsetCount = r2;
        r2 = r7.pendingOutputStreamOffsetsUs;
        r3 = r7.pendingOutputStreamOffsetsUs;
        r4 = r7.pendingOutputStreamOffsetCount;
        java.lang.System.arraycopy(r2, r12, r3, r13, r4);
        goto L_0x000a;
    L_0x002d:
        r2 = r7.outputStreamOffsetUs;
        r14 = r0 - r2;
        if (r35 == 0) goto L_0x0037;
    L_0x0033:
        r7.skipOutputBuffer(r10, r11, r14);
        return r12;
    L_0x0037:
        r2 = r0 - r25;
        r4 = r7.surface;
        r12 = r7.dummySurface;
        if (r4 != r12) goto L_0x004d;
    L_0x003f:
        r0 = isBufferLate(r2);
        if (r0 == 0) goto L_0x004c;
    L_0x0045:
        r7.forceRenderFrame = r13;
        r7.skipOutputBuffer(r10, r11, r14);
        r0 = 1;
        return r0;
    L_0x004c:
        return r13;
    L_0x004d:
        r4 = r7.renderedFirstFrame;
        if (r4 == 0) goto L_0x00e1;
    L_0x0051:
        r4 = r7.forceRenderFrame;
        if (r4 == 0) goto L_0x0057;
    L_0x0055:
        goto L_0x00e1;
    L_0x0057:
        r4 = r24.getState();
        r12 = 2;
        if (r4 == r12) goto L_0x005f;
    L_0x005e:
        return r13;
    L_0x005f:
        r16 = android.os.SystemClock.elapsedRealtime();
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r16 = r16 * r18;
        r20 = r16 - r8;
        r16 = r2 - r20;
        r2 = java.lang.System.nanoTime();
        r16 = r16 * r18;
        r22 = r14;
        r13 = r2 + r16;
        r4 = r7.frameReleaseTimeHelper;
        r12 = r4.adjustReleaseTime(r0, r13);
        r0 = r12 - r2;
        r14 = r0 / r18;
        r0 = r7.shouldDropBuffersToKeyframe(r14, r8);
        if (r0 == 0) goto L_0x0097;
    L_0x0085:
        r0 = r7;
        r1 = r10;
        r2 = r11;
        r3 = r22;
        r5 = r25;
        r0 = r0.maybeDropBuffersToKeyframe(r1, r2, r3, r5);
        if (r0 == 0) goto L_0x0097;
    L_0x0092:
        r0 = 1;
        r7.forceRenderFrame = r0;
        r0 = 0;
        return r0;
    L_0x0097:
        r0 = 1;
        r1 = r7.shouldDropOutputBuffer(r14, r8);
        if (r1 == 0) goto L_0x00a4;
    L_0x009e:
        r3 = r22;
        r7.dropOutputBuffer(r10, r11, r3);
        return r0;
    L_0x00a4:
        r3 = r22;
        r0 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x00be;
    L_0x00ac:
        r0 = 50000; // 0xc350 float:7.0065E-41 double:2.47033E-319;
        r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1));
        if (r2 >= 0) goto L_0x00bc;
    L_0x00b3:
        r0 = r7;
        r1 = r10;
        r2 = r11;
        r5 = r12;
        r0.renderOutputBufferV21(r1, r2, r3, r5);
        r0 = 1;
        return r0;
    L_0x00bc:
        r0 = 0;
        goto L_0x00e0;
    L_0x00be:
        r0 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
        r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1));
        if (r2 >= 0) goto L_0x00bc;
    L_0x00c4:
        r0 = 11000; // 0x2af8 float:1.5414E-41 double:5.4347E-320;
        r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x00db;
    L_0x00ca:
        r0 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r5 = r14 - r0;
        r5 = r5 / r18;	 Catch:{ InterruptedException -> 0x00d4 }
        java.lang.Thread.sleep(r5);	 Catch:{ InterruptedException -> 0x00d4 }
        goto L_0x00db;
    L_0x00d4:
        r0 = java.lang.Thread.currentThread();
        r0.interrupt();
    L_0x00db:
        r7.renderOutputBuffer(r10, r11, r3);
        r0 = 1;
        return r0;
    L_0x00e0:
        return r0;
    L_0x00e1:
        r0 = r13;
        r3 = r14;
        r7.forceRenderFrame = r0;
        r0 = org.telegram.messenger.exoplayer2.util.Util.SDK_INT;
        r1 = 21;
        if (r0 < r1) goto L_0x00f7;
    L_0x00eb:
        r5 = java.lang.System.nanoTime();
        r0 = r7;
        r1 = r10;
        r2 = r11;
        r0.renderOutputBufferV21(r1, r2, r3, r5);
    L_0x00f5:
        r0 = 1;
        goto L_0x00fb;
    L_0x00f7:
        r7.renderOutputBuffer(r10, r11, r3);
        goto L_0x00f5;
    L_0x00fb:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.video.MediaCodecVideoRenderer.processOutputBuffer(long, long, android.media.MediaCodec, java.nio.ByteBuffer, int, int, long, boolean):boolean");
    }

    protected void onProcessedOutputBuffer(long j) {
        this.buffersInCodecCount--;
    }

    protected boolean shouldDropOutputBuffer(long j, long j2) {
        return isBufferLate(j);
    }

    protected boolean shouldDropBuffersToKeyframe(long j, long j2) {
        return isBufferVeryLate(j);
    }

    protected void skipOutputBuffer(MediaCodec mediaCodec, int i, long j) {
        TraceUtil.beginSection("skipVideoBuffer");
        mediaCodec.releaseOutputBuffer(i, 0);
        TraceUtil.endSection();
        mediaCodec = this.decoderCounters;
        mediaCodec.skippedOutputBufferCount++;
    }

    protected void dropOutputBuffer(MediaCodec mediaCodec, int i, long j) {
        TraceUtil.beginSection("dropVideoBuffer");
        mediaCodec.releaseOutputBuffer(i, 0);
        TraceUtil.endSection();
        updateDroppedBufferCounters(1);
    }

    protected boolean maybeDropBuffersToKeyframe(MediaCodec mediaCodec, int i, long j, long j2) throws ExoPlaybackException {
        mediaCodec = skipSource(j2);
        if (mediaCodec == null) {
            return null;
        }
        i = this.decoderCounters;
        i.droppedToKeyframeCount++;
        updateDroppedBufferCounters(this.buffersInCodecCount + mediaCodec);
        flushCodec();
        return true;
    }

    protected void updateDroppedBufferCounters(int i) {
        DecoderCounters decoderCounters = this.decoderCounters;
        decoderCounters.droppedBufferCount += i;
        this.droppedFrames += i;
        this.consecutiveDroppedFrameCount += i;
        this.decoderCounters.maxConsecutiveDroppedBufferCount = Math.max(this.consecutiveDroppedFrameCount, this.decoderCounters.maxConsecutiveDroppedBufferCount);
        if (this.droppedFrames >= this.maxDroppedFramesToNotify) {
            maybeNotifyDroppedFrames();
        }
    }

    protected void renderOutputBuffer(MediaCodec mediaCodec, int i, long j) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        mediaCodec.releaseOutputBuffer(i, true);
        TraceUtil.endSection();
        mediaCodec = this.decoderCounters;
        mediaCodec.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = null;
        maybeNotifyRenderedFirstFrame();
    }

    @TargetApi(21)
    protected void renderOutputBufferV21(MediaCodec mediaCodec, int i, long j, long j2) {
        maybeNotifyVideoSizeChanged();
        TraceUtil.beginSection("releaseOutputBuffer");
        mediaCodec.releaseOutputBuffer(i, j2);
        TraceUtil.endSection();
        mediaCodec = this.decoderCounters;
        mediaCodec.renderedOutputBufferCount++;
        this.consecutiveDroppedFrameCount = null;
        maybeNotifyRenderedFirstFrame();
    }

    private boolean shouldUseDummySurface(MediaCodecInfo mediaCodecInfo) {
        return (Util.SDK_INT < 23 || this.tunneling || codecNeedsSetOutputSurfaceWorkaround(mediaCodecInfo.name) || (mediaCodecInfo.secure != null && DummySurface.isSecureSupported(this.context) == null)) ? null : true;
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
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.eventDispatcher.droppedFrames(this.droppedFrames, elapsedRealtime - this.droppedFrameAccumulationStartTimeMs);
            this.droppedFrames = 0;
            this.droppedFrameAccumulationStartTimeMs = elapsedRealtime;
        }
    }

    @TargetApi(23)
    private static void setOutputSurfaceV23(MediaCodec mediaCodec, Surface surface) {
        mediaCodec.setOutputSurface(surface);
    }

    @TargetApi(21)
    private static void configureTunnelingV21(MediaFormat mediaFormat, int i) {
        mediaFormat.setFeatureEnabled("tunneled-playback", true);
        mediaFormat.setInteger("audio-session-id", i);
    }

    protected CodecMaxValues getCodecMaxValues(MediaCodecInfo mediaCodecInfo, Format format, Format[] formatArr) throws DecoderQueryException {
        int i = format.width;
        int i2 = format.height;
        int maxInputSize = getMaxInputSize(format);
        if (formatArr.length == 1) {
            return new CodecMaxValues(i, i2, maxInputSize);
        }
        int length = formatArr.length;
        int i3 = i2;
        int i4 = maxInputSize;
        i2 = 0;
        maxInputSize = i;
        for (i = i2; i < length; i++) {
            Format format2 = formatArr[i];
            if (areAdaptationCompatible(mediaCodecInfo.adaptive, format, format2)) {
                int i5;
                if (format2.width != -1) {
                    if (format2.height != -1) {
                        i5 = 0;
                        i2 |= i5;
                        maxInputSize = Math.max(maxInputSize, format2.width);
                        i3 = Math.max(i3, format2.height);
                        i4 = Math.max(i4, getMaxInputSize(format2));
                    }
                }
                i5 = 1;
                i2 |= i5;
                maxInputSize = Math.max(maxInputSize, format2.width);
                i3 = Math.max(i3, format2.height);
                i4 = Math.max(i4, getMaxInputSize(format2));
            }
        }
        if (i2 != 0) {
            formatArr = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Resolutions unknown. Codec max resolution: ");
            stringBuilder.append(maxInputSize);
            stringBuilder.append("x");
            stringBuilder.append(i3);
            Log.w(formatArr, stringBuilder.toString());
            mediaCodecInfo = getCodecMaxSize(mediaCodecInfo, format);
            if (mediaCodecInfo != null) {
                maxInputSize = Math.max(maxInputSize, mediaCodecInfo.x);
                i3 = Math.max(i3, mediaCodecInfo.y);
                i4 = Math.max(i4, getMaxInputSize(format.sampleMimeType, maxInputSize, i3));
                mediaCodecInfo = TAG;
                format = new StringBuilder();
                format.append("Codec max resolution adjusted to: ");
                format.append(maxInputSize);
                format.append("x");
                format.append(i3);
                Log.w(mediaCodecInfo, format.toString());
            }
        }
        return new CodecMaxValues(maxInputSize, i3, i4);
    }

    @SuppressLint({"InlinedApi"})
    protected MediaFormat getMediaFormat(Format format, CodecMaxValues codecMaxValues, boolean z, int i) {
        format = getMediaFormatForPlayback(format);
        format.setInteger("max-width", codecMaxValues.width);
        format.setInteger("max-height", codecMaxValues.height);
        if (codecMaxValues.inputSize != -1) {
            format.setInteger("max-input-size", codecMaxValues.inputSize);
        }
        if (z) {
            format.setInteger("auto-frc", false);
        }
        if (i != 0) {
            configureTunnelingV21(format, i);
        }
        return format;
    }

    private static Point getCodecMaxSize(MediaCodecInfo mediaCodecInfo, Format format) throws DecoderQueryException {
        int i = 0;
        int i2 = format.height > format.width ? 1 : 0;
        int i3 = i2 != 0 ? format.height : format.width;
        int i4 = i2 != 0 ? format.width : format.height;
        float f = ((float) i4) / ((float) i3);
        int[] iArr = STANDARD_LONG_EDGE_VIDEO_PX;
        int length = iArr.length;
        while (i < length) {
            int i5 = iArr[i];
            int i6 = (int) (((float) i5) * f);
            if (i5 > i3) {
                if (i6 > i4) {
                    int i7;
                    if (Util.SDK_INT >= 21) {
                        i7 = i2 != 0 ? i6 : i5;
                        if (i2 == 0) {
                            i5 = i6;
                        }
                        Point alignVideoSizeV21 = mediaCodecInfo.alignVideoSizeV21(i7, i5);
                        if (mediaCodecInfo.isVideoSizeAndRateSupportedV21(alignVideoSizeV21.x, alignVideoSizeV21.y, (double) format.frameRate)) {
                            return alignVideoSizeV21;
                        }
                    } else {
                        i5 = Util.ceilDivide(i5, 16) * 16;
                        i7 = 16 * Util.ceilDivide(i6, 16);
                        if (i5 * i7 <= MediaCodecUtil.maxH264DecodableFrameSize()) {
                            format = i2 != 0 ? i7 : i5;
                            if (i2 != 0) {
                                i7 = i5;
                            }
                            return new Point(format, i7);
                        }
                    }
                    i++;
                }
            }
            return null;
        }
        return null;
    }

    private static int getMaxInputSize(Format format) {
        if (format.maxInputSize == -1) {
            return getMaxInputSize(format.sampleMimeType, format.width, format.height);
        }
        int i = 0;
        int i2 = 0;
        while (i < format.initializationData.size()) {
            i2 += ((byte[]) format.initializationData.get(i)).length;
            i++;
        }
        return format.maxInputSize + i2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getMaxInputSize(String str, int i, int i2) {
        if (i != -1) {
            if (i2 != -1) {
                int i3 = 4;
                switch (str.hashCode()) {
                    case -1664118616:
                        if (str.equals(MimeTypes.VIDEO_H263) != null) {
                            str = null;
                            break;
                        }
                    case -1662541442:
                        if (str.equals(MimeTypes.VIDEO_H265) != null) {
                            str = 4;
                            break;
                        }
                    case 1187890754:
                        if (str.equals(MimeTypes.VIDEO_MP4V) != null) {
                            str = true;
                            break;
                        }
                    case 1331836730:
                        if (str.equals("video/avc") != null) {
                            str = 2;
                            break;
                        }
                    case 1599127256:
                        if (str.equals(MimeTypes.VIDEO_VP8) != null) {
                            str = 3;
                            break;
                        }
                    case 1599127257:
                        if (str.equals(MimeTypes.VIDEO_VP9) != null) {
                            str = 5;
                            break;
                        }
                    default:
                }
                str = -1;
                switch (str) {
                    case null:
                    case 1:
                        i *= i2;
                        break;
                    case 2:
                        if ("BRAVIA 4K 2015".equals(Util.MODEL) == null) {
                            i = ((Util.ceilDivide(i, 16) * Util.ceilDivide(i2, 16)) * 16) * 16;
                            break;
                        }
                        return -1;
                    case 3:
                        i *= i2;
                        break;
                    case 4:
                    case 5:
                        i *= i2;
                        break;
                    default:
                        return -1;
                }
                i3 = 2;
                return (i * 3) / (2 * i3);
            }
        }
        return -1;
    }

    private static void setVideoScalingMode(MediaCodec mediaCodec, int i) {
        mediaCodec.setVideoScalingMode(i);
    }

    private static boolean deviceNeedsAutoFrcWorkaround() {
        return Util.SDK_INT <= 22 && "foster".equals(Util.DEVICE) && "NVIDIA".equals(Util.MANUFACTURER);
    }

    private static boolean codecNeedsSetOutputSurfaceWorkaround(String str) {
        if (!((("deb".equals(Util.DEVICE) || "flo".equals(Util.DEVICE)) && "OMX.qcom.video.decoder.avc".equals(str)) || (("tcl_eu".equals(Util.DEVICE) || "SVP-DTV15".equals(Util.DEVICE) || "BRAVIA_ATV2".equals(Util.DEVICE)) && "OMX.MTK.VIDEO.DECODER.AVC".equals(str)))) {
            if ("OMX.k3.video.decoder.avc".equals(str) == null || "ALE-L21".equals(Util.MODEL) == null) {
                return null;
            }
        }
        return true;
    }

    private static boolean areAdaptationCompatible(boolean z, Format format, Format format2) {
        return format.sampleMimeType.equals(format2.sampleMimeType) && getRotationDegrees(format) == getRotationDegrees(format2) && (z || (format.width == format2.width && format.height == format2.height));
    }

    private static float getPixelWidthHeightRatio(Format format) {
        return format.pixelWidthHeightRatio == -1.0f ? 1.0f : format.pixelWidthHeightRatio;
    }

    private static int getRotationDegrees(Format format) {
        return format.rotationDegrees == -1 ? null : format.rotationDegrees;
    }
}
