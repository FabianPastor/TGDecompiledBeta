package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.BubbleActivity;
/* loaded from: classes3.dex */
public class CropAreaView extends ViewGroup {
    private Control activeControl;
    private RectF actualRect;
    private Animator animator;
    private Paint bitmapPaint;
    private RectF bottomEdge;
    private RectF bottomLeftCorner;
    private float bottomPadding;
    private RectF bottomRightCorner;
    private Bitmap circleBitmap;
    private Paint dimPaint;
    private boolean dimVisibile;
    private Paint eraserPaint;
    private float frameAlpha;
    private Paint framePaint;
    private boolean frameVisible;
    private boolean freeform;
    private Animator gridAnimator;
    private float gridProgress;
    private GridType gridType;
    private Paint handlePaint;
    private boolean inBubbleMode;
    private AccelerateDecelerateInterpolator interpolator;
    private boolean isDragging;
    private long lastUpdateTime;
    private RectF leftEdge;
    private Paint linePaint;
    private AreaViewListener listener;
    private float lockAspectRatio;
    private float minWidth;
    private GridType previousGridType;
    private int previousX;
    private int previousY;
    private RectF rightEdge;
    public float rotate;
    public float scale;
    private Paint shadowPaint;
    private float sidePadding;
    private RectF targetRect;
    private RectF tempRect;
    private RectF topEdge;
    private RectF topLeftCorner;
    private RectF topRightCorner;
    public float tx;
    public float ty;

    /* loaded from: classes3.dex */
    interface AreaViewListener {
        void onAreaChange();

        void onAreaChangeBegan();

        void onAreaChangeEnded();
    }

