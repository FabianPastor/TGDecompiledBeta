package org.telegram.messenger.exoplayer2.text.webvtt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class WebvttParserUtil {
    private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
    private static final Pattern HEADER = Pattern.compile("^\ufeff?WEBVTT(( |\t).*)?$");

    private WebvttParserUtil() {
    }

    public static void validateWebvttHeaderLine(ParsableByteArray input) throws SubtitleDecoderException {
        String line = input.readLine();
        if (line != null) {
            if (HEADER.matcher(line).matches()) {
                return;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected WEBVTT. Got ");
        stringBuilder.append(line);
        throw new SubtitleDecoderException(stringBuilder.toString());
    }

    public static long parseTimestampUs(String timestamp) throws NumberFormatException {
        long value = 0;
        String[] parts = timestamp.split("\\.", 2);
        int i = 0;
        String[] subparts = parts[0].split(":");
        while (i < subparts.length) {
            value = (60 * value) + Long.parseLong(subparts[i]);
            i++;
        }
        value *= 1000;
        if (parts.length == 2) {
            value += Long.parseLong(parts[1]);
        }
        return 1000 * value;
    }

    public static float parsePercentage(String s) throws NumberFormatException {
        if (s.endsWith("%")) {
            return Float.parseFloat(s.substring(0, s.length() - 1)) / 100.0f;
        }
        throw new NumberFormatException("Percentages must end with %");
    }

    public static Matcher findNextCueHeader(ParsableByteArray input) {
        while (true) {
            String readLine = input.readLine();
            String line = readLine;
            if (readLine == null) {
                return null;
            }
            if (COMMENT.matcher(line).matches()) {
                while (true) {
                    readLine = input.readLine();
                    line = readLine;
                    if (readLine == null || line.isEmpty()) {
                        break;
                    }
                }
            } else {
                Matcher cueHeaderMatcher = WebvttCueParser.CUE_HEADER_PATTERN.matcher(line);
                if (cueHeaderMatcher.matches()) {
                    return cueHeaderMatcher;
                }
            }
        }
    }
}
