package org.telegram.messenger.utils;

import android.text.Spanned;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.TextStyleSpan;

public class CustomHtml {
    public static String toHtml(Spanned spanned) {
        StringBuilder sb = new StringBuilder();
        sb.append("<root>");
        wrapTextStyle(sb, spanned, 0, spanned.length());
        sb.append("</root>");
        return sb.toString();
    }

    private static void withinStyle(StringBuilder sb, CharSequence charSequence, int i, int i2) {
        int i3;
        char charAt;
        while (i < i2) {
            char charAt2 = charSequence.charAt(i);
            if (charAt2 == '<') {
                sb.append("&lt;");
            } else if (charAt2 == '>') {
                sb.append("&gt;");
            } else if (charAt2 == '&') {
                sb.append("&amp;");
            } else if (charAt2 < 55296 || charAt2 > 57343) {
                if (charAt2 > '~' || charAt2 < ' ') {
                    sb.append("&#");
                    sb.append(charAt2);
                    sb.append(";");
                } else if (charAt2 == ' ') {
                    while (true) {
                        int i4 = i + 1;
                        if (i4 >= i2 || charSequence.charAt(i4) != ' ') {
                            sb.append(' ');
                        } else {
                            sb.append("&nbsp;");
                            i = i4;
                        }
                    }
                    sb.append(' ');
                } else {
                    sb.append(charAt2);
                }
            } else if (charAt2 < 56320 && (i3 = i + 1) < i2 && (charAt = charSequence.charAt(i3)) >= 56320 && charAt <= 57343) {
                sb.append("&#");
                sb.append(65536 | ((charAt2 - 55296) << 10) | (charAt - 56320));
                sb.append(";");
                i = i3;
            }
            i++;
        }
    }

    private static void wrapTextStyle(StringBuilder sb, Spanned spanned, int i, int i2) {
        while (i < i2) {
            int nextSpanTransition = spanned.nextSpanTransition(i, i2, TextStyleSpan.class);
            if (nextSpanTransition < 0) {
                nextSpanTransition = i2;
            }
            TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) spanned.getSpans(i, nextSpanTransition, TextStyleSpan.class);
            if (textStyleSpanArr != null) {
                for (TextStyleSpan textStyleSpan : textStyleSpanArr) {
                    if (textStyleSpan != null) {
                        int styleFlags = textStyleSpan.getStyleFlags();
                        if ((styleFlags & 768) > 0) {
                            sb.append("<spoiler>");
                        }
                        if ((styleFlags & 4) > 0) {
                            sb.append("<pre>");
                        }
                        if ((styleFlags & 1) > 0) {
                            sb.append("<b>");
                        }
                        if ((styleFlags & 2) > 0) {
                            sb.append("<i>");
                        }
                        if ((styleFlags & 16) > 0) {
                            sb.append("<u>");
                        }
                        if ((styleFlags & 8) > 0) {
                            sb.append("<s>");
                        }
                        if (!((styleFlags & 128) <= 0 || textStyleSpan.getTextStyleRun() == null || textStyleSpan.getTextStyleRun().urlEntity == null)) {
                            sb.append("<a href=\"");
                            sb.append(textStyleSpan.getTextStyleRun().urlEntity.url);
                            sb.append("\">");
                        }
                    }
                }
            }
            toHTML_4_wrapAnimatedEmoji(sb, spanned, i, nextSpanTransition);
            if (textStyleSpanArr != null) {
                for (TextStyleSpan textStyleSpan2 : textStyleSpanArr) {
                    if (textStyleSpan2 != null) {
                        int styleFlags2 = textStyleSpan2.getStyleFlags();
                        if (!((styleFlags2 & 128) <= 0 || textStyleSpan2.getTextStyleRun() == null || textStyleSpan2.getTextStyleRun().urlEntity == null)) {
                            sb.append("</a>");
                        }
                        if ((styleFlags2 & 8) > 0) {
                            sb.append("</s>");
                        }
                        if ((styleFlags2 & 16) > 0) {
                            sb.append("</u>");
                        }
                        if ((styleFlags2 & 2) > 0) {
                            sb.append("</i>");
                        }
                        if ((styleFlags2 & 1) > 0) {
                            sb.append("</b>");
                        }
                        if ((styleFlags2 & 4) > 0) {
                            sb.append("</pre>");
                        }
                        if ((styleFlags2 & 768) > 0) {
                            sb.append("</spoiler>");
                        }
                    }
                }
            }
            i = nextSpanTransition;
        }
    }

    private static void toHTML_4_wrapAnimatedEmoji(StringBuilder sb, Spanned spanned, int i, int i2) {
        while (i < i2) {
            int nextSpanTransition = spanned.nextSpanTransition(i, i2, AnimatedEmojiSpan.class);
            if (nextSpanTransition < 0) {
                nextSpanTransition = i2;
            }
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spanned.getSpans(i, nextSpanTransition, AnimatedEmojiSpan.class);
            if (animatedEmojiSpanArr != null) {
                for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                    if (animatedEmojiSpan != null) {
                        sb.append("<animated_emoji_documentId=\"" + animatedEmojiSpan.documentId + "\">");
                    }
                }
            }
            withinStyle(sb, spanned, i, nextSpanTransition);
            if (animatedEmojiSpanArr != null) {
                for (AnimatedEmojiSpan animatedEmojiSpan2 : animatedEmojiSpanArr) {
                    if (animatedEmojiSpan2 != null) {
                        sb.append("</animated_emoji_documentId=\"" + animatedEmojiSpan2.documentId + "\">");
                    }
                }
            }
            i = nextSpanTransition;
        }
    }
}
