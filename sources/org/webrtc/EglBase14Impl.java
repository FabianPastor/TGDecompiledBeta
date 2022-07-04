package org.webrtc;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.os.Build;
import android.view.Surface;
import org.webrtc.EglBase;
import org.webrtc.EglBase14;

class EglBase14Impl implements EglBase14 {
    /* access modifiers changed from: private */
    public static final int CURRENT_SDK_VERSION = Build.VERSION.SDK_INT;
    private static final int EGLExt_SDK_VERSION = 18;
    private static final String TAG = "EglBase14Impl";
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    private EGLSurface eglSurfaceBackground = EGL14.EGL_NO_SURFACE;

    public static boolean isEGL14Supported() {
        StringBuilder sb = new StringBuilder();
        sb.append("SDK version: ");
        int i = CURRENT_SDK_VERSION;
        sb.append(i);
        sb.append(". isEGL14Supported: ");
        sb.append(i >= 18);
        Logging.d("EglBase14Impl", sb.toString());
        if (i >= 18) {
            return true;
        }
        return false;
    }

    public static class Context implements EglBase14.Context {
        private final EGLContext egl14Context;

        public EGLContext getRawContext() {
            return this.egl14Context;
        }

        public long getNativeEglContext() {
            if (EglBase14Impl.CURRENT_SDK_VERSION >= 21) {
                return this.egl14Context.getNativeHandle();
            }
            return (long) this.egl14Context.getHandle();
        }

        public Context(EGLContext eglContext) {
            this.egl14Context = eglContext;
        }
    }

    public EglBase14Impl(EGLContext sharedContext, int[] configAttributes) {
        EGLDisplay eglDisplay2 = getEglDisplay();
        this.eglDisplay = eglDisplay2;
        this.eglConfig = getEglConfig(eglDisplay2, configAttributes);
        int openGlesVersion = EglBase.CC.getOpenGlesVersionFromConfig(configAttributes);
        Logging.d("EglBase14Impl", "Using OpenGL ES version " + openGlesVersion);
        this.eglContext = createEglContext(sharedContext, this.eglDisplay, this.eglConfig, openGlesVersion);
    }

    public void createSurface(Surface surface) {
        createSurfaceInternal(surface, false);
    }

    public void createBackgroundSurface(SurfaceTexture surface) {
        createSurfaceInternal(surface, true);
    }

    public void createSurface(SurfaceTexture surfaceTexture) {
        createSurfaceInternal(surfaceTexture, false);
    }

