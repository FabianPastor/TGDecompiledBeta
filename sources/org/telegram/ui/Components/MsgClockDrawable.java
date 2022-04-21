package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;

public class MsgClockDrawable extends Drawable {
    private int alpha = 255;
    private int color;
    private int colorAlpha = 255;
    private Drawable.ConstantState constantState;
    private Paint paint;
    private long startTime;

    public MsgClockDrawable() {
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        this.startTime = System.currentTimeMillis();
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        canvas2.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) ((Math.min(bounds.width(), bounds.height()) >> 1) - AndroidUtilities.dp(0.5f)), this.paint);
        long currentTime = System.currentTimeMillis();
        float rotateHourTime = 1500.0f * 3.0f;
        canvas.save();
        canvas2.rotate(((((float) (currentTime - this.startTime)) % 1500.0f) * 360.0f) / 1500.0f, (float) bounds.centerX(), (float) bounds.centerY());
        canvas.drawLine((float) bounds.centerX(), (float) bounds.centerY(), (float) bounds.centerX(), (float) (bounds.centerY() - AndroidUtilities.dp(3.0f)), this.paint);
        canvas.restore();
        canvas.save();
        canvas2.rotate(((((float) (currentTime - this.startTime)) % rotateHourTime) * 360.0f) / rotateHourTime, (float) bounds.centerX(), (float) bounds.centerY());
        canvas.drawLine((float) bounds.centerX(), (float) bounds.centerY(), (float) (bounds.centerX() + AndroidUtilities.dp(2.3f)), (float) bounds.centerY(), this.paint);
        canvas.restore();
    }

    public void setColor(int color2) {
        if (color2 != this.color) {
            int alpha2 = Color.alpha(color2);
            this.colorAlpha = alpha2;
            this.paint.setColor(ColorUtils.setAlphaComponent(color2, (int) (((float) this.alpha) * (((float) alpha2) / 255.0f))));
        }
        this.color = color2;
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(12.0f);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(12.0f);
    }

    public void setAlpha(int i) {
        if (this.alpha != i) {
            this.alpha = i;
            this.paint.setAlpha((int) (((float) i) * (((float) this.colorAlpha) / 255.0f)));
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -2;
    }

    public Drawable.ConstantState getConstantState() {
        if (this.constantState == null) {
            this.constantState = new Drawable.ConstantState() {
                public Drawable newDrawable() {
                    return new MsgClockDrawable();
                }

                public int getChangingConfigurations() {
                    return 0;
                }
            };
        }
        return this.constantState;
    }
}
