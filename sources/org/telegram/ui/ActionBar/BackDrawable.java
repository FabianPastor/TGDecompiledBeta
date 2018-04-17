package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public class BackDrawable extends Drawable {
    private boolean alwaysClose;
    private boolean animationInProgress;
    private float animationTime = 300.0f;
    private int color = -1;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private boolean reverseAngle = false;
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

    public void setRotation(float rotation, boolean animated) {
        this.lastFrameTime = 0;
        if (this.currentRotation == 1.0f) {
            this.reverseAngle = true;
        } else if (this.currentRotation == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (animated) {
            if (this.currentRotation < rotation) {
                this.currentAnimationTime = (int) (this.currentRotation * this.animationTime);
            } else {
                this.currentAnimationTime = (int) ((1.0f - this.currentRotation) * this.animationTime);
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
        Canvas canvas2 = canvas;
        if (this.currentRotation != this.finalRotation) {
            if (r0.lastFrameTime != 0) {
                r0.currentAnimationTime = (int) (((long) r0.currentAnimationTime) + (System.currentTimeMillis() - r0.lastFrameTime));
                if (((float) r0.currentAnimationTime) >= r0.animationTime) {
                    r0.currentRotation = r0.finalRotation;
                } else if (r0.currentRotation < r0.finalRotation) {
                    r0.currentRotation = r0.interpolator.getInterpolation(((float) r0.currentAnimationTime) / r0.animationTime) * r0.finalRotation;
                } else {
                    r0.currentRotation = 1.0f - r0.interpolator.getInterpolation(((float) r0.currentAnimationTime) / r0.animationTime);
                }
            }
            r0.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        int i = 0;
        int rD = r0.rotated ? (int) (((float) (Color.red(r0.rotatedColor) - Color.red(r0.color))) * r0.currentRotation) : 0;
        int rG = r0.rotated ? (int) (((float) (Color.green(r0.rotatedColor) - Color.green(r0.color))) * r0.currentRotation) : 0;
        if (r0.rotated) {
            i = (int) (((float) (Color.blue(r0.rotatedColor) - Color.blue(r0.color))) * r0.currentRotation);
        }
        r0.paint.setColor(Color.rgb(Color.red(r0.color) + rD, Color.green(r0.color) + rG, Color.blue(r0.color) + i));
        canvas.save();
        canvas2.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        float rotation = r0.currentRotation;
        if (r0.alwaysClose) {
            canvas2.rotate(135.0f + (r0.currentRotation * ((float) (r0.reverseAngle ? -180 : 180))));
            rotation = 1.0f;
        } else {
            canvas2.rotate(r0.currentRotation * ((float) (r0.reverseAngle ? -225 : TsExtractor.TS_STREAM_TYPE_E_AC3)));
        }
        float rotation2 = rotation;
        canvas2.drawLine(((float) (-AndroidUtilities.dp(7.0f))) - (((float) AndroidUtilities.dp(1.0f)) * rotation2), 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, r0.paint);
        float startYDiff = (float) (-AndroidUtilities.dp(0.5f));
        float endYDiff = ((float) AndroidUtilities.dp(7.0f)) + (((float) AndroidUtilities.dp(1.0f)) * rotation2);
        Canvas canvas3 = canvas2;
        float dp = ((float) (-AndroidUtilities.dp(7.0f))) + (((float) AndroidUtilities.dp(7.0f)) * rotation2);
        float dp2 = ((float) AndroidUtilities.dp(0.5f)) - (((float) AndroidUtilities.dp(0.5f)) * rotation2);
        canvas3.drawLine(dp, -startYDiff, dp2, -endYDiff, r0.paint);
        canvas3.drawLine(dp, startYDiff, dp2, endYDiff, r0.paint);
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
