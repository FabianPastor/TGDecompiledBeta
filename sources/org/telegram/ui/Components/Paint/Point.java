package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f54x;
    /* renamed from: y */
    public double f55y;
    /* renamed from: z */
    public double f56z;

    public Point(double x, double y, double z) {
        this.f54x = x;
        this.f55y = y;
        this.f56z = z;
    }

    public Point(Point point) {
        this.f54x = point.f54x;
        this.f55y = point.f55y;
        this.f56z = point.f56z;
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
        if (!(this.f54x == other.f54x && this.f55y == other.f55y && this.f56z == other.f56z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f54x + point.f54x) * scalar, (this.f55y + point.f55y) * scalar, (this.f56z + point.f56z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f54x * scalar) + point.f54x, (this.f55y * scalar) + point.f55y, (this.f56z * scalar) + point.f56z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f54x += point.f54x * scalar;
        this.f55y += point.f55y * scalar;
        this.f56z += point.f56z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f54x + point.f54x, this.f55y + point.f55y, this.f56z + point.f56z);
    }

    Point substract(Point point) {
        return new Point(this.f54x - point.f54x, this.f55y - point.f55y, this.f56z - point.f56z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f54x * scalar, this.f55y * scalar, this.f56z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f54x * this.f54x) + (this.f55y * this.f55y)) + (this.f56z * this.f56z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f54x - point.f54x, 2.0d) + Math.pow(this.f55y - point.f55y, 2.0d)) + Math.pow(this.f56z - point.f56z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f54x, (float) this.f55y);
    }
}
