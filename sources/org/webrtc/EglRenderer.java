package org.webrtc;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.RendererCommon;

public class EglRenderer implements VideoSink {
    private static final long LOG_INTERVAL_SEC = 4;
    private static final String TAG = "EglRenderer";
    private final GlTextureFrameBuffer bitmapTextureFramebuffer;
    private final Matrix drawMatrix;
    private RendererCommon.GlDrawer drawer;
    /* access modifiers changed from: private */
    public EglBase eglBase;
    private final EglSurfaceCreation eglSurfaceCreationRunnable;
    private volatile ErrorCallback errorCallback;
    private final Object fpsReductionLock;
    private final VideoFrameDrawer frameDrawer;
    private final ArrayList<FrameListenerAndParams> frameListeners;
    private final Object frameLock;
    private int framesDropped;
    private int framesReceived;
    private int framesRendered;
    /* access modifiers changed from: private */
    public final Object handlerLock;
    private float layoutAspectRatio;
    private final Object layoutLock;
    /* access modifiers changed from: private */
    public final Runnable logStatisticsRunnable;
    private long minRenderPeriodNs;
    private boolean mirrorHorizontally;
    private boolean mirrorVertically;
    protected final String name;
    private long nextFrameTimeNs;
    private VideoFrame pendingFrame;
    private long renderSwapBufferTimeNs;
    /* access modifiers changed from: private */
    public Handler renderThreadHandler;
    private long renderTimeNs;
    private final Object statisticsLock;
    private long statisticsStartTimeNs;
    private boolean usePresentationTimeStamp;

    public interface ErrorCallback {
        void onGlOutOfMemory();
    }

    public interface FrameListener {
        void onFrame(Bitmap bitmap);
    }

    private static class FrameListenerAndParams {
        public final boolean applyFpsReduction;
        public final RendererCommon.GlDrawer drawer;
        public final FrameListener listener;
        public final float scale;

        public FrameListenerAndParams(FrameListener frameListener, float f, RendererCommon.GlDrawer glDrawer, boolean z) {
            this.listener = frameListener;
            this.scale = f;
            this.drawer = glDrawer;
            this.applyFpsReduction = z;
        }
    }

    private class EglSurfaceCreation implements Runnable {
        private Object surface;

        private EglSurfaceCreation() {
        }

        public synchronized void setSurface(Object obj) {
            this.surface = obj;
        }

        public synchronized void run() {
            if (!(this.surface == null || EglRenderer.this.eglBase == null || EglRenderer.this.eglBase.hasSurface())) {
                if (this.surface instanceof Surface) {
                    EglRenderer.this.eglBase.createSurface((Surface) this.surface);
                } else if (this.surface instanceof SurfaceTexture) {
                    EglRenderer.this.eglBase.createSurface((SurfaceTexture) this.surface);
                } else {
                    throw new IllegalStateException("Invalid surface: " + this.surface);
                }
                EglRenderer.this.eglBase.makeCurrent();
                GLES20.glPixelStorei(3317, 1);
            }
        }
    }

    private static class HandlerWithExceptionCallback extends Handler {
        private final Runnable exceptionCallback;

        public HandlerWithExceptionCallback(Looper looper, Runnable runnable) {
            super(looper);
            this.exceptionCallback = runnable;
        }

        public void dispatchMessage(Message message) {
            try {
                super.dispatchMessage(message);
            } catch (Exception e) {
                Logging.e("EglRenderer", "Exception on EglRenderer thread", e);
                this.exceptionCallback.run();
                throw e;
            }
        }
    }

    public EglRenderer(String str) {
        this(str, new VideoFrameDrawer());
    }

    public EglRenderer(String str, VideoFrameDrawer videoFrameDrawer) {
        this.handlerLock = new Object();
        this.frameListeners = new ArrayList<>();
        this.fpsReductionLock = new Object();
        this.drawMatrix = new Matrix();
        this.frameLock = new Object();
        this.layoutLock = new Object();
        this.statisticsLock = new Object();
        this.bitmapTextureFramebuffer = new GlTextureFrameBuffer(6408);
        this.logStatisticsRunnable = new Runnable() {
            public void run() {
                EglRenderer.this.logStatistics();
                synchronized (EglRenderer.this.handlerLock) {
                    if (EglRenderer.this.renderThreadHandler != null) {
                        EglRenderer.this.renderThreadHandler.removeCallbacks(EglRenderer.this.logStatisticsRunnable);
                        EglRenderer.this.renderThreadHandler.postDelayed(EglRenderer.this.logStatisticsRunnable, TimeUnit.SECONDS.toMillis(4));
                    }
                }
            }
        };
        this.eglSurfaceCreationRunnable = new EglSurfaceCreation();
        this.name = str;
        this.frameDrawer = videoFrameDrawer;
    }

