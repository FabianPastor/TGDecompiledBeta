package org.telegram.messenger.exoplayer.hls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.ParserException;

final class HlsParserUtil {
    private static final String BOOLEAN_NO = "NO";
    private static final String BOOLEAN_YES = "YES";

    private HlsParserUtil() {
    }

    public static String parseStringAttr(String line, Pattern pattern, String tag) throws ParserException {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        throw new ParserException("Couldn't match " + tag + " tag in " + line);
    }

    public static int parseIntAttr(String line, Pattern pattern, String tag) throws ParserException {
        return Integer.parseInt(parseStringAttr(line, pattern, tag));
    }

    public static double parseDoubleAttr(String line, Pattern pattern, String tag) throws ParserException {
        return Double.parseDouble(parseStringAttr(line, pattern, tag));
    }

    public static String parseOptionalStringAttr(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean parseOptionalBooleanAttr(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return BOOLEAN_YES.equals(matcher.group(1));
        }
        return false;
    }

    public static Pattern compileBooleanAttrPattern(String attrName) {
        return Pattern.compile(attrName + "=(" + BOOLEAN_YES + "|" + BOOLEAN_NO + ")");
    }
}
