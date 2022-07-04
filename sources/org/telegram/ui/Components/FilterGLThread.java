package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Looper;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
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
import org.telegram.ui.Components.FilterShaders;

public class FilterGLThread extends DispatchQueue {
    /* access modifiers changed from: private */
    public boolean blurred;
    private Bitmap currentBitmap;
    private Runnable drawRunnable = new Runnable() {
        public void run() {
            if (FilterGLThread.this.initied) {
                if ((FilterGLThread.this.eglContext.equals(FilterGLThread.this.egl10.eglGetCurrentContext()) && FilterGLThread.this.eglSurface.equals(FilterGLThread.this.egl10.eglGetCurrentSurface(12377))) || FilterGLThread.this.egl10.eglMakeCurrent(FilterGLThread.this.eglDisplay, FilterGLThread.this.eglSurface, FilterGLThread.this.eglSurface, FilterGLThread.this.eglContext)) {
                    if (FilterGLThread.this.updateSurface) {
                        FilterGLThread.this.videoSurfaceTexture.updateTexImage();
                        FilterGLThread.this.videoSurfaceTexture.getTransformMatrix(FilterGLThread.this.videoTextureMatrix);
                        FilterGLThread.this.setRenderData();
                        boolean unused = FilterGLThread.this.updateSurface = false;
                        FilterGLThread.this.filterShaders.onVideoFrameUpdate(FilterGLThread.this.videoTextureMatrix);
                        boolean unused2 = FilterGLThread.this.videoFrameAvailable = true;
                    }
                    if (FilterGLThread.this.renderDataSet) {
                        if (FilterGLThread.this.videoDelegate == null || FilterGLThread.this.videoFrameAvailable) {
                            GLES20.glViewport(0, 0, FilterGLThread.this.renderBufferWidth, FilterGLThread.this.renderBufferHeight);
                            FilterGLThread.this.filterShaders.drawSkinSmoothPass();
                            FilterGLThread.this.filterShaders.drawEnhancePass();
                            if (FilterGLThread.this.videoDelegate == null) {
                                FilterGLThread.this.filterShaders.drawSharpenPass();
                            }
                            FilterGLThread.this.filterShaders.drawCustomParamsPass();
                            FilterGLThread filterGLThread = FilterGLThread.this;
                            boolean unused3 = filterGLThread.blurred = filterGLThread.filterShaders.drawBlurPass();
                        }
                        GLES20.glViewport(0, 0, FilterGLThread.this.surfaceWidth, FilterGLThread.this.surfaceHeight);
                        GLES20.glBindFramebuffer(36160, 0);
                        GLES20.glUseProgram(FilterGLThread.this.simpleShaderProgram);
                        GLES20.glActiveTexture(33984);
                        GLES20.glBindTexture(3553, FilterGLThread.this.filterShaders.getRenderTexture(true ^ FilterGLThread.this.blurred ? 1 : 0));
                        GLES20.glUniform1i(FilterGLThread.this.simpleSourceImageHandle, 0);
                        GLES20.glEnableVertexAttribArray(FilterGLThread.this.simpleInputTexCoordHandle);
                        GLES20.glVertexAttribPointer(FilterGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, FilterGLThread.this.textureBuffer != null ? FilterGLThread.this.textureBuffer : FilterGLThread.this.filterShaders.getTextureBuffer());
                        GLES20.glEnableVertexAttribArray(FilterGLThread.this.simplePositionHandle);
                        GLES20.glVertexAttribPointer(FilterGLThread.this.simplePositionHandle, 2, 5126, false, 8, FilterGLThread.this.filterShaders.getVertexBuffer());
                        GLES20.glDrawArrays(5, 0, 4);
                        FilterGLThread.this.egl10.eglSwapBuffers(FilterGLThread.this.eglDisplay, FilterGLThread.this.eglSurface);
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(FilterGLThread.this.egl10.eglGetError()));
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public EGL10 egl10;
    /* access modifiers changed from: private */
    public EGLContext eglContext;
    /* access modifiers changed from: private */
    public EGLDisplay eglDisplay;
    /* access modifiers changed from: private */
    public EGLSurface eglSurface;
    /* access modifiers changed from: private */
    public FilterShaders filterShaders;
    /* access modifiers changed from: private */
    public boolean initied;
    private long lastRenderCallTime;
    private int orientation;
    /* access modifiers changed from: private */
    public int renderBufferHeight;
    /* access modifiers changed from: private */
    public int renderBufferWidth;
    /* access modifiers changed from: private */
    public boolean renderDataSet;
    /* access modifiers changed from: private */
    public int simpleInputTexCoordHandle;
    /* access modifiers changed from: private */
    public int simplePositionHandle;
    /* access modifiers changed from: private */
    public int simpleShaderProgram;
    /* access modifiers changed from: private */
    public int simpleSourceImageHandle;
    /* access modifiers changed from: private */
    public volatile int surfaceHeight;
    private SurfaceTexture surfaceTexture;
    /* access modifiers changed from: private */
    public volatile int surfaceWidth;
    /* access modifiers changed from: private */
    public FloatBuffer textureBuffer;
    /* access modifiers changed from: private */
    public boolean updateSurface;
    /* access modifiers changed from: private */
    public FilterGLThreadVideoDelegate videoDelegate;
    /* access modifiers changed from: private */
    public boolean videoFrameAvailable;
    private int videoHeight;
    /* access modifiers changed from: private */
    public SurfaceTexture videoSurfaceTexture;
    private int[] videoTexture = new int[1];
    /* access modifiers changed from: private */
    public float[] videoTextureMatrix = new float[16];
    private int videoWidth;

    public interface FilterGLThreadVideoDelegate {
        void onVideoSurfaceCreated(SurfaceTexture surfaceTexture);
    }

    public FilterGLThread(SurfaceTexture surfaceTexture2, Bitmap bitmap, int i, boolean z) {
        super("PhotoFilterGLThread", false);
        this.surfaceTexture = surfaceTexture2;
        this.currentBitmap = bitmap;
        this.orientation = i;
        this.filterShaders = new FilterShaders(false);
        float[] fArr = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        if (z) {
            float f = fArr[2];
            fArr[2] = fArr[0];
            fArr[0] = f;
            float f2 = fArr[6];
            fArr[6] = fArr[4];
            fArr[4] = f2;
        }
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        this.textureBuffer = asFloatBuffer;
        asFloatBuffer.put(fArr);
        this.textureBuffer.position(0);
        start();
    }

    public FilterGLThread(SurfaceTexture surfaceTexture2, FilterGLThreadVideoDelegate filterGLThreadVideoDelegate) {
        super("VideoFilterGLThread", false);
        this.surfaceTexture = surfaceTexture2;
        this.videoDelegate = filterGLThreadVideoDelegate;
        this.filterShaders = new FilterShaders(true);
        start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setFilterGLThreadDelegate$0(FilterShaders.FilterShadersDelegate filterShadersDelegate) {
        this.filterShaders.setDelegate(filterShadersDelegate);
    }

    public void setFilterGLThreadDelegate(FilterShaders.FilterShadersDelegate filterShadersDelegate) {
        postRunnable(new FilterGLThread$$ExternalSyntheticLambda5(this, filterShadersDelegate));
    }

    private boolean initGL() {
        int i;
        int i2;
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
                    int loadShader = FilterShaders.loadShader(35633, "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}");
                    int loadShader2 = FilterShaders.loadShader(35632, "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}");
                    if (loadShader == 0 || loadShader2 == 0) {
                        return false;
                    }
                    int glCreateProgram = GLES20.glCreateProgram();
                    this.simpleShaderProgram = glCreateProgram;
                    GLES20.glAttachShader(glCreateProgram, loadShader);
                    GLES20.glAttachShader(this.simpleShaderProgram, loadShader2);
                    GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                    GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                    GLES20.glLinkProgram(this.simpleShaderProgram);
                    int[] iArr2 = new int[1];
                    GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, iArr2, 0);
                    if (iArr2[0] == 0) {
                        GLES20.glDeleteProgram(this.simpleShaderProgram);
                        this.simpleShaderProgram = 0;
                    } else {
                        this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                        this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                        this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                    }
                    Bitmap bitmap = this.currentBitmap;
                    if (bitmap != null) {
                        i2 = bitmap.getWidth();
                        i = this.currentBitmap.getHeight();
                    } else {
                        i2 = this.videoWidth;
                        i = this.videoHeight;
                    }
                    int i3 = i2;
                    int i4 = i;
                    if (this.videoDelegate != null) {
                        GLES20.glGenTextures(1, this.videoTexture, 0);
                        Matrix.setIdentityM(this.videoTextureMatrix, 0);
                        SurfaceTexture surfaceTexture3 = new SurfaceTexture(this.videoTexture[0]);
                        this.videoSurfaceTexture = surfaceTexture3;
                        surfaceTexture3.setOnFrameAvailableListener(new FilterGLThread$$ExternalSyntheticLambda0(this));
                        GLES20.glBindTexture(36197, this.videoTexture[0]);
                        GLES20.glTexParameterf(36197, 10240, 9729.0f);
                        GLES20.glTexParameterf(36197, 10241, 9728.0f);
                        GLES20.glTexParameteri(36197, 10242, 33071);
                        GLES20.glTexParameteri(36197, 10243, 33071);
                        AndroidUtilities.runOnUIThread(new FilterGLThread$$ExternalSyntheticLambda1(this));
                    }
                    if (!this.filterShaders.create()) {
                        finish();
                        return false;
                    }
                    if (!(i3 == 0 || i4 == 0)) {
                        this.filterShaders.setRenderData(this.currentBitmap, this.orientation, this.videoTexture[0], i3, i4);
                        this.renderDataSet = true;
                        this.renderBufferWidth = this.filterShaders.getRenderBufferWidth();
                        this.renderBufferHeight = this.filterShaders.getRenderBufferHeight();
                    }
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initGL$1(SurfaceTexture surfaceTexture2) {
        requestRender(false, true, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$initGL$2() {
        this.videoDelegate.onVideoSurfaceCreated(this.videoSurfaceTexture);
    }

    public void setVideoSize(int i, int i2) {
        postRunnable(new FilterGLThread$$ExternalSyntheticLambda3(this, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setVideoSize$3(int i, int i2) {
        if (this.videoWidth != i || this.videoHeight != i2) {
            this.videoWidth = i;
            this.videoHeight = i2;
            if (i > 1280 || i2 > 1280) {
                this.videoWidth = i / 2;
                this.videoHeight = i2 / 2;
            }
            this.renderDataSet = false;
            setRenderData();
            this.drawRunnable.run();
        }
    }

    public void finish() {
        this.currentBitmap = null;
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
        SurfaceTexture surfaceTexture2 = this.surfaceTexture;
        if (surfaceTexture2 != null) {
            surfaceTexture2.release();
        }
    }

    /* access modifiers changed from: private */
    public void setRenderData() {
        int i;
        int i2;
        if (!this.renderDataSet && (i = this.videoWidth) > 0 && (i2 = this.videoHeight) > 0) {
            this.filterShaders.setRenderData(this.currentBitmap, this.orientation, this.videoTexture[0], i, i2);
            this.renderDataSet = true;
            this.renderBufferWidth = this.filterShaders.getRenderBufferWidth();
            this.renderBufferHeight = this.filterShaders.getRenderBufferHeight();
        }
    }

    private Bitmap getRenderBufferBitmap() {
        int i;
        int i2 = this.renderBufferWidth;
        if (i2 == 0 || (i = this.renderBufferHeight) == 0) {
            return null;
        }
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(i2 * i * 4);
        GLES20.glReadPixels(0, 0, this.renderBufferWidth, this.renderBufferHeight, 6408, 5121, allocateDirect);
        Bitmap createBitmap = Bitmap.createBitmap(this.renderBufferWidth, this.renderBufferHeight, Bitmap.Config.ARGB_8888);
        createBitmap.copyPixelsFromBuffer(allocateDirect);
        return createBitmap;
    }

    public Bitmap getTexture() {
        if (!this.initied || !isAlive()) {
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Bitmap[] bitmapArr = new Bitmap[1];
        try {
            if (postRunnable(new FilterGLThread$$ExternalSyntheticLambda7(this, bitmapArr, countDownLatch))) {
                countDownLatch.await();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return bitmapArr[0];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getTexture$4(Bitmap[] bitmapArr, CountDownLatch countDownLatch) {
        GLES20.glBindFramebuffer(36160, this.filterShaders.getRenderFrameBuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.filterShaders.getRenderTexture(this.blurred ^ true ? 1 : 0), 0);
        GLES20.glClear(0);
        bitmapArr[0] = getRenderBufferBitmap();
        countDownLatch.countDown();
        GLES20.glBindFramebuffer(36160, 0);
        GLES20.glClear(0);
    }

    public void shutdown() {
        postRunnable(new FilterGLThread$$ExternalSyntheticLambda2(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$shutdown$5() {
        finish();
        Looper myLooper = Looper.myLooper();
        if (myLooper != null) {
            myLooper.quit();
        }
    }

    public void setSurfaceTextureSize(int i, int i2) {
        postRunnable(new FilterGLThread$$ExternalSyntheticLambda4(this, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setSurfaceTextureSize$6(int i, int i2) {
        this.surfaceWidth = i;
        this.surfaceHeight = i2;
    }

    public void run() {
        this.initied = initGL();
        super.run();
    }

    public void requestRender(boolean z) {
        requestRender(z, false, false);
    }

    public void requestRender(boolean z, boolean z2, boolean z3) {
        postRunnable(new FilterGLThread$$ExternalSyntheticLambda6(this, z, z3, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestRender$7(boolean z, boolean z2, boolean z3) {
        if (z) {
            this.filterShaders.requestUpdateBlurTexture();
        }
        if (z2) {
            this.updateSurface = true;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (z3 || Math.abs(this.lastRenderCallTime - currentTimeMillis) > 30) {
            this.lastRenderCallTime = currentTimeMillis;
            this.drawRunnable.run();
        }
    }
}
