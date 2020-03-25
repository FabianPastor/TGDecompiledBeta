package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.view.MotionEvent;
import java.util.Vector;
import org.telegram.messenger.AndroidUtilities;

public class Input {
    private boolean beganDrawing;
    /* access modifiers changed from: private */
    public boolean clearBuffer;
    private boolean hasMoved;
    private Matrix invertMatrix;
    private boolean isFirst;
    private Point lastLocation;
    /* access modifiers changed from: private */
    public double lastRemainder;
    private Point[] points = new Point[3];
    private int pointsCount;
    private RenderView renderView;
    private float[] tempPoint = new float[2];

    public Input(RenderView renderView2) {
        this.renderView = renderView2;
    }

    public void setMatrix(Matrix matrix) {
        Matrix matrix2 = new Matrix();
        this.invertMatrix = matrix2;
        matrix.invert(matrix2);
    }

    public void process(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        float x = motionEvent.getX();
        float height = ((float) this.renderView.getHeight()) - motionEvent.getY();
        float[] fArr = this.tempPoint;
        fArr[0] = x;
        fArr[1] = height;
        this.invertMatrix.mapPoints(fArr);
        float[] fArr2 = this.tempPoint;
        Point point = new Point((double) fArr2[0], (double) fArr2[1], 1.0d);
        if (actionMasked != 0) {
            if (actionMasked == 1) {
                if (!this.hasMoved) {
                    if (this.renderView.shouldDraw()) {
                        point.edge = true;
                        paintPath(new Path(point));
                    }
                    reset();
                } else if (this.pointsCount > 0) {
                    smoothenAndPaintPoints(true);
                }
                this.pointsCount = 0;
                this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
                this.beganDrawing = false;
                this.renderView.onFinishedDrawing(this.hasMoved);
                return;
            } else if (actionMasked != 2) {
                return;
            }
        }
        if (!this.beganDrawing) {
            this.beganDrawing = true;
            this.hasMoved = false;
            this.isFirst = true;
            this.lastLocation = point;
            this.points[0] = point;
            this.pointsCount = 1;
            this.clearBuffer = true;
        } else if (point.getDistanceTo(this.lastLocation) >= ((float) AndroidUtilities.dp(5.0f))) {
            if (!this.hasMoved) {
                this.renderView.onBeganDrawing();
                this.hasMoved = true;
            }
            Point[] pointArr = this.points;
            int i = this.pointsCount;
            pointArr[i] = point;
            int i2 = i + 1;
            this.pointsCount = i2;
            if (i2 == 3) {
                smoothenAndPaintPoints(false);
            }
            this.lastLocation = point;
        }
    }

    private void reset() {
        this.pointsCount = 0;
    }

    private void smoothenAndPaintPoints(boolean z) {
        int i = this.pointsCount;
        if (i > 2) {
            Vector vector = new Vector();
            Point[] pointArr = this.points;
            Point point = pointArr[0];
            Point point2 = pointArr[1];
            Point point3 = pointArr[2];
            if (point3 != null && point2 != null && point != null) {
                Point multiplySum = point2.multiplySum(point, 0.5d);
                Point multiplySum2 = point3.multiplySum(point2, 0.5d);
                int min = (int) Math.min(48.0d, Math.max(Math.floor((double) (multiplySum.getDistanceTo(multiplySum2) / ((float) 1))), 24.0d));
                float f = 0.0f;
                float f2 = 1.0f / ((float) min);
                for (int i2 = 0; i2 < min; i2++) {
                    Point smoothPoint = smoothPoint(multiplySum, multiplySum2, point2, f);
                    if (this.isFirst) {
                        smoothPoint.edge = true;
                        this.isFirst = false;
                    }
                    vector.add(smoothPoint);
                    f += f2;
                }
                if (z) {
                    multiplySum2.edge = true;
                }
                vector.add(multiplySum2);
                Point[] pointArr2 = new Point[vector.size()];
                vector.toArray(pointArr2);
                paintPath(new Path(pointArr2));
                Point[] pointArr3 = this.points;
                System.arraycopy(pointArr3, 1, pointArr3, 0, 2);
                if (z) {
                    this.pointsCount = 0;
                } else {
                    this.pointsCount = 2;
                }
            }
        } else {
            Point[] pointArr4 = new Point[i];
            System.arraycopy(this.points, 0, pointArr4, 0, i);
            paintPath(new Path(pointArr4));
        }
    }

    private Point smoothPoint(Point point, Point point2, Point point3, float f) {
        Point point4 = point;
        Point point5 = point2;
        Point point6 = point3;
        float f2 = 1.0f - f;
        double pow = Math.pow((double) f2, 2.0d);
        double d = (double) (f2 * 2.0f * f);
        double d2 = (double) (f * f);
        double d3 = point6.x;
        Double.isNaN(d);
        double d4 = (point4.x * pow) + (d3 * d);
        double d5 = point5.x;
        Double.isNaN(d2);
        double d6 = point4.y * pow;
        double d7 = point6.y;
        Double.isNaN(d);
        double d8 = point5.y;
        Double.isNaN(d2);
        return new Point(d4 + (d5 * d2), d6 + (d7 * d) + (d8 * d2), 1.0d);
    }

    private void paintPath(final Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = 0.0d;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new Runnable() {
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        AnonymousClass1 r0 = AnonymousClass1.this;
                        double unused = Input.this.lastRemainder = path.remainder;
                        boolean unused2 = Input.this.clearBuffer = false;
                    }
                });
            }
        });
    }
}
