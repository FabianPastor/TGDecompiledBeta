package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable2 extends Drawable {
    private float angle;
    private boolean animating;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private long lastFrameTime;
    private Paint paint = new Paint(1);
    private RectF rect = new RectF();

    public CloseProgressDrawable2() {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStyle(Style.STROKE);
    }

    public void startAnimation() {
        this.animating = true;
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        this.animating = false;
    }

    public void setColor(int value) {
        this.paint.setColor(value);
    }

    public void draw(Canvas canvas) {
        float f;
        Canvas canvas2 = canvas;
        long newTime = System.currentTimeMillis();
        float f2 = 0.0f;
        if (this.lastFrameTime != 0) {
            long dt = newTime - r0.lastFrameTime;
            if (r0.animating || r0.angle != 0.0f) {
                r0.angle += ((float) (360 * dt)) / 500.0f;
                if (r0.animating || r0.angle < 720.0f) {
                    r0.angle -= (float) (((int) (r0.angle / 720.0f)) * 720);
                } else {
                    r0.angle = 0.0f;
                }
                invalidateSelf();
            }
        }
        canvas.save();
        canvas2.translate((float) (getIntrinsicWidth() / 2), (float) (getIntrinsicHeight() / 2));
        canvas2.rotate(-45.0f);
        float progress1 = 1.0f;
        float progress2 = 1.0f;
        float progress3 = 1.0f;
        float progress4 = 0.0f;
        if (r0.angle >= 0.0f && r0.angle < 90.0f) {
            progress1 = 1.0f - (r0.angle / 90.0f);
        } else if (r0.angle >= 90.0f && r0.angle < 180.0f) {
            progress1 = 0.0f;
            progress2 = 1.0f - ((r0.angle - 90.0f) / 90.0f);
        } else if (r0.angle >= 180.0f && r0.angle < 270.0f) {
            progress2 = 0.0f;
            progress1 = 0.0f;
            progress3 = 1.0f - ((r0.angle - 180.0f) / 90.0f);
        } else if (r0.angle >= 270.0f && r0.angle < 360.0f) {
            progress3 = 0.0f;
            progress2 = 0.0f;
            progress1 = 0.0f;
            progress4 = (r0.angle - 270.0f) / 90.0f;
        } else if (r0.angle >= 360.0f && r0.angle < 450.0f) {
            progress3 = 0.0f;
            progress2 = 0.0f;
            progress1 = 0.0f;
            progress4 = 1.0f - ((r0.angle - 360.0f) / 90.0f);
        } else if (r0.angle >= 450.0f && r0.angle < 540.0f) {
            progress3 = 0.0f;
            progress2 = 0.0f;
            progress1 = (r0.angle - 450.0f) / 90.0f;
        } else if (r0.angle >= 540.0f && r0.angle < 630.0f) {
            progress3 = 0.0f;
            progress2 = (r0.angle - 540.0f) / 90.0f;
        } else if (r0.angle >= 630.0f && r0.angle < 720.0f) {
            progress3 = (r0.angle - 630.0f) / 90.0f;
        }
        float progress12 = progress1;
        float progress22 = progress2;
        float progress32 = progress3;
        float progress42 = progress4;
        if (progress12 != 0.0f) {
            f = 8.0f;
            canvas2.drawLine(0.0f, 0.0f, 0.0f, ((float) AndroidUtilities.dp(8.0f)) * progress12, r0.paint);
        } else {
            f = 8.0f;
        }
        if (progress22 != 0.0f) {
            canvas2.drawLine(((float) (-AndroidUtilities.dp(f))) * progress22, 0.0f, 0.0f, 0.0f, r0.paint);
        }
        if (progress32 != 0.0f) {
            canvas2.drawLine(0.0f, ((float) (-AndroidUtilities.dp(f))) * progress32, 0.0f, 0.0f, r0.paint);
        }
        if (progress42 != 1.0f) {
            canvas2.drawLine(((float) AndroidUtilities.dp(f)) * progress42, 0.0f, (float) AndroidUtilities.dp(f), 0.0f, r0.paint);
        }
        canvas.restore();
        int cx = getBounds().centerX();
        int cy = getBounds().centerY();
        r0.rect.set((float) (cx - AndroidUtilities.dp(f)), (float) (cy - AndroidUtilities.dp(f)), (float) (AndroidUtilities.dp(f) + cx), (float) (cy + AndroidUtilities.dp(f)));
        RectF rectF = r0.rect;
        if (r0.angle >= 360.0f) {
            f2 = r0.angle - 360.0f;
        }
        canvas2.drawArc(rectF, f2 - 45.0f, r0.angle < 360.0f ? r0.angle : 720.0f - r0.angle, false, r0.paint);
        r0.lastFrameTime = newTime;
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
        this.paint.setColorFilter(cf);
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
