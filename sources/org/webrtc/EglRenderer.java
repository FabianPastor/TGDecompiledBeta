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

    /* access modifiers changed from: protected */
    public void onFirstFrameRendered() {
    }

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
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
        this.bitmapTextureFramebuffer = new GlTextureFrameBuffer(6408);
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

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0035, code lost:
        org.webrtc.ThreadUtils.awaitUninterruptibly(r0);
        r0 = r5.frameLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x003a, code lost:
        monitor-enter(r0);
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
        monitor-exit(r0);
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
            java.lang.String r0 = "Already released"
            r5.logD(r0)     // Catch:{ all -> 0x004e }
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
            return
        L_0x0019:
            org.webrtc.-$$Lambda$EglRenderer$dbZIvF-jDfWfAxWS6_OKLODul18 r3 = new org.webrtc.-$$Lambda$EglRenderer$dbZIvF-jDfWfAxWS6_OKLODul18     // Catch:{ all -> 0x004e }
            r3.<init>(r0)     // Catch:{ all -> 0x004e }
            r2.postAtFrontOfQueue(r3)     // Catch:{ all -> 0x004e }
            android.os.Handler r2 = r5.renderThreadHandler     // Catch:{ all -> 0x004e }
            android.os.Looper r2 = r2.getLooper()     // Catch:{ all -> 0x004e }
            android.os.Handler r3 = r5.renderThreadHandler     // Catch:{ all -> 0x004e }
            org.webrtc.-$$Lambda$EglRenderer$pvyzzKSwJYfQ10Yf4Pez7unf1S4 r4 = new org.webrtc.-$$Lambda$EglRenderer$pvyzzKSwJYfQ10Yf4Pez7unf1S4     // Catch:{ all -> 0x004e }
            r4.<init>(r2)     // Catch:{ all -> 0x004e }
            r3.post(r4)     // Catch:{ all -> 0x004e }
            r2 = 0
            r5.renderThreadHandler = r2     // Catch:{ all -> 0x004e }
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
            org.webrtc.ThreadUtils.awaitUninterruptibly(r0)
            java.lang.Object r0 = r5.frameLock
            monitor-enter(r0)
            org.webrtc.VideoFrame r1 = r5.pendingFrame     // Catch:{ all -> 0x004b }
            if (r1 == 0) goto L_0x0044
            r1.release()     // Catch:{ all -> 0x004b }
            r5.pendingFrame = r2     // Catch:{ all -> 0x004b }
        L_0x0044:
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            java.lang.String r0 = "Releasing done."
            r5.logD(r0)
            return
        L_0x004b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004b }
            throw r1
        L_0x004e:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x004e }
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
        if (this.layoutAspectRatio != f) {
            synchronized (this.layoutLock) {
                this.layoutAspectRatio = f;
            }
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

    public void onFrame(VideoFrame videoFrame) {
        synchronized (this.handlerLock) {
            if (this.renderThreadHandler == null) {
                logD("Dropping frame - Not initialized or already released.");
                return;
            }
            synchronized (this.frameLock) {
                VideoFrame videoFrame2 = this.pendingFrame;
                if (videoFrame2 != null) {
                    videoFrame2.release();
                }
                this.pendingFrame = videoFrame;
                videoFrame.retain();
                this.renderThreadHandler.post(new Runnable() {
                    public final void run() {
                        EglRenderer.this.renderFrameOnRenderThread();
                    }
                });
            }
        }
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
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000f, code lost:
        if (r0 == null) goto L_0x017a;
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
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002f, code lost:
        if (r1 > 0) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0031, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0033, code lost:
        r1 = java.lang.System.nanoTime();
        r3 = r15.nextFrameTimeNs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003b, code lost:
        if (r1 >= r3) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003e, code lost:
        r3 = r3 + r15.minRenderPeriodNs;
        r15.nextFrameTimeNs = r3;
        r15.nextFrameTimeNs = java.lang.Math.max(r3, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004a, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004b, code lost:
        java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0056, code lost:
        if (java.lang.Math.abs(r15.rotation) == 90) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0060, code lost:
        if (java.lang.Math.abs(r15.rotation) != 270) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0063, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0065, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0066, code lost:
        if (r0 == false) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0068, code lost:
        r1 = r11.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x006d, code lost:
        r1 = r11.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0071, code lost:
        r1 = (float) r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0072, code lost:
        if (r0 == false) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0074, code lost:
        r2 = r11.getRotatedWidth();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0079, code lost:
        r2 = r11.getRotatedHeight();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x007d, code lost:
        r1 = r1 / ((float) r2);
        r2 = r15.layoutLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0081, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        r3 = r15.layoutAspectRatio;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0087, code lost:
        if (r3 == 0.0f) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x008a, code lost:
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x008b, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x008c, code lost:
        r2 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0090, code lost:
        if (r1 <= r3) goto L_0x0096;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0092, code lost:
        r3 = r3 / r1;
        r1 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0096, code lost:
        r1 = r1 / r3;
        r3 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0099, code lost:
        r15.drawMatrix.reset();
        r15.drawMatrix.preTranslate(0.5f, 0.5f);
        r15.drawMatrix.preRotate((float) r15.rotation);
        r4 = r15.drawMatrix;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b3, code lost:
        if (r15.mirrorHorizontally == false) goto L_0x00b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00b5, code lost:
        r5 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00b8, code lost:
        r5 = 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00bc, code lost:
        if (r15.mirrorVertically == false) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00be, code lost:
        r2 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00c0, code lost:
        r4.preScale(r5, r2);
        r15.drawMatrix.preScale(r3, r1);
        r15.drawMatrix.preTranslate(-0.5f, -0.5f);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00cf, code lost:
        if (r14 == false) goto L_0x0149;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
        r15.frameDrawer.drawFrame(r11, r15.drawer, r15.drawMatrix, 0, 0, r15.eglBase.surfaceWidth(), r15.eglBase.surfaceHeight(), r0, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00f1, code lost:
        if (r15.eglBase.hasBackgroundSurface() == false) goto L_0x012a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00f3, code lost:
        r15.eglBase.makeBackgroundCurrent();
        r15.frameDrawer.drawFrame(r11, r15.drawer, r15.drawMatrix, 0, 0, r15.eglBase.surfaceWidth(), r15.eglBase.surfaceHeight(), r0, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0114, code lost:
        if (r15.usePresentationTimeStamp == false) goto L_0x0120;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0116, code lost:
        r15.eglBase.swapBuffers(r11.getTimestampNs(), true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0120, code lost:
        r15.eglBase.swapBuffers(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0125, code lost:
        r15.eglBase.makeCurrent();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x012a, code lost:
        java.lang.System.nanoTime();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x012f, code lost:
        if (r15.usePresentationTimeStamp == false) goto L_0x013b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0131, code lost:
        r15.eglBase.swapBuffers(r11.getTimestampNs(), false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x013b, code lost:
        r15.eglBase.swapBuffers(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0142, code lost:
        if (r15.firstFrameRendered != false) goto L_0x0149;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0144, code lost:
        r15.firstFrameRendered = true;
        onFirstFrameRendered();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0149, code lost:
        notifyCallbacks(r11, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0150, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0152, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:?, code lost:
        logE("Error while drawing frame", r0);
        r0 = r15.errorCallback;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x015a, code lost:
        if (r0 != null) goto L_0x015c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x015c, code lost:
        r0.onGlOutOfMemory();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x015f, code lost:
        r15.drawer.release();
        r15.frameDrawer.release();
        r15.bitmapTextureFramebuffer.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0170, code lost:
        r11.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0173, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x017a, code lost:
        logD("Dropping frame - No surface");
        r11.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0182, code lost:
        return;
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
            org.webrtc.VideoFrame r11 = r15.pendingFrame     // Catch:{ all -> 0x0183 }
            if (r11 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0183 }
            return
        L_0x0009:
            r1 = 0
            r15.pendingFrame = r1     // Catch:{ all -> 0x0183 }
            monitor-exit(r0)     // Catch:{ all -> 0x0183 }
            org.webrtc.EglBase r0 = r15.eglBase
            if (r0 == 0) goto L_0x017a
            boolean r0 = r0.hasSurface()
            if (r0 != 0) goto L_0x0019
            goto L_0x017a
        L_0x0019:
            java.lang.Object r0 = r15.fpsReductionLock
            monitor-enter(r0)
            long r1 = r15.minRenderPeriodNs     // Catch:{ all -> 0x0177 }
            r3 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r12 = 0
            r13 = 1
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x002b
        L_0x0029:
            r14 = 0
            goto L_0x004a
        L_0x002b:
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 > 0) goto L_0x0033
        L_0x0031:
            r14 = 1
            goto L_0x004a
        L_0x0033:
            long r1 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0177 }
            long r3 = r15.nextFrameTimeNs     // Catch:{ all -> 0x0177 }
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x003e
            goto L_0x0029
        L_0x003e:
            long r5 = r15.minRenderPeriodNs     // Catch:{ all -> 0x0177 }
            long r3 = r3 + r5
            r15.nextFrameTimeNs = r3     // Catch:{ all -> 0x0177 }
            long r1 = java.lang.Math.max(r3, r1)     // Catch:{ all -> 0x0177 }
            r15.nextFrameTimeNs = r1     // Catch:{ all -> 0x0177 }
            goto L_0x0031
        L_0x004a:
            monitor-exit(r0)     // Catch:{ all -> 0x0177 }
            java.lang.System.nanoTime()
            int r0 = r15.rotation
            int r0 = java.lang.Math.abs(r0)
            r1 = 90
            if (r0 == r1) goto L_0x0065
            int r0 = r15.rotation
            int r0 = java.lang.Math.abs(r0)
            r1 = 270(0x10e, float:3.78E-43)
            if (r0 != r1) goto L_0x0063
            goto L_0x0065
        L_0x0063:
            r0 = 0
            goto L_0x0066
        L_0x0065:
            r0 = 1
        L_0x0066:
            if (r0 == 0) goto L_0x006d
            int r1 = r11.getRotatedHeight()
            goto L_0x0071
        L_0x006d:
            int r1 = r11.getRotatedWidth()
        L_0x0071:
            float r1 = (float) r1
            if (r0 == 0) goto L_0x0079
            int r2 = r11.getRotatedWidth()
            goto L_0x007d
        L_0x0079:
            int r2 = r11.getRotatedHeight()
        L_0x007d:
            float r2 = (float) r2
            float r1 = r1 / r2
            java.lang.Object r2 = r15.layoutLock
            monitor-enter(r2)
            float r3 = r15.layoutAspectRatio     // Catch:{ all -> 0x0174 }
            r4 = 0
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 == 0) goto L_0x008a
            goto L_0x008b
        L_0x008a:
            r3 = r1
        L_0x008b:
            monitor-exit(r2)     // Catch:{ all -> 0x0174 }
            r2 = 1065353216(0x3var_, float:1.0)
            int r4 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0096
            float r3 = r3 / r1
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x0099
        L_0x0096:
            float r1 = r1 / r3
            r3 = 1065353216(0x3var_, float:1.0)
        L_0x0099:
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
            if (r5 == 0) goto L_0x00b8
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x00ba
        L_0x00b8:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x00ba:
            boolean r7 = r15.mirrorVertically
            if (r7 == 0) goto L_0x00c0
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
        L_0x00c0:
            r4.preScale(r5, r2)
            android.graphics.Matrix r2 = r15.drawMatrix
            r2.preScale(r3, r1)
            android.graphics.Matrix r1 = r15.drawMatrix
            r2 = -1090519040(0xffffffffbvar_, float:-0.5)
            r1.preTranslate(r2, r2)
            if (r14 == 0) goto L_0x0149
            org.webrtc.VideoFrameDrawer r1 = r15.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            org.webrtc.RendererCommon$GlDrawer r3 = r15.drawer     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            android.graphics.Matrix r4 = r15.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r5 = 0
            r6 = 0
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            int r7 = r2.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            int r8 = r2.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r10 = 0
            r2 = r11
            r9 = r0
            r1.drawFrame(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            org.webrtc.EglBase r1 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            boolean r1 = r1.hasBackgroundSurface()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            if (r1 == 0) goto L_0x012a
            org.webrtc.EglBase r1 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r1.makeBackgroundCurrent()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            org.webrtc.VideoFrameDrawer r1 = r15.frameDrawer     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            org.webrtc.RendererCommon$GlDrawer r3 = r15.drawer     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            android.graphics.Matrix r4 = r15.drawMatrix     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r5 = 0
            r6 = 0
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            int r7 = r2.surfaceWidth()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            org.webrtc.EglBase r2 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            int r8 = r2.surfaceHeight()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r10 = 1
            r2 = r11
            r9 = r0
            r1.drawFrame(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            boolean r0 = r15.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            if (r0 == 0) goto L_0x0120
            org.webrtc.EglBase r0 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            long r1 = r11.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r0.swapBuffers(r1, r13)     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            goto L_0x0125
        L_0x0120:
            org.webrtc.EglBase r0 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r0.swapBuffers(r13)     // Catch:{ GlOutOfMemoryException -> 0x0152 }
        L_0x0125:
            org.webrtc.EglBase r0 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r0.makeCurrent()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
        L_0x012a:
            java.lang.System.nanoTime()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            boolean r0 = r15.usePresentationTimeStamp     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            if (r0 == 0) goto L_0x013b
            org.webrtc.EglBase r0 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            long r1 = r11.getTimestampNs()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r0.swapBuffers(r1, r12)     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            goto L_0x0140
        L_0x013b:
            org.webrtc.EglBase r0 = r15.eglBase     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r0.swapBuffers(r12)     // Catch:{ GlOutOfMemoryException -> 0x0152 }
        L_0x0140:
            boolean r0 = r15.firstFrameRendered     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            if (r0 != 0) goto L_0x0149
            r15.firstFrameRendered = r13     // Catch:{ GlOutOfMemoryException -> 0x0152 }
            r15.onFirstFrameRendered()     // Catch:{ GlOutOfMemoryException -> 0x0152 }
        L_0x0149:
            r15.notifyCallbacks(r11, r14)     // Catch:{ GlOutOfMemoryException -> 0x0152 }
        L_0x014c:
            r11.release()
            goto L_0x016f
        L_0x0150:
            r0 = move-exception
            goto L_0x0170
        L_0x0152:
            r0 = move-exception
            java.lang.String r1 = "Error while drawing frame"
            r15.logE(r1, r0)     // Catch:{ all -> 0x0150 }
            org.webrtc.EglRenderer$ErrorCallback r0 = r15.errorCallback     // Catch:{ all -> 0x0150 }
            if (r0 == 0) goto L_0x015f
            r0.onGlOutOfMemory()     // Catch:{ all -> 0x0150 }
        L_0x015f:
            org.webrtc.RendererCommon$GlDrawer r0 = r15.drawer     // Catch:{ all -> 0x0150 }
            r0.release()     // Catch:{ all -> 0x0150 }
            org.webrtc.VideoFrameDrawer r0 = r15.frameDrawer     // Catch:{ all -> 0x0150 }
            r0.release()     // Catch:{ all -> 0x0150 }
            org.webrtc.GlTextureFrameBuffer r0 = r15.bitmapTextureFramebuffer     // Catch:{ all -> 0x0150 }
            r0.release()     // Catch:{ all -> 0x0150 }
            goto L_0x014c
        L_0x016f:
            return
        L_0x0170:
            r11.release()
            throw r0
        L_0x0174:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0174 }
            throw r0
        L_0x0177:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0177 }
            throw r1
        L_0x017a:
            java.lang.String r0 = "Dropping frame - No surface"
            r15.logD(r0)
            r11.release()
            return
        L_0x0183:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0183 }
            goto L_0x0187
        L_0x0186:
            throw r1
        L_0x0187:
            goto L_0x0186
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
