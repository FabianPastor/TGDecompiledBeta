package org.webrtc;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import java.util.concurrent.Callable;
import org.webrtc.EglBase;
import org.webrtc.TextureBufferImpl;
import org.webrtc.VideoFrame;

public class SurfaceTextureHelper {
    private static final String TAG = "SurfaceTextureHelper";
    private final EglBase eglBase;
    /* access modifiers changed from: private */
    public final FrameRefMonitor frameRefMonitor;
    private int frameRotation;
    private final Handler handler;
    /* access modifiers changed from: private */
    public boolean hasPendingTexture;
    private boolean isQuitting;
    private volatile boolean isTextureInUse;
    /* access modifiers changed from: private */
    public VideoSink listener;
    private final int oesTextureId;
    /* access modifiers changed from: private */
    public VideoSink pendingListener;
    final Runnable setListenerRunnable;
    private final SurfaceTexture surfaceTexture;
    private int textureHeight;
    private final TextureBufferImpl.RefCountMonitor textureRefCountMonitor;
    private int textureWidth;
    private final TimestampAligner timestampAligner;
    private final YuvConverter yuvConverter;

    public interface FrameRefMonitor {
        void onDestroyBuffer(VideoFrame.TextureBuffer textureBuffer);

        void onNewBuffer(VideoFrame.TextureBuffer textureBuffer);

        void onReleaseBuffer(VideoFrame.TextureBuffer textureBuffer);

        void onRetainBuffer(VideoFrame.TextureBuffer textureBuffer);
    }

    public static SurfaceTextureHelper create(String str, EglBase.Context context, boolean z, YuvConverter yuvConverter2, FrameRefMonitor frameRefMonitor2) {
        HandlerThread handlerThread = new HandlerThread(str);
        handlerThread.start();
        Handler handler2 = new Handler(handlerThread.getLooper());
        final EglBase.Context context2 = context;
        final Handler handler3 = handler2;
        final boolean z2 = z;
        final YuvConverter yuvConverter3 = yuvConverter2;
        final FrameRefMonitor frameRefMonitor3 = frameRefMonitor2;
        final String str2 = str;
        return (SurfaceTextureHelper) ThreadUtils.invokeAtFrontUninterruptibly(handler2, new Callable<SurfaceTextureHelper>() {
            public SurfaceTextureHelper call() {
                try {
                    return new SurfaceTextureHelper(context2, handler3, z2, yuvConverter3, frameRefMonitor3);
                } catch (RuntimeException e) {
                    Logging.e("SurfaceTextureHelper", str2 + " create failure", e);
                    return null;
                }
            }
        });
    }

    public static SurfaceTextureHelper create(String str, EglBase.Context context) {
        return create(str, context, false, new YuvConverter(), (FrameRefMonitor) null);
    }

    public static SurfaceTextureHelper create(String str, EglBase.Context context, boolean z) {
        return create(str, context, z, new YuvConverter(), (FrameRefMonitor) null);
    }

    public static SurfaceTextureHelper create(String str, EglBase.Context context, boolean z, YuvConverter yuvConverter2) {
        return create(str, context, z, yuvConverter2, (FrameRefMonitor) null);
    }

