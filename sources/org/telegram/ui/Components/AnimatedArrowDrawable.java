package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import org.telegram.messenger.AndroidUtilities;

public class AnimatedArrowDrawable extends Drawable {
    private float animProgress;
    private float animateToProgress;
    private boolean isSmall;
    private long lastUpdateTime;
    private Paint paint;
    private Path path = new Path();

    public AnimatedArrowDrawable(int color, boolean small) {
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setColor(color);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.isSmall = small;
        updatePath();
    }

    public void draw(Canvas c) {
        c.drawPath(this.path, this.paint);
        checkAnimation();
    }

    private void updatePath() {
        this.path.reset();
        float p = (this.animProgress * 2.0f) - 1.0f;
        if (this.isSmall) {
            this.path.moveTo((float) AndroidUtilities.dp(3.0f), ((float) AndroidUtilities.dp(6.0f)) - (((float) AndroidUtilities.dp(2.0f)) * p));
            this.path.lineTo((float) AndroidUtilities.dp(8.0f), ((float) AndroidUtilities.dp(6.0f)) + (((float) AndroidUtilities.dp(2.0f)) * p));
            this.path.lineTo((float) AndroidUtilities.dp(13.0f), ((float) AndroidUtilities.dp(6.0f)) - (((float) AndroidUtilities.dp(2.0f)) * p));
            return;
        }
        this.path.moveTo((float) AndroidUtilities.dp(4.5f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * p));
        this.path.lineTo((float) AndroidUtilities.dp(13.0f), ((float) AndroidUtilities.dp(12.0f)) + (((float) AndroidUtilities.dp(4.0f)) * p));
        this.path.lineTo((float) AndroidUtilities.dp(21.5f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * p));
    }

    public void setAnimationProgress(float progress) {
        this.animProgress = progress;
        this.animateToProgress = progress;
        updatePath();
        invalidateSelf();
    }

    public void setAnimationProgressAnimated(float progress) {
        if (this.animateToProgress != progress) {
            this.animateToProgress = progress;
            this.lastUpdateTime = SystemClock.elapsedRealtime();
            invalidateSelf();
        }
    }

    private void checkAnimation() {
        if (this.animateToProgress != this.animProgress) {
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            float f = this.animProgress;
            float f2 = this.animateToProgress;
            if (f < f2) {
                float f3 = f + (((float) dt) / 180.0f);
                this.animProgress = f3;
                if (f3 > f2) {
                    this.animProgress = f2;
                }
            } else {
                float f4 = f - (((float) dt) / 180.0f);
                this.animProgress = f4;
                if (f4 < f2) {
                    this.animProgress = f2;
                }
            }
            updatePath();
            invalidateSelf();
        }
    }

    public void setColor(int color) {
        this.paint.setColor(color);
        invalidateSelf();
    }

    public float getAnimationProgress() {
        return this.animProgress;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(26.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(26.0f);
    }
}
