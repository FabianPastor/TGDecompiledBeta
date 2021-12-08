package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

public class ColoredImageSpan extends ReplacementSpan {
    Drawable drawable;
    int drawableColor;

    public ColoredImageSpan(Drawable drawable2) {
        this.drawable = drawable2;
        drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i1, Paint.FontMetricsInt fontMetricsInt) {
        return this.drawable.getIntrinsicWidth();
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        if (this.drawableColor != paint.getColor()) {
            this.drawableColor = paint.getColor();
            this.drawable.setColorFilter(new PorterDuffColorFilter(this.drawableColor, PorterDuff.Mode.MULTIPLY));
        }
        canvas.save();
        canvas.translate(x, (float) (top + (((bottom - top) - this.drawable.getIntrinsicHeight()) / 2)));
        this.drawable.draw(canvas);
        canvas.restore();
    }
}