    private SurfaceTextureHelper(EglBase.Context context, Handler handler2, boolean z, YuvConverter yuvConverter2, FrameRefMonitor frameRefMonitor2) {
        this.textureRefCountMonitor = new TextureBufferImpl.RefCountMonitor() {
            public void onRetain(TextureBufferImpl textureBufferImpl) {
                if (SurfaceTextureHelper.this.frameRefMonitor != null) {
                    SurfaceTextureHelper.this.frameRefMonitor.onRetainBuffer(textureBufferImpl);
                }
            }

            public void onRelease(TextureBufferImpl textureBufferImpl) {
                if (SurfaceTextureHelper.this.frameRefMonitor != null) {
                    SurfaceTextureHelper.this.frameRefMonitor.onReleaseBuffer(textureBufferImpl);
                }
            }

            public void onDestroy(TextureBufferImpl textureBufferImpl) {
                SurfaceTextureHelper.this.returnTextureFrame();
                if (SurfaceTextureHelper.this.frameRefMonitor != null) {
                    SurfaceTextureHelper.this.frameRefMonitor.onDestroyBuffer(textureBufferImpl);
                }
            }
        };
        this.setListenerRunnable = new Runnable() {
            public void run() {
                Logging.d("SurfaceTextureHelper", "Setting listener to " + SurfaceTextureHelper.this.pendingListener);
                SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.this;
                VideoSink unused = surfaceTextureHelper.listener = surfaceTextureHelper.pendingListener;
                VideoSink unused2 = SurfaceTextureHelper.this.pendingListener = null;
                if (SurfaceTextureHelper.this.hasPendingTexture) {
                    SurfaceTextureHelper.this.updateTexImage();
                    boolean unused3 = SurfaceTextureHelper.this.hasPendingTexture = false;
                }
            }
        };
        if (handler2.getLooper().getThread() == Thread.currentThread()) {
            this.handler = handler2;
            this.timestampAligner = z ? new TimestampAligner() : null;
            this.yuvConverter = yuvConverter2;
            this.frameRefMonitor = frameRefMonitor2;
            EglBase create = EglBase.CC.create(context, EglBase.CONFIG_PIXEL_BUFFER);
            this.eglBase = create;
            try {
                create.createDummyPbufferSurface();
                this.eglBase.makeCurrent();
                this.oesTextureId = GlUtil.generateTexture(36197);
                SurfaceTexture surfaceTexture2 = new SurfaceTexture(this.oesTextureId);
                this.surfaceTexture = surfaceTexture2;
                setOnFrameAvailableListener(surfaceTexture2, new SurfaceTexture.OnFrameAvailableListener() {
                    public final void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        SurfaceTextureHelper.this.lambda$new$0$SurfaceTextureHelper(surfaceTexture);
                    }
                }, handler2);
            } catch (RuntimeException e) {
                this.eglBase.release();
                handler2.getLooper().quit();
                throw e;
            }
        } else {
            throw new IllegalStateException("SurfaceTextureHelper must be created on the handler thread");
        }
    }

    public /* synthetic */ void lambda$new$0$SurfaceTextureHelper(SurfaceTexture surfaceTexture2) {
        this.hasPendingTexture = true;
        tryDeliverTextureFrame();
    }

    @TargetApi(21)
    private static void setOnFrameAvailableListener(SurfaceTexture surfaceTexture2, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener, Handler handler2) {
        if (Build.VERSION.SDK_INT >= 21) {
            surfaceTexture2.setOnFrameAvailableListener(onFrameAvailableListener, handler2);
        } else {
            surfaceTexture2.setOnFrameAvailableListener(onFrameAvailableListener);
        }
    }

    public void startListening(VideoSink videoSink) {
        if (this.listener == null && this.pendingListener == null) {
            this.pendingListener = videoSink;
            this.handler.post(this.setListenerRunnable);
            return;
        }
        throw new IllegalStateException("SurfaceTextureHelper listener has already been set.");
    }

    public void stopListening() {
        Logging.d("SurfaceTextureHelper", "stopListening()");
        this.handler.removeCallbacks(this.setListenerRunnable);
        ThreadUtils.invokeAtFrontUninterruptibly(this.handler, (Runnable) new Runnable() {
            public final void run() {
                SurfaceTextureHelper.this.lambda$stopListening$1$SurfaceTextureHelper();
            }
        });
    }

    public /* synthetic */ void lambda$stopListening$1$SurfaceTextureHelper() {
        this.listener = null;
        this.pendingListener = null;
    }

    public void setTextureSize(int i, int i2) {
        if (i <= 0) {
            throw new IllegalArgumentException("Texture width must be positive, but was " + i);
        } else if (i2 > 0) {
            this.surfaceTexture.setDefaultBufferSize(i, i2);
            this.handler.post(new Runnable(i, i2) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SurfaceTextureHelper.this.lambda$setTextureSize$2$SurfaceTextureHelper(this.f$1, this.f$2);
                }
            });
        } else {
            throw new IllegalArgumentException("Texture height must be positive, but was " + i2);
        }
    }

    public /* synthetic */ void lambda$setTextureSize$2$SurfaceTextureHelper(int i, int i2) {
        this.textureWidth = i;
        this.textureHeight = i2;
        tryDeliverTextureFrame();
    }

    public void forceFrame() {
        this.handler.post(new Runnable() {
            public final void run() {
                SurfaceTextureHelper.this.lambda$forceFrame$3$SurfaceTextureHelper();
            }
        });
    }

    public /* synthetic */ void lambda$forceFrame$3$SurfaceTextureHelper() {
        this.hasPendingTexture = true;
        tryDeliverTextureFrame();
    }

    public /* synthetic */ void lambda$setFrameRotation$4$SurfaceTextureHelper(int i) {
        this.frameRotation = i;
    }

    public void setFrameRotation(int i) {
        this.handler.post(new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SurfaceTextureHelper.this.lambda$setFrameRotation$4$SurfaceTextureHelper(this.f$1);
            }
        });
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.surfaceTexture;
    }

    public Handler getHandler() {
        return this.handler;
    }

    /* access modifiers changed from: private */
    public void returnTextureFrame() {
        this.handler.post(new Runnable() {
            public final void run() {
                SurfaceTextureHelper.this.lambda$returnTextureFrame$5$SurfaceTextureHelper();
            }
        });
    }

    public /* synthetic */ void lambda$returnTextureFrame$5$SurfaceTextureHelper() {
        this.isTextureInUse = false;
        if (this.isQuitting) {
            release();
        } else {
            tryDeliverTextureFrame();
        }
    }

    public boolean isTextureInUse() {
        return this.isTextureInUse;
    }

    public void dispose() {
        Logging.d("SurfaceTextureHelper", "dispose()");
        ThreadUtils.invokeAtFrontUninterruptibly(this.handler, (Runnable) new Runnable() {
            public final void run() {
                SurfaceTextureHelper.this.lambda$dispose$6$SurfaceTextureHelper();
            }
        });
    }

    public /* synthetic */ void lambda$dispose$6$SurfaceTextureHelper() {
        this.isQuitting = true;
        if (!this.isTextureInUse) {
            release();
        }
    }

    @Deprecated
    public VideoFrame.I420Buffer textureToYuv(VideoFrame.TextureBuffer textureBuffer) {
        return textureBuffer.toI420();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0008 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateTexImage() {
        /*
            r2 = this;
            java.lang.Object r0 = org.webrtc.EglBase.lock
            monitor-enter(r0)
            android.graphics.SurfaceTexture r1 = r2.surfaceTexture     // Catch:{ all -> 0x0008 }
            r1.updateTexImage()     // Catch:{ all -> 0x0008 }
        L_0x0008:
            monitor-exit(r0)     // Catch:{ all -> 0x000a }
            return
        L_0x000a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x000a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.SurfaceTextureHelper.updateTexImage():void");
    }

    private void tryDeliverTextureFrame() {
        if (this.handler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Wrong thread.");
        } else if (!this.isQuitting && this.hasPendingTexture && !this.isTextureInUse && this.listener != null) {
            if (this.textureWidth == 0 || this.textureHeight == 0) {
                Logging.w("SurfaceTextureHelper", "Texture size has not been set.");
                return;
            }
            this.isTextureInUse = true;
            this.hasPendingTexture = false;
            updateTexImage();
            float[] fArr = new float[16];
            this.surfaceTexture.getTransformMatrix(fArr);
            long timestamp = this.surfaceTexture.getTimestamp();
            TimestampAligner timestampAligner2 = this.timestampAligner;
            if (timestampAligner2 != null) {
                timestamp = timestampAligner2.translateTimestamp(timestamp);
            }
            TextureBufferImpl textureBufferImpl = new TextureBufferImpl(this.textureWidth, this.textureHeight, VideoFrame.TextureBuffer.Type.OES, this.oesTextureId, RendererCommon.convertMatrixToAndroidGraphicsMatrix(fArr), this.handler, this.yuvConverter, this.textureRefCountMonitor);
            FrameRefMonitor frameRefMonitor2 = this.frameRefMonitor;
            if (frameRefMonitor2 != null) {
                frameRefMonitor2.onNewBuffer(textureBufferImpl);
            }
            VideoFrame videoFrame = new VideoFrame(textureBufferImpl, this.frameRotation, timestamp);
            this.listener.onFrame(videoFrame);
            videoFrame.release();
        }
    }

    private void release() {
        if (this.handler.getLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Wrong thread.");
        } else if (this.isTextureInUse || !this.isQuitting) {
            throw new IllegalStateException("Unexpected release.");
        } else {
            this.yuvConverter.release();
            GLES20.glDeleteTextures(1, new int[]{this.oesTextureId}, 0);
            this.surfaceTexture.release();
            this.eglBase.release();
            this.handler.getLooper().quit();
            TimestampAligner timestampAligner2 = this.timestampAligner;
            if (timestampAligner2 != null) {
                timestampAligner2.dispose();
            }
        }
    }
}
