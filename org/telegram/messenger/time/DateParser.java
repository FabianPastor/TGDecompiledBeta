package org.telegram.messenger.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract interface DateParser
{
  public abstract Locale getLocale();
  
  public abstract String getPattern();
  
  public abstract TimeZone getTimeZone();
  
  public abstract Date parse(String paramString)
    throws ParseException;
  
  public abstract Date parse(String paramString, ParsePosition paramParsePosition);
  
  public abstract Object parseObject(String paramString)
    throws ParseException;
  
  public abstract Object parseObject(String paramString, ParsePosition paramParsePosition);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/time/DateParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */