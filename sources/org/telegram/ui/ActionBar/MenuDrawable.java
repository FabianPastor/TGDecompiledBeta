package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class MenuDrawable extends Drawable {
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private int iconColor;
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
            if (f2 < f) {
                this.currentAnimationTime = (int) (f2 * 200.0f);
            } else {
                this.currentAnimationTime = (int) ((1.0f - f2) * 200.0f);
            }
            this.lastFrameTime = SystemClock.elapsedRealtime();
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
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = this.lastFrameTime;
            if (j != 0) {
                int i = (int) (((long) this.currentAnimationTime) + (elapsedRealtime - j));
                this.currentAnimationTime = i;
                if (i >= 200) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) i) / 200.0f) * this.finalRotation;
                } else {
                    this.currentRotation = 1.0f - this.interpolator.getInterpolation(((float) i) / 200.0f);
                }
            }
            this.lastFrameTime = elapsedRealtime;
            invalidateSelf();
        }
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        int i2 = this.iconColor;
        if (i2 == 0) {
            i2 = Theme.getColor("actionBarDefaultIcon");
        }
        if (this.rotateToBack) {
            canvas.rotate(this.currentRotation * ((float) (this.reverseAngle ? -180 : 180)));
            this.paint.setColor(i2);
            canvas.drawLine((float) (-AndroidUtilities.dp(9.0f)), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(3.0f)) * this.currentRotation), 0.0f, this.paint);
            f6 = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
            f5 = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(2.5f)) * Math.abs(this.currentRotation));
            f4 = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(2.0f)) * Math.abs(this.currentRotation));
            f3 = (float) (-AndroidUtilities.dp(9.0f));
            f2 = (float) AndroidUtilities.dp(7.5f);
            f = Math.abs(this.currentRotation);
        } else {
            canvas.rotate(this.currentRotation * ((float) (this.reverseAngle ? -225 : 135)));
            this.paint.setColor(AndroidUtilities.getOffsetColor(i2, Theme.getColor("actionBarActionModeDefaultIcon"), this.currentRotation, 1.0f));
            canvas.drawLine(((float) (-AndroidUtilities.dp(9.0f))) + (((float) AndroidUtilities.dp(1.0f)) * this.currentRotation), 0.0f, ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(1.0f)) * this.currentRotation), 0.0f, this.paint);
            f6 = (((float) AndroidUtilities.dp(5.0f)) * (1.0f - Math.abs(this.currentRotation))) - (((float) AndroidUtilities.dp(0.5f)) * Math.abs(this.currentRotation));
            f5 = ((float) AndroidUtilities.dp(9.0f)) - (((float) AndroidUtilities.dp(9.0f)) * Math.abs(this.currentRotation));
            f4 = ((float) AndroidUtilities.dp(5.0f)) + (((float) AndroidUtilities.dp(3.0f)) * Math.abs(this.currentRotation));
            f3 = (float) (-AndroidUtilities.dp(9.0f));
            f2 = (float) AndroidUtilities.dp(9.0f);
            f = Math.abs(this.currentRotation);
        }
        float f7 = f6;
        float f8 = f5;
        float f9 = f4;
        float var_ = f3 + (f2 * f);
        canvas.drawLine(var_, -f9, f8, -f7, this.paint);
        canvas.drawLine(var_, f9, f8, f7, this.paint);
        canvas.restore();
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }

    public void setIconColor(int i) {
        this.iconColor = i;
    }
}
