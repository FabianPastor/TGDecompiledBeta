package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintSpan(TextPaint textPaint2) {
        this.textPaint = textPaint2;
    }

    public void updateMeasureState(TextPaint textPaint2) {
        textPaint2.setColor(this.textPaint.getColor());
        textPaint2.setTypeface(this.textPaint.getTypeface());
        textPaint2.setFlags(this.textPaint.getFlags());
        textPaint2.setTextSize(this.textPaint.getTextSize());
        TextPaint textPaint3 = this.textPaint;
        textPaint2.baselineShift = textPaint3.baselineShift;
        textPaint2.bgColor = textPaint3.bgColor;
    }

    public void updateDrawState(TextPaint textPaint2) {
        textPaint2.setColor(this.textPaint.getColor());
        textPaint2.setTypeface(this.textPaint.getTypeface());
        textPaint2.setFlags(this.textPaint.getFlags());
        textPaint2.setTextSize(this.textPaint.getTextSize());
        TextPaint textPaint3 = this.textPaint;
        textPaint2.baselineShift = textPaint3.baselineShift;
        textPaint2.bgColor = textPaint3.bgColor;
    }
}
