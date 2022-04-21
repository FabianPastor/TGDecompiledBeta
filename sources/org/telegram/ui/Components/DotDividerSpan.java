package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import org.telegram.messenger.AndroidUtilities;

public class DotDividerSpan extends ReplacementSpan {
    int color;
    Paint p = new Paint(1);
    int topPadding;

    public int getSize(Paint paint, CharSequence charSequence, int i, int i1, Paint.FontMetricsInt fontMetricsInt) {
        return AndroidUtilities.dp(3.0f);
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        if (this.color != paint.getColor()) {
            this.p.setColor(paint.getColor());
        }
        float radius = AndroidUtilities.dpf2(3.0f) / 2.0f;
        canvas.drawCircle(x + radius, (float) (((bottom - top) / 2) + this.topPadding), radius, this.p);
    }

    public void setTopPadding(int topPadding2) {
        this.topPadding = topPadding2;
    }
}