    /* loaded from: classes3.dex */
    private enum Control {
        NONE,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public enum GridType {
        NONE,
        MINOR,
        MAJOR
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    public CropAreaView(Context context) {
        super(context);
        this.topLeftCorner = new RectF();
        this.topRightCorner = new RectF();
        this.bottomLeftCorner = new RectF();
        this.bottomRightCorner = new RectF();
        this.topEdge = new RectF();
        this.leftEdge = new RectF();
        this.bottomEdge = new RectF();
        this.rightEdge = new RectF();
        this.actualRect = new RectF();
        this.tempRect = new RectF();
        this.frameAlpha = 1.0f;
        this.interpolator = new AccelerateDecelerateInterpolator();
        this.freeform = true;
        this.targetRect = new RectF();
        this.rotate = 0.0f;
        this.scale = 1.0f;
        this.tx = 0.0f;
        this.ty = 0.0f;
        this.inBubbleMode = context instanceof BubbleActivity;
        this.frameVisible = true;
        this.dimVisibile = true;
        this.sidePadding = AndroidUtilities.dp(16.0f);
        this.minWidth = AndroidUtilities.dp(32.0f);
        this.gridType = GridType.NONE;
        Paint paint = new Paint();
        this.dimPaint = paint;
        paint.setColor(NUM);
        Paint paint2 = new Paint();
        this.shadowPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.shadowPaint.setColor(NUM);
        this.shadowPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        Paint paint3 = new Paint();
        this.linePaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        Paint paint4 = new Paint();
        this.handlePaint = paint4;
        paint4.setStyle(Paint.Style.FILL);
        this.handlePaint.setColor(-1);
        Paint paint5 = new Paint();
        this.framePaint = paint5;
        paint5.setStyle(Paint.Style.FILL);
        this.framePaint.setColor(-NUM);
        Paint paint6 = new Paint(1);
        this.eraserPaint = paint6;
        paint6.setColor(0);
        this.eraserPaint.setStyle(Paint.Style.FILL);
        this.eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Paint paint7 = new Paint(2);
        this.bitmapPaint = paint7;
        paint7.setColor(-1);
        setWillNotDraw(false);
    }

    public void setIsVideo(boolean z) {
        this.minWidth = AndroidUtilities.dp(z ? 64.0f : 32.0f);
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDimVisibility(boolean z) {
        this.dimVisibile = z;
    }

    public void setFrameVisibility(boolean z, boolean z2) {
        this.frameVisible = z;
        float f = 1.0f;
        if (z) {
            if (z2) {
                f = 0.0f;
            }
            this.frameAlpha = f;
            this.lastUpdateTime = SystemClock.elapsedRealtime();
            invalidate();
            return;
        }
        this.frameAlpha = 1.0f;
    }

    public void setBottomPadding(float f) {
        this.bottomPadding = f;
    }

    public Interpolator getInterpolator() {
        return this.interpolator;
    }

    public void setListener(AreaViewListener areaViewListener) {
        this.listener = areaViewListener;
    }

    public void setBitmap(int i, int i2, boolean z, boolean z2) {
        this.freeform = z2;
        float f = z ? i2 / i : i / i2;
        if (!z2) {
            this.lockAspectRatio = 1.0f;
            f = 1.0f;
        }
        setActualRect(f);
    }

    public void setFreeform(boolean z) {
        this.freeform = z;
    }

    public void setActualRect(float f) {
        calculateRect(this.actualRect, f);
        updateTouchAreas();
        invalidate();
    }

    public void setActualRect(RectF rectF) {
        this.actualRect.set(rectF);
        updateTouchAreas();
        invalidate();
    }

    public void setRotationScaleTranslation(float f, float f2, float f3, float f4) {
        this.rotate = f;
        this.scale = f2;
        this.tx = f3;
        this.ty = f4;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        int i2;
        int i3;
        int i4;
        if (this.freeform) {
            int dp = AndroidUtilities.dp(2.0f / this.scale);
            int dp2 = AndroidUtilities.dp(16.0f / this.scale);
            int dp3 = AndroidUtilities.dp(3.0f / this.scale);
            RectF rectF = this.actualRect;
            float f = rectF.left;
            int i5 = ((int) f) - dp;
            float f2 = rectF.top;
            int i6 = ((int) f2) - dp;
            int i7 = dp * 2;
            int i8 = ((int) (rectF.right - f)) + i7;
            int i9 = ((int) (rectF.bottom - f2)) + i7;
            canvas.save();
            canvas.translate(this.tx, this.ty);
            float f3 = this.scale;
            float f4 = (i8 / 2) + i5;
            float f5 = (i9 / 2) + i6;
            canvas.scale(f3, f3, f4, f5);
            canvas.rotate(this.rotate, f4, f5);
            if (this.dimVisibile) {
                this.dimPaint.setAlpha((int) (255.0f - (this.frameAlpha * 127.0f)));
                float f6 = (-getWidth()) * 4;
                float width = getWidth() * 4;
                i = i6;
                canvas.drawRect(f6, (-getHeight()) * 4, width, 0.0f, this.dimPaint);
                canvas.drawRect(f6, 0.0f, 0.0f, getHeight(), this.dimPaint);
                canvas.drawRect(getWidth(), 0.0f, width, getHeight(), this.dimPaint);
                canvas.drawRect(f6, getHeight(), width, getHeight() * 4, this.dimPaint);
                float f7 = i + dp;
                canvas.drawRect(0.0f, 0.0f, getWidth(), f7, this.dimPaint);
                float f8 = (i + i9) - dp;
                canvas.drawRect(0.0f, f7, i5 + dp, f8, this.dimPaint);
                canvas.drawRect((i5 + i8) - dp, f7, getWidth(), f8, this.dimPaint);
                canvas.drawRect(0.0f, f8, getWidth(), getHeight(), this.dimPaint);
            } else {
                i = i6;
            }
            if (!this.frameVisible) {
                return;
            }
            int i10 = dp3 - dp;
            int i11 = dp3 * 2;
            int i12 = i8 - i11;
            int i13 = i9 - i11;
            GridType gridType = this.gridType;
            if (gridType == GridType.NONE && this.gridProgress > 0.0f) {
                gridType = this.previousGridType;
            }
            GridType gridType2 = gridType;
            this.shadowPaint.setAlpha((int) (this.gridProgress * 26.0f * this.frameAlpha));
            this.linePaint.setAlpha((int) (this.gridProgress * 178.0f * this.frameAlpha));
            this.framePaint.setAlpha((int) (this.frameAlpha * 178.0f));
            this.handlePaint.setAlpha((int) (this.frameAlpha * 255.0f));
            int i14 = i5 + i10;
            float f9 = i14;
            float var_ = i + i10;
            int i15 = i5 + i8;
            float var_ = i15 - i10;
            canvas.drawRect(f9, var_, var_, i2 + dp, this.framePaint);
            float var_ = i14 + dp;
            int i16 = i + i9;
            float var_ = i16 - i10;
            canvas.drawRect(f9, var_, var_, var_, this.framePaint);
            canvas.drawRect(f9, i4 - dp, var_, var_, this.framePaint);
            canvas.drawRect(i3 - dp, var_, var_, var_, this.framePaint);
            int i17 = 0;
            while (true) {
                int i18 = 3;
                if (i17 >= 3) {
                    break;
                }
                if (gridType2 == GridType.MINOR) {
                    int i19 = 4;
                    int i20 = 1;
                    while (i20 < i19) {
                        if (i17 != 2 || i20 != i18) {
                            int i21 = i5 + dp3;
                            int i22 = i12 / 3;
                            float var_ = ((i22 / 3) * i20) + i21 + (i22 * i17);
                            int i23 = i + dp3;
                            float var_ = i23;
                            float var_ = i23 + i13;
                            canvas.drawLine(var_, var_, var_, var_, this.shadowPaint);
                            canvas.drawLine(var_, var_, var_, var_, this.linePaint);
                            int i24 = i13 / 3;
                            float var_ = i21;
                            float var_ = i23 + ((i24 / 3) * i20) + (i24 * i17);
                            float var_ = i21 + i12;
                            canvas.drawLine(var_, var_, var_, var_, this.shadowPaint);
                            canvas.drawLine(var_, var_, var_, var_, this.linePaint);
                        }
                        i20++;
                        i19 = 4;
                        i18 = 3;
                    }
                } else if (gridType2 == GridType.MAJOR && i17 > 0) {
                    int i25 = i5 + dp3;
                    float var_ = ((i12 / 3) * i17) + i25;
                    int i26 = i + dp3;
                    float var_ = i26;
                    float var_ = i26 + i13;
                    canvas.drawLine(var_, var_, var_, var_, this.shadowPaint);
                    canvas.drawLine(var_, var_, var_, var_, this.linePaint);
                    float var_ = i25;
                    float var_ = i26 + ((i13 / 3) * i17);
                    float var_ = i25 + i12;
                    canvas.drawLine(var_, var_, var_, var_, this.shadowPaint);
                    canvas.drawLine(var_, var_, var_, var_, this.linePaint);
                }
                i17++;
            }
            float var_ = i5;
            float var_ = i;
            float var_ = i5 + dp2;
            float var_ = i + dp3;
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            float var_ = i5 + dp3;
            float var_ = i + dp2;
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            float var_ = i15 - dp2;
            float var_ = i15;
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            float var_ = i15 - dp3;
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            float var_ = i16 - dp3;
            float var_ = i16;
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            float var_ = i16 - dp2;
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
            canvas.restore();
        } else {
            float measuredWidth = getMeasuredWidth() - (this.sidePadding * 2.0f);
            float measuredHeight = ((getMeasuredHeight() - this.bottomPadding) - ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight)) - (this.sidePadding * 2.0f);
            int min = (int) Math.min(measuredWidth, measuredHeight);
            Bitmap bitmap = this.circleBitmap;
            if (bitmap == null || bitmap.getWidth() != min) {
                Bitmap bitmap2 = this.circleBitmap;
                boolean z = bitmap2 != null;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                    this.circleBitmap = null;
                }
                try {
                    this.circleBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
                    Canvas canvas2 = new Canvas(this.circleBitmap);
                    float var_ = min;
                    canvas2.drawRect(0.0f, 0.0f, var_, var_, this.dimPaint);
                    canvas2.drawCircle(min / 2, min / 2, min / 2, this.eraserPaint);
                    canvas2.setBitmap(null);
                    if (!z) {
                        this.frameAlpha = 0.0f;
                        this.lastUpdateTime = SystemClock.elapsedRealtime();
                    }
                } catch (Throwable unused) {
                }
            }
            if (this.circleBitmap != null) {
                this.bitmapPaint.setAlpha((int) (this.frameAlpha * 255.0f));
                this.dimPaint.setAlpha((int) (this.frameAlpha * 127.0f));
                float var_ = this.sidePadding;
                float var_ = min;
                float var_ = var_ + ((measuredWidth - var_) / 2.0f);
                float var_ = var_ + ((measuredHeight - var_) / 2.0f) + ((Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight);
                float var_ = var_ + var_;
                float var_ = (int) var_;
                canvas.drawRect(0.0f, 0.0f, getWidth(), var_, this.dimPaint);
                float var_ = (int) var_;
                float var_ = (int) (var_ + var_);
                canvas.drawRect(0.0f, var_, var_, var_, this.dimPaint);
                canvas.drawRect((int) var_, var_, getWidth(), var_, this.dimPaint);
                canvas.drawRect(0.0f, var_, getWidth(), getHeight(), this.dimPaint);
                canvas.drawBitmap(this.circleBitmap, var_, var_, this.bitmapPaint);
            }
        }
        if (this.frameAlpha < 1.0f) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = elapsedRealtime - this.lastUpdateTime;
            if (j > 17) {
                j = 17;
            }
            this.lastUpdateTime = elapsedRealtime;
            float var_ = this.frameAlpha + (((float) j) / 180.0f);
            this.frameAlpha = var_;
            if (var_ > 1.0f) {
                this.frameAlpha = 1.0f;
            }
            invalidate();
        }
    }

