package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    public double x;
    public double y;
    public double z;

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
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
        if (!(this.x == other.x && this.y == other.y && this.z == other.z)) {
            z = false;
        }
        return z;
    }

    Point multiplySum(Point point, double scalar) {
        return new Point((this.x + point.x) * scalar, (this.y + point.y) * scalar, (this.z + point.z) * scalar);
    }

    Point multiplyAndAdd(double scalar, Point point) {
        return new Point((this.x * scalar) + point.x, (this.y * scalar) + point.y, (this.z * scalar) + point.z);
    }

    void alteringAddMultiplication(Point point, double scalar) {
        this.x += point.x * scalar;
        this.y += point.y * scalar;
        this.z += point.z * scalar;
    }

    Point add(Point point) {
        return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
    }

    Point substract(Point point) {
        return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
    }

    Point multiplyByScalar(double scalar) {
        return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        return Math.sqrt(((this.x * this.x) + (this.y * this.y)) + (this.z * this.z));
    }

    float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.x - point.x, 2.0d) + Math.pow(this.y - point.y, 2.0d)) + Math.pow(this.z - point.z, 2.0d));
    }

    PointF toPointF() {
        return new PointF((float) this.x, (float) this.y);
    }
}
