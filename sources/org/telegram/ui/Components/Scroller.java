package org.telegram.ui.Components;

import android.content.Context;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class Scroller {
    private static float DECELERATION_RATE = ((float) (Math.log(0.75d) / Math.log(0.9d)));
    private static final int DEFAULT_DURATION = 250;
    private static float END_TENSION = (1.0f - 0.4f);
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
        float x;
        float coef;
        float x_min = 0.0f;
        for (int i = 0; i <= 100; i++) {
            float t = ((float) i) / 100.0f;
            float x_max = 1.0f;
            while (true) {
                x = ((x_max - x_min) / 2.0f) + x_min;
                coef = 3.0f * x * (1.0f - x);
                float tx = ((((1.0f - x) * START_TENSION) + (END_TENSION * x)) * coef) + (x * x * x);
                if (((double) Math.abs(tx - t)) < 1.0E-5d) {
                    break;
                } else if (tx > t) {
                    x_max = x;
                } else {
                    x_min = x;
                }
            }
            SPLINE[i] = (x * x * x) + coef;
        }
        SPLINE[100] = 1.0f;
        sViscousFluidNormalize = 1.0f;
        sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }

    public Scroller(Context context) {
        this(context, (Interpolator) null);
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
        return this.mPpi * 386.0878f * friction;
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
        float x;
        if (this.mFinished) {
            return false;
        }
        int timePassed = (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
        int i = this.mDuration;
        if (timePassed < i) {
            switch (this.mMode) {
                case 0:
                    float x2 = ((float) timePassed) * this.mDurationReciprocal;
                    Interpolator interpolator = this.mInterpolator;
                    if (interpolator == null) {
                        x = viscousFluid(x2);
                    } else {
                        x = interpolator.getInterpolation(x2);
                    }
                    this.mCurrX = this.mStartX + Math.round(this.mDeltaX * x);
                    this.mCurrY = this.mStartY + Math.round(this.mDeltaY * x);
                    break;
                case 1:
                    float t = ((float) timePassed) / ((float) i);
                    int index = (int) (t * 100.0f);
                    float t_inf = ((float) index) / 100.0f;
                    float[] fArr = SPLINE;
                    float d_inf = fArr[index];
                    float distanceCoef = (((t - t_inf) / ((((float) (index + 1)) / 100.0f) - t_inf)) * (fArr[index + 1] - d_inf)) + d_inf;
                    int i2 = this.mStartX;
                    int round = i2 + Math.round(((float) (this.mFinalX - i2)) * distanceCoef);
                    this.mCurrX = round;
                    int min = Math.min(round, this.mMaxX);
                    this.mCurrX = min;
                    this.mCurrX = Math.max(min, this.mMinX);
                    int i3 = this.mStartY;
                    int round2 = i3 + Math.round(((float) (this.mFinalY - i3)) * distanceCoef);
                    this.mCurrY = round2;
                    int min2 = Math.min(round2, this.mMaxY);
                    this.mCurrY = min2;
                    int max = Math.max(min2, this.mMinY);
                    this.mCurrY = max;
                    if (this.mCurrX == this.mFinalX && max == this.mFinalY) {
                        this.mFinished = true;
                        break;
                    }
            }
        } else {
            this.mCurrX = this.mFinalX;
            this.mCurrY = this.mFinalY;
            this.mFinished = true;
        }
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
        int i = startX;
        int i2 = startY;
        int velocityX2 = velocityX;
        int velocityY2 = velocityY;
        if (this.mFlywheel && !this.mFinished) {
            float oldVel = getCurrVelocity();
            float dx = (float) (this.mFinalX - this.mStartX);
            float dy = (float) (this.mFinalY - this.mStartY);
            float hyp = (float) Math.sqrt((double) ((dx * dx) + (dy * dy)));
            float oldVelocityX = (dx / hyp) * oldVel;
            float oldVelocityY = (dy / hyp) * oldVel;
            if (Math.signum((float) velocityX2) == Math.signum(oldVelocityX) && Math.signum((float) velocityY2) == Math.signum(oldVelocityY)) {
                velocityX2 = (int) (((float) velocityX2) + oldVelocityX);
                velocityY2 = (int) (((float) velocityY2) + oldVelocityY);
            }
        }
        this.mMode = 1;
        this.mFinished = false;
        float velocity = (float) Math.sqrt((double) ((velocityX2 * velocityX2) + (velocityY2 * velocityY2)));
        this.mVelocity = velocity;
        double l = Math.log((double) ((START_TENSION * velocity) / 800.0f));
        double d = (double) DECELERATION_RATE;
        Double.isNaN(d);
        this.mDuration = (int) (Math.exp(l / (d - 1.0d)) * 1000.0d);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartX = i;
        this.mStartY = i2;
        float coeffY = 1.0f;
        float coeffX = velocity == 0.0f ? 1.0f : ((float) velocityX2) / velocity;
        if (velocity != 0.0f) {
            coeffY = ((float) velocityY2) / velocity;
        }
        double d2 = (double) 800.0f;
        float f = DECELERATION_RATE;
        int i3 = velocityX2;
        int i4 = velocityY2;
        double d3 = (double) f;
        float f2 = velocity;
        double d4 = (double) f;
        Double.isNaN(d4);
        Double.isNaN(d3);
        double exp = Math.exp((d3 / (d4 - 1.0d)) * l);
        Double.isNaN(d2);
        int totalDistance = (int) (d2 * exp);
        this.mMinX = minX;
        this.mMaxX = maxX;
        this.mMinY = minY;
        this.mMaxY = maxY;
        int round = Math.round(((float) totalDistance) * coeffX) + i;
        this.mFinalX = round;
        int min = Math.min(round, this.mMaxX);
        this.mFinalX = min;
        this.mFinalX = Math.max(min, this.mMinX);
        int round2 = Math.round(((float) totalDistance) * coeffY) + i2;
        this.mFinalY = round2;
        int min2 = Math.min(round2, this.mMaxY);
        this.mFinalY = min2;
        this.mFinalY = Math.max(min2, this.mMinY);
    }

    static float viscousFluid(float x) {
        float x2;
        float x3 = x * sViscousFluidScale;
        if (x3 < 1.0f) {
            x2 = x3 - (1.0f - ((float) Math.exp((double) (-x3))));
        } else {
            x2 = 0.36787945f + ((1.0f - 0.36787945f) * (1.0f - ((float) Math.exp((double) (1.0f - x3)))));
        }
        return x2 * sViscousFluidNormalize;
    }

    public void abortAnimation() {
        this.mCurrX = this.mFinalX;
        this.mCurrY = this.mFinalY;
        this.mFinished = true;
    }

    public void extendDuration(int extend) {
        int timePassed = timePassed() + extend;
        this.mDuration = timePassed;
        this.mDurationReciprocal = 1.0f / ((float) timePassed);
        this.mFinished = false;
    }

    public int timePassed() {
        return (int) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime);
    }

    public void setFinalX(int newX) {
        this.mFinalX = newX;
        this.mDeltaX = (float) (newX - this.mStartX);
        this.mFinished = false;
    }

    public void setFinalY(int newY) {
        this.mFinalY = newY;
        this.mDeltaY = (float) (newY - this.mStartY);
        this.mFinished = false;
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        return !this.mFinished && Math.signum(xvel) == Math.signum((float) (this.mFinalX - this.mStartX)) && Math.signum(yvel) == Math.signum((float) (this.mFinalY - this.mStartY));
    }
}
