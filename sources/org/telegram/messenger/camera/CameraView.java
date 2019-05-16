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
import android.hardware.Camera;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;

@SuppressLint({"NewApi"})
public class CameraView extends FrameLayout implements SurfaceTextureListener {
    private CameraSession cameraSession;
    private int clipLeft;
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
    private float outerAlpha;
    private Paint outerPaint = new Paint(1);
    private Size previewSize;
    private TextureView textureView;
    private Matrix txform = new Matrix();

    public interface CameraViewDelegate {
        void onCameraCreated(Camera camera);

        void onCameraInit();
    }

    private int clamp(int i, int i2, int i3) {
        return i > i3 ? i3 : i < i2 ? i2 : i;
    }

    public CameraView(Context context, boolean z) {
        super(context, null);
        this.isFrontface = z;
        this.initialFrontface = z;
        this.textureView = new TextureView(context);
        this.textureView.setSurfaceTextureListener(this);
        addView(this.textureView);
        this.focusAreaSize = AndroidUtilities.dp(96.0f);
        this.outerPaint.setColor(-1);
        this.outerPaint.setStyle(Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.innerPaint.setColor(Integer.MAX_VALUE);
    }

    /* Access modifiers changed, original: protected */
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

    public boolean hasFrontFaceCamera() {
        ArrayList cameras = CameraController.getInstance().getCameras();
        for (int i = 0; i < cameras.size(); i++) {
            if (((CameraInfo) cameras.get(i)).frontCamera != 0) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null, null);
            this.cameraSession = null;
        }
        this.initied = false;
        this.isFrontface ^= 1;
        initCamera();
    }

