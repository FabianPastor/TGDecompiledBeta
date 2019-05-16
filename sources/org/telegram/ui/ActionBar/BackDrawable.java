package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class BackDrawable extends Drawable {
    private boolean alwaysClose;
    private boolean animationInProgress;
    private float animationTime = 300.0f;
    private int arrowRotation;
    private int color = -1;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private boolean reverseAngle;
    private boolean rotated = true;
    private int rotatedColor = -9079435;

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public BackDrawable(boolean z) {
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.alwaysClose = z;
    }

    public void setColor(int i) {
        this.color = i;
        invalidateSelf();
    }

    public void setRotatedColor(int i) {
        this.rotatedColor = i;
        invalidateSelf();
    }

    public void setArrowRotation(int i) {
        this.arrowRotation = i;
        invalidateSelf();
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
                this.currentAnimationTime = (int) (f3 * this.animationTime);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f3) * this.animationTime);
            }
            this.lastFrameTime = System.currentTimeMillis();
            this.finalRotation = f;
        } else {
            this.currentRotation = f;
            this.finalRotation = f;
        }
        invalidateSelf();
    }

    public void setAnimationTime(float f) {
        this.animationTime = f;
    }

    public void setRotated(boolean z) {
        this.rotated = z;
    }

    public void draw(Canvas canvas) {
        int i;
        float f;
        float f2;
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                this.currentAnimationTime = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                i = this.currentAnimationTime;
                f = (float) i;
                float f3 = this.animationTime;
                if (f >= f3) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) i) / f3) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) i) / f3);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        int i2 = 0;
        i = this.rotated ? (int) (((float) (Color.red(this.rotatedColor) - Color.red(this.color))) * this.currentRotation) : 0;
        int green = this.rotated ? (int) (((float) (Color.green(this.rotatedColor) - Color.green(this.color))) * this.currentRotation) : 0;
        if (this.rotated) {
            i2 = (int) (((float) (Color.blue(this.rotatedColor) - Color.blue(this.color))) * this.currentRotation);
        }
        this.paint.setColor(Color.rgb(Color.red(this.color) + i, Color.green(this.color) + green, Color.blue(this.color) + i2));
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        i = this.arrowRotation;
        if (i != 0) {
            canvas.rotate((float) i);
        }
        float f4 = this.currentRotation;
        if (this.alwaysClose) {
            canvas.rotate((f4 * ((float) (this.reverseAngle ? -180 : 180))) + 135.0f);
            f2 = 1.0f;
        } else {
            canvas.rotate(((float) (this.reverseAngle ? -225 : 135)) * f4);
            f2 = f4;
        }
        canvas.drawLine(((float) (-AndroidUtilities.dp(7.0f))) - (((float) AndroidUtilities.dp(1.0f)) * f2), 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        float f5 = (float) (-AndroidUtilities.dp(0.5f));
        float dp = ((float) AndroidUtilities.dp(7.0f)) + (((float) AndroidUtilities.dp(1.0f)) * f2);
        Canvas canvas2 = canvas;
        f = ((float) (-AndroidUtilities.dp(7.0f))) + (((float) AndroidUtilities.dp(7.0f)) * f2);
        float dp2 = ((float) AndroidUtilities.dp(0.5f)) - (((float) AndroidUtilities.dp(0.5f)) * f2);
        canvas2.drawLine(f, -f5, dp2, -dp, this.paint);
        canvas2.drawLine(f, f5, dp2, dp, this.paint);
        canvas.restore();
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