    private void createSurfaceInternal(Object surface, boolean background) {
        if ((surface instanceof Surface) || (surface instanceof SurfaceTexture)) {
            checkIsNotReleased();
            if (background) {
                if (this.eglSurfaceBackground == EGL14.EGL_NO_SURFACE) {
                    EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surface, new int[]{12344}, 0);
                    this.eglSurfaceBackground = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == EGL14.EGL_NO_SURFACE) {
                        throw new RuntimeException("Failed to create window surface: 0x" + Integer.toHexString(EGL14.eglGetError()));
                    }
                    return;
                }
                throw new RuntimeException("Already has an EGLSurface");
            } else if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
                EGLSurface eglCreateWindowSurface2 = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surface, new int[]{12344}, 0);
                this.eglSurface = eglCreateWindowSurface2;
                if (eglCreateWindowSurface2 == EGL14.EGL_NO_SURFACE) {
                    throw new RuntimeException("Failed to create window surface: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
            } else {
                throw new RuntimeException("Already has an EGLSurface");
            }
        } else {
            throw new IllegalStateException("Input must be either a Surface or SurfaceTexture");
        }
    }

    public void createDummyPbufferSurface() {
        createPbufferSurface(1, 1);
    }

    public void createPbufferSurface(int width, int height) {
        checkIsNotReleased();
        if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
            EGLSurface eglCreatePbufferSurface = EGL14.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, new int[]{12375, width, 12374, height, 12344}, 0);
            this.eglSurface = eglCreatePbufferSurface;
            if (eglCreatePbufferSurface == EGL14.EGL_NO_SURFACE) {
                throw new RuntimeException("Failed to create pixel buffer surface with size " + width + "x" + height + ": 0x" + Integer.toHexString(EGL14.eglGetError()));
            }
            return;
        }
        throw new RuntimeException("Already has an EGLSurface");
    }

    public Context getEglBaseContext() {
        return new Context(this.eglContext);
    }

    public boolean hasSurface() {
        return this.eglSurface != EGL14.EGL_NO_SURFACE;
    }

    public int surfaceWidth() {
        int[] widthArray = new int[1];
        EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, widthArray, 0);
        return widthArray[0];
    }

    public int surfaceHeight() {
        int[] heightArray = new int[1];
        EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, heightArray, 0);
        return heightArray[0];
    }

    public void releaseSurface(boolean background) {
        if (background) {
            if (this.eglSurfaceBackground != EGL14.EGL_NO_SURFACE) {
                EGL14.eglDestroySurface(this.eglDisplay, this.eglSurfaceBackground);
                this.eglSurfaceBackground = EGL14.EGL_NO_SURFACE;
            }
        } else if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
        }
    }

    private void checkIsNotReleased() {
        if (this.eglDisplay == EGL14.EGL_NO_DISPLAY || this.eglContext == EGL14.EGL_NO_CONTEXT || this.eglConfig == null) {
            throw new RuntimeException("This object has been released");
        }
    }

    public void release() {
        checkIsNotReleased();
        releaseSurface(false);
        releaseSurface(true);
        detachCurrent();
        synchronized (EglBase.lock) {
            EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
        }
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(this.eglDisplay);
        this.eglContext = EGL14.EGL_NO_CONTEXT;
        this.eglDisplay = EGL14.EGL_NO_DISPLAY;
        this.eglConfig = null;
    }

    public void makeCurrent() {
        checkIsNotReleased();
        if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
            synchronized (EglBase.lock) {
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurface;
                if (!EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    throw new RuntimeException("eglMakeCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
            }
            return;
        }
        throw new RuntimeException("No EGLSurface - can't make current");
    }

    public void makeBackgroundCurrent() {
        checkIsNotReleased();
        if (this.eglSurfaceBackground != EGL14.EGL_NO_SURFACE) {
            synchronized (EglBase.lock) {
                EGLDisplay eGLDisplay = this.eglDisplay;
                EGLSurface eGLSurface = this.eglSurfaceBackground;
                if (!EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                    throw new RuntimeException("eglMakeCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
                }
            }
            return;
        }
        throw new RuntimeException("No EGLSurface - can't make current");
    }

    public boolean hasBackgroundSurface() {
        return this.eglSurfaceBackground != EGL14.EGL_NO_SURFACE;
    }

    public void detachCurrent() {
        synchronized (EglBase.lock) {
            if (!EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
                throw new RuntimeException("eglDetachCurrent failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
            }
        }
    }

    public void swapBuffers(boolean background) {
        checkIsNotReleased();
        EGLSurface surface = background ? this.eglSurfaceBackground : this.eglSurface;
        if (surface != EGL14.EGL_NO_SURFACE) {
            synchronized (EglBase.lock) {
                EGL14.eglSwapBuffers(this.eglDisplay, surface);
            }
            return;
        }
        throw new RuntimeException("No EGLSurface - can't swap buffers");
    }

    public void swapBuffers(long timeStampNs, boolean background) {
        checkIsNotReleased();
        EGLSurface surface = background ? this.eglSurfaceBackground : this.eglSurface;
        if (surface != EGL14.EGL_NO_SURFACE) {
            synchronized (EglBase.lock) {
                EGLExt.eglPresentationTimeANDROID(this.eglDisplay, surface, timeStampNs);
                EGL14.eglSwapBuffers(this.eglDisplay, surface);
            }
            return;
        }
        throw new RuntimeException("No EGLSurface - can't swap buffers");
    }

    private static EGLDisplay getEglDisplay() {
        EGLDisplay eglDisplay2 = EGL14.eglGetDisplay(0);
        if (eglDisplay2 != EGL14.EGL_NO_DISPLAY) {
            int[] version = new int[2];
            if (EGL14.eglInitialize(eglDisplay2, version, 0, version, 1)) {
                return eglDisplay2;
            }
            throw new RuntimeException("Unable to initialize EGL14: 0x" + Integer.toHexString(EGL14.eglGetError()));
        }
        throw new RuntimeException("Unable to get EGL14 display: 0x" + Integer.toHexString(EGL14.eglGetError()));
    }

    private static EGLConfig getEglConfig(EGLDisplay eglDisplay2, int[] configAttributes) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(eglDisplay2, configAttributes, 0, configs, 0, configs.length, numConfigs, 0)) {
            throw new RuntimeException("eglChooseConfig failed: 0x" + Integer.toHexString(EGL14.eglGetError()));
        } else if (numConfigs[0] > 0) {
            EGLConfig eglConfig2 = configs[0];
            if (eglConfig2 != null) {
                return eglConfig2;
            }
            throw new RuntimeException("eglChooseConfig returned null");
        } else {
            throw new RuntimeException("Unable to find any matching EGL config");
        }
    }

    private static EGLContext createEglContext(EGLContext sharedContext, EGLDisplay eglDisplay2, EGLConfig eglConfig2, int openGlesVersion) {
        EGLContext eglContext2;
        if (sharedContext == null || sharedContext != EGL14.EGL_NO_CONTEXT) {
            int[] contextAttributes = {12440, openGlesVersion, 12344};
            EGLContext rootContext = sharedContext == null ? EGL14.EGL_NO_CONTEXT : sharedContext;
            synchronized (EglBase.lock) {
                eglContext2 = EGL14.eglCreateContext(eglDisplay2, eglConfig2, rootContext, contextAttributes, 0);
            }
            if (eglContext2 != EGL14.EGL_NO_CONTEXT) {
                return eglContext2;
            }
            throw new RuntimeException("Failed to create EGL context: 0x" + Integer.toHexString(EGL14.eglGetError()));
        }
        throw new RuntimeException("Invalid sharedContext");
    }
}
