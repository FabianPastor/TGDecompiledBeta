package org.telegram.messenger.exoplayer.text.ttml;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

final class TtmlColorParser {
    static final int AQUA = 16777215;
    static final int BLACK = -16777216;
    static final int BLUE = -16776961;
    private static final Map<String, Integer> COLOR_NAME_MAP = new HashMap();
    static final int CYAN = -16711681;
    static final int FUCHSIA = -65281;
    static final int GRAY = -8355712;
    static final int GREEN = -16744448;
    static final int LIME = -16711936;
    static final int MAGENTA = -65281;
    static final int MAROON = -8388608;
    static final int NAVY = -16777088;
    static final int OLIVE = -8355840;
    static final int PURPLE = -8388480;
    static final int RED = -65536;
    private static final String RGB = "rgb";
    private static final String RGBA = "rgba";
    private static final Pattern RGBA_PATTERN = Pattern.compile("^rgba\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)$");
    private static final Pattern RGB_PATTERN = Pattern.compile("^rgb\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)$");
    static final int SILVER = -4144960;
    static final int TEAL = -16744320;
    static final int TRANSPARENT = 0;
    static final int WHITE = -1;
    static final int YELLOW = -256;

    TtmlColorParser() {
    }

    static {
        COLOR_NAME_MAP.put("transparent", Integer.valueOf(0));
        COLOR_NAME_MAP.put("black", Integer.valueOf(-16777216));
        COLOR_NAME_MAP.put("silver", Integer.valueOf(SILVER));
        COLOR_NAME_MAP.put("gray", Integer.valueOf(GRAY));
        COLOR_NAME_MAP.put("white", Integer.valueOf(-1));
        COLOR_NAME_MAP.put("maroon", Integer.valueOf(MAROON));
        COLOR_NAME_MAP.put("red", Integer.valueOf(-65536));
        COLOR_NAME_MAP.put("purple", Integer.valueOf(PURPLE));
        COLOR_NAME_MAP.put("fuchsia", Integer.valueOf(-65281));
        COLOR_NAME_MAP.put("magenta", Integer.valueOf(-65281));
        COLOR_NAME_MAP.put("green", Integer.valueOf(GREEN));
        COLOR_NAME_MAP.put("lime", Integer.valueOf(LIME));
        COLOR_NAME_MAP.put("olive", Integer.valueOf(OLIVE));
        COLOR_NAME_MAP.put("yellow", Integer.valueOf(-256));
        COLOR_NAME_MAP.put("navy", Integer.valueOf(NAVY));
        COLOR_NAME_MAP.put("blue", Integer.valueOf(BLUE));
        COLOR_NAME_MAP.put("teal", Integer.valueOf(TEAL));
        COLOR_NAME_MAP.put("aqua", Integer.valueOf(16777215));
        COLOR_NAME_MAP.put("cyan", Integer.valueOf(CYAN));
    }

    public static int parseColor(String colorExpression) {
        Assertions.checkArgument(!TextUtils.isEmpty(colorExpression));
        colorExpression = colorExpression.replace(" ", "");
        if (colorExpression.charAt(0) == '#') {
            int color = (int) Long.parseLong(colorExpression.substring(1), 16);
            if (colorExpression.length() == 7) {
                return color | -16777216;
            }
            if (colorExpression.length() == 9) {
                return ((color & 255) << 24) | (color >>> 8);
            }
            throw new IllegalArgumentException();
        }
        Matcher matcher;
        if (colorExpression.startsWith(RGBA)) {
            matcher = RGBA_PATTERN.matcher(colorExpression);
            if (matcher.matches()) {
                return argb(255 - Integer.parseInt(matcher.group(4), 10), Integer.parseInt(matcher.group(1), 10), Integer.parseInt(matcher.group(2), 10), Integer.parseInt(matcher.group(3), 10));
            }
        } else if (colorExpression.startsWith(RGB)) {
            matcher = RGB_PATTERN.matcher(colorExpression);
            if (matcher.matches()) {
                return rgb(Integer.parseInt(matcher.group(1), 10), Integer.parseInt(matcher.group(2), 10), Integer.parseInt(matcher.group(3), 10));
            }
        } else {
            Integer color2 = (Integer) COLOR_NAME_MAP.get(Util.toLowerInvariant(colorExpression));
            if (color2 != null) {
                return color2.intValue();
            }
        }
        throw new IllegalArgumentException();
    }

    private static int argb(int alpha, int red, int green, int blue) {
        return (((alpha << 24) | (red << 16)) | (green << 8)) | blue;
    }

    private static int rgb(int red, int green, int blue) {
        return argb(255, red, green, blue);
    }
}
