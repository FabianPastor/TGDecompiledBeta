package org.webrtc;

import android.os.Handler;
import android.os.HandlerThread;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import org.webrtc.EglBase;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

public class VideoFileRenderer implements VideoSink {
    private static final String TAG = "VideoFileRenderer";
    /* access modifiers changed from: private */
    public EglBase eglBase;
    private final HandlerThread fileThread;
    private final Handler fileThreadHandler;
    private int frameCount;
    private final int outputFileHeight;
    private final String outputFileName;
    private final int outputFileWidth;
    private final ByteBuffer outputFrameBuffer;
    private final int outputFrameSize;
    private final HandlerThread renderThread;
    private final Handler renderThreadHandler;
    private final FileOutputStream videoOutFile;
    /* access modifiers changed from: private */
    public YuvConverter yuvConverter;

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }

    public VideoFileRenderer(String outputFile, int outputFileWidth2, int outputFileHeight2, final EglBase.Context sharedContext) throws IOException {
        if (outputFileWidth2 % 2 == 1 || outputFileHeight2 % 2 == 1) {
            throw new IllegalArgumentException("Does not support uneven width or height");
        }
        this.outputFileName = outputFile;
        this.outputFileWidth = outputFileWidth2;
        this.outputFileHeight = outputFileHeight2;
        int i = ((outputFileWidth2 * outputFileHeight2) * 3) / 2;
        this.outputFrameSize = i;
        this.outputFrameBuffer = ByteBuffer.allocateDirect(i);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        this.videoOutFile = fileOutputStream;
        fileOutputStream.write(("YUV4MPEG2 CLASSNAME W" + outputFileWidth2 + " H" + outputFileHeight2 + " Ip var_:1 A1:1\n").getBytes(Charset.forName("US-ASCII")));
        HandlerThread handlerThread = new HandlerThread("VideoFileRendererRenderThread");
        this.renderThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        this.renderThreadHandler = handler;
        HandlerThread handlerThread2 = new HandlerThread("VideoFileRendererFileThread");
        this.fileThread = handlerThread2;
        handlerThread2.start();
        this.fileThreadHandler = new Handler(handlerThread2.getLooper());
        ThreadUtils.invokeAtFrontUninterruptibly(handler, (Runnable) new Runnable() {
            public void run() {
                EglBase unused = VideoFileRenderer.this.eglBase = EglBase.CC.create(sharedContext, EglBase.CONFIG_PIXEL_BUFFER);
                VideoFileRenderer.this.eglBase.createDummyPbufferSurface();
                VideoFileRenderer.this.eglBase.makeCurrent();
                YuvConverter unused2 = VideoFileRenderer.this.yuvConverter = new YuvConverter();
            }
        });
    }

    public void onFrame(VideoFrame frame) {
        frame.retain();
        this.renderThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda3(this, frame));
    }

    /* access modifiers changed from: private */
    /* renamed from: renderFrameOnRenderThread */
    public void m4125lambda$onFrame$0$orgwebrtcVideoFileRenderer(VideoFrame frame) {
        int cropHeight;
        int cropWidth;
        VideoFrame.Buffer buffer = frame.getBuffer();
        int targetWidth = frame.getRotation() % 180 == 0 ? this.outputFileWidth : this.outputFileHeight;
        int targetHeight = frame.getRotation() % 180 == 0 ? this.outputFileHeight : this.outputFileWidth;
        float frameAspectRatio = ((float) buffer.getWidth()) / ((float) buffer.getHeight());
        float fileAspectRatio = ((float) targetWidth) / ((float) targetHeight);
        int cropWidth2 = buffer.getWidth();
        int cropHeight2 = buffer.getHeight();
        if (fileAspectRatio > frameAspectRatio) {
            cropWidth = cropWidth2;
            cropHeight = (int) (((float) cropHeight2) * (frameAspectRatio / fileAspectRatio));
        } else {
            cropWidth = (int) (((float) cropWidth2) * (fileAspectRatio / frameAspectRatio));
            cropHeight = cropHeight2;
        }
        VideoFrame.Buffer scaledBuffer = buffer.cropAndScale((buffer.getWidth() - cropWidth) / 2, (buffer.getHeight() - cropHeight) / 2, cropWidth, cropHeight, targetWidth, targetHeight);
        frame.release();
        VideoFrame.I420Buffer i420 = scaledBuffer.toI420();
        scaledBuffer.release();
        this.fileThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda2(this, i420, frame));
    }

    /* renamed from: lambda$renderFrameOnRenderThread$1$org-webrtc-VideoFileRenderer  reason: not valid java name */
    public /* synthetic */ void m4128lambda$renderFrameOnRenderThread$1$orgwebrtcVideoFileRenderer(VideoFrame.I420Buffer i420, VideoFrame frame) {
        YuvHelper.I420Rotate(i420.getDataY(), i420.getStrideY(), i420.getDataU(), i420.getStrideU(), i420.getDataV(), i420.getStrideV(), this.outputFrameBuffer, i420.getWidth(), i420.getHeight(), frame.getRotation());
        i420.release();
        try {
            this.videoOutFile.write("FRAME\n".getBytes(Charset.forName("US-ASCII")));
            this.videoOutFile.write(this.outputFrameBuffer.array(), this.outputFrameBuffer.arrayOffset(), this.outputFrameSize);
            this.frameCount++;
        } catch (IOException e) {
            throw new RuntimeException("Error writing video to disk", e);
        }
    }

    public void release() {
        CountDownLatch cleanupBarrier = new CountDownLatch(1);
        this.renderThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda1(this, cleanupBarrier));
        ThreadUtils.awaitUninterruptibly(cleanupBarrier);
        this.fileThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda0(this));
        try {
            this.fileThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logging.e("VideoFileRenderer", "Interrupted while waiting for the write to disk to complete.", e);
        }
    }

    /* renamed from: lambda$release$2$org-webrtc-VideoFileRenderer  reason: not valid java name */
    public /* synthetic */ void m4126lambda$release$2$orgwebrtcVideoFileRenderer(CountDownLatch cleanupBarrier) {
        this.yuvConverter.release();
        this.eglBase.release();
        this.renderThread.quit();
        cleanupBarrier.countDown();
    }

    /* renamed from: lambda$release$3$org-webrtc-VideoFileRenderer  reason: not valid java name */
    public /* synthetic */ void m4127lambda$release$3$orgwebrtcVideoFileRenderer() {
        try {
            this.videoOutFile.close();
            Logging.d("VideoFileRenderer", "Video written to disk as " + this.outputFileName + ". The number of frames is " + this.frameCount + " and the dimensions of the frames are " + this.outputFileWidth + "x" + this.outputFileHeight + ".");
            this.fileThread.quit();
        } catch (IOException e) {
            throw new RuntimeException("Error closing output file", e);
        }
    }
}
