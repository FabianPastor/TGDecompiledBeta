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

    public int getOpacity() {
        return -2;
    }

    public void setAlpha(int i) {
    }

    public ProxyDrawable(Context context) {
        this.emptyDrawable = context.getResources().getDrawable(NUM);
        this.fullDrawable = context.getResources().getDrawable(NUM);
        this.outerPaint.setStyle(Style.STROKE);
        this.outerPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outerPaint.setStrokeCap(Cap.ROUND);
        this.lastUpdateTime = SystemClock.elapsedRealtime();
    }

    public void setConnected(boolean z, boolean z2, boolean z3) {
        this.isEnabled = z;
        this.connected = z2;
        this.lastUpdateTime = SystemClock.elapsedRealtime();
        if (!z3) {
            this.connectedAnimationProgress = this.connected ? 1.0f : 0.0f;
        }
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        float f;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = elapsedRealtime - this.lastUpdateTime;
        this.lastUpdateTime = elapsedRealtime;
        if (!this.isEnabled) {
            this.emptyDrawable.setBounds(getBounds());
            this.emptyDrawable.draw(canvas);
        } else if (!(this.connected && this.connectedAnimationProgress == 1.0f)) {
            this.emptyDrawable.setBounds(getBounds());
            this.emptyDrawable.draw(canvas);
            this.outerPaint.setColor(Theme.getColor("contextProgressOuter2"));
            this.outerPaint.setAlpha((int) ((1.0f - this.connectedAnimationProgress) * 255.0f));
            this.radOffset = (int) (((float) this.radOffset) + (((float) (360 * j)) / 1000.0f));
            int width = getBounds().width();
            width = (width / 2) - AndroidUtilities.dp(3.0f);
            int height = (getBounds().height() / 2) - AndroidUtilities.dp(3.0f);
            this.cicleRect.set((float) width, (float) height, (float) (width + AndroidUtilities.dp(6.0f)), (float) (height + AndroidUtilities.dp(6.0f)));
            canvas.drawArc(this.cicleRect, (float) (this.radOffset - 90), 90.0f, false, this.outerPaint);
            invalidateSelf();
        }
        if (this.isEnabled && (this.connected || this.connectedAnimationProgress != 0.0f)) {
            this.fullDrawable.setAlpha((int) (this.connectedAnimationProgress * 255.0f));
            this.fullDrawable.setBounds(getBounds());
            this.fullDrawable.draw(canvas);
        }
        if (this.connected) {
            f = this.connectedAnimationProgress;
            if (f != 1.0f) {
                this.connectedAnimationProgress = f + (((float) j) / 300.0f);
                if (this.connectedAnimationProgress > 1.0f) {
                    this.connectedAnimationProgress = 1.0f;
                }
                invalidateSelf();
                return;
            }
        }
        if (!this.connected) {
            f = this.connectedAnimationProgress;
            if (f != 0.0f) {
                this.connectedAnimationProgress = f - (((float) j) / 300.0f);
                if (this.connectedAnimationProgress < 0.0f) {
                    this.connectedAnimationProgress = 0.0f;
                }
                invalidateSelf();
            }
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.emptyDrawable.setColorFilter(colorFilter);
        this.fullDrawable.setColorFilter(colorFilter);
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24.0f);
    }
}
