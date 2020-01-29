package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class MenuDrawable extends Drawable {
    private boolean animationInProgress;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private boolean reverseAngle;
    private boolean rotateToBack = true;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public MenuDrawable() {
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    public void setRotateToBack(boolean z) {
        this.rotateToBack = z;
    }

    public void setRotation(float f, boolean z) {
        this.lastFrameTime = 0;
        float f2 = this.currentRotation;
        if (f2 == 1.0f) {
            this.reverseAngle = true;
        } else if (f2 == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (z) {
            float f3 = this.currentRotation;
            if (f3 < f) {
                this.currentAnimationTime = (int) (f3 * 200.0f);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f3) * 200.0f);
            }
            this.lastFrameTime = SystemClock.uptimeMillis();
            this.finalRotation = f;
        } else {
            this.currentRotation = f;
            this.finalRotation = f;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        if (this.currentRotation != this.finalRotation) {
            long uptimeMillis = SystemClock.uptimeMillis();
            long j = this.lastFrameTime;
            if (j != 0) {
                this.currentAnimationTime = (int) (((long) this.currentAnimationTime) + (uptimeMillis - j));
                int i = this.currentAnimationTime;
                if (i >= 200) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) i) / 200.0f) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) i) / 200.0f);
                }
            }
            this.lastFrameTime = uptimeMillis;
            invalidateSelf();
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        int color = Theme.getColor("actionBarDefaultIcon");
        if (this.rotateToBack) {
            canvas.rotate(this.currentRotation * ((float) (this.reverseAngle ? -180 : 180)));
            this.paint.setColor(color);
            canvas.drawLine((float) (-AndroidUtilities.dp(9.0f)), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(3.0f)) * this.currentRotation), 0.0f, this.paint);
            f6 = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
            f5 = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(2.5f)) * Math.abs(this.currentRotation));
            f4 = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(2.0f)) * Math.abs(this.currentRotation));
            f3 = (float) (-AndroidUtilities.dp(9.0f));
            f2 = (float) AndroidUtilities.dp(7.5f);
            f = Math.abs(this.currentRotation);
        } else {
            canvas.rotate(this.currentRotation * ((float) (this.reverseAngle ? -225 : 135)));
            this.paint.setColor(AndroidUtilities.getOffsetColor(color, Theme.getColor("actionBarActionModeDefaultIcon"), this.currentRotation, 1.0f));
            canvas.drawLine(((float) (-AndroidUtilities.dp(9.0f))) + (((float) AndroidUtilities.dp(1.0f)) * this.currentRotation), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(1.0f)) * this.currentRotation), 0.0f, this.paint);
            f6 = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
            f5 = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(9.0f)) * Math.abs(this.currentRotation));
            f4 = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(3.0f)) * Math.abs(this.currentRotation));
            f3 = (float) (-AndroidUtilities.dp(9.0f));
            f2 = (float) AndroidUtilities.dp(9.0f);
            f = Math.abs(this.currentRotation);
        }
        float f7 = f3 + (f2 * f);
        Canvas canvas2 = canvas;
        float f8 = f7;
        float f9 = f5;
        canvas2.drawLine(f8, -f4, f9, -f6, this.paint);
        canvas2.drawLine(f8, f4, f9, f6, this.paint);
        canvas.restore();
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
