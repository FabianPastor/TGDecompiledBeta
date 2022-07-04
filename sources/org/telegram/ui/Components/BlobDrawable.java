package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;

public class BlobDrawable {
    public static float AMPLITUDE_SPEED = 0.33f;
    private static final float ANIMATION_SPEED_WAVE_HUGE = 0.65f;
    private static final float ANIMATION_SPEED_WAVE_SMALL = 0.45f;
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
    private static final float animationSpeed = 0.35000002f;
    private static final float animationSpeedTiny = 0.55f;
    private final float L;
    private final float N;
    public float amplitude;
    private float[] angle;
    private float[] angleNext;
    private float animateAmplitudeDiff;
    private float animateToAmplitude;
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

    public BlobDrawable(int n) {
        float f = (float) n;
        this.N = f;
        double d = (double) (f * 2.0f);
        Double.isNaN(d);
        this.L = (float) (Math.tan(3.141592653589793d / d) * 1.3333333333333333d);
        this.radius = new float[n];
        this.angle = new float[n];
        this.radiusNext = new float[n];
        this.angleNext = new float[n];
        this.progress = new float[n];
        this.speed = new float[n];
        for (int i = 0; ((float) i) < this.N; i++) {
            generateBlob(this.radius, this.angle, i);
            generateBlob(this.radiusNext, this.angleNext, i);
            this.progress[i] = 0.0f;
        }
    }

    private void generateBlob(float[] radius2, float[] angle2, int i) {
        float f = this.maxRadius;
        float f2 = this.minRadius;
        radius2[i] = f2 + (Math.abs((((float) this.random.nextInt()) % 100.0f) / 100.0f) * (f - f2));
        angle2[i] = ((360.0f / this.N) * ((float) i)) + (((((float) this.random.nextInt()) % 100.0f) / 100.0f) * (360.0f / this.N) * 0.05f);
        float[] fArr = this.speed;
        double abs = (double) (Math.abs(((float) this.random.nextInt()) % 100.0f) / 100.0f);
        Double.isNaN(abs);
        fArr[i] = (float) ((abs * 0.003d) + 0.017d);
    }

    public void update(float amplitude2, float speedScale) {
        for (int i = 0; ((float) i) < this.N; i++) {
            float[] fArr = this.progress;
            float f = fArr[i];
            float[] fArr2 = this.speed;
            fArr[i] = f + (fArr2[i] * MIN_SPEED) + (fArr2[i] * amplitude2 * MAX_SPEED * speedScale);
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

    public void draw(float cX, float cY, Canvas canvas, Paint paint2) {
        float f = cX;
        float f2 = cY;
        this.path.reset();
        int i = 0;
        while (true) {
            float f3 = this.N;
            if (((float) i) < f3) {
                float[] fArr = this.progress;
                float progress2 = fArr[i];
                int nextIndex = ((float) (i + 1)) < f3 ? i + 1 : 0;
                float progressNext = fArr[nextIndex];
                float[] fArr2 = this.radius;
                float f4 = fArr2[i] * (1.0f - progress2);
                float[] fArr3 = this.radiusNext;
                float r1 = f4 + (fArr3[i] * progress2);
                float r2 = (fArr2[nextIndex] * (1.0f - progressNext)) + (fArr3[nextIndex] * progressNext);
                float[] fArr4 = this.angle;
                float f5 = fArr4[i] * (1.0f - progress2);
                float[] fArr5 = this.angleNext;
                float angle1 = f5 + (fArr5[i] * progress2);
                float angle2 = (fArr4[nextIndex] * (1.0f - progressNext)) + (fArr5[nextIndex] * progressNext);
                float l = this.L * (Math.min(r1, r2) + ((Math.max(r1, r2) - Math.min(r1, r2)) / 2.0f)) * this.cubicBezierK;
                this.m.reset();
                this.m.setRotate(angle1, f, f2);
                float[] fArr6 = this.pointStart;
                fArr6[0] = f;
                fArr6[1] = f2 - r1;
                fArr6[2] = f + l;
                fArr6[3] = f2 - r1;
                this.m.mapPoints(fArr6);
                float[] fArr7 = this.pointEnd;
                fArr7[0] = f;
                fArr7[1] = f2 - r2;
                fArr7[2] = f - l;
                fArr7[3] = f2 - r2;
                this.m.reset();
                this.m.setRotate(angle2, f, f2);
                this.m.mapPoints(this.pointEnd);
                if (i == 0) {
                    Path path2 = this.path;
                    float[] fArr8 = this.pointStart;
                    path2.moveTo(fArr8[0], fArr8[1]);
                }
                Path path3 = this.path;
                float[] fArr9 = this.pointStart;
                float f6 = fArr9[2];
                float f7 = fArr9[3];
                float[] fArr10 = this.pointEnd;
                path3.cubicTo(f6, f7, fArr10[2], fArr10[3], fArr10[0], fArr10[1]);
                i++;
                f = cX;
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

    public void setValue(float value, boolean isBig) {
        this.animateToAmplitude = value;
        if (isBig) {
            float f = this.amplitude;
            if (value > f) {
                this.animateAmplitudeDiff = (value - f) / 205.0f;
            } else {
                this.animateAmplitudeDiff = (value - f) / 275.0f;
            }
        } else {
            float f2 = this.amplitude;
            if (value > f2) {
                this.animateAmplitudeDiff = (value - f2) / 320.0f;
            } else {
                this.animateAmplitudeDiff = (value - f2) / 375.0f;
            }
        }
    }

    public void updateAmplitude(long dt) {
        float f = this.animateToAmplitude;
        float f2 = this.amplitude;
        if (f != f2) {
            float f3 = this.animateAmplitudeDiff;
            float f4 = f2 + (((float) dt) * f3);
            this.amplitude = f4;
            if (f3 > 0.0f) {
                if (f4 > f) {
                    this.amplitude = f;
                }
            } else if (f4 < f) {
                this.amplitude = f;
            }
        }
    }
}
