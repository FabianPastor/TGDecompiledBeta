package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;

public class TextStyleSpan extends MetricAffectingSpan {
    private int color;
    private boolean strike;
    private int textSize;
    private Typeface typeface;
    private boolean underline;

    public TextStyleSpan(Typeface typeface) {
        this.typeface = typeface;
    }

    public TextStyleSpan(Typeface typeface, int i) {
        this.typeface = typeface;
        this.textSize = i;
    }

    public TextStyleSpan(Typeface typeface, int i, int i2) {
        this.typeface = typeface;
        if (i > 0) {
            this.textSize = i;
        }
        this.color = i2;
    }

    public Typeface getTypeface() {
        return this.typeface;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public boolean isMono() {
        return this.typeface == Typeface.MONOSPACE;
    }

    public boolean isBold() {
        return this.typeface == AndroidUtilities.getTypeface("fonts/rmedium.ttf");
    }

    public boolean isItalic() {
        return this.typeface == AndroidUtilities.getTypeface("fonts/ritalic.ttf");
    }

    public boolean isBoldItalic() {
        return this.typeface == AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
    }

    public void updateMeasureState(TextPaint textPaint) {
        Typeface typeface = this.typeface;
        if (typeface != null) {
            textPaint.setTypeface(typeface);
        }
        int i = this.textSize;
        if (i != 0) {
            textPaint.setTextSize((float) i);
        }
        if (this.underline) {
            textPaint.setFlags(textPaint.getFlags() | 8);
        } else {
            textPaint.setFlags(textPaint.getFlags() & -9);
        }
        if (this.strike) {
            textPaint.setFlags(textPaint.getFlags() | 16);
        } else {
            textPaint.setFlags(textPaint.getFlags() & -17);
        }
        textPaint.setFlags(textPaint.getFlags() | 128);
    }

    public void updateDrawState(TextPaint textPaint) {
        Typeface typeface = this.typeface;
        if (typeface != null) {
            textPaint.setTypeface(typeface);
        }
        int i = this.textSize;
        if (i != 0) {
            textPaint.setTextSize((float) i);
        }
        i = this.color;
        if (i != 0) {
            textPaint.setColor(i);
        }
        textPaint.setFlags(textPaint.getFlags() | 128);
    }
}
