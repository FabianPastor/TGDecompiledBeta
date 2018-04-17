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

    public CubicBezierInterpolator(PointF start, PointF end) throws IllegalArgumentException {
        this.f15a = new PointF();
        this.f16b = new PointF();
        this.f17c = new PointF();
        if (start.x >= 0.0f) {
            if (start.x <= 1.0f) {
                if (end.x >= 0.0f) {
                    if (end.x <= 1.0f) {
                        this.start = start;
                        this.end = end;
                        return;
                    }
                }
                throw new IllegalArgumentException("endX value must be in the range [0, 1]");
            }
        }
        throw new IllegalArgumentException("startX value must be in the range [0, 1]");
    }

    public CubicBezierInterpolator(float startX, float startY, float endX, float endY) {
        this(new PointF(startX, startY), new PointF(endX, endY));
    }

    public CubicBezierInterpolator(double startX, double startY, double endX, double endY) {
        this((float) startX, (float) startY, (float) endX, (float) endY);
    }

    public float getInterpolation(float time) {
        return getBezierCoordinateY(getXForTime(time));
    }

    protected float getBezierCoordinateY(float time) {
        this.f17c.y = this.start.y * 3.0f;
        this.f16b.y = (3.0f * (this.end.y - this.start.y)) - this.f17c.y;
        this.f15a.y = (1.0f - this.f17c.y) - this.f16b.y;
        return (this.f17c.y + ((this.f16b.y + (this.f15a.y * time)) * time)) * time;
    }

    protected float getXForTime(float time) {
        float x = time;
        for (int i = 1; i < 14; i++) {
            float z = getBezierCoordinateX(x) - time;
            if (((double) Math.abs(z)) < 0.001d) {
                break;
            }
            x -= z / getXDerivate(x);
        }
        return x;
    }

    private float getXDerivate(float t) {
        return this.f17c.x + (((2.0f * this.f16b.x) + ((3.0f * this.f15a.x) * t)) * t);
    }

    private float getBezierCoordinateX(float time) {
        this.f17c.x = this.start.x * 3.0f;
        this.f16b.x = (3.0f * (this.end.x - this.start.x)) - this.f17c.x;
        this.f15a.x = (1.0f - this.f17c.x) - this.f16b.x;
        return (this.f17c.x + ((this.f16b.x + (this.f15a.x * time)) * time)) * time;
    }
}
