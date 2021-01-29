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
    private boolean firstFrameRendered;
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
    private int rotation;
    private final Object statisticsLock;
    private long statisticsStartTimeNs;
    private boolean usePresentationTimeStamp;

    public interface ErrorCallback {
        void onGlOutOfMemory();
    }

    public interface FrameListener {
        void onFrame(Bitmap bitmap);
    }

    /* access modifiers changed from: protected */
    public void onFirstFrameRendered() {
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
                Object obj = this.surface;
                if (obj instanceof Surface) {
                    EglRenderer.this.eglBase.createSurface((Surface) this.surface);
                } else if (obj instanceof SurfaceTexture) {
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
                this.firstFrameRendered = false;
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$0 */
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

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003c, code lost:
        org.webrtc.ThreadUtils.awaitUninterruptibly(r0);
        r0 = r5.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0041, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r1 = r5.pendingFrame;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0044, code lost:
        if (r1 == null) goto L_0x004b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0046, code lost:
        r1.release();
        r5.pendingFrame = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004b, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004c, code lost:
        logD("Releasing done.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0051, code lost:
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
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0055 }
            if (r2 != 0) goto L_0x0019
            java.lang.String r0 = "Already released"
            r5.logD(r0)     // Catch:{ all -> 0x0055 }
            monitor-exit(r1)     // Catch:{ all -> 0x0055 }
            return
        L_0x0019:
            java.lang.Runnable r3 = r5.logStatisticsRunnable     // Catch:{ all -> 0x0055 }
            r2.removeCallbacks(r3)     // Catch:{ all -> 0x0055 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0055 }
            org.webrtc.-$$Lambda$EglRenderer$dbZIvF-jDfWfAxWS6_OKLODul18 r3 = new org.webrtc.-$$Lambda$EglRenderer$dbZIvF-jDfWfAxWS6_OKLODul18     // Catch:{ all -> 0x0055 }
            r3.<init>(r0)     // Catch:{ all -> 0x0055 }
            r2.postAtFrontOfQueue(r3)     // Catch:{ all -> 0x0055 }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x0055 }
            android.os.Looper r2 = r2.getLooper()     // Catch:{ all -> 0x0055 }
            android.os.Handler r3 = r5.renderThreadHandler     // Catch:{ all -> 0x0055 }
            org.webrtc.-$$Lambda$EglRenderer$pvyzzKSwJYfQ10Yf4Pez7unf1S4 r4 = new org.webrtc.-$$Lambda$EglRenderer$pvyzzKSwJYfQ10Yf4Pez7unf1S4     // Catch:{ all -> 0x0055 }
            r4.<init>(r2)     // Catch:{ all -> 0x0055 }
            r3.post(r4)     // Catch:{ all -> 0x0055 }
            r2 = 0
            r5.renderThreadHandler = r2     // Catch:{ all -> 0x0055 }
            monitor-exit(r1)     // Catch:{ all -> 0x0055 }
            org.webrtc.ThreadUtils.awaitUninterruptibly(r0)
            java.lang.Object r0 = r5.frameLock
            monitor-enter(r0)
            org.webrtc.VideoFrame r1 = r5.pendingFrame     // Catch:{ all -> 0x0052 }
            if (r1 == 0) goto L_0x004b
            r1.release()     // Catch:{ all -> 0x0052 }
            r5.pendingFrame = r2     // Catch:{ all -> 0x0052 }
        L_0x004b:
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            java.lang.String r0 = "Releasing done."
            r5.logD(r0)
            return
        L_0x0052:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            throw r1
        L_0x0055:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0055 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.release():void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$release$1 */
    public /* synthetic */ void lambda$release$1$EglRenderer(CountDownLatch countDownLatch) {
        synchronized (EglBase.lock) {
            GLES20.glUseProgram(0);
        }
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$release$2 */
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
            Handler handler = this.renderThreadHandler;
            if (handler == null) {
                thread = null;
            } else {
                thread = handler.getLooper().getThread();
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$addFrameListener$3 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$removeFrameListener$4 */
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

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0038, code lost:
        if (r4 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003a, code lost:
        r6 = r5.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003c, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r5.framesDropped++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0042, code lost:
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
            int r1 = r5.framesReceived     // Catch:{ all -> 0x004e }
            r2 = 1
            int r1 = r1 + r2
            r5.framesReceived = r1     // Catch:{ all -> 0x004e }
            monitor-exit(r0)     // Catch:{ all -> 0x004e }
            java.lang.Object r1 = r5.handlerLock
            monitor-enter(r1)
            android.os.Handler r0 = r5.renderThreadHandler     // Catch:{ all -> 0x004b }
            if (r0 != 0) goto L_0x0018
            java.lang.String r6 = "Dropping frame - Not initialized or already released."
            r5.logD(r6)     // Catch:{ all -> 0x004b }
            monitor-exit(r1)     // Catch:{ all -> 0x004b }
            return
        L_0x0018:
            java.lang.Object r0 = r5.frameLock     // Catch:{ all -> 0x004b }
            monitor-enter(r0)     // Catch:{ all -> 0x004b }
            org.webrtc.VideoFrame r3 = r5.pendingFrame     // Catch:{ all -> 0x0048 }
            if (r3 == 0) goto L_0x0021
            r4 = 1
            goto L_0x0022
        L_0x0021:
            r4 = 0
        L_0x0022:
            if (r4 == 0) goto L_0x0027
            r3.release()     // Catch:{ all -> 0x0048 }
        L_0x0027:
            r5.pendingFrame = r6     // Catch:{ all -> 0x0048 }
            r6.retain()     // Catch:{ all -> 0x0048 }
            android.os.Handler r6 = r5.renderThreadHandler     // Catch:{ all -> 0x0048 }
            org.webrtc.-$$Lambda$EglRenderer$im8Sa54i366ODPy-soB9Bg4O-w4 r3 = new org.webrtc.-$$Lambda$EglRenderer$im8Sa54i366ODPy-soB9Bg4O-w4     // Catch:{ all -> 0x0048 }
            r3.<init>()     // Catch:{ all -> 0x0048 }
            r6.post(r3)     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            monitor-exit(r1)     // Catch:{ all -> 0x004b }
            if (r4 == 0) goto L_0x0047
            java.lang.Object r6 = r5.statisticsLock
            monitor-enter(r6)
            int r0 = r5.framesDropped     // Catch:{ all -> 0x0044 }
            int r0 = r0 + r2
            r5.framesDropped = r0     // Catch:{ all -> 0x0044 }
            monitor-exit(r6)     // Catch:{ all -> 0x0044 }
            goto L_0x0047
        L_0x0044:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0044 }
            throw r0
        L_0x0047:
            return
        L_0x0048:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            throw r6     // Catch:{ all -> 0x004b }
        L_0x004b:
            r6 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004b }
            throw r6
        L_0x004e:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004e }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.onFrame(org.webrtc.VideoFrame):void");
    }

    public void setRotation(int i) {
        synchronized (this.layoutLock) {
            this.rotation = i;
        }
    }

    public void releaseEglSurface(Runnable runnable) {
        this.eglSurfaceCreationRunnable.setSurface((Object) null);
        synchronized (this.handlerLock) {
            Handler handler = this.renderThreadHandler;
            if (handler != null) {
                handler.removeCallbacks(this.eglSurfaceCreationRunnable);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$releaseEglSurface$5 */
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
            Handler handler = this.renderThreadHandler;
            if (handler != null) {
                handler.post(runnable);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: clearSurfaceOnRenderThread */
    public void lambda$clearImage$6(float f, float f2, float f3, float f4) {
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
            Handler handler = this.renderThreadHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(new Runnable(f, f2, f3, f4) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0166, code lost:
        logD("Dropping frame - No surface");
        r10.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x016e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000f, code lost:
        if (r0 == null) goto L_0x0166;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0015, code lost:
        if (r0.hasSurface() != false) goto L_0x0019;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0019, code lost:
        r0 = r15.fpsReductionLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001b, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r1 = r15.minRenderPeriodNs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0027, code lost:
        if (r1 != Long.MAX_VALUE) goto L_0x002b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002f, code lost:
        if (r1 > 0) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0031, code lost:
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0033, code lost:
        r1 = java.lang.System.nanoTime();
        r3 = r15.nextFrameTimeNs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003b, code lost:
        if (r1 >= r3) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003d, code lost:
        logD("Skipping frame rendering - fps reduction is active.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0043, code lost:
        r3 = r3 + r15.minRenderPeriodNs;
        r15.nextFrameTimeNs = r3;
        r15.nextFrameTimeNs = java.lang.Math.max(r3, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004f, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0050, code lost:
        r13 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005c, code lost:
        if (java.lang.Math.abs(r15.rotation) == 90) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0066, code lost:
        if (java.lang.Math.abs(r15.rotation) != 270) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0069, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006b, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x006c, code lost:
        if (r9 == false) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006e, code lost:
        r0 = r10.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0073, code lost:
        r0 = r10.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0077, code lost:
        r0 = (float) r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0078, code lost:
        if (r9 == false) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007a, code lost:
        r1 = r10.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x007f, code lost:
        r1 = r10.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0083, code lost:
        r0 = r0 / ((float) r1);
        r1 = r15.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0087, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r2 = r15.layoutAspectRatio;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x008d, code lost:
        if (r2 == 0.0f) goto L_0x0090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0090, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0091, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0092, code lost:
        r1 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0096, code lost:
        if (r0 <= r2) goto L_0x009c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0098, code lost:
        r2 = r2 / r0;
        r0 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x009c, code lost:
        r0 = r0 / r2;
        r2 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x009f, code lost:
        r15.drawMatrix.reset();
        r15.drawMatrix.preTranslate(0.5f, 0.5f);
        r15.drawMatrix.preRotate((float) r15.rotation);
        r4 = r15.drawMatrix;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b9, code lost:
        if (r15.mirrorHorizontally == false) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00bb, code lost:
        r5 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00be, code lost:
        r5 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00c2, code lost:
        if (r15.mirrorVertically == false) goto L_0x00c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00c4, code lost:
        r1 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00c6, code lost:
        r4.preScale(r5, r1);
        r15.drawMatrix.preScale(r2, r0);
        r15.drawMatrix.preTranslate(-0.5f, -0.5f);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00d5, code lost:
        if (r12 == false) goto L_0x0135;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        android.opengl.GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        android.opengl.GLES20.glClear(16384);
        r15.frameDrawer.drawFrame(r10, r15.drawer, r15.drawMatrix, 0, 0, r15.eglBase.surfaceWidth(), r15.eglBase.surfaceHeight(), r9);
        r0 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00fd, code lost:
        if (r15.usePresentationTimeStamp == false) goto L_0x0109;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00ff, code lost:
        r15.eglBase.swapBuffers(r10.getTimestampNs());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0109, code lost:
        r15.eglBase.swapBuffers();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0110, code lost:
        if (r15.firstFrameRendered != false) goto L_0x0117;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0112, code lost:
        r15.firstFrameRendered = true;
        onFirstFrameRendered();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0117, code lost:
        r2 = java.lang.System.nanoTime();
        r4 = r15.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x011d, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:?, code lost:
        r15.framesRendered++;
        r15.renderTimeNs += r2 - r13;
        r15.renderSwapBufferTimeNs += r2 - r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0130, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0135, code lost:
        notifyCallbacks(r10, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0138, code lost:
        r10.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x013c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x013e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:?, code lost:
        logE("Error while drawing frame", r0);
        r0 = r15.errorCallback;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0146, code lost:
        if (r0 != null) goto L_0x0148;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0148, code lost:
        r0.onGlOutOfMemory();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x014b, code lost:
        r15.drawer.release();
        r15.frameDrawer.release();
        r15.bitmapTextureFramebuffer.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x015b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x015c, code lost:
        r10.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x015f, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000d, code lost:
        r0 = r15.eglBase;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrameOnRenderThread() {
        /*
            r15 = this;
            java.lang.Object r0 = r15.frameLock
            monitor-enter(r0)
            org.webrtc.VideoFrame r10 = r15.pendingFrame     // Catch:{ all -> 0x016f }
            if (r10 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x016f }
            return
        L_0x0009:
            r1 = 0
            r15.pendingFrame = r1     // Catch:{ all -> 0x016f }
            monitor-exit(r0)     // Catch:{ all -> 0x016f }
            org.webrtc.EglBase r0 = r15.eglBase
            if (r0 == 0) goto L_0x0166
            boolean r0 = r0.hasSurface()
            if (r0 != 0) goto L_0x0019
            goto L_0x0166
        L_0x0019:
            java.lang.Object r0 = r15.fpsReductionLock
            monitor-enter(r0)
            long r1 = r15.minRenderPeriodNs     // Catch:{ all -> 0x0163 }
            r3 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r5 = 0
            r11 = 1
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 != 0) goto L_0x002b
        L_0x0029:
            r12 = 0
            goto L_0x004f
        L_0x002b:
            r3 = 0
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 > 0) goto L_0x0033
        L_0x0031:
            r12 = 1
            goto L_0x004f
        L_0x0033:
            long r1 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0163 }
            long r3 = r15.nextFrameTimeNs     // Catch:{ all -> 0x0163 }
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 >= 0) goto L_0x0043
            java.lang.String r1 = "Skipping frame rendering - fps reduction is active."
            r15.logD(r1)     // Catch:{ all -> 0x0163 }
            goto L_0x0029
        L_0x0043:
            long r6 = r15.minRenderPeriodNs     // Catch:{ all -> 0x0163 }
            long r3 = r3 + r6
            r15.nextFrameTimeNs = r3     // Catch:{ all -> 0x0163 }
            long r1 = java.lang.Math.max(r3, r1)     // Catch:{ all -> 0x0163 }
            r15.nextFrameTimeNs = r1     // Catch:{ all -> 0x0163 }
            goto L_0x0031
        L_0x004f:
            monitor-exit(r0)     // Catch:{ all -> 0x0163 }
            long r13 = java.lang.System.nanoTime()
            int r0 = r15.rotation
            int r0 = java.lang.Math.abs(r0)
            r1 = 90
            if (r0 == r1) goto L_0x006b
            int r0 = r15.rotation
            int r0 = java.lang.Math.abs(r0)
            r1 = 270(0x10e, float:3.78E-43)
            if (r0 != r1) goto L_0x0069
            goto L_0x006b
        L_0x0069:
            r9 = 0
            goto L_0x006c
        L_0x006b:
            r9 = 1
        L_0x006c:
            if (r9 == 0) goto L_0x0073
            int r0 = r10.getRotatedHeight()
            goto L_0x0077
        L_0x0073:
            int r0 = r10.getRotatedWidth()
        L_0x0077:
            float r0 = (float) r0
            if (r9 == 0) goto L_0x007f
            int r1 = r10.getRotatedWidth()
            goto L_0x0083
        L_0x007f:
            int r1 = r10.getRotatedHeight()
        L_0x0083:
            float r1 = (float) r1
            float r0 = r0 / r1
            java.lang.Object r1 = r15.layoutLock
            monitor-enter(r1)
            float r2 = r15.layoutAspectRatio     // Catch:{ all -> 0x0160 }
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x0090
            goto L_0x0091
        L_0x0090:
            r2 = r0
        L_0x0091:
            monitor-exit(r1)     // Catch:{ all -> 0x0160 }
            r1 = 1065353216(0x3var_, float:1.0)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x009c
            float r2 = r2 / r0
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x009f
        L_0x009c:
            float r0 = r0 / r2
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x009f:
            android.graphics.Matrix r4 = r15.drawMatrix
            r4.reset()
            android.graphics.Matrix r4 = r15.drawMatrix
            r5 = 1056964608(0x3var_, float:0.5)
            r4.preTranslate(r5, r5)
            android.graphics.Matrix r4 = r15.drawMatrix
            int r5 = r15.rotation
            float r5 = (float) r5
            r4.preRotate(r5)
            android.graphics.Matrix r4 = r15.drawMatrix
            boolean r5 = r15.mirrorHorizontally
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r5 == 0) goto L_0x00be
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x00c0
        L_0x00be:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x00c0:
            boolean r7 = r15.mirrorVertically
            if (r7 == 0) goto L_0x00c6
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x00c6:
            r4.preScale(r5, r1)
            android.graphics.Matrix r1 = r15.drawMatrix
            r1.preScale(r2, r0)
            android.graphics.Matrix r0 = r15.drawMatrix
            r1 = -1090519040(0xffffffffbvar_, float:-0.5)
            r0.preTranslate(r1, r1)
            if (r12 == 0) goto L_0x0135
            android.opengl.GLES20.glClearColor(r3, r3, r3, r3)     // Catch:{ GlOutOfMemoryException -> 0x013e }
            r0 = 16384(0x4000, float:2.2959E-41)
            android.opengl.GLES20.glClear(r0)     // Catch:{ GlOutOfMemoryException -> 0x013e }
            org.webrtc.VideoFrameDrawer r1 = r15.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x013e }
            org.webrtc.RendererCommon$GlDrawer r3 = r15.drawer     // Catch:{ GlOutOfMemoryException -> 0x013e }
            android.graphics.Matrix r4 = r15.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x013e }
            r5 = 0
            r6 = 0
            org.webrtc.EglBase r0 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x013e }
            int r7 = r0.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x013e }
            org.webrtc.EglBase r0 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x013e }
            int r8 = r0.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x013e }
            r2 = r10
            r1.drawFrame(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ GlOutOfMemoryException -> 0x013e }
            long r0 = java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x013e }
            boolean r2 = r15.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x013e }
            if (r2 == 0) goto L_0x0109
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x013e }
            long r3 = r10.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x013e }
            r2.swapBuffers(r3)     // Catch:{ GlOutOfMemoryException -> 0x013e }
            goto L_0x010e
        L_0x0109:
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x013e }
            r2.swapBuffers()     // Catch:{ GlOutOfMemoryException -> 0x013e }
        L_0x010e:
            boolean r2 = r15.firstFrameRendered     // Catch:{ GlOutOfMemoryException -> 0x013e }
            if (r2 != 0) goto L_0x0117
            r15.firstFrameRendered = r11     // Catch:{ GlOutOfMemoryException -> 0x013e }
            r15.onFirstFrameRendered()     // Catch:{ GlOutOfMemoryException -> 0x013e }
        L_0x0117:
            long r2 = java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x013e }
            java.lang.Object r4 = r15.statisticsLock     // Catch:{ GlOutOfMemoryException -> 0x013e }
            monitor-enter(r4)     // Catch:{ GlOutOfMemoryException -> 0x013e }
            int r5 = r15.framesRendered     // Catch:{ all -> 0x0132 }
            int r5 = r5 + r11
            r15.framesRendered = r5     // Catch:{ all -> 0x0132 }
            long r5 = r15.renderTimeNs     // Catch:{ all -> 0x0132 }
            long r7 = r2 - r13
            long r5 = r5 + r7
            r15.renderTimeNs = r5     // Catch:{ all -> 0x0132 }
            long r5 = r15.renderSwapBufferTimeNs     // Catch:{ all -> 0x0132 }
            long r2 = r2 - r0
            long r5 = r5 + r2
            r15.renderSwapBufferTimeNs = r5     // Catch:{ all -> 0x0132 }
            monitor-exit(r4)     // Catch:{ all -> 0x0132 }
            goto L_0x0135
        L_0x0132:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0132 }
            throw r0     // Catch:{ GlOutOfMemoryException -> 0x013e }
        L_0x0135:
            r15.notifyCallbacks(r10, r12)     // Catch:{ GlOutOfMemoryException -> 0x013e }
        L_0x0138:
            r10.release()
            goto L_0x015b
        L_0x013c:
            r0 = move-exception
            goto L_0x015c
        L_0x013e:
            r0 = move-exception
            java.lang.String r1 = "Error while drawing frame"
            r15.logE(r1, r0)     // Catch:{ all -> 0x013c }
            org.webrtc.EglRenderer$ErrorCallback r0 = r15.errorCallback     // Catch:{ all -> 0x013c }
            if (r0 == 0) goto L_0x014b
            r0.onGlOutOfMemory()     // Catch:{ all -> 0x013c }
        L_0x014b:
            org.webrtc.RendererCommon$GlDrawer r0 = r15.drawer     // Catch:{ all -> 0x013c }
            r0.release()     // Catch:{ all -> 0x013c }
            org.webrtc.VideoFrameDrawer r0 = r15.frameDrawer     // Catch:{ all -> 0x013c }
            r0.release()     // Catch:{ all -> 0x013c }
            org.webrtc.GlTextureFrameBuffer r0 = r15.bitmapTextureFramebuffer     // Catch:{ all -> 0x013c }
            r0.release()     // Catch:{ all -> 0x013c }
            goto L_0x0138
        L_0x015b:
            return
        L_0x015c:
            r10.release()
            throw r0
        L_0x0160:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0160 }
            throw r0
        L_0x0163:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0163 }
            throw r1
        L_0x0166:
            java.lang.String r0 = "Dropping frame - No surface"
            r15.logD(r0)
            r10.release()
            return
        L_0x016f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x016f }
            goto L_0x0173
        L_0x0172:
            throw r1
        L_0x0173:
            goto L_0x0172
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.renderFrameOnRenderThread():void");
    }

    private void notifyCallbacks(VideoFrame videoFrame, boolean z) {
        if (!this.frameListeners.isEmpty()) {
            this.drawMatrix.reset();
            this.drawMatrix.preTranslate(0.5f, 0.5f);
            this.drawMatrix.preRotate((float) this.rotation);
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
                        this.frameDrawer.drawFrame(videoFrame, next.drawer, this.drawMatrix, 0, 0, rotatedWidth, rotatedHeight, false);
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
