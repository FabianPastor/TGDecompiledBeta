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
    private float animationTime = 300.0f;
    private int arrowRotation;
    private int color = -1;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private Paint prevPaint = new Paint(1);
    private boolean reverseAngle;
    private boolean rotated = true;
    private int rotatedColor = -9079435;

    public int getOpacity() {
        return -2;
    }

    public BackDrawable(boolean z) {
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.prevPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.prevPaint.setColor(-65536);
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
            if (f2 < f) {
                this.currentAnimationTime = (int) (f2 * this.animationTime);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f2) * this.animationTime);
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

    public void draw(Canvas canvas) {
        float f;
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                int currentTimeMillis = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                this.currentAnimationTime = currentTimeMillis;
                float f2 = this.animationTime;
                if (((float) currentTimeMillis) >= f2) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) currentTimeMillis) / f2) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) currentTimeMillis) / f2);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        int i = 0;
        int red = this.rotated ? (int) (((float) (Color.red(this.rotatedColor) - Color.red(this.color))) * this.currentRotation) : 0;
        int green = this.rotated ? (int) (((float) (Color.green(this.rotatedColor) - Color.green(this.color))) * this.currentRotation) : 0;
        if (this.rotated) {
            i = (int) (((float) (Color.blue(this.rotatedColor) - Color.blue(this.color))) * this.currentRotation);
        }
        this.paint.setColor(Color.rgb(Color.red(this.color) + red, Color.green(this.color) + green, Color.blue(this.color) + i));
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        int i2 = this.arrowRotation;
        if (i2 != 0) {
            canvas.rotate((float) i2);
        }
        float f3 = this.currentRotation;
        if (!this.alwaysClose) {
            canvas.rotate(((float) (this.reverseAngle ? -225 : 135)) * f3);
            f = f3;
        } else {
            canvas.rotate((f3 * ((float) (this.reverseAngle ? -180 : 180))) + 135.0f);
            f = 1.0f;
        }
        float f4 = 1.0f - f;
        canvas.drawLine((float) AndroidUtilities.dp(AndroidUtilities.lerp(-6.75f, -8.0f, f)), 0.0f, ((float) AndroidUtilities.dp(8.0f)) - ((this.paint.getStrokeWidth() / 2.0f) * f4), 0.0f, this.paint);
        float dp = (float) AndroidUtilities.dp(-0.25f);
        float dp2 = ((float) AndroidUtilities.dp(AndroidUtilities.lerp(7.0f, 8.0f, f))) - ((this.paint.getStrokeWidth() / 4.0f) * f4);
        Canvas canvas2 = canvas;
        float dp3 = (float) AndroidUtilities.dp(AndroidUtilities.lerp(-7.25f, 0.0f, f));
        canvas2.drawLine(dp3, -dp, 0.0f, -dp2, this.paint);
        canvas2.drawLine(dp3, dp, 0.0f, dp2, this.paint);
        canvas.restore();
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
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
