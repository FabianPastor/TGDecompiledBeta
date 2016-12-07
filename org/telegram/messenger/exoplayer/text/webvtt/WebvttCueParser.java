package org.telegram.messenger.exoplayer.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.text.webvtt.WebvttCue.Builder;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class WebvttCueParser {
    private static final char CHAR_AMPERSAND = '&';
    private static final char CHAR_GREATER_THAN = '>';
    private static final char CHAR_LESS_THAN = '<';
    private static final char CHAR_SEMI_COLON = ';';
    private static final char CHAR_SLASH = '/';
    private static final char CHAR_SPACE = ' ';
    private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
    public static final Pattern CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
    private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
    private static final String ENTITY_AMPERSAND = "amp";
    private static final String ENTITY_GREATER_THAN = "gt";
    private static final String ENTITY_LESS_THAN = "lt";
    private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
    private static final String SPACE = " ";
    private static final int STYLE_BOLD = 1;
    private static final int STYLE_ITALIC = 2;
    private static final String TAG = "WebvttCueParser";
    private static final String TAG_BOLD = "b";
    private static final String TAG_CLASS = "c";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_LANG = "lang";
    private static final String TAG_UNDERLINE = "u";
    private static final String TAG_VOICE = "v";
    private final StringBuilder textBuilder = new StringBuilder();

    private static final class StartTag {
        public final String name;
        public final int position;

        public StartTag(String name, int position) {
            this.position = position;
            this.name = name;
        }
    }

    boolean parseNextValidCue(ParsableByteArray webvttData, Builder builder) {
        Matcher cueHeaderMatcher;
        do {
            cueHeaderMatcher = findNextCueHeader(webvttData);
            if (cueHeaderMatcher == null) {
                return false;
            }
        } while (!parseCue(cueHeaderMatcher, webvttData, builder, this.textBuilder));
        return true;
    }

    static void parseCueSettingsList(String cueSettingsList, Builder builder) {
        Matcher cueSettingMatcher = CUE_SETTING_PATTERN.matcher(cueSettingsList);
        while (cueSettingMatcher.find()) {
            String name = cueSettingMatcher.group(1);
            String value = cueSettingMatcher.group(2);
            try {
                if ("line".equals(name)) {
                    parseLineAttribute(value, builder);
                } else if ("align".equals(name)) {
                    builder.setTextAlignment(parseTextAlignment(value));
                } else if ("position".equals(name)) {
                    parsePositionAttribute(value, builder);
                } else if ("size".equals(name)) {
                    builder.setWidth(WebvttParserUtil.parsePercentage(value));
                } else {
                    Log.w(TAG, "Unknown cue setting " + name + ":" + value);
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Skipping bad cue setting: " + cueSettingMatcher.group());
            }
        }
    }

    public static Matcher findNextCueHeader(ParsableByteArray input) {
        while (true) {
            String line = input.readLine();
            if (line == null) {
                return null;
            }
            if (COMMENT.matcher(line).matches()) {
                while (true) {
                    line = input.readLine();
                    if (line == null || line.isEmpty()) {
                        break;
                    }
                }
            } else {
                Matcher cueHeaderMatcher = CUE_HEADER_PATTERN.matcher(line);
                if (cueHeaderMatcher.matches()) {
                    return cueHeaderMatcher;
                }
            }
        }
    }

    static void parseCueText(String markup, Builder builder) {
        SpannableStringBuilder spannedText = new SpannableStringBuilder();
        Stack<StartTag> startTagStack = new Stack();
        int pos = 0;
        while (pos < markup.length()) {
            char curr = markup.charAt(pos);
            switch (curr) {
                case '&':
                    int semiColonEnd = markup.indexOf(59, pos + 1);
                    int spaceEnd = markup.indexOf(32, pos + 1);
                    int entityEnd = semiColonEnd == -1 ? spaceEnd : spaceEnd == -1 ? semiColonEnd : Math.min(semiColonEnd, spaceEnd);
                    if (entityEnd == -1) {
                        spannedText.append(curr);
                        pos++;
                        break;
                    }
                    applyEntity(markup.substring(pos + 1, entityEnd), spannedText);
                    if (entityEnd == spaceEnd) {
                        spannedText.append(SPACE);
                    }
                    pos = entityEnd + 1;
                    break;
                case '<':
                    if (pos + 1 < markup.length()) {
                        int i;
                        int ltPos = pos;
                        boolean isClosingTag = markup.charAt(ltPos + 1) == CHAR_SLASH;
                        pos = findEndOfTag(markup, ltPos + 1);
                        boolean isVoidTag = markup.charAt(pos + -2) == CHAR_SLASH;
                        int i2 = ltPos + (isClosingTag ? 2 : 1);
                        if (isVoidTag) {
                            i = pos - 2;
                        } else {
                            i = pos - 1;
                        }
                        String[] tagTokens = tokenizeTag(markup.substring(i2, i));
                        if (tagTokens != null && isSupportedTag(tagTokens[0])) {
                            if (!isClosingTag) {
                                if (!isVoidTag) {
                                    startTagStack.push(new StartTag(tagTokens[0], spannedText.length()));
                                    break;
                                }
                                break;
                            }
                            while (!startTagStack.isEmpty()) {
                                StartTag startTag = (StartTag) startTagStack.pop();
                                applySpansForTag(startTag, spannedText);
                                if (startTag.name.equals(tagTokens[0])) {
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    pos++;
                    break;
                default:
                    spannedText.append(curr);
                    pos++;
                    break;
            }
        }
        while (!startTagStack.isEmpty()) {
            applySpansForTag((StartTag) startTagStack.pop(), spannedText);
        }
        builder.setText(spannedText);
    }

    private static boolean parseCue(Matcher cueHeaderMatcher, ParsableByteArray webvttData, Builder builder, StringBuilder textBuilder) {
        try {
            builder.setStartTime(WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(1))).setEndTime(WebvttParserUtil.parseTimestampUs(cueHeaderMatcher.group(2)));
            parseCueSettingsList(cueHeaderMatcher.group(3), builder);
            textBuilder.setLength(0);
            while (true) {
                String line = webvttData.readLine();
                if (line == null || line.isEmpty()) {
                    parseCueText(textBuilder.toString(), builder);
                } else {
                    if (textBuilder.length() > 0) {
                        textBuilder.append("\n");
                    }
                    textBuilder.append(line.trim());
                }
            }
            parseCueText(textBuilder.toString(), builder);
            return true;
        } catch (NumberFormatException e) {
            Log.w(TAG, "Skipping cue with bad header: " + cueHeaderMatcher.group());
            return false;
        }
    }

    private static void parseLineAttribute(String s, Builder builder) throws NumberFormatException {
        int commaPosition = s.indexOf(44);
        if (commaPosition != -1) {
            builder.setLineAnchor(parsePositionAnchor(s.substring(commaPosition + 1)));
            s = s.substring(0, commaPosition);
        } else {
            builder.setLineAnchor(Integer.MIN_VALUE);
        }
        if (s.endsWith("%")) {
            builder.setLine(WebvttParserUtil.parsePercentage(s)).setLineType(0);
        } else {
            builder.setLine((float) Integer.parseInt(s)).setLineType(1);
        }
    }

    private static void parsePositionAttribute(String s, Builder builder) throws NumberFormatException {
        int commaPosition = s.indexOf(44);
        if (commaPosition != -1) {
            builder.setPositionAnchor(parsePositionAnchor(s.substring(commaPosition + 1)));
            s = s.substring(0, commaPosition);
        } else {
            builder.setPositionAnchor(Integer.MIN_VALUE);
        }
        builder.setPosition(WebvttParserUtil.parsePercentage(s));
    }

    private static int parsePositionAnchor(String s) {
        int i = -1;
        switch (s.hashCode()) {
            case -1364013995:
                if (s.equals(TtmlNode.CENTER)) {
                    i = 1;
                    break;
                }
                break;
            case -1074341483:
                if (s.equals("middle")) {
                    i = 2;
                    break;
                }
                break;
            case 100571:
                if (s.equals(TtmlNode.END)) {
                    i = 3;
                    break;
                }
                break;
            case 109757538:
                if (s.equals(TtmlNode.START)) {
                    i = 0;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
                return 0;
            case 1:
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                Log.w(TAG, "Invalid anchor value: " + s);
                return Integer.MIN_VALUE;
        }
    }

    private static Alignment parseTextAlignment(String s) {
        Object obj = -1;
        switch (s.hashCode()) {
            case -1364013995:
                if (s.equals(TtmlNode.CENTER)) {
                    obj = 2;
                    break;
                }
                break;
            case -1074341483:
                if (s.equals("middle")) {
                    obj = 3;
                    break;
                }
                break;
            case 100571:
                if (s.equals(TtmlNode.END)) {
                    obj = 4;
                    break;
                }
                break;
            case 3317767:
                if (s.equals(TtmlNode.LEFT)) {
                    obj = 1;
                    break;
                }
                break;
            case 108511772:
                if (s.equals(TtmlNode.RIGHT)) {
                    obj = 5;
                    break;
                }
                break;
            case 109757538:
                if (s.equals(TtmlNode.START)) {
                    obj = null;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
            case 1:
                return Alignment.ALIGN_NORMAL;
            case 2:
            case 3:
                return Alignment.ALIGN_CENTER;
            case 4:
            case 5:
                return Alignment.ALIGN_OPPOSITE;
            default:
                Log.w(TAG, "Invalid alignment value: " + s);
                return null;
        }
    }

    private static int findEndOfTag(String markup, int startPos) {
        int idx = markup.indexOf(62, startPos);
        return idx == -1 ? markup.length() : idx + 1;
    }

    private static void applyEntity(String entity, SpannableStringBuilder spannedText) {
        Object obj = -1;
        switch (entity.hashCode()) {
            case 3309:
                if (entity.equals(ENTITY_GREATER_THAN)) {
                    obj = 1;
                    break;
                }
                break;
            case 3464:
                if (entity.equals(ENTITY_LESS_THAN)) {
                    obj = null;
                    break;
                }
                break;
            case 96708:
                if (entity.equals(ENTITY_AMPERSAND)) {
                    obj = 3;
                    break;
                }
                break;
            case 3374865:
                if (entity.equals(ENTITY_NON_BREAK_SPACE)) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                spannedText.append(CHAR_LESS_THAN);
                return;
            case 1:
                spannedText.append(CHAR_GREATER_THAN);
                return;
            case 2:
                spannedText.append(CHAR_SPACE);
                return;
            case 3:
                spannedText.append(CHAR_AMPERSAND);
                return;
            default:
                Log.w(TAG, "ignoring unsupported entity: '&" + entity + ";'");
                return;
        }
    }

    private static boolean isSupportedTag(String tagName) {
        boolean z = true;
        switch (tagName.hashCode()) {
            case 98:
                if (tagName.equals(TAG_BOLD)) {
                    z = false;
                    break;
                }
                break;
            case 99:
                if (tagName.equals(TAG_CLASS)) {
                    z = true;
                    break;
                }
                break;
            case 105:
                if (tagName.equals(TAG_ITALIC)) {
                    z = true;
                    break;
                }
                break;
            case 117:
                if (tagName.equals(TAG_UNDERLINE)) {
                    z = true;
                    break;
                }
                break;
            case 118:
                if (tagName.equals(TAG_VOICE)) {
                    z = true;
                    break;
                }
                break;
            case 3314158:
                if (tagName.equals(TAG_LANG)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
            case true:
            case true:
            case true:
            case true:
            case true:
                return true;
            default:
                return false;
        }
    }

    private static void applySpansForTag(StartTag startTag, SpannableStringBuilder spannedText) {
        String str = startTag.name;
        int i = -1;
        switch (str.hashCode()) {
            case 98:
                if (str.equals(TAG_BOLD)) {
                    i = 0;
                    break;
                }
                break;
            case 105:
                if (str.equals(TAG_ITALIC)) {
                    i = 1;
                    break;
                }
                break;
            case 117:
                if (str.equals(TAG_UNDERLINE)) {
                    i = 2;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
                spannedText.setSpan(new StyleSpan(1), startTag.position, spannedText.length(), 33);
                return;
            case 1:
                spannedText.setSpan(new StyleSpan(2), startTag.position, spannedText.length(), 33);
                return;
            case 2:
                spannedText.setSpan(new UnderlineSpan(), startTag.position, spannedText.length(), 33);
                return;
            default:
                return;
        }
    }

    private static String[] tokenizeTag(String fullTagExpression) {
        fullTagExpression = fullTagExpression.replace("\\s+", SPACE).trim();
        if (fullTagExpression.length() == 0) {
            return null;
        }
        if (fullTagExpression.contains(SPACE)) {
            fullTagExpression = fullTagExpression.substring(0, fullTagExpression.indexOf(SPACE));
        }
        return fullTagExpression.split("\\.");
    }
}
