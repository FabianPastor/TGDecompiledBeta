package org.telegram.messenger.exoplayer2.text;

import java.util.List;

public abstract interface Subtitle
{
  public abstract List<Cue> getCues(long paramLong);
  
  public abstract long getEventTime(int paramInt);
  
  public abstract int getEventTimeCount();
  
  public abstract int getNextEventTimeIndex(long paramLong);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/Subtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */