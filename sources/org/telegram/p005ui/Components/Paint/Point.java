package org.telegram.p005ui.Components.Paint;

import android.graphics.PointF;

/* renamed from: org.telegram.ui.Components.Paint.Point */
public class Point {
    public boolean edge;
    /* renamed from: x */
    public double var_x;
    /* renamed from: y */
    public double var_y;
    /* renamed from: z */
    public double var_z;

    public Point(double x, double y, double z) {
        this.var_x = x;
        this.var_y = y;
        this.var_z = z;
    }

    public Point(Point point) {
        this.var_x = point.var_x;
        this.var_y = point.var_y;
        this.var_z = point.var_z;
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
        if (!(this.var_x == other.var_x && this.var_y == other.var_y && this.var_z == other.var_z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.var_x + point.var_x) * scalar, (this.var_y + point.var_y) * scalar, (this.var_z + point.var_z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.var_x * scalar) + point.var_x, (this.var_y * scalar) + point.var_y, (this.var_z * scalar) + point.var_z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.var_x += point.var_x * scalar;
        this.var_y += point.var_y * scalar;
        this.var_z += point.var_z * scalar;
    }

    Point add(Point point) {
        return new Point(this.var_x + point.var_x, this.var_y + point.var_y, this.var_z + point.var_z);
    }

    Point substract(Point point) {
        return new Point(this.var_x - point.var_x, this.var_y - point.var_y, this.var_z - point.var_z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.var_x * scalar, this.var_y * scalar, this.var_z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.var_x * this.var_x) + (this.var_y * this.var_y)) + (this.var_z * this.var_z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.var_x - point.var_x, 2.0d) + Math.pow(this.var_y - point.var_y, 2.0d)) + Math.pow(this.var_z - point.var_z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.var_x, (float) this.var_y);
    }
}
