package org.telegram.messenger.exoplayer.text;

import java.util.List;

final class PlayableSubtitle
  implements Subtitle
{
  private final long offsetUs;
  public final long startTimeUs;
  private final Subtitle subtitle;
  
  public PlayableSubtitle(Subtitle paramSubtitle, boolean paramBoolean, long paramLong1, long paramLong2)
  {
    this.subtitle = paramSubtitle;
    this.startTimeUs = paramLong1;
    if (paramBoolean) {}
    for (;;)
    {
      this.offsetUs = (paramLong1 + paramLong2);
      return;
      paramLong1 = 0L;
    }
  }
  
  public List<Cue> getCues(long paramLong)
  {
    return this.subtitle.getCues(paramLong - this.offsetUs);
  }
  
  public long getEventTime(int paramInt)
  {
    return this.subtitle.getEventTime(paramInt) + this.offsetUs;
  }
  
  public int getEventTimeCount()
  {
    return this.subtitle.getEventTimeCount();
  }
  
  public long getLastEventTime()
  {
    return this.subtitle.getLastEventTime() + this.offsetUs;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    return this.subtitle.getNextEventTimeIndex(paramLong - this.offsetUs);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/PlayableSubtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */