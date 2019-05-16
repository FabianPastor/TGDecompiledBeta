package org.telegram.ui.Components.Paint;

import android.graphics.PointF;

public class Point {
    public boolean edge;
    public double x;
    public double y;
    public double z;

    public Point(double d, double d2, double d3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
        this.z = point.z;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point point = (Point) obj;
        if (this.x == point.x && this.y == point.y && this.z == point.z) {
            z = true;
        }
        return z;
    }

    /* Access modifiers changed, original: 0000 */
    public Point multiplySum(Point point, double d) {
        return new Point((this.x + point.x) * d, (this.y + point.y) * d, (this.z + point.z) * d);
    }

    /* Access modifiers changed, original: 0000 */
    public Point multiplyAndAdd(double d, Point point) {
        return new Point((this.x * d) + point.x, (this.y * d) + point.y, (this.z * d) + point.z);
    }

    /* Access modifiers changed, original: 0000 */
    public void alteringAddMultiplication(Point point, double d) {
        this.x += point.x * d;
        this.y += point.y * d;
        this.z += point.z * d;
    }

    /* Access modifiers changed, original: 0000 */
    public Point add(Point point) {
        return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
    }

    /* Access modifiers changed, original: 0000 */
    public Point substract(Point point) {
        return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
    }

    /* Access modifiers changed, original: 0000 */
    public Point multiplyByScalar(double d) {
        return new Point(this.x * d, this.y * d, this.z * d);
    }

    /* Access modifiers changed, original: 0000 */
    public Point getNormalized() {
        return multiplyByScalar(1.0d / getMagnitude());
    }

    private double getMagnitude() {
        double d = this.x;
        d *= d;
        double d2 = this.y;
        d += d2 * d2;
        d2 = this.z;
        return Math.sqrt(d + (d2 * d2));
    }

    /* Access modifiers changed, original: 0000 */
    public float getDistanceTo(Point point) {
        return (float) Math.sqrt((Math.pow(this.x - point.x, 2.0d) + Math.pow(this.y - point.y, 2.0d)) + Math.pow(this.z - point.z, 2.0d));
    }

    /* Access modifiers changed, original: 0000 */
    public PointF toPointF() {
        return new PointF((float) this.x, (float) this.y);
    }
}
