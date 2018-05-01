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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
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
    class C11231 implements OnPreDrawListener {
        C11231() {
        }

        public boolean onPreDraw() {
            CropView.this.reset();
            CropView.this.imageView.getViewTreeObserver().removeOnPreDrawListener(this);
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.Crop.CropView$7 */
    class C11297 implements OnCancelListener {
        C11297() {
        }

        public void onCancel(DialogInterface dialogInterface) {
            CropView.this.hasAspectRatioDialog = false;
        }
    }

    private class CropRectangle {
        float[] coords = new float[8];

        CropRectangle() {
        }

        void setRect(RectF rectF) {
            this.coords[0] = rectF.left;
            this.coords[1] = rectF.top;
            this.coords[2] = rectF.right;
            this.coords[3] = rectF.top;
            this.coords[4] = rectF.right;
            this.coords[5] = rectF.bottom;
            this.coords[6] = rectF.left;
            this.coords[7] = rectF.bottom;
        }

        void applyMatrix(Matrix matrix) {
            matrix.mapPoints(this.coords);
        }

        void getRect(RectF rectF) {
            rectF.set(this.coords[0], this.coords[1], this.coords[2], this.coords[7]);
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

        private CropState(Bitmap bitmap, int i) {
            this.width = (float) bitmap.getWidth();
            this.height = (float) bitmap.getHeight();
            this.f13x = 0.0f;
            this.f14y = 0.0f;
            this.scale = 1.0f;
            this.baseRotation = (float) i;
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

        private void translate(float f, float f2) {
            this.f13x += f;
            this.f14y += f2;
            this.matrix.postTranslate(f, f2);
        }

        private float getX() {
            return this.f13x;
        }

        private float getY() {
            return this.f14y;
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
            this.f13x = 0.0f;
            this.f14y = 0.0f;
            this.rotation = 0.0f;
            this.orientation = f;
            f = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.height : this.width;
            float f2 = (this.orientation + this.baseRotation) % 180.0f != 0.0f ? this.width : this.height;
            if (z) {
                this.minimumScale = cropAreaView.getCropWidth() / f;
            } else {
                this.minimumScale = Math.max(cropAreaView.getCropWidth() / f, cropAreaView.getCropHeight() / f2);
            }
            this.scale = this.minimumScale;
            this.matrix.postScale(this.scale, this.scale);
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

    public void setListener(CropViewListener cropViewListener) {
        this.listener = cropViewListener;
    }

    public void setBottomPadding(float f) {
        this.bottomPadding = f;
        this.areaView.setBottomPadding(f);
    }

    public void setBitmap(Bitmap bitmap, int i, boolean z) {
        this.bitmap = bitmap;
        this.freeform = z;
        this.state = new CropState(this.bitmap, i);
        this.backView.setVisibility(4);
        this.imageView.setVisibility(4);
        if (z) {
            this.areaView.setDimVisibility(0);
        }
        this.imageView.getViewTreeObserver().addOnPreDrawListener(new C11231());
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

    private void fillAreaView(RectF rectF, boolean z) {
        boolean z2;
        float access$800;
        final float[] fArr = new float[1];
        int i = 0;
        fArr[0] = 1.0f;
        float max = Math.max(rectF.width() / this.areaView.getCropWidth(), rectF.height() / this.areaView.getCropHeight());
        if (this.state.getScale() * max > MAX_SCALE) {
            z2 = true;
            access$800 = MAX_SCALE / this.state.getScale();
        } else {
            z2 = false;
            access$800 = max;
        }
        if (VERSION.SDK_INT >= 21) {
            i = AndroidUtilities.statusBarHeight;
        }
        final float access$900 = this.state.getOrientedWidth() * ((rectF.centerX() - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth());
        float centerY = ((rectF.centerY() - (((((float) this.imageView.getHeight()) - this.bottomPadding) + ((float) i)) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight();
        Animator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        final float f = centerY;
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = (1.0f + ((access$800 - 1.0f) * ((Float) valueAnimator.getAnimatedValue()).floatValue())) / fArr[0];
                valueAnimator = fArr;
                valueAnimator[0] = valueAnimator[0] * floatValue;
                CropView.this.state.scale(floatValue, access$900, f);
                CropView.this.updateMatrix();
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (z2 != null) {
                    CropView.this.fitContentInBounds(false, false, true);
                }
            }
        });
        this.areaView.fill(rectF, ofFloat, true);
        this.initialAreaRect.set(rectF);
    }

    private float fitScale(RectF rectF, float f, float f2) {
        float width = rectF.width() * f2;
        float height = rectF.height() * f2;
        float width2 = (rectF.width() - width) / 2.0f;
        float height2 = (rectF.height() - height) / 2.0f;
        rectF.set(rectF.left + width2, rectF.top + height2, (rectF.left + width2) + width, (rectF.top + height2) + height);
        return f * f2;
    }

    private void fitTranslation(RectF rectF, RectF rectF2, PointF pointF, float f) {
        float f2 = rectF2.left;
        float f3 = rectF2.top;
        float f4 = rectF2.right;
        float f5 = rectF2.bottom;
        if (rectF.left > f2) {
            f4 += rectF.left - f2;
            f2 = rectF.left;
        }
        if (rectF.top > f3) {
            f5 += rectF.top - f3;
            f3 = rectF.top;
        }
        if (rectF.right < f4) {
            f2 += rectF.right - f4;
        }
        if (rectF.bottom < f5) {
            f3 += rectF.bottom - f5;
        }
        rectF = rectF2.centerX() - (f2 + (rectF2.width() / 2.0f));
        f2 = rectF2.centerY() - (f3 + (rectF2.height() / NUM));
        double d = (double) f;
        double d2 = 1.5707963267948966d - d;
        rectF = (double) rectF;
        f = (float) (Math.sin(d2) * rectF);
        rectF = (float) (Math.cos(d2) * rectF);
        double d3 = 1.5707963267948966d + d;
        d2 = (double) f2;
        pointF.set((pointF.x + f) + ((float) (Math.cos(d3) * d2)), (pointF.y + rectF) + ((float) (Math.sin(d3) * d2)));
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
        float cropWidth = this.areaView.getCropWidth();
        float cropHeight = this.areaView.getCropHeight();
        float access$900 = this.state.getOrientedWidth();
        float access$1000 = this.state.getOrientedHeight();
        float access$1400 = this.state.getRotation();
        float toRadians = (float) Math.toRadians((double) access$1400);
        RectF calculateBoundingBox = calculateBoundingBox(cropWidth, cropHeight, access$1400);
        RectF rectF = new RectF(0.0f, 0.0f, access$900, access$1000);
        cropWidth = (cropWidth - access$900) / 2.0f;
        cropHeight = (cropHeight - access$1000) / 2.0f;
        float access$800 = this.state.getScale();
        this.tempRect.setRect(rectF);
        Matrix access$1500 = this.state.getMatrix();
        access$1500.preTranslate(cropWidth / access$800, cropHeight / access$800);
        this.tempMatrix.reset();
        this.tempMatrix.setTranslate(rectF.centerX(), rectF.centerY());
        this.tempMatrix.setConcat(this.tempMatrix, access$1500);
        this.tempMatrix.preTranslate(-rectF.centerX(), -rectF.centerY());
        this.tempRect.applyMatrix(this.tempMatrix);
        this.tempMatrix.reset();
        this.tempMatrix.preRotate(-access$1400, access$900 / 2.0f, access$1000 / 2.0f);
        this.tempRect.applyMatrix(this.tempMatrix);
        this.tempRect.getRect(rectF);
        PointF pointF = new PointF(this.state.getX(), this.state.getY());
        if (!rectF.contains(calculateBoundingBox)) {
            cropHeight = (!z || (calculateBoundingBox.width() <= rectF.width() && calculateBoundingBox.height() <= rectF.height())) ? access$800 : fitScale(rectF, access$800, calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF));
            fitTranslation(rectF, calculateBoundingBox, pointF, toRadians);
        } else if (!z2 || r6.rotationStartScale <= 0.0f) {
            cropHeight = access$800;
        } else {
            cropHeight = calculateBoundingBox.width() / scaleWidthToMaxSize(calculateBoundingBox, rectF);
            if (r6.state.getScale() * cropHeight < r6.rotationStartScale) {
                cropHeight = 1.0f;
            }
            cropHeight = fitScale(rectF, access$800, cropHeight);
            fitTranslation(rectF, calculateBoundingBox, pointF, toRadians);
        }
        access$1000 = pointF.x - r6.state.getX();
        access$1400 = pointF.y - r6.state.getY();
        if (z3) {
            toRadians = cropHeight / access$800;
            if (Math.abs(toRadians - 1.0f) >= EPSILON || Math.abs(access$1000) >= EPSILON || Math.abs(access$1400) >= EPSILON) {
                r6.animating = true;
                float[] fArr = new float[]{1.0f, 0.0f, 0.0f};
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                access$900 = access$1000;
                final float[] fArr2 = fArr;
                ofFloat.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        valueAnimator = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        float f = (access$900 * valueAnimator) - fArr2[1];
                        float[] fArr = fArr2;
                        fArr[1] = fArr[1] + f;
                        float f2 = (access$1400 * valueAnimator) - fArr2[2];
                        float[] fArr2 = fArr2;
                        fArr2[2] = fArr2[2] + f2;
                        CropView.this.state.translate(f * fArr2[0], f2 * fArr2[0]);
                        f2 = (1.0f + ((toRadians - 1.0f) * valueAnimator)) / fArr2[0];
                        valueAnimator = fArr2;
                        valueAnimator[0] = valueAnimator[0] * f2;
                        CropView.this.state.scale(f2, 0.0f, 0.0f);
                        CropView.this.updateMatrix();
                    }
                });
                final boolean z5 = z4;
                final boolean z6 = z;
                final boolean z7 = z2;
                final boolean z8 = z3;
                ofFloat.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        CropView.this.animating = false;
                        if (z5 == null) {
                            CropView.this.fitContentInBounds(z6, z7, z8, true);
                        }
                    }
                });
                ofFloat.setInterpolator(r6.areaView.getInterpolator());
                ofFloat.setDuration(z4 ? 100 : 200);
                ofFloat.start();
            } else {
                return;
            }
        }
        r6.state.translate(access$1000, access$1400);
        r6.state.scale(cropHeight / access$800, 0.0f, 0.0f);
        updateMatrix();
    }

    public void rotate90Degrees() {
        this.areaView.resetAnimator();
        resetRotationStartScale();
        float access$600 = ((this.state.getOrientation() - this.state.getBaseRotation()) - 90.0f) % 360.0f;
        boolean z = this.freeform;
        boolean z2 = true;
        if (!this.freeform || this.areaView.getLockAspectRatio() <= 0.0f) {
            this.areaView.setBitmap(this.bitmap, (this.state.getBaseRotation() + access$600) % 180.0f != 0.0f, this.freeform);
        } else {
            this.areaView.setLockedAspectRatio(1.0f / this.areaView.getLockAspectRatio());
            this.areaView.setActualRect(this.areaView.getLockAspectRatio());
            z = false;
        }
        this.state.reset(this.areaView, access$600, z);
        updateMatrix();
        if (this.listener != null) {
            CropViewListener cropViewListener = this.listener;
            if (access$600 != 0.0f || this.areaView.getLockAspectRatio() != 0.0f) {
                z2 = false;
            }
            cropViewListener.onChange(z2);
        }
    }

    public boolean onTouchEvent(android.view.MotionEvent r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r0 = r3.animating;
        r1 = 1;
        if (r0 == 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = 0;
        r2 = r3.areaView;
        r2 = r2.onTouchEvent(r4);
        if (r2 == 0) goto L_0x0010;
    L_0x000f:
        return r1;
    L_0x0010:
        r1 = r4.getAction();
        r2 = 3;
        if (r1 == r2) goto L_0x001f;
    L_0x0017:
        switch(r1) {
            case 0: goto L_0x001b;
            case 1: goto L_0x001f;
            default: goto L_0x001a;
        };
    L_0x001a:
        goto L_0x0022;
    L_0x001b:
        r3.onScrollChangeBegan();
        goto L_0x0022;
    L_0x001f:
        r3.onScrollChangeEnded();
    L_0x0022:
        r1 = r3.detector;	 Catch:{ Exception -> 0x0029 }
        r4 = r1.onTouchEvent(r4);	 Catch:{ Exception -> 0x0029 }
        goto L_0x002a;
    L_0x0029:
        r4 = r0;
    L_0x002a:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Crop.CropView.onTouchEvent(android.view.MotionEvent):boolean");
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
            if (this.listener != null) {
                this.listener.onChange(false);
            }
        }
    }

    public void onScrollChangeEnded() {
        this.areaView.setGridType(GridType.NONE, true);
        fitContentInBounds(true, false, true);
    }

    public void onScale(float f, float f2, float f3) {
        if (!this.animating) {
            if (this.state.getScale() * f > MAX_SCALE) {
                f = MAX_SCALE / this.state.getScale();
            }
            this.state.scale(f, ((f2 - ((float) (this.imageView.getWidth() / 2))) / this.areaView.getCropWidth()) * this.state.getOrientedWidth(), ((f3 - (((((float) this.imageView.getHeight()) - this.bottomPadding) - ((float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0))) / 2.0f)) / this.areaView.getCropHeight()) * this.state.getOrientedHeight());
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

    public void setRotation(float f) {
        this.state.rotate(f - this.state.getRotation(), 0.0f, 0.0f);
        fitContentInBounds(true, true, false);
    }

    public Bitmap getResult() {
        if (!this.state.hasChanges() && this.state.getBaseRotation() < EPSILON && this.freeform) {
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
            r1 = new String[8];
            r3 = new Integer[6][];
            r3[0] = new Integer[]{Integer.valueOf(3), Integer.valueOf(2)};
            r3[1] = new Integer[]{Integer.valueOf(5), Integer.valueOf(3)};
            r3[2] = new Integer[]{Integer.valueOf(4), Integer.valueOf(3)};
            r3[3] = new Integer[]{Integer.valueOf(5), Integer.valueOf(4)};
            r3[4] = new Integer[]{Integer.valueOf(7), Integer.valueOf(5)};
            r3[5] = new Integer[]{Integer.valueOf(16), Integer.valueOf(9)};
            r1[0] = LocaleController.getString("CropOriginal", C0446R.string.CropOriginal);
            r1[1] = LocaleController.getString("CropSquare", C0446R.string.CropSquare);
            int i = 2;
            for (Object[] objArr : r3) {
                if (this.areaView.getAspectRatio() > 1.0f) {
                    r1[i] = String.format("%d:%d", new Object[]{objArr[0], objArr[1]});
                } else {
                    r1[i] = String.format("%d:%d", new Object[]{objArr[1], objArr[0]});
                }
                i++;
            }
            AlertDialog create = new Builder(getContext()).setItems(r1, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    CropView.this.hasAspectRatioDialog = false;
                    switch (i) {
                        case 0:
                            CropView.this.setLockedAspectRatio((CropView.this.state.getBaseRotation() % NUM != null ? CropView.this.state.getHeight() : CropView.this.state.getWidth()) / (CropView.this.state.getBaseRotation() % 180.0f != 0.0f ? CropView.this.state.getWidth() : CropView.this.state.getHeight()));
                            return;
                        case 1:
                            CropView.this.setLockedAspectRatio(1.0f);
                            return;
                        default:
                            i = r3[i - 2];
                            if (CropView.this.areaView.getAspectRatio() > 1.0f) {
                                CropView.this.setLockedAspectRatio(((float) i[0].intValue()) / ((float) i[1].intValue()));
                                return;
                            } else {
                                CropView.this.setLockedAspectRatio(((float) i[1].intValue()) / ((float) i[0].intValue()));
                                return;
                            }
                    }
                }
            }).create();
            create.setCanceledOnTouchOutside(true);
            create.setOnCancelListener(new C11297());
            create.show();
        }
    }

    public void updateLayout() {
        float cropWidth = this.areaView.getCropWidth();
        this.areaView.calculateRect(this.initialAreaRect, this.state.getWidth() / this.state.getHeight());
        this.areaView.setActualRect(this.areaView.getAspectRatio());
        this.areaView.getCropRect(this.previousAreaRect);
        this.state.scale(this.areaView.getCropWidth() / cropWidth, 0.0f, 0.0f);
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
