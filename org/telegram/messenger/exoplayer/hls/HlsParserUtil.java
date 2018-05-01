package org.telegram.messenger.exoplayer.hls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.ParserException;

final class HlsParserUtil
{
  private static final String BOOLEAN_NO = "NO";
  private static final String BOOLEAN_YES = "YES";
  
  public static Pattern compileBooleanAttrPattern(String paramString)
  {
    return Pattern.compile(paramString + "=(" + "YES" + "|" + "NO" + ")");
  }
  
  public static double parseDoubleAttr(String paramString1, Pattern paramPattern, String paramString2)
    throws ParserException
  {
    return Double.parseDouble(parseStringAttr(paramString1, paramPattern, paramString2));
  }
  
  public static int parseIntAttr(String paramString1, Pattern paramPattern, String paramString2)
    throws ParserException
  {
    return Integer.parseInt(parseStringAttr(paramString1, paramPattern, paramString2));
  }
  
  public static boolean parseOptionalBooleanAttr(String paramString, Pattern paramPattern)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find()) {
      return "YES".equals(paramString.group(1));
    }
    return false;
  }
  
  public static String parseOptionalStringAttr(String paramString, Pattern paramPattern)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find()) {
      return paramString.group(1);
    }
    return null;
  }
  
  public static String parseStringAttr(String paramString1, Pattern paramPattern, String paramString2)
    throws ParserException
  {
    paramPattern = paramPattern.matcher(paramString1);
    if ((paramPattern.find()) && (paramPattern.groupCount() == 1)) {
      return paramPattern.group(1);
    }
    throw new ParserException("Couldn't match " + paramString2 + " tag in " + paramString1);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/HlsParserUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */