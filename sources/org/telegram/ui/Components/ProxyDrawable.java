package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ProxyDrawable extends Drawable {
    private RectF cicleRect = new RectF();
    private boolean connected;
    private float connectedAnimationProgress;
    private int currentColorType;
    private Drawable emptyDrawable;
    private Drawable fullDrawable;
    private boolean isEnabled;
    private long lastUpdateTime;
    private Paint outerPaint = new Paint(1);
    private int radOffset = 0;

    public ProxyDrawable(Context context) {
        this.emptyDrawable = context.getResources().getDrawable(NUM);
        this.fullDrawable = context.getResources().getDrawable(NUM);
        this.outerPaint.setStyle(Paint.Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
        this.lastUpdateTime = SystemClock.elapsedRealtime();
    }

    public void setConnected(boolean enabled, boolean value, boolean animated) {
        this.isEnabled = enabled;
        this.connected = value;
        this.lastUpdateTime = SystemClock.elapsedRealtime();
        if (!animated) {
            this.connectedAnimationProgress = this.connected ? 1.0f : 0.0f;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        long newTime = SystemClock.elapsedRealtime();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (!this.isEnabled) {
            this.emptyDrawable.setBounds(getBounds());
            this.emptyDrawable.draw(canvas2);
        } else if (!this.connected || this.connectedAnimationProgress != 1.0f) {
            this.emptyDrawable.setBounds(getBounds());
            this.emptyDrawable.draw(canvas2);
            this.outerPaint.setColor(Theme.getColor("contextProgressOuter2"));
            this.outerPaint.setAlpha((int) ((1.0f - this.connectedAnimationProgress) * 255.0f));
            this.radOffset = (int) (((float) this.radOffset) + (((float) (360 * dt)) / 1000.0f));
            int width = getBounds().width();
            int height = getBounds().height();
            int x = (width / 2) - AndroidUtilities.dp(3.0f);
            int y = (height / 2) - AndroidUtilities.dp(3.0f);
            this.cicleRect.set((float) x, (float) y, (float) (x + AndroidUtilities.dp(6.0f)), (float) (AndroidUtilities.dp(6.0f) + y));
            int i = y;
            int i2 = x;
            canvas.drawArc(this.cicleRect, (float) (this.radOffset - 90), 90.0f, false, this.outerPaint);
            invalidateSelf();
        }
        if (this.isEnabled && (this.connected || this.connectedAnimationProgress != 0.0f)) {
            this.fullDrawable.setAlpha((int) (this.connectedAnimationProgress * 255.0f));
            this.fullDrawable.setBounds(getBounds());
            this.fullDrawable.draw(canvas2);
        }
        boolean z = this.connected;
        if (z) {
            float f = this.connectedAnimationProgress;
            if (f != 1.0f) {
                float f2 = f + (((float) dt) / 300.0f);
                this.connectedAnimationProgress = f2;
                if (f2 > 1.0f) {
                    this.connectedAnimationProgress = 1.0f;
                }
                invalidateSelf();
                return;
            }
        }
        if (!z) {
            float f3 = this.connectedAnimationProgress;
            if (f3 != 0.0f) {
                float f4 = f3 - (((float) dt) / 300.0f);
                this.connectedAnimationProgress = f4;
                if (f4 < 0.0f) {
                    this.connectedAnimationProgress = 0.0f;
                }
                invalidateSelf();
            }
        }
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
        this.emptyDrawable.setColorFilter(cf);
        this.fullDrawable.setColorFilter(cf);
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
