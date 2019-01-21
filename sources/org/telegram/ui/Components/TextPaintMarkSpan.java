package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintMarkSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintMarkSpan(TextPaint paint) {
        this.textPaint = paint;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public void updateMeasureState(TextPaint p) {
        if (this.textPaint != null) {
            p.setColor(this.textPaint.getColor());
            p.setTypeface(this.textPaint.getTypeface());
            p.setFlags(this.textPaint.getFlags());
            p.setTextSize(this.textPaint.getTextSize());
            p.baselineShift = this.textPaint.baselineShift;
            p.bgColor = this.textPaint.bgColor;
        }
    }

    public void updateDrawState(TextPaint p) {
        if (this.textPaint != null) {
            p.setColor(this.textPaint.getColor());
            p.setTypeface(this.textPaint.getTypeface());
            p.setFlags(this.textPaint.getFlags());
            p.setTextSize(this.textPaint.getTextSize());
            p.baselineShift = this.textPaint.baselineShift;
            p.bgColor = this.textPaint.bgColor;
        }
    }
}
