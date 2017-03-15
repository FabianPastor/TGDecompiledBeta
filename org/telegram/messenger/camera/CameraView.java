package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Looper;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.ConnectionsManager;

@SuppressLint({"NewApi"})
public class CameraView extends FrameLayout implements SurfaceTextureListener {
    private CameraSession cameraSession;
    private boolean circleShape = false;
    private int clipLeft;
    private int clipTop;
    private int cx;
    private int cy;
    private CameraViewDelegate delegate;
    private EGLThread eglThread;
    private int focusAreaSize;
    private float focusProgress = 1.0f;
    private boolean initied;
    private float innerAlpha;
    private Paint innerPaint = new Paint(1);
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isFrontface;
    private long lastDrawTime;
    private Matrix matrix = new Matrix();
    private boolean mirror;
    private float outerAlpha;
    private Paint outerPaint = new Paint(1);
    private Size previewSize;
    private TextureView textureView;
    private Matrix txform = new Matrix();

    public interface CameraViewDelegate {
        void onCameraCreated(Camera camera);

        void onCameraInit();
    }

    public class EGLThread extends DispatchQueue {
        private static final String simpleFragmentShaderCode = "varying highp vec2 texCoord;uniform sampler2D sourceImage;void main() {gl_FragColor = texture2D(sourceImage, texCoord);}";
        private static final String simpleVertexShaderCode = "attribute vec4 position;attribute vec2 inputTexCoord;varying vec2 texCoord;void main() {gl_Position = position;texCoord = inputTexCoord;}";
        private final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private final int EGL_OPENGL_ES2_BIT = 4;
        private SurfaceTexture cameraTexture;
        private Runnable drawRunnable = new Runnable() {
            public void run() {
                if (!EGLThread.this.initied) {
                    return;
                }
                if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                    EGLThread.this.cameraTexture.updateTexImage();
                    GLES20.glViewport(0, 0, EGLThread.this.surfaceWidth, EGLThread.this.surfaceHeight);
                    GLES20.glBindFramebuffer(36160, 0);
                    GLES20.glClear(0);
                    GLES20.glUseProgram(EGLThread.this.simpleShaderProgram);
                    GLES20.glActiveTexture(33984);
                    GLES20.glBindTexture(3553, EGLThread.this.renderTexture[0]);
                    GLES20.glUniform1i(EGLThread.this.simpleSourceImageHandle, 0);
                    GLES20.glEnableVertexAttribArray(EGLThread.this.simpleInputTexCoordHandle);
                    GLES20.glVertexAttribPointer(EGLThread.this.simpleInputTexCoordHandle, 2, 5126, false, 8, EGLThread.this.textureBuffer);
                    GLES20.glEnableVertexAttribArray(EGLThread.this.simplePositionHandle);
                    GLES20.glVertexAttribPointer(EGLThread.this.simplePositionHandle, 2, 5126, false, 8, EGLThread.this.vertexBuffer);
                    GLES20.glDrawArrays(5, 0, 4);
                    EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                    return;
                }
                FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
            }
        };
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private GL gl;
        private boolean initied;
        private long lastRenderCallTime;
        private int[] renderTexture = new int[1];
        private int simpleInputTexCoordHandle;
        private int simplePositionHandle;
        private int simpleShaderProgram;
        private int simpleSourceImageHandle;
        private volatile int surfaceHeight;
        private SurfaceTexture surfaceTexture;
        private volatile int surfaceWidth;
        private FloatBuffer textureBuffer;
        private FloatBuffer vertexBuffer;
        private FloatBuffer vertexInvertBuffer;

