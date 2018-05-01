package com.stripe.android.util;

import com.stripe.android.time.Clock;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils
{
  public static boolean hasMonthPassed(int paramInt1, int paramInt2)
  {
    boolean bool = true;
    if (hasYearPassed(paramInt1)) {}
    for (;;)
    {
      return bool;
      Calendar localCalendar = Clock.getCalendarInstance();
      if ((normalizeYear(paramInt1) != localCalendar.get(1)) || (paramInt2 >= localCalendar.get(2) + 1)) {
        bool = false;
      }
    }
  }
  
  public static boolean hasYearPassed(int paramInt)
  {
    boolean bool = true;
    if (normalizeYear(paramInt) < Clock.getCalendarInstance().get(1)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  private static int normalizeYear(int paramInt)
  {
    int i = paramInt;
    if (paramInt < 100)
    {
      i = paramInt;
      if (paramInt >= 0)
      {
        String str = String.valueOf(Clock.getCalendarInstance().get(1));
        str = str.substring(0, str.length() - 2);
        i = Integer.parseInt(String.format(Locale.US, "%s%02d", new Object[] { str, Integer.valueOf(paramInt) }));
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/util/DateUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */