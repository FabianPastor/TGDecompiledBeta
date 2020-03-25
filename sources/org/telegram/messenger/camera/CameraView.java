package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;

@SuppressLint({"NewApi"})
public class CameraView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private CameraSession cameraSession;
    private int clipBottom;
    private int clipTop;
    private int cx;
    private int cy;
    private CameraViewDelegate delegate;
    private int focusAreaSize;
    private float focusProgress = 1.0f;
    private boolean initialFrontface;
    private boolean initied;
    private float innerAlpha;
    private Paint innerPaint = new Paint(1);
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isFrontface;
    private long lastDrawTime;
    private Matrix matrix = new Matrix();
    private boolean mirror;
    private boolean optimizeForBarcode;
    private float outerAlpha;
    private Paint outerPaint = new Paint(1);
    private Size previewSize;
    private TextureView textureView;
    private Matrix txform = new Matrix();
    private boolean useMaxPreview;

    public interface CameraViewDelegate {
        void onCameraCreated(Camera camera);

        void onCameraInit();
    }

    private int clamp(int i, int i2, int i3) {
        return i > i3 ? i3 : i < i2 ? i2 : i;
    }

    public CameraView(Context context, boolean z) {
        super(context, (AttributeSet) null);
        this.isFrontface = z;
        this.initialFrontface = z;
        TextureView textureView2 = new TextureView(context);
        this.textureView = textureView2;
        textureView2.setSurfaceTextureListener(this);
        addView(this.textureView);
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
            CameraController.getInstance().close(this.cameraSession, (CountDownLatch) null, (Runnable) null);
            this.cameraSession = null;
        }
        this.initied = false;
        this.isFrontface = !this.isFrontface;
        initCamera();
    }

    public void initCamera() {
        CameraInfo cameraInfo;
        int i;
        int i2;
        Size size;
        Size size2;
        int i3;
        ArrayList<CameraInfo> cameras = CameraController.getInstance().getCameras();
        if (cameras != null) {
            int i4 = 0;
            while (true) {
                if (i4 >= cameras.size()) {
                    cameraInfo = null;
                    break;
                }
                cameraInfo = cameras.get(i4);
                if ((this.isFrontface && cameraInfo.frontCamera != 0) || (!this.isFrontface && cameraInfo.frontCamera == 0)) {
                    break;
                }
                i4++;
            }
            if (cameraInfo != null) {
                Point point = AndroidUtilities.displaySize;
                Point point2 = AndroidUtilities.displaySize;
                float max = ((float) Math.max(point.x, point.y)) / ((float) Math.min(point2.x, point2.y));
                if (this.initialFrontface) {
                    size = new Size(16, 9);
                    i2 = 480;
                    i = 270;
                } else {
                    if (Math.abs(max - 1.3333334f) < 0.1f) {
                        size = new Size(4, 3);
                        i = 960;
                    } else {
                        size = new Size(16, 9);
                        i = 720;
                    }
                    i2 = 1280;
                }
                if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                    if (this.useMaxPreview) {
                        Point point3 = AndroidUtilities.displaySize;
                        i3 = Math.max(point3.x, point3.y);
                    } else {
                        Point point4 = AndroidUtilities.displaySize;
                        i3 = Math.min(point4.x, point4.y);
                    }
                    this.previewSize = CameraController.chooseOptimalSize(cameraInfo.getPreviewSizes(), i3, (size.getHeight() * i3) / size.getWidth(), size);
                }
                Size chooseOptimalSize = CameraController.chooseOptimalSize(cameraInfo.getPictureSizes(), i2, i, size);
                if (chooseOptimalSize.getWidth() >= 1280 && chooseOptimalSize.getHeight() >= 1280) {
                    if (Math.abs(max - 1.3333334f) < 0.1f) {
                        size2 = new Size(3, 4);
                    } else {
                        size2 = new Size(9, 16);
                    }
                    Size chooseOptimalSize2 = CameraController.chooseOptimalSize(cameraInfo.getPictureSizes(), i, i2, size2);
                    if (chooseOptimalSize2.getWidth() < 1280 || chooseOptimalSize2.getHeight() < 1280) {
                        chooseOptimalSize = chooseOptimalSize2;
                    }
                }
                SurfaceTexture surfaceTexture = this.textureView.getSurfaceTexture();
                Size size3 = this.previewSize;
                if (size3 != null && surfaceTexture != null) {
                    surfaceTexture.setDefaultBufferSize(size3.getWidth(), this.previewSize.getHeight());
                    CameraSession cameraSession2 = new CameraSession(cameraInfo, this.previewSize, chooseOptimalSize, 256);
                    this.cameraSession = cameraSession2;
                    boolean z = this.optimizeForBarcode;
                    if (z) {
                        cameraSession2.setOptimizeForBarcode(z);
                    }
                    CameraController.getInstance().open(this.cameraSession, surfaceTexture, new Runnable() {
                        public final void run() {
                            CameraView.this.lambda$initCamera$0$CameraView();
                        }
                    }, new Runnable() {
                        public final void run() {
                            CameraView.this.lambda$initCamera$1$CameraView();
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$initCamera$0$CameraView() {
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            cameraSession2.setInitied();
        }
        checkPreviewMatrix();
    }

    public /* synthetic */ void lambda$initCamera$1$CameraView() {
        CameraViewDelegate cameraViewDelegate = this.delegate;
        if (cameraViewDelegate != null) {
            cameraViewDelegate.onCameraCreated(this.cameraSession.cameraInfo.camera);
        }
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        initCamera();
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        checkPreviewMatrix();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.cameraSession == null) {
            return false;
        }
        CameraController.getInstance().close(this.cameraSession, (CountDownLatch) null, (Runnable) null);
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
            adjustAspectRatio(this.previewSize.getWidth(), this.previewSize.getHeight(), ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation());
        }
    }

    private void adjustAspectRatio(int i, int i2, int i3) {
        float f;
        this.txform.reset();
        int width = getWidth();
        int height = getHeight();
        float f2 = (float) (width / 2);
        float f3 = (float) (height / 2);
        if (i3 == 0 || i3 == 2) {
            f = Math.max(((float) ((this.clipTop + height) + this.clipBottom)) / ((float) i), ((float) width) / ((float) i2));
        } else {
            f = Math.max(((float) ((this.clipTop + height) + this.clipBottom)) / ((float) i2), ((float) width) / ((float) i));
        }
        float f4 = (float) width;
        float f5 = (float) height;
        this.txform.postScale((((float) i2) * f) / f4, (((float) i) * f) / f5, f2, f3);
        if (1 == i3 || 3 == i3) {
            this.txform.postRotate((float) ((i3 - 2) * 90), f2, f3);
        } else if (2 == i3) {
            this.txform.postRotate(180.0f, f2, f3);
        }
        if (this.mirror) {
            this.txform.postScale(-1.0f, 1.0f, f2, f3);
        }
        int i4 = this.clipTop;
        if (i4 != 0) {
            this.txform.postTranslate(0.0f, (float) ((-i4) / 2));
        } else {
            int i5 = this.clipBottom;
            if (i5 != 0) {
                this.txform.postTranslate(0.0f, (float) (i5 / 2));
            }
        }
        this.textureView.setTransform(this.txform);
        Matrix matrix2 = new Matrix();
        CameraSession cameraSession2 = this.cameraSession;
        if (cameraSession2 != null) {
            matrix2.postRotate((float) cameraSession2.getDisplayOrientation());
        }
        matrix2.postScale(f4 / 2000.0f, f5 / 2000.0f);
        matrix2.postTranslate(f4 / 2.0f, f5 / 2.0f);
        matrix2.invert(this.matrix);
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
}
