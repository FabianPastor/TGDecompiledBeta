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
import org.telegram.messenger.R;
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
    /* access modifiers changed from: private */
    public boolean animating;
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
    private RectF previousAreaRect;
    private float rotationStartScale;
    RectF sizeRect = new RectF(0.0f, 0.0f, 1280.0f, 1280.0f);
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

    public void onFling(float f, float f2, float f3, float f4) {
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public class CropState {
        public float baseRotation;
        public float height;
        public Matrix matrix;
        public float minimumScale;
        public boolean mirrored;
        public float orientation;
        public float rotation;
        public float scale;
        public float width;
        public float x;
        public float y;

        private CropState(int i, int i2, int i3) {
            this.width = (float) i;
            this.height = (float) i2;
            this.x = 0.0f;
            this.y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = (float) i3;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        /* access modifiers changed from: private */
        public void update(int i, int i2, int i3) {
            float f = (float) i;
            this.scale *= this.width / f;
            this.width = f;
            this.height = (float) i2;
            updateMinimumScale();
            this.matrix.getValues(CropView.this.values);
            this.matrix.reset();
            Matrix matrix2 = this.matrix;
            float f2 = this.scale;
            matrix2.postScale(f2, f2);
            Matrix matrix3 = this.matrix;
            float[] fArr = CropView.this.values;
            matrix3.postTranslate(fArr[2], fArr[5]);
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
        public void translate(float f, float f2) {
            this.x += f;
            this.y += f2;
            this.matrix.postTranslate(f, f2);
        }

        /* access modifiers changed from: private */
        public float getX() {
            return this.x;
        }

        /* access modifiers changed from: private */
        public float getY() {
            return this.y;
        }

        /* access modifiers changed from: private */
        public void scale(float f, float f2, float f3) {
            this.scale *= f;
            this.matrix.postScale(f, f, f2, f3);
        }

        /* access modifiers changed from: private */
        public float getScale() {
            return this.scale;
        }

        /* access modifiers changed from: private */
        public void rotate(float f, float f2, float f3) {
            this.rotation += f;
            this.matrix.postRotate(f, f2, f3);
        }

        /* access modifiers changed from: private */
        public float getRotation() {
            return this.rotation;
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
        public void reset(CropAreaView cropAreaView, float f, boolean z) {
            this.matrix.reset();
            this.x = 0.0f;
            this.y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = f;
            updateMinimumScale();
            float f2 = this.minimumScale;
            this.scale = f2;
            this.matrix.postScale(f2, f2);
        }

        private void updateMinimumScale() {
            float f = this.orientation;
            float f2 = this.baseRotation;
            float f3 = (f + f2) % 180.0f != 0.0f ? this.height : this.width;
            float f4 = (f + f2) % 180.0f != 0.0f ? this.width : this.height;
            if (CropView.this.freeform) {
                this.minimumScale = CropView.this.areaView.getCropWidth() / f3;
            } else {
                this.minimumScale = Math.max(CropView.this.areaView.getCropWidth() / f3, CropView.this.areaView.getCropHeight() / f4);
            }
        }

        /* access modifiers changed from: private */
        public void getConcatMatrix(Matrix matrix2) {
            matrix2.postConcat(this.matrix);
        }

        /* access modifiers changed from: private */
        public Matrix getMatrix() {
            Matrix matrix2 = new Matrix();
            matrix2.set(this.matrix);
            return matrix2;
        }
    }

    public float getStateOrientation() {
        CropState cropState = this.state;
        if (cropState == null) {
            return 0.0f;
        }
        return cropState.orientation;
    }

    public float getStateFullOrientation() {
        CropState cropState = this.state;
        return cropState.baseRotation + cropState.orientation;
    }

    public boolean getStateMirror() {
        return this.state.mirrored;
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

    public void setListener(CropViewListener cropViewListener) {
        this.listener = cropViewListener;
    }

    public void setBottomPadding(float f) {
        this.bottomPadding = f;
        this.areaView.setBottomPadding(f);
    }

    public void setAspectRatio(float f) {
        this.areaView.setActualRect(f);
    }

    public void setBitmap(Bitmap bitmap2, int i, boolean z, boolean z2, PaintingOverlay paintingOverlay, CropTransform cropTransform2, VideoEditTextureView videoEditTextureView2, final MediaController.CropState cropState) {
        this.freeform = z;
        this.videoEditTextureView = videoEditTextureView2;
        this.cropTransform = cropTransform2;
        this.bitmapRotation = i;
        this.bitmap = bitmap2;
        this.areaView.setIsVideo(videoEditTextureView2 != null);
        Bitmap bitmap3 = null;
        if (bitmap2 == null && videoEditTextureView2 == null) {
            this.state = null;
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        final int currentWidth = getCurrentWidth();
        final int currentHeight = getCurrentHeight();
        CropState cropState2 = this.state;
        if (cropState2 == null || !z2) {
            this.state = new CropState(currentWidth, currentHeight, 0);
            this.areaView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    int i;
                    int i2;
                    float f;
                    float f2;
                    CropView.this.reset();
                    MediaController.CropState cropState = cropState;
                    if (cropState != null) {
                        float f3 = cropState.lockedAspectRatio;
                        boolean z = true;
                        if (f3 > 1.0E-4f) {
                            CropView.this.areaView.setLockedAspectRatio(f3);
                            if (CropView.this.listener != null) {
                                CropView.this.listener.onAspectLock(true);
                            }
                        }
                        CropView.this.setFreeform(cropState.freeform);
                        float aspectRatio = CropView.this.areaView.getAspectRatio();
                        int i3 = cropState.transformRotation;
                        if (i3 == 90 || i3 == 270) {
                            aspectRatio = 1.0f / aspectRatio;
                            CropState cropState2 = CropView.this.state;
                            f = cropState2.height;
                            f2 = cropState2.width;
                            i2 = currentHeight;
                            i = currentWidth;
                        } else {
                            CropState cropState3 = CropView.this.state;
                            f = cropState3.width;
                            f2 = cropState3.height;
                            i2 = currentWidth;
                            i = currentHeight;
                        }
                        boolean access$000 = CropView.this.freeform;
                        if (!CropView.this.freeform || CropView.this.areaView.getLockAspectRatio() <= 0.0f) {
                            CropView cropView = CropView.this;
                            CropAreaView cropAreaView = cropView.areaView;
                            int access$300 = cropView.getCurrentWidth();
                            int access$400 = CropView.this.getCurrentHeight();
                            if ((((float) i3) + CropView.this.state.getBaseRotation()) % 180.0f == 0.0f) {
                                z = false;
                            }
                            cropAreaView.setBitmap(access$300, access$400, z, CropView.this.freeform);
                        } else {
                            CropAreaView cropAreaView2 = CropView.this.areaView;
                            cropAreaView2.setLockedAspectRatio(1.0f / cropAreaView2.getLockAspectRatio());
                            CropAreaView cropAreaView3 = CropView.this.areaView;
                            cropAreaView3.setActualRect(cropAreaView3.getLockAspectRatio());
                            access$000 = false;
                        }
                        CropView cropView2 = CropView.this;
                        cropView2.state.reset(cropView2.areaView, (float) i3, access$000);
                        CropAreaView cropAreaView4 = CropView.this.areaView;
                        MediaController.CropState cropState4 = cropState;
                        cropAreaView4.setActualRect((aspectRatio * cropState4.cropPw) / cropState4.cropPh);
                        CropState cropState5 = CropView.this.state;
                        MediaController.CropState cropState6 = cropState;
                        cropState5.mirrored = cropState6.mirrored;
                        cropState5.rotate(cropState6.cropRotate, 0.0f, 0.0f);
                        CropState cropState7 = CropView.this.state;
                        MediaController.CropState cropState8 = cropState;
                        float f4 = cropState8.cropPx * ((float) i2);
                        float f5 = cropState7.minimumScale;
                        cropState7.translate(f4 * f5, cropState8.cropPy * ((float) i) * f5);
                        float max = Math.max(CropView.this.areaView.getCropWidth() / f, CropView.this.areaView.getCropHeight() / f2);
                        CropState cropState9 = CropView.this.state;
                        cropState9.scale(cropState.cropScale * (max / cropState9.minimumScale), 0.0f, 0.0f);
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
            cropState2.update(currentWidth, currentHeight, i);
        }
        ImageView imageView2 = this.imageView;
        if (videoEditTextureView2 == null) {
            bitmap3 = this.bitmap;
        }
        imageView2.setImageBitmap(bitmap3);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true, false);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }

    public void setFreeform(boolean z) {
        this.areaView.setFreeform(z);
        this.freeform = z;
    }

    public void onShow() {
        this.isVisible = true;
    }

    public void onHide() {
        this.videoEditTextureView = null;
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
        reset(false);
    }

    public void reset(boolean z) {
        this.areaView.resetAnimator();
        this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), this.state.getBaseRotation() % 180.0f != 0.0f, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        this.state.reset(this.areaView, 0.0f, this.freeform);
        this.state.mirrored = false;
        this.areaView.getCropRect(this.initialAreaRect);
        updateMatrix(z);
        resetRotationStartScale();
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(true);
            this.listener.onAspectLock(false);
        }
    }

    public void updateMatrix() {
        updateMatrix(false);
    }

    public void updateMatrix(boolean z) {
        this.overlayMatrix.reset();
        if (this.state.getBaseRotation() == 90.0f || this.state.getBaseRotation() == 270.0f) {
            this.overlayMatrix.postTranslate((-this.state.getHeight()) / 2.0f, (-this.state.getWidth()) / 2.0f);
        } else {
            this.overlayMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        }
        this.overlayMatrix.postRotate((float) this.state.getOrientationOnly());
        this.state.getConcatMatrix(this.overlayMatrix);
        this.overlayMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        if (!this.freeform || this.isVisible || z) {
            updateCropTransform();
            this.listener.onUpdate();
        }
        invalidate();
    }

    private void fillAreaView(RectF rectF, boolean z) {
        final boolean z2;
        float f;
        int i = 0;
        float[] fArr = {1.0f};
        float max = Math.max(rectF.width() / this.areaView.getCropWidth(), rectF.height() / this.areaView.getCropHeight());
        if (this.state.getScale() * max > 30.0f) {
            f = 30.0f / this.state.getScale();
            z2 = true;
        } else {
            f = max;
            z2 = false;
        }
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            i = AndroidUtilities.statusBarHeight;
        }
        float f2 = (float) i;
        float access$1600 = this.state.getOrientedWidth() * ((rectF.centerX() - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth());
        float centerY = ((rectF.centerY() - (((((float) this.imageView.getHeight()) - this.bottomPadding) + f2) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new CropView$$ExternalSyntheticLambda1(this, f, fArr, access$1600, centerY));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (z2) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(rectF, ofFloat, true);
        this.initialAreaRect.set(rectF);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fillAreaView$0(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = (((f - 1.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue()) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * floatValue;
        this.state.scale(floatValue, f2, f3);
        updateMatrix();
    }

    private float fitScale(RectF rectF, float f, float f2) {
        float width = rectF.width() * f2;
        float height = rectF.height() * f2;
        float width2 = (rectF.width() - width) / 2.0f;
        float height2 = (rectF.height() - height) / 2.0f;
        float f3 = rectF.left;
        float f4 = rectF.top;
        rectF.set(f3 + width2, f4 + height2, f3 + width2 + width, f4 + height2 + height);
        return f * f2;
    }

    private void fitTranslation(RectF rectF, RectF rectF2, PointF pointF, float f) {
        float f2 = rectF2.left;
        float f3 = rectF2.top;
        float f4 = rectF2.right;
        float f5 = rectF2.bottom;
        float f6 = rectF.left;
        if (f6 > f2) {
            f4 += f6 - f2;
            f2 = f6;
        }
        float f7 = rectF.top;
        if (f7 > f3) {
            f5 += f7 - f3;
            f3 = f7;
        }
        float f8 = rectF.right;
        if (f8 < f4) {
            f2 += f8 - f4;
        }
        float f9 = rectF.bottom;
        if (f9 < f5) {
            f3 += f9 - f5;
        }
        float centerX = rectF2.centerX() - (f2 + (rectF2.width() / 2.0f));
        float centerY = rectF2.centerY() - (f3 + (rectF2.height() / 2.0f));
        double d = (double) f;
        Double.isNaN(d);
        double d2 = 1.5707963267948966d - d;
        double sin = Math.sin(d2);
        double d3 = (double) centerX;
        Double.isNaN(d3);
        double cos = Math.cos(d2);
        Double.isNaN(d3);
        float var_ = (float) (cos * d3);
        Double.isNaN(d);
        double d4 = d + 1.5707963267948966d;
        double cos2 = Math.cos(d4);
        double d5 = (double) centerY;
        Double.isNaN(d5);
        double sin2 = Math.sin(d4);
        Double.isNaN(d5);
        pointF.set(pointF.x + ((float) (sin * d3)) + ((float) (cos2 * d5)), pointF.y + var_ + ((float) (sin2 * d5)));
    }

    public RectF calculateBoundingBox(float f, float f2, float f3) {
        RectF rectF = new RectF(0.0f, 0.0f, f, f2);
        Matrix matrix = new Matrix();
        matrix.postRotate(f3, f / 2.0f, f2 / 2.0f);
        matrix.mapRect(rectF);
        return rectF;
    }

    public float scaleWidthToMaxSize(RectF rectF, RectF rectF2) {
        float width = rectF2.width();
        return ((float) Math.floor((double) ((rectF.height() * width) / rectF.width()))) > rectF2.height() ? (float) Math.floor((double) ((rectF2.height() * rectF.width()) / rectF.height())) : width;
    }

    private static class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        /* access modifiers changed from: package-private */
        public void setRect(RectF rectF) {
            float[] fArr = this.coords;
            float f = rectF.left;
            fArr[0] = f;
            float f2 = rectF.top;
            fArr[1] = f2;
            float f3 = rectF.right;
            fArr[2] = f3;
            fArr[3] = f2;
            fArr[4] = f3;
            float f4 = rectF.bottom;
            fArr[5] = f4;
            fArr[6] = f;
            fArr[7] = f4;
        }

        /* access modifiers changed from: package-private */
        public void applyMatrix(Matrix matrix) {
            matrix.mapPoints(this.coords);
        }

        /* access modifiers changed from: package-private */
        public void getRect(RectF rectF) {
            float[] fArr = this.coords;
            rectF.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean z, boolean z2, boolean z3) {
        fitContentInBounds(z, z2, z3, false);
    }

    /* access modifiers changed from: private */
    public void fitContentInBounds(boolean z, boolean z2, boolean z3, boolean z4) {
        float f;
        if (this.state != null) {
            float cropWidth = this.areaView.getCropWidth();
            float cropHeight = this.areaView.getCropHeight();
            float access$1600 = this.state.getOrientedWidth();
            float access$1700 = this.state.getOrientedHeight();
            float access$1900 = this.state.getRotation();
            float radians = (float) Math.toRadians((double) access$1900);
            RectF calculateBoundingBox = calculateBoundingBox(cropWidth, cropHeight, access$1900);
            RectF rectF = new RectF(0.0f, 0.0f, access$1600, access$1700);
            float access$1500 = this.state.getScale();
            this.tempRect.setRect(rectF);
            Matrix access$2000 = this.state.getMatrix();
            access$2000.preTranslate(((cropWidth - access$1600) / 2.0f) / access$1500, ((cropHeight - access$1700) / 2.0f) / access$1500);
            this.tempMatrix.reset();
            this.tempMatrix.setTranslate(rectF.centerX(), rectF.centerY());
            Matrix matrix = this.tempMatrix;
            matrix.setConcat(matrix, access$2000);
            this.tempMatrix.preTranslate(-rectF.centerX(), -rectF.centerY());
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempMatrix.reset();
            this.tempMatrix.preRotate(-access$1900, access$1600 / 2.0f, access$1700 / 2.0f);
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempRect.getRect(rectF);
            PointF pointF = new PointF(this.state.getX(), this.state.getY());
            if (!rectF.contains(calculateBoundingBox)) {
                f = (!z || (calculateBoundingBox.width() <= rectF.width() && calculateBoundingBox.height() <= rectF.height())) ? access$1500 : fitScale(rectF, access$1500, calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF));
                fitTranslation(rectF, calculateBoundingBox, pointF, radians);
            } else if (!z2 || this.rotationStartScale <= 0.0f) {
                f = access$1500;
            } else {
                float width = calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF);
                if (this.state.getScale() * width < this.rotationStartScale) {
                    width = 1.0f;
                }
                f = fitScale(rectF, access$1500, width);
                fitTranslation(rectF, calculateBoundingBox, pointF, radians);
            }
            float access$2100 = pointF.x - this.state.getX();
            float access$2200 = pointF.y - this.state.getY();
            if (z3) {
                float f2 = f / access$1500;
                if (Math.abs(f2 - 1.0f) >= 1.0E-5f || Math.abs(access$2100) >= 1.0E-5f || Math.abs(access$2200) >= 1.0E-5f) {
                    this.animating = true;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new CropView$$ExternalSyntheticLambda0(this, access$2100, new float[]{1.0f, 0.0f, 0.0f}, access$2200, f2));
                    final boolean z5 = z4;
                    final boolean z6 = z;
                    final boolean z7 = z2;
                    final boolean z8 = z3;
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            boolean unused = CropView.this.animating = false;
                            if (!z5) {
                                CropView.this.fitContentInBounds(z6, z7, z8, true);
                            }
                        }
                    });
                    ofFloat.setInterpolator(this.areaView.getInterpolator());
                    ofFloat.setDuration(z4 ? 100 : 200);
                    ofFloat.start();
                    return;
                }
                return;
            }
            this.state.translate(access$2100, access$2200);
            this.state.scale(f / access$1500, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fitContentInBounds$1(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f4 = (f * floatValue) - fArr[1];
        fArr[1] = fArr[1] + f4;
        float f5 = (f2 * floatValue) - fArr[2];
        fArr[2] = fArr[2] + f5;
        this.state.translate(f4 * fArr[0], f5 * fArr[0]);
        float f6 = (((f3 - 1.0f) * floatValue) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * f6;
        this.state.scale(f6, 0.0f, 0.0f);
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

    public boolean isMirrored() {
        CropState cropState = this.state;
        if (cropState == null) {
            return false;
        }
        return cropState.mirrored;
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
            float access$2600 = (this.state.getOrientation() - this.state.getBaseRotation()) % 360.0f;
            CropViewListener cropViewListener = this.listener;
            if (!this.state.hasChanges() && access$2600 == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored) {
                z = true;
            }
            cropViewListener.onChange(z);
        }
        return this.state.mirrored;
    }

    public void maximize(boolean z) {
        int i;
        float f;
        float f2 = this.state.minimumScale;
        this.areaView.resetAnimator();
        if (this.state.getOrientation() % 180.0f != 0.0f) {
            f = (float) getCurrentHeight();
            i = getCurrentWidth();
        } else {
            f = (float) getCurrentWidth();
            i = getCurrentHeight();
        }
        float f3 = f / ((float) i);
        float f4 = 1.0f;
        if (!this.freeform) {
            f3 = 1.0f;
        }
        this.areaView.calculateRect(this.initialAreaRect, f3);
        CropAreaView cropAreaView = this.areaView;
        if (this.freeform) {
            f4 = 0.0f;
        }
        cropAreaView.setLockedAspectRatio(f4);
        resetRotationStartScale();
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            RectF rectF = new RectF();
            RectF rectF2 = new RectF();
            this.areaView.getCropRect(rectF);
            CropState cropState = this.state;
            ofFloat.addUpdateListener(new CropView$$ExternalSyntheticLambda2(this, rectF, rectF2, cropState.x, cropState.y, cropState.rotation, cropState.scale, f2));
            ofFloat.setInterpolator(this.areaView.getInterpolator());
            ofFloat.setDuration(250);
            ofFloat.start();
            return;
        }
        this.areaView.setActualRect(this.initialAreaRect);
        CropState cropState2 = this.state;
        cropState2.translate(-cropState2.x, -cropState2.y);
        CropState cropState3 = this.state;
        cropState3.scale(cropState3.minimumScale / cropState3.scale, 0.0f, 0.0f);
        CropState cropState4 = this.state;
        cropState4.rotate(-cropState4.rotation, 0.0f, 0.0f);
        updateMatrix();
        resetRotationStartScale();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$maximize$2(RectF rectF, RectF rectF2, float f, float f2, float f3, float f4, float f5, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        AndroidUtilities.lerp(rectF, this.initialAreaRect, floatValue, rectF2);
        this.areaView.setActualRect(rectF2);
        CropState cropState = this.state;
        float f6 = 1.0f - floatValue;
        float f7 = cropState.x - (f * f6);
        float f8 = cropState.y - (f2 * f6);
        float f9 = cropState.rotation - (f3 * f6);
        float lerp = AndroidUtilities.lerp(f4, f5, floatValue);
        CropState cropState2 = this.state;
        cropState2.translate(-f7, -f8);
        this.state.scale(lerp / cropState2.scale, 0.0f, 0.0f);
        this.state.rotate(-f9, 0.0f, 0.0f);
        fitContentInBounds(true, false, false);
    }

    public boolean rotate(float f) {
        if (this.state == null) {
            return false;
        }
        this.areaView.resetAnimator();
        resetRotationStartScale();
        float access$2600 = ((this.state.getOrientation() - this.state.getBaseRotation()) + f) % 360.0f;
        boolean z = this.freeform;
        if (!z || this.areaView.getLockAspectRatio() <= 0.0f) {
            this.areaView.setBitmap(getCurrentWidth(), getCurrentHeight(), (this.state.getBaseRotation() + access$2600) % 180.0f != 0.0f, this.freeform);
        } else {
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
            CropAreaView cropAreaView2 = this.areaView;
            cropAreaView2.setActualRect(cropAreaView2.getLockAspectRatio());
            z = false;
        }
        this.state.reset(this.areaView, access$2600, z);
        updateMatrix();
        fitContentInBounds(true, false, false);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(access$2600 == 0.0f && this.areaView.getLockAspectRatio() == 0.0f && !this.state.mirrored);
        }
        if (this.state.getOrientationOnly() != 0) {
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.animating || this.areaView.onTouchEvent(motionEvent)) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            onScrollChangeBegan();
        } else if (action == 1 || action == 3) {
            onScrollChangeEnded();
        }
        try {
            return this.detector.onTouchEvent(motionEvent);
        } catch (Exception unused) {
            return false;
        }
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
        float centerX = this.previousAreaRect.centerX() - this.areaView.getCropCenterX();
        float centerY = this.previousAreaRect.centerY() - this.areaView.getCropCenterY();
        CropState cropState = this.state;
        if (cropState != null) {
            cropState.translate(centerX, centerY);
        }
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    public void onAreaChangeEnded() {
        this.areaView.setGridType(CropAreaView.GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    public void onDrag(float f, float f2) {
        if (!this.animating) {
            this.state.translate(f, f2);
            updateMatrix();
        }
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

    public void onScale(float f, float f2, float f3) {
        if (!this.animating) {
            if (this.state.getScale() * f > 30.0f) {
                f = 30.0f / this.state.getScale();
            }
            this.state.scale(f, ((f2 - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((f3 - (((((float) this.imageView.getHeight()) - this.bottomPadding) - ((float) ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight))) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
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

    public void setRotation(float f) {
        this.state.rotate(f - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public static void editBitmap(Context context, String str, Bitmap bitmap2, Canvas canvas, Bitmap bitmap3, Bitmap.CompressFormat compressFormat, Matrix matrix, int i, int i2, float f, float f2, float f3, float f4, boolean z, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z2) {
        Bitmap bitmap4 = bitmap3;
        float f5 = f3;
        float f6 = f4;
        ArrayList<VideoEditedInfo.MediaEntity> arrayList2 = arrayList;
        char c = 0;
        if (z2) {
            try {
                bitmap4.eraseColor(0);
            } catch (Throwable th) {
                FileLog.e(th);
                return;
            }
        }
        Bitmap decodeFile = bitmap2 == null ? BitmapFactory.decodeFile(str) : bitmap2;
        float max = ((float) Math.max(decodeFile.getWidth(), decodeFile.getHeight())) / ((float) Math.max(i, i2));
        Matrix matrix2 = new Matrix();
        int i3 = 2;
        matrix2.postTranslate((float) ((-decodeFile.getWidth()) / 2), (float) ((-decodeFile.getHeight()) / 2));
        if (z) {
            matrix2.postScale(-1.0f, 1.0f);
        }
        float f7 = 1.0f / max;
        matrix2.postScale(f7, f7);
        matrix2.postRotate(f5);
        matrix2.postConcat(matrix);
        matrix2.postScale(f6, f6);
        matrix2.postTranslate((float) (bitmap3.getWidth() / 2), (float) (bitmap3.getHeight() / 2));
        canvas.drawBitmap(decodeFile, matrix2, new Paint(2));
        FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
        bitmap4.compress(compressFormat, 87, fileOutputStream);
        fileOutputStream.close();
        if (arrayList2 != null && !arrayList.isEmpty()) {
            float[] fArr = new float[4];
            float width = f7 * f6 * f * (((float) decodeFile.getWidth()) / ((float) bitmap3.getWidth()));
            TextPaintView textPaintView = null;
            int size = arrayList.size();
            int i4 = 0;
            while (i4 < size) {
                VideoEditedInfo.MediaEntity mediaEntity = arrayList2.get(i4);
                fArr[c] = (mediaEntity.x * ((float) decodeFile.getWidth())) + ((((float) mediaEntity.viewWidth) * mediaEntity.scale) / 2.0f);
                fArr[1] = (mediaEntity.y * ((float) decodeFile.getHeight())) + ((((float) mediaEntity.viewHeight) * mediaEntity.scale) / 2.0f);
                fArr[i3] = mediaEntity.textViewX * ((float) decodeFile.getWidth());
                fArr[3] = mediaEntity.textViewY * ((float) decodeFile.getHeight());
                matrix2.mapPoints(fArr);
                byte b = mediaEntity.type;
                if (b == 0) {
                    int width2 = bitmap3.getWidth() / i3;
                    mediaEntity.viewHeight = width2;
                    mediaEntity.viewWidth = width2;
                } else if (b == 1) {
                    mediaEntity.fontSize = bitmap3.getWidth() / 9;
                    if (textPaintView == null) {
                        textPaintView = new TextPaintView(context, new Point(0.0f, 0.0f), mediaEntity.fontSize, "", new Swatch(-16777216, 0.85f, 0.1f), 0);
                        textPaintView.setMaxWidth(bitmap3.getWidth() - 20);
                    }
                    byte b2 = mediaEntity.subType;
                    textPaintView.setType((b2 & 1) != 0 ? 0 : (b2 & 4) != 0 ? 2 : 1);
                    textPaintView.setText(mediaEntity.text);
                    textPaintView.measure(View.MeasureSpec.makeMeasureSpec(bitmap3.getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(bitmap3.getHeight(), Integer.MIN_VALUE));
                    mediaEntity.viewWidth = textPaintView.getMeasuredWidth();
                    mediaEntity.viewHeight = textPaintView.getMeasuredHeight();
                }
                float f8 = mediaEntity.scale * width;
                mediaEntity.scale = f8;
                c = 0;
                mediaEntity.x = (fArr[0] - ((((float) mediaEntity.viewWidth) * f8) / 2.0f)) / ((float) bitmap3.getWidth());
                mediaEntity.y = (fArr[1] - ((((float) mediaEntity.viewHeight) * mediaEntity.scale) / 2.0f)) / ((float) bitmap3.getHeight());
                mediaEntity.textViewX = fArr[2] / ((float) bitmap3.getWidth());
                mediaEntity.textViewY = fArr[3] / ((float) bitmap3.getHeight());
                mediaEntity.width = (((float) mediaEntity.viewWidth) * mediaEntity.scale) / ((float) bitmap3.getWidth());
                mediaEntity.height = (((float) mediaEntity.viewHeight) * mediaEntity.scale) / ((float) bitmap3.getHeight());
                mediaEntity.textViewWidth = ((float) mediaEntity.viewWidth) / ((float) bitmap3.getWidth());
                mediaEntity.textViewHeight = ((float) mediaEntity.viewHeight) / ((float) bitmap3.getHeight());
                double d = (double) mediaEntity.rotation;
                double d2 = (double) (f2 + f5);
                Double.isNaN(d2);
                Double.isNaN(d);
                mediaEntity.rotation = (float) (d - (d2 * 0.017453292519943295d));
                i4++;
                Bitmap bitmap5 = bitmap3;
                f5 = f3;
                i3 = 2;
            }
        }
        decodeFile.recycle();
    }

    private void updateCropTransform() {
        int i;
        float f;
        float f2;
        if (this.cropTransform != null && this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            int ceil = (int) Math.ceil((double) scaleWidthToMaxSize(this.cropRect, this.sizeRect));
            float f3 = (float) ceil;
            int ceil2 = (int) Math.ceil((double) (f3 / this.areaView.getAspectRatio()));
            float cropWidth = f3 / this.areaView.getCropWidth();
            this.state.matrix.getValues(this.values);
            CropState cropState = this.state;
            float f4 = cropState.minimumScale * cropWidth;
            int access$1300 = cropState.getOrientationOnly();
            while (access$1300 < 0) {
                access$1300 += 360;
            }
            if (access$1300 == 90 || access$1300 == 270) {
                CropState cropState2 = this.state;
                i = (int) cropState2.height;
                f = cropState2.width;
            } else {
                CropState cropState3 = this.state;
                i = (int) cropState3.width;
                f = cropState3.height;
            }
            double d = (double) ceil;
            float f5 = (float) i;
            double ceil3 = Math.ceil((double) (f5 * f4));
            Double.isNaN(d);
            float f6 = (float) (d / ceil3);
            double d2 = (double) ceil2;
            float f7 = (float) ((int) f);
            double ceil4 = Math.ceil((double) (f4 * f7));
            Double.isNaN(d2);
            float f8 = (float) (d2 / ceil4);
            if (f6 > 1.0f || f8 > 1.0f) {
                float max = Math.max(f6, f8);
                f6 /= max;
                f8 /= max;
            }
            float f9 = f8;
            float var_ = f6;
            RectF targetRectToFill = this.areaView.getTargetRectToFill(f5 / f7);
            if (this.freeform) {
                f2 = targetRectToFill.width() / f5;
            } else {
                f2 = Math.max(targetRectToFill.width() / f5, targetRectToFill.height() / f7);
            }
            CropState cropState4 = this.state;
            float var_ = cropState4.scale;
            float var_ = var_ / f2;
            float var_ = var_ / cropState4.minimumScale;
            float[] fArr = this.values;
            float var_ = (fArr[2] / f5) / var_;
            float var_ = (fArr[5] / f7) / var_;
            float var_ = cropState4.rotation;
            RectF targetRectToFill2 = this.areaView.getTargetRectToFill();
            float cropCenterX = this.areaView.getCropCenterX() - targetRectToFill2.centerX();
            float cropCenterY = this.areaView.getCropCenterY() - targetRectToFill2.centerY();
            CropTransform cropTransform2 = this.cropTransform;
            CropState cropState5 = this.state;
            boolean z = cropState5.mirrored || cropState5.hasChanges() || this.state.getBaseRotation() >= 1.0E-5f;
            int access$13002 = this.state.getOrientationOnly();
            CropState cropState6 = this.state;
            cropTransform2.setViewTransform(z, var_, var_, var_, access$13002, var_, var_, cropState6.minimumScale / f2, var_, f9, cropCenterX, cropCenterY, cropState6.mirrored);
        }
    }

    public static String getCopy(String str) {
        File directory = FileLoader.getDirectory(4);
        File file = new File(directory, SharedConfig.getLastLocalId() + "_temp.jpg");
        try {
            AndroidUtilities.copyFile(new File(str), file);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return file.getAbsolutePath();
    }

    public void makeCrop(MediaController.MediaEditState mediaEditState) {
        MediaController.CropState cropState;
        int i;
        int i2;
        float f;
        MediaController.MediaEditState mediaEditState2 = mediaEditState;
        if (this.state != null) {
            this.areaView.getCropRect(this.cropRect);
            int ceil = (int) Math.ceil((double) scaleWidthToMaxSize(this.cropRect, this.sizeRect));
            float f2 = (float) ceil;
            int ceil2 = (int) Math.ceil((double) (f2 / this.areaView.getAspectRatio()));
            float cropWidth = f2 / this.areaView.getCropWidth();
            if (mediaEditState2.paintPath != null) {
                Bitmap createBitmap = Bitmap.createBitmap(ceil, ceil2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                String copy = getCopy(mediaEditState2.paintPath);
                if (mediaEditState2.croppedPaintPath != null) {
                    new File(mediaEditState2.croppedPaintPath).delete();
                    mediaEditState2.croppedPaintPath = null;
                }
                mediaEditState2.croppedPaintPath = copy;
                ArrayList<VideoEditedInfo.MediaEntity> arrayList = mediaEditState2.mediaEntities;
                if (arrayList == null || arrayList.isEmpty()) {
                    mediaEditState2.croppedMediaEntities = null;
                } else {
                    mediaEditState2.croppedMediaEntities = new ArrayList<>(mediaEditState2.mediaEntities.size());
                    int size = mediaEditState2.mediaEntities.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        mediaEditState2.croppedMediaEntities.add(mediaEditState2.mediaEntities.get(i3).copy());
                    }
                }
                Context context = getContext();
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
                Matrix matrix = this.state.matrix;
                int currentWidth = getCurrentWidth();
                int currentHeight = getCurrentHeight();
                CropState cropState2 = this.state;
                editBitmap(context, copy, (Bitmap) null, canvas, createBitmap, compressFormat, matrix, currentWidth, currentHeight, cropState2.scale, cropState2.rotation, (float) cropState2.getOrientationOnly(), cropWidth, false, mediaEditState2.croppedMediaEntities, false);
            }
            if (mediaEditState2.cropState == null) {
                mediaEditState2.cropState = new MediaController.CropState();
            }
            this.state.matrix.getValues(this.values);
            CropState cropState3 = this.state;
            float f3 = cropState3.minimumScale * cropWidth;
            mediaEditState2.cropState.transformRotation = cropState3.getOrientationOnly();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("set transformRotation = " + mediaEditState2.cropState.transformRotation);
            }
            while (true) {
                cropState = mediaEditState2.cropState;
                i = cropState.transformRotation;
                if (i >= 0) {
                    break;
                }
                cropState.transformRotation = i + 360;
            }
            if (i == 90 || i == 270) {
                CropState cropState4 = this.state;
                i2 = (int) cropState4.height;
                f = cropState4.width;
            } else {
                CropState cropState5 = this.state;
                i2 = (int) cropState5.width;
                f = cropState5.height;
            }
            double d = (double) ceil;
            float f4 = (float) i2;
            double ceil3 = Math.ceil((double) (f4 * f3));
            Double.isNaN(d);
            cropState.cropPw = (float) (d / ceil3);
            MediaController.CropState cropState6 = mediaEditState2.cropState;
            double d2 = (double) ceil2;
            float f5 = (float) ((int) f);
            double ceil4 = Math.ceil((double) (f3 * f5));
            Double.isNaN(d2);
            cropState6.cropPh = (float) (d2 / ceil4);
            MediaController.CropState cropState7 = mediaEditState2.cropState;
            float f6 = cropState7.cropPw;
            if (f6 > 1.0f || cropState7.cropPh > 1.0f) {
                float max = Math.max(f6, cropState7.cropPh);
                MediaController.CropState cropState8 = mediaEditState2.cropState;
                cropState8.cropPw /= max;
                cropState8.cropPh /= max;
            }
            mediaEditState2.cropState.cropScale = this.state.scale * Math.min(f4 / this.areaView.getCropWidth(), f5 / this.areaView.getCropHeight());
            MediaController.CropState cropState9 = mediaEditState2.cropState;
            float[] fArr = this.values;
            float f7 = fArr[2] / f4;
            CropState cropState10 = this.state;
            float f8 = cropState10.scale;
            cropState9.cropPx = f7 / f8;
            cropState9.cropPy = (fArr[5] / f5) / f8;
            cropState9.cropRotate = cropState10.rotation;
            cropState9.stateScale = f8;
            cropState9.mirrored = cropState10.mirrored;
            cropState9.scale = cropWidth;
            cropState9.matrix = cropState10.matrix;
            cropState9.width = ceil;
            cropState9.height = ceil2;
            cropState9.freeform = this.freeform;
            cropState9.lockedAspectRatio = this.areaView.getLockAspectRatio();
            mediaEditState2.cropState.initied = true;
        }
    }

    private void setLockedAspectRatio(float f) {
        this.areaView.setLockedAspectRatio(f);
        RectF rectF = new RectF();
        this.areaView.calculateRect(rectF, f);
        fillAreaView(rectF, true);
        CropViewListener cropViewListener = this.listener;
        if (cropViewListener != null) {
            cropViewListener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    public void showAspectRatioDialog() {
        if (this.state != null && !this.hasAspectRatioDialog) {
            this.hasAspectRatioDialog = true;
            String[] strArr = new String[8];
            Integer[][] numArr = {new Integer[]{3, 2}, new Integer[]{5, 3}, new Integer[]{4, 3}, new Integer[]{5, 4}, new Integer[]{7, 5}, new Integer[]{16, 9}};
            strArr[0] = LocaleController.getString("CropOriginal", R.string.CropOriginal);
            strArr[1] = LocaleController.getString("CropSquare", R.string.CropSquare);
            int i = 2;
            for (int i2 = 0; i2 < 6; i2++) {
                Integer[] numArr2 = numArr[i2];
                if (this.areaView.getAspectRatio() > 1.0f) {
                    strArr[i] = String.format("%d:%d", new Object[]{numArr2[0], numArr2[1]});
                } else {
                    strArr[i] = String.format("%d:%d", new Object[]{numArr2[1], numArr2[0]});
                }
                i++;
            }
            AlertDialog create = new AlertDialog.Builder(getContext()).setItems(strArr, new CropView$$ExternalSyntheticLambda4(this, numArr)).create();
            create.setCanceledOnTouchOutside(true);
            create.setOnCancelListener(new CropView$$ExternalSyntheticLambda3(this));
            create.show();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showAspectRatioDialog$3(Integer[][] numArr, DialogInterface dialogInterface, int i) {
        this.hasAspectRatioDialog = false;
        if (i == 0) {
            setLockedAspectRatio((this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getHeight() : this.state.getWidth()) / (this.state.getBaseRotation() % 180.0f != 0.0f ? this.state.getWidth() : this.state.getHeight()));
        } else if (i != 1) {
            Integer[] numArr2 = numArr[i - 2];
            if (this.areaView.getAspectRatio() > 1.0f) {
                setLockedAspectRatio(((float) numArr2[0].intValue()) / ((float) numArr2[1].intValue()));
            } else {
                setLockedAspectRatio(((float) numArr2[1].intValue()) / ((float) numArr2[0].intValue()));
            }
        } else {
            setLockedAspectRatio(1.0f);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showAspectRatioDialog$4(DialogInterface dialogInterface) {
        this.hasAspectRatioDialog = false;
    }

    public void updateLayout() {
        CropState cropState;
        float cropWidth = this.areaView.getCropWidth();
        if (cropWidth != 0.0f && (cropState = this.state) != null) {
            this.areaView.calculateRect(this.initialAreaRect, cropState.getWidth() / this.state.getHeight());
            CropAreaView cropAreaView = this.areaView;
            cropAreaView.setActualRect(cropAreaView.getAspectRatio());
            this.areaView.getCropRect(this.previousAreaRect);
            this.state.scale(this.areaView.getCropWidth() / cropWidth, 0.0f, 0.0f);
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
