package com.stripe.android.time;

import java.util.Calendar;

public class Clock
{
  private static Clock instance;
  protected Calendar calendarInstance;
  
  private Calendar _calendarInstance()
  {
    if (this.calendarInstance != null) {}
    for (Calendar localCalendar = (Calendar)this.calendarInstance.clone();; localCalendar = Calendar.getInstance()) {
      return localCalendar;
    }
  }
  
  public static Calendar getCalendarInstance()
  {
    return getInstance()._calendarInstance();
  }
  
  protected static Clock getInstance()
  {
    if (instance == null) {
      instance = new Clock();
    }
    return instance;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/time/Clock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */