package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f23x;
    /* renamed from: y */
    public double f24y;
    /* renamed from: z */
    public double f25z;

    public Point(double x, double y, double z) {
        this.f23x = x;
        this.f24y = y;
        this.f25z = z;
    }

    public Point(Point point) {
        this.f23x = point.f23x;
        this.f24y = point.f24y;
        this.f25z = point.f25z;
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
        if (!(this.f23x == other.f23x && this.f24y == other.f24y && this.f25z == other.f25z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f23x + point.f23x) * scalar, (this.f24y + point.f24y) * scalar, (this.f25z + point.f25z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f23x * scalar) + point.f23x, (this.f24y * scalar) + point.f24y, (this.f25z * scalar) + point.f25z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f23x += point.f23x * scalar;
        this.f24y += point.f24y * scalar;
        this.f25z += point.f25z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f23x + point.f23x, this.f24y + point.f24y, this.f25z + point.f25z);
    }

    Point substract(Point point) {
        return new Point(this.f23x - point.f23x, this.f24y - point.f24y, this.f25z - point.f25z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f23x * scalar, this.f24y * scalar, this.f25z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f23x * this.f23x) + (this.f24y * this.f24y)) + (this.f25z * this.f25z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f23x - point.f23x, 2.0d) + Math.pow(this.f24y - point.f24y, 2.0d)) + Math.pow(this.f25z - point.f25z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f23x, (float) this.f24y);
    }
}
