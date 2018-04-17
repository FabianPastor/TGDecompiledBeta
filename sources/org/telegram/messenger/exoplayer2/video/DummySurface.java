package org.telegram.messenger.exoplayer2.video;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(17)
public final class DummySurface extends Surface {
    private static final int EGL_PROTECTED_CONTENT_EXT = 12992;
    private static final String EXTENSION_PROTECTED_CONTENT = "EGL_EXT_protected_content";
    private static final String EXTENSION_SURFACELESS_CONTEXT = "EGL_KHR_surfaceless_context";
    private static final int SECURE_MODE_NONE = 0;
    private static final int SECURE_MODE_PROTECTED_PBUFFER = 2;
    private static final int SECURE_MODE_SURFACELESS_CONTEXT = 1;
    private static final String TAG = "DummySurface";
    private static int secureMode;
    private static boolean secureModeInitialized;
    public final boolean secure;
    private final DummySurfaceThread thread;
    private boolean threadReleased;

    private static class DummySurfaceThread extends HandlerThread implements OnFrameAvailableListener, Callback {
        private static final int MSG_INIT = 1;
        private static final int MSG_RELEASE = 3;
        private static final int MSG_UPDATE_TEXTURE = 2;
        private EGLContext context;
        private EGLDisplay display;
        private Handler handler;
        private Error initError;
        private RuntimeException initException;
        private EGLSurface pbuffer;
        private DummySurface surface;
        private SurfaceTexture surfaceTexture;
        private final int[] textureIdHolder = new int[1];

        public DummySurfaceThread() {
            super("dummySurface");
        }

        public DummySurface init(int secureMode) {
            start();
            this.handler = new Handler(getLooper(), this);
            boolean wasInterrupted = false;
            synchronized (this) {
                this.handler.obtainMessage(1, secureMode, 0).sendToTarget();
                while (this.surface == null && this.initException == null && this.initError == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        wasInterrupted = true;
                    }
                }
            }
            if (wasInterrupted) {
                Thread.currentThread().interrupt();
            }
            if (this.initException != null) {
                throw this.initException;
            } else if (this.initError == null) {
                return this.surface;
            } else {
                throw this.initError;
            }
        }

