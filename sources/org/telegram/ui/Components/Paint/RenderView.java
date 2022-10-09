package org.telegram.ui.Components.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.TextureView;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Size;
/* loaded from: classes3.dex */
public class RenderView extends TextureView {
    private Bitmap bitmap;
    private Brush brush;
    private int color;
    private RenderViewDelegate delegate;
    private boolean firstDrawSent;
    private Input input;
    private CanvasInternal internal;
    private Painting painting;
    private DispatchQueue queue;
    private boolean shuttingDown;
    private boolean transformedBitmap;
    private UndoStore undoStore;
    private float weight;

    /* loaded from: classes3.dex */
    public interface RenderViewDelegate {
        void onBeganDrawing();

        void onFinishedDrawing(boolean z);

        void onFirstDraw();

        boolean shouldDraw();
    }

    public RenderView(Context context, Painting painting, Bitmap bitmap) {
        super(context);
        setOpaque(false);
        this.bitmap = bitmap;
        this.painting = painting;
        painting.setRenderView(this);
        setSurfaceTextureListener(new AnonymousClass1());
        this.input = new Input(this);
        this.painting.setDelegate(new Painting.PaintingDelegate() { // from class: org.telegram.ui.Components.Paint.RenderView.2
            @Override // org.telegram.ui.Components.Paint.Painting.PaintingDelegate
            public void contentChanged() {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.scheduleRedraw();
                }
            }

            @Override // org.telegram.ui.Components.Paint.Painting.PaintingDelegate
            public UndoStore requestUndoStore() {
                return RenderView.this.undoStore;
            }

            @Override // org.telegram.ui.Components.Paint.Painting.PaintingDelegate
            public DispatchQueue requestDispatchQueue() {
                return RenderView.this.queue;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.Components.Paint.RenderView$1  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass1 implements TextureView.SurfaceTextureListener {
        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        AnonymousClass1() {
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            if (surfaceTexture == null || RenderView.this.internal != null) {
                return;
            }
            RenderView.this.internal = new CanvasInternal(surfaceTexture);
            RenderView.this.internal.setBufferSize(i, i2);
            RenderView.this.updateTransform();
            RenderView.this.internal.requestRender();
            if (!RenderView.this.painting.isPaused()) {
                return;
            }
            RenderView.this.painting.onResume();
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            if (RenderView.this.internal == null) {
                return;
            }
            RenderView.this.internal.setBufferSize(i, i2);
            RenderView.this.updateTransform();
            RenderView.this.internal.requestRender();
            RenderView.this.internal.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.AnonymousClass1.this.lambda$onSurfaceTextureSizeChanged$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSurfaceTextureSizeChanged$0() {
            if (RenderView.this.internal != null) {
                RenderView.this.internal.requestRender();
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (RenderView.this.internal != null && !RenderView.this.shuttingDown) {
                RenderView.this.painting.onPause(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        RenderView.AnonymousClass1.this.lambda$onSurfaceTextureDestroyed$1();
                    }
                });
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSurfaceTextureDestroyed$1() {
            RenderView.this.internal.shutdown();
            RenderView.this.internal = null;
        }
    }

    public void redraw() {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal == null) {
            return;
        }
        canvasInternal.requestRender();
    }

    public boolean onTouch(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            return false;
        }
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null && canvasInternal.initialized && this.internal.ready) {
            this.input.process(motionEvent, getScaleX());
        }
        return true;
    }

    public void setUndoStore(UndoStore undoStore) {
        this.undoStore = undoStore;
    }

    public void setQueue(DispatchQueue dispatchQueue) {
        this.queue = dispatchQueue;
    }

    public void setDelegate(RenderViewDelegate renderViewDelegate) {
        this.delegate = renderViewDelegate;
    }

    public Painting getPainting() {
        return this.painting;
    }

    private float brushWeightForSize(float f) {
        float f2 = this.painting.getSize().width;
        return (0.00390625f * f2) + (f2 * 0.043945312f * f);
    }

    public int getCurrentColor() {
        return this.color;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public float getCurrentWeight() {
        return this.weight;
    }

    public void setBrushSize(float f) {
        this.weight = brushWeightForSize(f);
    }

    public Brush getCurrentBrush() {
        return this.brush;
    }

    public void setBrush(Brush brush) {
        Painting painting = this.painting;
        this.brush = brush;
        painting.setBrush(brush);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTransform() {
        Matrix matrix = new Matrix();
        float f = 1.0f;
        float width = this.painting != null ? getWidth() / this.painting.getSize().width : 1.0f;
        if (width > 0.0f) {
            f = width;
        }
        Size size = getPainting().getSize();
        matrix.preTranslate(getWidth() / 2.0f, getHeight() / 2.0f);
        matrix.preScale(f, -f);
        matrix.preTranslate((-size.width) / 2.0f, (-size.height) / 2.0f);
        this.input.setMatrix(matrix);
        this.painting.setRenderProjection(GLMatrix.MultiplyMat4f(GLMatrix.LoadOrtho(0.0f, this.internal.bufferWidth, 0.0f, this.internal.bufferHeight, -1.0f, 1.0f), GLMatrix.LoadGraphicsMatrix(matrix)));
    }

    public boolean shouldDraw() {
        RenderViewDelegate renderViewDelegate = this.delegate;
        return renderViewDelegate == null || renderViewDelegate.shouldDraw();
    }

    public void onBeganDrawing() {
        RenderViewDelegate renderViewDelegate = this.delegate;
        if (renderViewDelegate != null) {
            renderViewDelegate.onBeganDrawing();
        }
    }

    public void onFinishedDrawing(boolean z) {
        RenderViewDelegate renderViewDelegate = this.delegate;
        if (renderViewDelegate != null) {
            renderViewDelegate.onFinishedDrawing(z);
        }
    }

    public void shutdown() {
        this.shuttingDown = true;
        if (this.internal != null) {
            performInContext(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.this.lambda$shutdown$0();
                }
            });
        }
        setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$shutdown$0() {
        this.painting.cleanResources(this.transformedBitmap);
        this.internal.shutdown();
        this.internal = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CanvasInternal extends DispatchQueue {
        private int bufferHeight;
        private int bufferWidth;
        private Runnable drawRunnable;
        private EGL10 egl10;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initialized;
        private volatile boolean ready;
        private Runnable scheduledRunnable;
        private SurfaceTexture surfaceTexture;

        public CanvasInternal(SurfaceTexture surfaceTexture) {
            super("CanvasInternal");
            this.drawRunnable = new AnonymousClass1();
            this.surfaceTexture = surfaceTexture;
        }

        @Override // org.telegram.messenger.DispatchQueue, java.lang.Thread, java.lang.Runnable
        public void run() {
            if (RenderView.this.bitmap == null || RenderView.this.bitmap.isRecycled()) {
                return;
            }
            this.initialized = initGL();
            super.run();
        }

        private boolean initGL() {
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl10;
            EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(eglGetDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] iArr = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (iArr[0] > 0) {
                EGLConfig eGLConfig = eGLConfigArr[0];
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture = this.surfaceTexture;
                if (surfaceTexture instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, eGLConfig, surfaceTexture, null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (!this.egl10.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else {
                        GLES20.glEnable(3042);
                        GLES20.glDisable(3024);
                        GLES20.glDisable(2960);
                        GLES20.glDisable(2929);
                        RenderView.this.painting.setupShaders();
                        checkBitmap();
                        RenderView.this.painting.setBitmap(RenderView.this.bitmap);
                        Utils.HasGLError();
                        return true;
                    }
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        private void checkBitmap() {
            Size size = RenderView.this.painting.getSize();
            if (RenderView.this.bitmap.getWidth() == size.width && RenderView.this.bitmap.getHeight() == size.height) {
                return;
            }
            Bitmap createBitmap = Bitmap.createBitmap((int) size.width, (int) size.height, Bitmap.Config.ARGB_8888);
            new Canvas(createBitmap).drawBitmap(RenderView.this.bitmap, (Rect) null, new RectF(0.0f, 0.0f, size.width, size.height), (Paint) null);
            RenderView.this.bitmap = createBitmap;
            RenderView.this.transformedBitmap = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean setCurrentContext() {
            if (!this.initialized) {
                return false;
            }
            if (this.eglContext.equals(this.egl10.eglGetCurrentContext()) && this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                return true;
            }
            EGL10 egl10 = this.egl10;
            EGLDisplay eGLDisplay = this.eglDisplay;
            EGLSurface eGLSurface = this.eglSurface;
            return egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$1  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 implements Runnable {
            AnonymousClass1() {
            }

            @Override // java.lang.Runnable
            public void run() {
                if (!CanvasInternal.this.initialized || RenderView.this.shuttingDown) {
                    return;
                }
                CanvasInternal.this.setCurrentContext();
                GLES20.glBindFramebuffer(36160, 0);
                GLES20.glViewport(0, 0, CanvasInternal.this.bufferWidth, CanvasInternal.this.bufferHeight);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GLES20.glClear(16384);
                RenderView.this.painting.render();
                GLES20.glBlendFunc(1, 771);
                CanvasInternal.this.egl10.eglSwapBuffers(CanvasInternal.this.eglDisplay, CanvasInternal.this.eglSurface);
                if (!RenderView.this.firstDrawSent) {
                    RenderView.this.firstDrawSent = true;
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            RenderView.CanvasInternal.AnonymousClass1.this.lambda$run$0();
                        }
                    });
                }
                if (CanvasInternal.this.ready) {
                    return;
                }
                CanvasInternal.this.ready = true;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                RenderView.this.delegate.onFirstDraw();
            }
        }

        public void setBufferSize(int i, int i2) {
            this.bufferWidth = i;
            this.bufferHeight = i2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$requestRender$0() {
            this.drawRunnable.run();
        }

        public void requestRender() {
            postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.CanvasInternal.this.lambda$requestRender$0();
                }
            });
        }

        public void scheduleRedraw() {
            Runnable runnable = this.scheduledRunnable;
            if (runnable != null) {
                cancelRunnable(runnable);
                this.scheduledRunnable = null;
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.CanvasInternal.this.lambda$scheduleRedraw$1();
                }
            };
            this.scheduledRunnable = runnable2;
            postRunnable(runnable2, 1L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$scheduleRedraw$1() {
            this.scheduledRunnable = null;
            this.drawRunnable.run();
        }

        public void finish() {
            if (this.eglSurface != null) {
                EGL10 egl10 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay2 = this.eglDisplay;
            if (eGLDisplay2 != null) {
                this.egl10.eglTerminate(eGLDisplay2);
                this.eglDisplay = null;
            }
        }

        public void shutdown() {
            postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RenderView.CanvasInternal.this.lambda$shutdown$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$shutdown$2() {
            finish();
            Looper myLooper = Looper.myLooper();
            if (myLooper != null) {
                myLooper.quit();
            }
        }

        public Bitmap getTexture() {
            if (!this.initialized) {
                return null;
            }
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Bitmap[] bitmapArr = new Bitmap[1];
            try {
                postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$CanvasInternal$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        RenderView.CanvasInternal.this.lambda$getTexture$3(bitmapArr, countDownLatch);
                    }
                });
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e(e);
            }
            return bitmapArr[0];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getTexture$3(Bitmap[] bitmapArr, CountDownLatch countDownLatch) {
            Painting.PaintingData paintingData = RenderView.this.painting.getPaintingData(new RectF(0.0f, 0.0f, RenderView.this.painting.getSize().width, RenderView.this.painting.getSize().height), false);
            if (paintingData != null) {
                bitmapArr[0] = paintingData.bitmap;
            }
            countDownLatch.countDown();
        }
    }

    public Bitmap getResultBitmap() {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null) {
            return canvasInternal.getTexture();
        }
        return null;
    }

    public void performInContext(final Runnable runnable) {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal == null) {
            return;
        }
        canvasInternal.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.RenderView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RenderView.this.lambda$performInContext$1(runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performInContext$1(Runnable runnable) {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal == null || !canvasInternal.initialized) {
            return;
        }
        this.internal.setCurrentContext();
        runnable.run();
    }
}
