package org.telegram.messenger.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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
        this.textureView = new TextureView(context);
        this.textureView.setSurfaceTextureListener(this);
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

    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00f6, code lost:
        if (r0.getHeight() >= 1280) goto L_0x00f9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initCamera() {
        /*
            r15 = this;
            org.telegram.messenger.camera.CameraController r0 = org.telegram.messenger.camera.CameraController.getInstance()
            java.util.ArrayList r0 = r0.getCameras()
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            r1 = 0
        L_0x000c:
            int r2 = r0.size()
            if (r1 >= r2) goto L_0x002c
            java.lang.Object r2 = r0.get(r1)
            org.telegram.messenger.camera.CameraInfo r2 = (org.telegram.messenger.camera.CameraInfo) r2
            boolean r3 = r15.isFrontface
            if (r3 == 0) goto L_0x0020
            int r3 = r2.frontCamera
            if (r3 != 0) goto L_0x002d
        L_0x0020:
            boolean r3 = r15.isFrontface
            if (r3 != 0) goto L_0x0029
            int r3 = r2.frontCamera
            if (r3 != 0) goto L_0x0029
            goto L_0x002d
        L_0x0029:
            int r1 = r1 + 1
            goto L_0x000c
        L_0x002c:
            r2 = 0
        L_0x002d:
            if (r2 != 0) goto L_0x0030
            return
        L_0x0030:
            r0 = 1068149419(0x3faaaaab, float:1.3333334)
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r1.x
            int r1 = r1.y
            int r1 = java.lang.Math.max(r3, r1)
            float r1 = (float) r1
            android.graphics.Point r3 = org.telegram.messenger.AndroidUtilities.displaySize
            int r4 = r3.x
            int r3 = r3.y
            int r3 = java.lang.Math.min(r4, r3)
            float r3 = (float) r3
            float r1 = r1 / r3
            boolean r3 = r15.initialFrontface
            r4 = 3
            r5 = 4
            r6 = 1036831949(0x3dcccccd, float:0.1)
            r7 = 9
            r8 = 16
            r9 = 1280(0x500, float:1.794E-42)
            if (r3 == 0) goto L_0x0063
            org.telegram.messenger.camera.Size r3 = new org.telegram.messenger.camera.Size
            r3.<init>(r8, r7)
            r10 = 480(0x1e0, float:6.73E-43)
            r11 = 270(0x10e, float:3.78E-43)
            goto L_0x007e
        L_0x0063:
            float r3 = r1 - r0
            float r3 = java.lang.Math.abs(r3)
            int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r3 >= 0) goto L_0x0075
            org.telegram.messenger.camera.Size r3 = new org.telegram.messenger.camera.Size
            r3.<init>(r5, r4)
            r11 = 960(0x3c0, float:1.345E-42)
            goto L_0x007c
        L_0x0075:
            org.telegram.messenger.camera.Size r3 = new org.telegram.messenger.camera.Size
            r3.<init>(r8, r7)
            r11 = 720(0x2d0, float:1.009E-42)
        L_0x007c:
            r10 = 1280(0x500, float:1.794E-42)
        L_0x007e:
            android.view.TextureView r12 = r15.textureView
            int r12 = r12.getWidth()
            if (r12 <= 0) goto L_0x00bc
            android.view.TextureView r12 = r15.textureView
            int r12 = r12.getHeight()
            if (r12 <= 0) goto L_0x00bc
            boolean r12 = r15.useMaxPreview
            if (r12 == 0) goto L_0x009d
            android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.displaySize
            int r13 = r12.x
            int r12 = r12.y
            int r12 = java.lang.Math.max(r13, r12)
            goto L_0x00a7
        L_0x009d:
            android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.displaySize
            int r13 = r12.x
            int r12 = r12.y
            int r12 = java.lang.Math.min(r13, r12)
        L_0x00a7:
            int r13 = r3.getHeight()
            int r13 = r13 * r12
            int r14 = r3.getWidth()
            int r13 = r13 / r14
            java.util.ArrayList r14 = r2.getPreviewSizes()
            org.telegram.messenger.camera.Size r12 = org.telegram.messenger.camera.CameraController.chooseOptimalSize(r14, r12, r13, r3)
            r15.previewSize = r12
        L_0x00bc:
            java.util.ArrayList r12 = r2.getPictureSizes()
            org.telegram.messenger.camera.Size r3 = org.telegram.messenger.camera.CameraController.chooseOptimalSize(r12, r10, r11, r3)
            int r12 = r3.getWidth()
            if (r12 < r9) goto L_0x00f9
            int r12 = r3.getHeight()
            if (r12 < r9) goto L_0x00f9
            float r1 = r1 - r0
            float r0 = java.lang.Math.abs(r1)
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x00df
            org.telegram.messenger.camera.Size r0 = new org.telegram.messenger.camera.Size
            r0.<init>(r4, r5)
            goto L_0x00e4
        L_0x00df:
            org.telegram.messenger.camera.Size r0 = new org.telegram.messenger.camera.Size
            r0.<init>(r7, r8)
        L_0x00e4:
            java.util.ArrayList r1 = r2.getPictureSizes()
            org.telegram.messenger.camera.Size r0 = org.telegram.messenger.camera.CameraController.chooseOptimalSize(r1, r11, r10, r0)
            int r1 = r0.getWidth()
            if (r1 < r9) goto L_0x00fa
            int r1 = r0.getHeight()
            if (r1 >= r9) goto L_0x00f9
            goto L_0x00fa
        L_0x00f9:
            r0 = r3
        L_0x00fa:
            android.view.TextureView r1 = r15.textureView
            android.graphics.SurfaceTexture r1 = r1.getSurfaceTexture()
            org.telegram.messenger.camera.Size r3 = r15.previewSize
            if (r3 == 0) goto L_0x013a
            if (r1 == 0) goto L_0x013a
            int r3 = r3.getWidth()
            org.telegram.messenger.camera.Size r4 = r15.previewSize
            int r4 = r4.getHeight()
            r1.setDefaultBufferSize(r3, r4)
            org.telegram.messenger.camera.CameraSession r3 = new org.telegram.messenger.camera.CameraSession
            org.telegram.messenger.camera.Size r4 = r15.previewSize
            r5 = 256(0x100, float:3.59E-43)
            r3.<init>(r2, r4, r0, r5)
            r15.cameraSession = r3
            boolean r0 = r15.optimizeForBarcode
            if (r0 == 0) goto L_0x0127
            org.telegram.messenger.camera.CameraSession r2 = r15.cameraSession
            r2.setOptimizeForBarcode(r0)
        L_0x0127:
            org.telegram.messenger.camera.CameraController r0 = org.telegram.messenger.camera.CameraController.getInstance()
            org.telegram.messenger.camera.CameraSession r2 = r15.cameraSession
            org.telegram.messenger.camera.-$$Lambda$CameraView$p1Q9XvCpkKK5Re9wn8tNGeDQ4vs r3 = new org.telegram.messenger.camera.-$$Lambda$CameraView$p1Q9XvCpkKK5Re9wn8tNGeDQ4vs
            r3.<init>()
            org.telegram.messenger.camera.-$$Lambda$CameraView$l4G-1k9CLASSNAMEfFIDGX-fnY3ljxHc0 r4 = new org.telegram.messenger.camera.-$$Lambda$CameraView$l4G-1k9CLASSNAMEfFIDGX-fnY3ljxHc0
            r4.<init>()
            r0.open(r2, r1, r3, r4)
        L_0x013a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.initCamera():void");
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
                this.focusProgress = f2 + (((float) j2) / 200.0f);
                if (this.focusProgress > 1.0f) {
                    this.focusProgress = 1.0f;
                }
                invalidate();
            } else {
                float f3 = this.innerAlpha;
                if (f3 != 0.0f) {
                    this.innerAlpha = f3 - (((float) j2) / 150.0f);
                    if (this.innerAlpha < 0.0f) {
                        this.innerAlpha = 0.0f;
                    }
                    invalidate();
                } else {
                    float f4 = this.outerAlpha;
                    if (f4 != 0.0f) {
                        this.outerAlpha = f4 - (((float) j2) / 150.0f);
                        if (this.outerAlpha < 0.0f) {
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
