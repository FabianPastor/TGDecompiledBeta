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
import org.telegram.messenger.FileLog;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;

public class EglRenderer implements VideoSink {
    private static final long LOG_INTERVAL_SEC = 4;
    private static final String TAG = "EglRenderer";
    private final GlTextureFrameBuffer bitmapTextureFramebuffer;
    private final Matrix drawMatrix;
    private RendererCommon.GlDrawer drawer;
    /* access modifiers changed from: private */
    public EglBase eglBase;
    private final EglSurfaceCreation eglSurfaceBackgroundCreationRunnable;
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
        private final boolean background;
        private Object surface;

        public EglSurfaceCreation(boolean z) {
            this.background = z;
        }

        public synchronized void setSurface(Object obj) {
            this.surface = obj;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
            if (org.webrtc.EglRenderer.access$000(r3.this$0).hasSurface() == false) goto L_0x002a;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void run() {
            /*
                r3 = this;
                monitor-enter(r3)
                java.lang.Object r0 = r3.surface     // Catch:{ all -> 0x00b2 }
                if (r0 == 0) goto L_0x00b0
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                if (r0 == 0) goto L_0x00b0
                boolean r0 = r3.background     // Catch:{ all -> 0x00b2 }
                if (r0 == 0) goto L_0x001e
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                boolean r0 = r0.hasBackgroundSurface()     // Catch:{ all -> 0x00b2 }
                if (r0 != 0) goto L_0x00b0
                goto L_0x002a
            L_0x001e:
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                boolean r0 = r0.hasSurface()     // Catch:{ all -> 0x00b2 }
                if (r0 != 0) goto L_0x00b0
            L_0x002a:
                java.lang.Object r0 = r3.surface     // Catch:{ all -> 0x00b2 }
                boolean r1 = r0 instanceof android.view.Surface     // Catch:{ all -> 0x00b2 }
                if (r1 == 0) goto L_0x003e
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                java.lang.Object r1 = r3.surface     // Catch:{ all -> 0x00b2 }
                android.view.Surface r1 = (android.view.Surface) r1     // Catch:{ all -> 0x00b2 }
                r0.createSurface((android.view.Surface) r1)     // Catch:{ all -> 0x00b2 }
                goto L_0x0061
            L_0x003e:
                boolean r0 = r0 instanceof android.graphics.SurfaceTexture     // Catch:{ all -> 0x00b2 }
                if (r0 == 0) goto L_0x0097
                boolean r0 = r3.background     // Catch:{ all -> 0x00b2 }
                if (r0 == 0) goto L_0x0054
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                java.lang.Object r1 = r3.surface     // Catch:{ all -> 0x00b2 }
                android.graphics.SurfaceTexture r1 = (android.graphics.SurfaceTexture) r1     // Catch:{ all -> 0x00b2 }
                r0.createBackgroundSurface(r1)     // Catch:{ all -> 0x00b2 }
                goto L_0x0061
            L_0x0054:
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                java.lang.Object r1 = r3.surface     // Catch:{ all -> 0x00b2 }
                android.graphics.SurfaceTexture r1 = (android.graphics.SurfaceTexture) r1     // Catch:{ all -> 0x00b2 }
                r0.createSurface((android.graphics.SurfaceTexture) r1)     // Catch:{ all -> 0x00b2 }
            L_0x0061:
                boolean r0 = r3.background     // Catch:{ all -> 0x00b2 }
                r1 = 1
                r2 = 3317(0xcf5, float:4.648E-42)
                if (r0 != 0) goto L_0x0075
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                r0.makeCurrent()     // Catch:{ all -> 0x00b2 }
                android.opengl.GLES20.glPixelStorei(r2, r1)     // Catch:{ all -> 0x00b2 }
                goto L_0x00b0
            L_0x0075:
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                r0.makeBackgroundCurrent()     // Catch:{ all -> 0x00b2 }
                android.opengl.GLES20.glPixelStorei(r2, r1)     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                boolean r0 = r0.hasSurface()     // Catch:{ all -> 0x00b2 }
                if (r0 == 0) goto L_0x00b0
                org.webrtc.EglRenderer r0 = org.webrtc.EglRenderer.this     // Catch:{ all -> 0x00b2 }
                org.webrtc.EglBase r0 = r0.eglBase     // Catch:{ all -> 0x00b2 }
                r0.makeCurrent()     // Catch:{ all -> 0x00b2 }
                goto L_0x00b0
            L_0x0097:
                java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00b2 }
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b2 }
                r1.<init>()     // Catch:{ all -> 0x00b2 }
                java.lang.String r2 = "Invalid surface: "
                r1.append(r2)     // Catch:{ all -> 0x00b2 }
                java.lang.Object r2 = r3.surface     // Catch:{ all -> 0x00b2 }
                r1.append(r2)     // Catch:{ all -> 0x00b2 }
                java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00b2 }
                r0.<init>(r1)     // Catch:{ all -> 0x00b2 }
                throw r0     // Catch:{ all -> 0x00b2 }
            L_0x00b0:
                monitor-exit(r3)
                return
            L_0x00b2:
                r0 = move-exception
                monitor-exit(r3)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.EglSurfaceCreation.run():void");
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
        this.eglSurfaceCreationRunnable = new EglSurfaceCreation(false);
        this.eglSurfaceBackgroundCreationRunnable = new EglSurfaceCreation(true);
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
                handlerWithExceptionCallback.post(new Runnable(context, iArr) {
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
        createEglSurfaceInternal(surface, false);
    }

    public void createEglSurface(SurfaceTexture surfaceTexture) {
        createEglSurfaceInternal(surfaceTexture, false);
    }

    public void createBackgroundSurface(SurfaceTexture surfaceTexture) {
        createEglSurfaceInternal(surfaceTexture, true);
    }

    private void createEglSurfaceInternal(Object obj, boolean z) {
        if (z) {
            this.eglSurfaceBackgroundCreationRunnable.setSurface(obj);
            synchronized (this.handlerLock) {
                Handler handler = this.renderThreadHandler;
                if (handler != null) {
                    handler.post(this.eglSurfaceBackgroundCreationRunnable);
                } else {
                    FileLog.d("can't create background surface. render thread is null");
                }
            }
            return;
        }
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
        synchronized (this.handlerLock) {
            Handler handler = this.renderThreadHandler;
            Thread thread = handler == null ? null : handler.getLooper().getThread();
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

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0021, code lost:
        r4.run();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001f, code lost:
        if (r4 == null) goto L_?;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseEglSurface(java.lang.Runnable r4, boolean r5) {
        /*
            r3 = this;
            org.webrtc.EglRenderer$EglSurfaceCreation r0 = r3.eglSurfaceCreationRunnable
            r1 = 0
            r0.setSurface(r1)
            java.lang.Object r0 = r3.handlerLock
            monitor-enter(r0)
            android.os.Handler r1 = r3.renderThreadHandler     // Catch:{ all -> 0x0025 }
            if (r1 == 0) goto L_0x001e
            org.webrtc.EglRenderer$EglSurfaceCreation r2 = r3.eglSurfaceCreationRunnable     // Catch:{ all -> 0x0025 }
            r1.removeCallbacks(r2)     // Catch:{ all -> 0x0025 }
            android.os.Handler r1 = r3.renderThreadHandler     // Catch:{ all -> 0x0025 }
            org.webrtc.-$$Lambda$EglRenderer$QVOzCVAVmXRPog88Qz_fF-zgKvY r2 = new org.webrtc.-$$Lambda$EglRenderer$QVOzCVAVmXRPog88Qz_fF-zgKvY     // Catch:{ all -> 0x0025 }
            r2.<init>(r5, r4)     // Catch:{ all -> 0x0025 }
            r1.postAtFrontOfQueue(r2)     // Catch:{ all -> 0x0025 }
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            return
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            if (r4 == 0) goto L_0x0024
            r4.run()
        L_0x0024:
            return
        L_0x0025:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.releaseEglSurface(java.lang.Runnable, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$releaseEglSurface$5 */
    public /* synthetic */ void lambda$releaseEglSurface$5$EglRenderer(boolean z, Runnable runnable) {
        EglBase eglBase2 = this.eglBase;
        if (eglBase2 != null) {
            eglBase2.detachCurrent();
            this.eglBase.releaseSurface(z);
        }
        if (runnable != null) {
            runnable.run();
        }
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
            this.eglBase.swapBuffers(false);
        }
    }

    public void clearImage() {
        clearImage(0.0f, 0.0f, 0.0f, 0.0f);
        this.firstFrameRendered = false;
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

    public void getTexture(GlGenericDrawer.TextureCallback textureCallback) {
        synchronized (this.handlerLock) {
            try {
                Handler handler = this.renderThreadHandler;
                if (handler != null) {
                    handler.post(new Runnable(textureCallback) {
                        public final /* synthetic */ GlGenericDrawer.TextureCallback f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            EglRenderer.this.lambda$getTexture$7$EglRenderer(this.f$1);
                        }
                    });
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getTexture$7 */
    public /* synthetic */ void lambda$getTexture$7$EglRenderer(GlGenericDrawer.TextureCallback textureCallback) {
        this.frameDrawer.getRenderBufferBitmap(this.drawer, this.rotation, textureCallback);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x01a1, code lost:
        logD("Dropping frame - No surface");
        r13.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x01a9, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0011, code lost:
        if (r0 == null) goto L_0x01a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0017, code lost:
        if (r0.hasSurface() != false) goto L_0x001b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001b, code lost:
        r2 = r1.fpsReductionLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001d, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r3 = r1.minRenderPeriodNs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
        if (r3 != Long.MAX_VALUE) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0031, code lost:
        if (r3 > 0) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0033, code lost:
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0035, code lost:
        r3 = java.lang.System.nanoTime();
        r5 = r1.nextFrameTimeNs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003d, code lost:
        if (r3 >= r5) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x003f, code lost:
        logD("Skipping frame rendering - fps reduction is active.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0045, code lost:
        r5 = r5 + r1.minRenderPeriodNs;
        r1.nextFrameTimeNs = r5;
        r1.nextFrameTimeNs = java.lang.Math.max(r5, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0051, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0052, code lost:
        r16 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005e, code lost:
        if (java.lang.Math.abs(r1.rotation) == 90) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0068, code lost:
        if (java.lang.Math.abs(r1.rotation) != 270) goto L_0x006b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006b, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006d, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x006e, code lost:
        if (r2 == false) goto L_0x0075;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0070, code lost:
        r3 = r13.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0075, code lost:
        r3 = r13.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0079, code lost:
        r3 = (float) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007a, code lost:
        if (r2 == false) goto L_0x0081;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007c, code lost:
        r4 = r13.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0081, code lost:
        r4 = r13.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0085, code lost:
        r3 = r3 / ((float) r4);
        r4 = r1.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0089, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r5 = r1.layoutAspectRatio;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x008f, code lost:
        if (r5 == 0.0f) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0092, code lost:
        r5 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0093, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0094, code lost:
        r4 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0098, code lost:
        if (r3 <= r5) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x009a, code lost:
        r5 = r5 / r3;
        r3 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x009e, code lost:
        r3 = r3 / r5;
        r5 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00a1, code lost:
        r1.drawMatrix.reset();
        r1.drawMatrix.preTranslate(0.5f, 0.5f);
        r1.drawMatrix.preRotate((float) r1.rotation);
        r6 = r1.drawMatrix;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00bb, code lost:
        if (r1.mirrorHorizontally == false) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00bd, code lost:
        r7 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00c0, code lost:
        r7 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00c4, code lost:
        if (r1.mirrorVertically == false) goto L_0x00c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00c6, code lost:
        r4 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00c8, code lost:
        r6.preScale(r7, r4);
        r1.drawMatrix.preScale(r5, r3);
        r1.drawMatrix.preTranslate(-0.5f, -0.5f);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00d7, code lost:
        if (r15 == false) goto L_0x0170;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        r1.frameDrawer.drawFrame(r13, r1.drawer, r1.drawMatrix, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight(), r2, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00f9, code lost:
        if (r1.eglBase.hasBackgroundSurface() == false) goto L_0x0132;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00fb, code lost:
        r1.eglBase.makeBackgroundCurrent();
        r1.frameDrawer.drawFrame(r13, r1.drawer, r1.drawMatrix, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight(), r2, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x011c, code lost:
        if (r1.usePresentationTimeStamp == false) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x011e, code lost:
        r1.eglBase.swapBuffers(r13.getTimestampNs(), true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0128, code lost:
        r1.eglBase.swapBuffers(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x012d, code lost:
        r1.eglBase.makeCurrent();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0132, code lost:
        r2 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0138, code lost:
        if (r1.usePresentationTimeStamp == false) goto L_0x0144;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x013a, code lost:
        r1.eglBase.swapBuffers(r13.getTimestampNs(), false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0144, code lost:
        r1.eglBase.swapBuffers(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x014b, code lost:
        if (r1.firstFrameRendered != false) goto L_0x0152;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x014d, code lost:
        r1.firstFrameRendered = true;
        onFirstFrameRendered();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0152, code lost:
        r4 = java.lang.System.nanoTime();
        r6 = r1.statisticsLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0158, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:?, code lost:
        r1.framesRendered++;
        r1.renderTimeNs += r4 - r16;
        r1.renderSwapBufferTimeNs += r4 - r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x016b, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0170, code lost:
        notifyCallbacks(r13, r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0173, code lost:
        r13.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0177, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0179, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:?, code lost:
        logE("Error while drawing frame", r0);
        r0 = r1.errorCallback;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0181, code lost:
        if (r0 != null) goto L_0x0183;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0183, code lost:
        r0.onGlOutOfMemory();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0186, code lost:
        r1.drawer.release();
        r1.frameDrawer.release();
        r1.bitmapTextureFramebuffer.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0196, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0197, code lost:
        r13.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x019a, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000f, code lost:
        r0 = r1.eglBase;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrameOnRenderThread() {
        /*
            r18 = this;
            r1 = r18
            java.lang.Object r2 = r1.frameLock
            monitor-enter(r2)
            org.webrtc.VideoFrame r13 = r1.pendingFrame     // Catch:{ all -> 0x01aa }
            if (r13 != 0) goto L_0x000b
            monitor-exit(r2)     // Catch:{ all -> 0x01aa }
            return
        L_0x000b:
            r0 = 0
            r1.pendingFrame = r0     // Catch:{ all -> 0x01aa }
            monitor-exit(r2)     // Catch:{ all -> 0x01aa }
            org.webrtc.EglBase r0 = r1.eglBase
            if (r0 == 0) goto L_0x01a1
            boolean r0 = r0.hasSurface()
            if (r0 != 0) goto L_0x001b
            goto L_0x01a1
        L_0x001b:
            java.lang.Object r2 = r1.fpsReductionLock
            monitor-enter(r2)
            long r3 = r1.minRenderPeriodNs     // Catch:{ all -> 0x019e }
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r0 = 0
            r14 = 1
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x002d
        L_0x002b:
            r15 = 0
            goto L_0x0051
        L_0x002d:
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 > 0) goto L_0x0035
        L_0x0033:
            r15 = 1
            goto L_0x0051
        L_0x0035:
            long r3 = java.lang.System.nanoTime()     // Catch:{ all -> 0x019e }
            long r5 = r1.nextFrameTimeNs     // Catch:{ all -> 0x019e }
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x0045
            java.lang.String r3 = "Skipping frame rendering - fps reduction is active."
            r1.logD(r3)     // Catch:{ all -> 0x019e }
            goto L_0x002b
        L_0x0045:
            long r7 = r1.minRenderPeriodNs     // Catch:{ all -> 0x019e }
            long r5 = r5 + r7
            r1.nextFrameTimeNs = r5     // Catch:{ all -> 0x019e }
            long r3 = java.lang.Math.max(r5, r3)     // Catch:{ all -> 0x019e }
            r1.nextFrameTimeNs = r3     // Catch:{ all -> 0x019e }
            goto L_0x0033
        L_0x0051:
            monitor-exit(r2)     // Catch:{ all -> 0x019e }
            long r16 = java.lang.System.nanoTime()
            int r2 = r1.rotation
            int r2 = java.lang.Math.abs(r2)
            r3 = 90
            if (r2 == r3) goto L_0x006d
            int r2 = r1.rotation
            int r2 = java.lang.Math.abs(r2)
            r3 = 270(0x10e, float:3.78E-43)
            if (r2 != r3) goto L_0x006b
            goto L_0x006d
        L_0x006b:
            r2 = 0
            goto L_0x006e
        L_0x006d:
            r2 = 1
        L_0x006e:
            if (r2 == 0) goto L_0x0075
            int r3 = r13.getRotatedHeight()
            goto L_0x0079
        L_0x0075:
            int r3 = r13.getRotatedWidth()
        L_0x0079:
            float r3 = (float) r3
            if (r2 == 0) goto L_0x0081
            int r4 = r13.getRotatedWidth()
            goto L_0x0085
        L_0x0081:
            int r4 = r13.getRotatedHeight()
        L_0x0085:
            float r4 = (float) r4
            float r3 = r3 / r4
            java.lang.Object r4 = r1.layoutLock
            monitor-enter(r4)
            float r5 = r1.layoutAspectRatio     // Catch:{ all -> 0x019b }
            r6 = 0
            int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r6 == 0) goto L_0x0092
            goto L_0x0093
        L_0x0092:
            r5 = r3
        L_0x0093:
            monitor-exit(r4)     // Catch:{ all -> 0x019b }
            r4 = 1065353216(0x3var_, float:1.0)
            int r6 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r6 <= 0) goto L_0x009e
            float r5 = r5 / r3
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x00a1
        L_0x009e:
            float r3 = r3 / r5
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x00a1:
            android.graphics.Matrix r6 = r1.drawMatrix
            r6.reset()
            android.graphics.Matrix r6 = r1.drawMatrix
            r7 = 1056964608(0x3var_, float:0.5)
            r6.preTranslate(r7, r7)
            android.graphics.Matrix r6 = r1.drawMatrix
            int r7 = r1.rotation
            float r7 = (float) r7
            r6.preRotate(r7)
            android.graphics.Matrix r6 = r1.drawMatrix
            boolean r7 = r1.mirrorHorizontally
            r8 = -1082130432(0xffffffffbvar_, float:-1.0)
            if (r7 == 0) goto L_0x00c0
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x00c2
        L_0x00c0:
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x00c2:
            boolean r9 = r1.mirrorVertically
            if (r9 == 0) goto L_0x00c8
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x00c8:
            r6.preScale(r7, r4)
            android.graphics.Matrix r4 = r1.drawMatrix
            r4.preScale(r5, r3)
            android.graphics.Matrix r3 = r1.drawMatrix
            r4 = -1090519040(0xffffffffbvar_, float:-0.5)
            r3.preTranslate(r4, r4)
            if (r15 == 0) goto L_0x0170
            org.webrtc.VideoFrameDrawer r3 = r1.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            org.webrtc.RendererCommon$GlDrawer r5 = r1.drawer     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            android.graphics.Matrix r6 = r1.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r7 = 0
            r8 = 0
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            int r9 = r4.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            int r10 = r4.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r12 = 0
            r4 = r13
            r11 = r2
            r3.drawFrame(r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            org.webrtc.EglBase r3 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            boolean r3 = r3.hasBackgroundSurface()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            if (r3 == 0) goto L_0x0132
            org.webrtc.EglBase r3 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r3.makeBackgroundCurrent()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            org.webrtc.VideoFrameDrawer r3 = r1.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            org.webrtc.RendererCommon$GlDrawer r5 = r1.drawer     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            android.graphics.Matrix r6 = r1.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r7 = 0
            r8 = 0
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            int r9 = r4.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            int r10 = r4.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r12 = 1
            r4 = r13
            r11 = r2
            r3.drawFrame(r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            boolean r2 = r1.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            if (r2 == 0) goto L_0x0128
            org.webrtc.EglBase r2 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            long r3 = r13.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r2.swapBuffers(r3, r14)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            goto L_0x012d
        L_0x0128:
            org.webrtc.EglBase r2 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r2.swapBuffers(r14)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
        L_0x012d:
            org.webrtc.EglBase r2 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r2.makeCurrent()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
        L_0x0132:
            long r2 = java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            boolean r4 = r1.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            if (r4 == 0) goto L_0x0144
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            long r5 = r13.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r4.swapBuffers(r5, r0)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            goto L_0x0149
        L_0x0144:
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r4.swapBuffers(r0)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
        L_0x0149:
            boolean r0 = r1.firstFrameRendered     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            if (r0 != 0) goto L_0x0152
            r1.firstFrameRendered = r14     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            r18.onFirstFrameRendered()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
        L_0x0152:
            long r4 = java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            java.lang.Object r6 = r1.statisticsLock     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            monitor-enter(r6)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
            int r0 = r1.framesRendered     // Catch:{ all -> 0x016d }
            int r0 = r0 + r14
            r1.framesRendered = r0     // Catch:{ all -> 0x016d }
            long r7 = r1.renderTimeNs     // Catch:{ all -> 0x016d }
            long r9 = r4 - r16
            long r7 = r7 + r9
            r1.renderTimeNs = r7     // Catch:{ all -> 0x016d }
            long r7 = r1.renderSwapBufferTimeNs     // Catch:{ all -> 0x016d }
            long r4 = r4 - r2
            long r7 = r7 + r4
            r1.renderSwapBufferTimeNs = r7     // Catch:{ all -> 0x016d }
            monitor-exit(r6)     // Catch:{ all -> 0x016d }
            goto L_0x0170
        L_0x016d:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x016d }
            throw r0     // Catch:{ GlOutOfMemoryException -> 0x0179 }
        L_0x0170:
            r1.notifyCallbacks(r13, r15)     // Catch:{ GlOutOfMemoryException -> 0x0179 }
        L_0x0173:
            r13.release()
            goto L_0x0196
        L_0x0177:
            r0 = move-exception
            goto L_0x0197
        L_0x0179:
            r0 = move-exception
            java.lang.String r2 = "Error while drawing frame"
            r1.logE(r2, r0)     // Catch:{ all -> 0x0177 }
            org.webrtc.EglRenderer$ErrorCallback r0 = r1.errorCallback     // Catch:{ all -> 0x0177 }
            if (r0 == 0) goto L_0x0186
            r0.onGlOutOfMemory()     // Catch:{ all -> 0x0177 }
        L_0x0186:
            org.webrtc.RendererCommon$GlDrawer r0 = r1.drawer     // Catch:{ all -> 0x0177 }
            r0.release()     // Catch:{ all -> 0x0177 }
            org.webrtc.VideoFrameDrawer r0 = r1.frameDrawer     // Catch:{ all -> 0x0177 }
            r0.release()     // Catch:{ all -> 0x0177 }
            org.webrtc.GlTextureFrameBuffer r0 = r1.bitmapTextureFramebuffer     // Catch:{ all -> 0x0177 }
            r0.release()     // Catch:{ all -> 0x0177 }
            goto L_0x0173
        L_0x0196:
            return
        L_0x0197:
            r13.release()
            throw r0
        L_0x019b:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x019b }
            throw r0
        L_0x019e:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x019e }
            throw r0
        L_0x01a1:
            java.lang.String r0 = "Dropping frame - No surface"
            r1.logD(r0)
            r13.release()
            return
        L_0x01aa:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x01aa }
            goto L_0x01ae
        L_0x01ad:
            throw r0
        L_0x01ae:
            goto L_0x01ad
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
                        this.frameDrawer.drawFrame(videoFrame, next.drawer, this.drawMatrix, 0, 0, rotatedWidth, rotatedHeight, false, false);
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