        public EGLThread(SurfaceTexture surface) {
            super("CameraGlThread");
            this.surfaceTexture = surface;
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
            FileLog.e(GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }

        private boolean initGL() {
            this.egl10 = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                finish();
                return false;
            }
            if (this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                int[] configsCount = new int[1];
                EGLConfig[] configs = new EGLConfig[1];
                if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, configs, 1, configsCount)) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    finish();
                    return false;
                } else if (configsCount[0] > 0) {
                    this.eglConfig = configs[0];
                    this.eglContext = this.egl10.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                    if (this.eglContext == null) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        finish();
                        return false;
                    } else if (this.surfaceTexture instanceof SurfaceTexture) {
                        this.eglSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surfaceTexture, null);
                        if (this.eglSurface == null || this.eglSurface == EGL10.EGL_NO_SURFACE) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            finish();
                            return false;
                        }
                        if (this.egl10.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                            this.gl = this.eglContext.getGL();
                            float[] squareCoordinates = new float[]{-1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f};
                            ByteBuffer bb = ByteBuffer.allocateDirect(squareCoordinates.length * 4);
                            bb.order(ByteOrder.nativeOrder());
                            this.vertexBuffer = bb.asFloatBuffer();
                            this.vertexBuffer.put(squareCoordinates);
                            this.vertexBuffer.position(0);
                            float[] squareCoordinates2 = new float[]{-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
                            bb = ByteBuffer.allocateDirect(squareCoordinates2.length * 4);
                            bb.order(ByteOrder.nativeOrder());
                            this.vertexInvertBuffer = bb.asFloatBuffer();
                            this.vertexInvertBuffer.put(squareCoordinates2);
                            this.vertexInvertBuffer.position(0);
                            float[] textureCoordinates = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
                            bb = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
                            bb.order(ByteOrder.nativeOrder());
                            this.textureBuffer = bb.asFloatBuffer();
                            this.textureBuffer.put(textureCoordinates);
                            this.textureBuffer.position(0);
                            GLES20.glGenTextures(1, this.renderTexture, 0);
                            this.cameraTexture = new SurfaceTexture(this.renderTexture[0]);
                            this.cameraTexture.setOnFrameAvailableListener(new OnFrameAvailableListener() {
                                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                                    EGLThread.this.requestRender(true);
                                }
                            });
                            int vertexShader = loadShader(35633, simpleVertexShaderCode);
                            int fragmentShader = loadShader(35632, simpleFragmentShaderCode);
                            if (vertexShader == 0 || fragmentShader == 0) {
                                finish();
                                return false;
                            }
                            this.simpleShaderProgram = GLES20.glCreateProgram();
                            GLES20.glAttachShader(this.simpleShaderProgram, vertexShader);
                            GLES20.glAttachShader(this.simpleShaderProgram, fragmentShader);
                            GLES20.glBindAttribLocation(this.simpleShaderProgram, 0, "position");
                            GLES20.glBindAttribLocation(this.simpleShaderProgram, 1, "inputTexCoord");
                            GLES20.glLinkProgram(this.simpleShaderProgram);
                            int[] linkStatus = new int[1];
                            GLES20.glGetProgramiv(this.simpleShaderProgram, 35714, linkStatus, 0);
                            if (linkStatus[0] == 0) {
                                GLES20.glDeleteProgram(this.simpleShaderProgram);
                                this.simpleShaderProgram = 0;
                            } else {
                                this.simplePositionHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "position");
                                this.simpleInputTexCoordHandle = GLES20.glGetAttribLocation(this.simpleShaderProgram, "inputTexCoord");
                                this.simpleSourceImageHandle = GLES20.glGetUniformLocation(this.simpleShaderProgram, "sourceImage");
                            }
                            return true;
                        }
                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        finish();
                        return false;
                    } else {
                        finish();
                        return false;
                    }
                } else {
                    FileLog.e("eglConfig not initialized");
                    finish();
                    return false;
                }
            }
            FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
            finish();
            return false;
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

        public void shutdown() {
            postRunnable(new Runnable() {
                public void run() {
                    EGLThread.this.finish();
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                    }
                }
            });
        }

        public void setSurfaceTextureSize(int width, int height) {
            this.surfaceWidth = width;
            this.surfaceHeight = height;
        }

        public void run() {
            this.initied = initGL();
            if (this.initied) {
                CameraView.this.initCamera(CameraView.this.isFrontface);
            }
            super.run();
        }

        public void requestRender(final boolean force) {
            postRunnable(new Runnable() {
                public void run() {
                    long newTime = System.currentTimeMillis();
                    if (force || Math.abs(EGLThread.this.lastRenderCallTime - newTime) > 30) {
                        EGLThread.this.lastRenderCallTime = newTime;
                        EGLThread.this.drawRunnable.run();
                    }
                }
            });
        }
    }

    public CameraView(Context context, boolean frontface) {
        super(context, null);
        this.isFrontface = frontface;
        this.textureView = new TextureView(context);
        this.textureView.setSurfaceTextureListener(this);
        addView(this.textureView);
        this.focusAreaSize = AndroidUtilities.dp(96.0f);
        this.outerPaint.setColor(-1);
        this.outerPaint.setStyle(Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.innerPaint.setColor(ConnectionsManager.DEFAULT_DATACENTER_ID);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        checkPreviewMatrix();
    }

    public void setMirror(boolean value) {
        this.mirror = value;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public boolean hasFrontFaceCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        for (int a = 0; a < cameraInfos.size(); a++) {
            if (((CameraInfo) cameraInfos.get(a)).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        boolean z = false;
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null, null);
            this.cameraSession = null;
        }
        this.initied = false;
        if (!this.isFrontface) {
            z = true;
        }
        this.isFrontface = z;
        initCamera(this.isFrontface);
    }

    private void initCamera(boolean front) {
        CameraInfo info = null;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos != null) {
            for (int a = 0; a < cameraInfos.size(); a++) {
                CameraInfo cameraInfo = (CameraInfo) cameraInfos.get(a);
                if ((this.isFrontface && cameraInfo.frontCamera != 0) || (!this.isFrontface && cameraInfo.frontCamera == 0)) {
                    info = cameraInfo;
                    break;
                }
            }
            if (info != null) {
                Size aspectRatio;
                int wantedWidth;
                int wantedHeight;
                SurfaceTexture surfaceTexture;
                float screenSize = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
                if (Math.abs(screenSize - 1.3333334f) < 0.1f) {
                    aspectRatio = new Size(4, 3);
                    wantedWidth = 1280;
                    wantedHeight = 960;
                } else {
                    aspectRatio = new Size(16, 9);
                    wantedWidth = 1280;
                    wantedHeight = 720;
                }
                if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                    int width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                    this.previewSize = CameraController.chooseOptimalSize(info.getPreviewSizes(), width, (aspectRatio.getHeight() * width) / aspectRatio.getWidth(), aspectRatio);
                }
                Size pictureSize = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedWidth, wantedHeight, aspectRatio);
                if (pictureSize.getWidth() >= 1280 && pictureSize.getHeight() >= 1280) {
                    if (Math.abs(screenSize - 1.3333334f) < 0.1f) {
                        aspectRatio = new Size(3, 4);
                    } else {
                        aspectRatio = new Size(9, 16);
                    }
                    Size pictureSize2 = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedHeight, wantedWidth, aspectRatio);
                    if (pictureSize2.getWidth() < 1280 || pictureSize2.getHeight() < 1280) {
                        pictureSize = pictureSize2;
                    }
                }
                if (this.circleShape) {
                    surfaceTexture = this.eglThread.cameraTexture;
                } else {
                    surfaceTexture = this.textureView.getSurfaceTexture();
                }
                if (this.previewSize != null && surfaceTexture != null) {
                    surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                    this.cameraSession = new CameraSession(info, this.previewSize, pictureSize, 256);
                    CameraController.getInstance().open(this.cameraSession, surfaceTexture, new Runnable() {
                        public void run() {
                            if (CameraView.this.cameraSession != null) {
                                CameraView.this.cameraSession.setInitied();
                            }
                            CameraView.this.checkPreviewMatrix();
                        }
                    }, new Runnable() {
                        public void run() {
                            if (CameraView.this.delegate != null) {
                                CameraView.this.delegate.onCameraCreated(CameraView.this.cameraSession.cameraInfo.camera);
                            }
                        }
                    });
                }
            }
        }
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (!this.circleShape) {
            initCamera(this.isFrontface);
        } else if (this.eglThread == null && surface != null) {
            this.eglThread = new EGLThread(surface);
            this.eglThread.setSurfaceTextureSize(width, height);
            this.eglThread.requestRender(true);
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        if (!this.circleShape) {
            checkPreviewMatrix();
        } else if (this.eglThread != null) {
            this.eglThread.setSurfaceTextureSize(width, height);
            this.eglThread.requestRender(true);
            this.eglThread.postRunnable(new Runnable() {
                public void run() {
                    if (CameraView.this.eglThread != null) {
                        CameraView.this.eglThread.requestRender(true);
                    }
                }
            });
        }
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.circleShape) {
            if (this.eglThread != null) {
                this.eglThread.shutdown();
                this.eglThread = null;
            }
        } else if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null, null);
        }
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (!this.initied && this.cameraSession != null && this.cameraSession.isInitied()) {
            if (this.delegate != null) {
                this.delegate.onCameraInit();
            }
            this.initied = true;
        }
    }

    public void setClipTop(int value) {
        this.clipTop = value;
    }

    public void setClipLeft(int value) {
        this.clipLeft = value;
    }

    private void checkPreviewMatrix() {
        if (this.previewSize != null) {
            adjustAspectRatio(this.previewSize.getWidth(), this.previewSize.getHeight(), ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    private void adjustAspectRatio(int previewWidth, int previewHeight, int rotation) {
        float scale;
        this.txform.reset();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float viewCenterX = (float) (viewWidth / 2);
        float viewCenterY = (float) (viewHeight / 2);
        if (rotation == 0 || rotation == 2) {
            scale = Math.max(((float) (this.clipTop + viewHeight)) / ((float) previewWidth), ((float) (this.clipLeft + viewWidth)) / ((float) previewHeight));
        } else {
            scale = Math.max(((float) (this.clipTop + viewHeight)) / ((float) previewHeight), ((float) (this.clipLeft + viewWidth)) / ((float) previewWidth));
        }
        this.txform.postScale((((float) previewHeight) * scale) / ((float) viewWidth), (((float) previewWidth) * scale) / ((float) viewHeight), viewCenterX, viewCenterY);
        if (1 == rotation || 3 == rotation) {
            this.txform.postRotate((float) ((rotation - 2) * 90), viewCenterX, viewCenterY);
        } else if (2 == rotation) {
            this.txform.postRotate(BitmapDescriptorFactory.HUE_CYAN, viewCenterX, viewCenterY);
        }
        if (this.mirror) {
            this.txform.postScale(-1.0f, 1.0f, viewCenterX, viewCenterY);
        }
        if (!(this.clipTop == 0 && this.clipLeft == 0)) {
            this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
        }
        this.textureView.setTransform(this.txform);
        Matrix matrix = new Matrix();
        matrix.postRotate((float) this.cameraSession.getDisplayOrientation());
        matrix.postScale(((float) viewWidth) / 2000.0f, ((float) viewHeight) / 2000.0f);
        matrix.postTranslate(((float) viewWidth) / 2.0f, ((float) viewHeight) / 2.0f);
        matrix.invert(this.matrix);
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(((float) this.focusAreaSize) * coefficient).intValue();
        int left = clamp(((int) x) - (areaSize / 2), 0, getWidth() - areaSize);
        int top = clamp(((int) y) - (areaSize / 2), 0, getHeight() - areaSize);
        RectF rectF = new RectF((float) left, (float) top, (float) (left + areaSize), (float) (top + areaSize));
        this.matrix.mapRect(rectF);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public void focusToPoint(int x, int y) {
        Rect focusRect = calculateTapArea((float) x, (float) y, 1.0f);
        Rect meteringRect = calculateTapArea((float) x, (float) y, 1.5f);
        if (this.cameraSession != null) {
            this.cameraSession.focusToRect(focusRect, meteringRect);
        }
        this.focusProgress = 0.0f;
        this.innerAlpha = 1.0f;
        this.outerAlpha = 1.0f;
        this.cx = x;
        this.cy = y;
        this.lastDrawTime = System.currentTimeMillis();
        invalidate();
    }

    public void setDelegate(CameraViewDelegate cameraViewDelegate) {
        this.delegate = cameraViewDelegate;
    }

    public boolean isInitied() {
        return this.initied;
    }

    public CameraSession getCameraSession() {
        return this.cameraSession;
    }

    public void destroy(boolean async, Runnable beforeDestroyRunnable) {
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new Semaphore(0) : null, beforeDestroyRunnable);
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (!(this.focusProgress == 1.0f && this.innerAlpha == 0.0f && this.outerAlpha == 0.0f)) {
            int baseRad = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastDrawTime;
            if (dt < 0 || dt > 17) {
                dt = 17;
            }
            this.lastDrawTime = newTime;
            this.outerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.outerAlpha) * 255.0f));
            this.innerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.innerAlpha) * 127.0f));
            float interpolated = this.interpolator.getInterpolation(this.focusProgress);
            canvas.drawCircle((float) this.cx, (float) this.cy, ((float) baseRad) + (((float) baseRad) * (1.0f - interpolated)), this.outerPaint);
            canvas.drawCircle((float) this.cx, (float) this.cy, ((float) baseRad) * interpolated, this.innerPaint);
            if (this.focusProgress < 1.0f) {
                this.focusProgress += ((float) dt) / 200.0f;
                if (this.focusProgress > 1.0f) {
                    this.focusProgress = 1.0f;
                }
                invalidate();
            } else if (this.innerAlpha != 0.0f) {
                this.innerAlpha -= ((float) dt) / 150.0f;
                if (this.innerAlpha < 0.0f) {
                    this.innerAlpha = 0.0f;
                }
                invalidate();
            } else if (this.outerAlpha != 0.0f) {
                this.outerAlpha -= ((float) dt) / 150.0f;
                if (this.outerAlpha < 0.0f) {
                    this.outerAlpha = 0.0f;
                }
                invalidate();
            }
        }
        return result;
    }
}
