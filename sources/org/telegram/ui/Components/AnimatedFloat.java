package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.os.SystemClock;
import android.view.View;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;

public class AnimatedFloat {
    private boolean firstSet;
    private View parent;
    private float startValue;
    private float targetValue;
    private boolean transition;
    private long transitionDelay;
    private long transitionDuration;
    private TimeInterpolator transitionInterpolator;
    private long transitionStart;
    private float value;

    public AnimatedFloat() {
        this.transitionDelay = 0;
        this.transitionDuration = 200;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = null;
        this.firstSet = true;
    }

    public AnimatedFloat(long transitionDuration2, TimeInterpolator transitionInterpolator2) {
        this.transitionDelay = 0;
        this.transitionDuration = 200;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = null;
        this.transitionDuration = transitionDuration2;
        this.transitionInterpolator = transitionInterpolator2;
        this.firstSet = true;
    }

    public AnimatedFloat(long transitionDelay2, long transitionDuration2, TimeInterpolator transitionInterpolator2) {
        this.transitionDelay = 0;
        this.transitionDuration = 200;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = null;
        this.transitionDelay = transitionDelay2;
        this.transitionDuration = transitionDuration2;
        this.transitionInterpolator = transitionInterpolator2;
        this.firstSet = true;
    }

    public AnimatedFloat(View parentToInvalidate) {
        this.transitionDelay = 0;
        this.transitionDuration = 200;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = parentToInvalidate;
        this.firstSet = true;
    }

    public AnimatedFloat(View parentToInvalidate, long transitionDuration2, TimeInterpolator transitionInterpolator2) {
        this.transitionDelay = 0;
        this.transitionDuration = 200;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = parentToInvalidate;
        this.transitionDuration = transitionDuration2;
        this.transitionInterpolator = transitionInterpolator2;
        this.firstSet = true;
    }

    public AnimatedFloat(float initialValue, View parentToInvalidate) {
        this.transitionDelay = 0;
        this.transitionDuration = 200;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = parentToInvalidate;
        this.targetValue = initialValue;
        this.value = initialValue;
        this.firstSet = false;
    }

    public AnimatedFloat(float initialValue, View parentToInvalidate, long transitionDelay2, long transitionDuration2, TimeInterpolator transitionInterpolator2) {
        this.transitionDelay = 0;
        this.transitionDuration = 200;
        this.transitionInterpolator = CubicBezierInterpolator.DEFAULT;
        this.parent = parentToInvalidate;
        this.targetValue = initialValue;
        this.value = initialValue;
        this.transitionDelay = transitionDelay2;
        this.transitionDuration = transitionDuration2;
        this.transitionInterpolator = transitionInterpolator2;
        this.firstSet = false;
    }

    public float get() {
        return this.value;
    }

    public float set(float mustBe) {
        return set(mustBe, false);
    }

    public float set(float mustBe, boolean force) {
        long now = SystemClock.elapsedRealtime();
        if (force || this.firstSet) {
            this.targetValue = mustBe;
            this.value = mustBe;
            this.transition = false;
            this.firstSet = false;
        } else if (Math.abs(this.targetValue - mustBe) > 1.0E-4f) {
            this.transition = true;
            this.targetValue = mustBe;
            this.startValue = this.value;
            this.transitionStart = now;
        }
        if (this.transition) {
            float t = MathUtils.clamp(((float) ((now - this.transitionStart) - this.transitionDelay)) / ((float) this.transitionDuration), 0.0f, 1.0f);
            if (now - this.transitionStart >= this.transitionDelay) {
                this.value = AndroidUtilities.lerp(this.startValue, this.targetValue, this.transitionInterpolator.getInterpolation(t));
            }
            if (t >= 1.0f) {
                this.transition = false;
            } else {
                View view = this.parent;
                if (view != null) {
                    view.invalidate();
                }
            }
        }
        return this.value;
    }

    public void setParent(View parent2) {
        this.parent = parent2;
    }
}
