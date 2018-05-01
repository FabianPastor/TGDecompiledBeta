package net.hockeyapp.android;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class PrivateEventManager
{
  public static final int EVENT_TYPE_UNCAUGHT_EXCEPTION = 1;
  private static List<HockeyEventListener> sEventListeners = new LinkedList();
  
  public static void addEventListener(HockeyEventListener paramHockeyEventListener)
  {
    sEventListeners.add(paramHockeyEventListener);
  }
  
  static void postEvent(Event paramEvent)
  {
    Iterator localIterator = sEventListeners.iterator();
    while (localIterator.hasNext()) {
      ((HockeyEventListener)localIterator.next()).onHockeyEvent(paramEvent);
    }
  }
  
  public static final class Event
  {
    private final int mType;
    
    protected Event(int paramInt)
    {
      this.mType = paramInt;
    }
    
    public int getType()
    {
      return this.mType;
    }
  }
  
  public static abstract interface HockeyEventListener
  {
    public abstract void onHockeyEvent(PrivateEventManager.Event paramEvent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/PrivateEventManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */