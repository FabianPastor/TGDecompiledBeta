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

    public CameraView(Context context, boolean frontface) {
        super(context, null);
        this.isFrontface = frontface;
        this.initialFrontface = frontface;
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
        if (this.cameraSession != null) {
            CameraController.getInstance().close(this.cameraSession, null, null);
            this.cameraSession = null;
        }
        this.initied = false;
        this.isFrontface ^= 1;
        initCamera(this.isFrontface);
    }

    private void initCamera(boolean front) {
        CameraView cameraView = this;
        CameraInfo info = null;
        ArrayList<CameraInfo> cameraInfos = CameraController.getInstance().getCameras();
        if (cameraInfos != null) {
            for (int a = 0; a < cameraInfos.size(); a++) {
                CameraInfo cameraInfo = (CameraInfo) cameraInfos.get(a);
                if ((cameraView.isFrontface && cameraInfo.frontCamera != 0) || (!cameraView.isFrontface && cameraInfo.frontCamera == 0)) {
                    info = cameraInfo;
                    break;
                }
            }
            if (info != null) {
                Size aspectRatio;
                int wantedWidth;
                int wantedHeight;
                int width;
                Size pictureSize;
                Size pictureSize2;
                SurfaceTexture surfaceTexture;
                float screenSize = ((float) Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y));
                if (cameraView.initialFrontface) {
                    aspectRatio = new Size(16, 9);
                    wantedWidth = 480;
                    wantedHeight = 270;
                } else if (Math.abs(screenSize - 1.3333334f) < 0.1f) {
                    aspectRatio = new Size(4, 3);
                    wantedWidth = 1280;
                    wantedHeight = 960;
                } else {
                    aspectRatio = new Size(16, 9);
                    wantedWidth = 1280;
                    wantedHeight = 720;
                    if (cameraView.textureView.getWidth() > 0 && cameraView.textureView.getHeight() > 0) {
                        width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                        cameraView.previewSize = CameraController.chooseOptimalSize(info.getPreviewSizes(), width, (aspectRatio.getHeight() * width) / aspectRatio.getWidth(), aspectRatio);
                    }
                    pictureSize = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedWidth, wantedHeight, aspectRatio);
                    if (pictureSize.getWidth() >= 1280 && pictureSize.getHeight() >= 1280) {
                        if (Math.abs(screenSize - 1.3333334f) >= 0.1f) {
                            aspectRatio = new Size(3, 4);
                        } else {
                            aspectRatio = new Size(9, 16);
                        }
                        pictureSize2 = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedHeight, wantedWidth, aspectRatio);
                        if (pictureSize2.getWidth() < 1280 || pictureSize2.getHeight() < 1280) {
                            pictureSize = pictureSize2;
                        }
                    }
                    surfaceTexture = cameraView.textureView.getSurfaceTexture();
                    if (!(cameraView.previewSize == null || surfaceTexture == null)) {
                        surfaceTexture.setDefaultBufferSize(cameraView.previewSize.getWidth(), cameraView.previewSize.getHeight());
                        cameraView.cameraSession = new CameraSession(info, cameraView.previewSize, pictureSize, 256);
                        CameraController.getInstance().open(cameraView.cameraSession, surfaceTexture, new C05401(), new C05412());
                    }
                }
                width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
                cameraView.previewSize = CameraController.chooseOptimalSize(info.getPreviewSizes(), width, (aspectRatio.getHeight() * width) / aspectRatio.getWidth(), aspectRatio);
                pictureSize = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedWidth, wantedHeight, aspectRatio);
                if (Math.abs(screenSize - 1.3333334f) >= 0.1f) {
                    aspectRatio = new Size(9, 16);
                } else {
                    aspectRatio = new Size(3, 4);
                }
                pictureSize2 = CameraController.chooseOptimalSize(info.getPictureSizes(), wantedHeight, wantedWidth, aspectRatio);
                pictureSize = pictureSize2;
                surfaceTexture = cameraView.textureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(cameraView.previewSize.getWidth(), cameraView.previewSize.getHeight());
                cameraView.cameraSession = new CameraSession(info, cameraView.previewSize, pictureSize, 256);
                CameraController.getInstance().open(cameraView.cameraSession, surfaceTexture, new C05401(), new C05412());
            }
        }
    }

    public Size getPreviewSize() {
        return this.previewSize;
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        initCamera(this.isFrontface);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        checkPreviewMatrix();
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (this.cameraSession != null) {
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
        Matrix matrix;
        int i = previewWidth;
        int i2 = previewHeight;
        int i3 = rotation;
        this.txform.reset();
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float viewCenterX = (float) (viewWidth / 2);
        float viewCenterY = (float) (viewHeight / 2);
        if (i3 != 0) {
            if (i3 != 2) {
                scale = Math.max(((float) (r0.clipTop + viewHeight)) / ((float) i2), ((float) (r0.clipLeft + viewWidth)) / ((float) i));
                r0.txform.postScale((((float) i2) * scale) / ((float) viewWidth), (((float) i) * scale) / ((float) viewHeight), viewCenterX, viewCenterY);
                if (1 != i3) {
                    if (3 == i3) {
                        if (2 == i3) {
                            r0.txform.postRotate(180.0f, viewCenterX, viewCenterY);
                        }
                        if (r0.mirror) {
                            r0.txform.postScale(-1.0f, 1.0f, viewCenterX, viewCenterY);
                        }
                        if (!(r0.clipTop == 0 && r0.clipLeft == 0)) {
                            r0.txform.postTranslate((float) ((-r0.clipLeft) / 2), (float) ((-r0.clipTop) / 2));
                        }
                        r0.textureView.setTransform(r0.txform);
                        matrix = new Matrix();
                        matrix.postRotate((float) r0.cameraSession.getDisplayOrientation());
                        matrix.postScale(((float) viewWidth) / 2000.0f, ((float) viewHeight) / 2000.0f);
                        matrix.postTranslate(((float) viewWidth) / 2.0f, ((float) viewHeight) / 2.0f);
                        matrix.invert(r0.matrix);
                    }
                }
                r0.txform.postRotate((float) (90 * (i3 - 2)), viewCenterX, viewCenterY);
                if (r0.mirror) {
                    r0.txform.postScale(-1.0f, 1.0f, viewCenterX, viewCenterY);
                }
                r0.txform.postTranslate((float) ((-r0.clipLeft) / 2), (float) ((-r0.clipTop) / 2));
                r0.textureView.setTransform(r0.txform);
                matrix = new Matrix();
                matrix.postRotate((float) r0.cameraSession.getDisplayOrientation());
                matrix.postScale(((float) viewWidth) / 2000.0f, ((float) viewHeight) / 2000.0f);
                matrix.postTranslate(((float) viewWidth) / 2.0f, ((float) viewHeight) / 2.0f);
                matrix.invert(r0.matrix);
            }
        }
        scale = Math.max(((float) (r0.clipTop + viewHeight)) / ((float) i), ((float) (r0.clipLeft + viewWidth)) / ((float) i2));
        r0.txform.postScale((((float) i2) * scale) / ((float) viewWidth), (((float) i) * scale) / ((float) viewHeight), viewCenterX, viewCenterY);
        if (1 != i3) {
            if (3 == i3) {
                if (2 == i3) {
                    r0.txform.postRotate(180.0f, viewCenterX, viewCenterY);
                }
                if (r0.mirror) {
                    r0.txform.postScale(-1.0f, 1.0f, viewCenterX, viewCenterY);
                }
                r0.txform.postTranslate((float) ((-r0.clipLeft) / 2), (float) ((-r0.clipTop) / 2));
                r0.textureView.setTransform(r0.txform);
                matrix = new Matrix();
                matrix.postRotate((float) r0.cameraSession.getDisplayOrientation());
                matrix.postScale(((float) viewWidth) / 2000.0f, ((float) viewHeight) / 2000.0f);
                matrix.postTranslate(((float) viewWidth) / 2.0f, ((float) viewHeight) / 2.0f);
                matrix.invert(r0.matrix);
            }
        }
        r0.txform.postRotate((float) (90 * (i3 - 2)), viewCenterX, viewCenterY);
        if (r0.mirror) {
            r0.txform.postScale(-1.0f, 1.0f, viewCenterX, viewCenterY);
        }
        r0.txform.postTranslate((float) ((-r0.clipLeft) / 2), (float) ((-r0.clipTop) / 2));
        r0.textureView.setTransform(r0.txform);
        matrix = new Matrix();
        matrix.postRotate((float) r0.cameraSession.getDisplayOrientation());
        matrix.postScale(((float) viewWidth) / 2000.0f, ((float) viewHeight) / 2000.0f);
        matrix.postTranslate(((float) viewWidth) / 2.0f, ((float) viewHeight) / 2.0f);
        matrix.invert(r0.matrix);
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
            CameraController.getInstance().close(this.cameraSession, !async ? new CountDownLatch(1) : null, beforeDestroyRunnable);
        }
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Canvas canvas2 = canvas;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (!(this.focusProgress == 1.0f && r0.innerAlpha == 0.0f && r0.outerAlpha == 0.0f)) {
            int baseRad = AndroidUtilities.dp(NUM);
            long newTime = System.currentTimeMillis();
            long dt = newTime - r0.lastDrawTime;
            if (dt < 0 || dt > 17) {
                dt = 17;
            }
            r0.lastDrawTime = newTime;
            r0.outerPaint.setAlpha((int) (r0.interpolator.getInterpolation(r0.outerAlpha) * 255.0f));
            r0.innerPaint.setAlpha((int) (r0.interpolator.getInterpolation(r0.innerAlpha) * 127.0f));
            float interpolated = r0.interpolator.getInterpolation(r0.focusProgress);
            canvas2.drawCircle((float) r0.cx, (float) r0.cy, ((float) baseRad) + (((float) baseRad) * (1.0f - interpolated)), r0.outerPaint);
            canvas2.drawCircle((float) r0.cx, (float) r0.cy, ((float) baseRad) * interpolated, r0.innerPaint);
            if (r0.focusProgress < 1.0f) {
                r0.focusProgress += ((float) dt) / 200.0f;
                if (r0.focusProgress > 1.0f) {
                    r0.focusProgress = 1.0f;
                }
                invalidate();
            } else if (r0.innerAlpha != 0.0f) {
                r0.innerAlpha -= ((float) dt) / 150.0f;
                if (r0.innerAlpha < 0.0f) {
                    r0.innerAlpha = 0.0f;
                }
                invalidate();
            } else if (r0.outerAlpha != 0.0f) {
                r0.outerAlpha -= ((float) dt) / 150.0f;
                if (r0.outerAlpha < 0.0f) {
                    r0.outerAlpha = 0.0f;
                }
                invalidate();
            }
        }
        return result;
    }
}
