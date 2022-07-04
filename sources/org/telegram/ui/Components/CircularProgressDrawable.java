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
    private float size;
    private long start;
    private float thickness;

    public CircularProgressDrawable() {
        this(-1);
    }

    public CircularProgressDrawable(int color) {
        this.size = (float) AndroidUtilities.dp(18.0f);
        this.thickness = (float) AndroidUtilities.dp(2.25f);
        this.start = -1;
        this.interpolator = new FastOutSlowInInterpolator();
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.bounds = new RectF();
        setColor(color);
    }

    public CircularProgressDrawable(float size2, float thickness2, int color) {
        this.size = (float) AndroidUtilities.dp(18.0f);
        this.thickness = (float) AndroidUtilities.dp(2.25f);
        this.start = -1;
        this.interpolator = new FastOutSlowInInterpolator();
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.bounds = new RectF();
        this.size = size2;
        this.thickness = thickness2;
        setColor(color);
    }

    private void updateSegment() {
        long t = (SystemClock.elapsedRealtime() - this.start) % 5400;
        this.segmentFrom = (((float) (t * 1520)) / 5400.0f) - 20.0f;
        this.segmentTo = ((float) (1520 * t)) / 5400.0f;
        for (int i = 0; i < 4; i++) {
            this.segmentTo += this.interpolator.getInterpolation(((float) (t - ((long) (i * 1350)))) / 667.0f) * 250.0f;
            this.segmentFrom += this.interpolator.getInterpolation(((float) (t - ((long) ((i * 1350) + 667)))) / 667.0f) * 250.0f;
        }
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
        int width = right - left;
        int height = bottom - top;
        RectF rectF = this.bounds;
        float f = this.thickness;
        float f2 = this.size;
        rectF.set(((float) left) + (((((float) width) - (f / 2.0f)) - f2) / 2.0f), ((float) top) + (((((float) height) - (f / 2.0f)) - f2) / 2.0f), ((float) left) + (((((float) width) + (f / 2.0f)) + f2) / 2.0f), ((float) top) + (((((float) height) + (f / 2.0f)) + f2) / 2.0f));
        super.setBounds(left, top, right, bottom);
        this.paint.setStrokeWidth(this.thickness);
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -2;
    }
}
