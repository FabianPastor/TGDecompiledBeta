package org.telegram.ui.Components.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
    private RenderViewDelegate delegate;
    private Input input = new Input(this);
    /* access modifiers changed from: private */
    public CanvasInternal internal;
    /* access modifiers changed from: private */
    public int orientation;
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

        boolean shouldDraw();
    }

    public RenderView(Context context, Painting painting2, Bitmap bitmap2, int i) {
        super(context);
        this.bitmap = bitmap2;
        this.orientation = i;
        this.painting = painting2;
        this.painting.setRenderView(this);
        setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                if (surfaceTexture != null && RenderView.this.internal == null) {
                    RenderView renderView = RenderView.this;
                    CanvasInternal unused = renderView.internal = new CanvasInternal(surfaceTexture);
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
                    RenderView.this.internal.postRunnable(new Runnable() {
                        public void run() {
                            if (RenderView.this.internal != null) {
                                RenderView.this.internal.requestRender();
                            }
                        }
                    });
                }
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (RenderView.this.internal != null && !RenderView.this.shuttingDown) {
                    RenderView.this.painting.onPause(new Runnable() {
                        public void run() {
                            RenderView.this.internal.shutdown();
                            CanvasInternal unused = RenderView.this.internal = null;
                        }
                    });
                }
                return true;
            }
        });
        this.painting.setDelegate(new Painting.PaintingDelegate() {
            public void strokeCommited() {
            }

            public void contentChanged(RectF rectF) {
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

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() > 1) {
            return false;
        }
        CanvasInternal canvasInternal = this.internal;
        if (canvasInternal != null && canvasInternal.initialized && this.internal.ready) {
            this.input.process(motionEvent);
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
        float width = this.painting != null ? ((float) getWidth()) / this.painting.getSize().width : 1.0f;
        if (width <= 0.0f) {
            width = 1.0f;
        }
        Size size = getPainting().getSize();
        matrix.preTranslate(((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
        matrix.preScale(width, -width);
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
            performInContext(new Runnable() {
                public void run() {
                    RenderView.this.painting.cleanResources(RenderView.this.transformedBitmap);
                    RenderView.this.internal.shutdown();
                    CanvasInternal unused = RenderView.this.internal = null;
                }
            });
        }
        setVisibility(8);
    }

    private class CanvasInternal extends DispatchQueue {
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        /* access modifiers changed from: private */
        public int bufferHeight;
        /* access modifiers changed from: private */
        public int bufferWidth;
        /* access modifiers changed from: private */
        public Runnable drawRunnable = new Runnable() {
            public void run() {
                if (CanvasInternal.this.initialized && !RenderView.this.shuttingDown) {
                    boolean unused = CanvasInternal.this.setCurrentContext();
                    GLES20.glBindFramebuffer(36160, 0);
                    GLES20.glViewport(0, 0, CanvasInternal.this.bufferWidth, CanvasInternal.this.bufferHeight);
                    GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                    GLES20.glClear(16384);
                    RenderView.this.painting.render();
                    GLES20.glBlendFunc(1, 771);
                    CanvasInternal.this.egl10.eglSwapBuffers(CanvasInternal.this.eglDisplay, CanvasInternal.this.eglSurface);
                    if (!CanvasInternal.this.ready) {
                        RenderView.this.queue.postRunnable(new Runnable() {
                            public void run() {
                                boolean unused = CanvasInternal.this.ready = true;
                            }
                        }, 200);
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        /* access modifiers changed from: private */
        public EGLDisplay eglDisplay;
        /* access modifiers changed from: private */
        public EGLSurface eglSurface;
        /* access modifiers changed from: private */
        public boolean initialized;
        private long lastRenderCallTime;
        /* access modifiers changed from: private */
        public boolean ready;
        /* access modifiers changed from: private */
        public Runnable scheduledRunnable;
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
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(eGLDisplay, new int[2])) {
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
                this.eglConfig = eGLConfigArr[0];
                this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                if (this.eglContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 instanceof SurfaceTexture) {
                    this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture2, (int[]) null);
                    EGLSurface eGLSurface = this.eglSurface;
                    if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (!this.egl10.eglMakeCurrent(this.eglDisplay, eGLSurface, eGLSurface, this.eglContext)) {
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

        private Bitmap createBitmap(Bitmap bitmap, float f) {
            Matrix matrix = new Matrix();
            matrix.setScale(f, f);
            matrix.postRotate((float) RenderView.this.orientation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        private void checkBitmap() {
            Size size = RenderView.this.painting.getSize();
            if (((float) RenderView.this.bitmap.getWidth()) != size.width || ((float) RenderView.this.bitmap.getHeight()) != size.height || RenderView.this.orientation != 0) {
                float width = (float) RenderView.this.bitmap.getWidth();
                if (RenderView.this.orientation % 360 == 90 || RenderView.this.orientation % 360 == 270) {
                    width = (float) RenderView.this.bitmap.getHeight();
                }
                float f = size.width / width;
                RenderView renderView = RenderView.this;
                Bitmap unused = renderView.bitmap = createBitmap(renderView.bitmap, f);
                int unused2 = RenderView.this.orientation = 0;
                boolean unused3 = RenderView.this.transformedBitmap = true;
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

        public void requestRender() {
            postRunnable(new Runnable() {
                public void run() {
                    CanvasInternal.this.drawRunnable.run();
                }
            });
        }

        public void scheduleRedraw() {
            Runnable runnable = this.scheduledRunnable;
            if (runnable != null) {
                cancelRunnable(runnable);
                this.scheduledRunnable = null;
            }
            this.scheduledRunnable = new Runnable() {
                public void run() {
                    Runnable unused = CanvasInternal.this.scheduledRunnable = null;
                    CanvasInternal.this.drawRunnable.run();
                }
            };
            postRunnable(this.scheduledRunnable, 1);
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
            postRunnable(new Runnable() {
                public void run() {
                    CanvasInternal.this.finish();
                    Looper myLooper = Looper.myLooper();
                    if (myLooper != null) {
                        myLooper.quit();
                    }
                }
            });
        }

        public Bitmap getTexture() {
            if (!this.initialized) {
                return null;
            }
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Bitmap[] bitmapArr = new Bitmap[1];
            try {
                postRunnable(new Runnable() {
                    public void run() {
                        bitmapArr[0] = RenderView.this.painting.getPaintingData(new RectF(0.0f, 0.0f, RenderView.this.painting.getSize().width, RenderView.this.painting.getSize().height), false).bitmap;
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.await();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            return bitmapArr[0];
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
        if (canvasInternal != null) {
            canvasInternal.postRunnable(new Runnable() {
                public void run() {
                    if (RenderView.this.internal != null && RenderView.this.internal.initialized) {
                        boolean unused = RenderView.this.internal.setCurrentContext();
                        runnable.run();
                    }
                }
            });
        }
    }
}
