package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private int currentColor;
    private int globalColorAlpha = 255;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();
    private int side;

    public CloseProgressDrawable2() {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStyle(Paint.Style.STROKE);
        this.side = AndroidUtilities.dp(8.0f);
    }

    public void startAnimation() {
        this.animating = true;
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        this.animating = false;
    }

    public boolean isAnimating() {
        return this.animating;
    }

    private void setColor(int value) {
        if (this.currentColor != value) {
            this.globalColorAlpha = Color.alpha(value);
            this.paint.setColor(ColorUtils.setAlphaComponent(value, 255));
        }
    }

    public void setSide(int value) {
        this.side = value;
    }

    public void draw(Canvas canvas) {
        float progress4;
        float progress3;
        float progress2;
        float progress1;
        Canvas canvas2 = canvas;
        long newTime = System.currentTimeMillis();
        setColor(getCurrentColor());
        long j = this.lastFrameTime;
        float f = 0.0f;
        if (j != 0) {
            long dt = newTime - j;
            boolean z = this.animating;
            if (z || this.angle != 0.0f) {
                float f2 = this.angle + (((float) (360 * dt)) / 500.0f);
                this.angle = f2;
                if (z || f2 < 720.0f) {
                    this.angle = f2 - ((float) (((int) (f2 / 720.0f)) * 720));
                } else {
                    this.angle = 0.0f;
                }
                invalidateSelf();
            }
        }
        if (this.globalColorAlpha == 255 || getBounds() == null || getBounds().isEmpty()) {
            canvas.save();
        } else {
            canvas.saveLayerAlpha((float) getBounds().left, (float) getBounds().top, (float) getBounds().right, (float) getBounds().bottom, this.globalColorAlpha, 31);
        }
        canvas2.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas2.rotate(-45.0f);
        float f3 = this.angle;
        if (f3 >= 0.0f && f3 < 90.0f) {
            progress1 = 1.0f - (f3 / 90.0f);
            progress2 = 1.0f;
            progress3 = 1.0f;
            progress4 = 0.0f;
        } else if (f3 >= 90.0f && f3 < 180.0f) {
            progress1 = 0.0f;
            progress2 = 1.0f - ((f3 - 90.0f) / 90.0f);
            progress3 = 1.0f;
            progress4 = 0.0f;
        } else if (f3 >= 180.0f && f3 < 270.0f) {
            progress1 = 0.0f;
            progress2 = 0.0f;
            progress3 = 1.0f - ((f3 - 180.0f) / 90.0f);
            progress4 = 0.0f;
        } else if (f3 >= 270.0f && f3 < 360.0f) {
            progress1 = 0.0f;
            progress2 = 0.0f;
            progress3 = 0.0f;
            progress4 = (f3 - 270.0f) / 90.0f;
        } else if (f3 >= 360.0f && f3 < 450.0f) {
            progress1 = 0.0f;
            progress2 = 0.0f;
            progress3 = 0.0f;
            progress4 = 1.0f - ((f3 - 360.0f) / 90.0f);
        } else if (f3 >= 450.0f && f3 < 540.0f) {
            progress1 = (f3 - 450.0f) / 90.0f;
            progress2 = 0.0f;
            progress3 = 0.0f;
            progress4 = 0.0f;
        } else if (f3 >= 540.0f && f3 < 630.0f) {
            progress1 = 1.0f;
            progress2 = (f3 - 540.0f) / 90.0f;
            progress3 = 0.0f;
            progress4 = 0.0f;
        } else if (f3 < 630.0f || f3 >= 720.0f) {
            progress1 = 1.0f;
            progress2 = 1.0f;
            progress3 = 1.0f;
            progress4 = 0.0f;
        } else {
            progress1 = 1.0f;
            progress2 = 1.0f;
            progress3 = (f3 - 630.0f) / 90.0f;
            progress4 = 0.0f;
        }
        if (progress1 != 0.0f) {
            canvas.drawLine(0.0f, 0.0f, 0.0f, ((float) this.side) * progress1, this.paint);
        }
        if (progress2 != 0.0f) {
            canvas.drawLine(((float) (-this.side)) * progress2, 0.0f, 0.0f, 0.0f, this.paint);
        }
        if (progress3 != 0.0f) {
            canvas.drawLine(0.0f, ((float) (-this.side)) * progress3, 0.0f, 0.0f, this.paint);
        }
        if (progress4 != 1.0f) {
            int i = this.side;
            canvas.drawLine(((float) i) * progress4, 0.0f, (float) i, 0.0f, this.paint);
        }
        canvas.restore();
        int cx = getBounds().centerX();
        int cy = getBounds().centerY();
        RectF rectF = this.rect;
        int i2 = this.side;
        rectF.set((float) (cx - i2), (float) (cy - i2), (float) (cx + i2), (float) (cy + i2));
        RectF rectF2 = this.rect;
        float f4 = this.angle;
        if (f4 >= 360.0f) {
            f = f4 - 360.0f;
        }
        canvas.drawArc(rectF2, f - 45.0f, f4 < 360.0f ? f4 : 720.0f - f4, false, this.paint);
        this.lastFrameTime = newTime;
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

    /* access modifiers changed from: protected */
    public int getCurrentColor() {
        return -1;
    }
}
