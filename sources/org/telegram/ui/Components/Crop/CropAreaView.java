package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import org.telegram.messenger.AndroidUtilities;

public class CropAreaView extends View {
    private Control activeControl;
    private RectF actualRect = new RectF();
    private Animator animator;
    private RectF bottomEdge = new RectF();
    private RectF bottomLeftCorner = new RectF();
    private float bottomPadding;
    private RectF bottomRightCorner = new RectF();
    Paint dimPaint = new Paint();
    private boolean dimVisibile = true;
    Paint framePaint;
    private boolean frameVisible = true;
    private Animator gridAnimator;
    private float gridProgress;
    private GridType gridType = GridType.NONE;
    Paint handlePaint;
    AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private boolean isDragging;
    private RectF leftEdge = new RectF();
    Paint linePaint;
    private AreaViewListener listener;
    private float lockAspectRatio;
    private float minWidth = ((float) AndroidUtilities.dp(32.0f));
    private GridType previousGridType;
    private int previousX;
    private int previousY;
    private RectF rightEdge = new RectF();
    Paint shadowPaint;
    private float sidePadding = ((float) AndroidUtilities.dp(16.0f));
    private RectF tempRect = new RectF();
    private RectF topEdge = new RectF();
    private RectF topLeftCorner = new RectF();
    private RectF topRightCorner = new RectF();

    /* renamed from: org.telegram.ui.Components.Crop.CropAreaView$1 */
    class C11171 extends AnimatorListenerAdapter {
        C11171() {
        }

        public void onAnimationEnd(Animator animator) {
            CropAreaView.this.gridAnimator = null;
        }
    }

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
        this.dimPaint.setColor(-872415232);
        this.shadowPaint = new Paint();
        this.shadowPaint.setStyle(Style.FILL);
        this.shadowPaint.setColor(436207616);
        this.shadowPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.linePaint = new Paint();
        this.linePaint.setStyle(Style.FILL);
        this.linePaint.setColor(-1);
        this.linePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.handlePaint = new Paint();
        this.handlePaint.setStyle(Style.FILL);
        this.handlePaint.setColor(-1);
        this.framePaint = new Paint();
        this.framePaint.setStyle(Style.FILL);
        this.framePaint.setColor(-NUM);
    }

    public boolean isDragging() {
        return this.isDragging;
    }

    public void setDimVisibility(boolean z) {
        this.dimVisibile = z;
    }

    public void setFrameVisibility(boolean z) {
        this.frameVisible = z;
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

    public void setBitmap(Bitmap bitmap, boolean z, boolean z2) {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                if (z) {
                    z = ((float) bitmap.getHeight()) / ((float) bitmap.getWidth());
                } else {
                    z = ((float) bitmap.getWidth()) / ((float) bitmap.getHeight());
                }
                float f = NUM;
                if (z2) {
                    f = z;
                } else {
                    this.lockAspectRatio = 1.0f;
                }
                setActualRect(f);
            }
        }
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

    protected void onDraw(Canvas canvas) {
        int dp = AndroidUtilities.dp(2.0f);
        int dp2 = AndroidUtilities.dp(16.0f);
        int dp3 = AndroidUtilities.dp(3.0f);
        int i = ((int) this.actualRect.left) - dp;
        int i2 = ((int) this.actualRect.top) - dp;
        int i3 = dp * 2;
        int i4 = ((int) (this.actualRect.right - this.actualRect.left)) + i3;
        int i5 = ((int) (this.actualRect.bottom - this.actualRect.top)) + i3;
        if (this.dimVisibile) {
            float f = (float) (i2 + dp);
            Canvas canvas2 = canvas;
            canvas2.drawRect(0.0f, 0.0f, (float) getWidth(), f, r0.dimPaint);
            Canvas canvas3 = canvas;
            float f2 = f;
            float f3 = (float) ((i2 + i5) - dp);
            canvas3.drawRect(0.0f, f2, (float) (i + dp), f3, r0.dimPaint);
            canvas3.drawRect((float) ((i + i4) - dp), f2, (float) getWidth(), f3, r0.dimPaint);
            canvas.drawRect(0.0f, f3, (float) getWidth(), (float) getHeight(), r0.dimPaint);
        }
        if (r0.frameVisible) {
            i3 = dp3 - dp;
            int i6 = dp3 * 2;
            int i7 = i4 - i6;
            i6 = i5 - i6;
            GridType gridType = r0.gridType;
            if (gridType == GridType.NONE && r0.gridProgress > 0.0f) {
                gridType = r0.previousGridType;
            }
            r0.shadowPaint.setAlpha((int) (r0.gridProgress * 26.0f));
            r0.linePaint.setAlpha((int) (r0.gridProgress * 178.0f));
            int i8 = 0;
            while (true) {
                int i9 = 3;
                int i10;
                int i11;
                int i12;
                int i13;
                int i14;
                float f4;
                float f5;
                float f6;
                float f7;
                float f8;
                float f9;
                float f10;
                if (i8 < 3) {
                    float f11;
                    float f12;
                    if (gridType == GridType.MINOR) {
                        int i15 = 1;
                        while (i15 < 4) {
                            if (i8 == 2 && i15 == r13) {
                                i10 = dp;
                                i11 = dp2;
                                i12 = i4;
                                i13 = i5;
                            } else {
                                i14 = i + dp3;
                                int i16 = i7 / 3;
                                f4 = (float) ((i14 + ((i16 / 3) * i15)) + (i16 * i8));
                                i11 = dp2;
                                dp2 = i2 + dp3;
                                i13 = i5;
                                i10 = dp;
                                i12 = i4;
                                Canvas canvas4 = canvas;
                                f5 = f4;
                                f11 = (float) dp2;
                                f6 = f4;
                                float f13 = (float) (dp2 + i6);
                                canvas4.drawLine(f5, f11, f6, f13, r0.shadowPaint);
                                canvas4.drawLine(f5, f11, f6, f13, r0.linePaint);
                                i4 = i6 / 3;
                                f12 = (float) ((dp2 + ((i4 / 3) * i15)) + (i4 * i8));
                                f5 = (float) i14;
                                f11 = f12;
                                f6 = (float) (i14 + i7);
                                f13 = f12;
                                canvas4.drawLine(f5, f11, f6, f13, r0.shadowPaint);
                                canvas4.drawLine(f5, f11, f6, f13, r0.linePaint);
                            }
                            i15++;
                            dp2 = i11;
                            i5 = i13;
                            dp = i10;
                            i4 = i12;
                            i9 = 3;
                        }
                        i10 = dp;
                        i11 = dp2;
                        i12 = i4;
                        i13 = i5;
                    } else {
                        i10 = dp;
                        i11 = dp2;
                        i12 = i4;
                        i13 = i5;
                        if (gridType == GridType.MAJOR && i8 > 0) {
                            dp = i + dp3;
                            f12 = (float) (((i7 / 3) * i8) + dp);
                            i4 = i2 + dp3;
                            f7 = (float) i4;
                            f2 = (float) (i4 + i6);
                            canvas3 = canvas;
                            f8 = f12;
                            float f14 = f12;
                            f3 = f2;
                            canvas3.drawLine(f8, f7, f14, f3, r0.shadowPaint);
                            canvas3.drawLine(f8, f7, f14, f3, r0.linePaint);
                            f12 = (float) dp;
                            f9 = (float) (i4 + ((i6 / 3) * i8));
                            Canvas canvas5 = canvas;
                            f10 = f12;
                            float f15 = f9;
                            f5 = (float) (dp + i7);
                            f11 = f9;
                            canvas5.drawLine(f10, f15, f5, f11, r0.shadowPaint);
                            canvas5.drawLine(f10, f15, f5, f11, r0.linePaint);
                        }
                    }
                    i8++;
                    dp2 = i11;
                    i5 = i13;
                    dp = i10;
                    i4 = i12;
                } else {
                    i10 = dp;
                    i11 = dp2;
                    i12 = i4;
                    i13 = i5;
                    dp = i + i3;
                    i4 = i2 + i3;
                    f8 = (float) i4;
                    i14 = i + i12;
                    i9 = i14 - i3;
                    float f16 = (float) i9;
                    f9 = (float) (i4 + i10);
                    Canvas canvas6 = canvas;
                    float f17 = (float) dp;
                    float f18 = f8;
                    float f19 = f16;
                    f3 = f16;
                    f16 = f9;
                    i4 = i9;
                    canvas6.drawRect(f17, f18, f19, f16, r0.framePaint);
                    f19 = (float) (dp + i10);
                    dp = i2 + i13;
                    i3 = dp - i3;
                    f4 = (float) i3;
                    float f20 = f4;
                    canvas6.drawRect(f17, f18, f19, f4, r0.framePaint);
                    f19 = f3;
                    f16 = f20;
                    canvas6.drawRect(f17, (float) (i3 - i10), f19, f16, r0.framePaint);
                    canvas6.drawRect((float) (i4 - i10), f8, f19, f16, r0.framePaint);
                    f16 = (float) i2;
                    f4 = (float) (i + i11);
                    f18 = (float) (i2 + dp3);
                    Canvas canvas7 = canvas;
                    float f21 = (float) i;
                    f10 = f16;
                    canvas7.drawRect(f21, f10, f4, f18, r0.handlePaint);
                    float f22 = (float) (i + dp3);
                    float f23 = (float) (i2 + i11);
                    canvas7.drawRect(f21, f10, f22, f23, r0.handlePaint);
                    f8 = (float) (i14 - i11);
                    f19 = (float) i14;
                    Canvas canvas8 = canvas;
                    f7 = f16;
                    f6 = f19;
                    canvas8.drawRect(f8, f7, f19, f18, r0.handlePaint);
                    f2 = (float) (i14 - dp3);
                    canvas8.drawRect(f2, f7, f6, f23, r0.handlePaint);
                    float f24 = (float) (dp - dp3);
                    f23 = (float) dp;
                    f5 = f23;
                    canvas7.drawRect(f21, f24, f4, f5, r0.handlePaint);
                    float f25 = (float) (dp - i11);
                    canvas7.drawRect(f21, f25, f22, f5, r0.handlePaint);
                    Canvas canvas9 = canvas;
                    float f26 = f6;
                    f21 = f23;
                    canvas9.drawRect(f8, f24, f26, f21, r0.handlePaint);
                    canvas9.drawRect(f2, f25, f26, f21, r0.handlePaint);
                    return;
                }
            }
        }
    }

    private void updateTouchAreas() {
        float dp = (float) AndroidUtilities.dp(16.0f);
        this.topLeftCorner.set(this.actualRect.left - dp, this.actualRect.top - dp, this.actualRect.left + dp, this.actualRect.top + dp);
        this.topRightCorner.set(this.actualRect.right - dp, this.actualRect.top - dp, this.actualRect.right + dp, this.actualRect.top + dp);
        this.bottomLeftCorner.set(this.actualRect.left - dp, this.actualRect.bottom - dp, this.actualRect.left + dp, this.actualRect.bottom + dp);
        this.bottomRightCorner.set(this.actualRect.right - dp, this.actualRect.bottom - dp, this.actualRect.right + dp, this.actualRect.bottom + dp);
        this.topEdge.set(this.actualRect.left + dp, this.actualRect.top - dp, this.actualRect.right - dp, this.actualRect.top + dp);
        this.leftEdge.set(this.actualRect.left - dp, this.actualRect.top + dp, this.actualRect.left + dp, this.actualRect.bottom - dp);
        this.rightEdge.set(this.actualRect.right - dp, this.actualRect.top + dp, this.actualRect.right + dp, this.actualRect.bottom - dp);
        this.bottomEdge.set(this.actualRect.left + dp, this.actualRect.bottom - dp, this.actualRect.right - dp, this.actualRect.bottom + dp);
    }

    public float getLockAspectRatio() {
        return this.lockAspectRatio;
    }

    public void setLockedAspectRatio(float f) {
        this.lockAspectRatio = f;
    }

    public void setGridType(GridType gridType, boolean z) {
        if (!(this.gridAnimator == null || (z && this.gridType == gridType))) {
            this.gridAnimator.cancel();
            this.gridAnimator = null;
        }
        if (this.gridType != gridType) {
            this.previousGridType = this.gridType;
            this.gridType = gridType;
            float f = gridType == GridType.NONE ? 0.0f : 1.0f;
            if (z) {
                this.gridAnimator = ObjectAnimator.ofFloat(this, "gridProgress", new float[]{this.gridProgress, f});
                this.gridAnimator.setDuration(200);
                this.gridAnimator.addListener(new C11171());
                if (gridType == GridType.NONE) {
                    this.gridAnimator.setStartDelay(200);
                }
                this.gridAnimator.start();
            } else {
                this.gridProgress = f;
                invalidate();
            }
        }
    }

    private void setGridProgress(float f) {
        this.gridProgress = f;
        invalidate();
    }

    private float getGridProgress() {
        return this.gridProgress;
    }

    public float getAspectRatio() {
        return (this.actualRect.right - this.actualRect.left) / (this.actualRect.bottom - this.actualRect.top);
    }

    public void fill(final RectF rectF, Animator animator, boolean z) {
        if (z) {
            if (this.animator) {
                this.animator.cancel();
                this.animator = false;
            }
            z = new AnimatorSet();
            this.animator = z;
            z.setDuration(300);
            Animator[] animatorArr = new Animator[5];
            animatorArr[0] = ObjectAnimator.ofFloat(this, "cropLeft", new float[]{rectF.left});
            animatorArr[0].setInterpolator(this.interpolator);
            animatorArr[1] = ObjectAnimator.ofFloat(this, "cropTop", new float[]{rectF.top});
            animatorArr[1].setInterpolator(this.interpolator);
            animatorArr[2] = ObjectAnimator.ofFloat(this, "cropRight", new float[]{rectF.right});
            animatorArr[2].setInterpolator(this.interpolator);
            animatorArr[3] = ObjectAnimator.ofFloat(this, "cropBottom", new float[]{rectF.bottom});
            animatorArr[3].setInterpolator(this.interpolator);
            animatorArr[4] = animator;
            animatorArr[4].setInterpolator(this.interpolator);
            z.playTogether(animatorArr);
            z.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    CropAreaView.this.setActualRect(rectF);
                    CropAreaView.this.animator = null;
                }
            });
            z.start();
            return;
        }
        setActualRect(rectF);
    }

    public void resetAnimator() {
        if (this.animator != null) {
            this.animator.cancel();
            this.animator = null;
        }
    }

    private void setCropLeft(float f) {
        this.actualRect.left = f;
        invalidate();
    }

    public float getCropLeft() {
        return this.actualRect.left;
    }

    private void setCropTop(float f) {
        this.actualRect.top = f;
        invalidate();
    }

    public float getCropTop() {
        return this.actualRect.top;
    }

    private void setCropRight(float f) {
        this.actualRect.right = f;
        invalidate();
    }

    public float getCropRight() {
        return this.actualRect.right;
    }

    private void setCropBottom(float f) {
        this.actualRect.bottom = f;
        invalidate();
    }

    public float getCropBottom() {
        return this.actualRect.bottom;
    }

    public float getCropCenterX() {
        return this.actualRect.left + ((this.actualRect.right - this.actualRect.left) / 2.0f);
    }

    public float getCropCenterY() {
        return this.actualRect.top + ((this.actualRect.bottom - this.actualRect.top) / 2.0f);
    }

    public float getCropWidth() {
        return this.actualRect.right - this.actualRect.left;
    }

    public float getCropHeight() {
        return this.actualRect.bottom - this.actualRect.top;
    }

    public RectF getTargetRectToFill() {
        RectF rectF = new RectF();
        calculateRect(rectF, getAspectRatio());
        return rectF;
    }

    public void calculateRect(RectF rectF, float f) {
        float f2 = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        float measuredHeight = (((float) getMeasuredHeight()) - this.bottomPadding) - f2;
        float measuredWidth = ((float) getMeasuredWidth()) / measuredHeight;
        float min = Math.min((float) getMeasuredWidth(), measuredHeight) - (this.sidePadding * 2.0f);
        float measuredWidth2 = ((float) getMeasuredWidth()) - (this.sidePadding * 2.0f);
        float f3 = measuredHeight - (this.sidePadding * 2.0f);
        float measuredWidth3 = ((float) getMeasuredWidth()) / 2.0f;
        f2 += measuredHeight / 2.0f;
        if (((double) Math.abs(1.0f - f)) < 1.0E-4d) {
            min /= 2.0f;
            f = measuredWidth3 - min;
            measuredHeight = f2 - min;
            measuredWidth3 += min;
            f2 += min;
        } else if (f > measuredWidth) {
            measuredHeight = measuredWidth2 / 2.0f;
            measuredWidth = measuredWidth3 - measuredHeight;
            measuredWidth2 = (measuredWidth2 / f) / 2.0f;
            f = f2 - measuredWidth2;
            measuredWidth3 += measuredHeight;
            f2 += measuredWidth2;
            measuredHeight = f;
            f = measuredWidth;
        } else {
            f = (f * f3) / 2.0f;
            measuredHeight = measuredWidth3 - f;
            f3 /= 2.0f;
            measuredWidth = f2 - f3;
            measuredWidth3 += f;
            f2 += f3;
            f = measuredHeight;
            measuredHeight = measuredWidth;
        }
        rectF.set(f, measuredHeight, measuredWidth3, f2);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) (motionEvent.getX() - ((ViewGroup) getParent()).getX());
        int y = (int) (motionEvent.getY() - ((ViewGroup) getParent()).getY());
        float f = (float) (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        motionEvent = motionEvent.getActionMasked();
        float f2;
        if (motionEvent == null) {
            f = (float) x;
            f2 = (float) y;
            if (this.topLeftCorner.contains(f, f2) != null) {
                this.activeControl = Control.TOP_LEFT;
            } else if (this.topRightCorner.contains(f, f2) != null) {
                this.activeControl = Control.TOP_RIGHT;
            } else if (this.bottomLeftCorner.contains(f, f2) != null) {
                this.activeControl = Control.BOTTOM_LEFT;
            } else if (this.bottomRightCorner.contains(f, f2) != null) {
                this.activeControl = Control.BOTTOM_RIGHT;
            } else if (this.leftEdge.contains(f, f2) != null) {
                this.activeControl = Control.LEFT;
            } else if (this.topEdge.contains(f, f2) != null) {
                this.activeControl = Control.TOP;
            } else if (this.rightEdge.contains(f, f2) != null) {
                this.activeControl = Control.RIGHT;
            } else if (this.bottomEdge.contains(f, f2) != null) {
                this.activeControl = Control.BOTTOM;
            } else {
                this.activeControl = Control.NONE;
                return false;
            }
            this.previousX = x;
            this.previousY = y;
            setGridType(GridType.MAJOR, false);
            this.isDragging = true;
            if (this.listener != null) {
                this.listener.onAreaChangeBegan();
            }
            return true;
        }
        if (motionEvent != 1) {
            if (motionEvent != 3) {
                if (motionEvent != 2 || this.activeControl == Control.NONE) {
                    return false;
                }
                this.tempRect.set(this.actualRect);
                motionEvent = (float) (x - this.previousX);
                float f3 = (float) (y - this.previousY);
                this.previousX = x;
                this.previousY = y;
                RectF rectF;
                float width;
                switch (this.activeControl) {
                    case TOP_LEFT:
                        rectF = this.tempRect;
                        rectF.left += motionEvent;
                        rectF = this.tempRect;
                        rectF.top += f3;
                        if (this.lockAspectRatio > 0.0f) {
                            width = this.tempRect.width();
                            f2 = this.tempRect.height();
                            if (Math.abs(motionEvent) > Math.abs(f3)) {
                                constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                            } else {
                                constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                            }
                            motionEvent = this.tempRect;
                            motionEvent.left -= this.tempRect.width() - width;
                            motionEvent = this.tempRect;
                            motionEvent.top -= this.tempRect.width() - f2;
                            break;
                        }
                        break;
                    case TOP_RIGHT:
                        rectF = this.tempRect;
                        rectF.right += motionEvent;
                        rectF = this.tempRect;
                        rectF.top += f3;
                        if (this.lockAspectRatio > 0.0f) {
                            width = this.tempRect.height();
                            if (Math.abs(motionEvent) > Math.abs(f3)) {
                                constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                            } else {
                                constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                            }
                            motionEvent = this.tempRect;
                            motionEvent.top -= this.tempRect.width() - width;
                            break;
                        }
                        break;
                    case BOTTOM_LEFT:
                        rectF = this.tempRect;
                        rectF.left += motionEvent;
                        rectF = this.tempRect;
                        rectF.bottom += f3;
                        if (this.lockAspectRatio > 0.0f) {
                            width = this.tempRect.width();
                            if (Math.abs(motionEvent) > Math.abs(f3)) {
                                constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                            } else {
                                constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                            }
                            motionEvent = this.tempRect;
                            motionEvent.left -= this.tempRect.width() - width;
                            break;
                        }
                        break;
                    case BOTTOM_RIGHT:
                        rectF = this.tempRect;
                        rectF.right += motionEvent;
                        rectF = this.tempRect;
                        rectF.bottom += f3;
                        if (this.lockAspectRatio > 0.0f) {
                            if (Math.abs(motionEvent) <= Math.abs(f3)) {
                                constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                                break;
                            }
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                            break;
                        }
                        break;
                    case TOP:
                        motionEvent = this.tempRect;
                        motionEvent.top += f3;
                        if (this.lockAspectRatio > null) {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                            break;
                        }
                        break;
                    case LEFT:
                        rectF = this.tempRect;
                        rectF.left += motionEvent;
                        if (this.lockAspectRatio > null) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                            break;
                        }
                        break;
                    case RIGHT:
                        rectF = this.tempRect;
                        rectF.right += motionEvent;
                        if (this.lockAspectRatio > null) {
                            constrainRectByWidth(this.tempRect, this.lockAspectRatio);
                            break;
                        }
                        break;
                    case BOTTOM:
                        motionEvent = this.tempRect;
                        motionEvent.bottom += f3;
                        if (this.lockAspectRatio > null) {
                            constrainRectByHeight(this.tempRect, this.lockAspectRatio);
                            break;
                        }
                        break;
                    default:
                        break;
                }
                if (this.tempRect.left < this.sidePadding) {
                    if (this.lockAspectRatio > null) {
                        this.tempRect.bottom = this.tempRect.top + ((this.tempRect.right - this.sidePadding) / this.lockAspectRatio);
                    }
                    this.tempRect.left = this.sidePadding;
                } else if (this.tempRect.right > ((float) getWidth()) - this.sidePadding) {
                    this.tempRect.right = ((float) getWidth()) - this.sidePadding;
                    if (this.lockAspectRatio > null) {
                        this.tempRect.bottom = this.tempRect.top + (this.tempRect.width() / this.lockAspectRatio);
                    }
                }
                f += this.sidePadding;
                motionEvent = this.bottomPadding + this.sidePadding;
                if (this.tempRect.top < f) {
                    if (this.lockAspectRatio > null) {
                        this.tempRect.right = this.tempRect.left + ((this.tempRect.bottom - f) * this.lockAspectRatio);
                    }
                    this.tempRect.top = f;
                } else if (this.tempRect.bottom > ((float) getHeight()) - motionEvent) {
                    this.tempRect.bottom = ((float) getHeight()) - motionEvent;
                    if (this.lockAspectRatio > null) {
                        this.tempRect.right = this.tempRect.left + (this.tempRect.height() * this.lockAspectRatio);
                    }
                }
                if (this.tempRect.width() < this.minWidth) {
                    this.tempRect.right = this.tempRect.left + this.minWidth;
                }
                if (this.tempRect.height() < this.minWidth) {
                    this.tempRect.bottom = this.tempRect.top + this.minWidth;
                }
                if (this.lockAspectRatio > null) {
                    if (this.lockAspectRatio < 1.0f) {
                        if (this.tempRect.width() <= this.minWidth) {
                            this.tempRect.right = this.tempRect.left + this.minWidth;
                            this.tempRect.bottom = this.tempRect.top + (this.tempRect.width() / this.lockAspectRatio);
                        }
                    } else if (this.tempRect.height() <= this.minWidth) {
                        this.tempRect.bottom = this.tempRect.top + this.minWidth;
                        this.tempRect.right = this.tempRect.left + (this.tempRect.height() * this.lockAspectRatio);
                    }
                }
                setActualRect(this.tempRect);
                if (this.listener != null) {
                    this.listener.onAreaChange();
                }
                return true;
            }
        }
        this.isDragging = false;
        if (this.activeControl == Control.NONE) {
            return false;
        }
        this.activeControl = Control.NONE;
        if (this.listener != null) {
            this.listener.onAreaChangeEnded();
        }
        return true;
    }

    private void constrainRectByWidth(RectF rectF, float f) {
        float width = rectF.width();
        f = width / f;
        rectF.right = rectF.left + width;
        rectF.bottom = rectF.top + f;
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
