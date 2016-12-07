package org.telegram.messenger.exoplayer.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GaplessInfo {
    private static final String GAPLESS_COMMENT_ID = "iTunSMPB";
    private static final Pattern GAPLESS_COMMENT_PATTERN = Pattern.compile("^ [0-9a-fA-F]{8} ([0-9a-fA-F]{8}) ([0-9a-fA-F]{8})");
    public final int encoderDelay;
    public final int encoderPadding;

    public static GaplessInfo createFromComment(String name, String data) {
        if (!GAPLESS_COMMENT_ID.equals(name)) {
            return null;
        }
        Matcher matcher = GAPLESS_COMMENT_PATTERN.matcher(data);
        if (!matcher.find()) {
            return null;
        }
        try {
            int encoderDelay = Integer.parseInt(matcher.group(1), 16);
            int encoderPadding = Integer.parseInt(matcher.group(2), 16);
            if (encoderDelay == 0 && encoderPadding == 0) {
                return null;
            }
            return new GaplessInfo(encoderDelay, encoderPadding);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static GaplessInfo createFromXingHeaderValue(int value) {
        int encoderDelay = value >> 12;
        int encoderPadding = value & 4095;
        return (encoderDelay == 0 && encoderPadding == 0) ? null : new GaplessInfo(encoderDelay, encoderPadding);
    }

    private GaplessInfo(int encoderDelay, int encoderPadding) {
        this.encoderDelay = encoderDelay;
        this.encoderPadding = encoderPadding;
    }
}
