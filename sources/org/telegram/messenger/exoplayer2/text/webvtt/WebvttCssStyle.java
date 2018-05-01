package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.Layout.Alignment;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.util.Util;

public final class WebvttCssStyle {
    public static final int FONT_SIZE_UNIT_EM = 2;
    public static final int FONT_SIZE_UNIT_PERCENT = 3;
    public static final int FONT_SIZE_UNIT_PIXEL = 1;
    private static final int OFF = 0;
    private static final int ON = 1;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_BOLD_ITALIC = 3;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_NORMAL = 0;
    public static final int UNSPECIFIED = -1;
    private int backgroundColor;
    private int bold;
    private int fontColor;
    private String fontFamily;
    private float fontSize;
    private int fontSizeUnit;
    private boolean hasBackgroundColor;
    private boolean hasFontColor;
    private int italic;
    private int linethrough;
    private List<String> targetClasses;
    private String targetId;
    private String targetTag;
    private String targetVoice;
    private Alignment textAlign;
    private int underline;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FontSizeUnit {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface OptionalBoolean {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface StyleFlags {
    }

    public WebvttCssStyle() {
        reset();
    }

    public void reset() {
        this.targetId = TtmlNode.ANONYMOUS_REGION_ID;
        this.targetTag = TtmlNode.ANONYMOUS_REGION_ID;
        this.targetClasses = Collections.emptyList();
        this.targetVoice = TtmlNode.ANONYMOUS_REGION_ID;
        this.fontFamily = null;
        this.hasFontColor = false;
        this.hasBackgroundColor = false;
        this.linethrough = -1;
        this.underline = -1;
        this.bold = -1;
        this.italic = -1;
        this.fontSizeUnit = -1;
        this.textAlign = null;
    }

    public void setTargetId(String str) {
        this.targetId = str;
    }

    public void setTargetTagName(String str) {
        this.targetTag = str;
    }

    public void setTargetClasses(String[] strArr) {
        this.targetClasses = Arrays.asList(strArr);
    }

    public void setTargetVoice(String str) {
        this.targetVoice = str;
    }

    public int getSpecificityScore(String str, String str2, String[] strArr, String str3) {
        if (this.targetId.isEmpty() && this.targetTag.isEmpty() && this.targetClasses.isEmpty() && this.targetVoice.isEmpty()) {
            return str2.isEmpty();
        }
        str = updateScoreForMatch(updateScoreForMatch(updateScoreForMatch(0, this.targetId, str, NUM), this.targetTag, str2, 2), this.targetVoice, str3, 4);
        if (str != -1) {
            if (Arrays.asList(strArr).containsAll(this.targetClasses) != null) {
                return str + (this.targetClasses.size() * 4);
            }
        }
        return 0;
    }

    public int getStyle() {
        if (this.bold == -1 && this.italic == -1) {
            return -1;
        }
        int i = 0;
        int i2 = this.bold == 1 ? 1 : 0;
        if (this.italic == 1) {
            i = 2;
        }
        return i2 | i;
    }

    public boolean isLinethrough() {
        return this.linethrough == 1;
    }

    public WebvttCssStyle setLinethrough(boolean z) {
        this.linethrough = z;
        return this;
    }

    public boolean isUnderline() {
        return this.underline == 1;
    }

    public WebvttCssStyle setUnderline(boolean z) {
        this.underline = z;
        return this;
    }

    public WebvttCssStyle setBold(boolean z) {
        this.bold = z;
        return this;
    }

    public WebvttCssStyle setItalic(boolean z) {
        this.italic = z;
        return this;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public WebvttCssStyle setFontFamily(String str) {
        this.fontFamily = Util.toLowerInvariant(str);
        return this;
    }

    public int getFontColor() {
        if (this.hasFontColor) {
            return this.fontColor;
        }
        throw new IllegalStateException("Font color not defined");
    }

    public WebvttCssStyle setFontColor(int i) {
        this.fontColor = i;
        this.hasFontColor = true;
        return this;
    }

    public boolean hasFontColor() {
        return this.hasFontColor;
    }

    public int getBackgroundColor() {
        if (this.hasBackgroundColor) {
            return this.backgroundColor;
        }
        throw new IllegalStateException("Background color not defined.");
    }

    public WebvttCssStyle setBackgroundColor(int i) {
        this.backgroundColor = i;
        this.hasBackgroundColor = true;
        return this;
    }

    public boolean hasBackgroundColor() {
        return this.hasBackgroundColor;
    }

    public Alignment getTextAlign() {
        return this.textAlign;
    }

    public WebvttCssStyle setTextAlign(Alignment alignment) {
        this.textAlign = alignment;
        return this;
    }

    public WebvttCssStyle setFontSize(float f) {
        this.fontSize = f;
        return this;
    }

    public WebvttCssStyle setFontSizeUnit(short s) {
        this.fontSizeUnit = s;
        return this;
    }

    public int getFontSizeUnit() {
        return this.fontSizeUnit;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void cascadeFrom(WebvttCssStyle webvttCssStyle) {
        if (webvttCssStyle.hasFontColor) {
            setFontColor(webvttCssStyle.fontColor);
        }
        if (webvttCssStyle.bold != -1) {
            this.bold = webvttCssStyle.bold;
        }
        if (webvttCssStyle.italic != -1) {
            this.italic = webvttCssStyle.italic;
        }
        if (webvttCssStyle.fontFamily != null) {
            this.fontFamily = webvttCssStyle.fontFamily;
        }
        if (this.linethrough == -1) {
            this.linethrough = webvttCssStyle.linethrough;
        }
        if (this.underline == -1) {
            this.underline = webvttCssStyle.underline;
        }
        if (this.textAlign == null) {
            this.textAlign = webvttCssStyle.textAlign;
        }
        if (this.fontSizeUnit == -1) {
            this.fontSizeUnit = webvttCssStyle.fontSizeUnit;
            this.fontSize = webvttCssStyle.fontSize;
        }
        if (webvttCssStyle.hasBackgroundColor) {
            setBackgroundColor(webvttCssStyle.backgroundColor);
        }
    }

    private static int updateScoreForMatch(int i, String str, String str2, int i2) {
        if (!str.isEmpty()) {
            int i3 = -1;
            if (i != -1) {
                if (str.equals(str2) != null) {
                    i3 = i + i2;
                }
                return i3;
            }
        }
        return i;
    }
}
