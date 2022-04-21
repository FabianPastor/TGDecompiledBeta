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

    public BackDrawable(boolean close) {
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.alwaysClose = close;
    }

    public void setColor(int value) {
        this.color = value;
        invalidateSelf();
    }

    public void setRotatedColor(int value) {
        this.rotatedColor = value;
        invalidateSelf();
    }

    public void setArrowRotation(int angle) {
        this.arrowRotation = angle;
        invalidateSelf();
    }

    public void setRotation(float rotation, boolean animated) {
        this.lastFrameTime = 0;
        float f = this.currentRotation;
        if (f == 1.0f) {
            this.reverseAngle = true;
        } else if (f == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (animated) {
            if (f < rotation) {
                this.currentAnimationTime = (int) (f * this.animationTime);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f) * this.animationTime);
            }
            this.lastFrameTime = System.currentTimeMillis();
            this.finalRotation = rotation;
        } else {
            this.currentRotation = rotation;
            this.finalRotation = rotation;
        }
        invalidateSelf();
    }

    public void setAnimationTime(float value) {
        this.animationTime = value;
    }

    public void setRotated(boolean value) {
        this.rotated = value;
    }

    public void draw(Canvas canvas) {
        float rotation;
        Canvas canvas2 = canvas;
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                int currentTimeMillis = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                this.currentAnimationTime = currentTimeMillis;
                float f = this.animationTime;
                if (((float) currentTimeMillis) >= f) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) currentTimeMillis) / f) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) currentTimeMillis) / f);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        int rB = 0;
        int rD = this.rotated ? (int) (((float) (Color.red(this.rotatedColor) - Color.red(this.color))) * this.currentRotation) : 0;
        int rG = this.rotated ? (int) (((float) (Color.green(this.rotatedColor) - Color.green(this.color))) * this.currentRotation) : 0;
        if (this.rotated) {
            rB = (int) (((float) (Color.blue(this.rotatedColor) - Color.blue(this.color))) * this.currentRotation);
        }
        this.paint.setColor(Color.rgb(Color.red(this.color) + rD, Color.green(this.color) + rG, Color.blue(this.color) + rB));
        canvas.save();
        canvas2.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        int i = this.arrowRotation;
        if (i != 0) {
            canvas2.rotate((float) i);
        }
        float rotation2 = this.currentRotation;
        if (!this.alwaysClose) {
            canvas2.rotate(this.currentRotation * ((float) (this.reverseAngle ? -225 : 135)));
            rotation = rotation2;
        } else {
            canvas2.rotate((this.currentRotation * ((float) (this.reverseAngle ? -180 : 180))) + 135.0f);
            rotation = 1.0f;
        }
        canvas.drawLine(((float) (-AndroidUtilities.dp(7.0f))) - (((float) AndroidUtilities.dp(1.0f)) * rotation), 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        float startYDiff = (float) (-AndroidUtilities.dp(0.5f));
        float endYDiff = ((float) AndroidUtilities.dp(7.0f)) + (((float) AndroidUtilities.dp(1.0f)) * rotation);
        Canvas canvas3 = canvas;
        float dp = ((float) (-AndroidUtilities.dp(7.0f))) + (((float) AndroidUtilities.dp(7.0f)) * rotation);
        float dp2 = ((float) AndroidUtilities.dp(0.5f)) - (((float) AndroidUtilities.dp(0.5f)) * rotation);
        canvas3.drawLine(dp, -startYDiff, dp2, -endYDiff, this.paint);
        canvas3.drawLine(dp, startYDiff, dp2, endYDiff, this.paint);
        canvas.restore();
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
