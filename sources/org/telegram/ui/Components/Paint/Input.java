package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.view.MotionEvent;
import java.util.Vector;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Paint.Brush;

public class Input {
    private boolean beganDrawing;
    private boolean clearBuffer;
    private boolean hasMoved;
    private Matrix invertMatrix;
    private boolean isFirst;
    private float lastAngle;
    private Point lastLocation;
    private double lastRemainder;
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

    public void process(MotionEvent motionEvent, float f) {
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
                    if (this.renderView.getCurrentBrush() instanceof Brush.Arrow) {
                        float f2 = this.lastAngle;
                        Point point2 = this.points[this.pointsCount - 1];
                        Point point3 = new Point(point2.x, point2.y, 0.800000011920929d);
                        double d = point2.x;
                        double d2 = (double) f2;
                        Double.isNaN(d2);
                        double cos = Math.cos(d2 - 2.356194490192345d);
                        double currentWeight = (double) (this.renderView.getCurrentWeight() * 4.5f);
                        Double.isNaN(currentWeight);
                        double d3 = d + (cos * currentWeight);
                        double d4 = point2.y;
                        Double.isNaN(d2);
                        double sin = Math.sin(d2 - 2.5132741228718345d);
                        Double.isNaN(currentWeight);
                        Point point4 = new Point(d3, d4 + (sin * currentWeight), 1.0d);
                        point4.edge = true;
                        paintPath(new Path(new Point[]{point3, point4}));
                        double d5 = point2.x;
                        Double.isNaN(d2);
                        double cos2 = Math.cos(2.356194490192345d + d2);
                        Double.isNaN(currentWeight);
                        double d6 = d5 + (cos2 * currentWeight);
                        double d7 = point2.y;
                        Double.isNaN(d2);
                        double sin2 = Math.sin(d2 + 2.5132741228718345d);
                        Double.isNaN(currentWeight);
                        Point point5 = new Point(d6, d7 + (sin2 * currentWeight), 1.0d);
                        point5.edge = true;
                        paintPath(new Path(new Point[]{point3, point5}));
                    }
                }
                this.pointsCount = 0;
                this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
                this.beganDrawing = false;
                this.renderView.onFinishedDrawing(this.hasMoved);
                return;
            } else if (actionMasked != 2) {
                if (actionMasked == 3) {
                    this.renderView.getPainting().clearStroke();
                    this.pointsCount = 0;
                    this.beganDrawing = false;
                    return;
                }
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
        } else if (point.getDistanceTo(this.lastLocation) >= ((float) AndroidUtilities.dp(5.0f)) / f) {
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
                this.lastAngle = (float) Math.atan2(pointArr[2].y - pointArr[1].y, pointArr[2].x - pointArr[1].x);
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

    private void paintPath(Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = 0.0d;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new Runnable(path) {
            public final /* synthetic */ Path f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Input.this.lambda$paintPath$1$Input(this.f$1);
            }
        });
        this.clearBuffer = false;
    }

    public /* synthetic */ void lambda$null$0$Input(Path path) {
        this.lastRemainder = path.remainder;
    }

    public /* synthetic */ void lambda$paintPath$1$Input(Path path) {
        AndroidUtilities.runOnUIThread(new Runnable(path) {
            public final /* synthetic */ Path f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Input.this.lambda$null$0$Input(this.f$1);
            }
        });
    }
}
