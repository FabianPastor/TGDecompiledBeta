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
    private final FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();
    private final Paint paint;
    private float segmentFrom;
    private float segmentTo;
    private float size = ((float) AndroidUtilities.dp(18.0f));
    private long start = -1;
    private float thickness = ((float) AndroidUtilities.dp(2.25f));

    public int getOpacity() {
        return -2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public CircularProgressDrawable(int i) {
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.bounds = new RectF();
        setColor(i);
    }

    public CircularProgressDrawable(float f, float f2, int i) {
        Paint paint2 = new Paint();
        this.paint = paint2;
        paint2.setStyle(Paint.Style.STROKE);
        this.bounds = new RectF();
        this.size = f;
        this.thickness = f2;
        setColor(i);
    }

    private void updateSegment() {
        long elapsedRealtime = (SystemClock.elapsedRealtime() - this.start) % 5400;
        float f = ((float) (1520 * elapsedRealtime)) / 5400.0f;
        this.segmentFrom = f - 20.0f;
        this.segmentTo = f;
        for (int i = 0; i < 4; i++) {
            int i2 = i * 1350;
            this.segmentTo += this.interpolator.getInterpolation(((float) (elapsedRealtime - ((long) i2))) / 667.0f) * 250.0f;
            this.segmentFrom += this.interpolator.getInterpolation(((float) (elapsedRealtime - ((long) (i2 + 667)))) / 667.0f) * 250.0f;
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

    public void setBounds(int i, int i2, int i3, int i4) {
        RectF rectF = this.bounds;
        float f = (float) i;
        float f2 = (float) (i3 - i);
        float f3 = this.thickness;
        float f4 = this.size;
        float f5 = (float) i2;
        float f6 = (float) (i4 - i2);
        rectF.set((((f2 - (f3 / 2.0f)) - f4) / 2.0f) + f, (((f6 - (f3 / 2.0f)) - f4) / 2.0f) + f5, f + (((f2 + (f3 / 2.0f)) + f4) / 2.0f), f5 + (((f6 + (f3 / 2.0f)) + f4) / 2.0f));
        super.setBounds(i, i2, i3, i4);
        this.paint.setStrokeWidth(this.thickness);
    }

    public void setColor(int i) {
        this.paint.setColor(i);
    }

    public void setAlpha(int i) {
        this.paint.setAlpha(i);
    }
}
