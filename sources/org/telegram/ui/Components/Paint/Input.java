package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.view.MotionEvent;
import java.util.Vector;
import org.telegram.messenger.AndroidUtilities;

public class Input {
    private boolean beganDrawing;
    private boolean clearBuffer;
    private boolean hasMoved;
    private Matrix invertMatrix;
    private boolean isFirst;
    private Point lastLocation;
    private double lastRemainder;
    private Point[] points = new Point[3];
    private int pointsCount;
    private RenderView renderView;
    private float[] tempPoint = new float[2];

    public Input(RenderView renderView) {
        this.renderView = renderView;
    }

    public void setMatrix(Matrix matrix) {
        this.invertMatrix = new Matrix();
        matrix.invert(this.invertMatrix);
    }

    public void process(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        float height = ((float) this.renderView.getHeight()) - motionEvent.getY();
        this.tempPoint[0] = motionEvent.getX();
        this.tempPoint[1] = height;
        this.invertMatrix.mapPoints(this.tempPoint);
        Point point = new Point((double) this.tempPoint[0], (double) this.tempPoint[1], 1.0d);
        switch (actionMasked) {
            case 0:
            case 2:
                if (this.beganDrawing) {
                    if (point.getDistanceTo(this.lastLocation) >= ((float) AndroidUtilities.dp(5.0f))) {
                        if (!this.hasMoved) {
                            this.renderView.onBeganDrawing();
                            this.hasMoved = true;
                        }
                        this.points[this.pointsCount] = point;
                        this.pointsCount++;
                        if (this.pointsCount == 3) {
                            smoothenAndPaintPoints(false);
                        }
                        this.lastLocation = point;
                        break;
                    }
                    return;
                }
                this.beganDrawing = true;
                this.hasMoved = false;
                this.isFirst = true;
                this.lastLocation = point;
                this.points[0] = point;
                this.pointsCount = 1;
                this.clearBuffer = true;
                break;
            case 1:
                if (!this.hasMoved) {
                    if (this.renderView.shouldDraw()) {
                        point.edge = true;
                        paintPath(new Path(point));
                    }
                    reset();
                } else if (this.pointsCount > null) {
                    smoothenAndPaintPoints(true);
                }
                this.pointsCount = 0;
                this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
                this.beganDrawing = false;
                this.renderView.onFinishedDrawing(this.hasMoved);
                break;
            default:
                break;
        }
    }

    private void reset() {
        this.pointsCount = 0;
    }

    private void smoothenAndPaintPoints(boolean z) {
        if (this.pointsCount > 2) {
            Vector vector = new Vector();
            Point point = this.points[0];
            Point point2 = this.points[1];
            Point point3 = this.points[2];
            if (!(point3 == null || point2 == null)) {
                if (point != null) {
                    point = point2.multiplySum(point, 0.5d);
                    point3 = point3.multiplySum(point2, 0.5d);
                    int min = (int) Math.min(48.0d, Math.max(Math.floor((double) (point.getDistanceTo(point3) / ((float) 1))), 24.0d));
                    float f = 1.0f / ((float) min);
                    float f2 = 0.0f;
                    for (int i = 0; i < min; i++) {
                        Point smoothPoint = smoothPoint(point, point3, point2, f2);
                        if (this.isFirst) {
                            smoothPoint.edge = true;
                            this.isFirst = false;
                        }
                        vector.add(smoothPoint);
                        f2 += f;
                    }
                    if (z) {
                        point3.edge = true;
                    }
                    vector.add(point3);
                    Point[] pointArr = new Point[vector.size()];
                    vector.toArray(pointArr);
                    paintPath(new Path(pointArr));
                    System.arraycopy(this.points, 1, this.points, 0, 2);
                    if (z) {
                        this.pointsCount = 0;
                    } else {
                        this.pointsCount = 2;
                    }
                }
            }
            return;
        }
        Point[] pointArr2 = new Point[this.pointsCount];
        System.arraycopy(this.points, 0, pointArr2, 0, this.pointsCount);
        paintPath(new Path(pointArr2));
    }

    private Point smoothPoint(Point point, Point point2, Point point3, float f) {
        Point point4 = point;
        Point point5 = point2;
        Point point6 = point3;
        float f2 = 1.0f - f;
        double pow = Math.pow((double) f2, 2.0d);
        double d = (double) ((2.0f * f2) * f);
        double d2 = (double) (f * f);
        return new Point(((point4.f20x * pow) + (point6.f20x * d)) + (point5.f20x * d2), ((point4.f21y * pow) + (point6.f21y * d)) + (point5.f21y * d2), 1.0d);
    }

    private void paintPath(final Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = 0.0d;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new Runnable() {

            /* renamed from: org.telegram.ui.Components.Paint.Input$1$1 */
            class C12001 implements Runnable {
                C12001() {
                }

                public void run() {
                    Input.this.lastRemainder = path.remainder;
                    Input.this.clearBuffer = false;
                }
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C12001());
            }
        });
    }
}
