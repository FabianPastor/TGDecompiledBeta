package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.Crop.CropAreaView;
import org.telegram.ui.Components.Crop.CropGestureDetector;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.VideoEditTextureView;

public class CropView extends FrameLayout implements CropAreaView.AreaViewListener, CropGestureDetector.CropGestureListener {
    private static final float EPSILON = 1.0E-5f;
    private static final float MAX_SCALE = 30.0f;
    private static final int RESULT_SIDE = 1280;
    /* access modifiers changed from: private */
    public boolean animating;
    /* access modifiers changed from: private */
    public CropAreaView areaView;
    private Bitmap bitmap;
    private int bitmapRotation;
    private float bottomPadding;
    RectF cropRect = new RectF();
    private CropTransform cropTransform;
    private CropGestureDetector detector;
    /* access modifiers changed from: private */
    public boolean freeform;
    private boolean hasAspectRatioDialog;
    private ImageView imageView;
    private boolean inBubbleMode;
    private RectF initialAreaRect;
    private boolean isVisible;
    /* access modifiers changed from: private */
    public CropViewListener listener;
    private Matrix overlayMatrix;
    private PaintingOverlay paintingOverlay;
    private RectF previousAreaRect;
    private float rotationStartScale;
    RectF sizeRect = new RectF(0.0f, 0.0f, 1280.0f, 1280.0f);
    /* access modifiers changed from: private */
    public CropState state;
    private Matrix tempMatrix;
    private CropRectangle tempRect;
    float[] values = new float[9];
    private VideoEditTextureView videoEditTextureView;

    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);

        void onTapUp();

        void onUpdate();
    }

    private class CropState {
        private float baseRotation;
        /* access modifiers changed from: private */
        public float height;
        /* access modifiers changed from: private */
        public Matrix matrix;
        /* access modifiers changed from: private */
        public float minimumScale;
        /* access modifiers changed from: private */
        public boolean mirrored;
        private float orientation;
        /* access modifiers changed from: private */
        public float rotation;
        /* access modifiers changed from: private */
        public float scale;
        /* access modifiers changed from: private */
        public float width;
        private float x;
        private float y;

        private CropState(int w, int h, int bRotation) {
            this.width = (float) w;
            this.height = (float) h;
            this.x = 0.0f;
            this.y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = (float) bRotation;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        /* access modifiers changed from: private */
        public void update(int w, int h, int rotation2) {
            this.scale *= this.width / ((float) w);
            this.width = (float) w;
            this.height = (float) h;
            updateMinimumScale();
            this.matrix.getValues(CropView.this.values);
            this.matrix.reset();
            Matrix matrix2 = this.matrix;
            float f = this.scale;
            matrix2.postScale(f, f);
            this.matrix.postTranslate(CropView.this.values[2], CropView.this.values[5]);
            CropView.this.updateMatrix();
        }

        /* access modifiers changed from: private */
        public boolean hasChanges() {
            return Math.abs(this.x) > 1.0E-5f || Math.abs(this.y) > 1.0E-5f || Math.abs(this.scale - this.minimumScale) > 1.0E-5f || Math.abs(this.rotation) > 1.0E-5f || Math.abs(this.orientation) > 1.0E-5f;
        }

        /* access modifiers changed from: private */
        public float getWidth() {
            return this.width;
        }

        /* access modifiers changed from: private */
        public float getHeight() {
            return this.height;
        }

        /* access modifiers changed from: private */
        public float getOrientedWidth() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
        }

        /* access modifiers changed from: private */
        public float getOrientedHeight() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
        }

        /* access modifiers changed from: private */
        public void translate(float x2, float y2) {
            this.x += x2;
            this.y += y2;
            this.matrix.postTranslate(x2, y2);
        }

        /* access modifiers changed from: private */
        public float getX() {
            return this.x;
        }

        /* access modifiers changed from: private */
        public float getY() {
            return this.y;
        }

        private void setScale(float s, float pivotX, float pivotY) {
            this.scale = s;
            this.matrix.reset();
            this.matrix.setScale(s, s, pivotX, pivotY);
        }

        /* access modifiers changed from: private */
        public void scale(float s, float pivotX, float pivotY) {
            this.scale *= s;
            this.matrix.postScale(s, s, pivotX, pivotY);
        }

        /* access modifiers changed from: private */
        public float getScale() {
            return this.scale;
        }

        private float getMinimumScale() {
            return this.minimumScale;
        }

        /* access modifiers changed from: private */
        public void rotate(float angle, float pivotX, float pivotY) {
            this.rotation += angle;
            this.matrix.postRotate(angle, pivotX, pivotY);
        }

        /* access modifiers changed from: private */
        public float getRotation() {
            return this.rotation;
        }

        private boolean isMirrored() {
            return this.mirrored;
        }

        /* access modifiers changed from: private */
        public float getOrientation() {
            return this.orientation + this.baseRotation;
        }

        /* access modifiers changed from: private */
        public int getOrientationOnly() {
            return (int) this.orientation;
        }

        /* access modifiers changed from: private */
        public float getBaseRotation() {
            return this.baseRotation;
        }

        /* access modifiers changed from: private */
        public void mirror() {
            this.mirrored = !this.mirrored;
        }

        /* access modifiers changed from: private */
        public void reset(CropAreaView areaView, float orient, boolean freeform) {
            this.matrix.reset();
            this.x = 0.0f;
            this.y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = orient;
            updateMinimumScale();
            float f = this.minimumScale;
            this.scale = f;
            this.matrix.postScale(f, f);
        }

        private void updateMinimumScale() {
            float f = this.orientation;
            float f2 = this.baseRotation;
            float w = (f + f2) % 180.0f != 0.0f ? this.height : this.width;
            float h = (f + f2) % 180.0f != 0.0f ? this.width : this.height;
            if (CropView.this.freeform) {
                this.minimumScale = CropView.this.areaView.getCropWidth() / w;
            } else {
                this.minimumScale = Math.max(CropView.this.areaView.getCropWidth() / w, CropView.this.areaView.getCropHeight() / h);
            }
        }

        /* access modifiers changed from: private */
        public void getConcatMatrix(Matrix toMatrix) {
            toMatrix.postConcat(this.matrix);
        }

        /* access modifiers changed from: private */
        public Matrix getMatrix() {
            Matrix m = new Matrix();
            m.set(this.matrix);
            return m;
        }
    }

    public CropView(Context context) {
        super(context);
        this.inBubbleMode = context instanceof BubbleActivity;
        this.previousAreaRect = new RectF();
        this.initialAreaRect = new RectF();
        this.overlayMatrix = new Matrix();
        this.tempRect = new CropRectangle();
        this.tempMatrix = new Matrix();
        this.animating = false;
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.MATRIX);
        addView(this.imageView);
        CropGestureDetector cropGestureDetector = new CropGestureDetector(context);
        this.detector = cropGestureDetector;
        cropGestureDetector.setOnGestureListener(this);
        CropAreaView cropAreaView = new CropAreaView(context);
        this.areaView = cropAreaView;
        cropAreaView.setListener(this);
        addView(this.areaView);
    }

    public boolean isReady() {
        return !this.detector.isScaling() && !this.detector.isDragging() && !this.areaView.isDragging();
    }

    public void setListener(CropViewListener l) {
        this.listener = l;
    }

    public void setBottomPadding(float value) {
        this.bottomPadding = value;
        this.areaView.setBottomPadding(value);
    }

    public void setAspectRatio(float ratio) {
        this.areaView.setActualRect(ratio);
    }

    public void setBitmap(Bitmap b, int rotation, boolean fform, boolean same, PaintingOverlay overlay, CropTransform transform, VideoEditTextureView videoView, MediaController.CropState restoreState) {
        Bitmap bitmap2 = b;
        int i = rotation;
        VideoEditTextureView videoEditTextureView2 = videoView;
        this.freeform = fform;
        this.paintingOverlay = overlay;
        this.videoEditTextureView = videoEditTextureView2;
        this.cropTransform = transform;
        this.bitmapRotation = i;
        this.bitmap = bitmap2;
        this.areaView.setIsVideo(videoEditTextureView2 != null);
        if (bitmap2 == null && videoEditTextureView2 == null) {
            this.state = null;
            this.imageView.setImageDrawable((Drawable) null);
            MediaController.CropState cropState = restoreState;
            return;
        }
        final int w = getCurrentWidth();
        final int h = getCurrentHeight();
        CropState cropState2 = this.state;
        if (cropState2 == null || !same) {
            CropState cropState3 = r0;
            CropState cropState4 = new CropState(w, h, 0);
            this.state = cropState3;
            final MediaController.CropState cropState5 = restoreState;
            this.areaView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    int rotatedH;
                    int rotatedW;
                    float stateHeight;
                    float stateWidth;
                    CropView.this.reset();
                    MediaController.CropState cropState = cropState5;
                    if (cropState != null) {
                        boolean z = true;
                        if (cropState.lockedAspectRatio > 1.0E-4f) {
                            CropView.this.areaView.setLockedAspectRatio(cropState5.lockedAspectRatio);
                            if (CropView.this.listener != null) {
                                CropView.this.listener.onAspectLock(true);
                            }
                        }
                        CropView.this.setFreeform(cropState5.freeform);
                        float aspect = CropView.this.areaView.getAspectRatio();
                        if (cropState5.transformRotation == 90 || cropState5.transformRotation == 270) {
                            aspect = 1.0f / aspect;
                            stateWidth = CropView.this.state.height;
                            stateHeight = CropView.this.state.width;
                            rotatedW = h;
                            rotatedH = w;
                        } else {
                            stateWidth = CropView.this.state.width;
                            stateHeight = CropView.this.state.height;
                            rotatedW = w;
                            rotatedH = h;
                        }
                        int orientation = cropState5.transformRotation;
                        boolean fform = CropView.this.freeform;
                        if (!CropView.this.freeform || CropView.this.areaView.getLockAspectRatio() <= 0.0f) {
                            CropAreaView access$100 = CropView.this.areaView;
                            int access$700 = CropView.this.getCurrentWidth();
                            int access$800 = CropView.this.getCurrentHeight();
                            if ((((float) orientation) + CropView.this.state.getBaseRotation()) % 180.0f == 0.0f) {
                                z = false;
                            }
                            access$100.setBitmap(access$700, access$800, z, CropView.this.freeform);
                        } else {
                            CropView.this.areaView.setLockedAspectRatio(1.0f / CropView.this.areaView.getLockAspectRatio());
                            CropView.this.areaView.setActualRect(CropView.this.areaView.getLockAspectRatio());
                            fform = false;
                        }
                        CropView.this.state.reset(CropView.this.areaView, (float) orientation, fform);
                        CropView.this.areaView.setActualRect((cropState5.cropPw * aspect) / cropState5.cropPh);
                        boolean unused = CropView.this.state.mirrored = cropState5.mirrored;
                        CropView.this.state.rotate(cropState5.cropRotate, 0.0f, 0.0f);
                        CropView.this.state.translate(cropState5.cropPx * ((float) rotatedW) * CropView.this.state.minimumScale, cropState5.cropPy * ((float) rotatedH) * CropView.this.state.minimumScale);
                        CropView.this.state.scale(cropState5.cropScale * (Math.max(CropView.this.areaView.getCropWidth() / stateWidth, CropView.this.areaView.getCropHeight() / stateHeight) / CropView.this.state.minimumScale), 0.0f, 0.0f);
                        CropView.this.updateMatrix();
                        if (CropView.this.listener != null) {
                            CropView.this.listener.onChange(false);
                        }
                    }
                    CropView.this.areaView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            cropState2.update(w, h, i);
            MediaController.CropState cropState6 = restoreState;
        }
        this.imageView.setImageBitmap(videoEditTextureView2 == null ? this.bitmap : null);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true, false);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }

    public void setFreeform(boolean fform) {
        this.areaView.setFreeform(fform);
        this.freeform = fform;
    }

    public void onShow() {
        this.isVisible = true;
    }

    public void onHide() {
        this.videoEditTextureView = null;
        this.paintingOverlay = null;
        this.isVisible = false;
    }

    public void show() {
        updateCropTransform();
        this.areaView.setDimVisibility(true);
        this.areaView.setFrameVisibility(true, true);
        this.areaView.invalidate();
    }

    public void hide() {
        this.imageView.setVisibility(4);
        this.areaView.setDimVisibility(false);
        this.areaView.setFrameVisibility(false, false);
        this.areaView.invalidate();
    }

    public void reset() {
        this.areaView.resetAnimator();
        this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), this.state.getBaseRotation() % 180.0f != 0.0f, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        this.state.reset(this.areaView, 0.0f, this.freeform);
        boolean unused = this.state.mirrored = false;
        this.areaView.getCropRect(this.initialAreaRect);
        updateMatrix();
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(true);
            this.listener.onAspectLock(false);
        }
    }

    public void updateMatrix() {
        this.overlayMatrix.reset();
        if (this.state.getBaseRotation() == 90.0f || this.state.getBaseRotation() == 270.0f) {
            this.overlayMatrix.postTranslate((-this.state.getHeight()) / 2.0f, (-this.state.getWidth()) / 2.0f);
        } else {
            this.overlayMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        }
        this.overlayMatrix.postRotate((float) this.state.getOrientationOnly());
        this.state.getConcatMatrix(this.overlayMatrix);
        this.overlayMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        if (!this.freeform || this.isVisible) {
            updateCropTransform();
            this.listener.onUpdate();
        }
        invalidate();
    }

    private void fillAreaView(RectF targetRect, boolean allowZoomOut) {
        boolean ensureFit;
        float scale;
        RectF rectF = targetRect;
        int i = 0;
        float[] currentScale = {1.0f};
        float scale2 = Math.max(targetRect.width() / this.areaView.getCropWidth(), targetRect.height() / this.areaView.getCropHeight());
        float newScale = this.state.getScale() * scale2;
        if (newScale > 30.0f) {
            scale = 30.0f / this.state.getScale();
            ensureFit = true;
        } else {
            scale = scale2;
            ensureFit = false;
        }
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            i = AndroidUtilities.statusBarHeight;
        }
        float x = ((targetRect.centerX() - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth();
        float y = ((targetRect.centerY() - (((((float) this.imageView.getHeight()) - this.bottomPadding) + ((float) i)) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        final boolean animEnsureFit = ensureFit;
        CropView$$ExternalSyntheticLambda0 cropView$$ExternalSyntheticLambda0 = r0;
        float f = newScale;
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        CropView$$ExternalSyntheticLambda0 cropView$$ExternalSyntheticLambda02 = new CropView$$ExternalSyntheticLambda0(this, scale, currentScale, x, y);
        animator.addUpdateListener(cropView$$ExternalSyntheticLambda0);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animEnsureFit) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(rectF, animator, true);
        this.initialAreaRect.set(rectF);
    }

    /* renamed from: lambda$fillAreaView$0$org-telegram-ui-Components-Crop-CropView  reason: not valid java name */
    public /* synthetic */ void m2222lambda$fillAreaView$0$orgtelegramuiComponentsCropCropView(float targetScale, float[] currentScale, float x, float y, ValueAnimator animation) {
        float deltaScale = (((targetScale - 1.0f) * ((Float) animation.getAnimatedValue()).floatValue()) + 1.0f) / currentScale[0];
        currentScale[0] = currentScale[0] * deltaScale;
        this.state.scale(deltaScale, x, y);
        updateMatrix();
    }

    private float fitScale(RectF contentRect, float scale, float ratio) {
        float scaledW = contentRect.width() * ratio;
        float scaledH = contentRect.height() * ratio;
        float scaledX = (contentRect.width() - scaledW) / 2.0f;
        float scaledY = (contentRect.height() - scaledH) / 2.0f;
        contentRect.set(contentRect.left + scaledX, contentRect.top + scaledY, contentRect.left + scaledX + scaledW, contentRect.top + scaledY + scaledH);
        return scale * ratio;
    }

    private void fitTranslation(RectF contentRect, RectF boundsRect, PointF translation, float radians) {
        RectF rectF = contentRect;
        RectF rectF2 = boundsRect;
        PointF pointF = translation;
        float f = radians;
        float frameLeft = rectF2.left;
        float frameTop = rectF2.top;
        float frameRight = rectF2.right;
        float frameBottom = rectF2.bottom;
        if (rectF.left > frameLeft) {
            frameRight += rectF.left - frameLeft;
            frameLeft = rectF.left;
        }
        if (rectF.top > frameTop) {
            frameBottom += rectF.top - frameTop;
            frameTop = rectF.top;
        }
        if (rectF.right < frameRight) {
            frameLeft += rectF.right - frameRight;
        }
        if (rectF.bottom < frameBottom) {
            frameTop += rectF.bottom - frameBottom;
        }
        float deltaX = boundsRect.centerX() - ((boundsRect.width() / 2.0f) + frameLeft);
        float deltaY = boundsRect.centerY() - ((boundsRect.height() / 2.0f) + frameTop);
        double d = (double) f;
        Double.isNaN(d);
        double sin = Math.sin(1.5707963267948966d - d);
        double d2 = (double) deltaX;
        Double.isNaN(d2);
        double d3 = (double) f;
        Double.isNaN(d3);
        double cos = Math.cos(1.5707963267948966d - d3);
        double d4 = (double) deltaX;
        Double.isNaN(d4);
        float xCompY = (float) (cos * d4);
        double d5 = (double) f;
        Double.isNaN(d5);
        double cos2 = Math.cos(d5 + 1.5707963267948966d);
        double d6 = (double) deltaY;
        Double.isNaN(d6);
        double d7 = (double) f;
        Double.isNaN(d7);
        double sin2 = Math.sin(d7 + 1.5707963267948966d);
        double d8 = (double) deltaY;
        Double.isNaN(d8);
        float f2 = pointF.x + ((float) (sin * d2));
        pointF.set(f2 + ((float) (cos2 * d6)), pointF.y + xCompY + ((float) (sin2 * d8)));
    }

    public RectF calculateBoundingBox(float w, float h, float rotation) {
        RectF result = new RectF(0.0f, 0.0f, w, h);
        Matrix m = new Matrix();
        m.postRotate(rotation, w / 2.0f, h / 2.0f);
        m.mapRect(result);
        return result;
    }

    public float scaleWidthToMaxSize(RectF sizeRect2, RectF maxSizeRect) {
        float w = maxSizeRect.width();
        if (((float) Math.floor((double) ((sizeRect2.height() * w) / sizeRect2.width()))) <= maxSizeRect.height()) {
            return w;
        }
        return (float) Math.floor((double) ((sizeRect2.width() * maxSizeRect.height()) / sizeRect2.height()));
    }

    private static class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        /* access modifiers changed from: package-private */
        public void setRect(RectF rect) {
            this.coords[0] = rect.left;
            this.coords[1] = rect.top;
            this.coords[2] = rect.right;
            this.coords[3] = rect.top;
            this.coords[4] = rect.right;
            this.coords[5] = rect.bottom;
            this.coords[6] = rect.left;
            this.coords[7] = rect.bottom;
        }

        /* access modifiers changed from: package-private */
        public void applyMatrix(Matrix m) {
            m.mapPoints(this.coords);
        }

        /* access modifiers changed from: package-private */
        public void getRect(RectF rect) {
            float[] fArr = this.coords;
            rect.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean allowScale, boolean maximize, boolean animated) {
        fitContentInBounds(allowScale, maximize, animated, false);
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean allowScale, boolean maximize, boolean animated, boolean fast) {
        float targetScale;
        if (this.state != null) {
            float boundsW = this.areaView.getCropWidth();
            float boundsH = this.areaView.getCropHeight();
            float contentW = this.state.getOrientedWidth();
            float contentH = this.state.getOrientedHeight();
            float rotation = this.state.getRotation();
            float radians = (float) Math.toRadians((double) rotation);
            RectF boundsRect = calculateBoundingBox(boundsW, boundsH, rotation);
            RectF contentRect = new RectF(0.0f, 0.0f, contentW, contentH);
            float scale = this.state.getScale();
            this.tempRect.setRect(contentRect);
            Matrix matrix = this.state.getMatrix();
            matrix.preTranslate(((boundsW - contentW) / 2.0f) / scale, ((boundsH - contentH) / 2.0f) / scale);
            this.tempMatrix.reset();
            this.tempMatrix.setTranslate(contentRect.centerX(), contentRect.centerY());
            Matrix matrix2 = this.tempMatrix;
            matrix2.setConcat(matrix2, matrix);
            this.tempMatrix.preTranslate(-contentRect.centerX(), -contentRect.centerY());
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempMatrix.reset();
            this.tempMatrix.preRotate(-rotation, contentW / 2.0f, contentH / 2.0f);
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempRect.getRect(contentRect);
            PointF targetTranslation = new PointF(this.state.getX(), this.state.getY());
            float targetScale2 = scale;
            if (!contentRect.contains(boundsRect)) {
                if (allowScale && (boundsRect.width() > contentRect.width() || boundsRect.height() > contentRect.height())) {
                    targetScale2 = fitScale(contentRect, scale, boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect));
                }
                fitTranslation(contentRect, boundsRect, targetTranslation, radians);
                targetScale = targetScale2;
            } else if (!maximize || this.rotationStartScale <= 0.0f) {
                targetScale = targetScale2;
            } else {
                float ratio = boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect);
                if (this.state.getScale() * ratio < this.rotationStartScale) {
                    ratio = 1.0f;
                }
                float targetScale3 = fitScale(contentRect, scale, ratio);
                fitTranslation(contentRect, boundsRect, targetTranslation, radians);
                targetScale = targetScale3;
            }
            float dx = targetTranslation.x - this.state.getX();
            float dy = targetTranslation.y - this.state.getY();
            if (animated) {
                float animScale = targetScale / scale;
                float animDX = dx;
                float animDY = dy;
                if (Math.abs(animScale - 1.0f) >= 1.0E-5f || Math.abs(animDX) >= 1.0E-5f || Math.abs(animDY) >= 1.0E-5f) {
                    PointF pointF = targetTranslation;
                    this.animating = true;
                    float f = boundsW;
                    float boundsW2 = dy;
                    float f2 = boundsH;
                    ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    float f3 = contentW;
                    float contentW2 = dx;
                    Matrix matrix3 = matrix;
                    animator.addUpdateListener(new CropView$$ExternalSyntheticLambda1(this, animDX, new float[]{1.0f, 0.0f, 0.0f}, animDY, animScale));
                    float f4 = scale;
                    RectF rectF = contentRect;
                    final boolean z = fast;
                    RectF rectF2 = boundsRect;
                    final boolean z2 = allowScale;
                    float f5 = radians;
                    final boolean z3 = maximize;
                    float f6 = rotation;
                    final boolean z4 = animated;
                    animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            boolean unused = CropView.this.animating = false;
                            if (!z) {
                                CropView.this.fitContentInBounds(z2, z3, z4, true);
                            }
                        }
                    });
                    animator.setInterpolator(this.areaView.getInterpolator());
                    animator.setDuration(fast ? 100 : 200);
                    animator.start();
                    return;
                }
                return;
            }
            Matrix matrix4 = matrix;
            RectF rectF3 = contentRect;
            RectF rectF4 = boundsRect;
            float f7 = radians;
            float f8 = rotation;
            float f9 = boundsW;
            float var_ = boundsH;
            float var_ = contentW;
            this.state.translate(dx, dy);
            this.state.scale(targetScale / scale, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    /* renamed from: lambda$fitContentInBounds$1$org-telegram-ui-Components-Crop-CropView  reason: not valid java name */
    public /* synthetic */ void m2223x3055CLASSNAME(float animDX, float[] currentValues, float animDY, float animScale, ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        float deltaX = (animDX * value) - currentValues[1];
        currentValues[1] = currentValues[1] + deltaX;
        float deltaY = (animDY * value) - currentValues[2];
        currentValues[2] = currentValues[2] + deltaY;
        this.state.translate(currentValues[0] * deltaX, currentValues[0] * deltaY);
        float deltaScale = (((animScale - 1.0f) * value) + 1.0f) / currentValues[0];
        currentValues[0] = currentValues[0] * deltaScale;
        this.state.scale(deltaScale, 0.0f, 0.0f);
        updateMatrix();
    }

    /* access modifiers changed from: private */
    public int getCurrentWidth() {
        VideoEditTextureView videoEditTextureView2 = this.videoEditTextureView;
        if (videoEditTextureView2 != null) {
            return videoEditTextureView2.getVideoWidth();
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? this.bitmap.getHeight() : this.bitmap.getWidth();
    }

    /* access modifiers changed from: private */
    public int getCurrentHeight() {
        VideoEditTextureView videoEditTextureView2 = this.videoEditTextureView;
        if (videoEditTextureView2 != null) {
            return videoEditTextureView2.getVideoHeight();
        }
        int i = this.bitmapRotation;
        return (i == 90 || i == 270) ? this.bitmap.getWidth() : this.bitmap.getHeight();
    }

    public boolean mirror() {
        CropState cropState = this.state;
        boolean z = false;
        if (cropState == null) {
            return false;
        }
        cropState.mirror();
        updateMatrix();
        if (this.listener != null) {
            float orientation = (this.state.getOrientation() - this.state.getBaseRotation()) % 360.0f;
            CropViewListener cropViewListener = this.listener;
            if (!this.state.hasChanges() && orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored) {
                z = true;
            }
            cropViewListener.onChange(z);
        }
        return this.state.mirrored;
    }

    public boolean rotate90Degrees() {
        if (this.state == null) {
            return false;
        }
        this.areaView.resetAnimator();
        resetRotationStartScale();
        float orientation = ((this.state.getOrientation() - this.state.getBaseRotation()) - 90.0f) % 360.0f;
        boolean fform = this.freeform;
        if (!this.freeform || this.areaView.getLockAspectRatio() <= 0.0f) {
            this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), (this.state.getBaseRotation() + orientation) % 180.0f != 0.0f, this.freeform);
        } else {
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
            CropAreaView cropAreaView2 = this.areaView;
            cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
            fform = false;
        }
        this.state.reset(this.areaView, orientation, fform);
        updateMatrix();
        fitContentInBounds(true, false, false);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored);
        }
        if (this.state.getOrientationOnly() != 0) {
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.animating || this.areaView.onTouchEvent(event)) {
            return true;
        }
        switch (event.getAction()) {
            case 0:
                onScrollChangeBegan();
                break;
            case 1:
            case 3:
                onScrollChangeEnded();
                break;
        }
        try {
            return this.detector.onTouchEvent(event);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void onAreaChangeBegan() {
        this.areaView.getCropRect(this.previousAreaRect);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
        }
    }

    public void onAreaChange() {
        this.areaView.setGridType(CropAreaView.GridType.MAJOR, false);
        float x = this.previousAreaRect.centerX() - this.areaView.getCropCenterX();
        float y = this.previousAreaRect.centerY() - this.areaView.getCropCenterY();
        CropState cropState = this.state;
        if (cropState != null) {
            cropState.translate(x, y);
        }
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    public void onAreaChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    public void onDrag(float dx, float dy) {
        if (!this.animating) {
            this.state.translate(dx, dy);
            updateMatrix();
        }
    }

    public void onFling(float startX, float startY, float velocityX, float velocityY) {
    }

    public void onTapUp() {
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onTapUp();
        }
    }

    public void onScrollChangeBegan() {
        if (!this.animating) {
            this.areaView.setGridType(CropAreaView.GridType.MAJOR, true);
            resetRotationStartScale();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                cropViewListener.onChange(false);
            }
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    public void onScale(float scale, float x, float y) {
        if (!this.animating) {
            if (this.state.getScale() * scale > 30.0f) {
                scale = 30.0f / this.state.getScale();
            }
            this.state.scale(scale, ((x - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((y - (((((float) this.imageView.getHeight()) - this.bottomPadding) - ((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight))) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
            updateMatrix();
        }
    }

    public void onRotationBegan() {
        this.areaView.setGridType(CropAreaView.GridType.MINOR, false);
        if (this.rotationStartScale < 1.0E-5f) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    public void setRotation(float angle) {
        this.state.rotate(angle - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public static void editBitmap(Context context, String path, Bitmap b, Canvas canvas, Bitmap canvasBitmap, Bitmap.CompressFormat format, Matrix stateMatrix, int contentWidth, int contentHeight, float stateScale, float rotation, float orientationOnly, float scale, boolean mirror, ArrayList<VideoEditedInfo.MediaEntity> entities, boolean clear) {
        Bitmap b2;
        int N;
        FileOutputStream stream;
        Matrix matrix;
        TextPaintView textPaintView;
        Matrix matrix2;
        FileOutputStream stream2;
        int type;
        Bitmap bitmap2 = canvasBitmap;
        float f = orientationOnly;
        float f2 = scale;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList = entities;
        if (clear) {
            try {
                bitmap2.eraseColor(0);
            } catch (Throwable th) {
                e = th;
                String str = path;
                Bitmap bitmap3 = b;
                Bitmap.CompressFormat compressFormat = format;
                FileLog.e(e);
            }
        }
        if (b == null) {
            b2 = BitmapFactory.decodeFile(path);
        } else {
            b2 = b;
        }
        try {
            float sc = ((float) Math.max(b2.getWidth(), b2.getHeight())) / ((float) Math.max(contentWidth, contentHeight));
            Matrix matrix3 = new Matrix();
            matrix3.postTranslate((float) ((-b2.getWidth()) / 2), (float) ((-b2.getHeight()) / 2));
            if (mirror) {
                matrix3.postScale(-1.0f, 1.0f);
            }
            matrix3.postScale(1.0f / sc, 1.0f / sc);
            matrix3.postRotate(f);
            matrix3.postConcat(stateMatrix);
            matrix3.postScale(f2, f2);
            matrix3.postTranslate((float) (canvasBitmap.getWidth() / 2), (float) (canvasBitmap.getHeight() / 2));
            canvas.drawBitmap(b2, matrix3, new Paint(2));
            try {
                FileOutputStream stream3 = new FileOutputStream(new File(path));
                try {
                    bitmap2.compress(format, 87, stream3);
                    stream3.close();
                    if (arrayList == null || entities.isEmpty()) {
                        Matrix matrix4 = matrix3;
                        FileOutputStream fileOutputStream = stream3;
                    } else {
                        float[] point = new float[4];
                        float newScale = (1.0f / sc) * f2 * stateScale * (((float) b2.getWidth()) / ((float) canvasBitmap.getWidth()));
                        TextPaintView textPaintView2 = null;
                        int N2 = entities.size();
                        int a = 0;
                        while (true) {
                            N = N2;
                            if (a >= N) {
                                break;
                            }
                            int N3 = N;
                            VideoEditedInfo.MediaEntity entity = arrayList.get(a);
                            float sc2 = sc;
                            point[0] = (entity.x * ((float) b2.getWidth())) + ((((float) entity.viewWidth) * entity.scale) / 2.0f);
                            point[1] = (entity.y * ((float) b2.getHeight())) + ((((float) entity.viewHeight) * entity.scale) / 2.0f);
                            point[2] = entity.textViewX * ((float) b2.getWidth());
                            point[3] = entity.textViewY * ((float) b2.getHeight());
                            matrix3.mapPoints(point);
                            if (entity.type == 0) {
                                int width = canvasBitmap.getWidth() / 2;
                                entity.viewHeight = width;
                                entity.viewWidth = width;
                                textPaintView = textPaintView2;
                                matrix2 = matrix3;
                                stream2 = stream3;
                            } else if (entity.type == 1) {
                                entity.fontSize = canvasBitmap.getWidth() / 9;
                                if (textPaintView2 == null) {
                                    TextPaintView textPaintView3 = textPaintView2;
                                    matrix = matrix3;
                                    stream = stream3;
                                    textPaintView2 = new TextPaintView(context, new Point(0.0f, 0.0f), entity.fontSize, "", new Swatch(-16777216, 0.85f, 0.1f), 0);
                                    textPaintView2.setMaxWidth(canvasBitmap.getWidth() - 20);
                                } else {
                                    TextPaintView textPaintView4 = textPaintView2;
                                    matrix = matrix3;
                                    stream = stream3;
                                }
                                if ((entity.subType & 1) != 0) {
                                    type = 0;
                                } else if ((entity.subType & 4) != 0) {
                                    type = 2;
                                } else {
                                    type = 1;
                                }
                                textPaintView2.setType(type);
                                textPaintView2.setText(entity.text);
                                textPaintView2.measure(View.MeasureSpec.makeMeasureSpec(canvasBitmap.getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(canvasBitmap.getHeight(), Integer.MIN_VALUE));
                                entity.viewWidth = textPaintView2.getMeasuredWidth();
                                entity.viewHeight = textPaintView2.getMeasuredHeight();
                                entity.scale *= newScale;
                                entity.x = (point[0] - ((((float) entity.viewWidth) * entity.scale) / 2.0f)) / ((float) canvasBitmap.getWidth());
                                entity.y = (point[1] - ((((float) entity.viewHeight) * entity.scale) / 2.0f)) / ((float) canvasBitmap.getHeight());
                                entity.textViewX = point[2] / ((float) canvasBitmap.getWidth());
                                entity.textViewY = point[3] / ((float) canvasBitmap.getHeight());
                                entity.width = (((float) entity.viewWidth) * entity.scale) / ((float) canvasBitmap.getWidth());
                                entity.height = (((float) entity.viewHeight) * entity.scale) / ((float) canvasBitmap.getHeight());
                                entity.textViewWidth = ((float) entity.viewWidth) / ((float) canvasBitmap.getWidth());
                                entity.textViewHeight = ((float) entity.viewHeight) / ((float) canvasBitmap.getHeight());
                                double d = (double) entity.rotation;
                                double d2 = (double) (rotation + f);
                                Double.isNaN(d2);
                                Double.isNaN(d);
                                entity.rotation = (float) (d - (d2 * 0.017453292519943295d));
                                a++;
                                Canvas canvas2 = canvas;
                                Matrix matrix5 = stateMatrix;
                                float f3 = scale;
                                arrayList = entities;
                                N2 = N3;
                                sc = sc2;
                                matrix3 = matrix;
                                stream3 = stream;
                            } else {
                                textPaintView = textPaintView2;
                                matrix2 = matrix3;
                                stream2 = stream3;
                            }
                            textPaintView2 = textPaintView;
                            entity.scale *= newScale;
                            entity.x = (point[0] - ((((float) entity.viewWidth) * entity.scale) / 2.0f)) / ((float) canvasBitmap.getWidth());
                            entity.y = (point[1] - ((((float) entity.viewHeight) * entity.scale) / 2.0f)) / ((float) canvasBitmap.getHeight());
                            entity.textViewX = point[2] / ((float) canvasBitmap.getWidth());
                            entity.textViewY = point[3] / ((float) canvasBitmap.getHeight());
                            entity.width = (((float) entity.viewWidth) * entity.scale) / ((float) canvasBitmap.getWidth());
                            entity.height = (((float) entity.viewHeight) * entity.scale) / ((float) canvasBitmap.getHeight());
                            entity.textViewWidth = ((float) entity.viewWidth) / ((float) canvasBitmap.getWidth());
                            entity.textViewHeight = ((float) entity.viewHeight) / ((float) canvasBitmap.getHeight());
                            double d3 = (double) entity.rotation;
                            double d22 = (double) (rotation + f);
                            Double.isNaN(d22);
                            Double.isNaN(d3);
                            entity.rotation = (float) (d3 - (d22 * 0.017453292519943295d));
                            a++;
                            Canvas canvas22 = canvas;
                            Matrix matrix52 = stateMatrix;
                            float var_ = scale;
                            arrayList = entities;
                            N2 = N3;
                            sc = sc2;
                            matrix3 = matrix;
                            stream3 = stream;
                        }
                        TextPaintView textPaintView5 = textPaintView2;
                        int i = N;
                        float f4 = sc;
                        Matrix matrix6 = matrix3;
                        FileOutputStream fileOutputStream2 = stream3;
                    }
                    b2.recycle();
                } catch (Throwable th2) {
                    e = th2;
                    FileLog.e(e);
                }
            } catch (Throwable th3) {
                e = th3;
                Bitmap.CompressFormat compressFormat2 = format;
                FileLog.e(e);
            }
        } catch (Throwable th4) {
            e = th4;
            String str2 = path;
            Bitmap.CompressFormat compressFormat3 = format;
            FileLog.e(e);
        }
    }

    private void updateCropTransform() {
        int sh;
        int sw;
        float realMininumScale;
        if (this.cropTransform != null && this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            int width = (int) Math.ceil((double) scaleWidthToMaxSize(this.cropRect, this.sizeRect));
            int height = (int) Math.ceil((double) (((float) width) / this.areaView.getAspectRatio()));
            float scale = ((float) width) / this.areaView.getCropWidth();
            this.state.matrix.getValues(this.values);
            float sc = this.state.minimumScale * scale;
            int transformRotation = this.state.getOrientationOnly();
            while (transformRotation < 0) {
                transformRotation += 360;
            }
            if (transformRotation == 90 || transformRotation == 270) {
                sw = (int) this.state.height;
                sh = (int) this.state.width;
            } else {
                sw = (int) this.state.width;
                sh = (int) this.state.height;
            }
            double d = (double) width;
            double ceil = Math.ceil((double) (((float) sw) * sc));
            Double.isNaN(d);
            float cropPw = (float) (d / ceil);
            double d2 = (double) height;
            double ceil2 = Math.ceil((double) (((float) sh) * sc));
            Double.isNaN(d2);
            float cropPh = (float) (d2 / ceil2);
            if (cropPw > 1.0f || cropPh > 1.0f) {
                float max = Math.max(cropPw, cropPh);
                cropPw /= max;
                cropPh /= max;
            }
            RectF rect = this.areaView.getTargetRectToFill(((float) sw) / ((float) sh));
            if (this.freeform) {
                realMininumScale = rect.width() / ((float) sw);
            } else {
                realMininumScale = Math.max(rect.width() / ((float) sw), rect.height() / ((float) sh));
            }
            float cropScale = this.state.scale / realMininumScale;
            float trueCropScale = this.state.scale / this.state.minimumScale;
            float cropPx = (this.values[2] / ((float) sw)) / this.state.scale;
            float cropPy = (this.values[5] / ((float) sh)) / this.state.scale;
            float cropRotate = this.state.rotation;
            RectF targetRect = this.areaView.getTargetRectToFill();
            float tx = this.areaView.getCropCenterX() - targetRect.centerX();
            float ty = this.areaView.getCropCenterY() - targetRect.centerY();
            this.cropTransform.setViewTransform(this.state.mirrored || this.state.hasChanges() || this.state.getBaseRotation() >= 1.0E-5f, cropPx, cropPy, cropRotate, this.state.getOrientationOnly(), cropScale, trueCropScale, this.state.minimumScale / realMininumScale, cropPw, cropPh, tx, ty, this.state.mirrored);
        }
    }

    public static String getCopy(String path) {
        File directory = FileLoader.getDirectory(4);
        File f = new File(directory, SharedConfig.getLastLocalId() + "_temp.jpg");
        try {
            AndroidUtilities.copyFile(new File(path), f);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return f.getAbsolutePath();
    }

    public void makeCrop(MediaController.MediaEditState editState) {
        int sh;
        int sw;
        MediaController.MediaEditState mediaEditState = editState;
        if (this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            int width = (int) Math.ceil((double) scaleWidthToMaxSize(this.cropRect, this.sizeRect));
            int height = (int) Math.ceil((double) (((float) width) / this.areaView.getAspectRatio()));
            float scale = ((float) width) / this.areaView.getCropWidth();
            if (mediaEditState.paintPath != null) {
                Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(resultBitmap);
                String path = getCopy(mediaEditState.paintPath);
                if (mediaEditState.croppedPaintPath != null) {
                    new File(mediaEditState.croppedPaintPath).delete();
                    mediaEditState.croppedPaintPath = null;
                }
                mediaEditState.croppedPaintPath = path;
                if (mediaEditState.mediaEntities == null || mediaEditState.mediaEntities.isEmpty()) {
                    mediaEditState.croppedMediaEntities = null;
                } else {
                    mediaEditState.croppedMediaEntities = new ArrayList<>(mediaEditState.mediaEntities.size());
                    int N = mediaEditState.mediaEntities.size();
                    for (int a = 0; a < N; a++) {
                        mediaEditState.croppedMediaEntities.add(mediaEditState.mediaEntities.get(a).copy());
                    }
                }
                String str = path;
                editBitmap(getContext(), path, (Bitmap) null, canvas, resultBitmap, Bitmap.CompressFormat.PNG, this.state.matrix, getCurrentWidth(), getCurrentHeight(), this.state.scale, this.state.rotation, (float) this.state.getOrientationOnly(), scale, false, mediaEditState.croppedMediaEntities, false);
            }
            if (mediaEditState.cropState == null) {
                mediaEditState.cropState = new MediaController.CropState();
            }
            this.state.matrix.getValues(this.values);
            float sc = this.state.minimumScale * scale;
            mediaEditState.cropState.transformRotation = this.state.getOrientationOnly();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("set transformRotation = " + mediaEditState.cropState.transformRotation);
            }
            while (mediaEditState.cropState.transformRotation < 0) {
                mediaEditState.cropState.transformRotation += 360;
            }
            if (mediaEditState.cropState.transformRotation == 90 || mediaEditState.cropState.transformRotation == 270) {
                sw = (int) this.state.height;
                sh = (int) this.state.width;
            } else {
                sw = (int) this.state.width;
                sh = (int) this.state.height;
            }
            MediaController.CropState cropState = mediaEditState.cropState;
            double d = (double) width;
            double ceil = Math.ceil((double) (((float) sw) * sc));
            Double.isNaN(d);
            cropState.cropPw = (float) (d / ceil);
            MediaController.CropState cropState2 = mediaEditState.cropState;
            double d2 = (double) height;
            double ceil2 = Math.ceil((double) (((float) sh) * sc));
            Double.isNaN(d2);
            cropState2.cropPh = (float) (d2 / ceil2);
            if (mediaEditState.cropState.cropPw > 1.0f || mediaEditState.cropState.cropPh > 1.0f) {
                float max = Math.max(mediaEditState.cropState.cropPw, mediaEditState.cropState.cropPh);
                mediaEditState.cropState.cropPw /= max;
                mediaEditState.cropState.cropPh /= max;
            }
            mediaEditState.cropState.cropScale = this.state.scale * Math.min(((float) sw) / this.areaView.getCropWidth(), ((float) sh) / this.areaView.getCropHeight());
            mediaEditState.cropState.cropPx = (this.values[2] / ((float) sw)) / this.state.scale;
            mediaEditState.cropState.cropPy = (this.values[5] / ((float) sh)) / this.state.scale;
            mediaEditState.cropState.cropRotate = this.state.rotation;
            mediaEditState.cropState.stateScale = this.state.scale;
            mediaEditState.cropState.mirrored = this.state.mirrored;
            mediaEditState.cropState.scale = scale;
            mediaEditState.cropState.matrix = this.state.matrix;
            mediaEditState.cropState.width = width;
            mediaEditState.cropState.height = height;
            mediaEditState.cropState.freeform = this.freeform;
            mediaEditState.cropState.lockedAspectRatio = this.areaView.getLockAspectRatio();
            mediaEditState.cropState.initied = true;
        }
    }

    private void setLockedAspectRatio(float aspectRatio) {
        this.areaView.setLockedAspectRatio(aspectRatio);
        RectF targetRect = new RectF();
        this.areaView.calculateRect(targetRect, aspectRatio);
        fillAreaView(targetRect, true);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    public void showAspectRatioDialog() {
        if (this.state != null && !this.hasAspectRatioDialog) {
            this.hasAspectRatioDialog = true;
            String[] actions = new String[8];
            Integer[][] ratios = {new Integer[]{3, 2}, new Integer[]{5, 3}, new Integer[]{4, 3}, new Integer[]{5, 4}, new Integer[]{7, 5}, new Integer[]{16, 9}};
            actions[0] = LocaleController.getString("CropOriginal", NUM);
            actions[1] = LocaleController.getString("CropSquare", NUM);
            int i = 2;
            for (Integer[] ratioPair : ratios) {
                if (this.areaView.getAspectRatio() > 1.0f) {
                    actions[i] = String.format("%d:%d", new Object[]{ratioPair[0], ratioPair[1]});
                } else {
                    actions[i] = String.format("%d:%d", new Object[]{ratioPair[1], ratioPair[0]});
                }
                i++;
            }
            AlertDialog dialog = new AlertDialog.Builder(getContext()).setItems(actions, new CropView$$ExternalSyntheticLambda3(this, ratios)).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnCancelListener(new CropView$$ExternalSyntheticLambda2(this));
            dialog.show();
        }
    }

    /* renamed from: lambda$showAspectRatioDialog$2$org-telegram-ui-Components-Crop-CropView  reason: not valid java name */
    public /* synthetic */ void m2224xeff4eac6(Integer[][] ratios, DialogInterface dialog12, int which) {
        this.hasAspectRatioDialog = false;
        switch (which) {
            case 0:
                setLockedAspectRatio((this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getHeight() : this.state.getWidth()) / (this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getWidth() : this.state.getHeight()));
                return;
            case 1:
                setLockedAspectRatio(1.0f);
                return;
            default:
                Integer[] ratioPair = ratios[which - 2];
                if (this.areaView.getAspectRatio() > 1.0f) {
                    setLockedAspectRatio(((float) ratioPair[0].intValue()) / ((float) ratioPair[1].intValue()));
                    return;
                } else {
                    setLockedAspectRatio(((float) ratioPair[1].intValue()) / ((float) ratioPair[0].intValue()));
                    return;
                }
        }
    }

    /* renamed from: lambda$showAspectRatioDialog$3$org-telegram-ui-Components-Crop-CropView  reason: not valid java name */
    public /* synthetic */ void m2225xvar_a1fe5(DialogInterface dialog1) {
        this.hasAspectRatioDialog = false;
    }

    public void updateLayout() {
        CropState cropState;
        float w = this.areaView.getCropWidth();
        if (w != 0.0f && (cropState = this.state) != null) {
            this.areaView.calculateRect(this.initialAreaRect, cropState.getWidth() / this.state.getHeight());
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setActualRect(cropAreaView.getAspectRatio());
            this.areaView.getCropRect(this.previousAreaRect);
            this.state.scale(this.areaView.getCropWidth() / w, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public float getCropLeft() {
        return this.areaView.getCropLeft();
    }

    public float getCropTop() {
        return this.areaView.getCropTop();
    }

    public float getCropWidth() {
        return this.areaView.getCropWidth();
    }

    public float getCropHeight() {
        return this.areaView.getCropHeight();
    }

    public RectF getActualRect() {
        this.areaView.getCropRect(this.cropRect);
        return this.cropRect;
    }
}
