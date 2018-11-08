package org.telegram.p005ui.Components.Paint;

import android.graphics.PointF;

/* renamed from: org.telegram.ui.Components.Paint.Point */
public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f219x;
    /* renamed from: y */
    public double f220y;
    /* renamed from: z */
    public double f221z;

    public Point(double x, double y, double z) {
        this.f219x = x;
        this.f220y = y;
        this.f221z = z;
    }

    public Point(Point point) {
        this.f219x = point.f219x;
        this.f220y = point.f220y;
        this.f221z = point.f221z;
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
        if (!(this.f219x == other.f219x && this.f220y == other.f220y && this.f221z == other.f221z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f219x + point.f219x) * scalar, (this.f220y + point.f220y) * scalar, (this.f221z + point.f221z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f219x * scalar) + point.f219x, (this.f220y * scalar) + point.f220y, (this.f221z * scalar) + point.f221z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f219x += point.f219x * scalar;
        this.f220y += point.f220y * scalar;
        this.f221z += point.f221z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f219x + point.f219x, this.f220y + point.f220y, this.f221z + point.f221z);
    }

    Point substract(Point point) {
        return new Point(this.f219x - point.f219x, this.f220y - point.f220y, this.f221z - point.f221z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f219x * scalar, this.f220y * scalar, this.f221z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f219x * this.f219x) + (this.f220y * this.f220y)) + (this.f221z * this.f221z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f219x - point.f219x, 2.0d) + Math.pow(this.f220y - point.f220y, 2.0d)) + Math.pow(this.f221z - point.f221z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f219x, (float) this.f220y);
    }
}
