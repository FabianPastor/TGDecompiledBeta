package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
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

    /* JADX WARNING: Missing block: B:4:0x0036, code skipped:
            if (r0 != 2) goto L_0x00bb;
     */
    public void process(android.view.MotionEvent r12) {
        /*
        r11 = this;
        r0 = r12.getActionMasked();
        r1 = r12.getX();
        r2 = r11.renderView;
        r2 = r2.getHeight();
        r2 = (float) r2;
        r12 = r12.getY();
        r2 = r2 - r12;
        r12 = r11.tempPoint;
        r3 = 0;
        r12[r3] = r1;
        r1 = 1;
        r12[r1] = r2;
        r2 = r11.invertMatrix;
        r2.mapPoints(r12);
        r12 = new org.telegram.ui.Components.Paint.Point;
        r2 = r11.tempPoint;
        r4 = r2[r3];
        r5 = (double) r4;
        r2 = r2[r1];
        r7 = (double) r2;
        r9 = NUM; // 0x3ffNUM float:0.0 double:1.0;
        r4 = r12;
        r4.<init>(r5, r7, r9);
        if (r0 == 0) goto L_0x0076;
    L_0x0033:
        if (r0 == r1) goto L_0x003a;
    L_0x0035:
        r2 = 2;
        if (r0 == r2) goto L_0x0076;
    L_0x0038:
        goto L_0x00bb;
    L_0x003a:
        r0 = r11.hasMoved;
        if (r0 != 0) goto L_0x0054;
    L_0x003e:
        r0 = r11.renderView;
        r0 = r0.shouldDraw();
        if (r0 == 0) goto L_0x0050;
    L_0x0046:
        r12.edge = r1;
        r0 = new org.telegram.ui.Components.Paint.Path;
        r0.<init>(r12);
        r11.paintPath(r0);
    L_0x0050:
        r11.reset();
        goto L_0x005b;
    L_0x0054:
        r12 = r11.pointsCount;
        if (r12 <= 0) goto L_0x005b;
    L_0x0058:
        r11.smoothenAndPaintPoints(r1);
    L_0x005b:
        r11.pointsCount = r3;
        r12 = r11.renderView;
        r12 = r12.getPainting();
        r0 = r11.renderView;
        r0 = r0.getCurrentColor();
        r12.commitStroke(r0);
        r11.beganDrawing = r3;
        r12 = r11.renderView;
        r0 = r11.hasMoved;
        r12.onFinishedDrawing(r0);
        goto L_0x00bb;
    L_0x0076:
        r0 = r11.beganDrawing;
        if (r0 != 0) goto L_0x008b;
    L_0x007a:
        r11.beganDrawing = r1;
        r11.hasMoved = r3;
        r11.isFirst = r1;
        r11.lastLocation = r12;
        r0 = r11.points;
        r0[r3] = r12;
        r11.pointsCount = r1;
        r11.clearBuffer = r1;
        goto L_0x00bb;
    L_0x008b:
        r0 = r11.lastLocation;
        r0 = r12.getDistanceTo(r0);
        r2 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x009d;
    L_0x009c:
        return;
    L_0x009d:
        r0 = r11.hasMoved;
        if (r0 != 0) goto L_0x00a8;
    L_0x00a1:
        r0 = r11.renderView;
        r0.onBeganDrawing();
        r11.hasMoved = r1;
    L_0x00a8:
        r0 = r11.points;
        r2 = r11.pointsCount;
        r0[r2] = r12;
        r2 = r2 + r1;
        r11.pointsCount = r2;
        r0 = r11.pointsCount;
        r1 = 3;
        if (r0 != r1) goto L_0x00b9;
    L_0x00b6:
        r11.smoothenAndPaintPoints(r3);
    L_0x00b9:
        r11.lastLocation = r12;
    L_0x00bb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Input.process(android.view.MotionEvent):void");
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
                point = point2.multiplySum(point, 0.5d);
                point3 = point3.multiplySum(point2, 0.5d);
                int min = (int) Math.min(48.0d, Math.max(Math.floor((double) (point.getDistanceTo(point3) / ((float) 1))), 24.0d));
                float f = 1.0f / ((float) min);
                float f2 = 0.0f;
                for (int i2 = 0; i2 < min; i2++) {
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
                pointArr = new Point[vector.size()];
                vector.toArray(pointArr);
                paintPath(new Path(pointArr));
                Point[] pointArr2 = this.points;
                System.arraycopy(pointArr2, 1, pointArr2, 0, 2);
                if (z) {
                    this.pointsCount = 0;
                } else {
                    this.pointsCount = 2;
                }
            } else {
                return;
            }
        }
        Point[] pointArr3 = new Point[i];
        System.arraycopy(this.points, 0, pointArr3, 0, i);
        paintPath(new Path(pointArr3));
    }

    private Point smoothPoint(Point point, Point point2, Point point3, float f) {
        Point point4 = point;
        Point point5 = point2;
        Point point6 = point3;
        float f2 = 1.0f - f;
        double pow = Math.pow((double) f2, 2.0d);
        double d = (double) ((f2 * 2.0f) * f);
        double d2 = (double) (f * f);
        double d3 = point4.x * pow;
        double d4 = point6.x;
        Double.isNaN(d);
        d3 += d4 * d;
        d4 = point5.x;
        Double.isNaN(d2);
        double d5 = d3 + (d4 * d2);
        double d6 = point4.y * pow;
        pow = point6.y;
        Double.isNaN(d);
        d6 += pow * d;
        double d7 = point5.y;
        Double.isNaN(d2);
        return new Point(d5, d6 + (d7 * d2), 1.0d);
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
                        AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                        Input.this.lastRemainder = path.remainder;
                        Input.this.clearBuffer = false;
                    }
                });
            }
        });
    }
}
