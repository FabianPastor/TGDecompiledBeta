package org.telegram.p005ui.Components.Paint;

import android.graphics.PointF;

/* renamed from: org.telegram.ui.Components.Paint.Point */
public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f255x;
    /* renamed from: y */
    public double f256y;
    /* renamed from: z */
    public double f257z;

    public Point(double x, double y, double z) {
        this.f255x = x;
        this.f256y = y;
        this.f257z = z;
    }

    public Point(Point point) {
        this.f255x = point.f255x;
        this.f256y = point.f256y;
        this.f257z = point.f257z;
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
        if (!(this.f255x == other.f255x && this.f256y == other.f256y && this.f257z == other.f257z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f255x + point.f255x) * scalar, (this.f256y + point.f256y) * scalar, (this.f257z + point.f257z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f255x * scalar) + point.f255x, (this.f256y * scalar) + point.f256y, (this.f257z * scalar) + point.f257z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f255x += point.f255x * scalar;
        this.f256y += point.f256y * scalar;
        this.f257z += point.f257z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f255x + point.f255x, this.f256y + point.f256y, this.f257z + point.f257z);
    }

    Point substract(Point point) {
        return new Point(this.f255x - point.f255x, this.f256y - point.f256y, this.f257z - point.f257z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f255x * scalar, this.f256y * scalar, this.f257z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f255x * this.f255x) + (this.f256y * this.f256y)) + (this.f257z * this.f257z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f255x - point.f255x, 2.0d) + Math.pow(this.f256y - point.f256y, 2.0d)) + Math.pow(this.f257z - point.f257z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f255x, (float) this.f256y);
    }
}
