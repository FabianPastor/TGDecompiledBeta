package org.telegram.messenger.exoplayer2.source;

import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;

public abstract class ForwardingTimeline
  extends Timeline
{
  protected final Timeline timeline;
  
  public ForwardingTimeline(Timeline paramTimeline)
  {
    this.timeline = paramTimeline;
  }
  
  public int getFirstWindowIndex(boolean paramBoolean)
  {
    return this.timeline.getFirstWindowIndex(paramBoolean);
  }
  
  public int getIndexOfPeriod(Object paramObject)
  {
    return this.timeline.getIndexOfPeriod(paramObject);
  }
  
  public int getLastWindowIndex(boolean paramBoolean)
  {
    return this.timeline.getLastWindowIndex(paramBoolean);
  }
  
  public int getNextWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return this.timeline.getNextWindowIndex(paramInt1, paramInt2, paramBoolean);
  }
  
  public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    return this.timeline.getPeriod(paramInt, paramPeriod, paramBoolean);
  }
  
  public int getPeriodCount()
  {
    return this.timeline.getPeriodCount();
  }
  
  public int getPreviousWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return this.timeline.getPreviousWindowIndex(paramInt1, paramInt2, paramBoolean);
  }
  
  public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    return this.timeline.getWindow(paramInt, paramWindow, paramBoolean, paramLong);
  }
  
  public int getWindowCount()
  {
    return this.timeline.getWindowCount();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ForwardingTimeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */