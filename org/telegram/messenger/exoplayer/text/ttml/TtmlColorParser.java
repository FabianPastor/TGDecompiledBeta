package org.telegram.messenger.exoplayer.text.ttml;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

final class TtmlColorParser
{
  static final int AQUA = 16777215;
  static final int BLACK = -16777216;
  static final int BLUE = -16776961;
  private static final Map<String, Integer> COLOR_NAME_MAP;
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
  private static final Pattern RGBA_PATTERN;
  private static final Pattern RGB_PATTERN = Pattern.compile("^rgb\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)$");
  static final int SILVER = -4144960;
  static final int TEAL = -16744320;
  static final int TRANSPARENT = 0;
  static final int WHITE = -1;
  static final int YELLOW = -256;
  
  static
  {
    RGBA_PATTERN = Pattern.compile("^rgba\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)$");
    COLOR_NAME_MAP = new HashMap();
    COLOR_NAME_MAP.put("transparent", Integer.valueOf(0));
    COLOR_NAME_MAP.put("black", Integer.valueOf(-16777216));
    COLOR_NAME_MAP.put("silver", Integer.valueOf(-4144960));
    COLOR_NAME_MAP.put("gray", Integer.valueOf(-8355712));
    COLOR_NAME_MAP.put("white", Integer.valueOf(-1));
    COLOR_NAME_MAP.put("maroon", Integer.valueOf(-8388608));
    COLOR_NAME_MAP.put("red", Integer.valueOf(-65536));
    COLOR_NAME_MAP.put("purple", Integer.valueOf(-8388480));
    COLOR_NAME_MAP.put("fuchsia", Integer.valueOf(-65281));
    COLOR_NAME_MAP.put("magenta", Integer.valueOf(-65281));
    COLOR_NAME_MAP.put("green", Integer.valueOf(-16744448));
    COLOR_NAME_MAP.put("lime", Integer.valueOf(-16711936));
    COLOR_NAME_MAP.put("olive", Integer.valueOf(-8355840));
    COLOR_NAME_MAP.put("yellow", Integer.valueOf(65280));
    COLOR_NAME_MAP.put("navy", Integer.valueOf(-16777088));
    COLOR_NAME_MAP.put("blue", Integer.valueOf(-16776961));
    COLOR_NAME_MAP.put("teal", Integer.valueOf(-16744320));
    COLOR_NAME_MAP.put("aqua", Integer.valueOf(16777215));
    COLOR_NAME_MAP.put("cyan", Integer.valueOf(-16711681));
  }
  
  private static int argb(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return paramInt1 << 24 | paramInt2 << 16 | paramInt3 << 8 | paramInt4;
  }
  
  public static int parseColor(String paramString)
  {
    if (!TextUtils.isEmpty(paramString)) {}
    int i;
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      paramString = paramString.replace(" ", "");
      if (paramString.charAt(0) != '#') {
        break label94;
      }
      i = (int)Long.parseLong(paramString.substring(1), 16);
      if (paramString.length() != 7) {
        break;
      }
      return i | 0xFF000000;
    }
    if (paramString.length() == 9) {
      return (i & 0xFF) << 24 | i >>> 8;
    }
    throw new IllegalArgumentException();
    label94:
    if (paramString.startsWith("rgba"))
    {
      paramString = RGBA_PATTERN.matcher(paramString);
      if (paramString.matches()) {
        return argb(255 - Integer.parseInt(paramString.group(4), 10), Integer.parseInt(paramString.group(1), 10), Integer.parseInt(paramString.group(2), 10), Integer.parseInt(paramString.group(3), 10));
      }
    }
    else if (paramString.startsWith("rgb"))
    {
      paramString = RGB_PATTERN.matcher(paramString);
      if (paramString.matches()) {
        return rgb(Integer.parseInt(paramString.group(1), 10), Integer.parseInt(paramString.group(2), 10), Integer.parseInt(paramString.group(3), 10));
      }
    }
    else
    {
      paramString = (Integer)COLOR_NAME_MAP.get(Util.toLowerInvariant(paramString));
      if (paramString != null) {
        return paramString.intValue();
      }
    }
    throw new IllegalArgumentException();
  }
  
  private static int rgb(int paramInt1, int paramInt2, int paramInt3)
  {
    return argb(255, paramInt1, paramInt2, paramInt3);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/ttml/TtmlColorParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */