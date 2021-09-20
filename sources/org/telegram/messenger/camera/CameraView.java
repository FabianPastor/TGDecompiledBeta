package org.telegram.messenger.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
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

@SuppressLint({"NewApi"})
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

    private int clamp(int i, int i2, int i3) {
        return i > i3 ? i3 : i < i2 ? i2 : i;
    }

    public void setRecordFile(File file) {
        this.recordFile = file;
    }

    public boolean startRecording(File file, Runnable runnable) {
        this.cameraThread.startRecording(file);
        this.onRecordingFinishRunnable = runnable;
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
                boolean z;
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (floatValue < 0.5f) {
                    z = false;
                } else {
                    floatValue -= 1.0f;
                    z = true;
                }
                float f = floatValue * 180.0f;
                CameraView.this.textureView.setRotationY(f);
                CameraView.this.blurredStubView.setRotationY(f);
                if (z) {
                    CameraView cameraView = CameraView.this;
                    if (!cameraView.flipHalfReached) {
                        cameraView.blurredStubView.setAlpha(1.0f);
                        CameraView.this.flipHalfReached = true;
                    }
                }
            }
        });
        this.flipAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                CameraView cameraView = CameraView.this;
                cameraView.flipAnimator = null;
                cameraView.textureView.setTranslationY(0.0f);
                CameraView.this.textureView.setRotationX(0.0f);
                CameraView.this.textureView.setRotationY(0.0f);
                CameraView.this.textureView.setScaleX(1.0f);
                CameraView.this.textureView.setScaleY(1.0f);
                CameraView.this.blurredStubView.setRotationY(0.0f);
                CameraView cameraView2 = CameraView.this;
                if (!cameraView2.flipHalfReached) {
                    cameraView2.blurredStubView.setAlpha(1.0f);
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

    public CameraView(Context context, boolean z) {
        super(context, (AttributeSet) null);
        this.isFrontface = z;
        this.initialFrontface = z;
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

    public void setOptimizeForBarcode(boolean z) {
        this.optimizeForBarcode = z;
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setOptimizeForBarcode(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        CameraSession cameraSession2;
        int i3;
        int i4;
        if (!(this.previewSize == null || (cameraSession2 = this.cameraSession) == null)) {
            cameraSession2.updateRotation();
            if (this.cameraSession.getWorldAngle() == 90 || this.cameraSession.getWorldAngle() == 270) {
                i4 = this.previewSize.getWidth();
                i3 = this.previewSize.getHeight();
            } else {
                i4 = this.previewSize.getHeight();
                i3 = this.previewSize.getWidth();
            }
            float f = (float) i4;
            float f2 = (float) i3;
            float max = Math.max(((float) View.MeasureSpec.getSize(i)) / f, ((float) View.MeasureSpec.getSize(i2)) / f2);
            ViewGroup.LayoutParams layoutParams = this.blurredStubView.getLayoutParams();
            int i5 = (int) (f * max);
            this.textureView.getLayoutParams().width = i5;
            layoutParams.width = i5;
            ViewGroup.LayoutParams layoutParams2 = this.blurredStubView.getLayoutParams();
            int i6 = (int) (max * f2);
            this.textureView.getLayoutParams().height = i6;
            layoutParams2.height = i6;
        }
        super.onMeasure(i, i2);
        checkPreviewMatrix();
    }

    public float getTextureHeight(float f, float f2) {
        CameraSession cameraSession2;
        int i;
        int i2;
        if (this.previewSize == null || (cameraSession2 = this.cameraSession) == null) {
            return f2;
        }
        if (cameraSession2.getWorldAngle() == 90 || this.cameraSession.getWorldAngle() == 270) {
            i2 = this.previewSize.getWidth();
            i = this.previewSize.getHeight();
        } else {
            i2 = this.previewSize.getHeight();
            i = this.previewSize.getWidth();
        }
        float f3 = f / ((float) i2);
        float f4 = (float) i;
        return (float) ((int) (Math.max(f3, f2 / f4) * f4));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        checkPreviewMatrix();
    }

    public void setMirror(boolean z) {
        this.mirror = z;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public TextureView getTextureView() {
        return this.textureView;
    }

    public void setUseMaxPreview(boolean z) {
        this.useMaxPreview = z;
    }

    public boolean hasFrontFaceCamera() {
        ArrayList<CameraInfo> cameras = CameraController.getInstance().getCameras();
        for (int i = 0; i < cameras.size(); i++) {
            if (cameras.get(i).frontCamera != 0) {
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

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        updateCameraInfoSize();
        this.surfaceHeight = i2;
        this.surfaceWidth = i;
        if (this.cameraThread == null && surfaceTexture != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView start create thread");
            }
            this.cameraThread = new CameraGLThread(surfaceTexture);
            checkPreviewMatrix();
        }
    }

    private void updateCameraInfoSize() {
        int i;
        Size size;
        CameraInfo cameraInfo;
        ArrayList<CameraInfo> cameras = CameraController.getInstance().getCameras();
        if (cameras != null) {
            int i2 = 0;
            while (true) {
                if (i2 >= cameras.size()) {
                    break;
                }
                cameraInfo = cameras.get(i2);
                boolean z = this.isFrontface;
                if ((!z || cameraInfo.frontCamera == 0) && (z || cameraInfo.frontCamera != 0)) {
                    i2++;
                }
            }
            this.info = cameraInfo;
            if (this.info != null) {
                Point point = AndroidUtilities.displaySize;
                Point point2 = AndroidUtilities.displaySize;
                float max = ((float) Math.max(point.x, point.y)) / ((float) Math.min(point2.x, point2.y));
                int i3 = 1920;
                int i4 = 960;
                int i5 = 1280;
                if (this.initialFrontface) {
                    size = new Size(16, 9);
                    i3 = 480;
                    i4 = 270;
                    i = 270;
                    i5 = 480;
                } else if (Math.abs(max - 1.3333334f) < 0.1f) {
                    size = new Size(4, 3);
                    if (SharedConfig.getDevicePerformanceClass() == 0) {
                        i = 960;
                        i3 = 1280;
                    } else {
                        i = 1440;
                    }
                } else {
                    size = new Size(16, 9);
                    if (SharedConfig.getDevicePerformanceClass() == 0) {
                        i = 960;
                        i3 = 1280;
                    } else {
                        i = 1080;
                    }
                    i4 = 720;
                }
                this.previewSize = CameraController.chooseOptimalSize(this.info.getPreviewSizes(), i5, i4, size);
                this.pictureSize = CameraController.chooseOptimalSize(this.info.getPictureSizes(), i3, i, size);
                requestLayout();
            }
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        this.surfaceHeight = i2;
        this.surfaceWidth = i;
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

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        CameraSession cameraSession2;
        if (!this.initied && (cameraSession2 = this.cameraSession) != null && cameraSession2.isInitied()) {
            CameraViewDelegate cameraViewDelegate = this.delegate;
            if (cameraViewDelegate != null) {
                cameraViewDelegate.onCameraInit();
            }
            this.initied = true;
        }
    }

    public void setClipTop(int i) {
        this.clipTop = i;
    }

    public void setClipBottom(int i) {
        this.clipBottom = i;
    }

    private void checkPreviewMatrix() {
        if (this.previewSize != null) {
            int width = this.textureView.getWidth();
            int height = this.textureView.getHeight();
            Matrix matrix2 = new Matrix();
            CameraSession cameraSession2 = this.cameraSession;
            if (cameraSession2 != null) {
                matrix2.postRotate((float) cameraSession2.getDisplayOrientation());
            }
            float f = (float) width;
            float f2 = (float) height;
            matrix2.postScale(f / 2000.0f, f2 / 2000.0f);
            matrix2.postTranslate(f / 2.0f, f2 / 2.0f);
            matrix2.invert(this.matrix);
            CameraGLThread cameraGLThread = this.cameraThread;
            if (cameraGLThread != null) {
                cameraGLThread.postRunnable(new CameraView$$ExternalSyntheticLambda2(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkPreviewMatrix$0() {
        CameraGLThread cameraGLThread = this.cameraThread;
        if (cameraGLThread != null && cameraGLThread.currentSession != null) {
            int worldAngle = cameraGLThread.currentSession.getWorldAngle();
            android.opengl.Matrix.setIdentityM(this.mMVPMatrix, 0);
            if (worldAngle != 0) {
                android.opengl.Matrix.rotateM(this.mMVPMatrix, 0, (float) worldAngle, 0.0f, 0.0f, 1.0f);
            }
        }
    }

    private Rect calculateTapArea(float f, float f2, float f3) {
        int intValue = Float.valueOf(((float) this.focusAreaSize) * f3).intValue();
        int i = intValue / 2;
        int clamp = clamp(((int) f) - i, 0, getWidth() - intValue);
        int clamp2 = clamp(((int) f2) - i, 0, getHeight() - intValue);
        RectF rectF = new RectF((float) clamp, (float) clamp2, (float) (clamp + intValue), (float) (clamp2 + intValue));
        this.matrix.mapRect(rectF);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    public void focusToPoint(int i, int i2) {
        float f = (float) i;
        float f2 = (float) i2;
        Rect calculateTapArea = calculateTapArea(f, f2, 1.0f);
        Rect calculateTapArea2 = calculateTapArea(f, f2, 1.5f);
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.focusToRect(calculateTapArea, calculateTapArea2);
        }
        this.focusProgress = 0.0f;
        this.innerAlpha = 1.0f;
        this.outerAlpha = 1.0f;
        this.cx = i;
        this.cy = i2;
        this.lastDrawTime = System.currentTimeMillis();
        invalidate();
    }

    public void setZoom(float f) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setZoom(f);
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

    public void destroy(boolean z, Runnable runnable) {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.destroy();
            CameraController.getInstance().close(this.cameraSession, !z ? new CountDownLatch(1) : null, runnable);
        }
    }

    public Matrix getMatrix() {
        return this.txform;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean drawChild = super.drawChild(canvas, view, j);
        if (!(this.focusProgress == 1.0f && this.innerAlpha == 0.0f && this.outerAlpha == 0.0f)) {
            int dp = AndroidUtilities.dp(30.0f);
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastDrawTime;
            if (j2 < 0 || j2 > 17) {
                j2 = 17;
            }
            this.lastDrawTime = currentTimeMillis;
            this.outerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.outerAlpha) * 255.0f));
            this.innerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.innerAlpha) * 127.0f));
            float interpolation = this.interpolator.getInterpolation(this.focusProgress);
            float f = (float) dp;
            canvas.drawCircle((float) this.cx, (float) this.cy, ((1.0f - interpolation) * f) + f, this.outerPaint);
            canvas.drawCircle((float) this.cx, (float) this.cy, f * interpolation, this.innerPaint);
            float f2 = this.focusProgress;
            if (f2 < 1.0f) {
                float f3 = f2 + (((float) j2) / 200.0f);
                this.focusProgress = f3;
                if (f3 > 1.0f) {
                    this.focusProgress = 1.0f;
                }
                invalidate();
            } else {
                float f4 = this.innerAlpha;
                if (f4 != 0.0f) {
                    float f5 = f4 - (((float) j2) / 150.0f);
                    this.innerAlpha = f5;
                    if (f5 < 0.0f) {
                        this.innerAlpha = 0.0f;
                    }
                    invalidate();
                } else {
                    float f6 = this.outerAlpha;
                    if (f6 != 0.0f) {
                        float f7 = f6 - (((float) j2) / 150.0f);
                        this.outerAlpha = f7;
                        if (f7 < 0.0f) {
                            this.outerAlpha = 0.0f;
                        }
                        invalidate();
                    }
                }
            }
        }
        return drawChild;
    }

    public void startTakePictureAnimation() {
        this.takePictureProgress = 0.0f;
        invalidate();
        runHaptic();
    }

    public void runHaptic() {
        long[] jArr = {0, 1};
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService("vibrator");
            VibrationEffect createWaveform = VibrationEffect.createWaveform(jArr, -1);
            vibrator.cancel();
            vibrator.vibrate(createWaveform);
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

        public CameraGLThread(SurfaceTexture surfaceTexture2) {
            super("CameraGLThread");
            this.surfaceTexture = surfaceTexture2;
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
            if (!this.egl10.eglInitialize(eglGetDisplay, new int[2])) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] iArr = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!this.egl10.eglChooseConfig(this.eglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (iArr[0] > 0) {
                EGLConfig eGLConfig = eGLConfigArr[0];
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
                    } else if (!this.egl10.eglMakeCurrent(this.eglDisplay, eglCreateWindowSurface, eglCreateWindowSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    } else {
                        this.eglContext.getGL();
                        android.opengl.Matrix.setIdentityM(CameraView.this.mSTMatrix, 0);
                        int access$300 = CameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                        int access$3002 = CameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                        if (access$300 == 0 || access$3002 == 0) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("failed creating shader");
                            }
                            finish();
                            return false;
                        }
                        int glCreateProgram = GLES20.glCreateProgram();
                        this.drawProgram = glCreateProgram;
                        GLES20.glAttachShader(glCreateProgram, access$300);
                        GLES20.glAttachShader(this.drawProgram, access$3002);
                        GLES20.glLinkProgram(this.drawProgram);
                        int[] iArr2 = new int[1];
                        GLES20.glGetProgramiv(this.drawProgram, 35714, iArr2, 0);
                        if (iArr2[0] == 0) {
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
                        FloatBuffer unused = CameraView.this.vertexBuffer = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        CameraView.this.vertexBuffer.put(new float[]{-1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f}).position(0);
                        FloatBuffer unused2 = CameraView.this.textureBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
                        CameraView.this.textureBuffer.put(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f}).position(0);
                        SurfaceTexture surfaceTexture3 = new SurfaceTexture(CameraView.this.cameraTexture[0]);
                        this.cameraSurface = surfaceTexture3;
                        surfaceTexture3.setOnFrameAvailableListener(new CameraView$CameraGLThread$$ExternalSyntheticLambda0(this));
                        CameraView.this.createCamera(this.cameraSurface);
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
        public /* synthetic */ void lambda$initGL$0(SurfaceTexture surfaceTexture2) {
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

        public void setCurrentSession(CameraSession cameraSession) {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(3, cameraSession), 0);
            }
        }

        private void onDraw(Integer num, boolean z) {
            boolean z2;
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
                if (z) {
                    try {
                        this.cameraSurface.updateTexImage();
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
                synchronized (CameraView.this.layoutLock) {
                    if (CameraView.this.fpsLimit > 0) {
                        long nanoTime = System.nanoTime();
                        CameraView cameraView = CameraView.this;
                        long j = cameraView.nextFrameTimeNs;
                        if (nanoTime < j) {
                            z2 = false;
                        } else {
                            cameraView.nextFrameTimeNs = j + (TimeUnit.SECONDS.toNanos(1) / ((long) CameraView.this.fpsLimit));
                            CameraView cameraView2 = CameraView.this;
                            cameraView2.nextFrameTimeNs = Math.max(cameraView2.nextFrameTimeNs, nanoTime);
                        }
                    }
                    z2 = true;
                }
                CameraSession cameraSession = this.currentSession;
                if (cameraSession != null && cameraSession.cameraInfo.cameraId == num.intValue()) {
                    if (this.recording && CameraView.this.videoEncoder != null) {
                        CameraView.this.videoEncoder.frameAvailable(this.cameraSurface, num, System.nanoTime());
                    }
                    if (z2) {
                        this.cameraSurface.getTransformMatrix(CameraView.this.mSTMatrix);
                        this.egl10.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, this.array);
                        int[] iArr = this.array;
                        int i = iArr[0];
                        this.egl10.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, iArr);
                        GLES20.glViewport(0, 0, i, this.array[0]);
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
                            CameraView cameraView3 = CameraView.this;
                            if (!cameraView3.firstFrameRendered) {
                                cameraView3.firstFrameRendered = true;
                                AndroidUtilities.runOnUIThread(new CameraView$CameraGLThread$$ExternalSyntheticLambda2(this));
                            }
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onDraw$1() {
            CameraView.this.onFirstFrameRendered();
        }

        public void run() {
            this.initied = initGL();
            super.run();
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                onDraw((Integer) message.obj, true);
            } else if (i == 1) {
                finish();
                if (this.recording) {
                    CameraView.this.videoEncoder.stopRecording(message.arg1);
                }
                Looper myLooper = Looper.myLooper();
                if (myLooper != null) {
                    myLooper.quit();
                }
            } else if (i == 2) {
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
                    this.cameraId = (Integer) message.obj;
                    GLES20.glBindTexture(36197, CameraView.this.cameraTexture[0]);
                    GLES20.glTexParameteri(36197, 10241, 9729);
                    GLES20.glTexParameteri(36197, 10240, 9729);
                    GLES20.glTexParameteri(36197, 10242, 33071);
                    GLES20.glTexParameteri(36197, 10243, 33071);
                    SurfaceTexture surfaceTexture3 = new SurfaceTexture(CameraView.this.cameraTexture[0]);
                    this.cameraSurface = surfaceTexture3;
                    surfaceTexture3.setOnFrameAvailableListener(new CameraView$CameraGLThread$$ExternalSyntheticLambda1(this));
                    CameraView.this.createCamera(this.cameraSurface);
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
            } else if (i == 3) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView set gl rednderer session");
                }
                CameraSession cameraSession = (CameraSession) message.obj;
                if (this.currentSession != cameraSession) {
                    this.currentSession = cameraSession;
                    this.cameraId = Integer.valueOf(cameraSession.cameraInfo.cameraId);
                }
                this.currentSession.updateRotation();
                int worldAngle = this.currentSession.getWorldAngle();
                android.opengl.Matrix.setIdentityM(CameraView.this.mMVPMatrix, 0);
                if (worldAngle != 0) {
                    android.opengl.Matrix.rotateM(CameraView.this.mMVPMatrix, 0, (float) worldAngle, 0.0f, 0.0f, 1.0f);
                }
            } else if (i != 4) {
                if (i == 5) {
                    if (CameraView.this.videoEncoder != null) {
                        CameraView.this.videoEncoder.stopRecording(0);
                        VideoRecorder unused = CameraView.this.videoEncoder = null;
                    }
                    this.recording = false;
                }
            } else if (this.initied) {
                CameraView cameraView = CameraView.this;
                cameraView.recordFile = (File) message.obj;
                VideoRecorder unused2 = cameraView.videoEncoder = new VideoRecorder();
                this.recording = true;
                CameraView.this.videoEncoder.startRecording(CameraView.this.recordFile, EGL14.eglGetCurrentContext());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$handleMessage$2(SurfaceTexture surfaceTexture2) {
            requestRender();
        }

        public void shutdown(int i) {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(1, i, 0), 0);
            }
        }

        public void requestRender() {
            Handler handler = CameraView.this.getHandler();
            if (handler != null) {
                sendMessage(handler.obtainMessage(0, this.cameraId), 0);
            }
        }

        public boolean startRecording(File file) {
            Handler handler = CameraView.this.getHandler();
            if (handler == null) {
                return true;
            }
            sendMessage(handler.obtainMessage(4, file), 0);
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
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    CameraView.this.blurredStubView.setVisibility(8);
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public int loadShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(glCreateShader));
        }
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }

    /* access modifiers changed from: private */
    public void createCamera(SurfaceTexture surfaceTexture) {
        AndroidUtilities.runOnUIThread(new CameraView$$ExternalSyntheticLambda3(this, surfaceTexture));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createCamera$3(SurfaceTexture surfaceTexture) {
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
                CameraController.getInstance().open(this.cameraSession, surfaceTexture, new CameraView$$ExternalSyntheticLambda1(this), new CameraView$$ExternalSyntheticLambda0(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createCamera$1() {
        if (this.cameraSession != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("CameraView camera initied");
            }
            this.cameraSession.setInitied();
            requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createCamera$2() {
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
                    if (org.telegram.messenger.camera.CameraView.VideoRecorder.access$1700(r13.this$1) == 0) goto L_0x00e4;
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r13 = this;
                        r0 = -1
                        r2 = 0
                        r4 = r0
                        r3 = 0
                    L_0x0005:
                        r6 = 1
                        if (r3 != 0) goto L_0x00e4
                        org.telegram.messenger.camera.CameraView$VideoRecorder r7 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r7 = r7.running
                        if (r7 != 0) goto L_0x0031
                        org.telegram.messenger.camera.CameraView$VideoRecorder r7 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        android.media.AudioRecord r7 = r7.audioRecorder
                        int r7 = r7.getRecordingState()
                        if (r7 == r6) goto L_0x0031
                        org.telegram.messenger.camera.CameraView$VideoRecorder r7 = org.telegram.messenger.camera.CameraView.VideoRecorder.this     // Catch:{ Exception -> 0x0026 }
                        android.media.AudioRecord r7 = r7.audioRecorder     // Catch:{ Exception -> 0x0026 }
                        r7.stop()     // Catch:{ Exception -> 0x0026 }
                        goto L_0x0027
                    L_0x0026:
                        r3 = 1
                    L_0x0027:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r7 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        int r7 = r7.sendWhenDone
                        if (r7 != 0) goto L_0x0031
                        goto L_0x00e4
                    L_0x0031:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r7 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r7 = r7.buffers
                        boolean r7 = r7.isEmpty()
                        if (r7 == 0) goto L_0x0043
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r7 = new org.telegram.ui.Components.InstantCameraView$AudioBufferInfo
                        r7.<init>()
                        goto L_0x004f
                    L_0x0043:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r7 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        java.util.concurrent.ArrayBlockingQueue r7 = r7.buffers
                        java.lang.Object r7 = r7.poll()
                        org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r7 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r7
                    L_0x004f:
                        r7.lastWroteBuffer = r2
                        r8 = 10
                        r7.results = r8
                        r9 = 0
                    L_0x0056:
                        if (r9 >= r8) goto L_0x009d
                        int r10 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
                        if (r10 != 0) goto L_0x0063
                        long r4 = java.lang.System.nanoTime()
                        r10 = 1000(0x3e8, double:4.94E-321)
                        long r4 = r4 / r10
                    L_0x0063:
                        java.nio.ByteBuffer[] r10 = r7.buffer
                        r10 = r10[r9]
                        r10.rewind()
                        org.telegram.messenger.camera.CameraView$VideoRecorder r11 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        android.media.AudioRecord r11 = r11.audioRecorder
                        r12 = 2048(0x800, float:2.87E-42)
                        int r10 = r11.read(r10, r12)
                        if (r10 > 0) goto L_0x0085
                        r7.results = r9
                        org.telegram.messenger.camera.CameraView$VideoRecorder r9 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r9 = r9.running
                        if (r9 != 0) goto L_0x009d
                        r7.last = r6
                        goto L_0x009d
                    L_0x0085:
                        long[] r11 = r7.offset
                        r11[r9] = r4
                        int[] r11 = r7.read
                        r11[r9] = r10
                        r11 = 1000000(0xvar_, float:1.401298E-39)
                        int r10 = r10 * r11
                        r11 = 44100(0xaCLASSNAME, float:6.1797E-41)
                        int r10 = r10 / r11
                        int r10 = r10 / 2
                        long r10 = (long) r10
                        long r4 = r4 + r10
                        int r9 = r9 + 1
                        goto L_0x0056
                    L_0x009d:
                        int r9 = r7.results
                        if (r9 >= 0) goto L_0x00bf
                        boolean r9 = r7.last
                        if (r9 == 0) goto L_0x00a6
                        goto L_0x00bf
                    L_0x00a6:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r8 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r8 = r8.running
                        if (r8 != 0) goto L_0x00b1
                        r3 = 1
                        goto L_0x0005
                    L_0x00b1:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r6 = org.telegram.messenger.camera.CameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00bc }
                        java.util.concurrent.ArrayBlockingQueue r6 = r6.buffers     // Catch:{ Exception -> 0x00bc }
                        r6.put(r7)     // Catch:{ Exception -> 0x00bc }
                        goto L_0x0005
                    L_0x00bc:
                        goto L_0x0005
                    L_0x00bf:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r9 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        boolean r9 = r9.running
                        if (r9 != 0) goto L_0x00cc
                        int r9 = r7.results
                        if (r9 >= r8) goto L_0x00cc
                        goto L_0x00cd
                    L_0x00cc:
                        r6 = r3
                    L_0x00cd:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r3 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r3 = r3.handler
                        org.telegram.messenger.camera.CameraView$VideoRecorder r8 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r8 = r8.handler
                        r9 = 3
                        android.os.Message r7 = r8.obtainMessage(r9, r7)
                        r3.sendMessage(r7)
                        r3 = r6
                        goto L_0x0005
                    L_0x00e4:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r0 = org.telegram.messenger.camera.CameraView.VideoRecorder.this     // Catch:{ Exception -> 0x00ee }
                        android.media.AudioRecord r0 = r0.audioRecorder     // Catch:{ Exception -> 0x00ee }
                        r0.release()     // Catch:{ Exception -> 0x00ee }
                        goto L_0x00f2
                    L_0x00ee:
                        r0 = move-exception
                        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                    L_0x00f2:
                        org.telegram.messenger.camera.CameraView$VideoRecorder r0 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r0 = r0.handler
                        org.telegram.messenger.camera.CameraView$VideoRecorder r1 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        org.telegram.messenger.camera.CameraView$EncoderHandler r1 = r1.handler
                        org.telegram.messenger.camera.CameraView$VideoRecorder r3 = org.telegram.messenger.camera.CameraView.VideoRecorder.this
                        int r3 = r3.sendWhenDone
                        android.os.Message r1 = r1.obtainMessage(r6, r3, r2)
                        r0.sendMessage(r1)
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.VideoRecorder.AnonymousClass1.run():void");
                }
            };
        }

        /* JADX WARNING: Code restructure failed: missing block: B:25:0x007d, code lost:
            r3.keyframeThumbs.clear();
            r3.frameCount = 0;
            r5 = r3.generateKeyframeThumbsQueue;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0087, code lost:
            if (r5 == null) goto L_0x0091;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0089, code lost:
            r5.cleanupQueue();
            r3.generateKeyframeThumbsQueue.recycle();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0091, code lost:
            r3.generateKeyframeThumbsQueue = new org.telegram.messenger.DispatchQueue("keyframes_thumb_queque");
            r3.handler.sendMessage(r3.handler.obtainMessage(0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x00a5, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void startRecording(java.io.File r4, android.opengl.EGLContext r5) {
            /*
                r3 = this;
                java.lang.String r0 = android.os.Build.DEVICE
                org.telegram.messenger.camera.CameraView r0 = org.telegram.messenger.camera.CameraView.this
                org.telegram.messenger.camera.Size r0 = r0.previewSize
                int r1 = r0.mHeight
                int r2 = r0.mWidth
                int r1 = java.lang.Math.min(r1, r2)
                r2 = 720(0x2d0, float:1.009E-42)
                if (r1 < r2) goto L_0x0018
                r1 = 3500000(0x3567e0, float:4.904545E-39)
                goto L_0x001b
            L_0x0018:
                r1 = 1800000(0x1b7740, float:2.522337E-39)
            L_0x001b:
                r3.videoFile = r4
                org.telegram.messenger.camera.CameraView r4 = org.telegram.messenger.camera.CameraView.this
                org.telegram.messenger.camera.CameraSession r4 = r4.cameraSession
                int r4 = r4.getWorldAngle()
                r2 = 90
                if (r4 == r2) goto L_0x0047
                org.telegram.messenger.camera.CameraView r4 = org.telegram.messenger.camera.CameraView.this
                org.telegram.messenger.camera.CameraSession r4 = r4.cameraSession
                int r4 = r4.getWorldAngle()
                r2 = 270(0x10e, float:3.78E-43)
                if (r4 != r2) goto L_0x003a
                goto L_0x0047
            L_0x003a:
                int r4 = r0.getHeight()
                r3.videoWidth = r4
                int r4 = r0.getWidth()
                r3.videoHeight = r4
                goto L_0x0053
            L_0x0047:
                int r4 = r0.getWidth()
                r3.videoWidth = r4
                int r4 = r0.getHeight()
                r3.videoHeight = r4
            L_0x0053:
                r3.videoBitrate = r1
                r3.sharedEglContext = r5
                java.lang.Object r4 = r3.sync
                monitor-enter(r4)
                boolean r5 = r3.running     // Catch:{ all -> 0x00a6 }
                if (r5 == 0) goto L_0x0060
                monitor-exit(r4)     // Catch:{ all -> 0x00a6 }
                return
            L_0x0060:
                r5 = 1
                r3.running = r5     // Catch:{ all -> 0x00a6 }
                java.lang.Thread r5 = new java.lang.Thread     // Catch:{ all -> 0x00a6 }
                java.lang.String r0 = "TextureMovieEncoder"
                r5.<init>(r3, r0)     // Catch:{ all -> 0x00a6 }
                r0 = 10
                r5.setPriority(r0)     // Catch:{ all -> 0x00a6 }
                r5.start()     // Catch:{ all -> 0x00a6 }
            L_0x0072:
                boolean r5 = r3.ready     // Catch:{ all -> 0x00a6 }
                if (r5 != 0) goto L_0x007c
                java.lang.Object r5 = r3.sync     // Catch:{ InterruptedException -> 0x0072 }
                r5.wait()     // Catch:{ InterruptedException -> 0x0072 }
                goto L_0x0072
            L_0x007c:
                monitor-exit(r4)     // Catch:{ all -> 0x00a6 }
                java.util.ArrayList<android.graphics.Bitmap> r4 = r3.keyframeThumbs
                r4.clear()
                r4 = 0
                r3.frameCount = r4
                org.telegram.messenger.DispatchQueue r5 = r3.generateKeyframeThumbsQueue
                if (r5 == 0) goto L_0x0091
                r5.cleanupQueue()
                org.telegram.messenger.DispatchQueue r5 = r3.generateKeyframeThumbsQueue
                r5.recycle()
            L_0x0091:
                org.telegram.messenger.DispatchQueue r5 = new org.telegram.messenger.DispatchQueue
                java.lang.String r0 = "keyframes_thumb_queque"
                r5.<init>(r0)
                r3.generateKeyframeThumbsQueue = r5
                org.telegram.messenger.camera.CameraView$EncoderHandler r5 = r3.handler
                org.telegram.messenger.camera.CameraView$EncoderHandler r0 = r3.handler
                android.os.Message r4 = r0.obtainMessage(r4)
                r5.sendMessage(r4)
                return
            L_0x00a6:
                r5 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x00a6 }
                goto L_0x00aa
            L_0x00a9:
                throw r5
            L_0x00aa:
                goto L_0x00a9
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.VideoRecorder.startRecording(java.io.File, android.opengl.EGLContext):void");
        }

        public void stopRecording(int i) {
            this.handler.sendMessage(this.handler.obtainMessage(1, i, 0));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
            r5 = r4.zeroTimeStamps + 1;
            r4.zeroTimeStamps = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
            if (r5 <= 1) goto L_0x0026;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x002b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0020, code lost:
            org.telegram.messenger.FileLog.d("CameraView fix timestamp enabled");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0026, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0027, code lost:
            r4.zeroTimeStamps = 0;
            r7 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x002b, code lost:
            r4.handler.sendMessage(r4.handler.obtainMessage(2, (int) (r7 >> 32), (int) r7, r6));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x000a, code lost:
            r0 = r5.getTimestamp();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r0 != 0) goto L_0x0027;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void frameAvailable(android.graphics.SurfaceTexture r5, java.lang.Integer r6, long r7) {
            /*
                r4 = this;
                java.lang.Object r0 = r4.sync
                monitor-enter(r0)
                boolean r1 = r4.ready     // Catch:{ all -> 0x003e }
                if (r1 != 0) goto L_0x0009
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                return
            L_0x0009:
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                long r0 = r5.getTimestamp()
                r2 = 0
                int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r5 != 0) goto L_0x0027
                int r5 = r4.zeroTimeStamps
                r0 = 1
                int r5 = r5 + r0
                r4.zeroTimeStamps = r5
                if (r5 <= r0) goto L_0x0026
                boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r5 == 0) goto L_0x002b
                java.lang.String r5 = "CameraView fix timestamp enabled"
                org.telegram.messenger.FileLog.d(r5)
                goto L_0x002b
            L_0x0026:
                return
            L_0x0027:
                r5 = 0
                r4.zeroTimeStamps = r5
                r7 = r0
            L_0x002b:
                org.telegram.messenger.camera.CameraView$EncoderHandler r5 = r4.handler
                org.telegram.messenger.camera.CameraView$EncoderHandler r0 = r4.handler
                r1 = 2
                r2 = 32
                long r2 = r7 >> r2
                int r3 = (int) r2
                int r8 = (int) r7
                android.os.Message r6 = r0.obtainMessage(r1, r3, r8, r6)
                r5.sendMessage(r6)
                return
            L_0x003e:
                r5 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x003e }
                throw r5
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
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00c8  */
        /* JADX WARNING: Removed duplicated region for block: B:97:0x00fb A[EDGE_INSN: B:97:0x00fb->B:38:0x00fb ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleAudioFrameAvailable(org.telegram.ui.Components.InstantCameraView.AudioBufferInfo r17) {
            /*
                r16 = this;
                r1 = r16
                boolean r0 = r1.audioStopedByTime
                if (r0 == 0) goto L_0x0007
                return
            L_0x0007:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                r2 = r17
                r0.add(r2)
                long r3 = r1.audioFirst
                r5 = -1
                r7 = 0
                r8 = 1
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x00fb
                long r3 = r1.videoFirst
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x0028
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x0027
                java.lang.String r0 = "CameraView video record not yet started"
                org.telegram.messenger.FileLog.d(r0)
            L_0x0027:
                return
            L_0x0028:
                r0 = 0
            L_0x0029:
                int r3 = r2.results
                if (r0 >= r3) goto L_0x00c5
                if (r0 != 0) goto L_0x0069
                long r3 = r1.videoFirst
                long[] r9 = r2.offset
                r10 = r9[r0]
                long r3 = r3 - r10
                long r3 = java.lang.Math.abs(r3)
                r9 = 10000000(0x989680, double:4.9406565E-317)
                int r11 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r11 <= 0) goto L_0x0069
                long r3 = r1.videoFirst
                long[] r9 = r2.offset
                r10 = r9[r0]
                long r3 = r3 - r10
                r1.desyncTime = r3
                r3 = r9[r0]
                r1.audioFirst = r3
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x009d
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "CameraView detected desync between audio and video "
                r0.append(r3)
                long r3 = r1.desyncTime
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
                goto L_0x009d
            L_0x0069:
                long[] r3 = r2.offset
                r9 = r3[r0]
                long r11 = r1.videoFirst
                java.lang.String r4 = " timestamp = "
                int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                if (r13 < 0) goto L_0x009f
                r2.lastWroteBuffer = r0
                r9 = r3[r0]
                r1.audioFirst = r9
                boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r3 == 0) goto L_0x009d
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r9 = "CameraView found first audio frame at "
                r3.append(r9)
                r3.append(r0)
                r3.append(r4)
                long[] r4 = r2.offset
                r9 = r4[r0]
                r3.append(r9)
                java.lang.String r0 = r3.toString()
                org.telegram.messenger.FileLog.d(r0)
            L_0x009d:
                r0 = 1
                goto L_0x00c6
            L_0x009f:
                boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r3 == 0) goto L_0x00c1
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r9 = "CameraView ignore first audio frame at "
                r3.append(r9)
                r3.append(r0)
                r3.append(r4)
                long[] r4 = r2.offset
                r9 = r4[r0]
                r3.append(r9)
                java.lang.String r3 = r3.toString()
                org.telegram.messenger.FileLog.d(r3)
            L_0x00c1:
                int r0 = r0 + 1
                goto L_0x0029
            L_0x00c5:
                r0 = 0
            L_0x00c6:
                if (r0 != 0) goto L_0x00fb
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x00e2
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "CameraView first audio frame not found, removing buffers "
                r0.append(r3)
                int r3 = r2.results
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.d(r0)
            L_0x00e2:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                r0.remove(r2)
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00fa
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                java.lang.Object r0 = r0.get(r7)
                r2 = r0
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2
                goto L_0x0028
            L_0x00fa:
                return
            L_0x00fb:
                long r3 = r1.audioStartTime
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x0109
                long[] r0 = r2.offset
                int r3 = r2.lastWroteBuffer
                r3 = r0[r3]
                r1.audioStartTime = r3
            L_0x0109:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                int r0 = r0.size()
                if (r0 <= r8) goto L_0x011a
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite
                java.lang.Object r0 = r0.get(r7)
                r2 = r0
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2
            L_0x011a:
                r1.drainEncoder(r7)     // Catch:{ Exception -> 0x011e }
                goto L_0x0123
            L_0x011e:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0123:
                r0 = 0
            L_0x0124:
                if (r2 == 0) goto L_0x020c
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x0208 }
                r4 = 0
                int r10 = r3.dequeueInputBuffer(r4)     // Catch:{ all -> 0x0208 }
                if (r10 < 0) goto L_0x0202
                int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0208 }
                r6 = 21
                if (r3 < r6) goto L_0x013d
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x0208 }
                java.nio.ByteBuffer r3 = r3.getInputBuffer(r10)     // Catch:{ all -> 0x0208 }
                goto L_0x0148
            L_0x013d:
                android.media.MediaCodec r3 = r1.audioEncoder     // Catch:{ all -> 0x0208 }
                java.nio.ByteBuffer[] r3 = r3.getInputBuffers()     // Catch:{ all -> 0x0208 }
                r3 = r3[r10]     // Catch:{ all -> 0x0208 }
                r3.clear()     // Catch:{ all -> 0x0208 }
            L_0x0148:
                long[] r6 = r2.offset     // Catch:{ all -> 0x0208 }
                int r9 = r2.lastWroteBuffer     // Catch:{ all -> 0x0208 }
                r11 = r6[r9]     // Catch:{ all -> 0x0208 }
            L_0x014e:
                int r6 = r2.results     // Catch:{ all -> 0x0208 }
                r13 = 0
                if (r9 > r6) goto L_0x01e0
                if (r9 >= r6) goto L_0x01ac
                boolean r6 = r1.running     // Catch:{ all -> 0x0208 }
                if (r6 != 0) goto L_0x0197
                long[] r6 = r2.offset     // Catch:{ all -> 0x0208 }
                r14 = r6[r9]     // Catch:{ all -> 0x0208 }
                long r4 = r1.videoLast     // Catch:{ all -> 0x0208 }
                long r7 = r1.desyncTime     // Catch:{ all -> 0x0208 }
                long r4 = r4 - r7
                int r7 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
                if (r7 < 0) goto L_0x0197
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x0208 }
                if (r0 == 0) goto L_0x018c
                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0208 }
                r0.<init>()     // Catch:{ all -> 0x0208 }
                java.lang.String r4 = "CameraView stop audio encoding because of stoped video recording at "
                r0.append(r4)     // Catch:{ all -> 0x0208 }
                long[] r2 = r2.offset     // Catch:{ all -> 0x0208 }
                r4 = r2[r9]     // Catch:{ all -> 0x0208 }
                r0.append(r4)     // Catch:{ all -> 0x0208 }
                java.lang.String r2 = " last video "
                r0.append(r2)     // Catch:{ all -> 0x0208 }
                long r4 = r1.videoLast     // Catch:{ all -> 0x0208 }
                r0.append(r4)     // Catch:{ all -> 0x0208 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0208 }
                org.telegram.messenger.FileLog.d(r0)     // Catch:{ all -> 0x0208 }
            L_0x018c:
                r2 = 1
                r1.audioStopedByTime = r2     // Catch:{ all -> 0x0208 }
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r0 = r1.buffersToWrite     // Catch:{ all -> 0x0208 }
                r0.clear()     // Catch:{ all -> 0x0208 }
                r2 = r13
                r0 = 1
                goto L_0x01e0
            L_0x0197:
                int r4 = r3.remaining()     // Catch:{ all -> 0x0208 }
                int[] r5 = r2.read     // Catch:{ all -> 0x0208 }
                r5 = r5[r9]     // Catch:{ all -> 0x0208 }
                if (r4 >= r5) goto L_0x01a5
                r2.lastWroteBuffer = r9     // Catch:{ all -> 0x0208 }
                r2 = r13
                goto L_0x01e0
            L_0x01a5:
                java.nio.ByteBuffer[] r4 = r2.buffer     // Catch:{ all -> 0x0208 }
                r4 = r4[r9]     // Catch:{ all -> 0x0208 }
                r3.put(r4)     // Catch:{ all -> 0x0208 }
            L_0x01ac:
                int r4 = r2.results     // Catch:{ all -> 0x0208 }
                r5 = 1
                int r4 = r4 - r5
                if (r9 < r4) goto L_0x01d7
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffersToWrite     // Catch:{ all -> 0x0208 }
                r4.remove(r2)     // Catch:{ all -> 0x0208 }
                boolean r4 = r1.running     // Catch:{ all -> 0x0208 }
                if (r4 == 0) goto L_0x01c0
                java.util.concurrent.ArrayBlockingQueue<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffers     // Catch:{ all -> 0x0208 }
                r4.put(r2)     // Catch:{ all -> 0x0208 }
            L_0x01c0:
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r4 = r1.buffersToWrite     // Catch:{ all -> 0x0208 }
                boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x0208 }
                if (r4 != 0) goto L_0x01d2
                java.util.ArrayList<org.telegram.ui.Components.InstantCameraView$AudioBufferInfo> r2 = r1.buffersToWrite     // Catch:{ all -> 0x0208 }
                r4 = 0
                java.lang.Object r2 = r2.get(r4)     // Catch:{ all -> 0x0208 }
                org.telegram.ui.Components.InstantCameraView$AudioBufferInfo r2 = (org.telegram.ui.Components.InstantCameraView.AudioBufferInfo) r2     // Catch:{ all -> 0x0208 }
                goto L_0x01d8
            L_0x01d2:
                r4 = 0
                boolean r0 = r2.last     // Catch:{ all -> 0x0208 }
                r2 = r13
                goto L_0x01e2
            L_0x01d7:
                r4 = 0
            L_0x01d8:
                int r9 = r9 + 1
                r4 = 0
                r7 = 0
                r8 = 1
                goto L_0x014e
            L_0x01e0:
                r4 = 0
                r5 = 1
            L_0x01e2:
                android.media.MediaCodec r9 = r1.audioEncoder     // Catch:{ all -> 0x0208 }
                r6 = 0
                int r3 = r3.position()     // Catch:{ all -> 0x0208 }
                r7 = 0
                int r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
                if (r13 != 0) goto L_0x01f1
            L_0x01ef:
                r13 = r7
                goto L_0x01f6
            L_0x01f1:
                long r7 = r1.audioStartTime     // Catch:{ all -> 0x0208 }
                long r7 = r11 - r7
                goto L_0x01ef
            L_0x01f6:
                if (r0 == 0) goto L_0x01fb
                r7 = 4
                r15 = 4
                goto L_0x01fc
            L_0x01fb:
                r15 = 0
            L_0x01fc:
                r11 = r6
                r12 = r3
                r9.queueInputBuffer(r10, r11, r12, r13, r15)     // Catch:{ all -> 0x0208 }
                goto L_0x0204
            L_0x0202:
                r4 = 0
                r5 = 1
            L_0x0204:
                r7 = 0
                r8 = 1
                goto L_0x0124
            L_0x0208:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x020c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.VideoRecorder.handleAudioFrameAvailable(org.telegram.ui.Components.InstantCameraView$AudioBufferInfo):void");
        }

        /* access modifiers changed from: private */
        public void handleVideoFrameAvailable(long j, Integer num) {
            try {
                drainEncoder(false);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (!this.lastCameraId.equals(num)) {
                this.lastTimestamp = -1;
                this.lastCameraId = num;
            }
            long j2 = this.lastTimestamp;
            long j3 = 0;
            if (j2 == -1) {
                this.lastTimestamp = j;
                if (this.currentTimestamp != 0) {
                    j3 = 1000000 * (System.currentTimeMillis() - this.lastCommitedFrameTime);
                }
            } else {
                j3 = j - j2;
                this.lastTimestamp = j;
            }
            this.lastCommitedFrameTime = System.currentTimeMillis();
            if (!this.skippedFirst) {
                long j4 = this.skippedTime + j3;
                this.skippedTime = j4;
                if (j4 >= NUM) {
                    this.skippedFirst = true;
                } else {
                    return;
                }
            }
            this.currentTimestamp += j3;
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
        public void handleStopRecording(int i) {
            if (this.running) {
                this.sendWhenDone = i;
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
            android.opengl.EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != EGL14.EGL_NO_DISPLAY) {
                android.opengl.EGLSurface eGLSurface = EGL14.EGL_NO_SURFACE;
                EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL14.EGL_NO_CONTEXT);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$handleStopRecording$0() {
            CameraView.this.cameraSession.stopVideoRecording();
            CameraView.this.onRecordingFinishRunnable.run();
        }

        /* access modifiers changed from: private */
        public void prepareEncoder() {
            try {
                int minBufferSize = AudioRecord.getMinBufferSize(44100, 16, 2);
                if (minBufferSize <= 0) {
                    minBufferSize = 3584;
                }
                int i = 49152;
                if (49152 < minBufferSize) {
                    i = ((minBufferSize / 2048) + 1) * 2048 * 2;
                }
                for (int i2 = 0; i2 < 3; i2++) {
                    this.buffers.add(new InstantCameraView.AudioBufferInfo());
                }
                AudioRecord audioRecord = r9;
                AudioRecord audioRecord2 = new AudioRecord(0, 44100, 16, 2, i);
                this.audioRecorder = audioRecord;
                audioRecord.startRecording();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("CameraView initied audio record with channels " + this.audioRecorder.getChannelCount() + " sample rate = " + this.audioRecorder.getSampleRate() + " bufferSize = " + i);
                }
                Thread thread = new Thread(this.recorderRunnable);
                thread.setPriority(10);
                thread.start();
                this.audioBufferInfo = new MediaCodec.BufferInfo();
                this.videoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat mediaFormat = new MediaFormat();
                mediaFormat.setString("mime", "audio/mp4a-latm");
                mediaFormat.setInteger("sample-rate", 44100);
                mediaFormat.setInteger("channel-count", 1);
                mediaFormat.setInteger("bitrate", 32000);
                mediaFormat.setInteger("max-input-size", 20480);
                MediaCodec createEncoderByType = MediaCodec.createEncoderByType("audio/mp4a-latm");
                this.audioEncoder = createEncoderByType;
                createEncoderByType.configure(mediaFormat, (Surface) null, (MediaCrypto) null, 1);
                this.audioEncoder.start();
                this.videoEncoder = MediaCodec.createEncoderByType("video/avc");
                this.firstEncode = true;
                MediaFormat createVideoFormat = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                createVideoFormat.setInteger("color-format", NUM);
                createVideoFormat.setInteger("bitrate", this.videoBitrate);
                createVideoFormat.setInteger("frame-rate", 30);
                createVideoFormat.setInteger("i-frame-interval", 1);
                this.videoEncoder.configure(createVideoFormat, (Surface) null, (MediaCrypto) null, 1);
                this.surface = this.videoEncoder.createInputSurface();
                this.videoEncoder.start();
                Mp4Movie mp4Movie = new Mp4Movie();
                mp4Movie.setCacheFile(this.videoFile);
                mp4Movie.setRotation(0);
                mp4Movie.setSize(this.videoWidth, this.videoHeight);
                this.mediaMuxer = new MP4Builder().createMovie(mp4Movie, false);
                if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    android.opengl.EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
                    this.eglDisplay = eglGetDisplay;
                    if (eglGetDisplay != EGL14.EGL_NO_DISPLAY) {
                        int[] iArr = new int[2];
                        if (EGL14.eglInitialize(eglGetDisplay, iArr, 0, iArr, 1)) {
                            if (this.eglContext == EGL14.EGL_NO_CONTEXT) {
                                android.opengl.EGLConfig[] eGLConfigArr = new android.opengl.EGLConfig[1];
                                if (EGL14.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344}, 0, eGLConfigArr, 0, 1, new int[1], 0)) {
                                    this.eglContext = EGL14.eglCreateContext(this.eglDisplay, eGLConfigArr[0], this.sharedEglContext, new int[]{12440, 2, 12344}, 0);
                                    this.eglConfig = eGLConfigArr[0];
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
                                    FloatBuffer asFloatBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
                                    this.textureBuffer = asFloatBuffer;
                                    asFloatBuffer.put(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f}).position(0);
                                    int access$300 = CameraView.this.loadShader(35633, "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n   gl_Position = uMVPMatrix * aPosition;\n   vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n");
                                    int access$3002 = CameraView.this.loadShader(35632, "#extension GL_OES_EGL_image_external : require\nprecision lowp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n   gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
                                    if (access$300 != 0 && access$3002 != 0) {
                                        int glCreateProgram = GLES20.glCreateProgram();
                                        this.drawProgram = glCreateProgram;
                                        GLES20.glAttachShader(glCreateProgram, access$300);
                                        GLES20.glAttachShader(this.drawProgram, access$3002);
                                        GLES20.glLinkProgram(this.drawProgram);
                                        int[] iArr2 = new int[1];
                                        GLES20.glGetProgramiv(this.drawProgram, 35714, iArr2, 0);
                                        if (iArr2[0] == 0) {
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Surface getInputSurface() {
            return this.surface;
        }

        public void drainEncoder(boolean z) throws Exception {
            ByteBuffer byteBuffer;
            ByteBuffer byteBuffer2;
            ByteBuffer byteBuffer3;
            ByteBuffer byteBuffer4;
            if (z) {
                this.videoEncoder.signalEndOfInputStream();
            }
            ByteBuffer[] outputBuffers = Build.VERSION.SDK_INT < 21 ? this.videoEncoder.getOutputBuffers() : null;
            while (true) {
                int dequeueOutputBuffer = this.videoEncoder.dequeueOutputBuffer(this.videoBufferInfo, 10000);
                byte b = 1;
                if (dequeueOutputBuffer == -1) {
                    if (!z) {
                        break;
                    }
                } else if (dequeueOutputBuffer == -3) {
                    if (Build.VERSION.SDK_INT < 21) {
                        outputBuffers = this.videoEncoder.getOutputBuffers();
                    }
                } else if (dequeueOutputBuffer == -2) {
                    MediaFormat outputFormat = this.videoEncoder.getOutputFormat();
                    if (this.videoTrackIndex == -5) {
                        this.videoTrackIndex = this.mediaMuxer.addTrack(outputFormat, false);
                        if (outputFormat.containsKey("prepend-sps-pps-to-idr-frames") && outputFormat.getInteger("prepend-sps-pps-to-idr-frames") == 1) {
                            this.prependHeaderSize = outputFormat.getByteBuffer("csd-0").limit() + outputFormat.getByteBuffer("csd-1").limit();
                        }
                    }
                } else if (dequeueOutputBuffer < 0) {
                    continue;
                } else {
                    if (Build.VERSION.SDK_INT < 21) {
                        byteBuffer2 = outputBuffers[dequeueOutputBuffer];
                    } else {
                        byteBuffer2 = this.videoEncoder.getOutputBuffer(dequeueOutputBuffer);
                    }
                    if (byteBuffer2 != null) {
                        MediaCodec.BufferInfo bufferInfo = this.videoBufferInfo;
                        int i = bufferInfo.size;
                        if (i > 1) {
                            int i2 = bufferInfo.flags;
                            if ((i2 & 2) == 0) {
                                int i3 = this.prependHeaderSize;
                                if (!(i3 == 0 || (i2 & 1) == 0)) {
                                    bufferInfo.offset += i3;
                                    bufferInfo.size = i - i3;
                                }
                                if (this.firstEncode && (i2 & 1) != 0) {
                                    if (bufferInfo.size > 100) {
                                        byteBuffer2.position(bufferInfo.offset);
                                        byte[] bArr = new byte[100];
                                        byteBuffer2.get(bArr);
                                        int i4 = 0;
                                        int i5 = 0;
                                        while (true) {
                                            if (i4 < 96) {
                                                if (bArr[i4] == 0 && bArr[i4 + 1] == 0 && bArr[i4 + 2] == 0 && bArr[i4 + 3] == 1 && (i5 = i5 + 1) > 1) {
                                                    MediaCodec.BufferInfo bufferInfo2 = this.videoBufferInfo;
                                                    bufferInfo2.offset += i4;
                                                    bufferInfo2.size -= i4;
                                                    break;
                                                }
                                                i4++;
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    this.firstEncode = false;
                                }
                                this.mediaMuxer.writeSampleData(this.videoTrackIndex, byteBuffer2, this.videoBufferInfo, true);
                            } else if (this.videoTrackIndex == -5) {
                                byte[] bArr2 = new byte[i];
                                byteBuffer2.limit(bufferInfo.offset + i);
                                byteBuffer2.position(this.videoBufferInfo.offset);
                                byteBuffer2.get(bArr2);
                                int i6 = this.videoBufferInfo.size - 1;
                                while (true) {
                                    if (i6 < 0 || i6 <= 3) {
                                        byteBuffer4 = null;
                                        byteBuffer3 = null;
                                    } else {
                                        if (bArr2[i6] == b && bArr2[i6 - 1] == 0 && bArr2[i6 - 2] == 0) {
                                            int i7 = i6 - 3;
                                            if (bArr2[i7] == 0) {
                                                byteBuffer4 = ByteBuffer.allocate(i7);
                                                byteBuffer3 = ByteBuffer.allocate(this.videoBufferInfo.size - i7);
                                                byteBuffer4.put(bArr2, 0, i7).position(0);
                                                byteBuffer3.put(bArr2, i7, this.videoBufferInfo.size - i7).position(0);
                                                break;
                                            }
                                        }
                                        i6--;
                                        b = 1;
                                    }
                                }
                                byteBuffer4 = null;
                                byteBuffer3 = null;
                                MediaFormat createVideoFormat = MediaFormat.createVideoFormat("video/avc", this.videoWidth, this.videoHeight);
                                if (!(byteBuffer4 == null || byteBuffer3 == null)) {
                                    createVideoFormat.setByteBuffer("csd-0", byteBuffer4);
                                    createVideoFormat.setByteBuffer("csd-1", byteBuffer3);
                                }
                                this.videoTrackIndex = this.mediaMuxer.addTrack(createVideoFormat, false);
                            }
                        }
                        this.videoEncoder.releaseOutputBuffer(dequeueOutputBuffer, false);
                        if ((this.videoBufferInfo.flags & 4) != 0) {
                            break;
                        }
                    } else {
                        throw new RuntimeException("encoderOutputBuffer " + dequeueOutputBuffer + " was null");
                    }
                }
            }
            if (Build.VERSION.SDK_INT < 21) {
                outputBuffers = this.audioEncoder.getOutputBuffers();
            }
            while (true) {
                int dequeueOutputBuffer2 = this.audioEncoder.dequeueOutputBuffer(this.audioBufferInfo, 0);
                if (dequeueOutputBuffer2 == -1) {
                    if (!z) {
                        return;
                    }
                    if (!this.running && this.sendWhenDone == 0) {
                        return;
                    }
                } else if (dequeueOutputBuffer2 == -3) {
                    if (Build.VERSION.SDK_INT < 21) {
                        outputBuffers = this.audioEncoder.getOutputBuffers();
                    }
                } else if (dequeueOutputBuffer2 == -2) {
                    MediaFormat outputFormat2 = this.audioEncoder.getOutputFormat();
                    if (this.audioTrackIndex == -5) {
                        this.audioTrackIndex = this.mediaMuxer.addTrack(outputFormat2, true);
                    }
                } else if (dequeueOutputBuffer2 < 0) {
                    continue;
                } else {
                    if (Build.VERSION.SDK_INT < 21) {
                        byteBuffer = outputBuffers[dequeueOutputBuffer2];
                    } else {
                        byteBuffer = this.audioEncoder.getOutputBuffer(dequeueOutputBuffer2);
                    }
                    if (byteBuffer != null) {
                        MediaCodec.BufferInfo bufferInfo3 = this.audioBufferInfo;
                        if ((bufferInfo3.flags & 2) != 0) {
                            bufferInfo3.size = 0;
                        }
                        if (bufferInfo3.size != 0) {
                            this.mediaMuxer.writeSampleData(this.audioTrackIndex, byteBuffer, bufferInfo3, false);
                        }
                        this.audioEncoder.releaseOutputBuffer(dequeueOutputBuffer2, false);
                        if ((this.audioBufferInfo.flags & 4) != 0) {
                            return;
                        }
                    } else {
                        throw new RuntimeException("encoderOutputBuffer " + dequeueOutputBuffer2 + " was null");
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            try {
                android.opengl.EGLDisplay eGLDisplay = this.eglDisplay;
                if (eGLDisplay != EGL14.EGL_NO_DISPLAY) {
                    android.opengl.EGLSurface eGLSurface = EGL14.EGL_NO_SURFACE;
                    EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, EGL14.EGL_NO_CONTEXT);
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

        public EncoderHandler(VideoRecorder videoRecorder) {
            this.mWeakEncoder = new WeakReference<>(videoRecorder);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            VideoRecorder videoRecorder = (VideoRecorder) this.mWeakEncoder.get();
            if (videoRecorder != null) {
                if (i == 0) {
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("start encoder");
                        }
                        videoRecorder.prepareEncoder();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        videoRecorder.handleStopRecording(0);
                        Looper.myLooper().quit();
                    }
                } else if (i == 1) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("stop encoder");
                    }
                    videoRecorder.handleStopRecording(message.arg1);
                } else if (i == 2) {
                    videoRecorder.handleVideoFrameAvailable((((long) message.arg1) << 32) | (((long) message.arg2) & 4294967295L), (Integer) message.obj);
                } else if (i == 3) {
                    videoRecorder.handleAudioFrameAvailable((InstantCameraView.AudioBufferInfo) message.obj);
                }
            }
        }

        public void exit() {
            Looper.myLooper().quit();
        }
    }

    public void setFpsLimit(int i) {
        this.fpsLimit = i;
    }
}