    /* JADX WARNING: Missing block: B:42:0x00e7, code skipped:
            if (r0.getHeight() >= 1280) goto L_0x00ea;
     */
    private void initCamera() {
        /*
        r15 = this;
        r0 = org.telegram.messenger.camera.CameraController.getInstance();
        r0 = r0.getCameras();
        if (r0 != 0) goto L_0x000b;
    L_0x000a:
        return;
    L_0x000b:
        r1 = 0;
    L_0x000c:
        r2 = r0.size();
        if (r1 >= r2) goto L_0x002c;
    L_0x0012:
        r2 = r0.get(r1);
        r2 = (org.telegram.messenger.camera.CameraInfo) r2;
        r3 = r15.isFrontface;
        if (r3 == 0) goto L_0x0020;
    L_0x001c:
        r3 = r2.frontCamera;
        if (r3 != 0) goto L_0x002d;
    L_0x0020:
        r3 = r15.isFrontface;
        if (r3 != 0) goto L_0x0029;
    L_0x0024:
        r3 = r2.frontCamera;
        if (r3 != 0) goto L_0x0029;
    L_0x0028:
        goto L_0x002d;
    L_0x0029:
        r1 = r1 + 1;
        goto L_0x000c;
    L_0x002c:
        r2 = 0;
    L_0x002d:
        if (r2 != 0) goto L_0x0030;
    L_0x002f:
        return;
    L_0x0030:
        r0 = NUM; // 0x3faaaaab float:1.3333334 double:5.277359326E-315;
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r1.x;
        r1 = r1.y;
        r1 = java.lang.Math.max(r3, r1);
        r1 = (float) r1;
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r3.x;
        r3 = r3.y;
        r3 = java.lang.Math.min(r4, r3);
        r3 = (float) r3;
        r1 = r1 / r3;
        r3 = r15.initialFrontface;
        r4 = 3;
        r5 = 4;
        r6 = NUM; // 0x3dcccccd float:0.1 double:5.122630465E-315;
        r7 = 9;
        r8 = 16;
        r9 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        if (r3 == 0) goto L_0x0063;
    L_0x0059:
        r3 = new org.telegram.messenger.camera.Size;
        r3.<init>(r8, r7);
        r10 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        r11 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x007e;
    L_0x0063:
        r3 = r1 - r0;
        r3 = java.lang.Math.abs(r3);
        r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
        if (r3 >= 0) goto L_0x0075;
    L_0x006d:
        r3 = new org.telegram.messenger.camera.Size;
        r3.<init>(r5, r4);
        r11 = 960; // 0x3c0 float:1.345E-42 double:4.743E-321;
        goto L_0x007c;
    L_0x0075:
        r3 = new org.telegram.messenger.camera.Size;
        r3.<init>(r8, r7);
        r11 = 720; // 0x2d0 float:1.009E-42 double:3.557E-321;
    L_0x007c:
        r10 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
    L_0x007e:
        r12 = r15.textureView;
        r12 = r12.getWidth();
        if (r12 <= 0) goto L_0x00ad;
    L_0x0086:
        r12 = r15.textureView;
        r12 = r12.getHeight();
        if (r12 <= 0) goto L_0x00ad;
    L_0x008e:
        r12 = org.telegram.messenger.AndroidUtilities.displaySize;
        r13 = r12.x;
        r12 = r12.y;
        r12 = java.lang.Math.min(r13, r12);
        r13 = r3.getHeight();
        r13 = r13 * r12;
        r14 = r3.getWidth();
        r13 = r13 / r14;
        r14 = r2.getPreviewSizes();
        r12 = org.telegram.messenger.camera.CameraController.chooseOptimalSize(r14, r12, r13, r3);
        r15.previewSize = r12;
    L_0x00ad:
        r12 = r2.getPictureSizes();
        r3 = org.telegram.messenger.camera.CameraController.chooseOptimalSize(r12, r10, r11, r3);
        r12 = r3.getWidth();
        if (r12 < r9) goto L_0x00ea;
    L_0x00bb:
        r12 = r3.getHeight();
        if (r12 < r9) goto L_0x00ea;
    L_0x00c1:
        r1 = r1 - r0;
        r0 = java.lang.Math.abs(r1);
        r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
        if (r0 >= 0) goto L_0x00d0;
    L_0x00ca:
        r0 = new org.telegram.messenger.camera.Size;
        r0.<init>(r4, r5);
        goto L_0x00d5;
    L_0x00d0:
        r0 = new org.telegram.messenger.camera.Size;
        r0.<init>(r7, r8);
    L_0x00d5:
        r1 = r2.getPictureSizes();
        r0 = org.telegram.messenger.camera.CameraController.chooseOptimalSize(r1, r11, r10, r0);
        r1 = r0.getWidth();
        if (r1 < r9) goto L_0x00eb;
    L_0x00e3:
        r1 = r0.getHeight();
        if (r1 >= r9) goto L_0x00ea;
    L_0x00e9:
        goto L_0x00eb;
    L_0x00ea:
        r0 = r3;
    L_0x00eb:
        r1 = r15.textureView;
        r1 = r1.getSurfaceTexture();
        r3 = r15.previewSize;
        if (r3 == 0) goto L_0x0122;
    L_0x00f5:
        if (r1 == 0) goto L_0x0122;
    L_0x00f7:
        r3 = r3.getWidth();
        r4 = r15.previewSize;
        r4 = r4.getHeight();
        r1.setDefaultBufferSize(r3, r4);
        r3 = new org.telegram.messenger.camera.CameraSession;
        r4 = r15.previewSize;
        r5 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        r3.<init>(r2, r4, r0, r5);
        r15.cameraSession = r3;
        r0 = org.telegram.messenger.camera.CameraController.getInstance();
        r2 = r15.cameraSession;
        r3 = new org.telegram.messenger.camera.CameraView$1;
        r3.<init>();
        r4 = new org.telegram.messenger.camera.CameraView$2;
        r4.<init>();
        r0.open(r2, r1, r3, r4);
    L_0x0122:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.CameraView.initCamera():void");
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
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null, null);
        }
        return false;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (!this.initied) {
            CameraSession cameraSession = this.cameraSession;
            if (cameraSession != null && cameraSession.isInitied()) {
                CameraViewDelegate cameraViewDelegate = this.delegate;
                if (cameraViewDelegate != null) {
                    cameraViewDelegate.onCameraInit();
                }
                this.initied = true;
            }
        }
    }

    public void setClipTop(int i) {
        this.clipTop = i;
    }

    public void setClipLeft(int i) {
        this.clipLeft = i;
    }

    private void checkPreviewMatrix() {
        Size size = this.previewSize;
        if (size != null) {
            adjustAspectRatio(size.getWidth(), this.previewSize.getHeight(), ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    private void adjustAspectRatio(int i, int i2, int i3) {
        float max;
        this.txform.reset();
        int width = getWidth();
        int height = getHeight();
        float f = (float) (width / 2);
        float f2 = (float) (height / 2);
        if (i3 == 0 || i3 == 2) {
            max = Math.max(((float) (this.clipTop + height)) / ((float) i), ((float) (this.clipLeft + width)) / ((float) i2));
        } else {
            max = Math.max(((float) (this.clipTop + height)) / ((float) i2), ((float) (this.clipLeft + width)) / ((float) i));
        }
        float f3 = (float) width;
        float f4 = (float) height;
        this.txform.postScale((((float) i2) * max) / f3, (((float) i) * max) / f4, f, f2);
        if (1 == i3 || 3 == i3) {
            this.txform.postRotate((float) ((i3 - 2) * 90), f, f2);
        } else if (2 == i3) {
            this.txform.postRotate(180.0f, f, f2);
        }
        if (this.mirror) {
            this.txform.postScale(-1.0f, 1.0f, f, f2);
        }
        if (!(this.clipTop == 0 && this.clipLeft == 0)) {
            this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
        }
        this.textureView.setTransform(this.txform);
        Matrix matrix = new Matrix();
        matrix.postRotate((float) this.cameraSession.getDisplayOrientation());
        matrix.postScale(f3 / 2000.0f, f4 / 2000.0f);
        matrix.postTranslate(f3 / 2.0f, f4 / 2.0f);
        matrix.invert(this.matrix);
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
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.focusToRect(calculateTapArea, calculateTapArea2);
        }
        this.focusProgress = 0.0f;
        this.innerAlpha = 1.0f;
        this.outerAlpha = 1.0f;
        this.cx = i;
        this.cy = i2;
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

    public void destroy(boolean z, Runnable runnable) {
        CameraSession cameraSession = this.cameraSession;
        if (cameraSession != null) {
            cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !z ? new CountDownLatch(1) : null, runnable);
        }
    }

    /* Access modifiers changed, original: protected */
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
                f2 = this.innerAlpha;
                if (f2 != 0.0f) {
                    this.innerAlpha = f2 - (((float) j2) / 150.0f);
                    if (this.innerAlpha < 0.0f) {
                        this.innerAlpha = 0.0f;
                    }
                    invalidate();
                } else {
                    f2 = this.outerAlpha;
                    if (f2 != 0.0f) {
                        this.outerAlpha = f2 - (((float) j2) / 150.0f);
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
