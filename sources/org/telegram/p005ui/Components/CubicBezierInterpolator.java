package org.telegram.p005ui.Components;

import android.graphics.PointF;
import android.view.animation.Interpolator;

/* renamed from: org.telegram.ui.Components.CubicBezierInterpolator */
public class CubicBezierInterpolator implements Interpolator {
    public static final CubicBezierInterpolator DEFAULT = new CubicBezierInterpolator(0.25d, 0.1d, 0.25d, 1.0d);
    public static final CubicBezierInterpolator EASE_BOTH = new CubicBezierInterpolator(0.42d, 0.0d, 0.58d, 1.0d);
    public static final CubicBezierInterpolator EASE_IN = new CubicBezierInterpolator(0.42d, 0.0d, 1.0d, 1.0d);
    public static final CubicBezierInterpolator EASE_OUT = new CubicBezierInterpolator(0.0d, 0.0d, 0.58d, 1.0d);
    public static final CubicBezierInterpolator EASE_OUT_QUINT = new CubicBezierInterpolator(0.23d, 1.0d, 0.32d, 1.0d);
    /* renamed from: a */
    protected PointF f224a;
    /* renamed from: b */
    protected PointF f225b;
    /* renamed from: c */
    protected PointF f226c;
    protected PointF end;
    protected PointF start;

    public CubicBezierInterpolator(PointF start, PointF end) throws IllegalArgumentException {
        this.f224a = new PointF();
        this.f225b = new PointF();
        this.f226c = new PointF();
        if (start.x < 0.0f || start.x > 1.0f) {
            throw new IllegalArgumentException("startX value must be in the range [0, 1]");
        } else if (end.x < 0.0f || end.x > 1.0f) {
            throw new IllegalArgumentException("endX value must be in the range [0, 1]");
        } else {
            this.start = start;
            this.end = end;
        }
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
        this.f226c.y = this.start.y * 3.0f;
        this.f225b.y = ((this.end.y - this.start.y) * 3.0f) - this.f226c.y;
        this.f224a.y = (1.0f - this.f226c.y) - this.f225b.y;
        return (this.f226c.y + ((this.f225b.y + (this.f224a.y * time)) * time)) * time;
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
        return this.f226c.x + (((2.0f * this.f225b.x) + ((3.0f * this.f224a.x) * t)) * t);
    }

    private float getBezierCoordinateX(float time) {
        this.f226c.x = this.start.x * 3.0f;
        this.f225b.x = ((this.end.x - this.start.x) * 3.0f) - this.f226c.x;
        this.f224a.x = (1.0f - this.f226c.x) - this.f225b.x;
        return (this.f226c.x + ((this.f225b.x + (this.f224a.x * time)) * time)) * time;
    }
}
