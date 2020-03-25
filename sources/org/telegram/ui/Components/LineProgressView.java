package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class LineProgressView extends View {
    private static DecelerateInterpolator decelerateInterpolator;
    private static Paint progressPaint;
    private float animatedAlphaValue = 1.0f;
    private float animatedProgressValue;
    private float animationProgressStart;
    private int backColor;
    private float currentProgress;
    private long currentProgressTime;
    private long lastUpdateTime;
    private int progressColor;

    public LineProgressView(Context context) {
        super(context);
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
            Paint paint = new Paint(1);
            progressPaint = paint;
            paint.setStrokeCap(Paint.Cap.ROUND);
            progressPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    private void updateAnimation() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = currentTimeMillis;
        float f = this.animatedProgressValue;
        if (f != 1.0f) {
            float f2 = this.currentProgress;
            if (f != f2) {
                float f3 = this.animationProgressStart;
                float f4 = f2 - f3;
                if (f4 > 0.0f) {
                    long j2 = this.currentProgressTime + j;
                    this.currentProgressTime = j2;
                    if (j2 >= 300) {
                        this.animatedProgressValue = f2;
                        this.animationProgressStart = f2;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = f3 + (f4 * decelerateInterpolator.getInterpolation(((float) j2) / 300.0f));
                    }
                }
                invalidate();
            }
        }
        float f5 = this.animatedProgressValue;
        if (f5 >= 1.0f && f5 == 1.0f) {
            float f6 = this.animatedAlphaValue;
            if (f6 != 0.0f) {
                float f7 = f6 - (((float) j) / 200.0f);
                this.animatedAlphaValue = f7;
                if (f7 <= 0.0f) {
                    this.animatedAlphaValue = 0.0f;
                }
                invalidate();
            }
        }
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }

    public void setBackColor(int i) {
        this.backColor = i;
    }

    public void setProgress(float f, boolean z) {
        if (!z) {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        } else {
            this.animationProgressStart = this.animatedProgressValue;
        }
        if (f != 1.0f) {
            this.animatedAlphaValue = 1.0f;
        }
        this.currentProgress = f;
        this.currentProgressTime = 0;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public float getCurrentProgress() {
        return this.currentProgress;
    }

    public void onDraw(Canvas canvas) {
        int i = this.backColor;
        if (!(i == 0 || this.animatedProgressValue == 1.0f)) {
            progressPaint.setColor(i);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            canvas.drawRect((float) ((int) (((float) getWidth()) * this.animatedProgressValue)), 0.0f, (float) getWidth(), (float) getHeight(), progressPaint);
        }
        progressPaint.setColor(this.progressColor);
        progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
        canvas.drawRect(0.0f, 0.0f, ((float) getWidth()) * this.animatedProgressValue, (float) getHeight(), progressPaint);
        updateAnimation();
    }
}
