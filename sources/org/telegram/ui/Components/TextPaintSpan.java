package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
/* loaded from: classes3.dex */
public class TextPaintSpan extends MetricAffectingSpan {
    private TextPaint textPaint;

    public TextPaintSpan(TextPaint textPaint) {
        this.textPaint = textPaint;
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint textPaint) {
        textPaint.setColor(this.textPaint.getColor());
        textPaint.setTypeface(this.textPaint.getTypeface());
        textPaint.setFlags(this.textPaint.getFlags());
        textPaint.setTextSize(this.textPaint.getTextSize());
        TextPaint textPaint2 = this.textPaint;
        textPaint.baselineShift = textPaint2.baselineShift;
        textPaint.bgColor = textPaint2.bgColor;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setColor(this.textPaint.getColor());
        textPaint.setTypeface(this.textPaint.getTypeface());
        textPaint.setFlags(this.textPaint.getFlags());
        textPaint.setTextSize(this.textPaint.getTextSize());
        TextPaint textPaint2 = this.textPaint;
        textPaint.baselineShift = textPaint2.baselineShift;
        textPaint.bgColor = textPaint2.bgColor;
    }
}
