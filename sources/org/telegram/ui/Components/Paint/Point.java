package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f50x;
    /* renamed from: y */
    public double f51y;
    /* renamed from: z */
    public double f52z;

    public Point(double x, double y, double z) {
        this.f50x = x;
        this.f51y = y;
        this.f52z = z;
    }

    public Point(Point point) {
        this.f50x = point.f50x;
        this.f51y = point.f51y;
        this.f52z = point.f52z;
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
        if (!(this.f50x == other.f50x && this.f51y == other.f51y && this.f52z == other.f52z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f50x + point.f50x) * scalar, (this.f51y + point.f51y) * scalar, (this.f52z + point.f52z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f50x * scalar) + point.f50x, (this.f51y * scalar) + point.f51y, (this.f52z * scalar) + point.f52z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f50x += point.f50x * scalar;
        this.f51y += point.f51y * scalar;
        this.f52z += point.f52z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f50x + point.f50x, this.f51y + point.f51y, this.f52z + point.f52z);
    }

    Point substract(Point point) {
        return new Point(this.f50x - point.f50x, this.f51y - point.f51y, this.f52z - point.f52z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f50x * scalar, this.f51y * scalar, this.f52z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f50x * this.f50x) + (this.f51y * this.f51y)) + (this.f52z * this.f52z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f50x - point.f50x, 2.0d) + Math.pow(this.f51y - point.f51y, 2.0d)) + Math.pow(this.f52z - point.f52z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f50x, (float) this.f51y);
    }
}
