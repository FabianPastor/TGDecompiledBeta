package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
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
            progressPaint = new Paint(1);
            progressPaint.setStrokeCap(Cap.ROUND);
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
                f = this.animationProgressStart;
                float f3 = f2 - f;
                if (f3 > 0.0f) {
                    this.currentProgressTime += j;
                    long j2 = this.currentProgressTime;
                    if (j2 >= 300) {
                        this.animatedProgressValue = f2;
                        this.animationProgressStart = f2;
                        this.currentProgressTime = 0;
                    } else {
                        this.animatedProgressValue = f + (f3 * decelerateInterpolator.getInterpolation(((float) j2) / 300.0f));
                    }
                }
                invalidate();
            }
        }
        f = this.animatedProgressValue;
        if (f >= 1.0f && f == 1.0f) {
            f = this.animatedAlphaValue;
            if (f != 0.0f) {
                this.animatedAlphaValue = f - (((float) j) / 200.0f);
                if (this.animatedAlphaValue <= 0.0f) {
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
        if (z) {
            this.animationProgressStart = this.animatedProgressValue;
        } else {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
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
