package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC$MessageEntity;

public class TextStyleSpan extends MetricAffectingSpan {
    private int color;
    private TextStyleRun style;
    private int textSize;

    public static class TextStyleRun {
        public int end;
        public int flags;
        public int start;
        public TLRPC$MessageEntity urlEntity;

        public TextStyleRun() {
        }

        public TextStyleRun(TextStyleRun textStyleRun) {
            this.flags = textStyleRun.flags;
            this.start = textStyleRun.start;
            this.end = textStyleRun.end;
            this.urlEntity = textStyleRun.urlEntity;
        }

        public void merge(TextStyleRun textStyleRun) {
            TLRPC$MessageEntity tLRPC$MessageEntity;
            this.flags |= textStyleRun.flags;
            if (this.urlEntity == null && (tLRPC$MessageEntity = textStyleRun.urlEntity) != null) {
                this.urlEntity = tLRPC$MessageEntity;
            }
        }

        public void replace(TextStyleRun textStyleRun) {
            this.flags = textStyleRun.flags;
            this.urlEntity = textStyleRun.urlEntity;
        }

        public void applyStyle(TextPaint textPaint) {
            Typeface typeface = getTypeface();
            if (typeface != null) {
                textPaint.setTypeface(typeface);
            }
            if ((this.flags & 16) != 0) {
                textPaint.setFlags(textPaint.getFlags() | 8);
            } else {
                textPaint.setFlags(textPaint.getFlags() & -9);
            }
            if ((this.flags & 8) != 0) {
                textPaint.setFlags(textPaint.getFlags() | 16);
            } else {
                textPaint.setFlags(textPaint.getFlags() & -17);
            }
        }

        public Typeface getTypeface() {
            int i = this.flags;
            if ((i & 4) != 0 || (i & 32) != 0) {
                return Typeface.MONOSPACE;
            }
            if ((i & 1) != 0 && (i & 2) != 0) {
                return AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
            }
            if ((i & 1) != 0) {
                return AndroidUtilities.getTypeface("fonts/rmedium.ttf");
            }
            if ((i & 2) != 0) {
                return AndroidUtilities.getTypeface("fonts/ritalic.ttf");
            }
            return null;
        }
    }

    public TextStyleSpan(TextStyleRun textStyleRun) {
        this(textStyleRun, 0, 0);
    }

    public TextStyleSpan(TextStyleRun textStyleRun, int i, int i2) {
        this.style = textStyleRun;
        if (i > 0) {
            this.textSize = i;
        }
        this.color = i2;
    }

    public int getStyleFlags() {
        return this.style.flags;
    }

    public TextStyleRun getTextStyleRun() {
        return this.style;
    }

    public void updateMeasureState(TextPaint textPaint) {
        int i = this.textSize;
        if (i != 0) {
            textPaint.setTextSize((float) i);
        }
        textPaint.setFlags(textPaint.getFlags() | 128);
        this.style.applyStyle(textPaint);
    }

    public void updateDrawState(TextPaint textPaint) {
        int i = this.textSize;
        if (i != 0) {
            textPaint.setTextSize((float) i);
        }
        int i2 = this.color;
        if (i2 != 0) {
            textPaint.setColor(i2);
        }
        textPaint.setFlags(textPaint.getFlags() | 128);
        this.style.applyStyle(textPaint);
    }
}
