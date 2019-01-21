package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintSpan(TextPaint paint) {
        this.textPaint = paint;
    }

    public void updateMeasureState(TextPaint p) {
        p.setColor(this.textPaint.getColor());
        p.setTypeface(this.textPaint.getTypeface());
        p.setFlags(this.textPaint.getFlags());
        p.setTextSize(this.textPaint.getTextSize());
        p.baselineShift = this.textPaint.baselineShift;
        p.bgColor = this.textPaint.bgColor;
    }

    public void updateDrawState(TextPaint p) {
        p.setColor(this.textPaint.getColor());
        p.setTypeface(this.textPaint.getTypeface());
        p.setFlags(this.textPaint.getFlags());
        p.setTextSize(this.textPaint.getTextSize());
        p.baselineShift = this.textPaint.baselineShift;
        p.bgColor = this.textPaint.bgColor;
    }
}
