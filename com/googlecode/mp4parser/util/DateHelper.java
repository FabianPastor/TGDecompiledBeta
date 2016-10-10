package com.googlecode.mp4parser.util;

import java.util.Date;

public class DateHelper
{
  public static long convert(Date paramDate)
  {
    return paramDate.getTime() / 1000L + 2082844800L;
  }
  
  public static Date convert(long paramLong)
  {
    return new Date((paramLong - 2082844800L) * 1000L);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/DateHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */