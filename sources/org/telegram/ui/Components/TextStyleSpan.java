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

        public TextStyleRun(TextStyleRun run) {
            this.flags = run.flags;
            this.start = run.start;
            this.end = run.end;
            this.urlEntity = run.urlEntity;
        }

        public void merge(TextStyleRun run) {
            TLRPC.MessageEntity messageEntity;
            this.flags |= run.flags;
            if (this.urlEntity == null && (messageEntity = run.urlEntity) != null) {
                this.urlEntity = messageEntity;
            }
        }

        public void replace(TextStyleRun run) {
            this.flags = run.flags;
            this.urlEntity = run.urlEntity;
        }

        public void applyStyle(TextPaint p) {
            Typeface typeface = getTypeface();
            if (typeface != null) {
                p.setTypeface(typeface);
            }
            if ((this.flags & 16) != 0) {
                p.setFlags(p.getFlags() | 8);
            } else {
                p.setFlags(p.getFlags() & -9);
            }
            if ((this.flags & 8) != 0) {
                p.setFlags(p.getFlags() | 16);
            } else {
                p.setFlags(p.getFlags() & -17);
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

    public TextStyleSpan(TextStyleRun run) {
        this(run, 0, 0);
    }

    public TextStyleSpan(TextStyleRun run, int size) {
        this(run, size, 0);
    }

    public TextStyleSpan(TextStyleRun run, int size, int textColor) {
        this.style = run;
        if (size > 0) {
            this.textSize = size;
        }
        this.color = textColor;
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

    public void setColor(int value) {
        this.color = value;
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

    public void updateMeasureState(TextPaint p) {
        int i = this.textSize;
        if (i != 0) {
            p.setTextSize((float) i);
        }
        p.setFlags(p.getFlags() | 128);
        this.style.applyStyle(p);
    }

    public void updateDrawState(TextPaint p) {
        int i = this.textSize;
        if (i != 0) {
            p.setTextSize((float) i);
        }
        int i2 = this.color;
        if (i2 != 0) {
            p.setColor(i2);
        }
        p.setFlags(p.getFlags() | 128);
        this.style.applyStyle(p);
    }
}
