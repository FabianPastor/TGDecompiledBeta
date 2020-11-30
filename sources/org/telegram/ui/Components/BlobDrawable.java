package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;

public class BlobDrawable {
    public static float AMPLITUDE_SPEED = 0.33f;
    public static float FORM_BIG_MAX = 0.6f;
    public static float FORM_BUTTON_MAX = 0.0f;
    public static float FORM_SMALL_MAX = 0.6f;
    public static float GLOBAL_SCALE = 1.0f;
    public static float GRADIENT_SPEED_MAX = 0.01f;
    public static float GRADIENT_SPEED_MIN = 0.5f;
    public static float LIGHT_GRADIENT_SIZE = 0.5f;
    public static float MAX_SPEED = 8.2f;
    public static float MIN_SPEED = 0.8f;
    public static float SCALE_BIG = 0.807f;
    public static float SCALE_BIG_MIN = 0.878f;
    public static float SCALE_SMALL = 0.704f;
    public static float SCALE_SMALL_MIN = 0.926f;
    private final float L;
    private final float N;
    private float[] angle;
    private float[] angleNext;
    public float cubicBezierK = 1.0f;
    private final Matrix m = new Matrix();
    public float maxRadius;
    public float minRadius;
    public Paint paint = new Paint(1);
    private Path path = new Path();
    private float[] pointEnd = new float[4];
    private float[] pointStart = new float[4];
    private float[] progress;
    private float[] radius;
    private float[] radiusNext;
    final Random random = new Random();
    private float[] speed;

    public BlobDrawable(int i) {
        float f = (float) i;
        this.N = f;
        double d = (double) (f * 2.0f);
        Double.isNaN(d);
        this.L = (float) (Math.tan(3.141592653589793d / d) * 1.3333333333333333d);
        this.radius = new float[i];
        this.angle = new float[i];
        this.radiusNext = new float[i];
        this.angleNext = new float[i];
        this.progress = new float[i];
        this.speed = new float[i];
        for (int i2 = 0; ((float) i2) < this.N; i2++) {
            generateBlob(this.radius, this.angle, i2);
            generateBlob(this.radiusNext, this.angleNext, i2);
            this.progress[i2] = 0.0f;
        }
    }

    private void generateBlob(float[] fArr, float[] fArr2, int i) {
        float f = this.maxRadius;
        float f2 = this.minRadius;
        fArr[i] = f2 + (Math.abs((((float) this.random.nextInt()) % 100.0f) / 100.0f) * (f - f2));
        fArr2[i] = ((360.0f / this.N) * ((float) i)) + (((((float) this.random.nextInt()) % 100.0f) / 100.0f) * (360.0f / this.N) * 0.05f);
        float[] fArr3 = this.speed;
        double abs = (double) (Math.abs(((float) this.random.nextInt()) % 100.0f) / 100.0f);
        Double.isNaN(abs);
        fArr3[i] = (float) ((abs * 0.003d) + 0.017d);
    }

    public void update(float f, float f2) {
        for (int i = 0; ((float) i) < this.N; i++) {
            float[] fArr = this.progress;
            float f3 = fArr[i];
            float[] fArr2 = this.speed;
            fArr[i] = f3 + (fArr2[i] * MIN_SPEED) + (fArr2[i] * f * MAX_SPEED * f2);
            if (fArr[i] >= 1.0f) {
                fArr[i] = 0.0f;
                float[] fArr3 = this.radius;
                float[] fArr4 = this.radiusNext;
                fArr3[i] = fArr4[i];
                float[] fArr5 = this.angle;
                float[] fArr6 = this.angleNext;
                fArr5[i] = fArr6[i];
                generateBlob(fArr4, fArr6, i);
            }
        }
    }

    public void draw(float f, float f2, Canvas canvas, Paint paint2) {
        float f3 = f;
        float f4 = f2;
        this.path.reset();
        int i = 0;
        while (true) {
            float f5 = this.N;
            if (((float) i) < f5) {
                float[] fArr = this.progress;
                float f6 = fArr[i];
                int i2 = i + 1;
                int i3 = ((float) i2) < f5 ? i2 : 0;
                float f7 = fArr[i3];
                float[] fArr2 = this.radius;
                float f8 = 1.0f - f6;
                float[] fArr3 = this.radiusNext;
                float f9 = (fArr2[i] * f8) + (fArr3[i] * f6);
                float var_ = 1.0f - f7;
                float var_ = (fArr2[i3] * var_) + (fArr3[i3] * f7);
                float[] fArr4 = this.angle;
                float var_ = fArr4[i] * f8;
                float[] fArr5 = this.angleNext;
                float var_ = (fArr4[i3] * var_) + (fArr5[i3] * f7);
                float min = this.L * (Math.min(f9, var_) + ((Math.max(f9, var_) - Math.min(f9, var_)) / 2.0f)) * this.cubicBezierK;
                this.m.reset();
                this.m.setRotate(var_ + (fArr5[i] * f6), f3, f4);
                float[] fArr6 = this.pointStart;
                fArr6[0] = f3;
                float var_ = f4 - f9;
                fArr6[1] = var_;
                fArr6[2] = f3 + min;
                fArr6[3] = var_;
                this.m.mapPoints(fArr6);
                float[] fArr7 = this.pointEnd;
                fArr7[0] = f3;
                float var_ = f4 - var_;
                fArr7[1] = var_;
                fArr7[2] = f3 - min;
                fArr7[3] = var_;
                this.m.reset();
                this.m.setRotate(var_, f3, f4);
                this.m.mapPoints(this.pointEnd);
                if (i == 0) {
                    Path path2 = this.path;
                    float[] fArr8 = this.pointStart;
                    path2.moveTo(fArr8[0], fArr8[1]);
                }
                Path path3 = this.path;
                float[] fArr9 = this.pointStart;
                float var_ = fArr9[2];
                float var_ = fArr9[3];
                float[] fArr10 = this.pointEnd;
                path3.cubicTo(var_, var_, fArr10[2], fArr10[3], fArr10[0], fArr10[1]);
                i = i2;
            } else {
                canvas.save();
                canvas.drawPath(this.path, paint2);
                canvas.restore();
                return;
            }
        }
    }

    public void generateBlob() {
        for (int i = 0; ((float) i) < this.N; i++) {
            generateBlob(this.radius, this.angle, i);
            generateBlob(this.radiusNext, this.angleNext, i);
            this.progress[i] = 0.0f;
        }
    }
}
