package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Render {
    public static RectF RenderPath(Path path, RenderState renderState) {
        renderState.baseWeight = path.getBaseWeight();
        renderState.spacing = path.getBrush().getSpacing();
        renderState.alpha = path.getBrush().getAlpha();
        renderState.angle = path.getBrush().getAngle();
        renderState.scale = path.getBrush().getScale();
        int length = path.getLength();
        if (length == 0) {
            return null;
        }
        int i = 0;
        if (length == 1) {
            PaintStamp(path.getPoints()[0], renderState);
        } else {
            Point[] points = path.getPoints();
            renderState.prepare();
            while (i < points.length - 1) {
                Point point = points[i];
                i++;
                PaintSegment(point, points[i], renderState);
            }
        }
        path.remainder = renderState.remainder;
        return Draw(renderState);
    }

    private static void PaintSegment(org.telegram.ui.Components.Paint.Point r23, org.telegram.ui.Components.Paint.Point r24, org.telegram.ui.Components.Paint.RenderState r25) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r3_0 org.telegram.ui.Components.Paint.Point) in PHI: PHI: (r3_4 org.telegram.ui.Components.Paint.Point) = (r3_0 org.telegram.ui.Components.Paint.Point), (r3_3 org.telegram.ui.Components.Paint.Point) binds: {(r3_0 org.telegram.ui.Components.Paint.Point)=B:6:0x0048, (r3_3 org.telegram.ui.Components.Paint.Point)=B:7:0x004a}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = r23;
        r1 = r24;
        r8 = r25;
        r2 = r23.getDistanceTo(r24);
        r9 = (double) r2;
        r2 = r1.substract(r0);
        r3 = new org.telegram.ui.Components.Paint.Point;
        r12 = 460718241NUM; // 0x3ff000NUM float:0.0 double:1.0;
        r14 = 460718241NUM; // 0x3ff000NUM float:0.0 double:1.0;
        r16 = 0;
        r11 = r3;
        r11.<init>(r12, r14, r16);
        r4 = r8.angle;
        r4 = java.lang.Math.abs(r4);
        r5 = 0;
        r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1));
        if (r4 <= 0) goto L_0x002a;
    L_0x0026:
        r4 = r8.angle;
    L_0x0028:
        r11 = r4;
        goto L_0x0034;
    L_0x002a:
        r4 = r2.f21y;
        r6 = r2.f20x;
        r4 = java.lang.Math.atan2(r4, r6);
        r4 = (float) r4;
        goto L_0x0028;
    L_0x0034:
        r4 = r8.baseWeight;
        r5 = r8.scale;
        r12 = r4 * r5;
        r4 = r8.spacing;
        r4 = r4 * r12;
        r5 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = java.lang.Math.max(r5, r4);
        r13 = (double) r4;
        r6 = 0;
        r4 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1));
        if (r4 <= 0) goto L_0x0051;
    L_0x004a:
        r3 = 460718241NUM; // 0x3ff000NUM float:0.0 double:1.0;
        r3 = r3 / r9;
        r3 = r2.multiplyByScalar(r3);
    L_0x0051:
        r15 = r3;
        r2 = r8.alpha;
        r3 = NUM; // 0x3f933333 float:1.15 double:5.26976103E-315;
        r2 = r2 * r3;
        r16 = java.lang.Math.min(r5, r2);
        r2 = r0.edge;
        r7 = r1.edge;
        r3 = r8.remainder;
        r3 = r9 - r3;
        r3 = r3 / r13;
        r3 = java.lang.Math.ceil(r3);
        r3 = (int) r3;
        r4 = r25.getCount();
        r8.appendValuesCount(r3);
        r8.setPosition(r4);
        r3 = r8.remainder;
        r3 = r15.multiplyByScalar(r3);
        r0 = r0.add(r3);
        r3 = r8.remainder;
        r6 = 1;
        r17 = r3;
        r3 = r6;
    L_0x0084:
        r4 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1));
        if (r4 > 0) goto L_0x00b9;
    L_0x0088:
        if (r2 == 0) goto L_0x008d;
    L_0x008a:
        r19 = r16;
        goto L_0x0091;
    L_0x008d:
        r2 = r8.alpha;
        r19 = r2;
    L_0x0091:
        r3 = r0.toPointF();
        r20 = -1;
        r2 = r8;
        r4 = r12;
        r5 = r11;
        r21 = r9;
        r9 = r6;
        r6 = r19;
        r10 = r7;
        r7 = r20;
        r3 = r2.addPoint(r3, r4, r5, r6, r7);
        if (r3 != 0) goto L_0x00a9;
    L_0x00a8:
        goto L_0x00bd;
    L_0x00a9:
        r2 = r15.multiplyByScalar(r13);
        r0 = r0.add(r2);
        r2 = 0;
        r17 = r17 + r13;
        r6 = r9;
        r7 = r10;
        r9 = r21;
        goto L_0x0084;
    L_0x00b9:
        r21 = r9;
        r9 = r6;
        r10 = r7;
    L_0x00bd:
        if (r3 == 0) goto L_0x00d1;
    L_0x00bf:
        if (r10 == 0) goto L_0x00d1;
    L_0x00c1:
        r8.appendValuesCount(r9);
        r1 = r24.toPointF();
        r5 = -1;
        r0 = r8;
        r2 = r12;
        r3 = r11;
        r4 = r16;
        r0.addPoint(r1, r2, r3, r4, r5);
    L_0x00d1:
        r0 = r17 - r21;
        r8.remainder = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Render.PaintSegment(org.telegram.ui.Components.Paint.Point, org.telegram.ui.Components.Paint.Point, org.telegram.ui.Components.Paint.RenderState):void");
    }

    private static void PaintStamp(Point point, RenderState renderState) {
        float f = renderState.baseWeight * renderState.scale;
        PointF toPointF = point.toPointF();
        float f2 = Math.abs(renderState.angle) > null ? renderState.angle : 0.0f;
        float f3 = renderState.alpha;
        renderState.prepare();
        renderState.appendValuesCount(1);
        renderState.addPoint(toPointF, f, f2, f3, 0);
    }

    private static RectF Draw(RenderState renderState) {
        RectF rectF = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        int count = renderState.getCount();
        if (count == 0) {
            return rectF;
        }
        int i = count - 1;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(20 * ((count * 4) + (i * 2)));
        allocateDirect.order(ByteOrder.nativeOrder());
        FloatBuffer asFloatBuffer = allocateDirect.asFloatBuffer();
        int i2 = 0;
        asFloatBuffer.position(0);
        renderState.setPosition(0);
        int i3 = 0;
        int i4 = i3;
        while (i3 < count) {
            int i5;
            float f;
            float read = renderState.read();
            float read2 = renderState.read();
            float read3 = renderState.read();
            float read4 = renderState.read();
            float read5 = renderState.read();
            RectF rectF2 = new RectF(read - read3, read2 - read3, read + read3, read2 + read3);
            float[] fArr = new float[]{rectF2.left, rectF2.top, rectF2.right, rectF2.top, rectF2.left, rectF2.bottom, rectF2.right, rectF2.bottom};
            float centerX = rectF2.centerX();
            read3 = rectF2.centerY();
            Matrix matrix = new Matrix();
            int i6 = i;
            matrix.setRotate((float) Math.toDegrees((double) read4), centerX, read3);
            matrix.mapPoints(fArr);
            matrix.mapRect(rectF2);
            Utils.RectFIntegral(rectF2);
            rectF.union(rectF2);
            if (i4 != 0) {
                i5 = 0;
                asFloatBuffer.put(fArr[0]);
                i2 = 1;
                asFloatBuffer.put(fArr[1]);
                f = 0.0f;
                asFloatBuffer.put(0.0f);
                asFloatBuffer.put(0.0f);
                asFloatBuffer.put(read5);
                i4++;
            } else {
                i5 = 0;
                i2 = 1;
                f = 0.0f;
            }
            asFloatBuffer.put(fArr[i5]);
            asFloatBuffer.put(fArr[i2]);
            asFloatBuffer.put(f);
            asFloatBuffer.put(f);
            asFloatBuffer.put(read5);
            i4 += i2;
            asFloatBuffer.put(fArr[2]);
            asFloatBuffer.put(fArr[3]);
            asFloatBuffer.put(1.0f);
            asFloatBuffer.put(f);
            asFloatBuffer.put(read5);
            i4 += i2;
            asFloatBuffer.put(fArr[4]);
            asFloatBuffer.put(fArr[5]);
            asFloatBuffer.put(f);
            asFloatBuffer.put(1.0f);
            asFloatBuffer.put(read5);
            i4 += i2;
            asFloatBuffer.put(fArr[6]);
            asFloatBuffer.put(fArr[7]);
            asFloatBuffer.put(1.0f);
            asFloatBuffer.put(1.0f);
            asFloatBuffer.put(read5);
            i4 += i2;
            i2 = i6;
            if (i3 != i2) {
                asFloatBuffer.put(fArr[6]);
                asFloatBuffer.put(fArr[7]);
                asFloatBuffer.put(1.0f);
                asFloatBuffer.put(1.0f);
                asFloatBuffer.put(read5);
                i4++;
            }
            i3++;
            read4 = f;
            i = i2;
            i2 = 0;
        }
        asFloatBuffer.position(i2);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 20, asFloatBuffer.slice());
        GLES20.glEnableVertexAttribArray(0);
        asFloatBuffer.position(2);
        GLES20.glVertexAttribPointer(1, 2, 5126, true, 20, asFloatBuffer.slice());
        GLES20.glEnableVertexAttribArray(1);
        asFloatBuffer.position(4);
        GLES20.glVertexAttribPointer(2, 1, 5126, true, 20, asFloatBuffer.slice());
        GLES20.glEnableVertexAttribArray(2);
        GLES20.glDrawArrays(5, 0, i4);
        return rectF;
    }
}
