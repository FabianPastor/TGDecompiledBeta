package org.telegram.messenger.exoplayer2.text.webvtt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class WebvttParserUtil
{
  private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
  private static final Pattern HEADER = Pattern.compile("^﻿?WEBVTT(( |\t).*)?$");
  
  public static Matcher findNextCueHeader(ParsableByteArray paramParsableByteArray)
  {
    Object localObject;
    do
    {
      localObject = paramParsableByteArray.readLine();
      if (localObject == null) {
        break;
      }
      if (COMMENT.matcher((CharSequence)localObject).matches()) {
        for (;;)
        {
          localObject = paramParsableByteArray.readLine();
          if ((localObject == null) || (((String)localObject).isEmpty())) {
            break;
          }
        }
      }
      localObject = WebvttCueParser.CUE_HEADER_PATTERN.matcher((CharSequence)localObject);
    } while (!((Matcher)localObject).matches());
    for (paramParsableByteArray = (ParsableByteArray)localObject;; paramParsableByteArray = null) {
      return paramParsableByteArray;
    }
  }
  
  public static float parsePercentage(String paramString)
    throws NumberFormatException
  {
    if (!paramString.endsWith("%")) {
      throw new NumberFormatException("Percentages must end with %");
    }
    return Float.parseFloat(paramString.substring(0, paramString.length() - 1)) / 100.0F;
  }
  
  public static long parseTimestampUs(String paramString)
    throws NumberFormatException
  {
    long l1 = 0L;
    String[] arrayOfString = paramString.split("\\.", 2);
    paramString = arrayOfString[0].split(":");
    int i = paramString.length;
    for (int j = 0; j < i; j++) {
      l1 = 60L * l1 + Long.parseLong(paramString[j]);
    }
    long l2 = l1 * 1000L;
    l1 = l2;
    if (arrayOfString.length == 2) {
      l1 = l2 + Long.parseLong(arrayOfString[1]);
    }
    return 1000L * l1;
  }
  
  public static void validateWebvttHeaderLine(ParsableByteArray paramParsableByteArray)
    throws SubtitleDecoderException
  {
    paramParsableByteArray = paramParsableByteArray.readLine();
    if ((paramParsableByteArray == null) || (!HEADER.matcher(paramParsableByteArray).matches())) {
      throw new SubtitleDecoderException("Expected WEBVTT. Got " + paramParsableByteArray);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/webvtt/WebvttParserUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */