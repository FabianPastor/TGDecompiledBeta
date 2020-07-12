package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

public class CropAreaView extends View {
    private Control activeControl;
    private RectF actualRect = new RectF();
    /* access modifiers changed from: private */
    public Animator animator;
    private Paint bitmapPaint;
    private RectF bottomEdge = new RectF();
    private RectF bottomLeftCorner = new RectF();
    private float bottomPadding;
    private RectF bottomRightCorner = new RectF();
    private Bitmap circleBitmap;
    private Paint dimPaint;
    private boolean dimVisibile = true;
    private Paint eraserPaint;
    private float frameAlpha = 1.0f;
    private Paint framePaint;
    private boolean frameVisible = true;
    private boolean freeform = true;
    /* access modifiers changed from: private */
    public Animator gridAnimator;
    private float gridProgress;
    private GridType gridType = GridType.NONE;
    private Paint handlePaint;
    private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private boolean isDragging;
    private long lastUpdateTime;
    private RectF leftEdge = new RectF();
    private Paint linePaint;
    private AreaViewListener listener;
    private float lockAspectRatio;
    private float minWidth = ((float) AndroidUtilities.dp(32.0f));
    private GridType previousGridType;
    private int previousX;
    private int previousY;
    private RectF rightEdge = new RectF();
    private Paint shadowPaint;
    private float sidePadding = ((float) AndroidUtilities.dp(16.0f));
    private RectF targetRect = new RectF();
    private RectF tempRect = new RectF();
    private RectF topEdge = new RectF();
    private RectF topLeftCorner = new RectF();
    private RectF topRightCorner = new RectF();

    interface AreaViewListener {
        void onAreaChange();

        void onAreaChangeBegan();

        void onAreaChangeEnded();
    }

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

    enum GridType {
        NONE,
        MINOR,
        MAJOR
    }

    public CropAreaView(Context context) {
        super(context);
        Paint paint = new Paint();
        this.dimPaint = paint;
        paint.setColor(NUM);
        Paint paint2 = new Paint();
        this.shadowPaint = paint2;
        paint2.setStyle(Paint.Style.FILL);
        this.shadowPaint.setColor(NUM);
        this.shadowPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        Paint paint3 = new Paint();
        this.linePaint = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
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
    }

    public void setIsVideo(boolean z) {
        this.minWidth = (float) AndroidUtilities.dp(z ? 64.0f : 32.0f);
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
        float f = z ? ((float) i2) / ((float) i) : ((float) i) / ((float) i2);
        if (!this.freeform) {
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

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        if (this.freeform) {
            int dp = AndroidUtilities.dp(2.0f);
            int dp2 = AndroidUtilities.dp(16.0f);
            int dp3 = AndroidUtilities.dp(3.0f);
            RectF rectF = this.actualRect;
            float f = rectF.left;
            int i9 = ((int) f) - dp;
            float f2 = rectF.top;
            int i10 = ((int) f2) - dp;
            int i11 = dp * 2;
            int i12 = ((int) (rectF.right - f)) + i11;
            int i13 = ((int) (rectF.bottom - f2)) + i11;
            if (this.dimVisibile) {
                float f3 = (float) (i10 + dp);
                canvas.drawRect(0.0f, 0.0f, (float) getWidth(), f3, this.dimPaint);
                float f4 = (float) ((i10 + i13) - dp);
                Canvas canvas2 = canvas;
                float f5 = f3;
                float f6 = f4;
                canvas2.drawRect(0.0f, f5, (float) (i9 + dp), f6, this.dimPaint);
                canvas2.drawRect((float) ((i9 + i12) - dp), f5, (float) getWidth(), f6, this.dimPaint);
                canvas.drawRect(0.0f, f4, (float) getWidth(), (float) getHeight(), this.dimPaint);
            }
            if (this.frameVisible) {
                int i14 = dp3 - dp;
                int i15 = dp3 * 2;
                int i16 = i12 - i15;
                int i17 = i13 - i15;
                GridType gridType2 = this.gridType;
                if (gridType2 == GridType.NONE && this.gridProgress > 0.0f) {
                    gridType2 = this.previousGridType;
                }
                this.shadowPaint.setAlpha((int) (this.gridProgress * 26.0f * this.frameAlpha));
                this.linePaint.setAlpha((int) (this.gridProgress * 178.0f * this.frameAlpha));
                this.framePaint.setAlpha((int) (this.frameAlpha * 178.0f));
                this.handlePaint.setAlpha((int) (this.frameAlpha * 255.0f));
                int i18 = 0;
                while (true) {
                    int i19 = 3;
                    if (i18 >= 3) {
                        break;
                    }
                    if (gridType2 == GridType.MINOR) {
                        int i20 = 1;
                        while (i20 < 4) {
                            if (i18 == 2 && i20 == i19) {
                                i6 = dp;
                                i8 = dp2;
                                i7 = i13;
                                i5 = i12;
                            } else {
                                int i21 = i9 + dp3;
                                int i22 = i16 / 3;
                                float f7 = (float) (i21 + ((i22 / 3) * i20) + (i22 * i18));
                                i8 = dp2;
                                int i23 = i10 + dp3;
                                i7 = i13;
                                i6 = dp;
                                i5 = i12;
                                Canvas canvas3 = canvas;
                                float f8 = f7;
                                float f9 = (float) i23;
                                float var_ = f7;
                                float var_ = (float) (i23 + i17);
                                canvas3.drawLine(f8, f9, var_, var_, this.shadowPaint);
                                canvas3.drawLine(f8, f9, var_, var_, this.linePaint);
                                int i24 = i17 / 3;
                                float var_ = (float) (i23 + ((i24 / 3) * i20) + (i24 * i18));
                                float var_ = (float) i21;
                                float var_ = var_;
                                float var_ = (float) (i21 + i16);
                                float var_ = var_;
                                canvas3.drawLine(var_, var_, var_, var_, this.shadowPaint);
                                canvas3.drawLine(var_, var_, var_, var_, this.linePaint);
                            }
                            i20++;
                            dp2 = i8;
                            i13 = i7;
                            dp = i6;
                            i12 = i5;
                            i19 = 3;
                        }
                        i2 = dp;
                        i4 = dp2;
                        i3 = i13;
                        i = i12;
                    } else {
                        i2 = dp;
                        i4 = dp2;
                        i3 = i13;
                        i = i12;
                        if (gridType2 == GridType.MAJOR && i18 > 0) {
                            int i25 = i9 + dp3;
                            float var_ = (float) (((i16 / 3) * i18) + i25);
                            int i26 = i10 + dp3;
                            Canvas canvas4 = canvas;
                            float var_ = var_;
                            float var_ = (float) i26;
                            float var_ = var_;
                            float var_ = (float) (i26 + i17);
                            canvas4.drawLine(var_, var_, var_, var_, this.shadowPaint);
                            canvas4.drawLine(var_, var_, var_, var_, this.linePaint);
                            float var_ = (float) (i26 + ((i17 / 3) * i18));
                            float var_ = (float) i25;
                            float var_ = var_;
                            float var_ = (float) (i25 + i16);
                            float var_ = var_;
                            canvas4.drawLine(var_, var_, var_, var_, this.shadowPaint);
                            canvas4.drawLine(var_, var_, var_, var_, this.linePaint);
                        }
                    }
                    i18++;
                    dp2 = i4;
                    i13 = i3;
                    dp = i2;
                    i12 = i;
                }
                int i27 = dp;
                int i28 = dp2;
                int i29 = i13;
                int i30 = i9 + i14;
                int i31 = i10 + i14;
                float var_ = (float) i31;
                int i32 = i9 + i12;
                int i33 = i32 - i14;
                float var_ = (float) i33;
                Canvas canvas5 = canvas;
                float var_ = (float) i30;
                float var_ = var_;
                canvas5.drawRect(var_, var_, var_, (float) (i31 + i27), this.framePaint);
                int i34 = i10 + i29;
                int i35 = i34 - i14;
                float var_ = (float) i35;
                canvas5.drawRect(var_, var_, (float) (i30 + i27), var_, this.framePaint);
                float var_ = var_;
                canvas5.drawRect(var_, (float) (i35 - i27), var_, var_, this.framePaint);
                canvas5.drawRect((float) (i33 - i27), var_, var_, var_, this.framePaint);
                float var_ = (float) i9;
                float var_ = (float) (i9 + i28);
                float var_ = (float) (i10 + dp3);
                Canvas canvas6 = canvas;
                float var_ = var_;
                float var_ = (float) i10;
                canvas6.drawRect(var_, var_, var_, var_, this.handlePaint);
                float var_ = (float) (i9 + dp3);
                float var_ = (float) (i10 + i28);
                canvas6.drawRect(var_, var_, var_, var_, this.handlePaint);
                float var_ = (float) (i32 - i28);
                float var_ = (float) i32;
                float var_ = var_;
                canvas6.drawRect(var_, var_, var_, var_, this.handlePaint);
                float var_ = (float) (i32 - dp3);
                canvas6.drawRect(var_, var_, var_, var_, this.handlePaint);
                float var_ = (float) (i34 - dp3);
                float var_ = (float) i34;
                float var_ = var_;
                float var_ = var_;
                canvas6.drawRect(var_, var_, var_, var_, this.handlePaint);
                float var_ = (float) (i34 - i28);
                canvas6.drawRect(var_, var_, var_, var_, this.handlePaint);
                canvas.drawRect(var_, var_, var_, var_, this.handlePaint);
                canvas6.drawRect(var_, var_, var_, var_, this.handlePaint);
            } else {
                return;
            }
        } else {
            Bitmap bitmap = this.circleBitmap;
            if (bitmap == null || ((float) bitmap.getWidth()) != this.actualRect.width()) {
                boolean z = this.circleBitmap != null;
                Bitmap bitmap2 = this.circleBitmap;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                    this.circleBitmap = null;
                }
                try {
                    this.circleBitmap = Bitmap.createBitmap((int) this.actualRect.width(), (int) this.actualRect.height(), Bitmap.Config.ARGB_8888);
                    Canvas canvas7 = new Canvas(this.circleBitmap);
                    canvas7.drawRect(0.0f, 0.0f, this.actualRect.width(), this.actualRect.height(), this.dimPaint);
                    canvas7.drawCircle(this.actualRect.width() / 2.0f, this.actualRect.height() / 2.0f, this.actualRect.width() / 2.0f, this.eraserPaint);
                    canvas7.setBitmap((Bitmap) null);
                    if (!z) {
                        this.frameAlpha = 0.0f;
                        this.lastUpdateTime = SystemClock.elapsedRealtime();
                    }
                } catch (Throwable unused) {
                }
            }
            if (this.circleBitmap != null) {
                this.bitmapPaint.setAlpha((int) (this.frameAlpha * 255.0f));
                Canvas canvas8 = canvas;
                canvas8.drawRect(0.0f, 0.0f, (float) getWidth(), (float) ((int) this.actualRect.top), this.dimPaint);
                RectF rectF2 = this.actualRect;
                Canvas canvas9 = canvas;
                canvas9.drawRect(0.0f, (float) ((int) rectF2.top), (float) ((int) rectF2.left), (float) ((int) rectF2.bottom), this.dimPaint);
                RectF rectF3 = this.actualRect;
                canvas8.drawRect((float) ((int) rectF3.right), (float) ((int) rectF3.top), (float) getWidth(), (float) ((int) this.actualRect.bottom), this.dimPaint);
                canvas9.drawRect(0.0f, (float) ((int) this.actualRect.bottom), (float) getWidth(), (float) getHeight(), this.dimPaint);
                Bitmap bitmap3 = this.circleBitmap;
                RectF rectF4 = this.actualRect;
                canvas.drawBitmap(bitmap3, (float) ((int) rectF4.left), (float) ((int) rectF4.top), this.bitmapPaint);
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
        float f2 = (float) dp;
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

    public void setGridType(GridType gridType2, boolean z) {
        if (this.gridAnimator != null && (!z || this.gridType != gridType2)) {
            this.gridAnimator.cancel();
            this.gridAnimator = null;
        }
        GridType gridType3 = this.gridType;
        if (gridType3 != gridType2) {
            this.previousGridType = gridType3;
            this.gridType = gridType2;
            float f = gridType2 == GridType.NONE ? 0.0f : 1.0f;
            if (!z) {
                this.gridProgress = f;
                invalidate();
                return;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "gridProgress", new float[]{this.gridProgress, f});
            this.gridAnimator = ofFloat;
            ofFloat.setDuration(200);
            this.gridAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    Animator unused = CropAreaView.this.gridAnimator = null;
                }
            });
            if (gridType2 == GridType.NONE) {
                this.gridAnimator.setStartDelay(200);
            }
            this.gridAnimator.start();
        }
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

    public void fill(final RectF rectF, Animator animator2, boolean z) {
        if (z) {
            Animator animator3 = this.animator;
            if (animator3 != null) {
                animator3.cancel();
                this.animator = null;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.animator = animatorSet;
            animatorSet.setDuration(300);
            Animator[] animatorArr = new Animator[5];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "cropLeft", new float[]{rectF.left});
            animatorArr[0].setInterpolator(this.interpolator);
            animatorArr[1] = ObjectAnimator.ofFloat(this, "cropTop", new float[]{rectF.top});
            animatorArr[1].setInterpolator(this.interpolator);
            animatorArr[2] = ObjectAnimator.ofFloat(this, "cropRight", new float[]{rectF.right});
            animatorArr[2].setInterpolator(this.interpolator);
            animatorArr[3] = ObjectAnimator.ofFloat(this, "cropBottom", new float[]{rectF.bottom});
            animatorArr[3].setInterpolator(this.interpolator);
            animatorArr[4] = animator2;
            animatorArr[4].setInterpolator(this.interpolator);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CropAreaView.this.setActualRect(rectF);
                    Animator unused = CropAreaView.this.animator = null;
                }
            });
            animatorSet.start();
            return;
        }
        setActualRect(rectF);
    }

    public void resetAnimator() {
        Animator animator2 = this.animator;
        if (animator2 != null) {
            animator2.cancel();
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
        float f6 = (float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float measuredHeight = (((float) getMeasuredHeight()) - this.bottomPadding) - f6;
        float measuredWidth = ((float) getMeasuredWidth()) / measuredHeight;
        float min = Math.min((float) getMeasuredWidth(), measuredHeight) - (this.sidePadding * 2.0f);
        float f7 = this.sidePadding;
        float measuredWidth2 = ((float) getMeasuredWidth()) - (f7 * 2.0f);
        float f8 = measuredHeight - (f7 * 2.0f);
        float measuredWidth3 = ((float) getMeasuredWidth()) / 2.0f;
        float f9 = f6 + (measuredHeight / 2.0f);
        if (((double) Math.abs(1.0f - f)) < 1.0E-4d) {
            float var_ = min / 2.0f;
            f2 = measuredWidth3 - var_;
            f4 = f9 - var_;
            f3 = measuredWidth3 + var_;
            f5 = f9 + var_;
        } else {
            if (((double) (f - measuredWidth)) <= 1.0E-4d) {
                float var_ = f8 * f;
                if (var_ <= measuredWidth2) {
                    float var_ = var_ / 2.0f;
                    f2 = measuredWidth3 - var_;
                    float var_ = f8 / 2.0f;
                    float var_ = f9 - var_;
                    f3 = measuredWidth3 + var_;
                    f5 = f9 + var_;
                    f4 = var_;
                }
            }
            float var_ = measuredWidth2 / 2.0f;
            float var_ = measuredWidth3 - var_;
            float var_ = (measuredWidth2 / f) / 2.0f;
            float var_ = f9 - var_;
            f3 = measuredWidth3 + var_;
            f5 = f9 + var_;
            f4 = var_;
            f2 = var_;
        }
        rectF.set(f2, f4, f3, f5);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) (motionEvent.getX() - ((ViewGroup) getParent()).getX());
        int y = (int) (motionEvent.getY() - ((ViewGroup) getParent()).getY());
        boolean z = false;
        float f = (float) (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            if (this.freeform) {
                float f2 = (float) x;
                float f3 = (float) y;
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
            float f4 = (float) (x - this.previousX);
            float f5 = (float) (y - this.previousY);
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
                        if (!z) {
                            constrainRectByHeight(rectF8, f6);
                            break;
                        } else {
                            constrainRectByWidth(rectF8, f6);
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
                this.tempRect.left = this.sidePadding;
            } else if (rectvar_.right > ((float) getWidth()) - this.sidePadding) {
                this.tempRect.right = ((float) getWidth()) - this.sidePadding;
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
                this.tempRect.top = var_;
            } else if (rectvar_.bottom > ((float) getHeight()) - var_) {
                this.tempRect.bottom = ((float) getHeight()) - var_;
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
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control;

        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|18) */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                org.telegram.ui.Components.Crop.CropAreaView$Control[] r0 = org.telegram.ui.Components.Crop.CropAreaView.Control.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control = r0
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.TOP_LEFT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control     // Catch:{ NoSuchFieldError -> 0x001d }
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.TOP_RIGHT     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.BOTTOM_LEFT     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control     // Catch:{ NoSuchFieldError -> 0x0033 }
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.BOTTOM_RIGHT     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control     // Catch:{ NoSuchFieldError -> 0x003e }
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.TOP     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control     // Catch:{ NoSuchFieldError -> 0x0049 }
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.LEFT     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control     // Catch:{ NoSuchFieldError -> 0x0054 }
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.RIGHT     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$Crop$CropAreaView$Control     // Catch:{ NoSuchFieldError -> 0x0060 }
                org.telegram.ui.Components.Crop.CropAreaView$Control r1 = org.telegram.ui.Components.Crop.CropAreaView.Control.BOTTOM     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Crop.CropAreaView.AnonymousClass3.<clinit>():void");
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
