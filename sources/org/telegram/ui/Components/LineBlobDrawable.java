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

    public LineBlobDrawable(int n) {
        this.N = (float) n;
        this.radius = new float[(n + 1)];
        this.radiusNext = new float[(n + 1)];
        this.progress = new float[(n + 1)];
        this.speed = new float[(n + 1)];
        for (int i = 0; ((float) i) <= this.N; i++) {
            generateBlob(this.radius, i);
            generateBlob(this.radiusNext, i);
            this.progress[i] = 0.0f;
        }
    }

    private void generateBlob(float[] radius2, int i) {
        float f = this.maxRadius;
        float f2 = this.minRadius;
        radius2[i] = f2 + (Math.abs((((float) this.random.nextInt()) % 100.0f) / 100.0f) * (f - f2));
        float[] fArr = this.speed;
        double abs = (double) (Math.abs(((float) this.random.nextInt()) % 100.0f) / 100.0f);
        Double.isNaN(abs);
        fArr[i] = (float) ((abs * 0.003d) + 0.017d);
    }

    public void update(float amplitude, float speedScale) {
        for (int i = 0; ((float) i) <= this.N; i++) {
            float[] fArr = this.progress;
            fArr[i] = fArr[i] + (this.speed[i] * BlobDrawable.MIN_SPEED) + (this.speed[i] * amplitude * BlobDrawable.MAX_SPEED * speedScale);
            float[] fArr2 = this.progress;
            if (fArr2[i] >= 1.0f) {
                fArr2[i] = 0.0f;
                float[] fArr3 = this.radius;
                float[] fArr4 = this.radiusNext;
                fArr3[i] = fArr4[i];
                generateBlob(fArr4, i);
            }
        }
    }

    public void draw(float left, float top, float right, float bottom, Canvas canvas, Paint paint2, float pinnedTop, float progressToPinned) {
        float f = left;
        float f2 = right;
        float f3 = bottom;
        this.path.reset();
        this.path.moveTo(f2, f3);
        this.path.lineTo(f, f3);
        int i = 0;
        while (true) {
            float f4 = this.N;
            if (((float) i) <= f4) {
                if (i == 0) {
                    float progress2 = this.progress[i];
                    this.path.lineTo(f, ((top - ((this.radius[i] * (1.0f - progress2)) + (this.radiusNext[i] * progress2))) * progressToPinned) + ((1.0f - progressToPinned) * pinnedTop));
                } else {
                    float[] fArr = this.progress;
                    float progress3 = fArr[i - 1];
                    float[] fArr2 = this.radius;
                    float f5 = fArr2[i - 1] * (1.0f - progress3);
                    float[] fArr3 = this.radiusNext;
                    float r1 = f5 + (fArr3[i - 1] * progress3);
                    float progressNext = fArr[i];
                    float r2 = (fArr2[i] * (1.0f - progressNext)) + (fArr3[i] * progressNext);
                    float x1 = ((f2 - f) / f4) * ((float) (i - 1));
                    float x2 = ((f2 - f) / f4) * ((float) i);
                    float cx = ((x2 - x1) / 2.0f) + x1;
                    float y2 = ((1.0f - progressToPinned) * pinnedTop) + ((top - r2) * progressToPinned);
                    this.path.cubicTo(cx, ((top - r1) * progressToPinned) + ((1.0f - progressToPinned) * pinnedTop), cx, y2, x2, y2);
                    if (((float) i) == this.N) {
                        this.path.lineTo(f2, f3);
                    }
                }
                i++;
            } else {
                canvas.drawPath(this.path, paint2);
                return;
            }
        }
    }

    public void generateBlob() {
        for (int i = 0; ((float) i) < this.N; i++) {
            generateBlob(this.radius, i);
            generateBlob(this.radiusNext, i);
            this.progress[i] = 0.0f;
        }
    }
}
