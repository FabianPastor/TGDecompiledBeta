package org.telegram.messenger.exoplayer2.text.ttml;

import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import java.util.Map;

final class TtmlRenderUtil {
    public static TtmlStyle resolveStyle(TtmlStyle style, String[] styleIds, Map<String, TtmlStyle> globalStyles) {
        if (style == null && styleIds == null) {
            return null;
        }
        if (style == null && styleIds.length == 1) {
            return (TtmlStyle) globalStyles.get(styleIds[0]);
        }
        if (style == null && styleIds.length > 1) {
            TtmlStyle chainedStyle = new TtmlStyle();
            for (String id : styleIds) {
                chainedStyle.chain((TtmlStyle) globalStyles.get(id));
            }
            return chainedStyle;
        } else if (style != null && styleIds != null && styleIds.length == 1) {
            return style.chain((TtmlStyle) globalStyles.get(styleIds[0]));
        } else {
            if (style == null || styleIds == null || styleIds.length <= 1) {
                return style;
            }
            for (String id2 : styleIds) {
                style.chain((TtmlStyle) globalStyles.get(id2));
            }
            return style;
        }
    }

    public static void applyStylesToSpan(SpannableStringBuilder builder, int start, int end, TtmlStyle style) {
        if (style.getStyle() != -1) {
            builder.setSpan(new StyleSpan(style.getStyle()), start, end, 33);
        }
        if (style.isLinethrough()) {
            builder.setSpan(new StrikethroughSpan(), start, end, 33);
        }
        if (style.isUnderline()) {
            builder.setSpan(new UnderlineSpan(), start, end, 33);
        }
        if (style.hasFontColor()) {
            builder.setSpan(new ForegroundColorSpan(style.getFontColor()), start, end, 33);
        }
        if (style.hasBackgroundColor()) {
            builder.setSpan(new BackgroundColorSpan(style.getBackgroundColor()), start, end, 33);
        }
        if (style.getFontFamily() != null) {
            builder.setSpan(new TypefaceSpan(style.getFontFamily()), start, end, 33);
        }
        if (style.getTextAlign() != null) {
            builder.setSpan(new Standard(style.getTextAlign()), start, end, 33);
        }
        switch (style.getFontSizeUnit()) {
            case 1:
                builder.setSpan(new AbsoluteSizeSpan((int) style.getFontSize(), true), start, end, 33);
                return;
            case 2:
                builder.setSpan(new RelativeSizeSpan(style.getFontSize()), start, end, 33);
                return;
            case 3:
                builder.setSpan(new RelativeSizeSpan(style.getFontSize() / 100.0f), start, end, 33);
                return;
            default:
                return;
        }
    }

    static void endParagraph(SpannableStringBuilder builder) {
        int position = builder.length() - 1;
        while (position >= 0 && builder.charAt(position) == ' ') {
            position--;
        }
        if (position >= 0 && builder.charAt(position) != '\n') {
            builder.append('\n');
        }
    }

    static String applyTextElementSpacePolicy(String in) {
        return in.replaceAll("\r\n", "\n").replaceAll(" *\n *", "\n").replaceAll("\n", " ").replaceAll("[ \t\\x0B\f\r]+", " ");
    }

    private TtmlRenderUtil() {
    }
}
