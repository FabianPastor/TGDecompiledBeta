package org.telegram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;

public class TextStyleSpan extends MetricAffectingSpan {
    public static final int FLAG_STYLE_BOLD = 1;
    public static final int FLAG_STYLE_ITALIC = 2;
    public static final int FLAG_STYLE_MENTION = 64;
    public static final int FLAG_STYLE_MONO = 4;
    public static final int FLAG_STYLE_QUOTE = 32;
    public static final int FLAG_STYLE_STRIKE = 8;
    public static final int FLAG_STYLE_UNDERLINE = 16;
    public static final int FLAG_STYLE_URL = 128;
    private int color;
    private TextStyleRun style;
    private int textSize;

    public static class TextStyleRun {
        public int end;
        public int flags;
        public int start;
        public TLRPC.MessageEntity urlEntity;

        public TextStyleRun() {
        }

        public TextStyleRun(TextStyleRun textStyleRun) {
            this.flags = textStyleRun.flags;
            this.start = textStyleRun.start;
            this.end = textStyleRun.end;
            this.urlEntity = textStyleRun.urlEntity;
        }

        public void merge(TextStyleRun textStyleRun) {
            TLRPC.MessageEntity messageEntity;
            this.flags |= textStyleRun.flags;
            if (this.urlEntity == null && (messageEntity = textStyleRun.urlEntity) != null) {
                this.urlEntity = messageEntity;
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
            int i2 = this.flags;
            if ((i2 & 1) != 0) {
                return AndroidUtilities.getTypeface("fonts/rmedium.ttf");
            }
            if ((i2 & 2) != 0) {
                return AndroidUtilities.getTypeface("fonts/ritalic.ttf");
            }
            return null;
        }
    }

    public TextStyleSpan(TextStyleRun textStyleRun) {
        this(textStyleRun, 0, 0);
    }

    public TextStyleSpan(TextStyleRun textStyleRun, int i) {
        this(textStyleRun, i, 0);
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

    public Typeface getTypeface() {
        return this.style.getTypeface();
    }

    public void setColor(int i) {
        this.color = i;
    }

    public boolean isMono() {
        return this.style.getTypeface() == Typeface.MONOSPACE;
    }

    public boolean isBold() {
        return this.style.getTypeface() == AndroidUtilities.getTypeface("fonts/rmedium.ttf");
    }

    public boolean isItalic() {
        return this.style.getTypeface() == AndroidUtilities.getTypeface("fonts/ritalic.ttf");
    }

    public boolean isBoldItalic() {
        return this.style.getTypeface() == AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf");
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
