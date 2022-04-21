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
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.VideoSink;

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
    public boolean firstFrameRendered;
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
    private boolean usePresentationTimeStamp;

    public interface ErrorCallback {
        void onGlOutOfMemory();
    }

    public interface FrameListener {
        void onFrame(Bitmap bitmap);
    }

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }

    private static class FrameListenerAndParams {
        public final boolean applyFpsReduction;
        public final RendererCommon.GlDrawer drawer;
        public final FrameListener listener;
        public final float scale;

        public FrameListenerAndParams(FrameListener listener2, float scale2, RendererCommon.GlDrawer drawer2, boolean applyFpsReduction2) {
            this.listener = listener2;
            this.scale = scale2;
            this.drawer = drawer2;
            this.applyFpsReduction = applyFpsReduction2;
        }
    }

    private class EglSurfaceCreation implements Runnable {
        private final boolean background;
        private Object surface;

        public EglSurfaceCreation(boolean background2) {
            this.background = background2;
        }

        public synchronized void setSurface(Object surface2) {
            this.surface = surface2;
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

        public HandlerWithExceptionCallback(Looper looper, Runnable exceptionCallback2) {
            super(looper);
            this.exceptionCallback = exceptionCallback2;
        }

        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                Logging.e("EglRenderer", "Exception on EglRenderer thread", e);
                this.exceptionCallback.run();
                throw e;
            }
        }
    }

    public EglRenderer(String name2) {
        this(name2, new VideoFrameDrawer());
    }

    public EglRenderer(String name2, VideoFrameDrawer videoFrameDrawer) {
        this.handlerLock = new Object();
        this.frameListeners = new ArrayList<>();
        this.fpsReductionLock = new Object();
        this.drawMatrix = new Matrix();
        this.frameLock = new Object();
        this.layoutLock = new Object();
        this.bitmapTextureFramebuffer = new GlTextureFrameBuffer(6408);
        this.eglSurfaceCreationRunnable = new EglSurfaceCreation(false);
        this.eglSurfaceBackgroundCreationRunnable = new EglSurfaceCreation(true);
        this.name = name2;
        this.frameDrawer = videoFrameDrawer;
    }

    public void init(EglBase.Context sharedContext, int[] configAttributes, RendererCommon.GlDrawer drawer2, boolean usePresentationTimeStamp2) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                logD("Initializing EglRenderer");
                this.drawer = drawer2;
                this.usePresentationTimeStamp = usePresentationTimeStamp2;
                this.firstFrameRendered = false;
                HandlerThread renderThread = new HandlerThread(this.name + "EglRenderer");
                renderThread.start();
                HandlerWithExceptionCallback handlerWithExceptionCallback = new HandlerWithExceptionCallback(renderThread.getLooper(), new Runnable() {
                    public void run() {
                        synchronized (EglRenderer.this.handlerLock) {
                            Handler unused = EglRenderer.this.renderThreadHandler = null;
                        }
                    }
                });
                this.renderThreadHandler = handlerWithExceptionCallback;
                handlerWithExceptionCallback.post(new EglRenderer$$ExternalSyntheticLambda5(this, sharedContext, configAttributes));
                this.renderThreadHandler.post(this.eglSurfaceCreationRunnable);
            } else {
                throw new IllegalStateException(this.name + "Already initialized");
            }
        }
    }

    /* renamed from: lambda$init$0$org-webrtc-EglRenderer  reason: not valid java name */
    public /* synthetic */ void m4616lambda$init$0$orgwebrtcEglRenderer(EglBase.Context sharedContext, int[] configAttributes) {
        if (sharedContext == null) {
            logD("EglBase10.create context");
            this.eglBase = EglBase.CC.createEgl10(configAttributes);
            return;
        }
        logD("EglBase.create shared context");
        this.eglBase = EglBase.CC.create(sharedContext, configAttributes);
    }

    public void init(EglBase.Context sharedContext, int[] configAttributes, RendererCommon.GlDrawer drawer2) {
        init(sharedContext, configAttributes, drawer2, false);
    }

    public void createEglSurface(Surface surface) {
        createEglSurfaceInternal(surface, false);
    }

    public void createEglSurface(SurfaceTexture surfaceTexture) {
        createEglSurfaceInternal(surfaceTexture, false);
    }

    public void createBackgroundSurface(SurfaceTexture surface) {
        createEglSurfaceInternal(surface, true);
    }

    private void createEglSurfaceInternal(Object surface, boolean background) {
        if (background) {
            this.eglSurfaceBackgroundCreationRunnable.setSurface(surface);
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
        this.eglSurfaceCreationRunnable.setSurface(surface);
        postToRenderThread(this.eglSurfaceCreationRunnable);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0035, code lost:
        org.webrtc.ThreadUtils.awaitUninterruptibly(r0);
        r2 = r5.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003a, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r1 = r5.pendingFrame;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003d, code lost:
        if (r1 == null) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003f, code lost:
        r1.release();
        r5.pendingFrame = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0045, code lost:
        logD("Releasing done.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004a, code lost:
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
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x004e }
            if (r2 != 0) goto L_0x0019
            java.lang.String r2 = "Already released"
            r5.logD(r2)     // Catch:{ all -> 0x004e }
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
            return
        L_0x0019:
            org.webrtc.EglRenderer$$ExternalSyntheticLambda3 r3 = new org.webrtc.EglRenderer$$ExternalSyntheticLambda3     // Catch:{ all -> 0x004e }
            r3.<init>(r5, r0)     // Catch:{ all -> 0x004e }
            r2.postAtFrontOfQueue(r3)     // Catch:{ all -> 0x004e }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x004e }
            android.os.Looper r2 = r2.getLooper()     // Catch:{ all -> 0x004e }
            android.os.Handler r3 = r5.renderThreadHandler     // Catch:{ all -> 0x004e }
            org.webrtc.EglRenderer$$ExternalSyntheticLambda2 r4 = new org.webrtc.EglRenderer$$ExternalSyntheticLambda2     // Catch:{ all -> 0x004e }
            r4.<init>(r5, r2)     // Catch:{ all -> 0x004e }
            r3.post(r4)     // Catch:{ all -> 0x004e }
            r3 = 0
            r5.renderThreadHandler = r3     // Catch:{ all -> 0x004e }
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
            org.webrtc.ThreadUtils.awaitUninterruptibly(r0)
            java.lang.Object r2 = r5.frameLock
            monitor-enter(r2)
            org.webrtc.VideoFrame r1 = r5.pendingFrame     // Catch:{ all -> 0x004b }
            if (r1 == 0) goto L_0x0044
            r1.release()     // Catch:{ all -> 0x004b }
            r5.pendingFrame = r3     // Catch:{ all -> 0x004b }
        L_0x0044:
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            java.lang.String r1 = "Releasing done."
            r5.logD(r1)
            return
        L_0x004b:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            throw r1
        L_0x004e:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.release():void");
    }

    /* renamed from: lambda$release$1$org-webrtc-EglRenderer  reason: not valid java name */
    public /* synthetic */ void m4617lambda$release$1$orgwebrtcEglRenderer(CountDownLatch eglCleanupBarrier) {
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
        eglCleanupBarrier.countDown();
    }

    /* renamed from: lambda$release$2$org-webrtc-EglRenderer  reason: not valid java name */
    public /* synthetic */ void m4618lambda$release$2$orgwebrtcEglRenderer(Looper renderLooper) {
        logD("Quitting render thread.");
        renderLooper.quit();
    }

    public void printStackTrace() {
        synchronized (this.handlerLock) {
            Handler handler = this.renderThreadHandler;
            Thread renderThread = handler == null ? null : handler.getLooper().getThread();
            if (renderThread != null) {
                StackTraceElement[] renderStackTrace = renderThread.getStackTrace();
                if (renderStackTrace.length > 0) {
                    logW("EglRenderer stack trace:");
                    for (StackTraceElement traceElem : renderStackTrace) {
                        logW(traceElem.toString());
                    }
                }
            }
        }
    }

    public void setMirror(boolean mirror) {
        logD("setMirrorHorizontally: " + mirror);
        synchronized (this.layoutLock) {
            this.mirrorHorizontally = mirror;
        }
    }

    public void setMirrorVertically(boolean mirrorVertically2) {
        logD("setMirrorVertically: " + mirrorVertically2);
        synchronized (this.layoutLock) {
            this.mirrorVertically = mirrorVertically2;
        }
    }

    public void setLayoutAspectRatio(float layoutAspectRatio2) {
        if (this.layoutAspectRatio != layoutAspectRatio2) {
            synchronized (this.layoutLock) {
                this.layoutAspectRatio = layoutAspectRatio2;
            }
        }
    }

    public void setFpsReduction(float fps) {
        logD("setFpsReduction: " + fps);
        synchronized (this.fpsReductionLock) {
            long previousRenderPeriodNs = this.minRenderPeriodNs;
            if (fps <= 0.0f) {
                this.minRenderPeriodNs = Long.MAX_VALUE;
            } else {
                this.minRenderPeriodNs = (long) (((float) TimeUnit.SECONDS.toNanos(1)) / fps);
            }
            if (this.minRenderPeriodNs != previousRenderPeriodNs) {
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

    public void addFrameListener(FrameListener listener, float scale) {
        addFrameListener(listener, scale, (RendererCommon.GlDrawer) null, false);
    }

    public void addFrameListener(FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam) {
        addFrameListener(listener, scale, drawerParam, false);
    }

    public void addFrameListener(FrameListener listener, float scale, RendererCommon.GlDrawer drawerParam, boolean applyFpsReduction) {
        postToRenderThread(new EglRenderer$$ExternalSyntheticLambda7(this, drawerParam, listener, scale, applyFpsReduction));
    }

    /* renamed from: lambda$addFrameListener$3$org-webrtc-EglRenderer  reason: not valid java name */
    public /* synthetic */ void m4613lambda$addFrameListener$3$orgwebrtcEglRenderer(RendererCommon.GlDrawer drawerParam, FrameListener listener, float scale, boolean applyFpsReduction) {
        this.frameListeners.add(new FrameListenerAndParams(listener, scale, drawerParam == null ? this.drawer : drawerParam, applyFpsReduction));
    }

    public void removeFrameListener(FrameListener listener) {
        CountDownLatch latch = new CountDownLatch(1);
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler != null) {
                if (Thread.currentThread() != this.renderThreadHandler.getLooper().getThread()) {
                    postToRenderThread(new EglRenderer$$ExternalSyntheticLambda4(this, latch, listener));
                    ThreadUtils.awaitUninterruptibly(latch);
                    return;
                }
                throw new RuntimeException("removeFrameListener must not be called on the render thread.");
            }
        }
    }

    /* renamed from: lambda$removeFrameListener$4$org-webrtc-EglRenderer  reason: not valid java name */
    public /* synthetic */ void m4620lambda$removeFrameListener$4$orgwebrtcEglRenderer(CountDownLatch latch, FrameListener listener) {
        latch.countDown();
        Iterator<FrameListenerAndParams> iter = this.frameListeners.iterator();
        while (iter.hasNext()) {
            if (iter.next().listener == listener) {
                iter.remove();
            }
        }
    }

    public void setErrorCallback(ErrorCallback errorCallback2) {
        this.errorCallback = errorCallback2;
    }

    public void onFrame(VideoFrame frame) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                logD("Dropping frame - Not initialized or already released.");
                return;
            }
            synchronized (this.frameLock) {
                VideoFrame videoFrame = this.pendingFrame;
                if (videoFrame != null) {
                    videoFrame.release();
                }
                this.pendingFrame = frame;
                frame.retain();
                this.renderThreadHandler.post(new EglRenderer$$ExternalSyntheticLambda0(this));
            }
        }
    }

    public void setRotation(int value) {
        synchronized (this.layoutLock) {
            this.rotation = value;
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
            org.webrtc.EglRenderer$$ExternalSyntheticLambda8 r2 = new org.webrtc.EglRenderer$$ExternalSyntheticLambda8     // Catch:{ all -> 0x0025 }
            r2.<init>(r3, r5, r4)     // Catch:{ all -> 0x0025 }
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
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0025 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.releaseEglSurface(java.lang.Runnable, boolean):void");
    }

    /* renamed from: lambda$releaseEglSurface$5$org-webrtc-EglRenderer  reason: not valid java name */
    public /* synthetic */ void m4619lambda$releaseEglSurface$5$orgwebrtcEglRenderer(boolean background, Runnable completionCallback) {
        EglBase eglBase2 = this.eglBase;
        if (eglBase2 != null) {
            eglBase2.detachCurrent();
            this.eglBase.releaseSurface(background);
        }
        if (completionCallback != null) {
            completionCallback.run();
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
    public void m4614lambda$clearImage$6$orgwebrtcEglRenderer(float r, float g, float b, float a) {
        EglBase eglBase2 = this.eglBase;
        if (eglBase2 != null && eglBase2.hasSurface()) {
            logD("clearSurface");
            GLES20.glClearColor(r, g, b, a);
            GLES20.glClear(16384);
            this.eglBase.swapBuffers(false);
        }
    }

    public void clearImage() {
        clearImage(0.0f, 0.0f, 0.0f, 0.0f);
        this.firstFrameRendered = false;
    }

    public void clearImage(float r, float g, float b, float a) {
        synchronized (this.handlerLock) {
            Handler handler = this.renderThreadHandler;
            if (handler != null) {
                handler.postAtFrontOfQueue(new EglRenderer$$ExternalSyntheticLambda1(this, r, g, b, a));
            }
        }
    }

    public void getTexture(GlGenericDrawer.TextureCallback callback) {
        synchronized (this.handlerLock) {
            try {
                Handler handler = this.renderThreadHandler;
                if (handler != null) {
                    handler.post(new EglRenderer$$ExternalSyntheticLambda6(this, callback));
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$getTexture$7$org-webrtc-EglRenderer  reason: not valid java name */
    public /* synthetic */ void m4615lambda$getTexture$7$orgwebrtcEglRenderer(GlGenericDrawer.TextureCallback callback) {
        this.frameDrawer.getRenderBufferBitmap(this.drawer, this.rotation, callback);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x01b0, code lost:
        logD("Dropping frame - No surface");
        r13.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x01b8, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0012, code lost:
        if (r0 == null) goto L_0x01b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0018, code lost:
        if (r0.hasSurface() != false) goto L_0x001c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001c, code lost:
        r2 = r1.fpsReductionLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001e, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r3 = r1.minRenderPeriodNs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0028, code lost:
        if (r3 != Long.MAX_VALUE) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002a, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0031, code lost:
        if (r3 > 0) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0033, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0036, code lost:
        r3 = java.lang.System.nanoTime();
        r5 = r1.nextFrameTimeNs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003e, code lost:
        if (r3 >= r5) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0040, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0043, code lost:
        r5 = r5 + r1.minRenderPeriodNs;
        r1.nextFrameTimeNs = r5;
        r1.nextFrameTimeNs = java.lang.Math.max(r5, r3);
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0050, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0051, code lost:
        r15 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005f, code lost:
        if (java.lang.Math.abs(r1.rotation) == 90) goto L_0x006e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0069, code lost:
        if (java.lang.Math.abs(r1.rotation) != 270) goto L_0x006c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006c, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006e, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x006f, code lost:
        r17 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0071, code lost:
        if (r17 == false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0073, code lost:
        r0 = r13.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0078, code lost:
        r0 = r13.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007c, code lost:
        r0 = (float) r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007d, code lost:
        if (r17 == false) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x007f, code lost:
        r2 = r13.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0084, code lost:
        r2 = r13.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0088, code lost:
        r18 = r0 / ((float) r2);
        r3 = r1.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x008d, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        r0 = r1.layoutAspectRatio;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0093, code lost:
        if (r0 == 0.0f) goto L_0x0096;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0096, code lost:
        r0 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0098, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0099, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x009c, code lost:
        if (r18 <= r2) goto L_0x00a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x009e, code lost:
        r10 = r2 / r18;
        r9 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00a5, code lost:
        r10 = 1.0f;
        r9 = r18 / r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ab, code lost:
        r1.drawMatrix.reset();
        r1.drawMatrix.preTranslate(0.5f, 0.5f);
        r1.drawMatrix.preRotate((float) r1.rotation);
        r0 = r1.drawMatrix;
        r4 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00c7, code lost:
        if (r1.mirrorHorizontally == false) goto L_0x00cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00c9, code lost:
        r3 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00cc, code lost:
        r3 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00d0, code lost:
        if (r1.mirrorVertically == false) goto L_0x00d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00d3, code lost:
        r4 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00d5, code lost:
        r0.preScale(r3, r4);
        r1.drawMatrix.preScale(r10, r9);
        r1.drawMatrix.preTranslate(-0.5f, -0.5f);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00e4, code lost:
        if (r14 == false) goto L_0x017b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00fa, code lost:
        r21 = r9;
        r22 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
        r1.frameDrawer.drawFrame(r13, r1.drawer, r1.drawMatrix, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight(), r17, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0112, code lost:
        if (r1.eglBase.hasBackgroundSurface() == false) goto L_0x014c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0114, code lost:
        r1.eglBase.makeBackgroundCurrent();
        r1.frameDrawer.drawFrame(r13, r1.drawer, r1.drawMatrix, 0, 0, r1.eglBase.surfaceWidth(), r1.eglBase.surfaceHeight(), r17, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0136, code lost:
        if (r1.usePresentationTimeStamp == false) goto L_0x0142;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0138, code lost:
        r1.eglBase.swapBuffers(r13.getTimestampNs(), true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0142, code lost:
        r1.eglBase.swapBuffers(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0147, code lost:
        r1.eglBase.makeCurrent();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x014c, code lost:
        r3 = java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0152, code lost:
        if (r1.usePresentationTimeStamp == false) goto L_0x015f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0154, code lost:
        r1.eglBase.swapBuffers(r13.getTimestampNs(), false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x015f, code lost:
        r1.eglBase.swapBuffers(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0167, code lost:
        if (r1.firstFrameRendered != false) goto L_0x017f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0169, code lost:
        r1.firstFrameRendered = true;
        onFirstFrameRendered();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x016f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0170, code lost:
        r21 = r9;
        r22 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0175, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0176, code lost:
        r21 = r9;
        r22 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x017b, code lost:
        r21 = r9;
        r22 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x017f, code lost:
        notifyCallbacks(r13, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0182, code lost:
        r13.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0186, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0188, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:?, code lost:
        logE("Error while drawing frame", r0);
        r3 = r1.errorCallback;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x0190, code lost:
        if (r3 != null) goto L_0x0192;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0192, code lost:
        r3.onGlOutOfMemory();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0195, code lost:
        r1.drawer.release();
        r1.frameDrawer.release();
        r1.bitmapTextureFramebuffer.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x01a5, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x01a6, code lost:
        r13.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01a9, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        r0 = r1.eglBase;
     */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0192 A[Catch:{ all -> 0x0186 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void renderFrameOnRenderThread() {
        /*
            r23 = this;
            r1 = r23
            java.lang.Object r2 = r1.frameLock
            monitor-enter(r2)
            org.webrtc.VideoFrame r0 = r1.pendingFrame     // Catch:{ all -> 0x01b9 }
            if (r0 != 0) goto L_0x000b
            monitor-exit(r2)     // Catch:{ all -> 0x01b9 }
            return
        L_0x000b:
            r13 = r0
            r0 = 0
            r1.pendingFrame = r0     // Catch:{ all -> 0x01b9 }
            monitor-exit(r2)     // Catch:{ all -> 0x01b9 }
            org.webrtc.EglBase r0 = r1.eglBase
            if (r0 == 0) goto L_0x01b0
            boolean r0 = r0.hasSurface()
            if (r0 != 0) goto L_0x001c
            goto L_0x01b0
        L_0x001c:
            java.lang.Object r2 = r1.fpsReductionLock
            monitor-enter(r2)
            long r3 = r1.minRenderPeriodNs     // Catch:{ all -> 0x01ad }
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x002d
            r0 = 0
            r14 = r0
            goto L_0x0050
        L_0x002d:
            r5 = 0
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 > 0) goto L_0x0036
            r0 = 1
            r14 = r0
            goto L_0x0050
        L_0x0036:
            long r3 = java.lang.System.nanoTime()     // Catch:{ all -> 0x01ad }
            long r5 = r1.nextFrameTimeNs     // Catch:{ all -> 0x01ad }
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x0043
            r0 = 0
            r14 = r0
            goto L_0x0050
        L_0x0043:
            long r7 = r1.minRenderPeriodNs     // Catch:{ all -> 0x01ad }
            long r5 = r5 + r7
            r1.nextFrameTimeNs = r5     // Catch:{ all -> 0x01ad }
            long r5 = java.lang.Math.max(r5, r3)     // Catch:{ all -> 0x01ad }
            r1.nextFrameTimeNs = r5     // Catch:{ all -> 0x01ad }
            r0 = 1
            r14 = r0
        L_0x0050:
            monitor-exit(r2)     // Catch:{ all -> 0x01ad }
            long r15 = java.lang.System.nanoTime()
            int r0 = r1.rotation
            int r0 = java.lang.Math.abs(r0)
            r2 = 90
            r12 = 0
            r11 = 1
            if (r0 == r2) goto L_0x006e
            int r0 = r1.rotation
            int r0 = java.lang.Math.abs(r0)
            r2 = 270(0x10e, float:3.78E-43)
            if (r0 != r2) goto L_0x006c
            goto L_0x006e
        L_0x006c:
            r0 = 0
            goto L_0x006f
        L_0x006e:
            r0 = 1
        L_0x006f:
            r17 = r0
            if (r17 == 0) goto L_0x0078
            int r0 = r13.getRotatedHeight()
            goto L_0x007c
        L_0x0078:
            int r0 = r13.getRotatedWidth()
        L_0x007c:
            float r0 = (float) r0
            if (r17 == 0) goto L_0x0084
            int r2 = r13.getRotatedWidth()
            goto L_0x0088
        L_0x0084:
            int r2 = r13.getRotatedHeight()
        L_0x0088:
            float r2 = (float) r2
            float r18 = r0 / r2
            java.lang.Object r3 = r1.layoutLock
            monitor-enter(r3)
            float r0 = r1.layoutAspectRatio     // Catch:{ all -> 0x01aa }
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 == 0) goto L_0x0096
            goto L_0x0098
        L_0x0096:
            r0 = r18
        L_0x0098:
            r2 = r0
            monitor-exit(r3)     // Catch:{ all -> 0x01aa }
            int r0 = (r18 > r2 ? 1 : (r18 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x00a5
            float r0 = r2 / r18
            r3 = 1065353216(0x3var_, float:1.0)
            r10 = r0
            r9 = r3
            goto L_0x00ab
        L_0x00a5:
            r0 = 1065353216(0x3var_, float:1.0)
            float r3 = r18 / r2
            r10 = r0
            r9 = r3
        L_0x00ab:
            android.graphics.Matrix r0 = r1.drawMatrix
            r0.reset()
            android.graphics.Matrix r0 = r1.drawMatrix
            r3 = 1056964608(0x3var_, float:0.5)
            r0.preTranslate(r3, r3)
            android.graphics.Matrix r0 = r1.drawMatrix
            int r3 = r1.rotation
            float r3 = (float) r3
            r0.preRotate(r3)
            android.graphics.Matrix r0 = r1.drawMatrix
            boolean r3 = r1.mirrorHorizontally
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = 1065353216(0x3var_, float:1.0)
            if (r3 == 0) goto L_0x00cc
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x00ce
        L_0x00cc:
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x00ce:
            boolean r6 = r1.mirrorVertically
            if (r6 == 0) goto L_0x00d3
            goto L_0x00d5
        L_0x00d3:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x00d5:
            r0.preScale(r3, r4)
            android.graphics.Matrix r0 = r1.drawMatrix
            r0.preScale(r10, r9)
            android.graphics.Matrix r0 = r1.drawMatrix
            r3 = -1090519040(0xffffffffbvar_, float:-0.5)
            r0.preTranslate(r3, r3)
            if (r14 == 0) goto L_0x017b
            org.webrtc.VideoFrameDrawer r3 = r1.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x0175, all -> 0x016f }
            org.webrtc.RendererCommon$GlDrawer r5 = r1.drawer     // Catch:{ GlOutOfMemoryException -> 0x0175, all -> 0x016f }
            android.graphics.Matrix r6 = r1.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x0175, all -> 0x016f }
            r7 = 0
            r8 = 0
            org.webrtc.EglBase r0 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0175, all -> 0x016f }
            int r0 = r0.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x0175, all -> 0x016f }
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0175, all -> 0x016f }
            int r19 = r4.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x0175, all -> 0x016f }
            r20 = 0
            r4 = r13
            r21 = r9
            r9 = r0
            r22 = r10
            r10 = r19
            r0 = 1
            r11 = r17
            r12 = r20
            r3.drawFrame(r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            org.webrtc.EglBase r3 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            boolean r3 = r3.hasBackgroundSurface()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            if (r3 == 0) goto L_0x014c
            org.webrtc.EglBase r3 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r3.makeBackgroundCurrent()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            org.webrtc.VideoFrameDrawer r3 = r1.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            org.webrtc.RendererCommon$GlDrawer r5 = r1.drawer     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            android.graphics.Matrix r6 = r1.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r7 = 0
            r8 = 0
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            int r9 = r4.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            org.webrtc.EglBase r4 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            int r10 = r4.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r12 = 1
            r4 = r13
            r11 = r17
            r3.drawFrame(r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            boolean r3 = r1.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            if (r3 == 0) goto L_0x0142
            org.webrtc.EglBase r3 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            long r4 = r13.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r3.swapBuffers(r4, r0)     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            goto L_0x0147
        L_0x0142:
            org.webrtc.EglBase r3 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r3.swapBuffers(r0)     // Catch:{ GlOutOfMemoryException -> 0x0188 }
        L_0x0147:
            org.webrtc.EglBase r3 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r3.makeCurrent()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
        L_0x014c:
            long r3 = java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            boolean r5 = r1.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            if (r5 == 0) goto L_0x015f
            org.webrtc.EglBase r5 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            long r6 = r13.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r8 = 0
            r5.swapBuffers(r6, r8)     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            goto L_0x0165
        L_0x015f:
            r8 = 0
            org.webrtc.EglBase r5 = r1.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r5.swapBuffers(r8)     // Catch:{ GlOutOfMemoryException -> 0x0188 }
        L_0x0165:
            boolean r5 = r1.firstFrameRendered     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            if (r5 != 0) goto L_0x017f
            r1.firstFrameRendered = r0     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            r23.onFirstFrameRendered()     // Catch:{ GlOutOfMemoryException -> 0x0188 }
            goto L_0x017f
        L_0x016f:
            r0 = move-exception
            r21 = r9
            r22 = r10
            goto L_0x01a6
        L_0x0175:
            r0 = move-exception
            r21 = r9
            r22 = r10
            goto L_0x0189
        L_0x017b:
            r21 = r9
            r22 = r10
        L_0x017f:
            r1.notifyCallbacks(r13, r14)     // Catch:{ GlOutOfMemoryException -> 0x0188 }
        L_0x0182:
            r13.release()
            goto L_0x01a5
        L_0x0186:
            r0 = move-exception
            goto L_0x01a6
        L_0x0188:
            r0 = move-exception
        L_0x0189:
            java.lang.String r3 = "Error while drawing frame"
            r1.logE(r3, r0)     // Catch:{ all -> 0x0186 }
            org.webrtc.EglRenderer$ErrorCallback r3 = r1.errorCallback     // Catch:{ all -> 0x0186 }
            if (r3 == 0) goto L_0x0195
            r3.onGlOutOfMemory()     // Catch:{ all -> 0x0186 }
        L_0x0195:
            org.webrtc.RendererCommon$GlDrawer r4 = r1.drawer     // Catch:{ all -> 0x0186 }
            r4.release()     // Catch:{ all -> 0x0186 }
            org.webrtc.VideoFrameDrawer r4 = r1.frameDrawer     // Catch:{ all -> 0x0186 }
            r4.release()     // Catch:{ all -> 0x0186 }
            org.webrtc.GlTextureFrameBuffer r4 = r1.bitmapTextureFramebuffer     // Catch:{ all -> 0x0186 }
            r4.release()     // Catch:{ all -> 0x0186 }
            goto L_0x0182
        L_0x01a5:
            return
        L_0x01a6:
            r13.release()
            throw r0
        L_0x01aa:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x01aa }
            throw r0
        L_0x01ad:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x01ad }
            throw r0
        L_0x01b0:
            java.lang.String r0 = "Dropping frame - No surface"
            r1.logD(r0)
            r13.release()
            return
        L_0x01b9:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x01b9 }
            goto L_0x01bd
        L_0x01bc:
            throw r0
        L_0x01bd:
            goto L_0x01bc
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.EglRenderer.renderFrameOnRenderThread():void");
    }

    /* access modifiers changed from: protected */
    public void onFirstFrameRendered() {
    }

    private void notifyCallbacks(VideoFrame frame, boolean wasRendered) {
        if (!this.frameListeners.isEmpty()) {
            this.drawMatrix.reset();
            this.drawMatrix.preTranslate(0.5f, 0.5f);
            this.drawMatrix.preRotate((float) this.rotation);
            this.drawMatrix.preScale(this.mirrorHorizontally ? -1.0f : 1.0f, this.mirrorVertically ? -1.0f : 1.0f);
            this.drawMatrix.preScale(1.0f, -1.0f);
            this.drawMatrix.preTranslate(-0.5f, -0.5f);
            Iterator<FrameListenerAndParams> it = this.frameListeners.iterator();
            while (it.hasNext()) {
                FrameListenerAndParams listenerAndParams = it.next();
                if (wasRendered || !listenerAndParams.applyFpsReduction) {
                    it.remove();
                    int scaledWidth = (int) (listenerAndParams.scale * ((float) frame.getRotatedWidth()));
                    int scaledHeight = (int) (listenerAndParams.scale * ((float) frame.getRotatedHeight()));
                    if (scaledWidth == 0 || scaledHeight == 0) {
                        listenerAndParams.listener.onFrame((Bitmap) null);
                    } else {
                        this.bitmapTextureFramebuffer.setSize(scaledWidth, scaledHeight);
                        GLES20.glBindFramebuffer(36160, this.bitmapTextureFramebuffer.getFrameBufferId());
                        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.bitmapTextureFramebuffer.getTextureId(), 0);
                        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        GLES20.glClear(16384);
                        this.frameDrawer.drawFrame(frame, listenerAndParams.drawer, this.drawMatrix, 0, 0, scaledWidth, scaledHeight, false, false);
                        ByteBuffer bitmapBuffer = ByteBuffer.allocateDirect(scaledWidth * scaledHeight * 4);
                        GLES20.glViewport(0, 0, scaledWidth, scaledHeight);
                        GLES20.glReadPixels(0, 0, scaledWidth, scaledHeight, 6408, 5121, bitmapBuffer);
                        GLES20.glBindFramebuffer(36160, 0);
                        GlUtil.checkNoGLES2Error("EglRenderer.notifyCallbacks");
                        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(bitmapBuffer);
                        listenerAndParams.listener.onFrame(bitmap);
                    }
                }
            }
        }
    }

    private void logE(String string, Throwable e) {
        Logging.e("EglRenderer", this.name + string, e);
    }

    private void logD(String string) {
        Logging.d("EglRenderer", this.name + string);
    }

    private void logW(String string) {
        Logging.w("EglRenderer", this.name + string);
    }
}
