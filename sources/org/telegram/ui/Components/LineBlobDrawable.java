package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Random;

public class LineBlobDrawable {
    private final float N;
    public float maxRadius;
    public float minRadius;
    public Paint paint = new Paint(1);
    public Path path = new Path();
    private float[] progress;
    private float[] radius;
    private float[] radiusNext;
    final Random random = new Random();
    private float[] speed;

    public LineBlobDrawable(int i) {
        this.N = (float) i;
        int i2 = i + 1;
        this.radius = new float[i2];
        this.radiusNext = new float[i2];
        this.progress = new float[i2];
        this.speed = new float[i2];
        for (int i3 = 0; ((float) i3) <= this.N; i3++) {
            generateBlob(this.radius, i3);
            generateBlob(this.radiusNext, i3);
            this.progress[i3] = 0.0f;
        }
    }

    private void generateBlob(float[] fArr, int i) {
        float f = this.maxRadius;
        float f2 = this.minRadius;
        fArr[i] = f2 + (Math.abs((((float) this.random.nextInt()) % 100.0f) / 100.0f) * (f - f2));
        float[] fArr2 = this.speed;
        double abs = (double) (Math.abs(((float) this.random.nextInt()) % 100.0f) / 100.0f);
        Double.isNaN(abs);
        fArr2[i] = (float) ((abs * 0.003d) + 0.017d);
    }

    public void update(float f, float f2) {
        for (int i = 0; ((float) i) <= this.N; i++) {
            float[] fArr = this.progress;
            float f3 = fArr[i];
            float[] fArr2 = this.speed;
            fArr[i] = f3 + (fArr2[i] * BlobDrawable.MIN_SPEED) + (fArr2[i] * f * BlobDrawable.MAX_SPEED * f2);
            if (fArr[i] >= 1.0f) {
                fArr[i] = 0.0f;
                float[] fArr3 = this.radius;
                float[] fArr4 = this.radiusNext;
                fArr3[i] = fArr4[i];
                generateBlob(fArr4, i);
            }
        }
    }

    public void draw(float f, float f2, float f3, float f4, Canvas canvas, Paint paint2, float f5, float f6) {
        float f7 = f;
        float f8 = f3;
        float f9 = f4;
        this.path.reset();
        this.path.moveTo(f8, f9);
        this.path.lineTo(f7, f9);
        int i = 0;
        while (true) {
            float var_ = (float) i;
            float var_ = this.N;
            if (var_ <= var_) {
                if (i == 0) {
                    float var_ = this.progress[i];
                    this.path.lineTo(f7, ((f2 - ((this.radius[i] * (1.0f - var_)) + (this.radiusNext[i] * var_))) * f6) + (f5 * (1.0f - f6)));
                } else {
                    float[] fArr = this.progress;
                    int i2 = i - 1;
                    float var_ = fArr[i2];
                    float[] fArr2 = this.radius;
                    float var_ = fArr2[i2] * (1.0f - var_);
                    float[] fArr3 = this.radiusNext;
                    float var_ = fArr[i];
                    float var_ = (fArr2[i] * (1.0f - var_)) + (fArr3[i] * var_);
                    float var_ = f8 - f7;
                    float var_ = (var_ / var_) * ((float) i2);
                    float var_ = (var_ / var_) * var_;
                    float var_ = var_ + ((var_ - var_) / 2.0f);
                    float var_ = (1.0f - f6) * f5;
                    float var_ = ((f2 - var_) * f6) + var_;
                    this.path.cubicTo(var_, ((f2 - (var_ + (fArr3[i2] * var_))) * f6) + var_, var_, var_, var_, var_);
                    if (var_ == this.N) {
                        this.path.lineTo(f8, f9);
                    }
                }
                i++;
            } else {
                canvas.drawPath(this.path, paint2);
                return;
            }
        }
    }
}