    public void updateTouchAreas() {
        int dp = AndroidUtilities.dp(16.0f);
        RectF rectF = this.topLeftCorner;
        RectF rectF2 = this.actualRect;
        float f = rectF2.left;
        float f2 = dp;
        float f3 = rectF2.top;
        rectF.set(f - f2, f3 - f2, f + f2, f3 + f2);
        RectF rectF3 = this.topRightCorner;
        RectF rectF4 = this.actualRect;
        float f4 = rectF4.right;
        float f5 = rectF4.top;
        rectF3.set(f4 - f2, f5 - f2, f4 + f2, f5 + f2);
        RectF rectF5 = this.bottomLeftCorner;
        RectF rectF6 = this.actualRect;
        float f6 = rectF6.left;
        float f7 = rectF6.bottom;
        rectF5.set(f6 - f2, f7 - f2, f6 + f2, f7 + f2);
        RectF rectF7 = this.bottomRightCorner;
        RectF rectF8 = this.actualRect;
        float f8 = rectF8.right;
        float f9 = rectF8.bottom;
        rectF7.set(f8 - f2, f9 - f2, f8 + f2, f9 + f2);
        RectF rectF9 = this.topEdge;
        RectF rectvar_ = this.actualRect;
        float var_ = rectvar_.top;
        rectF9.set(rectvar_.left + f2, var_ - f2, rectvar_.right - f2, var_ + f2);
        RectF rectvar_ = this.leftEdge;
        RectF rectvar_ = this.actualRect;
        float var_ = rectvar_.left;
        rectvar_.set(var_ - f2, rectvar_.top + f2, var_ + f2, rectvar_.bottom - f2);
        RectF rectvar_ = this.rightEdge;
        RectF rectvar_ = this.actualRect;
        float var_ = rectvar_.right;
        rectvar_.set(var_ - f2, rectvar_.top + f2, var_ + f2, rectvar_.bottom - f2);
        RectF rectvar_ = this.bottomEdge;
        RectF rectvar_ = this.actualRect;
        float var_ = rectvar_.bottom;
        rectvar_.set(rectvar_.left + f2, var_ - f2, rectvar_.right - f2, var_ + f2);
    }

