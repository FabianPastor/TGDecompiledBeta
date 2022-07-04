package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Render {
    public static RectF RenderPath(Path path, RenderState state) {
        state.baseWeight = path.getBaseWeight();
        state.spacing = path.getBrush().getSpacing();
        state.alpha = path.getBrush().getAlpha();
        state.angle = path.getBrush().getAngle();
        state.scale = path.getBrush().getScale();
        int length = path.getLength();
        if (length == 0) {
            return null;
        }
        if (length == 1) {
            PaintStamp(path.getPoints()[0], state);
        } else {
            Point[] points = path.getPoints();
            state.prepare();
            for (int i = 0; i < points.length - 1; i++) {
                PaintSegment(points[i], points[i + 1], state);
            }
        }
        path.remainder = state.remainder;
        return Draw(state);
    }

    private static void PaintSegment(Point lastPoint, Point point, RenderState state) {
        Point unitVector;
        Point point2 = lastPoint;
        Point point3 = point;
        RenderState renderState = state;
        double distance = (double) lastPoint.getDistanceTo(point);
        Point vector = point3.substract(point2);
        Point unitVector2 = new Point(1.0d, 1.0d, 0.0d);
        float vectorAngle = Math.abs(renderState.angle) > 0.0f ? renderState.angle : (float) Math.atan2(vector.y, vector.x);
        float brushWeight = ((renderState.baseWeight * renderState.scale) * 1.0f) / renderState.viewportScale;
        double step = (double) Math.max(1.0f, renderState.spacing * brushWeight);
        if (distance > 0.0d) {
            Double.isNaN(distance);
            unitVector = vector.multiplyByScalar(1.0d / distance);
        } else {
            unitVector = unitVector2;
        }
        float boldenedAlpha = Math.min(1.0f, renderState.alpha * 1.15f);
        boolean boldenHead = point2.edge;
        boolean boldenTail = point3.edge;
        double d = renderState.remainder;
        Double.isNaN(distance);
        Double.isNaN(step);
        int count = (int) Math.ceil((distance - d) / step);
        int currentCount = state.getCount();
        renderState.appendValuesCount(count);
        renderState.setPosition(currentCount);
        double f = renderState.remainder;
        boolean succeed = true;
        boolean boldenHead2 = boldenHead;
        Point start = point2.add(unitVector.multiplyByScalar(renderState.remainder));
        while (true) {
            if (f > distance) {
                int i = count;
                break;
            }
            Point start2 = start;
            int currentCount2 = currentCount;
            int count2 = count;
            succeed = state.addPoint(start.toPointF(), brushWeight, vectorAngle, boldenHead2 ? boldenedAlpha : renderState.alpha, -1);
            if (!succeed) {
                Point point4 = start2;
                break;
            }
            start = start2.add(unitVector.multiplyByScalar(step));
            boldenHead2 = false;
            Double.isNaN(step);
            f += step;
            currentCount = currentCount2;
            count = count2;
        }
        if (!succeed || !boldenTail) {
            Point point5 = unitVector;
            double d2 = step;
        } else {
            renderState.appendValuesCount(1);
            boolean z = boldenTail;
            Point point6 = unitVector;
            double d3 = step;
            state.addPoint(point.toPointF(), brushWeight, vectorAngle, boldenedAlpha, -1);
        }
        Double.isNaN(distance);
        renderState.remainder = f - distance;
    }

    private static void PaintStamp(Point point, RenderState state) {
        float brushWeight = ((state.baseWeight * state.scale) * 1.0f) / state.viewportScale;
        PointF start = point.toPointF();
        float angle = Math.abs(state.angle) > 0.0f ? state.angle : 0.0f;
        float alpha = state.alpha;
        state.prepare();
        state.appendValuesCount(1);
        state.addPoint(start, brushWeight, angle, alpha, 0);
    }

    private static RectF Draw(RenderState state) {
        RectF dataBounds = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        int count = state.getCount();
        if (count == 0) {
            return dataBounds;
        }
        int capacity = 20 * ((count * 4) + ((count - 1) * 2));
        ByteBuffer bb = ByteBuffer.allocateDirect(capacity);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexData = bb.asFloatBuffer();
        vertexData.position(0);
        state.setPosition(0);
        int i = 0;
        int n = 0;
        while (i < count) {
            float x = state.read();
            float y = state.read();
            float size = state.read();
            float angle = state.read();
            float alpha = state.read();
            int capacity2 = capacity;
            float f = x;
            RectF rect = new RectF(x - size, y - size, x + size, y + size);
            float[] points = {rect.left, rect.top, rect.right, rect.top, rect.left, rect.bottom, rect.right, rect.bottom};
            float centerX = rect.centerX();
            float centerY = rect.centerY();
            ByteBuffer bb2 = bb;
            Matrix t = new Matrix();
            t.setRotate((float) Math.toDegrees((double) angle), centerX, centerY);
            t.mapPoints(points);
            t.mapRect(rect);
            Utils.RectFIntegral(rect);
            dataBounds.union(rect);
            if (n != 0) {
                float f2 = angle;
                vertexData.put(points[0]);
                vertexData.put(points[1]);
                vertexData.put(0.0f);
                vertexData.put(0.0f);
                vertexData.put(alpha);
                n++;
            }
            vertexData.put(points[0]);
            vertexData.put(points[1]);
            vertexData.put(0.0f);
            vertexData.put(0.0f);
            vertexData.put(alpha);
            vertexData.put(points[2]);
            vertexData.put(points[3]);
            vertexData.put(1.0f);
            vertexData.put(0.0f);
            vertexData.put(alpha);
            vertexData.put(points[4]);
            vertexData.put(points[5]);
            vertexData.put(0.0f);
            vertexData.put(1.0f);
            vertexData.put(alpha);
            vertexData.put(points[6]);
            vertexData.put(points[7]);
            vertexData.put(1.0f);
            vertexData.put(1.0f);
            vertexData.put(alpha);
            n = n + 1 + 1 + 1 + 1;
            if (i != count - 1) {
                vertexData.put(points[6]);
                vertexData.put(points[7]);
                vertexData.put(1.0f);
                vertexData.put(1.0f);
                vertexData.put(alpha);
                n++;
            }
            i++;
            capacity = capacity2;
            bb = bb2;
        }
        ByteBuffer byteBuffer = bb;
        vertexData.position(0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 20, vertexData.slice());
        GLES20.glEnableVertexAttribArray(0);
        vertexData.position(2);
        GLES20.glVertexAttribPointer(1, 2, 5126, true, 20, vertexData.slice());
        GLES20.glEnableVertexAttribArray(1);
        vertexData.position(4);
        GLES20.glVertexAttribPointer(2, 1, 5126, true, 20, vertexData.slice());
        GLES20.glEnableVertexAttribArray(2);
        GLES20.glDrawArrays(5, 0, n);
        return dataBounds;
    }
}
