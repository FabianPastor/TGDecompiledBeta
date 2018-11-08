package org.telegram.p005ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/* renamed from: org.telegram.ui.Components.TextPaintSpan */
public class TextPaintSpan extends MetricAffectingSpan {
    private int color;
    private TextPaint textPaint;
    private int textSize;

    public TextPaintSpan(TextPaint paint) {
        this.textPaint = paint;
    }

    public void updateMeasureState(TextPaint p) {
        p.setColor(this.textPaint.getColor());
        p.setTypeface(this.textPaint.getTypeface());
        p.setFlags(this.textPaint.getFlags());
    }

    public void updateDrawState(TextPaint p) {
        p.setColor(this.textPaint.getColor());
        p.setTypeface(this.textPaint.getTypeface());
        p.setFlags(this.textPaint.getFlags());
    }
}
