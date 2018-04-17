package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
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
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
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

    /* renamed from: org.telegram.ui.Components.Crop.CropView$1 */
    class C11171 implements OnPreDrawListener {
        C11171() {
        }

        public boolean onPreDraw() {
            CropView.this.reset();
            CropView.this.imageView.getViewTreeObserver().removeOnPreDrawListener(this);
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.Crop.CropView$7 */
    class C11237 implements OnCancelListener {
        C11237() {
        }

        public void onCancel(DialogInterface dialog) {
            CropView.this.hasAspectRatioDialog = false;
        }
    }

    private class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        void setRect(RectF rect) {
            this.coords[0] = rect.left;
            this.coords[1] = rect.top;
            this.coords[2] = rect.right;
            this.coords[3] = rect.top;
            this.coords[4] = rect.right;
            this.coords[5] = rect.bottom;
            this.coords[6] = rect.left;
            this.coords[7] = rect.bottom;
        }

        void applyMatrix(Matrix m) {
            m.mapPoints(this.coords);
        }

        void getRect(RectF rect) {
            rect.set(this.coords[0], this.coords[1], this.coords[2], this.coords[7]);
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
        /* renamed from: x */
        private float f13x;
        /* renamed from: y */
        private float f14y;

        private CropState(Bitmap bitmap, int bRotation) {
            this.width = (float) bitmap.getWidth();
            this.height = (float) bitmap.getHeight();
            this.f13x = 0.0f;
            this.f14y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = (float) bRotation;
            this.rotation = 0.0f;
            this.matrix = new Matrix();
        }

        private boolean hasChanges() {
            if (Math.abs(this.f13x) <= CropView.EPSILON && Math.abs(this.f14y) <= CropView.EPSILON && Math.abs(this.scale - this.minimumScale) <= CropView.EPSILON && Math.abs(this.rotation) <= CropView.EPSILON) {
                if (Math.abs(this.orientation) <= CropView.EPSILON) {
                    return false;
                }
            }
            return true;
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

        private void translate(float x, float y) {
            this.f13x += x;
            this.f14y += y;
            this.matrix.postTranslate(x, y);
        }

        private float getX() {
            return this.f13x;
        }

        private float getY() {
            return this.f14y;
        }

        private void scale(float s, float pivotX, float pivotY) {
            this.scale *= s;
            this.matrix.postScale(s, s, pivotX, pivotY);
        }

        private float getScale() {
            return this.scale;
        }

        private float getMinimumScale() {
            return this.minimumScale;
        }

        private void rotate(float angle, float pivotX, float pivotY) {
            this.rotation += angle;
            this.matrix.postRotate(angle, pivotX, pivotY);
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

        private void reset(CropAreaView areaView, float orient, boolean freeform) {
            this.matrix.reset();
            this.f13x = 0.0f;
            this.f14y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = orient;
            float w = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
            float h = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
            if (freeform) {
                this.minimumScale = areaView.getCropWidth() / w;
            } else {
                this.minimumScale = Math.max(areaView.getCropWidth() / w, areaView.getCropHeight() / h);
            }
            this.scale = this.minimumScale;
            this.matrix.postScale(this.scale, this.scale);
        }

        private void getConcatMatrix(Matrix toMatrix) {
            toMatrix.postConcat(this.matrix);
        }

        private Matrix getMatrix() {
            Matrix m = new Matrix();
            m.set(this.matrix);
            return m;
        }
    }

    public interface CropViewListener {
        void onAspectLock(boolean z);

        void onChange(boolean z);
    }

    public CropView(Context context) {
        super(context);
        this.backView = new View(context);
        this.backView.setBackgroundColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
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

    public void setListener(CropViewListener l) {
        this.listener = l;
    }

    public void setBottomPadding(float value) {
        this.bottomPadding = value;
        this.areaView.setBottomPadding(value);
    }

    public void setBitmap(Bitmap b, int rotation, boolean fform) {
        this.bitmap = b;
        this.freeform = fform;
        this.state = new CropState(this.bitmap, rotation);
        this.backView.setVisibility(4);
        this.imageView.setVisibility(4);
        if (fform) {
            this.areaView.setDimVisibility(false);
        }
        this.imageView.getViewTreeObserver().addOnPreDrawListener(new C11171());
        this.imageView.setImageBitmap(this.bitmap);
    }

    public void willShow() {
        this.areaView.setFrameVisibility(true);
        this.areaView.setDimVisibility(true);
        this.areaView.invalidate();
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
        if (this.listener != null) {
            this.listener.onChange(true);
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

    private void fillAreaView(RectF targetRect, boolean allowZoomOut) {
        boolean ensureFit;
        float scale;
        RectF rectF = targetRect;
        final float[] currentScale = new float[1];
        int i = 0;
        currentScale[0] = 1.0f;
        float scale2 = Math.max(targetRect.width() / this.areaView.getCropWidth(), targetRect.height() / this.areaView.getCropHeight());
        float newScale = this.state.getScale() * scale2;
        if (newScale > MAX_SCALE) {
            ensureFit = true;
            scale = MAX_SCALE / r6.state.getScale();
        } else {
            scale = scale2;
            ensureFit = false;
        }
        if (VERSION.SDK_INT >= 21) {
            i = AndroidUtilities.statusBarHeight;
        }
        float y = ((targetRect.centerY() - (((((float) r6.imageView.getHeight()) - r6.bottomPadding) + ((float) i)) / 2.0f)) / r6.areaView.getCropHeight()) * r6.state.getOrientedHeight();
        final float targetScale = scale;
        final boolean animEnsureFit = ensureFit;
        C11182 c11182 = r0;
        final float centerX = ((targetRect.centerX() - ((float) (r6.imageView.getWidth() / 2))) / r6.areaView.getCropWidth()) * r6.state.getOrientedWidth();
        ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        final ValueAnimator animator2 = y;
        C11182 c111822 = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float deltaScale = (1.0f + ((targetScale - 1.0f) * ((Float) animation.getAnimatedValue()).floatValue())) / currentScale[0];
                float[] fArr = currentScale;
                fArr[0] = fArr[0] * deltaScale;
                CropView.this.state.scale(deltaScale, centerX, animator2);
                CropView.this.updateMatrix();
            }
        };
        animator.addUpdateListener(c11182);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (animEnsureFit) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        r6.areaView.fill(rectF, animator, true);
        r6.initialAreaRect.set(rectF);
    }

    private float fitScale(RectF contentRect, float scale, float ratio) {
        float scaledW = contentRect.width() * ratio;
        float scaledH = contentRect.height() * ratio;
        float scaledX = (contentRect.width() - scaledW) / 2.0f;
        float scaledY = (contentRect.height() - scaledH) / 2.0f;
        contentRect.set(contentRect.left + scaledX, contentRect.top + scaledY, (contentRect.left + scaledX) + scaledW, (contentRect.top + scaledY) + scaledH);
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
        float yCompX = (float) (Math.cos(((double) f) + 1.5707963267948966d) * ((double) deltaY));
        float yCompY = (float) (Math.sin(((double) f) + 1.5707963267948966d) * ((double) deltaY));
        pointF.set((pointF.x + ((float) (Math.sin(1.5707963267948966d - ((double) f)) * ((double) deltaX)))) + yCompX, (pointF.y + ((float) (Math.cos(1.5707963267948966d - ((double) f)) * ((double) deltaX)))) + yCompY);
    }

    public RectF calculateBoundingBox(float w, float h, float rotation) {
        RectF result = new RectF(0.0f, 0.0f, w, h);
        Matrix m = new Matrix();
        m.postRotate(rotation, w / 2.0f, h / 2.0f);
        m.mapRect(result);
        return result;
    }

    public float scaleWidthToMaxSize(RectF sizeRect, RectF maxSizeRect) {
        float w = maxSizeRect.width();
        if (((float) Math.floor((double) ((sizeRect.height() * w) / sizeRect.width()))) <= maxSizeRect.height()) {
            return w;
        }
        return (float) Math.floor((double) ((sizeRect.width() * maxSizeRect.height()) / sizeRect.height()));
    }

    private void fitContentInBounds(boolean allowScale, boolean maximize, boolean animated) {
        fitContentInBounds(allowScale, maximize, animated, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void fitContentInBounds(boolean allowScale, boolean maximize, boolean animated, boolean fast) {
        float ratio;
        float boundsW = this.areaView.getCropWidth();
        float boundsH = this.areaView.getCropHeight();
        float contentW = this.state.getOrientedWidth();
        float contentH = this.state.getOrientedHeight();
        float rotation = this.state.getRotation();
        float radians = (float) Math.toRadians((double) rotation);
        RectF boundsRect = calculateBoundingBox(boundsW, boundsH, rotation);
        RectF contentRect = new RectF(0.0f, 0.0f, contentW, contentH);
        float initialX = (boundsW - contentW) / 2.0f;
        float initialY = (boundsH - contentH) / 2.0f;
        float scale = this.state.getScale();
        this.tempRect.setRect(contentRect);
        Matrix matrix = this.state.getMatrix();
        matrix.preTranslate(initialX / scale, initialY / scale);
        this.tempMatrix.reset();
        this.tempMatrix.setTranslate(contentRect.centerX(), contentRect.centerY());
        this.tempMatrix.setConcat(this.tempMatrix, matrix);
        this.tempMatrix.preTranslate(-contentRect.centerX(), -contentRect.centerY());
        this.tempRect.applyMatrix(this.tempMatrix);
        this.tempMatrix.reset();
        this.tempMatrix.preRotate(-rotation, contentW / 2.0f, contentH / 2.0f);
        this.tempRect.applyMatrix(this.tempMatrix);
        this.tempRect.getRect(contentRect);
        PointF targetTranslation = new PointF(this.state.getX(), this.state.getY());
        float targetScale = scale;
        if (!contentRect.contains(boundsRect)) {
            if (allowScale && (boundsRect.width() > contentRect.width() || boundsRect.height() > contentRect.height())) {
                targetScale = fitScale(contentRect, scale, boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect));
            }
            fitTranslation(contentRect, boundsRect, targetTranslation, radians);
        } else if (maximize && r10.rotationStartScale > 0.0f) {
            ratio = boundsRect.width() / scaleWidthToMaxSize(boundsRect, contentRect);
            if (r10.state.getScale() * ratio < r10.rotationStartScale) {
                ratio = 1.0f;
            }
            targetScale = fitScale(contentRect, scale, ratio);
            fitTranslation(contentRect, boundsRect, targetTranslation, radians);
        }
        float targetScale2 = targetScale;
        ratio = targetTranslation.x - r10.state.getX();
        float dy = targetTranslation.y - r10.state.getY();
        if (animated) {
            float animScale = targetScale2 / scale;
            targetScale = ratio;
            float animDY = dy;
            float dy2 = dy;
            if (Math.abs(animScale - 1.0f) >= EPSILON || Math.abs(targetScale) >= EPSILON || Math.abs(animDY) >= EPSILON) {
                r10.animating = true;
                float animDY2 = animDY;
                final float[] currentValues = new float[]{1.0f, 0.0f, 0.0f};
                float scale2 = scale;
                boundsW = dy2;
                boundsH = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                contentW = ratio;
                ratio = targetScale;
                final float f = animDY2;
                matrix = animScale;
                boundsH.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = ((Float) animation.getAnimatedValue()).floatValue();
                        float deltaX = (ratio * value) - currentValues[1];
                        float[] fArr = currentValues;
                        fArr[1] = fArr[1] + deltaX;
                        float deltaY = (f * value) - currentValues[2];
                        float[] fArr2 = currentValues;
                        fArr2[2] = fArr2[2] + deltaY;
                        CropView.this.state.translate(currentValues[0] * deltaX, currentValues[0] * deltaY);
                        float deltaScale = (1.0f + ((matrix - 1.0f) * value)) / currentValues[0];
                        fArr2 = currentValues;
                        fArr2[0] = fArr2[0] * deltaScale;
                        CropView.this.state.scale(deltaScale, 0.0f, 0.0f);
                        CropView.this.updateMatrix();
                    }
                });
                dy = scale2;
                final boolean z = fast;
                ratio = contentRect;
                final boolean z2 = allowScale;
                final boolean z3 = maximize;
                final boolean radians2 = animated;
                boundsH.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        CropView.this.animating = false;
                        if (!z) {
                            CropView.this.fitContentInBounds(z2, z3, radians2, true);
                        }
                    }
                });
                boundsH.setInterpolator(r10.areaView.getInterpolator());
                boundsH.setDuration(fast ? 100 : 200);
                boundsH.start();
            } else {
                return;
            }
        }
        Matrix matrix2 = matrix;
        RectF rectF = boundsRect;
        float f2 = radians;
        float f3 = boundsW;
        float f4 = boundsH;
        float f5 = contentW;
        boundsW = dy;
        contentW = ratio;
        dy = scale;
        r10.state.translate(contentW, boundsW);
        r10.state.scale(targetScale2 / dy, 0.0f, 0.0f);
        updateMatrix();
    }

    public void rotate90Degrees() {
        this.areaView.resetAnimator();
        resetRotationStartScale();
        float orientation = ((this.state.getOrientation() - this.state.getBaseRotation()) - 90.0f) % 360.0f;
        boolean fform = this.freeform;
        boolean z = false;
        if (!this.freeform || this.areaView.getLockAspectRatio() <= 0.0f) {
            this.areaView.setBitmap(this.bitmap, (this.state.getBaseRotation() + orientation) % 180.0f != 0.0f, this.freeform);
        } else {
            this.areaView.setLockedAspectRatio(1.0f / this.areaView.getLockAspectRatio());
            this.areaView.setActualRect(this.areaView.getLockAspectRatio());
            fform = false;
        }
        this.state.reset(this.areaView, orientation, fform);
        updateMatrix();
        if (this.listener != null) {
            CropViewListener cropViewListener = this.listener;
            if (orientation == 0.0f && this.areaView.getLockAspectRatio() == 0.0f) {
                z = true;
            }
            cropViewListener.onChange(z);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.animating) {
            return true;
        }
        boolean result = false;
        if (this.areaView.onTouchEvent(event)) {
            return true;
        }
        int action = event.getAction();
        if (action != 3) {
            switch (action) {
                case 0:
                    onScrollChangeBegan();
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
        onScrollChangeEnded();
        try {
            result = this.detector.onTouchEvent(event);
        } catch (Exception e) {
        }
        return result;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public void onAreaChangeBegan() {
        this.areaView.getCropRect(this.previousAreaRect);
        resetRotationStartScale();
        if (this.listener != null) {
            this.listener.onChange(false);
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

    public void onDrag(float dx, float dy) {
        if (!this.animating) {
            this.state.translate(dx, dy);
            updateMatrix();
        }
    }

    public void onFling(float startX, float startY, float velocityX, float velocityY) {
    }

    public void onScrollChangeBegan() {
        if (!this.animating) {
            this.areaView.setGridType(GridType.MAJOR, true);
            resetRotationStartScale();
            if (this.listener != null) {
                this.listener.onChange(false);
            }
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    public void onScale(float scale, float x, float y) {
        if (!this.animating) {
            if (this.state.getScale() * scale > MAX_SCALE) {
                scale = MAX_SCALE / this.state.getScale();
            }
            this.state.scale(scale, ((x - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((y - (((((float) this.imageView.getHeight()) - this.bottomPadding) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0.0f))) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
            updateMatrix();
        }
    }

    public void onRotationBegan() {
        this.areaView.setGridType(GridType.MINOR, false);
        if (this.rotationStartScale < EPSILON) {
            this.rotationStartScale = this.state.getScale();
        }
    }

    public void onRotationEnded() {
        this.areaView.setGridType(GridType.NONE, true);
    }

    private void resetRotationStartScale() {
        this.rotationStartScale = 0.0f;
    }

    public void setRotation(float angle) {
        this.state.rotate(angle - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public Bitmap getResult() {
        if (!this.state.hasChanges() && this.state.getBaseRotation() < EPSILON && this.freeform) {
            return this.bitmap;
        }
        RectF cropRect = new RectF();
        this.areaView.getCropRect(cropRect);
        int width = (int) Math.ceil((double) scaleWidthToMaxSize(cropRect, new RectF(0.0f, 0.0f, 1280.0f, 1280.0f)));
        int height = (int) Math.ceil((double) (((float) width) / this.areaView.getAspectRatio()));
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Matrix matrix = new Matrix();
        matrix.postTranslate((-this.state.getWidth()) / 2.0f, (-this.state.getHeight()) / 2.0f);
        matrix.postRotate(this.state.getOrientation());
        this.state.getConcatMatrix(matrix);
        float scale = ((float) width) / this.areaView.getCropWidth();
        matrix.postScale(scale, scale);
        matrix.postTranslate((float) (width / 2), (float) (height / 2));
        new Canvas(resultBitmap).drawBitmap(this.bitmap, matrix, new Paint(2));
        return resultBitmap;
    }

    private void setLockedAspectRatio(float aspectRatio) {
        this.areaView.setLockedAspectRatio(aspectRatio);
        RectF targetRect = new RectF();
        this.areaView.calculateRect(targetRect, aspectRatio);
        fillAreaView(targetRect, true);
        if (this.listener != null) {
            this.listener.onChange(false);
            this.listener.onAspectLock(true);
        }
    }

    public void showAspectRatioDialog() {
        if (this.areaView.getLockAspectRatio() > 0.0f) {
            this.areaView.setLockedAspectRatio(0.0f);
            if (this.listener != null) {
                this.listener.onAspectLock(false);
            }
        } else if (!this.hasAspectRatioDialog) {
            this.hasAspectRatioDialog = true;
            actions = new String[8];
            ratios = new Integer[6][];
            ratios[0] = new Integer[]{Integer.valueOf(3), Integer.valueOf(2)};
            ratios[1] = new Integer[]{Integer.valueOf(5), Integer.valueOf(3)};
            ratios[2] = new Integer[]{Integer.valueOf(4), Integer.valueOf(3)};
            ratios[3] = new Integer[]{Integer.valueOf(5), Integer.valueOf(4)};
            ratios[4] = new Integer[]{Integer.valueOf(7), Integer.valueOf(5)};
            ratios[5] = new Integer[]{Integer.valueOf(16), Integer.valueOf(9)};
            actions[0] = LocaleController.getString("CropOriginal", R.string.CropOriginal);
            actions[1] = LocaleController.getString("CropSquare", R.string.CropSquare);
            int i = 2;
            for (Integer[] ratioPair : ratios) {
                if (this.areaView.getAspectRatio() > 1.0f) {
                    actions[i] = String.format("%d:%d", new Object[]{ratioPair[0], ratioPair[1]});
                } else {
                    actions[i] = String.format("%d:%d", new Object[]{ratioPair[1], ratioPair[0]});
                }
                i++;
            }
            AlertDialog dialog = new Builder(getContext()).setItems(actions, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    CropView.this.hasAspectRatioDialog = false;
                    switch (which) {
                        case 0:
                            CropView.this.setLockedAspectRatio((CropView.this.state.getBaseRotation() % 180.0f != 0.0f ? CropView.this.state.getHeight() : CropView.this.state.getWidth()) / (CropView.this.state.getBaseRotation() % 180.0f != 0.0f ? CropView.this.state.getWidth() : CropView.this.state.getHeight()));
                            return;
                        case 1:
                            CropView.this.setLockedAspectRatio(1.0f);
                            return;
                        default:
                            Integer[] ratioPair = ratios[which - 2];
                            if (CropView.this.areaView.getAspectRatio() > 1.0f) {
                                CropView.this.setLockedAspectRatio(((float) ratioPair[0].intValue()) / ((float) ratioPair[1].intValue()));
                                return;
                            } else {
                                CropView.this.setLockedAspectRatio(((float) ratioPair[1].intValue()) / ((float) ratioPair[0].intValue()));
                                return;
                            }
                    }
                }
            }).create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnCancelListener(new C11237());
            dialog.show();
        }
    }

    public void updateLayout() {
        float w = this.areaView.getCropWidth();
        this.areaView.calculateRect(this.initialAreaRect, this.state.getWidth() / this.state.getHeight());
        this.areaView.setActualRect(this.areaView.getAspectRatio());
        this.areaView.getCropRect(this.previousAreaRect);
        this.state.scale(this.areaView.getCropWidth() / w, 0.0f, 0.0f);
        updateMatrix();
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
