package org.telegram.p005ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/* renamed from: org.telegram.ui.Components.TextPaintUrlSpan */
public class TextPaintUrlSpan extends MetricAffectingSpan {
    private int color;
    private String currentUrl;
    private TextPaint textPaint;
    private int textSize;

    public TextPaintUrlSpan(TextPaint paint, String url) {
        this.textPaint = paint;
        this.currentUrl = url;
    }

    public String getUrl() {
        return this.currentUrl;
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
