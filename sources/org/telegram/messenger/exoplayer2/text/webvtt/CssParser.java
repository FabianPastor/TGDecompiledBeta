package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.TextUtils;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.util.ColorParser;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class CssParser {
    private static final String BLOCK_END = "}";
    private static final String BLOCK_START = "{";
    private static final String PROPERTY_BGCOLOR = "background-color";
    private static final String PROPERTY_FONT_FAMILY = "font-family";
    private static final String PROPERTY_FONT_STYLE = "font-style";
    private static final String PROPERTY_FONT_WEIGHT = "font-weight";
    private static final String PROPERTY_TEXT_DECORATION = "text-decoration";
    private static final String VALUE_BOLD = "bold";
    private static final String VALUE_ITALIC = "italic";
    private static final String VALUE_UNDERLINE = "underline";
    private static final Pattern VOICE_NAME_PATTERN = Pattern.compile("\\[voice=\"([^\"]*)\"\\]");
    private final StringBuilder stringBuilder = new StringBuilder();
    private final ParsableByteArray styleInput = new ParsableByteArray();

    public WebvttCssStyle parseBlock(ParsableByteArray parsableByteArray) {
        this.stringBuilder.setLength(0);
        int position = parsableByteArray.getPosition();
        skipStyleBlock(parsableByteArray);
        this.styleInput.reset(parsableByteArray.data, parsableByteArray.getPosition());
        this.styleInput.setPosition(position);
        parsableByteArray = parseSelector(this.styleInput, this.stringBuilder);
        WebvttCssStyle webvttCssStyle = null;
        if (parsableByteArray != null) {
            if (BLOCK_START.equals(parseNextToken(this.styleInput, this.stringBuilder))) {
                WebvttCssStyle webvttCssStyle2 = new WebvttCssStyle();
                applySelectorToStyle(webvttCssStyle2, parsableByteArray);
                Object obj = null;
                parsableByteArray = null;
                while (parsableByteArray == null) {
                    int i;
                    parsableByteArray = this.styleInput.getPosition();
                    obj = parseNextToken(this.styleInput, this.stringBuilder);
                    if (obj != null) {
                        if (!BLOCK_END.equals(obj)) {
                            i = 0;
                            if (i == 0) {
                                this.styleInput.setPosition(parsableByteArray);
                                parseStyleDeclaration(this.styleInput, webvttCssStyle2, this.stringBuilder);
                            }
                            parsableByteArray = i;
                        }
                    }
                    i = 1;
                    if (i == 0) {
                        this.styleInput.setPosition(parsableByteArray);
                        parseStyleDeclaration(this.styleInput, webvttCssStyle2, this.stringBuilder);
                    }
                    parsableByteArray = i;
                }
                if (BLOCK_END.equals(obj) != null) {
                    webvttCssStyle = webvttCssStyle2;
                }
                return webvttCssStyle;
            }
        }
        return null;
    }

    private static String parseSelector(ParsableByteArray parsableByteArray, StringBuilder stringBuilder) {
        skipWhitespaceAndComments(parsableByteArray);
        if (parsableByteArray.bytesLeft() < 5) {
            return null;
        }
        if (!"::cue".equals(parsableByteArray.readString(5))) {
            return null;
        }
        int position = parsableByteArray.getPosition();
        String parseNextToken = parseNextToken(parsableByteArray, stringBuilder);
        if (parseNextToken == null) {
            return null;
        }
        if (BLOCK_START.equals(parseNextToken)) {
            parsableByteArray.setPosition(position);
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        String readCueTarget = "(".equals(parseNextToken) ? readCueTarget(parsableByteArray) : null;
        parsableByteArray = parseNextToken(parsableByteArray, stringBuilder);
        if (")".equals(parsableByteArray) != null) {
            if (parsableByteArray != null) {
                return readCueTarget;
            }
        }
        return null;
    }

    private static String readCueTarget(ParsableByteArray parsableByteArray) {
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        Object obj = null;
        while (position < limit && r3 == null) {
            int i = position + 1;
            obj = ((char) parsableByteArray.data[position]) == ')' ? 1 : null;
            position = i;
        }
        return parsableByteArray.readString((position - 1) - parsableByteArray.getPosition()).trim();
    }

    private static void parseStyleDeclaration(ParsableByteArray parsableByteArray, WebvttCssStyle webvttCssStyle, StringBuilder stringBuilder) {
        skipWhitespaceAndComments(parsableByteArray);
        String parseIdentifier = parseIdentifier(parsableByteArray, stringBuilder);
        if (!TtmlNode.ANONYMOUS_REGION_ID.equals(parseIdentifier) && ":".equals(parseNextToken(parsableByteArray, stringBuilder))) {
            skipWhitespaceAndComments(parsableByteArray);
            String parsePropertyValue = parsePropertyValue(parsableByteArray, stringBuilder);
            if (parsePropertyValue != null) {
                if (!TtmlNode.ANONYMOUS_REGION_ID.equals(parsePropertyValue)) {
                    int position = parsableByteArray.getPosition();
                    stringBuilder = parseNextToken(parsableByteArray, stringBuilder);
                    if (!";".equals(stringBuilder)) {
                        if (BLOCK_END.equals(stringBuilder) != null) {
                            parsableByteArray.setPosition(position);
                        } else {
                            return;
                        }
                    }
                    if (TtmlNode.ATTR_TTS_COLOR.equals(parseIdentifier) != null) {
                        webvttCssStyle.setFontColor(ColorParser.parseCssColor(parsePropertyValue));
                    } else if (PROPERTY_BGCOLOR.equals(parseIdentifier) != null) {
                        webvttCssStyle.setBackgroundColor(ColorParser.parseCssColor(parsePropertyValue));
                    } else if (PROPERTY_TEXT_DECORATION.equals(parseIdentifier) != null) {
                        if ("underline".equals(parsePropertyValue) != null) {
                            webvttCssStyle.setUnderline(true);
                        }
                    } else if (PROPERTY_FONT_FAMILY.equals(parseIdentifier) != null) {
                        webvttCssStyle.setFontFamily(parsePropertyValue);
                    } else if (PROPERTY_FONT_WEIGHT.equals(parseIdentifier) != null) {
                        if ("bold".equals(parsePropertyValue) != null) {
                            webvttCssStyle.setBold(true);
                        }
                    } else if (!(PROPERTY_FONT_STYLE.equals(parseIdentifier) == null || "italic".equals(parsePropertyValue) == null)) {
                        webvttCssStyle.setItalic(true);
                    }
                }
            }
        }
    }

    static void skipWhitespaceAndComments(ParsableByteArray parsableByteArray) {
        while (true) {
            Object obj = 1;
            while (parsableByteArray.bytesLeft() > 0 && r1 != null) {
                if (!maybeSkipWhitespace(parsableByteArray)) {
                    if (!maybeSkipComment(parsableByteArray)) {
                        obj = null;
                    }
                }
            }
            return;
        }
    }

    static String parseNextToken(ParsableByteArray parsableByteArray, StringBuilder stringBuilder) {
        skipWhitespaceAndComments(parsableByteArray);
        if (parsableByteArray.bytesLeft() == 0) {
            return null;
        }
        stringBuilder = parseIdentifier(parsableByteArray, stringBuilder);
        if (!TtmlNode.ANONYMOUS_REGION_ID.equals(stringBuilder)) {
            return stringBuilder;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append((char) parsableByteArray.readUnsignedByte());
        return stringBuilder.toString();
    }

    private static boolean maybeSkipWhitespace(ParsableByteArray parsableByteArray) {
        switch (peekCharAtPosition(parsableByteArray, parsableByteArray.getPosition())) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
                parsableByteArray.skipBytes(1);
                return true;
            default:
                return null;
        }
    }

    static void skipStyleBlock(ParsableByteArray parsableByteArray) {
        do {
        } while (!TextUtils.isEmpty(parsableByteArray.readLine()));
    }

    private static char peekCharAtPosition(ParsableByteArray parsableByteArray, int i) {
        return (char) parsableByteArray.data[i];
    }

    private static String parsePropertyValue(ParsableByteArray parsableByteArray, StringBuilder stringBuilder) {
        StringBuilder stringBuilder2 = new StringBuilder();
        Object obj = null;
        while (obj == null) {
            int position = parsableByteArray.getPosition();
            String parseNextToken = parseNextToken(parsableByteArray, stringBuilder);
            if (parseNextToken == null) {
                return null;
            }
            if (!BLOCK_END.equals(parseNextToken)) {
                if (!";".equals(parseNextToken)) {
                    stringBuilder2.append(parseNextToken);
                }
            }
            parsableByteArray.setPosition(position);
            obj = 1;
        }
        return stringBuilder2.toString();
    }

    private static boolean maybeSkipComment(ParsableByteArray parsableByteArray) {
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        byte[] bArr = parsableByteArray.data;
        if (position + 2 <= limit) {
            int i = position + 1;
            if (bArr[position] == (byte) 47) {
                position = i + 1;
                if (bArr[i] == (byte) 42) {
                    while (true) {
                        i = position + 1;
                        if (i >= limit) {
                            parsableByteArray.skipBytes(limit - parsableByteArray.getPosition());
                            return true;
                        } else if (((char) bArr[position]) == '*' && ((char) bArr[i]) == '/') {
                            position = i + 1;
                            limit = position;
                        } else {
                            position = i;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String parseIdentifier(ParsableByteArray parsableByteArray, StringBuilder stringBuilder) {
        int i = 0;
        stringBuilder.setLength(0);
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        while (position < limit && r0 == 0) {
            char c = (char) parsableByteArray.data[position];
            if ((c < 'A' || c > 'Z') && ((c < 'a' || c > 'z') && !((c >= '0' && c <= '9') || c == '#' || c == '-' || c == '.'))) {
                if (c != '_') {
                    i = 1;
                }
            }
            position++;
            stringBuilder.append(c);
        }
        parsableByteArray.skipBytes(position - parsableByteArray.getPosition());
        return stringBuilder.toString();
    }

    private void applySelectorToStyle(WebvttCssStyle webvttCssStyle, String str) {
        if (!TtmlNode.ANONYMOUS_REGION_ID.equals(str)) {
            int indexOf = str.indexOf(91);
            if (indexOf != -1) {
                Matcher matcher = VOICE_NAME_PATTERN.matcher(str.substring(indexOf));
                if (matcher.matches()) {
                    webvttCssStyle.setTargetVoice(matcher.group(1));
                }
                str = str.substring(0, indexOf);
            }
            str = str.split("\\.");
            String str2 = str[0];
            int indexOf2 = str2.indexOf(35);
            if (indexOf2 != -1) {
                webvttCssStyle.setTargetTagName(str2.substring(0, indexOf2));
                webvttCssStyle.setTargetId(str2.substring(indexOf2 + 1));
            } else {
                webvttCssStyle.setTargetTagName(str2);
            }
            if (str.length > 1) {
                webvttCssStyle.setTargetClasses((String[]) Arrays.copyOfRange(str, 1, str.length));
            }
        }
    }
}
