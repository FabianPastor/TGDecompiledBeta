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

    public static void validateWebvttHeaderLine(ParsableByteArray parsableByteArray) throws SubtitleDecoderException {
        parsableByteArray = parsableByteArray.readLine();
        if (parsableByteArray != null) {
            if (HEADER.matcher(parsableByteArray).matches()) {
                return;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected WEBVTT. Got ");
        stringBuilder.append(parsableByteArray);
        throw new SubtitleDecoderException(stringBuilder.toString());
    }

    public static long parseTimestampUs(String str) throws NumberFormatException {
        str = str.split("\\.", 2);
        int i = 0;
        String[] split = str[0].split(":");
        long j = 0;
        while (i < split.length) {
            i++;
            j = (j * 60) + Long.parseLong(split[i]);
        }
        j *= 1000;
        if (str.length == 2) {
            j += Long.parseLong(str[1]);
        }
        return j * 1000;
    }

    public static float parsePercentage(String str) throws NumberFormatException {
        if (str.endsWith("%")) {
            return Float.parseFloat(str.substring(0, str.length() - 1)) / 100.0f;
        }
        throw new NumberFormatException("Percentages must end with %");
    }

    public static Matcher findNextCueHeader(ParsableByteArray parsableByteArray) {
        while (true) {
            CharSequence readLine = parsableByteArray.readLine();
            if (readLine == null) {
                return null;
            }
            if (COMMENT.matcher(readLine).matches()) {
                while (true) {
                    String readLine2 = parsableByteArray.readLine();
                    if (readLine2 == null || readLine2.isEmpty()) {
                        break;
                    }
                }
            } else {
                Matcher matcher = WebvttCueParser.CUE_HEADER_PATTERN.matcher(readLine);
                if (matcher.matches()) {
                    return matcher;
                }
            }
        }
    }
}
