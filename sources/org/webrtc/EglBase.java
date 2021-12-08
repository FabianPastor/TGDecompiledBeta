package org.webrtc;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLContext;
import org.webrtc.EglBase10;
import org.webrtc.EglBase14;

public interface EglBase {
    public static final int[] CONFIG_PIXEL_BUFFER = CC.configBuilder().setSupportsPixelBuffer(true).createConfigAttributes();
    public static final int[] CONFIG_PIXEL_RGBA_BUFFER = CC.configBuilder().setHasAlphaChannel(true).setSupportsPixelBuffer(true).createConfigAttributes();
    public static final int[] CONFIG_PLAIN = CC.configBuilder().createConfigAttributes();
    public static final int[] CONFIG_RECORDABLE = CC.configBuilder().setIsRecordable(true).createConfigAttributes();
    public static final int[] CONFIG_RGBA = CC.configBuilder().setHasAlphaChannel(true).createConfigAttributes();
    public static final int EGL_OPENGL_ES2_BIT = 4;
    public static final int EGL_OPENGL_ES3_BIT = 64;
    public static final int EGL_RECORDABLE_ANDROID = 12610;
    public static final Object lock = new Object();

    public interface Context {
        public static final long NO_CONTEXT = 0;

        long getNativeEglContext();
    }

    void createBackgroundSurface(SurfaceTexture surfaceTexture);

    void createDummyPbufferSurface();

    void createPbufferSurface(int i, int i2);

    void createSurface(SurfaceTexture surfaceTexture);

    void createSurface(Surface surface);

    void detachCurrent();

    Context getEglBaseContext();

    boolean hasBackgroundSurface();

    boolean hasSurface();

    void makeBackgroundCurrent();

    void makeCurrent();

    void release();

    void releaseSurface(boolean z);

    int surfaceHeight();

    int surfaceWidth();

    void swapBuffers(long j, boolean z);

    void swapBuffers(boolean z);

    /* renamed from: org.webrtc.EglBase$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        static {
            Object obj = EglBase.lock;
        }

        public static ConfigBuilder configBuilder() {
            return new ConfigBuilder();
        }

        public static int getOpenGlesVersionFromConfig(int[] configAttributes) {
            int i = 0;
            while (i < configAttributes.length - 1) {
                if (configAttributes[i] == 12352) {
                    switch (configAttributes[i + 1]) {
                        case 4:
                            return 2;
                        case 64:
                            return 3;
                        default:
                            return 1;
                    }
                } else {
                    i++;
                }
            }
            return 1;
        }

        public static EglBase create(Context sharedContext, int[] configAttributes) {
            if (sharedContext == null) {
                if (EglBase14Impl.isEGL14Supported()) {
                    return createEgl14(configAttributes);
                }
                return createEgl10(configAttributes);
            } else if (sharedContext instanceof EglBase14.Context) {
                return createEgl14((EglBase14.Context) sharedContext, configAttributes);
            } else {
                if (sharedContext instanceof EglBase10.Context) {
                    return createEgl10((EglBase10.Context) sharedContext, configAttributes);
                }
                throw new IllegalArgumentException("Unrecognized Context");
            }
        }

        public static EglBase create() {
            return create((Context) null, EglBase.CONFIG_PLAIN);
        }

        public static EglBase create(Context sharedContext) {
            return create(sharedContext, EglBase.CONFIG_PLAIN);
        }

        public static EglBase10 createEgl10(int[] configAttributes) {
            return new EglBase10Impl((EGLContext) null, configAttributes);
        }

        public static EglBase10 createEgl10(EglBase10.Context sharedContext, int[] configAttributes) {
            return new EglBase10Impl(sharedContext == null ? null : sharedContext.getRawContext(), configAttributes);
        }

        public static EglBase10 createEgl10(EGLContext sharedContext, int[] configAttributes) {
            return new EglBase10Impl(sharedContext, configAttributes);
        }

        public static EglBase14 createEgl14(int[] configAttributes) {
            return new EglBase14Impl((android.opengl.EGLContext) null, configAttributes);
        }

        public static EglBase14 createEgl14(EglBase14.Context sharedContext, int[] configAttributes) {
            return new EglBase14Impl(sharedContext == null ? null : sharedContext.getRawContext(), configAttributes);
        }

        public static EglBase14 createEgl14(android.opengl.EGLContext sharedContext, int[] configAttributes) {
            return new EglBase14Impl(sharedContext, configAttributes);
        }
    }

    public static class ConfigBuilder {
        private boolean hasAlphaChannel;
        private boolean isRecordable;
        private int openGlesVersion = 2;
        private boolean supportsPixelBuffer;

        public ConfigBuilder setOpenGlesVersion(int version) {
            if (version < 1 || version > 3) {
                throw new IllegalArgumentException("OpenGL ES version " + version + " not supported");
            }
            this.openGlesVersion = version;
            return this;
        }

        public ConfigBuilder setHasAlphaChannel(boolean hasAlphaChannel2) {
            this.hasAlphaChannel = hasAlphaChannel2;
            return this;
        }

        public ConfigBuilder setSupportsPixelBuffer(boolean supportsPixelBuffer2) {
            this.supportsPixelBuffer = supportsPixelBuffer2;
            return this;
        }

        public ConfigBuilder setIsRecordable(boolean isRecordable2) {
            this.isRecordable = isRecordable2;
            return this;
        }

        public int[] createConfigAttributes() {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(12324);
            list.add(8);
            list.add(12323);
            list.add(8);
            list.add(12322);
            list.add(8);
            if (this.hasAlphaChannel) {
                list.add(12321);
                list.add(8);
            }
            int i = this.openGlesVersion;
            if (i == 2 || i == 3) {
                list.add(12352);
                list.add(Integer.valueOf(this.openGlesVersion == 3 ? 64 : 4));
            }
            if (this.supportsPixelBuffer) {
                list.add(12339);
                list.add(1);
            }
            if (this.isRecordable) {
                list.add(12610);
                list.add(1);
            }
            list.add(12344);
            int[] res = new int[list.size()];
            for (int i2 = 0; i2 < list.size(); i2++) {
                res[i2] = list.get(i2).intValue();
            }
            return res;
        }
    }
}