    public float getLockAspectRatio() {
        return this.lockAspectRatio;
    }

    public void setLockedAspectRatio(float f) {
        this.lockAspectRatio = f;
    }

    public void setGridType(GridType gridType, boolean z) {
        Animator animator = this.gridAnimator;
        if (animator != null && (!z || this.gridType != gridType)) {
            animator.cancel();
            this.gridAnimator = null;
        }
        GridType gridType2 = this.gridType;
        if (gridType2 == gridType) {
            return;
        }
        this.previousGridType = gridType2;
        this.gridType = gridType;
        GridType gridType3 = GridType.NONE;
        float f = gridType == gridType3 ? 0.0f : 1.0f;
        if (!z) {
            this.gridProgress = f;
            invalidate();
            return;
        }
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "gridProgress", this.gridProgress, f);
        this.gridAnimator = ofFloat;
        ofFloat.setDuration(200L);
        this.gridAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Crop.CropAreaView.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                CropAreaView.this.gridAnimator = null;
            }
        });
        if (gridType == gridType3) {
            this.gridAnimator.setStartDelay(200L);
        }
        this.gridAnimator.start();
    }

    @Keep
    private void setGridProgress(float f) {
        this.gridProgress = f;
        invalidate();
    }

    @Keep
    private float getGridProgress() {
        return this.gridProgress;
    }

    public float getAspectRatio() {
        RectF rectF = this.actualRect;
        return (rectF.right - rectF.left) / (rectF.bottom - rectF.top);
    }

    public void fill(final RectF rectF, Animator animator, boolean z) {
        if (z) {
            Animator animator2 = this.animator;
            if (animator2 != null) {
                animator2.cancel();
                this.animator = null;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.animator = animatorSet;
            animatorSet.setDuration(300L);
            float[] fArr = {rectF.left};
            r0[0].setInterpolator(this.interpolator);
            float[] fArr2 = {rectF.top};
            r0[1].setInterpolator(this.interpolator);
            float[] fArr3 = {rectF.right};
            r0[2].setInterpolator(this.interpolator);
            float[] fArr4 = {rectF.bottom};
            r0[3].setInterpolator(this.interpolator);
            Animator[] animatorArr = {ObjectAnimator.ofFloat(this, "cropLeft", fArr), ObjectAnimator.ofFloat(this, "cropTop", fArr2), ObjectAnimator.ofFloat(this, "cropRight", fArr3), ObjectAnimator.ofFloat(this, "cropBottom", fArr4), animator};
            animatorArr[4].setInterpolator(this.interpolator);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Crop.CropAreaView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator3) {
                    CropAreaView.this.setActualRect(rectF);
                    CropAreaView.this.animator = null;
                }
            });
            animatorSet.start();
            return;
        }
        setActualRect(rectF);
    }

    public void resetAnimator() {
        Animator animator = this.animator;
        if (animator != null) {
            animator.cancel();
            this.animator = null;
        }
    }

    @Keep
    private void setCropLeft(float f) {
        this.actualRect.left = f;
        invalidate();
    }

    @Keep
    public float getCropLeft() {
        return this.actualRect.left;
    }

    @Keep
    private void setCropTop(float f) {
        this.actualRect.top = f;
        invalidate();
    }

    @Keep
    public float getCropTop() {
        return this.actualRect.top;
    }

    @Keep
    private void setCropRight(float f) {
        this.actualRect.right = f;
        invalidate();
    }

    @Keep
    public float getCropRight() {
        return this.actualRect.right;
    }

    @Keep
    private void setCropBottom(float f) {
        this.actualRect.bottom = f;
        invalidate();
    }

    @Keep
    public float getCropBottom() {
        return this.actualRect.bottom;
    }

    public float getCropCenterX() {
        RectF rectF = this.actualRect;
        return (rectF.left + rectF.right) / 2.0f;
    }

    public float getCropCenterY() {
        RectF rectF = this.actualRect;
        return (rectF.top + rectF.bottom) / 2.0f;
    }

    public float getCropWidth() {
        RectF rectF = this.actualRect;
        return rectF.right - rectF.left;
    }

    public float getCropHeight() {
        RectF rectF = this.actualRect;
        return rectF.bottom - rectF.top;
    }

    public RectF getTargetRectToFill() {
        return getTargetRectToFill(getAspectRatio());
    }

    public RectF getTargetRectToFill(float f) {
        calculateRect(this.targetRect, f);
        return this.targetRect;
    }

    public void calculateRect(RectF rectF, float f) {
        float f2;
        float f3;
        float f4;
        float f5;
        float f6 = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
        float measuredHeight = (getMeasuredHeight() - this.bottomPadding) - f6;
        float measuredWidth = getMeasuredWidth() / measuredHeight;
        float min = Math.min(getMeasuredWidth(), measuredHeight) - (this.sidePadding * 2.0f);
        float f7 = this.sidePadding;
        float measuredWidth2 = getMeasuredWidth() - (f7 * 2.0f);
        float f8 = measuredHeight - (f7 * 2.0f);
        float measuredWidth3 = getMeasuredWidth() / 2.0f;
        float f9 = f6 + (measuredHeight / 2.0f);
        if (Math.abs(1.0f - f) < 1.0E-4d) {
            float var_ = min / 2.0f;
            f5 = measuredWidth3 - var_;
            f4 = f9 - var_;
            f2 = measuredWidth3 + var_;
            f3 = f9 + var_;
        } else {
            if (f - measuredWidth <= 1.0E-4d) {
                float var_ = f8 * f;
                if (var_ <= measuredWidth2) {
                    float var_ = var_ / 2.0f;
                    f5 = measuredWidth3 - var_;
                    float var_ = f8 / 2.0f;
                    float var_ = f9 - var_;
                    f2 = measuredWidth3 + var_;
                    f3 = f9 + var_;
                    f4 = var_;
                }
            }
            float var_ = measuredWidth2 / 2.0f;
            float var_ = measuredWidth3 - var_;
            float var_ = (measuredWidth2 / f) / 2.0f;
            float var_ = f9 - var_;
            f2 = measuredWidth3 + var_;
            f3 = f9 + var_;
            f4 = var_;
            f5 = var_;
        }
        rectF.set(f5, f4, f2, f3);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.isDragging) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void updateStatusShow(boolean z) {
        try {
            View decorView = ((Activity) getContext()).getWindow().getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(z ? systemUiVisibility | 4 : systemUiVisibility & (-5));
        } catch (Exception unused) {
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) (motionEvent.getX() - ((ViewGroup) getParent()).getX());
        int y = (int) (motionEvent.getY() - ((ViewGroup) getParent()).getY());
        boolean z = false;
        float f = (Build.VERSION.SDK_INT < 21 || this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            if (this.freeform) {
                float f2 = x;
                float f3 = y;
                if (this.topLeftCorner.contains(f2, f3)) {
                    this.activeControl = Control.TOP_LEFT;
                } else if (this.topRightCorner.contains(f2, f3)) {
                    this.activeControl = Control.TOP_RIGHT;
                } else if (this.bottomLeftCorner.contains(f2, f3)) {
                    this.activeControl = Control.BOTTOM_LEFT;
                } else if (this.bottomRightCorner.contains(f2, f3)) {
                    this.activeControl = Control.BOTTOM_RIGHT;
                } else if (this.leftEdge.contains(f2, f3)) {
                    this.activeControl = Control.LEFT;
                } else if (this.topEdge.contains(f2, f3)) {
                    this.activeControl = Control.TOP;
                } else if (this.rightEdge.contains(f2, f3)) {
                    this.activeControl = Control.RIGHT;
                } else if (this.bottomEdge.contains(f2, f3)) {
                    this.activeControl = Control.BOTTOM;
                } else {
                    this.activeControl = Control.NONE;
                    return false;
                }
                this.previousX = x;
                this.previousY = y;
                setGridType(GridType.MAJOR, false);
                this.isDragging = true;
                updateStatusShow(true);
                AreaViewListener areaViewListener = this.listener;
                if (areaViewListener != null) {
                    areaViewListener.onAreaChangeBegan();
                }
                return true;
            }
            this.activeControl = Control.NONE;
            return false;
        } else if (actionMasked == 1 || actionMasked == 3) {
            this.isDragging = false;
            updateStatusShow(false);
            Control control = this.activeControl;
            Control control2 = Control.NONE;
            if (control == control2) {
                return false;
            }
            this.activeControl = control2;
            AreaViewListener areaViewListener2 = this.listener;
            if (areaViewListener2 != null) {
                areaViewListener2.onAreaChangeEnded();
            }
            return true;
        } else if (actionMasked != 2 || this.activeControl == Control.NONE) {
            return false;
        } else {
            this.tempRect.set(this.actualRect);
            float f4 = x - this.previousX;
            float f5 = y - this.previousY;
            this.previousX = x;
            this.previousY = y;
            if (Math.abs(f4) > Math.abs(f5)) {
                z = true;
            }
            switch (AnonymousClass3.$SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[this.activeControl.ordinal()]) {
                case 1:
                    RectF rectF = this.tempRect;
                    rectF.left += f4;
                    rectF.top += f5;
                    if (this.lockAspectRatio > 0.0f) {
                        float width = rectF.width();
                        float height = this.tempRect.height();
                        if (z) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        RectF rectF2 = this.tempRect;
                        rectF2.left -= rectF2.width() - width;
                        RectF rectF3 = this.tempRect;
                        rectF3.top -= rectF3.width() - height;
                        break;
                    }
                    break;
                case 2:
                    RectF rectF4 = this.tempRect;
                    rectF4.right += f4;
                    rectF4.top += f5;
                    if (this.lockAspectRatio > 0.0f) {
                        float height2 = rectF4.height();
                        if (z) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        RectF rectF5 = this.tempRect;
                        rectF5.top -= rectF5.width() - height2;
                        break;
                    }
                    break;
                case 3:
                    RectF rectF6 = this.tempRect;
                    rectF6.left += f4;
                    rectF6.bottom += f5;
                    if (this.lockAspectRatio > 0.0f) {
                        float width2 = rectF6.width();
                        if (z) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                        } else {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                        }
                        RectF rectF7 = this.tempRect;
                        rectF7.left -= rectF7.width() - width2;
                        break;
                    }
                    break;
                case 4:
                    RectF rectF8 = this.tempRect;
                    rectF8.right += f4;
                    rectF8.bottom += f5;
                    float f6 = this.lockAspectRatio;
                    if (f6 > 0.0f) {
                        if (z) {
                            constrainRectByWidth(rectF8, f6);
                            break;
                        } else {
                            constrainRectByHeight(rectF8, f6);
                            break;
                        }
                    }
                    break;
                case 5:
                    RectF rectF9 = this.tempRect;
                    rectF9.top += f5;
                    float f7 = this.lockAspectRatio;
                    if (f7 > 0.0f) {
                        constrainRectByHeight(rectF9, f7);
                        break;
                    }
                    break;
                case 6:
                    RectF rectvar_ = this.tempRect;
                    rectvar_.left += f4;
                    float f8 = this.lockAspectRatio;
                    if (f8 > 0.0f) {
                        constrainRectByWidth(rectvar_, f8);
                        break;
                    }
                    break;
                case 7:
                    RectF rectvar_ = this.tempRect;
                    rectvar_.right += f4;
                    float f9 = this.lockAspectRatio;
                    if (f9 > 0.0f) {
                        constrainRectByWidth(rectvar_, f9);
                        break;
                    }
                    break;
                case 8:
                    RectF rectvar_ = this.tempRect;
                    rectvar_.bottom += f5;
                    float var_ = this.lockAspectRatio;
                    if (var_ > 0.0f) {
                        constrainRectByHeight(rectvar_, var_);
                        break;
                    }
                    break;
            }
            RectF rectvar_ = this.tempRect;
            float var_ = rectvar_.left;
            float var_ = this.sidePadding;
            if (var_ < var_) {
                float var_ = this.lockAspectRatio;
                if (var_ > 0.0f) {
                    rectvar_.bottom = rectvar_.top + ((rectvar_.right - var_) / var_);
                }
                rectvar_.left = var_;
            } else if (rectvar_.right > getWidth() - this.sidePadding) {
                this.tempRect.right = getWidth() - this.sidePadding;
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectvar_ = this.tempRect;
                    rectvar_.bottom = rectvar_.top + (rectvar_.width() / this.lockAspectRatio);
                }
            }
            float var_ = this.sidePadding;
            float var_ = f + var_;
            float var_ = this.bottomPadding + var_;
            RectF rectvar_ = this.tempRect;
            if (rectvar_.top < var_) {
                float var_ = this.lockAspectRatio;
                if (var_ > 0.0f) {
                    rectvar_.right = rectvar_.left + ((rectvar_.bottom - var_) * var_);
                }
                rectvar_.top = var_;
            } else if (rectvar_.bottom > getHeight() - var_) {
                this.tempRect.bottom = getHeight() - var_;
                if (this.lockAspectRatio > 0.0f) {
                    RectF rectvar_ = this.tempRect;
                    rectvar_.right = rectvar_.left + (rectvar_.height() * this.lockAspectRatio);
                }
            }
            float width3 = this.tempRect.width();
            float var_ = this.minWidth;
            if (width3 < var_) {
                RectF rectvar_ = this.tempRect;
                rectvar_.right = rectvar_.left + var_;
            }
            float height3 = this.tempRect.height();
            float var_ = this.minWidth;
            if (height3 < var_) {
                RectF rectvar_ = this.tempRect;
                rectvar_.bottom = rectvar_.top + var_;
            }
            float var_ = this.lockAspectRatio;
            if (var_ > 0.0f) {
                if (var_ < 1.0f) {
                    float width4 = this.tempRect.width();
                    float var_ = this.minWidth;
                    if (width4 <= var_) {
                        RectF rectvar_ = this.tempRect;
                        rectvar_.right = rectvar_.left + var_;
                        rectvar_.bottom = rectvar_.top + (rectvar_.width() / this.lockAspectRatio);
                    }
                } else {
                    float height4 = this.tempRect.height();
                    float var_ = this.minWidth;
                    if (height4 <= var_) {
                        RectF rectvar_ = this.tempRect;
                        rectvar_.bottom = rectvar_.top + var_;
                        rectvar_.right = rectvar_.left + (rectvar_.height() * this.lockAspectRatio);
                    }
                }
            }
            setActualRect(this.tempRect);
            AreaViewListener areaViewListener3 = this.listener;
            if (areaViewListener3 != null) {
                areaViewListener3.onAreaChange();
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.Components.Crop.CropAreaView$3  reason: invalid class name */
    /* loaded from: classes3.dex */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;

        static {
            int[] iArr = new int[Control.values().length];
            $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control = iArr;
            try {
                iArr[Control.TOP_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.TOP_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.TOP.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.LEFT.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control[Control.BOTTOM.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    private void constrainRectByWidth(RectF rectF, float f) {
        float width = rectF.width();
        rectF.right = rectF.left + width;
        rectF.bottom = rectF.top + (width / f);
    }

    private void constrainRectByHeight(RectF rectF, float f) {
        float height = rectF.height();
        rectF.right = rectF.left + (f * height);
        rectF.bottom = rectF.top + height;
    }

    public void getCropRect(RectF rectF) {
        rectF.set(this.actualRect);
    }
}
