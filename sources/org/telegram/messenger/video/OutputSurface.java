package org.telegram.messenger.video;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.GLES20;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class OutputSurface implements OnFrameAvailableListener {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    private EGL10 mEGL;
    private EGLContext mEGLContext = null;
    private EGLDisplay mEGLDisplay = null;
    private EGLSurface mEGLSurface = null;
    private boolean mFrameAvailable;
    private final Object mFrameSyncObject = new Object();
    private int mHeight;
    private ByteBuffer mPixelBuf;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private TextureRenderer mTextureRender;
    private int mWidth;
    private int rotateRender = 0;

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0036 in {8, 10, 14, 18, 22} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public void awaitNewImage() {
        /*
        r4 = this;
        r0 = r4.mFrameSyncObject;
        monitor-enter(r0);
        r1 = r4.mFrameAvailable;	 Catch:{ all -> 0x0033 }
        if (r1 != 0) goto L_0x0022;
        r1 = r4.mFrameSyncObject;	 Catch:{ InterruptedException -> 0x001b }
        r2 = 2500; // 0x9c4 float:3.503E-42 double:1.235E-320;	 Catch:{ InterruptedException -> 0x001b }
        r1.wait(r2);	 Catch:{ InterruptedException -> 0x001b }
        r1 = r4.mFrameAvailable;	 Catch:{ InterruptedException -> 0x001b }
        if (r1 == 0) goto L_0x0013;	 Catch:{ InterruptedException -> 0x001b }
        goto L_0x0003;	 Catch:{ InterruptedException -> 0x001b }
        r1 = new java.lang.RuntimeException;	 Catch:{ InterruptedException -> 0x001b }
        r2 = "Surface frame wait timed out";	 Catch:{ InterruptedException -> 0x001b }
        r1.<init>(r2);	 Catch:{ InterruptedException -> 0x001b }
        throw r1;	 Catch:{ InterruptedException -> 0x001b }
        r1 = move-exception;
        r2 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0033 }
        r2.<init>(r1);	 Catch:{ all -> 0x0033 }
        throw r2;	 Catch:{ all -> 0x0033 }
        r1 = 0;	 Catch:{ all -> 0x0033 }
        r4.mFrameAvailable = r1;	 Catch:{ all -> 0x0033 }
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
        r0 = r4.mTextureRender;
        r1 = "before updateTexImage";
        r0.checkGlError(r1);
        r0 = r4.mSurfaceTexture;
        r0.updateTexImage();
        return;
        r1 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
        throw r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.OutputSurface.awaitNewImage():void");
    }

    public OutputSurface(int i, int i2, int i3) {
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException();
        }
        this.mWidth = i;
        this.mHeight = i2;
        this.rotateRender = i3;
        this.mPixelBuf = ByteBuffer.allocateDirect((this.mWidth * this.mHeight) * 4);
        this.mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
        eglSetup(i, i2);
        makeCurrent();
        setup();
    }

    public OutputSurface() {
        setup();
    }

    private void setup() {
        this.mTextureRender = new TextureRenderer(this.rotateRender);
        this.mTextureRender.surfaceCreated();
        this.mSurfaceTexture = new SurfaceTexture(this.mTextureRender.getTextureId());
        this.mSurfaceTexture.setOnFrameAvailableListener(this);
        this.mSurface = new Surface(this.mSurfaceTexture);
    }

    private void eglSetup(int i, int i2) {
        this.mEGL = (EGL10) EGLContext.getEGL();
        this.mEGLDisplay = this.mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        EGLDisplay eGLDisplay = this.mEGLDisplay;
        if (eGLDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL10 display");
        } else if (this.mEGL.eglInitialize(eGLDisplay, null)) {
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (this.mEGL.eglChooseConfig(this.mEGLDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12339, 1, 12352, 4, 12344}, eGLConfigArr, eGLConfigArr.length, new int[1])) {
                this.mEGLContext = this.mEGL.eglCreateContext(this.mEGLDisplay, eGLConfigArr[0], EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                checkEglError("eglCreateContext");
                if (this.mEGLContext != null) {
                    this.mEGLSurface = this.mEGL.eglCreatePbufferSurface(this.mEGLDisplay, eGLConfigArr[0], new int[]{12375, i, 12374, i2, 12344});
                    checkEglError("eglCreatePbufferSurface");
                    if (this.mEGLSurface == null) {
                        throw new RuntimeException("surface was null");
                    }
                    return;
                }
                throw new RuntimeException("null context");
            }
            throw new RuntimeException("unable to find RGB888+pbuffer EGL config");
        } else {
            this.mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL10");
        }
    }

    public void release() {
        EGL10 egl10 = this.mEGL;
        if (egl10 != null) {
            if (egl10.eglGetCurrentContext().equals(this.mEGLContext)) {
                egl10 = this.mEGL;
                EGLDisplay eGLDisplay = this.mEGLDisplay;
                EGLSurface eGLSurface = EGL10.EGL_NO_SURFACE;
                egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL10.EGL_NO_CONTEXT);
            }
            this.mEGL.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
            this.mEGL.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
        }
        this.mSurface.release();
        this.mEGLDisplay = null;
        this.mEGLContext = null;
        this.mEGLSurface = null;
        this.mEGL = null;
        this.mTextureRender = null;
        this.mSurface = null;
        this.mSurfaceTexture = null;
    }

    public void makeCurrent() {
        if (this.mEGL != null) {
            checkEglError("before makeCurrent");
            EGL10 egl10 = this.mEGL;
            EGLDisplay eGLDisplay = this.mEGLDisplay;
            EGLSurface eGLSurface = this.mEGLSurface;
            if (!egl10.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.mEGLContext)) {
                throw new RuntimeException("eglMakeCurrent failed");
            }
            return;
        }
        throw new RuntimeException("not configured for makeCurrent");
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public void drawImage(boolean z) {
        this.mTextureRender.drawFrame(this.mSurfaceTexture, z);
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this.mFrameSyncObject) {
            if (this.mFrameAvailable) {
                throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
            }
            this.mFrameAvailable = true;
            this.mFrameSyncObject.notifyAll();
        }
    }

    public ByteBuffer getFrame() {
        this.mPixelBuf.rewind();
        GLES20.glReadPixels(0, 0, this.mWidth, this.mHeight, 6408, 5121, this.mPixelBuf);
        return this.mPixelBuf;
    }

    private void checkEglError(String str) {
        if (this.mEGL.eglGetError() != 12288) {
            throw new RuntimeException("EGL error encountered (see log)");
        }
    }
}
