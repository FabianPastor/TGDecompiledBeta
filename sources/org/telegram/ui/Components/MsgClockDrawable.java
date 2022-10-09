package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes3.dex */
public class MsgClockDrawable extends Drawable {
    private int alpha;
    private int color;
    private int colorAlpha;
    private Drawable.ConstantState constantState;
    private Paint paint;
    private long startTime;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public MsgClockDrawable() {
        Paint paint = new Paint(1);
        this.paint = paint;
        this.alpha = 255;
        this.colorAlpha = 255;
        paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        this.startTime = System.currentTimeMillis();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        android.graphics.Rect bounds = getBounds();
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), (Math.min(bounds.width(), bounds.height()) >> 1) - AndroidUtilities.dp(0.5f), this.paint);
        long currentTimeMillis = System.currentTimeMillis();
        canvas.save();
        canvas.rotate(((((float) (currentTimeMillis - this.startTime)) % 1500.0f) * 360.0f) / 1500.0f, bounds.centerX(), bounds.centerY());
        canvas.drawLine(bounds.centerX(), bounds.centerY(), bounds.centerX(), bounds.centerY() - AndroidUtilities.dp(3.0f), this.paint);
        canvas.restore();
        canvas.save();
        canvas.rotate(((((float) (currentTimeMillis - this.startTime)) % 4500.0f) * 360.0f) / 4500.0f, bounds.centerX(), bounds.centerY());
        canvas.drawLine(bounds.centerX(), bounds.centerY(), bounds.centerX() + AndroidUtilities.dp(2.3f), bounds.centerY(), this.paint);
        canvas.restore();
    }

    public void setColor(int i) {
        if (i != this.color) {
            int alpha = Color.alpha(i);
            this.colorAlpha = alpha;
            this.paint.setColor(ColorUtils.setAlphaComponent(i, (int) (this.alpha * (alpha / 255.0f))));
        }
        this.color = i;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(12.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(12.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (this.alpha != i) {
            this.alpha = i;
            this.paint.setAlpha((int) (i * (this.colorAlpha / 255.0f)));
        }
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        if (this.constantState == null) {
            this.constantState = new Drawable.ConstantState(this) { // from class: org.telegram.ui.Components.MsgClockDrawable.1
                @Override // android.graphics.drawable.Drawable.ConstantState
                public int getChangingConfigurations() {
                    return 0;
                }

                @Override // android.graphics.drawable.Drawable.ConstantState
                public Drawable newDrawable() {
                    return new MsgClockDrawable();
                }
            };
        }
        return this.constantState;
    }
}
