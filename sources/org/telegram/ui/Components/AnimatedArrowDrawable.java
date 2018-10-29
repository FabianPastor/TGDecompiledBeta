package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.Keep;
import org.telegram.messenger.AndroidUtilities;

public class AnimatedArrowDrawable extends Drawable {
    private float animProgress;
    private float animateToProgress;
    private long lastUpdateTime;
    private Paint paint = new Paint(1);
    private Path path = new Path();

    public AnimatedArrowDrawable(int color) {
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setColor(color);
        updatePath();
    }

    public void draw(Canvas c) {
        c.drawPath(this.path, this.paint);
        checkAnimation();
    }

    private void updatePath() {
        this.path.reset();
        float p = (this.animProgress * 2.0f) - 1.0f;
        this.path.moveTo((float) AndroidUtilities.dp(3.0f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * p));
        this.path.lineTo((float) AndroidUtilities.dp(13.0f), ((float) AndroidUtilities.dp(12.0f)) + (((float) AndroidUtilities.dp(4.0f)) * p));
        this.path.lineTo((float) AndroidUtilities.dp(23.0f), ((float) AndroidUtilities.dp(12.0f)) - (((float) AndroidUtilities.dp(4.0f)) * p));
    }

    @Keep
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
            if (this.animProgress < this.animateToProgress) {
                this.animProgress += ((float) dt) / 180.0f;
                if (this.animProgress > this.animateToProgress) {
                    this.animProgress = this.animateToProgress;
                }
            } else {
                this.animProgress -= ((float) dt) / 180.0f;
                if (this.animProgress < this.animateToProgress) {
                    this.animProgress = this.animateToProgress;
                }
            }
            updatePath();
            invalidateSelf();
        }
    }

    public void setColor(int color) {
        this.paint.setColor(color);
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
