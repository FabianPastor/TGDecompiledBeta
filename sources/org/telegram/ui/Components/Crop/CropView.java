package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.Components.Crop.CropGestureDetector.CropGestureListener;

public class CropView extends FrameLayout implements AreaViewListener, CropGestureListener {
    private static final float EPSILON = 1.0E-5f;
    private static final float MAX_SCALE = 30.0f;
    private static final int RESULT_SIDE = 1280;
    private boolean animating = false;
    private CropAreaView areaView;
    private View backView;
    private Bitmap bitmap;
    private float bottomPadding;
    private CropGestureDetector detector;
    private boolean freeform;
    private boolean hasAspectRatioDialog;
    private ImageView imageView;
    private RectF initialAreaRect = new RectF();
    private CropViewListener listener;
    private Matrix presentationMatrix = new Matrix();
    private RectF previousAreaRect = new RectF();
    private float rotationStartScale;
    private CropState state;
    private Matrix tempMatrix = new Matrix();
    private CropRectangle tempRect = new CropRectangle();

    private class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        /* Access modifiers changed, original: 0000 */
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

        /* Access modifiers changed, original: 0000 */
        public void applyMatrix(Matrix matrix) {
            matrix.mapPoints(this.coords);
        }

        /* Access modifiers changed, original: 0000 */
        public void getRect(RectF rectF) {
            float[] fArr = this.coords;
            rectF.set(fArr[0], fArr[1], fArr[2], fArr[7]);
        }
    }

    private class CropState {
        private float baseRotation;
        private float height;
        private Matrix matrix;
        private float minimumScale;
        private float orientation;
        private float rotation;
        private float scale;
        private float width;
        private float x;
        private float y;

        /* synthetic */ CropState(CropView cropView, Bitmap bitmap, int i, AnonymousClass1 anonymousClass1) {
            this(bitmap, i);
        }

        private CropState(Bitmap bitmap, int i) {
            this.width = (float) bitmap.getWidth();
            this.height = (float) bitmap.getHeight();
            this.x = 0.0f;
            this.y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = (float) i;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        private void updateBitmap(Bitmap bitmap, int i) {
            this.scale *= this.width / ((float) bitmap.getWidth());
            this.width = (float) bitmap.getWidth();
            this.height = (float) bitmap.getHeight();
            updateMinimumScale();
            float[] fArr = new float[9];
            this.matrix.getValues(fArr);
            this.matrix.reset();
            Matrix matrix = this.matrix;
            float f = this.scale;
            matrix.postScale(f, f);
            this.matrix.postTranslate(fArr[2], fArr[5]);
            CropView.this.updateMatrix();
        }

        private boolean hasChanges() {
            return Math.abs(this.x) > 1.0E-5f || Math.abs(this.y) > 1.0E-5f || Math.abs(this.scale - this.minimumScale) > 1.0E-5f || Math.abs(this.rotation) > 1.0E-5f || Math.abs(this.orientation) > 1.0E-5f;
        }

        private float getWidth() {
            return this.width;
        }

        private float getHeight() {
            return this.height;
        }

        private float getOrientedWidth() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
        }

        private float getOrientedHeight() {
            return (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
        }

        private void translate(float f, float f2) {
            this.x += f;
            this.y += f2;
            this.matrix.postTranslate(f, f2);
        }

        private float getX() {
            return this.x;
        }

        private float getY() {
            return this.y;
        }

        private void scale(float f, float f2, float f3) {
            this.scale *= f;
            this.matrix.postScale(f, f, f2, f3);
        }

        private float getScale() {
            return this.scale;
        }

        private float getMinimumScale() {
            return this.minimumScale;
        }

        private void rotate(float f, float f2, float f3) {
            this.rotation += f;
            this.matrix.postRotate(f, f2, f3);
        }

        private float getRotation() {
            return this.rotation;
        }

        private float getOrientation() {
            return this.orientation + this.baseRotation;
        }

        private float getBaseRotation() {
            return this.baseRotation;
        }

        private void reset(CropAreaView cropAreaView, float f, boolean z) {
            this.matrix.reset();
            this.x = 0.0f;
            this.y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = f;
            updateMinimumScale();
            this.scale = this.minimumScale;
            Matrix matrix = this.matrix;
            f = this.scale;
            matrix.postScale(f, f);
        }

        private void updateMinimumScale() {
            float f = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
            float f2 = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
            if (CropView.this.freeform) {
                this.minimumScale = CropView.this.areaView.getCropWidth() / f;
            } else {
                this.minimumScale = Math.max(CropView.this.areaView.getCropWidth() / f, CropView.this.areaView.getCropHeight() / f2);
            }
        }

        private void getConcatMatrix(Matrix matrix) {
            matrix.postConcat(this.matrix);
        }

        private Matrix getMatrix() {
            Matrix matrix = new Matrix();
            matrix.set(this.matrix);
            return matrix;
        }
    }

    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);
    }

    public void onFling(float f, float f2, float f3, float f4) {
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    public CropView(Context context) {
        super(context);
        this.backView = new View(context);
        this.backView.setBackgroundColor(-16777216);
        this.backView.setVisibility(4);
        addView(this.backView);
        this.imageView = new ImageView(context);
        this.imageView.setDrawingCacheEnabled(true);
        this.imageView.setScaleType(ScaleType.MATRIX);
        addView(this.imageView);
        this.detector = new CropGestureDetector(context);
        this.detector.setOnGestureListener(this);
        this.areaView = new CropAreaView(context);
        this.areaView.setListener(this);
        addView(this.areaView);
    }

    public boolean isReady() {
        return (this.detector.isScaling() || this.detector.isDragging() || this.areaView.isDragging()) ? false : true;
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

    public void setBitmap(Bitmap bitmap, int i, boolean z, boolean z2) {
        this.freeform = z;
        if (bitmap == null) {
            this.bitmap = null;
            this.state = null;
            this.imageView.setImageDrawable(null);
            return;
        }
        this.bitmap = bitmap;
        CropState cropState = this.state;
        if (cropState == null || !z2) {
            this.state = new CropState(this, this.bitmap, i, null);
            this.imageView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    CropView.this.reset();
                    CropView.this.imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        } else {
            cropState.updateBitmap(this.bitmap, i);
        }
        this.imageView.setImageBitmap(this.bitmap);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
    }

    public void hideBackView() {
        this.backView.setVisibility(4);
    }

    public void showBackView() {
        this.backView.setVisibility(0);
    }

    public void setFreeform(boolean z) {
        this.areaView.setFreeform(z);
        this.freeform = z;
    }

    public void show() {
        this.backView.setVisibility(0);
        this.imageView.setVisibility(0);
        this.areaView.setDimVisibility(true);
        this.areaView.setFrameVisibility(true);
        this.areaView.invalidate();
    }

    public void hide() {
        this.backView.setVisibility(4);
        this.imageView.setVisibility(4);
        this.areaView.setDimVisibility(false);
        this.areaView.setFrameVisibility(false);
        this.areaView.invalidate();
    }

    public void reset() {
        this.areaView.resetAnimator();
        this.areaView.setBitmap(this.bitmap, this.state.getBaseRotation() % 180.0f != 0.0f, this.freeform);
        this.areaView.setLockedAspectRatio(this.freeform ? 0.0f : 1.0f);
        this.state.reset(this.areaView, 0.0f, this.freeform);
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
        this.presentationMatrix.reset();
        this.presentationMatrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        this.presentationMatrix.postRotate(this.state.getOrientation());
        this.state.getConcatMatrix(this.presentationMatrix);
        this.presentationMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
        this.imageView.setImageMatrix(this.presentationMatrix);
    }

    private void fillAreaView(RectF rectF, boolean z) {
        float access$1100;
        boolean z2;
        float[] fArr = new float[1];
        int i = 0;
        fArr[0] = 1.0f;
        float max = Math.max(rectF.width() / this.areaView.getCropWidth(), rectF.height() / this.areaView.getCropHeight());
        if (this.state.getScale() * max > 30.0f) {
            access$1100 = 30.0f / this.state.getScale();
            z2 = true;
        } else {
            access$1100 = max;
            z2 = false;
        }
        if (VERSION.SDK_INT >= 21) {
            i = AndroidUtilities.statusBarHeight;
        }
        float access$1200 = this.state.getOrientedWidth() * ((rectF.centerX() - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth());
        float centerY = ((rectF.centerY() - (((((float) this.imageView.getHeight()) - this.bottomPadding) + ((float) i)) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new -$$Lambda$CropView$u7JJSQis3TQtsCeT54hdvCdMU-Y(this, access$1100, fArr, access$1200, centerY));
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

    public /* synthetic */ void lambda$fillAreaView$0$CropView(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        f = (((f - 1.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue()) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * f;
        this.state.scale(f, f2, f3);
        updateMatrix();
    }

    private float fitScale(RectF rectF, float f, float f2) {
        float width = rectF.width() * f2;
        float height = rectF.height() * f2;
        float width2 = (rectF.width() - width) / 2.0f;
        float height2 = (rectF.height() - height) / 2.0f;
        float f3 = rectF.left;
        float f4 = f3 + width2;
        float f5 = rectF.top;
        rectF.set(f4, f5 + height2, (f3 + width2) + width, (f5 + height2) + height);
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
        f6 = rectF.top;
        if (f6 > f3) {
            f5 += f6 - f3;
            f3 = f6;
        }
        f6 = rectF.right;
        if (f6 < f4) {
            f2 += f6 - f4;
        }
        float f7 = rectF.bottom;
        if (f7 < f5) {
            f3 += f7 - f5;
        }
        f7 = rectF2.centerX() - (f2 + (rectF2.width() / 2.0f));
        f2 = rectF2.centerY() - (f3 + (rectF2.height() / 2.0f));
        double d = (double) f;
        Double.isNaN(d);
        double d2 = 1.5707963267948966d - d;
        double sin = Math.sin(d2);
        double d3 = (double) f7;
        Double.isNaN(d3);
        f = (float) (sin * d3);
        d2 = Math.cos(d2);
        Double.isNaN(d3);
        f7 = (float) (d2 * d3);
        Double.isNaN(d);
        d += 1.5707963267948966d;
        double cos = Math.cos(d);
        d2 = (double) f2;
        Double.isNaN(d2);
        float f8 = (float) (cos * d2);
        double sin2 = Math.sin(d);
        Double.isNaN(d2);
        pointF.set((pointF.x + f) + f8, (pointF.y + f7) + ((float) (sin2 * d2)));
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

    private void fitContentInBounds(boolean z, boolean z2, boolean z3) {
        fitContentInBounds(z, z2, z3, false);
    }

    private void fitContentInBounds(boolean z, boolean z2, boolean z3, boolean z4) {
        if (this.state != null) {
            float cropWidth = this.areaView.getCropWidth();
            float cropHeight = this.areaView.getCropHeight();
            float access$1200 = this.state.getOrientedWidth();
            float access$1300 = this.state.getOrientedHeight();
            float access$1500 = this.state.getRotation();
            float toRadians = (float) Math.toRadians((double) access$1500);
            RectF calculateBoundingBox = calculateBoundingBox(cropWidth, cropHeight, access$1500);
            RectF rectF = new RectF(0.0f, 0.0f, access$1200, access$1300);
            cropWidth = (cropWidth - access$1200) / 2.0f;
            cropHeight = (cropHeight - access$1300) / 2.0f;
            float access$1100 = this.state.getScale();
            this.tempRect.setRect(rectF);
            Matrix access$1600 = this.state.getMatrix();
            access$1600.preTranslate(cropWidth / access$1100, cropHeight / access$1100);
            this.tempMatrix.reset();
            this.tempMatrix.setTranslate(rectF.centerX(), rectF.centerY());
            Matrix matrix = this.tempMatrix;
            matrix.setConcat(matrix, access$1600);
            this.tempMatrix.preTranslate(-rectF.centerX(), -rectF.centerY());
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempMatrix.reset();
            this.tempMatrix.preRotate(-access$1500, access$1200 / 2.0f, access$1300 / 2.0f);
            this.tempRect.applyMatrix(this.tempMatrix);
            this.tempRect.getRect(rectF);
            PointF pointF = new PointF(this.state.getX(), this.state.getY());
            if (!rectF.contains(calculateBoundingBox)) {
                cropHeight = (!z || (calculateBoundingBox.width() <= rectF.width() && calculateBoundingBox.height() <= rectF.height())) ? access$1100 : fitScale(rectF, access$1100, calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF));
                fitTranslation(rectF, calculateBoundingBox, pointF, toRadians);
            } else if (!z2 || this.rotationStartScale <= 0.0f) {
                cropHeight = access$1100;
            } else {
                cropHeight = calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF);
                if (this.state.getScale() * cropHeight < this.rotationStartScale) {
                    cropHeight = 1.0f;
                }
                cropHeight = fitScale(rectF, access$1100, cropHeight);
                fitTranslation(rectF, calculateBoundingBox, pointF, toRadians);
            }
            access$1300 = pointF.x - this.state.getX();
            access$1500 = pointF.y - this.state.getY();
            if (z3) {
                toRadians = cropHeight / access$1100;
                if (Math.abs(toRadians - 1.0f) >= 1.0E-5f || Math.abs(access$1300) >= 1.0E-5f || Math.abs(access$1500) >= 1.0E-5f) {
                    this.animating = true;
                    float[] fArr = new float[]{1.0f, 0.0f, 0.0f};
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new -$$Lambda$CropView$wiih4K5GW50uIgzkO67FnI-u-xc(this, access$1300, fArr, access$1500, toRadians));
                    final boolean z5 = z4;
                    final boolean z6 = z;
                    final boolean z7 = z2;
                    final boolean z8 = z3;
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            CropView.this.animating = false;
                            if (!z5) {
                                CropView.this.fitContentInBounds(z6, z7, z8, true);
                            }
                        }
                    });
                    ofFloat.setInterpolator(this.areaView.getInterpolator());
                    ofFloat.setDuration(z4 ? 100 : 200);
                    ofFloat.start();
                } else {
                    return;
                }
            }
            this.state.translate(access$1300, access$1500);
            this.state.scale(cropHeight / access$1100, 0.0f, 0.0f);
            updateMatrix();
        }
    }

    public /* synthetic */ void lambda$fitContentInBounds$1$CropView(float f, float[] fArr, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        f = (f * floatValue) - fArr[1];
        fArr[1] = fArr[1] + f;
        f2 = (f2 * floatValue) - fArr[2];
        fArr[2] = fArr[2] + f2;
        this.state.translate(f * fArr[0], f2 * fArr[0]);
        f3 = (((f3 - 1.0f) * floatValue) + 1.0f) / fArr[0];
        fArr[0] = fArr[0] * f3;
        this.state.scale(f3, 0.0f, 0.0f);
        updateMatrix();
    }

    public void rotate90Degrees() {
        if (this.state != null) {
            this.areaView.resetAnimator();
            resetRotationStartScale();
            float access$900 = ((this.state.getOrientation() - this.state.getBaseRotation()) - 90.0f) % 360.0f;
            boolean z = this.freeform;
            boolean z2 = true;
            if (!z || this.areaView.getLockAspectRatio() <= 0.0f) {
                this.areaView.setBitmap(this.bitmap, (this.state.getBaseRotation() + access$900) % 180.0f != 0.0f, this.freeform);
            } else {
                CropAreaView cropAreaView = this.areaView;
                cropAreaView.setLockedAspectRatio(1.0f / cropAreaView.getLockAspectRatio());
                cropAreaView = this.areaView;
                cropAreaView.setActualRect(cropAreaView.getLockAspectRatio());
                z = false;
            }
            this.state.reset(this.areaView, access$900, z);
            updateMatrix();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                if (!(access$900 == 0.0f && this.areaView.getLockAspectRatio() == 0.0f)) {
                    z2 = false;
                }
                cropViewListener.onChange(z2);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.animating) {
            return true;
        }
        boolean z = false;
        if (this.areaView.onTouchEvent(motionEvent)) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            onScrollChangeBegan();
        } else if (action == 1 || action == 3) {
            onScrollChangeEnded();
        }
        try {
            z = this.detector.onTouchEvent(motionEvent);
        } catch (Exception unused) {
        }
        return z;
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
        this.areaView.setGridType(GridType.MAJOR, false);
        this.state.translate(this.previousAreaRect.centerX() - this.areaView.getCropCenterX(), this.previousAreaRect.centerY() - this.areaView.getCropCenterY());
        updateMatrix();
        this.areaView.getCropRect(this.previousAreaRect);
        fitContentInBounds(true, false, false);
    }

    public void onAreaChangeEnded() {
        this.areaView.setGridType(GridType.NONE, true);
        fillAreaView(this.areaView.getTargetRectToFill(), false);
    }

    public void onDrag(float f, float f2) {
        if (!this.animating) {
            this.state.translate(f, f2);
            updateMatrix();
        }
    }

    public void onScrollChangeBegan() {
        if (!this.animating) {
            this.areaView.setGridType(GridType.MAJOR, true);
            resetRotationStartScale();
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                cropViewListener.onChange(false);
            }
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    public void onScale(float f, float f2, float f3) {
        if (!this.animating) {
            if (this.state.getScale() * f > 30.0f) {
                f = 30.0f / this.state.getScale();
            }
            this.state.scale(f, ((f2 - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((f3 - (((((float) this.imageView.getHeight()) - this.bottomPadding) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
            updateMatrix();
        }
    }

    public void onRotationBegan() {
        this.areaView.setGridType(GridType.MINOR, false);
        if (this.rotationStartScale < 1.0E-5f) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(GridType.NONE, true);
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    public void setRotation(float f) {
        this.state.rotate(f - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public Bitmap getResult() {
        CropState cropState = this.state;
        if (cropState == null || (!cropState.hasChanges() && this.state.getBaseRotation() < 1.0E-5f && this.freeform)) {
            return this.bitmap;
        }
        RectF rectF = new RectF();
        this.areaView.getCropRect(rectF);
        int ceil = (int) Math.ceil((double) scaleWidthToMaxSize(rectF, new RectF(0.0f, 0.0f, 1280.0f, 1280.0f)));
        float f = (float) ceil;
        int ceil2 = (int) Math.ceil((double) (f / this.areaView.getAspectRatio()));
        Bitmap createBitmap = Bitmap.createBitmap(ceil, ceil2, Config.ARGB_8888);
        Matrix matrix = new Matrix();
        matrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        matrix.postRotate(this.state.getOrientation());
        this.state.getConcatMatrix(matrix);
        f /= this.areaView.getCropWidth();
        matrix.postScale(f, f);
        matrix.postTranslate((float) (ceil / 2), (float) (ceil2 / 2));
        new Canvas(createBitmap).drawBitmap(this.bitmap, matrix, new Paint(2));
        return createBitmap;
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
        if (this.areaView.getLockAspectRatio() > 0.0f) {
            this.areaView.setLockedAspectRatio(0.0f);
            CropViewListener cropViewListener = this.listener;
            if (cropViewListener != null) {
                cropViewListener.onAspectLock(false);
            }
        } else if (!this.hasAspectRatioDialog) {
            this.hasAspectRatioDialog = true;
            r1 = new String[8];
            r3 = new Integer[6][];
            r3[0] = new Integer[]{Integer.valueOf(3), Integer.valueOf(2)};
            r3[1] = new Integer[]{Integer.valueOf(5), Integer.valueOf(3)};
            r3[2] = new Integer[]{Integer.valueOf(4), Integer.valueOf(3)};
            r3[3] = new Integer[]{Integer.valueOf(5), Integer.valueOf(4)};
            r3[4] = new Integer[]{Integer.valueOf(7), Integer.valueOf(5)};
            r3[5] = new Integer[]{Integer.valueOf(16), Integer.valueOf(9)};
            r1[0] = LocaleController.getString("CropOriginal", NUM);
            r1[1] = LocaleController.getString("CropSquare", NUM);
            int i = 2;
            for (Object[] objArr : r3) {
                String str = "%d:%d";
                if (this.areaView.getAspectRatio() > 1.0f) {
                    r1[i] = String.format(str, new Object[]{objArr[0], objArr[1]});
                } else {
                    r1[i] = String.format(str, new Object[]{objArr[1], objArr[0]});
                }
                i++;
            }
            AlertDialog create = new Builder(getContext()).setItems(r1, new -$$Lambda$CropView$05yvHZ5A2zqkNRsfdUscWVy3MHM(this, r3)).create();
            create.setCanceledOnTouchOutside(true);
            create.setOnCancelListener(new -$$Lambda$CropView$MBImByPOxBcWFdcV4k41dkfucIY(this));
            create.show();
        }
    }

    public /* synthetic */ void lambda$showAspectRatioDialog$2$CropView(Integer[][] numArr, DialogInterface dialogInterface, int i) {
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

    public /* synthetic */ void lambda$showAspectRatioDialog$3$CropView(DialogInterface dialogInterface) {
        this.hasAspectRatioDialog = false;
    }

    public void updateLayout() {
        float cropWidth = this.areaView.getCropWidth();
        CropState cropState = this.state;
        if (cropState != null) {
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
}
