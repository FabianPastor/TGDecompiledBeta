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
    private boolean circleShape = false;
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

    /* renamed from: org.telegram.messenger.camera.CameraView$1 */
    class C05401 implements Runnable {
        C05401() {
        }

        public void run() {
            if (CameraView.this.cameraSession != null) {
                CameraView.this.cameraSession.setInitied();
            }
            CameraView.this.checkPreviewMatrix();
        }
    }

    /* renamed from: org.telegram.messenger.camera.CameraView$2 */
    class C05412 implements Runnable {
        C05412() {
        }

        public void run() {
            if (CameraView.this.delegate != null) {
                CameraView.this.delegate.onCameraCreated(CameraView.this.cameraSession.cameraInfo.camera);
            }
        }
    }

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
        this.outerPaint.setColor(true);
        this.outerPaint.setStyle(Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(true));
        this.innerPaint.setColor(true);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
        initCamera(this.isFrontface);
    }

    private void initCamera(boolean z) {
        z = CameraController.getInstance().getCameras();
        if (z) {
            CameraInfo cameraInfo;
            int i = 0;
            while (i < z.size()) {
                cameraInfo = (CameraInfo) z.get(i);
                if (!this.isFrontface || cameraInfo.frontCamera == 0) {
                    if (!this.isFrontface && cameraInfo.frontCamera == 0) {
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            cameraInfo = null;
            if (cameraInfo != null) {
                Size size;
                int i2;
                int i3;
                SurfaceTexture surfaceTexture;
                float max = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
                if (this.initialFrontface) {
                    size = new Size(16, 9);
                    i2 = 480;
                    i3 = 270;
                } else {
                    if (Math.abs(max - 1.3333334f) < 0.1f) {
                        size = new Size(4, 3);
                        i3 = 960;
                    } else {
                        size = new Size(16, 9);
                        i3 = 720;
                    }
                    i2 = 1280;
                }
                if (this.textureView.getWidth() > 0 && this.textureView.getHeight() > 0) {
                    int min = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                    this.previewSize = CameraController.chooseOptimalSize(cameraInfo.getPreviewSizes(), min, (size.getHeight() * min) / size.getWidth(), size);
                }
                size = CameraController.chooseOptimalSize(cameraInfo.getPictureSizes(), i2, i3, size);
                if (size.getWidth() >= 1280 && size.getHeight() >= 1280) {
                    if (Math.abs(max - 1.3333334f) < true) {
                        z = new Size(3, 4);
                    } else {
                        z = new Size(9, 16);
                    }
                    z = CameraController.chooseOptimalSize(cameraInfo.getPictureSizes(), i3, i2, z);
                    if (z.getWidth() >= 1280) {
                        if (z.getHeight() < 1280) {
                        }
                    }
                    surfaceTexture = this.textureView.getSurfaceTexture();
                    if (!(this.previewSize == null || surfaceTexture == null)) {
                        surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                        this.cameraSession = new CameraSession(cameraInfo, this.previewSize, z, 256);
                        CameraController.getInstance().open(this.cameraSession, surfaceTexture, new C05401(), new C05412());
                    }
                }
                z = size;
                surfaceTexture = this.textureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(this.previewSize.getWidth(), this.previewSize.getHeight());
                this.cameraSession = new CameraSession(cameraInfo, this.previewSize, z, 256);
                CameraController.getInstance().open(this.cameraSession, surfaceTexture, new C05401(), new C05412());
            }
        }
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        initCamera(this.isFrontface);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
        checkPreviewMatrix();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null, null);
        }
        return null;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        if (this.initied == null && this.cameraSession != null && this.cameraSession.isInitied() != null) {
            if (this.delegate != null) {
                this.delegate.onCameraInit();
            }
            this.initied = true;
        }
    }

    public void setClipTop(int i) {
        this.clipTop = i;
    }

    public void setClipLeft(int i) {
        this.clipLeft = i;
    }

    private void checkPreviewMatrix() {
        if (this.previewSize != null) {
            adjustAspectRatio(this.previewSize.getWidth(), this.previewSize.getHeight(), ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation());
        }
    }

    private void adjustAspectRatio(int i, int i2, int i3) {
        float max;
        float f;
        float f2;
        this.txform.reset();
        int width = getWidth();
        int height = getHeight();
        float f3 = (float) (width / 2);
        float f4 = (float) (height / 2);
        if (i3 != 0) {
            if (i3 != 2) {
                max = Math.max(((float) (this.clipTop + height)) / ((float) i2), ((float) (this.clipLeft + width)) / ((float) i));
                f = (float) width;
                f2 = (float) height;
                this.txform.postScale((((float) i2) * max) / f, (((float) i) * max) / f2, f3, f4);
                if (1 != i3) {
                    if (3 == i3) {
                        if (2 == i3) {
                            this.txform.postRotate(NUM, f3, f4);
                        }
                        if (this.mirror != 0) {
                            this.txform.postScale(-NUM, NUM, f3, f4);
                        }
                        if (!(this.clipTop == 0 && this.clipLeft == 0)) {
                            this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
                        }
                        this.textureView.setTransform(this.txform);
                        i = new Matrix();
                        i.postRotate((float) this.cameraSession.getDisplayOrientation());
                        i.postScale(f / NUM, f2 / NUM);
                        i.postTranslate(f / 2.0f, f2 / 2.0f);
                        i.invert(this.matrix);
                    }
                }
                this.txform.postRotate((float) (90 * (i3 - 2)), f3, f4);
                if (this.mirror != 0) {
                    this.txform.postScale(-NUM, NUM, f3, f4);
                }
                this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
                this.textureView.setTransform(this.txform);
                i = new Matrix();
                i.postRotate((float) this.cameraSession.getDisplayOrientation());
                i.postScale(f / NUM, f2 / NUM);
                i.postTranslate(f / 2.0f, f2 / 2.0f);
                i.invert(this.matrix);
            }
        }
        max = Math.max(((float) (this.clipTop + height)) / ((float) i), ((float) (this.clipLeft + width)) / ((float) i2));
        f = (float) width;
        f2 = (float) height;
        this.txform.postScale((((float) i2) * max) / f, (((float) i) * max) / f2, f3, f4);
        if (1 != i3) {
            if (3 == i3) {
                if (2 == i3) {
                    this.txform.postRotate(NUM, f3, f4);
                }
                if (this.mirror != 0) {
                    this.txform.postScale(-NUM, NUM, f3, f4);
                }
                this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
                this.textureView.setTransform(this.txform);
                i = new Matrix();
                i.postRotate((float) this.cameraSession.getDisplayOrientation());
                i.postScale(f / NUM, f2 / NUM);
                i.postTranslate(f / 2.0f, f2 / 2.0f);
                i.invert(this.matrix);
            }
        }
        this.txform.postRotate((float) (90 * (i3 - 2)), f3, f4);
        if (this.mirror != 0) {
            this.txform.postScale(-NUM, NUM, f3, f4);
        }
        this.txform.postTranslate((float) ((-this.clipLeft) / 2), (float) ((-this.clipTop) / 2));
        this.textureView.setTransform(this.txform);
        i = new Matrix();
        i.postRotate((float) this.cameraSession.getDisplayOrientation());
        i.postScale(f / NUM, f2 / NUM);
        i.postTranslate(f / 2.0f, f2 / 2.0f);
        i.invert(this.matrix);
    }

    private Rect calculateTapArea(float f, float f2, float f3) {
        f3 = Float.valueOf(((float) this.focusAreaSize) * f3).intValue();
        int i = f3 / 2;
        f = clamp(((int) f) - i, 0, getWidth() - f3);
        f2 = clamp(((int) f2) - i, 0, getHeight() - f3);
        RectF rectF = new RectF((float) f, (float) f2, (float) (f + f3), (float) (f2 + f3));
        this.matrix.mapRect(rectF);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    public void focusToPoint(int i, int i2) {
        float f = (float) i;
        float f2 = (float) i2;
        Rect calculateTapArea = calculateTapArea(f, f2, 1.0f);
        Rect calculateTapArea2 = calculateTapArea(f, f2, 1.5f);
        if (this.cameraSession != null) {
            this.cameraSession.focusToRect(calculateTapArea, calculateTapArea2);
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
        if (this.cameraSession != null) {
            this.cameraSession.destroy();
            CameraController.getInstance().close(this.cameraSession, !z ? new CountDownLatch(1) : false, runnable);
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        view = super.drawChild(canvas, view, j);
        if (!(this.focusProgress == NUM && this.innerAlpha == 0 && this.outerAlpha == 0)) {
            j = AndroidUtilities.dp(NUM);
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastDrawTime;
            long j3 = 17;
            if (j2 >= 0) {
                if (j2 <= 17) {
                    j3 = j2;
                }
            }
            this.lastDrawTime = currentTimeMillis;
            this.outerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.outerAlpha) * 255.0f));
            this.innerPaint.setAlpha((int) (this.interpolator.getInterpolation(this.innerAlpha) * 127.0f));
            float interpolation = this.interpolator.getInterpolation(this.focusProgress);
            j = (float) j;
            canvas.drawCircle((float) this.cx, (float) this.cy, ((1.0f - interpolation) * j) + j, this.outerPaint);
            canvas.drawCircle((float) this.cx, (float) this.cy, j * interpolation, this.innerPaint);
            if (this.focusProgress < NUM) {
                this.focusProgress += ((float) j3) / 200.0f;
                if (this.focusProgress > NUM) {
                    this.focusProgress = 1.0f;
                }
                invalidate();
            } else if (this.innerAlpha != null) {
                this.innerAlpha -= ((float) j3) / 150.0f;
                if (this.innerAlpha < null) {
                    this.innerAlpha = 0.0f;
                }
                invalidate();
            } else if (this.outerAlpha != null) {
                this.outerAlpha -= ((float) j3) / 150.0f;
                if (this.outerAlpha < null) {
                    this.outerAlpha = 0.0f;
                }
                invalidate();
            }
        }
        return view;
    }
}