    public void init(EglBase.Context context, int[] iArr, RendererCommon.GlDrawer glDrawer, boolean z) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                logD("Initializing EglRenderer");
                this.drawer = glDrawer;
                this.usePresentationTimeStamp = z;
                HandlerThread handlerThread = new HandlerThread(this.name + "EglRenderer");
                handlerThread.start();
                HandlerWithExceptionCallback handlerWithExceptionCallback = new HandlerWithExceptionCallback(handlerThread.getLooper(), new Runnable() {
                    public void run() {
                        synchronized (EglRenderer.this.handlerLock) {
                            Handler unused = EglRenderer.this.renderThreadHandler = null;
                        }
                    }
                });
                this.renderThreadHandler = handlerWithExceptionCallback;
                ThreadUtils.invokeAtFrontUninterruptibly((Handler) handlerWithExceptionCallback, (Runnable) new Runnable(context, iArr) {
                    public final /* synthetic */ EglBase.Context f$1;
                    public final /* synthetic */ int[] f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        EglRenderer.this.lambda$init$0$EglRenderer(this.f$1, this.f$2);
                    }
                });
                this.renderThreadHandler.post(this.eglSurfaceCreationRunnable);
                resetStatistics(System.nanoTime());
                this.renderThreadHandler.postDelayed(this.logStatisticsRunnable, TimeUnit.SECONDS.toMillis(4));
            } else {
                throw new IllegalStateException(this.name + "Already initialized");
            }
        }
    }

    public /* synthetic */ void lambda$init$0$EglRenderer(EglBase.Context context, int[] iArr) {
        if (context == null) {
            logD("EglBase10.create context");
            this.eglBase = EglBase.CC.createEgl10(iArr);
            return;
        }
        logD("EglBase.create shared context");
        this.eglBase = EglBase.CC.create(context, iArr);
    }

    public void init(EglBase.Context context, int[] iArr, RendererCommon.GlDrawer glDrawer) {
        init(context, iArr, glDrawer, false);
    }

    public void createEglSurface(Surface surface) {
        createEglSurfaceInternal(surface);
    }

    public void createEglSurface(SurfaceTexture surfaceTexture) {
        createEglSurfaceInternal(surfaceTexture);
    }

    private void createEglSurfaceInternal(Object obj) {
        this.eglSurfaceCreationRunnable.setSurface(obj);
        postToRenderThread(this.eglSurfaceCreationRunnable);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003e, code lost:
        org.webrtc.ThreadUtils.awaitUninterruptibly(r0);
        r0 = r5.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0043, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0046, code lost:
        if (r5.pendingFrame == null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0048, code lost:
        r5.pendingFrame.release();
        r5.pendingFrame = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004f, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0050, code lost:
        logD("Releasing done.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0055, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void release() {
        /*
            r5 = this;
            java.lang.String r0 = "Releasing."
            r5.logD(r0)
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch
            r1 = 1
            r0.<init>(r1)
            java.lang.Object r1 = r5.handlerLock
            monitor-enter(r1)
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            if (r2 != 0) goto L_0x0019
            java.lang.String r0 = "Already released"
            r5.logD(r0)     // Catch:{ all -> 0x0059 }
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            return
        L_0x0019:
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            java.lang.Runnable r3 = r5.logStatisticsRunnable     // Catch:{ all -> 0x0059 }
            r2.removeCallbacks(r3)     // Catch:{ all -> 0x0059 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            org.webrtc.-$$Lambda$EglRenderer$MFF8Cl7oJsgEmXm7UI2GkKtNTYY r3 = new org.webrtc.-$$Lambda$EglRenderer$MFF8Cl7oJsgEmXm7UI2GkKtNTYY     // Catch:{ all -> 0x0059 }
            r3.<init>(r0)     // Catch:{ all -> 0x0059 }
            r2.postAtFrontOfQueue(r3)     // Catch:{ all -> 0x0059 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            android.os.Looper r2 = r2.getLooper()     // Catch:{ all -> 0x0059 }
            android.os.Handler r3 = r5.renderThreadHandler     // Catch:{ all -> 0x0059 }
            org.webrtc.-$$Lambda$EglRenderer$0TOf6TQvvPy5g4d42QjmzelnDZI r4 = new org.webrtc.-$$Lambda$EglRenderer$0TOf6TQvvPy5g4d42QjmzelnDZI     // Catch:{ all -> 0x0059 }
            r4.<init>(r2)     // Catch:{ all -> 0x0059 }
            r3.post(r4)     // Catch:{ all -> 0x0059 }
            r2 = 0
            r5.renderThreadHandler = r2     // Catch:{ all -> 0x0059 }
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            org.webrtc.ThreadUtils.awaitUninterruptibly(r0)
            java.lang.Object r0 = r5.frameLock
            monitor-enter(r0)
            org.webrtc.VideoFrame r1 = r5.pendingFrame     // Catch:{ all -> 0x0056 }
            if (r1 == 0) goto L_0x004f
            org.webrtc.VideoFrame r1 = r5.pendingFrame     // Catch:{ all -> 0x0056 }
            r1.release()     // Catch:{ all -> 0x0056 }
            r5.pendingFrame = r2     // Catch:{ all -> 0x0056 }
        L_0x004f:
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            java.lang.String r0 = "Releasing done."
            r5.logD(r0)
            return
        L_0x0056:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            throw r1
        L_0x0059:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0059 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.release():void");
    }

    public /* synthetic */ void lambda$release$1$EglRenderer(CountDownLatch countDownLatch) {
        GLES20.glUseProgram(0);
        RendererCommon.GlDrawer glDrawer = this.drawer;
        if (glDrawer != null) {
            glDrawer.release();
            this.drawer = null;
        }
        this.frameDrawer.release();
        this.bitmapTextureFramebuffer.release();
        if (this.eglBase != null) {
            logD("eglBase detach and release.");
            this.eglBase.detachCurrent();
            this.eglBase.release();
            this.eglBase = null;
        }
        this.frameListeners.clear();
        countDownLatch.countDown();
    }

    public /* synthetic */ void lambda$release$2$EglRenderer(Looper looper) {
        logD("Quitting render thread.");
        looper.quit();
    }

    private void resetStatistics(long j) {
        synchronized (this.statisticsLock) {
            this.statisticsStartTimeNs = j;
            this.framesReceived = 0;
            this.framesDropped = 0;
            this.framesRendered = 0;
            this.renderTimeNs = 0;
            this.renderSwapBufferTimeNs = 0;
        }
    }

    public void printStackTrace() {
        Thread thread;
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                thread = null;
            } else {
                thread = this.renderThreadHandler.getLooper().getThread();
            }
            if (thread != null) {
                StackTraceElement[] stackTrace = thread.getStackTrace();
                if (stackTrace.length > 0) {
                    logW("EglRenderer stack trace:");
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        logW(stackTraceElement.toString());
                    }
                }
            }
        }
    }

    public void setMirror(boolean z) {
        logD("setMirrorHorizontally: " + z);
        synchronized (this.layoutLock) {
            this.mirrorHorizontally = z;
        }
    }

    public void setMirrorVertically(boolean z) {
        logD("setMirrorVertically: " + z);
        synchronized (this.layoutLock) {
            this.mirrorVertically = z;
        }
    }

    public void setLayoutAspectRatio(float f) {
        logD("setLayoutAspectRatio: " + f);
        synchronized (this.layoutLock) {
            this.layoutAspectRatio = f;
        }
    }

    public void setFpsReduction(float f) {
        logD("setFpsReduction: " + f);
        synchronized (this.fpsReductionLock) {
            long j = this.minRenderPeriodNs;
            if (f <= 0.0f) {
                this.minRenderPeriodNs = Long.MAX_VALUE;
            } else {
                this.minRenderPeriodNs = (long) (((float) TimeUnit.SECONDS.toNanos(1)) / f);
            }
            if (this.minRenderPeriodNs != j) {
                this.nextFrameTimeNs = System.nanoTime();
            }
        }
    }

    public void disableFpsReduction() {
        setFpsReduction(Float.POSITIVE_INFINITY);
    }

    public void pauseVideo() {
        setFpsReduction(0.0f);
    }

    public void addFrameListener(FrameListener frameListener, float f) {
        addFrameListener(frameListener, f, (RendererCommon.GlDrawer) null, false);
    }

    public void addFrameListener(FrameListener frameListener, float f, RendererCommon.GlDrawer glDrawer) {
        addFrameListener(frameListener, f, glDrawer, false);
    }

    public void addFrameListener(FrameListener frameListener, float f, RendererCommon.GlDrawer glDrawer, boolean z) {
        postToRenderThread(new Runnable(glDrawer, frameListener, f, z) {
            public final /* synthetic */ RendererCommon.GlDrawer f$1;
            public final /* synthetic */ EglRenderer.FrameListener f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                EglRenderer.this.lambda$addFrameListener$3$EglRenderer(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$addFrameListener$3$EglRenderer(RendererCommon.GlDrawer glDrawer, FrameListener frameListener, float f, boolean z) {
        if (glDrawer == null) {
            glDrawer = this.drawer;
        }
        this.frameListeners.add(new FrameListenerAndParams(frameListener, f, glDrawer, z));
    }

    public void removeFrameListener(FrameListener frameListener) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                if (Thread.currentThread() != this.renderThreadHandler.getLooper().getThread()) {
                    postToRenderThread(new Runnable(countDownLatch, frameListener) {
                        public final /* synthetic */ CountDownLatch f$1;
                        public final /* synthetic */ EglRenderer.FrameListener f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            EglRenderer.this.lambda$removeFrameListener$4$EglRenderer(this.f$1, this.f$2);
                        }
                    });
                    ThreadUtils.awaitUninterruptibly(countDownLatch);
                    return;
                }
                throw new RuntimeException("removeFrameListener must not be called on the render thread.");
            }
        }
    }

    public /* synthetic */ void lambda$removeFrameListener$4$EglRenderer(CountDownLatch countDownLatch, FrameListener frameListener) {
        countDownLatch.countDown();
        Iterator<FrameListenerAndParams> it = this.frameListeners.iterator();
        while (it.hasNext()) {
            if (it.next().listener == frameListener) {
                it.remove();
            }
        }
    }

    public void setErrorCallback(ErrorCallback errorCallback2) {
        this.errorCallback = errorCallback2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x003a, code lost:
        if (r3 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003c, code lost:
        r6 = r5.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003e, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r5.framesDropped++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0044, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onFrame(org.webrtc.VideoFrame r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.statisticsLock
            monitor-enter(r0)
            int r1 = r5.framesReceived     // Catch:{ all -> 0x0050 }
            r2 = 1
            int r1 = r1 + r2
            r5.framesReceived = r1     // Catch:{ all -> 0x0050 }
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            java.lang.Object r1 = r5.handlerLock
            monitor-enter(r1)
            android.os.Handler r0 = r5.renderThreadHandler     // Catch:{ all -> 0x004d }
            if (r0 != 0) goto L_0x0018
            java.lang.String r6 = "Dropping frame - Not initialized or already released."
            r5.logD(r6)     // Catch:{ all -> 0x004d }
            monitor-exit(r1)     // Catch:{ all -> 0x004d }
            return
        L_0x0018:
            java.lang.Object r0 = r5.frameLock     // Catch:{ all -> 0x004d }
            monitor-enter(r0)     // Catch:{ all -> 0x004d }
            org.webrtc.VideoFrame r3 = r5.pendingFrame     // Catch:{ all -> 0x004a }
            if (r3 == 0) goto L_0x0021
            r3 = 1
            goto L_0x0022
        L_0x0021:
            r3 = 0
        L_0x0022:
            if (r3 == 0) goto L_0x0029
            org.webrtc.VideoFrame r4 = r5.pendingFrame     // Catch:{ all -> 0x004a }
            r4.release()     // Catch:{ all -> 0x004a }
        L_0x0029:
            r5.pendingFrame = r6     // Catch:{ all -> 0x004a }
            r6.retain()     // Catch:{ all -> 0x004a }
            android.os.Handler r6 = r5.renderThreadHandler     // Catch:{ all -> 0x004a }
            org.webrtc.-$$Lambda$EglRenderer$vWDJEj1GWjHSjwoQQjEEK_IVOJE r4 = new org.webrtc.-$$Lambda$EglRenderer$vWDJEj1GWjHSjwoQQjEEK_IVOJE     // Catch:{ all -> 0x004a }
            r4.<init>()     // Catch:{ all -> 0x004a }
            r6.post(r4)     // Catch:{ all -> 0x004a }
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            monitor-exit(r1)     // Catch:{ all -> 0x004d }
            if (r3 == 0) goto L_0x0049
            java.lang.Object r6 = r5.statisticsLock
            monitor-enter(r6)
            int r0 = r5.framesDropped     // Catch:{ all -> 0x0046 }
            int r0 = r0 + r2
            r5.framesDropped = r0     // Catch:{ all -> 0x0046 }
            monitor-exit(r6)     // Catch:{ all -> 0x0046 }
            goto L_0x0049
        L_0x0046:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0046 }
            throw r0
        L_0x0049:
            return
        L_0x004a:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004a }
            throw r6     // Catch:{ all -> 0x004d }
        L_0x004d:
            r6 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004d }
            throw r6
        L_0x0050:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.onFrame(org.webrtc.VideoFrame):void");
    }

    public void releaseEglSurface(Runnable runnable) {
        this.eglSurfaceCreationRunnable.setSurface((Object) null);
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.removeCallbacks(this.eglSurfaceCreationRunnable);
                this.renderThreadHandler.postAtFrontOfQueue(new Runnable(runnable) {
                    public final /* synthetic */ Runnable f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        EglRenderer.this.lambda$releaseEglSurface$5$EglRenderer(this.f$1);
                    }
                });
                return;
            }
            runnable.run();
        }
    }

    public /* synthetic */ void lambda$releaseEglSurface$5$EglRenderer(Runnable runnable) {
        EglBase eglBase2 = this.eglBase;
        if (eglBase2 != null) {
            eglBase2.detachCurrent();
            this.eglBase.releaseSurface();
        }
        runnable.run();
    }

    private void postToRenderThread(Runnable runnable) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.post(runnable);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: clearSurfaceOnRenderThread */
    public void lambda$clearImage$6$EglRenderer(float f, float f2, float f3, float f4) {
        EglBase eglBase2 = this.eglBase;
        if (eglBase2 != null && eglBase2.hasSurface()) {
            logD("clearSurface");
            GLES20.glClearColor(f, f2, f3, f4);
            GLES20.glClear(16384);
            this.eglBase.swapBuffers();
        }
    }

    public void clearImage() {
        clearImage(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clearImage(float f, float f2, float f3, float f4) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                this.renderThreadHandler.postAtFrontOfQueue(new Runnable(f, f2, f3, f4) {
                    public final /* synthetic */ float f$1;
                    public final /* synthetic */ float f$2;
                    public final /* synthetic */ float f$3;
                    public final /* synthetic */ float f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        EglRenderer.this.lambda$clearImage$6$EglRenderer(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0011, code lost:
        if (r0 == null) goto L_0x0137;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0017, code lost:
        if (r0.hasSurface() != false) goto L_0x001b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        r0 = r14.fpsReductionLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001d, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
        if (r14.minRenderPeriodNs != Long.MAX_VALUE) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0033, code lost:
        if (r14.minRenderPeriodNs > 0) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0035, code lost:
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0037, code lost:
        r2 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003f, code lost:
        if (r2 >= r14.nextFrameTimeNs) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0041, code lost:
        logD("Skipping frame rendering - fps reduction is active.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0047, code lost:
        r4 = r14.nextFrameTimeNs + r14.minRenderPeriodNs;
        r14.nextFrameTimeNs = r4;
        r14.nextFrameTimeNs = java.lang.Math.max(r4, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0055, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0056, code lost:
        r12 = java.lang.System.nanoTime();
        r0 = ((float) r1.getRotatedWidth()) / ((float) r1.getRotatedHeight());
        r2 = r14.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0067, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006d, code lost:
        if (r14.layoutAspectRatio == 0.0f) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006f, code lost:
        r3 = r14.layoutAspectRatio;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0072, code lost:
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0073, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0074, code lost:
        r2 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0078, code lost:
        if (r0 <= r3) goto L_0x007e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007a, code lost:
        r3 = r3 / r0;
        r0 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007e, code lost:
        r0 = r0 / r3;
        r3 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0081, code lost:
        r14.drawMatrix.reset();
        r14.drawMatrix.preTranslate(0.5f, 0.5f);
        r5 = r14.drawMatrix;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0093, code lost:
        if (r14.mirrorHorizontally == false) goto L_0x0098;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0095, code lost:
        r6 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0098, code lost:
        r6 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x009c, code lost:
        if (r14.mirrorVertically == false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x009e, code lost:
        r2 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00a0, code lost:
        r5.preScale(r6, r2);
        r14.drawMatrix.preScale(r3, r0);
        r14.drawMatrix.preTranslate(-0.5f, -0.5f);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00af, code lost:
        if (r11 == false) goto L_0x0106;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:?, code lost:
        android.opengl.GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        android.opengl.GLES20.glClear(16384);
        r14.frameDrawer.drawFrame(r1, r14.drawer, r14.drawMatrix, 0, 0, r14.eglBase.surfaceWidth(), r14.eglBase.surfaceHeight());
        r2 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d7, code lost:
        if (r14.usePresentationTimeStamp == false) goto L_0x00e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00d9, code lost:
        r14.eglBase.swapBuffers(r1.getTimestampNs());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00e3, code lost:
        r14.eglBase.swapBuffers();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00e8, code lost:
        r4 = java.lang.System.nanoTime();
        r0 = r14.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ee, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        r14.framesRendered++;
        r14.renderTimeNs += r4 - r12;
        r14.renderSwapBufferTimeNs += r4 - r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0101, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0106, code lost:
        notifyCallbacks(r1, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0109, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x010d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x010f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
        logE("Error while drawing frame", r0);
        r0 = r14.errorCallback;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0117, code lost:
        if (r0 != null) goto L_0x0119;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0119, code lost:
        r0.onGlOutOfMemory();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x011c, code lost:
        r14.drawer.release();
        r14.frameDrawer.release();
        r14.bitmapTextureFramebuffer.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x012c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x012d, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0130, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0137, code lost:
        logD("Dropping frame - No surface");
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x013f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000f, code lost:
        r0 = r14.eglBase;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrameOnRenderThread() {
        /*
            r14 = this;
            java.lang.Object r0 = r14.frameLock
            monitor-enter(r0)
            org.webrtc.VideoFrame r1 = r14.pendingFrame     // Catch:{ all -> 0x0140 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0140 }
            return
        L_0x0009:
            org.webrtc.VideoFrame r1 = r14.pendingFrame     // Catch:{ all -> 0x0140 }
            r2 = 0
            r14.pendingFrame = r2     // Catch:{ all -> 0x0140 }
            monitor-exit(r0)     // Catch:{ all -> 0x0140 }
            org.webrtc.EglBase r0 = r14.eglBase
            if (r0 == 0) goto L_0x0137
            boolean r0 = r0.hasSurface()
            if (r0 != 0) goto L_0x001b
            goto L_0x0137
        L_0x001b:
            java.lang.Object r0 = r14.fpsReductionLock
            monitor-enter(r0)
            long r2 = r14.minRenderPeriodNs     // Catch:{ all -> 0x0134 }
            r4 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r6 = 0
            r10 = 1
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 != 0) goto L_0x002d
        L_0x002b:
            r11 = 0
            goto L_0x0055
        L_0x002d:
            long r2 = r14.minRenderPeriodNs     // Catch:{ all -> 0x0134 }
            r4 = 0
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 > 0) goto L_0x0037
        L_0x0035:
            r11 = 1
            goto L_0x0055
        L_0x0037:
            long r2 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0134 }
            long r4 = r14.nextFrameTimeNs     // Catch:{ all -> 0x0134 }
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x0047
            java.lang.String r2 = "Skipping frame rendering - fps reduction is active."
            r14.logD(r2)     // Catch:{ all -> 0x0134 }
            goto L_0x002b
        L_0x0047:
            long r4 = r14.nextFrameTimeNs     // Catch:{ all -> 0x0134 }
            long r6 = r14.minRenderPeriodNs     // Catch:{ all -> 0x0134 }
            long r4 = r4 + r6
            r14.nextFrameTimeNs = r4     // Catch:{ all -> 0x0134 }
            long r2 = java.lang.Math.max(r4, r2)     // Catch:{ all -> 0x0134 }
            r14.nextFrameTimeNs = r2     // Catch:{ all -> 0x0134 }
            goto L_0x0035
        L_0x0055:
            monitor-exit(r0)     // Catch:{ all -> 0x0134 }
            long r12 = java.lang.System.nanoTime()
            int r0 = r1.getRotatedWidth()
            float r0 = (float) r0
            int r2 = r1.getRotatedHeight()
            float r2 = (float) r2
            float r0 = r0 / r2
            java.lang.Object r2 = r14.layoutLock
            monitor-enter(r2)
            float r3 = r14.layoutAspectRatio     // Catch:{ all -> 0x0131 }
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0072
            float r3 = r14.layoutAspectRatio     // Catch:{ all -> 0x0131 }
            goto L_0x0073
        L_0x0072:
            r3 = r0
        L_0x0073:
            monitor-exit(r2)     // Catch:{ all -> 0x0131 }
            r2 = 1065353216(0x3var_, float:1.0)
            int r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x007e
            float r3 = r3 / r0
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x0081
        L_0x007e:
            float r0 = r0 / r3
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0081:
            android.graphics.Matrix r5 = r14.drawMatrix
            r5.reset()
            android.graphics.Matrix r5 = r14.drawMatrix
            r6 = 1056964608(0x3var_, float:0.5)
            r5.preTranslate(r6, r6)
            android.graphics.Matrix r5 = r14.drawMatrix
            boolean r6 = r14.mirrorHorizontally
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r6 == 0) goto L_0x0098
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x009a
        L_0x0098:
            r6 = 1065353216(0x3var_, float:1.0)
        L_0x009a:
            boolean r8 = r14.mirrorVertically
            if (r8 == 0) goto L_0x00a0
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x00a0:
            r5.preScale(r6, r2)
            android.graphics.Matrix r2 = r14.drawMatrix
            r2.preScale(r3, r0)
            android.graphics.Matrix r0 = r14.drawMatrix
            r2 = -1090519040(0xffffffffbvar_, float:-0.5)
            r0.preTranslate(r2, r2)
            if (r11 == 0) goto L_0x0106
            android.opengl.GLES20.glClearColor(r4, r4, r4, r4)     // Catch:{ GlOutOfMemoryException -> 0x010f }
            r0 = 16384(0x4000, float:2.2959E-41)
            android.opengl.GLES20.glClear(r0)     // Catch:{ GlOutOfMemoryException -> 0x010f }
            org.webrtc.VideoFrameDrawer r2 = r14.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x010f }
            org.webrtc.RendererCommon$GlDrawer r4 = r14.drawer     // Catch:{ GlOutOfMemoryException -> 0x010f }
            android.graphics.Matrix r5 = r14.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x010f }
            r6 = 0
            r7 = 0
            org.webrtc.EglBase r0 = r14.eglBase     // Catch:{ GlOutOfMemoryException -> 0x010f }
            int r8 = r0.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x010f }
            org.webrtc.EglBase r0 = r14.eglBase     // Catch:{ GlOutOfMemoryException -> 0x010f }
            int r9 = r0.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x010f }
            r3 = r1
            r2.drawFrame(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ GlOutOfMemoryException -> 0x010f }
            long r2 = java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x010f }
            boolean r0 = r14.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x010f }
            if (r0 == 0) goto L_0x00e3
            org.webrtc.EglBase r0 = r14.eglBase     // Catch:{ GlOutOfMemoryException -> 0x010f }
            long r4 = r1.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x010f }
            r0.swapBuffers(r4)     // Catch:{ GlOutOfMemoryException -> 0x010f }
            goto L_0x00e8
        L_0x00e3:
            org.webrtc.EglBase r0 = r14.eglBase     // Catch:{ GlOutOfMemoryException -> 0x010f }
            r0.swapBuffers()     // Catch:{ GlOutOfMemoryException -> 0x010f }
        L_0x00e8:
            long r4 = java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x010f }
            java.lang.Object r0 = r14.statisticsLock     // Catch:{ GlOutOfMemoryException -> 0x010f }
            monitor-enter(r0)     // Catch:{ GlOutOfMemoryException -> 0x010f }
            int r6 = r14.framesRendered     // Catch:{ all -> 0x0103 }
            int r6 = r6 + r10
            r14.framesRendered = r6     // Catch:{ all -> 0x0103 }
            long r6 = r14.renderTimeNs     // Catch:{ all -> 0x0103 }
            long r8 = r4 - r12
            long r6 = r6 + r8
            r14.renderTimeNs = r6     // Catch:{ all -> 0x0103 }
            long r6 = r14.renderSwapBufferTimeNs     // Catch:{ all -> 0x0103 }
            long r4 = r4 - r2
            long r6 = r6 + r4
            r14.renderSwapBufferTimeNs = r6     // Catch:{ all -> 0x0103 }
            monitor-exit(r0)     // Catch:{ all -> 0x0103 }
            goto L_0x0106
        L_0x0103:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0103 }
            throw r2     // Catch:{ GlOutOfMemoryException -> 0x010f }
        L_0x0106:
            r14.notifyCallbacks(r1, r11)     // Catch:{ GlOutOfMemoryException -> 0x010f }
        L_0x0109:
            r1.release()
            goto L_0x012c
        L_0x010d:
            r0 = move-exception
            goto L_0x012d
        L_0x010f:
            r0 = move-exception
            java.lang.String r2 = "Error while drawing frame"
            r14.logE(r2, r0)     // Catch:{ all -> 0x010d }
            org.webrtc.EglRenderer$ErrorCallback r0 = r14.errorCallback     // Catch:{ all -> 0x010d }
            if (r0 == 0) goto L_0x011c
            r0.onGlOutOfMemory()     // Catch:{ all -> 0x010d }
        L_0x011c:
            org.webrtc.RendererCommon$GlDrawer r0 = r14.drawer     // Catch:{ all -> 0x010d }
            r0.release()     // Catch:{ all -> 0x010d }
            org.webrtc.VideoFrameDrawer r0 = r14.frameDrawer     // Catch:{ all -> 0x010d }
            r0.release()     // Catch:{ all -> 0x010d }
            org.webrtc.GlTextureFrameBuffer r0 = r14.bitmapTextureFramebuffer     // Catch:{ all -> 0x010d }
            r0.release()     // Catch:{ all -> 0x010d }
            goto L_0x0109
        L_0x012c:
            return
        L_0x012d:
            r1.release()
            throw r0
        L_0x0131:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0131 }
            throw r0
        L_0x0134:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0134 }
            throw r1
        L_0x0137:
            java.lang.String r0 = "Dropping frame - No surface"
            r14.logD(r0)
            r1.release()
            return
        L_0x0140:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0140 }
            goto L_0x0144
        L_0x0143:
            throw r1
        L_0x0144:
            goto L_0x0143
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.renderFrameOnRenderThread():void");
    }

    private void notifyCallbacks(VideoFrame videoFrame, boolean z) {
        if (!this.frameListeners.isEmpty()) {
            this.drawMatrix.reset();
            this.drawMatrix.preTranslate(0.5f, 0.5f);
            this.drawMatrix.preScale(this.mirrorHorizontally ? -1.0f : 1.0f, this.mirrorVertically ? -1.0f : 1.0f);
            this.drawMatrix.preScale(1.0f, -1.0f);
            this.drawMatrix.preTranslate(-0.5f, -0.5f);
            Iterator<FrameListenerAndParams> it = this.frameListeners.iterator();
            while (it.hasNext()) {
                FrameListenerAndParams next = it.next();
                if (z || !next.applyFpsReduction) {
                    it.remove();
                    int rotatedWidth = (int) (next.scale * ((float) videoFrame.getRotatedWidth()));
                    int rotatedHeight = (int) (next.scale * ((float) videoFrame.getRotatedHeight()));
                    if (rotatedWidth == 0 || rotatedHeight == 0) {
                        next.listener.onFrame((Bitmap) null);
                    } else {
                        this.bitmapTextureFramebuffer.setSize(rotatedWidth, rotatedHeight);
                        GLES20.glBindFramebuffer(36160, this.bitmapTextureFramebuffer.getFrameBufferId());
                        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.bitmapTextureFramebuffer.getTextureId(), 0);
                        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        GLES20.glClear(16384);
                        this.frameDrawer.drawFrame(videoFrame, next.drawer, this.drawMatrix, 0, 0, rotatedWidth, rotatedHeight);
                        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(rotatedWidth * rotatedHeight * 4);
                        GLES20.glViewport(0, 0, rotatedWidth, rotatedHeight);
                        GLES20.glReadPixels(0, 0, rotatedWidth, rotatedHeight, 6408, 5121, allocateDirect);
                        GLES20.glBindFramebuffer(36160, 0);
                        GlUtil.checkNoGLES2Error("EglRenderer.notifyCallbacks");
                        Bitmap createBitmap = Bitmap.createBitmap(rotatedWidth, rotatedHeight, Bitmap.Config.ARGB_8888);
                        createBitmap.copyPixelsFromBuffer(allocateDirect);
                        next.listener.onFrame(createBitmap);
                    }
                }
            }
        }
    }

    private String averageTimeAsString(long j, int i) {
        if (i <= 0) {
            return "NA";
        }
        return TimeUnit.NANOSECONDS.toMicros(j / ((long) i)) + " us";
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x00a9, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void logStatistics() {
        /*
            r11 = this;
            java.text.DecimalFormat r0 = new java.text.DecimalFormat
            java.lang.String r1 = "#.0"
            r0.<init>(r1)
            long r1 = java.lang.System.nanoTime()
            java.lang.Object r3 = r11.statisticsLock
            monitor-enter(r3)
            long r4 = r11.statisticsStartTimeNs     // Catch:{ all -> 0x00aa }
            long r4 = r1 - r4
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x00a8
            long r6 = r11.minRenderPeriodNs     // Catch:{ all -> 0x00aa }
            r8 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x0029
            int r6 = r11.framesReceived     // Catch:{ all -> 0x00aa }
            if (r6 != 0) goto L_0x0029
            goto L_0x00a8
        L_0x0029:
            int r6 = r11.framesRendered     // Catch:{ all -> 0x00aa }
            long r6 = (long) r6     // Catch:{ all -> 0x00aa }
            java.util.concurrent.TimeUnit r8 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ all -> 0x00aa }
            r9 = 1
            long r8 = r8.toNanos(r9)     // Catch:{ all -> 0x00aa }
            long r6 = r6 * r8
            float r6 = (float) r6     // Catch:{ all -> 0x00aa }
            float r7 = (float) r4     // Catch:{ all -> 0x00aa }
            float r6 = r6 / r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x00aa }
            r7.<init>()     // Catch:{ all -> 0x00aa }
            java.lang.String r8 = "Duration: "
            r7.append(r8)     // Catch:{ all -> 0x00aa }
            java.util.concurrent.TimeUnit r8 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch:{ all -> 0x00aa }
            long r4 = r8.toMillis(r4)     // Catch:{ all -> 0x00aa }
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            java.lang.String r4 = " ms. Frames received: "
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            int r4 = r11.framesReceived     // Catch:{ all -> 0x00aa }
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            java.lang.String r4 = ". Dropped: "
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            int r4 = r11.framesDropped     // Catch:{ all -> 0x00aa }
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            java.lang.String r4 = ". Rendered: "
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            int r4 = r11.framesRendered     // Catch:{ all -> 0x00aa }
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            java.lang.String r4 = ". Render fps: "
            r7.append(r4)     // Catch:{ all -> 0x00aa }
            double r4 = (double) r6     // Catch:{ all -> 0x00aa }
            java.lang.String r0 = r0.format(r4)     // Catch:{ all -> 0x00aa }
            r7.append(r0)     // Catch:{ all -> 0x00aa }
            java.lang.String r0 = ". Average render time: "
            r7.append(r0)     // Catch:{ all -> 0x00aa }
            long r4 = r11.renderTimeNs     // Catch:{ all -> 0x00aa }
            int r0 = r11.framesRendered     // Catch:{ all -> 0x00aa }
            java.lang.String r0 = r11.averageTimeAsString(r4, r0)     // Catch:{ all -> 0x00aa }
            r7.append(r0)     // Catch:{ all -> 0x00aa }
            java.lang.String r0 = ". Average swapBuffer time: "
            r7.append(r0)     // Catch:{ all -> 0x00aa }
            long r4 = r11.renderSwapBufferTimeNs     // Catch:{ all -> 0x00aa }
            int r0 = r11.framesRendered     // Catch:{ all -> 0x00aa }
            java.lang.String r0 = r11.averageTimeAsString(r4, r0)     // Catch:{ all -> 0x00aa }
            r7.append(r0)     // Catch:{ all -> 0x00aa }
            java.lang.String r0 = "."
            r7.append(r0)     // Catch:{ all -> 0x00aa }
            java.lang.String r0 = r7.toString()     // Catch:{ all -> 0x00aa }
            r11.logD(r0)     // Catch:{ all -> 0x00aa }
            r11.resetStatistics(r1)     // Catch:{ all -> 0x00aa }
            monitor-exit(r3)     // Catch:{ all -> 0x00aa }
            return
        L_0x00a8:
            monitor-exit(r3)     // Catch:{ all -> 0x00aa }
            return
        L_0x00aa:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00aa }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.logStatistics():void");
    }

    private void logE(String str, Throwable th) {
        Logging.e("EglRenderer", this.name + str, th);
    }

    private void logD(String str) {
        Logging.d("EglRenderer", this.name + str);
    }

    private void logW(String str) {
        Logging.w("EglRenderer", this.name + str);
    }
}
