package org.telegram.p005ui.Components.Paint;

import android.graphics.PointF;

/* renamed from: org.telegram.ui.Components.Paint.Point */
public class Point {
    public boolean edge;
    /* renamed from: x */
    public double f231x;
    /* renamed from: y */
    public double f232y;
    /* renamed from: z */
    public double f233z;

    public Point(double x, double y, double z) {
        this.f231x = x;
        this.f232y = y;
        this.f233z = z;
    }

    public Point(Point point) {
        this.f231x = point.f231x;
        this.f232y = point.f232y;
        this.f233z = point.f233z;
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
        if (!(this.f231x == other.f231x && this.f232y == other.f232y && this.f233z == other.f233z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.f231x + point.f231x) * scalar, (this.f232y + point.f232y) * scalar, (this.f233z + point.f233z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.f231x * scalar) + point.f231x, (this.f232y * scalar) + point.f232y, (this.f233z * scalar) + point.f233z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.f231x += point.f231x * scalar;
        this.f232y += point.f232y * scalar;
        this.f233z += point.f233z * scalar;
    }

    Point add(Point point) {
        return new Point(this.f231x + point.f231x, this.f232y + point.f232y, this.f233z + point.f233z);
    }

    Point substract(Point point) {
        return new Point(this.f231x - point.f231x, this.f232y - point.f232y, this.f233z - point.f233z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.f231x * scalar, this.f232y * scalar, this.f233z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.f231x * this.f231x) + (this.f232y * this.f232y)) + (this.f233z * this.f233z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.f231x - point.f231x, 2.0d) + Math.pow(this.f232y - point.f232y, 2.0d)) + Math.pow(this.f233z - point.f233z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.f231x, (float) this.f232y);
    }
}
