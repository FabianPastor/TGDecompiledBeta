package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
    private boolean reverseAngle = false;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public MenuDrawable() {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    public void setRotation(float f, boolean z) {
        this.lastFrameTime = 0;
        if (this.currentRotation == 1.0f) {
            this.reverseAngle = true;
        } else if (this.currentRotation == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (z) {
            if (this.currentRotation < f) {
                this.currentAnimationTime = (int) (this.currentRotation * true);
            } else {
                this.currentAnimationTime = (int) ((1.0f - this.currentRotation) * 300.0f);
            }
            this.lastFrameTime = System.currentTimeMillis();
            this.finalRotation = f;
        } else {
            this.currentRotation = f;
            this.finalRotation = f;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                this.currentAnimationTime = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                if (this.currentAnimationTime >= 300) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) this.currentAnimationTime) / 300.0f) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) this.currentAnimationTime) / 300.0f);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas.rotate(this.currentRotation * ((float) (this.reverseAngle ? -180 : 180)));
        canvas.drawLine((float) (-AndroidUtilities.dp(9.0f)), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(3.0f)) * this.currentRotation), 0.0f, this.paint);
        float dp = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
        float dp2 = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(2.0f)) * Math.abs(this.currentRotation));
        Canvas canvas2 = canvas;
        float dp3 = ((float) (-AndroidUtilities.dp(9.0f))) + (((float) AndroidUtilities.dp(7.5f)) * Math.abs(this.currentRotation));
        float dp4 = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(2.5f)) * Math.abs(this.currentRotation));
        canvas2.drawLine(dp3, -dp2, dp4, -dp, this.paint);
        canvas2.drawLine(dp3, dp2, dp4, dp, this.paint);
        canvas.restore();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
