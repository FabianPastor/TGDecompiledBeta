package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;

public class CircleBezierDrawable {
    private final float L;
    private final int N;
    public float cubicBezierK = 1.0f;
    float globalRotate = 0.0f;
    public float idleStateDiff = 0.0f;
    private Matrix m = new Matrix();
    private Path path = new Path();
    private float[] pointEnd = new float[4];
    private float[] pointStart = new float[4];
    public float radius;
    public float radiusDiff;
    final Random random = new Random();
    float[] randomAdditionals;
    public float randomK;

    public CircleBezierDrawable(int n) {
        this.N = n;
        double d = (double) (n * 2);
        Double.isNaN(d);
        this.L = (float) (Math.tan(3.141592653589793d / d) * 1.3333333333333333d);
        this.randomAdditionals = new float[n];
        calculateRandomAdditionals();
    }

    public void calculateRandomAdditionals() {
        for (int i = 0; i < this.N; i++) {
            this.randomAdditionals[i] = ((float) (this.random.nextInt() % 100)) / 100.0f;
        }
    }

    public void setAdditionals(int[] additionals) {
        for (int i = 0; i < this.N; i += 2) {
            float[] fArr = this.randomAdditionals;
            fArr[i] = (float) additionals[i / 2];
            fArr[i + 1] = 0.0f;
        }
    }

    public void draw(float cX, float cY, Canvas canvas, Paint paint) {
        float f = cX;
        float f2 = cY;
        Canvas canvas2 = canvas;
        float f3 = this.radius;
        float f4 = this.idleStateDiff;
        float f5 = this.radiusDiff;
        float r1 = (f3 - (f4 / 2.0f)) - (f5 / 2.0f);
        float r2 = f3 + (f5 / 2.0f) + (f4 / 2.0f);
        float l = this.L * Math.max(r1, r2) * this.cubicBezierK;
        this.path.reset();
        for (int i = 0; i < this.N; i++) {
            this.m.reset();
            this.m.setRotate((360.0f / ((float) this.N)) * ((float) i), f, f2);
            float f6 = i % 2 == 0 ? r1 : r2;
            float f7 = this.randomK;
            float[] fArr = this.randomAdditionals;
            float r = f6 + (fArr[i] * f7);
            float[] fArr2 = this.pointStart;
            fArr2[0] = f;
            fArr2[1] = f2 - r;
            fArr2[2] = f + l + (f7 * fArr[i] * this.L);
            fArr2[3] = f2 - r;
            this.m.mapPoints(fArr2);
            int j = i + 1;
            if (j >= this.N) {
                j = 0;
            }
            float f8 = j % 2 == 0 ? r1 : r2;
            float f9 = this.randomK;
            float[] fArr3 = this.randomAdditionals;
            float r3 = f8 + (fArr3[j] * f9);
            float[] fArr4 = this.pointEnd;
            fArr4[0] = f;
            fArr4[1] = f2 - r3;
            fArr4[2] = (f - l) + (f9 * fArr3[j] * this.L);
            fArr4[3] = f2 - r3;
            this.m.reset();
            this.m.setRotate((360.0f / ((float) this.N)) * ((float) j), f, f2);
            this.m.mapPoints(this.pointEnd);
            if (i == 0) {
                Path path2 = this.path;
                float[] fArr5 = this.pointStart;
                path2.moveTo(fArr5[0], fArr5[1]);
            }
            Path path3 = this.path;
            float[] fArr6 = this.pointStart;
            float var_ = fArr6[2];
            float var_ = fArr6[3];
            float[] fArr7 = this.pointEnd;
            path3.cubicTo(var_, var_, fArr7[2], fArr7[3], fArr7[0], fArr7[1]);
        }
        canvas.save();
        canvas2.rotate(this.globalRotate, f, f2);
        canvas2.drawPath(this.path, paint);
        canvas.restore();
    }

    public void setRandomAdditions(float randomK2) {
        this.randomK = randomK2;
    }
}
