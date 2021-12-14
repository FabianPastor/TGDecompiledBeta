package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintMarkSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintMarkSpan(TextPaint textPaint2) {
        this.textPaint = textPaint2;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public void updateMeasureState(TextPaint textPaint2) {
        TextPaint textPaint3 = this.textPaint;
        if (textPaint3 != null) {
            textPaint2.setColor(textPaint3.getColor());
            textPaint2.setTypeface(this.textPaint.getTypeface());
            textPaint2.setFlags(this.textPaint.getFlags());
            textPaint2.setTextSize(this.textPaint.getTextSize());
            TextPaint textPaint4 = this.textPaint;
            textPaint2.baselineShift = textPaint4.baselineShift;
            textPaint2.bgColor = textPaint4.bgColor;
        }
    }

    public void updateDrawState(TextPaint textPaint2) {
        TextPaint textPaint3 = this.textPaint;
        if (textPaint3 != null) {
            textPaint2.setColor(textPaint3.getColor());
            textPaint2.setTypeface(this.textPaint.getTypeface());
            textPaint2.setFlags(this.textPaint.getFlags());
            textPaint2.setTextSize(this.textPaint.getTextSize());
            TextPaint textPaint4 = this.textPaint;
            textPaint2.baselineShift = textPaint4.baselineShift;
            textPaint2.bgColor = textPaint4.bgColor;
        }
    }
}
