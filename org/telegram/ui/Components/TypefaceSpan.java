package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TypefaceSpan extends MetricAffectingSpan {
    private int color;
    private Typeface mTypeface;
    private int textSize;

    public TypefaceSpan(Typeface typeface) {
        this.mTypeface = typeface;
    }

    public TypefaceSpan(Typeface typeface, int size) {
        this.mTypeface = typeface;
        this.textSize = size;
    }

    public TypefaceSpan(Typeface typeface, int size, int textColor) {
        this.mTypeface = typeface;
        this.textSize = size;
        this.color = textColor;
    }

    public void updateMeasureState(TextPaint p) {
        if (this.mTypeface != null) {
            p.setTypeface(this.mTypeface);
        }
        if (this.textSize != 0) {
            p.setTextSize((float) this.textSize);
        }
        p.setFlags(p.getFlags() | 128);
    }

    public void updateDrawState(TextPaint tp) {
        if (this.mTypeface != null) {
            tp.setTypeface(this.mTypeface);
        }
        if (this.textSize != 0) {
            tp.setTextSize((float) this.textSize);
        }
        if (this.color != 0) {
            tp.setColor(this.color);
        }
        tp.setFlags(tp.getFlags() | 128);
    }
}
