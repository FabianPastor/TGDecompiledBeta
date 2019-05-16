package org.telegram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintUrlSpan extends MetricAffectingSpan {
    private int color;
    private String currentUrl;
    private TextPaint textPaint;
    private int textSize;

    public TextPaintUrlSpan(TextPaint textPaint, String str) {
        this.textPaint = textPaint;
        this.currentUrl = str;
    }

    public String getUrl() {
        return this.currentUrl;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public void updateMeasureState(TextPaint textPaint) {
        TextPaint textPaint2 = this.textPaint;
        if (textPaint2 != null) {
            textPaint.setColor(textPaint2.getColor());
            textPaint.setTypeface(this.textPaint.getTypeface());
            textPaint.setFlags(this.textPaint.getFlags());
            textPaint.setTextSize(this.textPaint.getTextSize());
            textPaint2 = this.textPaint;
            textPaint.baselineShift = textPaint2.baselineShift;
            textPaint.bgColor = textPaint2.bgColor;
        }
    }

    public void updateDrawState(TextPaint textPaint) {
        TextPaint textPaint2 = this.textPaint;
        if (textPaint2 != null) {
            textPaint.setColor(textPaint2.getColor());
            textPaint.setTypeface(this.textPaint.getTypeface());
            textPaint.setFlags(this.textPaint.getFlags());
            textPaint.setTextSize(this.textPaint.getTextSize());
            textPaint2 = this.textPaint;
            textPaint.baselineShift = textPaint2.baselineShift;
            textPaint.bgColor = textPaint2.bgColor;
        }
    }
}
