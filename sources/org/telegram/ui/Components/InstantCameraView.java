package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraInfo;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.Size;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

@TargetApi(18)
public class InstantCameraView extends FrameLayout implements NotificationCenterDelegate {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    private AnimatorSet animatorSet;
    private Size aspectRatio;
    private ChatActivity baseFragment;
    private FrameLayout cameraContainer;
    private File cameraFile;
    private volatile boolean cameraReady;
    private CameraSession cameraSession;
    private int[] cameraTexture = new int[1];
    private float cameraTextureAlpha = 1.0f;
    private CameraGLThread cameraThread;
    private boolean cancelled;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean deviceHasGoodCamera;
    private long duration;
    private InputEncryptedFile encryptedFile;
    private InputFile file;
    private boolean isFrontface = true;
    private boolean isSecretChat;
    private byte[] iv;
    private byte[] key;
    private Bitmap lastBitmap;
    private float[] mMVPMatrix;
    private float[] mSTMatrix;
    private float[] moldSTMatrix;
    private AnimatorSet muteAnimation;
    private ImageView muteImageView;
    private int[] oldCameraTexture = new int[1];
    private Paint paint;
    private Size pictureSize;
    private int[] position = new int[2];
    private Size previewSize;
    private float progress;
    private Timer progressTimer;
    private long recordStartTime;
    private long recordedTime;
    private boolean recording;
    private RectF rect;
    private boolean requestingPermissions;
    private float scaleX;
    private float scaleY;
    private CameraInfo selectedCamera;
    private long size;
    private ImageView switchCameraButton;
    private FloatBuffer textureBuffer;
    private BackupImageView textureOverlayView;
    private TextureView textureView;
    private Runnable timerRunnable = new Runnable() {
        public void run() {
            if (InstantCameraView.this.recording) {
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordProgressChanged, Long.valueOf(InstantCameraView.this.duration = System.currentTimeMillis() - InstantCameraView.this.recordStartTime), Double.valueOf(0.0d));
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable, 50);
            }
        }
    };
    private FloatBuffer vertexBuffer;
    private VideoEditedInfo videoEditedInfo;
    private VideoPlayer videoPlayer;

    private class AudioBufferInfo {
        byte[] buffer;
        boolean last;
        int lastWroteBuffer;
        long[] offset;
        int[] read;
        int results;

        private AudioBufferInfo() {
            this.buffer = new byte[20480];
            this.offset = new long[10];
            this.read = new int[10];
        }

        /* synthetic */ AudioBufferInfo(InstantCameraView x0, AnonymousClass1 x1) {
            this();
        }
    }

    public class CameraGLThread extends DispatchQueue {
        private final int DO_REINIT_MESSAGE = 2;
        private final int DO_RENDER_MESSAGE = 0;
        private final int DO_SETSESSION_MESSAGE = 3;
        private final int DO_SHUTDOWN_MESSAGE = 1;
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        private Integer cameraId = Integer.valueOf(0);
        private SurfaceTexture cameraSurface;
        private CameraSession currentSession;
        private int drawProgram;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private GL gl;
        private boolean initied;
        private int positionHandle;
        private boolean recording;
        private int rotationAngle;
        private SurfaceTexture surfaceTexture;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private VideoRecorder videoEncoder;

        public CameraGLThread(SurfaceTexture surface, int surfaceWidth, int surfaceHeight) {
            super("CameraGLThread");
            this.surfaceTexture = surface;
            int width = InstantCameraView.this.previewSize.getWidth();
            int height = InstantCameraView.this.previewSize.getHeight();
            float scale = ((float) surfaceWidth) / ((float) Math.min(width, height));
            width = (int) (((float) width) * scale);
            height = (int) (((float) height) * scale);
            if (width > height) {
                InstantCameraView.this.scaleX = 1.0f;
                InstantCameraView.this.scaleY = ((float) width) / ((float) surfaceHeight);
                return;
            }
            InstantCameraView.this.scaleX = ((float) height) / ((float) surfaceWidth);
            InstantCameraView.this.scaleY = 1.0f;
        }

        private boolean initGL() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start init gl");
            }
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                int[] configsCount = new int[1];
                EGLConfig[] configs = new EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, configs, 1, configsCount)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                } else if (configsCount[0] > 0) {
                    this.eglConfig = configs[0];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.eglContext == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else if (this.surfaceTexture instanceof SurfaceTexture) {
                        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
                        if (this.eglSurface == null || this.eglSurface == EGL10.EGL_NO_SURFACE) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            }
                            finish();
                            return false;
                        }
                        if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                            this.gl = this.eglContext.getGL();
                            float tX = (1.0f / InstantCameraView.this.scaleX) / 2.0f;
                            float tY = (1.0f / InstantCameraView.this.scaleY) / 2.0f;
                            float[] fArr = new float[12];
                            fArr = new float[]{-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                            float[] texData = new float[]{0.5f - tX, 0.5f - tY, 0.5f + tX, 0.5f - tY, 0.5f - tX, 0.5f + tY, 0.5f + tX, 0.5f + tY};
                            this.videoEncoder = new VideoRecorder(InstantCameraView.this, null);
                            InstantCameraView.this.vertexBuffer = ByteBuffer.allocateDirect(fArr.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                            InstantCameraView.this.vertexBuffer.put(fArr).position(0);
                            InstantCameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                            InstantCameraView.this.textureBuffer.put(texData).position(0);
                            Matrix.setIdentityM(InstantCameraView.this.mSTMatrix, 0);
                            int vertexShader = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                            int fragmentShader = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                            if (vertexShader == 0 || fragmentShader == 0) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.e("failed creating shader");
                                }
                                finish();
                                return false;
                            }
                            this.drawProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.drawProgram, vertexShader);
                            GLES20.glAttachShader(this.drawProgram, fragmentShader);
                            GLES20.glLinkProgram(this.drawProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] == 0) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.e("failed link shader");
                                }
                                GLES20.glDeleteProgram(this.drawProgram);
                                this.drawProgram = 0;
                            } else {
                                this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                                this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                                this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                            }
                            GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                            GLES20.glTexParameteri(36197, 10241, 9729);
                            GLES20.glTexParameteri(36197, 10240, 9729);
                            GLES20.glTexParameteri(36197, 10242, 33071);
                            GLES20.glTexParameteri(36197, 10243, 33071);
                            Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                            this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                            this.cameraSurface.setOnFrameAvailableListener(new InstantCameraView$CameraGLThread$$Lambda$0(this));
                            InstantCameraView.this.createCamera(this.cameraSurface);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("gl initied");
                            }
                            return true;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
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
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            }
            finish();
            return false;
        }

        public void reinitForNewCamera() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(2), 0);
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            if (this.eglContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, this.eglContext);
                this.eglContext = null;
            }
            if (this.eglDisplay != null) {
                this.egl10.eglTerminate(this.eglDisplay);
                this.eglDisplay = null;
            }
        }

        public void setCurrentSession(CameraSession session) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, session), 0);
            }
        }

        private void onDraw(Integer cameraId) {
            if (!this.initied) {
                return;
            }
            if ((this.eglContext.equals(this.egl10.eglGetCurrentContext()) && this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) || this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                this.cameraSurface.updateTexImage();
                if (!this.recording) {
                    this.videoEncoder.startRecording(InstantCameraView.this.cameraFile, EGL14.eglGetCurrentContext());
                    this.recording = true;
                    int orientation = this.currentSession.getCurrentOrientation();
                    if (orientation == 90 || orientation == 270) {
                        float temp = InstantCameraView.this.scaleX;
                        InstantCameraView.this.scaleX = InstantCameraView.this.scaleY;
                        InstantCameraView.this.scaleY = temp;
                    }
                }
                this.videoEncoder.frameAvailable(this.cameraSurface, cameraId, System.nanoTime());
                this.cameraSurface.getTransformMatrix(InstantCameraView.this.mSTMatrix);
                GLES20.glUseProgram(this.drawProgram);
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
                GLES20.glEnableVertexAttribArray(this.positionHandle);
                GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
                GLES20.glEnableVertexAttribArray(this.textureHandle);
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
                GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
                GLES20.glDrawArrays(5, 0, 4);
                GLES20.glDisableVertexAttribArray(this.positionHandle);
                GLES20.glDisableVertexAttribArray(this.textureHandle);
                GLES20.glBindTexture(36197, 0);
                GLES20.glUseProgram(0);
                this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            }
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void handleMessage(Message inputMessage) {
            switch (inputMessage.what) {
                case 0:
                    onDraw((Integer) inputMessage.obj);
                    return;
                case 1:
                    finish();
                    if (this.recording) {
                        this.videoEncoder.stopRecording(inputMessage.arg1);
                    }
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                        return;
                    }
                    return;
                case 2:
                    if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                        if (this.cameraSurface != null) {
                            this.cameraSurface.getTransformMatrix(InstantCameraView.this.moldSTMatrix);
                            this.cameraSurface.setOnFrameAvailableListener(null);
                            this.cameraSurface.release();
                            InstantCameraView.this.oldCameraTexture[0] = InstantCameraView.this.cameraTexture[0];
                            InstantCameraView.this.cameraTextureAlpha = 0.0f;
                            InstantCameraView.this.cameraTexture[0] = 0;
                        }
                        Integer num = this.cameraId;
                        this.cameraId = Integer.valueOf(this.cameraId.intValue() + 1);
                        InstantCameraView.this.cameraReady = false;
                        GLES20.glGenTextures(1, InstantCameraView.this.cameraTexture, 0);
                        GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
                        GLES20.glTexParameteri(36197, 10241, 9729);
                        GLES20.glTexParameteri(36197, 10240, 9729);
                        GLES20.glTexParameteri(36197, 10242, 33071);
                        GLES20.glTexParameteri(36197, 10243, 33071);
                        this.cameraSurface = new SurfaceTexture(InstantCameraView.this.cameraTexture[0]);
                        this.cameraSurface.setOnFrameAvailableListener(new InstantCameraView$CameraGLThread$$Lambda$1(this));
                        InstantCameraView.this.createCamera(this.cameraSurface);
                        return;
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("set gl rednderer session");
                    }
                    CameraSession newSession = inputMessage.obj;
                    if (this.currentSession == newSession) {
                        this.rotationAngle = this.currentSession.getWorldAngle();
                        Matrix.setIdentityM(InstantCameraView.this.mMVPMatrix, 0);
                        if (this.rotationAngle != 0) {
                            Matrix.rotateM(InstantCameraView.this.mMVPMatrix, 0, (float) this.rotationAngle, 0.0f, 0.0f, 1.0f);
                            return;
                        }
                        return;
                    }
                    this.currentSession = newSession;
                    return;
                default:
                    return;
            }
        }

        public void shutdown(int send) {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, send, 0), 0);
            }
        }

        /* renamed from: requestRender */
        public void lambda$initGL$0$InstantCameraView$CameraGLThread() {
            Handler handler = InstantCameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0, this.cameraId), 0);
            }
        }
    }

    private static class EncoderHandler extends Handler {
        private WeakReference<VideoRecorder> mWeakEncoder;

        public EncoderHandler(VideoRecorder encoder) {
            this.mWeakEncoder = new WeakReference(encoder);
        }

        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            VideoRecorder encoder = (VideoRecorder) this.mWeakEncoder.get();
            if (encoder != null) {
                switch (what) {
                    case 0:
                        try {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("start encoder");
                            }
                            encoder.prepareEncoder();
                            return;
                        } catch (Throwable e) {
                            FileLog.e(e);
                            encoder.handleStopRecording(0);
                            Looper.myLooper().quit();
                            return;
                        }
                    case 1:
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("stop encoder");
                        }
                        encoder.handleStopRecording(inputMessage.arg1);
                        return;
                    case 2:
                        encoder.handleVideoFrameAvailable((((long) inputMessage.arg1) << 32) | (((long) inputMessage.arg2) & 4294967295L), inputMessage.obj);
                        return;
                    case 3:
                        encoder.handleAudioFrameAvailable((AudioBufferInfo) inputMessage.obj);
                        return;
                    default:
                        return;
                }
            }
        }

        public void exit() {
            Looper.myLooper().quit();
        }
    }

    private class VideoRecorder implements Runnable {
        private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
        private static final int FRAME_RATE = 30;
        private static final int IFRAME_INTERVAL = 1;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private int alphaHandle;
        private BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioFirst;
        private AudioRecord audioRecorder;
        private long audioStartTime;
        private boolean audioStopedByTime;
        private int audioTrackIndex;
        private boolean blendEnabled;
        private ArrayBlockingQueue<AudioBufferInfo> buffers;
        private ArrayList<AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private long desyncTime;
        private int drawProgram;
        private android.opengl.EGLConfig eglConfig;
        private android.opengl.EGLContext eglContext;
        private android.opengl.EGLDisplay eglDisplay;
        private android.opengl.EGLSurface eglSurface;
        private volatile EncoderHandler handler;
        private Integer lastCameraId;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private boolean ready;
        private Runnable recorderRunnable;
        private volatile boolean running;
        private int scaleXHandle;
        private int scaleYHandle;
        private volatile int sendWhenDone;
        private android.opengl.EGLContext sharedEglContext;
        private boolean skippedFirst;
        private long skippedTime;
        private Surface surface;
        private final Object sync;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private BufferInfo videoBufferInfo;
        private boolean videoConvertFirstWrite;
        private MediaCodec videoEncoder;
        private File videoFile;
        private long videoFirst;
        private int videoHeight;
        private long videoLast;
        private int videoTrackIndex;
        private int videoWidth;
        private int zeroTimeStamps;

        private VideoRecorder() {
            this.videoConvertFirstWrite = true;
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            this.buffersToWrite = new ArrayList();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.audioStartTime = -1;
            this.currentTimestamp = 0;
            this.lastTimestamp = -1;
            this.sync = new Object();
            this.videoFirst = -1;
            this.audioFirst = -1;
            this.lastCameraId = Integer.valueOf(0);
            this.buffers = new ArrayBlockingQueue(10);
            this.recorderRunnable = new Runnable() {
                /* JADX WARNING: Missing block: B:9:0x002c, code:
            if (org.telegram.ui.Components.InstantCameraView.VideoRecorder.access$3300(r15.this$1) == 0) goto L_0x002e;
     */
                public void run() {
                    /*
                    r15 = this;
                    r14 = 0;
                    r13 = 10;
                    r12 = 1;
                    r2 = -1;
                    r5 = 0;
                L_0x0007:
                    if (r5 != 0) goto L_0x002e;
                L_0x0009:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.running;
                    if (r8 != 0) goto L_0x0054;
                L_0x0011:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.audioRecorder;
                    r8 = r8.getRecordingState();
                    if (r8 == r12) goto L_0x0054;
                L_0x001d:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;	 Catch:{ Exception -> 0x0051 }
                    r8 = r8.audioRecorder;	 Catch:{ Exception -> 0x0051 }
                    r8.stop();	 Catch:{ Exception -> 0x0051 }
                L_0x0026:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.sendWhenDone;
                    if (r8 != 0) goto L_0x0054;
                L_0x002e:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;	 Catch:{ Exception -> 0x0105 }
                    r8 = r8.audioRecorder;	 Catch:{ Exception -> 0x0105 }
                    r8.release();	 Catch:{ Exception -> 0x0105 }
                L_0x0037:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.handler;
                    r9 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r9 = r9.handler;
                    r10 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r10 = r10.sendWhenDone;
                    r9 = r9.obtainMessage(r12, r10, r14);
                    r8.sendMessage(r9);
                    return;
                L_0x0051:
                    r6 = move-exception;
                    r5 = 1;
                    goto L_0x0026;
                L_0x0054:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.buffers;
                    r8 = r8.isEmpty();
                    if (r8 == 0) goto L_0x00c8;
                L_0x0060:
                    r1 = new org.telegram.ui.Components.InstantCameraView$AudioBufferInfo;
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = org.telegram.ui.Components.InstantCameraView.this;
                    r9 = 0;
                    r1.<init>(r8, r9);
                L_0x006a:
                    r1.lastWroteBuffer = r14;
                    r1.results = r13;
                    r0 = 0;
                L_0x006f:
                    if (r0 >= r13) goto L_0x009d;
                L_0x0071:
                    r8 = -1;
                    r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
                    if (r8 != 0) goto L_0x007f;
                L_0x0077:
                    r8 = java.lang.System.nanoTime();
                    r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                    r2 = r8 / r10;
                L_0x007f:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.audioRecorder;
                    r9 = r1.buffer;
                    r10 = r0 * 2048;
                    r11 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
                    r7 = r8.read(r9, r10, r11);
                    if (r7 > 0) goto L_0x00d5;
                L_0x0091:
                    r1.results = r0;
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.running;
                    if (r8 != 0) goto L_0x009d;
                L_0x009b:
                    r1.last = r12;
                L_0x009d:
                    r8 = r1.results;
                    if (r8 >= 0) goto L_0x00a5;
                L_0x00a1:
                    r8 = r1.last;
                    if (r8 == 0) goto L_0x00ec;
                L_0x00a5:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.running;
                    if (r8 != 0) goto L_0x00b2;
                L_0x00ad:
                    r8 = r1.results;
                    if (r8 >= r13) goto L_0x00b2;
                L_0x00b1:
                    r5 = 1;
                L_0x00b2:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.handler;
                    r9 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r9 = r9.handler;
                    r10 = 3;
                    r9 = r9.obtainMessage(r10, r1);
                    r8.sendMessage(r9);
                    goto L_0x0007;
                L_0x00c8:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.buffers;
                    r1 = r8.poll();
                    r1 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r1;
                    goto L_0x006a;
                L_0x00d5:
                    r8 = r1.offset;
                    r8[r0] = r2;
                    r8 = r1.read;
                    r8[r0] = r7;
                    r8 = 1000000; // 0xvar_ float:1.401298E-39 double:4.940656E-318;
                    r8 = r8 * r7;
                    r9 = 44100; // 0xaCLASSNAME float:6.1797E-41 double:2.17883E-319;
                    r8 = r8 / r9;
                    r4 = r8 / 2;
                    r8 = (long) r4;
                    r2 = r2 + r8;
                    r0 = r0 + 1;
                    goto L_0x006f;
                L_0x00ec:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;
                    r8 = r8.running;
                    if (r8 != 0) goto L_0x00f7;
                L_0x00f4:
                    r5 = 1;
                    goto L_0x0007;
                L_0x00f7:
                    r8 = org.telegram.ui.Components.InstantCameraView.VideoRecorder.this;	 Catch:{ Exception -> 0x0102 }
                    r8 = r8.buffers;	 Catch:{ Exception -> 0x0102 }
                    r8.put(r1);	 Catch:{ Exception -> 0x0102 }
                    goto L_0x0007;
                L_0x0102:
                    r8 = move-exception;
                    goto L_0x0007;
                L_0x0105:
                    r6 = move-exception;
                    org.telegram.messenger.FileLog.e(r6);
                    goto L_0x0037;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.1.run():void");
                }
            };
        }

        /* synthetic */ VideoRecorder(InstantCameraView x0, AnonymousClass1 x1) {
            this();
        }

        public void startRecording(File outputFile, android.opengl.EGLContext sharedContext) {
            int resolution;
            int bitrate;
            String model = Build.DEVICE;
            if (model == null) {
                model = "";
            }
            if (model.startsWith("zeroflte") || model.startsWith("zenlte")) {
                resolution = 320;
                bitrate = 600000;
            } else {
                resolution = 240;
                bitrate = 400000;
            }
            this.videoFile = outputFile;
            this.videoWidth = resolution;
            this.videoHeight = resolution;
            this.videoBitrate = bitrate;
            this.sharedEglContext = sharedContext;
            synchronized (this.sync) {
                if (this.running) {
                    return;
                }
                this.running = true;
                Thread thread = new Thread(this, "TextureMovieEncoder");
                thread.setPriority(10);
                thread.start();
                while (!this.ready) {
                    try {
                        this.sync.wait();
                    } catch (InterruptedException e) {
                    }
                }
                this.handler.sendMessage(this.handler.obtainMessage(0));
            }
        }

        public void stopRecording(int send) {
            this.handler.sendMessage(this.handler.obtainMessage(1, send, 0));
        }

        /* JADX WARNING: Missing block: B:7:0x000a, code:
            r0 = r10.getTimestamp();
     */
        /* JADX WARNING: Missing block: B:8:0x0012, code:
            if (r0 != 0) goto L_0x0040;
     */
        /* JADX WARNING: Missing block: B:9:0x0014, code:
            r9.zeroTimeStamps++;
     */
        /* JADX WARNING: Missing block: B:10:0x001d, code:
            if (r9.zeroTimeStamps <= 1) goto L_?;
     */
        /* JADX WARNING: Missing block: B:12:0x0021, code:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0029;
     */
        /* JADX WARNING: Missing block: B:13:0x0023, code:
            org.telegram.messenger.FileLog.d("fix timestamp enabled");
     */
        /* JADX WARNING: Missing block: B:14:0x0029, code:
            r0 = r12;
     */
        /* JADX WARNING: Missing block: B:15:0x002a, code:
            r9.handler.sendMessage(r9.handler.obtainMessage(2, (int) (r0 >> 32), (int) r0, r11));
     */
        /* JADX WARNING: Missing block: B:20:0x0040, code:
            r9.zeroTimeStamps = 0;
     */
        /* JADX WARNING: Missing block: B:25:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:26:?, code:
            return;
     */
        public void frameAvailable(android.graphics.SurfaceTexture r10, java.lang.Integer r11, long r12) {
            /*
            r9 = this;
            r3 = r9.sync;
            monitor-enter(r3);
            r2 = r9.ready;	 Catch:{ all -> 0x003d }
            if (r2 != 0) goto L_0x0009;
        L_0x0007:
            monitor-exit(r3);	 Catch:{ all -> 0x003d }
        L_0x0008:
            return;
        L_0x0009:
            monitor-exit(r3);	 Catch:{ all -> 0x003d }
            r0 = r10.getTimestamp();
            r2 = 0;
            r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r2 != 0) goto L_0x0040;
        L_0x0014:
            r2 = r9.zeroTimeStamps;
            r2 = r2 + 1;
            r9.zeroTimeStamps = r2;
            r2 = r9.zeroTimeStamps;
            r3 = 1;
            if (r2 <= r3) goto L_0x0008;
        L_0x001f:
            r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
            if (r2 == 0) goto L_0x0029;
        L_0x0023:
            r2 = "fix timestamp enabled";
            org.telegram.messenger.FileLog.d(r2);
        L_0x0029:
            r0 = r12;
        L_0x002a:
            r2 = r9.handler;
            r3 = r9.handler;
            r4 = 2;
            r5 = 32;
            r6 = r0 >> r5;
            r5 = (int) r6;
            r6 = (int) r0;
            r3 = r3.obtainMessage(r4, r5, r6, r11);
            r2.sendMessage(r3);
            goto L_0x0008;
        L_0x003d:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x003d }
            throw r2;
        L_0x0040:
            r2 = 0;
            r9.zeroTimeStamps = r2;
            goto L_0x002a;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.frameAvailable(android.graphics.SurfaceTexture, java.lang.Integer, long):void");
        }

        public void run() {
            Looper.prepare();
            synchronized (this.sync) {
                this.handler = new EncoderHandler(this);
                this.ready = true;
                this.sync.notify();
            }
            Looper.loop();
            synchronized (this.sync) {
                this.ready = false;
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:94:0x0144 A:{SYNTHETIC, EDGE_INSN: B:94:0x0144->B:34:0x0144 ?: BREAK  } */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x008a  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x008a  */
        /* JADX WARNING: Removed duplicated region for block: B:94:0x0144 A:{SYNTHETIC, EDGE_INSN: B:94:0x0144->B:34:0x0144 ?: BREAK  } */
        private void handleAudioFrameAvailable(org.telegram.ui.Components.InstantCameraView.AudioBufferInfo r21) {
            /*
            r20 = this;
            r0 = r20;
            r2 = r0.audioStopedByTime;
            if (r2 == 0) goto L_0x0007;
        L_0x0006:
            return;
        L_0x0007:
            r0 = r20;
            r2 = r0.buffersToWrite;
            r0 = r21;
            r2.add(r0);
            r0 = r20;
            r4 = r0.audioFirst;
            r6 = -1;
            r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r2 != 0) goto L_0x0144;
        L_0x001a:
            r0 = r20;
            r4 = r0.videoFirst;
            r6 = -1;
            r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r2 != 0) goto L_0x002f;
        L_0x0024:
            r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
            if (r2 == 0) goto L_0x0006;
        L_0x0028:
            r2 = "video record not yet started";
            org.telegram.messenger.FileLog.d(r2);
            goto L_0x0006;
        L_0x002f:
            r14 = 0;
            r9 = 0;
        L_0x0031:
            r0 = r21;
            r2 = r0.results;
            if (r9 >= r2) goto L_0x0088;
        L_0x0037:
            if (r9 != 0) goto L_0x00c9;
        L_0x0039:
            r0 = r20;
            r4 = r0.videoFirst;
            r0 = r21;
            r2 = r0.offset;
            r6 = r2[r9];
            r4 = r4 - r6;
            r4 = java.lang.Math.abs(r4);
            r6 = 10000000; // 0x989680 float:1.4012985E-38 double:4.9406565E-317;
            r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r2 <= 0) goto L_0x00c9;
        L_0x004f:
            r0 = r20;
            r4 = r0.videoFirst;
            r0 = r21;
            r2 = r0.offset;
            r6 = r2[r9];
            r4 = r4 - r6;
            r0 = r20;
            r0.desyncTime = r4;
            r0 = r21;
            r2 = r0.offset;
            r4 = r2[r9];
            r0 = r20;
            r0.audioFirst = r4;
            r14 = 1;
            r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
            if (r2 == 0) goto L_0x0088;
        L_0x006d:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r4 = "detected desync between audio and video ";
            r2 = r2.append(r4);
            r0 = r20;
            r4 = r0.desyncTime;
            r2 = r2.append(r4);
            r2 = r2.toString();
            org.telegram.messenger.FileLog.d(r2);
        L_0x0088:
            if (r14 != 0) goto L_0x0144;
        L_0x008a:
            r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
            if (r2 == 0) goto L_0x00a9;
        L_0x008e:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r4 = "first audio frame not found, removing buffers ";
            r2 = r2.append(r4);
            r0 = r21;
            r4 = r0.results;
            r2 = r2.append(r4);
            r2 = r2.toString();
            org.telegram.messenger.FileLog.d(r2);
        L_0x00a9:
            r0 = r20;
            r2 = r0.buffersToWrite;
            r0 = r21;
            r2.remove(r0);
            r0 = r20;
            r2 = r0.buffersToWrite;
            r2 = r2.isEmpty();
            if (r2 != 0) goto L_0x0006;
        L_0x00bc:
            r0 = r20;
            r2 = r0.buffersToWrite;
            r4 = 0;
            r21 = r2.get(r4);
            r21 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r21;
            goto L_0x002f;
        L_0x00c9:
            r0 = r21;
            r2 = r0.offset;
            r4 = r2[r9];
            r0 = r20;
            r6 = r0.videoFirst;
            r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r2 < 0) goto L_0x0114;
        L_0x00d7:
            r0 = r21;
            r0.lastWroteBuffer = r9;
            r0 = r21;
            r2 = r0.offset;
            r4 = r2[r9];
            r0 = r20;
            r0.audioFirst = r4;
            r14 = 1;
            r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
            if (r2 == 0) goto L_0x0088;
        L_0x00ea:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r4 = "found first audio frame at ";
            r2 = r2.append(r4);
            r2 = r2.append(r9);
            r4 = " timestamp = ";
            r2 = r2.append(r4);
            r0 = r21;
            r4 = r0.offset;
            r4 = r4[r9];
            r2 = r2.append(r4);
            r2 = r2.toString();
            org.telegram.messenger.FileLog.d(r2);
            goto L_0x0088;
        L_0x0114:
            r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
            if (r2 == 0) goto L_0x0140;
        L_0x0118:
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r4 = "ignore first audio frame at ";
            r2 = r2.append(r4);
            r2 = r2.append(r9);
            r4 = " timestamp = ";
            r2 = r2.append(r4);
            r0 = r21;
            r4 = r0.offset;
            r4 = r4[r9];
            r2 = r2.append(r4);
            r2 = r2.toString();
            org.telegram.messenger.FileLog.d(r2);
        L_0x0140:
            r9 = r9 + 1;
            goto L_0x0031;
        L_0x0144:
            r0 = r20;
            r4 = r0.audioStartTime;
            r6 = -1;
            r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r2 != 0) goto L_0x015c;
        L_0x014e:
            r0 = r21;
            r2 = r0.offset;
            r0 = r21;
            r4 = r0.lastWroteBuffer;
            r4 = r2[r4];
            r0 = r20;
            r0.audioStartTime = r4;
        L_0x015c:
            r0 = r20;
            r2 = r0.buffersToWrite;
            r2 = r2.size();
            r4 = 1;
            if (r2 <= r4) goto L_0x0172;
        L_0x0167:
            r0 = r20;
            r2 = r0.buffersToWrite;
            r4 = 0;
            r21 = r2.get(r4);
            r21 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r21;
        L_0x0172:
            r2 = 0;
            r0 = r20;
            r0.drainEncoder(r2);	 Catch:{ Exception -> 0x0229 }
        L_0x0178:
            r13 = 0;
        L_0x0179:
            if (r21 == 0) goto L_0x0006;
        L_0x017b:
            r0 = r20;
            r2 = r0.audioEncoder;	 Catch:{ Throwable -> 0x0223 }
            r4 = 0;
            r3 = r2.dequeueInputBuffer(r4);	 Catch:{ Throwable -> 0x0223 }
            if (r3 < 0) goto L_0x0179;
        L_0x0187:
            r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0223 }
            r4 = 21;
            if (r2 < r4) goto L_0x022f;
        L_0x018d:
            r0 = r20;
            r2 = r0.audioEncoder;	 Catch:{ Throwable -> 0x0223 }
            r11 = r2.getInputBuffer(r3);	 Catch:{ Throwable -> 0x0223 }
        L_0x0195:
            r0 = r21;
            r2 = r0.offset;	 Catch:{ Throwable -> 0x0223 }
            r0 = r21;
            r4 = r0.lastWroteBuffer;	 Catch:{ Throwable -> 0x0223 }
            r16 = r2[r4];	 Catch:{ Throwable -> 0x0223 }
            r0 = r21;
            r9 = r0.lastWroteBuffer;	 Catch:{ Throwable -> 0x0223 }
        L_0x01a3:
            r0 = r21;
            r2 = r0.results;	 Catch:{ Throwable -> 0x0223 }
            if (r9 > r2) goto L_0x020a;
        L_0x01a9:
            r0 = r21;
            r2 = r0.results;	 Catch:{ Throwable -> 0x0223 }
            if (r9 >= r2) goto L_0x0260;
        L_0x01af:
            r0 = r20;
            r2 = r0.running;	 Catch:{ Throwable -> 0x0223 }
            if (r2 != 0) goto L_0x023e;
        L_0x01b5:
            r0 = r21;
            r2 = r0.offset;	 Catch:{ Throwable -> 0x0223 }
            r4 = r2[r9];	 Catch:{ Throwable -> 0x0223 }
            r0 = r20;
            r6 = r0.videoLast;	 Catch:{ Throwable -> 0x0223 }
            r0 = r20;
            r0 = r0.desyncTime;	 Catch:{ Throwable -> 0x0223 }
            r18 = r0;
            r6 = r6 - r18;
            r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r2 < 0) goto L_0x023e;
        L_0x01cb:
            r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Throwable -> 0x0223 }
            if (r2 == 0) goto L_0x01fb;
        L_0x01cf:
            r2 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0223 }
            r2.<init>();	 Catch:{ Throwable -> 0x0223 }
            r4 = "stop audio encoding because of stoped video recording at ";
            r2 = r2.append(r4);	 Catch:{ Throwable -> 0x0223 }
            r0 = r21;
            r4 = r0.offset;	 Catch:{ Throwable -> 0x0223 }
            r4 = r4[r9];	 Catch:{ Throwable -> 0x0223 }
            r2 = r2.append(r4);	 Catch:{ Throwable -> 0x0223 }
            r4 = " last video ";
            r2 = r2.append(r4);	 Catch:{ Throwable -> 0x0223 }
            r0 = r20;
            r4 = r0.videoLast;	 Catch:{ Throwable -> 0x0223 }
            r2 = r2.append(r4);	 Catch:{ Throwable -> 0x0223 }
            r2 = r2.toString();	 Catch:{ Throwable -> 0x0223 }
            org.telegram.messenger.FileLog.d(r2);	 Catch:{ Throwable -> 0x0223 }
        L_0x01fb:
            r2 = 1;
            r0 = r20;
            r0.audioStopedByTime = r2;	 Catch:{ Throwable -> 0x0223 }
            r13 = 1;
            r21 = 0;
            r0 = r20;
            r2 = r0.buffersToWrite;	 Catch:{ Throwable -> 0x0223 }
            r2.clear();	 Catch:{ Throwable -> 0x0223 }
        L_0x020a:
            r0 = r20;
            r2 = r0.audioEncoder;	 Catch:{ Throwable -> 0x0223 }
            r4 = 0;
            r5 = r11.position();	 Catch:{ Throwable -> 0x0223 }
            r6 = 0;
            r6 = (r16 > r6 ? 1 : (r16 == r6 ? 0 : -1));
            if (r6 != 0) goto L_0x02a4;
        L_0x0219:
            r6 = 0;
        L_0x021b:
            if (r13 == 0) goto L_0x02ac;
        L_0x021d:
            r8 = 4;
        L_0x021e:
            r2.queueInputBuffer(r3, r4, r5, r6, r8);	 Catch:{ Throwable -> 0x0223 }
            goto L_0x0179;
        L_0x0223:
            r10 = move-exception;
            org.telegram.messenger.FileLog.e(r10);
            goto L_0x0006;
        L_0x0229:
            r10 = move-exception;
            org.telegram.messenger.FileLog.e(r10);
            goto L_0x0178;
        L_0x022f:
            r0 = r20;
            r2 = r0.audioEncoder;	 Catch:{ Throwable -> 0x0223 }
            r12 = r2.getInputBuffers();	 Catch:{ Throwable -> 0x0223 }
            r11 = r12[r3];	 Catch:{ Throwable -> 0x0223 }
            r11.clear();	 Catch:{ Throwable -> 0x0223 }
            goto L_0x0195;
        L_0x023e:
            r2 = r11.remaining();	 Catch:{ Throwable -> 0x0223 }
            r0 = r21;
            r4 = r0.read;	 Catch:{ Throwable -> 0x0223 }
            r4 = r4[r9];	 Catch:{ Throwable -> 0x0223 }
            if (r2 >= r4) goto L_0x0251;
        L_0x024a:
            r0 = r21;
            r0.lastWroteBuffer = r9;	 Catch:{ Throwable -> 0x0223 }
            r21 = 0;
            goto L_0x020a;
        L_0x0251:
            r0 = r21;
            r2 = r0.buffer;	 Catch:{ Throwable -> 0x0223 }
            r4 = r9 * 2048;
            r0 = r21;
            r5 = r0.read;	 Catch:{ Throwable -> 0x0223 }
            r5 = r5[r9];	 Catch:{ Throwable -> 0x0223 }
            r11.put(r2, r4, r5);	 Catch:{ Throwable -> 0x0223 }
        L_0x0260:
            r0 = r21;
            r2 = r0.results;	 Catch:{ Throwable -> 0x0223 }
            r2 = r2 + -1;
            if (r9 < r2) goto L_0x0298;
        L_0x0268:
            r0 = r20;
            r2 = r0.buffersToWrite;	 Catch:{ Throwable -> 0x0223 }
            r0 = r21;
            r2.remove(r0);	 Catch:{ Throwable -> 0x0223 }
            r0 = r20;
            r2 = r0.running;	 Catch:{ Throwable -> 0x0223 }
            if (r2 == 0) goto L_0x0280;
        L_0x0277:
            r0 = r20;
            r2 = r0.buffers;	 Catch:{ Throwable -> 0x0223 }
            r0 = r21;
            r2.put(r0);	 Catch:{ Throwable -> 0x0223 }
        L_0x0280:
            r0 = r20;
            r2 = r0.buffersToWrite;	 Catch:{ Throwable -> 0x0223 }
            r2 = r2.isEmpty();	 Catch:{ Throwable -> 0x0223 }
            if (r2 != 0) goto L_0x029c;
        L_0x028a:
            r0 = r20;
            r2 = r0.buffersToWrite;	 Catch:{ Throwable -> 0x0223 }
            r4 = 0;
            r2 = r2.get(r4);	 Catch:{ Throwable -> 0x0223 }
            r0 = r2;
            r0 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r0;	 Catch:{ Throwable -> 0x0223 }
            r21 = r0;
        L_0x0298:
            r9 = r9 + 1;
            goto L_0x01a3;
        L_0x029c:
            r0 = r21;
            r13 = r0.last;	 Catch:{ Throwable -> 0x0223 }
            r21 = 0;
            goto L_0x020a;
        L_0x02a4:
            r0 = r20;
            r6 = r0.audioStartTime;	 Catch:{ Throwable -> 0x0223 }
            r6 = r16 - r6;
            goto L_0x021b;
        L_0x02ac:
            r8 = 0;
            goto L_0x021e;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.InstantCameraView.VideoRecorder.handleAudioFrameAvailable(org.telegram.ui.Components.InstantCameraView$AudioBufferInfo):void");
        }

        private void handleVideoFrameAvailable(long timestampNanos, Integer cameraId) {
            long dt;
            long alphaDt;
            try {
                drainEncoder(false);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (!this.lastCameraId.equals(cameraId)) {
                this.lastTimestamp = -1;
                this.lastCameraId = cameraId;
            }
            if (this.lastTimestamp == -1) {
                this.lastTimestamp = timestampNanos;
                if (this.currentTimestamp != 0) {
                    dt = (System.currentTimeMillis() - this.lastCommitedFrameTime) * 1000000;
                    alphaDt = 0;
                } else {
                    dt = 0;
                    alphaDt = 0;
                }
            } else {
                dt = timestampNanos - this.lastTimestamp;
                alphaDt = dt;
                this.lastTimestamp = timestampNanos;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (!this.skippedFirst) {
                this.skippedTime += dt;
                if (this.skippedTime >= NUM) {
                    this.skippedFirst = true;
                } else {
                    return;
                }
            }
            this.currentTimestamp += dt;
            if (this.videoFirst == -1) {
                this.videoFirst = timestampNanos / 1000;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("first video frame was at " + this.videoFirst);
                }
            }
            this.videoLast = timestampNanos;
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, InstantCameraView.this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, InstantCameraView.this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniform1f(this.scaleXHandle, InstantCameraView.this.scaleX);
            GLES20.glUniform1f(this.scaleYHandle, InstantCameraView.this.scaleY);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, InstantCameraView.this.mMVPMatrix, 0);
            GLES20.glActiveTexture(33984);
            if (InstantCameraView.this.oldCameraTexture[0] != 0) {
                if (!this.blendEnabled) {
                    GLES20.glEnable(3042);
                    this.blendEnabled = true;
                }
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.moldSTMatrix, 0);
                GLES20.glUniform1f(this.alphaHandle, 1.0f);
                GLES20.glBindTexture(36197, InstantCameraView.this.oldCameraTexture[0]);
                GLES20.glDrawArrays(5, 0, 4);
            }
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, InstantCameraView.this.mSTMatrix, 0);
            GLES20.glUniform1f(this.alphaHandle, InstantCameraView.this.cameraTextureAlpha);
            GLES20.glBindTexture(36197, InstantCameraView.this.cameraTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glDisableVertexAttribArray(this.positionHandle);
            GLES20.glDisableVertexAttribArray(this.textureHandle);
            GLES20.glBindTexture(36197, 0);
            GLES20.glUseProgram(0);
            EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, this.currentTimestamp);
            EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
            if (InstantCameraView.this.oldCameraTexture[0] != 0 && InstantCameraView.this.cameraTextureAlpha < 1.0f) {
                InstantCameraView.this.cameraTextureAlpha = InstantCameraView.this.cameraTextureAlpha + (((float) alphaDt) / 2.0E8f);
                if (InstantCameraView.this.cameraTextureAlpha > 1.0f) {
                    GLES20.glDisable(3042);
                    this.blendEnabled = false;
                    InstantCameraView.this.cameraTextureAlpha = 1.0f;
                    GLES20.glDeleteTextures(1, InstantCameraView.this.oldCameraTexture, 0);
                    InstantCameraView.this.oldCameraTexture[0] = 0;
                    if (!InstantCameraView.this.cameraReady) {
                        InstantCameraView.this.cameraReady = true;
                        AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$Lambda$0(this));
                    }
                }
            } else if (!InstantCameraView.this.cameraReady) {
                InstantCameraView.this.cameraReady = true;
                AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$Lambda$1(this));
            }
        }

        final /* synthetic */ void lambda$handleVideoFrameAvailable$0$InstantCameraView$VideoRecorder() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        final /* synthetic */ void lambda$handleVideoFrameAvailable$1$InstantCameraView$VideoRecorder() {
            InstantCameraView.this.textureOverlayView.animate().setDuration(120).alpha(0.0f).setInterpolator(new DecelerateInterpolator()).start();
        }

        private void handleStopRecording(int send) {
            if (this.running) {
                this.sendWhenDone = send;
                this.running = false;
                return;
            }
            try {
                drainEncoder(true);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (this.videoEncoder != null) {
                try {
                    this.videoEncoder.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
            if (this.audioEncoder != null) {
                try {
                    this.audioEncoder.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Throwable e22) {
                    FileLog.e(e22);
                }
            }
            if (this.mediaMuxer != null) {
                try {
                    this.mediaMuxer.finishMovie();
                } catch (Throwable e222) {
                    FileLog.e(e222);
                }
            }
            if (send != 0) {
                AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$Lambda$2(this, send));
            } else {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).cancelUploadFile(this.videoFile.getAbsolutePath(), false);
                this.videoFile.delete();
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            if (this.surface != null) {
                this.surface.release();
                this.surface = null;
            }
            if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(this.eglDisplay);
            }
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            this.eglConfig = null;
            this.handler.exit();
        }

        final /* synthetic */ void lambda$handleStopRecording$2$InstantCameraView$VideoRecorder(int send) {
            InstantCameraView.this.videoEditedInfo = new VideoEditedInfo();
            InstantCameraView.this.videoEditedInfo.roundVideo = true;
            InstantCameraView.this.videoEditedInfo.startTime = -1;
            InstantCameraView.this.videoEditedInfo.endTime = -1;
            InstantCameraView.this.videoEditedInfo.file = InstantCameraView.this.file;
            InstantCameraView.this.videoEditedInfo.encryptedFile = InstantCameraView.this.encryptedFile;
            InstantCameraView.this.videoEditedInfo.key = InstantCameraView.this.key;
            InstantCameraView.this.videoEditedInfo.iv = InstantCameraView.this.iv;
            InstantCameraView.this.videoEditedInfo.estimatedSize = Math.max(1, InstantCameraView.this.size);
            InstantCameraView.this.videoEditedInfo.framerate = 25;
            VideoEditedInfo access$1000 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalWidth = 240;
            access$1000.resultWidth = 240;
            access$1000 = InstantCameraView.this.videoEditedInfo;
            InstantCameraView.this.videoEditedInfo.originalHeight = 240;
            access$1000.resultHeight = 240;
            InstantCameraView.this.videoEditedInfo.originalPath = this.videoFile.getAbsolutePath();
            if (send == 1) {
                InstantCameraView.this.baseFragment.sendMedia(new PhotoEntry(0, 0, 0, this.videoFile.getAbsolutePath(), 0, true), InstantCameraView.this.videoEditedInfo);
            } else {
                InstantCameraView.this.videoPlayer = new VideoPlayer();
                InstantCameraView.this.videoPlayer.setDelegate(new VideoPlayerDelegate() {
                    public void onStateChanged(boolean playWhenReady, int playbackState) {
                        long j = 0;
                        if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoPlayer.isPlaying() && playbackState == 4) {
                            VideoPlayer access$900 = InstantCameraView.this.videoPlayer;
                            if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                                j = InstantCameraView.this.videoEditedInfo.startTime;
                            }
                            access$900.seekTo(j);
                        }
                    }

                    public void onError(Exception e) {
                        FileLog.e((Throwable) e);
                    }

                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    }

                    public void onRenderedFirstFrame() {
                    }

                    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
                        return false;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    }
                });
                InstantCameraView.this.videoPlayer.setTextureView(InstantCameraView.this.textureView);
                InstantCameraView.this.videoPlayer.preparePlayer(Uri.fromFile(this.videoFile), "other");
                InstantCameraView.this.videoPlayer.play();
                InstantCameraView.this.videoPlayer.setMute(true);
                InstantCameraView.this.startProgressTimer();
                AnimatorSet animatorSet = new AnimatorSet();
                r1 = new Animator[3];
                r1[0] = ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "alpha", new float[]{0.0f});
                r1[1] = ObjectAnimator.ofInt(InstantCameraView.this.paint, "alpha", new int[]{0});
                r1[2] = ObjectAnimator.ofFloat(InstantCameraView.this.muteImageView, "alpha", new float[]{1.0f});
                animatorSet.playTogether(r1);
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.start();
                InstantCameraView.this.videoEditedInfo.estimatedDuration = InstantCameraView.this.duration;
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.audioDidSent, InstantCameraView.this.videoEditedInfo, this.videoFile.getAbsolutePath());
            }
            didWriteData(this.videoFile, true);
        }

        private void prepareEncoder() {
            try {
                int recordBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                if (recordBufferSize <= 0) {
                    recordBufferSize = 3584;
                }
                int bufferSize = 49152;
                if (49152 < recordBufferSize) {
                    bufferSize = (((recordBufferSize / 2048) + 1) * 2048) * 2;
                }
                for (int a = 0; a < 3; a++) {
                    this.buffers.add(new AudioBufferInfo(InstantCameraView.this, null));
                }
                this.audioRecorder = new AudioRecord(1, 44100, 16, 2, bufferSize);
                this.audioRecorder.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + bufferSize);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new BufferInfo();
                this.videoBufferInfo = new BufferInfo();
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString("mime", "audio/mp4a-latm");
                audioFormat.setInteger("aac-profile", 2);
                audioFormat.setInteger("sample-rate", 44100);
                audioFormat.setInteger("channel-count", 1);
                audioFormat.setInteger("bitrate", 32000);
                audioFormat.setInteger("max-input-size", 20480);
                this.audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder.configure(audioFormat, null, null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                format.setInteger("color-format", NUM);
                MediaFormat mediaFormat = format;
                mediaFormat.setInteger("bitrate", this.videoBitrate);
                format.setInteger("frame-rate", 30);
                format.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(format, null, null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie movie = new Mp4Movie();
                movie.setCacheFile(this.videoFile);
                movie.setRotation(0);
                movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(movie, InstantCameraView.this.isSecretChat);
                AndroidUtilities.runOnUIThread(new InstantCameraView$VideoRecorder$$Lambda$3(this));
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("EGL already set up");
                }
                this.eglDisplay = EGL14.eglGetDisplay(0);
                if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    throw new RuntimeException("unable to get EGL14 display");
                }
                int[] version = new int[2];
                if (EGL14.eglInitialize(this.eglDisplay, version, 0, version, 1)) {
                    if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                        android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
                        if (EGL14.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344}, 0, configs, 0, configs.length, new int[1], 0)) {
                            int[] iArr = new int[3];
                            this.eglContext = EGL14.eglCreateContext(this.eglDisplay, configs[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                            this.eglConfig = configs[0];
                        } else {
                            throw new RuntimeException("Unable to find a suitable EGLConfig");
                        }
                    }
                    EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, new int[1], 0);
                    if (this.eglSurface != EGL14.EGL_NO_SURFACE) {
                        throw new IllegalStateException("surface already created");
                    }
                    this.eglSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, new int[]{12344}, 0);
                    if (this.eglSurface == null) {
                        throw new RuntimeException("surface was null");
                    } else if (EGL14.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                        GLES20.glBlendFunc(770, 771);
                        int vertexShader = InstantCameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                        int fragmentShader = InstantCameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform float scaleX;\nuniform float scaleY;\nuniform float alpha;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   vec2 coord = vec2((vTextureCoord.x - 0.5) * scaleX, (vTextureCoord.y - 0.5) * scaleY);\n   float coef = ceil(clamp(0.2601 - dot(coord, coord), 0.0, 1.0));\n   vec3 color = texture2D(sTexture, vTextureCoord).rgb * coef + (1.0 - step(0.001, coef));\n   gl_FragColor = vec4(color * alpha, alpha);\n}\n");
                        if (vertexShader != 0 && fragmentShader != 0) {
                            this.drawProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.drawProgram, vertexShader);
                            GLES20.glAttachShader(this.drawProgram, fragmentShader);
                            GLES20.glLinkProgram(this.drawProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.drawProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] == 0) {
                                GLES20.glDeleteProgram(this.drawProgram);
                                this.drawProgram = 0;
                                return;
                            }
                            this.positionHandle = GLES20.glGetAttribLocation(this.drawProgram, "aPosition");
                            this.textureHandle = GLES20.glGetAttribLocation(this.drawProgram, "aTextureCoord");
                            this.scaleXHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleX");
                            this.scaleYHandle = GLES20.glGetUniformLocation(this.drawProgram, "scaleY");
                            this.alphaHandle = GLES20.glGetUniformLocation(this.drawProgram, "alpha");
                            this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                            this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                            return;
                        }
                        return;
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                        }
                        throw new RuntimeException("eglMakeCurrent failed");
                    }
                }
                this.eglDisplay = null;
                throw new RuntimeException("unable to initialize EGL14");
            } catch (Throwable ioe) {
                throw new RuntimeException(ioe);
            }
        }

        final /* synthetic */ void lambda$prepareEncoder$3$InstantCameraView$VideoRecorder() {
            if (!InstantCameraView.this.cancelled) {
                try {
                    InstantCameraView.this.performHapticFeedback(3, 2);
                } catch (Exception e) {
                }
                AndroidUtilities.lockOrientation(InstantCameraView.this.baseFragment.getParentActivity());
                InstantCameraView.this.recording = true;
                InstantCameraView.this.recordStartTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(InstantCameraView.this.timerRunnable);
                NotificationCenter.getInstance(InstantCameraView.this.currentAccount).postNotificationName(NotificationCenter.recordStarted, new Object[0]);
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        private void didWriteData(File file, boolean last) {
            long j = 0;
            FileLoader instance;
            String file2;
            boolean access$3700;
            long length;
            if (this.videoConvertFirstWrite) {
                FileLoader.getInstance(InstantCameraView.this.currentAccount).uploadFile(file.toString(), InstantCameraView.this.isSecretChat, false, 1, 33554432);
                this.videoConvertFirstWrite = false;
                if (last) {
                    instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
                    file2 = file.toString();
                    access$3700 = InstantCameraView.this.isSecretChat;
                    length = file.length();
                    if (last) {
                        j = file.length();
                    }
                    instance.checkUploadNewDataAvailable(file2, access$3700, length, j);
                    return;
                }
                return;
            }
            instance = FileLoader.getInstance(InstantCameraView.this.currentAccount);
            file2 = file.toString();
            access$3700 = InstantCameraView.this.isSecretChat;
            length = file.length();
            if (last) {
                j = file.length();
            }
            instance.checkUploadNewDataAvailable(file2, access$3700, length, j);
        }

        public void drainEncoder(boolean endOfStream) throws Exception {
            int encoderStatus;
            MediaFormat newFormat;
            ByteBuffer encodedData;
            if (endOfStream) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = null;
            if (VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
            }
            while (true) {
                encoderStatus = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000);
                if (encoderStatus == -1) {
                    if (!endOfStream) {
                        break;
                    }
                } else if (encoderStatus == -3) {
                    if (VERSION.SDK_INT < 21) {
                        encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus == -2) {
                    newFormat = this.videoEncoder.getOutputFormat();
                    if (this.videoTrackIndex == -5) {
                        this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                    }
                } else if (encoderStatus < 0) {
                    continue;
                } else {
                    if (VERSION.SDK_INT < 21) {
                        encodedData = encoderOutputBuffers[encoderStatus];
                    } else {
                        encodedData = this.videoEncoder.getOutputBuffer(encoderStatus);
                    }
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                    }
                    if (this.videoBufferInfo.size > 1) {
                        if ((this.videoBufferInfo.flags & 2) == 0) {
                            if (this.mediaMuxer.writeSampleData(this.videoTrackIndex, encodedData, this.videoBufferInfo, true)) {
                                didWriteData(this.videoFile, false);
                            }
                        } else if (this.videoTrackIndex == -5) {
                            byte[] csd = new byte[this.videoBufferInfo.size];
                            encodedData.limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
                            encodedData.position(this.videoBufferInfo.offset);
                            encodedData.get(csd);
                            ByteBuffer sps = null;
                            ByteBuffer pps = null;
                            int a = this.videoBufferInfo.size - 1;
                            while (a >= 0 && a > 3) {
                                if (csd[a] == (byte) 1 && csd[a - 1] == (byte) 0 && csd[a - 2] == (byte) 0 && csd[a - 3] == (byte) 0) {
                                    sps = ByteBuffer.allocate(a - 3);
                                    pps = ByteBuffer.allocate(this.videoBufferInfo.size - (a - 3));
                                    sps.put(csd, 0, a - 3).position(0);
                                    pps.put(csd, a - 3, this.videoBufferInfo.size - (a - 3)).position(0);
                                    break;
                                }
                                a--;
                            }
                            newFormat = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                            if (!(sps == null || pps == null)) {
                                newFormat.setByteBuffer("csd-0", sps);
                                newFormat.setByteBuffer("csd-1", pps);
                            }
                            this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                        }
                    }
                    this.videoEncoder.releaseOutputBuffer(encoderStatus, false);
                    if ((this.videoBufferInfo.flags & 4) != 0) {
                        break;
                    }
                }
            }
            if (VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                encoderStatus = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0);
                if (encoderStatus == -1) {
                    if (!endOfStream) {
                        return;
                    }
                    if (!this.running && this.sendWhenDone == 0) {
                        return;
                    }
                } else if (encoderStatus == -3) {
                    if (VERSION.SDK_INT < 21) {
                        encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus == -2) {
                    newFormat = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(newFormat, true);
                    }
                } else if (encoderStatus < 0) {
                    continue;
                } else {
                    if (VERSION.SDK_INT < 21) {
                        encodedData = encoderOutputBuffers[encoderStatus];
                    } else {
                        encodedData = this.audioEncoder.getOutputBuffer(encoderStatus);
                    }
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                    }
                    if ((this.audioBufferInfo.flags & 2) != 0) {
                        this.audioBufferInfo.size = 0;
                    }
                    if (this.audioBufferInfo.size != 0 && this.mediaMuxer.writeSampleData(this.audioTrackIndex, encodedData, this.audioBufferInfo, false)) {
                        didWriteData(this.videoFile, false);
                    }
                    this.audioEncoder.releaseOutputBuffer(encoderStatus, false);
                    if ((this.audioBufferInfo.flags & 4) != 0) {
                        return;
                    }
                }
            }
        }

        protected void finalize() throws Throwable {
            try {
                if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                    EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
                    EGL14.eglReleaseThread();
                    EGL14.eglTerminate(this.eglDisplay);
                    this.eglDisplay = EGL14.EGL_NO_DISPLAY;
                    this.eglContext = EGL14.EGL_NO_CONTEXT;
                    this.eglConfig = null;
                }
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
            }
        }
    }

    public InstantCameraView(Context context, ChatActivity parentFragment) {
        Size size;
        super(context);
        if (SharedConfig.roundCamera16to9) {
            size = new Size(16, 9);
        } else {
            size = new Size(4, 3);
        }
        this.aspectRatio = size;
        this.mMVPMatrix = new float[16];
        this.mSTMatrix = new float[16];
        this.moldSTMatrix = new float[16];
        setOnTouchListener(new InstantCameraView$$Lambda$0(this));
        setWillNotDraw(false);
        setBackgroundColor(-NUM);
        this.baseFragment = parentFragment;
        this.isSecretChat = this.baseFragment.getCurrentEncryptedChat() != null;
        this.paint = new Paint(1) {
            public void setAlpha(int a) {
                super.setAlpha(a);
                InstantCameraView.this.invalidate();
            }
        };
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setColor(-1);
        this.rect = new RectF();
        if (VERSION.SDK_INT >= 21) {
            this.cameraContainer = new FrameLayout(context) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    InstantCameraView.this.invalidate();
                }
            };
            this.cameraContainer.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                }
            });
            this.cameraContainer.setClipToOutline(true);
            this.cameraContainer.setWillNotDraw(false);
        } else {
            final Path path = new Path();
            final Paint paint = new Paint(1);
            paint.setColor(-16777216);
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            this.cameraContainer = new FrameLayout(context) {
                public void setScaleX(float scaleX) {
                    super.setScaleX(scaleX);
                    InstantCameraView.this.invalidate();
                }

                protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    path.reset();
                    path.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Direction.CW);
                    path.toggleInverseFillType();
                }

                protected void dispatchDraw(Canvas canvas) {
                    try {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(path, paint);
                    } catch (Exception e) {
                    }
                }
            };
            this.cameraContainer.setWillNotDraw(false);
            this.cameraContainer.setLayerType(2, null);
        }
        addView(this.cameraContainer, new LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize, 17));
        this.switchCameraButton = new ImageView(context);
        this.switchCameraButton.setScaleType(ScaleType.CENTER);
        addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48.0f, 83, 20.0f, 0.0f, 0.0f, 14.0f));
        this.switchCameraButton.setOnClickListener(new InstantCameraView$$Lambda$1(this));
        this.muteImageView = new ImageView(context);
        this.muteImageView.setScaleType(ScaleType.CENTER);
        this.muteImageView.setImageResource(R.drawable.video_mute);
        this.muteImageView.setAlpha(0.0f);
        addView(this.muteImageView, LayoutHelper.createFrame(48, 48, 17));
        ((LayoutParams) this.muteImageView.getLayoutParams()).topMargin = (AndroidUtilities.roundMessageSize / 2) - AndroidUtilities.dp(24.0f);
        this.textureOverlayView = new BackupImageView(getContext());
        this.textureOverlayView.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        addView(this.textureOverlayView, new LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize, 17));
        setVisibility(4);
    }

    final /* synthetic */ boolean lambda$new$0$InstantCameraView(View v, MotionEvent event) {
        float f = 1.0f;
        if (event.getAction() == 0 && this.baseFragment != null) {
            if (this.videoPlayer != null) {
                float f2;
                boolean mute = !this.videoPlayer.isMuted();
                this.videoPlayer.setMute(mute);
                if (this.muteAnimation != null) {
                    this.muteAnimation.cancel();
                }
                this.muteAnimation = new AnimatorSet();
                AnimatorSet animatorSet = this.muteAnimation;
                Animator[] animatorArr = new Animator[3];
                ImageView imageView = this.muteImageView;
                String str = "alpha";
                float[] fArr = new float[1];
                fArr[0] = mute ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(imageView, str, fArr);
                imageView = this.muteImageView;
                str = "scaleX";
                fArr = new float[1];
                if (mute) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.5f;
                }
                fArr[0] = f2;
                animatorArr[1] = ObjectAnimator.ofFloat(imageView, str, fArr);
                imageView = this.muteImageView;
                str = "scaleY";
                fArr = new float[1];
                if (!mute) {
                    f = 0.5f;
                }
                fArr[0] = f;
                animatorArr[2] = ObjectAnimator.ofFloat(imageView, str, fArr);
                animatorSet.playTogether(animatorArr);
                this.muteAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(InstantCameraView.this.muteAnimation)) {
                            InstantCameraView.this.muteAnimation = null;
                        }
                    }
                });
                this.muteAnimation.setDuration(180);
                this.muteAnimation.setInterpolator(new DecelerateInterpolator());
                this.muteAnimation.start();
            } else {
                this.baseFragment.checkRecordLocked();
            }
        }
        return true;
    }

    final /* synthetic */ void lambda$new$1$InstantCameraView(View v) {
        if (this.cameraReady && this.cameraSession != null && this.cameraSession.isInitied() && this.cameraThread != null) {
            switchCamera();
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    InstantCameraView.this.switchCameraButton.setImageResource(InstantCameraView.this.isFrontface ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                    ObjectAnimator.ofFloat(InstantCameraView.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
                }
            });
            animator.start();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getVisibility() != 0) {
            this.cameraContainer.setTranslationY((float) (getMeasuredHeight() / 2));
            this.textureOverlayView.setTranslationY((float) (getMeasuredHeight() / 2));
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.recordProgressChanged) {
            long t = ((Long) args[0]).longValue();
            this.progress = ((float) t) / 60000.0f;
            this.recordedTime = t;
            invalidate();
        } else if (id == NotificationCenter.FileDidUpload) {
            String location = args[0];
            if (this.cameraFile != null && this.cameraFile.getAbsolutePath().equals(location)) {
                this.file = (InputFile) args[1];
                this.encryptedFile = (InputEncryptedFile) args[2];
                this.size = ((Long) args[5]).longValue();
                if (this.encryptedFile != null) {
                    this.key = (byte[]) args[3];
                    this.iv = (byte[]) args[4];
                }
            }
        }
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
        }
    }

    protected void onDraw(Canvas canvas) {
        float x = this.cameraContainer.getX();
        float y = this.cameraContainer.getY();
        this.rect.set(x - ((float) AndroidUtilities.dp(8.0f)), y - ((float) AndroidUtilities.dp(8.0f)), (((float) this.cameraContainer.getMeasuredWidth()) + x) + ((float) AndroidUtilities.dp(8.0f)), (((float) this.cameraContainer.getMeasuredHeight()) + y) + ((float) AndroidUtilities.dp(8.0f)));
        if (this.progress != 0.0f) {
            canvas.drawArc(this.rect, -90.0f, this.progress * 360.0f, false, this.paint);
        }
        if (Theme.chat_roundVideoShadow != null) {
            int x1 = ((int) x) - AndroidUtilities.dp(3.0f);
            int y1 = ((int) y) - AndroidUtilities.dp(2.0f);
            canvas.save();
            canvas.scale(this.cameraContainer.getScaleX(), this.cameraContainer.getScaleY(), (float) (((AndroidUtilities.roundMessageSize / 2) + x1) + AndroidUtilities.dp(3.0f)), (float) (((AndroidUtilities.roundMessageSize / 2) + y1) + AndroidUtilities.dp(3.0f)));
            Theme.chat_roundVideoShadow.setAlpha((int) (this.cameraContainer.getAlpha() * 255.0f));
            Theme.chat_roundVideoShadow.setBounds(x1, y1, (AndroidUtilities.roundMessageSize + x1) + AndroidUtilities.dp(6.0f), (AndroidUtilities.roundMessageSize + y1) + AndroidUtilities.dp(6.0f));
            Theme.chat_roundVideoShadow.draw(canvas);
            canvas.restore();
        }
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        setAlpha(0.0f);
        this.switchCameraButton.setAlpha(0.0f);
        this.cameraContainer.setAlpha(0.0f);
        this.textureOverlayView.setAlpha(0.0f);
        this.muteImageView.setAlpha(0.0f);
        this.muteImageView.setScaleX(1.0f);
        this.muteImageView.setScaleY(1.0f);
        this.cameraContainer.setScaleX(0.1f);
        this.cameraContainer.setScaleY(0.1f);
        this.textureOverlayView.setScaleX(0.1f);
        this.textureOverlayView.setScaleY(0.1f);
        if (this.cameraContainer.getMeasuredWidth() != 0) {
            this.cameraContainer.setPivotX((float) (this.cameraContainer.getMeasuredWidth() / 2));
            this.cameraContainer.setPivotY((float) (this.cameraContainer.getMeasuredHeight() / 2));
            this.textureOverlayView.setPivotX((float) (this.textureOverlayView.getMeasuredWidth() / 2));
            this.textureOverlayView.setPivotY((float) (this.textureOverlayView.getMeasuredHeight() / 2));
        }
        if (visibility == 0) {
            try {
                ((Activity) getContext()).getWindow().addFlags(128);
                return;
            } catch (Throwable e) {
                FileLog.e(e);
                return;
            }
        }
        ((Activity) getContext()).getWindow().clearFlags(128);
    }

    public void showCamera() {
        if (this.textureView == null) {
            this.switchCameraButton.setImageResource(R.drawable.camera_revert1);
            this.textureOverlayView.setAlpha(1.0f);
            if (this.lastBitmap == null) {
                try {
                    this.lastBitmap = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg").getAbsolutePath());
                } catch (Throwable th) {
                }
            }
            if (this.lastBitmap != null) {
                this.textureOverlayView.setImageBitmap(this.lastBitmap);
            } else {
                this.textureOverlayView.setImageResource(R.drawable.icplaceholder);
            }
            this.cameraReady = false;
            this.isFrontface = true;
            this.selectedCamera = null;
            this.recordedTime = 0;
            this.progress = 0.0f;
            this.cancelled = false;
            this.file = null;
            this.encryptedFile = null;
            this.key = null;
            this.iv = null;
            if (initCamera()) {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(MediaController.getInstance().getPlayingMessageObject());
                this.cameraFile = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + ".mp4");
                SharedConfig.saveConfig();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("show round camera");
                }
                this.textureView = new TextureView(getContext());
                this.textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("camera surface available");
                        }
                        if (InstantCameraView.this.cameraThread == null && surface != null && !InstantCameraView.this.cancelled) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("start create thread");
                            }
                            InstantCameraView.this.cameraThread = new CameraGLThread(surface, width, height);
                        }
                    }

                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    }

                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                        if (InstantCameraView.this.cameraThread != null) {
                            InstantCameraView.this.cameraThread.shutdown(0);
                            InstantCameraView.this.cameraThread = null;
                        }
                        if (InstantCameraView.this.cameraSession != null) {
                            CameraController.getInstance().close(InstantCameraView.this.cameraSession, null, null);
                        }
                        return true;
                    }

                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    }
                });
                this.cameraContainer.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
                setVisibility(0);
                startAnimation(true);
            }
        }
    }

    public FrameLayout getCameraContainer() {
        return this.cameraContainer;
    }

    public void startAnimation(boolean open) {
        float f;
        int i;
        float f2 = 1.0f;
        float f3 = 0.0f;
        if (this.animatorSet != null) {
            this.animatorSet.cancel();
        }
        PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
        if (pipRoundVideoView != null) {
            pipRoundVideoView.showTemporary(!open);
        }
        this.animatorSet = new AnimatorSet();
        AnimatorSet animatorSet = this.animatorSet;
        Animator[] animatorArr = new Animator[12];
        String str = "alpha";
        float[] fArr = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr[0] = f;
        animatorArr[0] = ObjectAnimator.ofFloat(this, str, fArr);
        ImageView imageView = this.switchCameraButton;
        String str2 = "alpha";
        float[] fArr2 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr2[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(imageView, str2, fArr2);
        animatorArr[2] = ObjectAnimator.ofFloat(this.muteImageView, "alpha", new float[]{0.0f});
        Paint paint = this.paint;
        String str3 = "alpha";
        int[] iArr = new int[1];
        if (open) {
            i = 255;
        } else {
            i = 0;
        }
        iArr[0] = i;
        animatorArr[3] = ObjectAnimator.ofInt(paint, str3, iArr);
        FrameLayout frameLayout = this.cameraContainer;
        str3 = "alpha";
        float[] fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        fArr3[0] = f;
        animatorArr[4] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        frameLayout = this.cameraContainer;
        str3 = "scaleX";
        fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.1f;
        }
        fArr3[0] = f;
        animatorArr[5] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        frameLayout = this.cameraContainer;
        str3 = "scaleY";
        fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.1f;
        }
        fArr3[0] = f;
        animatorArr[6] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        frameLayout = this.cameraContainer;
        str3 = "translationY";
        fArr3 = new float[2];
        if (open) {
            f = (float) (getMeasuredHeight() / 2);
        } else {
            f = 0.0f;
        }
        fArr3[0] = f;
        fArr3[1] = open ? 0.0f : (float) (getMeasuredHeight() / 2);
        animatorArr[7] = ObjectAnimator.ofFloat(frameLayout, str3, fArr3);
        BackupImageView backupImageView = this.textureOverlayView;
        str3 = "alpha";
        fArr3 = new float[1];
        fArr3[0] = open ? 1.0f : 0.0f;
        animatorArr[8] = ObjectAnimator.ofFloat(backupImageView, str3, fArr3);
        backupImageView = this.textureOverlayView;
        str3 = "scaleX";
        fArr3 = new float[1];
        if (open) {
            f = 1.0f;
        } else {
            f = 0.1f;
        }
        fArr3[0] = f;
        animatorArr[9] = ObjectAnimator.ofFloat(backupImageView, str3, fArr3);
        BackupImageView backupImageView2 = this.textureOverlayView;
        str2 = "scaleY";
        fArr2 = new float[1];
        if (!open) {
            f2 = 0.1f;
        }
        fArr2[0] = f2;
        animatorArr[10] = ObjectAnimator.ofFloat(backupImageView2, str2, fArr2);
        BackupImageView backupImageView3 = this.textureOverlayView;
        str = "translationY";
        fArr = new float[2];
        if (open) {
            f = (float) (getMeasuredHeight() / 2);
        } else {
            f = 0.0f;
        }
        fArr[0] = f;
        if (!open) {
            f3 = (float) (getMeasuredHeight() / 2);
        }
        fArr[1] = f3;
        animatorArr[11] = ObjectAnimator.ofFloat(backupImageView3, str, fArr);
        animatorSet.playTogether(animatorArr);
        if (!open) {
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(InstantCameraView.this.animatorSet)) {
                        InstantCameraView.this.hideCamera(true);
                        InstantCameraView.this.setVisibility(4);
                    }
                }
            });
        }
        this.animatorSet.setDuration(180);
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.start();
    }

    public Rect getCameraRect() {
        this.cameraContainer.getLocationOnScreen(this.position);
        return new Rect((float) this.position[0], (float) this.position[1], (float) this.cameraContainer.getWidth(), (float) this.cameraContainer.getHeight());
    }

    public void changeVideoPreviewState(int state, float progress) {
        if (this.videoPlayer != null) {
            if (state == 0) {
                startProgressTimer();
                this.videoPlayer.play();
            } else if (state == 1) {
                stopProgressTimer();
                this.videoPlayer.pause();
            } else if (state == 2) {
                this.videoPlayer.seekTo((long) (((float) this.videoPlayer.getDuration()) * progress));
            }
        }
    }

    public void send(int state) {
        if (this.textureView != null) {
            stopProgressTimer();
            if (this.videoPlayer != null) {
                this.videoPlayer.releasePlayer();
                this.videoPlayer = null;
            }
            if (state == 4) {
                if (this.videoEditedInfo.needConvert()) {
                    long endTime;
                    VideoEditedInfo videoEditedInfo;
                    this.file = null;
                    this.encryptedFile = null;
                    this.key = null;
                    this.iv = null;
                    double totalDuration = (double) this.videoEditedInfo.estimatedDuration;
                    long startTime = this.videoEditedInfo.startTime >= 0 ? this.videoEditedInfo.startTime : 0;
                    if (this.videoEditedInfo.endTime >= 0) {
                        endTime = this.videoEditedInfo.endTime;
                    } else {
                        endTime = this.videoEditedInfo.estimatedDuration;
                    }
                    this.videoEditedInfo.estimatedDuration = endTime - startTime;
                    this.videoEditedInfo.estimatedSize = Math.max(1, (long) (((double) this.size) * (((double) this.videoEditedInfo.estimatedDuration) / totalDuration)));
                    this.videoEditedInfo.bitrate = 400000;
                    if (this.videoEditedInfo.startTime > 0) {
                        videoEditedInfo = this.videoEditedInfo;
                        videoEditedInfo.startTime *= 1000;
                    }
                    if (this.videoEditedInfo.endTime > 0) {
                        videoEditedInfo = this.videoEditedInfo;
                        videoEditedInfo.endTime *= 1000;
                    }
                    FileLoader.getInstance(this.currentAccount).cancelUploadFile(this.cameraFile.getAbsolutePath(), false);
                } else {
                    this.videoEditedInfo.estimatedSize = Math.max(1, this.size);
                }
                this.videoEditedInfo.file = this.file;
                this.videoEditedInfo.encryptedFile = this.encryptedFile;
                this.videoEditedInfo.key = this.key;
                this.videoEditedInfo.iv = this.iv;
                this.baseFragment.sendMedia(new PhotoEntry(0, 0, 0, this.cameraFile.getAbsolutePath(), 0, true), this.videoEditedInfo);
                return;
            }
            this.cancelled = this.recordedTime < 800;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            if (this.cameraThread != null) {
                int send;
                NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                int i = NotificationCenter.recordStopped;
                Object[] objArr = new Object[1];
                int i2 = (this.cancelled || state != 3) ? 0 : 2;
                objArr[0] = Integer.valueOf(i2);
                instance.postNotificationName(i, objArr);
                if (this.cancelled) {
                    send = 0;
                } else if (state == 3) {
                    send = 2;
                } else {
                    send = 1;
                }
                saveLastCameraBitmap();
                this.cameraThread.shutdown(send);
                this.cameraThread = null;
            }
            if (this.cancelled) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.audioRecordTooShort, Boolean.valueOf(true));
                startAnimation(false);
            }
        }
    }

    private void saveLastCameraBitmap() {
        if (this.textureView.getBitmap() != null) {
            this.lastBitmap = Bitmap.createScaledBitmap(this.textureView.getBitmap(), 80, 80, true);
            if (this.lastBitmap != null) {
                Utilities.blurBitmap(this.lastBitmap, 7, 1, this.lastBitmap.getWidth(), this.lastBitmap.getHeight(), this.lastBitmap.getRowBytes());
                try {
                    this.lastBitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "icthumb.jpg")));
                } catch (Throwable th) {
                }
            }
        }
    }

    public void cancel() {
        stopProgressTimer();
        if (this.videoPlayer != null) {
            this.videoPlayer.releasePlayer();
            this.videoPlayer = null;
        }
        if (this.textureView != null) {
            this.cancelled = true;
            this.recording = false;
            AndroidUtilities.cancelRunOnUIThread(this.timerRunnable);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.recordStopped, Integer.valueOf(0));
            if (this.cameraThread != null) {
                saveLastCameraBitmap();
                this.cameraThread.shutdown(0);
                this.cameraThread = null;
            }
            if (this.cameraFile != null) {
                this.cameraFile.delete();
                this.cameraFile = null;
            }
            startAnimation(false);
        }
    }

    @Keep
    public void setAlpha(float alpha) {
        ((ColorDrawable) getBackground()).setAlpha((int) (192.0f * alpha));
        invalidate();
    }

    public View getSwitchButtonView() {
        return this.switchCameraButton;
    }

    public View getMuteImageView() {
        return this.muteImageView;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void hideCamera(boolean async) {
        destroy(async, null);
        this.cameraContainer.removeView(this.textureView);
        this.cameraContainer.setTranslationX(0.0f);
        this.cameraContainer.setTranslationY(0.0f);
        this.textureOverlayView.setTranslationX(0.0f);
        this.textureOverlayView.setTranslationY(0.0f);
        this.textureView = null;
    }

    private void switchCamera() {
        boolean z;
        saveLastCameraBitmap();
        if (this.lastBitmap != null) {
            this.textureOverlayView.setImageBitmap(this.lastBitmap);
            this.textureOverlayView.animate().setDuration(120).alpha(1.0f).setInterpolator(new DecelerateInterpolator()).start();
        }
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, null, null);
            this.cameraSession = null;
        }
        if (this.isFrontface) {
            z = false;
        } else {
            z = true;
        }
        this.isFrontface = z;
        initCamera();
        this.cameraReady = false;
        this.cameraThread.reinitForNewCamera();
    }

    private boolean initCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos == null) {
            return false;
        }
        int a;
        CameraInfo notFrontface = null;
        for (a = 0; a < cameraInfos.size(); a++) {
            CameraInfo cameraInfo = (CameraInfo) cameraInfos.get(a);
            if (!cameraInfo.isFrontface()) {
                notFrontface = cameraInfo;
            }
            if ((this.isFrontface && cameraInfo.isFrontface()) || (!this.isFrontface && !cameraInfo.isFrontface())) {
                this.selectedCamera = cameraInfo;
                break;
            }
            notFrontface = cameraInfo;
        }
        if (this.selectedCamera == null) {
            this.selectedCamera = notFrontface;
        }
        if (this.selectedCamera == null) {
            return false;
        }
        ArrayList<Size> previewSizes = this.selectedCamera.getPreviewSizes();
        ArrayList<Size> pictureSizes = this.selectedCamera.getPictureSizes();
        this.previewSize = CameraController.chooseOptimalSize(previewSizes, 480, 270, this.aspectRatio);
        this.pictureSize = CameraController.chooseOptimalSize(pictureSizes, 480, 270, this.aspectRatio);
        if (this.previewSize.mWidth != this.pictureSize.mWidth) {
            Size preview;
            int b;
            Size picture;
            boolean found = false;
            for (a = previewSizes.size() - 1; a >= 0; a--) {
                preview = (Size) previewSizes.get(a);
                for (b = pictureSizes.size() - 1; b >= 0; b--) {
                    picture = (Size) pictureSizes.get(b);
                    if (preview.mWidth >= this.pictureSize.mWidth && preview.mHeight >= this.pictureSize.mHeight && preview.mWidth == picture.mWidth && preview.mHeight == picture.mHeight) {
                        this.previewSize = preview;
                        this.pictureSize = picture;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                for (a = previewSizes.size() - 1; a >= 0; a--) {
                    preview = (Size) previewSizes.get(a);
                    for (b = pictureSizes.size() - 1; b >= 0; b--) {
                        picture = (Size) pictureSizes.get(b);
                        if (preview.mWidth >= 240 && preview.mHeight >= 240 && preview.mWidth == picture.mWidth && preview.mHeight == picture.mHeight) {
                            this.previewSize = preview;
                            this.pictureSize = picture;
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("preview w = " + this.previewSize.mWidth + " h = " + this.previewSize.mHeight);
        }
        return true;
    }

    private void createCamera(SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new InstantCameraView$$Lambda$2(this, surfaceTexture));
    }

    final /* synthetic */ void lambda$createCamera$4$InstantCameraView(SurfaceTexture surfaceTexture) {
        if (this.cameraThread != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("create camera session");
            }
            surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
            this.cameraSession = new CameraSession(this.selectedCamera, this.previewSize, this.pictureSize, 256);
            this.cameraThread.setCurrentSession(this.cameraSession);
            CameraController.getInstance().openRound(this.cameraSession, surfaceTexture, new InstantCameraView$$Lambda$3(this), new InstantCameraView$$Lambda$4(this));
        }
    }

    final /* synthetic */ void lambda$null$2$InstantCameraView() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("camera initied");
            }
            this.cameraSession.setInitied();
        }
    }

    final /* synthetic */ void lambda$null$3$InstantCameraView() {
        this.cameraThread.setCurrentSession(this.cameraSession);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] != 0) {
            return shader;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(shader));
        }
        GLES20.glDeleteShader(shader);
        return 0;
    }

    private void startProgressTimer() {
        if (this.progressTimer != null) {
            try {
                this.progressTimer.cancel();
                this.progressTimer = null;
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask() {
            public void run() {
                AndroidUtilities.runOnUIThread(new InstantCameraView$10$$Lambda$0(this));
            }

            final /* synthetic */ void lambda$run$0$InstantCameraView$10() {
                long j = 0;
                try {
                    if (InstantCameraView.this.videoPlayer != null && InstantCameraView.this.videoEditedInfo != null && InstantCameraView.this.videoEditedInfo.endTime > 0 && InstantCameraView.this.videoPlayer.getCurrentPosition() >= InstantCameraView.this.videoEditedInfo.endTime) {
                        VideoPlayer access$900 = InstantCameraView.this.videoPlayer;
                        if (InstantCameraView.this.videoEditedInfo.startTime > 0) {
                            j = InstantCameraView.this.videoEditedInfo.startTime;
                        }
                        access$900.seekTo(j);
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }, 0, 17);
    }

    private void stopProgressTimer() {
        if (this.progressTimer != null) {
            try {
                this.progressTimer.cancel();
                this.progressTimer = null;
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }
}
