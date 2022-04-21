package org.webrtc;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.SystemClock;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.FileLog;
import org.webrtc.EglBase;
import org.webrtc.EncodedImage;
import org.webrtc.ThreadUtils;
import org.webrtc.VideoDecoder;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

class AndroidVideoDecoder implements VideoDecoder, VideoSink {
    private static final int DEQUEUE_INPUT_TIMEOUT_US = 500000;
    private static final int DEQUEUE_OUTPUT_BUFFER_TIMEOUT_US = 100000;
    private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
    private static final String MEDIA_FORMAT_KEY_CROP_BOTTOM = "crop-bottom";
    private static final String MEDIA_FORMAT_KEY_CROP_LEFT = "crop-left";
    private static final String MEDIA_FORMAT_KEY_CROP_RIGHT = "crop-right";
    private static final String MEDIA_FORMAT_KEY_CROP_TOP = "crop-top";
    private static final String MEDIA_FORMAT_KEY_SLICE_HEIGHT = "slice-height";
    private static final String MEDIA_FORMAT_KEY_STRIDE = "stride";
    private static final String TAG = "AndroidVideoDecoder";
    private VideoDecoder.Callback callback;
    private MediaCodecWrapper codec;
    private final String codecName;
    private final VideoCodecMimeType codecType;
    private int colorFormat;
    private ThreadUtils.ThreadChecker decoderThreadChecker;
    private final Object dimensionLock = new Object();
    private final BlockingDeque<FrameInfo> frameInfos;
    private boolean hasDecodedFirstFrame;
    private int height;
    private boolean keyFrameRequired;
    private final MediaCodecWrapperFactory mediaCodecWrapperFactory;
    private Thread outputThread;
    /* access modifiers changed from: private */
    public ThreadUtils.ThreadChecker outputThreadChecker;
    private DecodedTextureMetadata renderedTextureMetadata;
    private final Object renderedTextureMetadataLock = new Object();
    /* access modifiers changed from: private */
    public volatile boolean running;
    private final EglBase.Context sharedContext;
    private volatile Exception shutdownException;
    private int sliceHeight;
    private int stride;
    private Surface surface;
    private SurfaceTextureHelper surfaceTextureHelper;
    private int width;

