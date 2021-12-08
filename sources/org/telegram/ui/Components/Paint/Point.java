package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    public double x;
    public double y;
    public double z;

    public Point(double x2, double y2, double z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

    public boolean equals(Object obj) {
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
        if (this.x == other.x && this.y == other.y && this.z == other.z) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public Point multiplySum(Point point, double scalar) {
        return new Point((this.x + point.x) * scalar, (this.y + point.y) * scalar, (this.z + point.z) * scalar);
    }

    /* access modifiers changed from: package-private */
    public Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.x * scalar) + point.x, (this.y * scalar) + point.y, (this.z * scalar) + point.z);
    }

    /* access modifiers changed from: package-private */
    public void alteringAddMultiplication(Point point, double scalar) {
        this.x += point.x * scalar;
        this.y += point.y * scalar;
        this.z += point.z * scalar;
    }

    /* access modifiers changed from: package-private */
    public Point add(Point point) {
        return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
    }

    /* access modifiers changed from: package-private */
    public Point substract(Point point) {
        return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
    }

    /* access modifiers changed from: package-private */
    public Point multiplyByScalar(double scalar) {
        return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    /* access modifiers changed from: package-private */
    public Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        double d = this.x;
        double d2 = this.y;
        double d3 = (d * d) + (d2 * d2);
        double d4 = this.z;
        return Math.sqrt(d3 + (d4 * d4));
    }

    /* access modifiers changed from: package-private */
    public float getDistanceTo(Point point) {
        return (float) Math.sqrt(Math.pow(this.x - point.x, 2.0d) + Math.pow(this.y - point.y, 2.0d) + Math.pow(this.z - point.z, 2.0d));
    }

    /* access modifiers changed from: package-private */
    public PointF toPointF() {
        return new PointF((float) this.x, (float) this.y);
    }
}
