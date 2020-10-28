package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgressView extends View {
    private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private float animatedProgress;
    private RectF cicleRect = new RectF();
    private float currentCircleLength;
    private float currentProgress;
    private float currentProgressTime;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private long lastUpdateTime;
    private boolean noProgress = true;
    private float progressAnimationStart;
    private int progressColor = Theme.getColor("progressCircle");
    private Paint progressPaint;
    private int progressTime;
    private float radOffset;
    private boolean risingCircleLength;
    private int size = AndroidUtilities.dp(40.0f);
    private boolean useSelfAlpha;

    public RadialProgressView(Context context) {
        super(context);
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.progressPaint.setColor(this.progressColor);
    }

    public void setUseSelfAlpha(boolean z) {
        this.useSelfAlpha = z;
    }

    @Keep
    public void setAlpha(float f) {
        super.setAlpha(f);
        if (this.useSelfAlpha) {
            Drawable background = getBackground();
            int i = (int) (f * 255.0f);
            if (background != null) {
                background.setAlpha(i);
            }
            this.progressPaint.setAlpha(i);
        }
    }

    public void setNoProgress(boolean z) {
        this.noProgress = z;
    }

    public void setProgress(float f) {
        this.currentProgress = f;
        if (this.animatedProgress > f) {
            this.animatedProgress = f;
        }
        this.progressAnimationStart = this.animatedProgress;
        this.currentProgress = f;
        this.progressTime = 0;
    }

    private void updateAnimation() {
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.lastUpdateTime;
        if (j > 17) {
            j = 17;
        }
        this.lastUpdateTime = currentTimeMillis;
        float f = this.radOffset + (((float) (360 * j)) / 2000.0f);
        this.radOffset = f;
        this.radOffset = f - ((float) (((int) (f / 360.0f)) * 360));
        if (this.noProgress) {
            float f2 = this.currentProgressTime + ((float) j);
            this.currentProgressTime = f2;
            if (f2 >= 500.0f) {
                this.currentProgressTime = 500.0f;
            }
            if (this.risingCircleLength) {
                this.currentCircleLength = (this.accelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f) * 266.0f) + 4.0f;
            } else {
                this.currentCircleLength = 4.0f - ((1.0f - this.decelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f)) * 270.0f);
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
        } else {
            float f3 = this.currentProgress;
            float f4 = this.progressAnimationStart;
            float f5 = f3 - f4;
            if (f5 > 0.0f) {
                int i = (int) (((long) this.progressTime) + j);
                this.progressTime = i;
                if (((float) i) >= 200.0f) {
                    this.progressAnimationStart = f3;
                    this.animatedProgress = f3;
                    this.progressTime = 0;
                } else {
                    this.animatedProgress = f4 + (f5 * AndroidUtilities.decelerateInterpolator.getInterpolation(((float) i) / 200.0f));
                }
            }
            this.currentCircleLength = Math.max(4.0f, this.animatedProgress * 360.0f);
        }
        invalidate();
    }

    public void setSize(int i) {
        this.size = i;
        invalidate();
    }

    public void setStrokeWidth(float f) {
        this.progressPaint.setStrokeWidth((float) AndroidUtilities.dp(f));
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
        this.progressPaint.setColor(i);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int measuredWidth = (getMeasuredWidth() - this.size) / 2;
        int measuredHeight = getMeasuredHeight();
        int i = this.size;
        int i2 = (measuredHeight - i) / 2;
        this.cicleRect.set((float) measuredWidth, (float) i2, (float) (measuredWidth + i), (float) (i2 + i));
        canvas.drawArc(this.cicleRect, this.radOffset, this.currentCircleLength, false, this.progressPaint);
        updateAnimation();
    }
}