        public void release() {
            this.handler.sendEmptyMessage(3);
        }

        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            this.handler.sendEmptyMessage(2);
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        initInternal(msg.arg1);
                        synchronized (this) {
                            notify();
                        }
                    } catch (RuntimeException e) {
                        Log.e(DummySurface.TAG, "Failed to initialize dummy surface", e);
                        this.initException = e;
                        synchronized (this) {
                            notify();
                        }
                    } catch (Error e2) {
                        try {
                            Log.e(DummySurface.TAG, "Failed to initialize dummy surface", e2);
                            this.initError = e2;
                            synchronized (this) {
                                notify();
                            }
                        } catch (Throwable th) {
                            synchronized (this) {
                                notify();
                            }
                        }
                    }
                    return true;
                case 2:
                    this.surfaceTexture.updateTexImage();
                    return true;
                case 3:
                    try {
                        releaseInternal();
                    } catch (Throwable th2) {
                        quit();
                    }
                    quit();
                    return true;
                default:
                    return true;
            }
        }

        private void initInternal(int secureMode) {
            int[] glAttributes;
            EGLSurface surface;
            int i = secureMode;
            boolean z = false;
            this.display = EGL14.eglGetDisplay(0);
            Assertions.checkState(this.display != null, "eglGetDisplay failed");
            int[] version = new int[2];
            Assertions.checkState(EGL14.eglInitialize(r0.display, version, 0, version, 1), "eglInitialize failed");
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            boolean z2 = EGL14.eglChooseConfig(r0.display, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12327, 12344, 12339, 4, 12344}, 0, configs, 0, 1, numConfigs, null) && numConfigs[0] > 0 && configs[0] != null;
            Assertions.checkState(z2, "eglChooseConfig failed");
            EGLConfig config = configs[0];
            if (i == 0) {
                glAttributes = new int[]{12440, 2, 12344};
            } else {
                glAttributes = new int[]{12440, 2, DummySurface.EGL_PROTECTED_CONTENT_EXT, 1, 12344};
            }
            r0.context = EGL14.eglCreateContext(r0.display, config, EGL14.EGL_NO_CONTEXT, glAttributes, 0);
            Assertions.checkState(r0.context != null, "eglCreateContext failed");
            if (i == 1) {
                surface = EGL14.EGL_NO_SURFACE;
            } else {
                int[] pbufferAttributes;
                if (i == 2) {
                    pbufferAttributes = new int[]{12375, 1, 12374, 1, DummySurface.EGL_PROTECTED_CONTENT_EXT, 1, 12344};
                } else {
                    pbufferAttributes = new int[]{12375, 1, 12374, 1, 12344};
                }
                r0.pbuffer = EGL14.eglCreatePbufferSurface(r0.display, config, pbufferAttributes, 0);
                Assertions.checkState(r0.pbuffer != null, "eglCreatePbufferSurface failed");
                surface = r0.pbuffer;
            }
            Assertions.checkState(EGL14.eglMakeCurrent(r0.display, surface, surface, r0.context), "eglMakeCurrent failed");
            GLES20.glGenTextures(1, r0.textureIdHolder, 0);
            r0.surfaceTexture = new SurfaceTexture(r0.textureIdHolder[0]);
            r0.surfaceTexture.setOnFrameAvailableListener(r0);
            SurfaceTexture surfaceTexture = r0.surfaceTexture;
            if (i != 0) {
                z = true;
            }
            r0.surface = new DummySurface(r0, surfaceTexture, z);
        }

        private void releaseInternal() {
            try {
                if (this.surfaceTexture != null) {
                    this.surfaceTexture.release();
                    GLES20.glDeleteTextures(1, this.textureIdHolder, 0);
                }
                if (this.pbuffer != null) {
                    EGL14.eglDestroySurface(this.display, this.pbuffer);
                }
                if (this.context != null) {
                    EGL14.eglDestroyContext(this.display, this.context);
                }
                this.pbuffer = null;
                this.context = null;
                this.display = null;
                this.surface = null;
                this.surfaceTexture = null;
            } catch (Throwable th) {
                if (this.pbuffer != null) {
                    EGL14.eglDestroySurface(this.display, this.pbuffer);
                }
                if (this.context != null) {
                    EGL14.eglDestroyContext(this.display, this.context);
                }
                this.pbuffer = null;
                this.context = null;
                this.display = null;
                this.surface = null;
                this.surfaceTexture = null;
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface SecureMode {
    }

    public static synchronized boolean isSecureSupported(Context context) {
        boolean z;
        synchronized (DummySurface.class) {
            z = true;
            if (!secureModeInitialized) {
                secureMode = Util.SDK_INT < 24 ? 0 : getSecureModeV24(context);
                secureModeInitialized = true;
            }
            if (secureMode == 0) {
                z = false;
            }
        }
        return z;
    }

    public static DummySurface newInstanceV17(Context context, boolean secure) {
        boolean z;
        DummySurfaceThread thread;
        assertApiLevel17OrHigher();
        int i = 0;
        if (secure) {
            if (!isSecureSupported(context)) {
                z = false;
                Assertions.checkState(z);
                thread = new DummySurfaceThread();
                if (secure) {
                    i = secureMode;
                }
                return thread.init(i);
            }
        }
        z = true;
        Assertions.checkState(z);
        thread = new DummySurfaceThread();
        if (secure) {
            i = secureMode;
        }
        return thread.init(i);
    }

    private DummySurface(DummySurfaceThread thread, SurfaceTexture surfaceTexture, boolean secure) {
        super(surfaceTexture);
        this.thread = thread;
        this.secure = secure;
    }

    public void release() {
        super.release();
        synchronized (this.thread) {
            if (!this.threadReleased) {
                this.thread.release();
                this.threadReleased = true;
            }
        }
    }

    private static void assertApiLevel17OrHigher() {
        if (Util.SDK_INT < 17) {
            throw new UnsupportedOperationException("Unsupported prior to API level 17");
        }
    }

    @TargetApi(24)
    private static int getSecureModeV24(Context context) {
        if (Util.SDK_INT < 26 && ("samsung".equals(Util.MANUFACTURER) || "XT1650".equals(Util.MODEL))) {
            return 0;
        }
        if (Util.SDK_INT < 26 && !context.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance")) {
            return 0;
        }
        String eglExtensions = EGL14.eglQueryString(EGL14.eglGetDisplay(0), 12373);
        if (eglExtensions == null || !eglExtensions.contains(EXTENSION_PROTECTED_CONTENT)) {
            return 0;
        }
        return eglExtensions.contains(EXTENSION_SURFACELESS_CONTEXT) ? 1 : 2;
    }
}
