package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;

public class InfiniteProgress {
    private static final float risingTime = 500.0f;
    private static final float rotationTime = 2000.0f;
    private RectF cicleRect = new RectF();
    private float currentCircleLength;
    private float currentProgressTime;
    private long lastUpdateTime;
    private int progressColor;
    private Paint progressPaint;
    private float radOffset;
    private int radius;
    private boolean risingCircleLength;

    public InfiniteProgress(int rad) {
        this.radius = rad;
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setAlpha(float alpha) {
        this.progressPaint.setAlpha((int) (((float) Color.alpha(this.progressColor)) * alpha));
    }

    public void setColor(int color) {
        this.progressColor = color;
        this.progressPaint.setColor(color);
    }

    private void updateAnimation() {
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        if (dt > 17) {
            dt = 17;
        }
        this.lastUpdateTime = newTime;
        float f = this.radOffset + (((float) (360 * dt)) / 2000.0f);
        this.radOffset = f;
        this.radOffset = f - ((float) (((int) (f / 360.0f)) * 360));
        float f2 = this.currentProgressTime + ((float) dt);
        this.currentProgressTime = f2;
        if (f2 >= 500.0f) {
            this.currentProgressTime = 500.0f;
        }
        if (this.risingCircleLength) {
            this.currentCircleLength = (AndroidUtilities.accelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f) * 266.0f) + 4.0f;
        } else {
            this.currentCircleLength = 4.0f - ((1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f)) * 270.0f);
        }
        if (this.currentProgressTime == 500.0f) {
            boolean z = this.risingCircleLength;
            if (z) {
                this.radOffset += 270.0f;
                this.currentCircleLength = -266.0f;
            }
            this.risingCircleLength = !z;
            this.currentProgressTime = 0.0f;
        }
    }

    public void draw(Canvas canvas, float cx, float cy, float scale) {
        RectF rectF = this.cicleRect;
        int i = this.radius;
        rectF.set(cx - (((float) i) * scale), cy - (((float) i) * scale), (((float) i) * scale) + cx, (((float) i) * scale) + cy);
        this.progressPaint.setStrokeWidth(((float) AndroidUtilities.dp(2.0f)) * scale);
        canvas.drawArc(this.cicleRect, this.radOffset, this.currentCircleLength, false, this.progressPaint);
        updateAnimation();
    }
}
