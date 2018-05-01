package org.telegram.messenger.exoplayer2.text.pgs;

import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;

final class PgsSubtitle
  implements Subtitle
{
  private final List<Cue> cues;
  
  public PgsSubtitle(List<Cue> paramList)
  {
    this.cues = paramList;
  }
  
  public List<Cue> getCues(long paramLong)
  {
    return this.cues;
  }
  
  public long getEventTime(int paramInt)
  {
    return 0L;
  }
  
  public int getEventTimeCount()
  {
    return 1;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    return -1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/pgs/PgsSubtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */