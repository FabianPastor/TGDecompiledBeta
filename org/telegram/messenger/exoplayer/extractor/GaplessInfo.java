package org.telegram.messenger.exoplayer.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GaplessInfo
{
  private static final String GAPLESS_COMMENT_ID = "iTunSMPB";
  private static final Pattern GAPLESS_COMMENT_PATTERN = Pattern.compile("^ [0-9a-fA-F]{8} ([0-9a-fA-F]{8}) ([0-9a-fA-F]{8})");
  public final int encoderDelay;
  public final int encoderPadding;
  
  private GaplessInfo(int paramInt1, int paramInt2)
  {
    this.encoderDelay = paramInt1;
    this.encoderPadding = paramInt2;
  }
  
  public static GaplessInfo createFromComment(String paramString1, String paramString2)
  {
    if (!"iTunSMPB".equals(paramString1)) {}
    for (;;)
    {
      return null;
      paramString1 = GAPLESS_COMMENT_PATTERN.matcher(paramString2);
      if (paramString1.find()) {
        try
        {
          int i = Integer.parseInt(paramString1.group(1), 16);
          int j = Integer.parseInt(paramString1.group(2), 16);
          if ((i != 0) || (j != 0))
          {
            paramString1 = new GaplessInfo(i, j);
            return paramString1;
          }
        }
        catch (NumberFormatException paramString1) {}
      }
    }
    return null;
  }
  
  public static GaplessInfo createFromXingHeaderValue(int paramInt)
  {
    int i = paramInt >> 12;
    paramInt &= 0xFFF;
    if ((i == 0) && (paramInt == 0)) {
      return null;
    }
    return new GaplessInfo(i, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/GaplessInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */