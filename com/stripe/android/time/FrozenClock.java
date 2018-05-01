package com.stripe.android.time;

import java.util.Calendar;

public class FrozenClock
  extends Clock
{
  public static void freeze(Calendar paramCalendar)
  {
    getInstance().calendarInstance = paramCalendar;
  }
  
  public static void unfreeze()
  {
    getInstance().calendarInstance = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/time/FrozenClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */