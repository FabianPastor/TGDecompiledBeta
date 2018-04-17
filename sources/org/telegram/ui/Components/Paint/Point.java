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

    public Point(double x, double y, double z) {
        this.f20x = x;
        this.f21y = y;
        this.f22z = z;
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
        Point other = (Point) obj;
        if (this.f20x == other.f20x && this.f21y == other.f21y && this.f22z == other.f22z) {
            z = true;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f20x + point.f20x) * scalar, (this.f21y + point.f21y) * scalar, (this.f22z + point.f22z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f20x * scalar) + point.f20x, (this.f21y * scalar) + point.f21y, (this.f22z * scalar) + point.f22z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f20x += point.f20x * scalar;
        this.f21y += point.f21y * scalar;
        this.f22z += point.f22z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f20x + point.f20x, this.f21y + point.f21y, this.f22z + point.f22z);
    }

    Point substract(Point point) {
        return new Point(this.f20x - point.f20x, this.f21y - point.f21y, this.f22z - point.f22z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f20x * scalar, this.f21y * scalar, this.f22z * scalar);
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
