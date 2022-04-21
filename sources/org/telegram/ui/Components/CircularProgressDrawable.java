package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class CircularProgressDrawable extends Drawable {
    private final RectF bounds;
    private final FastOutSlowInInterpolator interpolator;
    private final Paint paint;
    private float segmentFrom;
    private float segmentTo;
    private long start;

    public CircularProgressDrawable() {
        this(-1);
    }

    public CircularProgressDrawable(int color) {
        this.start = -1;
        this.interpolator = new FastOutSlowInInterpolator();
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.bounds = new RectF();
        setColor(color);
    }

    private void updateSegment() {
        float t = (((float) (SystemClock.elapsedRealtime() - this.start)) % 5400.0f) / 667.0f;
        this.segmentFrom = ((t * 187.74815f) + ((((this.interpolator.getInterpolation(t - 1.0f) + this.interpolator.getInterpolation(t - 3.024f)) + this.interpolator.getInterpolation(t - 5.048f)) + this.interpolator.getInterpolation(t - 7.072f)) * 250.0f)) - 20.0f;
        this.segmentTo = (187.74815f * t) + ((this.interpolator.getInterpolation(t) + this.interpolator.getInterpolation(t - 2.024f) + this.interpolator.getInterpolation(t - 4.048f) + this.interpolator.getInterpolation(t - 6.072f)) * 250.0f);
    }

    public void draw(Canvas canvas) {
        if (this.start < 0) {
            this.start = SystemClock.elapsedRealtime();
        }
        updateSegment();
        RectF rectF = this.bounds;
        float f = this.segmentFrom;
        canvas.drawArc(rectF, f, this.segmentTo - f, false, this.paint);
        invalidateSelf();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        int i = left;
        int i2 = top;
        float radius = (float) AndroidUtilities.dp(9.0f);
        float thickness = (float) AndroidUtilities.dp(2.25f);
        int width = right - i;
        int height = bottom - i2;
        this.bounds.set((((float) i) + ((((float) width) - (thickness / 2.0f)) / 2.0f)) - radius, (((float) i2) + ((((float) height) - (thickness / 2.0f)) / 2.0f)) - radius, ((float) i) + ((((float) width) + (thickness / 2.0f)) / 2.0f) + radius, ((float) i2) + ((((float) height) + (thickness / 2.0f)) / 2.0f) + radius);
        super.setBounds(left, top, right, bottom);
        this.paint.setStrokeWidth(thickness);
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -2;
    }
}
