package org.telegram.messenger.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.LayoutHelper;

public class CameraView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private static final String FRAGMENT_SCREEN_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
    private static final int MSG_AUDIOFRAME_AVAILABLE = 3;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_VIDEOFRAME_AVAILABLE = 2;
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
    /* access modifiers changed from: private */
    public ImageView blurredStubView;
    private File cameraFile;
    /* access modifiers changed from: private */
    public CameraSession cameraSession;
    /* access modifiers changed from: private */
    public int[] cameraTexture = new int[1];
    CameraGLThread cameraThread;
    private int clipBottom;
    private int clipTop;
    private int cx;
    private int cy;
    private CameraViewDelegate delegate;
    boolean firstFrameRendered;
    ValueAnimator flipAnimator;
    boolean flipHalfReached;
    private int focusAreaSize;
    private float focusProgress = 1.0f;
    /* access modifiers changed from: private */
    public int fpsLimit = -1;
    CameraInfo info;
    private boolean initialFrontface;
    private boolean initied;
    private float innerAlpha;
    private Paint innerPaint = new Paint(1);
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isFrontface;
    private long lastDrawTime;
    /* access modifiers changed from: private */
    public final Object layoutLock = new Object();
    /* access modifiers changed from: private */
    public float[] mMVPMatrix = new float[16];
    /* access modifiers changed from: private */
    public float[] mSTMatrix = new float[16];
    private Matrix matrix = new Matrix();
    private boolean mirror;
    /* access modifiers changed from: private */
    public float[] moldSTMatrix = new float[16];
    long nextFrameTimeNs;
    /* access modifiers changed from: private */
    public int[] oldCameraTexture = new int[1];
    Runnable onRecordingFinishRunnable;
    private boolean optimizeForBarcode;
    private float outerAlpha;
    private Paint outerPaint = new Paint(1);
    private Size pictureSize;
    private int[] position = new int[2];
    /* access modifiers changed from: private */
    public Size previewSize;
    File recordFile;
    private volatile int surfaceHeight;
    private volatile int surfaceWidth;
    private float takePictureProgress = 1.0f;
    /* access modifiers changed from: private */
    public FloatBuffer textureBuffer;
    /* access modifiers changed from: private */
    public TextureView textureView;
    private Matrix txform = new Matrix();
    private boolean useMaxPreview;
    /* access modifiers changed from: private */
    public FloatBuffer vertexBuffer;
    /* access modifiers changed from: private */
    public VideoRecorder videoEncoder;

    public interface CameraViewDelegate {
        void onCameraCreated(Camera camera);

        void onCameraInit();
    }

    public void setRecordFile(File generateVideoPath) {
        this.recordFile = generateVideoPath;
    }

    public boolean startRecording(File path, Runnable onFinished) {
        this.cameraThread.startRecording(path);
        this.onRecordingFinishRunnable = onFinished;
        return true;
    }

    public void stopRecording() {
        this.cameraThread.stopRecording();
    }

    public void startSwitchingAnimation() {
        ValueAnimator valueAnimator = this.flipAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.blurredStubView.animate().setListener((Animator.AnimatorListener) null).cancel();
        if (this.firstFrameRendered) {
            Bitmap bitmap = this.textureView.getBitmap(100, 100);
            if (bitmap != null) {
                Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                this.blurredStubView.setBackground(new BitmapDrawable(bitmap));
            }
            this.blurredStubView.setAlpha(0.0f);
        } else {
            this.blurredStubView.setAlpha(1.0f);
        }
        this.blurredStubView.setVisibility(0);
        synchronized (this.layoutLock) {
            this.firstFrameRendered = false;
        }
        this.flipHalfReached = false;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.flipAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float rotation;
                float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                boolean halfReached = false;
                if (v < 0.5f) {
                    rotation = v;
                } else {
                    halfReached = true;
                    rotation = v - 1.0f;
                }
                float rotation2 = rotation * 180.0f;
                CameraView.this.textureView.setRotationY(rotation2);
                CameraView.this.blurredStubView.setRotationY(rotation2);
                if (halfReached && !CameraView.this.flipHalfReached) {
                    CameraView.this.blurredStubView.setAlpha(1.0f);
                    CameraView.this.flipHalfReached = true;
                }
            }
        });
        this.flipAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                CameraView.this.flipAnimator = null;
                CameraView.this.textureView.setTranslationY(0.0f);
                CameraView.this.textureView.setRotationX(0.0f);
                CameraView.this.textureView.setRotationY(0.0f);
                CameraView.this.textureView.setScaleX(1.0f);
                CameraView.this.textureView.setScaleY(1.0f);
                CameraView.this.blurredStubView.setRotationY(0.0f);
                if (!CameraView.this.flipHalfReached) {
                    CameraView.this.blurredStubView.setAlpha(1.0f);
                    CameraView.this.flipHalfReached = true;
                }
                CameraView.this.invalidate();
            }
        });
        this.flipAnimator.setDuration(400);
        this.flipAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.flipAnimator.start();
        invalidate();
    }

    public CameraView(Context context, boolean frontface) {
        super(context, (AttributeSet) null);
        this.isFrontface = frontface;
        this.initialFrontface = frontface;
        TextureView textureView2 = new TextureView(context);
        this.textureView = textureView2;
        textureView2.setSurfaceTextureListener(this);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
        ImageView imageView = new ImageView(context);
        this.blurredStubView = imageView;
        addView(imageView, LayoutHelper.createFrame(-1, -1, 17));
        this.blurredStubView.setVisibility(8);
        this.focusAreaSize = AndroidUtilities.dp(96.0f);
        this.outerPaint.setColor(-1);
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.innerPaint.setColor(Integer.MAX_VALUE);
    }

    public void setOptimizeForBarcode(boolean value) {
        this.optimizeForBarcode = value;
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setOptimizeForBarcode(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CameraSession cameraSession2;
        int frameHeight;
        int frameWidth;
        if (!(this.previewSize == null || (cameraSession2 = this.cameraSession) == null)) {
            cameraSession2.updateRotation();
            if (this.cameraSession.getWorldAngle() == 90 || this.cameraSession.getWorldAngle() == 270) {
                frameWidth = this.previewSize.getWidth();
                frameHeight = this.previewSize.getHeight();
            } else {
                frameWidth = this.previewSize.getHeight();
                frameHeight = this.previewSize.getWidth();
            }
            float s = Math.max(((float) View.MeasureSpec.getSize(widthMeasureSpec)) / ((float) frameWidth), ((float) View.MeasureSpec.getSize(heightMeasureSpec)) / ((float) frameHeight));
            ViewGroup.LayoutParams layoutParams = this.blurredStubView.getLayoutParams();
            int i = (int) (((float) frameWidth) * s);
            this.textureView.getLayoutParams().width = i;
            layoutParams.width = i;
            ViewGroup.LayoutParams layoutParams2 = this.blurredStubView.getLayoutParams();
            int i2 = (int) (((float) frameHeight) * s);
            this.textureView.getLayoutParams().height = i2;
            layoutParams2.height = i2;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        checkPreviewMatrix();
    }

    public float getTextureHeight(float width, float height) {
        CameraSession cameraSession2;
        int frameHeight;
        int frameWidth;
        if (this.previewSize == null || (cameraSession2 = this.cameraSession) == null) {
            return height;
        }
        if (cameraSession2.getWorldAngle() == 90 || this.cameraSession.getWorldAngle() == 270) {
            frameWidth = this.previewSize.getWidth();
            frameHeight = this.previewSize.getHeight();
        } else {
            frameWidth = this.previewSize.getHeight();
            frameHeight = this.previewSize.getWidth();
        }
        return (float) ((int) (((float) frameHeight) * Math.max(width / ((float) frameWidth), height / ((float) frameHeight))));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        checkPreviewMatrix();
    }

    public void setMirror(boolean value) {
        this.mirror = value;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void setUseMaxPreview(boolean value) {
        this.useMaxPreview = value;
    }

    public boolean hasFrontFaceCamera() {
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        for (int a = 0; a < cameraInfos.size(); a++) {
            if (cameraInfos.get(a).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, new CountDownLatch(1), (Runnable) null);
            this.cameraSession = null;
        }
        this.initied = false;
        this.isFrontface = !this.isFrontface;
        updateCameraInfoSize();
        this.cameraThread.reinitForNewCamera();
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        updateCameraInfoSize();
        this.surfaceHeight = height;
        this.surfaceWidth = width;
        if (this.cameraThread == null && surface != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView start create thread");
            }
            this.cameraThread = new CameraGLThread(surface);
            checkPreviewMatrix();
        }
    }

    private void updateCameraInfoSize() {
        int wantedHeight;
        int photoMaxHeight;
        int wantedWidth;
        int photoMaxWidth;
        Size aspectRatio;
        CameraInfo cameraInfo;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos != null) {
            int a = 0;
            while (true) {
                if (a >= cameraInfos.size()) {
                    break;
                }
                cameraInfo = cameraInfos.get(a);
                if ((!this.isFrontface || cameraInfo.frontCamera == 0) && (this.isFrontface || cameraInfo.frontCamera != 0)) {
                    a++;
                }
            }
            this.info = cameraInfo;
            if (this.info != null) {
                float screenSize = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
                if (this.initialFrontface) {
                    aspectRatio = new Size(16, 9);
                    photoMaxWidth = 480;
                    wantedWidth = 480;
                    photoMaxHeight = 270;
                    wantedHeight = 270;
                } else if (Math.abs(screenSize - 1.3333334f) < 0.1f) {
                    aspectRatio = new Size(4, 3);
                    wantedWidth = 1280;
                    wantedHeight = 960;
                    if (SharedConfig.getDevicePerformanceClass() == 0) {
                        photoMaxWidth = 1280;
                        photoMaxHeight = 960;
                    } else {
                        photoMaxWidth = 1920;
                        photoMaxHeight = 1440;
                    }
                } else {
                    aspectRatio = new Size(16, 9);
                    wantedWidth = 1280;
                    wantedHeight = 720;
                    if (SharedConfig.getDevicePerformanceClass() == 0) {
                        photoMaxWidth = 1280;
                        photoMaxHeight = 960;
                    } else {
                        photoMaxWidth = 1920;
                        photoMaxHeight = 1080;
                    }
                }
                this.previewSize = CameraController.chooseOptimalSize(this.info.getPreviewSizes(), wantedWidth, wantedHeight, aspectRatio);
                this.pictureSize = CameraController.chooseOptimalSize(this.info.getPictureSizes(), photoMaxWidth, photoMaxHeight, aspectRatio);
                requestLayout();
            }
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int surfaceW, int surfaceH) {
        this.surfaceHeight = surfaceH;
        this.surfaceWidth = surfaceW;
        checkPreviewMatrix();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        CameraGLThread cameraGLThread = this.cameraThread;
        if (cameraGLThread != null) {
            cameraGLThread.shutdown(0);
            this.cameraThread = null;
        }
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, (CountDownLatch) null, (Runnable) null);
        }
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        CameraSession cameraSession2;
        if (!this.initied && (cameraSession2 = this.cameraSession) != null && cameraSession2.isInitied()) {
            CameraViewDelegate cameraViewDelegate = this.delegate;
            if (cameraViewDelegate != null) {
                cameraViewDelegate.onCameraInit();
            }
            this.initied = true;
        }
    }

    public void setClipTop(int value) {
        this.clipTop = value;
    }

    public void setClipBottom(int value) {
        this.clipBottom = value;
    }

    private void checkPreviewMatrix() {
        if (this.previewSize != null) {
            int viewWidth = this.textureView.getWidth();
            int viewHeight = this.textureView.getHeight();
            Matrix matrix2 = new Matrix();
            CameraSession cameraSession2 = this.cameraSession;
            if (cameraSession2 != null) {
                matrix2.postRotate((float) cameraSession2.getDisplayOrientation());
            }
            matrix2.postScale(((float) viewWidth) / 2000.0f, ((float) viewHeight) / 2000.0f);
            matrix2.postTranslate(((float) viewWidth) / 2.0f, ((float) viewHeight) / 2.0f);
            matrix2.invert(this.matrix);
            CameraGLThread cameraGLThread = this.cameraThread;
            if (cameraGLThread != null) {
                cameraGLThread.postRunnable(new CameraView$$ExternalSyntheticLambda0(this));
            }
        }
    }

    /* renamed from: lambda$checkPreviewMatrix$0$org-telegram-messenger-camera-CameraView  reason: not valid java name */
    public /* synthetic */ void m1179x692aae4f() {
        CameraGLThread cameraThread2 = this.cameraThread;
        if (cameraThread2 != null && cameraThread2.currentSession != null) {
            int rotationAngle = cameraThread2.currentSession.getWorldAngle();
            android.opengl.Matrix.setIdentityM(this.mMVPMatrix, 0);
            if (rotationAngle != 0) {
                android.opengl.Matrix.rotateM(this.mMVPMatrix, 0, (float) rotationAngle, 0.0f, 0.0f, 1.0f);
            }
        }
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
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.focusToRect(focusRect, meteringRect);
        }
        this.focusProgress = 0.0f;
        this.innerAlpha = 1.0f;
        this.outerAlpha = 1.0f;
        this.cx = x;
        this.cy = y;
        this.lastDrawTime = System.currentTimeMillis();
        invalidate();
    }

    public void setZoom(float value) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setZoom(value);
        }
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
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
        }
    }

    public Matrix getMatrix() {
        return this.txform;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Canvas canvas2 = canvas;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (!(this.focusProgress == 1.0f && this.innerAlpha == 0.0f && this.outerAlpha == 0.0f)) {
            int baseRad = AndroidUtilities.dp(30.0f);
            long newTime = System.currentTimeMillis();
            long dt = newTime - this.lastDrawTime;
            if (dt < 0 || dt > 17) {
                dt = 17;
            }
            this.lastDrawTime = newTime;
            this.outerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.outerAlpha) * 255.0f));
            this.innerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.innerAlpha) * 127.0f));
            float interpolated = this.interpolator.getInterpolation(this.focusProgress);
            canvas2.drawCircle((float) this.cx, (float) this.cy, ((float) baseRad) + (((float) baseRad) * (1.0f - interpolated)), this.outerPaint);
            canvas2.drawCircle((float) this.cx, (float) this.cy, ((float) baseRad) * interpolated, this.innerPaint);
            float f = this.focusProgress;
            if (f < 1.0f) {
                float f2 = f + (((float) dt) / 200.0f);
                this.focusProgress = f2;
                if (f2 > 1.0f) {
                    this.focusProgress = 1.0f;
                }
                invalidate();
            } else {
                float f3 = this.innerAlpha;
                if (f3 != 0.0f) {
                    float f4 = f3 - (((float) dt) / 150.0f);
                    this.innerAlpha = f4;
                    if (f4 < 0.0f) {
                        this.innerAlpha = 0.0f;
                    }
                    invalidate();
                } else {
                    float f5 = this.outerAlpha;
                    if (f5 != 0.0f) {
                        float f6 = f5 - (((float) dt) / 150.0f);
                        this.outerAlpha = f6;
                        if (f6 < 0.0f) {
                            this.outerAlpha = 0.0f;
                        }
                        invalidate();
                    }
                }
            }
        }
        return result;
    }

    public void startTakePictureAnimation() {
        this.takePictureProgress = 0.0f;
        invalidate();
        runHaptic();
    }

    public void runHaptic() {
        long[] vibrationWaveFormDurationPattern = {0, 1};
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, -1);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect);
            return;
        }
        performHapticFeedback(3, 2);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.flipAnimator != null) {
            canvas.drawColor(-16777216);
        }
        super.dispatchDraw(canvas);
        float f = this.takePictureProgress;
        if (f != 1.0f) {
            float f2 = f + 0.10666667f;
            this.takePictureProgress = f2;
            if (f2 > 1.0f) {
                this.takePictureProgress = 1.0f;
            } else {
                invalidate();
            }
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) ((1.0f - this.takePictureProgress) * 150.0f)));
        }
    }

    public class CameraGLThread extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private final int DO_REINIT_MESSAGE = 2;
        private final int DO_RENDER_MESSAGE = 0;
        private final int DO_SETSESSION_MESSAGE = 3;
        private final int DO_SHUTDOWN_MESSAGE = 1;
        private final int DO_START_RECORDING = 4;
        private final int DO_STOP_RECORDING = 5;
        final int[] array = new int[1];
        private Integer cameraId = 0;
        private SurfaceTexture cameraSurface;
        /* access modifiers changed from: private */
        public CameraSession currentSession;
        private int drawProgram;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initied;
        private boolean needRecord;
        private int positionHandle;
        private boolean recording;
        private SurfaceTexture surfaceTexture;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;

        public CameraGLThread(SurfaceTexture surface) {
            super("CameraGLThread");
            this.surfaceTexture = surface;
        }

        private boolean initGL() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView start init gl");
            }
            EGL10 egl102 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl102;
            EGLDisplay eglGetDisplay = egl102.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                this.eglDisplay = null;
                finish();
                return false;
            }
            if (!this.egl10.eglInitialize(this.eglDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eGLConfig = configs[0];
                this.eglConfig = eGLConfig;
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{12440, 2, 12344});
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null || eglCreateContext == EGL10.EGL_NO_CONTEXT) {
                    this.eglContext = null;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture2 = this.surfaceTexture;
                if (surfaceTexture2 != null) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture2, (int[]) null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    EGL10 egl103 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl103.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    GL gl = this.eglContext.getGL();
                    android.opengl.Matrix.setIdentityM(CameraView.this.mSTMatrix, 0);
                    int vertexShader = CameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                    int fragmentShader = CameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                    if (vertexShader == 0 || fragmentShader == 0) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("failed creating shader");
                        }
                        finish();
                        return false;
                    }
                    int glCreateProgram = GLES20.glCreateProgram();
                    this.drawProgram = glCreateProgram;
                    GLES20.glAttachShader(glCreateProgram, vertexShader);
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
                    GLES20.glGenTextures(1, CameraView.this.cameraTexture, 0);
                    GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
                    GLES20.glTexParameteri(36197, 10241, 9729);
                    GLES20.glTexParameteri(36197, 10240, 9729);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    android.opengl.Matrix.setIdentityM(CameraView.this.mMVPMatrix, 0);
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("gl initied");
                    }
                    float[] verticesData = {-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f};
                    float[] texData = {0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f + 0.5f, 0.5f + 0.5f};
                    FloatBuffer unused = CameraView.this.vertexBuffer = ByteBuffer.allocateDirect(verticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                    CameraView.this.vertexBuffer.put(verticesData).position(0);
                    FloatBuffer unused2 = CameraView.this.textureBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                    CameraView.this.textureBuffer.put(texData).position(0);
                    SurfaceTexture surfaceTexture3 = new SurfaceTexture(CameraView.this.cameraTexture[0]);
                    this.cameraSurface = surfaceTexture3;
                    surfaceTexture3.setOnFrameAvailableListener(new CameraView$CameraGLThread$$ExternalSyntheticLambda1(this));
                    CameraView.this.createCamera(this.cameraSurface);
                    return true;
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        /* renamed from: lambda$initGL$0$org-telegram-messenger-camera-CameraView$CameraGLThread  reason: not valid java name */
        public /* synthetic */ void m1184xvar_d(SurfaceTexture surfaceTexture2) {
            requestRender();
        }

        public void reinitForNewCamera() {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(2, Integer.valueOf(CameraView.this.info.cameraId)), 0);
            }
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        public void setCurrentSession(CameraSession session) {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, session), 0);
            }
        }

        private void onDraw(Integer cameraId2, boolean updateTexImage) {
            boolean shouldRenderFrame;
            if (this.initied) {
                if (!this.eglContext.equals(this.egl10.eglGetCurrentContext()) || !this.eglSurface.equals(this.egl10.eglGetCurrentSurface(12377))) {
                    EGL10 egl102 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                            return;
                        }
                        return;
                    }
                }
                if (updateTexImage) {
                    try {
                        this.cameraSurface.updateTexImage();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                synchronized (CameraView.this.layoutLock) {
                    if (CameraView.this.fpsLimit <= 0) {
                        shouldRenderFrame = true;
                    } else {
                        long currentTimeNs = System.nanoTime();
                        if (currentTimeNs < CameraView.this.nextFrameTimeNs) {
                            shouldRenderFrame = false;
                        } else {
                            CameraView.this.nextFrameTimeNs += TimeUnit.SECONDS.toNanos(1) / ((long) CameraView.this.fpsLimit);
                            CameraView cameraView = CameraView.this;
                            cameraView.nextFrameTimeNs = Math.max(cameraView.nextFrameTimeNs, currentTimeNs);
                            shouldRenderFrame = true;
                        }
                    }
                }
                CameraSession cameraSession = this.currentSession;
                if (cameraSession != null && cameraSession.cameraInfo.cameraId == cameraId2.intValue()) {
                    if (this.recording && CameraView.this.videoEncoder != null) {
                        CameraView.this.videoEncoder.frameAvailable(this.cameraSurface, cameraId2, System.nanoTime());
                    }
                    if (shouldRenderFrame) {
                        this.cameraSurface.getTransformMatrix(CameraView.this.mSTMatrix);
                        this.egl10.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, this.array);
                        int[] iArr = this.array;
                        int drawnWidth = iArr[0];
                        this.egl10.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, iArr);
                        GLES20.glViewport(0, 0, drawnWidth, this.array[0]);
                        GLES20.glUseProgram(this.drawProgram);
                        GLES20.glActiveTexture(33984);
                        GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
                        GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, CameraView.this.vertexBuffer);
                        GLES20.glEnableVertexAttribArray(this.positionHandle);
                        GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, CameraView.this.textureBuffer);
                        GLES20.glEnableVertexAttribArray(this.textureHandle);
                        GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, CameraView.this.mSTMatrix, 0);
                        GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, CameraView.this.mMVPMatrix, 0);
                        GLES20.glDrawArrays(5, 0, 4);
                        GLES20.glDisableVertexAttribArray(this.positionHandle);
                        GLES20.glDisableVertexAttribArray(this.textureHandle);
                        GLES20.glBindTexture(36197, 0);
                        GLES20.glUseProgram(0);
                        this.egl10.eglSwapBuffers(this.eglDisplay, this.eglSurface);
                        synchronized (CameraView.this.layoutLock) {
                            if (!CameraView.this.firstFrameRendered) {
                                CameraView.this.firstFrameRendered = true;
                                AndroidUtilities.runOnUIThread(new CameraView$CameraGLThread$$ExternalSyntheticLambda2(this));
                            }
                        }
                    }
                }
            }
        }

        /* renamed from: lambda$onDraw$1$org-telegram-messenger-camera-CameraView$CameraGLThread  reason: not valid java name */
        public /* synthetic */ void m1185x9c0a5ccc() {
            CameraView.this.onFirstFrameRendered();
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void handleMessage(Message inputMessage) {
            switch (inputMessage.what) {
                case 0:
                    onDraw((Integer) inputMessage.obj, true);
                    return;
                case 1:
                    finish();
                    if (this.recording) {
                        CameraView.this.videoEncoder.stopRecording(inputMessage.arg1);
                    }
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                        return;
                    }
                    return;
                case 2:
                    EGL10 egl102 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        SurfaceTexture surfaceTexture2 = this.cameraSurface;
                        if (surfaceTexture2 != null) {
                            surfaceTexture2.getTransformMatrix(CameraView.this.moldSTMatrix);
                            this.cameraSurface.setOnFrameAvailableListener((SurfaceTexture.OnFrameAvailableListener) null);
                            this.cameraSurface.release();
                        }
                        this.cameraId = (Integer) inputMessage.obj;
                        GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
                        GLES20.glTexParameteri(36197, 10241, 9729);
                        GLES20.glTexParameteri(36197, 10240, 9729);
                        GLES20.glTexParameteri(36197, 10242, 33071);
                        GLES20.glTexParameteri(36197, 10243, 33071);
                        SurfaceTexture surfaceTexture3 = new SurfaceTexture(CameraView.this.cameraTexture[0]);
                        this.cameraSurface = surfaceTexture3;
                        surfaceTexture3.setOnFrameAvailableListener(new CameraView$CameraGLThread$$ExternalSyntheticLambda0(this));
                        CameraView.this.createCamera(this.cameraSurface);
                        return;
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("CameraView eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("CameraView set gl rednderer session");
                    }
                    CameraSession newSession = (CameraSession) inputMessage.obj;
                    if (this.currentSession != newSession) {
                        this.currentSession = newSession;
                        this.cameraId = Integer.valueOf(newSession.cameraInfo.cameraId);
                    }
                    this.currentSession.updateRotation();
                    int rotationAngle = this.currentSession.getWorldAngle();
                    android.opengl.Matrix.setIdentityM(CameraView.this.mMVPMatrix, 0);
                    if (rotationAngle != 0) {
                        android.opengl.Matrix.rotateM(CameraView.this.mMVPMatrix, 0, (float) rotationAngle, 0.0f, 0.0f, 1.0f);
                        return;
                    }
                    return;
                case 4:
                    if (this.initied) {
                        CameraView.this.recordFile = (File) inputMessage.obj;
                        VideoRecorder unused = CameraView.this.videoEncoder = new VideoRecorder();
                        this.recording = true;
                        CameraView.this.videoEncoder.startRecording(CameraView.this.recordFile, EGL14.eglGetCurrentContext());
                        return;
                    }
                    return;
                case 5:
                    if (CameraView.this.videoEncoder != null) {
                        CameraView.this.videoEncoder.stopRecording(0);
                        VideoRecorder unused2 = CameraView.this.videoEncoder = null;
                    }
                    this.recording = false;
                    return;
                default:
                    return;
            }
        }

        /* renamed from: lambda$handleMessage$2$org-telegram-messenger-camera-CameraView$CameraGLThread  reason: not valid java name */
        public /* synthetic */ void m1183xd2064eaf(SurfaceTexture surfaceTexture2) {
            requestRender();
        }

        public void shutdown(int send) {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, send, 0), 0);
            }
        }

        public void requestRender() {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0, this.cameraId), 0);
            }
        }

        public boolean startRecording(File path) {
            Handler handler = CameraView.this.getHandler();
            if (handler == null) {
                return true;
            }
            sendMessage(handler.obtainMessage(4, path), 0);
            return false;
        }

        public void stopRecording() {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(5), 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onFirstFrameRendered() {
        if (this.blurredStubView.getVisibility() == 0) {
            this.blurredStubView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    CameraView.this.blurredStubView.setVisibility(8);
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public int loadShader(int type, String shaderCode) {
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

    /* access modifiers changed from: private */
    public void createCamera(SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new CameraView$$ExternalSyntheticLambda3(this, surfaceTexture));
    }

    /* renamed from: lambda$createCamera$3$org-telegram-messenger-camera-CameraView  reason: not valid java name */
    public /* synthetic */ void m1182lambda$createCamera$3$orgtelegrammessengercameraCameraView(SurfaceTexture surfaceTexture) {
        if (this.cameraThread != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView create camera session");
            }
            if (this.previewSize == null) {
                updateCameraInfoSize();
            }
            Size size = this.previewSize;
            if (size != null) {
                surfaceTexture.setDefaultBufferSize(size.getWidth(), this.previewSize.getHeight());
                CameraSession cameraSession2 = new CameraSession(this.info, this.previewSize, this.pictureSize, 256, false);
                this.cameraSession = cameraSession2;
                this.cameraThread.setCurrentSession(cameraSession2);
                requestLayout();
                CameraController.getInstance().open(this.cameraSession, surfaceTexture, new CameraView$$ExternalSyntheticLambda1(this), new CameraView$$ExternalSyntheticLambda2(this));
            }
        }
    }

    /* renamed from: lambda$createCamera$1$org-telegram-messenger-camera-CameraView  reason: not valid java name */
    public /* synthetic */ void m1180lambda$createCamera$1$orgtelegrammessengercameraCameraView() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView camera initied");
            }
            this.cameraSession.setInitied();
            requestLayout();
        }
    }

    /* renamed from: lambda$createCamera$2$org-telegram-messenger-camera-CameraView  reason: not valid java name */
    public /* synthetic */ void m1181lambda$createCamera$2$orgtelegrammessengercameraCameraView() {
        this.cameraThread.setCurrentSession(this.cameraSession);
    }

    private class VideoRecorder implements Runnable {
        private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";
        private static final int FRAME_RATE = 30;
        private static final int IFRAME_INTERVAL = 1;
        private static final String VIDEO_MIME_TYPE = "video/avc";
        private MediaCodec.BufferInfo audioBufferInfo;
        private MediaCodec audioEncoder;
        private long audioFirst;
        /* access modifiers changed from: private */
        public AudioRecord audioRecorder;
        private long audioStartTime;
        private boolean audioStopedByTime;
        private int audioTrackIndex;
        private boolean blendEnabled;
        /* access modifiers changed from: private */
        public ArrayBlockingQueue<InstantCameraView.AudioBufferInfo> buffers;
        private ArrayList<InstantCameraView.AudioBufferInfo> buffersToWrite;
        private long currentTimestamp;
        private long desyncTime;
        private int drawProgram;
        private android.opengl.EGLConfig eglConfig;
        private android.opengl.EGLContext eglContext;
        private android.opengl.EGLDisplay eglDisplay;
        private android.opengl.EGLSurface eglSurface;
        private boolean firstEncode;
        private int frameCount;
        private DispatchQueue generateKeyframeThumbsQueue;
        /* access modifiers changed from: private */
        public volatile EncoderHandler handler;
        private ArrayList<Bitmap> keyframeThumbs;
        private Integer lastCameraId;
        private long lastCommitedFrameTime;
        private long lastTimestamp;
        private MP4Builder mediaMuxer;
        private int positionHandle;
        private int prependHeaderSize;
        private boolean ready;
        private Runnable recorderRunnable;
        /* access modifiers changed from: private */
        public volatile boolean running;
        /* access modifiers changed from: private */
        public volatile int sendWhenDone;
        private android.opengl.EGLContext sharedEglContext;
        private boolean skippedFirst;
        private long skippedTime;
        private Surface surface;
        private final Object sync;
        private FloatBuffer textureBuffer;
        private int textureHandle;
        private int textureMatrixHandle;
        private int vertexMatrixHandle;
        private int videoBitrate;
        private MediaCodec.BufferInfo videoBufferInfo;
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
            this.buffersToWrite = new ArrayList<>();
            this.videoTrackIndex = -5;
            this.audioTrackIndex = -5;
            this.audioStartTime = -1;
            this.currentTimestamp = 0;
            this.lastTimestamp = -1;
            this.sync = new Object();
            this.videoFirst = -1;
            this.audioFirst = -1;
            this.lastCameraId = 0;
            this.buffers = new ArrayBlockingQueue<>(10);
            this.keyframeThumbs = new ArrayList<>();
            this.recorderRunnable = new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:12:0x002d, code lost:
                    if (org.telegram.messenger.camera.CameraView.VideoRecorder.access$1700(r12.this$1) == 0) goto L_0x00e2;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r12 = this;
                        r0 = -1
                        r2 = 0
                    L_0x0003:
                        r3 = 0
                        r4 = 1
                        if (r2 != 0) goto L_0x00e2
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r5 = r5.running
                        if (r5 != 0) goto L_0x0031
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        android.media.AudioRecord r5 = r5.audioRecorder
                        int r5 = r5.getRecordingState()
                        if (r5 == r4) goto L_0x0031
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0025 }
                        android.media.AudioRecord r5 = r5.audioRecorder     // Catch:{ Exception -> 0x0025 }
                        r5.stop()     // Catch:{ Exception -> 0x0025 }
                        goto L_0x0027
                    L_0x0025:
                        r5 = move-exception
                        r2 = 1
                    L_0x0027:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        int r5 = r5.sendWhenDone
                        if (r5 != 0) goto L_0x0031
                        goto L_0x00e2
                    L_0x0031:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r5 = r5.buffers
                        boolean r5 = r5.isEmpty()
                        if (r5 == 0) goto L_0x0043
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r5 = new org.telegram.ui.Components.InstantCameraView$AudioBufferInfo
                        r5.<init>()
                        goto L_0x004f
                    L_0x0043:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r5 = r5.buffers
                        java.lang.Object r5 = r5.poll()
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r5 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r5
                    L_0x004f:
                        r5.lastWroteBuffer = r3
                        r3 = 10
                        r5.results = r3
                        r6 = 0
                    L_0x0056:
                        if (r6 >= r3) goto L_0x00a0
                        r7 = -1
                        int r9 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
                        if (r9 != 0) goto L_0x0066
                        long r7 = java.lang.System.nanoTime()
                        r9 = 1000(0x3e8, double:4.94E-321)
                        long r0 = r7 / r9
                    L_0x0066:
                        java.nio.ByteBuffer[] r7 = r5.buffer
                        r7 = r7[r6]
                        r7.rewind()
                        org.telegram.messenger.camera.CameraView$VideoRecorder r8 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        android.media.AudioRecord r8 = r8.audioRecorder
                        r9 = 2048(0x800, float:2.87E-42)
                        int r8 = r8.read(r7, r9)
                        if (r8 > 0) goto L_0x0088
                        r5.results = r6
                        org.telegram.messenger.camera.CameraView$VideoRecorder r9 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r9 = r9.running
                        if (r9 != 0) goto L_0x00a0
                        r5.last = r4
                        goto L_0x00a0
                    L_0x0088:
                        long[] r9 = r5.offset
                        r9[r6] = r0
                        int[] r9 = r5.read
                        r9[r6] = r8
                        r9 = 1000000(0xvar_, float:1.401298E-39)
                        int r9 = r9 * r8
                        r10 = 44100(0xaCLASSNAME, float:6.1797E-41)
                        int r9 = r9 / r10
                        int r9 = r9 / 2
                        long r10 = (long) r9
                        long r0 = r0 + r10
                        int r6 = r6 + 1
                        goto L_0x0056
                    L_0x00a0:
                        int r4 = r5.results
                        if (r4 >= 0) goto L_0x00bf
                        boolean r4 = r5.last
                        if (r4 == 0) goto L_0x00a9
                        goto L_0x00bf
                    L_0x00a9:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r3 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r3 = r3.running
                        if (r3 != 0) goto L_0x00b3
                        r2 = 1
                        goto L_0x00e0
                    L_0x00b3:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r3 = org.telegram.messenger.camera.CameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00bd }
                        java.util.concurrent.ArrayBlockingQueue r3 = r3.buffers     // Catch:{ Exception -> 0x00bd }
                        r3.put(r5)     // Catch:{ Exception -> 0x00bd }
                        goto L_0x00e0
                    L_0x00bd:
                        r3 = move-exception
                        goto L_0x00e0
                    L_0x00bf:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r4 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r4 = r4.running
                        if (r4 != 0) goto L_0x00cc
                        int r4 = r5.results
                        if (r4 >= r3) goto L_0x00cc
                        r2 = 1
                    L_0x00cc:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r3 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r3 = r3.handler
                        org.telegram.messenger.camera.CameraView$VideoRecorder r4 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r4 = r4.handler
                        r6 = 3
                        android.os.Message r4 = r4.obtainMessage(r6, r5)
                        r3.sendMessage(r4)
                    L_0x00e0:
                        goto L_0x0003
                    L_0x00e2:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00ec }
                        android.media.AudioRecord r5 = r5.audioRecorder     // Catch:{ Exception -> 0x00ec }
                        r5.release()     // Catch:{ Exception -> 0x00ec }
                        goto L_0x00f0
                    L_0x00ec:
                        r5 = move-exception
                        org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
                    L_0x00f0:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r5 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r5 = r5.handler
                        org.telegram.messenger.camera.CameraView$VideoRecorder r6 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r6 = r6.handler
                        org.telegram.messenger.camera.CameraView$VideoRecorder r7 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        int r7 = r7.sendWhenDone
                        android.os.Message r3 = r6.obtainMessage(r4, r7, r3)
                        r5.sendMessage(r3)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.VideoRecorder.AnonymousClass1.run():void");
                }
            };
        }

        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0083, code lost:
            r6.keyframeThumbs.clear();
            r6.frameCount = 0;
            r4 = r6.generateKeyframeThumbsQueue;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x008d, code lost:
            if (r4 == null) goto L_0x0097;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x008f, code lost:
            r4.cleanupQueue();
            r6.generateKeyframeThumbsQueue.recycle();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x0097, code lost:
            r6.generateKeyframeThumbsQueue = new org.telegram.messenger.DispatchQueue("keyframes_thumb_queque");
            r6.handler.sendMessage(r6.handler.obtainMessage(0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00ab, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void startRecording(java.io.File r7, android.opengl.EGLContext r8) {
            /*
                r6 = this;
                java.lang.String r0 = android.os.Build.DEVICE
                if (r0 != 0) goto L_0x0006
                java.lang.String r0 = ""
            L_0x0006:
                org.telegram.messenger.camera.CameraView r1 = org.telegram.messenger.camera.CameraView.this
                org.telegram.messenger.camera.Size r1 = r1.previewSize
                int r2 = r1.mHeight
                int r3 = r1.mWidth
                int r2 = java.lang.Math.min(r2, r3)
                r3 = 720(0x2d0, float:1.009E-42)
                if (r2 < r3) goto L_0x001c
                r2 = 3500000(0x3567e0, float:4.904545E-39)
                goto L_0x001f
            L_0x001c:
                r2 = 1800000(0x1b7740, float:2.522337E-39)
            L_0x001f:
                r6.videoFile = r7
                org.telegram.messenger.camera.CameraView r3 = org.telegram.messenger.camera.CameraView.this
                org.telegram.messenger.camera.CameraSession r3 = r3.cameraSession
                int r3 = r3.getWorldAngle()
                r4 = 90
                if (r3 == r4) goto L_0x004b
                org.telegram.messenger.camera.CameraView r3 = org.telegram.messenger.camera.CameraView.this
                org.telegram.messenger.camera.CameraSession r3 = r3.cameraSession
                int r3 = r3.getWorldAngle()
                r4 = 270(0x10e, float:3.78E-43)
                if (r3 != r4) goto L_0x003e
                goto L_0x004b
            L_0x003e:
                int r3 = r1.getHeight()
                r6.videoWidth = r3
                int r3 = r1.getWidth()
                r6.videoHeight = r3
                goto L_0x0057
            L_0x004b:
                int r3 = r1.getWidth()
                r6.videoWidth = r3
                int r3 = r1.getHeight()
                r6.videoHeight = r3
            L_0x0057:
                r6.videoBitrate = r2
                r6.sharedEglContext = r8
                java.lang.Object r3 = r6.sync
                monitor-enter(r3)
                boolean r4 = r6.running     // Catch:{ all -> 0x00ac }
                if (r4 == 0) goto L_0x0064
                monitor-exit(r3)     // Catch:{ all -> 0x00ac }
                return
            L_0x0064:
                r4 = 1
                r6.running = r4     // Catch:{ all -> 0x00ac }
                java.lang.Thread r4 = new java.lang.Thread     // Catch:{ all -> 0x00ac }
                java.lang.String r5 = "TextureMovieEncoder"
                r4.<init>(r6, r5)     // Catch:{ all -> 0x00ac }
                r5 = 10
                r4.setPriority(r5)     // Catch:{ all -> 0x00ac }
                r4.start()     // Catch:{ all -> 0x00ac }
            L_0x0076:
                boolean r5 = r6.ready     // Catch:{ all -> 0x00ac }
                if (r5 != 0) goto L_0x0082
                java.lang.Object r5 = r6.sync     // Catch:{ InterruptedException -> 0x0080 }
                r5.wait()     // Catch:{ InterruptedException -> 0x0080 }
                goto L_0x0081
            L_0x0080:
                r5 = move-exception
            L_0x0081:
                goto L_0x0076
            L_0x0082:
                monitor-exit(r3)     // Catch:{ all -> 0x00ac }
                java.util.ArrayList<android.graphics.Bitmap> r3 = r6.keyframeThumbs
                r3.clear()
                r3 = 0
                r6.frameCount = r3
                org.telegram.messenger.DispatchQueue r4 = r6.generateKeyframeThumbsQueue
                if (r4 == 0) goto L_0x0097
                r4.cleanupQueue()
                org.telegram.messenger.DispatchQueue r4 = r6.generateKeyframeThumbsQueue
                r4.recycle()
            L_0x0097:
                org.telegram.messenger.DispatchQueue r4 = new org.telegram.messenger.DispatchQueue
                java.lang.String r5 = "keyframes_thumb_queque"
                r4.<init>(r5)
                r6.generateKeyframeThumbsQueue = r4
                org.telegram.messenger.camera.CameraView$EncoderHandler r4 = r6.handler
                org.telegram.messenger.camera.CameraView$EncoderHandler r5 = r6.handler
                android.os.Message r3 = r5.obtainMessage(r3)
                r4.sendMessage(r3)
                return
            L_0x00ac:
                r4 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x00ac }
                goto L_0x00b0
            L_0x00af:
                throw r4
            L_0x00b0:
                goto L_0x00af
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.VideoRecorder.startRecording(java.io.File, android.opengl.EGLContext):void");
        }

        public void stopRecording(int send) {
            this.handler.sendMessage(this.handler.obtainMessage(1, send, 0));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
            r2 = r7.zeroTimeStamps + 1;
            r7.zeroTimeStamps = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
            if (r2 <= 1) goto L_0x0027;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x0025;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
            org.telegram.messenger.FileLog.d("CameraView fix timestamp enabled");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0025, code lost:
            r0 = r10;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0028, code lost:
            r7.zeroTimeStamps = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
            r7.handler.sendMessage(r7.handler.obtainMessage(2, (int) (r0 >> 32), (int) r0, r9));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x000a, code lost:
            r0 = r8.getTimestamp();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r0 != 0) goto L_0x0028;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(android.graphics.SurfaceTexture r8, java.lang.Integer r9, long r10) {
            /*
                r7 = this;
                java.lang.Object r0 = r7.sync
                monitor-enter(r0)
                boolean r1 = r7.ready     // Catch:{ all -> 0x003e }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                return
            L_0x0009:
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                long r0 = r8.getTimestamp()
                r2 = 0
                int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r4 != 0) goto L_0x0028
                int r2 = r7.zeroTimeStamps
                r3 = 1
                int r2 = r2 + r3
                r7.zeroTimeStamps = r2
                if (r2 <= r3) goto L_0x0027
                boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r2 == 0) goto L_0x0025
                java.lang.String r2 = "CameraView fix timestamp enabled"
                org.telegram.messenger.FileLog.d(r2)
            L_0x0025:
                r0 = r10
                goto L_0x002b
            L_0x0027:
                return
            L_0x0028:
                r2 = 0
                r7.zeroTimeStamps = r2
            L_0x002b:
                org.telegram.messenger.camera.CameraView$EncoderHandler r2 = r7.handler
                org.telegram.messenger.camera.CameraView$EncoderHandler r3 = r7.handler
                r4 = 2
                r5 = 32
                long r5 = r0 >> r5
                int r6 = (int) r5
                int r5 = (int) r0
                android.os.Message r3 = r3.obtainMessage(r4, r6, r5, r9)
                r2.sendMessage(r3)
                return
            L_0x003e:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.VideoRecorder.frameAvailable(android.graphics.SurfaceTexture, java.lang.Integer, long):void");
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

        /* access modifiers changed from: private */
        public void handleAudioFrameAvailable(InstantCameraView.AudioBufferInfo input) {
            ByteBuffer inputBuffer;
            if (!this.audioStopedByTime) {
                InstantCameraView.AudioBufferInfo input2 = input;
                this.buffersToWrite.add(input2);
                if (this.audioFirst == -1) {
                    if (this.videoFirst != -1) {
                        while (true) {
                            boolean ok = false;
                            int a = 0;
                            while (true) {
                                if (a >= input2.results) {
                                    break;
                                } else if (a == 0 && Math.abs(this.videoFirst - input2.offset[a]) > 10000000) {
                                    this.desyncTime = this.videoFirst - input2.offset[a];
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("CameraView detected desync between audio and video " + this.desyncTime);
                                    }
                                } else if (input2.offset[a] >= this.videoFirst) {
                                    input2.lastWroteBuffer = a;
                                    this.audioFirst = input2.offset[a];
                                    ok = true;
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("CameraView found first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("CameraView ignore first audio frame at " + a + " timestamp = " + input2.offset[a]);
                                    }
                                    a++;
                                }
                            }
                            if (ok) {
                                break;
                            }
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("CameraView first audio frame not found, removing buffers " + input2.results);
                            }
                            this.buffersToWrite.remove(input2);
                            if (!this.buffersToWrite.isEmpty()) {
                                input2 = this.buffersToWrite.get(0);
                            } else {
                                return;
                            }
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("CameraView video record not yet started");
                        return;
                    } else {
                        return;
                    }
                }
                if (this.audioStartTime == -1) {
                    this.audioStartTime = input2.offset[input2.lastWroteBuffer];
                }
                if (this.buffersToWrite.size() > 1) {
                    input2 = this.buffersToWrite.get(0);
                }
                try {
                    drainEncoder(false);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                boolean isLast = false;
                while (input2 != null) {
                    try {
                        int inputBufferIndex = this.audioEncoder.dequeueInputBuffer(0);
                        if (inputBufferIndex >= 0) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                inputBuffer = this.audioEncoder.getInputBuffer(inputBufferIndex);
                            } else {
                                ByteBuffer inputBuffer2 = this.audioEncoder.getInputBuffers()[inputBufferIndex];
                                inputBuffer2.clear();
                                inputBuffer = inputBuffer2;
                            }
                            long startWriteTime = input2.offset[input2.lastWroteBuffer];
                            int a2 = input2.lastWroteBuffer;
                            while (true) {
                                if (a2 > input2.results) {
                                    break;
                                }
                                if (a2 < input2.results) {
                                    if (!this.running && input2.offset[a2] >= this.videoLast - this.desyncTime) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("CameraView stop audio encoding because of stoped video recording at " + input2.offset[a2] + " last video " + this.videoLast);
                                        }
                                        this.audioStopedByTime = true;
                                        isLast = true;
                                        input2 = null;
                                        this.buffersToWrite.clear();
                                    } else if (inputBuffer.remaining() < input2.read[a2]) {
                                        input2.lastWroteBuffer = a2;
                                        input2 = null;
                                        break;
                                    } else {
                                        inputBuffer.put(input2.buffer[a2]);
                                    }
                                }
                                if (a2 >= input2.results - 1) {
                                    this.buffersToWrite.remove(input2);
                                    if (this.running) {
                                        this.buffers.put(input2);
                                    }
                                    if (this.buffersToWrite.isEmpty()) {
                                        isLast = input2.last;
                                        input2 = null;
                                        break;
                                    }
                                    input2 = this.buffersToWrite.get(0);
                                }
                                a2++;
                            }
                            MediaCodec mediaCodec = this.audioEncoder;
                            int position = inputBuffer.position();
                            long j = 0;
                            if (startWriteTime != 0) {
                                j = startWriteTime - this.audioStartTime;
                            }
                            mediaCodec.queueInputBuffer(inputBufferIndex, 0, position, j, isLast ? 4 : 0);
                        }
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                        return;
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void handleVideoFrameAvailable(long timestampNanos, Integer cameraId) {
            long dt;
            long j = timestampNanos;
            Integer num = cameraId;
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (!this.lastCameraId.equals(num)) {
                this.lastTimestamp = -1;
                this.lastCameraId = num;
            }
            long dt2 = this.lastTimestamp;
            if (dt2 == -1) {
                this.lastTimestamp = j;
                if (this.currentTimestamp != 0) {
                    dt = (System.currentTimeMillis() - this.lastCommitedFrameTime) * 1000000;
                } else {
                    dt = 0;
                }
            } else {
                dt = j - dt2;
                this.lastTimestamp = j;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (!this.skippedFirst) {
                long j2 = this.skippedTime + dt;
                this.skippedTime = j2;
                if (j2 >= NUM) {
                    this.skippedFirst = true;
                } else {
                    return;
                }
            }
            this.currentTimestamp += dt;
            if (this.videoFirst == -1) {
                this.videoFirst = j / 1000;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView first video frame was at " + this.videoFirst);
                }
            }
            this.videoLast = j;
            GLES20.glUseProgram(this.drawProgram);
            GLES20.glVertexAttribPointer(this.positionHandle, 3, 5126, false, 12, CameraView.this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.positionHandle);
            GLES20.glVertexAttribPointer(this.textureHandle, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(this.textureHandle);
            GLES20.glUniformMatrix4fv(this.vertexMatrixHandle, 1, false, CameraView.this.mMVPMatrix, 0);
            GLES20.glActiveTexture(33984);
            if (CameraView.this.oldCameraTexture[0] != 0) {
                if (!this.blendEnabled) {
                    GLES20.glEnable(3042);
                    this.blendEnabled = true;
                }
                GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, CameraView.this.moldSTMatrix, 0);
                GLES20.glBindTexture(36197, CameraView.this.oldCameraTexture[0]);
                GLES20.glDrawArrays(5, 0, 4);
            }
            GLES20.glUniformMatrix4fv(this.textureMatrixHandle, 1, false, CameraView.this.mSTMatrix, 0);
            GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glDisableVertexAttribArray(this.positionHandle);
            GLES20.glDisableVertexAttribArray(this.textureHandle);
            GLES20.glBindTexture(36197, 0);
            GLES20.glUseProgram(0);
            EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, this.currentTimestamp);
            EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
        }

        /* access modifiers changed from: private */
        public void handleStopRecording(int send) {
            if (this.running) {
                this.sendWhenDone = send;
                this.running = false;
                return;
            }
            try {
                drainEncoder(true);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            MediaCodec mediaCodec = this.videoEncoder;
            if (mediaCodec != null) {
                try {
                    mediaCodec.stop();
                    this.videoEncoder.release();
                    this.videoEncoder = null;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            MediaCodec mediaCodec2 = this.audioEncoder;
            if (mediaCodec2 != null) {
                try {
                    mediaCodec2.stop();
                    this.audioEncoder.release();
                    this.audioEncoder = null;
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            MP4Builder mP4Builder = this.mediaMuxer;
            if (mP4Builder != null) {
                try {
                    mP4Builder.finishMovie();
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            Surface surface2 = this.surface;
            if (surface2 != null) {
                surface2.release();
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
            AndroidUtilities.runOnUIThread(new CameraView$VideoRecorder$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$handleStopRecording$0$org-telegram-messenger-camera-CameraView$VideoRecorder  reason: not valid java name */
        public /* synthetic */ void m1186x81855938() {
            CameraView.this.cameraSession.stopVideoRecording();
            CameraView.this.onRecordingFinishRunnable.run();
        }

        /* access modifiers changed from: private */
        public void prepareEncoder() {
            try {
                int recordBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                if (recordBufferSize <= 0) {
                    recordBufferSize = 3584;
                }
                int bufferSize = 49152;
                if (49152 < recordBufferSize) {
                    bufferSize = ((recordBufferSize / 2048) + 1) * 2048 * 2;
                }
                for (int a = 0; a < 3; a++) {
                    this.buffers.add(new InstantCameraView.AudioBufferInfo());
                }
                AudioRecord audioRecord = r9;
                AudioRecord audioRecord2 = new AudioRecord(0, 44100, 16, 2, bufferSize);
                this.audioRecorder = audioRecord;
                audioRecord.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + bufferSize);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new MediaCodec.BufferInfo();
                this.videoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString("mime", "audio/mp4a-latm");
                audioFormat.setInteger("sample-rate", 44100);
                audioFormat.setInteger("channel-count", 1);
                audioFormat.setInteger("bitrate", 32000);
                audioFormat.setInteger("max-input-size", 20480);
                MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder = createEncoderByType;
                createEncoderByType.configure(audioFormat, (Surface) null, (MediaCrypto) null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                this.firstEncode = true;
                MediaFormat format = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                format.setInteger("color-format", NUM);
                format.setInteger("bitrate", this.videoBitrate);
                format.setInteger("frame-rate", 30);
                format.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(format, (Surface) null, (MediaCrypto) null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie movie = new Mp4Movie();
                movie.setCacheFile(this.videoFile);
                movie.setRotation(0);
                movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(movie, false);
                if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    android.opengl.EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
                    this.eglDisplay = eglGetDisplay;
                    if (eglGetDisplay != EGL14.EGL_NO_DISPLAY) {
                        int[] version = new int[2];
                        if (EGL14.eglInitialize(this.eglDisplay, version, 0, version, 1)) {
                            if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                                int[] attribList = {12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344};
                                android.opengl.EGLConfig[] configs = new android.opengl.EGLConfig[1];
                                if (EGL14.eglChooseConfig(this.eglDisplay, attribList, 0, configs, 0, configs.length, new int[1], 0)) {
                                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, configs[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                                    this.eglConfig = configs[0];
                                } else {
                                    throw new RuntimeException("Unable to find a suitable EGLConfig");
                                }
                            }
                            EGL14.eglQueryContext(this.eglDisplay, this.eglContext, 12440, new int[1], 0);
                            if (this.eglSurface == EGL14.EGL_NO_SURFACE) {
                                android.opengl.EGLSurface eglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.surface, new int[]{12344}, 0);
                                this.eglSurface = eglCreateWindowSurface;
                                if (eglCreateWindowSurface == null) {
                                    throw new RuntimeException("surface was null");
                                } else if (!EGL14.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
                                    }
                                    throw new RuntimeException("eglMakeCurrent failed");
                                } else {
                                    GLES20.glBlendFunc(770, 771);
                                    float[] texData = {0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f - 0.5f, 0.5f - 0.5f, 0.5f + 0.5f, 0.5f + 0.5f, 0.5f + 0.5f};
                                    FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(texData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                                    this.textureBuffer = asFloatBuffer;
                                    asFloatBuffer.put(texData).position(0);
                                    int vertexShader = CameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                                    int fragmentShader = CameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                                    if (vertexShader != 0 && fragmentShader != 0) {
                                        int glCreateProgram = GLES20.glCreateProgram();
                                        this.drawProgram = glCreateProgram;
                                        GLES20.glAttachShader(glCreateProgram, vertexShader);
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
                                        this.vertexMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uMVPMatrix");
                                        this.textureMatrixHandle = GLES20.glGetUniformLocation(this.drawProgram, "uSTMatrix");
                                    }
                                }
                            } else {
                                throw new IllegalStateException("surface already created");
                            }
                        } else {
                            this.eglDisplay = null;
                            throw new RuntimeException("unable to initialize EGL14");
                        }
                    } else {
                        throw new RuntimeException("unable to get EGL14 display");
                    }
                } else {
                    throw new RuntimeException("EGL already set up");
                }
            } catch (Exception ioe) {
                throw new RuntimeException(ioe);
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        public void drainEncoder(boolean endOfStream) throws Exception {
            ByteBuffer encodedData;
            ByteBuffer encodedData2;
            if (endOfStream) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = null;
            int i = 21;
            if (Build.VERSION.SDK_INT < 21) {
                encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000);
                byte b = 1;
                if (encoderStatus != -1) {
                    if (encoderStatus == -3) {
                        if (Build.VERSION.SDK_INT < i) {
                            encoderOutputBuffers = this.videoEncoder.getOutputBuffers();
                        }
                    } else if (encoderStatus == -2) {
                        MediaFormat newFormat = this.videoEncoder.getOutputFormat();
                        if (this.videoTrackIndex == -5) {
                            this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat, false);
                            if (newFormat.containsKey("prepend-sps-pps-to-idr-frames") && newFormat.getInteger("prepend-sps-pps-to-idr-frames") == 1) {
                                this.prependHeaderSize = newFormat.getByteBuffer("csd-0").limit() + newFormat.getByteBuffer("csd-1").limit();
                            }
                        }
                    } else if (encoderStatus < 0) {
                        continue;
                    } else {
                        if (Build.VERSION.SDK_INT < i) {
                            encodedData2 = encoderOutputBuffers[encoderStatus];
                        } else {
                            encodedData2 = this.videoEncoder.getOutputBuffer(encoderStatus);
                        }
                        if (encodedData2 != null) {
                            if (this.videoBufferInfo.size > 1) {
                                if ((this.videoBufferInfo.flags & 2) == 0) {
                                    if (!(this.prependHeaderSize == 0 || (this.videoBufferInfo.flags & 1) == 0)) {
                                        this.videoBufferInfo.offset += this.prependHeaderSize;
                                        this.videoBufferInfo.size -= this.prependHeaderSize;
                                    }
                                    if (this.firstEncode && (this.videoBufferInfo.flags & 1) != 0) {
                                        if (this.videoBufferInfo.size > 100) {
                                            encodedData2.position(this.videoBufferInfo.offset);
                                            byte[] temp = new byte[100];
                                            encodedData2.get(temp);
                                            int nalCount = 0;
                                            int a = 0;
                                            while (true) {
                                                if (a < temp.length - 4) {
                                                    if (temp[a] == 0 && temp[a + 1] == 0 && temp[a + 2] == 0 && temp[a + 3] == 1 && (nalCount = nalCount + 1) > 1) {
                                                        this.videoBufferInfo.offset += a;
                                                        this.videoBufferInfo.size -= a;
                                                        break;
                                                    }
                                                    a++;
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                        this.firstEncode = false;
                                    }
                                    this.mediaMuxer.writeSampleData(this.videoTrackIndex, encodedData2, this.videoBufferInfo, true);
                                } else if (this.videoTrackIndex == -5) {
                                    byte[] csd = new byte[this.videoBufferInfo.size];
                                    encodedData2.limit(this.videoBufferInfo.offset + this.videoBufferInfo.size);
                                    encodedData2.position(this.videoBufferInfo.offset);
                                    encodedData2.get(csd);
                                    ByteBuffer sps = null;
                                    ByteBuffer pps = null;
                                    int a2 = this.videoBufferInfo.size - 1;
                                    while (true) {
                                        if (a2 >= 0 && a2 > 3) {
                                            if (csd[a2] == b && csd[a2 - 1] == 0 && csd[a2 - 2] == 0 && csd[a2 - 3] == 0) {
                                                sps = ByteBuffer.allocate(a2 - 3);
                                                pps = ByteBuffer.allocate(this.videoBufferInfo.size - (a2 - 3));
                                                sps.put(csd, 0, a2 - 3).position(0);
                                                pps.put(csd, a2 - 3, this.videoBufferInfo.size - (a2 - 3)).position(0);
                                                break;
                                            }
                                            a2--;
                                            b = 1;
                                        } else {
                                            break;
                                        }
                                    }
                                    MediaFormat newFormat2 = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                                    if (!(sps == null || pps == null)) {
                                        newFormat2.setByteBuffer("csd-0", sps);
                                        newFormat2.setByteBuffer("csd-1", pps);
                                    }
                                    this.videoTrackIndex = this.mediaMuxer.addTrack(newFormat2, false);
                                }
                            }
                            this.videoEncoder.releaseOutputBuffer(encoderStatus, false);
                            if ((this.videoBufferInfo.flags & 4) != 0) {
                                break;
                            }
                        } else {
                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                        }
                    }
                    i = 21;
                } else if (!endOfStream) {
                    break;
                } else {
                    i = 21;
                }
            }
            if (Build.VERSION.SDK_INT < i) {
                encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int encoderStatus2 = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0);
                if (encoderStatus2 == -1) {
                    if (!endOfStream) {
                        return;
                    }
                    if (!this.running && this.sendWhenDone == 0) {
                        return;
                    }
                } else if (encoderStatus2 == -3) {
                    if (Build.VERSION.SDK_INT < i) {
                        encoderOutputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (encoderStatus2 == -2) {
                    MediaFormat newFormat3 = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(newFormat3, true);
                    }
                } else if (encoderStatus2 < 0) {
                    continue;
                } else {
                    if (Build.VERSION.SDK_INT < i) {
                        encodedData = encoderOutputBuffers[encoderStatus2];
                    } else {
                        encodedData = this.audioEncoder.getOutputBuffer(encoderStatus2);
                    }
                    if (encodedData != null) {
                        if ((this.audioBufferInfo.flags & 2) != 0) {
                            this.audioBufferInfo.size = 0;
                        }
                        if (this.audioBufferInfo.size != 0) {
                            this.mediaMuxer.writeSampleData(this.audioTrackIndex, encodedData, this.audioBufferInfo, false);
                        }
                        this.audioEncoder.releaseOutputBuffer(encoderStatus2, false);
                        if ((this.audioBufferInfo.flags & 4) != 0) {
                            return;
                        }
                    } else {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus2 + " was null");
                    }
                }
                i = 21;
            }
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
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
            } finally {
                super.finalize();
            }
        }
    }

    private static class EncoderHandler extends Handler {
        private WeakReference<VideoRecorder> mWeakEncoder;

        public EncoderHandler(VideoRecorder encoder) {
            this.mWeakEncoder = new WeakReference<>(encoder);
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
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
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
                        encoder.handleVideoFrameAvailable((((long) inputMessage.arg1) << 32) | (((long) inputMessage.arg2) & 4294967295L), (Integer) inputMessage.obj);
                        return;
                    case 3:
                        encoder.handleAudioFrameAvailable((InstantCameraView.AudioBufferInfo) inputMessage.obj);
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

    public void setFpsLimit(int fpsLimit2) {
        this.fpsLimit = fpsLimit2;
    }
}
