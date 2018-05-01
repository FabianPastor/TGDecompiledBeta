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

        public org.telegram.messenger.exoplayer2.video.DummySurface init(int r4) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r3 = this;
            r3.start();
            r0 = new android.os.Handler;
            r1 = r3.getLooper();
            r0.<init>(r1, r3);
            r3.handler = r0;
            monitor-enter(r3);
            r0 = r3.handler;	 Catch:{ all -> 0x0047 }
            r1 = 1;	 Catch:{ all -> 0x0047 }
            r2 = 0;	 Catch:{ all -> 0x0047 }
            r4 = r0.obtainMessage(r1, r4, r2);	 Catch:{ all -> 0x0047 }
            r4.sendToTarget();	 Catch:{ all -> 0x0047 }
        L_0x001a:
            r4 = r3.surface;	 Catch:{ all -> 0x0047 }
            if (r4 != 0) goto L_0x002c;	 Catch:{ all -> 0x0047 }
        L_0x001e:
            r4 = r3.initException;	 Catch:{ all -> 0x0047 }
            if (r4 != 0) goto L_0x002c;	 Catch:{ all -> 0x0047 }
        L_0x0022:
            r4 = r3.initError;	 Catch:{ all -> 0x0047 }
            if (r4 != 0) goto L_0x002c;
        L_0x0026:
            r3.wait();	 Catch:{ InterruptedException -> 0x002a }
            goto L_0x001a;
        L_0x002a:
            r2 = r1;
            goto L_0x001a;
        L_0x002c:
            monitor-exit(r3);	 Catch:{ all -> 0x0047 }
            if (r2 == 0) goto L_0x0036;
        L_0x002f:
            r4 = java.lang.Thread.currentThread();
            r4.interrupt();
        L_0x0036:
            r4 = r3.initException;
            if (r4 == 0) goto L_0x003d;
        L_0x003a:
            r4 = r3.initException;
            throw r4;
        L_0x003d:
            r4 = r3.initError;
            if (r4 == 0) goto L_0x0044;
        L_0x0041:
            r4 = r3.initError;
            throw r4;
        L_0x0044:
            r4 = r3.surface;
            return r4;
        L_0x0047:
            r4 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0047 }
            throw r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.video.DummySurface.DummySurfaceThread.init(int):org.telegram.messenger.exoplayer2.video.DummySurface");
        }

        public void release() {
            this.handler.sendEmptyMessage(3);
        }

        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            this.handler.sendEmptyMessage(2);
        }

        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    try {
                        initInternal(message.arg1);
                        synchronized (this) {
                            notify();
                        }
                    } catch (Message message2) {
                        Log.e(DummySurface.TAG, "Failed to initialize dummy surface", message2);
                        this.initException = message2;
                        synchronized (this) {
                            notify();
                        }
                    } catch (Message message22) {
                        try {
                            Log.e(DummySurface.TAG, "Failed to initialize dummy surface", message22);
                            this.initError = message22;
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

        private void initInternal(int i) {
            int[] iArr;
            EGLSurface eGLSurface;
            boolean z = false;
            this.display = EGL14.eglGetDisplay(0);
            Assertions.checkState(this.display != null, "eglGetDisplay failed");
            int[] iArr2 = new int[2];
            Assertions.checkState(EGL14.eglInitialize(this.display, iArr2, 0, iArr2, 1), "eglInitialize failed");
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            int[] iArr3 = new int[1];
            boolean z2 = EGL14.eglChooseConfig(this.display, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12327, 12344, 12339, 4, 12344}, 0, eGLConfigArr, 0, 1, iArr3, 0) && iArr3[0] > 0 && eGLConfigArr[0] != null;
            Assertions.checkState(z2, "eglChooseConfig failed");
            EGLConfig eGLConfig = eGLConfigArr[0];
            if (i == 0) {
                iArr = new int[]{12440, 2, 12344};
            } else {
                iArr = new int[]{12440, 2, DummySurface.EGL_PROTECTED_CONTENT_EXT, 1, 12344};
            }
            this.context = EGL14.eglCreateContext(this.display, eGLConfig, EGL14.EGL_NO_CONTEXT, iArr, 0);
            Assertions.checkState(this.context != null, "eglCreateContext failed");
            if (i == 1) {
                eGLSurface = EGL14.EGL_NO_SURFACE;
            } else {
                int[] iArr4;
                if (i == 2) {
                    iArr4 = new int[]{12375, 1, 12374, 1, DummySurface.EGL_PROTECTED_CONTENT_EXT, 1, 12344};
                } else {
                    iArr4 = new int[]{12375, 1, 12374, 1, 12344};
                }
                this.pbuffer = EGL14.eglCreatePbufferSurface(this.display, eGLConfig, iArr4, 0);
                Assertions.checkState(this.pbuffer != null, "eglCreatePbufferSurface failed");
                eGLSurface = this.pbuffer;
            }
            Assertions.checkState(EGL14.eglMakeCurrent(this.display, eGLSurface, eGLSurface, this.context), "eglMakeCurrent failed");
            GLES20.glGenTextures(1, this.textureIdHolder, 0);
            this.surfaceTexture = new SurfaceTexture(this.textureIdHolder[0]);
            this.surfaceTexture.setOnFrameAvailableListener(this);
            SurfaceTexture surfaceTexture = this.surfaceTexture;
            if (i != 0) {
                z = true;
            }
            this.surface = new DummySurface(this, surfaceTexture, z);
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
                secureMode = Util.SDK_INT < 24 ? null : getSecureModeV24(context);
                secureModeInitialized = true;
            }
            if (secureMode == null) {
                z = false;
            }
        }
        return z;
    }

    public static DummySurface newInstanceV17(Context context, boolean z) {
        assertApiLevel17OrHigher();
        int i = 0;
        if (z) {
            if (isSecureSupported(context) == null) {
                context = null;
                Assertions.checkState(context);
                context = new DummySurfaceThread();
                if (z) {
                    i = secureMode;
                }
                return context.init(i);
            }
        }
        context = true;
        Assertions.checkState(context);
        context = new DummySurfaceThread();
        if (z) {
            i = secureMode;
        }
        return context.init(i);
    }

    private DummySurface(DummySurfaceThread dummySurfaceThread, SurfaceTexture surfaceTexture, boolean z) {
        super(surfaceTexture);
        this.thread = dummySurfaceThread;
        this.secure = z;
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
        if (Util.SDK_INT < 26 && context.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance") == null) {
            return 0;
        }
        context = EGL14.eglQueryString(EGL14.eglGetDisplay(0), 12373);
        if (context == null || !context.contains(EXTENSION_PROTECTED_CONTENT)) {
            return 0;
        }
        return context.contains(EXTENSION_SURFACELESS_CONTEXT) != null ? true : 2;
    }
}
