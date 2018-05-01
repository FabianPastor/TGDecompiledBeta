package org.telegram.ui.Components;

import android.content.Context;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class Scroller {
    private static float DECELERATION_RATE = ((float) (Math.log(0.75d) / Math.log(0.9d)));
    private static final int DEFAULT_DURATION = 250;
    private static float END_TENSION = (1.0f - START_TENSION);
    private static final int FLING_MODE = 1;
    private static final int NB_SAMPLES = 100;
    private static final int SCROLL_MODE = 0;
    private static final float[] SPLINE = new float[101];
    private static float START_TENSION = 0.4f;
    private static float sViscousFluidNormalize = (1.0f / viscousFluid(1.0f));
    private static float sViscousFluidScale = 8.0f;
    private int mCurrX;
    private int mCurrY;
    private float mDeceleration;
    private float mDeltaX;
    private float mDeltaY;
    private int mDuration;
    private float mDurationReciprocal;
    private int mFinalX;
    private int mFinalY;
    private boolean mFinished;
    private boolean mFlywheel;
    private Interpolator mInterpolator;
    private int mMaxX;
    private int mMaxY;
    private int mMinX;
    private int mMinY;
    private int mMode;
    private final float mPpi;
    private long mStartTime;
    private int mStartX;
    private int mStartY;
    private float mVelocity;

    static {
        float f = 0.0f;
        for (int i = 0; i <= NB_SAMPLES; i++) {
            float f2;
            float f3;
            float f4 = ((float) i) / 100.0f;
            float f5 = 1.0f;
            while (true) {
                float f6 = ((f5 - f) / 2.0f) + f;
                float f7 = 1.0f - f6;
                f2 = (3.0f * f6) * f7;
                f3 = (f6 * f6) * f6;
                f7 = (((f7 * START_TENSION) + (END_TENSION * f6)) * f2) + f3;
                if (((double) Math.abs(f7 - f4)) < 1.0E-5d) {
                    break;
                } else if (f7 > f4) {
                    f5 = f6;
                } else {
                    f = f6;
                }
            }
            SPLINE[i] = f2 + f3;
        }
        SPLINE[NB_SAMPLES] = 1.0f;
    }

    public Scroller(Context context) {
        this(context, null);
    }

    public Scroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public Scroller(Context context, Interpolator interpolator, boolean z) {
        this.mFinished = true;
        this.mInterpolator = interpolator;
        this.mPpi = context.getResources().getDisplayMetrics().density * NUM;
        this.mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
        this.mFlywheel = z;
    }

    public final void setFriction(float f) {
        this.mDeceleration = computeDeceleration(f);
    }

    private float computeDeceleration(float f) {
        return (386.0878f * this.mPpi) * f;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final void forceFinished(boolean z) {
        this.mFinished = z;
    }

    public final int getDuration() {
        return this.mDuration;
    }

    public final int getCurrX() {
        return this.mCurrX;
    }

    public final int getCurrY() {
        return this.mCurrY;
    }

    public float getCurrVelocity() {
        return this.mVelocity - ((this.mDeceleration * ((float) timePassed())) / 2000.0f);
    }

    public final int getStartX() {
        return this.mStartX;
    }

    public final int getStartY() {
        return this.mStartY;
    }

    public final int getFinalX() {
        return this.mFinalX;
    }

    public final int getFinalY() {
        return this.mFinalY;
    }

    public boolean computeScrollOffset() {
        if (this.mFinished) {
            return false;
        }
        int currentAnimationTimeMillis = (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
        if (currentAnimationTimeMillis < this.mDuration) {
            float f;
            switch (this.mMode) {
                case 0:
                    f = ((float) currentAnimationTimeMillis) * this.mDurationReciprocal;
                    if (this.mInterpolator == null) {
                        f = viscousFluid(f);
                    } else {
                        f = this.mInterpolator.getInterpolation(f);
                    }
                    this.mCurrX = this.mStartX + Math.round(this.mDeltaX * f);
                    this.mCurrY = this.mStartY + Math.round(f * this.mDeltaY);
                    break;
                case 1:
                    f = ((float) currentAnimationTimeMillis) / ((float) this.mDuration);
                    int i = (int) (100.0f * f);
                    float f2 = ((float) i) / 100.0f;
                    int i2 = i + 1;
                    float f3 = ((float) i2) / 100.0f;
                    float f4 = SPLINE[i];
                    f4 += ((f - f2) / (f3 - f2)) * (SPLINE[i2] - f4);
                    this.mCurrX = this.mStartX + Math.round(((float) (this.mFinalX - this.mStartX)) * f4);
                    this.mCurrX = Math.min(this.mCurrX, this.mMaxX);
                    this.mCurrX = Math.max(this.mCurrX, this.mMinX);
                    this.mCurrY = this.mStartY + Math.round(f4 * ((float) (this.mFinalY - this.mStartY)));
                    this.mCurrY = Math.min(this.mCurrY, this.mMaxY);
                    this.mCurrY = Math.max(this.mCurrY, this.mMinY);
                    if (this.mCurrX == this.mFinalX && this.mCurrY == this.mFinalY) {
                        this.mFinished = true;
                        break;
                    }
                default:
                    break;
            }
        }
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
        return true;
    }

    public void startScroll(int i, int i2, int i3, int i4) {
        startScroll(i, i2, i3, i4, 250);
    }

    public void startScroll(int i, int i2, int i3, int i4, int i5) {
        this.mMode = 0;
        this.mFinished = false;
        this.mDuration = i5;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = i;
        this.mStartY = i2;
        this.mFinalX = i + i3;
        this.mFinalY = i2 + i4;
        this.mDeltaX = (float) i3;
        this.mDeltaY = (float) i4;
        this.mDurationReciprocal = NUM / ((float) this.mDuration);
    }

    public void fling(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        int i9;
        float currVelocity;
        float f;
        int i10;
        double log;
        float f2;
        int exp;
        int i11 = i;
        int i12 = i2;
        if (!this.mFlywheel || r0.mFinished) {
            i9 = i3;
        } else {
            currVelocity = getCurrVelocity();
            f = (float) (r0.mFinalX - r0.mStartX);
            float f3 = (float) (r0.mFinalY - r0.mStartY);
            float sqrt = (float) Math.sqrt((double) ((f * f) + (f3 * f3)));
            f = (f / sqrt) * currVelocity;
            f3 = (f3 / sqrt) * currVelocity;
            i9 = i3;
            sqrt = (float) i9;
            if (Math.signum(sqrt) == Math.signum(f)) {
                i10 = i4;
                float f4 = (float) i10;
                if (Math.signum(f4) == Math.signum(f3)) {
                    i9 = (int) (sqrt + f);
                    i10 = (int) (f4 + f3);
                }
                r0.mMode = 1;
                r0.mFinished = false;
                f = (float) Math.sqrt((double) ((i9 * i9) + (i10 * i10)));
                r0.mVelocity = f;
                log = Math.log((double) ((START_TENSION * f) / 800.0f));
                r0.mDuration = (int) (1000.0d * Math.exp(log / (((double) DECELERATION_RATE) - 1.0d)));
                r0.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                r0.mStartX = i11;
                r0.mStartY = i12;
                f2 = 1.0f;
                currVelocity = f != 0.0f ? 1.0f : ((float) i9) / f;
                if (f == 0.0f) {
                    f2 = ((float) i10) / f;
                }
                exp = (int) (((double) NUM) * Math.exp((((double) DECELERATION_RATE) / (((double) DECELERATION_RATE) - 1.0d)) * log));
                r0.mMinX = i5;
                r0.mMaxX = i6;
                r0.mMinY = i7;
                r0.mMaxY = i8;
                f = (float) exp;
                r0.mFinalX = i11 + Math.round(currVelocity * f);
                r0.mFinalX = Math.min(r0.mFinalX, r0.mMaxX);
                r0.mFinalX = Math.max(r0.mFinalX, r0.mMinX);
                r0.mFinalY = Math.round(f * f2) + i12;
                r0.mFinalY = Math.min(r0.mFinalY, r0.mMaxY);
                r0.mFinalY = Math.max(r0.mFinalY, r0.mMinY);
            }
        }
        i10 = i4;
        r0.mMode = 1;
        r0.mFinished = false;
        f = (float) Math.sqrt((double) ((i9 * i9) + (i10 * i10)));
        r0.mVelocity = f;
        log = Math.log((double) ((START_TENSION * f) / 800.0f));
        r0.mDuration = (int) (1000.0d * Math.exp(log / (((double) DECELERATION_RATE) - 1.0d)));
        r0.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        r0.mStartX = i11;
        r0.mStartY = i12;
        f2 = 1.0f;
        if (f != 0.0f) {
        }
        if (f == 0.0f) {
            f2 = ((float) i10) / f;
        }
        exp = (int) (((double) NUM) * Math.exp((((double) DECELERATION_RATE) / (((double) DECELERATION_RATE) - 1.0d)) * log));
        r0.mMinX = i5;
        r0.mMaxX = i6;
        r0.mMinY = i7;
        r0.mMaxY = i8;
        f = (float) exp;
        r0.mFinalX = i11 + Math.round(currVelocity * f);
        r0.mFinalX = Math.min(r0.mFinalX, r0.mMaxX);
        r0.mFinalX = Math.max(r0.mFinalX, r0.mMinX);
        r0.mFinalY = Math.round(f * f2) + i12;
        r0.mFinalY = Math.min(r0.mFinalY, r0.mMaxY);
        r0.mFinalY = Math.max(r0.mFinalY, r0.mMinY);
    }

    static float viscousFluid(float f) {
        f *= sViscousFluidScale;
        if (f < 1.0f) {
            f -= 1.0f - ((float) Math.exp((double) (-f)));
        } else {
            f = 0.36787945f + ((1.0f - ((float) Math.exp((double) (1.0f - f)))) * 0.63212055f);
        }
        return f * sViscousFluidNormalize;
    }

    public void abortAnimation() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
    }

    public void extendDuration(int i) {
        this.mDuration = timePassed() + i;
        this.mDurationReciprocal = 1.0f / ((float) this.mDuration);
        this.mFinished = false;
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    }

    public void setFinalX(int i) {
        this.mFinalX = i;
        this.mDeltaX = (float) (this.mFinalX - this.mStartX);
        this.mFinished = false;
    }

    public void setFinalY(int i) {
        this.mFinalY = i;
        this.mDeltaY = (float) (this.mFinalY - this.mStartY);
        this.mFinished = false;
    }

    public boolean isScrollingInDirection(float f, float f2) {
        return !this.mFinished && Math.signum(f) == Math.signum((float) (this.mFinalX - this.mStartX)) && Math.signum(f2) == Math.signum((float) (this.mFinalY - this.mStartY));
    }
}