    public /* synthetic */ long createNativeVideoDecoder() {
        return VideoDecoder.CC.$default$createNativeVideoDecoder(this);
    }

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }

    private static class FrameInfo {
        final long decodeStartTimeMs;
        final int rotation;

        FrameInfo(long decodeStartTimeMs2, int rotation2) {
            this.decodeStartTimeMs = decodeStartTimeMs2;
            this.rotation = rotation2;
        }
    }

    private static class DecodedTextureMetadata {
        final Integer decodeTimeMs;
        final long presentationTimestampUs;

        DecodedTextureMetadata(long presentationTimestampUs2, Integer decodeTimeMs2) {
            this.presentationTimestampUs = presentationTimestampUs2;
            this.decodeTimeMs = decodeTimeMs2;
        }
    }

    AndroidVideoDecoder(MediaCodecWrapperFactory mediaCodecWrapperFactory2, String codecName2, VideoCodecMimeType codecType2, int colorFormat2, EglBase.Context sharedContext2) {
        if (isSupportedColorFormat(colorFormat2)) {
            Logging.d("AndroidVideoDecoder", "ctor name: " + codecName2 + " type: " + codecType2 + " color format: " + colorFormat2 + " context: " + sharedContext2);
            this.mediaCodecWrapperFactory = mediaCodecWrapperFactory2;
            this.codecName = codecName2;
            this.codecType = codecType2;
            this.colorFormat = colorFormat2;
            this.sharedContext = sharedContext2;
            this.frameInfos = new LinkedBlockingDeque();
            return;
        }
        throw new IllegalArgumentException("Unsupported color format: " + colorFormat2);
    }

    public VideoCodecStatus initDecode(VideoDecoder.Settings settings, VideoDecoder.Callback callback2) {
        this.decoderThreadChecker = new ThreadUtils.ThreadChecker();
        this.callback = callback2;
        if (this.sharedContext != null) {
            this.surfaceTextureHelper = createSurfaceTextureHelper();
            this.surface = new Surface(this.surfaceTextureHelper.getSurfaceTexture());
            this.surfaceTextureHelper.startListening(this);
        }
        return initDecodeInternal(settings.width, settings.height);
    }

    private VideoCodecStatus initDecodeInternal(int width2, int height2) {
        this.decoderThreadChecker.checkIsOnValidThread();
        Logging.d("AndroidVideoDecoder", "initDecodeInternal name: " + this.codecName + " type: " + this.codecType + " width: " + width2 + " height: " + height2);
        if (this.outputThread != null) {
            Logging.e("AndroidVideoDecoder", "initDecodeInternal called while the codec is already running");
            return VideoCodecStatus.FALLBACK_SOFTWARE;
        }
        this.width = width2;
        this.height = height2;
        this.stride = width2;
        this.sliceHeight = height2;
        this.hasDecodedFirstFrame = false;
        this.keyFrameRequired = true;
        try {
            this.codec = this.mediaCodecWrapperFactory.createByCodecName(this.codecName);
            try {
                MediaFormat format = MediaFormat.createVideoFormat(this.codecType.mimeType(), width2, height2);
                if (this.sharedContext == null) {
                    format.setInteger("color-format", this.colorFormat);
                }
                this.codec.configure(format, this.surface, (MediaCrypto) null, 0);
                this.codec.start();
                this.running = true;
                Thread createOutputThread = createOutputThread();
                this.outputThread = createOutputThread;
                createOutputThread.start();
                Logging.d("AndroidVideoDecoder", "initDecodeInternal done");
                return VideoCodecStatus.OK;
            } catch (IllegalArgumentException | IllegalStateException e) {
                Logging.e("AndroidVideoDecoder", "initDecode failed", e);
                release();
                return VideoCodecStatus.FALLBACK_SOFTWARE;
            }
        } catch (IOException | IllegalArgumentException | IllegalStateException e2) {
            Logging.e("AndroidVideoDecoder", "Cannot create media decoder " + this.codecName);
            return VideoCodecStatus.FALLBACK_SOFTWARE;
        }
    }

    public VideoCodecStatus decode(EncodedImage frame, VideoDecoder.DecodeInfo info) {
        int width2;
        int height2;
        VideoCodecStatus status;
        this.decoderThreadChecker.checkIsOnValidThread();
        boolean z = false;
        if (this.codec == null || this.callback == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("decode uninitalized, codec: ");
            if (this.codec != null) {
                z = true;
            }
            sb.append(z);
            sb.append(", callback: ");
            sb.append(this.callback);
            Logging.d("AndroidVideoDecoder", sb.toString());
            return VideoCodecStatus.UNINITIALIZED;
        } else if (frame.buffer == null) {
            Logging.e("AndroidVideoDecoder", "decode() - no input data");
            return VideoCodecStatus.ERR_PARAMETER;
        } else {
            int size = frame.buffer.remaining();
            if (size == 0) {
                Logging.e("AndroidVideoDecoder", "decode() - input buffer empty");
                return VideoCodecStatus.ERR_PARAMETER;
            }
            synchronized (this.dimensionLock) {
                width2 = this.width;
                height2 = this.height;
            }
            if (frame.encodedWidth * frame.encodedHeight > 0 && ((frame.encodedWidth != width2 || frame.encodedHeight != height2) && (status = reinitDecode(frame.encodedWidth, frame.encodedHeight)) != VideoCodecStatus.OK)) {
                return status;
            }
            if (!this.keyFrameRequired || frame.frameType == EncodedImage.FrameType.VideoFrameKey) {
                try {
                    int index = this.codec.dequeueInputBuffer(500000);
                    if (index < 0) {
                        Logging.e("AndroidVideoDecoder", "decode() - no HW buffers available; decoder falling behind");
                        return VideoCodecStatus.ERROR;
                    }
                    try {
                        ByteBuffer buffer = this.codec.getInputBuffers()[index];
                        if (buffer.capacity() < size) {
                            Logging.e("AndroidVideoDecoder", "decode() - HW buffer too small");
                            return VideoCodecStatus.ERROR;
                        }
                        buffer.put(frame.buffer);
                        this.frameInfos.offer(new FrameInfo(SystemClock.elapsedRealtime(), frame.rotation));
                        try {
                            this.codec.queueInputBuffer(index, 0, size, TimeUnit.NANOSECONDS.toMicros(frame.captureTimeNs), 0);
                            if (this.keyFrameRequired) {
                                this.keyFrameRequired = false;
                            }
                            return VideoCodecStatus.OK;
                        } catch (IllegalStateException e) {
                            Logging.e("AndroidVideoDecoder", "queueInputBuffer failed", e);
                            this.frameInfos.pollLast();
                            return VideoCodecStatus.ERROR;
                        }
                    } catch (IllegalStateException e2) {
                        Logging.e("AndroidVideoDecoder", "getInputBuffers failed", e2);
                        return VideoCodecStatus.ERROR;
                    }
                } catch (IllegalStateException e3) {
                    Logging.e("AndroidVideoDecoder", "dequeueInputBuffer failed", e3);
                    return VideoCodecStatus.ERROR;
                }
            } else {
                Logging.e("AndroidVideoDecoder", "decode() - key frame required first");
                return VideoCodecStatus.NO_OUTPUT;
            }
        }
    }

    public boolean getPrefersLateDecoding() {
        return true;
    }

    public String getImplementationName() {
        return this.codecName;
    }

    public VideoCodecStatus release() {
        Logging.d("AndroidVideoDecoder", "release");
        VideoCodecStatus status = releaseInternal();
        if (this.surface != null) {
            releaseSurface();
            this.surface = null;
            this.surfaceTextureHelper.stopListening();
            this.surfaceTextureHelper.dispose();
            this.surfaceTextureHelper = null;
        }
        synchronized (this.renderedTextureMetadataLock) {
            this.renderedTextureMetadata = null;
        }
        this.callback = null;
        this.frameInfos.clear();
        return status;
    }

    private VideoCodecStatus releaseInternal() {
        if (!this.running) {
            Logging.d("AndroidVideoDecoder", "release: Decoder is not running.");
            return VideoCodecStatus.OK;
        }
        try {
            this.running = false;
            if (!ThreadUtils.joinUninterruptibly(this.outputThread, 5000)) {
                Logging.e("AndroidVideoDecoder", "Media decoder release timeout", new RuntimeException());
                return VideoCodecStatus.TIMEOUT;
            } else if (this.shutdownException != null) {
                Logging.e("AndroidVideoDecoder", "Media decoder release error", new RuntimeException(this.shutdownException));
                this.shutdownException = null;
                VideoCodecStatus videoCodecStatus = VideoCodecStatus.ERROR;
                this.codec = null;
                this.outputThread = null;
                return videoCodecStatus;
            } else {
                this.codec = null;
                this.outputThread = null;
                return VideoCodecStatus.OK;
            }
        } finally {
            this.codec = null;
            this.outputThread = null;
        }
    }

    private VideoCodecStatus reinitDecode(int newWidth, int newHeight) {
        this.decoderThreadChecker.checkIsOnValidThread();
        VideoCodecStatus status = releaseInternal();
        if (status != VideoCodecStatus.OK) {
            return status;
        }
        return initDecodeInternal(newWidth, newHeight);
    }

    private Thread createOutputThread() {
        return new Thread("AndroidVideoDecoder.outputThread") {
            public void run() {
                ThreadUtils.ThreadChecker unused = AndroidVideoDecoder.this.outputThreadChecker = new ThreadUtils.ThreadChecker();
                while (AndroidVideoDecoder.this.running) {
                    AndroidVideoDecoder.this.deliverDecodedFrame();
                }
                AndroidVideoDecoder.this.releaseCodecOnOutputThread();
            }
        };
    }

    /* access modifiers changed from: protected */
    public void deliverDecodedFrame() {
        this.outputThreadChecker.checkIsOnValidThread();
        try {
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int result = this.codec.dequeueOutputBuffer(info, 100000);
            if (result == -2) {
                reformat(this.codec.getOutputFormat());
            } else if (result < 0) {
                Logging.v("AndroidVideoDecoder", "dequeueOutputBuffer returned " + result);
            } else {
                FrameInfo frameInfo = this.frameInfos.poll();
                Integer decodeTimeMs = null;
                int rotation = 0;
                if (frameInfo != null) {
                    decodeTimeMs = Integer.valueOf((int) (SystemClock.elapsedRealtime() - frameInfo.decodeStartTimeMs));
                    rotation = frameInfo.rotation;
                }
                this.hasDecodedFirstFrame = true;
                if (this.surfaceTextureHelper != null) {
                    deliverTextureFrame(result, info, rotation, decodeTimeMs);
                } else {
                    deliverByteFrame(result, info, rotation, decodeTimeMs);
                }
            }
        } catch (IllegalStateException e) {
            Logging.e("AndroidVideoDecoder", "deliverDecodedFrame failed", e);
        }
    }

    private void deliverTextureFrame(int index, MediaCodec.BufferInfo info, int rotation, Integer decodeTimeMs) {
        int width2;
        int height2;
        synchronized (this.dimensionLock) {
            width2 = this.width;
            height2 = this.height;
        }
        synchronized (this.renderedTextureMetadataLock) {
            if (this.renderedTextureMetadata != null) {
                this.codec.releaseOutputBuffer(index, false);
                return;
            }
            this.surfaceTextureHelper.setTextureSize(width2, height2);
            this.surfaceTextureHelper.setFrameRotation(rotation);
            this.renderedTextureMetadata = new DecodedTextureMetadata(info.presentationTimeUs, decodeTimeMs);
            this.codec.releaseOutputBuffer(index, true);
        }
    }

    public void onFrame(VideoFrame frame) {
        long timestampNs;
        Integer decodeTimeMs;
        synchronized (this.renderedTextureMetadataLock) {
            DecodedTextureMetadata decodedTextureMetadata = this.renderedTextureMetadata;
            if (decodedTextureMetadata != null) {
                timestampNs = decodedTextureMetadata.presentationTimestampUs * 1000;
                decodeTimeMs = this.renderedTextureMetadata.decodeTimeMs;
                this.renderedTextureMetadata = null;
            } else {
                throw new IllegalStateException("Rendered texture metadata was null in onTextureFrameAvailable.");
            }
        }
        this.callback.onDecodedFrame(new VideoFrame(frame.getBuffer(), frame.getRotation(), timestampNs), decodeTimeMs, (Integer) null);
    }

    private void deliverByteFrame(int result, MediaCodec.BufferInfo info, int rotation, Integer decodeTimeMs) {
        int width2;
        int height2;
        int stride2;
        int sliceHeight2;
        int stride3;
        VideoFrame.Buffer frameBuffer;
        synchronized (this.dimensionLock) {
            width2 = this.width;
            height2 = this.height;
            stride2 = this.stride;
            sliceHeight2 = this.sliceHeight;
        }
        if (info.size < ((width2 * height2) * 3) / 2) {
            Logging.e("AndroidVideoDecoder", "Insufficient output buffer size: " + info.size);
            return;
        }
        if (info.size >= ((stride2 * height2) * 3) / 2 || sliceHeight2 != height2 || stride2 <= width2) {
            stride3 = stride2;
        } else {
            stride3 = (info.size * 2) / (height2 * 3);
        }
        ByteBuffer buffer = this.codec.getOutputBuffers()[result];
        buffer.position(info.offset);
        buffer.limit(info.offset + info.size);
        ByteBuffer buffer2 = buffer.slice();
        if (this.colorFormat == 19) {
            frameBuffer = copyI420Buffer(buffer2, stride3, sliceHeight2, width2, height2);
        } else {
            frameBuffer = copyNV12ToI420Buffer(buffer2, stride3, sliceHeight2, width2, height2);
        }
        this.codec.releaseOutputBuffer(result, false);
        VideoFrame frame = new VideoFrame(frameBuffer, rotation, info.presentationTimeUs * 1000);
        this.callback.onDecodedFrame(frame, decodeTimeMs, (Integer) null);
        frame.release();
    }

    private VideoFrame.Buffer copyNV12ToI420Buffer(ByteBuffer buffer, int stride2, int sliceHeight2, int width2, int height2) {
        return new NV12Buffer(width2, height2, stride2, sliceHeight2, buffer, (Runnable) null).toI420();
    }

    private VideoFrame.Buffer copyI420Buffer(ByteBuffer buffer, int stride2, int sliceHeight2, int width2, int height2) {
        ByteBuffer byteBuffer = buffer;
        int i = stride2;
        int i2 = width2;
        int i3 = height2;
        if (i % 2 == 0) {
            int chromaWidth = (i2 + 1) / 2;
            int chromaHeight = sliceHeight2 % 2 == 0 ? (i3 + 1) / 2 : i3 / 2;
            int uvStride = i / 2;
            int yEnd = (i * i3) + 0;
            int uPos = (i * sliceHeight2) + 0;
            int uEnd = uPos + (uvStride * chromaHeight);
            int vPos = uPos + ((uvStride * sliceHeight2) / 2);
            int vEnd = vPos + (uvStride * chromaHeight);
            VideoFrame.I420Buffer frameBuffer = allocateI420Buffer(i2, i3);
            try {
                byteBuffer.limit(yEnd);
                byteBuffer.position(0);
                int vEnd2 = vEnd;
                int vPos2 = vPos;
                int yPos = uEnd;
                int uPos2 = uPos;
                int i4 = yEnd;
                try {
                    copyPlane(buffer.slice(), stride2, frameBuffer.getDataY(), frameBuffer.getStrideY(), width2, height2);
                    byteBuffer.limit(yPos);
                    byteBuffer.position(uPos2);
                    copyPlane(buffer.slice(), uvStride, frameBuffer.getDataU(), frameBuffer.getStrideU(), chromaWidth, chromaHeight);
                    if (sliceHeight2 % 2 == 1) {
                        byteBuffer.position(uPos2 + ((chromaHeight - 1) * uvStride));
                        ByteBuffer dataU = frameBuffer.getDataU();
                        dataU.position(frameBuffer.getStrideU() * chromaHeight);
                        dataU.put(byteBuffer);
                    }
                    byteBuffer.limit(vEnd2);
                    byteBuffer.position(vPos2);
                    copyPlane(buffer.slice(), uvStride, frameBuffer.getDataV(), frameBuffer.getStrideV(), chromaWidth, chromaHeight);
                    if (sliceHeight2 % 2 == 1) {
                        byteBuffer.position(vPos2 + ((chromaHeight - 1) * uvStride));
                        ByteBuffer dataV = frameBuffer.getDataV();
                        dataV.position(frameBuffer.getStrideV() * chromaHeight);
                        dataV.put(byteBuffer);
                    }
                } catch (Throwable th) {
                    e = th;
                    FileLog.e(e);
                    return frameBuffer;
                }
            } catch (Throwable th2) {
                e = th2;
                int i5 = vEnd;
                int i6 = vPos;
                int i7 = uPos;
                int i8 = yEnd;
                int yPos2 = uEnd;
                FileLog.e(e);
                return frameBuffer;
            }
            return frameBuffer;
        }
        throw new AssertionError("Stride is not divisible by two: " + stride2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00bd, code lost:
        if (r6.surfaceTextureHelper != null) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00c5, code lost:
        if (r7.containsKey("color-format") == false) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00c7, code lost:
        r6.colorFormat = r7.getInteger("color-format");
        org.webrtc.Logging.d("AndroidVideoDecoder", "Color: 0x" + java.lang.Integer.toHexString(r6.colorFormat));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00f1, code lost:
        if (isSupportedColorFormat(r6.colorFormat) != false) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00f3, code lost:
        stopOnOutputThread(new java.lang.IllegalStateException("Unsupported color format: " + r6.colorFormat));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x010e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x010f, code lost:
        r3 = r6.dimensionLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0111, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0118, code lost:
        if (r7.containsKey("stride") == false) goto L_0x0122;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x011a, code lost:
        r6.stride = r7.getInteger("stride");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0128, code lost:
        if (r7.containsKey("slice-height") == false) goto L_0x0132;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x012a, code lost:
        r6.sliceHeight = r7.getInteger("slice-height");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0132, code lost:
        org.webrtc.Logging.d("AndroidVideoDecoder", "Frame stride and slice height: " + r6.stride + " x " + r6.sliceHeight);
        r6.stride = java.lang.Math.max(r6.width, r6.stride);
        r6.sliceHeight = java.lang.Math.max(r6.height, r6.sliceHeight);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0168, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0169, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void reformat(android.media.MediaFormat r7) {
        /*
            r6 = this;
            org.webrtc.ThreadUtils$ThreadChecker r0 = r6.outputThreadChecker
            r0.checkIsOnValidThread()
            java.lang.String r0 = "AndroidVideoDecoder"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Decoder format changed: "
            r1.append(r2)
            java.lang.String r2 = r7.toString()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            org.webrtc.Logging.d(r0, r1)
            java.lang.String r0 = "crop-left"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-right"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-bottom"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-top"
            boolean r0 = r7.containsKey(r0)
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = "crop-right"
            int r0 = r7.getInteger(r0)
            int r0 = r0 + 1
            java.lang.String r1 = "crop-left"
            int r1 = r7.getInteger(r1)
            int r0 = r0 - r1
            java.lang.String r1 = "crop-bottom"
            int r1 = r7.getInteger(r1)
            int r1 = r1 + 1
            java.lang.String r2 = "crop-top"
            int r2 = r7.getInteger(r2)
            int r1 = r1 - r2
            goto L_0x006a
        L_0x005e:
            java.lang.String r0 = "width"
            int r0 = r7.getInteger(r0)
            java.lang.String r1 = "height"
            int r1 = r7.getInteger(r1)
        L_0x006a:
            java.lang.Object r2 = r6.dimensionLock
            monitor-enter(r2)
            int r3 = r6.width     // Catch:{ all -> 0x01a6 }
            if (r0 != r3) goto L_0x0075
            int r3 = r6.height     // Catch:{ all -> 0x01a6 }
            if (r1 == r3) goto L_0x00ba
        L_0x0075:
            boolean r3 = r6.hasDecodedFirstFrame     // Catch:{ all -> 0x01a6 }
            if (r3 == 0) goto L_0x00b0
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ all -> 0x01a6 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01a6 }
            r4.<init>()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "Unexpected size change. Configured "
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            int r5 = r6.width     // Catch:{ all -> 0x01a6 }
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "*"
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            int r5 = r6.height     // Catch:{ all -> 0x01a6 }
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = ". New "
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            r4.append(r0)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "*"
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            r4.append(r1)     // Catch:{ all -> 0x01a6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01a6 }
            r3.<init>(r4)     // Catch:{ all -> 0x01a6 }
            r6.stopOnOutputThread(r3)     // Catch:{ all -> 0x01a6 }
            monitor-exit(r2)     // Catch:{ all -> 0x01a6 }
            return
        L_0x00b0:
            if (r0 <= 0) goto L_0x016d
            if (r1 > 0) goto L_0x00b6
            goto L_0x016d
        L_0x00b6:
            r6.width = r0     // Catch:{ all -> 0x01a6 }
            r6.height = r1     // Catch:{ all -> 0x01a6 }
        L_0x00ba:
            monitor-exit(r2)     // Catch:{ all -> 0x01a6 }
            org.webrtc.SurfaceTextureHelper r2 = r6.surfaceTextureHelper
            if (r2 != 0) goto L_0x010f
            java.lang.String r2 = "color-format"
            boolean r2 = r7.containsKey(r2)
            if (r2 == 0) goto L_0x010f
            java.lang.String r2 = "color-format"
            int r2 = r7.getInteger(r2)
            r6.colorFormat = r2
            java.lang.String r2 = "AndroidVideoDecoder"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Color: 0x"
            r3.append(r4)
            int r4 = r6.colorFormat
            java.lang.String r4 = java.lang.Integer.toHexString(r4)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            org.webrtc.Logging.d(r2, r3)
            int r2 = r6.colorFormat
            boolean r2 = r6.isSupportedColorFormat(r2)
            if (r2 != 0) goto L_0x010f
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unsupported color format: "
            r3.append(r4)
            int r4 = r6.colorFormat
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            r6.stopOnOutputThread(r2)
            return
        L_0x010f:
            java.lang.Object r3 = r6.dimensionLock
            monitor-enter(r3)
            java.lang.String r2 = "stride"
            boolean r2 = r7.containsKey(r2)     // Catch:{ all -> 0x016a }
            if (r2 == 0) goto L_0x0122
            java.lang.String r2 = "stride"
            int r2 = r7.getInteger(r2)     // Catch:{ all -> 0x016a }
            r6.stride = r2     // Catch:{ all -> 0x016a }
        L_0x0122:
            java.lang.String r2 = "slice-height"
            boolean r2 = r7.containsKey(r2)     // Catch:{ all -> 0x016a }
            if (r2 == 0) goto L_0x0132
            java.lang.String r2 = "slice-height"
            int r2 = r7.getInteger(r2)     // Catch:{ all -> 0x016a }
            r6.sliceHeight = r2     // Catch:{ all -> 0x016a }
        L_0x0132:
            java.lang.String r2 = "AndroidVideoDecoder"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x016a }
            r4.<init>()     // Catch:{ all -> 0x016a }
            java.lang.String r5 = "Frame stride and slice height: "
            r4.append(r5)     // Catch:{ all -> 0x016a }
            int r5 = r6.stride     // Catch:{ all -> 0x016a }
            r4.append(r5)     // Catch:{ all -> 0x016a }
            java.lang.String r5 = " x "
            r4.append(r5)     // Catch:{ all -> 0x016a }
            int r5 = r6.sliceHeight     // Catch:{ all -> 0x016a }
            r4.append(r5)     // Catch:{ all -> 0x016a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x016a }
            org.webrtc.Logging.d(r2, r4)     // Catch:{ all -> 0x016a }
            int r2 = r6.width     // Catch:{ all -> 0x016a }
            int r4 = r6.stride     // Catch:{ all -> 0x016a }
            int r2 = java.lang.Math.max(r2, r4)     // Catch:{ all -> 0x016a }
            r6.stride = r2     // Catch:{ all -> 0x016a }
            int r2 = r6.height     // Catch:{ all -> 0x016a }
            int r4 = r6.sliceHeight     // Catch:{ all -> 0x016a }
            int r2 = java.lang.Math.max(r2, r4)     // Catch:{ all -> 0x016a }
            r6.sliceHeight = r2     // Catch:{ all -> 0x016a }
            monitor-exit(r3)     // Catch:{ all -> 0x016a }
            return
        L_0x016a:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x016a }
            throw r2
        L_0x016d:
            java.lang.String r3 = "AndroidVideoDecoder"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01a6 }
            r4.<init>()     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "Unexpected format dimensions. Configured "
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            int r5 = r6.width     // Catch:{ all -> 0x01a6 }
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "*"
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            int r5 = r6.height     // Catch:{ all -> 0x01a6 }
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = ". New "
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            r4.append(r0)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = "*"
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            r4.append(r1)     // Catch:{ all -> 0x01a6 }
            java.lang.String r5 = ". Skip it"
            r4.append(r5)     // Catch:{ all -> 0x01a6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01a6 }
            org.webrtc.Logging.w(r3, r4)     // Catch:{ all -> 0x01a6 }
            monitor-exit(r2)     // Catch:{ all -> 0x01a6 }
            return
        L_0x01a6:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x01a6 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.AndroidVideoDecoder.reformat(android.media.MediaFormat):void");
    }

    /* access modifiers changed from: private */
    public void releaseCodecOnOutputThread() {
        this.outputThreadChecker.checkIsOnValidThread();
        Logging.d("AndroidVideoDecoder", "Releasing MediaCodec on output thread");
        try {
            this.codec.stop();
        } catch (Exception e) {
            Logging.e("AndroidVideoDecoder", "Media decoder stop failed", e);
        }
        try {
            this.codec.release();
        } catch (Exception e2) {
            Logging.e("AndroidVideoDecoder", "Media decoder release failed", e2);
            this.shutdownException = e2;
        }
        Logging.d("AndroidVideoDecoder", "Release on output thread done");
    }

    private void stopOnOutputThread(Exception e) {
        this.outputThreadChecker.checkIsOnValidThread();
        this.running = false;
        this.shutdownException = e;
    }

    private boolean isSupportedColorFormat(int colorFormat2) {
        for (int supported : MediaCodecUtils.DECODER_COLOR_FORMATS) {
            if (supported == colorFormat2) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public SurfaceTextureHelper createSurfaceTextureHelper() {
        return SurfaceTextureHelper.create("decoder-texture-thread", this.sharedContext);
    }

    /* access modifiers changed from: protected */
    public void releaseSurface() {
        this.surface.release();
    }

    /* access modifiers changed from: protected */
    public VideoFrame.I420Buffer allocateI420Buffer(int width2, int height2) {
        return JavaI420Buffer.allocate(width2, height2);
    }

    /* access modifiers changed from: protected */
    public void copyPlane(ByteBuffer src, int srcStride, ByteBuffer dst, int dstStride, int width2, int height2) {
        YuvHelper.copyPlane(src, srcStride, dst, dstStride, width2, height2);
    }
}
