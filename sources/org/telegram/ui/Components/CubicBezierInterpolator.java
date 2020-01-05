package org.telegram.ui.Components;

import android.graphics.PointF;
import android.view.animation.Interpolator;

public class CubicBezierInterpolator implements Interpolator {
    public static final CubicBezierInterpolator DEFAULT = new CubicBezierInterpolator(0.25d, 0.1d, 0.25d, 1.0d);
    public static final CubicBezierInterpolator EASE_BOTH = new CubicBezierInterpolator(0.42d, 0.0d, 0.58d, 1.0d);
    public static final CubicBezierInterpolator EASE_IN = new CubicBezierInterpolator(0.42d, 0.0d, 1.0d, 1.0d);
    public static final CubicBezierInterpolator EASE_OUT = new CubicBezierInterpolator(0.0d, 0.0d, 0.58d, 1.0d);
    public static final CubicBezierInterpolator EASE_OUT_QUINT = new CubicBezierInterpolator(0.23d, 1.0d, 0.32d, 1.0d);
    protected PointF a;
    protected PointF b;
    protected PointF c;
    protected PointF end;
    protected PointF start;

    public CubicBezierInterpolator(PointF pointF, PointF pointF2) throws IllegalArgumentException {
        this.a = new PointF();
        this.b = new PointF();
        this.c = new PointF();
        float f = pointF.x;
        if (f < 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("startX value must be in the range [0, 1]");
        }
        f = pointF2.x;
        if (f < 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("endX value must be in the range [0, 1]");
        }
        this.start = pointF;
        this.end = pointF2;
    }

    public CubicBezierInterpolator(float f, float f2, float f3, float f4) {
        this(new PointF(f, f2), new PointF(f3, f4));
    }

    public CubicBezierInterpolator(double d, double d2, double d3, double d4) {
        this((float) d, (float) d2, (float) d3, (float) d4);
    }

    public float getInterpolation(float f) {
        return getBezierCoordinateY(getXForTime(f));
    }

    /* Access modifiers changed, original: protected */
    public float getBezierCoordinateY(float f) {
        PointF pointF = this.c;
        PointF pointF2 = this.start;
        pointF.y = pointF2.y * 3.0f;
        PointF pointF3 = this.b;
        pointF3.y = ((this.end.y - pointF2.y) * 3.0f) - pointF.y;
        pointF2 = this.a;
        pointF2.y = (1.0f - pointF.y) - pointF3.y;
        return f * (pointF.y + ((pointF3.y + (pointF2.y * f)) * f));
    }

    /* Access modifiers changed, original: protected */
    public float getXForTime(float f) {
        float f2 = f;
        for (int i = 1; i < 14; i++) {
            float bezierCoordinateX = getBezierCoordinateX(f2) - f;
            if (((double) Math.abs(bezierCoordinateX)) < 0.001d) {
                break;
            }
            f2 -= bezierCoordinateX / getXDerivate(f2);
        }
        return f2;
    }

    private float getXDerivate(float f) {
        return this.c.x + (f * ((this.b.x * 2.0f) + ((this.a.x * 3.0f) * f)));
    }

    private float getBezierCoordinateX(float f) {
        PointF pointF = this.c;
        PointF pointF2 = this.start;
        pointF.x = pointF2.x * 3.0f;
        PointF pointF3 = this.b;
        pointF3.x = ((this.end.x - pointF2.x) * 3.0f) - pointF.x;
        pointF2 = this.a;
        pointF2.x = (1.0f - pointF.x) - pointF3.x;
        return f * (pointF.x + ((pointF3.x + (pointF2.x * f)) * f));
    }
}
