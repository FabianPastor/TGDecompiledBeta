package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;
/* loaded from: classes3.dex */
public class CircleBezierDrawable {
    private final float L;
    private final int N;
    public float radius;
    public float radiusDiff;
    float[] randomAdditionals;
    public float randomK;
    private Path path = new Path();
    private float[] pointStart = new float[4];
    private float[] pointEnd = new float[4];
    private Matrix m = new Matrix();
    float globalRotate = 0.0f;
    public float idleStateDiff = 0.0f;
    public float cubicBezierK = 1.0f;
    final Random random = new Random();

    public CircleBezierDrawable(int i) {
        this.N = i;
        double d = i * 2;
        Double.isNaN(d);
        this.L = (float) (Math.tan(3.141592653589793d / d) * 1.3333333333333333d);
        this.randomAdditionals = new float[i];
        calculateRandomAdditionals();
    }

    public void calculateRandomAdditionals() {
        for (int i = 0; i < this.N; i++) {
            this.randomAdditionals[i] = (this.random.nextInt() % 100) / 100.0f;
        }
    }

    public void setAdditionals(int[] iArr) {
        for (int i = 0; i < this.N; i += 2) {
            float[] fArr = this.randomAdditionals;
            fArr[i] = iArr[i / 2];
            fArr[i + 1] = 0.0f;
        }
    }

    public void draw(float f, float f2, Canvas canvas, Paint paint) {
        float f3 = this.radius;
        float f4 = this.idleStateDiff;
        float f5 = this.radiusDiff;
        float f6 = (f3 - (f4 / 2.0f)) - (f5 / 2.0f);
        float f7 = f3 + (f5 / 2.0f) + (f4 / 2.0f);
        float max = this.L * Math.max(f6, f7) * this.cubicBezierK;
        this.path.reset();
        int i = 0;
        while (i < this.N) {
            this.m.reset();
            this.m.setRotate((360.0f / this.N) * i, f, f2);
            float f8 = i % 2 == 0 ? f6 : f7;
            float f9 = this.randomK;
            float[] fArr = this.randomAdditionals;
            float var_ = f8 + (fArr[i] * f9);
            float[] fArr2 = this.pointStart;
            fArr2[0] = f;
            float var_ = f2 - var_;
            fArr2[1] = var_;
            fArr2[2] = f + max + (f9 * fArr[i] * this.L);
            fArr2[3] = var_;
            this.m.mapPoints(fArr2);
            int i2 = i + 1;
            int i3 = i2 >= this.N ? 0 : i2;
            float var_ = i3 % 2 == 0 ? f6 : f7;
            float var_ = this.randomK;
            float[] fArr3 = this.randomAdditionals;
            float[] fArr4 = this.pointEnd;
            fArr4[0] = f;
            float var_ = f2 - (var_ + (fArr3[i3] * var_));
            fArr4[1] = var_;
            fArr4[2] = (f - max) + (var_ * fArr3[i3] * this.L);
            fArr4[3] = var_;
            this.m.reset();
            this.m.setRotate((360.0f / this.N) * i3, f, f2);
            this.m.mapPoints(this.pointEnd);
            if (i == 0) {
                Path path = this.path;
                float[] fArr5 = this.pointStart;
                path.moveTo(fArr5[0], fArr5[1]);
            }
            Path path2 = this.path;
            float[] fArr6 = this.pointStart;
            float var_ = fArr6[2];
            float var_ = fArr6[3];
            float[] fArr7 = this.pointEnd;
            path2.cubicTo(var_, var_, fArr7[2], fArr7[3], fArr7[0], fArr7[1]);
            i = i2;
        }
        canvas.save();
        canvas.rotate(this.globalRotate, f, f2);
        canvas.drawPath(this.path, paint);
        canvas.restore();
    }
}
