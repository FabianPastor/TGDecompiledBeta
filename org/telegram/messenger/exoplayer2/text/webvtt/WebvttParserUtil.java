package org.telegram.messenger.exoplayer2.text.webvtt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class WebvttParserUtil {
    private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
    private static final Pattern HEADER = Pattern.compile("^﻿?WEBVTT(( |\t).*)?$");

    private WebvttParserUtil() {
    }

    public static void validateWebvttHeaderLine(ParsableByteArray input) throws SubtitleDecoderException {
        String line = input.readLine();
        if (line == null || !HEADER.matcher(line).matches()) {
            throw new SubtitleDecoderException("Expected WEBVTT. Got " + line);
        }
    }

    public static long parseTimestampUs(String timestamp) throws NumberFormatException {
        long value = 0;
        String[] parts = timestamp.split("\\.", 2);
        for (String subpart : parts[0].split(":")) {
            value = (60 * value) + Long.parseLong(subpart);
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
                Matcher cueHeaderMatcher = WebvttCueParser.CUE_HEADER_PATTERN.matcher(line);
                if (cueHeaderMatcher.matches()) {
                    return cueHeaderMatcher;
                }
            }
        }
    }
}
