package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.Theme;

public class ColoredImageSpan extends ReplacementSpan {
    String colorKey;
    Drawable drawable;
    int drawableColor;
    private int size;
    private int topOffset;
    boolean usePaintColor;

    public ColoredImageSpan(int imageRes) {
        this(ContextCompat.getDrawable(ApplicationLoader.applicationContext, imageRes));
    }

    public ColoredImageSpan(Drawable drawable2) {
        this.usePaintColor = true;
        this.topOffset = 0;
        this.drawable = drawable2;
        drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
    }

    public void setSize(int size2) {
        this.size = size2;
        this.drawable.setBounds(0, 0, size2, size2);
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i1, Paint.FontMetricsInt fontMetricsInt) {
        int i2 = this.size;
        return i2 != 0 ? i2 : this.drawable.getIntrinsicWidth();
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color;
        if (this.usePaintColor) {
            color = paint.getColor();
        } else {
            color = Theme.getColor(this.colorKey);
        }
        if (this.drawableColor != color) {
            this.drawableColor = color;
            this.drawable.setColorFilter(new PorterDuffColorFilter(this.drawableColor, PorterDuff.Mode.MULTIPLY));
        }
        int lineHeight = bottom - top;
        int drawableHeight = this.size;
        if (drawableHeight == 0) {
            drawableHeight = this.drawable.getIntrinsicHeight();
        }
        canvas.save();
        canvas.translate(x, (float) (top + ((lineHeight - drawableHeight) / 2) + AndroidUtilities.dp((float) this.topOffset)));
        this.drawable.draw(canvas);
        canvas.restore();
    }

    public void setColorKey(String colorKey2) {
        this.colorKey = colorKey2;
        this.usePaintColor = false;
    }

    public void setTopOffset(int topOffset2) {
        this.topOffset = topOffset2;
    }
}
