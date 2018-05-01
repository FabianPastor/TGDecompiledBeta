package org.telegram.ui.Components;

import android.graphics.PointF;
import android.view.animation.Interpolator;

public class CubicBezierInterpolator implements Interpolator {
    public static final CubicBezierInterpolator DEFAULT = new CubicBezierInterpolator(0.25d, 0.1d, 0.25d, 1.0d);
    public static final CubicBezierInterpolator EASE_BOTH = new CubicBezierInterpolator(0.42d, 0.0d, 0.58d, 1.0d);
    public static final CubicBezierInterpolator EASE_IN = new CubicBezierInterpolator(0.42d, 0.0d, 1.0d, 1.0d);
    public static final CubicBezierInterpolator EASE_OUT = new CubicBezierInterpolator(0.0d, 0.0d, 0.58d, 1.0d);
    public static final CubicBezierInterpolator EASE_OUT_QUINT = new CubicBezierInterpolator(0.23d, 1.0d, 0.32d, 1.0d);
    /* renamed from: a */
    protected PointF f15a;
    /* renamed from: b */
    protected PointF f16b;
    /* renamed from: c */
    protected PointF f17c;
    protected PointF end;
    protected PointF start;

    public CubicBezierInterpolator(PointF pointF, PointF pointF2) throws IllegalArgumentException {
        this.f15a = new PointF();
        this.f16b = new PointF();
        this.f17c = new PointF();
        if (pointF.x >= 0.0f) {
            if (pointF.x <= 1.0f) {
                if (pointF2.x >= 0.0f) {
                    if (pointF2.x <= 1.0f) {
                        this.start = pointF;
                        this.end = pointF2;
                        return;
                    }
                }
                throw new IllegalArgumentException("endX value must be in the range [0, 1]");
            }
        }
        throw new IllegalArgumentException("startX value must be in the range [0, 1]");
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

    protected float getBezierCoordinateY(float f) {
        this.f17c.y = this.start.y * 3.0f;
        this.f16b.y = (3.0f * (this.end.y - this.start.y)) - this.f17c.y;
        this.f15a.y = (1.0f - this.f17c.y) - this.f16b.y;
        return f * (this.f17c.y + ((this.f16b.y + (this.f15a.y * f)) * f));
    }

    protected float getXForTime(float f) {
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
        return this.f17c.x + (f * ((2.0f * this.f16b.x) + ((3.0f * this.f15a.x) * f)));
    }

    private float getBezierCoordinateX(float f) {
        this.f17c.x = this.start.x * 3.0f;
        this.f16b.x = (3.0f * (this.end.x - this.start.x)) - this.f17c.x;
        this.f15a.x = (1.0f - this.f17c.x) - this.f16b.x;
        return f * (this.f17c.x + ((this.f16b.x + (this.f15a.x * f)) * f));
    }
}
