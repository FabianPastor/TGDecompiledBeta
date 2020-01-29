package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;

public class TypefaceSpan extends MetricAffectingSpan {
    private int color;
    private int textSize;
    private Typeface typeface;

    public TypefaceSpan(Typeface typeface2) {
        this.typeface = typeface2;
    }

    public TypefaceSpan(Typeface typeface2, int i) {
        this.typeface = typeface2;
        this.textSize = i;
    }

    public TypefaceSpan(Typeface typeface2, int i, int i2) {
        this.typeface = typeface2;
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

    public void updateMeasureState(TextPaint textPaint) {
        Typeface typeface2 = this.typeface;
        if (typeface2 != null) {
            textPaint.setTypeface(typeface2);
        }
        int i = this.textSize;
        if (i != 0) {
            textPaint.setTextSize((float) i);
        }
        textPaint.setFlags(textPaint.getFlags() | 128);
    }

    public void updateDrawState(TextPaint textPaint) {
        Typeface typeface2 = this.typeface;
        if (typeface2 != null) {
            textPaint.setTypeface(typeface2);
        }
        int i = this.textSize;
        if (i != 0) {
            textPaint.setTextSize((float) i);
        }
        int i2 = this.color;
        if (i2 != 0) {
            textPaint.setColor(i2);
        }
        textPaint.setFlags(textPaint.getFlags() | 128);
    }
}
