package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f20x;
    /* renamed from: y */
    public double f21y;
    /* renamed from: z */
    public double f22z;

    public Point(double d, double d2, double d3) {
        this.f20x = d;
        this.f21y = d2;
        this.f22z = d3;
    }

    public Point(Point point) {
        this.f20x = point.f20x;
        this.f21y = point.f21y;
        this.f22z = point.f22z;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point point = (Point) obj;
        if (this.f20x == point.f20x && this.f21y == point.f21y && this.f22z == point.f22z) {
            z = true;
        }
        return z;
    }

    Point multiplySum(Point point, double d) {
        return new Point((this.f20x + point.f20x) * d, (this.f21y + point.f21y) * d, (this.f22z + point.f22z) * d);
    }

    Point multiplyAndAdd(double d, Point point) {
        return new Point((this.f20x * d) + point.f20x, (this.f21y * d) + point.f21y, (this.f22z * d) + point.f22z);
    }

    void alteringAddMultiplication(Point point, double d) {
        this.f20x += point.f20x * d;
        this.f21y += point.f21y * d;
        this.f22z += point.f22z * d;
    }

    Point add(Point point) {
        return new Point(this.f20x + point.f20x, this.f21y + point.f21y, this.f22z + point.f22z);
    }

    Point substract(Point point) {
        return new Point(this.f20x - point.f20x, this.f21y - point.f21y, this.f22z - point.f22z);
    }

    Point multiplyByScalar(double d) {
        return new Point(this.f20x * d, this.f21y * d, this.f22z * d);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f20x * this.f20x) + (this.f21y * this.f21y)) + (this.f22z * this.f22z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f20x - point.f20x, 2.0d) + Math.pow(this.f21y - point.f21y, 2.0d)) + Math.pow(this.f22z - point.f22z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f20x, (float) this.f21y);
    }
}
