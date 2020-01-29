package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable extends Drawable {
    private int currentAnimationTime;
    private int currentSegment;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public CloseProgressDrawable() {
        this.paint.setColor(-9079435);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void draw(Canvas canvas) {
        long currentTimeMillis = System.currentTimeMillis();
        long j = this.lastFrameTime;
        if (j != 0) {
            this.currentAnimationTime = (int) (((long) this.currentAnimationTime) + (currentTimeMillis - j));
            if (this.currentAnimationTime > 200) {
                this.currentAnimationTime = 0;
                this.currentSegment++;
                int i = this.currentSegment;
                if (i == 4) {
                    this.currentSegment = i - 4;
                }
            }
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas.rotate(45.0f);
        this.paint.setAlpha(255 - ((this.currentSegment % 4) * 40));
        canvas.drawLine((float) (-AndroidUtilities.dp(8.0f)), 0.0f, 0.0f, 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 1) % 4) * 40));
        Canvas canvas2 = canvas;
        canvas2.drawLine(0.0f, (float) (-AndroidUtilities.dp(8.0f)), 0.0f, 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 2) % 4) * 40));
        canvas2.drawLine(0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        this.paint.setAlpha(255 - (((this.currentSegment + 3) % 4) * 40));
        canvas.drawLine(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(8.0f), this.paint);
        canvas.restore();
        this.lastFrameTime = currentTimeMillis;
        invalidateSelf();
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
