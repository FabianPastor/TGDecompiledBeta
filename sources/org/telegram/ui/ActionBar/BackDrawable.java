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
                this.currentAnimationTime = (int) (this.currentRotation * this.animationTime);
            } else {
                this.currentAnimationTime = (int) ((1.0f - this.currentRotation) * this.animationTime);
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
        float f;
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                this.currentAnimationTime = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                if (((float) this.currentAnimationTime) >= this.animationTime) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) this.currentAnimationTime) / this.animationTime) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) this.currentAnimationTime) / this.animationTime);
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
        float f2 = this.currentRotation;
        if (this.alwaysClose) {
            canvas.rotate(135.0f + (this.currentRotation * ((float) (this.reverseAngle ? -180 : 180))));
            f = 1.0f;
        } else {
            canvas.rotate(this.currentRotation * ((float) (this.reverseAngle ? -225 : TsExtractor.TS_STREAM_TYPE_E_AC3)));
            f = f2;
        }
        canvas.drawLine(((float) (-AndroidUtilities.dp(7.0f))) - (((float) AndroidUtilities.dp(1.0f)) * f), 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        float f3 = (float) (-AndroidUtilities.dp(0.5f));
        float dp = ((float) AndroidUtilities.dp(7.0f)) + (((float) AndroidUtilities.dp(1.0f)) * f);
        Canvas canvas2 = canvas;
        float dp2 = ((float) (-AndroidUtilities.dp(7.0f))) + (((float) AndroidUtilities.dp(7.0f)) * f);
        float dp3 = ((float) AndroidUtilities.dp(0.5f)) - (((float) AndroidUtilities.dp(0.5f)) * f);
        canvas2.drawLine(dp2, -f3, dp3, -dp, this.paint);
        canvas2.drawLine(dp2, f3, dp3, dp, this.paint);
        canvas.restore();
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
