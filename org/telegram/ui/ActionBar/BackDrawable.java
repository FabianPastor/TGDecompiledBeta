package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class BackDrawable extends Drawable {
    private boolean alwaysClose;
    private boolean animationInProgress;
    private float animationTime = BitmapDescriptorFactory.HUE_MAGENTA;
    private int color = -1;
    private int currentAnimationTime;
    private float currentRotation;
    private float finalRotation;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private boolean reverseAngle = false;
    private boolean rotatedColor = true;

    public BackDrawable(boolean close) {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.alwaysClose = close;
    }

    public void setColor(int value) {
        this.color = value;
        invalidateSelf();
    }

    public void setRotation(float rotation, boolean animated) {
        this.lastFrameTime = 0;
        if (this.currentRotation == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
            this.reverseAngle = true;
        } else if (this.currentRotation == 0.0f) {
            this.reverseAngle = false;
        }
        this.lastFrameTime = 0;
        if (animated) {
            if (this.currentRotation < rotation) {
                this.currentAnimationTime = (int) (this.currentRotation * this.animationTime);
            } else {
                this.currentAnimationTime = (int) ((DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.currentRotation) * this.animationTime);
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

    public void setRotatedColor(boolean value) {
        this.rotatedColor = value;
    }

    public void draw(Canvas canvas) {
        if (this.currentRotation != this.finalRotation) {
            if (this.lastFrameTime != 0) {
                this.currentAnimationTime = (int) (((long) this.currentAnimationTime) + (System.currentTimeMillis() - this.lastFrameTime));
                if (((float) this.currentAnimationTime) >= this.animationTime) {
                    this.currentRotation = this.finalRotation;
                } else if (this.currentRotation < this.finalRotation) {
                    this.currentRotation = this.interpolator.getInterpolation(((float) this.currentAnimationTime) / this.animationTime) * this.finalRotation;
                } else {
                    this.currentRotation = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT - this.interpolator.getInterpolation(((float) this.currentAnimationTime) / this.animationTime);
                }
            }
            this.lastFrameTime = System.currentTimeMillis();
            invalidateSelf();
        }
        int rD = this.rotatedColor ? (int) (-138.0f * this.currentRotation) : 0;
        this.paint.setColor(Color.rgb(Color.red(this.color) + rD, Color.green(this.color) + rD, Color.blue(this.color) + rD));
        canvas.save();
        canvas.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        float rotation = this.currentRotation;
        if (this.alwaysClose) {
            canvas.rotate((((float) (this.reverseAngle ? -180 : 180)) * this.currentRotation) + 135.0f);
            rotation = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
        } else {
            canvas.rotate(((float) (this.reverseAngle ? -225 : TsExtractor.TS_STREAM_TYPE_E_AC3)) * this.currentRotation);
        }
        canvas.drawLine(((float) (-AndroidUtilities.dp(7.0f))) - (((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) * rotation), 0.0f, (float) AndroidUtilities.dp(8.0f), 0.0f, this.paint);
        float startYDiff = (float) (-AndroidUtilities.dp(0.5f));
        float endYDiff = ((float) AndroidUtilities.dp(7.0f)) + (((float) AndroidUtilities.dp(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)) * rotation);
        float startXDiff = ((float) (-AndroidUtilities.dp(7.0f))) + (((float) AndroidUtilities.dp(7.0f)) * rotation);
        float endXDiff = ((float) AndroidUtilities.dp(0.5f)) - (((float) AndroidUtilities.dp(0.5f)) * rotation);
        canvas.drawLine(startXDiff, -startYDiff, endXDiff, -endYDiff, this.paint);
        canvas.drawLine(startXDiff, startYDiff, endXDiff, endYDiff, this.paint);
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
