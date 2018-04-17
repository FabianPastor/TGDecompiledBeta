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
    private static float sViscousFluidNormalize;
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
        float x_min = 0.0f;
        int i = 0;
        while (i <= NB_SAMPLES) {
            float x;
            float coef;
            float t = ((float) i) / 100.0f;
            float x_min2 = x_min;
            x_min = 1.0f;
            while (true) {
                x = ((x_min - x_min2) / 2.0f) + x_min2;
                coef = (3.0f * x) * (1.0f - x);
                float tx = ((((1.0f - x) * START_TENSION) + (END_TENSION * x)) * coef) + ((x * x) * x);
                if (((double) Math.abs(tx - t)) < 1.0E-5d) {
                    break;
                } else if (tx > t) {
                    x_min = x;
                } else {
                    x_min2 = x;
                }
            }
            SPLINE[i] = ((x * x) * x) + coef;
            i++;
            x_min = x_min2;
        }
        SPLINE[NB_SAMPLES] = 1.0f;
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }

    public Scroller(Context context) {
        this(context, null);
    }

    public Scroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public Scroller(Context context, Interpolator interpolator, boolean flywheel) {
        this.mFinished = true;
        this.mInterpolator = interpolator;
        this.mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
        this.mDeceleration = computeDeceleration(ViewConfiguration.getScrollFriction());
        this.mFlywheel = flywheel;
    }

    public final void setFriction(float friction) {
        this.mDeceleration = computeDeceleration(friction);
    }

    private float computeDeceleration(float friction) {
        return (386.0878f * this.mPpi) * friction;
    }

    public final boolean isFinished() {
        return this.mFinished;
    }

    public final void forceFinished(boolean finished) {
        this.mFinished = finished;
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
        int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
        if (timePassed < this.mDuration) {
            float x;
            switch (this.mMode) {
                case 0:
                    x = ((float) timePassed) * this.mDurationReciprocal;
                    if (this.mInterpolator == null) {
                        x = viscousFluid(x);
                    } else {
                        x = this.mInterpolator.getInterpolation(x);
                    }
                    this.mCurrX = this.mStartX + Math.round(this.mDeltaX * x);
                    this.mCurrY = this.mStartY + Math.round(this.mDeltaY * x);
                    break;
                case 1:
                    x = ((float) timePassed) / ((float) this.mDuration);
                    int index = (int) (NUM * x);
                    float t_inf = ((float) index) / 100.0f;
                    float t_sup = ((float) (index + 1)) / 100.0f;
                    float d_inf = SPLINE[index];
                    float distanceCoef = (((x - t_inf) / (t_sup - t_inf)) * (SPLINE[index + 1] - d_inf)) + d_inf;
                    this.mCurrX = this.mStartX + Math.round(((float) (this.mFinalX - this.mStartX)) * distanceCoef);
                    this.mCurrX = Math.min(this.mCurrX, this.mMaxX);
                    this.mCurrX = Math.max(this.mCurrX, this.mMinX);
                    this.mCurrY = this.mStartY + Math.round(((float) (this.mFinalY - this.mStartY)) * distanceCoef);
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

    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, 250);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        this.mMode = 0;
        this.mFinished = false;
        this.mDuration = duration;
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = startX;
        this.mStartY = startY;
        this.mFinalX = startX + dx;
        this.mFinalY = startY + dy;
        this.mDeltaX = (float) dx;
        this.mDeltaY = (float) dy;
        this.mDurationReciprocal = 1.0f / ((float) this.mDuration);
    }

    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        float oldVel;
        float oldVelocityX;
        int i = startX;
        int i2 = startY;
        int velocityX2 = velocityX;
        int velocityY2 = velocityY;
        if (this.mFlywheel && !r0.mFinished) {
            oldVel = getCurrVelocity();
            float dx = (float) (r0.mFinalX - r0.mStartX);
            float dy = (float) (r0.mFinalY - r0.mStartY);
            float hyp = (float) Math.sqrt((double) ((dx * dx) + (dy * dy)));
            oldVelocityX = (dx / hyp) * oldVel;
            float oldVelocityY = (dy / hyp) * oldVel;
            if (Math.signum((float) velocityX2) == Math.signum(oldVelocityX) && Math.signum((float) velocityY2) == Math.signum(oldVelocityY)) {
                velocityX2 = (int) (((float) velocityX2) + oldVelocityX);
                velocityY2 = (int) (((float) velocityY2) + oldVelocityY);
            }
        }
        r0.mMode = 1;
        r0.mFinished = false;
        oldVel = (float) Math.sqrt((double) ((velocityX2 * velocityX2) + (velocityY2 * velocityY2)));
        r0.mVelocity = oldVel;
        double l = Math.log((double) ((START_TENSION * oldVel) / 800.0f));
        r0.mDuration = (int) (1000.0d * Math.exp(l / (((double) DECELERATION_RATE) - 1.0d)));
        r0.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        r0.mStartX = i;
        r0.mStartY = i2;
        oldVelocityX = 1.0f;
        float coeffX = oldVel == 0.0f ? 1.0f : ((float) velocityX2) / oldVel;
        if (oldVel != 0.0f) {
            oldVelocityX = ((float) velocityY2) / oldVel;
        }
        float coeffY = oldVelocityX;
        velocityX2 = (int) (((double) NUM) * Math.exp((((double) DECELERATION_RATE) / (((double) DECELERATION_RATE) - 1.0d)) * l));
        r0.mMinX = minX;
        r0.mMaxX = maxX;
        r0.mMinY = minY;
        r0.mMaxY = maxY;
        r0.mFinalX = Math.round(((float) velocityX2) * coeffX) + i;
        r0.mFinalX = Math.min(r0.mFinalX, r0.mMaxX);
        r0.mFinalX = Math.max(r0.mFinalX, r0.mMinX);
        r0.mFinalY = Math.round(((float) velocityX2) * coeffY) + i2;
        r0.mFinalY = Math.min(r0.mFinalY, r0.mMaxY);
        r0.mFinalY = Math.max(r0.mFinalY, r0.mMinY);
    }

    static float viscousFluid(float x) {
        x *= sViscousFluidScale;
        if (x < 1.0f) {
            x -= 1.0f - ((float) Math.exp((double) (-x)));
        } else {
            x = 0.36787945f + ((1.0f - 0.36787945f) * (1.0f - ((float) Math.exp((double) (1.0f - x)))));
        }
        return x * sViscousFluidNormalize;
    }

    public void abortAnimation() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
    }

    public void extendDuration(int extend) {
        this.mDuration = timePassed() + extend;
        this.mDurationReciprocal = 1.0f / ((float) this.mDuration);
        this.mFinished = false;
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    }

    public void setFinalX(int newX) {
        this.mFinalX = newX;
        this.mDeltaX = (float) (this.mFinalX - this.mStartX);
        this.mFinished = false;
    }

    public void setFinalY(int newY) {
        this.mFinalY = newY;
        this.mDeltaY = (float) (this.mFinalY - this.mStartY);
        this.mFinished = false;
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        return !this.mFinished && Math.signum(xvel) == Math.signum((float) (this.mFinalX - this.mStartX)) && Math.signum(yvel) == Math.signum((float) (this.mFinalY - this.mStartY));
    }
}
