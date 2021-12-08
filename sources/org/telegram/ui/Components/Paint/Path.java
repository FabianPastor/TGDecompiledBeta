package org.telegram.ui.Components.Paint;

import java.util.Arrays;
import java.util.Vector;

public class Path {
    private float baseWeight;
    private Brush brush;
    private int color;
    private Vector<Point> points;
    public double remainder;

    public Path(Point point) {
        Vector<Point> vector = new Vector<>();
        this.points = vector;
        vector.add(point);
    }

    public Path(Point[] points2) {
        Vector<Point> vector = new Vector<>();
        this.points = vector;
        vector.addAll(Arrays.asList(points2));
    }

    public int getLength() {
        Vector<Point> vector = this.points;
        if (vector == null) {
            return 0;
        }
        return vector.size();
    }

    public Point[] getPoints() {
        Point[] points2 = new Point[this.points.size()];
        this.points.toArray(points2);
        return points2;
    }

    public int getColor() {
        return this.color;
    }

    public float getBaseWeight() {
        return this.baseWeight;
    }

    public Brush getBrush() {
        return this.brush;
    }

    public void setup(int color2, float baseWeight2, Brush brush2) {
        this.color = color2;
        this.baseWeight = baseWeight2;
        this.brush = brush2;
    }
}
