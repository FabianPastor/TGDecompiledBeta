package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;

public class CircleBezierDrawable {
    private final float L;
    private final int N;
    float cubicBezierK = 1.0f;
    float globalRotate = 0.0f;
    public float idleStateDiff = 0.0f;
    private Matrix m = new Matrix();
    private Path path = new Path();
    private float[] pointEnd = new float[4];
    private float[] pointStart = new float[4];
    float radius;
    float radiusDiff;
    final Random random = new Random();
    float[] randomAdditionals;
    float randomK;

    public CircleBezierDrawable(int i) {
        this.N = i;
        double d = (double) (i * 2);
        Double.isNaN(d);
        this.L = (float) (Math.tan(3.141592653589793d / d) * 1.3333333333333333d);
        this.randomAdditionals = new float[i];
        calculateRandomAdditionals();
    }

    public void calculateRandomAdditionals() {
        for (int i = 0; i < this.N; i++) {
            this.randomAdditionals[i] = ((float) (this.random.nextInt() % 100)) / 100.0f;
        }
    }

    /* access modifiers changed from: protected */
    public void draw(float f, float f2, Canvas canvas, Paint paint) {
        float f3 = f;
        float f4 = f2;
        Canvas canvas2 = canvas;
        float f5 = this.radius;
        float f6 = this.idleStateDiff;
        float f7 = this.radiusDiff;
        float f8 = (f5 - (f6 / 2.0f)) - (f7 / 2.0f);
        float f9 = f5 + (f7 / 2.0f) + (f6 / 2.0f);
        float max = this.L * Math.max(f8, f9) * this.cubicBezierK;
        this.path.reset();
        int i = 0;
        while (i < this.N) {
            this.m.reset();
            this.m.setRotate((360.0f / ((float) this.N)) * ((float) i), f3, f4);
            float var_ = i % 2 == 0 ? f8 : f9;
            float var_ = this.randomK;
            float[] fArr = this.randomAdditionals;
            float var_ = var_ + (fArr[i] * var_);
            float[] fArr2 = this.pointStart;
            fArr2[0] = f3;
            float var_ = f4 - var_;
            fArr2[1] = var_;
            fArr2[2] = f3 + max + (var_ * fArr[i] * this.L);
            fArr2[3] = var_;
            this.m.mapPoints(fArr2);
            int i2 = i + 1;
            int i3 = i2 >= this.N ? 0 : i2;
            float var_ = i3 % 2 == 0 ? f8 : f9;
            float var_ = this.randomK;
            float[] fArr3 = this.randomAdditionals;
            float[] fArr4 = this.pointEnd;
            fArr4[0] = f3;
            float var_ = f4 - (var_ + (fArr3[i3] * var_));
            fArr4[1] = var_;
            fArr4[2] = (f3 - max) + (var_ * fArr3[i3] * this.L);
            fArr4[3] = var_;
            this.m.reset();
            this.m.setRotate((360.0f / ((float) this.N)) * ((float) i3), f3, f4);
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
            i = i2;
        }
        canvas.save();
        canvas2.rotate(this.globalRotate, f3, f4);
        canvas2.drawPath(this.path, paint);
        canvas.restore();
    }

    public void setRandomAdditions(float f) {
        this.randomK = f;
    }
}
