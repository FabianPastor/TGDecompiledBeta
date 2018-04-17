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
        int i = 0;
        if (length == 1) {
            PaintStamp(path.getPoints()[0], state);
        } else {
            Point[] points = path.getPoints();
            state.prepare();
            while (i < points.length - 1) {
                PaintSegment(points[i], points[i + 1], state);
                i++;
            }
        }
        path.remainder = state.remainder;
        return Draw(state);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void PaintSegment(Point lastPoint, Point point, RenderState state) {
        double f;
        int count;
        Point point2;
        double d;
        Point point3 = lastPoint;
        Point point4 = point;
        RenderState renderState = state;
        double distance = (double) lastPoint.getDistanceTo(point);
        Point vector = point4.substract(point3);
        Point unitVector = new Point(1.0d, 1.0d, 0.0d);
        float vectorAngle = Math.abs(renderState.angle) > 0.0f ? renderState.angle : (float) Math.atan2(vector.f21y, vector.f20x);
        float brushWeight = renderState.baseWeight * renderState.scale;
        double step = (double) Math.max(1.0f, renderState.spacing * brushWeight);
        if (distance > 0.0d) {
            unitVector = vector.multiplyByScalar(1.0d / distance);
        }
        Point unitVector2 = unitVector;
        float boldenedAlpha = Math.min(1.0f, renderState.alpha * 1.15f);
        boolean boldenHead = point3.edge;
        boolean boldenTail = point4.edge;
        int count2 = (int) Math.ceil((distance - renderState.remainder) / step);
        int currentCount = state.getCount();
        renderState.appendValuesCount(count2);
        renderState.setPosition(currentCount);
        boolean boldenHead2 = boldenHead;
        Point start = point3.add(unitVector2.multiplyByScalar(renderState.remainder));
        boolean succeed = true;
        double f2 = renderState.remainder;
        Point start2 = start;
        while (true) {
            f = f2;
            if (f > distance) {
                break;
            }
            Point start3 = start2;
            Point start4 = start3;
            int currentCount2 = currentCount;
            count = count2;
            succeed = renderState.addPoint(start3.toPointF(), brushWeight, vectorAngle, boldenHead2 ? boldenedAlpha : renderState.alpha, -1);
            if (!succeed) {
                break;
            }
            boldenHead2 = false;
            start2 = start4.add(unitVector2.multiplyByScalar(step));
            f2 = f + step;
            currentCount = currentCount2;
            count2 = count;
            if (succeed || !boldenTail) {
                point2 = unitVector2;
                d = step;
            } else {
                renderState.appendValuesCount(1);
                renderState.addPoint(point.toPointF(), brushWeight, vectorAngle, boldenedAlpha, -1);
            }
            renderState.remainder = f - distance;
        }
        count = count2;
        if (succeed) {
        }
        point2 = unitVector2;
        d = step;
        renderState.remainder = f - distance;
    }

    private static void PaintStamp(Point point, RenderState state) {
        float brushWeight = state.baseWeight * state.scale;
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
        ByteBuffer bb;
        int capacity = 20 * ((count * 4) + ((count - 1) * 2));
        ByteBuffer bb2 = ByteBuffer.allocateDirect(capacity);
        bb2.order(ByteOrder.nativeOrder());
        FloatBuffer vertexData = bb2.asFloatBuffer();
        vertexData.position(0);
        state.setPosition(0);
        int n = 0;
        int i = 0;
        while (i < count) {
            float x = state.read();
            float y = state.read();
            float size = state.read();
            float angle = state.read();
            float alpha = state.read();
            RectF rect = new RectF(x - size, y - size, x + size, y + size);
            float[] points = new float[]{rect.left, rect.top, rect.right, rect.top, rect.left, rect.bottom, rect.right, rect.bottom};
            float centerX = rect.centerX();
            float centerY = rect.centerY();
            Matrix t = new Matrix();
            int capacity2 = capacity;
            bb = bb2;
            t.setRotate((float) Math.toDegrees((double) angle), centerX, centerY);
            t.mapPoints(points);
            t.mapRect(rect);
            Utils.RectFIntegral(rect);
            dataBounds.union(rect);
            if (n != 0) {
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
            n++;
            vertexData.put(points[2]);
            vertexData.put(points[3]);
            vertexData.put(1.0f);
            vertexData.put(0.0f);
            vertexData.put(alpha);
            n++;
            vertexData.put(points[4]);
            vertexData.put(points[5]);
            vertexData.put(0.0f);
            vertexData.put(1.0f);
            vertexData.put(alpha);
            n++;
            vertexData.put(points[6]);
            vertexData.put(points[7]);
            vertexData.put(1.0f);
            vertexData.put(1.0f);
            vertexData.put(alpha);
            n++;
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
            bb2 = bb;
        }
        bb = bb2;
        vertexData.position(0);
        int i2 = 1;
        capacity = 4;
        int i3 = 5;
        int i4 = 20;
        int n2 = n;
        GLES20.glVertexAttribPointer(0, 2, 5126, false, i4, vertexData.slice());
        GLES20.glEnableVertexAttribArray(0);
        vertexData.position(2);
        GLES20.glVertexAttribPointer(1, 2, 5126, true, i4, vertexData.slice());
        GLES20.glEnableVertexAttribArray(i2);
        vertexData.position(capacity);
        GLES20.glVertexAttribPointer(2, 1, 5126, true, i4, vertexData.slice());
        GLES20.glEnableVertexAttribArray(2);
        GLES20.glDrawArrays(i3, 0, n2);
        return dataBounds;
    }
}
