package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f64x;
    /* renamed from: y */
    public double f65y;
    /* renamed from: z */
    public double f66z;

    public Point(double x, double y, double z) {
        this.f64x = x;
        this.f65y = y;
        this.f66z = z;
    }

    public Point(Point point) {
        this.f64x = point.f64x;
        this.f65y = point.f65y;
        this.f66z = point.f66z;
    }

    public boolean equals(Object obj) {
        boolean z = true;
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
        if (!(this.f64x == other.f64x && this.f65y == other.f65y && this.f66z == other.f66z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f64x + point.f64x) * scalar, (this.f65y + point.f65y) * scalar, (this.f66z + point.f66z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f64x * scalar) + point.f64x, (this.f65y * scalar) + point.f65y, (this.f66z * scalar) + point.f66z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f64x += point.f64x * scalar;
        this.f65y += point.f65y * scalar;
        this.f66z += point.f66z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f64x + point.f64x, this.f65y + point.f65y, this.f66z + point.f66z);
    }

    Point substract(Point point) {
        return new Point(this.f64x - point.f64x, this.f65y - point.f65y, this.f66z - point.f66z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f64x * scalar, this.f65y * scalar, this.f66z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f64x * this.f64x) + (this.f65y * this.f65y)) + (this.f66z * this.f66z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f64x - point.f64x, 2.0d) + Math.pow(this.f65y - point.f65y, 2.0d)) + Math.pow(this.f66z - point.f66z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f64x, (float) this.f65y);
    }
}
