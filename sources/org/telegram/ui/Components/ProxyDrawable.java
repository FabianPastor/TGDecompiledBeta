package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;

public class ProxyDrawable extends Drawable {
    private RectF cicleRect = new RectF();
    private boolean connected;
    private float connectedAnimationProgress;
    private int currentColorType;
    private Drawable emptyDrawable;
    private Drawable fullDrawable;
    private long lastUpdateTime;
    private Paint outerPaint = new Paint(1);
    private int radOffset = 0;

    public ProxyDrawable(Context context) {
        this.emptyDrawable = context.getResources().getDrawable(R.drawable.proxy_off);
        this.fullDrawable = context.getResources().getDrawable(R.drawable.proxy_on);
        this.outerPaint.setStyle(Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Cap.ROUND);
        this.lastUpdateTime = SystemClock.uptimeMillis();
    }

    public void setConnected(boolean value, boolean animated) {
        this.connected = value;
        this.lastUpdateTime = SystemClock.uptimeMillis();
        if (!animated) {
            this.connectedAnimationProgress = this.connected ? 1.0f : 0.0f;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        long newTime = SystemClock.uptimeMillis();
        long dt = newTime - this.lastUpdateTime;
        this.lastUpdateTime = newTime;
        if (!(this.connected && this.connectedAnimationProgress == 1.0f)) {
            this.emptyDrawable.setBounds(getBounds());
            this.emptyDrawable.draw(canvas);
            this.outerPaint.setColor(Theme.getColor(Theme.key_contextProgressOuter2));
            this.outerPaint.setAlpha((int) (255.0f * (1.0f - this.connectedAnimationProgress)));
            this.radOffset = (int) (((float) this.radOffset) + (((float) (360 * dt)) / 1000.0f));
            int width = getBounds().width();
            int x = (width / 2) - AndroidUtilities.dp(3.0f);
            int y = (getBounds().height() / 2) - AndroidUtilities.dp(3.0f);
            this.cicleRect.set((float) x, (float) y, (float) (AndroidUtilities.dp(6.0f) + x), (float) (AndroidUtilities.dp(6.0f) + y));
            canvas.drawArc(this.cicleRect, (float) (this.radOffset - 90), 90.0f, false, this.outerPaint);
            invalidateSelf();
        }
        if (this.connected || this.connectedAnimationProgress != 0.0f) {
            this.fullDrawable.setAlpha((int) (255.0f * this.connectedAnimationProgress));
            this.fullDrawable.setBounds(getBounds());
            this.fullDrawable.draw(canvas);
        }
        if (this.connected && this.connectedAnimationProgress != 1.0f) {
            this.connectedAnimationProgress += ((float) dt) / 300.0f;
            if (this.connectedAnimationProgress > 1.0f) {
                this.connectedAnimationProgress = 1.0f;
            }
            invalidateSelf();
        } else if (!this.connected && this.connectedAnimationProgress != 0.0f) {
            this.connectedAnimationProgress -= ((float) dt) / 300.0f;
            if (this.connectedAnimationProgress < 0.0f) {
                this.connectedAnimationProgress = 0.0f;
            }
            invalidateSelf();
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
