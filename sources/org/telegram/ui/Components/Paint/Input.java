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

    public Input(RenderView render) {
        this.renderView = render;
    }

    public void setMatrix(Matrix m) {
        this.invertMatrix = new Matrix();
        m.invert(this.invertMatrix);
    }

    public void process(MotionEvent event) {
        int action = event.getActionMasked();
        float y = ((float) this.renderView.getHeight()) - event.getY();
        this.tempPoint[0] = event.getX();
        this.tempPoint[1] = y;
        this.invertMatrix.mapPoints(this.tempPoint);
        Point point = new Point((double) this.tempPoint[0], (double) this.tempPoint[1], 1.0d);
        switch (action) {
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
                } else if (this.pointsCount > 0) {
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

    private void smoothenAndPaintPoints(boolean ended) {
        if (this.pointsCount > 2) {
            Vector<Point> points = new Vector();
            Point prev2 = r0.points[0];
            Point prev1 = r0.points[1];
            Point cur = r0.points[2];
            Vector<Point> vector;
            Point point;
            if (cur == null || prev1 == null) {
                vector = points;
                point = cur;
            } else if (prev2 == null) {
                vector = points;
                point = cur;
            } else {
                Point midPoint1 = prev1.multiplySum(prev2, 0.5d);
                Point midPoint2 = cur.multiplySum(prev1, 0.5d);
                int numberOfSegments = (int) Math.min(48.0d, Math.max(Math.floor((double) (midPoint1.getDistanceTo(midPoint2) / ((float) 1))), 24.0d));
                float step = 1.0f / ((float) numberOfSegments);
                cur = 0.0f;
                for (int j = 0; j < numberOfSegments; j++) {
                    Point point2 = smoothPoint(midPoint1, midPoint2, prev1, cur);
                    if (r0.isFirst) {
                        point2.edge = true;
                        r0.isFirst = false;
                    }
                    points.add(point2);
                    cur += step;
                }
                if (ended) {
                    midPoint2.edge = true;
                }
                points.add(midPoint2);
                Point[] result = new Point[points.size()];
                points.toArray(result);
                paintPath(new Path(result));
                System.arraycopy(r0.points, 1, r0.points, 0, 2);
                if (ended) {
                    r0.pointsCount = 0;
                } else {
                    r0.pointsCount = 2;
                }
            }
            return;
        }
        Point[] result2 = new Point[r0.pointsCount];
        System.arraycopy(r0.points, 0, result2, 0, r0.pointsCount);
        paintPath(new Path(result2));
    }

    private Point smoothPoint(Point midPoint1, Point midPoint2, Point prev1, float t) {
        Point point = midPoint1;
        Point point2 = midPoint2;
        Point point3 = prev1;
        double a1 = Math.pow((double) (1.0f - t), 2.0d);
        double a2 = (double) ((2.0f * (1.0f - t)) * t);
        double a3 = (double) (t * t);
        return new Point(((point.f20x * a1) + (point3.f20x * a2)) + (point2.f20x * a3), ((point.f21y * a1) + (point3.f21y * a2)) + (point2.f21y * a3), 1.0d);
    }

    private void paintPath(final Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = 0.0d;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new Runnable() {

            /* renamed from: org.telegram.ui.Components.Paint.Input$1$1 */
            class C11941 implements Runnable {
                C11941() {
                }

                public void run() {
                    Input.this.lastRemainder = path.remainder;
                    Input.this.clearBuffer = false;
                }
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C11941());
            }
        });
    }
}
