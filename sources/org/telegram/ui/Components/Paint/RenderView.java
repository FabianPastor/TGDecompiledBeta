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
import org.telegram.ui.Components.Size;

public class RenderView extends TextureView {
    /* access modifiers changed from: private */
    public Bitmap bitmap;
    private Brush brush;
    private int color;
    /* access modifiers changed from: private */
    public RenderViewDelegate delegate;
    /* access modifiers changed from: private */
    public boolean firstDrawSent;
    private Input input = new Input(this);
    /* access modifiers changed from: private */
    public CanvasInternal internal;
    /* access modifiers changed from: private */
    public Painting painting;
    /* access modifiers changed from: private */
    public DispatchQueue queue;
    /* access modifiers changed from: private */
    public boolean shuttingDown;
    /* access modifiers changed from: private */
    public boolean transformedBitmap;
    /* access modifiers changed from: private */
    public UndoStore undoStore;
    private float weight;

    public interface RenderViewDelegate {
        void onBeganDrawing();

        void onFinishedDrawing(boolean z);

        void onFirstDraw();

        boolean shouldDraw();
    }

    public RenderView(Context context, Painting painting2, Bitmap bitmap2) {
        super(context);
        setOpaque(false);
        this.bitmap = bitmap2;
        this.painting = painting2;
        painting2.setRenderView(this);
        setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (surfaceTexture != null && RenderView.this.internal == null) {
                    CanvasInternal unused = RenderView.this.internal = new CanvasInternal(surfaceTexture);
                    RenderView.this.internal.setBufferSize(i, i2);
                    RenderView.this.updateTransform();
                    RenderView.this.internal.requestRender();
                    if (RenderView.this.painting.isPaused()) {
                        RenderView.this.painting.onResume();
                    }
                }
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.setBufferSize(i, i2);
                    RenderView.this.updateTransform();
                    RenderView.this.internal.requestRender();
                    RenderView.this.internal.postRunnable(new RenderView$1$$ExternalSyntheticLambda1(this));
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSurfaceTextureSizeChanged$0() {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.requestRender();
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (RenderView.this.internal != null && !RenderView.this.shuttingDown) {
                    RenderView.this.painting.onPause(new RenderView$1$$ExternalSyntheticLambda0(this));
                }
                return true;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onSurfaceTextureDestroyed$1() {
                RenderView.this.internal.shutdown();
                CanvasInternal unused = RenderView.this.internal = null;
            }
        });
        this.painting.setDelegate(new Painting.PaintingDelegate() {
            public void contentChanged() {
                if (RenderView.this.internal != null) {
                    RenderView.this.internal.scheduleRedraw();
                }
            }

            public UndoStore requestUndoStore() {
                return RenderView.this.undoStore;
            }

            public DispatchQueue requestDispatchQueue() {
                return RenderView.this.queue;
            }
        });
    }

    public void redraw() {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null) {
            canvasInternal.requestRender();
        }
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

    public void setUndoStore(UndoStore undoStore2) {
        this.undoStore = undoStore2;
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

    public void setBrush(Brush brush2) {
        Painting painting2 = this.painting;
        this.brush = brush2;
        painting2.setBrush(brush2);
    }

    /* access modifiers changed from: private */
    public void updateTransform() {
        Matrix matrix = new Matrix();
        float f = 1.0f;
        float width = this.painting != null ? ((float) getWidth()) / this.painting.getSize().width : 1.0f;
        if (width > 0.0f) {
            f = width;
        }
        Size size = getPainting().getSize();
        matrix.preTranslate(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
        matrix.preScale(f, -f);
        matrix.preTranslate((-size.width) / 2.0f, (-size.height) / 2.0f);
        this.input.setMatrix(matrix);
        this.painting.setRenderProjection(GLMatrix.MultiplyMat4f(GLMatrix.LoadOrtho(0.0f, (float) this.internal.bufferWidth, 0.0f, (float) this.internal.bufferHeight, -1.0f, 1.0f), GLMatrix.LoadGraphicsMatrix(matrix)));
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
            performInContext(new RenderView$$ExternalSyntheticLambda0(this));
        }
        setVisibility(8);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$shutdown$0() {
        this.painting.cleanResources(this.transformedBitmap);
        this.internal.shutdown();
        this.internal = null;
    }

    private class CanvasInternal extends DispatchQueue {
        /* access modifiers changed from: private */
        public int bufferHeight;
        /* access modifiers changed from: private */
        public int bufferWidth;
        private Runnable drawRunnable = new Runnable() {
            public void run() {
                if (CanvasInternal.this.initialized && !RenderView.this.shuttingDown) {
                    boolean unused = CanvasInternal.this.setCurrentContext();
                    GLES20.glBindFramebuffer(36160, 0);
                    GLES20.glViewport(0, 0, CanvasInternal.this.bufferWidth, CanvasInternal.this.bufferHeight);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                    GLES20.glClear(16384);
                    RenderView.this.painting.render();
                    GLES20.glBlendFunc(1, 771);
                    CanvasInternal.this.egl10.eglSwapBuffers(CanvasInternal.this.eglDisplay, CanvasInternal.this.eglSurface);
                    if (!RenderView.this.firstDrawSent) {
                        boolean unused2 = RenderView.this.firstDrawSent = true;
                        AndroidUtilities.runOnUIThread(new RenderView$CanvasInternal$1$$ExternalSyntheticLambda0(this));
                    }
                    if (!CanvasInternal.this.ready) {
                        boolean unused3 = CanvasInternal.this.ready = true;
                    }
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                RenderView.this.delegate.onFirstDraw();
            }
        };
        /* access modifiers changed from: private */
        public EGL10 egl10;
        private EGLContext eglContext;
        /* access modifiers changed from: private */
        public EGLDisplay eglDisplay;
        /* access modifiers changed from: private */
        public EGLSurface eglSurface;
        /* access modifiers changed from: private */
        public boolean initialized;
        /* access modifiers changed from: private */
        public volatile boolean ready;
        private Runnable scheduledRunnable;
        private SurfaceTexture surfaceTexture;

        public CanvasInternal(SurfaceTexture surfaceTexture2) {
            super("CanvasInternal");
            this.surfaceTexture = surfaceTexture2;
        }

        public void run() {
            if (RenderView.this.bitmap != null && !RenderView.this.bitmap.isRecycled()) {
                this.initialized = initGL();
                super.run();
            }
        }

        private boolean initGL() {
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
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
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, eGLConfig, surfaceTexture2, (int[]) null);
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
                } else {
                    finish();
                    return false;
                }
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
            if (((float) RenderView.this.bitmap.getWidth()) != size.width || ((float) RenderView.this.bitmap.getHeight()) != size.height) {
                Bitmap createBitmap = Bitmap.createBitmap((int) size.width, (int) size.height, Bitmap.Config.ARGB_8888);
                new Canvas(createBitmap).drawBitmap(RenderView.this.bitmap, (Rect) null, new RectF(0.0f, 0.0f, size.width, size.height), (Paint) null);
                Bitmap unused = RenderView.this.bitmap = createBitmap;
                boolean unused2 = RenderView.this.transformedBitmap = true;
            }
        }

        /* access modifiers changed from: private */
        public boolean setCurrentContext() {
            if (!this.initialized) {
                return false;
            }
            if (this.eglContext.equals(this.egl10.eglGetCurrentContext()) && this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                return true;
            }
            EGL10 egl102 = this.egl10;
            EGLDisplay eGLDisplay = this.eglDisplay;
            EGLSurface eGLSurface = this.eglSurface;
            if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                return false;
            }
            return true;
        }

        public void setBufferSize(int i, int i2) {
            this.bufferWidth = i;
            this.bufferHeight = i2;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$requestRender$0() {
            this.drawRunnable.run();
        }

        public void requestRender() {
            postRunnable(new RenderView$CanvasInternal$$ExternalSyntheticLambda0(this));
        }

        public void scheduleRedraw() {
            Runnable runnable = this.scheduledRunnable;
            if (runnable != null) {
                cancelRunnable(runnable);
                this.scheduledRunnable = null;
            }
            RenderView$CanvasInternal$$ExternalSyntheticLambda2 renderView$CanvasInternal$$ExternalSyntheticLambda2 = new RenderView$CanvasInternal$$ExternalSyntheticLambda2(this);
            this.scheduledRunnable = renderView$CanvasInternal$$ExternalSyntheticLambda2;
            postRunnable(renderView$CanvasInternal$$ExternalSyntheticLambda2, 1);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$scheduleRedraw$1() {
            this.scheduledRunnable = null;
            this.drawRunnable.run();
        }

        public void finish() {
            if (this.eglSurface != null) {
                EGL10 egl102 = this.egl10;
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
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
            postRunnable(new RenderView$CanvasInternal$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
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
            CountDownLatch countDownLatch = new CountDownLatch(1);
            Bitmap[] bitmapArr = new Bitmap[1];
            try {
                postRunnable(new RenderView$CanvasInternal$$ExternalSyntheticLambda3(this, bitmapArr, countDownLatch));
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            return bitmapArr[0];
        }

        /* access modifiers changed from: private */
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

    public void performInContext(Runnable runnable) {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null) {
            canvasInternal.postRunnable(new RenderView$$ExternalSyntheticLambda1(this, runnable));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performInContext$1(Runnable runnable) {
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null && canvasInternal.initialized) {
            boolean unused = this.internal.setCurrentContext();
            runnable.run();
        }
    }
}
