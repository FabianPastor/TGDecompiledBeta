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

    public VideoFileRenderer(String str, int i, int i2, final EglBase.Context context) throws IOException {
        if (i % 2 == 1 || i2 % 2 == 1) {
            throw new IllegalArgumentException("Does not support uneven width or height");
        }
        this.outputFileName = str;
        this.outputFileWidth = i;
        this.outputFileHeight = i2;
        int i3 = ((i * i2) * 3) / 2;
        this.outputFrameSize = i3;
        this.outputFrameBuffer = ByteBuffer.allocateDirect(i3);
        FileOutputStream fileOutputStream = new FileOutputStream(str);
        this.videoOutFile = fileOutputStream;
        fileOutputStream.write(("YUV4MPEG2 CLASSNAME W" + i + " H" + i2 + " Ip var_:1 A1:1\n").getBytes(Charset.forName("US-ASCII")));
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
                EglBase unused = VideoFileRenderer.this.eglBase = EglBase.CC.create(context, EglBase.CONFIG_PIXEL_BUFFER);
                VideoFileRenderer.this.eglBase.createDummyPbufferSurface();
                VideoFileRenderer.this.eglBase.makeCurrent();
                YuvConverter unused2 = VideoFileRenderer.this.yuvConverter = new YuvConverter();
            }
        });
    }

    public void onFrame(VideoFrame videoFrame) {
        videoFrame.retain();
        this.renderThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda3(this, videoFrame));
    }

    /* access modifiers changed from: private */
    /* renamed from: renderFrameOnRenderThread */
    public void lambda$onFrame$0(VideoFrame videoFrame) {
        VideoFrame.Buffer buffer = videoFrame.getBuffer();
        int i = videoFrame.getRotation() % 180 == 0 ? this.outputFileWidth : this.outputFileHeight;
        int i2 = videoFrame.getRotation() % 180 == 0 ? this.outputFileHeight : this.outputFileWidth;
        float width = ((float) buffer.getWidth()) / ((float) buffer.getHeight());
        float f = ((float) i) / ((float) i2);
        int width2 = buffer.getWidth();
        int height = buffer.getHeight();
        if (f > width) {
            height = (int) (((float) height) * (width / f));
        } else {
            width2 = (int) (((float) width2) * (f / width));
        }
        VideoFrame.Buffer cropAndScale = buffer.cropAndScale((buffer.getWidth() - width2) / 2, (buffer.getHeight() - height) / 2, width2, height, i, i2);
        videoFrame.release();
        VideoFrame.I420Buffer i420 = cropAndScale.toI420();
        cropAndScale.release();
        this.fileThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda2(this, i420, videoFrame));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$renderFrameOnRenderThread$1(VideoFrame.I420Buffer i420Buffer, VideoFrame videoFrame) {
        YuvHelper.I420Rotate(i420Buffer.getDataY(), i420Buffer.getStrideY(), i420Buffer.getDataU(), i420Buffer.getStrideU(), i420Buffer.getDataV(), i420Buffer.getStrideV(), this.outputFrameBuffer, i420Buffer.getWidth(), i420Buffer.getHeight(), videoFrame.getRotation());
        i420Buffer.release();
        try {
            this.videoOutFile.write("FRAME\n".getBytes(Charset.forName("US-ASCII")));
            this.videoOutFile.write(this.outputFrameBuffer.array(), this.outputFrameBuffer.arrayOffset(), this.outputFrameSize);
            this.frameCount++;
        } catch (IOException e) {
            throw new RuntimeException("Error writing video to disk", e);
        }
    }

    public void release() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.renderThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda1(this, countDownLatch));
        ThreadUtils.awaitUninterruptibly(countDownLatch);
        this.fileThreadHandler.post(new VideoFileRenderer$$ExternalSyntheticLambda0(this));
        try {
            this.fileThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logging.e("VideoFileRenderer", "Interrupted while waiting for the write to disk to complete.", e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$release$2(CountDownLatch countDownLatch) {
        this.yuvConverter.release();
        this.eglBase.release();
        this.renderThread.quit();
        countDownLatch.countDown();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$release$3() {
        try {
            this.videoOutFile.close();
            Logging.d("VideoFileRenderer", "Video written to disk as " + this.outputFileName + ". The number of frames is " + this.frameCount + " and the dimensions of the frames are " + this.outputFileWidth + "x" + this.outputFileHeight + ".");
            this.fileThread.quit();
        } catch (IOException e) {
            throw new RuntimeException("Error closing output file", e);
        }
    }
}
