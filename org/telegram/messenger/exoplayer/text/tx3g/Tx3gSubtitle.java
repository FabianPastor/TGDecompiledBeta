package org.telegram.messenger.exoplayer.text.tx3g;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.Subtitle;
import org.telegram.messenger.exoplayer.util.Assertions;

final class Tx3gSubtitle
  implements Subtitle
{
  public static final Tx3gSubtitle EMPTY = new Tx3gSubtitle();
  private final List<Cue> cues;
  
  private Tx3gSubtitle()
  {
    this.cues = Collections.emptyList();
  }
  
  public Tx3gSubtitle(Cue paramCue)
  {
    this.cues = Collections.singletonList(paramCue);
  }
  
  public List<Cue> getCues(long paramLong)
  {
    if (paramLong >= 0L) {
      return this.cues;
    }
    return Collections.emptyList();
  }
  
  public long getEventTime(int paramInt)
  {
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      return 0L;
    }
  }
  
  public int getEventTimeCount()
  {
    return 1;
  }
  
  public long getLastEventTime()
  {
    return 0L;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    if (paramLong < 0L) {
      return 0;
    }
    return -1;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/tx3g/Tx3gSubtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */