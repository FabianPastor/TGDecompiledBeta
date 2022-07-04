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

    public Input(RenderView render) {
        this.renderView = render;
    }

    public void setMatrix(Matrix m) {
        Matrix matrix = new Matrix();
        this.invertMatrix = matrix;
        m.invert(matrix);
    }

    public void process(MotionEvent event, float scale) {
        int i;
        int action = event.getActionMasked();
        float x = event.getX();
        float y = ((float) this.renderView.getHeight()) - event.getY();
        float[] fArr = this.tempPoint;
        fArr[0] = x;
        fArr[1] = y;
        this.invertMatrix.mapPoints(fArr);
        float[] fArr2 = this.tempPoint;
        Point point = new Point((double) fArr2[0], (double) fArr2[1], 1.0d);
        switch (action) {
            case 0:
            case 2:
                float f = x;
                float f2 = y;
                if (this.beganDrawing == 0) {
                    this.beganDrawing = true;
                    this.hasMoved = false;
                    this.isFirst = true;
                    this.lastLocation = point;
                    this.points[0] = point;
                    this.pointsCount = 1;
                    this.clearBuffer = true;
                    return;
                } else if (point.getDistanceTo(this.lastLocation) >= ((float) AndroidUtilities.dp(5.0f)) / scale) {
                    if (!this.hasMoved) {
                        this.renderView.onBeganDrawing();
                        i = 1;
                        this.hasMoved = true;
                    } else {
                        i = 1;
                    }
                    Point[] pointArr = this.points;
                    int i2 = this.pointsCount;
                    pointArr[i2] = point;
                    int i3 = i2 + i;
                    this.pointsCount = i3;
                    if (i3 == 3) {
                        this.lastAngle = (float) Math.atan2(pointArr[2].y - this.points[i].y, this.points[2].x - this.points[i].x);
                        smoothenAndPaintPoints(false);
                    }
                    this.lastLocation = point;
                    return;
                } else {
                    return;
                }
            case 1:
                if (!this.hasMoved) {
                    if (this.renderView.shouldDraw()) {
                        point.edge = true;
                        paintPath(new Path(point));
                    }
                    reset();
                    int i4 = action;
                    float f3 = x;
                    float f4 = y;
                } else if (this.pointsCount > 0) {
                    smoothenAndPaintPoints(true);
                    Brush brush = this.renderView.getCurrentBrush();
                    if (brush instanceof Brush.Arrow) {
                        float arrowLength = this.renderView.getCurrentWeight() * 4.5f;
                        float angle = this.lastAngle;
                        Point location = this.points[this.pointsCount - 1];
                        Point tip = new Point(location.x, location.y, 0.800000011920929d);
                        double d = location.x;
                        double d2 = (double) angle;
                        Double.isNaN(d2);
                        double cos = Math.cos(d2 - 2.356194490192345d);
                        Brush brush2 = brush;
                        double d3 = (double) arrowLength;
                        Double.isNaN(d3);
                        double d4 = location.y;
                        double d5 = (double) angle;
                        Double.isNaN(d5);
                        double sin = Math.sin(d5 - 2.5132741228718345d);
                        int i5 = action;
                        float f5 = x;
                        double d6 = (double) arrowLength;
                        Double.isNaN(d6);
                        Point leftTip = new Point(d + (cos * d3), d4 + (sin * d6), 1.0d);
                        leftTip.edge = true;
                        Path left = new Path(new Point[]{tip, leftTip});
                        paintPath(left);
                        double d7 = location.x;
                        double d8 = (double) angle;
                        Double.isNaN(d8);
                        double cos2 = Math.cos(d8 + 2.356194490192345d);
                        double d9 = (double) arrowLength;
                        Double.isNaN(d9);
                        double d10 = d7 + (cos2 * d9);
                        double d11 = location.y;
                        Point point2 = leftTip;
                        Path path = left;
                        double d12 = (double) angle;
                        Double.isNaN(d12);
                        double sin2 = Math.sin(d12 + 2.5132741228718345d);
                        float f6 = y;
                        Point location2 = location;
                        double d13 = (double) arrowLength;
                        Double.isNaN(d13);
                        Point rightTip = new Point(d10, d11 + (sin2 * d13), 1.0d);
                        rightTip.edge = true;
                        paintPath(new Path(new Point[]{tip, rightTip}));
                        Point point3 = location2;
                    } else {
                        float f7 = x;
                        float f8 = y;
                        Brush brush3 = brush;
                    }
                } else {
                    float f9 = x;
                    float var_ = y;
                }
                this.pointsCount = 0;
                this.renderView.getPainting().commitStroke(this.renderView.getCurrentColor());
                this.beganDrawing = false;
                this.renderView.onFinishedDrawing(this.hasMoved);
                return;
            case 3:
                this.renderView.getPainting().clearStroke();
                this.pointsCount = 0;
                this.beganDrawing = false;
                int i6 = action;
                float var_ = x;
                float var_ = y;
                return;
            default:
                int i7 = action;
                float var_ = x;
                float var_ = y;
                return;
        }
    }

    private void reset() {
        this.pointsCount = 0;
    }

    private void smoothenAndPaintPoints(boolean ended) {
        int i = this.pointsCount;
        if (i > 2) {
            Vector<Point> points2 = new Vector<>();
            Point[] pointArr = this.points;
            Point prev2 = pointArr[0];
            Point prev1 = pointArr[1];
            Point cur = pointArr[2];
            if (cur == null || prev1 == null) {
                Vector<Point> vector = points2;
            } else if (prev2 == null) {
                Vector<Point> vector2 = points2;
            } else {
                Point midPoint1 = prev1.multiplySum(prev2, 0.5d);
                Point midPoint2 = cur.multiplySum(prev1, 0.5d);
                int numberOfSegments = (int) Math.min(48.0d, Math.max(Math.floor((double) (midPoint1.getDistanceTo(midPoint2) / ((float) 1))), 24.0d));
                float t = 0.0f;
                float step = 1.0f / ((float) numberOfSegments);
                for (int j = 0; j < numberOfSegments; j++) {
                    Point point = smoothPoint(midPoint1, midPoint2, prev1, t);
                    if (this.isFirst) {
                        point.edge = true;
                        this.isFirst = false;
                    }
                    points2.add(point);
                    t += step;
                }
                if (ended) {
                    midPoint2.edge = true;
                }
                points2.add(midPoint2);
                Point[] result = new Point[points2.size()];
                points2.toArray(result);
                paintPath(new Path(result));
                Point[] pointArr2 = this.points;
                Vector<Point> vector3 = points2;
                int i2 = numberOfSegments;
                System.arraycopy(pointArr2, 1, pointArr2, 0, 2);
                if (ended) {
                    this.pointsCount = 0;
                } else {
                    this.pointsCount = 2;
                }
            }
        } else {
            Point[] result2 = new Point[i];
            System.arraycopy(this.points, 0, result2, 0, i);
            paintPath(new Path(result2));
        }
    }

    private Point smoothPoint(Point midPoint1, Point midPoint2, Point prev1, float t) {
        Point point = midPoint1;
        Point point2 = midPoint2;
        Point point3 = prev1;
        double a1 = Math.pow((double) (1.0f - t), 2.0d);
        double a2 = (double) ((1.0f - t) * 2.0f * t);
        double a3 = (double) (t * t);
        double d = point3.x;
        Double.isNaN(a2);
        double d2 = (point.x * a1) + (d * a2);
        double d3 = point2.x;
        Double.isNaN(a3);
        double d4 = point.y * a1;
        double d5 = a1;
        double a12 = point3.y;
        Double.isNaN(a2);
        double d6 = d4 + (a12 * a2);
        double d7 = point2.y;
        Double.isNaN(a3);
        return new Point(d2 + (d3 * a3), d6 + (d7 * a3), 1.0d);
    }

    private void paintPath(Path path) {
        path.setup(this.renderView.getCurrentColor(), this.renderView.getCurrentWeight(), this.renderView.getCurrentBrush());
        if (this.clearBuffer) {
            this.lastRemainder = 0.0d;
        }
        path.remainder = this.lastRemainder;
        this.renderView.getPainting().paintStroke(path, this.clearBuffer, new Input$$ExternalSyntheticLambda1(this, path));
        this.clearBuffer = false;
    }

    /* renamed from: lambda$paintPath$0$org-telegram-ui-Components-Paint-Input  reason: not valid java name */
    public /* synthetic */ void m1120lambda$paintPath$0$orgtelegramuiComponentsPaintInput(Path path) {
        this.lastRemainder = path.remainder;
    }

    /* renamed from: lambda$paintPath$1$org-telegram-ui-Components-Paint-Input  reason: not valid java name */
    public /* synthetic */ void m1121lambda$paintPath$1$orgtelegramuiComponentsPaintInput(Path path) {
        AndroidUtilities.runOnUIThread(new Input$$ExternalSyntheticLambda0(this, path));
    }
}
